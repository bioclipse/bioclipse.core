package net.bioclipse.scripting;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;

import org.codehaus.groovy.control.CompilationFailedException;

/**
 * Groovy environment. Holds variables and evaluates expressions.
 *
 * @author masak
 *
 */
public class GroovyEnvironment implements ScriptingEnvironment {

    private GroovyShell shell;

    public GroovyEnvironment() {
        reset();
    }

    public final void reset() {
        shell = new GroovyShell();
    }

    public String eval(String expression) {
        try {
            Object result = shell.evaluate(expression);
            return result.toString();
        }
        catch (CompilationFailedException cfe) {
            return "Syntax not understood: " + cfe;
        }
        catch (GroovyRuntimeException gre) {
            return gre.getMessage();
        }
    }
}
