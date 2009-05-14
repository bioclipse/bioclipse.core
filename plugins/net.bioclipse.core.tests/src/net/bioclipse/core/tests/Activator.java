 /*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *     Ola Spjuth
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.core.tests;

import net.bioclipse.core.business.IJSTestManager;
import net.bioclipse.core.business.IJTestManager;
import net.bioclipse.core.business.ITestManager;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    private static final Logger logger = Logger.getLogger(Activator.class);
    
    // The shared instance
    private static Activator plugin;

    //For Spring
    private ServiceTracker javaFinderTracker;
    private ServiceTracker javaScriptFinderTracker;

    /**
     * The constructor
     */
    public Activator() {
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        javaFinderTracker 
            = new ServiceTracker( context, 
                                  IJTestManager.class.getName(), 
                                  null );
        
        javaFinderTracker.open();
        javaScriptFinderTracker 
            = new ServiceTracker( context, 
                                  IJSTestManager.class.getName(), 
                                  null );
              
              javaFinderTracker.open();
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

    public ITestManager getJavaTestManager() {
        ITestManager manager = null;
        try {
            manager = (ITestManager) 
                      javaFinderTracker.waitForService(1000*10);
        } catch (InterruptedException e) {
            LogUtils.debugTrace(logger, e);
            throw new IllegalStateException("Could not get the manager: " +
                e.getMessage(), e);
        }
        if(manager == null) {
            throw new IllegalStateException("Could not get the manager.");
        }
        return manager;
    }
    
    public ITestManager getJavaScriptTestManager() {
        ITestManager manager = null;
        try {
            manager = (ITestManager) 
                      javaScriptFinderTracker.waitForService(1000*10);
        } catch (InterruptedException e) {
            LogUtils.debugTrace(logger, e);
        }
        if(manager == null) {
            throw new IllegalStateException("Could not get the CDK manager");
        }
        return manager;
    }
}
