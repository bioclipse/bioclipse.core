package net.bioclipse.managers.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.bioclipse.core.IResourcePathTransformer;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.jobs.IReturner;
import net.bioclipse.managers.MonitorContainer;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @author jonalv
 *
 */
public abstract class AbstractManagerMethodDispatcher 
                implements MethodInterceptor {

    protected Object[] arguments;
    protected Method methodCalled;
    protected IResourcePathTransformer transformer 
        = ResourcePathTransformer.getInstance();
    private final Logger logger 
        = Logger.getLogger( AbstractManagerMethodDispatcher.class );
    
    protected static class ReturnCollector implements IReturner {

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
    
    public Object invoke( MethodInvocation invocation ) throws Throwable {

        this.arguments = invocation.getArguments().clone();
        this.methodCalled = invocation.getMethod();
                
        Method m = findMethodToRun( invocation, 
                                    (Class<? extends IBioclipseManager>) 
                                        invocation.getThis().getClass() );
        IBioclipseManager manager = (IBioclipseManager)invocation.getThis();
        if ( invocation.getMethod().getAnnotation( GuiAction.class ) != null ) {
            
            logger.debug( manager.getManagerName() + "." 
                          + invocation.getMethod().getName() 
                          + " has @GuiAction - running in gui thread" );
            return doInvokeInGuiThread( (IBioclipseManager)invocation.getThis(),
                                        m,
                                        invocation.getArguments() );
        }
        
        Object returnValue;
        if ( invocation.getMethod().getReturnType() != BioclipseJob.class &&
             invocation.getMethod().getReturnType() != void.class ) {
            if ( Arrays.asList( m.getParameterTypes() )
                       .contains( IProgressMonitor.class) &&
                 !(this instanceof JavaScriptManagerMethodDispatcher) )  {
                logger.warn( manager.getManagerName() + "." 
                              + invocation.getMethod().getName() 
                              + " is not void or returning a BioclipseJob."
                              + " But implementation takes a progress monitor. "
                              + " Can not run as Job. Running in same" 
                              + " thread." );
            }
            
            returnValue = doInvokeInSameThread( (IBioclipseManager)
                                            invocation.getThis(), 
                                            m, 
                                            invocation.getArguments() );
        }
        else {
            logger.debug( "Creating job for " + manager.getManagerName() + "." 
                          + invocation.getMethod().getName() );
            returnValue = doInvoke( (IBioclipseManager)invocation.getThis(), 
                                    m, 
                                    invocation.getArguments() );
        }

        if ( returnValue instanceof IFile && 
             invocation.getMethod().getReturnType() == String.class ) {
            returnValue = ( (IFile) returnValue ).getLocationURI()
                                                 .getPath();
        }
        return returnValue;
    }

    private BioclipseUIJob<Object> getBioclipseUIJob() {

       for ( Object o : arguments ) {
           if ( o instanceof BioclipseUIJob) {
               return (BioclipseUIJob<Object>) o;
           }
       }
       return null;
    }

    protected abstract Object doInvokeInGuiThread( IBioclipseManager manager, 
                                                   Method m,
                                                   Object[] arguments );

    protected abstract Object doInvokeInSameThread( IBioclipseManager manager, 
                                                    Method m,
                                                    Object[] arguments )
                              throws BioclipseException;
    
    public Object doInvoke( IBioclipseManager manager, Method method,
                            Object[] arguments ) throws BioclipseException {

        List<Object> newArguments = new ArrayList<Object>();
        newArguments.addAll( Arrays.asList( arguments ) );
        
        boolean doingPartialReturns = false;
        ReturnCollector returnCollector = new ReturnCollector();
        //add partial returner
        for ( Class<?> param : method.getParameterTypes() ) {
            if ( param == IReturner.class ) {
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
        
        Object returnValue = null;
        try {
            if ( doingPartialReturns ) {
                method.invoke( manager, arguments );
                returnValue = returnCollector.getReturnValue();
                if ( returnValue == null ) {
                    returnValue = returnCollector.getReturnValues();
                }
            }
            else {
                returnValue = method.invoke( manager, arguments );
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
        
        if ( uiJob != null ) {
            uiJob.setReturnValue( returnValue );
            final BioclipseUIJob finalUiJob = uiJob;
            new WorkbenchJob("Refresh") {
                
                @Override
                public IStatus runInUIThread( 
                        IProgressMonitor monitor ) {
                    finalUiJob.runInUI();
                    return Status.OK_STATUS;
                }
                
            }.schedule();
        }
        return returnValue;
    }
    
    private Method findMethodToRun( 
                       MethodInvocation invocation, 
                       Class<? extends IBioclipseManager> manager ) {
        
        Method result;
        
        //If a method with the same signature exists use that one
        try {
            result = manager.getMethod( invocation.getMethod().getName(), 
                                        invocation.getMethod()
                                                  .getParameterTypes() );
        } 
        catch ( SecurityException e ) {
            throw new RuntimeException("Failed to find the method to run", e);
        } 
        catch ( NoSuchMethodException e ) {
            result = null;
        }
        if ( result != null ) {
            return result;
        }

        //Look for "the JavaScript method" (taking String instead of IFile)
        METHODS:
        for ( Method m : manager.getMethods() ) {
            Method refMethod = invocation.getMethod();
            int refLength = refMethod.getParameterTypes().length;
            int mLength   = m.getParameterTypes().length;
            if ( m.getName().equals( refMethod.getName() ) &&
                  mLength >= refLength && 
                  mLength <= refLength + 2 ) {
                PARAMS:
                for ( int i = 0, j = 0; 
                      i < m.getParameterTypes().length; 
                      i++ ) {
                    Class<?> currentParam = m.getParameterTypes()[i];
                    if ( currentParam == IReturner.class ) {
                        continue PARAMS;
                    }
                    if ( invocation.getMethod()
                                   .getParameterTypes().length >= j + 1 &&
                         ( invocation.getMethod()
                                     .getParameterTypes()[j] 
                             == BioclipseUIJob.class  || 
                           invocation.getMethod()
                                     .getParameterTypes()[j] 
                             == BioclipseJobUpdateHook.class  ) ) {
                        j++;
                    }
                    if ( currentParam == IProgressMonitor.class &&
                         // can only skip if there is nothing 
                         // corresponding in the refMethods parameter types.
                         refMethod.getParameterTypes().length < j + 1 ) {
                        continue PARAMS;
                    }
                    if ( invocation.getMethod()
                                   .getParameterTypes().length <= j ) {
                        continue METHODS;
                    }
                    Class<?> refParam = invocation.getMethod()
                                                  .getParameterTypes()[j++];
                    if ( currentParam == refParam ) {
                        continue PARAMS;
                    }
                    if ( currentParam == IFile.class && 
                         refParam == String.class ) {
                        continue PARAMS;
                    }
                    continue METHODS;
                }
                return m;
            }
        }
        
        throw new RuntimeException("Failed to find the method to run");
    }
}
