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
import org.eclipse.core.resources.IResource;
import org.junit.*;
public class BioclipseStoreTest {
    @Test
    public void testPutAndGet() throws FileNotFoundException {
        Object key     = new Object();
        Object model   = new Object();
        IResource file = new MockIFile();
        BioclipseStore.put( file, key, model );
        assertSame( model, BioclipseStore.get( file, key ) ); 
    }
    @Test
    public void testMultiplePutAndGet() throws FileNotFoundException {
        Object key1    = new Object();
        Object model1  = new Object();
        Object key2    = new Object();
        Object model2  = new Object();
        IResource file = new MockIFile();
        BioclipseStore.put( file, key1, model1 );
        BioclipseStore.put( file, key2, model2 );
        assertSame( model1, BioclipseStore.get( file, key1 ) );
        assertSame( model2, BioclipseStore.get( file, key2 ) );
    }
}
