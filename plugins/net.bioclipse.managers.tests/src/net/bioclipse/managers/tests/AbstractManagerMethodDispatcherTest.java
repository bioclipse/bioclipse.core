package net.bioclipse.managers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class primarily checks that a method is being called and no exceptions 
 * thrown. In some but not in all cases it also checks that a Job is 
 * actually created.
 * 
 * @author jonalv
 *
 */
public abstract class AbstractManagerMethodDispatcherTest {

    private MethodInterceptor dispatcher;
    
    private static final String FILENAME = "test.file";
    private static final String PATH = "/Virtual/";
    
    private static IFile file 
        = net.bioclipse.core.Activator.getVirtualProject()
             .getFile( new Path(FILENAME) );
    
    public AbstractManagerMethodDispatcherTest( MethodInterceptor dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    @BeforeClass
    public static void setUpTestFile() throws CoreException {
        if ( !file.exists() ) {
            file.create( new ByteArrayInputStream("".getBytes()), 
                         true, 
                         new NullProgressMonitor() );
        }
    }

    @Test
    public void iFileAndBioclipseJobUpdateHook() throws Throwable {

        BioclipseJob<?> job = (BioclipseJob<?>) dispatcher.invoke( 
            new MyInvocation(
                ITestManager.class.getMethod( "getBioObjects", 
                                              IFile.class, 
                                              BioclipseJobUpdateHook.class),
            new Object[] { file, 
                           new BioclipseJobUpdateHook("") {
                               @Override
                               public void processResult( IBioObject chunk ) {
                    
                               } 
                           } }, 
                           null ) );
        job.join();
        assertTrue( job.getReturnValue() instanceof BioList ) ;
    }
    
    @Test
    public void getListIFile() throws Throwable {
        
        dispatcher.invoke( 
            new MyInvocation(
                ITestManager.class.getMethod( "getBioObjects", 
                                              IFile.class ),
                new Object[] { file },
                null ) );
    }

    @Test
    public void getListString() throws Throwable {
        
        dispatcher.invoke( 
            new MyInvocation(
                ITestManager.class.getMethod( "getBioObjects", 
                                              String.class ),
                new Object[] { PATH + FILENAME },
                null ) );
    }
    
    @Test
    public void plainOldMethodCall() throws Throwable {
        assertEquals( 
            "OH HAI Ceiling cat", 
            dispatcher.invoke( 
                new MyInvocation(
                    ITestManager.class.getMethod( "getGreeting", 
                                                  String.class ),
                new Object[] { "Ceiling cat" },
                null ) 
            ) 
        );
    }
    
    @Test
    public void runAsJobString() throws Throwable {
        
        assertTrue( file.exists() );
        
        dispatcher.invoke( 
            new MyInvocation(
                ITestManager.class.getMethod( "runAsJob", 
                                              String.class ),
                new Object[] { PATH + FILENAME },
                null ) );
    }
    
    @Test
    public void runAsJobIFile() throws Throwable {

        assertTrue( file.exists() );
        
        dispatcher.invoke( 
            new MyInvocation(
                ITestManager.class.getMethod( "runAsJob", 
                                              IFile.class ),
                new Object[] { file },
                null ) );
    }
    
    protected static class MyInvocation implements MethodInvocation {

        private Method method;
        private Object[] arguments;
        private Object manager;

        public MyInvocation( Method method, 
                             Object[] arguments,
                             Object manager ) {
            this.method = method;
            this.arguments = arguments;
            this.manager = manager;
        }
        
        public Method getMethod() {
            return method;
        }

        public Object[] getArguments() {
            return arguments;
        }

        public AccessibleObject getStaticPart() {
            throw new RuntimeException( "ooops, the test code is broken" );
        }

        public Object getThis() {
            return manager;
        }

        public Object proceed() throws Throwable {
            throw new RuntimeException( "ooops, the test code is broken" );
        }
    }
}
