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



import net.bioclipse.cdk10.business.CDK10Manager;
import net.bioclipse.cdk10.business.CDK10Molecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.AtomIndexSelection;
import net.bioclipse.core.domain.IChemicalSelection;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
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
    private final static StructureDiagramGenerator sdg = new StructureDiagramGenerator();
    
    private IChemObjectBuilder chemObjBuilder;

    private CDK10Manager cdk;
    
    /**
     * The constructor.
     */
    public Java2DRendererView() {
        
        chemObjBuilder=DefaultChemObjectBuilder.getInstance();
        cdk=new CDK10Manager();
        
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
        
        if (!(selection instanceof IStructuredSelection))
        return;
        IStructuredSelection ssel = (IStructuredSelection) selection;
        
        Object obj = ssel.getFirstElement();
        
        updateByObject(obj);
        
    }

    /**
     * Set content in view based on input object.
     * @param obj the input object
     */
	private void updateByObject(Object obj) {
		
//		System.out.println("Updating by object in 2dview: " + obj);
		
		//If we have an ICDKMolecule, just get the AC
        if (obj instanceof CDK10Molecule) {
            CDK10Molecule mol = (CDK10Molecule) obj;
            if (mol.getAtomContainer()==null){
                logger.debug("CDKMolecule but can't get AtomContainer.");
                return;
            }
            setAtomContainer(mol.getAtomContainer());
        }
        
        //Try to get an IMolecule via the adapter
        else if (obj instanceof IAdaptable) {
            IAdaptable ada=(IAdaptable)obj;

            
            //Start by requesting molecule
            Object molobj=ada
                    .getAdapter( net.bioclipse.core.domain.IMolecule.class );
            if (molobj==null ){
                //Nothing to show
//                clearView();
                return;
            }

            net.bioclipse.core.domain.IMolecule bcmol 
                                = (net.bioclipse.core.domain.IMolecule) molobj;
            
            try {

                //Create cdkmol from IMol, via CML or SMILES if that fails
                CDK10Molecule cdkMol=cdk.create( bcmol );

                //Create molecule
                IAtomContainer ac=cdkMol.getAtomContainer();
                molecule=new Molecule(ac);

                //Create 2D-coordinates if not available
                if (GeometryTools.has2DCoordinatesNew( molecule )==0){
                    sdg.setMolecule((IMolecule)molecule.clone());
                    sdg.generateCoordinates();
                    molecule = sdg.getMolecule();
                }
                
                //Set AtomColorer based on active editor
                //RFE: AtomColorer på JCPWidget
                //TODO

                //Update widget
                setAtomContainer(molecule);
            } catch (CloneNotSupportedException e) {
                clearView();
                logger.debug( "Unable to clone structure in 2Dview: " 
                              + e.getMessage() );
            } catch ( BioclipseException e ) {
                clearView();
                logger.debug( "Unable to generate structure in 2Dview: " 
                              + e.getMessage() );
            } catch ( Exception e ) {
                clearView();
                logger.debug( "Unable to generate structure in 2Dview: " 
                              + e.getMessage() );
            }

            
            //Handle case where Iadaptable can return atoms to be highlighted
            Object selobj=ada
            .getAdapter( IChemicalSelection.class );
//            ArrayList<Integer> atomSelectionIndices=new ArrayList<Integer>();

            if (selobj!=null){
                IChemicalSelection atomSelection=(IChemicalSelection)selobj;
                
                if ( atomSelection instanceof AtomIndexSelection ) {
                    AtomIndexSelection isel = (AtomIndexSelection) atomSelection;
                    int[] selindices = isel.getSelection();
//                    System.out.println("\n** Should highlight these JCP atoms:\n");
                    IAtomContainer selectedMols=new AtomContainer();
                    for (int i=0; i<selindices.length;i++){
                        selectedMols.addAtom( molecule.getAtom( selindices[i] ));
//                        System.out.println(i);
                    }
                    canvasView.getRendererModel().setExternalSelectedPart( selectedMols );
                    canvasView.redraw();
                }
                
                
            }

            
        }
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
                canvasView.redraw();
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