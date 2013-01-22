/* *****************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth
 *     Jonathan Alvarsson
 *
 ******************************************************************************/
package net.bioclipse.scripting.ui.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.Assert;
import org.junit.Test;

import net.bioclipse.core.MockIFile;
import net.bioclipse.core.tests.AbstractManagerTest;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.scripting.ui.business.IJsConsoleManager;
import net.bioclipse.scripting.ui.business.JsConsoleManager;

public class JsConsoleManagerTest extends AbstractManagerTest {

    JsConsoleManager console;

    //Do not use SPRING OSGI for this manager
    //since we are only testing the implementations of the manager methods
    public JsConsoleManagerTest() {
        console = new JsConsoleManager();
    }

    public IBioclipseManager getManager() {
        return console;
    }

    @Override
    public Class<? extends IBioclipseManager> getManagerInterface() {
        return IJsConsoleManager.class;
    }

    @Test
    public void testClear() {
    	console.clear();
    }
    
    @Test
    public void testDelay() {
    	console.delay(5);
    }
    
    @Test
    public void testEval() {
    	console.eval("i = 5;");
    }

    @Test
    public void testPrint() {
    	console.print("Hello world!");
    }

    @Test
    public void testSay() {
    	console.say("Hello world!");
    }

    @Test
    public void testExecuteFile() {
    	IFile file = new MockIFile(
            new ByteArrayInputStream("i = 5;".getBytes())
        ).extension( "js" );
    	console.executeFile(file);
    }
    
    @Test
    public void testExecute() throws Exception {
        final IFile file1 = new MockIFile(
            new ByteArrayInputStream("i = 5;".getBytes())
        ).extension( "js" );
        final IFile file2 = new MockIFile(
            new ByteArrayInputStream("i = 5;".getBytes())
        ).extension( "js" );
        console.executeFile(file1);
        console.execute( file1 );
        console.execute( new ArrayList<IFile>() {{add(file1); add(file2);}} );

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject("TEST");
        project.create(new NullProgressMonitor());
        project.open(new NullProgressMonitor());
        String filePath = "/TEST/testFile99883423427.js";
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
                              new Path(filePath)
                     );
        Job job = new Job("test") {
            @Override
            protected IStatus run( IProgressMonitor monitor ) {
                try {
                    file.create( new ByteArrayInputStream(
                                         "i = 5;".getBytes()), true, monitor );
                } catch ( CoreException e ) {
                    fail( e.getMessage() );
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
        job.setRule( file );
        job.schedule();
        job.join();

        final IFile savedFile = ResourcesPlugin.getWorkspace()
                                               .getRoot()
                                               .getFile( new Path(filePath) );
        
        console.execute( new ArrayList<String>() {{ 
                                 add(file.getFullPath().toString()); 
                                 add(file.getFullPath().toString());
                              }} );
    }
}
