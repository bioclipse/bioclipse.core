/* *****************************************************************************
 * Copyright (c) 2007-2009 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jonathan Alvarsson
 *               Arvid Berg <goglepox@users.sf.net>
 *
 *******************************************************************************/
package net.bioclipse.core;

import org.eclipse.core.resources.IFile;

public interface IResourcePathTransformer {

    /**
     * Converts resourceString to an IFile. First check if the path is a
     * workspace relative path, if that fails it tries to lookup the file using
     * an URI. Last it assumes the path is an absolute path to the file system
     * not in the workspace and creates a link in /Virtual.
     *
     * @param resourceString
     * @return IFile
     */
    public IFile transform( String resourceString );

}