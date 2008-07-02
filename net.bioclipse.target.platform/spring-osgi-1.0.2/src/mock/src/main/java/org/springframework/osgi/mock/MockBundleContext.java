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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * BundleContext mock.
 * 
 * <p/> Can be configured to use a predefined Bundle or/and configuration. By
 * default, will create an internal MockBundle. Most of the operations are no-op
 * (as annonymous classes with specific functionality can be created per use
 * basis).
 * 
 * @author Costin Leau
 * 
 */
public class MockBundleContext implements BundleContext {

	public static final Properties DEFAULT_PROPERTIES = new DefaultBundleContextProperties();

	private Bundle bundle;

	private Properties properties;

	protected Set serviceListeners, bundleListeners;

	public MockBundleContext() {
		this(null, null);
	}

	public MockBundleContext(Bundle bundle) {
		this(bundle, null);
	}

	public MockBundleContext(Bundle bundle, Properties props) {
		this.bundle = (bundle == null ? new MockBundle() : bundle);
		properties = new Properties(DEFAULT_PROPERTIES);
		if (props != null)
			properties.putAll(props);

		// make sure the order is preserved
		this.serviceListeners = new LinkedHashSet(2);
		this.bundleListeners = new LinkedHashSet(2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#addBundleListener(org.osgi.framework.BundleListener)
	 */
	public void addBundleListener(BundleListener listener) {
		bundleListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#addFrameworkListener(org.osgi.framework.FrameworkListener)
	 */
	public void addFrameworkListener(FrameworkListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#addServiceListener(org.osgi.framework.ServiceListener)
	 */
	public void addServiceListener(ServiceListener listener) {
		try {
			addServiceListener(listener, null);
		}
		catch (InvalidSyntaxException ex) {
			throw new IllegalStateException("exception should not occur");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#addServiceListener(org.osgi.framework.ServiceListener,
	 * java.lang.String)
	 */
	public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
		if (listener == null)
			throw new IllegalArgumentException("non-null listener required");
		this.serviceListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#createFilter(java.lang.String)
	 */
	public Filter createFilter(String filter) throws InvalidSyntaxException {
		return new MockFilter(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#getAllServiceReferences(java.lang.String,
	 * java.lang.String)
	 */
	public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
		return new ServiceReference[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#getBundle()
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#getBundle(long)
	 */
	public Bundle getBundle(long id) {
		return bundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#getBundles()
	 */
	public Bundle[] getBundles() {
		return new Bundle[] { bundle };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#getDataFile(java.lang.String)
	 */
	public File getDataFile(String filename) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#getProperty(java.lang.String)
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#getService(org.osgi.framework.ServiceReference)
	 */
	public Object getService(ServiceReference reference) {
		return new Object();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#getServiceReference(java.lang.String)
	 */
	public ServiceReference getServiceReference(String clazz) {
		return new MockServiceReference(getBundle(), new String[] { clazz });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#getServiceReferences(java.lang.String,
	 * java.lang.String)
	 */
	public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
		// Some jiggery-pokery to get round the fact that we don't ever use the
		// clazz
		if (clazz == null)
			if (filter != null) {
				{
					int i = filter.indexOf(Constants.OBJECTCLASS + "=");
					if (i > 0) {
						clazz = filter.substring(i + Constants.OBJECTCLASS.length() + 1);
						clazz = clazz.substring(0, clazz.indexOf(")"));
					}
				}
			}
			else
				clazz = Object.class.getName();
		return new ServiceReference[] { new MockServiceReference(getBundle(), new String[] { clazz }) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#installBundle(java.lang.String)
	 */
	public Bundle installBundle(String location) throws BundleException {
		MockBundle bundle = new MockBundle();
		bundle.setLocation(location);
		return bundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#installBundle(java.lang.String,
	 * java.io.InputStream)
	 */
	public Bundle installBundle(String location, InputStream input) throws BundleException {
		try {
			input.close();
		}
		catch (IOException ex) {
			throw new BundleException("cannot close stream", ex);
		}
		return installBundle(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#registerService(java.lang.String[],
	 * java.lang.Object, java.util.Dictionary)
	 */
	public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) {
		MockServiceRegistration reg = new MockServiceRegistration(properties);

		// disabled for now
		// MockServiceReference ref = new MockServiceReference(this.bundle,
		// properties, reg, clazzes);
		// ServiceEvent event = new ServiceEvent(ServiceEvent.REGISTERED, ref);
		//
		// for (Iterator iter = serviceListeners.iterator(); iter.hasNext();) {
		// ServiceListener listener = (ServiceListener) iter.next();
		// listener.serviceChanged(event);
		// }

		return reg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#registerService(java.lang.String,
	 * java.lang.Object, java.util.Dictionary)
	 */
	public ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
		return registerService(new String[] { clazz }, service, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#removeBundleListener(org.osgi.framework.BundleListener)
	 */
	public void removeBundleListener(BundleListener listener) {
		bundleListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#removeFrameworkListener(org.osgi.framework.FrameworkListener)
	 */
	public void removeFrameworkListener(FrameworkListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#removeServiceListener(org.osgi.framework.ServiceListener)
	 */
	public void removeServiceListener(ServiceListener listener) {
		serviceListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleContext#ungetService(org.osgi.framework.ServiceReference)
	 */
	public boolean ungetService(ServiceReference reference) {
		return false;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	// hooks

	/**
	 * Handy method when mocking with listeners is required.
	 */
	public Set getServiceListeners() {
		return serviceListeners;
	}

	public Set getBundleListeners() {
		return bundleListeners;
	}

}
