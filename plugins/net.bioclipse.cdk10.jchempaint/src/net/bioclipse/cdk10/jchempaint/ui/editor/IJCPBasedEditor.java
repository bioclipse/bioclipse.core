package net.bioclipse.cdk10.jchempaint.ui.editor;

import org.eclipse.core.commands.operations.IUndoContext;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;

/**
 * Common interface for editors based on JCP.
 * @author ola
 *
 */
public interface IJCPBasedEditor {

    public JChemPaintModel getJcpModel();

    public DrawingPanel getDrawingPanel();

    public IUndoContext getUndoContext();

    public JCPComposite getJcpComposite();
    
    
}
