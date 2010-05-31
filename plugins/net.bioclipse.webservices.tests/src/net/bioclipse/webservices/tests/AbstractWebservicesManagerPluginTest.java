/* *****************************************************************************
 * Copyright (c) 2009-2010  Ola Spjuth <ola@bioclipse.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.webservices.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.webservices.business.IWebservicesManager;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

/**
 * 
 * @author ola
 *
 */
public abstract class AbstractWebservicesManagerPluginTest {

    protected static IWebservicesManager managerNamespace;


    @Test
    public void testQuerySinglePDB() throws BioclipseException, 
                                          CoreException, 
                                          IOException{

        List<String> pdblist = managerNamespace.queryPDB( "1d66" );
        assertEquals( 1, pdblist.size() );
        assertTrue( isValidPDBString(pdblist.get( 0 )) );
    }

    @Test
    public void testQueryMultiplePDBs() throws BioclipseException, 
                                          CoreException, 
                                          IOException{

        List<String> pdblist = managerNamespace.queryPDB( "1d66,1ale" );
        assertEquals( 2, pdblist.size() );
        assertTrue( isValidPDBString(pdblist.get( 0 )) );
        assertTrue( isValidPDBString(pdblist.get( 1 )) );

    }
    
    @Test
    public void testQuerySinglePDBtoVirtual() throws BioclipseException, 
                                          CoreException, 
                                          IOException{

        List<String> pdblist = managerNamespace.queryPDB( "2pdz", 
                             net.bioclipse.core.Activator.getVirtualProject() );
        assertEquals( 1, pdblist.size() );
        assertTrue( isValidPDBString(pdblist.get( 0 )) );

    }
    
    @Test
    public void testQuerySinglePDBtoProjectByPath() throws BioclipseException, 
                                          CoreException, 
                                          IOException{

        //Test dl single pdb to existing file based project by path as String
        IProject testProject = ResourcesPlugin.getWorkspace().getRoot()
        .getProject("PluginTestProject");

        if (!(testProject.exists())){
            testProject.create( new NullProgressMonitor() );
        }
        testProject.open( new NullProgressMonitor() );

        List<String> pdblist = managerNamespace.queryPDB( "1pdz", 
                                       testProject.getFullPath().toOSString() );
        assertEquals( 1, pdblist.size() );
        assertTrue( isValidPDBString(pdblist.get( 0 )) );

    }

    private boolean isValidPDBString( String string ){

        // TODO IMPLEMENT
        return true;
    }


}
