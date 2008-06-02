package net.bioclipse.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;


public class AlignmentEditor extends MultiPageEditorPart {

    @Override
    protected void createPages() {
        try {
            int pageIndex1 = this.addPage( new Aligner(), getEditorInput() );
            setPageText(pageIndex1, "Alignment");
            int pageIndex2 = this.addPage( new TextEditor(), getEditorInput() );
            setPageText(pageIndex2, "Source");
        } catch ( PartInitException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void doSave( IProgressMonitor monitor ) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

}
