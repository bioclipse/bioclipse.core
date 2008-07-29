/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;
import net.bioclipse.cdk10.jchempaint.ui.editor.mdl.MDLMolfileEditor;

public class PopUpListener implements ActionListener {

    private JCPAction action;
    private JCPMultiPageEditorContributor contributor;

    public PopUpListener(JCPMultiPageEditorContributor contributor, JCPAction a) {
        this.contributor = contributor;
        this.action = a;
    }

    public void actionPerformed(ActionEvent e) {
        action.run(e);
        if(contributor.getActiveEditorPart() instanceof JCPPage)
         ((JCPPage)contributor.getActiveEditorPart()).getDrawingPanel().repaint();
        else if(contributor.getActiveEditorPart() instanceof MDLMolfileEditor){
        	DrawingPanel drawingPanel=((MDLMolfileEditor)contributor.
        			getActiveEditorPart()).getDrawingPanel(); 
        	drawingPanel.repaint();        
        }else{
            // TODO fixed ClassCastException for SDFEditor but still possible for other Exceptions
             
            ((IJCPBasedEditor)contributor.getActiveEditorPart()).getDrawingPanel().repaint();
        }
    }


}
