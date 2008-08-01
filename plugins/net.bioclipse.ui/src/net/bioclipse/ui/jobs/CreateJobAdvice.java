package net.bioclipse.ui.jobs;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.scripting.ScriptingThread;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Creates jobs for manager methods
 * 
 * @author jonalv
 *
 */
public class CreateJobAdvice implements ICreateJobAdvice {

    private Object lock = new Object();
    
    public Object invoke( final MethodInvocation invocation ) 
                  throws Throwable {

        Method toBeInvocated = invocation.getMethod();
        
        for ( Method m : invocation.getMethod()
                                   .getDeclaringClass().getMethods() ) {
            Collection<Class<?>> paramTypes 
                = Arrays.asList( m.getParameterTypes() );
            if ( m.getName().equals( invocation.getMethod().getName() )
                 && paramTypes.contains( IFile.class )
                 && paramTypes.contains( IProgressMonitor.class ) ) {
                
                toBeInvocated = m;
            }
        }
        
        String jobName = ( (IBioclipseManager)invocation.getThis() )
                         .getNamespace() + "." 
                         + invocation.getMethod().getName(); 
        
        BioclipseJob job = new BioclipseJob( jobName, 
                                             toBeInvocated, 
                                             invocation,
                                             lock );  
            
        if ( Thread.currentThread() instanceof ScriptingThread ) {
            //from console
        }
        else {
            //from gui
            job.setUser( true );
        }

        job.schedule();
        if ( !invocation.getMethod()
                        .getReturnType().equals( Void.TYPE ) ) {
            synchronized ( lock ) {
                while ( job.getReturnValue() == null )
                    lock.wait();
                return job.getReturnValue();
            }
        }
        return null;
    }
}
