package net.bioclipse.managers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.junit.Test;

import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.managers.business.JavaManagerMethodDispatcher;
import org.eclipse.core.runtime.IProgressMonitor;
import net.bioclipse.jobs.IReturner;
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

    public interface IManager extends IBioclipseManager {
        public void getList( 
            Thread t,
            BioclipseUIJob<List<IBioObject>> uiJob );
    }
    
    private static volatile boolean sameThread = false;
    private static volatile boolean done = false;
    private final Object lock = new Object();
    
    public class B extends BioObject {
        
    }
    
    public class Manager implements IBioclipseManager {

        public String getManagerName() {
            return "manager";
        }
        
        public void getList( Thread t,
                             IReturner returner,
                             IProgressMonitor monitor ) {
            
            sameThread = Thread.currentThread() == t;
                 
            returner.partialReturn( new B() );
            returner.partialReturn( new B() );
            done = true;
            synchronized ( lock ) {
                lock.notifyAll();
            }
        }
        
    }
    
    @Test
    public void BioclipseUIJobMethodsRunsAsJobs() throws Throwable {

        final Object lock = new Object();
        
        BioclipseUIJob<List<IBioObject>> uiJob 
            = new BioclipseUIJob<List<IBioObject>>() {
                @Override
                public void runInUI() {
                    getReturnValue();
                }
        };
        
        dispatcher.invoke( 
            new MyInvocation( 
                IManager.class.getMethod( "getList", 
                                          Thread.class,
                                          BioclipseUIJob.class ),
            new Object[] { Thread.currentThread(), uiJob },
            new Manager() ) );
        
        int waited = 0;
        while (!done) {
            synchronized ( lock ) {
                lock.wait( 1000 );
                waited += 1000;
            }
            if ( waited > 5000 ) {
                throw new RuntimeException("Timed out");
            }
        }
        assertTrue( "Should be run in another thread", !sameThread );
    }
}
