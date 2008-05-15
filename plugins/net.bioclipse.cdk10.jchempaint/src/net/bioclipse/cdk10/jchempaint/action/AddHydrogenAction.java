/*
 *  $RCSfile$
 *  $Author: shk3 $
 *  $Date: 2007-07-11 13:38:05 +0200 (Wed, 11 Jul 2007) $
 *  $Revision: 3488 $
 *
 *  Copyright (C) 2003-2005  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.bioclipse.cdk10.jchempaint.action;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.undo.UndoableEdit;

import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.DrawingPanel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.AddHydrogenEdit;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;


/**
 * An action triggering the addition of hydrogens to 
 * selected structures
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class AddHydrogenAction extends JCPAction
{

	private HydrogenAdder hydrogenAdder = null;
	private IAtomContainer changedAtomsAndBonds = null;
    private HashMap hydrogenAtomMap = null;
	private JChemPaintModel jcpmodel;

	/**
	 *  Constructor for the JCPAction object
	 *
	 *@param  jcpPanel       Description of the Parameter
	 *@param  isPopupAction  Description of the Parameter
	 */
	public AddHydrogenAction(String text, int style)
	{
		super(text, style);
	}

	/**
	 *  Constructor for the JCPAction object
	 *
	 *@param  jcpPanel       Description of the Parameter
	 *@param  isPopupAction  Description of the Parameter
	 */
	public AddHydrogenAction()
	{
		super();
	}
	
	public void run() {
		//TODO propably all actions on swing composite should work like this...
		EventQueue.invokeLater(new Runnable() {;
		public void run() {
			AddHydrogenAction.this.run(null);			
		} });
	}
	
	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	public void run(ActionEvent event)
	{
        this.hydrogenAtomMap = null;
        this.changedAtomsAndBonds = null;
		logger.debug("Trying to add hydrogen in mode: " +  type);
		if (hydrogenAdder == null)
		{
			hydrogenAdder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");
		}
		jcpmodel = ((JCPPage)this.getContributor().getActiveEditorPart()).getJcpModel();
		if (jcpmodel != null)
		{
			IChemObject object = null;
			if (event == null) {
				object = jcpmodel.getRendererModel().getSelectedPart();
			}
			else {
				object = getSource(event);
			}
			// now add hydrogens
			IChemModel model = jcpmodel.getChemModel();
            
//			IChemObject object = getSource(event);
			if (object != null)
			{
				if (object instanceof Atom)
				{
					logger.debug("Adding hydrogens to this specific atom");
					Atom atom = (Atom) object;
                    addHydrogenToOneAtom(ChemModelManipulator.getRelevantAtomContainer(model, atom), atom);
				} else if (object instanceof ChemModel) {
					logger.debug("Adding hydrogens to all atoms");
					addHydrogenToAllAtoms((IChemModel) object);
				}else if (object instanceof IAtomContainer){
					logger.debug("Adding hydrogens to selected atoms");
					addHydrogenToSelectedAtoms((IAtomContainer) object);
				} else {
					logger.error("Can only add hydrogens to Atom's");
				}
			} else
			{
                logger.debug("Adding hydrogens to all atoms");
				addHydrogenToAllAtoms(model);
			}
            UndoableEdit edit = null;
            if (type.equals("explicit")) {
                edit = new  AddHydrogenEdit(model, changedAtomsAndBonds);
            }
            else if ( type.equals("implicit")) {
                edit = new  AddHydrogenEdit(model, hydrogenAtomMap);
            }
            else if (type.equals("allimplicit")) {
               edit = new  AddHydrogenEdit(model, hydrogenAtomMap);
            }
//            UndoableAction.pushToUndoRedoStack(edit,jcpmodel,((JCPPage)this.getContributor().getActiveEditorPart()).getUndoContext(), ((JCPPage)this.getContributor().getActiveEditorPart()).getDrawingPanel());
			if (type.equals("implicit"))
			{
				if(!jcpmodel.getControllerModel().getAutoUpdateImplicitHydrogens()){
					jcpmodel.getControllerModel().setAutoUpdateImplicitHydrogens(true);
				}else{
					jcpmodel.getControllerModel().setAutoUpdateImplicitHydrogens(false);
				}
			}
            jcpmodel.fireChange();
//			DrawingPanel drawingPanel = ((JCPPage)this.getContributor().getActiveEditorPart()).getDrawingPanel();
//			drawingPanel.repaint();
		}
	}


	private void addHydrogenToSelectedAtoms(IAtomContainer ac) {
		Iterator atomsI = ac.atoms();
		while(atomsI.hasNext()){
			IAtom atom = (IAtom) atomsI.next();
			addHydrogenToOneAtom(ChemModelManipulator.getRelevantAtomContainer(jcpmodel.getChemModel(), atom), atom);
		}
	}

	/**
	 *  Adds a feature to the HydrogenToAllAtoms attribute of the AddHydrogenAction
	 *  object
	 *
	 *@param  model  The feature to be added to the HydrogenToAllAtoms attribute
	 */
	private void addHydrogenToAllAtoms(IChemModel model)
	{
		IMoleculeSet som = model.getMoleculeSet();
		IReactionSet sor = model.getReactionSet();
		if (som != null)
		{
			addHydrogenToAllMolecules(som);
		} else if (sor != null)
		{
			logger.debug("#reactions " + sor.getReactionCount());
			som = ReactionSetManipulator.getAllMolecules(sor);
			logger.debug("Found molecules: " + som.getMoleculeCount());
			addHydrogenToAllMolecules(som);
		}
	}


	/**
	 *  Adds a feature to the HydrogenToAllMolecules attribute of the
	 *  AddHydrogenAction object
	 *
	 *@param  som  The feature to be added to the HydrogenToAllMolecules attribute
	 */
	private void addHydrogenToAllMolecules(IMoleculeSet som)
	{
		Controller2DModel controllerModel = jcpmodel.getControllerModel();
        try
		{
        	Iterator molsI = som.molecules();
        	while(molsI.hasNext()){
				IMolecule molecule = (IMolecule)molsI.next();
				if (molecule != null)
				{
					if (type.equals("implicit"))
					{
						if(!controllerModel.getAutoUpdateImplicitHydrogens()){
							hydrogenAtomMap = hydrogenAdder.addImplicitHydrogensToSatisfyValency(molecule);
						}else{
							Iterator atomsI = molecule.atoms();
							while(atomsI.hasNext()){
								((IAtom)atomsI.next()).setHydrogenCount(0);
							}
						}
					} else if (type.equals("explicit"))
					{
						double bondLength = GeometryTools.getBondLengthAverage(molecule,jcpmodel.getRendererModel().getRenderingCoordinates());
						if (Double.isNaN(bondLength))
						{
							logger.warn("Could not determine average bond length from structure!");
							bondLength = controllerModel.getBondPointerLength();
						}
                        changedAtomsAndBonds = hydrogenAdder.addExplicitHydrogensToSatisfyValency(molecule);
                        HydrogenPlacer hPlacer = new HydrogenPlacer();
						hPlacer.placeHydrogens2D(molecule, bondLength, jcpmodel.getRendererModel().getRenderingCoordinates());
					} else if (type.equals("allimplicit"))
					{
							// remove explicit hydrogen if necessary
							for(int i=molecule.getAtomCount()-1;i>=0;i--){
								IAtom atom = molecule.getAtom(i);
								if (atom.getSymbol().equals("H"))
								{
									logger.debug("Atom is a hydrogen");
									molecule.removeAtomAndConnectedElectronContainers(atom);
								}
							}
							// add implicit hydrogen
	                        hydrogenAtomMap = hydrogenAdder.addImplicitHydrogensToSatisfyValency(molecule);
					}
				} else
				{
					logger.error("Molecule is null! Cannot add hydrogens!");
				}
			}
		} catch (Exception exc)
		{
			logger.error("Error while adding hydrogen: " + exc.getMessage());
			logger.debug(exc);
		}
	}


	/**
	 *  Adds a feature to the HydrogenToOneAtom attribute of the AddHydrogenAction
	 *  object
	 *
	 *@param  container  The feature to be added to the HydrogenToOneAtom attribute
	 *@param  atom       The feature to be added to the HydrogenToOneAtom attribute
	 */
	private void addHydrogenToOneAtom(IAtomContainer container, IAtom atom)
	{
		Controller2DModel controllerModel = jcpmodel.getControllerModel();
		try
		{
			if (type.equals("implicit"))
			{
				int[] hydrogens = hydrogenAdder.addImplicitHydrogensToSatisfyValency(container, atom);
               hydrogenAtomMap.put(atom, hydrogens);
//                changedAtomsAndBonds = hydrogenAdder.addImplicitHydrogensToSatisfyValency(container, atom);
			} else if (type.equals("explicit"))
			{
				double bondLength = GeometryTools.getBondLengthAverage(container,jcpmodel.getRendererModel().getRenderingCoordinates());
				if (Double.isNaN(bondLength))
				{
					logger.warn("Could not determine average bond length from structure!");
					bondLength = controllerModel.getBondPointerLength();
				}
//                hydrogenAdder.addExplicitHydrogensToSatisfyValency(container, atom, container);
                changedAtomsAndBonds = hydrogenAdder.addExplicitHydrogensToSatisfyValency(container, atom, container);
				HydrogenPlacer hPlacer = new HydrogenPlacer();
				hPlacer.placeHydrogens2D(container, atom, bondLength);
			}
		} catch (Exception exc)
		{
			logger.error("Error while adding hydrogen: " + exc.getMessage());
			logger.debug(exc);
		}
	}
}

