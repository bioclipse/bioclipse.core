package net.bioclipse.managers.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( value = Suite.class )
@SuiteClasses( value = { JavaManagerMethodTest.class,
                         JavaScriptManagerMethodTest.class } )
public class AllManagerMethodDispatcherTests {

}
