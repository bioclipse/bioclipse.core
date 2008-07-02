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
package org.springframework.osgi.util;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.springframework.osgi.mock.MockBundle;

/**
 * @author Costin Leau
 * 
 */
public class OsgiStringUtilsTest extends TestCase {

	private static int state;

	private Bundle bundle;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		OsgiStringUtilsTest.state = Bundle.UNINSTALLED;
		bundle = new MockBundle() {
			public int getState() {
				return state;
			}
		};
	}

	public void testGetBundleEventAsString() {
		assertSame("INSTALLED", OsgiStringUtils.nullSafeBundleEventToString(BundleEvent.INSTALLED));
		assertSame("STARTING", OsgiStringUtils.nullSafeBundleEventToString(BundleEvent.STARTING));
		assertSame("UNINSTALLED", OsgiStringUtils.nullSafeBundleEventToString(BundleEvent.UNINSTALLED));
		assertSame("UPDATED", OsgiStringUtils.nullSafeBundleEventToString(BundleEvent.UPDATED));
		assertTrue(OsgiStringUtils.nullSafeBundleEventToString(-1324).startsWith("UNKNOWN"));
	}

	public void testGetBundleStateAsName() throws Exception {
		OsgiStringUtilsTest.state = Bundle.ACTIVE;
		assertEquals("ACTIVE", OsgiStringUtils.bundleStateAsString(bundle));
		OsgiStringUtilsTest.state = Bundle.STARTING;
		assertEquals("STARTING", OsgiStringUtils.bundleStateAsString(bundle));
		OsgiStringUtilsTest.state = Bundle.STOPPING;
		assertEquals("STOPPING", OsgiStringUtils.bundleStateAsString(bundle));
		OsgiStringUtilsTest.state = -123;
		assertEquals("UNKNOWN STATE", OsgiStringUtils.bundleStateAsString(bundle));
	}
}
