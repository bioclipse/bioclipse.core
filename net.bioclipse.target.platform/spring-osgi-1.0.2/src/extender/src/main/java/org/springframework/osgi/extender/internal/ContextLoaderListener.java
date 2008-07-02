/*
 * Copyright 2006-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.springframework.osgi.extender.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.Version;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.CollectionFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.context.ConfigurableOsgiBundleApplicationContext;
import org.springframework.osgi.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;
import org.springframework.osgi.extender.internal.dependencies.shutdown.ComparatorServiceDependencySorter;
import org.springframework.osgi.extender.internal.dependencies.shutdown.ServiceDependencySorter;
import org.springframework.osgi.extender.internal.dependencies.startup.DependencyWaiterApplicationContextExecutor;
import org.springframework.osgi.extender.internal.support.ApplicationContextConfiguration;
import org.springframework.osgi.extender.internal.support.NamespaceManager;
import org.springframework.osgi.extender.internal.util.ConfigUtils;
import org.springframework.osgi.extender.internal.util.concurrent.Counter;
import org.springframework.osgi.extender.internal.util.concurrent.RunnableTimedExecution;
import org.springframework.osgi.util.OsgiBundleUtils;
import org.springframework.osgi.util.OsgiStringUtils;
import org.springframework.util.ObjectUtils;

/**
 * Osgi Extender that bootstraps 'Spring powered bundles'. <p/> <p/> <p/> <p/>
 * The class listens to bundle events and manages the creation and destruction
 * of application contexts for bundles that have one or both of:
 * <ul>
 * <li> A manifest header entry Spring-Context
 * <li> XML files in META-INF/spring
 * </ul>
 * <p/> The extender also discovers any Spring namespace handlers in resolved
 * bundles and publishes a namespace resolving service for each.
 * </p>
 * <p/> <p/> <p/> If a fragment is attached to the extender bundle that defines
 * a resource META-INF/spring/extender.xml then this file will be used to create
 * an application context for configuring the extender bundle itself. By
 * defining a bean named "taskExecutor" in that context you can configure how
 * the extender bundle schedules asynchronous activity. The extender context is
 * created during a synchronous OSGi lifecycle callback and should contain only
 * simple bean definitions that will not delay context initialisation.
 * </p>
 * 
 * @author Bill Gallagher
 * @author Andy Piper
 * @author Hal Hildebrand
 * @author Adrian Colyer
 * @author Costin Leau
 */
public class ContextLoaderListener implements BundleActivator {

	/**
	 * Common base class for {@link ContextLoaderListener} listeners.
	 * 
	 * @author Costin Leau
	 */
	private abstract class BaseListener implements SynchronousBundleListener {

		/**
		 * A bundle has been started, stopped, resolved, or unresolved. This
		 * method is a synchronous callback, do not do any long-running work in
		 * this thread.
		 * 
		 * @see org.osgi.framework.SynchronousBundleListener#bundleChanged
		 */
		public void bundleChanged(BundleEvent event) {

			boolean trace = log.isTraceEnabled();

			// check if the listener is still alive
			synchronized (monitor) {
				if (isClosed) {
					if (trace)
						log.trace("Listener is closed; events are being ignored");
					return;
				}
			}
			if (trace) {
				log.debug("Processing bundle event [" + OsgiStringUtils.nullSafeToString(event) + "] for bundle ["
						+ OsgiStringUtils.nullSafeSymbolicName(event.getBundle()) + "]");
			}
			try {
				handleEvent(event);
			}
			catch (Exception ex) {
				/* log exceptions before swallowing */
				log.warn("Got exception while handling event " + event, ex);
			}
		}

		protected abstract void handleEvent(BundleEvent event);
	}

	/**
	 * Bundle listener used for detecting namespace handler/resolvers. Exists as
	 * a separate listener so that it can be registered early to avoid race
	 * conditions with bundles in INSTALLING state but still to avoid premature
	 * context creation before the Spring {@link ContextLoaderListener} is not
	 * fully initialized.
	 * 
	 * @author Costin Leau
	 */
	private class NamespaceBundleLister extends BaseListener {
		protected void handleEvent(BundleEvent event) {

			Bundle bundle = event.getBundle();

			switch (event.getType()) {
			case BundleEvent.RESOLVED: {
				maybeAddNamespaceHandlerFor(bundle);
				break;
			}
			case BundleEvent.UNRESOLVED: {
				maybeRemoveNameSpaceHandlerFor(bundle);
				break;
			}
			default:
				break;
			}
		}
	}

