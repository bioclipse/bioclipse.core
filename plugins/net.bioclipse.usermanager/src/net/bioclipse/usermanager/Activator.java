/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.usermanager;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.tools.logging.PluginLogManager;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author jonalv
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.usermanager";

	// The shared instance
	private static Activator plugin;

	private final String LOG_PROPERTIES_FILE="logger.properties";
	private PluginLogManager logManager;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}
	
	/**
	 * @return the logmanager
	 */
	public static PluginLogManager getLogManager() {
		return getDefault().logManager; 
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		//New logger with com.tools.logging
		configureLogger();
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

	private void configureLogger() {

		try {
			URL url = Platform.getBundle(PLUGIN_ID).getEntry("/" + LOG_PROPERTIES_FILE);
			
			InputStream propertiesInputStream = url.openStream();
			
			if (propertiesInputStream != null) {
				Properties props = new Properties();
				props.load(propertiesInputStream);
				propertiesInputStream.close();
				this.logManager = new PluginLogManager(this, props);
			}	
		} 
		catch (Exception e) {
			String message = "Error while initializing log properties." + 
			e.getMessage();
			System.err.println(message);
			throw new RuntimeException(
					"Error while initializing log properties.",e);
		}         
	}	
}
