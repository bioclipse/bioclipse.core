/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import org.eclipse.swt.widgets.Composite;

public class JCPComposite extends Composite {

    private boolean hasFocus = false;

    public JCPComposite(Composite parent, int style) {
        super(parent, style);
    }

    public void setHasFocus(boolean b) {
        this.hasFocus = b;
        
    }
    public boolean getFocus() {
        return hasFocus;
    }

    @Override
    public boolean setFocus() {
        this.setHasFocus(true);
        return super.setFocus();
    }

}
