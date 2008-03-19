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

import java.net.URL;

import net.bioclipse.ui.BioclipseActivator;
import net.bioclipse.usermanager.business.IUserManager;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author jonalv
 */
public class Activator extends BioclipseActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.usermanager";

	// The shared instance
	private static Activator plugin;

	private final String LOG_PROPERTIES_FILE="logger.properties";
	
	private ServiceTracker finderTracker;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}
		
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		finderTracker = new ServiceTracker( context, 
                                            IUserManager.class.getName(), 
                                            null );
		finderTracker.open();
	}

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

	@Override
	public URL getLoggerURL() {
		return getBundle().getEntry("/" + LOG_PROPERTIES_FILE);
	}
	
	public IUserManager getUserManager() {
		IUserManager manager = null;
		try {
			manager = (IUserManager) finderTracker.waitForService(1000*10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(manager == null) {
			throw new IllegalStateException("Could not get the user manager");
		}
		return manager;
	}
}
