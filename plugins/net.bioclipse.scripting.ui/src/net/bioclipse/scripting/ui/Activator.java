/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting.ui;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.jmol.business.IJmolManager;
import net.bioclipse.scripting.ui.business.IJsConsoleManager;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author jonalv
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.springBasedPrototypePlugin";

    // The shared instance
    private static Activator plugin;
    
    // tracks the js console manager
    private ServiceTracker finderTracker;
    
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
        
        finderTracker = new ServiceTracker( context, 
                                            IJsConsoleManager.class.getName(), 
                                            null );
        finderTracker.open();
    }

    /**
     * Returns a reference to the example manager object
     * 
     * @return the exampleManager
     */
    public IJsConsoleManager getJsConsoleManager() {
        IJsConsoleManager jsConsoleManager = null;
        try {
            jsConsoleManager
                = (IJsConsoleManager) finderTracker.waitForService(1000*30);
        }
        catch (InterruptedException e) {
            throw
              new IllegalStateException("Could not get js console manager", e);
        }
        if (jsConsoleManager == null) {
            throw new IllegalStateException("Could not get js console manager");
        }
        return jsConsoleManager;
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

    public IJmolManager getJmolManager() {
        IJmolManager manager = null;
        try {
            manager = (IJmolManager) finderTracker.waitForService(1000*10);
        } catch (InterruptedException e) {
            LogUtils.debugTrace(logger, e);
        }
        if(manager == null) {
            throw new IllegalStateException("Could not get the Jmol manager");
        }
        return manager;
    }
}
