/*
 * $RCSfile: JSEditorPlugin.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSEditorPlugin.java,v $
 * Revision 1.4  2004/04/05 20:50:48  rdclark
 * Updated for Eclipse 3.0M8
 *
 * Revision 1.3  2003/12/10 20:19:16  agfitzp
 * 3.0 port
 *
 * Revision 1.2  2003/06/21 03:48:51  agfitzp
 * fixed global variables as functions bug
 * fixed length calculation of instance variables
 * Automatic outlining is now a preference
 *
 * Revision 1.1  2003/05/28 15:17:12  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import org.eclipse.jface.preference.IPreferenceStore;

import net.sourceforge.jseditor.preferences.PreferenceNames;

import java.util.*;


/**
 * The main plugin class to be used in the desktop.
 */
public class JSEditorPlugin extends AbstractUIPlugin
{
   //The shared instance.
   private static JSEditorPlugin plugin;

   //Resource bundle.
   private ResourceBundle resourceBundle;
   
   private boolean defaultsInitialized = false;
   
   /**
    * current func list
    */
   private List currentFunctions = new LinkedList();
     
   /**
    * The constructor.
    * @param descriptor
    */
   public JSEditorPlugin(IPluginDescriptor descriptor)
   {
      super(descriptor);
      plugin = this;

      try
      {
         resourceBundle = ResourceBundle.getBundle("net.sourceforge.jseditor.jseditorPluginResources");
      }
      catch(MissingResourceException x)
      {
         resourceBundle = null;
      }
   }

   /**
    * Returns the shared instance.
    * @return
    */
   public static JSEditorPlugin getDefault()
   {
      return plugin;
   }

   /**
    * Returns the workspace instance.
    * @return
    */
   public static IWorkspace getWorkspace()
   {
      return ResourcesPlugin.getWorkspace();
   }

   /**
    * Returns the string from the plugin's resource bundle, or 'key' if not found.
    * @param key
    * 
    * @return
    */
   public static String getResourceString(String key)
   {
      ResourceBundle bundle = JSEditorPlugin.getDefault().getResourceBundle();

      try
      {
      	return (bundle != null) ? bundle.getString(key) : key;
      }
      catch(MissingResourceException e)
      {
         return key;
      }
   }

   /**
    * Returns the plugin's resource bundle,
    * @return
    */
   public ResourceBundle getResourceBundle()
   {
      return resourceBundle;
   }
   
	/**
	 * Returns the currentFunctions.
	 * @return List
	 */
	public List getCurrentFunctions()
	{
		return currentFunctions;
	}

	/**
	 * Sets the currentFunctions.
	 * @param currentFunctions The currentFunctions to set
	 */
	public void setCurrentFunctions(List currentFunctions)
	{
		this.currentFunctions = currentFunctions;
	}

	public IPreferenceStore getPreferenceStore()
	{
		IPreferenceStore store = super.getPreferenceStore();
		
		if(! defaultsInitialized) {
			initializeDefaults(store);
		}
		return store;
	}
	
	private void initializeDefaults(IPreferenceStore store) {
		store.setDefault(PreferenceNames.P_AUTO_OUTLINE, true);

		store.setDefault(PreferenceNames.P_COMMENT_COLOR, "63,127,95");
		store.setDefault(PreferenceNames.P_STRING_COLOR, "42,0,255");
		store.setDefault(PreferenceNames.P_KEYWORD_COLOR, "127,0,85");
		store.setDefault(PreferenceNames.P_DEFAULT_COLOR, "0,0,0");

		this.defaultsInitialized = true;
	}
}