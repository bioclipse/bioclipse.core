/*
 *  $RCSfile$
 *  $Author: shk3 $
 *  $Date: 2007-05-21 22:40:02 +0200 (Mon, 21 May 2007) $
 *  $Revision: 3111 $
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

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.JOptionPane;

import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.applications.jchempaint.DrawingPanel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * This class implements editing options from the 'Edit' menu.
 * These actions are implemented:
 * <ul>
 *   <li>cut, deletes all atoms and connected electron containers
 *   <li>cutSelected, deletes all selected atoms and electron containers
 *   <li>selectAll, selects all atoms and electron containers
 *   <li>selectFromChemObject,selects all atoms and electron containers in
 *       the ChemObject set in the event source
 * </ul>
 *
 * @author        hel
 * @cdk.created       27. April 2005
 * @cdk.module    jchempaint
 */
public class EditAction extends JCPAction {

	
	public void run() {
		run(null);
	}
	/**
	 *  Description of the Method
	 *
	 * @param  event  Description of the Parameter
	 */
	public void run(ActionEvent event) {
		
//		// learn some stuff about event
//		logger.debug("Event source: ", event.getSource().getClass().getName());
//		logger.debug("  ChemObject: ", getSource(event));
		JChemPaintModel jcpModel = ((JCPPage)this.getContributor().getActiveEditorPart()).getJcpModel();
		Renderer2DModel renderModel = jcpModel.getRendererModel();
		IChemModel chemModel = jcpModel.getChemModel();
		
		IChemObject object = null;
		if (event == null) {
			object = renderModel.getSelectedPart();
		}
		else {
			object = getSource(event);
		}
		
		if (type.equals("cut")) {
			IAtom atomInRange = null;
//			object = getSource(event);
			logger.debug("Source of call: " + object);
			if (object instanceof Atom) {
				atomInRange = (Atom) object;
			}
			else {
				atomInRange = renderModel.getHighlightedAtom();
			}
			if (atomInRange != null) {
				ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, atomInRange);
			}
			else {
				IBond bond = renderModel.getHighlightedBond();
				if (bond != null) {
					ChemModelManipulator.removeElectronContainer(chemModel, bond);
				}
			}
			jcpModel.fireChange();
		}
		else if (type.equals("cutSelected")) {
			logger.debug("Deleting all selected atoms...");
			if (renderModel.getSelectedPart() == null || renderModel.getSelectedPart().getAtomCount() == 0) {
//				DrawingPanel drawingPanel = ((JCPPage)this.getContributor().getActiveEditorPart()).getDrawingPanel();
//				JOptionPane.showMessageDialog(drawingPanel, "No selection made. Please select some atoms first!", "Error warning", JOptionPane.WARNING_MESSAGE);
			}
			else {
				Iterator selectedI = renderModel.getSelectedPart().atoms();
				logger.debug("Found # atoms to delete: " + renderModel.getSelectedPart().getAtomCount());
				while (selectedI.hasNext()){
					ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, (IAtom)selectedI.next());
				}
			}
			renderModel.setSelectedPart(new org.openscience.cdk.AtomContainer());
			jcpModel.fireChange();
		}
		else if (type.equals("selectAll")) {
			renderModel.setSelectedPart(jcpModel.getChemModel().getMoleculeSet().getAtomContainer(0));
			//TODO needs some workaround for the eclipse toolbar buttons...
//			((JButton)jcpPanel.lastAction.get(0)).setBackground(Color.LIGHT_GRAY);
//			jcpPanel.lastAction.set(0,jcpPanel.getMoveButton());
//			jcpPanel.getMoveButton().setBackground(Color.GRAY);
			jcpModel.getControllerModel().setDrawMode(Controller2DModel.MOVE);
			jcpModel.fireChange();
		} else if (type.equals("selectMolecule")) {
//			object = getSource(event);
			if (object instanceof Atom) {
				renderModel.setSelectedPart(ChemModelManipulator.getRelevantAtomContainer(jcpModel.getChemModel(),(Atom)object));
			} else if (object instanceof IBond) {
				renderModel.setSelectedPart(ChemModelManipulator.getRelevantAtomContainer(jcpModel.getChemModel(),(Bond)object));
			} else {
				logger.warn("selectMolecule not defined for the calling object " + object);
			}
			jcpModel.fireChange();
		} else if (type.equals("selectFromChemObject")) {
			// FIXME: implement for others than Reaction, Atom, Bond
//			IChemObject object = getSource(event);
			if (object instanceof Atom) {
				AtomContainer container = new org.openscience.cdk.AtomContainer();
				container.addAtom((Atom) object);
				renderModel.setSelectedPart(container);
				jcpModel.fireChange();
			}
			else if (object instanceof IBond) {
				AtomContainer container = new org.openscience.cdk.AtomContainer();
				container.addBond((Bond) object);
				renderModel.setSelectedPart(container);
				jcpModel.fireChange();
			}
			else if (object instanceof Reaction) {
				//TODO I changed this because of disappearing getAllInOneMethods, but unclear if right
				renderModel.setSelectedPart(((Reaction) object).getReactants().getAtomContainer(0));
				jcpModel.fireChange();
			}
			else {
				logger.warn("Cannot select everything in : " + object);
			}
		}
		else if (type.equals("selectReactionReactants")) {
//			IChemObject object = getSource(event);
			if (object instanceof Reaction) {
				Reaction reaction = (Reaction) object;
				//TODO I changed this because of disappearing getAllInOneMethods, but unclear if right
				renderModel.setSelectedPart(reaction.getReactants().getAtomContainer(0));
				jcpModel.fireChange();
			}
			else {
				logger.warn("Cannot select reactants from : " + object);
			}
		}
		else if (type.equals("selectReactionProducts")) {
//			IChemObject object = getSource(event);
			if (object instanceof Reaction) {
				Reaction reaction = (Reaction) object;
				//TODO I changed this because of disappearing getAllInOneMethods, but unclear if right
				renderModel.setSelectedPart(reaction.getProducts().getAtomContainer(0));
				jcpModel.fireChange();
			}
			else {
				logger.warn("Cannot select reactants from : " + object);
			}
		}
		else {
			logger.warn("Unsupported EditAction: " + type);
		}
	}

}

