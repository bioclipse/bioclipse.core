package net.bioclipse.ui.jobs;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.bioclipse.core.business.IBioclipseManager;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
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

        // If the method returns something and doesn't contain any UIJob then
        // the conventions are not really followed. Best not create a Job.
        // TODO: This should probably change the day the managers no longer 
        //       implements the manager interfaces. Then the interface version 
        //       should be void but not the manager version...
        if ( invocation.getMethod().getReturnType() != Void.TYPE && 
             findUIJob( invocation ) == null ) {
            return null;
        }
        
        List<Class<?>> invocationParamTypes 
            = Arrays.asList( invocation.getMethod().getParameterTypes() );
        METHODS:
        for ( Method m : invocation.getMethod()
                                   .getDeclaringClass().getMethods() ) {

            List<Class<?>> currentParamTypes 
                = Arrays.asList( m.getParameterTypes() );
            // Return a method that has the same name and the same paramaters 
            // (except for perhaps a BioclipseUIJob on the "invocation method") 
            // as the "invocation method" plus a progress monitor
            // oh and deal with IFile / String transformation too...
            if ( !m.getName().equals( invocation.getMethod().getName() ) ) 
                continue METHODS;
            
            if ( !currentParamTypes.contains( IProgressMonitor.class ) ) 
                continue METHODS;

            if ( currentParamTypes.size() + 1 != invocationParamTypes.size() &&
                 !( invocationParamTypes.contains( BioclipseUIJob.class ) &&
                   currentParamTypes.size() == invocationParamTypes.size() ) ){
                continue METHODS;
            }
            
            PARAMS:
            for ( int i = 0; i < currentParamTypes.size(); i++ ) {
                 Object arg = currentParamTypes.get( i );
                 
                 if ( arg.equals( invocationParamTypes.get( i ) ) ) {
                     continue PARAMS;
                 }
                 else {
                     if ( (arg == IFile.class &&
                           invocationParamTypes.get( i ) == String.class) ) {
                         continue PARAMS;
                     }
                     if ( arg == IProgressMonitor.class &&
                          invocationParamTypes.get( i )
                              == BioclipseUIJob.class ) {
                         continue PARAMS;
                     }
                  }
                 continue METHODS;
            }
                   
            return m;
        }
        return null;
    }
}
