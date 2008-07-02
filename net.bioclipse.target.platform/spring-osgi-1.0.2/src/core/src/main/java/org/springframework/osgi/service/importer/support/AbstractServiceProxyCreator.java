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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.osgi.service.importer.internal.aop.ImportedOsgiServiceProxyAdvice;
import org.springframework.osgi.service.importer.internal.aop.ServiceTCCLInterceptor;
import org.springframework.osgi.service.importer.internal.aop.ServiceProxyCreator;
import org.springframework.osgi.util.DebugUtils;
import org.springframework.osgi.util.OsgiStringUtils;
import org.springframework.osgi.util.internal.ClassUtils;
import org.springframework.util.Assert;

/**
 * Internal (package visible) class used for handling common aspects in creating
 * a proxy over OSGi services.
 * 
 * Notably, this class creates common aspects such as publishing the
 * bundleContext on a thread-local or handling of thread context classloader.
 * 
 * @author Costin Leau
 */
abstract class AbstractServiceProxyCreator implements ServiceProxyCreator {

	private static final Log log = LogFactory.getLog(AbstractServiceProxyCreator.class);

	/** shared immutable interceptor for client TCCL selection (default) */
	private final Advice clientTCCLAdvice;

	/** shared immutable interceptor for publishing the client bundle context */
	private final Advice invokerBundleContextAdvice;

	/** importing bundle/client classLoader */
	protected final ClassLoader classLoader;

	/** proxy classes (for static generation) */
	protected final Class[] classes;

	/** client bundle context */
	protected final BundleContext bundleContext;

	private final ImportContextClassLoader iccl;

	AbstractServiceProxyCreator(Class[] classes, ClassLoader classLoader, BundleContext bundleContext,
			ImportContextClassLoader iccl) {
		Assert.notNull(bundleContext);
		Assert.notNull(classLoader);

		this.classes = classes;
		this.bundleContext = bundleContext;
		this.classLoader = classLoader;
		this.iccl = iccl;

		clientTCCLAdvice = new ServiceTCCLInterceptor(classLoader);
		invokerBundleContextAdvice = new LocalBundleContextAdvice(bundleContext);
	}

	public Object createServiceProxy(ServiceReference reference) {
		List advices = new ArrayList(4);

		// 1. the ServiceReference-like mixin
		Advice mixin = new ImportedOsgiServiceProxyAdvice(reference);
		advices.add(mixin);

		// 2. publication of bundleContext (if there is any)
		// FIXME: make this configurable (so it can be disabled)
		advices.add(invokerBundleContextAdvice);

		// 3. TCCL handling (if there is any)
		Advice tcclAdvice = determineTCCLAdvice(reference);

		if (tcclAdvice != null)
			advices.add(tcclAdvice);

		advices.add(createDispatcherInterceptor(reference));

		return createProxy(getInterfaces(reference), classLoader, bundleContext, advices);
	}

	private Advice determineTCCLAdvice(ServiceReference reference) {
		try {
			if (ImportContextClassLoader.CLIENT == iccl) {
				return clientTCCLAdvice;
			}
			else if (ImportContextClassLoader.SERVICE_PROVIDER == iccl) {
				return createServiceProviderTCCLAdvice(reference);
			}
			else if (ImportContextClassLoader.UNMANAGED == iccl) {
				// do nothing
				return null;
			}
			return null;

		}
		finally {
			if (log.isTraceEnabled()) {
				log.trace(iccl + " TCCL used for invoking " + OsgiStringUtils.nullSafeToString(reference));
			}
		}
	}

	private Object createProxy(Class[] classes, ClassLoader classLoader, BundleContext bundleContext, List advices) {
		ProxyFactory factory = new ProxyFactory();

		ClassUtils.configureFactoryForClass(factory, classes);

		for (Iterator iterator = advices.iterator(); iterator.hasNext();) {
			Advice advice = (Advice) iterator.next();
			factory.addAdvice(advice);
		}

		// no need to add optimize since it means implicit usage of CGLib always
		// which is determined automatically anyway
		// factory.setOptimize(true);
		factory.setFrozen(true);
		try {
			return factory.getProxy(classLoader);
		}
		catch (NoClassDefFoundError ncdfe) {
			DebugUtils.debugClassLoadingThrowable(ncdfe, bundleContext.getBundle(), classes);
			throw ncdfe;
		}
	}

	Class[] getInterfaces(ServiceReference reference) {
		return classes;
	}

	/**
	 * Create service provider TCCL advice. Subclasses should extend this based
	 * on their configuration (i.e. is a static proxy or is it dynamic).
	 * 
	 * @param reference service reference
	 * @return AOP advice
	 */
	abstract Advice createServiceProviderTCCLAdvice(ServiceReference reference);

	/**
	 * Create a dispatcher interceptor that actually execute the call on the
	 * target service.
	 * 
	 * @param reference service reference
	 * @return AOP advice
	 */
	abstract Advice createDispatcherInterceptor(ServiceReference reference);

}
