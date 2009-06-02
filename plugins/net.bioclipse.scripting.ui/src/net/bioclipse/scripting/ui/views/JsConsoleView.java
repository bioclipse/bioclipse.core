/*******************************************************************************
 * Copyright (c) 2008-2009 Carl Masak <carl.masak@farmbio.uu.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.scripting.ui.views;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.scripting.Activator;
import net.bioclipse.scripting.Hook;
import net.bioclipse.scripting.JsAction;
import net.bioclipse.scripting.JsThread;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.mozilla.javascript.NativeJavaObject;

public class JsConsoleView extends ScriptingConsoleView {

    private static final String JS_UNDEFINED_RE
      = "org.mozilla.javascript.Undefined@.*";
    private static JsThread jsThread = Activator.getDefault().JS_THREAD;

    @Override
    protected String executeCommand( String command ) {
        if (command.matches("help( .*)?") || command.matches("man( .*)?")) {
            printMessage( helpString(command) );
            return "";
        } else if (command. matches("doi( .*)?")) {
            printMessage( openDOI(command) );
            return "";
        }

        executeJsCommand(command);
        return "";
    }

    private void executeJsCommand(String command) {
        if (!jsThread.isAlive()) {
            Activator.getDefault().JS_THREAD = jsThread = new JsThread();
            jsThread.start();
        }
        jsThread.enqueue(new JsAction(command,
                                      new Hook() {
            public void run(final Object result) {
                final String[] message = new String[1];
                Display.getDefault().asyncExec( new Runnable() {
                    public void run() {
                        if ( null != result ) {
                            if (result instanceof NativeJavaObject) {
                          
                                Object unwrappedObject
                                  = ((NativeJavaObject)result).unwrap();
                          
                                if (unwrappedObject instanceof List) {
                                    List<?> list = (List<?>)unwrappedObject;
                                    StringBuilder sb
                                      = listToString( list, "[", ", ", "]" );
                              
                                    message[0] = sb.toString();
                                }
                                else {
                                    message[0] = unwrappedObject.toString();
                                }
                            }
                            else if (result instanceof Exception) {
                                message[0] = getErrorMessage((Exception)result);
                            }
                            else {
                                String s = result.toString();
                                message[0] = s.matches( JS_UNDEFINED_RE )
                                             ? "" : result.toString();
                            }
                            printMessage(message[0] + NEWLINE);
                        }
                    }
                } );
            }
        }));
    }

    public void waitUntilCommandFinished() {
        // If there is a nicer way to do this, please let me know. -- masak
        while (JsThread.isBusy()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public String getErrorMessage(Throwable t) {
        if (t == null)
            return "";

        while (!(t instanceof BioclipseException)
                && t.getCause() != null)
            t = t.getCause();

        return (t instanceof BioclipseException
                ? "" : t.getClass().getName() + ": ")
               + (t.getMessage() == null
                  ? ""
                  : t.getMessage() .replaceAll(" end of file", " end of line")
                 );
    }

    private StringBuilder listToString( List<?> list, String opener,
                                        String separator, String closer ) {

        StringBuilder sb = new StringBuilder();
        
        sb.append( opener );
        
        int index = 0;
        for ( Object item : list ) {
            if ( index++ > 0 )
                sb.append( separator );

            sb.append( item.toString() );
        }
        
        sb.append( closer );
        return sb;
    }

    /** Returns the specified amount of dashes.
     * 
     * @param length
     * @return
     */
    private String dashes(int length) {

        StringBuilder result = new StringBuilder();

        for ( int i = 0; i < length; ++i )
            result.append('-');

        return result.toString();
    }
    
    /**
     * Opens a web browser if the method for which the 'doi' command is
     * called has an associated DOI.
     *
     * @param command the complete command from the console input
     * @return nothing if everything went fine; error message or help on the
     *         command otherwise.
     */
    private String openDOI(String command) {
        if (command == null)
            return "";

        final String usageMessage = "Usage of doi: 'doi <manager>.<method>'";

        command = command.trim();

        if ( !command.matches("doi \\w+\\.\\w+") )
            return usageMessage;

        String helpObject = command.substring(command.indexOf(' ') + 1);

        String[] parts = helpObject.split("\\.");

        String managerName = parts[0];
        String methodName  = parts[1];

        IBioclipseManager manager
          = JsThread.js.getManagers().get(managerName);
        if (manager == null)
            return "No such manager: " + managerName
                   + NEWLINE + usageMessage;

        Set<String> uniqueDOIs = new LinkedHashSet<String>();
        for (Method method : findAllPublishedMethods(manager.getClass())) {
            if ( method.getName().equals(methodName) ) {
                PublishedMethod publishedMethod
                    = method.getAnnotation( PublishedMethod.class );

                String[] dois = publishedMethod.doi();
                if (dois == null)
                    continue;

                uniqueDOIs.addAll(Arrays.asList(dois));
            }
        }
        if (uniqueDOIs.size() == 0) {
            return "Method(s) does not refer to any DOI." + NEWLINE;
        } else {
            for (String doi : uniqueDOIs) {
                IWorkbenchBrowserSupport browserSupport =
                        getSite().getPage().getWorkbenchWindow().getWorkbench().
                        getBrowserSupport();
                IWebBrowser browser;
                try {
                    browser = browserSupport.createBrowser(
                        IWorkbenchBrowserSupport.LOCATION_BAR |
                        IWorkbenchBrowserSupport.NAVIGATION_BAR,
                        null, null, null
                    );
                    browser.openURL(new URL("http://dx.doi.org/" + doi));
                } catch (PartInitException e) {
                    return "Could not open DOI link: " + e.getMessage();
                } catch (MalformedURLException e) {
                    return "Invalid DOI: " + doi;
                }
            }
        }

        return "";
    }

    /**
     * Returns a help string documenting a Manager or one of its methods.
     * These help strings are printed to the console in response to the
     * command "help x" (where x is a manager) or "help x.y" (where y is
     * a method).
     *
     * @param command the complete command from the console input
     * @return a string documenting a manager or one of its methods
     */
    private String helpString(String command) {

        if (command == null)
            return "";

        final String usageMessage = "Usage of help: 'help', 'help <manager>', "
                                    + "or 'help <manager>.<method>'";
        StringBuilder result = new StringBuilder();

        command = command.trim();

        if ( "help".equals(command) || "man".equals(command) ) {
            
            StringBuilder sb = new StringBuilder();
            
            sb.append(usageMessage);
            List<String> managerNames
              = new ArrayList<String>( JsThread.js.getManagers().keySet() );
            if ( !managerNames.isEmpty() ) {
                Collections.sort( managerNames );
                sb.append( NEWLINE + "Available managers:" + NEWLINE );
                for ( String name : managerNames ) {
                    sb.append( "  " );
                    sb.append( name );
                    sb.append( NEWLINE );
                }
            }
            
            return sb.toString();
        }
        
        String helpObject = command.substring(command.indexOf(' ') + 1);

        if ( JsThread.topLevelCommands.containsKey(helpObject) )
            return helpObject
                   + "(" + JsThread.topLevelCommands.get(helpObject)[0] + ")"
                   + " is a shortcut for "
                   + JsThread.topLevelCommands.get(helpObject)[1] + NEWLINE;

        //Doing manager method 
        if ( helpObject.contains(".") ) {

            String[] parts = helpObject.split("\\.");

            if ( parts.length != 2 )
                return usageMessage;

            String managerName = parts[0];
            String methodName  = parts[1];

            IBioclipseManager manager
              = JsThread.js.getManagers().get(managerName);
            if (manager == null)
                return "No such manager: " + managerName
                       + NEWLINE + usageMessage;

            for (Method method : findAllPublishedMethods(manager.getClass())) {
                if ( method.getName().equals(methodName) ) {
                    PublishedMethod publishedMethod
                        = method.getAnnotation( PublishedMethod.class );

                    String line
                        = dashes(Math.min((managerName.length()
                                           + method.getName().length()
                                           + publishedMethod.params().length()
                                           + 3),
                                          MAX_OUTPUT_LINE_LENGTH));
    
                    for (String _ : new String[] {
                            line,                               NEWLINE,
                            managerName, ".", method.getName(),
                            "(", publishedMethod.params(), ")", NEWLINE,
                            line,                               NEWLINE,
                            publishedMethod.methodSummary(),    NEWLINE,
                                                                NEWLINE })
                        result.append(_);

                    String[] dois = publishedMethod.doi();
                    if (dois.length > 0) {
                        result.append("Further information (DOI): ")
                              .append(NEWLINE);
                        for (String doi : dois) {
                            if (doi != null && doi.length() > 0) {
                                result.append(doi).append(NEWLINE);
                            }
                        }
                    }
                    result.append(NEWLINE);
                }
            }
        }

        //Doing plain manager help
        else {
            IBioclipseManager manager
              = JsThread.js.getManagers().get(helpObject);

            if (manager == null)
                return "No such manager: " + helpObject + NEWLINE
                       + usageMessage;

            StringBuffer managerDescBuffer = new StringBuffer();
            for (Class<?> clazz
                    : findAllPublishedClasses(manager.getClass()))
                managerDescBuffer.append(clazz.getAnnotation(
                                       PublishedClass.class
                                   ).value());
            String managerDesc = managerDescBuffer.toString();

            String line = dashes( Math.min(helpObject.length(), 
                                  MAX_OUTPUT_LINE_LENGTH) );
            String theseMeths = " This manager has the following methods:";

            for (String _ : new String[] {
                    line,        NEWLINE,
                    helpObject,  NEWLINE,
                    line,        NEWLINE,
                    managerDesc, NEWLINE,
                                 NEWLINE,
                    theseMeths,  NEWLINE })
                result.append(_);

            for (Method method : findAllPublishedMethods(manager.getClass())) {
                if ( method.getAnnotation( PublishedMethod.class )
                           .params().length() == 0 ) {
                    result.append( method.getName() + "()" );
                }
                else {
                    result.append(
                            method.getName() + "( "
                            + method.getAnnotation( PublishedMethod.class )
                                    .params()
                            + " )" );
                }
                result.append(NEWLINE);
            }

            // Output the manager-associated DOIs
            for (Class<?> clazz
                            : findAllPublishedClasses(manager.getClass())) {
                PublishedClass publishedClass = clazz.getAnnotation(
                    PublishedClass.class
                );
                String[] dois = publishedClass.doi();
                if (dois.length > 0) {
                    for (String _ : new String[] {
                                                       NEWLINE,
                        line,                          NEWLINE,
                        "Further information (DOI): ", NEWLINE })
                        result.append(_);

                    for (String doi : dois) {
                        if (doi != null && doi.length() > 0)
                            result.append(doi).append(NEWLINE);
                    }
                }
                result.append(NEWLINE);
            }
        }

        return result.toString();
    }

    @SuppressWarnings("unchecked")
    protected List<String> allNamesIn(String object) {

        // Tab completion has to get in line, just as everything else. Instead
        // of blocking the console waiting for a command to finish, we take the
        // easy way out and disallow tab completion while a command is running.
        if ( JsThread.isBusy() ) {
            beep();
            return new ArrayList<String>();
        }        
        
        if (object == null || "".equals(object))
            object = "this";

        IBioclipseManager manager = JsThread.js.getManagers().get(object);
        if ( null != manager ) {
            List<String> variables = new ArrayList<String>();

            for ( Method method : findAllPublishedMethods(manager.getClass()) )
                if ( !variables.contains( method.getName() ))
                    variables.add( method.getName() );

            return variables;
        }

        final List<String>[] variables = new List[1];
        
        jsThread.enqueue(
            new JsAction( "zzz1 = new Array(); zzz2 = 0;"
                          + "for (var zzz3 in " + object
                          + ") { zzz1[zzz2++] = zzz3 } zzz1",
                          new Hook() {
                              public void run(Object o) {
                                  synchronized (variables) {
                                      if (o instanceof Exception) {
                                          // it's probably the tab-completed
                                          // object that doesn't exist
                                          variables[0] =
                                              new ArrayList<String>();
                                          variables.notifyAll();
                                          return;
                                      }
                                      String array = jsThread.toJsString(o);
                                      variables[0]
                                          = new ArrayList<String>(
                                                  Arrays.asList(
                                                      array.split( "," )));
                                      variables.notifyAll();
                                  }
                              }
                          }
             )
        );
        
        int attemptsLeft = 10;
        synchronized (variables) {
            while (variables[0] == null) {
                try {
                    Thread.sleep( 50 );
                    if (--attemptsLeft <= 0) // js is probably busy then
                        return Collections.EMPTY_LIST;
                    
                    variables.wait();
                } catch ( InterruptedException e ) {
                    return Collections.EMPTY_LIST;
                }
            }
        }

        // The following happens sometimes when we tab complete on something
        // unexpected. We choose to beep instead of outputting "syntax error".
        if (variables[0].size() == 1 &&
                ("syntax error".equals(variables[0].get(0)) ||
                 variables[0].get(0).startsWith("ReferenceError"))) {
            beep();
            return new ArrayList<String>();
        }

        variables[0].remove("zzz1");
        variables[0].remove("zzz2");
        variables[0].remove("zzz3");

        return variables[0];
    }

    @SuppressWarnings("serial")
    protected List<String> allSpecialCommands() {
        return new ArrayList<String>() {{
           add("help");
           add("man");
           add("doi");
        }};
    }

    private Method[] findAllPublishedMethods(Class<?> interfaze) {
        return findAllPublishedMethods(
                interfaze,
                new ArrayList<Method>(),
                new HashSet<String>()
        ).toArray(new Method[0]);
    }
    
    private List<Method> findAllPublishedMethods(Class<?> interfaze,
                                                 List<Method> methods,
                                                 HashSet<String> visited) {

        for ( Method method : interfaze.getMethods() ) {
            if ( method.isAnnotationPresent(PublishedMethod.class) ) {
                PublishedMethod publishedMethod
                = method.getAnnotation( PublishedMethod.class );

                String signature = method.getName() + publishedMethod.params();
                if (visited.contains( signature ))
                    continue;
                visited.add( signature ); 
                methods.add( method );
            }
        }
        
        for (Class<?> parent : interfaze.getInterfaces())
            findAllPublishedMethods(parent, methods, visited);
            
        return methods;
    }

    private Class<?>[] findAllPublishedClasses(Class<?> clazz) {
        return findAllPublishedClasses(
                clazz,
                new ArrayList<Class<?>>(),
                new HashSet<Class<?>>()
        ).toArray(new Class<?>[0]);
    }
    
    private List<Class<?>> findAllPublishedClasses(Class<?> clazz,
                                                   List<Class<?>> classes,
                                                   HashSet<Class<?>> visited) {
        if (visited.contains( clazz ))
            return classes;

        visited.add( clazz );
        if ( clazz.isAnnotationPresent(PublishedClass.class) )
            classes.add( clazz );

        for (Class<?> parent : clazz.getInterfaces())
            findAllPublishedClasses(parent, classes, visited);

        return classes;
    }

    /**
     * Outputs extra characters after the actual name of the completed thing.
     * For managers, this could be a period ("."), because that's what the
     * user will write herself anyway. For methods, it could be "(", or "()"
     * if the method has no parameters.
     * 
     * @param object the thing written before the dot (if any) when completing
     * @param completedVariable the variable that was just tab-completed
     * @return any extra characters to be output after the completed name
     */
    protected String tabCompletionHook( String parent, String completedName ) {
        
        // if the user typed any of the special-cased commands, we don't want
        // to complete with anything.
        for ( String prefix : allSpecialCommands() )
            if ( currentCommand().startsWith( prefix + " " ) )
                return "";
        
        // however, an extra space doesn't hurt
        for ( String specialCmd : allSpecialCommands() )
            if ( currentCommand().equals( specialCmd ) )
                return " ";

        // a manager gets a period ('.') appended to it, since that's what the
        // user wants to write anyway.
        if ( "".equals(parent)
             && JsThread.js.getManagers().containsKey( completedName ) )
            return ".";
        
        // a top level command is really an aliased method, and should have
        // a '(' and possibly a ')' on it
        if ( "".equals(parent)
             && JsThread.topLevelCommands.containsKey( completedName ) )
            return "".equals( JsThread.topLevelCommands.get(completedName)[0] )
                ? "()"
                : "(";

        // a manager method gets a '(', and possibly a ')' too if it takes
        // no parameters
        IBioclipseManager manager = JsThread.js.getManagers().get(parent);
        if ( null != manager )
            for (Class<?> clazz : findAllPublishedClasses(manager.getClass()))
                for ( Method method : clazz.getDeclaredMethods() )
                    if ( method.isAnnotationPresent(PublishedMethod.class)
                         && method.getName().equals(completedName) )

                        return method.getParameterTypes().length == 0
                               ? "()" : "(";
        
        // in all other cases, we add nothing
        return "";
    }
}
