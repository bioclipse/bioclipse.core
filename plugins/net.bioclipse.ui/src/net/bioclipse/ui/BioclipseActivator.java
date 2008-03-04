package net.bioclipse.ui;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.bioclipse.logging.PluginLogManager;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The BioclipseActivator is a class that can be extended by all Bioclispe 
 * plugins in order to simplify logging configuration. 
 * Requires the file: "logger.properties" for log4j in root of plugin.
 * @author ola
 *
 */
public abstract class BioclipseActivator extends AbstractUIPlugin {

	private Logger logger;

	// The shared instance.
	private static BioclipseActivator plugin;

	private PluginLogManager logManager;
	
	public static PluginLogManager getLogManager() {
		return getDefault().logManager;
	}

	/**
	 * The constructor.
	 */
	public BioclipseActivator() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		String[] currargs=Platform.getCommandLineArgs();

		for (int i=0;i<currargs.length;i++){
//			System.out.println("Detected argument "+ i + ": " + currargs[i]);
			//TODO: handle arguments for Bioclipse here
		}

		if (!(System.getProperty("java.version").startsWith("1.5")) &&
			!(System.getProperty("java.version").startsWith("1.6"))) {
			System.out.println("** Bioclipse startup FAILED **");
			System.out.println("Bioclipse must be run with Java 1.5 (sometimes referred to as 5.0) or better.");
			System.out.println("If you have multiple versions of Java installed, please edit the file 'bioclipse.ini' to point to java 1.5 or 1.6 by adding a line like below: ");
			System.out.println(" -vm /path/to/java1.5/bin/java");
			System.exit(0);
		}

		configureLogger();
		logger=getLogManager().getLogger(getDefault().getClass().toString());
		logger.debug("Configured logging in plugin: " + getDefault().toString());
		
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		if (this.logManager != null) {
			this.logManager.shutdown();
			this.logManager = null;
		}
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static BioclipseActivator getDefault() {
		return plugin;
	}

	
	private void configureLogger() {

		if (logManager!=null) return;
		
		try {
			URL url=getLoggerURL();
			URL newURL=FileLocator.toFileURL(url);

			File file=new File(newURL.getFile());
			if (!(file.exists())){
				System.out.println("File: " + file + " does not exist. Logging " +
						"not active in plugin: "+ this.getClass().toString());
				return;
			}
			
			
			InputStream propertiesInputStream = newURL.openStream();
			if (propertiesInputStream != null) {
				Properties props = new Properties();
				props.load(propertiesInputStream);
				propertiesInputStream.close();
				this.logManager = new PluginLogManager(this, props);
				// this.logManager.hookPlugin(
				// TestPlugin.getDefault().getBundle().getSymbolicName(),
				// TestPlugin.getDefault().getLog());
			}
		} catch (Exception e) {
			String message = "Error while initializing log properties: "
					+ e.getMessage();
			
			System.out.println(message);
			
			IStatus status = new Status(IStatus.ERROR, getDefault().getBundle()
					.getSymbolicName(), IStatus.ERROR, message, e);
			getLog().log(status);
			throw new RuntimeException(
					"Error while initializing log properties.", e);
		}
	}

	public abstract URL getLoggerURL();
}
