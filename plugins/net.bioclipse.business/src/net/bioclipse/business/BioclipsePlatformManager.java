/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.business;

import java.net.MalformedURLException;
import java.net.URL;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class BioclipsePlatformManager implements IBioclipseManager {

    public String getManagerName() {
        return "bioclipse";
    }
    
    public void planet() throws BioclipseException {
    	try {
			openURL(new URL("http://planet.bioclipse.net"));
		} catch (MalformedURLException e) {
			throw new BioclipseException("Error while opening browser: " +
				e.getMessage(), e);
		}
    }
    	
    public void bugTracker() throws BioclipseException {
    	try {
			openURL(new URL("http://bugs.bioclipse.net"));
		} catch (MalformedURLException e) {
			throw new BioclipseException("Error while opening browser: " +
				e.getMessage(), e);
		}
    }
    	
    private void openURL(URL url) throws BioclipseException {
    	IWorkbenchBrowserSupport browserSupport =
    		PlatformUI.getWorkbench().getBrowserSupport();
    	IWebBrowser browser;
    	try {
    		browser = browserSupport.createBrowser(
    				IWorkbenchBrowserSupport.LOCATION_BAR |
    				IWorkbenchBrowserSupport.NAVIGATION_BAR,
    				null, null, null
    		);
    		browser.openURL(url);
    	} catch (PartInitException e) {
    		throw new BioclipseException("Error while opening browser: " +
				e.getMessage(), e);
    	}
    }
}
