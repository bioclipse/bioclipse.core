package net.bioclipse.cdk10.jchempaint.ui.editor;

import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.commands.operations.IUndoContext;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.color.IAtomColorer;

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
    
    public JCPPage getJCPPage();

    /**
     * Editor can provide a colorer
     * @return
     */
    public IAtomColorer getColorer();

    /**
     * Editor must provide a way to parse the input to an IChemfile
     * @return
     */
    public IChemModel getModelFromEditorInput() throws BioclipseException;
    
}
