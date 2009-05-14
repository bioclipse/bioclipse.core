package net.bioclipse.managers.tests;

import net.bioclipse.managers.business.JavaManagerMethodDispatcher;

/**
 * @author jonalv
 *
 */
public class JavaManagerMethodTest 
       extends AbstractManagerMethodDispatcherTest {

    public JavaManagerMethodTest() {
        super( new JavaManagerMethodDispatcher() );
    }
}
