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

import java.lang.reflect.Modifier;

import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.osgi.service.importer.internal.aop.ServiceStaticInterceptor;
import org.springframework.osgi.service.importer.internal.aop.ServiceTCCLInterceptor;
import org.springframework.osgi.util.BundleDelegatingClassLoader;
import org.springframework.osgi.util.OsgiServiceReferenceUtils;
import org.springframework.osgi.util.internal.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author Costin Leau
 * 
 */
class StaticServiceProxyCreator extends AbstractServiceProxyCreator {

	private static final Log log = LogFactory.getLog(StaticServiceProxyCreator.class);

	/**
	 * Constructs a new <code>StaticServiceProxyCreator</code> instance.
	 * 
	 * @param classes
	 * @param classLoader
	 * @param bundleContext
	 * @param iccl
	 */
	StaticServiceProxyCreator(Class[] classes, ClassLoader classLoader, BundleContext bundleContext,
			ImportContextClassLoader iccl) {
		super(classes, classLoader, bundleContext, iccl);
	}

	Advice createDispatcherInterceptor(ServiceReference reference) {
		return new ServiceStaticInterceptor(bundleContext, reference);
	}

	Advice createServiceProviderTCCLAdvice(ServiceReference reference) {
		Bundle bundle = reference.getBundle();
		// if reference is dead already, it's impossible to provide the service
		// classloader
		if (bundle == null)
			return null;

		return new ServiceTCCLInterceptor(BundleDelegatingClassLoader.createBundleClassLoaderFor(bundle, ProxyFactory.class.getClassLoader()));
	}

	Class[] discoverProxyClasses(ServiceReference ref) {
		String[] classNames = OsgiServiceReferenceUtils.getServiceObjectClasses(ref);

		// try to get as many interfaces as possible
		Class[] classes = ClassUtils.loadClasses(classNames, classLoader);
		// exclude final classes
		classes = ClassUtils.excludeClassesWithModifier(classes, Modifier.FINAL);
		// remove class duplicates/parents
		classes = ClassUtils.removeParents(classes);

		return classes;
	}

	Class[] getInterfaces(ServiceReference reference) {
		Class[] clazzes = discoverProxyClasses(reference);
		if (log.isTraceEnabled())
			log.trace("generating 'greedy' service proxy using classes " + ObjectUtils.nullSafeToString(clazzes)
					+ " over " + ObjectUtils.nullSafeToString(this.classes));
		return clazzes;

	}

}
