/* ****************************************************************************
 *Copyright (c) 2008-2009 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package net.bioclipse.scripting;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.bioclipse.core.PublishedMethod;
import net.bioclipse.managers.business.IBioclipseManager;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * JavaScript environment. Holds variables and evaluates expressions.
 *
 * @author masak
 *
 */
public class JsEnvironment implements ScriptingEnvironment {

    ScriptEngine engine;
    private Map<String, IBioclipseManager> managers;
    private static final Logger logger = Logger.getLogger(JsEnvironment.class);

    public JsEnvironment() {
        reset();
    }

    /**
     * Initializes the JavaScript environment for use.
     */
    public final void reset() {
        ScriptEngineManager mgr
            = new ScriptEngineManager(JsEnvironment.class.getClassLoader());
        engine = mgr.getEngineByName("JavaScript");
        if ( engine == null ) {
            StringBuilder builder = new StringBuilder();
            for ( ScriptEngineFactory sef : mgr.getEngineFactories() ) {
                builder.append( sef.getLanguageName() ).append( ", " );
            }
            builder.delete( builder.length() - 2, builder.length() - 1 );
            logger.error( "Faild to find \"JavaScript\" scripting engine. Available engines are " + builder
                                          .toString() );
        }

        managers = new HashMap<String, IBioclipseManager>();

        installJsTools();
    }

    public Map<String, IBioclipseManager> getManagers() {
        return new HashMap<String, IBioclipseManager>(managers);
    }

    private void installJsTools() {

        IExtensionRegistry registry = Platform.getExtensionRegistry();

        if ( registry == null )
            return; // it likely means that the Eclipse workbench has not
                    // started, for example when running tests

        IExtensionPoint serviceObjectExtensionPoint = registry
            .getExtensionPoint("net.bioclipse.scripting.contribution");

        IExtension[] serviceObjectExtensions
            = serviceObjectExtensionPoint.getExtensions();
        for(IExtension extension : serviceObjectExtensions) {
            for( IConfigurationElement element
                 : extension.getConfigurationElements() ) {
                Object service = null;
                try {
                    service = element.createExecutableExtension("service");
                }
                catch (CoreException e) {
                    logger.error( "Failed to get a service: " + e.getMessage(),
                                  e );
                    continue;
                }
                if( service != null &&
                    !(service instanceof IBioclipseManager) ) {

                    throw new RuntimeException( "service object: " + service
                                               + "does not implement "
                                               + "IBioclipseManager" );
                }
                IBioclipseManager manager = (IBioclipseManager) service;
                String managerName = manager.getManagerName();
                engine.put( managerName, manager );
                managers.put( managerName, manager);
                logger.info( "Bioclipse manager: " + managerName +
                             " added to JavaScript " +
                             "environment." );
            }
        }

    }

    /**
     * Evaluates a given JavaScript expression.
     *
     * @param expression the expression to be evaluated
     * @return the result of the expression
     */
    public Object eval(String expression) {
        try {
            Object o = engine.eval(expression);
            return o;
        } catch ( ScriptException e ) {
           String message = e.getMessage();
           if( !message.contains( "Can't find method " ))
               throw new RuntimeException(e);
           return explanationAboutParameters( expression, message );
        }
    }

    private String explanationAboutParameters( String expression,
                                               String message ) {

        int iPeriod = message.indexOf( '.',
                                       message.indexOf( "Can't find method" ) ),
             iParen = message.indexOf( '(',
                                       message.indexOf( "Can't find method" ) );

        String calledMethod = message.substring( iPeriod + 1, iParen );

        if (expression.contains( "." + calledMethod + "(" )) {

            int iService
                = expression.indexOf( "." + calledMethod + "(" ) - 1;
            while ( iService > 0 && Character.isJavaIdentifierPart(
                       expression.charAt( iService - 1 )) )
                --iService;

            String managerName
                = expression.substring( iService,
                                        expression.indexOf(
                                           '.', iService ) );

              IBioclipseManager manager = getManagers().get(managerName);

              String params = null;
              int requiredParams = 0;
              if(manager != null) {

                  for ( Class<?> interfaze :
                          manager.getClass().getInterfaces() ) {
                      for ( Method method : interfaze.getMethods() ) {

                          if ( method.getName().equals(calledMethod) &&
                               method.isAnnotationPresent(
                                   PublishedMethod.class) ) {

                              PublishedMethod publishedMethod
                                  = method.getAnnotation(
                                      PublishedMethod.class);

                              params = publishedMethod.params();
                              requiredParams
                                  = numberOfSuchCharactersIn(
                                        params, ',' ) + 1;

                            if ( "".equals(publishedMethod.params()
                                           .trim()) )
                                requiredParams = 0;
                        }
                    }
                }

                int calledParams
                    = numberOfSuchCharactersIn(message, ',') + 1;
                if ( message.substring( iParen + 1,
                                        message.indexOf( ')' ))
                                            .trim().equals( "" ) )
                    calledParams = 0;

                if (calledParams != requiredParams)
                    return "The method " + calledMethod
                         + " can not be called with " + calledParams
                         + " parameter" + (calledParams == 1 ? "" : "s")
                         + ", it needs " + requiredParams + ".\n"
                         + managerName + '.' + calledMethod
                         + '(' + params + ")";
            }
        }

        return message;
    }

    private int numberOfSuchCharactersIn( String s, char c ) {

        int occurrances = 0, pos = 0;
        while ((pos = s.indexOf( c, pos ) + 1) != 0)
            ++occurrances;

        return occurrances;
    }

    /**
     * Evaluates a given JavaScript expression.
     *
     * @param expression the expression to be evaluated
     * @return the resulting object
     *
     * @throws RuntimeException when the evaluator couldn't parse
     *                          the expression
     * @throws EcmaError when the JavaScript runtime produced an
     *                   error evaluating the expression
     */
    public Object evalToObject(String expression) {
        try{
            return engine.eval(expression);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
