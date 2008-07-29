/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.bioclipse.scripting.Activator;
import net.bioclipse.scripting.RubyEnvironment;

public class RubyConsoleView extends ScriptingConsoleView {

    private RubyEnvironment ruby = Activator.getDefault().RUBY_SESSION;

    public RubyConsoleView() {
    }

    protected String commandLinePrompt() {
        return "ruby> ";
    }

    protected String executeCommand(String command) {
        String result = ruby.eval(command);
        return result == null ? "Syntax error" : result;
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllVariablesIn(String object) {
        // TODO: Generalize this to dotted completion
        return "".equals(object) ?
                new ArrayList<String>( Arrays.asList( executeCommand(
                        "local_variables.join(',')"
                ).split(",") ) ) :
                    Collections.EMPTY_LIST;
    }
}
