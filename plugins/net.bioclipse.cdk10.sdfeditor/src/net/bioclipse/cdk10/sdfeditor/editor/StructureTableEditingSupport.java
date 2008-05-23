package net.bioclipse.cdk10.sdfeditor.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.ui.IEditorPart;


public class StructureTableEditingSupport extends EditingSupport {

    private TextCellEditor editor;
    int colIndex;
    StructureTablePage structureTablePage;
    
    public int getColIndex() {
        return colIndex;
    }

    public StructureTableEditingSupport (TableViewer viewer, int colIndex, 
                                        StructureTablePage structureTablePage) {
        super(viewer);
        this.editor = new TextCellEditor(viewer.getTable());
        this.colIndex=colIndex;
        this.structureTablePage=structureTablePage;
    }

    protected boolean canEdit(Object element) {
        return true;
    }

    protected CellEditor getCellEditor(Object element) {
        return editor;
    }

    protected void setValue(Object element, Object value) {
        StructureTableEntry entry=(StructureTableEntry)element;
        
        //If no change
        if (value.toString().equals( entry.columns[getColIndex()])) return;

        entry.columns[getColIndex()]= value.toString();
        structureTablePage.setDirty(true);
        getViewer().update(element, null);
    }

    protected Object getValue(Object element) {
        StructureTableEntry entry=(StructureTableEntry)element;
        return entry.columns[getColIndex()].toString();
    }
}
