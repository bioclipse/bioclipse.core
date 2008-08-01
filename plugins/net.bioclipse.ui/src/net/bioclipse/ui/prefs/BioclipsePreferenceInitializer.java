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

import net.bioclipse.ui.BioclipseConstants;

import org.apache.log4j.Logger;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.PlatformUI;


/**
 * Initialize UI preferences. Don't forget plugin_customization.ini also
 * initializes some preferences.
 * @author ola
 *
 */
public class BioclipsePreferenceInitializer extends AbstractPreferenceInitializer {

    private static final Logger logger = Logger.getLogger(BioclipsePreferenceInitializer.class);

    @Override
    public void initializeDefaultPreferences() {
    	
		String defstr="Bioclipse Update Site" + IPreferenceConstants.PREFERENCES_DELIMITER+ BioclipseConstants.UPDATE_SITE;
    	PlatformUI.getPreferenceStore().putValue(IPreferenceConstants.UPDATE_SITES, defstr);
        
        logger.info("Default preferences initialized");
    }
}
