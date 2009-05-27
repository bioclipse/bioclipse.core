package net.bioclipse.managers.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.IResourcePathTransformer;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.core.util.IJavaScriptConsolePrinterChannel;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.jobs.IPartialReturner;
import net.bioclipse.managers.MonitorContainer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;

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
                            Object[] arguments ) throws BioclipseException {

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
        
        //remove any BioclipseUIJob
        BioclipseUIJob uiJob = null;
        for ( Object o : newArguments ) {
            if ( o instanceof BioclipseUIJob) {
                uiJob = (BioclipseUIJob) o;
            }
        }
        if ( uiJob != null ) {
            newArguments.remove( uiJob );
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
                Object returnValue = method.invoke( manager, arguments );
                return returnValue;
            }
        } catch ( IllegalArgumentException e ) {
            throw new RuntimeException("Failed to run method", e);
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException("Failed to run method", e);
        } catch ( InvocationTargetException e ) {
            Throwable t = e.getCause();
            while ( t != null ) {
                if ( t instanceof BioclipseException ) {
                    throw (BioclipseException)t;
                }
                t = t.getCause();
            }
            throw new RuntimeException("Failed to run method", e);
        }
    }

    private static class ReturnCollector implements IPartialReturner {

        private volatile Object returnValue = null;
        private List<Object> returnValues = new ArrayList<Object>();
        
        public void partialReturn( Object object ) {
            if ( returnValue != null ) {
                throw new IllegalStateException(
                    "Method completeReturn already called. " +
                    "Can't do more returns after completeReturn" );
            }
            synchronized ( returnValues ) {
                returnValues.add( object );
            }
        }
        
        public List<Object> getReturnValues() {
            synchronized ( returnValues ) {
                return returnValues;
            }
        }

        public void completeReturn( Object object ) {
            if ( !returnValues.isEmpty() ) {
                throw new IllegalStateException( 
                    "Partial returns detected. " +
                    "Can't do a complete return after partial " +
                    "returning commenced" );
            }
            returnValue = object;
        }
        
        public Object getReturnValue() {
            return returnValue;
        }
    }

    @Override
    protected Object doInvokeInGuiThread( final IBioclipseManager manager, 
                                          final Method method,
                                          final Object[] arguments ) {

        Display.getDefault().asyncExec( new Runnable() {
            public void run() {
                try {
                    doInvoke( manager, method, arguments );
                }
                catch (Throwable t) {
                    printError(t);
                }
            }
        } );
        return null;
    }
    
    private void printError( Throwable t ) {

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint serviceObjectExtensionPoint 
            = registry.getExtensionPoint(
                  "net.bioclipse.scripting.contribution" );

        IExtension[] serviceObjectExtensions
            = serviceObjectExtensionPoint.getExtensions();
        for ( IExtension extension : serviceObjectExtensions) {
            for ( IConfigurationElement element 
                    : extension.getConfigurationElements() ) {
                Object service = null;
                try {
                    service = element.createExecutableExtension("service");
                }
                catch (CoreException e) {
                    throw new RuntimeException(e);
                }
                ( (IJavaScriptConsolePrinterChannel) service ).printError(t);
            }
        }
    }
}