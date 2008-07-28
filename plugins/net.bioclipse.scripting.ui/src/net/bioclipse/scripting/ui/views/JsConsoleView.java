package net.bioclipse.scripting.ui.views;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.scripting.Hook;
import net.bioclipse.scripting.JsAction;
import net.bioclipse.scripting.JsThread;
import net.bioclipse.scripting.OutputProvider;
import net.bioclipse.ui.Activator;
import net.bioclipse.ui.ConsoleEchoer;
import net.bioclipse.ui.EchoEvent;
import net.bioclipse.ui.EchoListener;
import net.bioclipse.ui.JsPluginable;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;

/**
 * A console for a Javascript session. For more on Javascript, see
 * <a href="http://www.ecmascript.org/">the ECMAScript home page</a>.
 *
 * @author masak
 *
 */
public class JsConsoleView extends ScriptingConsoleView
                           implements EchoListener {

    private static final Logger logger = Logger.getLogger(JsConsoleView.class);

    private static Matcher jsBacktickMatcher;

    static {
        String noBackTick
          = "("                                   // capture into group:
              + "(?:"                               // either...
                + "[^`\'\"]+"                          // no backticks or quotes
                + "|"                                  // ...or...
                + "(?:\"(?:[^\"\\\\]|(?:\\\\.))*\")"   // a double quoted string
                + "|"                                  // ...or...
                + "(?:\'(?:[^\'\\\\]|(?:\\\\.))*\')"   // a single quoted string
              + ")*+"                               // zero or more of the above
            + ")";

        jsBacktickMatcher
          = Pattern.compile( noBackTick + "`" + noBackTick + "`" ).matcher("");
    }

    private static JsThread jsThread
        = net.bioclipse.scripting.Activator.getDefault().JS_THREAD;

    static {
        jsThread.enqueue( "function clear() { js.clear() }" );
        jsThread.enqueue( "function print(message) { js.print(message) }" );
    }

    private JsPluginable rConnection = null;

    private static enum Mode {
        JS,
        R;
    }

    private Mode mode = Mode.JS;

    /**
     * The constructor. Called by Eclipse reflection when a new console
     * is created.
     *
     * @see ScriptingConsoleView.<init>
     */
    public JsConsoleView() {
        super();
        Activator.getDefault().CONSOLE.addListener(this);
    }

    void setMode(Mode newMode) {
        if (newMode == Mode.R && rConnection == null)
            installR();

        if (newMode == Mode.R && rConnection == null) {
            printMessage("R is not installed -- get net.bioclipse.r");
            return;
        }

        mode = newMode;
    }

    /* (non-Javadoc)
     * @see net.bioclipse.core.views.ScriptingConsoleView#commandLinePrefix()
     */
    protected String commandLinePrompt() {
        return mode == Mode.JS ? "js> " : "R> ";
    }

    private void installR() {

        IExtensionRegistry registry = Platform.getExtensionRegistry();

        if (registry == null) { // for example, when we are running the tests
            logger.debug("Registry does not exist. If tests are running, "
                    + "this is in order.");
            return;             // nothing we can do anyway
        }

        IExtensionPoint extensionPoint
            = registry.getExtensionPoint("net.bioclipse.ui.r_provider");

        IExtension[] extensions = extensionPoint.getExtensions();

        if (extensions.length == 0)
            return;

        IExtension e = extensions[0];
        IConfigurationElement[] cfgElems = e.getConfigurationElements();

        for (IConfigurationElement cfgElem : cfgElems) {

            try {
                this.rConnection = (JsPluginable)cfgElem
                                   .createExecutableExtension("service");
                this.rConnection.setOutputProvider( new OutputProvider(){
                        public void output(final String r) {
                            Activator.getDefault().CONSOLE.echo(r);
                        }
                    }
                );
            }
            catch (Exception ex) {
                throw new RuntimeException(
                        "Failed to instantiate factory: "
                        + cfgElem.getAttribute("class")
                        + "\n in type: "
                        + extensionPoint.getUniqueIdentifier()
                        + "\n in plugin: "
                        + e.getExtensionPointUniqueIdentifier()
                        + "\n"
                        + ex.getMessage());
            }
        }
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

    private String executeRCommand(String command) {

        ArrayList<Object> al;
        try {
            al = interpolateJsVariables(command);
        }
        catch (RuntimeException rte) {
            return rte.toString();
        }

        try {
            rConnection.eval(al);
        } catch (IOException e) {
            LogUtils.debugTrace(logger, e);
        }

        return "";
    }

    private ArrayList<Object> interpolateJsVariables(String command) {

        ArrayList<Object> al = new ArrayList<Object>();
        jsBacktickMatcher.reset(command);

        int e = 0;
        while(jsBacktickMatcher.find()){

            al.add(jsBacktickMatcher.group(1));
            al.add(JsThread.js.evalToObject(jsBacktickMatcher.group(2)));
            e = jsBacktickMatcher.end();
        }
        al.add(command.substring(e));

        return al;
    }

    /* (non-Javadoc)
     * @see net.bioclipse.core.views.ScriptingConsoleView#executeCommand(java.lang.String)
     */
    protected String executeCommand(String command) {
        if (command == null)
            return "";

        if (command.matches("help( .*)?") || command.matches("man( .*)?")) {
            return helpString(command);
        }

        if (mode == Mode.JS) {
            if ("R".equals(command.trim())) {
                setMode(Mode.R);
                return "";
            }
            executeJsCommand(command);
            return "";
        }
        else {
            if ("q()".equals(command.trim())) {
                setMode(Mode.JS);
                return "";
            }
            return executeRCommand(command);
        }
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
                sb.append( "\nAvailable managers:\n" );
                for ( String name : managerNames ) {
                    sb.append( "  " );
                    sb.append( name );
                    sb.append( "\n" );
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
                       + "\n" + errorMessage;

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
                            = dashes(managerName.length()
                                     + method.getName().length()
                                     + publishedMethod.params().length()
                                     + 3,
                                     MAX_OUTPUT_LINE_LENGTH);

                        result.append( line );
                        result.append( '\n' );

                        result.append( managerName );
                        result.append( '.' );
                        result.append( method.getName() );
                        result.append( '(' );
                        result.append( publishedMethod.params() );
                        result.append( ")\n" );

                        result.append( line );
                        result.append( '\n' );

                        result.append( publishedMethod.methodSummary() );
                        result.append( '\n' );
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
                       + "\n" + errorMessage;

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
                        "\n\n This manager has " +
                        "the following methods: \n" );
                    
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
                            methodNames.add( 
                                method.getName() + "( "  
                                + method.getAnnotation( 
                                  PublishedMethod.class ).params() 
                                + " )" );
                        }
                    }
                    for ( String methodName : methodNames ) {
                        managerDescription.append( methodName );
                        managerDescription.append( "\n" );
                    }
                    
                    managerDescription.deleteCharAt( 
                        managerDescription.length()-1 );
                }
            }

            String line = dashes( helpObject.length(), 
                                  MAX_OUTPUT_LINE_LENGTH );

            result.append(line);
            result.append( '\n' );

            result.append(helpObject);
            result.append( '\n' );

            result.append(line);
            result.append( '\n' );

            result.append( managerDescription );
            result.append( '\n' );
        }

        return result.toString();
    }

    private String dashes(int length, int maxLength) {

        StringBuilder result = new StringBuilder();

        for ( int i = 0; i < Math.min(length, maxLength); ++i )
            result.append('-');

        return result.toString();
    }

    public void receiveLogEvent(EchoEvent e) {
        printMessage(e.getMessage());
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

            for ( Class<?> interfaze : manager.getClass().getInterfaces() )
                for ( Method method : interfaze.getDeclaredMethods() )
                    if ( method.isAnnotationPresent(PublishedMethod.class)
                         && !variables.contains( method.getName() ))

                        variables.add( method.getName() );

            return variables;
        }

        final List<String>[] variables = new List[1];
        
        jsThread.enqueue(
            new JsAction( "zzz1 = new Array(); zzz2 = 0;"
                          + "for (var zzz3 in " + object
                          + ") { zzz1[zzz2++] = zzz3 } zzz1",
                          new Hook() {
                              public void run(String array) {
                                  synchronized (variables) {
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
        
        // a manager gets a period ('.') appended to it, since that's what the
        // user wants to write anyway. (unless she typed "help" or "man")
        if ( "".equals(parent)
             && JsThread.js.getManagers().containsKey( completedName )
             && !currentCommand().startsWith( "help " )
             && !currentCommand().startsWith( "man " ) )
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
    
    public static class ConsoleProgressMonitor implements IProgressMonitor {

        private static final int WIDTH = 50;
        private int totalWork;
        private int current;
        private boolean isCanceled;
        private int painted = 1; 
        private static final ConsoleEchoer CONSOLE 
            = Activator.getDefault().CONSOLE; 

        public ConsoleProgressMonitor() {
            CONSOLE.echo( "|1%" + spaces(WIDTH - 8) + "100%|\n|" );
        }
        
        public void beginTask( String name, int totalWork ) {
            this.totalWork = totalWork;
        }

        private String spaces( int i ) {
            
            StringBuilder s = new StringBuilder();
            for ( int j = 0; j < i; j++ ) {
                s.append( " " );
            }
            return s.toString();
        }

        public void done() {
            current = totalWork;
            CONSOLE.echo( "\n" );
            updateText();
        }

        public void internalWorked( double work ) {
        }

        public boolean isCanceled() {
            return isCanceled;
        }

        public void setCanceled( boolean value ) {
            this.isCanceled = value;
        }

        public void setTaskName( String name ) {
        }

        public void subTask( String name ) {
        }

        public void worked( int work ) {
            this.current += work;
            updateText();
        }

        private void updateText() {
            double done = current / (totalWork * 1.0) ;
            if( done*WIDTH > painted ) {
                double numOfChars = done*WIDTH-painted;
                for( int i = 0 ; i < numOfChars ; i++) {
                    CONSOLE.echo( "|" );
                    painted++;
                }
            }
        }
    }
}
