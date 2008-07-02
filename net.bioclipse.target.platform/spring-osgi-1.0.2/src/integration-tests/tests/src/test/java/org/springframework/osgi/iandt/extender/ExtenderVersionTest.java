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
package org.springframework.osgi.iandt.extender;

import java.awt.Point;

import org.osgi.framework.Bundle;
import org.springframework.core.io.Resource;
import org.springframework.osgi.iandt.BaseIntegrationTest;
import org.springframework.osgi.util.OsgiBundleUtils;

/**
 * @author Costin Leau
 * 
 */
public class ExtenderVersionTest extends BaseIntegrationTest {

	protected String getManifestLocation() {
		return null;
	}

	// given bundle should not be picked up by the extender since it expects a
	// certain version
	public void testBundleIgnoredBasedOnSpringExtenderVersion() throws Exception {

		String bundleId = "org.springframework.osgi, org.springframework.osgi.iandt.extender-version-bundle,"
				+ getSpringDMVersion();
		Resource location = locateBundle(bundleId);

		Bundle bundle = bundleContext.installBundle(location.getURL().toString());
		assertNotNull(bundle);
		bundle.start();

		assertTrue(OsgiBundleUtils.isBundleActive(bundle));
		assertNull("no point should be published ", bundleContext.getServiceReference(Point.class.getName()));

	}
}
