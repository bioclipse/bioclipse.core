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
  private ServiceTracker jsFinderTracker;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
    finderTracker = new ServiceTracker( context,
                                        IUIManager.class.getName(),
                                        null );
    finderTracker.open();
    
    jsFinderTracker = new ServiceTracker( context,
                                          IJSUIManager.class.getName(),
                                          null );
    jsFinderTracker.open();
	}

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

  public IUIManager getUIManager() {
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
  
  public IUIManager getJSUIManager() {
      IJSUIManager jsuiManager;
      
      try {
          jsuiManager
              = (IJSUIManager) jsFinderTracker.waitForService(1000*30);
      }
      catch (InterruptedException e) {
          throw
            new IllegalStateException("Could not get js console manager", e);
      }
      if (jsuiManager == null) {
          throw new IllegalStateException("Could not get js console manager");
      }
      return jsuiManager;
  }
}
