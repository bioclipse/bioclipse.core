package net.bioclipse.core.business;

import net.bioclipse.managers.business.JavaScriptManagerMethodDispatcher;


/**
 * @author jonalv
 *
 */
public class JavaScriptManagerMethodTest 
       extends AbstractManagerMethodDispatcherTest {

    public JavaScriptManagerMethodTest() {
        super( new JavaScriptManagerMethodDispatcher() );
    }
}
