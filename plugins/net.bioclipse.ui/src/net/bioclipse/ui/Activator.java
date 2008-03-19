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
import java.io.File;

import net.bioclipse.recording.IHistory;

import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

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
        // there should be only one, right? Better to fail if there are two...
        
        Bundle b = Platform.getBundle("org.springframework.osgi.bundle.extender");
       
        // turns out using the no-arg start() would mark the bundle to be
        // autostarted at next startup--this would cause a race wrt log4j config

        b.start(Bundle.START_TRANSIENT);
        logger.debug("Spring Bundle Extender started.");       
        
        finderTracker = new ServiceTracker(context, IHistory.class.getName(),
                null);
        finderTracker.open();
    }

    public IHistory getHistoryObject() {
        IHistory history = null;
        try {
            history = (IHistory) finderTracker.waitForService(1000 * 30);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Could not get history object");
        }
        if (history == null) {
            return null;
        }
        return history;
    }

    private String getActualLogFileName() {

        FileAppender appender = ((FileAppender) Logger.getRootLogger()
                .getAppender(FILE_APPENDER_NAME));
        if (appender == null)
            return null;
        
        String requestedLogFileName = appender.getFile();
        if (requestedLogFileName == null)
            return null;
        
        String actualLogFileName = (new File(requestedLogFileName))
                .getAbsolutePath();
        
        return actualLogFileName;
    }
}

