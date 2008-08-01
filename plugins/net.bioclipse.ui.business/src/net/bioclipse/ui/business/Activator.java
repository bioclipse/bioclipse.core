package net.bioclipse.ui.business;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.ui.business";

	// The shared instance
	private static Activator plugin;
	
  private ServiceTracker finderTracker;
	
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
		
    finderTracker = new ServiceTracker(context,
                                       IUIManager.class.getName(),
                                       null);
    finderTracker.open();
	}

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

  public Object getUIManager() {
      IUIManager uiManager;
      
      try {
          uiManager
              = (IUIManager) finderTracker.waitForService(1000*30);
      }
      catch (InterruptedException e) {
          throw
            new IllegalStateException("Could not get js console manager", e);
      }
      if (uiManager == null) {
          throw new IllegalStateException("Could not get js console manager");
      }
      return uiManager;
  }
  
}
