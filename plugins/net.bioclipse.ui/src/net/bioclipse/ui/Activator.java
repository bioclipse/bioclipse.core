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
 ******************************************************************************/

package net.bioclipse.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

import net.bioclipse.recording.IHistory;

import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleException;

/**
 * Controls the plug-in life cycle.
 * 
 * @author ola
 * 
 */
public class Activator extends BioclipseActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.ui";

    // TODO remove - we actually use log4j.properties in a net.bioclipse.log4jconfig
    // The file for logger properties
    private static final String LOG_PROPERTIES_FILE = "logger.properties";

    public final ConsoleEchoer CONSOLE = new ConsoleEchoer();

    private ServiceTracker finderTracker;
    
    private static Logger logger = Logger.getLogger(Activator.class);
    private static String FILE_APPENDER_NAME = "file";
    private static String EXTENDER_BUNDLE_NAME = "org.springframework.osgi.bundle.extender";
    
    // The shared instance.
    private static Activator plugin;

    
    /**
     * Returns an image descriptor for the image file at the given plug-in
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

    
    @Override
    public void start(BundleContext context) throws Exception {

        super.start(context);
        
        String actualLogFileName = getActualLogFileName();
        if (actualLogFileName == null)
            System.err.println("WARNING: Bioclipse log file may not be configured.");       
        else
            logger.info("Log file location: " + actualLogFileName);

        // Make sure to start Spring Bundle Extender before any extendees
        
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
            PrintWriter trace = new PrintWriter(new ByteArrayOutputStream());
            e.printStackTrace(trace);
            logger.debug(trace);
        }
        if (history == null) {
            return null;
        }
        return history;
    }


    /* Queries Log4j for the "file" attribute of the "file" appender. 
     * Because the value of this attribute is naively passed by FileAppender
     * to java.io.FileOutputStream(String, int), we attempt to determine 
     * the absolute path to which FileOutputStream interprets this attribute.
     * 
     * Returns the (anticipated) absolute path of the File attribute of the 
     * "file" appender, or Null if there is no such appender, or if its
     * File attribute is not set.
     */
    
    private String getActualLogFileName() {

        FileAppender appender = ((FileAppender) Logger.getRootLogger()
                .getAppender(FILE_APPENDER_NAME));
        if (appender == null)
            return null;

        String requestedLogFileName = appender.getFile();
        if (requestedLogFileName == null)
            return null;
        
        // rather than mimic exactly what FileAppender does with its File
        // attribute (passing it to FileOutputStream, therefore actually
        // opening a file), create a File object which ought to interpret
        // its constructor's argument in the same way that FileOutputStream does.
        
        String actualLogFileName = (new File(requestedLogFileName))
                .getAbsolutePath();
        
        return actualLogFileName;
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
     * (Moreover implicity assumes that Spring appropriately labels its bundles
     * with the singleton directive, thus forcing the resolver to pick only 
     * one to resolve, if multiple extender bundles cannot coexist.) */
    
    private void startBundleExtender() {

        // How we define the bundles we consider startable. Ignores INSTALLED
        // bundles on the assumption that resolution just completed and there
        // must be a reason INSTALLED bundles were not resolved then.
        // (UNINSTALLED bundles are required never to be started.)
        
        Predicate<Bundle> isStartableAndHasBeenResolved = new Predicate<Bundle>() {
            public Boolean eval(Bundle b) {
                int mask = 
                    Bundle.RESOLVED |
                    Bundle.STARTING |
                    Bundle.ACTIVE |
                    Bundle.STOPPING;
                    
                return (b.getState() & mask) != 0;      // autobox to Boolean class
            }
        };
        
        // What we do with the bundles we want to start.
        // Note that not transiently starting bundles would commit them to 
        // autostart next time, potentially causing confusion and/or races
        
        // could easily factor out bundle name in logging comments to reuse
        
        Function<Object, Bundle> startTransiently = new Function<Object, Bundle>() {
            public Object eval(Bundle b) {
                logger.debug("Attempting to start Spring Bundle Extender...");
                try {
                    b.start(Bundle.START_TRANSIENT);  
                    logger.debug("Spring Bundle Extender started.");
                }
                catch (BundleException e) {
                    logger.error("Unable to start selected Spring Bundle Extender: " + e);
                }
                // no useful return value
                return null;
            }
        };
        
        // now do the obvious thing
        
        List<Bundle> extenders = java.util.Arrays.asList(
                Platform.getBundles(EXTENDER_BUNDLE_NAME, null));       
        List<Bundle> toStart = filter(extenders, isStartableAndHasBeenResolved);
        map(toStart, startTransiently);

        // now for some warnings

        int nToStart = toStart.size(); 
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
    
    
    // the venerable and handy map and filter functions. Could factor out into
    // util class and make public
    
    private static <S, T> List<S> map(Collection<T> in, Function<S, T> f) {
        List<S> out = new ArrayList<S>();
        for (T x : in)
            out.add(f.eval(x));
        return out;
    }
    
    
    private interface Function<S, T>  {
        public S eval(T arg);
    }
    
    
    private static <T> List<T> filter(Collection<T> in, Predicate<T> p) {
        List<T> out = new ArrayList<T>();
        for (T x : in)
            if (p.eval(x)) out.add(x);
        return out;
    }
    
    
    private interface Predicate<T> extends Function<Boolean, T> {
        public Boolean eval(T arg);
    }
}


