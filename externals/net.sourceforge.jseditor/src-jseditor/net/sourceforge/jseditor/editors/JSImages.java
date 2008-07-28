/*
 * $RCSfile: JSImages.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSImages.java,v $
 * Revision 1.1  2003/05/28 15:17:11  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import net.sourceforge.jseditor.JSEditorPlugin;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Convenience class for storing references to image descriptors used by the JS editor.
 * 
 * @author Addi
 */
public class JSImages
{
	static final URL BASE_URL = JSEditorPlugin.getDefault().getDescriptor().getInstallURL();
	static final ImageDescriptor ICON_EDITOR;
	static final ImageDescriptor ICON_VIEW_VARIABLE;
	static final ImageDescriptor ICON_VIEW_FUNCTION;
	static final ImageDescriptor ICON_VIEW_DYNAMIC_CLASS;
	static final ImageDescriptor ICON_VIEW_CLASS;
	static final ImageDescriptor ICON_VIEW_CLASS_METHOD;
	static final ImageDescriptor ICON_VIEW_INSTANCE_METHOD;
	static final ImageDescriptor ICON_VIEW_CLASS_VARIABLE;
	static final ImageDescriptor ICON_VIEW_INSTANCE_VARIABLE;

	static {
		String iconPath = "icons/";

		ICON_EDITOR = createImageDescriptor(iconPath + "js.gif");
		ICON_VIEW_VARIABLE = createImageDescriptor(iconPath + "global_variable.gif");
		ICON_VIEW_FUNCTION = createImageDescriptor(iconPath + "func.gif");
		ICON_VIEW_CLASS = createImageDescriptor(iconPath + "class_obj.gif");
		ICON_VIEW_DYNAMIC_CLASS = createImageDescriptor(iconPath + "dyn_class_obj.gif");
		ICON_VIEW_CLASS_METHOD = createImageDescriptor(iconPath + "class_method.gif");
		ICON_VIEW_INSTANCE_METHOD = createImageDescriptor(iconPath + "instance_method.gif");
		ICON_VIEW_CLASS_VARIABLE = createImageDescriptor(iconPath + "class_variable.gif");
		ICON_VIEW_INSTANCE_VARIABLE = createImageDescriptor(iconPath + "instance_variable.gif");
	}

	/**
	 * Utility method to create an <code>ImageDescriptor</code> from a path to a file.
	 * @param path
	 * 
	 * @return
	 */
	private static ImageDescriptor createImageDescriptor(String path)
	{
		try
		{
			URL url = new URL(BASE_URL, path);

			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e)
		{
		}

		return ImageDescriptor.getMissingImageDescriptor();
	}
}