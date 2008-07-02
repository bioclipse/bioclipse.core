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
 */

package org.springframework.osgi.service.importer.internal.aop;

import java.util.Arrays;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.service.ServiceUnavailableException;
import org.springframework.osgi.service.dependency.DependableServiceImporter;
import org.springframework.osgi.service.dependency.MandatoryDependencyEvent;
import org.springframework.osgi.service.dependency.MandatoryDependencyListener;
import org.springframework.osgi.service.importer.OsgiServiceLifecycleListener;
import org.springframework.osgi.service.importer.internal.support.DefaultRetryCallback;
import org.springframework.osgi.service.importer.internal.support.RetryTemplate;
import org.springframework.osgi.service.importer.internal.support.ServiceWrapper;
import org.springframework.osgi.service.importer.internal.util.OsgiServiceBindingUtils;
import org.springframework.osgi.util.OsgiListenerUtils;
import org.springframework.osgi.util.OsgiServiceReferenceUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Interceptor adding dynamic behaviour for unary service (..1 cardinality). It
 * will look for a service using the given filter, retrying if the service is
 * down or unavailable. Will dynamically rebound a new service, if one is
 * available with a higher service ranking. <p/> <p/> In case no service is
 * available, it will throw an exception. <p/> <strong>Note</strong>: this is a
 * stateful interceptor and should not be shared.
 * 
 * @author Costin Leau
 */
public class ServiceDynamicInterceptor extends ServiceInvoker implements InitializingBean, DisposableBean {

	/**
	 * Listener tracking the OSGi services which form the dynamic reference.
	 */
	// NOTE: while the listener here seems to share the same functionality as
	// the one in ServiceCollection in reality there are a big number of
	// differences in them - for example this one supports rebind
	// while the collection does not.
	//
	// the only common part is the TCCL handling before calling the listeners.
	private class Listener implements ServiceListener {

		public void serviceChanged(ServiceEvent event) {
			ClassLoader tccl = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(classLoader);
				ServiceReference ref = event.getServiceReference();

				// service id
				long serviceId = ((Long) ref.getProperty(Constants.SERVICE_ID)).longValue();
				// service ranking
				Integer rank = (Integer) ref.getProperty(Constants.SERVICE_RANKING);
				int ranking = (rank == null ? 0 : rank.intValue());

				boolean debug = log.isDebugEnabled();

				switch (event.getType()) {

					case (ServiceEvent.REGISTERED):
					case (ServiceEvent.MODIFIED): {
						// same as ServiceEvent.REGISTERED
						if (updateWrapperIfNecessary(ref, serviceId, ranking)) {
							// inform listeners
							OsgiServiceBindingUtils.callListenersBind(bundleContext, proxy, ref, listeners);

							// update dependency manager
							if (mandatoryListeners != null) {
								for (int i = 0; i < mandatoryListeners.size(); i++) {
									if (debug)
										log.debug("calling satisfied on dependency mandatoryListeners");
									((MandatoryDependencyListener) mandatoryListeners.get(i)).mandatoryDependencySatisfied(new MandatoryDependencyEvent(
										serviceImporter));
								}
							}
						}

						break;
					}
					case (ServiceEvent.UNREGISTERING): {

						boolean serviceRemoved = false;
						/**
						 * used if the service goes down and there is no
						 * replacement
						 */
						/**
						 * since the listeners will require a valid proxy, the
						 * invalidation has to happen *after* calling the
						 * listeners
						 */
						ServiceWrapper oldWrapper = wrapper;

						synchronized (lock) {
							// remove service
							if (wrapper != null) {
								if (serviceId == wrapper.getServiceId()) {
									serviceRemoved = true;
									wrapper = null;

								}
							}
						}

						ServiceReference newReference = null;

						boolean isDestroyed = false;
						
						synchronized (lock) {
							isDestroyed = destroyed;
						}

						// discover a new reference only if we are still running
						if (!isDestroyed) {
							newReference = OsgiServiceReferenceUtils.getServiceReference(bundleContext,
								(filter == null ? null : filter.toString()));

							// we have a rebind (a new service was bound)
							// so another candidate has to be searched from the existing candidates
							// - as they are alive already, we have to send an event for them ourselves
							// MODIFIED will be used for clarity
							if (newReference != null) {
								// update the listeners (through a MODIFIED event
								serviceChanged(new ServiceEvent(ServiceEvent.MODIFIED, newReference));
							}
						}

						// if no new reference was found and the service was indeed removed (it was bound to the interceptor)
						// then do an unbind
						if (newReference == null && serviceRemoved) {

							// reuse the old service for the time being
							synchronized (lock) {
								wrapper = oldWrapper;
							}

							// update dependency manager
							if (mandatoryListeners != null) {
								for (int i = 0; i < mandatoryListeners.size(); i++) {
									if (debug)
										log.debug("calling unsatisfied on dependency mandatoryListeners");
									((MandatoryDependencyListener) mandatoryListeners.get(i)).mandatoryDependencyUnsatisfied(new MandatoryDependencyEvent(
										serviceImporter));
								}
							}
							// inform listeners
							OsgiServiceBindingUtils.callListenersUnbind(bundleContext, proxy, ref, listeners);

							// clean up wrapper
							synchronized (lock) {
								wrapper = null;
							}

							if (debug) {
								String message = "service reference [" + ref + "] was unregistered";
								if (serviceRemoved) {
									message += " and was unbound from the service proxy";
								}
								else {
									message += " but did not affect the service proxy";
								}
								log.debug(message);
							}

						}

						break;
					}
					default:
						throw new IllegalArgumentException("unsupported event type");
				}
			}
			catch (Throwable e) {
				// The framework will swallow these exceptions without logging,
				// so log them here
				log.fatal("Exception during service event handling", e);
			}
			finally {
				Thread.currentThread().setContextClassLoader(tccl);
			}
		}

