package net.bioclipse.managers.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import net.bioclipse.core.IResourcePathTransformer;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;

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
                    .contains( IProgressMonitor.class ) ) {
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
        
        job.schedule();
        return job;
    }
}
