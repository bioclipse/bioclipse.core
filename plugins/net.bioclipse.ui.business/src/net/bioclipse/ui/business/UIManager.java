/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Carl Masak
 *     
 ******************************************************************************/
package net.bioclipse.ui.business;

import net.bioclipse.core.ResourcePathTransformer;

import org.eclipse.core.resources.IFile;

/**
 * Contains general methods for interacting with the Bioclipse graphical
 * user interface (GUI).
 * 
 * @author masak
 *
 */
public class UIManager implements IUIManager {

    public String getNamespace() {
        return "ui";
    }

    // TOFU: Hi olas. :)
    
    public void delete( IFile file ) {
    }

    public void open( IFile file ) {
    }

    public void delete( String filePath ) {
        delete(ResourcePathTransformer.getInstance().transform( filePath ));
    }

    public void open( String filePath ) {
        open(ResourcePathTransformer.getInstance().transform( filePath ));
    }
}
