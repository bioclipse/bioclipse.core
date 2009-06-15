/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth
 *
 ******************************************************************************/

package net.bioclipse.ui.prefs;

import net.bioclipse.ui.Activator;
import net.bioclipse.ui.BioclipseConstants;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;


/**
 * Initialize UI preferences. Don't forget plugin_customization.ini also
 * initializes some preferences.
 * @author ola
 *
 */
public class UIPreferenceInitializer extends AbstractPreferenceInitializer {

     public UIPreferenceInitializer() {
    	 super();
	}
    
    @Override
    public void initializeDefaultPreferences() {

    	//This string is the default update site for Bioclipse
		String defstr="Bioclipse Update Site" + 
		IPreferenceConstants.PREFERENCES_DELIMITER 
		+ BioclipseConstants.UPDATE_SITE
		+ IPreferenceConstants.PREFERENCES_OBJECT_DELIMITER
		+ "Speclipse Update Site"
		+ IPreferenceConstants.PREFERENCES_DELIMITER
		+ BioclipseConstants.SPECLIPSE_UPDATE_SITE
    + IPreferenceConstants.PREFERENCES_OBJECT_DELIMITER
    + "Bioclipse Experimental Update Site"
    + IPreferenceConstants.PREFERENCES_DELIMITER
    + BioclipseConstants.BIOCLIPSE_EXPERIMENTAL_UPDATE_SITE
    + IPreferenceConstants.PREFERENCES_OBJECT_DELIMITER
    + "IPB Halle Update Site"
    + IPreferenceConstants.PREFERENCES_DELIMITER
    + BioclipseConstants.IPB_UPDATE_SITE
    + IPreferenceConstants.PREFERENCES_OBJECT_DELIMITER
    + "Eclipse 3.3 Update Site"
    + IPreferenceConstants.PREFERENCES_DELIMITER
    + BioclipseConstants.ECLIPSE_33_UPDATE_SITE;

		IEclipsePreferences prefs = new DefaultScope().getNode(Activator.PLUGIN_ID);
		prefs.put(
				IPreferenceConstants.UPDATE_SITES, 
				defstr);
    	
    }
}
