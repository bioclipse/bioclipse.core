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

import net.bioclipse.recording.IHistory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggerRepository;
import org.eclipse.jface.resource.ImageDescriptor;
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

    // The file for logger properties
    private static final String LOG_PROPERTIES_FILE = "logger.properties";

    public final ConsoleEchoer CONSOLE = new ConsoleEchoer();

    private ServiceTracker finderTracker;

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

        // configure logging

        // TODO
        // the right way to configure logging is to keep lazy loading of log4j
        // by listening for its bundle start event (and of course to use file
        // based configuration)

        BasicConfigurator.configure();
        LogManager.getLoggerRepository().setThreshold(Level.DEBUG);
        Logger.getLogger("org.springframework").setLevel(Level.WARN);

        super.start(context);

        // make sure to start Spring Bundle Extender before any extendees

        for (Bundle b : context.getBundles()) {
            if ("org.springframework.osgi.bundle.extender".equals(
                    b.getSymbolicName())) {

                // turns out using the no-arg start() marks the bundle to be
                // autostarted at next startup--causes a race wrt log4j config

                b.start(Bundle.START_TRANSIENT);
                break;
            }
        }

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
}
