 /*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth
 *
 ******************************************************************************/

package net.bioclipse.cdk10.jchempaint.ui.editor.cml;

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
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.renderer.color.IAtomColorer;

/**
 * JChemPaint-based editor for CML files.
 * 
 * @author ola
 */
public class CMLfileEditor extends MultiPageEditorPart 
        implements IJCPBasedEditor, IResourceChangeListener{

    private static final Logger logger = Logger.getLogger(CMLfileEditor.class);

    public static final String EDITOR_ID 
        = "net.bioclipse.cdk10.jchempaint.ui.editor.cml.CMLfileEditor";

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

        IChemModel chemModel=null;
        
        try {
            chemModel=getModelFromEditorInput();
        } catch ( BioclipseException e1 ) {
            e1.printStackTrace();
            return;
        }
        

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
        
        CMLWriter cmlWriter=new CMLWriter(stringWriter);
        
        //FIXME: add pretty printing
        
        IChemModel model = this.getJcpModel().getChemModel();
        try {
            cmlWriter.write(model);
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
            
            CMLReader reader = new CMLReader(instream);
            IChemFile cf=(IChemFile)reader.read(new ChemFile());
            
            IChemModel cm=cf.getChemSequence( 0 ).getChemModel( 0 );
            
            return cm;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Use default colorer
     */
    public IAtomColorer getColorer() {
        return null;
    }


}
