package net.bioclipse.managers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.junit.Test;

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
    
    @Test
    public void returnIFile() throws Throwable {
        assertTrue( file.exists() );
        
        assertEquals( file, 
                      dispatcher.invoke( 
                          new MyInvocation(
                              ITestManager.class.getMethod( "returnsAFile", 
                                                            IFile.class ),
                          new Object[] { file },
                          m ) ) );
        assertMethodRun();
    }
}
