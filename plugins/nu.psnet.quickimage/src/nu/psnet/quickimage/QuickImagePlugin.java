/*
 * Software released under Common Public License (CPL) v1.0
 */
package nu.psnet.quickimage;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Per Salomonsson
 * 
 */
public class QuickImagePlugin extends AbstractUIPlugin {
	private static QuickImagePlugin plugin;
//	private ResourceBundle resourceBundle;
	public static final String PLUGIN_ID = "nu.psnet.quickimage.editors.QuickImageEditor";

	public QuickImagePlugin() {
		super();
		plugin = this;
//		try {
//			resourceBundle = ResourceBundle
//					.getBundle("nu.psnet.quickimage.QuickimagePluginResources");
//		} catch (MissingResourceException x) {
//			resourceBundle = null;
//		}
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	public static QuickImagePlugin getDefault() {
		return plugin;
	}

//	public static String getResourceString(String key) {
//		ResourceBundle bundle = QuickImagePlugin.getDefault()
//				.getResourceBundle();
//		try {
//			return (bundle != null) ? bundle.getString(key) : key;
//		} catch (MissingResourceException e) {
//			return key;
//		}
//	}
//
//	public ResourceBundle getResourceBundle() {
//		return resourceBundle;
//	}
}