/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.Color;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import net.bioclipse.cdk10.business.CDK10Molecule;
import net.bioclipse.cdk10.business.ICDK10Constants;
import net.bioclipse.cdk10.jchempaint.colorers.PropertyColorer;
import net.bioclipse.cdk10.jchempaint.outline.JCPOutlinePage;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.io.JCPSaveFileFilter;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CDKSourceCodeWriter;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.MDLRXNWriter;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2DModel;
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
        	IProgressMonitor monitor = new NullProgressMonitor();
         	try{
    	    	String towrite=null;
    	    	boolean correctfiletype=false;
    	    	IFile target=null;
     	        int ticks = 10000;
    	    	while(!correctfiletype){
    	        	SaveAsDialog saveasdialog=new SaveAsDialog(this.getSite().getShell());
    	        	int result=saveasdialog.open();
    	        	if(result==SaveAsDialog.CANCEL){
    	        		correctfiletype=true;
    	        		target=null;
    	        	}else{
	    	        	target = ResourcesPlugin.getWorkspace().getRoot().getFile(saveasdialog.getResult());
	    	        	String filetype = saveasdialog.getResult().getFileExtension();
	    	        	if(filetype==null)
	    	        		filetype="";
	    	    		correctfiletype=true;
	         	        monitor.beginTask( "Writing file", ticks );
		    	    	if(filetype.equals(JCPSaveFileFilter.mol)){
		    	            StringWriter writer = new StringWriter();
		    	            MDLWriter mdlWriter = new MDLWriter(writer);
		    	            mdlWriter.write(getChemModel());
		    	            towrite=writer.toString();
		    	    	} else if(filetype.equals(JCPSaveFileFilter.cml)){
		    	    		StringWriter writer = new StringWriter();
		    	            CMLWriter cmlWriter = new CMLWriter(writer);
		    	            cmlWriter.write(getChemModel());
		    	            towrite=writer.toString();
		    	    	} else if(filetype.equals(JCPSaveFileFilter.rxn)){
		    	    		StringWriter writer = new StringWriter();
		    	            MDLRXNWriter cmlWriter = new MDLRXNWriter(writer);
		    	            cmlWriter.write(getChemModel());
		    	            towrite=writer.toString();
		    	    	} else if(filetype.equals(JCPSaveFileFilter.smi)){
		    	    		StringWriter writer = new StringWriter();
		    	            SMILESWriter cmlWriter = new SMILESWriter(writer);
		    	            cmlWriter.write(getChemModel());
		    	            towrite=writer.toString();
		    	    	} else if(filetype.equals(JCPSaveFileFilter.cdk)){
		    	    		StringWriter writer = new StringWriter();
		    	            CDKSourceCodeWriter cmlWriter = new CDKSourceCodeWriter(writer);
		    	            cmlWriter.write(getChemModel());
		    	            towrite=writer.toString();
		    	    	} else {
		    	    		MessageDialog.openError(this.getSite().getShell(), "No valid file type!", "Valid file types are "+JCPSaveFileFilter.mol+", "+JCPSaveFileFilter.cml+", "+JCPSaveFileFilter.cdk+", "+JCPSaveFileFilter.rxn+" and "+JCPSaveFileFilter.smi+". The file extension must be one of these!");
		    	    		correctfiletype=false;
		    	    	}
    	        	}
    	    	}
    	    	if(target!=null && target.exists()){
    	        	 target.setContents(new StringBufferInputStream(towrite), false, true, monitor);
    	    	} else if(target!=null){
    		    	target.create(new StringBufferInputStream(towrite), false, monitor);
    	    	}
    	    	monitor.worked(ticks);
				//Activator.getDefault().getCDKManager().save(getChemModel(), file, saveasdialog.getResult().getFileExtension());
			} catch (CDKException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

        
    	public void setMoleculeColorProperties(List<Color> molColors) {

            Renderer2DModel model=jcpPage.getDrawingPanel().getRenderer2D().getRenderer2DModel();            

            IAtomContainer ac=chemModel.getMoleculeSet().getMolecule(0);

            //Confirm dimensions
    		if (molColors.size()!=ac.getAtomCount()){
    			logger.error("Molecule not of same dimension as MoleculeList.");
    			return;
    		}

            
            //Color by metaprint
            model.getColorHash().clear();
            for (int i=0; i< ac.getAtomCount(); i++){
                Color color=molColors.get(i);
                if (color !=null){
                    ac.getAtom( i ).setProperty( ICDK10Constants.COLOR_PROPERTY, color );
                }
                else{
                    ac.getAtom( i ).setProperty( ICDK10Constants.COLOR_PROPERTY, Color.BLACK );
                    
                    //For Background
                     model.getColorHash().put(ac.getAtom( i ), Color.WHITE);
                }

            }

            //Configure JCP
            model.setKekuleStructure( true );
            model.setShowAtomTypeNames( false );
            model.setShowImplicitHydrogens( false );
            model.setShowExplicitHydrogens(  false );
            Font font = new Font("courier", Font.BOLD, 14);
            model.setFont(font);
            
            //Update drawing
            model.fireChange();
   		
    	}

    	public void setMoleculeTooltips(List<String> molTooltips) {

            Renderer2DModel model=jcpPage.getDrawingPanel().getRenderer2D().getRenderer2DModel();            

            IAtomContainer ac=chemModel.getMoleculeSet().getMolecule(0);
            HashMap<IAtom, String> currentToolTip=new HashMap<IAtom, String>();

            //Confirm dimensions
    		if (molTooltips.size()!=ac.getAtomCount()){
    			logger.error("Molecule not of same dimension as TooltipList.");
    			return;
    		}

            
            //Color by metaprint
            for (int i=0; i< ac.getAtomCount(); i++){
                //Add tooltip
            	String tooltip=molTooltips.get(i);
                currentToolTip.put( ac.getAtom( i ), tooltip );
            	
            }

            model.setToolTipTextMap( currentToolTip );

            //Update drawing
            model.fireChange();

    		
    	}

        
        
}
