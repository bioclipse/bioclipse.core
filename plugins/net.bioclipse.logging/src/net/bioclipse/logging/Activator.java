package net.bioclipse.logging;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.logging";

	// The shared instance
	private static Activator plugin;

	//Hold the log managers contributed by plugins
	private List<PluginLogManager> logManagers
		= new ArrayList<PluginLogManager>();

	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		synchronized (this.logManagers) {
			for ( PluginLogManager logManager : this.logManagers )
				logManager.internalShutdown(); 

			this.logManagers.clear(); 
		}
		
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	
	/**
	 * Adds a log manager object to the list of active log managers
	 */	
	@SuppressWarnings("unchecked") public void addLogManager(PluginLogManager logManager) {
		synchronized (this.logManagers) {
			if (logManager != null)
				this.logManagers.add(logManager); 
		}
	}
	
	/**
	 * Removes a log manager object from the list of active log managers
	 */
	public void removeLogManager(PluginLogManager logManager) {
		synchronized (this.logManagers) {
			if (logManager != null)
				this.logManagers.remove(logManager); 
		}
	}
	
}
