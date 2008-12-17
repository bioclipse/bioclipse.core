/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>.
 * 
 * Contributors:
 *     Arvid Berg
 *     
 ******************************************************************************/
package net.bioclipse.core;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
/**
 * @author arvid
 *
 */
public class MockIFileTest {
    @Test
    public void readTwice() throws CoreException {
        String path = getClass().getResource("MockIFileTest.class").getPath();
        try {
        IFile file = new MockIFile( path );
            InputStream is = file.getContents();
            is.read();
            is = file.getContents();
            is.read();
        } catch ( IOException e ) {
            fail();
        }
    }
    @Test
    public void readIsReady() {
        String path = getClass().getResource("MockIFileTest.class").getPath();
        assertNotNull( path );
        try {
        IFile file = new MockIFile( path );
            InputStream is = file.getContents();
            is.skip( is.available() );
            BufferedInputStream buf = new BufferedInputStream(file.getContents());
            InputStreamReader reader = new InputStreamReader(buf);
            BufferedReader br = new BufferedReader(reader);
            assertTrue( br.ready() );
        } catch ( IOException e ) {
            fail();
        } catch ( CoreException e) {
            fail();
        }    
    }
    @Test
        public void write() throws CoreException, IOException {
            byte[] bytes = new byte[]{12,54,56,78,26,45};
            IFile file =  new MockIFile();
            InputStream is = new ByteArrayInputStream(bytes);
            try {
                file.create( is, false, null );
            } catch ( CoreException e ) {
                fail(e.getMessage());
            }
            byte[] readBuffer= new byte[bytes.length];
            is = file.getContents();
            is.read( readBuffer );
            assertArrayEquals( bytes, readBuffer );
        }
    @Test
    public void createMock() throws CoreException, IOException {
        byte[] bytes = new byte[]{12,54,56,78,26,45};
        InputStream is = new ByteArrayInputStream(bytes);
        IFile file =  new MockIFile(is);
        byte[] readBuffer= new byte[bytes.length];
        is = file.getContents();
        is.read( readBuffer );
        assertArrayEquals( bytes, readBuffer );
    }
    @Test
    public void extension() {
        String path = getClass().getResource("MockIFileTest.class").getPath();
        try {
            IFile file = new MockIFile( path );
            assertEquals( "class", file.getFileExtension() );
            file = new MockIFile().extension( "MockFile." );            
            assertEquals( "", file.getFileExtension() );
            file = new MockIFile().extension( "MockFile" );
            assertEquals( "MockFile", file.getFileExtension() );
        } catch ( FileNotFoundException e ) {
           fail(e.getMessage());
        }
    }
}
