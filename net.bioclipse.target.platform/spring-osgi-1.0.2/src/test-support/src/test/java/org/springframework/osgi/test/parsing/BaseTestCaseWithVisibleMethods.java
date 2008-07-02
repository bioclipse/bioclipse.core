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

package org.springframework.osgi.test.parsing;

import java.util.Properties;
import java.util.jar.Manifest;

import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

/**
 * @author Costin Leau
 * 
 */
public class BaseTestCaseWithVisibleMethods extends AbstractConfigurableBundleCreatorTests {

	public String getRootPath() {
		return super.getRootPath();
	}

	public Manifest getManifest() {
		return super.getManifest();
	}

	public Properties getSettings() throws Exception {
		return super.getSettings();
	}

	public String[] getBundleContentPattern() {
		return super.getBundleContentPattern();
	}
	
	
}
