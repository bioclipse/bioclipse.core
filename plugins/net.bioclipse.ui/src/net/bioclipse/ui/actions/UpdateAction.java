/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/
package net.bioclipse.ui.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


import net.bioclipse.ui.Activator;
import net.bioclipse.ui.prefs.UpdateSitesPreferencePage;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.update.internal.operations.UpdateUtils;
import org.eclipse.update.search.BackLevelFilter;
import org.eclipse.update.search.EnvironmentFilter;
import org.eclipse.update.search.UpdateSearchRequest;
import org.eclipse.update.search.UpdateSearchScope;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;

/**
 * 
 * @author ola
 *
 */
public class UpdateAction extends Action implements IAction {
	private IWorkbenchWindow window;

	public UpdateAction(IWorkbenchWindow window) {
		this.window = window;
		setId("net.bioclipse.newUpdates");
		setText("&Online update...");
		setToolTipText("Search for updates to currently installed features");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/usearch_obj.gif"));
		window.getWorkbench().getHelpSystem().setHelp(this,
				"net.bioclipse.updates");
	}

	public void run() {
		BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable() {
			public void run() {
				UpdateJob job = new UpdateJob("Searching for updates", getSearchRequest());
				UpdateManagerUI.openInstaller(window.getShell(), job);
			}
		});
	}
	
	private UpdateSearchRequest getSearchRequest() {
		UpdateSearchRequest result = new UpdateSearchRequest(
				UpdateSearchRequest.createDefaultUpdatesSearchCategory(),
				new UpdateSearchScope());
		result.addFilter(new BackLevelFilter());
		result.addFilter(new EnvironmentFilter());
		UpdateSearchScope scope = new UpdateSearchScope();
		scope.setFeatureProvidedSitesEnabled(false);

		//Get prefs from update site store
			ArrayList list=UpdateSitesPreferencePage.getPreferencesFromStore();
			if (list==null){
				return null;
			}


			//Add them one by one
			Iterator iter=list.iterator();
			while (iter.hasNext()) {
				String[] entry = (String[]) iter.next();
				try {
				scope.addSearchSite(entry[0], new URL(entry[1]), null);
				System.out.println("Added entry site: " + entry[0] + " - " + entry[1]);
				} catch (MalformedURLException e) {
					System.out.println("ERROR: Skipping site: " + entry[0] + " - " + entry[1] + ": Malformed URL.");
				}
			}		
			
		result.setScope(scope);
		return result;
	}
		
	
	
	
}