	/**
	 * Bundle listener used for context creation/destruction.
	 */
	private class ContextBundleListener extends BaseListener {
		protected void handleEvent(BundleEvent event) {

			Bundle bundle = event.getBundle();

			// ignore current bundle for context creation
			if (bundle.getBundleId() == bundleId) {
				return;
			}

			switch (event.getType()) {
			case BundleEvent.STARTED: {
				maybeCreateApplicationContextFor(bundle);
				break;
			}
			case BundleEvent.STOPPING: {
				if (OsgiBundleUtils.isSystemBundle(bundle)) {
					if (log.isDebugEnabled()) {
						log.debug("System bundle stopping");
					}
					// System bundle is shutting down; Special handling for
					// framework shutdown
					shutdown();
				}
				else {
					maybeCloseApplicationContextFor(bundle);
				}
				break;
			}
			default:
				break;
			}
		}
	}

	protected static final String EXTENDER_CONFIG_FILE_LOCATION = "META-INF/spring/extender.xml";

	protected static final String TASK_EXECUTOR_BEAN_NAME = "taskExecutor";

	private static final String AUTO_ANNOTATION_PROCESSING = "org.springframework.osgi.extender.annotation.auto.processing";

	/**
	 * wait 10 seconds for each context to close
	 */
	private static final long SHUTDOWN_WAIT_TIME = 10 * 1000;

	private static final String ANNOTATION_BPP_CLASS = "org.springframework.osgi.extensions.annotation.ServiceReferenceInjectionBeanPostProcessor";

	private static final Log log = LogFactory.getLog(ContextLoaderListener.class);

	// "Spring Application Context Creation Timer"
	protected Timer timer = new Timer(true);

	/**
	 * The id of the extender bundle itself
	 */
	protected long bundleId;

	/**
	 * Context created to configure the extender bundle itself (currently only
	 * used for overriding task executor implementation).
	 */
	protected OsgiBundleXmlApplicationContext extenderContext;

	/**
	 * The contexts we are currently managing. Keys are bundle ids, values are
	 * ServiceDependentOsgiApplicationContexts for the application context
	 */
	protected final Map managedContexts;

	/**
	 * List of listeners subscribed to spring bundle events
	 */
	protected final Set springBundleListeners = new LinkedHashSet();

	/**
	 * Task executor used for bootstraping the Spring contexts in async mode
	 */
	protected TaskExecutor taskExecutor;

	/**
	 * Task executor which uses the same thread for running tasks. Used when
	 * doing a synchronous wait-for-dependencies.
	 */
	private TaskExecutor sameThreadTaskExecutor = new SyncTaskExecutor();

	/**
	 * ThreadGroup when the taskExecutor is created internally
	 */
	private ThreadGroup threadGroup;

	/**
	 * is this class internally managing the task executor
	 */
	protected boolean isTaskExecutorManagedInternally = false;

	/**
	 * listener counter - used to properly synchronize shutdown
	 */
	protected Counter contextsStarted = new Counter("contextsStarted");

	/**
	 * ServiceDependency for dependencyDetector management
	 */
	protected ServiceRegistration listenerServiceRegistration;

	/**
	 * Spring namespace/resolver manager.
	 */
	private NamespaceManager nsManager;

	/**
	 * The bundle's context
	 */
	protected BundleContext context;

	/**
	 * Bundle listener interested in context creation
	 */
	private SynchronousBundleListener contextListener;

	/**
	 * Bundle listener interested in namespace resolvers/parsers discovery
	 */
	private SynchronousBundleListener nsListener;

	/**
	 * Service-based dependency sorter for shutdown
	 */
	private ServiceDependencySorter shutdownDependencySorter = new ComparatorServiceDependencySorter();

	/**
	 * Monitor used for dealing with the bundle activator and synchronous bundle
	 * threads
	 */
	private transient final Object monitor = new Object();

	/**
	 * flag indicating whether the context is down or not - useful during
	 * shutdown
	 */
	private boolean isClosed = false;

	/**
	 * this extender version
	 */
	private Version extenderVersion;

	/**
	 * Required by the BundleActivator contract
	 */
	public ContextLoaderListener() {
		this.managedContexts = CollectionFactory.createConcurrentMap(16);
	}

