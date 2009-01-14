/*******************************************************************************
 * Copyright (c) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.ioformats;

import net.bioclipse.ui.BioclipseActivator;

import org.eclipse.jface.resource.ImageDescriptor;

public class Activator extends BioclipseActivator {

    public static final String PLUGIN_ID = "net.bioclipse.ioformats";

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
