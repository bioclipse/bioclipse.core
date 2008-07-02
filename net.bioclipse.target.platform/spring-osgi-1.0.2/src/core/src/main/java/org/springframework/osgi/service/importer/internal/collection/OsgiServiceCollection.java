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

package org.springframework.osgi.service.importer.internal.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.service.ServiceUnavailableException;
import org.springframework.osgi.service.importer.ImportedOsgiServiceProxy;
import org.springframework.osgi.service.importer.OsgiServiceLifecycleListener;
import org.springframework.osgi.service.importer.internal.aop.ServiceProxyCreator;
import org.springframework.osgi.service.importer.internal.util.OsgiServiceBindingUtils;
import org.springframework.osgi.util.OsgiListenerUtils;
import org.springframework.util.Assert;

/**
 * OSGi service dynamic collection - allows iterating while the underlying
 * storage is being shrunk/expanded. This collection is read-only - its content
 * is being retrieved dynamically from the OSGi platform.
 * 
 * <p/> This collection and its iterators are thread-safe. That is, multiple
 * threads can access the collection. However, since the collection is
 * read-only, it cannot be modified by the client.
 * 
 * @see Collection
 * @author Costin Leau
 */
public class OsgiServiceCollection implements Collection, InitializingBean, CollectionProxy, DisposableBean {

	/**
	 * Listener tracking the OSGi services which form the dynamic collection.
	 * 
	 * @author Costin Leau
	 */
	private class Listener implements ServiceListener {

		public void serviceChanged(ServiceEvent event) {
			ClassLoader tccl = Thread.currentThread().getContextClassLoader();

			try {
				Thread.currentThread().setContextClassLoader(classLoader);
				ServiceReference ref = event.getServiceReference();
				Long serviceId = (Long) ref.getProperty(Constants.SERVICE_ID);
				boolean collectionModified = false;

				Object proxy = null;
				switch (event.getType()) {

					case (ServiceEvent.REGISTERED):
					case (ServiceEvent.MODIFIED):
						// same as ServiceEvent.REGISTERED
						synchronized (serviceProxies) {
							if (!servicesIdMap.containsKey(serviceId)) {
								proxy = proxyCreator.createServiceProxy(ref);
								// let the dynamic collection decide if the service
								// is added or not (think set, sorted set)
								if (serviceProxies.add(proxy)) {
									collectionModified = true;
									servicesIdMap.put(serviceId, proxy);
								}
							}
						}
						// inform listeners
						// TODO: should this be part of the lock also?
						if (collectionModified)
							OsgiServiceBindingUtils.callListenersBind(context, proxy, ref, listeners);

						break;
					case (ServiceEvent.UNREGISTERING):
						synchronized (serviceProxies) {
							// remove service id / proxy association
							proxy = servicesIdMap.remove(serviceId);
							if (proxy != null) {
								// before removal, allow analysis
								checkDeadProxies(proxy);
								// remove service proxy
								collectionModified = serviceProxies.remove(proxy);
								// invalidate it
								invalidateProxy(proxy);
							}
						}
						// TODO: should this be part of the lock also?
						if (collectionModified)
							OsgiServiceBindingUtils.callListenersUnbind(context, proxy, ref, listeners);
						break;

					default:
						throw new IllegalArgumentException("unsupported event type:" + event);
				}
			}
			// OSGi swallows these exceptions so make sure we get a chance to
			// see them.
			catch (Throwable re) {
				if (log.isWarnEnabled()) {
					log.warn("serviceChanged() processing failed", re);
				}
			}
			finally {
				Thread.currentThread().setContextClassLoader(tccl);
			}
		}
	}

	/**
	 * Read-only iterator wrapper around the dynamic collection iterator.
	 * 
	 * @author Costin Leau
	 * 
	 */
	protected class OsgiServiceIterator implements Iterator {

		// dynamic thread-safe iterator
		private final Iterator iter = serviceProxies.iterator();


		public boolean hasNext() {
			mandatoryServiceCheck();
			return iter.hasNext();
		}

		public Object next() {
			synchronized (serviceProxies) {
				mandatoryServiceCheck();
				Object proxy = iter.next();
				return (proxy == null ? tailDeadProxy : proxy);
			}
		}

		public void remove() {
			// write operations disabled
			throw new UnsupportedOperationException();
		}
	}


	private static final Log log = LogFactory.getLog(OsgiServiceCollection.class);

	// map of services
	// the service id is used as key while the service proxy is used for
	// values
	// Map<ServiceId, ImporterProxy>
	// 
	// NOTE: this collection is protected by the 'serviceProxies' lock.
	protected final Map servicesIdMap = new LinkedHashMap(8);

	/**
	 * The dynamic collection.
	 */
	protected DynamicCollection serviceProxies;

