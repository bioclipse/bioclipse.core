package net.bioclipse.cdk10.jchempaint.ui.editor.mdl;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.bioclipse.cdk10.jchempaint.outline.JCPOutlinePage;
import net.bioclipse.cdk10.jchempaint.ui.editor.DrawingPanel;
import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPComposite;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPMultiPageEditorContributor;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.color.CPKAtomColors;
import org.openscience.cdk.renderer.color.IAtomColorer;

/**
 * JChemPaint-based editor for MDL molfile V2000 files.
 * 
 * @author egonw, ola
 */
public class MDLMolfileEditor extends MultiPageEditorPart 
        implements IJCPBasedEditor, IResourceChangeListener{

    private static final Logger logger = Logger.getLogger(MDLMolfileEditor.class);

    public static final String EDITOR_ID 
        = "net.bioclipse.cdk10.jchempaint.ui.editor.mdl.MDLMolfileEditor";

    JCPPage jcpPage;
    TextEditor textEditor;
    int textEditorIndex;
    private IUndoContext undoContext=null;
    
    IAtomColorer colorer;

    private JCPOutlinePage fOutlinePage;
    private JCPMultiPageEditorContributor contributor;

    private IChemModel chemModel;

    
    
    public IChemModel getChemModel() {
    
        return chemModel;
    }

    
    public void setChemModel( IChemModel chemModel ) {
    
        this.chemModel = chemModel;
    }

    public IUndoContext getUndoContext() {
        return undoContext;
    }

    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
        super.init(site, input);
        setPartName(input.getName());
        colorer=getColorer();
    }

    //Default for MDLMolfileEditor is CPK
    public IAtomColorer getColorer() {
        return new CPKAtomColors();
    }

    public JChemPaintModel getJcpModel() {
        if (jcpPage != null) {
            return jcpPage.getJcpModel();
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
        
        chemModel=null;
        
        try {
            chemModel=getModelFromEditorInput();
        } catch ( BioclipseException e1 ) {
            e1.printStackTrace();
            return;
        }
        
        if (chemModel==null){
            logger.error("Could not parse file!! " );
            return;
        }
        
        org.openscience.cdk.interfaces.IMolecule mol=chemModel.getMoleculeSet().getMolecule( 0 );
//        if (GeometryTools.has2DCoordinates( mol )==false){
            //FIXME: add to CDK
            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            try {
                sdg.setMolecule((org.openscience.cdk.interfaces.IMolecule)mol.clone());
                sdg.generateCoordinates();
                mol = sdg.getMolecule();
                IMoleculeSet ms= new MoleculeSet();
                ms.addAtomContainer( mol );
                chemModel.setMoleculeSet( ms );
            } catch ( CloneNotSupportedException e ) {
                e.printStackTrace();
            } catch ( Exception e ) {
                e.printStackTrace();
            }

//        }
        
        

        if (colorer!=null)
            jcpPage=new JCPPage(chemModel, colorer);
        else
            jcpPage=new JCPPage(chemModel);
            
        textEditor=new TextEditor();
        
        try {
            int ix=addPage(jcpPage, getEditorInput());
//            jcpPage.activateJCP();
            setPageText(ix, "Structure");

            textEditorIndex=addPage(textEditor, getEditorInput());
            setPageText(textEditorIndex, "Source");
        } catch (PartInitException e) {
            LogUtils.debugTrace(logger, e);
        }
        
    }

    private String asText() {
        StringWriter stringWriter = new StringWriter(2000);
        MDLWriter mdlWriter = new MDLWriter(stringWriter);
        IChemModel model = this.getJcpModel().getChemModel();
        try {
            mdlWriter.write(model);
        } catch (CDKException e) {
            e.printStackTrace(new PrintWriter(stringWriter));
        }
        return stringWriter.toString();
    }

    public void updateTextEditorFromJCP() {
        textEditor.getDocumentProvider()
            .getDocument(textEditor.getEditorInput())
            .set(asText());
    }

    public void doSave(IProgressMonitor monitor) {
        this.showBusy(true);
        //Synch from JCP to texteditor
        updateTextEditorFromJCP();
        //Use textEditor to save
        textEditor.doSave(monitor);
        jcpPage.setDirty(false);
        firePropertyChange( IEditorPart.PROP_DIRTY );
        this.showBusy(false);
    }

    @Override
    public boolean isDirty() {
        return jcpPage.isDirty() || textEditor.isDirty();
    }
    
    public void doSaveAs() {
        doSave(null);
    }

    public boolean isSaveAsAllowed() {
        return true;
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

    /**
     * Get the IChemModel from the parsedResource
     * @return
     * @throws BioclipseException 
     */
    public IChemModel getModelFromEditorInput() throws BioclipseException{

        Object file = getEditorInput().getAdapter(IFile.class);
        if (!(file instanceof IFile)) {
            throw new BioclipseException(
                    "Invalid editor input: Does not provide an IFile");
        }

        IFile inputFile = (IFile) file;
        
        try {
            InputStream instream=inputFile.getContents();
            
            MDLV2000Reader reader = new MDLV2000Reader(instream);
            return (IChemModel)reader.read(new ChemModel());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
