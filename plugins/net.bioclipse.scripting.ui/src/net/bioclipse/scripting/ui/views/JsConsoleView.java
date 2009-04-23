package net.bioclipse.scripting.ui.views;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.scripting.Hook;
import net.bioclipse.scripting.JsAction;
import net.bioclipse.scripting.JsThread;

import org.eclipse.swt.widgets.Display;
import org.mozilla.javascript.NativeJavaObject;

public class JsConsoleView extends ScriptingConsoleView {

    private static final String JS_UNDEFINED_RE
      = "org.mozilla.javascript.Undefined@.*";
    private static JsThread jsThread
      = net.bioclipse.scripting.Activator.getDefault().JS_THREAD;

    static {
        jsThread.enqueue( "function clear() { js.clear() }" );
        jsThread.enqueue( "function print(message) { js.print(message) }" );
        jsThread.enqueue( "function say(message) { js.say(message) }" );
    }

    @Override
    protected String executeCommand( String command ) {
        if (command.matches("help( .*)?") || command.matches("man( .*)?")) {
            printMessage( helpString(command) );
            return "";
        }

        executeJsCommand(command);
        return "";
    }

    private void executeJsCommand(String command) {
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
                            printMessage(message[0] + "\n");
                        }
                    }
                } );
            }
        }));
    }

    public String getErrorMessage(Throwable t) {
        while (!(t instanceof BioclipseException)
                && t.getCause() != null)
            t = t.getCause();

        return (t instanceof BioclipseException
                ? "" : t.getClass().getName() + ": ")
               + t.getMessage()
                  .replaceAll( " end of file",
                               " end of line" );
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

        if ( "help".equals(command.trim()) || 
             "man".equals(command.trim()) ) {
            
            StringBuilder sb = new StringBuilder();
            
            sb.append(usageMessage);
            List<String> managerNames
                = new ArrayList<String>( JsThread.js.getManagers()
                                                 .keySet() );
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
                            line, NEWLINE,
                            managerName, ".", method.getName(), "(",
                            publishedMethod.params(), ")", NEWLINE,
                            line, NEWLINE,
                            publishedMethod.methodSummary(), NEWLINE })
                        result.append(_);
                }
            }
        }

        //Doing plain manager help
        else {
            IBioclipseManager manager
                = JsThread.js.getManagers().get(helpObject);

            if (manager == null)
                return "No such method: " + helpObject + NEWLINE
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
                    theseMeths,  NEWLINE } )
                result.append(_);

            for (Method method : findAllPublishedMethods(manager.getClass())) {
                if ( method
                        .getAnnotation( PublishedMethod.class )
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
        }

        return result.toString();
    }

    @SuppressWarnings("unchecked")
    protected List<String> getAllVariablesIn(String object) {

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
        
        // if the user typed "help" or "man", we don't want to complete with
        // anything.
        if ( currentCommand().startsWith( "help " )
             || currentCommand().startsWith( "man " ) )
            return "";
        
        // a manager gets a period ('.') appended to it, since that's what the
        // user wants to write anyway.
        if ( "".equals(parent)
             && JsThread.js.getManagers().containsKey( completedName ) )
            return ".";
        
        // a manager method gets a '(', and possibly a ')' too if it takes
        // no parameters
        IBioclipseManager manager = JsThread.js.getManagers().get(parent);
        if ( null != manager )
            for ( Class<?> interfaze : manager.getClass().getInterfaces() )
                for ( Method method : interfaze.getDeclaredMethods() )
                    if ( method.isAnnotationPresent(PublishedMethod.class)
                         && method.getName().equals( completedName ))

                        return "("
                          + (method.getParameterTypes().length == 0 ? ")" : "");
        
        // in all other cases, we add nothing
        return "";
    }
}
