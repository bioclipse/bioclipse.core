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
package org.springframework.osgi.extender.internal.dependencies.startup;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.springframework.osgi.util.OsgiServiceReferenceUtils;

/**
 * @author Costin Leau
 * 
 */
public class ServiceDependency {
	protected final Filter filter;

	protected final String filterAsString;

	protected final boolean isMandatory;

	protected final BundleContext bundleContext;

	public ServiceDependency(BundleContext bc, Filter serviceFilter, boolean isMandatory) {
		filter = serviceFilter;
		this.filterAsString = filter.toString();
		this.isMandatory = isMandatory;
		bundleContext = bc;

	}

	public boolean matches(ServiceEvent event) {
		return filter.match(event.getServiceReference());
	}

	/**
	 * @return
	 */
	public boolean isServicePresent() {
		return (!isMandatory || OsgiServiceReferenceUtils.isServicePresent(bundleContext, filterAsString));
	}

	public String toString() {
		return "Dependency on [" + filterAsString + "]";
	}

	/**
	 * @return Returns the filter.
	 */
	public Filter getFilter() {
		return filter;
	}

}
