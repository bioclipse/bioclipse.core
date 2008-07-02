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

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.springframework.osgi.mock.MockBundleContext;
import org.springframework.osgi.mock.MockServiceReference;
import org.springframework.util.CollectionUtils;

/**
 * Unit test regarding the importing of services inside a collection.
 * 
 * @author Costin Leau
 */
public class GreedyProxyTest extends TestCase {
	private StaticServiceProxyCreator proxyCreator;

	private String[] classesAsStrings = new String[] { Serializable.class.getName(), Comparable.class.getName() };

	protected void setUp() throws Exception {

		ClassLoader cl = getClass().getClassLoader();
		BundleContext ctx = new MockBundleContext();
		Class[] classes = new Class[] { Serializable.class, Comparable.class };

		proxyCreator = new StaticServiceProxyCreator(classes, cl, ctx, ImportContextClassLoader.UNMANAGED);
	}

	protected void tearDown() throws Exception {
		proxyCreator = null;
	}

	private String[] addExtraIntfs(String[] extraIntfs) {
		List list = new ArrayList();
		CollectionUtils.mergeArrayIntoCollection(extraIntfs, list);
		CollectionUtils.mergeArrayIntoCollection(classesAsStrings, list);
		return (String[]) list.toArray(new String[list.size()]);
	}

	private boolean containsClass(Class[] classes, Class clazz) {
		for (int i = 0; i < classes.length; i++) {
			if (clazz.equals(classes[i]))
				return true;

		}
		return false;
	}

	public void testMoreInterfacesAvailable() throws Exception {
		String[] extraClasses = new String[] { Cloneable.class.getName(), Runnable.class.getName() };

		MockServiceReference ref = new MockServiceReference(addExtraIntfs(extraClasses));

		Class[] clazzes = proxyCreator.discoverProxyClasses(ref);
		assertTrue(containsClass(clazzes, Cloneable.class));
		assertTrue(containsClass(clazzes, Runnable.class));
		assertTrue(containsClass(clazzes, Serializable.class));
	}

	public void testNonVisibleOrInvalidInterfacesFound() throws Exception {
		String[] extraClasses = new String[] { "a", "nonExistingClass" };

		MockServiceReference ref = new MockServiceReference(addExtraIntfs(extraClasses));

		Class[] clazzes = proxyCreator.discoverProxyClasses(ref);
		assertEquals(2, clazzes.length);
		assertTrue(containsClass(clazzes, Serializable.class));
		assertTrue(containsClass(clazzes, Comparable.class));
	}

	public void testParentInterfaces() throws Exception {
		String[] extraClasses = new String[] { Object.class.getName(), Cloneable.class.getName(), Date.class.getName(),
				Time.class.getName() };

		MockServiceReference ref = new MockServiceReference(addExtraIntfs(extraClasses));
		Class[] clazzes = proxyCreator.discoverProxyClasses(ref);
		assertEquals(1, clazzes.length);
		assertFalse(containsClass(clazzes, Date.class));
		assertFalse(containsClass(clazzes, Cloneable.class));
		assertTrue(containsClass(clazzes, Time.class));
	}

	public void testExcludeFinalClass() throws Exception {
		String[] extraClasses = new String[] { Object.class.getName(), Byte.class.getName() };
		MockServiceReference ref = new MockServiceReference(addExtraIntfs(extraClasses));
		Class[] clazzes = proxyCreator.discoverProxyClasses(ref);
		assertEquals(2, clazzes.length);
		assertFalse(containsClass(clazzes, Byte.class));
		assertTrue(containsClass(clazzes, Comparable.class));
		assertTrue(containsClass(clazzes, Serializable.class));
	}

}