	/**
	 * <p/> Called by OSGi when this bundle is started. Finds all previously
	 * resolved bundles and adds namespace handlers for them if necessary.
	 * </p>
	 * <p/> Creates application contexts for bundles started before the extender
	 * was started.
	 * </p>
	 * <p/> Registers a namespace/entity resolving service for use by web app
	 * contexts.
	 * </p>
	 * 
	 * @see org.osgi.framework.BundleActivator#start
	 */
	public void start(BundleContext context) throws Exception {
		this.extenderVersion = OsgiBundleUtils.getBundleVersion(context.getBundle());

		log.info("Starting org.springframework.osgi.extender bundle v.[" + extenderVersion + "]");

		this.context = context;
		this.bundleId = context.getBundle().getBundleId();

		// stage 1: discover existing namespaces
		nsManager = new NamespaceManager(context);

		// register listener first to make sure any bundles in INSTALLED state
		// are not lost
		nsListener = new NamespaceBundleLister();
		context.addBundleListener(nsListener);

		Bundle[] previousBundles = context.getBundles();

		for (int i = 0; i < previousBundles.length; i++) {
			Bundle bundle = previousBundles[i];
			if (OsgiBundleUtils.isBundleResolved(bundle)) {
				maybeAddNamespaceHandlerFor(bundle);
			}
		}

		// discovery finished, publish the resolvers/parsers in the OSGi space
		nsManager.afterPropertiesSet();

		// do this once namespace handlers have been detected
		this.taskExecutor = createTaskExecutor(context);

		// make sure to register this before any listening starts
		// registerShutdownHook();

		// stage 2: discover the bundles that are started
		// and require context creation

		// register the context creation listener
		contextListener = new ContextBundleListener();
		// listen to any changes in bundles
		context.addBundleListener(contextListener);

		// get the bundles again to get an updated view
		previousBundles = context.getBundles();

		// Instantiate all previously resolved bundles which are Spring
		// powered
		for (int i = 0; i < previousBundles.length; i++) {
			if (OsgiBundleUtils.isBundleActive(previousBundles[i])) {
				try {
					maybeCreateApplicationContextFor(previousBundles[i]);
				}
				catch (Throwable e) {
					log.warn("Cannot start bundle " + OsgiStringUtils.nullSafeSymbolicName(previousBundles[i])
							+ " due to", e);
				}
			}
		}
	}

	/**
	 * Called by OSGi when this bundled is stopped. Unregister the
	 * namespace/entity resolving service and clear all state. No further
	 * management of application contexts created by this extender prior to
	 * stopping the bundle occurs after this point (even if the extender bundle
	 * is subsequently restarted).
	 * 
	 * @see org.osgi.framework.BundleActivator#stop
	 */
	public void stop(BundleContext context) throws Exception {
		shutdown();
	}

