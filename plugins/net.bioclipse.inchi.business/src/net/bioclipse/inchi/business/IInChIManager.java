/*******************************************************************************
 * Copyright (c) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/    
 ******************************************************************************/
package net.bioclipse.inchi.business;
import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.domain.IMolecule;
@PublishedClass ("Manager for creating InChI and InChIKeys.")
@TestClasses("net.bioclipse.inchi.business.test.InChIManagerTest")
public interface IInChIManager extends IBioclipseManager {
    @Recorded
    @PublishedMethod(
        params = "IMolecule molecule",
        methodSummary = "Generates the InChI for the given molecule.")
    @TestMethods("testGenerate")
    public String generate(IMolecule molecule) throws Exception;
    @Recorded
    @PublishedMethod(
        params = "IMolecule molecule",
        methodSummary = "Generates the InChIKey for the given molecule.")
    @TestMethods("testGenerateKey")
    public String generateKey(IMolecule molecule) throws Exception;
}
