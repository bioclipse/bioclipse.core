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

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.service.importer.ImportedOsgiServiceProxy;
import org.springframework.util.Assert;

/**
 * {@link ServiceReference} adapter using a {@link ImportedOsgiServiceProxy}
 * internally for delegation.
 * 
 * @author Costin Leau
 * 
 */
class ServiceReferenceDelegate implements ServiceReference {
	private final ImportedOsgiServiceProxy delegate;

	public ServiceReferenceDelegate(ImportedOsgiServiceProxy delegate) {
		Assert.notNull(delegate, "delegate object should not be null");
		this.delegate = delegate;
	}

	public Bundle getBundle() {
		return delegate.getServiceReference().getBundle();
	}

	public Object getProperty(String key) {
		return delegate.getServiceReference().getProperty(key);
	}

	public String[] getPropertyKeys() {
		return delegate.getServiceReference().getPropertyKeys();
	}

	public Bundle[] getUsingBundles() {
		return delegate.getServiceReference().getUsingBundles();
	}

	public boolean isAssignableTo(Bundle bundle, String className) {
		return delegate.getServiceReference().isAssignableTo(bundle, className);
	}

	public String toString() {
		return "ServiceReference wrapper for " + delegate.getServiceReference();
	}
}
