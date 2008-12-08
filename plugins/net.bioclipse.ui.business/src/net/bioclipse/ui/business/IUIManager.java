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

import java.io.InputStream;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.IBioclipseManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Controls programmatic access to the Bioclipse graphical user
 * interface (GUI).
 * 
 * @author masak
 *
 */
@PublishedClass(value = "Controls access to Bioclipse UI.")
@TestClasses(
    "net.bioclipse.ui.business.tests.UIManagerTest," +
    "net.bioclipse.ui.business.tests.UIManagerPluginTest"
)
public interface IUIManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(params="String filePath",
                     methodSummary="Opens a file in an editor.")
    @TestMethods("testOpen_String")
    public void open(String filePath);

    @Recorded
    public void open(IFile file);
    
    @Recorded
    @PublishedMethod(params="String filePath",
                     methodSummary="Deletes a file.")
    @TestMethods("testSaveAndRemove_String")
    public void remove(String filePath);

    @Recorded
    @TestMethods("testSaveAndRemove_IFile")
    public void remove(IFile file);

    @Recorded
    @PublishedMethod(
        params="String path to save to, InputStream content to save",
        methodSummary="Save the content of the InputStream to the given path."
    )
    @TestMethods("testSaveAndRemove_String")
    public void save(String filePath, InputStream toWrite);

    @Recorded
    @PublishedMethod(
        params="IFile file to save to, InputStream content to save",
        methodSummary="Save the content of the InputStream to the file."
    )
    @TestMethods("testSaveAndRemove_IFile")
    public void save(IFile file, InputStream toWrite,
                     IProgressMonitor monitor, Runnable callBackFunction);

}
