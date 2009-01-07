/*******************************************************************************
 * Copyright (c) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contact: http://www.bioclipse.net/    
 ******************************************************************************/
package net.bioclipse.inchi.business;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author jonalv
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "net.bioclipse.inchi.business";

    private static Activator myself;
    
    private ServiceTracker finderTracker;
    
    public Activator() {}

    public void start(BundleContext context) throws Exception {
        super.start(context);
        myself = this;
        
        finderTracker = new ServiceTracker(
            context, 
            IInChIManager.class.getName(), 
            null
        );
        finderTracker.open();
    }

    public IInChIManager getInChIManager() {
        IInChIManager inchiManager = null;
        try {
            inchiManager = (IInChIManager) finderTracker.waitForService(1000*30);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Could not get inchi manager", e);
        }
        if(inchiManager == null) {
            throw new IllegalStateException("Could not get inchi manager");
        }
        return inchiManager;
    }

    public void stop(BundleContext context) throws Exception {
        myself = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return myself;
    }
}
