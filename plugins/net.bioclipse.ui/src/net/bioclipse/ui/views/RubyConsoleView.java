package net.bioclipse.ui.views;

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