	/**
	 * Shutdown the extender and all bundled managed by it. Shutdown of contexts
	 * is in the topological order of the dependency graph formed by the service
	 * references.
	 */
	protected void shutdown() {
		synchronized (monitor) {
			// if already closed, bail out
			if (isClosed)
				return;
			else
				isClosed = true;
		}
		log.info("Stopping org.springframework.osgi.extender bundle");

		// first stop the watchdog
		stopTimer();

		// remove the bundle listeners (we are closing down0
		if (contextListener != null) {
			context.removeBundleListener(contextListener);
			contextListener = null;
		}

		if (nsListener != null) {
			context.removeBundleListener(nsListener);
			nsListener = null;
		}

		Bundle[] bundles = new Bundle[managedContexts.size()];

		int i = 0;
		for (Iterator it = managedContexts.values().iterator(); it.hasNext();) {
			ConfigurableOsgiBundleApplicationContext context = (ConfigurableOsgiBundleApplicationContext) it.next();
			bundles[i++] = context.getBundle();
		}

		bundles = shutdownDependencySorter.computeServiceDependencyGraph(bundles);

		boolean debug = log.isDebugEnabled();

		StringBuffer buffer = new StringBuffer();
		if (debug) {
			buffer.append("Shutdown order is: {");
			for (i = 0; i < bundles.length; i++) {
				buffer.append("\nBundle [" + bundles[i].getSymbolicName() + "]");
			}
			buffer.append("\n}");
			log.debug(buffer);
		}

		final List taskList = new ArrayList(managedContexts.size());
		final List closedContexts = Collections.synchronizedList(new ArrayList());
		final Object[] contextClosingDown = new Object[1];

		for (i = 0; i < bundles.length; i++) {
			Long id = new Long(bundles[i].getBundleId());
			final ConfigurableOsgiBundleApplicationContext context = (ConfigurableOsgiBundleApplicationContext) managedContexts.get(id);
			if (context != null) {
				closedContexts.add(context);
				// add a new runnable
				taskList.add(new Runnable() {
					public void run() {
						contextClosingDown[0] = context;
						// eliminate context
						closedContexts.remove(context);
						if (log.isDebugEnabled())
							log.debug("Closing appCtx " + context.getDisplayName());
						context.close();
					}
				});
			}
		}

		// tasks
		final Runnable[] tasks = (Runnable[]) taskList.toArray(new Runnable[taskList.size()]);

		// start the ripper >:)
		for (int j = 0; j < tasks.length; j++) {
			if (RunnableTimedExecution.execute(tasks[j], SHUTDOWN_WAIT_TIME)) {
				if (debug) {
					log.debug(contextClosingDown[0] + " context did not closed succesfully; forcing shutdown");
				}
			}
		}

		this.managedContexts.clear();
		// clear the namespace registry
		nsManager.destroy();

		this.taskExecutor = null;
		if (this.extenderContext != null) {
			this.extenderContext.close();
			this.extenderContext = null;
		}

		// before bailing out; wait for the threads that might be left by
		// the
		// task executor
		stopTaskExecutor();

	}

	/**
	 * Cancel any tasks scheduled for the timer.
	 */
	private void stopTimer() {
		if (timer != null) {
			if (log.isDebugEnabled())
				log.debug("Canceling timer tasks");
			timer.cancel();
		}
		timer = null;
	}

	/**
	 * Shutdown the task executor in case is managed internally by the listener.
	 */
	private void stopTaskExecutor() {
		boolean debug = log.isDebugEnabled();

		if (taskExecutor != null) {
			// only apply these when working with internally created task
			// executors
			if (isTaskExecutorManagedInternally) {

				if (debug)
					log.debug("Waiting for " + contextsStarted + " service dependency listener(s) to stop...");

				contextsStarted.waitForZero(SHUTDOWN_WAIT_TIME);

				if (!contextsStarted.isZero()) {
					if (debug)
						log.debug(contextsStarted.getValue()
								+ " service dependency listener(s) did not responded in time; forcing them to shutdown");
					if (threadGroup != null) {
						threadGroup.stop();
						threadGroup = null;
					}
				}

				else
					log.debug("All listeners closed");
			}
		}
	}

	/**
	 * Utility method that does extender range versioning and approapriate
	 * logging.
	 * 
	 * @param bundle
	 */
	private boolean handlerBundleMatchesExtenderVersion(Bundle bundle) {
		if (!ConfigUtils.matchExtenderVersionRange(bundle, extenderVersion)) {
			if (log.isDebugEnabled())
				log.debug("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle)
						+ "] expects an extender w/ version["
						+ OsgiBundleUtils.getHeaderAsVersion(bundle, ConfigUtils.EXTENDER_VERSION)
						+ "] which does not match current extender w/ version[" + extenderVersion
						+ "]; skipping bundle from handler detection");
			return false;
		}
		return true;
	}

	private void maybeAddNamespaceHandlerFor(Bundle bundle) {
		if (handlerBundleMatchesExtenderVersion(bundle))
			nsManager.maybeAddNamespaceHandlerFor(bundle);
	}

	private void maybeRemoveNameSpaceHandlerFor(Bundle bundle) {
		if (handlerBundleMatchesExtenderVersion(bundle))
			nsManager.maybeRemoveNameSpaceHandlerFor(bundle);
	}

