package net.bioclipse.jobs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.jobs.BioclipseUIJob;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.WorkbenchJob;


/**
 * @author jonalv
 *
 */
public class BioclipseJob<T> extends Job implements IPartialReturner {

    private Method method;
    private MethodInvocation invocation;

    public BioclipseJob( String name, 
                         Method methodToBeInvocated, 
                         MethodInvocation originalInvocation ) {
        super( name );
        this.method     = methodToBeInvocated;
        this.invocation = originalInvocation;
    }

    public static final Object NULLVALUE = new Object();
    
    private Object returnValue = NULLVALUE;
    
    protected IStatus run( IProgressMonitor monitor ) {

        Object[] args;
        try {
            if ( method != invocation.getMethod() ) {
                /*
                 * Setup args array
                 */
                boolean hasUIJob = false;
                for ( Object o : invocation.getArguments() ) {
                    if ( o instanceof BioclipseUIJob ) {
                        hasUIJob = true;
                        break;
                    }
                }
                if (hasUIJob) {
                    args = new Object[invocation.getArguments().length];
                }
                else { 
                    args = new Object[invocation.getArguments().length + 1];
                }
                args[args.length-1] = monitor;
                System.arraycopy( invocation.getArguments(), 
                                  0, 
                                  args, 
                                  0, 
                                  args.length - 1 );
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
        
            returnValue = method.invoke( invocation.getThis(), args );
            
            int i = Arrays.asList( invocation.getMethod().getParameterTypes() )
                          .indexOf( BioclipseUIJob.class );

            final BioclipseUIJob uiJob ;
            
            if ( i != -1 )
                uiJob = ( (BioclipseUIJob)invocation.getArguments()[i] );
            else {
                uiJob = null;
            }
            
            if ( uiJob != null ) {
                uiJob.setReturnValue( returnValue );
                new WorkbenchJob("Refresh") {
        
                    @Override
                    public IStatus runInUIThread( 
                            IProgressMonitor monitor ) {
                        uiJob.runInUI();
                        return Status.OK_STATUS;
                    }
                    
                }.schedule();
            }
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
        
        monitor.done();
        return Status.OK_STATUS;
    }

    @SuppressWarnings("unchecked")
    public synchronized T getReturnValue() {
        if ( returnValue == NULLVALUE ) {
            throw new IllegalStateException( "There is no return value" );
        }
        return (T)returnValue;
    }

    public void partialReturn( BioObject bioObject ) {

        // TODO Auto-generated method stub
        
    }
}
