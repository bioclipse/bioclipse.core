/*******************************************************************************
 * Copyright (c) 2009  Ola Spjuth <ola@bioclipse.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.webservices.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "net.bioclipse.webservices.nwo.tests";
    private static Activator sharedInstance;
    
    public Activator() {}

    public void start(BundleContext context) throws Exception {
        super.start(context);
        sharedInstance = this;
    }

    public void stop(BundleContext context) throws Exception {
        sharedInstance = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return sharedInstance;
    }

}
