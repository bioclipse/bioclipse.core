/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.xml.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.tests.AbstractManagerTest;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.ui.business.UIManager;
import net.bioclipse.xml.business.IXmlManager;
import net.bioclipse.xml.business.XMLError;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class AbstractXmlManagerPluginTest extends AbstractManagerTest {

    protected static IXmlManager managerNamespace;
    
    private static UIManager ui = new UIManager();

    @BeforeClass
    public static void setUpVirtualProject() throws Exception {
        net.bioclipse.core.Activator.getVirtualProject();
        copyResourceIntoVirtual(
            "/testFiles/valid.xml",
            "validRNG.xml"
        );
        copyResourceIntoVirtual(
            "/testFiles/invalid.xml",
            "invalidRNG.xml"
        );
        copyResourceIntoVirtual(
            "/testFiles/datatype-sample.rng",
            "schema.rng"
        );
    }

    @Override
    public IBioclipseManager getManager() {
        return managerNamespace;
    }

    @Override
    public Class<? extends IBioclipseManager> getManagerInterface() {
        return IXmlManager.class;
    }

    @Test public void testIsValid() throws Exception {
        Assert.fail("Not implemented yet.");
    }

    @Test public void testIsWellFormed() throws Exception {
        String filename = "/Virtual/" + this.hashCode() + ".xml";
        ui.newFile(filename,
            "<foo/>"
        );
        Assert.assertTrue(managerNamespace.isWellFormed(filename));
    }

    @Test public void testIsNotWellFormed() throws Exception {
        String filename = "/Virtual/" + this.hashCode() + ".xml";
        ui.newFile(filename,
            "<foo>"
        );
        Assert.assertFalse(managerNamespace.isWellFormed(filename));
    }

    @Test public void testListNamespaces() {
        Assert.fail("Not implemented yet.");
    }

    @Test public void testValidateAgainstSchematron() {
        Assert.fail("Not implemented yet.");
    }

    private static IFile copyResourceIntoVirtual(
        String resource, String virtualName) throws Exception {
        URL url = AbstractXmlManagerPluginTest.class.
            getResource(resource).toURI().toURL();
        IProject project = net.bioclipse.core.Activator.getVirtualProject();
        IFile file = project.getFile(virtualName);
        if (!file.exists()) {
            file.create(url.openStream(), IResource.NONE, null);
        } else {
            file.setContents(
                url.openStream(), true, false, new NullProgressMonitor()
            );
        }
        return file;
    }
    
    @Test public void testValidateAgainstRelaxNG() throws Exception {
        List<XMLError> errors = managerNamespace.validateAgainstRelaxNG(
            "/Virtual/validRNG.xml",
            "/Virtual/schema.rng"
        );
        Assert.assertEquals(0, errors.size());
    }

    @Test public void testValidateAgainstRelaxNG_Invalid() throws Exception {
        List<XMLError> errors = managerNamespace.validateAgainstRelaxNG(
            "/Virtual/invalidRNG.xml",
            "/Virtual/schema.rng"
        );
        Assert.assertNotSame(0, errors.size());
    }

    @Test public void testValidateAgainstXMLSchema() {
        Assert.fail("Not implemented yet.");
    }

    @Test public void testValidate() {
        Assert.fail("Not implemented yet.");
    }

}
