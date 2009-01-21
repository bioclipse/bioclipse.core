package net.bioclipse.rhino.actions;

import net.bioclipse.rhino.Activator;
import net.bioclipse.rhino.PluginLogger;
import net.bioclipse.rhino.editors.JsEditor;
import net.bioclipse.rhino.exceptions.ScriptException;
import net.bioclipse.rhino.RhinoConsole;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

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

public class RunRhinoScriptAsJobAction extends Action {
	public RunRhinoScriptAsJobAction() {
		super("Run script in separate thread");
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

		runRhinoJob(scriptString, re.getTitle());
	}

	public static boolean runRhinoJob(String scriptString, String scriptDescription) {
		final String scriptStringFinal = scriptString;
		final String title = "Javascript - " + scriptDescription;

		// show progress window		
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchPage wbPage = wb.getActiveWorkbenchWindow().getActivePage(); 
        if (wbPage != null) {
            IViewPart progressView = wbPage.findView("org.eclipse.ui.views.ProgressView");
            if (progressView == null)
            	try {
            		wbPage.showView("org.eclipse.ui.views.ProgressView");
            	} catch (PartInitException e) {
            		RhinoConsole.writeToConsole("PartInitException: " + e.getMessage());
            	}
        }
        // define the job
		Job job = new Job(title) {
			private String scriptResult = "undefined";
			protected IStatus run(IProgressMonitor monitor) {
				boolean bSuccess = true;
				// set a friendly icon and keep the job in list when it is finished
				setProperty(IProgressConstants.ICON_PROPERTY,
						Activator.getImageDescriptor("icons/png/jsfilerun.png"));			
				
				monitor.beginTask("Running Javascript...", 2);
				try {
					monitor.worked(1);
					scriptResult =
						ScriptExecution.runRhinoScript(scriptStringFinal, monitor);
					monitor.done();
				} catch (Exception e) {
					monitor.setTaskName("Error: " + e.getMessage());
					scriptResult = e.getMessage();
					bSuccess = false;
				}

				if (bSuccess == true)
					monitor.setTaskName("Javascript done.");

				if (bSuccess == false) {
					// inform user about error.news
					setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
					setProperty(IProgressConstants.ACTION_PROPERTY, JobErrorAction());
				} // ... and else, finish job imediately!

				Display.getDefault().syncExec(new Runnable() {	// do not use async, we need the GUI!
					public void run() {
						RhinoConsole.writeToConsoleBlue(scriptResult);
						RhinoConsole.writeToConsoleBlue("Javascript done.");
					}
				});
				return Status.OK_STATUS;
			}
			protected Action JobErrorAction() {
				return new Action("Javacript done") {
					public void run() {
						MessageDialog.openError(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								title,
								"The Javascript returned an error:\n" + scriptResult);
					}
				};
			}
		};
		job.setUser(true);
		job.schedule();
		return true;
	}
}