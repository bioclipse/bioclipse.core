package net.bioclipse.cdk10.jchempaint.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;

public interface IJCPEditorPart {

    
    public JChemPaintModel getJcpModel();
    
    public Composite getJcpComposite();
    
    public DrawingPanel getDrawingPanel();
    
    public JCPScrollBar getJcpScrollBar();
}
