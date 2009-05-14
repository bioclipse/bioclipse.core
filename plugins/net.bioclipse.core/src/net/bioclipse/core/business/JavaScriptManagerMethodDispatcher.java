package net.bioclipse.core.business;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


/**
 * @author jonalv
 *
 */
public class JavaScriptManagerMethodDispatcher implements MethodInterceptor {

    public Object invoke( MethodInvocation invocation ) throws Throwable {

        for ( Class<?> interfaze : invocation.getClass().getInterfaces() ) {
            if ( Arrays.asList( interfaze.getMethods() )
                       .contains( invocation.getMethod() ) ) {
                interfaze.getAnnotation( ManagerImplementation.class );
            }
        }
        
        throw new BioclipseException( "Something went wrong when trying to " +
                                      "find the implementation for the method" +
                                      invocation.getMethod().getName() + "." );
    }
}
