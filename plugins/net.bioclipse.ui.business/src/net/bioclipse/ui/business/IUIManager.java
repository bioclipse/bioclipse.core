/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.ui.business;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.IBioclipseManager;

import org.eclipse.core.resources.IFile;

/**
 * Controls programmatic access to the Bioclipse graphical user
 * interface (GUI).
 * 
 * @author masak
 *
 */
@PublishedClass(value = "Controls access to Bioclipse UI.")
public interface IUIManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(params="String filePath",
                     methodSummary="Opens a file in an editor.")
    public void open(String filePath);

    @Recorded
    public void open(IFile file);
    
    @Recorded
    @PublishedMethod(params="String filePath",
                     methodSummary="Deletes a file.")
    public void remove(String filePath);

    @Recorded
    public void remove(IFile file);
}
