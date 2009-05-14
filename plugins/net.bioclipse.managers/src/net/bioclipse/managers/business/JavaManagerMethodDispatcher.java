package net.bioclipse.managers.business;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.IResourcePathTransformer;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author jonalv
 *
 */
public class JavaManagerMethodDispatcher 
       extends AbstractManagerMethodDispatcher {

    IResourcePathTransformer transformer 
        = ResourcePathTransformer.getInstance();
    
    @Override
    public Object doInvoke( IBioclipseManager manager, 
                            Method method,
                            Object[] arguments ) {

        if ( Arrays.asList( method.getParameterTypes() )
                   .contains( IProgressMonitor.class ) &&
             method.getReturnType() == void.class ) {
            return runAsJob(manager, method, arguments);
        }
        return runInSameThread(manager, method, arguments);
    }

    private Object runInSameThread( IBioclipseManager manager, Method method,
                                    Object[] arguments ) {

        //translate String -> IFile
        for ( int i = 0; i < arguments.length; i++ ) {
            if ( arguments[i] instanceof String &&
                 method.getParameterTypes()[i] == IFile.class ) {
                arguments[i] = transformer.transform( (String)arguments[i] );
            }
        }
        
        //Add a NullProgressMonitor if needed
        if ( Arrays.asList( method.getParameterTypes() )
                   .contains( IProgressMonitor.class ) &&
             !Arrays.asList( arguments )
                    .contains( IProgressMonitor.class ) ) {
            
            List<Object> args 
                = new ArrayList<Object>( Arrays.asList( arguments ) );
            args.add( new NullProgressMonitor() );
            arguments = args.toArray();
        }
        
        try {
            return method.invoke( manager, arguments );
        } catch ( Exception e ) {
            throw new RuntimeException (
                "Failed to run method " + manager.getNamespace() 
                + "." + method.getName(), 
                e);
        }
    }

    private Object runAsJob( IBioclipseManager manager, Method method,
                             Object[] arguments ) {

        List<Object> newArguments = new ArrayList<Object>();
        newArguments.addAll( Arrays.asList( arguments ) );
        
        boolean doingPartialReturns = false;
        
        //find update hook
        BioclipseJobUpdateHook hook = null;
        for ( Object argument : arguments ) {
            if ( argument instanceof BioclipseJobUpdateHook ) {
                doingPartialReturns = true;
                hook = (BioclipseJobUpdateHook) argument;
            }
        }

        BioclipseJob<?> job 
            = new BioclipseJob( hook != null ? hook.getJobName()
                                             : manager.getNamespace() + "." 
                                               + method.getName() );
        
        job.setMethod( method );
        job.setInvocation( invocation );
        job.setBioclipseManager( manager );
        
        job.schedule();
        return job;
    }

    @Override
    protected Object doInvokeInGuiThread( final IBioclipseManager manager, 
                                          final Method method,
                                          final Object[] arguments ) {

        //translate String -> IFile
        for ( int i = 0; i < arguments.length; i++ ) {
            if ( arguments[i] instanceof String &&
                 method.getParameterTypes()[i] == IFile.class ) {
                arguments[i] = transformer.transform( (String)arguments[i] );
            }
        }
        Display.getDefault().asyncExec( new Runnable() {

            public void run() {
                try {
                    method.invoke( manager, arguments );
                } catch ( Exception e ) {
                    ErrorDialog.openError( 
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                                 .getShell(), 
                        "Exception while running manaer method", 
                        "An exception occured while trying to run: " + 
                        manager.getNamespace() + "." + method.getName(), 
                        new Status( IStatus.ERROR, 
                                    "net.bioclipse.managers", 
                                    e.getClass().getSimpleName() 
                                    + ": " + e.getMessage(), 
                                    e) );
                }
            }
        });
        return null;
    }
}
