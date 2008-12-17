/*******************************************************************************
 * Copyright (c) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint.business.test;
import net.bioclipse.cdk.jchempaint.business.JChemPaintManager;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.tests.coverage.AbstractCoverageTest;
/**
 * JUnit tests for checking if the tested Manager is properly tested.
 * 
 * @author egonw
 */
public class CoverageTest extends AbstractCoverageTest {
    private JChemPaintManager manager = new JChemPaintManager();
    @Override
    public IBioclipseManager getManager() {
        return manager;
    }
}
