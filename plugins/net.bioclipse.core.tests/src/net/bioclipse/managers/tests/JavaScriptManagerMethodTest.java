package net.bioclipse.managers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
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
    
    @Override
    @Ignore ( "Have not yet explored how to do Hook callbacks from JavaScript" )
    @Test
    public void iFileAndBioclipseJobUpdateHookComplete() throws Throwable {
    
        super.iFileAndBioclipseJobUpdateHookComplete();
    }
    
    @Override
    @Ignore ( "Have not yet explored how to do Hook callbacks from JavaScript" )
    @Test
    public void iFileAndBioclipseJobUpdateHookPartial() throws Throwable {
    
        super.iFileAndBioclipseJobUpdateHookPartial();
    }
    
    @Override
    @Ignore ( "Have not yet explored how to do Hook callbacks from JavaScript" )
    @Test
    public void completeReturnerInference() throws Throwable {
    
        super.completeReturnerInference();
    }

    @Override
    @Test
    @Ignore ( "Unsure how to handle this in JavaScript" )
    public void testExtendedGetBioObjects() throws Throwable {
    
        super.testExtendedGetBioObjects();
    }
    
    @Override
    @Test
    @Ignore ( "Unsure how to handle this in JavaScript" )
    public void testExtendedVoidJobMethodIFile() throws Throwable {
    
        super.testExtendedVoidJobMethodIFile();
    }
    
    @Override
    @Test
    @Ignore ( "Unsure how to handle this in JavaScript" )
    public void testExtendedVoidJobMethodString() throws Throwable {
    
        super.testExtendedVoidJobMethodString();
    }
}
