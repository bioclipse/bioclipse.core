package net.bioclipse.scripting.ui.views;

import net.bioclipse.scripting.ui.Activator;


public class JmolConsoleView extends ScriptingConsoleView {

    @Override
    protected String executeCommand( String command ) {
        Activator.getDefault().getJmolManager().run( command );
        return null;
    }

}
