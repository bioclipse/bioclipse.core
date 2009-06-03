/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
/**
 * 
 */
package net.bioclipse.core.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * LogUtils:
 *   Utility functions for working with loggers.
 *   
 * @author rklancer
 *
 */
public class LogUtils {

    /** Returns a printable stack trace from a Throwable
     * 
     * @param t
     *            the Throwable
     * @return String with stack trace information from t
     */
    public static String traceStringOf(Throwable t) {
        // Java ... world heavyweight champion of annoying verbosity
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter traceWriter = new PrintWriter(out);
        t.printStackTrace(traceWriter);
        traceWriter.flush();
        return out.toString();
    }
    
    
    /** Prints stack trace from a Throwable to provided Logger
     * 
     * @param logger
     *           the Logger to which to output the stack trace from t
     * @param t
     *           the Throwable
     */
    public static void debugTrace(Logger logger, Throwable t) {
        // constructing stack trace string is expensive, so check isDebugEnabled
        if (logger.isDebugEnabled()) {
            logger.debug(traceStringOf(t));
        }
    }
    
    /**
     * This method handles an exception by showing a popup, 
     * logging and printing the stack trace.
     * 
     * @param ex The exception to handle.
     * @param logger The logger to use.
     * @deprecated replaced by 
     *             {@link #handleException(Exception, Logger, String)}
     */
    @Deprecated 
    public static void handleException(final Exception ex, Logger logger) {
        handleException( ex, logger, "unknown" );
    }
    
    /**
     * This method handles an exception by showing a popup, 
     * logging and printing the stack trace.
     * 
     * @param ex The exception to handle.
     * @param logger The logger to use.
     * @param pluginId The name of the plugin the exception occured in.
     */
    public static void handleException( final Exception ex, 
                                        Logger logger, 
                                        final String pluginId ) {
     		StringWriter strWr = new StringWriter();
     		PrintWriter prWr = new PrintWriter(strWr);
     		ex.printStackTrace(prWr);
     		if(logger!=null){
     			logger.error(strWr.toString());
     			debugTrace(logger, ex);
     		}
     		ex.printStackTrace();
     		Display.getDefault().asyncExec( new Runnable() {
    
            public void run() {
                ErrorDialog.openError( 
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                             .getShell(), 
                    "Unexpected error", 
                      "An unexpected error occured. Bioclipse has no idea " +
                      "how to handle this. If you would like to report this " +
                      "to the Bioclipse team a stack trace has been written " +
                      "to the log file ( " + 
                      net.bioclipse.logger.Activator.getActualLogFileName() +
                      ") that you can include in an email to " +
                      "bioclipse-devel@lists.sourceforge.net where you " +
                      "explain what you where doing and what went wrong. The " +
                      "more information about the problem you write the " +
                      "easier it is for the developer to fix your problem.", 
                    new Status( IStatus.ERROR, 
                                pluginId == null ? "unknown" : pluginId, 
                                ex.getClass().getSimpleName() 
                                + ": " + ex.getMessage(), 
                    ex ) );
            }
     		} );
    }
}
