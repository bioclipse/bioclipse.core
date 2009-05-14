package net.bioclipse.gist.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( value = Suite.class )
@SuiteClasses( value = { JavaGistManagerPluginTest.class,
                         JavaScriptGistManagerPluginTest.class } )
public class AllGistManagerPluginTests {

}
