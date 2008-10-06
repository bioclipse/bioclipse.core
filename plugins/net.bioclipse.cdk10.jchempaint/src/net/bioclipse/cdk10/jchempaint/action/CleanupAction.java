/*
 *  $RCSfile: CleanupAction.java,v $
 *  $Author: shk3 $
 *  $Date: 2006/01/06 10:45:47 $
 *  $Revision: 1.21.2.3 $
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
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
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.undo.UndoableEdit;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.CleanUpEdit;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.renderer.Renderer2DModel;


/**
 * Triggers the invocation of the structure diagram generator
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class CleanupAction extends JCPAction
{

	private StructureDiagramGenerator diagramGenerator;
    

	public void run() {
		run(null);			
	}

	/**
	 *  Relayouts a molecule
	 *
	 *@param  e  Description of the Parameter
	 */
	public void run(ActionEvent e)
	{	
		HashMap atomCoordsMap = new HashMap();

		JChemPaintModel jcpmodel=null;
		if ( this.getContributor().getActiveEditorPart() instanceof IJCPBasedEditor ) {
        IJCPBasedEditor ed = (IJCPBasedEditor) this.getContributor().getActiveEditorPart();
        jcpmodel = ed.getJcpModel();
    }
		else if (this.getContributor().getActiveEditorPart() instanceof JCPPage ){
        JCPPage ed = (JCPPage) this.getContributor().getActiveEditorPart();
        jcpmodel = ed.getJcpModel();
		}
		
        logger.info("Going to performe a clean up...");
		if (jcpmodel != null)
		{
			if (diagramGenerator == null) {
                diagramGenerator = new StructureDiagramGenerator();
                diagramGenerator.setTemplateHandler(
                    new TemplateHandler(jcpmodel.getChemModel().getBuilder())
                );
            }
			Renderer2DModel renderModel = jcpmodel.getRendererModel();
			double bondLength = renderModel.getBondLength() / renderModel.getScaleFactor();
			diagramGenerator.setBondLength(bondLength * 2.0);
			// FIXME this extra factor should not be necessary
			logger.debug("getting ChemModel");
			IChemModel model = jcpmodel.getChemModel();
			logger.debug("got ChemModel");
			IMoleculeSet som = model.getMoleculeSet();
			if (som != null)
			{
                
				logger.debug("no mols in som: " + som.getMoleculeCount());
				MoleculeSet newsom = new MoleculeSet();
				Iterator molsI = som.molecules();
				while(molsI.hasNext()){
					IMolecule mol = (IMolecule)molsI.next();
                    IMolecule molecule = mol;
                    IMolecule cleanedMol = relayoutMolecule(mol);
					newsom.addMolecule(cleanedMol);
//                  IAtom[] atoms = molecule.getAtoms();
//					IAtom[] newAtoms = cleanedMol.getAtoms();
                    for (int j=0; j< molecule.getAtomCount(); j++) {
                        Point2d oldCoord = molecule.getAtom(j).getPoint2d();
                        Point2d newCoord = cleanedMol.getAtom(j).getPoint2d();
                         if (!oldCoord.equals(newCoord)) {
                            Point2d[] coords = new Point2d[2];
                            coords[0] = newCoord;
                            coords[1] = oldCoord;
                            atomCoordsMap.put(cleanedMol.getAtom(j), coords);
                        }
                    }
				}
				model.setMoleculeSet(newsom);
                
                UndoableEdit  edit = new CleanUpEdit(atomCoordsMap, jcpmodel.getRendererModel());
                
//                UndoableAction.pushToUndoRedoStack(edit,jcpmodel,((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getUndoContext(), ((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getDrawingPanel());
			}
			IReactionSet reactionSet = model.getReactionSet();
			if (reactionSet != null)
			{
				ReactionSet newSet = new ReactionSet();
				// FIXME, this does not preserve reactionset properties!
				Iterator reactionsI = reactionSet.reactions();
				while(reactionsI.hasNext()){
					IReaction reaction = (IReaction)reactionsI.next();
					Reaction newReaction = new Reaction();
					// FIXME, this does not preserve reaction properties!
					Iterator reactantsI = reaction.getReactants().molecules();
					while(reactantsI.hasNext()){
						newReaction.addReactant(relayoutMolecule((IMolecule)reactantsI.next()));
					}
					Iterator productsI = reaction.getProducts().molecules();
					while(productsI.hasNext()){
						newReaction.addProduct(relayoutMolecule((IMolecule)productsI.next()));
					}
					newSet.addReaction(newReaction);
				}
				model.setReactionSet(newSet);
			}

			jcpmodel.getRendererModel().setSelectedPart(new AtomContainer());
			jcpmodel.fireChange();
//			DrawingPanel drawingPanel = ((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getDrawingPanel();
//			drawingPanel.updateRingSetInRenderer();
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  molecule  Description of the Parameter
	 *@return           Description of the Return Value
	 */
	private IMolecule relayoutMolecule(IMolecule molecule)
	{
	    JChemPaintModel jcpmodel=null;
	    if ( this.getContributor().getActiveEditorPart() instanceof IJCPBasedEditor ) {
	        IJCPBasedEditor ed = (IJCPBasedEditor) this.getContributor().getActiveEditorPart();
	        jcpmodel = ed.getJcpModel();
	    }
	    else if (this.getContributor().getActiveEditorPart() instanceof JCPPage ){
	        JCPPage ed = (JCPPage) this.getContributor().getActiveEditorPart();
	        jcpmodel = ed.getJcpModel();
	    }
		IMolecule cleanedMol = molecule;
       if (molecule != null)
		{
			if (molecule.getAtomCount() > 2)
			{
				try
				{
			    	Point2d centre = GeometryTools.get2DCentreOfMass(molecule,jcpmodel.getRendererModel().getRenderingCoordinates());
					diagramGenerator.setMolecule(molecule);
					diagramGenerator.generateExperimentalCoordinates(new Vector2d(0, 1));
					cleanedMol = diagramGenerator.getMolecule();
                    /*
					 *  make the molecule end up somewhere reasonable
					 *  See constructor of JCPPanel
					 */
					Thread.sleep(5000);
					GeometryTools.translateAllPositive(cleanedMol,jcpmodel.getRendererModel().getRenderingCoordinates());
					double scaleFactor = GeometryTools.getScaleFactor(cleanedMol, jcpmodel.getRendererModel().getBondLength(),jcpmodel.getRendererModel().getRenderingCoordinates());
					GeometryTools.scaleMolecule(cleanedMol, scaleFactor,jcpmodel.getRendererModel().getRenderingCoordinates());
					GeometryTools.translate2DCentreOfMassTo(cleanedMol, centre,jcpmodel.getRendererModel().getRenderingCoordinates());
				} catch (Exception exc)
				{
					logger.error("Could not generate coordinates for molecule");
					logger.debug(exc);
				}
			} else
			{
				logger.info("Molecule with less than 2 atoms are not cleaned up");
			}
		} else
		{
			logger.error("Molecule is null! Cannot do layout!");
		}
		return cleanedMol;
	}
}

