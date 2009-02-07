package net.bioclipse.rhino;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import java.lang.reflect.Method;
import java.net.MalformedURLException;

/**
 * 
 * This file is part of the Bioclipse Rhino Plug-in.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener
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
			RhinoConsole.writeToConsole("Error, could not convert File to URL.");
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean addURL(URL url) {
		
		ClassLoader sysloader = ClassLoader.getSystemClassLoader();
		
		if (!(sysloader instanceof URLClassLoader)) {
			RhinoConsole.writeToConsole("Error, system classloader is not instance of URLClassLoader.");
			return false;
		}
		
		Class sysclass = URLClassLoader.class;
		
		try {
			// access the methode in this ugly way as it is protected...
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke((URLClassLoader)sysloader, new Object[]{ url });
		} catch (Throwable t) {
			RhinoConsole.writeToConsole("Error, could not add URL to system's Classloader.");
			return false;
		}
		RhinoConsole.writeToConsole("Added " + url.toString() + " to system's Classloader.");
		return true;
	}
}
