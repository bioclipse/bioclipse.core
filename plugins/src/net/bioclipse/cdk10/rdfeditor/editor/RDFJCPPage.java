package net.bioclipse.cdk10.rdfeditor.editor;

import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.color.IAtomColorer;


/**
 * Extend JCPPage for use in RDFeditor
 * @author ola
 *
 */
public class RDFJCPPage extends JCPPage implements ISelectionListener{

    public boolean visible;
    
    
    public boolean isVisible() {
    
        return visible;
    }

    
    public void setVisible( boolean visible ) {
    
        this.visible = visible;
    }

    public RDFJCPPage(IChemModel chemModel_in) {
        super( chemModel_in );
    }
    
    public RDFJCPPage(IChemModel chemModel_in, IAtomColorer colorer) {
        super(chemModel_in, colorer);
    }
    
    @Override
    public void createPartControl( Composite parent ) {
        super.createPartControl( parent );
        
        getSite().getPage().addSelectionListener(this);
    }

    @Override
    public void reactOnSelection( ISelection selection ) {
        super.reactOnSelection( selection );

        if (!( selection instanceof IStructuredSelection )) return;
        IStructuredSelection sel = (IStructuredSelection) selection;

        if (!( sel.getFirstElement() instanceof ReactionTableEntry )) return;
        ReactionTableEntry entry = (ReactionTableEntry) sel.getFirstElement();
        
        goModel( entry.getIndex() );
        
    }
    
    @Override
    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        
        if (isVisible()==false) return;
        if (part.equals( this )) return;
        if (part.equals( getMPE() )) return;
        if (!( selection instanceof IStructuredSelection )) return;

        super.selectionChanged( part, selection );
    }
    
    

    
    
}
