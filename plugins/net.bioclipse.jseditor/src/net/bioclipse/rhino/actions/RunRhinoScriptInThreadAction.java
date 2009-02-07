package net.bioclipse.rhino.actions;

import net.bioclipse.rhino.editors.JsEditor;
import net.bioclipse.rhino.exceptions.ScriptException;
import net.bioclipse.rhino.RhinoConsole;
import net.bioclipse.rhino.PluginLogger;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * This file is part of the Bioclipse Rhino Plug-in.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener, Ola Spjuth
 */
public class RunRhinoScriptInThreadAction extends Action {
	public RunRhinoScriptInThreadAction() {
		super("Run script in main thread (default)");
	}

	public void run() {
		// show javascript console (eclipse console)
		RhinoConsole.show();
		
		String scriptString = "";
		// Get the command from the editor
		IEditorPart ep = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (!(ep instanceof JsEditor)) {
			PluginLogger.log("No Rhino Editor active");
			return;
		}

		JsEditor re = (JsEditor) ep;
		
		try {
			scriptString = re.getScriptString();
		} catch (ScriptException e) {
			RhinoConsole.writeToConsoleRed("Exception: " + e.getMessage());
		}

		RhinoConsole.writeToConsoleBlue("Running Javascript...");

		try {
			String result = ScriptExecution.runRhinoScript(scriptString);
			RhinoConsole.writeToConsole(result);
		} catch (Exception e) {
			RhinoConsole.writeToConsole(e.getMessage());
		}		
		RhinoConsole.writeToConsoleBlue("Javascript done.");
	}
}