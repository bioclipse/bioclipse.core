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
package org.springframework.osgi.compendium.internal;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.util.internal.MapBasedDictionary;
import org.springframework.util.Assert;

/**
 * Osgi Property Placeholder. Allows reading of properties from an OSGi
 * Configuration Admin Service as well as dynamic updates on interested beans.
 * 
 * @author Costin Leau
 * 
 */
public class OsgiPropertyPlaceholder extends PropertyPlaceholderConfigurer implements BundleContextAware,
		InitializingBean {

	private String persistentId;

	private BundleContext bundleContext;

	private Properties cmProperties;

	public String getPersistentId() {
		return persistentId;
	}

	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(persistentId, "peristentId property is required");
		Assert.notNull(bundleContext, "bundleContext property is required");

		ConfigurationAdmin admin = retrieveConfigurationAdmin(bundleContext);
		Configuration config = admin.getConfiguration(persistentId);

		// wrap configuration object as the backing properties of the
		// placeholder
		cmProperties = new Properties();
		Dictionary dict = config.getProperties();
		if (dict == null) {
			dict = new MapBasedDictionary();
		}

		// copy dictionary into properties
		for (Enumeration keys = dict.keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			cmProperties.put(key, dict.get(key));
		}

		// add properties to the existing ones (to allow overriding rules to
		// apply)
	}

	protected Properties mergeProperties() throws IOException {
		// add local properties as defaults
		Properties prop = new Properties(super.mergeProperties());

		// add the OSGi properties on top
		if (cmProperties != null)
			prop.putAll(cmProperties);
		return prop;
	}

	protected ConfigurationAdmin retrieveConfigurationAdmin(BundleContext bundleContext) {
		ServiceReference adminRef = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
		Assert.notNull(adminRef, "ConfigurationAdmin service reference could not be found");

		Object service = bundleContext.getService(adminRef);
		Assert.notNull(service, "ConfigurationAdmin Service could not be found");
		Assert.isInstanceOf(ConfigurationAdmin.class, service, "service " + service + " is not an instance of "
				+ ConfigurationAdmin.class.getName());

		return (ConfigurationAdmin) service;
	}

	public void setBundleContext(BundleContext context) {
		this.bundleContext = context;
	}

}
