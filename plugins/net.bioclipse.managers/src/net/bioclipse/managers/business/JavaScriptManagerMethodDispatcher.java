package net.bioclipse.managers.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jonalv
 *
 */
public class JavaScriptManagerMethodDispatcher 
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