	/**
	 * Context creation is a potentially long-running activity (certainly more
	 * than we want to do on the synchronous event callback). <p/> <p/>Based on
	 * our configuration, the context can be started on the same thread or on a
	 * different one. <p/> Kick off a background activity to create an
	 * application context for the given bundle if needed.
	 * 
	 * @param bundle
	 */
	protected void maybeCreateApplicationContextFor(Bundle bundle) {

		if (!ConfigUtils.matchExtenderVersionRange(bundle, extenderVersion)) {
			if (log.isDebugEnabled())
				log.debug("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle)
						+ "] expects an extender w/ version["
						+ OsgiBundleUtils.getHeaderAsVersion(bundle, ConfigUtils.EXTENDER_VERSION)
						+ "] which does not match current extender w/ version[" + extenderVersion
						+ "]; skipping bundle from context creation");
			return;
		}

		ApplicationContextConfiguration config = new ApplicationContextConfiguration(bundle);
		if (log.isDebugEnabled())
			log.debug("Created config " + config);

		if (!config.isSpringPoweredBundle()) {
			return;
		}

		Long bundleId = new Long(bundle.getBundleId());

		final DelegatedExecutionOsgiBundleApplicationContext context = createApplicationContext(
			OsgiBundleUtils.getBundleContext(bundle), config.getConfigurationLocations());

		if (managedContexts.containsKey(bundleId)) {
			if (log.isDebugEnabled()) {
				log.debug("Bundle is already under control: " + bundle.getSymbolicName());
			}
			return;
		}
		managedContexts.put(bundleId, context);

		context.setPublishContextAsService(config.isPublishContextAsService());

		Runnable contextRefresh = new Runnable() {
			public void run() {
				context.refresh();
			}
		};

		// executor used for creating the appCtx
		// chosen based on the sync/async configuration
		TaskExecutor executor = null;

		// synch/asynch context creation
		if (config.isCreateAsynchronously()) {
			if (log.isDebugEnabled()) {
				log.debug("Asynchronous context creation for bundle " + OsgiStringUtils.nullSafeNameAndSymName(bundle));
			}
			// for the async stuff use the executor
			executor = taskExecutor;
		}
		else {
			if (log.isDebugEnabled()) {
				log.debug("Synchronous context creation for bundle " + OsgiStringUtils.nullSafeNameAndSymName(bundle));
			}
			// for the sync stuff, use this thread
			executor = sameThreadTaskExecutor;
		}

		// wait/no wait for dependencies behaviour
		if (config.isWaitForDependencies()) {
			DependencyWaiterApplicationContextExecutor appCtxExecutor = new DependencyWaiterApplicationContextExecutor(
					context, !config.isCreateAsynchronously());

			appCtxExecutor.setTimeout(config.getTimeout());
			appCtxExecutor.setWatchdog(timer);
			appCtxExecutor.setTaskExecutor(executor);
			appCtxExecutor.setMonitoringCounter(contextsStarted);

			contextsStarted.increment();
		}
		else {
			// do nothing; by default contexts do not wait for services.
		}

		executor.execute(contextRefresh);
	}

	/**
	 * Create an application context from the given locations. By default, will
	 * use an xml application context.
	 * 
	 * @param context bundle context for the application context
	 * @param locations configuration locations
	 * @return
	 */
	protected DelegatedExecutionOsgiBundleApplicationContext createApplicationContext(BundleContext context,
			String[] locations) {
		DelegatedExecutionOsgiBundleApplicationContext sdoac = new OsgiBundleXmlApplicationContext(locations);
		sdoac.setBundleContext(context);

		postProcessContext(sdoac);
		return sdoac;
	}

	/**
	 * Post process the context (for example by adding bean post processors).
	 * 
	 * @param applicationContext application context to process
	 */
	protected void postProcessContext(DelegatedExecutionOsgiBundleApplicationContext applicationContext) {
		addAnnotationBPP(applicationContext);
	}

