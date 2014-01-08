/* *****************************************************************************
 * Copyright (c) 2007-2009 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contributors:
 *     Jonathan Alvarsson
 *     Christian Hofbauer
 *
 ******************************************************************************/
package net.bioclipse.ui.business;

import java.io.InputStream;
import java.util.List;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.GuiAction;
import net.bioclipse.managers.business.IBioclipseManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;

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
    "net.bioclipse.ui.business.tests.AbstractUIManagerPluginTest"
)
public interface IUIManager extends IBioclipseManager {


    @Recorded
    @GuiAction
    @PublishedMethod(
        params = "Object object",
        methodSummary = "Tries to open the object in an editor "
    )
    public void open(final Object object);
    
    @Recorded
    @PublishedMethod(params="List<Object> files",
    methodSummary="Opens a list of files (e.g. a gist) represented either " +
    		          "as IFiles or Strings in an editor.")
    @GuiAction
    public void openFiles( List<?> files );
    
    @Recorded
    @GuiAction
    @PublishedMethod(
        params = "Object object, String editor",
        methodSummary = "Opens a file in the designated editor, identified " +
        		            "by editor id or shortname." )
    public void open( final Object object, final String editor);

    @PublishedMethod(
        params = "",
        methodSummary = "List available editor ids"
                    )
    public void listEditorIDs();
    
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
        params = "String filePath, InputStream content",
        methodSummary = "Save the content of the InputStream to the given path."
    )
    @TestMethods("testSaveAndRemove_String")
    public void save(String filePath, InputStream toWrite);

    @Recorded
    @PublishedMethod(
        params = "String filePath, String content",
        methodSummary = "Save the content of the String to the given path."
    )
    public void save(String filePath, String toWrite);

    
    @Recorded
    @TestMethods("testSaveAndRemove_IFile")
    public void save( IFile file, 
                      InputStream toWrite,
                      Runnable callBackFunction );

    @Recorded
    @PublishedMethod(
        params = "String filePath, InputStream content",
        methodSummary = "Append the content of the InputStream to the given path."
    )
    public void append(String filePath, InputStream toWrite);

    @Recorded
    @PublishedMethod(
        params = "String filePath, String content",
        methodSummary = "Append the content of the String to the given path."
    )
    public void append(String filePath, String toWrite);

    
    @Recorded
    public void append( IFile file, 
                      InputStream toWrite,
                      Runnable callBackFunction );

    @Recorded
    @TestMethods("testExists_IFile")
    public boolean fileExists(IFile file);

    @Recorded
    @PublishedMethod(
        params = "String filePath",
        methodSummary = "Returns whether given file path point to a file " +
        		        "that exists in the workspace"
    )
    @TestMethods("testExists_String")
    public boolean fileExists(String filePath);

    @Recorded
    @GuiAction
    @PublishedMethod(
        methodSummary = "Returns a list of available editors"
    )
    public void getEditors() throws BioclipseException;

    @Recorded
    @PublishedMethod(
        params = "String path",
        methodSummary = "Creates an empty file at the specified path"
    )
    public IFile newFile( String path) throws CoreException, BioclipseException;

    @Recorded
    @PublishedMethod(
        params = "String path, String content",
        methodSummary = "Creates a new file at the specified path, with the " +
        		"given content."
    )
    public IFile newFile(String path, String content)
    throws CoreException, BioclipseException;
    
    @Recorded
    @PublishedMethod(methodSummary = "Closes the active editor")
    public void closeActiveEditor();

    @Recorded
    @PublishedMethod( params="String path",
                      methodSummary="Closes all editors working on a file" )
    public void closeEditor(String path);

    public void closeEditor(IFile file);

    @Recorded
    @GuiAction
    public void revealAndSelect( IFile file ) throws BioclipseException;

    @Recorded
    @GuiAction
    @PublishedMethod( params="String path",
                      methodSummary = "Reveals a file in the Navigator and " +
                      		          "selects it" )
    public void revealAndSelect( String path ) throws BioclipseException;

    @Recorded
    @PublishedMethod( params = "String path",
                      methodSummary = "Refresh resource given by path" )
    public void refresh(String path) throws BioclipseException;

    @Recorded
    @PublishedMethod( params="String feature",
                      methodSummary="Assert a feature is installed." )
    public void assertInstalled( String feature ) throws BioclipseException;

    @Recorded
    @PublishedMethod( methodSummary = "Returns a list of the installed " +
    		                          "features." )
    public List<String> getInstalledFeatures();
    
    
    @Recorded
    @PublishedMethod( params="String path",
                      methodSummary="Reads a file line by line into a String" )
    public String readFile(String path) throws BioclipseException;
    public String readFile(IFile file) throws BioclipseException;

    @Recorded
    @PublishedMethod( params="String path",
                      methodSummary="Reads a file line by line into a String[]" )
    public String[] readFileIntoArray(String path) throws BioclipseException;
    public String[] readFileIntoArray(IFile file) throws BioclipseException;

    @Recorded
    @PublishedMethod(
        params="String type, IContentType contentType",
        methodSummary="Checks if the given content type is of the given type," +
            " either as itself or as one of its base types."
    )
    public boolean isContentType(String type, IContentType contentType)
        throws BioclipseException;

    @Recorded
    @PublishedMethod(
        params="String name",
        methodSummary="Create a new project by name."
    )
    public String newProject(String name) 
        throws CoreException, BioclipseException;

    @Recorded
    @PublishedMethod(
        params="String name",
        methodSummary="Return a project by name."
    )
    public IProject getProject(String name) 
        throws CoreException, BioclipseException;
    
    @Recorded
    @PublishedMethod(
       params="String folder",
       methodSummary="Retrieves the full path information of all files in a folder"
    )
    public String[] getFiles(String folder)
       throws CoreException, BioclipseException;
    
    
    @Recorded
    @PublishedMethod(
       params="String folder",
       methodSummary="Retrieves the full path information of all subfolders in a folder"
    )
    public String[] getSubFolders(String folder)
       throws CoreException, BioclipseException;
    
    @Recorded
    @PublishedMethod(
       params="String[] components",
       methodSummary="Creates a path based on the components in the array. "
       		+ "The first component indicates the project, the next one the folder "
       		+ "followed by a set of subsequent subfolders.")
    public String buildPath(String[] components)
       throws BioclipseException;

    @Recorded
    @PublishedMethod(
       params="String root, String component",
       methodSummary="Creates a path starting from an existing root path and "
       		+ "extended by one additional component.")
    public String buildPath(String root, String component)
       throws BioclipseException;

    @Recorded
    @PublishedMethod(
       params="String root, String[] components",
       methodSummary="Creates a path starting from an existing root path and "
       		+ "extended by the components in the array. "
       		+ "This form of the method can be used to extend an existing path")
    public String buildPath(String root, String[] components)
       throws BioclipseException;
}
