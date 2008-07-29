/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

public class JCPCompFocusListener implements FocusListener {

    private JCPComposite comp;

    public JCPCompFocusListener(JCPComposite jcpComposite) {
        this.comp = jcpComposite;
    }

    public void focusGained(FocusEvent e) {
        comp.setHasFocus(true);

    }

    public void focusLost(FocusEvent e) {
        comp.setHasFocus(false);
    }

}
