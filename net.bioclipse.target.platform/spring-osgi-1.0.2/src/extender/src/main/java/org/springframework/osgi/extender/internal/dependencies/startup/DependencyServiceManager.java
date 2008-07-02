
package org.springframework.osgi.extender.internal.dependencies.startup;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.osgi.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.springframework.osgi.service.importer.support.AbstractOsgiServiceImportFactoryBean;
import org.springframework.osgi.util.OsgiListenerUtils;
import org.springframework.osgi.util.OsgiStringUtils;

/**
 * ServiceListener used for tracking dependent services. Even if the
 * ServiceListener receives event synchronously, mutable properties should be
 * synchronized to guarantee safe publishing between threads.
 * 
 * @author Costin Leau
 * @author Hal Hildebrand
 */
public class DependencyServiceManager {

	private static final Log log = LogFactory.getLog(DependencyServiceManager.class);

	protected final Set dependencies = Collections.synchronizedSet(new LinkedHashSet());

	protected final Set unsatisfiedDependencies = Collections.synchronizedSet(new LinkedHashSet());

	private final ContextExecutorStateAccessor contextStateAccessor;

	private final BundleContext bundleContext;

	private final ServiceListener listener;

	private final DelegatedExecutionOsgiBundleApplicationContext context;

	/**
	 * Task to execute if all dependencies are met.
	 */
	private final Runnable executeIfDone;


	/**
	 * Actual ServiceListener.
	 * 
	 * @author Costin Leau
	 * @author Hal Hildebrand
	 */
	private class DependencyServiceListener implements ServiceListener {

		/**
		 * Process serviceChanged events, completing context initialization if
		 * all the required dependencies are satisfied.
		 * 
		 * @param serviceEvent
		 */
		public void serviceChanged(ServiceEvent serviceEvent) {
			boolean trace = log.isTraceEnabled();
			boolean debug = log.isDebugEnabled();

			try {
				if (unsatisfiedDependencies.isEmpty()) { // already
					// completed.
					if (trace) {
						log.trace("handling service event, but no unsatisfied dependencies for "
								+ context.getDisplayName());
					}
					return;
				}

				ServiceReference ref = serviceEvent.getServiceReference();
				if (trace) {
					log.trace("handling service event [" + OsgiStringUtils.nullSafeToString(serviceEvent) + ":"
							+ OsgiStringUtils.nullSafeToString(ref) + "] for " + context.getDisplayName());
				}

				updateDependencies(serviceEvent);

				ContextState state = contextStateAccessor.getContextState();

				if (state.isResolved()) {
					deregister();
					return;
				}

				// Good to go!
				if (unsatisfiedDependencies.isEmpty()) {
					deregister();
					// context.listener = null;
					log.info("No outstanding OSGi service dependencies, completing initialization for "
							+ context.getDisplayName());

					// execute task to complete initialization
					// NOTE: the runnable should be able to delegate any long
					// process to a
					// different thread.
					executeIfDone.run();
				}
			}
			catch (Throwable e) {
				// frameworks will simply not log exception for event handlers
				log.error("Exception during dependency processing for " + context.getDisplayName(), e);
			}
		}

		private void updateDependencies(ServiceEvent serviceEvent) {
			boolean trace = log.isTraceEnabled();
			boolean debug = log.isDebugEnabled();

			for (Iterator i = dependencies.iterator(); i.hasNext();) {
				ServiceDependency dependency = (ServiceDependency) i.next();

				// check if there is a match on the service
				if (dependency.matches(serviceEvent)) {
					switch (serviceEvent.getType()) {

						case ServiceEvent.REGISTERED:
						case ServiceEvent.MODIFIED:
							unsatisfiedDependencies.remove(dependency);
							if (debug) {
								log.debug("found service; eliminating " + dependency);
							}
							break;

						case ServiceEvent.UNREGISTERING:
							unsatisfiedDependencies.add(dependency);
							if (debug) {
								log.debug("service unregistered; adding " + dependency);
							}
							break;
						default: // do nothing
							if (debug) {
								log.debug("Unknown service event type for: " + dependency);
							}
							break;
					}
				}
				else {
					if (trace) {
						log.trace(dependency + " does not match: "
								+ OsgiStringUtils.nullSafeToString(serviceEvent.getServiceReference()));
					}
				}
			}
		}
	}


