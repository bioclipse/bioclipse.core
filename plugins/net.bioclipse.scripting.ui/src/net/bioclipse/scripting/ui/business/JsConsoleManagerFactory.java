/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.scripting.ui.business;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import net.bioclipse.scripting.ui.Activator;

/**
 * Factory for <code>JsConsoleManager</code>.
 * 
 * @author masak
 *
 */
public class JsConsoleManagerFactory implements IExecutableExtension, 
                                                IExecutableExtensionFactory {

    private Object jsConsoleManager;
    
    public void setInitializationData( IConfigurationElement config,
                                       String propertyName, 
                                       Object data) throws CoreException {
    
        jsConsoleManager = Activator.getDefault().getJsConsoleManager();
    }
    
    public Object create() throws CoreException {
        return jsConsoleManager;
    }
}
