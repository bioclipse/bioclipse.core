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

package org.springframework.osgi.test.platform;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.framework.Felix;
import org.apache.felix.main.Main;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.test.internal.util.IOUtils;
import org.springframework.util.ClassUtils;

/**
 * Apache Felix (1.0.x) OSGi platform.
 * 
 * @author Costin Leau
 * 
 */
public class FelixPlatform extends AbstractOsgiPlatform {

	private static final Log log = LogFactory.getLog(FelixPlatform.class);

	private static final String FELIX_CONF_FILE = "felix.config.properties";

	private static final String FELIX_CONFIG_PROPERTY = "felix.config.properties";

	private static final String FELIX_PROFILE_DIR_PROPERTY = "felix.cache.profiledir";

	private BundleContext context;

	private Felix platform;

	private File felixStorageDir;


	public FelixPlatform() {
		toString = "Felix OSGi Platform";
	}

	protected Properties getPlatformProperties() {
		// load Felix configuration
		Properties props = new Properties();
		props.putAll(getFelixConfiguration());
		props.putAll(getLocalConfiguration());
		return props;
	}

	public BundleContext getBundleContext() {
		return context;
	}

	/**
	 * Configuration settings for the OSGi test run.
	 * 
	 * @return
	 */
	private Properties getLocalConfiguration() {
		Properties props = new Properties();

		felixStorageDir = createTempDir("felix");
		props.setProperty(FELIX_PROFILE_DIR_PROPERTY, this.felixStorageDir.getAbsolutePath());
		if (log.isTraceEnabled())
			log.trace("felix storage dir is " + felixStorageDir.getAbsolutePath());

		return props;
	}

	/**
	 * Loads Felix config.properties.
	 * 
	 * <strong>Note</strong> the current implementation uses Felix's Main class
	 * to resolve placeholders as opposed to loading the properties manually
	 * (through JDK's Properties class or Spring's PropertiesFactoryBean).
	 * 
	 * @return
	 */
	// TODO: this method should be removed once Felix 1.0.2 is released
	private Properties getFelixConfiguration() {
		String location = "/".concat(ClassUtils.classPackageAsResourcePath(FelixPlatform.class)).concat("/").concat(
			FELIX_CONF_FILE);
		URL url = FelixPlatform.class.getResource(location);
		if (url == null)
			throw new RuntimeException("cannot find felix configuration properties file:" + location);

		// used with Main
		System.getProperties().setProperty(FELIX_CONFIG_PROPERTY, url.toExternalForm());

		// load config.properties (use Felix's Main for resolving placeholders)
		return Main.loadConfigProperties();
	}

	public void start() throws Exception {

		platform = new Felix(getConfigurationProperties(), null);
		platform.start();

		Bundle systemBundle = platform;

		// call getBundleContext
		final Method getContext = systemBundle.getClass().getDeclaredMethod("getBundleContext", null);

		AccessController.doPrivileged(new PrivilegedAction() {

			public Object run() {
				getContext.setAccessible(true);
				return null;
			}
		});
		context = (BundleContext) getContext.invoke(systemBundle, null);
	}

	public void stop() throws Exception {
		try {
			platform.stop();
		}
		finally {
			// remove cache folder
			IOUtils.delete(felixStorageDir);
		}
	}

}
