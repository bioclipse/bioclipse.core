package net.bioclipse.ui.views;

import net.bioclipse.scripting.Activator;
import net.bioclipse.scripting.GroovyEnvironment;

/**
 * A console for a Groovy session. For more on Groovy, see
 * <a href="http://groovy.codehaus.org/">the Groovy home page</a>.
 * 
 * @author masak
 *
 */
public class GroovyConsoleView extends ScriptingConsoleView {

    private GroovyEnvironment groovy
        = Activator.getDefault().GROOVY_SESSION;
    
    /**
     * The constructor. Called by Eclipse reflection when a new console
     * is created.
     * 
     * @see ScriptingConsoleView.<init>
     */
    public GroovyConsoleView() {

        super();
    }

    /* (non-Javadoc)
     * @see net.bioclipse.core.views.ScriptingConsoleView#commandLinePrefix()
     */
    @Override
    protected String commandLinePrompt() {
        return "groovy> ";
    }

    /* (non-Javadoc)
     * @see net.bioclipse.core.views.ScriptingConsoleView#executeCommand(java.lang.String)
     */
    @Override
    protected String executeCommand(String command) {
        return groovy.eval(command);
    }

}
