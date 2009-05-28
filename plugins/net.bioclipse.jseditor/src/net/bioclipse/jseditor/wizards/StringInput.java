/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.jseditor.wizards;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * String input to allow opening editors without an IFile backend.
 * 
 * @author egonw
 */
public class StringInput implements IStorageEditorInput {

    private IStorage storage;
    
    StringInput(IStorage storage) {
        this.storage = storage;
    }
    
    public boolean exists() {
        return true;
    }
    
    public ImageDescriptor getImageDescriptor() {
        return null;
    }
    
    public String getName() {
        return storage.getName();
    }
    
    public IPersistableElement getPersistable() {
        return null;
    }
    
    public IStorage getStorage() {
        return storage;
    }
    
    public String getToolTipText() {
        return "String-based file: " + storage.getName();
    }
    
    public Object getAdapter( Class adapter ) {
        return null;
    }
}
