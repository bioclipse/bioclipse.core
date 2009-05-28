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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * IStorage for to allow opening editors without an IFile backend.
 * 
 * @author egonw
 */
public class StringStorage implements IStorage {

    private String string;

    StringStorage(String input) {
        this.string = input;
    }

    public InputStream getContents() throws CoreException {
        return new ByteArrayInputStream(string.getBytes());
    }

    public IPath getFullPath() {
        return null;
    }

    public String getName() {
        int len = Math.min(8, string.length());
        return string.substring(0, len).concat("...");
    }

    public boolean isReadOnly() {
        return false;
    }

    public Object getAdapter( Class adapter ) {
        return null;
    }

}
