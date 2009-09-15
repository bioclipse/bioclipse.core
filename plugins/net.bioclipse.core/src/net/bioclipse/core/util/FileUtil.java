/*******************************************************************************
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

import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

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
    
}
