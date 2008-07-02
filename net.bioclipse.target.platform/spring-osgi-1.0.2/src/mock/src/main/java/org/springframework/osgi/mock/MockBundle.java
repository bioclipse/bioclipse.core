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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * Bundle Mock.
 * 
 * <p/> The mock will the thread current classloader for loading
 * classes/resources.
 * 
 * @author Costin Leau
 * 
 */
public class MockBundle implements Bundle {

	private String location;

	private Dictionary headers;

	private static int GENERAL_BUNDLE_ID = 0;
	
	private long bundleId = (GENERAL_BUNDLE_ID++);

	// required for introspection by util classes (should be removed)
	private BundleContext bundleContext;

	private ClassLoader loader = getClass().getClassLoader();

	private Dictionary defaultHeaders = new Hashtable(0);

	private final String SYMBOLIC_NAME = "Mock-Bundle_" + System.currentTimeMillis();

	private final String symName;

	private static class EmptyEnumeration implements Enumeration {
		public boolean hasMoreElements() {
			return false;
		}

		public Object nextElement() {
			throw new NoSuchElementException();
		}
	}

	public MockBundle() {
		this(null, null, null);
	}

	public MockBundle(Dictionary headers) {
		this(null, headers, null);
	}

	public MockBundle(BundleContext context) {
		this(null, null, context);
	}

	public MockBundle(String symName) {
		this(symName, null, null);
	}

	public MockBundle(String symName, Dictionary headers, BundleContext context) {
		this.symName = ((symName != null && symName.length() > 0) ? symName : SYMBOLIC_NAME);
		defaultHeaders.put("Bundle-SymbolicName", this.symName);

		this.location = "<default location>";
		this.headers = (headers == null ? defaultHeaders : headers);
		this.bundleContext = (context == null ? new MockBundleContext(this) : context);
	}

	/**
	 * Delegates to the classloader. Identical to classLoader.getResources(path +
	 * filePattern);
	 * 
	 * @see org.osgi.framework.Bundle#findEntries(java.lang.String,
	 * java.lang.String, boolean)
	 */
	public Enumeration findEntries(String path, String filePattern, boolean recurse) {
		Enumeration enm = null;

		try {
			enm = loader.getResources(path + "/" + filePattern);
		}
		catch (IOException ex) {
			// catch to allow nice behavior
			System.err.println("returning an empty enumeration as cannot load resource; exception " + ex);
		}
		return (enm == null ? new EmptyEnumeration() : enm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getBundleId()
	 */
	public long getBundleId() {
		return this.bundleId;
	}

	public void setBundleId(long bundleId) {
		this.bundleId = bundleId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getEntry(java.lang.String)
	 */
	public URL getEntry(String name) {
		return loader.getResource(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getEntryPaths(java.lang.String)
	 */
	public Enumeration getEntryPaths(String path) {
		return new EmptyEnumeration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getHeaders()
	 */
	public Dictionary getHeaders() {
		return headers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getHeaders(java.lang.String)
	 */
	public Dictionary getHeaders(String locale) {
		return getHeaders();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getLastModified()
	 */
	public long getLastModified() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getLocation()
	 */
	public String getLocation() {
		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getRegisteredServices()
	 */
	public ServiceReference[] getRegisteredServices() {
		return new ServiceReference[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getResource(java.lang.String)
	 */
	public URL getResource(String name) {
		return loader.getResource(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getResources(java.lang.String)
	 */
	public Enumeration getResources(String name) throws IOException {
		return loader.getResources(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getServicesInUse()
	 */
	public ServiceReference[] getServicesInUse() {
		return new ServiceReference[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getState()
	 */
	public int getState() {
		return Bundle.ACTIVE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#getSymbolicName()
	 */
	public String getSymbolicName() {
		String name = (String) headers.get(Constants.BUNDLE_SYMBOLICNAME);
		return (name == null ? SYMBOLIC_NAME : name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#hasPermission(java.lang.Object)
	 */
	public boolean hasPermission(Object permission) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#loadClass(java.lang.String)
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		return loader.loadClass(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#start()
	 */
	public void start() throws BundleException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#stop()
	 */
	public void stop() throws BundleException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#uninstall()
	 */
	public void uninstall() throws BundleException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#update()
	 */
	public void update() throws BundleException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.Bundle#update(java.io.InputStream)
	 */
	public void update(InputStream in) throws BundleException {
	}

	// chiefly here so that compilers/find-bugs don't complain about the
	// "unused" bundleContext field.
	// also enables OsgiResoureUtils.getBundleContext to find the context via
	// reflection
	public BundleContext getContext() {
		return this.bundleContext;
	}

	public BundleContext getBundleContext() {
		return getContext();
	}

	public String toString() {
		return symName;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
