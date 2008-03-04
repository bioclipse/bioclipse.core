package net.bioclipse.pkpd;

import java.net.URL;

import net.bioclipse.ui.BioclipseActivator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends BioclipseActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.pkpd";

	//The file for logger properties
	private static final String LOG_PROPERTIES_FILE = "logger.properties";

	//Colors for the Forms
	private FormColors formColors;

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Need to provide this plugin's logger.properties to abstract class
	 */
	@Override
	public URL getLoggerURL() {
		return getBundle().getEntry("/" + LOG_PROPERTIES_FILE);
	}
	
	public FormColors getFormColors(Display display) {
		if (formColors == null) {
			formColors = new FormColors(display);
			formColors.markShared();
		}
		return formColors;
	}

}