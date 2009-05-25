/*******************************************************************************
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

import net.bioclipse.ui.business.UIManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.Test;

public class UIManagerPluginTest {

    private UIManager manager = new UIManager();
    
    @Test public void testOpen_String() {
        String filePath = "/Virtual/testFile99883423426.txt";
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        manager.save(
            file, new ByteArrayInputStream("test file".getBytes()),
            null
        );
        IFile savedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertTrue(savedFile.exists());
        manager.open(filePath);
    }

    @Test public void testSaveAndRemove_IFile() {
        String filePath = "/Virtual/testFile683442689.txt";
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        manager.save(
            file, new ByteArrayInputStream("test file".getBytes()),
            null
        );
        IFile savedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertTrue(savedFile.exists());
        manager.remove(savedFile, null);
        IFile removedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertFalse(removedFile.exists());
    }

    @Test public void testSaveAndRemove_String() {
        String filePath = "/Virtual/testFile124879043.txt";
        manager.save(
            filePath, new ByteArrayInputStream("test file".getBytes())
        );
        IFile savedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertTrue(savedFile.exists());
        manager.remove(filePath);
        IFile removedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertFalse(removedFile.exists());
    }

    @Test public void testExists_String() {
        String filePath = "/Virtual/testFile887434232.txt";
        manager.save(
            filePath, new ByteArrayInputStream("test file".getBytes())
        );
        Assert.assertTrue(manager.fileExists(filePath));
        manager.remove(filePath);
        Assert.assertFalse(manager.fileExists(filePath));
    }

    @Test public void testExists_IFile() {
        String filePath = "/Virtual/testFile734248911.txt";
        manager.save(
            filePath, new ByteArrayInputStream("test file".getBytes())
        );
        IFile savedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertTrue(manager.fileExists(savedFile));
        manager.remove(filePath);
        IFile removedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path(filePath)
        );
        Assert.assertFalse(manager.fileExists(removedFile));
    }

}
