package net.bioclipse;

import net.bioclipse.biojava.ui.test.AllBiojavaUiTestsSuite;
import net.bioclipse.cdk.ui.tests.AllCDKUiTestsSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { AllBiojavaUiTestsSuite.class,
                         AllCDKUiTestsSuite.class } )
public class AllBioclipsePluginTestsSuites {

}
