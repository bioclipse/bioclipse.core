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
package org.springframework.osgi.test.internal;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import junit.framework.TestCase;

import org.springframework.osgi.test.internal.storage.MemoryStorage;
import org.springframework.osgi.test.internal.storage.Storage;
import org.springframework.osgi.test.internal.util.IOUtils;
import org.springframework.osgi.test.internal.util.JarCreator;

/**
 * @author Costin Leau
 * 
 */
public class JarCreatorTests extends TestCase {

	private JarCreator creator;

	private Storage storage;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		creator = new JarCreator();
		storage = new MemoryStorage();
		creator.setStorage(storage);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		storage.dispose();
	}

	public void testJarCreation() throws Exception {

		final Manifest mf = new Manifest();

		Map entries = mf.getEntries();
		Attributes attrs = new Attributes();

		attrs.putValue("rocco-ventrella", "winelight");
		entries.put("test", attrs);

		String location = JarCreatorTests.class.getName().replace('.', '/') + ".class";
		// get absolute file location
		// file:/D:/work/i21/spring-osgi-sf/...s/org/springframework/osgi/test/JarCreatorTests.class
		final URL clazzURL = getClass().getClassLoader().getResource(location);

		// go two folders above
		// file:/D:/work/i21/spring-osgi-sf/...s/org/springframework/
		String rootPath = new URL(clazzURL, "../../").toExternalForm();
		
		
		String firstLevel = new URL(clazzURL, "../").toExternalForm().substring(rootPath.length());
		// get file folder
		String secondLevel = new URL(clazzURL, ".").toExternalForm().substring(rootPath.length());

		// now determine the file relative to the root
		String file = clazzURL.toExternalForm().substring(rootPath.length());

		// create a simple jar from a given class and a manifest
		creator.setContentPattern(new String[] { file });
		creator.setRootPath(rootPath);
		creator.setAddFolders(true);

		// create the jar
		creator.createJar(mf);

		// start reading the jar
		JarInputStream jarStream = null;

		try {
			jarStream = new JarInputStream(storage.getInputStream());
			// get manifest
			assertEquals(mf, jarStream.getManifest());

			// move the jar stream to the first entry (which should be org/
			// folder)
			String entryName = jarStream.getNextEntry().getName();
			assertEquals("folders above the file not included", firstLevel, entryName);
			
			entryName = jarStream.getNextEntry().getName();
			assertEquals("file folder not included", secondLevel, entryName);

			// now get the file
			jarStream.getNextEntry();
			// open the original file
			InputStream originalFile = clazzURL.openStream();

			int b;
			while ((b = originalFile.read()) != -1)
				assertEquals("incorrect jar content", b, jarStream.read());
		}
		finally {
			IOUtils.closeStream(jarStream);

		}
	}
}
