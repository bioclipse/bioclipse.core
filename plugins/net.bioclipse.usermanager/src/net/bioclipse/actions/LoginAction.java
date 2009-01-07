/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.actions;

import net.bioclipse.dialogs.UserManagerLoginDialog;
import net.bioclipse.usermanager.Activator;
import net.bioclipse.usermanager.UserContainer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * This action logs a user in to the Usermanager
 * 
 * @author jonalv
 *
 */
public class LoginAction implements IWorkbenchWindowActionDelegate {

    public void dispose() {
    }
    public void init(IWorkbenchWindow window) {
    }

    public void run(IAction action) {

//        logger.debug("LoginAction.run()");

        UserContainer sandboxUserContainer = Activator
                                             .getDefault()
                                             .getUserManager()
                                             .getSandBoxUserContainer();
        
        UserManagerLoginDialog loginDialog = 
            new UserManagerLoginDialog( PlatformUI
                                        .getWorkbench()
                                        .getActiveWorkbenchWindow()
                                        .getShell(),
                                        sandboxUserContainer );
        
        loginDialog.open();
        if(loginDialog.getReturnCode() == loginDialog.OK) {
            if( loginDialog.isUserContainerEdited() ) {
                Activator.getDefault().getUserManager()
                         .switchUserContainer(sandboxUserContainer);
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }
}
