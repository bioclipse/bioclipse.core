/*******************************************************************************
 * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.ds.signsig.bursi;

import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    private Logger logger = Logger.getLogger(Activator.class);

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.ds.signsig.bursi";

	 // The DStest ID, matches the one in plugin.xml
  public static final String DS_TEST_ID = "signsig.bursi";

	// The shared instance
	private static Activator plugin;
	
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
		
		//Set LD path for RHL at AZ
    logger.info("Signatures activator detected os name: " 
                + System.getProperty("os.name"));
    logger.info("Signatures activator detected os version: " 
                + System.getProperty("os.version"));
    logger.info("Signatures activator detected java lib path: " 
                + System.getProperty("java.library.path"));
		
    if (System.getProperty("os.name").toLowerCase().startsWith( "linux" )){
        System.setProperty("java.library.path",
                           System.getProperty("java.library.path") 
                           + ":/home/kcds733/Software/openbabel-2.1.1/lib");
        logger.info( "Set java.library path to: " +  System.getProperty("java.library.path"));
    }
		
		
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

}
