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

import java.io.IOException;
import java.io.InputStream;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.scripting.ui.business.GuiAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
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
    @GuiAction
    public void open(String filePath);

    @Recorded
    @PublishedMethod(params="String filePath, String editor",
                     methodSummary="Opens a file in an editor specified by ID or alias.")
    @GuiAction
    public void open(String filePath, String editor) throws BioclipseException;

    @Recorded
    @GuiAction
    public void open(IFile file);

    @Recorded
    @GuiAction
    @PublishedMethod(params="IBioObject bioObject=Object to open in editor, " +
    		"String editor = the editorID or alias",
                     methodSummary="Opens a file in the designated editor.")
    public void open( final IBioObject bioObject, final String editor) 
            throws BioclipseException;

    @Recorded
    @PublishedMethod(params="String filePath",
                     methodSummary="Deletes a file.")
    @TestMethods("testSaveAndRemove_String")
    public void remove(String filePath);

    @Recorded
    @TestMethods("testSaveAndRemove_IFile")
    public void remove(IFile file, IProgressMonitor monitor);

    @Recorded
    @PublishedMethod(
        params="String filePath, InputStream content",
        methodSummary="Save the content of the InputStream to the given path."
    )
    @TestMethods("testSaveAndRemove_String")
    public void save(String filePath, InputStream toWrite);

    @Recorded
    @TestMethods("testSaveAndRemove_IFile")
    public void save(IFile file, InputStream toWrite,
                     IProgressMonitor monitor, Runnable callBackFunction);

    @Recorded
    @TestMethods("testExists_IFile")
    public boolean fileExists(IFile file);

    @Recorded
    @PublishedMethod(
        params="String filePath",
        methodSummary="Determines if the given file exists in the workspace"
    )
    @TestMethods("testExists_String")
    public boolean fileExists(String filePath);

    @Recorded
    @GuiAction
    @PublishedMethod(
        params="IBioObject bioObject",
        methodSummary="Opens the IBikoObject in its preferred editor"
    )
    void open( IBioObject bioObject ) throws BioclipseException, CoreException, 
        IOException;

    @Recorded
    @GuiAction
    @PublishedMethod(
        methodSummary="Returns a list of available editors"
    )
    public void getEditors() throws BioclipseException;
}
