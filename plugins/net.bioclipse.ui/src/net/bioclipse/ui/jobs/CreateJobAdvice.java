package net.bioclipse.ui.jobs;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.IBioclipseManager;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Creates jobs for manager methods
 * 
 * @author jonalv
 *
 */
public class CreateJobAdvice implements ICreateJobAdvice {

    private Object lock = new Object();
    private Object returnValue;
    
    public Object invoke( final MethodInvocation invocation ) 
                  throws Throwable {

        returnValue = null;
        
        final Method[] toBeInvocated = { invocation.getMethod() };
        
        for ( Method m : invocation.getMethod()
                                   .getDeclaringClass().getMethods() ) {
            Collection<Class<?>> paramTypes 
                = Arrays.asList( m.getParameterTypes() );
            if ( m.getName().equals( invocation.getMethod().getName() )
                 && paramTypes.contains( IFile.class )
                 && paramTypes.contains( IProgressMonitor.class ) ) {
                
                toBeInvocated[0] = m;
            }
        }
        
        Job job = new Job( ( (IBioclipseManager)invocation.getThis() )
                           .getNamespace() + "." 
                           + invocation.getMethod().getName() ) {
            
            protected IStatus run( IProgressMonitor monitor ) {
                
                Object[] args;
                
                Method method = toBeInvocated[0];
                if ( method != invocation.getMethod() ) {
                    /*
                     * First the monitor
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
                     * Then substitute the correct string for an IFile
                     */
                    for ( int i = 0; i < args.length; i++ ) {
                        Object arg = args[i];
                        if ( arg instanceof String &&
                             method
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
                try {
                    returnValue = method.invoke( 
                        invocation.getThis(), args );
                } 
                catch ( final Exception e ) {
                    Display.getDefault().asyncExec( new Runnable() {
                        public void run() {
                            MessageDialog.openInformation( 
                                PlatformUI.getWorkbench()
                                          .getActiveWorkbenchWindow()
                                          .getShell(),
                                "Failed to perform task",
                                "An error has occured" );
                        }
                    });
                    throw new RuntimeException(e);
                }
                synchronized ( lock ) {
                    lock.notifyAll();
                }
                return Status.OK_STATUS;
            }
        };
        
        job.setUser( true );
        job.schedule();
        if ( !invocation.getMethod()
                        .getReturnType().equals( Void.TYPE ) ) {
            synchronized ( lock ) {
                while ( returnValue == null )
                    lock.wait();
                return returnValue;
            }
        }
        return null;
    }
}
