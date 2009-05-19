/*******************************************************************************
 * Copyright (c) 2009 Jonathan Alvarsson <jonathan.alvarsson@farmbio.uu.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.scripting.business;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.jsexecution.tools.MonitorContainer;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;


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
        Object[] args = invocation.getArguments();
        Method m = findMethodWithMonitor(invocation);
        if ( m != null) {
            methodToInvoke = m;
            /*
             * Setup args array
             */
            args = new Object[
                 invocation.getArguments().length + 1];
            
            IProgressMonitor monitor 
                = new SubProgressMonitor( MonitorContainer.getInstance()
                                                          .getMonitor(), 
                                          0 ); 
            if ( monitor.isCanceled() ) {
                throw new OperationCanceledException();
            }
            
            monitor.subTask( "Running: " + invocation.getMethod().getName() );
            
            args[args.length-1] = monitor;
            System.arraycopy( invocation.getArguments(), 
                              0, 
                              args, 
                              0, 
                              invocation.getArguments().length );

        }
        else {
            m = findMethodWithCorrespondingIFile(invocation);
            if ( m != null ) {
                methodToInvoke = m;
            }
        }

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
        
        return methodToInvoke.invoke( invocation.getThis(), args ); 
    }

    private Method 
            findMethodWithCorrespondingIFile( MethodInvocation invocation ) {

        List<Class<?>> invocationParamTypes 
            = Arrays.asList( invocation.getMethod().getParameterTypes() );
        METHODS:
        for ( Method m : invocation.getMethod()
                                   .getDeclaringClass().getMethods() ) {
    
            List<Class<?>> currentParamTypes 
                = Arrays.asList( m.getParameterTypes() );
            if ( !m.getName().equals( invocation.getMethod().getName() ) ) 
                continue METHODS;
            
            if ( currentParamTypes.size() != invocationParamTypes.size() ) {
                continue METHODS;
            }
            
            if ( !currentParamTypes.contains( IFile.class ) ) {
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
