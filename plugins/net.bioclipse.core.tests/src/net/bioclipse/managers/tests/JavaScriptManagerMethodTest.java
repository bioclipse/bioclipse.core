package net.bioclipse.managers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.bioclipse.managers.business.JavaScriptManagerMethodDispatcher;
import net.bioclipse.managers.tests.AbstractManagerMethodDispatcherTest.MyInvocation;

/**
 * @author jonalv
 *
 */
public class JavaScriptManagerMethodTest 
       extends AbstractManagerMethodDispatcherTest {

    public JavaScriptManagerMethodTest() {
        super( new JavaScriptManagerMethodDispatcher() );
    }
    
    @Test
    public void convertReturnIFileToString() throws Throwable {
        assertTrue( file.exists() );
        
        assertEquals( PATH + FILENAME, 
                      dispatcher.invoke( 
                          new MyInvocation(
                              ITestManager.class.getMethod( "returnsAFile", 
                                                            String.class ),
                          new Object[] { PATH + FILENAME },
                          m ) ) );
        assertMethodRun();
    }
}
