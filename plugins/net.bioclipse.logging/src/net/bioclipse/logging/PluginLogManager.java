package net.bioclipse.logging;

import java.util.Enumeration;


import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.RootCategory;
import java.util.Properties; 
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;

import java.util.HashMap;
import java.util.Iterator;

/**
 * PluginLogManager
 * This class encapsulates a Log4J Hierarchy and centralizes all Logger access.
 * @author Manoel Marques
 */
public class PluginLogManager {

	private ILog log;
	private IPath stateLocation;
	private Hierarchy hierarchy;
	private HashMap hookedPlugins = new HashMap(); 
	
	private class PluginEventListener implements HierarchyEventListener {
		
		/**
		 * Called when a new appender is added for a particular level.
		 * Internally it checks if the appender is one of our custom ones
		 * and sets its custom properties. 
		 * @param category level
		 * @param appender appender added for this level
		 */
		public void addAppenderEvent(Category cat, Appender appender) {
			if (appender instanceof PluginLogAppender) {
				((PluginLogAppender)appender).setLog(log);
			}			
			if (appender instanceof PluginFileAppender) {
				((PluginFileAppender)appender).setStateLocation(stateLocation);
			}
		}
		
		/**
		 * Called when a appender is removed from for a particular level.
		 * Does nothing.
		 * @param category level
		 * @param appender appender added for this level
		 */
		public void removeAppenderEvent(Category cat, Appender appender) {
		}
	}
	
	/**
	 * Creates a new PluginLogManager. Saves the plug-in log and state location.
	 * Creates a new Hierarchy and add a new PluginEventListener to it.
	 * Configure the hierarchy with the properties passed.
	 * Add this object to the lits of acctive plug-in log managers. 
	 * @param plugin the plug-in object
	 * @param properties log configuration properties
	 */
	public PluginLogManager(Plugin plugin,Properties properties) {
		this.log = plugin.getLog();  
		this.stateLocation = plugin.getStateLocation(); 
		this.hierarchy = new Hierarchy(new RootCategory(Level.DEBUG));
		this.hierarchy.addHierarchyEventListener(new PluginEventListener());
		new PropertyConfigurator().doConfigure(properties,this.hierarchy);	
		Activator.getDefault().addLogManager(this); 
	}

	public PluginLogManager(ILog lg, IPath pth,Properties properties) {
		
		this.log = lg;  
		this.stateLocation = pth; 
		this.hierarchy = new Hierarchy(new RootCategory(Level.DEBUG));
		this.hierarchy.addHierarchyEventListener(new PluginEventListener());
		new PropertyConfigurator().doConfigure(properties,this.hierarchy);	
		Activator.getDefault().addLogManager(this); 
	}

	
	/**
	 * Hooks a plug-in into this PluginLogManager. When the hooked plug-in uses the
	 * Eclipse log API, it will be channeled to this logging framework.
	 * @param id logger name (usually the the plug-in id)
	 * @param log plug-in log
	 */
	@SuppressWarnings("unchecked")
	public boolean hookPlugin(String id, ILog log) {
		synchronized(this.hookedPlugins) {
			if (log == null || id == null || this.hookedPlugins.containsKey(id))
				return false;
				
			PluginLogListener listener = new PluginLogListener(log,getLogger(id));
			this.hookedPlugins.put(id,listener);
		}		
		return true;
	}

	/**
	 * Unhooks a plug-in from this PluginLogManager. The Eclipse log API
	 * won't be channeled to this logging framework anymore.
	 * @param id logger name (usually the the plug-in id)
	 */
	public boolean unHookPlugin(String id) {
		synchronized(this.hookedPlugins) {
			if (id == null || !this.hookedPlugins.containsKey(id))
				return false;
					
			PluginLogListener listener = (PluginLogListener) this.hookedPlugins.get(id);
			listener.dispose(); 
			this.hookedPlugins.remove(id);
		}		
		return true;
	}
	
	/**
	 * Checks if this PluginLogManager is disabled for this level.
	 * @param level level value
	 * @return boolean true if it is disabled
	 */
	public boolean isDisabled(int level) {
		return this.hierarchy.isDisabled(level);
	}
	
	/**
	 * Enable logging for logging requests with level l or higher.
	 * By default all levels are enabled.
	 * @param level level object
	 */
	public void setThreshold(Level level) {
		this.hierarchy.setThreshold(level);
	}
	
	/**
	 * The string version of setThreshold(Level level)
	 * @param level level string
	 */
	public void setThreshold(String level) {
		this.hierarchy.setThreshold(level);
	}

	/**
	 * Get the repository-wide threshold.
	 * @return Level
	 */
	public Level getThreshold() {
		return this.hierarchy.getThreshold();
	}

	/**
	 * Returns a new logger instance named as the first parameter
	 * using the default factory. If a logger of that name already exists,
	 * then it will be returned. Otherwise, a new logger will be instantiated 
	 * and then linked with its existing ancestors as well as children.
	 * @param name logger name
	 * @return Logger
	 */
	public Logger getLogger(String name) {
		return this.hierarchy.getLogger(name);
	}
	
	/**
	 * The same as getLogger(String name) but using a factory instance instead of
	 * a default factory.
	 * @param name logger name
	 * @param factory factory instance 
	 * @return Logger
	 */
	public Logger getLogger(String name, LoggerFactory factory) {
		return this.hierarchy.getLogger(name,factory);
	}

	/**
	 * Returns the root of this hierarchy.
	 * @return Logger
	 */
	public Logger getRootLogger() {
		return this.hierarchy.getRootLogger();
	}

	/**
	 * Checks if this logger exists.
	 * @return Logger
	 */
	public Logger exists(String name) {
		return this.hierarchy.exists(name);
	}
	
	/**
	 * Removes appenders and disposes the logger hierarchy
	 *
	 */
	public void shutdown() {
		internalShutdown();
		Activator.getDefault().removeLogManager(this); 
	}
	
	/**
	 * Used by LoggingPlugin to shutdown without removing it from the LoggingPlugin list
	 *
	 */
	public void internalShutdown() {
		synchronized(this.hookedPlugins) {
			Iterator it = this.hookedPlugins.keySet().iterator();
			while (it.hasNext()) {
				String id = (String) it.next(); 
				PluginLogListener listener = (PluginLogListener) this.hookedPlugins.get(id);
				listener.dispose(); 
			}
			this.hookedPlugins.clear(); 
		}	
		this.hierarchy.shutdown();
	}
	
	/**
	 * Returns all the loggers in this manager.
	 * @return Enumeration logger enumeration
	 */
	public Enumeration getCurrentLoggers() {
		return this.hierarchy.getCurrentLoggers();
	}

	/**
	 * Resets configuration values to its defaults.
	 * 
	 */
	public void resetConfiguration() {
		this.hierarchy.resetConfiguration();
	}
}