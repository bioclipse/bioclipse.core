/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.rdfeditor.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;


public class ReactionTableEditingSupport extends EditingSupport {

    private TextCellEditor editor;
    int colIndex;
    ReactionTablePage reactionTablePage;
    
    public int getColIndex() {
        return colIndex;
    }

    public ReactionTableEditingSupport (TableViewer viewer, int colIndex, 
                                        ReactionTablePage reactionTablePage) {
        super(viewer);
        this.editor = new TextCellEditor(viewer.getTable());
        this.colIndex=colIndex;
        this.reactionTablePage=reactionTablePage;
    }

    protected boolean canEdit(Object element) {
        return true;
    }

    protected CellEditor getCellEditor(Object element) {
        return editor;
    }

    protected void setValue(Object element, Object value) {
        ReactionTableEntry entry=(ReactionTableEntry)element;
        
        //If no change
        if (value.toString().equals( entry.columns[getColIndex()])) return;

        entry.columns[getColIndex()]= value.toString();
        reactionTablePage.setDirty(true);
        getViewer().update(element, null);
    }

    protected Object getValue(Object element) {
        ReactionTableEntry entry=(ReactionTableEntry)element;
        return entry.columns[getColIndex()].toString();
    }
}
