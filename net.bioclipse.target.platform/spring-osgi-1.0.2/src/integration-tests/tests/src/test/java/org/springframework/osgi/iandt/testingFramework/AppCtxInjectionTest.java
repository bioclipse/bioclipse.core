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
package org.springframework.osgi.iandt.testingFramework;

import java.io.Serializable;

import org.springframework.osgi.iandt.BaseIntegrationTest;

/**
 * Test the injection executed on the current test. This verifies that the
 * application context has been created and that injection properly executes.
 * 
 * @author Costin Leau
 * 
 */
public class AppCtxInjectionTest extends BaseIntegrationTest {

	private Serializable serializable;

	public void setSerializable(Serializable serializable) {
		this.serializable = serializable;
	}

	public void testInjection() throws Exception {
		assertNotNull(serializable);
		assertEquals(applicationContext.getBean("injected-bean"), serializable);
	}

	protected String[] getConfigLocations() {
		return new String[] { "/org/springframework/osgi/iandt/testingFramework/AppCtxInjectionTest.xml" };
	}
}
