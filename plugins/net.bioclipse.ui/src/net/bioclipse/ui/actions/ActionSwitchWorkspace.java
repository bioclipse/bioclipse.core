/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
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
        // restart client
        PlatformUI.getWorkbench().restart();
    }
}
