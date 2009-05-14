package net.bioclipse.managers.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.IResourcePathTransformer;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jsexecution.tools.MonitorContainer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author jonalv
 *
 */
public class JavaScriptManagerMethodDispatcher 
       extends AbstractManagerMethodDispatcher {

    protected IResourcePathTransformer transformer 
        = ResourcePathTransformer.getInstance();
    
    @Override
    public Object doInvoke( IBioclipseManager manager, Method method,
                            Object[] arguments ) {

        List<Object> newArguments = new ArrayList<Object>();
        newArguments.addAll( Arrays.asList( arguments ) );
        
        if ( Arrays.asList( method.getParameterTypes() )
                .contains( BioclipseJob.class ) 
          ) {
            newArguments.add( null );
        }
        
        if ( Arrays.asList( method.getParameterTypes() )
                   .contains( IProgressMonitor.class ) 
             ) {
            IProgressMonitor m = MonitorContainer.getInstance().getMonitor();
            if ( m == null ) { 
                m = new NullProgressMonitor(); 
            }
            newArguments.add( m );
        }
        
        arguments = newArguments.toArray();

        //translate String -> IFile
        for ( int i = 0; i < arguments.length; i++ ) {
            if ( arguments[i] instanceof String &&
                 method.getParameterTypes()[i] == IFile.class ) {
                arguments[i] 
                    = transformer.transform( (String)arguments[i] );
            }
        } 
        
        try {
            return method.invoke( manager, arguments );
        } catch ( IllegalArgumentException e ) {
            throw new RuntimeException("Failed to run method", e);
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException("Failed to run method", e);
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException("Failed to run method", e);
        }
    }

}
