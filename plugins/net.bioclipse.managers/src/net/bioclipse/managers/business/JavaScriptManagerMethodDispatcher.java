package net.bioclipse.managers.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.IResourcePathTransformer;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.IPartialReturner;
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

    IResourcePathTransformer transformer 
        = ResourcePathTransformer.getInstance();
    
    @Override
    public Object doInvoke( IBioclipseManager manager, Method method,
                            Object[] arguments ) {

        List<Object> newArguments = new ArrayList<Object>();
        newArguments.addAll( Arrays.asList( arguments ) );
        
        boolean doingPartialReturns = false;
        ReturnCollector returnCollector = new ReturnCollector();
        //add partial returner
        for ( Class<?> param : method.getParameterTypes() ) {
            if ( param == IPartialReturner.class ) {
                doingPartialReturns = true;
                newArguments.add( returnCollector );
            }
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
            if ( doingPartialReturns ) {
                method.invoke( manager, arguments );
                return returnCollector.getReturnValues();
            }
            else {
                return method.invoke( manager, arguments );
            }
        } catch ( IllegalArgumentException e ) {
            throw new RuntimeException("Failed to run method", e);
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException("Failed to run method", e);
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException("Failed to run method", e);
        }
    }

    private static class ReturnCollector implements IPartialReturner {

        private List<IBioObject> returnValues = new BioList<IBioObject>();
        
        public void partialReturn( BioObject bioObject ) {
            
            synchronized ( returnValues ) {
                returnValues.add( bioObject );
            }
        }
        
        public List<IBioObject> getReturnValues() {
            synchronized ( returnValues ) {
                return returnValues;
            }
        }
    }
}