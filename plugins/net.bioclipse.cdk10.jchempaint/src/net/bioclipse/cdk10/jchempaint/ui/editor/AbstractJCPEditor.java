/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.cdk10.business.CDK10Molecule;
import net.bioclipse.cdk10.jchempaint.colorers.PropertyColorer;
import net.bioclipse.cdk10.jchempaint.outline.JCPOutlinePage;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.color.IAtomColorer;

public abstract class AbstractJCPEditor extends MultiPageEditorPart 
										implements IJCPBasedEditor,
												   IResourceChangeListener{

        private static final Logger logger = Logger.getLogger(AbstractJCPEditor.class);

        JCPPage jcpPage;
        TextEditor textEditor;
        int textEditorIndex;
        private IUndoContext undoContext=null;
        
        IAtomColorer colorer;

        private JCPOutlinePage fOutlinePage;
        private JCPMultiPageEditorContributor contributor;

        private IChemModel chemModel;

//    	private CDK10Molecule cdk10Molecule;
//
//        
//        
//        public CDK10Molecule getCdkmolecule() {
//    		return cdk10Molecule;
//    	}
//
//
//    	public void setCdkmolecule(CDK10Molecule cdkmolecule) {
//    		this.cdk10Molecule = cdkmolecule;
//    	}


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

        //Default is PropertyColorer by CDK
        public IAtomColorer getColorer() {
            return new PropertyColorer();
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
                try {
                    jcpPage=new JCPPage(chemModel);
                    textEditorIndex=addPage(
                                 textEditor=new TextEditor(), getEditorInput());
                } catch (PartInitException e) {
                    
                }
                return;
            }

            //Get the CDK Molecule
            org.openscience.cdk.interfaces.IMolecule mol=chemModel.getMoleculeSet().getMolecule( 0 );

            
//            if (GeometryTools.has2DCoordinates( mol )==false){
                //FIXME: add to CDK
                StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                try {
                    sdg.setMolecule(mol,true);
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

//            }
            
//              //Create a CDK10Molecule from the CDK Molecule (=AC)
              CDK10Molecule cdk10Molecule = new CDK10Molecule(mol);


            if (colorer!=null)
                jcpPage=new JCPPage(chemModel, colorer);
            else
                jcpPage=new JCPPage(chemModel);

//            //Send the CDK10Mol to JCPPage
            jcpPage.setCdk10Molecule(cdk10Molecule);
            

            textEditor=new TextEditor();
            
            try {
                int ix=addPage(jcpPage, getEditorInput());
//                jcpPage.activateJCP();
                setPageText(ix, "Structure");

                textEditorIndex=addPage(textEditor, getEditorInput());
                setPageText(textEditorIndex, "Source");
            } catch (PartInitException e) {
                LogUtils.debugTrace(logger, e);
            }
            
            
            //Post selections in to Eclipse
//            getSite().setSelectionProvider(this);

            
        }

        /**
         * Provide a serialization of the model as a String for display in source tab
         * @return
         */
        public abstract String asText();

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
        	//FIXME: implement
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
//            if (adapter == IMolecule.class){
//                return cdkmolecule;
//            }
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
        public abstract IChemModel getModelFromEditorInput() throws BioclipseException;

    }
