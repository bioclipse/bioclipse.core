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

import net.bioclipse.usermanager.Activator;
import net.bioclipse.usermanager.UserContainer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * This action logs out the currently logged in user
 * 
 * @author jonalv
 *
 */
public class LogoutAction implements IWorkbenchWindowActionDelegate {

    //Use logging configured in com.tools.logging plugin
//    private static final Logger logger = Activator.getLogManager().getLogger(LoginAction.class.toString());
    
    public void dispose() {
    }
    public void init(IWorkbenchWindow window) {
    }

    public void run(IAction action) {

//        logger.debug("LoginAction.run()");

        Activator.getDefault().getUserManager().logOut();
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }
}
