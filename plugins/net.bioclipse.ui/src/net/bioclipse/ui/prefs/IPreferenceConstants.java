/*******************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/
package net.bioclipse.ui.prefs;

import net.bioclipse.ui.BioclipseConstants;


/**
 * Interface to define all constants related to the preferences of this package.
 * 
 * @author ola
 *
 */
public interface IPreferenceConstants {

	public static final String UPDATE_SITES = "UpdateSites";

	//Delimits between preferences
	public static final String PREFERENCES_DELIMITER = "%";
	
	//Delimiter within a preference
	public static final String PREFERENCES_OBJECT_DELIMITER = "£";
	
	public static final String PREFERENCES_EXTENSION_DELIMITER = ",";
	
	public static final String AUTOLOAD_NEW_RESOURCES = "autoLoadNewResources";


	public static final String PREFERENCES_DEFAULT_UPDATESITES =
		"Bioclipse Update Site" + IPreferenceConstants.PREFERENCES_DELIMITER+ BioclipseConstants.UPDATE_SITE;

	
	 public static final String SKIP_UPDATE_DIALOG_ON_STARTUP = "skipAskUpdateOnStartup";
	 public static final String SKIP_UPDATE_ON_STARTUP = "skipUpdateOnStartup";

}
