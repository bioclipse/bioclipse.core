package net.bioclipse.scripting.ui.views;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.scripting.Hook;
import net.bioclipse.scripting.JsAction;
import net.bioclipse.scripting.JsThread;


public class NewJsConsoleView extends NewScriptingConsoleView {

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
            public void run(final String result) {
                Display.getDefault().asyncExec( new Runnable() {
                    public void run() {
                        if ( !"undefined".equals(result) )
                            printMessage(result + "\n");
                    }
                } );
            }
        }));
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

        final String errorMessage = "Usage of help: `help <manager>` " +
                                    "or: `help <manager>.<method>`";
        StringBuilder result = new StringBuilder();

        if ( "help".equals(command.trim()) || 
             "man".equals(command.trim()) ) {
            
            StringBuilder sb = new StringBuilder();
            
            sb.append(errorMessage);
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
                return errorMessage;

            String managerName = parts[0];
            String methodName  = parts[1];

            IBioclipseManager manager
                = JsThread.js.getManagers().get(managerName);
            if (manager == null)
                return "No such manager: " + managerName
                       + NEWLINE + errorMessage;

            for ( Class<?> interfaze : manager.getClass()
                                              .getInterfaces() ) {

                for ( Method method : interfaze.getMethods() ) {

                    if ( method.getName().equals(methodName) &&
                         method.isAnnotationPresent(
                             PublishedMethod.class ) ) {

                        PublishedMethod publishedMethod
                            = method.getAnnotation(
                                PublishedMethod.class );

                        String line
                            = dashes(Math.min(managerName.length()
                                     + method.getName().length()
                                     + publishedMethod.params().length()
                                     + 3, MAX_OUTPUT_LINE_LENGTH));

                        result.append( line );
                        result.append( NEWLINE );

                        result.append( managerName );
                        result.append( '.' );
                        result.append( method.getName() );
                        result.append( '(' );
                        result.append( publishedMethod.params() );
                        result.append( ")" );
                        result.append( NEWLINE );

                        result.append( line );
                        result.append( NEWLINE );

                        result.append( publishedMethod.methodSummary() );
                        result.append( NEWLINE );
                    }
                }
            }
        }

        //Doing plain manager help
        else {
            IBioclipseManager manager
                = JsThread.js.getManagers().get(helpObject);

            if (manager == null)
                return "No such method: " + helpObject
                       + NEWLINE + errorMessage;

            StringBuilder managerDescription = new StringBuilder();
            for ( Class<?> interfaze : manager.getClass()
                                              .getInterfaces() ) {
                
                if ( interfaze
                     .isAnnotationPresent(PublishedClass.class) ) {

                    managerDescription.append(
                            interfaze.getAnnotation(
                                    PublishedClass.class
                            ).value()
                    );
                    managerDescription.append( 
                        NEWLINE + NEWLINE + " This manager has " +
                        "the following methods: " + NEWLINE );
                    
                    List<String> methodNames = new ArrayList<String>();
                    Method[] methods = interfaze.getMethods();
                    Arrays.sort( methods, new Comparator<Method>()  {
                        public int compare( Method m1, Method m2 ) {
                            int c = m1.getName()
                                      .compareTo( m2.getName() );
                            return c != 0 
                                ? c
                                :  m1.getParameterTypes().length
                                 - m2.getParameterTypes().length;
                        }
                    });
                    for ( Method method : methods ) {
                        if ( method.isAnnotationPresent( 
                             PublishedMethod.class ) ) {
                            
                            if ( method
                                 .getAnnotation( PublishedMethod.class )
                                 .params().length() == 0 ) {
                                methodNames.add( method.getName() 
                                                 + "()" );
                            }
                            else {
                                methodNames.add( 
                                    method.getName() + "( "  
                                    + method.getAnnotation( 
                                      PublishedMethod.class ).params() 
                                    + " )" );
                            }
                        }
                    }
                    for ( String methodName : methodNames ) {
                        managerDescription.append( methodName );
                        managerDescription.append( NEWLINE );
                    }
                    
                    managerDescription.deleteCharAt( 
                        managerDescription.length()-1 );
                }
            }

            String line = dashes( Math.min(helpObject.length(), 
                                  MAX_OUTPUT_LINE_LENGTH) );

            result.append(line);
            result.append( NEWLINE );

            result.append(helpObject);
            result.append( NEWLINE );

            result.append(line);
            result.append( NEWLINE );

            result.append( managerDescription );
            result.append( NEWLINE );
        }

        return result.toString();
    }

}
