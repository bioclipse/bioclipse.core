package net.bioclipse.jsexecution.execution;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.jsexecution.Activator;
import net.bioclipse.jsexecution.exceptions.ScriptException;
import net.bioclipse.jsexecution.execution.helper.ThreadSafeConsoleWrap;
import net.bioclipse.jsexecution.tools.ScriptingTools;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.progress.IProgressConstants;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/*
 * This file is part of the Bioclipse JsExecution Plug-in.
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
public class ScriptExecution {
	
	/*
	 * This is the only function required by the JsEditor Plug-in
	 * The editor Plug-In passes
	 * The script as a String
	 * The filename of the script
	 * A (derived) class/object of MessageConsole that must show
	 * the stuff associated with the newly spawned js Context.
	 */
	
	public static void runRhinoScript(String scriptString,
			String scriptDescription,
			MessageConsole parent_console) {
		
		// The passed console is wrapped with a Thread Safe!! API, thus it is possible
		// to use it from non GUI thread.
		// Beside this the wrap supports simple methods to print with different colors
		ThreadSafeConsoleWrap console = new ThreadSafeConsoleWrap(parent_console);
		
		// now run the script in a JOB
		runRhinoScriptAsJob(scriptString, scriptDescription, console);
	}
	
	/*
	 * This function prepares a Job that harbors the script when running.
	 * TODO: add the code to pass the monitor to spring that it can cancel the script on manager calls.
	 */
	
	private static void runRhinoScriptAsJob(String scriptString,
			String scriptDescription,
			final ThreadSafeConsoleWrap console) {
		
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
            		console.writeToConsole("PartInitException: " + e.getMessage());
            	}
        }
        // define the job
		Job job = new Job(title) {
			private String scriptResult = "undefined";
			protected IStatus run(IProgressMonitor monitor) {
				boolean bSuccess = true;
				// set a friendly icon and keep the job in list when it is finished
				/*setProperty(IProgressConstants.ICON_PROPERTY,
						Activator.getImageDescriptor("icons/png/jsfilerun.png"));*/
				
				monitor.beginTask("Running Javascript...", 2);
				try {
					monitor.worked(1);
					scriptResult =
						runRhinoScript(scriptStringFinal, console, monitor);
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
						console.writeToConsole(scriptResult);
						console.writeToConsoleBlue("Javascript done.");
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
	}

	/*
	 * This is the method that actually runs the script. It collects the managers and pushes them
	 * in the newly created js context. One context per script execution.
	 * 
	 * Beside this it creates another object in the context
	 * that provides some helper functions located in
	 * 
	 * net.bioclipse.rhino.tools.ScriptingTools.java
	 * 
	 * used to pop up a message box, or to make a script sleep for some ms,
	 * or to run a runnable in the GUI context.
	 * 
	 * There is also one helper function that can be used to load an external .jar into the IDE.
	 * ( it uses net.bioclipse.rhino.tools.JarClasspathLoader.java )
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	private static String runRhinoScript(String scriptString,
			ThreadSafeConsoleWrap console,
			IProgressMonitor monitor) throws ScriptException {
		String scriptResult = "Invalid result.";
		// DO THE ACTUAL EXECUTION OF THE SCRIPT

		if (!ContextFactory.hasExplicitGlobal()) {
			ContextFactory.initGlobal(new ContextFactory());
			// THIS IS VERY IMPORTANT!!!
			ContextFactory.getGlobal().initApplicationClassLoader(
					Activator.class.getClassLoader());
		}
		Context cx = ContextFactory.getGlobal().enterContext();
		
		if (cx == null) {
			return "Could not create context.";
		}

		try {
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			ScriptingTools tools;
			tools = new ScriptingTools(console, monitor);
			
			Object wrappedOut = Context.javaToJS(tools, scope);
			ScriptableObject.putProperty(scope, "jst", wrappedOut);

			// also add all managers
			List<Object> managers = Activator.getManagers();
			if (managers != null && managers.size() > 0) {
				Iterator<Object> it = managers.iterator();
				while (it.hasNext() == true) {
					Object object = it.next();
					
					Class managerclass = object.getClass();
					// access the method in this ugly way however it is not protected...
					Method method = managerclass.getDeclaredMethod("getNamespace", new Class[0]);
					//method.setAccessible(true);
					Object managerName = (String)method.invoke(object);
					if (managerName instanceof String) {
						wrappedOut = Context.javaToJS(object, scope);
						ScriptableObject.putProperty(scope, (String)managerName, wrappedOut);
					}
				}
			}
			
			// Now evaluate the string we've colected.
			Object ev = cx.evaluateString(scope, scriptString, "line: ", 1, null);

			// Convert the result to a string and print it.
			scriptResult = Context.toString(ev);
		} catch (Exception e){
			throw new ScriptException(e);
		} finally {
			// Exit from the context.
			Context.exit();
		}

		return scriptResult;
	}
}