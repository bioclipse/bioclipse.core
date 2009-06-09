package net.bioclipse.ui.business;

import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
	
  // Virtual project
  public static final String VIRTUAL_PROJECT_NAME = "Virtual";

  private static final Logger logger = Logger.getLogger(Activator.class);

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
    
  deleteVirtualProject();

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
  
  protected static void  deleteVirtualProject(){
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      IProject project = root.getProject(VIRTUAL_PROJECT_NAME);
      try {
          project.delete(true, null);
      } catch (CoreException e) {            
          LogUtils.debugTrace( logger, e );
      }
  }

}
