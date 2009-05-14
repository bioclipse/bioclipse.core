/*******************************************************************************
 * Copyright (c) 2007  Jonathan Alvarsson
 *               2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/    
 ******************************************************************************/
package net.bioclipse.inchi.business;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class InChIManagerFactory implements IExecutableExtension, 
                                              IExecutableExtensionFactory {

    private Object inchiManager;
    
    public void setInitializationData(IConfigurationElement config,
                                      String propertyName, 
                                      Object data) throws CoreException {
        inchiManager = Activator.getDefault().getJavaScriptInChIManager();
    }
    
    public Object create() throws CoreException {
        return inchiManager;
    }
}
