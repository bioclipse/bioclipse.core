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

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * ServiceReference mock.
 * 
 * <p/> This mock tries to adhere to the OSGi spec as much as possible by
 * providing the mandatory serviceId properties such as
 * {@link Constants#SERVICE_ID}, {@link Constants#OBJECTCLASS} and
 * {@link Constants#SERVICE_RANKING}.
 * 
 * @author Costin Leau
 * 
 */
public class MockServiceReference implements ServiceReference {

	private Bundle bundle;

	private static long GLOBAL_SERVICE_ID = System.currentTimeMillis();

	private long serviceId;

	// private ServiceRegistration registration;
	private Dictionary properties;

	private String[] objectClass = new String[] { Object.class.getName() };

	public MockServiceReference() {
		this(null, null, null);
	}

	public MockServiceReference(Bundle bundle) {
		this(bundle, null, null);
	}

	public MockServiceReference(String[] classes) {
		this(null, null, null, classes);

	}

	public MockServiceReference(Bundle bundle, String[] classes) {
		this(bundle, null, null, classes);
	}

	public MockServiceReference(ServiceRegistration registration) {
		this(null, null, registration);
	}

	public MockServiceReference(Bundle bundle, Dictionary properties, ServiceRegistration registration) {
		this(bundle, properties, registration, null);
	}

	public MockServiceReference(Bundle bundle, Dictionary properties, ServiceRegistration registration, String[] classes) {
		this.bundle = (bundle == null ? new MockBundle() : bundle);
		// this.registration = (registration == null ? new
		// MockServiceRegistration() :
		// registration);
		this.properties = (properties == null ? new Hashtable() : properties);
		if (classes != null && classes.length > 0)
			this.objectClass = classes;
		addMandatoryProperties(this.properties);
	}

	private void addMandatoryProperties(Dictionary dict) {
		// add mandatory properties
		Object id = dict.get(Constants.SERVICE_ID);
		if (id == null || !(id instanceof Long))
			dict.put(Constants.SERVICE_ID, new Long(GLOBAL_SERVICE_ID++));

		if (dict.get(Constants.OBJECTCLASS) == null)
			dict.put(Constants.OBJECTCLASS, objectClass);

		Object ranking = dict.get(Constants.SERVICE_RANKING);
		if (ranking == null || !(ranking instanceof Integer))
			dict.put(Constants.SERVICE_RANKING, new Integer(0));

		serviceId = ((Long) dict.get(Constants.SERVICE_ID)).longValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.ServiceReference#getBundle()
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.ServiceReference#getProperty(java.lang.String)
	 */
	public Object getProperty(String key) {
		return properties.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.ServiceReference#getPropertyKeys()
	 */
	public String[] getPropertyKeys() {
		String[] keys = new String[this.properties.size()];
		Enumeration ks = this.properties.keys();

		for (int i = 0; i < keys.length && ks.hasMoreElements(); i++) {
			keys[i] = (String) ks.nextElement();
		}

		return keys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.ServiceReference#getUsingBundles()
	 */
	public Bundle[] getUsingBundles() {
		return new Bundle[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.ServiceReference#isAssignableTo(org.osgi.framework.Bundle,
	 * java.lang.String)
	 */
	public boolean isAssignableTo(Bundle bundle, String className) {
		return false;
	}

	public void setProperties(Dictionary properties) {
		/*
		 * Enumeration keys = props.keys(); while (keys.hasMoreElements())
		 * this.properties.remove(keys.nextElement());
		 * 
		 * Enumeration enm = props.keys(); while (enm.hasMoreElements()) {
		 * Object key = enm.nextElement(); this.properties.put(key,
		 * props.get(key)); }
		 */

		if (properties != null) {
			// copy mandatory properties
			properties.put(Constants.SERVICE_ID, this.properties.get(Constants.SERVICE_ID));
			properties.put(Constants.OBJECTCLASS, this.properties.get(Constants.OBJECTCLASS));
			// optional property
			if (properties.get(Constants.SERVICE_RANKING) == null)
				properties.put(Constants.SERVICE_RANKING, this.properties.get(Constants.SERVICE_RANKING));

			this.properties = properties;
		}
	}

	/**
	 * Two mock service references are equal if they contain the same service
	 * id.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof MockServiceReference) {
			return this.hashCode() == ((MockServiceReference) obj).hashCode();
		}
		return false;
	}

	/**
	 * Return a hash code based on the class and service id.
	 */
	public int hashCode() {
		return MockServiceReference.class.hashCode() * 13 + (int) serviceId;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "mock service reference [owning bundle id=" + bundle.hashCode() + "|props : " + properties + "]";
	}

}
