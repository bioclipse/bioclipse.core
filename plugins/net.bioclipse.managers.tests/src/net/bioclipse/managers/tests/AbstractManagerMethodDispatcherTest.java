package net.bioclipse.managers.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.managers.business.AbstractManagerMethodDispatcher;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.Before;
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
    
    private static TestManager m 
        = (TestManager) AbstractManagerMethodDispatcher
                        .getManager( TestManager.class );
    
    
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
    
    @Before
    public void resetTestManager() {
        synchronized ( TestManager.methodRun ) {
            TestManager.methodRun = false;
        }
    }

    @Test
    public void iFileAndBioclipseJobUpdateHook() throws Throwable {

        final int chunks[] = {0};
        BioclipseJob<?> job = (BioclipseJob<?>) dispatcher.invoke( 
            new MyInvocation(
                ITestManager.class.getMethod( "getBioObjects", 
                                              IFile.class, 
                                              BioclipseJobUpdateHook.class),
            new Object[] { file, 
                           new BioclipseJobUpdateHook("") {
                               @Override
                               public void partialReturn( BioObject chunk ) {
                                   assertNotNull( chunk );
                                   chunks[0]++;
                               }
                           } }, 
            null ) );
        job.join();
        assertMethodRun();
    }
    
    @Test
    public void getListIFile() throws Throwable {
        
        dispatcher.invoke( 
            new MyInvocation(
                ITestManager.class.getMethod( "getBioObjects", 
                                              IFile.class ),
                new Object[] { file },
                null ) );
        assertMethodRun();
    }

    private void assertMethodRun() throws InterruptedException {
        
        long time = System.currentTimeMillis();
        
        while ( !TestManager.methodRun )  {
            synchronized ( TestManager.lock ) {
                TestManager.lock.wait(1000);
            }
            if ( System.currentTimeMillis() - time > 1000 * 5 ) {
                fail("Timed out");
            }
        }
        
        assertTrue( TestManager.methodRun );
    }

    @Test
    public void getListString() throws Throwable {
        
        dispatcher.invoke( 
            new MyInvocation(
                ITestManager.class.getMethod( "getBioObjects", 
                                              String.class ),
                new Object[] { PATH + FILENAME },
                null ) );
        assertMethodRun();
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
        assertMethodRun();
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
        assertMethodRun();
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
        assertMethodRun();
    }
    
    @Test
    public void dontRunAsJobIFile() throws Throwable {
        assertTrue( file.exists() );
        
        dispatcher.invoke( 
            new MyInvocation(
                ITestManager.class.getMethod( "dontRunAsJob", 
                                              IFile.class ),
                new Object[] { file },
                null ) );
        assertMethodRun();
    }
    
    @Test
    public void dontRunAsJobString() throws Throwable {
        assertTrue( file.exists() );
        
        dispatcher.invoke( 
            new MyInvocation(
                ITestManager.class.getMethod( "dontRunAsJob", 
                                              String.class ),
                new Object[] { PATH + FILENAME },
                null ) );
        assertMethodRun();
    }
    
    @Test
    public void getPathIFile() throws Throwable {
        assertTrue( file.exists() );
        
        assertEquals( PATH + FILENAME , 
                      dispatcher.invoke( 
                          new MyInvocation(
                              ITestManager.class.getMethod( "getPath", 
                                                            IFile.class ),
                              new Object[] { file },
                              null ) ) );
        assertMethodRun();
    }
    
    @Test
    public void getPathString() throws Throwable {
        assertTrue( file.exists() );
        
        assertEquals( PATH + FILENAME, 
                      dispatcher.invoke( 
                          new MyInvocation(
                              ITestManager.class.getMethod( "getPath", 
                                                            String.class ),
                          new Object[] { PATH + FILENAME },
                          null ) ) );
        assertMethodRun();
    }
    
    @Test
    public void guiAction() throws Throwable {
        final Throwable[] exception = new Throwable[1];
        Job job = new Job("test") {

            @Override
            protected IStatus run( IProgressMonitor monitor ) {

                try {
                    dispatcher.invoke( 
                                      new MyInvocation(
                                          ITestManager.class.getMethod( "guiAction" ),
                                      new Object[] { },
                                      null ) );
                } 
                catch ( Throwable e ) {
                    exception[0] = e;
                    e.printStackTrace();
                }
                
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        job.join();
        if ( exception[0] != null ) {
            throw exception[0];
        }
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
