/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
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
