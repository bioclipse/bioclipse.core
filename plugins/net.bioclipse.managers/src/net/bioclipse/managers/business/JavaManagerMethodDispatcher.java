package net.bioclipse.managers.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author jonalv
 *
 */
public class JavaManagerMethodDispatcher 
       extends AbstractManagerMethodDispatcher {

    @Override
    public Object doInvoke( IBioclipseManager manager, Method method,
                            Object[] arguments ) {

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
