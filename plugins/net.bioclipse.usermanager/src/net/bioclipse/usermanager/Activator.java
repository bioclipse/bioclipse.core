/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.usermanager;
import net.bioclipse.ui.BioclipseActivator;
import net.bioclipse.usermanager.business.IUserManager;
import org.apache.log4j.Logger;
import net.bioclipse.core.util.LogUtils;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
/**
 * The activator class controls the plug-in life cycle
 * 
 * @author jonalv
 */
public class Activator extends BioclipseActivator {
    private static final Logger logger = Logger.getLogger(Activator.class);
    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.usermanager";
    // The shared instance
    private static Activator plugin;
    private ServiceTracker finderTracker;
    /**
     * The constructor
     */
    public Activator() {
    }
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        finderTracker = new ServiceTracker( context, 
                                            IUserManager.class.getName(), 
                                            null );
        finderTracker.open();
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
    public IUserManager getUserManager() {
        IUserManager manager = null;
        try {
            manager = (IUserManager) finderTracker.waitForService(1000*10);
        } catch (InterruptedException e) {
            LogUtils.debugTrace(logger, e);
        }
        if(manager == null) {
            throw new IllegalStateException("Could not get the user manager");
        }
        return manager;
    }
}
