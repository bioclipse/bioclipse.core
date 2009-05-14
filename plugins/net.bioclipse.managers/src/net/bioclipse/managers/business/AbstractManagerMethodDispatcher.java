package net.bioclipse.managers.business;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


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
            throw new IllegalStateException( interfaze.getName() + " does not" +
                " have the annotation " + ManagerImplementation.class.getName() 
                + " so can't figure out what method to run." );
        }
        
        Method m = findMethodToRun(invocation, annotation);
        
        return doInvoke( getManager( annotation.value() ), 
                         m, 
                         invocation.getArguments() );
    }

    private Method findMethodToRun( MethodInvocation invocation, 
                                    ManagerImplementation annotation ) {
        
        Method m;
        try {
            m = annotation.value().getMethod( invocation.getMethod().getName(), 
                                              invocation.getMethod()
                                                        .getParameterTypes() );
        } catch ( SecurityException e ) {
            throw new RuntimeException("Failed to find the method to run", e);
        } catch ( NoSuchMethodException e ) {
            m = null;
        }
        if ( m != null ) {
            return m;
        }

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
