/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.ui;

/**
 *
 * Tools for simplified Rhino scripting
 *
 * @author edrin, ola
 *
 */

import net.bioclipse.scripting.INamespaceProvider;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class ScriptingTools implements INamespaceProvider{

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
     * Checks if the threaded script was canceled by the user. This function is only
     * useful for scripts that are run in separate threads. It uses the monitor that was
     * passed to the constructor of threaded scripts.
     * @return true if the threaded script was canceled by the user, false otherwise
     */
    public boolean isCanceled() {
        if (monitor == null)    // always return false if not monitor is available
            return false;

        return monitor.isCanceled();
    }

    /**
     * Shows a message box. Please consider that this function requires special care when
     * used in a threaded script.
     * (Wraps: MessageDialog.openInformation(...);)
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
     * (Wraps: MessageDialog.openInformation(...);)
     * @param message The message within the message box window
     */
    public void showMessage(String message) {
        MessageDialog.openInformation(
                getShell(),
                "Bioclipse Rhino Script",
                message);
    }

    /**
     * Causes the script's thread to sleep for the specified amount of milliseconds.
     * (Wraps: Thread.sleep(ms);)
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
     * (Wraps: PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();)
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
        Display.getDefault().syncExec(runnable);
    }

    /**
     * Converts the specified string to integer.
     * (Wraps: Integer.parseInt(string);)
     * @param string The string
     * @return The integer
     */
    public int string2int(String string) {
        return Integer.parseInt(string);
    }

    /**
     * Converts the specified string to integer.
     * (Wraps: new Integer(i).toString();)
     * @param i The integer
     * @return The string
     */
    public String int2string(int i) {
        return new Integer(i).toString();
    }
}
