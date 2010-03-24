/* *****************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.xml;

import net.bioclipse.xml.business.IXmlManager;
import net.bioclipse.xml.business.IJavaXmlManager;
import net.bioclipse.xml.business.IJavaScriptXmlManager;

import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The Activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    private static final Logger logger = Logger.getLogger(Activator.class);

    // The shared instance
    private static Activator plugin;

    // Trackers for getting the managers
    private ServiceTracker javaFinderTracker;
    private ServiceTracker jsFinderTracker;

    public Activator() {
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        javaFinderTracker
            = new ServiceTracker( context,
                                  IJavaXmlManager.class.getName(),
                                  null );

        javaFinderTracker.open();
        jsFinderTracker
            = new ServiceTracker( context,
                                  IJavaScriptXmlManager.class.getName(),
                                  null );

        jsFinderTracker.open();
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

    public IXmlManager getJavaXmlManager() {
        IXmlManager manager = null;
        try {
            manager = (IXmlManager)
                      javaFinderTracker.waitForService(1000*10);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(
                          "Could not get the Java XmlManager",
                          e );
        }
        if (manager == null) {
            throw new IllegalStateException(
                          "Could not get the Java XmlManager");
        }
        return manager;
    }

    public IJavaScriptXmlManager getJavaScriptXmlManager() {
        IJavaScriptXmlManager manager = null;
        try {
            manager = (IJavaScriptXmlManager)
                      jsFinderTracker.waitForService(1000*10);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(
                          "Could not get the JavaScript XmlManager",
                          e );
        }
        if (manager == null) {
            throw new IllegalStateException(
                          "Could not get the JavaScript XmlManager");
        }
        return manager;
    }
}
