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

import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.core.util.Predicate;
import net.bioclipse.core.util.ListFuncs;
import net.bioclipse.recording.IHistory;

import org.apache.log4j.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
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

    public final ConsoleEchoer CONSOLE = new ConsoleEchoer();
    
    private static final Logger logger = Logger.getLogger(Activator.class);
    
    private ServiceTracker finderTracker;
    
    private static final String EXTENDER_BUNDLE_NAME = 
        "org.springframework.osgi.bundle.extender";

    private static final String JVM_VERSION_ERROR_MSG = 
        "** Bioclipse startup FAILED **\n" +
        "Bioclipse must be run with Java 1.5 (sometimes referred to as 5.0) or better.\n" +
        "If you have multiple versions of Java installed, please edit the file " +
             "'bioclipse.ini' to point to java 1.5 or 1.6 by adding a line like below: \n" +
        " -vm /path/to/java1.5/bin/java";
    
    // The shared singleton instance.
    private static Activator plugin; 
    
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
        startBundleExtender();
        
        finderTracker = new ServiceTracker(context, IHistory.class.getName(),
                null);
        finderTracker.open();
    }


    public IHistory getHistoryObject() {
        IHistory history = null;
        try {
            history = (IHistory) finderTracker.waitForService(1000 * 30);
        } catch (InterruptedException e) {
            // honoring the intention to print a stack trace here
            logger.warn("Could not get history object: " + e);
            LogUtils.debugTrace(logger, e);
        }
        if (history == null) {
            logger.debug("getHistoryObject() returning NULL.");
            return null;
        } 
        logger.debug("getHistoryObject() returning history object.");
        return history;
    }
  
    
    private void handleStartupArgs() {
        String[] args  = Platform.getCommandLineArgs();
        
        for (int i = 0; i < args.length; i++) {
            logger.debug("Detected argument "+ i + ": " + args[i]);
            //TODO: handle arguments for Bioclipse here
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
    
    
    /* Attempts to start all resolved Spring Bundle Extender bundles.
     * Will log an error if it can't find or start the extender, and warns if
     * there is more than one extender bundle in the resolved state in the
     * system.
     * 
     * Assumes that the OSGI resolver has done its job and that each resolved
     * bundle whose symbolic name is that of Spring Bundle Extender
     * is separately required. 
     * 
     * (Moreover implicitly assumes that Spring appropriately labels its bundles
     * with the singleton directive, thus forcing the resolver to pick only 
     * one to resolve, if multiple extender bundles cannot coexist.) */
    
    private void startBundleExtender() {

        // How we define the bundles we consider startable. Ignores INSTALLED
        // bundles on the assumption that resolution just completed and there
        // must be a reason INSTALLED bundles were not resolved then.
        // (UNINSTALLED bundles are required never to be started.)
        
        Predicate<Bundle> isStartableAndHasBeenResolved = new Predicate<Bundle>() {
            public Boolean eval(Bundle b) {
                final int mask = Bundle.INSTALLED | Bundle.UNINSTALLED;
                return (b.getState() & mask) == 0;    // autoboxes to Boolean class
            }
        };
        
        // start all Spring Extender bundles that meet the condition set out 
        // in the predicate above
        
        List<Bundle> allExtenders = 
            Arrays.asList(Platform.getBundles(EXTENDER_BUNDLE_NAME, null));    
        List<Bundle> toStart = 
            ListFuncs.filter(allExtenders, isStartableAndHasBeenResolved);
       
        for (Bundle b : toStart)
            startTransiently(b);

        // and now for some warnings

        final int nToStart = toStart.size();
        if (nToStart > 1) {
            logger.warn("More than one resolved Spring Bundle Extender found, "
                    + "is this expected?");
        }
        else if (nToStart == 0) {
            
            // This is more problematic. The most likely reason for getting
            // here is that we no longer have the correct symbolic name of the
            // bundle extender, or the other bundles no longer express a 
            // dependency on the bundle extender.
            
            logger.warn("No resolved Spring bundle extender found, valiantly "
                    + "attempting to soldier on anyway.");
        }
        else {
            assert (nToStart == 1): 
                "Oops forgot a case when counting startable bundle extenders.";
        }
    }
    
    
    /* What we do with the bundles we want to start. Notes:
       - could easily factor out bundle name in logging comments to reuse    
       - retain transient start or else started bundle will autostart 
         next time, potentially causing confusion and/or races */
    
    private Boolean startTransiently(Bundle b) {
        logger.debug("Attempting to start Spring Bundle Extender...");
        try {
            b.start(Bundle.START_TRANSIENT);  
            logger.debug("Spring Bundle Extender started.");
            return true;
        }
        catch (BundleException e) {
            logger.error("Unable to start resolved Spring Bundle Extender: " + e);
            LogUtils.debugTrace(logger, e);
            return false;
        }
    }
}

