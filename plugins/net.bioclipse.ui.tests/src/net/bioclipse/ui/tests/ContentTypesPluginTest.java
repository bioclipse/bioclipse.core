/*******************************************************************************
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
package net.bioclipse.ui.tests;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import junit.framework.Assert;
import net.bioclipse.core.MockIFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.junit.Test;


public class ContentTypesPluginTest{
 
    @Test public void testCMLSingle2d() throws Exception{
        URI uri = getClass().getResource("/testFiles/single2d.cml").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();
        IFile file=new MockIFile(path);
        IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
        InputStream stream = file.getContents();
        IContentType contentType = contentTypeManager.findContentTypeFor(stream, "single2d.cml");
        stream.close();
        Assert.assertEquals( "net.bioclipse.contenttypes.cml.singleMolecule2d",contentType.getId() );
    }
    @Test public void testCMLSingle3d() throws Exception{
        URI uri = getClass().getResource("/testFiles/single3d.cml").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();
        IFile file=new MockIFile(path);
        IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
        InputStream stream = file.getContents();
        IContentType contentType = contentTypeManager.findContentTypeFor(stream, "single3d.cml");
        stream.close();
        Assert.assertEquals( "net.bioclipse.contenttypes.cml.singleMolecule3d",contentType.getId() );
    }
    @Test public void testCMLSingle5d() throws Exception{
        URI uri = getClass().getResource("/testFiles/single5d.cml").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();
        IFile file=new MockIFile(path);
        IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
        InputStream stream = file.getContents();
        IContentType contentType = contentTypeManager.findContentTypeFor(stream, "single5d.cml");
        stream.close();
        Assert.assertEquals( "net.bioclipse.contenttypes.cml.singleMolecule5d",contentType.getId() );
    }
    @Test public void testCMLMultipleSingle2d() throws Exception{
        URI uri = getClass().getResource("/testFiles/multiple2d.cml").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();
        IFile file=new MockIFile(path);
        IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
        InputStream stream = file.getContents();
        IContentType contentType = contentTypeManager.findContentTypeFor(stream, "multiple2d.cml");
        stream.close();
        Assert.assertEquals( "net.bioclipse.contenttypes.cml.multipleMolecule2d",contentType.getId() );
    }
    @Test public void testCMLMultipleSingle3d() throws Exception{
        URI uri = getClass().getResource("/testFiles/multiple3d.cml").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();
        IFile file=new MockIFile(path);
        IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
        InputStream stream = file.getContents();
        IContentType contentType = contentTypeManager.findContentTypeFor(stream, "multiple3d.cml");
        stream.close();
        Assert.assertEquals( "net.bioclipse.contenttypes.cml.multipleMolecule3d",contentType.getId() );
    }
    @Test public void testCMLMultipleSingle5d() throws Exception{
        URI uri = getClass().getResource("/testFiles/multiple5d.cml").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();
        IFile file=new MockIFile(path);
        IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
        InputStream stream = file.getContents();
        IContentType contentType = contentTypeManager.findContentTypeFor(stream, "multiple5d.cml");
        stream.close();
        Assert.assertEquals( "net.bioclipse.contenttypes.cml.multipleMolecule5d",contentType.getId() );
    }
}
