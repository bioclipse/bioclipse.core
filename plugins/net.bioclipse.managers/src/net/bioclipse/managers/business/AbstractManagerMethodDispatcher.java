package net.bioclipse.managers.business;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.jobs.IPartialReturner;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;


public abstract class AbstractManagerMethodDispatcher 
                implements MethodInterceptor {

    protected MethodInvocation invocation;
    
    public Object invoke( MethodInvocation invocation ) throws Throwable {

        this.invocation = invocation;
                
        Method m = findMethodToRun( invocation, 
                                    (Class<? extends IBioclipseManager>) 
                                        invocation.getThis().getClass() );
        
        if ( invocation.getMethod().getAnnotation( GuiAction.class ) != null ) {
            return doInvokeInGuiThread( (IBioclipseManager)invocation.getThis(),
                                        m,
                                        invocation.getArguments() );
        }
        
        Object returnValue =  doInvoke( (IBioclipseManager)invocation.getThis(), 
                                        m, 
                                        invocation.getArguments() );

        if ( returnValue instanceof IFile && 
             invocation.getMethod().getReturnType() == String.class ) {
            returnValue = ( (IFile) returnValue ).getLocationURI()
                                                 .getPath();
        }
        return returnValue;
    }

    private BioclipseUIJob<Object> getBioclipseUIJob() {

       for ( Object o : invocation.getArguments() ) {
           if ( o instanceof BioclipseUIJob) {
               return (BioclipseUIJob<Object>) o;
           }
       }
       return null;
    }

    protected abstract Object doInvokeInGuiThread( IBioclipseManager manager, 
                                                   Method m,
                                                   Object[] arguments );

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
                    if ( currentParam == IPartialReturner.class ) {
                        continue PARAMS;
                    }
                    if ( invocation.getMethod()
                                   .getParameterTypes().length >= j + 1 &&
                         invocation.getMethod()
                                   .getParameterTypes()[j] 
                             == BioclipseUIJob.class ) {
                        j++;
                    }
                    if ( currentParam == IProgressMonitor.class &&
                         // can only skip if there is nothing 
                         // corresponding in the refMethods parameter types.
                         refMethod.getParameterTypes().length < j + 1 ) {
                        continue PARAMS;
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

    public abstract Object doInvoke( IBioclipseManager manager,
                                     Method method, 
                                     Object[] arguments );
}
