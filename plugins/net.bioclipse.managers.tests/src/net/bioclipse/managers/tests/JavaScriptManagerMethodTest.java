package net.bioclipse.managers.tests;

import net.bioclipse.managers.business.JavaScriptManagerMethodDispatcher;
import net.bioclipse.managers.business.UglyHacker;


/**
 * @author jonalv
 *
 */
public class JavaScriptManagerMethodTest 
       extends AbstractManagerMethodDispatcherTest {

    static JavaScriptManagerMethodDispatcher d 
        = new JavaScriptManagerMethodDispatcher();

    static {
        UglyHacker.switchTransformer( d ); 
    }
    
    public JavaScriptManagerMethodTest() {
        super( d );
    }
}
