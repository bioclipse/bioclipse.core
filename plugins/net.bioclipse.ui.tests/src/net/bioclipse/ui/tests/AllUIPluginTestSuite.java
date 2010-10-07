package net.bioclipse.ui.tests;

import net.bioclipse.ui.tests.feature.FeatureTest;
import net.bioclipse.ui.views.MyViewTestCase;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { ContentTypesPluginTest.class,
                         FeatureTest.class,
                         MyViewTestCase.class} )
public class AllUIPluginTestSuite {

}