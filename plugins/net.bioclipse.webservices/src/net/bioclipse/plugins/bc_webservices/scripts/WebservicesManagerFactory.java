 /*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stefan Kuhn
 *     
 ******************************************************************************/

package net.bioclipse.plugins.bc_webservices.scripts;

import net.bioclipse.webservices.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

/**
 * 
 * @author shk3
 */
public class WebservicesManagerFactory implements IExecutableExtension, 
                                              IExecutableExtensionFactory {

    private Object webservicesManager;
    
    public void setInitializationData(IConfigurationElement config,
            String propertyName, Object data) throws CoreException {
        
        webservicesManager = Activator.getDefault().getWebservicesManager();
        if(webservicesManager==null) {
            webservicesManager = new Object();
        }
    }

    public Object create() throws CoreException {
        return webservicesManager;
    }
}