		private boolean updateWrapperIfNecessary(ServiceReference ref, long serviceId, int serviceRanking) {
			boolean updated = false;
			try {
				synchronized (lock) {
					if (wrapper != null && wrapper.isServiceAlive()) {
						// if have a higher rank service
						if (serviceRanking > wrapper.getServiceRanking()) {
							updated = true;
							updateReferenceHolders(ref);
						}
						// if equality, use the service id
						if (serviceRanking == wrapper.getServiceRanking()) {
							if (serviceId < wrapper.getServiceId()) {
								updated = true;
								updateReferenceHolders(ref);
							}
						}
					}
					// we don't have any valid services bounded yet so just bind
					// the new
					// one
					else {
						updated = true;
						updateReferenceHolders(ref);
					}
					lock.notifyAll();
					return updated;
				}
			}
			finally {
				if (log.isDebugEnabled()) {
					String message = "service reference [" + ref + "]";
					if (updated)
						message += " bound to proxy";
					else
						message += " not bound to proxy";
					log.debug(message);
				}
			}
		}

		/**
		 * Update internal holders for the backing ServiceReference.
		 * 
		 * @param ref
		 */
		private void updateReferenceHolders(ServiceReference ref) {
			// no need for a lock since this method is called from a synchronized block
			wrapper = new ServiceWrapper(ref, bundleContext);
			referenceDelegate.swapDelegates(ref);
		}
	}


	private static final int hashCode = ServiceDynamicInterceptor.class.hashCode() * 13;

	private final BundleContext bundleContext;

	private final Filter filter;

	/**
	 * TCCL to set when calling listeners
	 */
	private final ClassLoader classLoader;

	private final ServiceReferenceDelegate referenceDelegate;

	private final ServiceListener listener;

	private boolean serviceRequiredAtStartup = true;

	/** flag indicating whether the destruction has started or not */
	private boolean isDuringDestruction = false;
	
	private boolean destroyed = false;

	/** private lock */
	private final Object lock = new Object();

	/**
	 * utility service wrapper
	 */
	private ServiceWrapper wrapper;

	/**
	 * Retry template
	 */
	private RetryTemplate retryTemplate;

	/**
	 * mandatory listeners
	 */
	private List mandatoryListeners;

	/**
	 * depending service importer
	 */
	private DependableServiceImporter serviceImporter;

	/**
	 * listener that need to be informed of bind/rebind/unbind
	 */
	private OsgiServiceLifecycleListener[] listeners = new OsgiServiceLifecycleListener[0];

	/**
	 * reference to the created proxy passed to the listeners
	 */
	private Object proxy;


	public ServiceDynamicInterceptor(BundleContext context, Filter filter, ClassLoader classLoader) {
		this.bundleContext = context;
		this.filter = filter;
		this.classLoader = classLoader;

		referenceDelegate = new ServiceReferenceDelegate();
		listener = new Listener();
	}

	public Object getTarget() {
		Object target = lookupService();

		// nothing found
		if (target == null) {
			throw new ServiceUnavailableException(filter);
		}
		return target;
	}

	/**
	 * Look the service by waiting the service to appear. Note this method
	 * should use the same lock as the listener handling the service reference.
	 */
	private Object lookupService() {
		synchronized (lock) {
			if (destroyed && !isDuringDestruction)
				throw new IllegalStateException("the service proxy has been destroyed!");
		}

		return (Object) retryTemplate.execute(new DefaultRetryCallback() {

			public Object doWithRetry() {
				return (wrapper != null) ? wrapper.getService() : null;
			}
		}, lock);
	}

	public void afterPropertiesSet() {
		Assert.notNull(proxy);

		boolean debug = log.isDebugEnabled();
		if (retryTemplate == null)
			retryTemplate = new RetryTemplate();

		if (debug)
			log.debug("adding osgi mandatoryListeners for services matching [" + filter + "]");
		OsgiListenerUtils.addSingleServiceListener(bundleContext, listener, filter);
		if (serviceRequiredAtStartup) {
			if (debug)
				log.debug("1..x cardinality - looking for service [" + filter + "] at startup...");
			Object target = getTarget();
			if (debug)
				log.debug("service retrieved " + target);
		}
	}

	public void destroy() throws Exception {
		OsgiListenerUtils.removeServiceListener(bundleContext, listener);
		synchronized (lock) {
			// set this flag first to make sure no rebind is done
			destroyed = true;
			isDuringDestruction = true;
			if (wrapper != null) {
				ServiceReference ref = wrapper.getReference();
				if (ref != null) {
					// send unregistration event to the listener
					listener.serviceChanged(new ServiceEvent(ServiceEvent.UNREGISTERING, ref));
				}
			}
			/** destruction process has ended */
			isDuringDestruction = false;
		}
	}

	public ServiceReference getServiceReference() {
		return referenceDelegate;
	}

	public void setRetryTemplate(RetryTemplate retryTemplate) {
		this.retryTemplate = retryTemplate;
	}

	public RetryTemplate getRetryTemplate() {
		return retryTemplate;
	}

	public OsgiServiceLifecycleListener[] getListeners() {
		return listeners;
	}

	public void setListeners(OsgiServiceLifecycleListener[] listeners) {
		this.listeners = listeners;
	}

	public void setDependencyListeners(List listeners) {
		this.mandatoryListeners = listeners;
	}

	public void setServiceImporter(DependableServiceImporter importer) {
		this.serviceImporter = importer;
	}

	public void setRequiredAtStartup(boolean requiredAtStartup) {
		this.serviceRequiredAtStartup = requiredAtStartup;
	}

	public void setProxy(Object proxy) {
		this.proxy = proxy;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other instanceof ServiceDynamicInterceptor) {
			ServiceDynamicInterceptor oth = (ServiceDynamicInterceptor) other;
			return (serviceRequiredAtStartup == oth.serviceRequiredAtStartup
					&& ObjectUtils.nullSafeEquals(wrapper, oth.wrapper)
					&& ObjectUtils.nullSafeEquals(filter, oth.filter)
					&& ObjectUtils.nullSafeEquals(retryTemplate, oth.retryTemplate)
					&& ObjectUtils.nullSafeEquals(serviceImporter, oth.serviceImporter) && Arrays.equals(listeners,
				oth.listeners));
		}
		else
			return false;
	}

	public int hashCode() {
		return hashCode;
	}

}
