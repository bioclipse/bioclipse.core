/* *****************************************************************************
 * Copyright (c) 2009  Ola Spjuth <ola@bioclipse.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package net.bioclipse.webservices.tests;

import net.bioclipse.core.tests.coverage.AbstractCoverageTest;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.webservices.business.IWebservicesManager;
import net.bioclipse.webservices.business.WebservicesManager;

/**
 * JUnit tests for checking if the tested Manager is properly tested.
 */
public class CoverageTest extends AbstractCoverageTest {
    
    private static WebservicesManager manager = new WebservicesManager();

    @Override
    public IBioclipseManager getManager() {
        return manager;
    }

    @Override
    public Class<? extends IBioclipseManager> getManagerInterface() {
        return IWebservicesManager.class;
    }

}