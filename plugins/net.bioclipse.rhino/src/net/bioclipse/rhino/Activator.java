package net.bioclipse.rhino;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.rhino.PluginLogger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/*
 * This file is part of the Bioclipse Rhino Plug-in.
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
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.rhino";

	// The shared instance
	private static Activator plugin;
	
	private static List<Object> MANAGERS = null;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	@SuppressWarnings("unchecked")
	private static boolean isIBioclipseManager(Class theClass) {
		/* this is a recursive hack */
		Class[] interfaces = theClass.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			if (interfaces[i].getSimpleName().equals("IBioclipseManager"))
				return true;
			// and recursively discover the 'tree'...
			if (isIBioclipseManager(interfaces[i]))
				return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private static List<Object> findPlatformManagers() {
		List<Object> list = new ArrayList<Object>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		if (registry != null) {
			IExtensionPoint serviceObjectExtensionPoint =
				registry.getExtensionPoint(
					"net.bioclipse.scripting.contribution");

			IExtension[] serviceObjectExtensions
				= serviceObjectExtensionPoint.getExtensions();
			
			for (IExtension extension : serviceObjectExtensions) {
				for(IConfigurationElement element
						: extension.getConfigurationElements()) {
					Object service = null;
					try {
						service = element.createExecutableExtension("service");
					}
					catch (CoreException e) {
						PluginLogger.log("Failed to get a service: " + e.getMessage());
					}
					Class theClass = service.getClass();
					if(service != null && isIBioclipseManager(theClass)) {
						list.add(service);
					}
				}
			}
		}
		return list;
	}
	
	public static List<Object> getManagers() {
		
		if (MANAGERS == null)
			MANAGERS = findPlatformManagers();
		
		return MANAGERS;
	}
}