	/**
	 * Create a dependency manager, indicating the executor bound to, the
	 * context that contains the dependencies and the task to execute if all
	 * dependencies are met.
	 * 
	 * @param executor
	 * @param context
	 * @param executeIfDone
	 */
	public DependencyServiceManager(ContextExecutorStateAccessor executor,
			DelegatedExecutionOsgiBundleApplicationContext context, Runnable executeIfDone) {
		this.contextStateAccessor = executor;
		this.context = context;
		this.bundleContext = context.getBundleContext();
		this.listener = new DependencyServiceListener();

		this.executeIfDone = executeIfDone;
	}

	protected void findServiceDependencies() {
		Thread currentThread = Thread.currentThread();
		ClassLoader oldTCCL = currentThread.getContextClassLoader();

		boolean debug = log.isDebugEnabled();
		try {
			currentThread.setContextClassLoader(context.getClassLoader());

			ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
			String[] beans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory,
				AbstractOsgiServiceImportFactoryBean.class, true, false);
			for (int i = 0; i < beans.length; i++) {
				String beanName = (beans[i].startsWith(BeanFactory.FACTORY_BEAN_PREFIX) ? beans[i]
						: BeanFactory.FACTORY_BEAN_PREFIX + beans[i]);

				AbstractOsgiServiceImportFactoryBean reference = (AbstractOsgiServiceImportFactoryBean) beanFactory.getBean(beanName);
				ServiceDependency dependency = new ServiceDependency(bundleContext, reference.getUnifiedFilter(),
					reference.isMandatory());

				String realBean = beanName.substring(1);

				if (debug)
					log.debug("destroying bean " + realBean + " from context " + beanFactory);

				// clean up factory singleton
				// ((DefaultListableBeanFactory)
				// beanFactory).destroySingleton(realBean);

				dependencies.add(dependency);
				if (!dependency.isServicePresent()) {
					if (debug)
						log.debug("adding OSGi service dependency for importer " + beanName);
					unsatisfiedDependencies.add(dependency);
				}
			}
		}
		finally {
			currentThread.setContextClassLoader(oldTCCL);
		}

		log.info(dependencies.size() + " OSGi service dependencies, " + unsatisfiedDependencies.size()
				+ " unsatisfied for " + context.getDisplayName());

	}

	protected boolean isSatisfied() {
		return unsatisfiedDependencies.isEmpty();
	}

	public Set getUnsatisfiedDependencies() {
		return unsatisfiedDependencies;
	}

	protected void register() {
		String filter = createDependencyFilter();
		if (log.isDebugEnabled()) {
			log.debug(context.getDisplayName() + " has registered service dependency dependencyDetector with filter: "
					+ filter);
		}
		OsgiListenerUtils.addServiceListener(bundleContext, listener, filter);
	}

	/**
	 * Look at the existing dependencies and create an appropriate filter. This
	 * method concatenates the filters into one.
	 * 
	 * @return
	 */
	protected String createDependencyFilter() {
		boolean multiple = unsatisfiedDependencies.size() > 1;
		StringBuffer sb = new StringBuffer(100 * unsatisfiedDependencies.size());
		if (multiple) {
			sb.append("(|");
		}
		for (Iterator i = unsatisfiedDependencies.iterator(); i.hasNext();) {
			sb.append(((ServiceDependency) i.next()).filterAsString);
		}
		if (multiple) {
			sb.append(')');
		}
		return sb.toString();
	}

	protected void deregister() {
		if (log.isDebugEnabled()) {
			log.debug("deregistering service dependency dependencyDetector for " + context.getDisplayName());
		}

		OsgiListenerUtils.removeServiceListener(bundleContext, listener);
	}

}