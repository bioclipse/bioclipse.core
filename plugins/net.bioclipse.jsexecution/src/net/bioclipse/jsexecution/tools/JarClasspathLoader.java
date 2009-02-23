package net.bioclipse.jsexecution.tools;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import java.lang.reflect.Method;
import java.net.MalformedURLException;

import net.bioclipse.jsexecution.execution.helper.ThreadSafeConsoleWrap;

/*
 * This file is part of the Bioclipse JsExecution Plug-in.
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
	 
	public static boolean addFile(String name, ThreadSafeConsoleWrap console) {
		File f = new File(name);
		return addFile(f, console);
	}
		 
	public static boolean addFile(File file, ThreadSafeConsoleWrap console) {
		try {
			addURL(file.toURI().toURL(), console);	// file:/c:/xws-aacodeconverter.jar
		} catch (MalformedURLException e) {
			console.writeToConsoleRed("Error, could not convert File to URL.");
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean addURL(URL url, ThreadSafeConsoleWrap console) {
		
		ClassLoader sysloader = ClassLoader.getSystemClassLoader();
		
		if (!(sysloader instanceof URLClassLoader)) {
			console.writeToConsoleRed("Error, system classloader is not instance of URLClassLoader.");
			return false;
		}
		
		Class sysclass = URLClassLoader.class;
		
		try {
			// access the methode in this ugly way as it is protected...
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke((URLClassLoader)sysloader, new Object[]{ url });
		} catch (Throwable t) {
			console.writeToConsoleRed("Error, could not add URL to system's Classloader.");
			return false;
		}
		console.writeToConsoleBlue("Added " + url.toString() + " to system's Classloader.");
		return true;
	}
}
