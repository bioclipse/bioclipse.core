package net.bioclipse.ui.jobs;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.jobs.Job;
import net.bioclipse.scripting.ScriptingThread;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.WorkbenchJob;

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

        if ( Thread.currentThread() instanceof ScriptingThread ) {
            return handleScriptingMode( invocation ); //TODO: remove
        }
        else {
            return handleUIMode( invocation );
        }
    }

    private Object handleScriptingMode( final MethodInvocation invocation ) 
                   throws Throwable {

        Method methodToInvoke = invocation.getMethod();
        Method m = findMethodWithMonitor(invocation);
        if ( m != null) {
            methodToInvoke = m;
        }

        Object[] args;
        if ( methodToInvoke != invocation.getMethod() ) {
            /*
             * Setup args array
             */
            args = new Object[
                 invocation.getArguments().length + 1];
            args[args.length-1] = monitor;
            System.arraycopy( invocation.getArguments(), 
                              0, 
                              args, 
                              0, 
                              invocation.getArguments().length );
            /*
             * Then substitute from String to IFile where suitable
             */
            for ( int i = 0; i < args.length; i++ ) {
                Object arg = args[i];
                if ( arg instanceof String &&
                     methodToInvoke
                         .getParameterTypes()[i] == IFile.class ) {
                     
                    args[i] = ResourcePathTransformer
                              .getInstance()
                              .transform( (String) arg );
                }
            }
        }
        else {
            args = invocation.getArguments();
        }
    
        return methodToInvoke.invoke( invocation.getThis(), args );
    }

    private Object handleUIMode( final MethodInvocation invocation ) 
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
        
        int i = Arrays.asList( invocation.getMethod().getParameterTypes() )
                                         .indexOf( BioclipseUIJob.class );
        
        final BioclipseUIJob uiJob ;
        
        if ( i != -1 )
            uiJob = ( (BioclipseUIJob)invocation.getArguments()[i] );
        else {
            uiJob = null;
        }
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

    private String createJobName( MethodInvocation invocation ) {
        return ( (IBioclipseManager)invocation.getThis() ).getNamespace() + "." 
               + invocation.getMethod().getName();
    }

    private Method findMethodWithMonitor( MethodInvocation invocation ) {

        for ( Method m : invocation.getMethod()
                .getDeclaringClass().getMethods() ) {

        Collection<Class<?>> paramTypes 
            = Arrays.asList( m.getParameterTypes() );
        
        if ( m.getName().equals( invocation.getMethod().getName() )
             && paramTypes.contains( IProgressMonitor.class ) ) {
        
                return m;
            }
        }
        return null;
    }
}
