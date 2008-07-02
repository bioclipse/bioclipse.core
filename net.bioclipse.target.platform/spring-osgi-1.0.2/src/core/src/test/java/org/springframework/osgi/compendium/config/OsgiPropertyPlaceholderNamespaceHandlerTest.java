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
package org.springframework.osgi.compendium.config;

import java.lang.reflect.Field;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.osgi.compendium.internal.OsgiPropertyPlaceholder;
import org.springframework.osgi.context.support.BundleContextAwareProcessor;
import org.springframework.osgi.mock.MockBundleContext;

/**
 * @author Costin Leau
 * 
 */
public class OsgiPropertyPlaceholderNamespaceHandlerTest extends TestCase {

	private GenericApplicationContext appContext;

	private BundleContext bundleContext;

	private MockControl adminControl;

	private ConfigurationAdmin admin;

	private Dictionary config;

	protected void setUp() throws Exception {

		adminControl = MockControl.createControl(ConfigurationAdmin.class);
		admin = (ConfigurationAdmin) adminControl.getMock();
		MockControl configMock = MockControl.createControl(Configuration.class);
		Configuration cfg = (Configuration) configMock.getMock();

		config = new Hashtable();

		adminControl.expectAndReturn(admin.getConfiguration("com.xyz.myapp"), cfg, MockControl.ONE_OR_MORE);
		configMock.expectAndReturn(cfg.getProperties(), config, MockControl.ONE_OR_MORE);

		adminControl.replay();
		configMock.replay();

		bundleContext = new MockBundleContext() {
			// add Configuration admin support
			public Object getService(ServiceReference reference) {
				return admin;
			}
		};

		appContext = new GenericApplicationContext();
		appContext.getBeanFactory().addBeanPostProcessor(new BundleContextAwareProcessor(bundleContext));

		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(appContext);
		// reader.setEventListener(this.listener);
		reader.loadBeanDefinitions(new ClassPathResource("osgiPropertyPlaceholder.xml", getClass()));
		appContext.refresh();
	}

	protected void tearDown() throws Exception {
		adminControl.verify();
	}

	public void testSimplePlaceholder() throws Exception {
		OsgiPropertyPlaceholder simple = (OsgiPropertyPlaceholder) appContext.getBean(OsgiPropertyPlaceholder.class.getName()
				+ "#0");
		assertEquals("com.xyz.myapp", simple.getPersistentId());

	}

	public void testAveragePlaceholder() throws Exception {
		OsgiPropertyPlaceholder average = (OsgiPropertyPlaceholder) appContext.getBean(OsgiPropertyPlaceholder.class.getName()
				+ "#1");
		assertEquals("com.xyz.myapp", average.getPersistentId());
		Properties[] props = (Properties[]) getField(average, PropertiesLoaderSupport.class, "localProperties");
		assertEquals(1, props.length);

		assertEquals(appContext.getBean("external-props"), props[0]);
	}

	public void testFullPlaceholder() throws Exception {
		OsgiPropertyPlaceholder full = (OsgiPropertyPlaceholder) appContext.getBean(OsgiPropertyPlaceholder.class.getName()
				+ "#2");
		assertEquals("com.xyz.myapp", full.getPersistentId());

		Properties[] props = (Properties[]) getField(full, PropertiesLoaderSupport.class, "localProperties");
		assertEquals(1, props.length);

		Properties correctProperties = new Properties();
		correctProperties.setProperty("rod", "johnson");
		correctProperties.setProperty("rick", "evans");

		assertEquals(correctProperties, props[0]);
	}

	private Object getField(Object target, Class clazz, String fieldName) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(target);
	}

}
