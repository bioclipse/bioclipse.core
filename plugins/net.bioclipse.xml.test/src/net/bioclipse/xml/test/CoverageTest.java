/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package net.bioclipse.xml.test;

import net.bioclipse.core.tests.coverage.AbstractCoverageTest;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.xml.business.IXmlManager;
import net.bioclipse.xml.business.XmlManager;

/**
 * JUnit tests for checking if the tested Manager is properly tested.
 */
public class CoverageTest extends AbstractCoverageTest {
    
    private static XmlManager manager = new XmlManager();

    @Override
    public IBioclipseManager getManager() {
        return manager;
    }

    @Override
    public Class<? extends IBioclipseManager> getManagerInterface() {
        return IXmlManager.class;
    }

}