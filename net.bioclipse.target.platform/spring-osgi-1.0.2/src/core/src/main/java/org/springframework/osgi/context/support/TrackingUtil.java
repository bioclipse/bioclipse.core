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

package org.springframework.osgi.context.support;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.osgi.util.DebugUtils;
import org.springframework.osgi.util.OsgiFilterUtils;
import org.springframework.osgi.util.OsgiServiceReferenceUtils;
import org.springframework.osgi.util.internal.ClassUtils;

/**
 * Utility class for easy, but reliable, tracking of OSGi services. It relies on
 * the service tracker internally but wraps the logic into a proxy or cglib-like
 * class to ease usage.
 * 
 * <p/> This class can be seen as a much shorter, less featured version of
 * {@link org.springframework.osgi.service.importer.support.OsgiServiceProxyFactoryBean}.
 * It is intended for the bootstrap areas of the project where no classloading
 * or listeners are required.
 * 
 * @author Costin Leau
 * 
 */
abstract class TrackingUtil {

	/**
	 * Advice which fetches the target using the ServiceTracker service provided
	 * by the OSGi space.
	 * 
	 * @author Costin Leau
	 * 
	 */
	private static class MethodInvocationServiceAdvice implements MethodInterceptor {

		private final Object fallbackObject;

		private final BundleContext context;

		private final String filter;


		public MethodInvocationServiceAdvice(Object fallbackObject, BundleContext bundleContext, String filter) {
			this.fallbackObject = fallbackObject;
			this.context = bundleContext;
			this.filter = filter;
		}

		public Object invoke(MethodInvocation invocation) throws Throwable {
			Method m = invocation.getMethod();

			Object target = context.getService(OsgiServiceReferenceUtils.getServiceReference(context, filter));

			if (target == null)
				target = fallbackObject;

			// re-route call to the target
			return m.invoke(target, invocation.getArguments());
		}
	}


	private static final Log log = LogFactory.getLog(TrackingUtil.class);


	public static Object getService(Class[] classes, String filter, ClassLoader classLoader, BundleContext context,
			Object fallbackObject) {
		ProxyFactory factory = new ProxyFactory();

		// mold the proxy
		ClassUtils.configureFactoryForClass(factory, classes);

		String flt = OsgiFilterUtils.unifyFilter(classes, filter);
		factory.addAdvice(new MethodInvocationServiceAdvice(fallbackObject, context, flt));

		try {
			return factory.getProxy(classLoader);
		}
		catch (NoClassDefFoundError ncdfe) {
			if (log.isWarnEnabled()) {
				DebugUtils.debugClassLoadingThrowable(ncdfe, context.getBundle(), classes);
			}
			throw ncdfe;
		}
	}

}
