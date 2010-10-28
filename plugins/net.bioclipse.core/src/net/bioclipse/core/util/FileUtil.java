/* *****************************************************************************
 * Copyright (c) 2009  Ola Spjuth <ospjuth@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import net.bioclipse.core.api.BioclipseException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

/**
 * Utility methods to manipulate files in Bioclipse
 * @author ola
 *
 */
public class FileUtil {

    private static final String LINKED_PROJECT = "Linked Files";

    /**
     * Create a IFile by linking to the file system
     * @param path absolute path to a file in local file system
     * @return IFile linking to the file
     * @throws CoreException
     */
    public static IFile createLinkedFile(String path) throws CoreException{

        IPath location = new Path(path);
        
        
      Random generator=new Random(System.currentTimeMillis());
      String newFileName=  + (generator.nextInt(9000) + 1000) +".sdf";

        
        IFile file = getLinkedProject().getFile(location.lastSegment());
        if(file.exists()){
            IPath p = file.getRawLocation(); // link file path
            if(p.equals( location ))
                return file;
        }
        
        file.createLink(location, IResource.NONE, null);
        return file;

    }

    /**
     * Get the Linked project.
     * @return A special hidden project that contains linked resources
     * @throws CoreException
     */
    private static IProject getLinkedProject() throws CoreException{
        
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        IProject project = ws.getRoot().getProject(LINKED_PROJECT);
        if (!project.exists())
            project.create(null);
        if (!project.isOpen())
            project.open(null);
        
        project.setHidden( true );
        return project;
    }

    /**
     * Delete the Linked project
     * @throws CoreException
     */
    public static void deleteLinkedProject() throws CoreException{
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        IProject project = ws.getRoot().getProject(LINKED_PROJECT);
        if (project.exists())
            project.delete( true, new NullProgressMonitor() );
    }
    
    /**
     * Get the absolute path from a file with plugin-relative path
     * @return absolute path to file.
     * @throws IOException 
     */
    public static String getFilePath(String pluginRelativePath, String pluginID) 
    	throws IllegalArgumentException, IOException {
    	
    	if (pluginRelativePath==null || pluginRelativePath.isEmpty()) 
    		throw new IllegalArgumentException("pluginRelativePath must " +
    				"not be empty");

    	if (pluginID==null || pluginID.isEmpty()) 
    		throw new IllegalArgumentException("pluginID must " +
    				"not be empty");

    	URL url = FileLocator.toFileURL(Platform.getBundle(pluginID)
    			.getEntry(pluginRelativePath));
    	File file=new File(url.getFile());
    	if (!file.exists())
    		throw new IOException("File: " + pluginRelativePath 
    				+ " does not exist in plugin: " + pluginID);

    	return url.getFile();
    }
}
