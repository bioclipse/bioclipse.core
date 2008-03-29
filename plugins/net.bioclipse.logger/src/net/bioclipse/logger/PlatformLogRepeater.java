/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Manoel Marques - initial author of com.tools.logging.PluginLogListener
 *     Richard Klancer - adaptation of com.tools.logging.PluginLogListener code
 *
 *     rpk@pobox.com 3/29/08
 *     
 ******************************************************************************/
package net.bioclipse.logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Platform;


/**
 * @author Manoel Marques
 * @author Richard Klancer rpk@pobox.com
 *
 */
public class PlatformLogRepeater implements ILogListener {
    
    private static PlatformLogRepeater singleton;
    private static final Logger repeaterClassLogger = 
        Logger.getLogger(PlatformLogRepeater.class);
    
    
    /**
     * Creates the PlatformLogRepeater singleton and adds it to the list
     * of listeners to the platform log.
     * 
     * @param plugin the plug-in object
     * @param logger logger instance
     */
    PlatformLogRepeater() {
        
        assert singleton == null: 
            "PlatformLogRepeater singleton constructor called twice.";
        if (singleton != null) {
            repeaterClassLogger.warn(
                    "PlatformLogRepeater singleton constructor called twice.");
            return;
        }
        
        singleton = this;
        repeaterClassLogger.debug("Created PlatformLogRepeater singleton.");
        
        Platform.addLogListener(singleton);
        repeaterClassLogger.debug("Registered PlatformLogRepeater as log listener.");
    }
    
    /**
     * Removes itself from the plug-in log, reset instance variables.
     */ 
    void dispose() {
        assert singleton != null: 
            "dispose() called on PlatformLogRepeater, but singleton is null";
        
        if (singleton != null) {
            Platform.removeLogListener(singleton);
        }
    }
    
    /**
     * A Log event happened on the eclipse platform log.
     * Translates the status instance to a log4j level and sends to a log4j 
     * logger for that plugin.
     * 
     * Status.ERROR -> Level.ERROR
     * Status.WARNING -> Level.WARN
     * Status.CANCEL -> Level.WARN (we don't know how severe a given cancellation is)
     * Status.INFO -> Level.INFO
     * default -> Level.DEBUG
     * 
     * @param status Status object being written to platform log
     * @param pluginName symbolic name of plug-in writing said Status object to log
     */ 
    public void logging(IStatus status, String pluginName) {
        
        if (status == null)
            return;
        
        logOneStatus(status);
        
        for (IStatus child : status.getChildren()) {
            logOneStatus(child);
        }
    }

    private void logOneStatus(IStatus status) {
        String pluginName = status.getPlugin();
        
        Logger logger = Logger.getLogger(pluginName);
        logger.log(log4jLevelFrom(status), status.getMessage());
        
        Throwable t = status.getException();
        if (t != null) 
            logger.debug(Activator.traceStringFrom(t));
    }
    
    private Level log4jLevelFrom(IStatus status) {    
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
