package net.bioclipse.scripting.business;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.bioclipse.core.Activator;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.domain.IBioObject;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.springframework.aop.framework.ProxyFactory;


/**
 * Special create job advice used by managers wanting special treatment of 
 * methods with progress monitors when used from JavaScript
 * 
 * @author jonalv
 *
 */
public class JSJobCreatorAdvice implements IJSJobCreatorAdvice {

    public Object invoke( MethodInvocation invocation ) throws Throwable {

        Method methodToInvoke = invocation.getMethod();
        Method m = findMethodWithMonitor(invocation);
        if ( m != null) {
            methodToInvoke = m;
        }

        Object[] args;
        if ( methodToInvoke != invocation.getMethod() ) {
            /*
             * Setup args array
             */
            args = new Object[
                 invocation.getArguments().length + 1];
            
            IProgressMonitor monitor 
                = new SubProgressMonitor( net.bioclipse.scripting.Activator
                                             .getDefault().JS_THREAD
                                             .getMonitor(), 
                                          1000000 ); 
            if ( monitor.isCanceled() ) {
                throw new OperationCanceledException();
            }
            args[args.length-1] = monitor;
            System.arraycopy( invocation.getArguments(), 
                              0, 
                              args, 
                              0, 
                              invocation.getArguments().length );
            /*
             * Then substitute from String to IFile where suitable
             */
            for ( int i = 0; i < args.length; i++ ) {
                Object arg = args[i];
                if ( arg instanceof String &&
                     methodToInvoke
                         .getParameterTypes()[i] == IFile.class ) {
                     
                    args[i] = ResourcePathTransformer
                              .getInstance()
                              .transform( (String) arg );
                }
            }
        }
        else {
            args = invocation.getArguments();
        }
    
        return methodToInvoke.invoke( invocation.getThis(), args ); 
    }

    private Method findMethodWithMonitor( MethodInvocation invocation ) {

        List<Class<?>> invocationParamTypes 
            = Arrays.asList( invocation.getMethod().getParameterTypes() );
        METHODS:
        for ( Method m : invocation.getMethod()
                                   .getDeclaringClass().getMethods() ) {

            List<Class<?>> currentParamTypes 
                = Arrays.asList( m.getParameterTypes() );
            // Return a method that has the same name and the same paramaters 
            // (except for perhaps a BioclipseUIJob on the "invocation method") 
            // as the "invocation method" plus a progress monitor
            // oh and deal with IFile / String transformation too...
            if ( !m.getName().equals( invocation.getMethod().getName() ) ) 
                continue METHODS;
            
            if ( !currentParamTypes.contains( IProgressMonitor.class ) ) 
                continue METHODS;

            if ( currentParamTypes.size() != invocationParamTypes.size()+1 ) {
            	  continue METHODS;
            }
            PARAMS:
            for ( int i = 0; i < invocationParamTypes.size(); i++ ) {
                 Object arg = currentParamTypes.get( i );
                 
                 if ( arg.equals( invocationParamTypes.get( i ) ) ) {
                     continue PARAMS;
                 }
                 else {
                     if ( (arg == IFile.class &&
                           invocationParamTypes.get( i ) == String.class) ) {
                         continue PARAMS;
                     }
                 }
                 continue METHODS;
            }
                   
            return m;
        }
        return null;
    }
}
