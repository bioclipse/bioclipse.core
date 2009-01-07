package net.bioclipse.ui.jobs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import net.bioclipse.core.ResourcePathTransformer;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * @author jonalv
 *
 */
public class BioclipseJob extends Job {

    private Method method;
    private MethodInvocation invocation;
    private Object lock;

    public BioclipseJob( String name, 
                         Method methodToBeInvocated, 
                         MethodInvocation originalInvocation,
                         Object lock ) {
        super( name );
        this.method     = methodToBeInvocated;
        this.invocation = originalInvocation;
        this.lock       = lock;
    }

    private Object returnValue;
    
    protected IStatus run( IProgressMonitor monitor ) {

        Object[] args;
        try {
            if ( method != invocation.getMethod() ) {
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
                         method.getParameterTypes()[i] == IFile.class ) {
                         
                        args[i] = ResourcePathTransformer
                                  .getInstance()
                                  .transform( (String) arg );
                    }
                }
            }
            else {
                args = invocation.getArguments();
                monitor.beginTask( "", IProgressMonitor.UNKNOWN );
            }
        
            returnValue = method.invoke( 
                invocation.getThis(), args );
        } 
        catch ( Exception e ) {
            returnValue = e;
            if (e instanceof InvocationTargetException) {
                returnValue = e.getCause();
            }
            throw new RuntimeException( "Exception occured: "
                                        + e.getClass().getSimpleName() 
                                        + " - "
                                        + e.getMessage(),
                                        e );
        }
        finally {
            synchronized ( lock ) {
                lock.notifyAll();
            }
        }
        monitor.done();
        return Status.OK_STATUS;
    }

    public synchronized Object getReturnValue() {
        return returnValue;
    }
}
