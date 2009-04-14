package net.bioclipse.jseditor;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import java.lang.reflect.Method;
import java.net.MalformedURLException;

/*
 * This file is part of the Bioclipse Javascript Editor Plug-in.
 * 
 * Copyright (c) 2008 Johannes Wagener.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Wagener - initial API and implementation
 */

public class JarClasspathLoader {
		 
	@SuppressWarnings("unchecked")
	private static final Class[] parameters = new Class[]{URL.class};
	 
	public static boolean addFile(String name) {
		File f = new File(name);
		return addFile(f);
	}
		 
	public static boolean addFile(File file) {
		try {
			addURL(file.toURI().toURL());	// file:/c:/xws-aacodeconverter.jar
		} catch (MalformedURLException e) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean addURL(URL url) {
		
		ClassLoader sysloader = ClassLoader.getSystemClassLoader();
		
		if (!(sysloader instanceof URLClassLoader)) {
			return false;
		}
		
		Class sysclass = URLClassLoader.class;
		
		try {
			// access the methode in this ugly way as it is protected...
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke((URLClassLoader)sysloader, new Object[]{ url });
		} catch (Throwable t) {
			return false;
		}
		return true;
	}
}
