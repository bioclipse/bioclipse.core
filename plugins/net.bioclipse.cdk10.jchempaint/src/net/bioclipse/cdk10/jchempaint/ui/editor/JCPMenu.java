/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import javax.swing.JMenu;

public class JCPMenu extends JMenu {

    public JCPMenu(String translation) {
        super(translation);
    }
    

    @Override
    public void menuSelectionChanged(boolean arg0) {
        this.getPopupMenu().menuSelectionChanged(arg0);
        super.menuSelectionChanged(arg0);
        this.getPopupMenu().repaint();
    }
}
