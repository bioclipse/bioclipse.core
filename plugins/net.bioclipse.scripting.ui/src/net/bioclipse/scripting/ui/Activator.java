/*******************************************************************************
 *Copyright (c) 2008-2009 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting.ui;

import net.bioclipse.scripting.ui.business.IJavaJsConsoleManager;
import net.bioclipse.scripting.ui.business.IJavaScriptJsConsoleManager;
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

    public static final String PLUGIN_ID
      = "net.bioclipse.springBasedPrototypePlugin";

    private static Activator plugin;
    
    private ServiceTracker javaFinderTracker;
    private ServiceTracker jsFinderTracker;
    
    /**
     * The constructor
     */
    public Activator() {
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        javaFinderTracker = new ServiceTracker( context, 
                IJavaJsConsoleManager.class.getName(), 
                null );
        javaFinderTracker.open();
        jsFinderTracker = new ServiceTracker( context, 
                IJavaScriptJsConsoleManager.class.getName(), 
                null );
        jsFinderTracker.open();
    }

    public IJsConsoleManager getJavaJsConsoleManager() {
        IJavaJsConsoleManager jsConsoleManager = null;
        try {
            jsConsoleManager
                = (IJavaJsConsoleManager)
                  javaFinderTracker.waitForService(1000*30);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(
                          "Could not get js console manager: ",
                          e );
        }
        if (jsConsoleManager == null) {
            throw new IllegalStateException("Could not get js console manager");
        }
        return jsConsoleManager;
    }

    public IJsConsoleManager getJavaScriptJsConsoleManager() {
        IJavaScriptJsConsoleManager jsConsoleManager = null;
        try {
            jsConsoleManager
                = (IJavaScriptJsConsoleManager)
                  javaFinderTracker.waitForService(1000*30);
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
