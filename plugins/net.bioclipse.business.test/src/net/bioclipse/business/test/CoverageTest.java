/*******************************************************************************
 * Copyright (c) 2010  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package net.bioclipse.business.test;

import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.core.tests.coverage.AbstractCoverageTest;
import net.bioclipse.business.IBioclipsePlatformManager;
import net.bioclipse.business.BioclipsePlatformManager;

/**
 * JUnit tests for checking if the tested Manager is properly tested.
 */
public class CoverageTest extends AbstractCoverageTest {
    
    private static BioclipsePlatformManager manager = new BioclipsePlatformManager();

    @Override
    public IBioclipseManager getManager() {
        return manager;
    }

    @Override
    public Class<? extends IBioclipseManager> getManagerInterface() {
        return IBioclipsePlatformManager.class;
    }

}