	/**
	 * Recall the last proxy for the rare case, where a service goes down
	 * between the #hasNext() and #next() call of an iterator.
	 * 
	 * Subclasses should implement their own strategy when it comes to assign a
	 * value to it through
	 */
	protected volatile Object tailDeadProxy;

	private boolean serviceRequiredAtStartup = true;

	private final Filter filter;

	private final BundleContext context;

	/** TCCL to set between calling listeners */
	private final ClassLoader classLoader;

	/** Service proxy creator */
	private final ServiceProxyCreator proxyCreator;

	private OsgiServiceLifecycleListener[] listeners = new OsgiServiceLifecycleListener[0];

	private final ServiceListener listener;


	public OsgiServiceCollection(Filter filter, BundleContext context, ClassLoader classLoader,
			ServiceProxyCreator proxyCreator) {
		Assert.notNull(classLoader, "ClassLoader is required");
		Assert.notNull(context, "context is required");

		this.filter = filter;
		this.context = context;
		this.classLoader = classLoader;

		this.proxyCreator = proxyCreator;
		listener = new Listener();
	}

	public void afterPropertiesSet() {
		// create service proxies collection
		this.serviceProxies = createInternalDynamicStorage();

		boolean trace = log.isTraceEnabled();

		if (trace)
			log.trace("adding osgi listener for services matching [" + filter + "]");
		OsgiListenerUtils.addServiceListener(context, listener, filter);

		if (serviceRequiredAtStartup) {
			if (trace)
				log.trace("1..x cardinality - looking for service [" + filter + "] at startup...");
			mandatoryServiceCheck();
		}
	}

	public void destroy() {
		OsgiListenerUtils.removeServiceListener(context, listener);

		synchronized (serviceProxies) {
			for (Iterator iterator = serviceProxies.iterator(); iterator.hasNext();) {
				ImportedOsgiServiceProxy serviceProxy = (ImportedOsgiServiceProxy) iterator.next();
				listener.serviceChanged(new ServiceEvent(ServiceEvent.UNREGISTERING, serviceProxy.getServiceReference()));
			}
			serviceProxies.clear();
		}
		servicesIdMap.clear();
	}

	/**
	 * Check to see whether at least one service is available.
	 */
	protected void mandatoryServiceCheck() {
		if (serviceRequiredAtStartup && serviceProxies.isEmpty())
			throw new ServiceUnavailableException(filter);
	}

	public boolean isSatisfied() {
		if (serviceRequiredAtStartup)
			return serviceProxies.isEmpty();
		else
			return true;
	}

	/**
	 * Create the dynamic storage used internally. The storage <strong>has</strong>
	 * to be thread-safe.
	 */
	protected DynamicCollection createInternalDynamicStorage() {
		return new DynamicCollection();
	}

	private void invalidateProxy(Object proxy) {
		// TODO: add proxy invalidation (if it makes sense)
	}

	/**
	 * Hook for tracking the last disappearing service to cope with the rare
	 * case, where the last service in the collection disappears between calls
	 * to hasNext() and next() on an iterator at the end of the collection.
	 * 
	 * @param proxy
	 */
	protected void checkDeadProxies(Object proxy, int proxyCollectionPos) {
		if (proxyCollectionPos == serviceProxies.size() - 1)
			tailDeadProxy = proxy;
	}

	/**
	 * Private method, computing the index and share it with subclasses.
	 * 
	 * @param proxy
	 */
	private void checkDeadProxies(Object proxy) {
		// no need for a collection lock (alreayd have it)
		int index = serviceProxies.indexOf(proxy);
		checkDeadProxies(proxy, index);
	}

	public Iterator iterator() {
		return new OsgiServiceIterator();
	}

	public int size() {
		mandatoryServiceCheck();
		return serviceProxies.size();
	}

	public String toString() {
		mandatoryServiceCheck();
		synchronized (serviceProxies) {
			return serviceProxies.toString();
		}
	}

	//
	// write operations forbidden
	//
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	public boolean add(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object o) {
		mandatoryServiceCheck();
		return serviceProxies.contains(o);
	}

	public boolean containsAll(Collection c) {
		mandatoryServiceCheck();
		return serviceProxies.containsAll(c);
	}

	public boolean isEmpty() {
		mandatoryServiceCheck();
		return size() == 0;
	}

	public Object[] toArray() {
		mandatoryServiceCheck();
		return serviceProxies.toArray();
	}

	public Object[] toArray(Object[] array) {
		mandatoryServiceCheck();
		return serviceProxies.toArray(array);
	}

	/**
	 * @param listeners The listeners to set.
	 */
	public void setListeners(OsgiServiceLifecycleListener[] listeners) {
		Assert.notNull(listeners, "argument should not be null");
		this.listeners = listeners;
	}

	public void setRequiredAtStartup(boolean serviceRequiredAtStartup) {
		this.serviceRequiredAtStartup = serviceRequiredAtStartup;
	}
}
