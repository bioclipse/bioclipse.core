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

package org.springframework.osgi.iandt.io;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.osgi.iandt.BaseIntegrationTest;
import org.springframework.osgi.io.OsgiBundleResourceLoader;
import org.springframework.osgi.io.OsgiBundleResourcePatternResolver;
import org.springframework.util.ObjectUtils;

/**
 * Common base test class for IO integration testing.
 * 
 * @author Costin Leau
 * 
 */
public abstract class BaseIoTest extends BaseIntegrationTest {

	protected final static String PACKAGE = "org/springframework/osgi/iandt/io/";

	protected Resource thisClass;

	protected ResourceLoader loader, defaultLoader;

	protected ResourcePatternResolver patternLoader;

	protected Bundle bundle;


	protected String[] getBundleContentPattern() {
		return (String[]) ObjectUtils.addObjectToArray(super.getBundleContentPattern(),
			"org/springframework/osgi/iandt/io/BaseIoTest.class");
	}

	protected void onSetUp() throws Exception {
		// load file using absolute path
		defaultLoader = new DefaultResourceLoader();
		thisClass = defaultLoader.getResource(getClass().getName().replace('.', '/').concat(".class"));
		bundle = bundleContext.getBundle();
		loader = new OsgiBundleResourceLoader(bundle);
		patternLoader = new OsgiBundleResourcePatternResolver(loader);

	}

	protected void onTearDown() throws Exception {
		thisClass = null;
	}

	protected String getManifestLocation() {
		// reuse the manifest from Fragment Io Tests
		return "org/springframework/osgi/iandt/io/FragmentIoTests.MF";
	}

	/**
	 * Add a bundle fragment.
	 */
	protected String[] getTestBundlesNames() {
		return new String[] {
			"org.springframework.osgi,org.springframework.osgi.iandt.io.fragment.1.bundle," + getSpringDMVersion(),
			"org.springframework.osgi,org.springframework.osgi.iandt.io.fragment.2.bundle," + getSpringDMVersion() };
	}

	protected Object[] copyEnumeration(Enumeration enm) {
		List list = new ArrayList();
		while (enm != null && enm.hasMoreElements())
			list.add(enm.nextElement());
		return list.toArray();
	}

	protected void assertResourceArray(Object[] array, int expectedSize) {
		System.out.println(ObjectUtils.nullSafeToString(array));
		assertTrue("found only " + ObjectUtils.nullSafeToString(array), array.length == expectedSize);
	}

	protected boolean isKF() {
		return (createPlatform().toString().startsWith("Knopflerfish"));
	}

	protected boolean isEquinox() {
		return (createPlatform().toString().startsWith("Equinox"));
	}

	protected boolean isFelix() {
		return (createPlatform().toString().startsWith("Felix"));
	}
}
