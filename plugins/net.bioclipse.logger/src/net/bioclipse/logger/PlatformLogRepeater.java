/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Richard Klancer - total rewrite of com.tools.logging.PluginLogListener
 *     Manoel Marques - author of com.tools.logging.PluginLogListener
 *     
 *     rpk@pobox.com 3/29/08
 *     
 ******************************************************************************/
package net.bioclipse.logger;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Richard Klancer rpk@pobox.com
 *
 */
public class PlatformLogRepeater implements ILogListener {

    /**
     * Called when an IStatus object has been written to the eclipse platform 
     * log. Translates the status instance to a log4j Level and sends to the
     * log4j logger for the plugin that logged the IStatus. <br />
     * <br />
     * If the status contains a exception, writes the exception stack trace
     * to the logger at the debug level.<br />
     * <br />
     * If the IStatus object is a MultiStatus object with child IStatuses,
     * the procedure above is repeated for each child, recursively.<br />
     * <br />
     * Translates Status severity to Level according to the following rules:
     * Status.ERROR -> Level.ERROR <br />
     * Status.WARNING -> Level.WARN <br />
     * Status.CANCEL -> Level.WARN (we don't know how severe a given cancellation is) <br />
     * Status.INFO -> Level.INFO <br />
     * default -> Level.DEBUG <br />
     * 
     * @param status IStatus object being written to platform log
     * @param bogusPluginName not the symbolic name of plug-in that actually created said object
     */ 
    public void logging(IStatus status, String bogusPluginName) {
        // org.eclipse.core.runtime passes its own plugin id when forwarding 
        // logging events, instead of extracting the name of the plugin that 
        // generated the status
        log(status);
    }

    /* Log an IStatus object to the log4j log. If status contains an
     * embedded exception, write its stack trace to the logger at the debug
     * level. 
     * 
     * Status objects can be "multistatus" objects with multiple children.
     * This function calls itself recursively to log all child statuses to
     * arbitrary depth.
     */
    
    private static void log(IStatus status) {
        log(status, new HashSet<IStatus>());
    }

    /* log function augmented with the set of multistatus objects previously
     * seen in this call chain, to defend against infinite looping on any 
     * possible cycles in MultiStatus objects passed to us */
    
    private static void log(IStatus status, Set<IStatus> seenBefore) {

        final Logger logger;
        final String pluginName;
        
        // defend against cycles in MultiStatus object
        if (status == null || seenBefore.contains(status))
            return;
        seenBefore.add(status);

        // get logger (using plugin id as logger name, rather than fully qualified class name)
        pluginName = status.getPlugin();
        if (pluginName == null || pluginName.trim() == "")
            logger = Logger.getRootLogger();
        else
            logger = Logger.getLogger(pluginName);
        
        // log the Status
        logger.log(log4jLevelFrom(status), status.getMessage());
        
        // log the stack trace
        Throwable t = status.getException();
        if (t != null) logger.debug(Activator.traceStringFrom(t));

        // log children, if this object is a multistatus
        for (IStatus child : status.getChildren()) {
            log(child, seenBefore);
        }
    }
    
    /* Roughly translates an Eclipse IStatus severity to a log4j Level.
     * 
     * Note that the semantics of status objects and log4j levels are different,
     * in that log4j levels apply to *messages* and status severities pertain
     * to the *result of an operation*, which is what an IStatus object
     * actually represents.
     * 
     * Note that this formally applies only to subclasses of Status. It is
     * possible for a plugin to define an IStatus implementation that is 
     * not a subclass of Status, and it is encouraged that they define severity
     * codes relevant to their own semantics.
     *
     * Therefore the values returned by getSeverity() are not limited to the
     * cases below. Such unknown severity codes are translated to Level.DEBUG. */
    
    private static Level log4jLevelFrom(IStatus status) {    
        switch (status.getSeverity()) {
            case Status.ERROR:
                return Level.ERROR;
            case Status.WARNING:
            case Status.CANCEL:
                return Level.WARN;
            case Status.INFO:
                return Level.INFO;
            case Status.OK:
                return Level.DEBUG;
            default:
                return Level.DEBUG;
        }
    }
}
