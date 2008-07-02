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

package org.springframework.osgi.context.support;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.osgi.io.OsgiBundleResource;
import org.springframework.osgi.util.BundleDelegatingClassLoader;

/**
 * @author Costin Leau
 * 
 */
public class AbstractRefreshableOsgiBundleApplicationContextTest extends TestCase {

	private AbstractOsgiBundleApplicationContext context;


	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		context = new AbstractOsgiBundleApplicationContext() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.springframework.context.support.AbstractRefreshableApplicationContext#loadBeanDefinitions(org.springframework.beans.factory.support.DefaultListableBeanFactory)
			 */
			protected void loadBeanDefinitions(DefaultListableBeanFactory arg0) throws IOException, BeansException {
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		context = null;
	}

	public void testBundleContext() throws Exception {
		MockControl bundleCtxCtrl = MockControl.createNiceControl(BundleContext.class);
		BundleContext bundleCtx = (BundleContext) bundleCtxCtrl.getMock();

		MockControl bundleCtrl = MockControl.createStrictControl(Bundle.class);
		Bundle bundle = (Bundle) bundleCtrl.getMock();

		bundleCtxCtrl.expectAndReturn(bundleCtx.getBundle(), bundle);

		String location = "osgibundle://someLocation";
		Resource bundleResource = new OsgiBundleResource(bundle, location);

		Dictionary dict = new Properties();
		bundleCtrl.expectAndReturn(bundle.getHeaders(), dict);
		bundleCtrl.expectAndReturn(bundle.getSymbolicName(), "symName", MockControl.ONE_OR_MORE);
		bundleCtrl.replay();
		bundleCtxCtrl.replay();

		context.setBundleContext(bundleCtx);
		assertSame(bundle, context.getBundle());
		assertSame(bundleCtx, context.getBundleContext());

		ClassLoader loader = context.getClassLoader();
		assertTrue(loader instanceof BundleDelegatingClassLoader);

		// do some resource loading
		assertEquals(bundleResource, context.getResource(location));

		bundleCtrl.verify();
		bundleCtxCtrl.verify();
	}
}
