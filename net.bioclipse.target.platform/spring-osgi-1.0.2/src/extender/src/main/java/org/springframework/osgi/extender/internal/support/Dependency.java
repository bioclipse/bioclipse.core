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
package org.springframework.osgi.extender.internal.support;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.service.importer.support.AbstractOsgiServiceImportFactoryBean;

/**
 * Helper class indicating a service dependency.
 * 
 * @author Hal Hildebrand
 */
public class Dependency {
	protected String filterString;

	protected Filter filter;

	protected boolean isMandatory;

	protected BundleContext bundleContext;

	public Dependency(BundleContext bc, AbstractOsgiServiceImportFactoryBean reference) {
		filter = reference.getUnifiedFilter();
		filterString = filter.toString();
		isMandatory = reference.isMandatory();
		bundleContext = bc;
	}

	public boolean matches(ServiceEvent event) {
		return filter.match(event.getServiceReference());
	}

	public void appendTo(StringBuffer sb) {
		sb.append(filterString);
	}

	/**
	 * @return
	 */
	public boolean isSatisfied() {
		ServiceReference[] refs;
		try {
			refs = bundleContext.getServiceReferences(null, filterString);
		}
		catch (InvalidSyntaxException e) {
			throw (IllegalStateException) new IllegalStateException("Filter '" + filterString
					+ "' has invalid syntax: " + e.getMessage()).initCause(e);
		}
		return !isMandatory || (refs != null && refs.length != 0);
	}

	public String toString() {
		return "Dependency on [" + filter + "]";
	}

	/**
	 * @return Returns the filter.
	 */
	public Filter getFilter() {
		return filter;
	}

}