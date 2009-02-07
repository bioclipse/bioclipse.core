package net.bioclipse.rhino;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IExtensionPoint;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


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
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
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
