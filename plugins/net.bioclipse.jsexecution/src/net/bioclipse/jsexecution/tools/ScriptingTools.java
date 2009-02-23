package net.bioclipse.jsexecution.tools;

import net.bioclipse.jsexecution.execution.helper.ThreadSafeConsoleWrap;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
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
public class ScriptingTools {
	
	private IProgressMonitor monitor = null;
	private ThreadSafeConsoleWrap console = null;
	
	/**
	 * Constructor for threaded scripts
	 * @param monitor The monitor, used to cancel a script
	 */
	public ScriptingTools(ThreadSafeConsoleWrap console, IProgressMonitor monitor) {
		this.console = console;
		this.monitor = monitor;
	}
	
	/**
	 * Prints to the Rhino Javascript Console.
	 * @return true if the threaded script was canceled by the user, false otherwise 
	 */
	public void print(String text) {
		console.writeToConsole(text);
	}
	
	/**
	 * Checks if the threaded script was canceled by the user. This function is only
	 * useful for scripts that are run in separate threads. It uses the monitor that was
	 * passed to the constructor of threaded scripts.
	 * @return true if the threaded script was canceled by the user, false otherwise 
	 */
	public boolean isCanceled() {
		if (monitor == null)	// always return false if not monitor is available
			return false;

		return monitor.isCanceled();
	}

	/**
	 * Shows a message box. Please consider that this function requires special care when
	 * used in a threaded script.
	 * (Warps: MessageDialog.openInformation(...);)
	 * @param title The title of the message box window
	 * @param message The message within the message box window
	 */
	public void showMessage(final String title, final String message) {
		Runnable r = new Runnable() {
            public void run() {
            	MessageDialog.openInformation(
        				getShell(),
        				title,
        				message);
            }
		};
		Display.getDefault().asyncExec(r);
	}

	/**
	 * Shows a message box. Please consider that this function requires special care when
	 * used in a threaded script.
	 * (Warps: MessageDialog.openInformation(...);)
	 * @param message The message within the message box window
	 */
	public void showMessage(final String message) {
		Runnable r = new Runnable() {
            public void run() {
            	MessageDialog.openInformation(
        				getShell(),
        				"Bioclipse Javascript",
        				message);
            }
		};
		Display.getDefault().asyncExec(r);
	}
	
	/**
	 * Causes the script's thread to sleep for the specified amount of milliseconds.
	 * (Warps: Thread.sleep(ms);)
	 * @param ms Milliseconds the thread should sleep
	 */
	public void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Returns Bioclipse's shell.
	 * (Warps: PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();)
	 * @return The shell
	 */
	public Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
	
	/**
	 * Executes the specified Runnable within Bioclipse's main thread. This function is only
	 * useful for scripts that are run in separate threads. Please refer to the eclipse documentation
	 * to learn more about asyncExec() and asyncExec().
	 * (Warps: Display.getDefault().syncExec(runnable);)
	 * @param runnable The runnable
	 */
	public void syncExec(Runnable runnable) {
		// do not use async, we need the GUI!
		Display.getDefault().syncExec(runnable);
	}
	
	/**
	 * Executes the specified Runnable within Bioclipse's main thread. This function is only
	 * useful for scripts that are run in separate threads. Please refer to the eclipse documentation
	 * to learn more about asyncExec() and asyncExec().
	 * (Warps: Display.getDefault().asyncExec(runnable);)
	 * @param runnable The runnable
	 */
	public void asyncExec(Runnable runnable) {
		Display.getDefault().asyncExec(runnable);
	}
	
	/**
	 * Converts the specified string to integer.
	 * (Warps: Integer.parseInt(string);) 
	 * @param string The string
	 * @return The integer
	 */
	public int string2int(String string) {
		return Integer.parseInt(string);
	}
	
	/**
	 * Converts the specified string to integer.
	 * (Warps: new Integer(i).toString();) 
	 * @param i The integer
	 * @return The string
	 */
	public String int2string(int i) {
		return new Integer(i).toString();
	}
	
	/**
	 * Loads the specified Java library to Bioclipse's classpath. The Java library can either
	 * be a java archive (.jar file) or a package directory that contains compiled java code 
	 * (.class files).
	 * Example: c:\\theDirectory\\myArchive.jar
	 * @param name The filename of the java archive or the package directory.
	 */
	public void load(String name) {
		JarClasspathLoader.addFile(name, console);
	}
}
