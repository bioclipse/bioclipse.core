/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     Richard Klancer - moved some code from BioclipseActivator, rewrote
 *                       Spring Bundle Extender startup
 *     
 ******************************************************************************/

package net.bioclipse.ui;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.util.ListFuncs;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.core.util.Predicate;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Plug-in singleton and Bundle activator for the Bioclipse UI plug-in.
 *  
 * The start method of the singleton instance of this class is the first 
 * Bioclipse code to be executed upon application startup, as long as the 
 * current product defines net.bioclipse.ui.Application as the application 
 * entry point (via the org.eclipse.core.runtime.application extension point.)
 * 
 * @author ola
 *
 */

public class Activator extends BioclipseActivator {
    
    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.ui";

    public boolean checkForUpdates;

    private static final Logger logger = Logger.getLogger(Activator.class);
    


    private static final String JVM_VERSION_ERROR_MSG = 
        "** Bioclipse startup FAILED **\n" +
        "Bioclipse must be run with Java 1.5 (sometimes referred to as 5.0) or better.\n" +
        "If you have multiple versions of Java installed, please edit the file " +
             "'bioclipse.ini' to point to java 1.5 or 1.6 by adding a line like below: \n" +
        " -vm /path/to/java1.5/bin/java";

    public static final String MOLECULE_2D_ICON = "icon.molecule_2D";
    
    // The shared singleton instance.
    private static Activator plugin; 
    
    
    
    @Override
    /** Check out these pages for more info
     *  http://richclientplatform.blogspot.com/2007/05/plugin-your-images.html
     *  http://eclipselowdown.blogspot.com/2006/05/image-management.html
     */
    protected void initializeImageRegistry( ImageRegistry registry ) {
        super.initializeImageRegistry( registry );
        Bundle bundle = Platform.getBundle( PLUGIN_ID );
        ImageDescriptor myImage = ImageDescriptor.createFromURL( 
                       FileLocator.find( bundle, 
                                         new Path("icons/chemistry/mol_2d.png"), 
                                         null ));
        registry.put( MOLECULE_2D_ICON, myImage );        
    }
    
     /** Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path
     *            the path
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

    
    public static Activator getDefault() {
        return plugin;
    }

    
    @Override
    public void start(BundleContext context) throws Exception {       
        super.start(context);

        checkJVMVersion();  
        handleStartupArgs();
        
//        initBioclipseCache();
        
    }


    private void initBioclipseCache() {
        try {
            File folder=BioclipseCache.getCacheDir();
            if (folder!=null)
                logger.info("Bioclipse cache dir: " + folder.getAbsolutePath());
            else
                logger.info("Error initializing Bioclipse cache dir.");
        } catch (CoreException e) {
            logger.info("Error initializing Bioclipse cache dir: " + e.getMessage());
        }
        
    }

    private void handleStartupArgs() {
        String[] args  = Platform.getCommandLineArgs();

        //Default is to check
    	checkForUpdates=true;

    	for (int i = 0; i < args.length; i++) {
            logger.debug("Detected argument "+ i + ": " + args[i]);
            
            if (args[i].equalsIgnoreCase("-noupdate")){
            	logger.debug("Argument -noupdate implies no auto check on startup");
            	checkForUpdates=false;
            }
            	
            //Handle other arguments for Bioclipse here
        }   
    	
    	if (checkForUpdates){
            	logger.debug("Args says: Check for updates is enabled");
    	}
        
        
    }
    
    
    private void checkJVMVersion() {

        if (!(System.getProperty("java.version").startsWith("1.5")) &&
            !(System.getProperty("java.version").startsWith("1.6"))) {
            System.err.println(JVM_VERSION_ERROR_MSG);
            // FIXME you should normally never call this from a plugin
            System.exit(0);
        }
    }
    
    
    public static Logger getLogger() {
        return logger;
    }
    
}

