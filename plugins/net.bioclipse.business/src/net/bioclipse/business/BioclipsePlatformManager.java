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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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

    public String download(String url, IProgressMonitor monitor) 
    throws BioclipseException {
        StringBuffer content = new StringBuffer();
        URLConnection rawConn;
        try {
            rawConn = createURL(url).openConnection();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(rawConn.getInputStream())
            );
            String line = reader.readLine();
            while (line != null) {
                content.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException exception) {
            throw new BioclipseException(
                "Error while downloading from URL.", exception
            );
        }
        return content.toString();
    }

    public IFile downloadAsFile(String url, IFile target,
                                IProgressMonitor monitor)
    throws BioclipseException {
        URLConnection rawConn;
        try {
            rawConn = createURL(url).openConnection();
            if (target.exists()) {
                target.setContents(rawConn.getInputStream(), true, false, null);
            } else {
                target.create(rawConn.getInputStream(), false, null);                
            };
        } catch (IOException exception) {
            throw new BioclipseException(
                "Error while downloading from URL.", exception
            );
        } catch (CoreException exception) {
            throw new BioclipseException(
                "Error while downloading from URL.", exception
            );
        }
        return target;
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

    private URL createURL(String url) throws BioclipseException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new BioclipseException("Error while opening browser: " +
                e.getMessage(), e);
        }
    }
}