	/**
	 * Add the annotation post processor if the system property setting is
	 * properly in place.
	 * 
	 * @param applicationContext
	 */
	private void addAnnotationBPP(DelegatedExecutionOsgiBundleApplicationContext applicationContext) {

		Map config = getExternalConfiguration();

		Object setting = config.get(AUTO_ANNOTATION_PROCESSING);
		if (setting != null && setting instanceof String && Boolean.getBoolean((String) setting)) {

			log.info("Enabled automatic Spring-DM annotation processing; [" + AUTO_ANNOTATION_PROCESSING + "="
					+ setting + "]");

			// Try and load the annotation code if it exists
			try {
				Class annotationBppClass = context.getBundle().loadClass(ANNOTATION_BPP_CLASS);

				final BeanPostProcessor annotationBpp = (BeanPostProcessor) BeanUtils.instantiateClass(annotationBppClass);
				((BundleContextAware) annotationBpp).setBundleContext(context);

				applicationContext.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
					public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
							throws BeansException {
						((BeanFactoryAware) annotationBpp).setBeanFactory(beanFactory);
						((BeanClassLoaderAware) annotationBpp).setBeanClassLoader(beanFactory.getBeanClassLoader());
						beanFactory.addBeanPostProcessor(annotationBpp);
					}
				});
			}
			catch (ClassNotFoundException exception) {
				log.info("Spring-dm annotation package cannot be found; automatic annotation processing is disabled");
				if (log.isDebugEnabled())
					log.debug("Cannot load annotatoin bpp", exception);
			}
		}
		else {
			log.info("Disabled automatic Spring-DM annotation processing; [ " + AUTO_ANNOTATION_PROCESSING + "="
					+ setting + "]");
		}

	}

	/**
	 * Return the configuration properties. In time this will be extended.
	 * 
	 * @return configuration properties for the extender
	 */
	protected Map getExternalConfiguration() {
		return System.getProperties();
	}

	/**
	 * Closing an application context is a potentially long-running activity,
	 * however, we *have* to do it synchronously during the event process as the
	 * BundleContext object is not valid once we return from this method.
	 * 
	 * @param bundle
	 */
	protected void maybeCloseApplicationContextFor(Bundle bundle) {
		final ConfigurableOsgiBundleApplicationContext context = (ConfigurableOsgiBundleApplicationContext) managedContexts.remove(new Long(
				bundle.getBundleId()));
		if (context == null) {
			return;
		}

		RunnableTimedExecution.execute(new Runnable() {
			public void run() {
				context.close();
			}
		}, SHUTDOWN_WAIT_TIME);
	}

	/**
	 * <p/> Create the task executor to be used for any asynchronous activity
	 * kicked off by this bundle. By default an
	 * <code>org.springframework.core.task.SimpleAsyncTaskExecutor</code> will
	 * be used. This should be sufficient for most purposes.
	 * </p>
	 * <p/> It is possible to configure the extender bundle to use an alternate
	 * task executor implementation (for example, a CommonJ WorkManager based
	 * implementation when running under WLS or WebSphere). To do this attach a
	 * fragment to the extender bundle that defines a Spring application context
	 * configuration file in META-INF/spring/extender.xml. If such a resource
	 * exists, then an application context will be created from that
	 * configuration file, and a bean named "taskExecutor" will be looked up by
	 * name. If such a bean exists, it will be used.
	 * </p>
	 * 
	 * @param context
	 * @return TaskExecutor
	 */
	// TODO: can we simplify this somewhat further so there is no need for a
	// different XML file
	protected TaskExecutor createTaskExecutor(BundleContext context) {
		Bundle extenderBundle = context.getBundle();
		URL extenderConfigFile = extenderBundle.getResource(EXTENDER_CONFIG_FILE_LOCATION);
		if (extenderConfigFile != null) {
			String[] locations = new String[] { extenderConfigFile.toExternalForm() };

			this.extenderContext = new OsgiBundleXmlApplicationContext(locations);
			this.extenderContext.setBundleContext(context);

			extenderContext.refresh();

			if (extenderContext.containsBean(TASK_EXECUTOR_BEAN_NAME)) {
				Object taskExecutor = extenderContext.getBean(TASK_EXECUTOR_BEAN_NAME);
				if (taskExecutor instanceof TaskExecutor) {
					return (TaskExecutor) taskExecutor;
				}
				else {
					if (log.isErrorEnabled()) {
						log.error("Bean 'taskExecutor' in META-INF/spring/extender.xml configuration file "
								+ "is not an instance of " + TaskExecutor.class.getName() + ". " + "Using defaults.");
					}
				}
			}
			else {
				if (log.isWarnEnabled()) {
					log.warn("Found META-INF/spring/extender.xml configuration file, but no bean "
							+ "named 'taskExecutor' was defined; using defaults.");
				}
			}
		}

		synchronized (monitor) {
			threadGroup = new ThreadGroup("spring-osgi-extender[" + ObjectUtils.getIdentityHexString(this)
					+ "]-threads");
			threadGroup.setDaemon(false);
		}

		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setThreadGroup(threadGroup);
		taskExecutor.setThreadNamePrefix("SpringOsgiExtenderThread-");
		return taskExecutor;
	}

}
