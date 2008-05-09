package net.bioclipse.cdk10.jchempaint.ui.editor;

import net.bioclipse.cdk10.jchempaint.outline.JCPOutlinePage;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;

/**
 * JChemPaint-based editor for MDL molfile V2000 files.
 * 
 * @author egonw
 */
public class MDLMolfileEditor extends MultiPageEditorPart 
        implements IJCPBasedEditor, IResourceChangeListener{

    private static final Logger logger = Logger.getLogger(MDLMolfileEditor.class);

    public static final String EDITOR_ID 
        = "net.bioclipse.cdk10.jchempaint.ui.editor.MDLMolfileEditor";

    JCPPage jcpPage;
    TextEditor textEditor;
    int textEditorIndex;
    private IUndoContext undoContext=null;

    private JCPOutlinePage fOutlinePage;
    private JCPMultiPageEditorContributor contributor;

    public IUndoContext getUndoContext() {
        return undoContext;
    }

    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
        super.init(site, input);
        setPartName(input.getName());
    }

    public JChemPaintModel getJcpModel() {
        if (jcpPage != null) {
            return jcpPage.getJCPModel();
        }
        return null;
    }

    public DrawingPanel getDrawingPanel() {
        return jcpPage.getDrawingPanel();
    }
    public JCPComposite getJcpComposite() {
        return (JCPComposite)jcpPage.getJcpComposite();
    }

    /**
     * Create JCP on page 1 and texteditor on page2
     */
    protected void createPages() {
        
        jcpPage=new JCPPage();
        textEditor=new TextEditor();
        
        try {
            int ix=addPage(jcpPage, getEditorInput());
            setPageText(ix, "Structure");

            textEditorIndex=addPage(textEditor, getEditorInput());
            setPageText(textEditorIndex, "Source");
        } catch (PartInitException e) {
            LogUtils.debugTrace(logger, e);
        }
        
    }

    public void doSave(IProgressMonitor monitor) {
        
        //Synch from JCP to texteditor
        //TODO

        //Use textEditor to save
        textEditor.doSave(monitor);
        
    }

    public void doSaveAs() {
        //Synch from JCP to texteditor
        //TODO

        //Use textEditor to save
        textEditor.doSaveAs();
    }

    public boolean isSaveAsAllowed() {

        //TODO: not implemented yet
        return false;
    }

    public void resourceChanged(IResourceChangeEvent event) {

        //React if resource is changed on disc.
        
    }
    
    public void setFocus() {
        super.setFocus();
    }
    
    public Object getAdapter(Class adapter) {
        
        if (IContentOutlinePage.class.equals(adapter)) {
            if (fOutlinePage == null) {
                fOutlinePage= new JCPOutlinePage(getEditorInput(), this);
            }
            return fOutlinePage;
        }
        
        
        return super.getAdapter(adapter);
    }

    public void setContributor(JCPMultiPageEditorContributor multiPageEditorContributor) {
        this.contributor = multiPageEditorContributor;
    }

    public JCPPage getJCPPage() {
        return jcpPage;
    }

}
