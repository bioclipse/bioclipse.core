package net.bioclipse.ui.tests;

import net.bioclipse.ui.tests.feature.FeatureTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { FeatureTest.class} )
public class AllUIPluginTestSuite {

}