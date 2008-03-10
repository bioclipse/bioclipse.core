/*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 ******************************************************************************/package net.bioclipse.ui;

import java.net.URL;

import net.bioclipse.recording.IHistory;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Controls the plug-in life cycle.
 * @author ola
 *
 */
public class Activator extends BioclipseActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.ui";

	//The file for logger properties
	private static final String LOG_PROPERTIES_FILE = "logger.properties";

	public final ConsoleEchoer CONSOLE = new ConsoleEchoer();
	
	private ServiceTracker finderTracker;
	
	// The shared instance.
	private static Activator plugin;

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * The constructor.
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * Need to provide this plugin's logger.properties to abstract class
	 */
	@Override
	public URL getLoggerURL() {
		return getBundle().getEntry("/" + LOG_PROPERTIES_FILE);
	}
	
	public static Activator getDefault() {
		return plugin;
	}
	
	public void start(BundleContext context) throws Exception {
		
		// TODO: replace this hack
		
		// two notes: 
		// 1. the right way to configure logging is to keep
		//    lazy loading of log4j by listening for bundle start
		// 2. there's a race condition between Spring Bundle Extender startup
		//    and configuration of logging by this bundle
		//    (both are 'started with eager activation' when the framework goes
		//    to startlevel 4, I believe)
		
		BasicConfigurator.configure();
		System.out.println("*** BasicConfigurator.configure() has been run");
		// end hack
		
		super.start(context);
		for( Bundle b : context.getBundles() ) {
			if("org.springframework.osgi.bundle.extender".equals( 
					b.getSymbolicName() ) ) {
				tempPrintBundleState(b);
				b.start();
			}
		}
		
		finderTracker = new ServiceTracker( context, 
                                            IHistory.class.getName(), 
                                            null );
		finderTracker.open();
	}
	
	
	// quick hack to print state of the Spring Bundle Extender bundle
	
	private void tempPrintBundleState(Bundle b) {

			String state;
			
			switch(b.getState()){
				case Bundle.INSTALLED:
					state = "INSTALLED";
					break;
				case Bundle.RESOLVED:
					state = "RESOLVED";
					break;
				case Bundle.ACTIVE:
					state = "ACTIVE";
					break;
				case Bundle.STARTING:
					state = "STARTING";
					break;
				case Bundle.START_TRANSIENT:
					state = "START_TRANSIENT";
					break;
				default:
					state = "Something unknown";
			}
	
			// this code exists to help configure logging properly, so let's use println()
			
			System.out.println("*** " + b.getSymbolicName() + ": " + "state = " + state);
	}
	
	
	
	public IHistory getHistoryObject() {
		IHistory history = null;
		try {
			history = (IHistory) finderTracker.waitForService(1000*30);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Could not get history object");
		}
		if(history == null) {
			return null;
		}
		return history;
	}
}
