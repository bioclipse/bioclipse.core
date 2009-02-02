/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 ******************************************************************************/
package net.bioclipse.data.wizards;

/**
 * Hold a folder object to be installed. Initialized from EP.
 * @author ola
 *
 */
public class InstallableFolder {

    private String name;
    private String description;
    private String location;
    public String getPluginID() {
		return pluginID;
	}

	public void setPluginID(String pluginID) {
		this.pluginID = pluginID;
	}
	private String pluginID;
    
    //Should this be installed?
    private boolean checked;
    
    //The wizard this folder should be installed in (if null then default is chosen)
	private String wizardID;

	//Constructor
	public InstallableFolder(String name, String description, String location, String pluginID, String wizardID) {
    	this.pluginID=pluginID;
        this.name = name;
        this.description = description;
        this.location = location;
        this.wizardID=wizardID;
    }

    public String getWizardID() {
		return wizardID;
	}

	public void setWizardID(String wizardID) {
		this.wizardID = wizardID;
	}

	public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    
    
}
