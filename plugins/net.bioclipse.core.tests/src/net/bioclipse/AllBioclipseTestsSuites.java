/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse;

import net.bioclipse.biojava.business.AllBiojavaBusinessTestsSuite;
import net.bioclipse.biomoby.tests.AllBiomobyTestsSuite;
import net.bioclipse.cdk.AllCDKBusinessTestsSuite;
import net.bioclipse.hsqldb.AllHSQLDBTestsSuite;
import net.bioclipse.scripting.AllScriptingTestsSuite;
import net.bioclipse.structuredb.AllStructuredbTestsSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { AllBiojavaBusinessTestsSuite.class, 
                         AllBiomobyTestsSuite.class,
                         AllCDKBusinessTestsSuite.class, 
                         AllHSQLDBTestsSuite.class, 
                         AllScriptingTestsSuite.class,
                         AllStructuredbTestsSuite.class,
                         AllCoreTestsSuite.class, } )
public class AllBioclipseTestsSuites {
}
