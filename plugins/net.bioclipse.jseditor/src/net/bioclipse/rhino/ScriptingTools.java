package net.bioclipse.rhino;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
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
 * @author Johannes Wagener
 */
public class ScriptingTools {
	
	private IProgressMonitor monitor = null;
	
	/**
	 * Constructor
	 */
	public ScriptingTools() {
	}
	
	/**
	 * Constructor for threaded scripts
	 * @param monitor The monitor, used to cancel a script
	 */
	public ScriptingTools(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
	
	/**
	 * Prints to the Rhino Javascript Console.
	 * @return true if the threaded script was canceled by the user, false otherwise 
	 */
	public void print(String text) {
		RhinoConsole.writeToConsole(text);
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
	public void showMessage(String title, String message) {
		MessageDialog.openInformation(
				getShell(),
				title,
				message);
	}

	/**
	 * Shows a message box. Please consider that this function requires special care when
	 * used in a threaded script.
	 * (Warps: MessageDialog.openInformation(...);)
	 * @param message The message within the message box window
	 */
	public void showMessage(String message) {
		MessageDialog.openInformation(
				getShell(),
				"Bioclipse Javascript",
				message);
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
	 * useful for scripts that are run in separate threads.
	 * (Please refere to the Java documentation or the sample scripts to learn more about Runnables)
	 * @param runnable The runnable
	 */
	public void syncExec(Runnable runnable) {
		// do not use async, we need the GUI!
		Display.getDefault().syncExec(runnable);
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
		JarClasspathLoader.addFile(name);
	}
}
