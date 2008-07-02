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
package org.springframework.osgi.mock;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * @author Costin Leau
 * 
 */
public class MockServiceReferenceTest extends TestCase {

	MockServiceReference mock;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		mock = new MockServiceReference();
	}

	/**
	 * Test method for
	 * {@link org.springframework.osgi.mock.MockServiceReference#MockServiceReference()}.
	 */
	public void testMockServiceReference() {
		assertNotNull(mock.getBundle());
		assertNotNull(mock.getPropertyKeys());
	}

	/**
	 * Test method for
	 * {@link org.springframework.osgi.mock.MockServiceReference#MockServiceReference(org.osgi.framework.Bundle)}.
	 */
	public void testMockServiceReferenceBundle() {
		Bundle bundle = new MockBundle();
		mock = new MockServiceReference(bundle);

		assertSame(bundle, mock.getBundle());
		assertNotNull(mock.getPropertyKeys());

	}

	/**
	 * Test method for
	 * {@link org.springframework.osgi.mock.MockServiceReference#MockServiceReference(org.osgi.framework.Bundle, java.util.Hashtable, org.osgi.framework.ServiceRegistration)}.
	 */
	public void testMockServiceReferenceBundleHashtable() {
		mock = new MockServiceReference(null, null, null);
		assertNotNull(mock.getBundle());
		assertNotNull(mock.getPropertyKeys());
	}

	/**
	 * Test method for
	 * {@link org.springframework.osgi.mock.MockServiceReference#getBundle()}.
	 */
	public void testGetBundle() {
		assertNotNull(mock.getBundle());
	}

	/**
	 * Test method for
	 * {@link org.springframework.osgi.mock.MockServiceReference#getProperty(java.lang.String)}.
	 */
	public void testGetProperty() {
		assertNull(mock.getProperty("foo"));
	}

	/**
	 * Test method for
	 * {@link org.springframework.osgi.mock.MockServiceReference#getPropertyKeys()}.
	 */
	public void testGetPropertyKeys() {
		assertNotNull(mock.getPropertyKeys());
	}

	/**
	 * Test method for
	 * {@link org.springframework.osgi.mock.MockServiceReference#getUsingBundles()}.
	 */
	public void testGetUsingBundles() {
		assertNotNull(mock.getUsingBundles());
	}

	/**
	 * Test method for
	 * {@link org.springframework.osgi.mock.MockServiceReference#isAssignableTo(org.osgi.framework.Bundle, java.lang.String)}.
	 */
	public void testIsAssignableTo() {
		assertFalse(mock.isAssignableTo(new MockBundle(), "foo"));
	}

	/**
	 * Test method for
	 * {@link org.springframework.osgi.mock.MockServiceReference#setProperties(java.util.Dictionary)}.
	 */
	public void testSetProperties() {
		Dictionary props = new Hashtable();
		String key = "foo";
		Object value = new Object();
		props.put(key, value);

		assertNull(mock.getProperty(key));
		mock.setProperties(props);
		assertSame(value, mock.getProperty(key));
	}

	public void testMandatoryProperties() {
		Object serviceId = mock.getProperty(Constants.SERVICE_ID);
		assertNotNull(serviceId);
		assertTrue(serviceId instanceof Long);
		Object objectClass = mock.getProperty(Constants.OBJECTCLASS);
		assertNotNull(objectClass);
		assertTrue(objectClass instanceof String[]);
	}

	public void testMandatoryPropertiesDontChange() {
		Object serviceId = mock.getProperty(Constants.SERVICE_ID);
		Object objectClass = mock.getProperty(Constants.OBJECTCLASS);

		mock.setProperties(new Hashtable());
		assertSame(serviceId, mock.getProperty(Constants.SERVICE_ID));
		assertSame(objectClass, mock.getProperty(Constants.OBJECTCLASS));

		Dictionary anotherDict = new Hashtable();
		anotherDict.put(Constants.SERVICE_ID, new Long(1234));
		anotherDict.put(Constants.OBJECTCLASS, new String[] { Date.class.getName() });
		mock.setProperties(anotherDict);

		assertSame(serviceId, mock.getProperty(Constants.SERVICE_ID));
		assertSame(objectClass, mock.getProperty(Constants.OBJECTCLASS));
	}

}
