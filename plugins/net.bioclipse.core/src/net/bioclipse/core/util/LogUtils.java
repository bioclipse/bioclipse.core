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

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

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
     * This method handles an exception by showing a popup, logging and printing the stack trace.
     * 
     * @param ex The exception to handle.
     * @param logger The logger to use.
     */
    public static void handleException(Exception ex, Logger logger){
 		StringWriter strWr = new StringWriter();
 		PrintWriter prWr = new PrintWriter(strWr);
 		ex.printStackTrace(prWr);
 		if(logger!=null){
 			logger.error(strWr.toString());
 			debugTrace(logger, ex);
 		}
 		ex.printStackTrace();
 		MessageDialog.openError(new Shell(), "Unexpected error", "An unexpected error occorued. Bioclipse has no idea how to handle this. The message is: "+ex.getMessage()+". If you do not know what to do, report this to the Bioclipse team. A stack trace has been written to the log file to be included in your report.");
    }
}
