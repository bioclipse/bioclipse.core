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
import java.util.Iterator;
import java.util.Properties;

import org.knopflerfish.framework.Framework;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.core.JdkVersion;
import org.springframework.core.io.ClassPathResource;
import org.springframework.osgi.test.internal.util.IOUtils;

/**
 * Knopflerfish 2.0.x Platform.
 * 
 * @author Costin Leau
 */
public class KnopflerfishPlatform extends AbstractOsgiPlatform {

	private BundleContext context;

	private Framework framework;

	private File kfStorageDir;

	private static final String ROOT = "/org/springframework/osgi/test/platform/";

	private static final String PKGS_1_4 = ROOT + "kf.packages1.4.txt";

	private static final String PKGS_1_5 = ROOT + "kf.packages1.5.txt";


	public KnopflerfishPlatform() {
		toString = "Knopflerfish OSGi Platform";
	}

	protected Properties getPlatformProperties() {
		kfStorageDir = createTempDir("kf");

		// default properties
		Properties props = new Properties();
		props.setProperty("org.osgi.framework.dir", kfStorageDir.getAbsolutePath());
		props.setProperty("org.knopflerfish.framework.bundlestorage", "file");
		props.setProperty("org.knopflerfish.framework.bundlestorage.file.reference", "true");
		props.setProperty("org.knopflerfish.framework.bundlestorage.file.unpack", "false");
		props.setProperty("org.knopflerfish.startlevel.use", "true");
		props.setProperty("org.knopflerfish.osgi.setcontextclassloader", "true");
		// embedded mode
		props.setProperty("org.knopflerfish.framework.exitonshutdown", "false");
		// disable patch CL
		props.setProperty("org.knopflerfish.framework.patch", "false");

		// load system packages
		props.setProperty(Constants.FRAMEWORK_SYSTEMPACKAGES, loadSystemPackages());
		return props;
	}

	protected String loadSystemPackages() {
		Properties pckgs = new Properties();
		String pckgFile = PKGS_1_4;

		if (JdkVersion.isAtLeastJava15())
			pckgFile = PKGS_1_5;

		try {
			pckgs.load(new ClassPathResource(pckgFile).getInputStream());
		}
		catch (Exception ex) {
			log.warn("cannot load system packages", ex);
		}
		StringBuffer bf = new StringBuffer();

		for (Iterator iterator = pckgs.keySet().iterator(); iterator.hasNext();) {
			bf.append(iterator.next());

			if (iterator.hasNext())
				bf.append(",");
		}

		if (log.isTraceEnabled())
			log.trace("loaded system properties [" + bf + "]");
		return bf.toString();
	}

	public BundleContext getBundleContext() {
		return context;
	}

	public void start() throws Exception {
		// copy configuration properties to sys properties
		System.getProperties().putAll(getConfigurationProperties());

		framework = new Framework(this);
		framework.launch(0);
		context = framework.getSystemBundleContext();
	}

	public void stop() throws Exception {
		try {
			framework.shutdown();
		}
		finally {
			IOUtils.delete(kfStorageDir);
		}
	}
}
