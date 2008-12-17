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
package net.bioclipse.usermanager.business;
import net.bioclipse.usermanager.Activator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
/**
 * 
 * @author jonalv
 */
public class UserManagerFactory implements IExecutableExtension, 
                                           IExecutableExtensionFactory {
    private Object userManager;
    public void setInitializationData( IConfigurationElement config,
                                       String propertyName, 
                                       Object data ) throws CoreException {
        userManager = Activator.getDefault().getUserManager();
        if (userManager == null) {
            userManager = new Object();
        }
    }
    public Object create() throws CoreException {
        return userManager;
    }
}
