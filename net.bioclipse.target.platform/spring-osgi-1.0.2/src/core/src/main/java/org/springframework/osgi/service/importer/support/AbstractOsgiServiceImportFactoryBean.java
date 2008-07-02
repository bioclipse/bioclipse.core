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

package org.springframework.osgi.service.importer.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.service.exporter.OsgiServicePropertiesResolver;
import org.springframework.osgi.service.importer.OsgiServiceLifecycleListener;
import org.springframework.osgi.util.OsgiFilterUtils;
import org.springframework.osgi.util.internal.ClassUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Base class for importing OSGi services. Provides most of the constructs
 * required for assembling the service proxies, leaving subclasses to decide on
 * the service cardinality (one service or multiple).
 * 
 * @author Costin Leau
 * @author Adrian Colyer
 * @author Hal Hildebrand
 * 
 */
public abstract class AbstractOsgiServiceImportFactoryBean extends AbstractDependableServiceImporter implements
		SmartFactoryBean, InitializingBean, DisposableBean, BundleContextAware, BeanClassLoaderAware {

	private static final Log log = LogFactory.getLog(AbstractOsgiServiceImportFactoryBean.class);

	private ClassLoader classLoader;

	private BundleContext bundleContext;

	private ImportContextClassLoader contextClassLoader = ImportContextClassLoader.CLIENT;

	// not required to be an interface, but usually should be...
	private Class[] interfaces;

	// filter used to narrow service matches, may be null
	private String filter;

	// Cumulated filter string between the specified classes/interfaces and the
	// given filter
	private Filter unifiedFilter;

	// service lifecycle listener
	private OsgiServiceLifecycleListener[] listeners;

	/** Service Bean property of the OSGi service * */
	private String serviceBeanName;

	private boolean initialized = false;

	private Object proxy;

	/**
	 * Returns a managed hook to access OSGi service(s).
	 * Subclasses can decide to create either a proxy managing only
	 * one OSGi service type or a collection of services matching a certain
	 * criteri
	 * 
	 * @return managed OSGi service(s)
	 */
	public Object getObject() {
		if (!initialized)
			throw new FactoryBeanNotInitializedException();

		if (proxy == null) {
			proxy = createProxy();
		}

		return proxy;
	}

	abstract Object createProxy();

	public boolean isSingleton() {
		return true;
	}

	public boolean isEagerInit() {
		return true;
	}

	public boolean isPrototype() {
		return false;
	}

	public void afterPropertiesSet() {
		Assert.notNull(this.bundleContext, "Required 'bundleContext' property was not set");
		Assert.notNull(classLoader, "Required 'classLoader' property was not set");
		Assert.notNull(interfaces, "Required 'interfaces' property was not set");
		// validate specified classes
		Assert.isTrue(!ClassUtils.containsUnrelatedClasses(interfaces),
			"more then one concrete class specified; cannot create proxy");

		this.listeners = (listeners == null ? new OsgiServiceLifecycleListener[0] : listeners);

		getUnifiedFilter(); // eager initialization of the cache to catch filter
		// errors
		Assert.notNull(interfaces, "Required serviceTypes property not specified");

		initialized = true;
	}

	/**
	 * Assembles the configuration properties into one unified OSGi filter. Note
	 * that this implementation creates the filter on the first call and caches
	 * it afterwards.
	 * 
	 * @return unified filter based on this factory bean configuration
	 */
	public Filter getUnifiedFilter() {
		if (unifiedFilter != null) {
			return unifiedFilter;
		}

		String filterWithClasses = OsgiFilterUtils.unifyFilter(interfaces, filter);

		if (log.isTraceEnabled())
			log.trace("unified classes=" + ObjectUtils.nullSafeToString(interfaces) + " and filter=[" + filter
					+ "]  in=[" + filterWithClasses + "]");

		// add the serviceBeanName constraint
		String filterWithServiceBeanName = OsgiFilterUtils.unifyFilter(
			OsgiServicePropertiesResolver.BEAN_NAME_PROPERTY_KEY, new String[] { serviceBeanName }, filterWithClasses);

		if (log.isTraceEnabled())
			log.trace("unified serviceBeanName [" + ObjectUtils.nullSafeToString(serviceBeanName) + "] and filter=["
					+ filterWithClasses + "]  in=[" + filterWithServiceBeanName + "]");

		// create (which implies validation) the actual filter
		unifiedFilter = OsgiFilterUtils.createFilter(filterWithServiceBeanName);

		return unifiedFilter;
	}

	public void destroy() throws Exception {
		DisposableBean bean = getDisposable();
		if (bean != null) {
			bean.destroy();
		}
		proxy = null;
	}

	/**
	 * Hook for getting a disposable instance backing the proxy returned to the
	 * user.
	 * 
	 * @return disposable bean for cleaning the proxy
	 */
	abstract DisposableBean getDisposable();

	/**
	 * Sets the classes that the imported service advertises.
	 * 
	 * @param interfaces array of advertised classes.
	 */
	public void setInterfaces(Class[] interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * Sets the thread context class loader management strategy to use for
	 * services imported by this service. By default
	 * {@link ImportContextClassLoader#CLIENT} is used.
	 * 
	 * @param contextClassLoader import context class loader management strategy
	 * @see ImportContextClassLoader
	 */
	public void setContextClassLoader(ImportContextClassLoader contextClassLoader) {
		Assert.notNull(contextClassLoader);
		this.contextClassLoader = contextClassLoader;
	}

	public void setBundleContext(BundleContext context) {
		this.bundleContext = context;
	}

	/**
	 * Sets the OSGi service filter. The filter will be concatenated with the
	 * rest of the configuration properties specified (such as interfaces) so
	 * there is no need to include them in the filter.
	 * 
	 * @param filter OSGi filter describing the importing OSGi service
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * Sets the lifecycle listeners interested in receiving events for this
	 * importer.
	 * 
	 * @param listeners importer listeners
	 */
	public void setListeners(OsgiServiceLifecycleListener[] listeners) {
		this.listeners = listeners;
	}

	/**
	 * Sets the OSGi service bean name. This setting should be normally used
	 * when the imported service has been exported by Spring DM exporter. You
	 * may specify additional filtering criteria if needed (using the filter
	 * property) but this is not required.
	 * 
	 * @param serviceBeanName importer service bean name
	 */
	public void setServiceBeanName(String serviceBeanName) {
		this.serviceBeanName = serviceBeanName;
	}

	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Returns the class loader used by this FactoryBean.
	 * 
	 * @return factory bean class loader
	 */
	public ClassLoader getBeanClassLoader() {
		return classLoader;
	}

	/**
	 * Returns the bundleContext used by this FactoryBean.
	 * 
	 * @return factory bean class loader
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * Returns the interfaces used for discovering the imported service(s).
	 * 
	 * @return interfaces advertised by services in the OSGi space
	 */
	public Class[] getInterfaces() {
		return interfaces;
	}

	/**
	 * Returns the filter describing the imported service(s).
	 * 
	 * @return filter describing the imported service(s)
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * Returns the listeners interested in receiving events for this importer.
	 * 
	 * @return lifecycle listeners used by this importer
	 */
	public OsgiServiceLifecycleListener[] getListeners() {
		return listeners;
	}

	/**
	 * Returns the context class loader management strategy.
	 * 
	 * @return the context class loader management strategy
	 */
	public ImportContextClassLoader getContextClassLoader() {
		return contextClassLoader;
	}

}
