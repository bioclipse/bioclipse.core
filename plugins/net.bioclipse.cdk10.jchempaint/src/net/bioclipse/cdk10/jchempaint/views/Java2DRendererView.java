/*******************************************************************************
 * Copyright (c) 2005-2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Kuhn <shk3@users.sf.net> - original implementation
 *     Carl <carl_marak@users.sf.net>  - converted into table
 *     Ola Spjuth                      - minor fixes
 *     Egon Willighagen                - adapted for the new renderer from CDK
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.views;



import java.util.Iterator;

import net.bioclipse.cdk10.business.CDK10Manager;
import net.bioclipse.cdk10.business.CDK10Molecule;
import net.bioclipse.cdk10.business.ICDK10Constants;
import net.bioclipse.cdk10.jchempaint.outline.CDKChemObject;
import net.bioclipse.cdk10.jchempaint.ui.editor.AbstractJCPEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPbasedMPE;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.AtomIndexSelection;
import net.bioclipse.core.domain.IChemicalSelection;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;

/**
 * 2D Rendering widget using the new Java2D based JChemPaint renderer.
 */
public class Java2DRendererView extends ViewPart
implements ISelectionListener {

	private static final Logger logger = Logger.getLogger(Java2DRendererView.class);

	private JChemPaintWidget canvasView;
	private IMolecule molecule;
	private final static StructureDiagramGenerator sdg = 
											new StructureDiagramGenerator();

	private IChemObjectBuilder chemObjBuilder;

	private CDK10Manager cdk;

	//Store highlighted selections
	private IAtomContainer selectedContent;

	//Cache current molecule
	private CDK10Molecule storedMolecule;

	/**
	 * The constructor.
	 */
	public Java2DRendererView() {

		chemObjBuilder=DefaultChemObjectBuilder.getInstance();
		cdk=new CDK10Manager();
		selectedContent = new AtomContainer();

	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		canvasView = new JChemPaintWidget(parent, SWT.PUSH );
		canvasView.setSize( 200, 200 );

		// Register this page as a listener for selections
		getViewSite().getPage().addSelectionListener(this);

		//See what's currently selected
		ISelection selection=PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getSelectionService().getSelection();
		reactOnSelection(selection);

	}

	@Override
	public void setFocus() {
		canvasView.setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		reactOnSelection(selection);
	}

	private void reactOnSelection(ISelection selection) {

		//We always remove all elements as highlighting is always only for latest selection
		selectedContent.removeAllElements();

		if (!(selection instanceof IStructuredSelection)){
			redraw();
			return;
		}

		IStructuredSelection ssel = (IStructuredSelection) selection;

		//If we have any objects in structuredSelection
		if (ssel.size()>0) {

			
			//Check if new model to show
			CDK10Molecule mol=getMoleculeFromSelection(ssel);
			if (mol==null){
				
				//Check that we have an active JCPEditor, else clear
				IEditorPart editor=getSite().getPage().getActiveEditor();
				if (editor instanceof IJCPBasedEditor) {
					IJCPBasedEditor jcpeditor = (IJCPBasedEditor) editor;
					mol=jcpeditor.getJCPPage().getCdk10Molecule();
				}
			}
			
			//If mol still null, get from adapter
			if (mol==null){
				IEditorPart editor=getSite().getPage().getActiveEditor();
				if (editor instanceof IAdaptable){
					net.bioclipse.core.domain.IMolecule imol=
						(net.bioclipse.core.domain.IMolecule) editor.
												getAdapter(IMolecule.class);
					if (imol!=null){
						if (imol instanceof CDK10Molecule) {
							mol= (CDK10Molecule) imol;
						}else{
							try {
								mol=cdk.create(imol);
							} catch (BioclipseException e) {
								logger.debug("Java2DView: Could not create cdk10mol " +
										"from " + imol);
							}
						}
				}
				}

			}

			//If mol still null, give up
			if (mol==null){
				storedMolecule=null;
				clearView();
				return;
			}

			if (mol!=storedMolecule){
				storedMolecule=mol;
				setMolecule(mol);
			}

			//Iterate over all in selection and deduce highlighting
			Iterator it=ssel.iterator();
			while( it.hasNext()){
				Object obj=it.next();
				if(obj instanceof CDKChemObject){
					
					
					obj=((CDKChemObject)obj).getChemobj();
                    if (obj instanceof IAtom){
						IAtom atom=(IAtom)obj;
						if (storedMolecule.getAtomContainer().contains(atom)){
							selectedContent.addAtom(atom);
						}                    }
                    if (obj instanceof IBond){
						IBond bond=(IBond)obj;
						if (storedMolecule.getAtomContainer().contains(bond)){
							selectedContent.addBond(bond);
						}
                    }
				}

				//TODO: Handle case where atoms/bonds selected by indices
				/*
                if(obj instanceof IChemicalSelection){
                    if (selobj!=null){
                        IChemicalSelection atomSelection=(IChemicalSelection)selobj;

                        if ( atomSelection instanceof AtomIndexSelection ) {
                            AtomIndexSelection isel = (AtomIndexSelection) atomSelection;
                            int[] selindices = isel.getSelection();
//                            System.out.println("\n** Should highlight these JCP atoms:\n");
                            IAtomContainer selectedMols=new AtomContainer();
                            for (int i=0; i<selindices.length;i++){
                                selectedMols.addAtom( molecule.getAtom( selindices[i] ));
//                                System.out.println(i);
                            }
                            canvasView.getRendererModel().setExternalSelectedPart( selectedMols );
                            canvasView.redraw();
                        }
                }

            }
				 */

				canvasView.getRendererModel().setExternalSelectedPart(selectedContent);
//				canvasView.getRendererModel().setSelectedPart(selectedContent);
			}

		}
		
		redraw();

	}

	private void redraw() {
		canvasView.setForceRepaint(true);
		canvasView.redraw();
		
	}

	private void setMolecule(CDK10Molecule mol) {

		//Create molecule
		IAtomContainer ac=mol.getAtomContainer();

		molecule=new Molecule(ac);

		//Create 2D-coordinates if not available
		if (GeometryTools.has2DCoordinatesNew( molecule )==0){
			try {
				sdg.setMolecule(molecule,true);
				sdg.generateCoordinates();
			} catch (CloneNotSupportedException e) {
				clearView();
				logger.debug( "Unable to clone structure in 2Dview: " 
						+ e.getMessage() );
			} catch ( Exception e ) {
				clearView();
				logger.debug( "Unable to generate structure in 2Dview: " 
						+ e.getMessage() );
			}
			molecule = sdg.getMolecule();
		}

		setAtomContainer(molecule);

	}

	private CDK10Molecule getMoleculeFromSelection(IStructuredSelection ssel) {

		Iterator it=ssel.iterator();
		
		while( it.hasNext()){
			Object obj=it.next();
			if (obj instanceof CDK10Molecule) {
				CDK10Molecule mol = (CDK10Molecule) obj;
				if (mol.getAtomContainer()==null){
					logger.debug("CDKMolecule but can't get AtomContainer.");
					return null;
				}
				return mol;
			}

			else if (obj instanceof IAdaptable) {
				IAdaptable ada=(IAdaptable)obj;


				//Start by requesting molecule
				Object molobj=ada
				.getAdapter( net.bioclipse.core.domain.IMolecule.class );
				if (molobj==null ){
					return null;
				}

				net.bioclipse.core.domain.IMolecule bcmol 
				= (net.bioclipse.core.domain.IMolecule) molobj;

				//IF this is a CDK!=mol, return it
				if (bcmol instanceof CDK10Molecule) {
					return (CDK10Molecule) bcmol;
				}

				//Else we need to create it
				//Create cdkmol from IMol, via CML or SMILES if that fails
				try {
					return cdk.create( bcmol );
				} catch (BioclipseException e) {
					logger.error("Could not create cdk10mol from cdkmol in " +
							"JCPPage: " + e.getMessage());
					return null;
				}

				//Set AtomColorer based on active editor
				//RFE: AtomColorer pŒ JCPWidget
				//TODO
			}

		}
		// TODO Auto-generated method stub
		return null;

	}


	/**
	 * Hide canvasview
	 */
	private void clearView() {
		canvasView.setVisible( false );
	}


	private void setAtomContainer(IAtomContainer ac) {
		try {
			canvasView.setAtomContainer(ac);
			canvasView.setVisible( true );
			canvasView.getRendererModel().setShowExplicitHydrogens( false );
		} catch (Exception e) {
			logger.debug("Error displaying molecule in viewer: " + e.getMessage());
		}

	}


	/**
	 * Unsubscriped from listening to the <code>BioResourceView</code> and
	 * delegates to superclass implementations.
	 */
	@Override
	public void dispose() {
		getViewSite().getPage().removeSelectionListener(this);
		canvasView.dispose();
		super.dispose();
	}

}