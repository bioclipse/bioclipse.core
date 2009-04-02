/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.ui.actions;

import net.bioclipse.core.PickWorkspaceDialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class ActionSwitchWorkspace extends Action {

    private Image _titleImage;

    public ActionSwitchWorkspace(Image titleImage) {
        super("Switch Workspace");
        _titleImage = titleImage;
        this.setId( "switchworkspace" );
    }

    @Override
    public void run() {
        PickWorkspaceDialog pwd = new PickWorkspaceDialog(true, _titleImage); 
        int pick = pwd.open(); 
        if (pick == Dialog.CANCEL) 
            return; 
 
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Switch Workspace", "The client will now restart with the new workspace"); 
          // restart client
        PickWorkspaceDialog.setStartedFromSwitchWorkspace( true );
        PlatformUI.getWorkbench().restart();
    }
}
