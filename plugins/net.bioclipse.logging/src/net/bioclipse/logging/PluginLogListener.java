package net.bioclipse.logging;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ILog;

/**
 * PluginLogListener
 * This class is responsible for adding itself to the plug-in logging framework
 * and translating plug-in log requests to Logger events.
 * @author Manoel Marques
 */
class PluginLogListener implements ILogListener {

	private ILog log;
	private Logger logger;
	
	/**
	 * Creates a new PluginLogListener. Saves the plug-in log and logger instance.
	 * Adds itself to the plug-in log.
	 * @param plugin the plug-in object
	 * @param logger logger instance
	 */
	PluginLogListener(ILog log,Logger logger) {
		this.log = log;
		this.logger = logger;
		log.addLogListener(this);
	}
	
	/**
	 * Removes itself from the plug-in log, reset instance variables.
	 */	
	void dispose() {
		if (this.log != null) {
			this.log.removeLogListener(this);
			this.log = null;
			this.logger = null;
		}
	}

	/**
	 * Log event happened.
	 * Translates status instance to Logger level and message.
	 * Status.ERROR - Level.ERROR
	 * Status.WARNING - Level.WARN
	 * Status.INFO - Level.INFO
	 * Status.CANCEL - Level.FATAL
	 * default - Level.DEBUG
	 * @param status Log Status
	 * @param plugin plug-in id
	 */	
	public void logging(IStatus status, String plugin) {
		if (null == this.logger || null == status) 
			return;
		
		int severity = status.getSeverity();
		Level level = Level.DEBUG;  
		if (severity == Status.ERROR)
			level = Level.ERROR;
		else
		if (severity == Status.WARNING)
			level = Level.WARN;
		else
		if (severity == Status.INFO)
			level = Level.INFO;
		else
		if (severity == Status.CANCEL)
			level = Level.FATAL;

		plugin = formatText(plugin);
		String statusPlugin = formatText(status.getPlugin());
		String statusMessage = formatText(status.getMessage());
	    StringBuffer message = new StringBuffer();
		if (plugin != null) {
		    message.append(plugin);
			message.append(" - ");
		}    
		if (statusPlugin != null &&
		    (plugin == null || !statusPlugin.equals(plugin))) {
		    message.append(statusPlugin);
		   	message.append(" - ");
		}	
		message.append(status.getCode());
		if (statusMessage != null) {
		    message.append(" - ");
		    message.append(statusMessage);
		}   				
		this.logger.log(level,message.toString(),status.getException());		
	}
	
	static private String formatText(String text) {
	    if (text != null) {
	        text = text.trim();
		    if (text.length() == 0) return null;
		} 
	    return text;
	}
}