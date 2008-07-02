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

import java.awt.Polygon;
import java.awt.Shape;

import junit.framework.TestCase;

import org.aopalliance.aop.Advice;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.osgi.mock.MockBundleContext;
import org.springframework.osgi.mock.MockServiceReference;
import org.springframework.osgi.service.importer.internal.aop.ServiceDynamicInterceptor;
import org.springframework.osgi.service.importer.internal.aop.ServiceStaticInterceptor;
import org.springframework.osgi.service.importer.internal.aop.ServiceTCCLInterceptor;
import org.springframework.osgi.service.importer.internal.support.RetryTemplate;
import org.springframework.util.ObjectUtils;

/**
 * @author Costin Leau
 * 
 */
public class OsgiServiceProxyEqualityTest extends TestCase {

	private Object target;

	private MockBundleContext bundleContext;

	private ClassLoader classLoader;

	/**
	 * Simple interface which declares equals.
	 * 
	 * @author Costin Leau
	 * 
	 */
	public static interface InterfaceWithEquals {

		int getCount();

		boolean equals(Object other);

		Object doSmth();
	}

	public static class Implementor implements InterfaceWithEquals {

		private int count = 0;

		public Implementor(int count) {
			this.count = count;
		}

		public Implementor() {
		}

		public Object doSmth() {
			return ObjectUtils.getIdentityHexString(this);
		}

		public int getCount() {
			return count;
		}

		public boolean equals(Object other) {
			if (this == other)
				return true;

			if (other instanceof InterfaceWithEquals) {
				InterfaceWithEquals oth = (InterfaceWithEquals) other;
				return getCount() == oth.getCount();
			}

			return false;
		}
	}

	ServiceReference ref; 
	protected void setUp() throws Exception {
		ref = new MockServiceReference();
		bundleContext = new MockBundleContext() {

			public ServiceReference getServiceReference(String clazz) {
				return ref;
			}

			public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
				return new ServiceReference[] { ref };
			}
		};

		classLoader = getClass().getClassLoader();
	}

	protected void tearDown() throws Exception {
		target = null;
		bundleContext = null;
	}

	private Object createProxy(Object target, Class intf, Advice[] advices) {
		ProxyFactory factory = new ProxyFactory();
		factory.addInterface(intf);
		if (advices != null)
			for (int i = 0; i < advices.length; i++) {
				factory.addAdvice(advices[0]);
			}

		factory.setTarget(target);
		return factory.getProxy();
	}

	private ServiceDynamicInterceptor createInterceptorWServiceRequired() {
		ServiceDynamicInterceptor interceptor = new ServiceDynamicInterceptor(bundleContext, null, classLoader);
		interceptor.setRequiredAtStartup(true);
		interceptor.setProxy(new Object());
		interceptor.afterPropertiesSet();
		return interceptor;
	}

	private ServiceDynamicInterceptor createInterceptorWOServiceRequired() {
		ServiceDynamicInterceptor interceptor = new ServiceDynamicInterceptor(bundleContext, null, classLoader);
		interceptor.setRequiredAtStartup(false);
		interceptor.setProxy(new Object());
		interceptor.afterPropertiesSet();
		return interceptor;

	}

	// TESTS on target W/O an equals defined on it

	public void testSameInterceptorEquality() throws Exception {
		target = new Polygon();

		Advice interceptor = createInterceptorWOServiceRequired();

		Object proxyA = createProxy(target, Shape.class, new Advice[] { interceptor });
		Object proxyB = createProxy(target, Shape.class, new Advice[] { interceptor });

		assertFalse(proxyA == proxyB);
		assertEquals(proxyA, proxyB);
	}

	public void testEqualsInterceptorsEquality() throws Exception {

		target = new Polygon();

		Advice interceptorA = createInterceptorWOServiceRequired();
		Advice interceptorB = createInterceptorWOServiceRequired();

		Object proxyA = createProxy(target, Shape.class, new Advice[] { interceptorA });
		Object proxyB = createProxy(target, Shape.class, new Advice[] { interceptorB });

		assertFalse(proxyA == proxyB);
		assertEquals(proxyA, proxyB);
		assertEquals(interceptorA, interceptorB);
	}

	public void testMultipleInterceptorEquality() throws Exception {
		target = new Polygon();

		Advice interceptorA1 = createInterceptorWOServiceRequired();

		Advice interceptorA2 = new LocalBundleContextAdvice(bundleContext);
		Advice interceptorA3 = new ServiceTCCLInterceptor(null);

		Advice interceptorB1 = createInterceptorWOServiceRequired();
		Advice interceptorB2 = new LocalBundleContextAdvice(bundleContext);
		Advice interceptorB3 = new ServiceTCCLInterceptor(null);

		Object proxyA = createProxy(target, Shape.class, new Advice[] { interceptorA1, interceptorA2, interceptorA3 });
		Object proxyB = createProxy(target, Shape.class, new Advice[] { interceptorB1, interceptorB2, interceptorB3 });

		assertFalse(proxyA == proxyB);
		assertEquals(interceptorA1, interceptorB1);
		assertEquals(interceptorA2, interceptorB2);
		assertEquals(interceptorA3, interceptorB3);

		assertEquals(proxyA, proxyB);
	}

	//
	// TESTS on object with an EQUAL defined on it
	//
	public void testDifferentInterceptorsButTargetHasEquals() throws Exception {
		target = new Implementor();
		bundleContext = new MockBundleContext() {
			public Object getService(ServiceReference reference) {
				return target;
			}
		};

		ServiceDynamicInterceptor interceptorA1 = createInterceptorWServiceRequired();
		interceptorA1.setRetryTemplate(new RetryTemplate(1, 10));

		Advice interceptorB1 = new ServiceStaticInterceptor(bundleContext, new MockServiceReference());

		InterfaceWithEquals proxyA = (InterfaceWithEquals) createProxy(target, InterfaceWithEquals.class,
			new Advice[] { interceptorA1 });
		InterfaceWithEquals proxyB = (InterfaceWithEquals) createProxy(target, InterfaceWithEquals.class,
			new Advice[] { interceptorB1 });

		assertFalse(proxyA == proxyB);
		assertFalse("interceptors should not be equal", interceptorA1.equals(interceptorB1));

		assertEquals(((InterfaceWithEquals) target).doSmth(), proxyA.doSmth());
		assertEquals(((InterfaceWithEquals) target).doSmth(), proxyB.doSmth());

		assertEquals(proxyA, proxyB);
	}

	public void testDifferentProxySetupButTargetHasEquals() throws Exception {
		target = new Implementor();

		Advice interceptorA1 = new LocalBundleContextAdvice(bundleContext);
		Advice interceptorB1 = new ServiceTCCLInterceptor(null);

		InterfaceWithEquals proxyA = (InterfaceWithEquals) createProxy(target, InterfaceWithEquals.class,
			new Advice[] { interceptorA1 });
		InterfaceWithEquals proxyB = (InterfaceWithEquals) createProxy(target, InterfaceWithEquals.class,
			new Advice[] { interceptorB1 });

		assertFalse(proxyA == proxyB);
		assertFalse("interceptors should not be equal", interceptorA1.equals(interceptorB1));

		assertEquals(((InterfaceWithEquals) target).doSmth(), proxyA.doSmth());
		assertEquals(((InterfaceWithEquals) target).doSmth(), proxyB.doSmth());

		assertEquals(proxyA, proxyB);
	}

}
