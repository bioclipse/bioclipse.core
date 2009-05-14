package net.bioclipse.jseditor.actions;

import net.bioclipse.jseditor.PluginLogger;
import net.bioclipse.jseditor.RhinoConsole;
import net.bioclipse.jseditor.editors.JsEditor;
import net.bioclipse.jseditor.exceptions.EditorException;
import net.bioclipse.jsexecution.execution.ScriptExecution;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/*
 * This file is part of the Bioclipse Javascript Editor Plug-in.
 * 
 * Copyright (c) 2008 Johannes Wagener.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Wagener - initial API and implementation
 */

public class RunRhinoScriptAsJobAction extends Action {
    public RunRhinoScriptAsJobAction() {
        super("Run script in separate thread");
    }

    public void run() {
        String scriptString = "";
        
        // show console:
        RhinoConsole.show();
        
        // Get the command from the editor
        IEditorPart ep = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                   .getActivePage().getActiveEditor();
        if (!(ep instanceof JsEditor)) {
            PluginLogger.log("No Javascript Editor active");
            return;
        }

        JsEditor re = (JsEditor) ep;

        try {
            scriptString = re.getScriptString();
        } catch (EditorException e) {
            RhinoConsole.writeToConsoleRed("Exception: " + e.getMessage());
        }

        RhinoConsole.writeToConsoleBlue("Running Javascript...");

        ScriptExecution.runRhinoScript(
                scriptString,
                re.getTitle(),
                RhinoConsole.getRhinoConsole()
        );
    }
}