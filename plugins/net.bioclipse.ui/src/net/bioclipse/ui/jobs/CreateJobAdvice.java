package net.bioclipse.ui.jobs;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import net.bioclipse.core.business.IBioclipseManager;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Creates jobs for manager methods
 * 
 * @author jonalv
 *
 */
public class CreateJobAdvice implements ICreateJobAdvice {

    private IProgressMonitor nullProgressMonitor = new NullProgressMonitor();
    private IProgressMonitor monitor = nullProgressMonitor;
    
    public void setMonitor( IProgressMonitor monitor ) {
        this.monitor = monitor;
    }
    
    public void clearMonitor() {
        this.monitor = nullProgressMonitor;
    }
    
    public Object invoke( MethodInvocation invocation ) 
                  throws Throwable {

        Method methodToInvoke = invocation.getMethod();

        Method m = findMethodWithMonitor(invocation);
        if ( m != null) {
            methodToInvoke = m;
        }
        else {
            //If we don't find any method with progress monitor no job
            return invocation.proceed(); 
        }
        
        BioclipseJob job = new BioclipseJob( createJobName(invocation), 
                                             methodToInvoke, 
                                             invocation );
        
        final BioclipseUIJob uiJob = findUIJob(invocation);
        
        // If uiJob parameter is null business as usual...
        if ( uiJob != null ) {
            job.setUser( !uiJob.runInBackground() );
        }
        else {
            job.setUser( true );
        }

        job.schedule();

        return null;
    }

    private BioclipseUIJob findUIJob( MethodInvocation invocation ) {

        int i = Arrays.asList( invocation.getMethod().getParameterTypes() )
                      .indexOf( BioclipseUIJob.class );
        if ( i != -1 )
            return ( (BioclipseUIJob)invocation.getArguments()[i] );
        return null;
    }

    private String createJobName( MethodInvocation invocation ) {
        return ( (IBioclipseManager)invocation.getThis() ).getNamespace() + "." 
               + invocation.getMethod().getName();
    }

    private Method findMethodWithMonitor( MethodInvocation invocation ) {

        Method toReturn = null;
        
        for ( Method m : invocation.getMethod()
                .getDeclaringClass().getMethods() ) {

        Collection<Class<?>> paramTypes 
            = Arrays.asList( m.getParameterTypes() );
        
        if ( m.getName().equals( invocation.getMethod().getName() )
             && paramTypes.contains( IProgressMonitor.class ) ) {
        
                toReturn = m;
                break;
            }
        }
        // If the method returns something and doesn't contain any UIJob then
        // the conventions are not really followed. Best not create a Job.
        // TODO: This should probably change the day the managers no longer 
        //       implements the manager interfaces. Then the interface version 
        //       should be void but not the manager version...
        
        if ( invocation.getMethod().getReturnType() != Void.TYPE && 
             findUIJob( invocation ) == null ) {
            toReturn = null;
        }
        return toReturn;
    }
}
