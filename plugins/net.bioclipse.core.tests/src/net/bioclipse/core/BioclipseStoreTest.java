/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.core;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import net.bioclipse.core.domain.ICachedModel;
import net.bioclipse.core.domain.IModelChangedListener;

import org.eclipse.core.resources.IFile;
import org.junit.*;

public class BioclipseStoreTest {

    @Test
    public void testPut() throws FileNotFoundException {
        ICachedModel o = new ICachedModel() {
            public void addChangeListener( IModelChangedListener listener ) {
            }
            public void fireChangeEvent() {
            }
            public void removeChangeListener( IModelChangedListener listener ) {
            }
        };
        IFile f = new MockIFile();
        BioclipseStore.put( o, f, o.getClass() );
        
        assertTrue( BioclipseStore.instance.models.containsValue( o ) );
        assertTrue( 
            BioclipseStore.instance
                          .modelKeysForLocation
                          .get( BioclipseStore
                                .generateLocationsKey( f ) )
                          .contains( BioclipseStore
                                     .generateModelsKey( f, 
                                                         o.getClass() ) 
                                   ) );
        
    }
}
