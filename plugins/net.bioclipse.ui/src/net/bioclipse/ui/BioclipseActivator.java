/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/
package net.bioclipse.ui;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
/**
 * The BioclipseActivator is a class that can be extended by all Bioclipse 
 * plugins in order to simplify logging configuration. 
 * Requires the file: "logger.properties" for log4j in root of plugin.
 * @author ola
 *
 */
public class BioclipseActivator extends AbstractUIPlugin {
    // The shared instance.
    private static BioclipseActivator plugin;
//    private static final Logger logger = Logger.getLogger(BioclipseActivator.class);
    /**
     * The constructor.
     */
    public BioclipseActivator() {
        plugin = this;
    }
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }
    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }
    /**
     * Returns the shared instance.
     */
    public static BioclipseActivator getDefault() {
        return plugin;
    }
}
