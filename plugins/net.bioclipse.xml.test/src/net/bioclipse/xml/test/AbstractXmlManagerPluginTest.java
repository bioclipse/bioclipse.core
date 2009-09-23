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

import junit.framework.Assert;
import net.bioclipse.ui.business.UIManager;
import net.bioclipse.xml.business.IXmlManager;
import net.bioclipse.xml.business.Validation;

import org.junit.Test;

public abstract class AbstractXmlManagerPluginTest {

    protected static IXmlManager managerNamespace;
    
    private static UIManager ui = new UIManager();
    
    @Test public void testIsValid() throws Exception {
        Assert.fail("Not implemented yet.");
    }

    @Test public void testIsWellFormed() throws Exception {
        String filename = "/Virtual/" + this.hashCode() + ".xml";
        ui.newFile(filename,
            "<foo/>"
        );
        Validation.Event event = managerNamespace.isWellFormed(filename);
        Assert.assertEquals(Validation.Event.IS_WELL_FORMED, event);
    }

    @Test public void testIsNotWellFormed() throws Exception {
        String filename = "/Virtual/" + this.hashCode() + ".xml";
        ui.newFile(filename,
            "<foo>"
        );
        Validation.Event event = managerNamespace.isWellFormed(filename);
        Assert.assertEquals(Validation.Event.NOT_WELL_FORMED, event);
    }

    @Test public void testListNamespaces() {
        Assert.fail("Not implemented yet.");
    }

    @Test public void testValidateAgainstSchematron() {
        Assert.fail("Not implemented yet.");
    }

    @Test public void testValidateAgainstRelaxNG() {
        Assert.fail("Not implemented yet.");
    }

    @Test public void testValidateAgainstXMLSchema() {
        Assert.fail("Not implemented yet.");
    }

}
