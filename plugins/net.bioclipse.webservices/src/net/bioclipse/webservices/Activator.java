package net.bioclipse.webservices;

/**
 * 
 * Wraps Webservices into an eclipse-plugin.
 * 
 * @author ola, edrin
 *
 */

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.plugins.bc_webservices.scripts.IWebservicesManager;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator extends AbstractUIPlugin {
	
	private static final Logger logger = Logger.getLogger(Activator.class);
	
	// The plug-in ID
	private static final String PLUGIN_ID="net.bioclipse.webservices";

	// The shared instance.
	private static Activator plugin;
		
    //For Spring
    private ServiceTracker finderTracker;
	
	/**
	 * The constructor.
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
        finderTracker = new ServiceTracker( context, 
                IWebservicesManager.class.getName(), 
                null );
        
        finderTracker.open();	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
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
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

    public IWebservicesManager getWebservicesManager() {
    	IWebservicesManager manager = null;
        try {
            manager = (IWebservicesManager) finderTracker.waitForService(1000*10);
        } catch (InterruptedException e) {
            LogUtils.debugTrace(logger, e);
        }
        if(manager == null) {
            throw new IllegalStateException("Could not get the webservices manager");
        }
        return manager;
    }
}