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
        
        invocationParamTypes.remove( BioclipseUIJob.class );
        
        for ( Method m : invocation.getMethod()
                                   .getDeclaringClass().getMethods() ) {

            List<Class<?>> currentParamTypes 
                = Arrays.asList( m.getParameterTypes() );
            // Return a method that has the same name and the same paramaters 
            // (except for perhaps a BioclipseUIJob on the "invocation method") 
            // as the "invocation method" plus a progress monitor
            if ( !m.getName().equals( invocation.getMethod().getName() ) ) 
                continue;
            
            if ( !currentParamTypes.contains( IProgressMonitor.class ) ) 
                continue;

            if ( !currentParamTypes.subList( 0, currentParamTypes.size() - 2 )
                    .equals( invocationParamTypes ) )  
                continue;
                   
            return m;
        }
        return null;
    }
}
