/* *****************************************************************************
 * Copyright (c) 2008  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.ui.business.tests;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.ui.business.IUIManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public abstract class AbstractUIManagerPluginTest {

    protected static IUIManager ui;
    
    @Test
    public void testManagerInstantiation() {
    	// the instance is created by the subclass prior to this test
    	Assert.assertNotNull(ui);
    }
    
    @Ignore
    @Test
    public void testOpenListIFile() throws CoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject("TEST");
        project.create(new NullProgressMonitor());
        project.open(new NullProgressMonitor());
        String filePath = "/TEST/testFile99883423427.txt";
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
                         new Path(filePath)
                     );
        ui.save( file, 
                 new ByteArrayInputStream("test file".getBytes()), 
                 null );
        final IFile savedFile = ResourcesPlugin.getWorkspace()
                                               .getRoot()
                                               .getFile( new Path(filePath) );
        Assert.assertTrue(savedFile.exists());
        ui.openFiles( new ArrayList<IFile>() {{add(savedFile);}});
        project.delete(true, new NullProgressMonitor());
    }
    
    @Ignore
    public void testOpen_String() {
        String filePath = "/Virtual/testFile99883423426.txt";
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        ui.save(
            file, new ByteArrayInputStream("test file".getBytes()), null
        );
        IFile savedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertTrue(savedFile.exists());
        ui.open(filePath);
    }

    @Ignore
    public void testSaveAndRemove_IFile() {
        String filePath = "/Virtual/testFile683442689.txt";
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        ui.save(
            file, new ByteArrayInputStream("test file".getBytes()),
            null
        );
        IFile savedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertTrue(savedFile.exists());
        ui.remove(savedFile);
        IFile removedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertFalse(removedFile.exists());
    }

    @Ignore
    public void testSaveAndRemove_String() {
        String filePath = "/Virtual/testFile124879043.txt";
        ui.save(
            filePath, new ByteArrayInputStream("test file".getBytes())
        );
        IFile savedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertTrue(savedFile.exists());
        ui.remove(filePath);
        IFile removedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertFalse(removedFile.exists());
    }

    @Ignore
    public void testExists_String() {
        String filePath = "/Virtual/testFile887434232.txt";
        ui.save(
            filePath, new ByteArrayInputStream("test file".getBytes())
        );
        Assert.assertTrue(ui.fileExists(filePath));
        ui.remove(filePath);
        Assert.assertFalse(ui.fileExists(filePath));
    }

    @Ignore
    public void testExists_IFile() {
        String filePath = "/Virtual/testFile734248911.txt";
        ui.save(
            filePath, new ByteArrayInputStream("test file".getBytes())
        );
        IFile savedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertTrue(ui.fileExists(savedFile));
        ui.remove(filePath);
        IFile removedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertFalse(ui.fileExists(removedFile));
    }

    @Ignore
    public void testReadFileIntoArray_IFile() throws BioclipseException {
        String filePath = "/Virtual/testFile734248911.txt";
        ui.save(
            filePath, new ByteArrayInputStream("test file".getBytes())
        );
        IFile savedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertTrue(ui.fileExists(savedFile));
        String[] contents = ui.readFileIntoArray(savedFile);
        Assert.assertNotNull(contents);
        Assert.assertEquals(1, contents.length);
        Assert.assertEquals("test file", contents[0]);
    }
    
    @Test
    public void testBuildPath() throws BioclipseException {
    	
    	//test components version
    	String root = "/project1/path2";
    	String component = "subpath";
    	
    	String project = "project1";
    	String folder  = "path2";
    	
    	String[] rootComponents = new String[2];
    	rootComponents[0] = project;
    	rootComponents[1] = folder;
    	
    	String rootpath = ui.buildPath(rootComponents);
    	Assert.assertEquals(root, rootpath);
    	
    	//test root component version
    	String[] components = new String[2];
    	components[0] = folder;
    	components[1] = component;
    	String componentsPath = ui.buildPath("/project1", components);
    	Assert.assertEquals(root + "/" + component, componentsPath);
    	
    	//test root component with one element array
    	components = new String[1];
    	components[0] = folder;
    	String path = ui.buildPath("/project2", components);
    	Assert.assertEquals("/project2/path2", path);
    	
    	//test root + 1 component as string version
    	
    	path = ui.buildPath("/project1/", folder);
    	Assert.assertEquals(root, path);    	
    }
    
    
    @Before
    public void setupTestProject() throws CoreException, BioclipseException {

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

    	String projectName = ui.newProject("nelson");
        IProject project = ui.getProject(projectName);
        IPath path = project.getFullPath();
        
        path = path.append("horatio");
        IFolder horatio = root.getFolder(path);
        horatio.create(false, true, null);
        
        path = path.append("admiral");
        IFolder admiral = root.getFolder(path);
        admiral.create(false, true, null);
        
        path = project.getFullPath();
        
        path = path.append("mandela");
        IFolder mandela = root.getFolder(path);
        mandela.create(false, true, null);
        
        ui.newFile(horatio.getFullPath().append("trafalgar.battle").toString());
        ui.newFile(horatio.getFullPath().append("nil.battle").toString());
        
        ui.newFile(admiral.getFullPath().append("hms-victory.ship").toString());
                
    }
    
    @After
    public void tearDownTestProject() throws CoreException, BioclipseException {
        IProject project = ui.getProject("nelson");
        project.delete(true, null);
    }
    
    @Test
    public void testGetFolders() throws CoreException, BioclipseException {
    	    	
    	String project = "/nelson";
    	String[] foldersExpected = { project + "/horatio", project + "/mandela" };    	
    	String[] foldersActual = ui.getSubFolders(project);
    	Assert.assertArrayEquals(foldersExpected, foldersActual);
    	
    	String[] subFoldersExpected = { project + "/horatio/admiral"};
    	foldersActual = ui.getSubFolders(project+"/horatio");
    	Assert.assertArrayEquals(subFoldersExpected, foldersActual);
    	
    }
    
    @Test
    public void testGetFiles() throws CoreException, BioclipseException {
    	
    	String path = "/nelson/horatio";
    	String[] horatioExpected = { "/nelson/horatio/nil.battle", 
    			                     "/nelson/horatio/trafalgar.battle"};
    	String[] horatioActual = ui.getFiles(path);
    	Assert.assertArrayEquals(horatioExpected, horatioActual);
    	
    	path = path + "/admiral";
    	String[] admiralExpected = { path + "/hms-victory.ship"};
    	String[] admiralActual = ui.getFiles(path);
    	Assert.assertArrayEquals(admiralExpected, admiralActual);
    	
    	path = "/nelson/mandela";
    	String[] mandelaExpected = {};
    	String[] mandelaActual = ui.getFiles(path);
    	Assert.assertArrayEquals(mandelaExpected, mandelaActual);

    }
}
