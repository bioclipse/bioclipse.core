/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *               2008  Ola Spjuth
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.gist;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.gist.business.IGistManager;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator extends AbstractUIPlugin {

    private static final Logger logger = Logger.getLogger(Activator.class);
    public static final String PLUGIN_ID = "net.bioclipse.gist";

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
    
    // The shared instance
    private static Activator plugin;

    // For Spring
    private ServiceTracker finderTracker;

    public Activator() {}

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        finderTracker = new ServiceTracker(
            context, 
            IGistManager.class.getName(), 
            null
        );
        finderTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return plugin;
    }

    public IGistManager getManager() {
        IGistManager manager = null;
        try {
            manager = (IGistManager) finderTracker.waitForService(1000*10);
        } catch (InterruptedException e) {
            LogUtils.debugTrace(logger, e);
        }
        if(manager == null) {
            throw new IllegalStateException("Could not get the Gist manager");
        }
        return manager;
    }
    
}
