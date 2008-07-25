package net.bioclipse;

import net.bioclipse.biojava.business.AllBiojavaBusinessTestsSuite;
import net.bioclipse.biojava.ui.test.AllBiojavaUiTestsSuite;
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
                         })
public class AllBioclipseTestsSuites {
}
