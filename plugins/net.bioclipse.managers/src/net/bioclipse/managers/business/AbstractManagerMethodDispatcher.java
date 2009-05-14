package net.bioclipse.managers.business;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.jobs.BioclipseUIJob;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;


public abstract class AbstractManagerMethodDispatcher 
                implements MethodInterceptor {

    private static final Map<Class<? extends IBioclipseManager>, 
                             IBioclipseManager> managers 
        = new WeakHashMap<Class<? extends IBioclipseManager>, 
                          IBioclipseManager>();
    
    public Object invoke( MethodInvocation invocation ) throws Throwable {

        Class<?> interfaze = invocation.getMethod().getDeclaringClass();
        
        ManagerImplementation annotation 
            = interfaze.getAnnotation( ManagerImplementation.class );
        
        if ( annotation == null ) {
            throw new IllegalStateException( interfaze.getName() + " does not "
                + "have the annotation " + ManagerImplementation.class.getName() 
                + " so can't figure out what method to run." );
        }
        
        Method m = findMethodToRun(invocation, annotation);
        
        return doInvoke( getManager( annotation.value() ), 
                         m, 
                         invocation.getArguments() );
    }

    private Method findMethodToRun( MethodInvocation invocation, 
                                    ManagerImplementation annotation ) {
        
        Method result;
        
        //If a method with the same signature exists use that one
        try {
            result = annotation.value()
                               .getMethod( invocation.getMethod().getName(), 
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
        for ( Method m : annotation.value().getMethods() ) {
            Method refMethod = invocation.getMethod();
            int refLength = refMethod.getParameterTypes().length;
            int mLength   = m.getParameterTypes().length;
            if ( m.getName().equals( refMethod.getName() ) &&
                  mLength >= refLength && 
                  mLength <= refLength + 2 ) {
                PARAMS:
                for ( int i = 0; i < m.getParameterTypes().length; i++ ) {
                    Class<?> currentParam = m.getParameterTypes()[i];
                    if ( currentParam == BioclipseJob.class ||
                         currentParam == IProgressMonitor.class ) {
                        continue PARAMS;
                    }
                    Class<?> refParam = invocation.getMethod()
                                                  .getParameterTypes()[i];
                    if ( currentParam == refParam ) {
                        continue PARAMS;
                    }
                    if ( !(currentParam == String.class && 
                           refParam == IFile.class) ) {
                        continue PARAMS;
                    }
                    continue METHODS;
                }
                return m;
            }
        }
        
        //Look for "the Java method" (taking an IFile)
        
        
        
        throw new RuntimeException("Failed to find the method to run");
    }

    private IBioclipseManager 
            getManager( Class<? extends IBioclipseManager> value ) {

        if ( !managers.containsKey(value) ) {
          try {
            managers.put(value, value.newInstance() );
          } 
          catch ( Exception e ) {
              throw new RuntimeException(
                  "Could not instatiate manager class:" + value.getName() , e);
          }
        }
        return managers.get(value);
    }

    public abstract Object doInvoke( IBioclipseManager manager,
                                     Method method, 
                                     Object[] arguments );
}
