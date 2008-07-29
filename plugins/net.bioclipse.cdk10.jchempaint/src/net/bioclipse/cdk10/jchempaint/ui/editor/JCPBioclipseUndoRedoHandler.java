/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import javax.swing.undo.UndoableEdit;

import net.bioclipse.cdk10.jchempaint.ui.editor.action.UndoableAction;

import org.eclipse.core.commands.operations.IUndoContext;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.IUndoRedoHandler;

public class JCPBioclipseUndoRedoHandler implements IUndoRedoHandler {
    JChemPaintModel jcpm=null;
    IUndoContext undoContext=null;
    DrawingPanel drawingPanel=null;

    public void postEdit(UndoableEdit edit) {
        UndoableAction.pushToUndoRedoStack(edit,jcpm,undoContext, drawingPanel);
    }

    public void setDrawingPanel(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    public void setJcpm(JChemPaintModel jcpm) {
        this.jcpm = jcpm;
    }

    public void setUndoContext(IUndoContext undoContext) {
        this.undoContext = undoContext;
    }


}