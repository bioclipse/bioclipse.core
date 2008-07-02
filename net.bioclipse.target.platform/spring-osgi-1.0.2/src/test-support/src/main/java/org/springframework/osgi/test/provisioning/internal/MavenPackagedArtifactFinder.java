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
package org.springframework.osgi.test.provisioning.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Adrian Colyer
 *
 * Find a packaged maven artifact starting in some root 
 * directory of a maven project.
 * 
 * Poor approximation as doesn't check groupId. Could probably
 * be done better using some maven API.... but I can't find
 * any good doc.
 */
public class MavenPackagedArtifactFinder {
	
	private static final String POM_XML = "pom.xml";
	private static final String TARGET = "target";

	private final String artifactName;
	
	public MavenPackagedArtifactFinder(
			String artifactId,
			String version,
            String type) {
		this.artifactName = artifactId + "-" + version + "." + type;
	}
	
	File findPackagedArtifact(File startingDirectory) throws IOException {
		if (!isMavenProjectDirectory(startingDirectory)) {
			throw new IllegalStateException(
				startingDirectory + " does not contain a pom.xml file");
		}
		File rootMavenProjectDir = findRootMavenProjectDir(startingDirectory.getCanonicalFile());
		File found = findInDirectoryTree(artifactName,rootMavenProjectDir);
        if (found == null) {
            throw new FileNotFoundException("Cannot find the artifact <" + artifactName + ">");
        }
        return found;
    }

	private boolean isMavenProjectDirectory(File dir) {
		if (!dir.isDirectory()) {
			return false;
		}
		return new File(dir,POM_XML).exists();
	}
	
	private File findRootMavenProjectDir(File dir) {
		File lastFoundMavenProjectDir = dir;
		File parentDir = dir.getParentFile();
		while (isMavenProjectDirectory(parentDir)) {
			lastFoundMavenProjectDir = parentDir;
			parentDir = parentDir.getParentFile();
		}
		return lastFoundMavenProjectDir;
	}
	
	private File findInDirectoryTree(String fileName,File root) {
		File targetDir = new File(root,TARGET);
		if (targetDir.exists()) {
			if (new File(targetDir,fileName).exists()) {
				return new File(targetDir,fileName);
			}
		}
		File[] children = root.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				if (!isMavenProjectDirectory(pathname)) {
					return false;
				}
				if (pathname.getName().equals("target")) {
					return false;
				}
				if (pathname.getName().equals("src")) {
					return false;
				}
				if (pathname.getName().equals(".svn")) {
					return false;
				}
				return true;
			}});
		
		for (int i=0; i < children.length; i++) {
			File found = findInDirectoryTree(fileName,children[i]);
			if (found != null) {
				return found;
			}
		}
		
		return null;
	}
}
