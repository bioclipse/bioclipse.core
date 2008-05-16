/*
 *  $RCSfile$
 *  $Author: miguelrojasch $
 *  $Date: 2006-10-02 12:52:54 +0200 (Mon, 02 Oct 2006) $
 *  $Revision: 1979 $
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
import java.util.Iterator;

import javax.swing.JOptionPane;

import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;


/**
 * Creates a reaction object
 *
 * @cdk.module jchempaint
 *@author     steinbeck
 */
public class CreateReactionAction extends JCPAction
{

	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	public void run(ActionEvent event)
	{
		IChemObject object = getSource(event);

		logger.debug("CreateReaction action");
		JChemPaintModel jcpmodel = ((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getJcpModel();
		
		IChemModel model = jcpmodel.getChemModel();
		IReactionSet reactionSet = model.getReactionSet();
		if (reactionSet == null)
		{
			reactionSet = model.getBuilder().newReactionSet();
		}
		IAtomContainer container = null;
		if (object instanceof IAtom)
		{
			container = ChemModelManipulator.getRelevantAtomContainer(model, (Atom) object);
		} else
		{
			logger.error("Cannot add to reaction object of type: " + object.getClass().getName());
		}
		if (container == null)
		{
			logger.error("Cannot find container to add object to!");
		} else
		{
			IAtomContainer newContainer = null;
			try {
				newContainer = (AtomContainer) container.clone();
			} catch (CloneNotSupportedException e) {
				logger.error("Could not clone AtomContainer!", e);
				logger.debug(e);
			}
			// delete atoms in current model
			Iterator atomsI = container.atoms();
			while(atomsI.hasNext()){
				ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, (IAtom)atomsI.next());
			}
			logger.debug("Deleted atom from old container...");

			// add reaction
			IReaction reaction = model.getBuilder().newReaction();
			reaction.setID("reaction-" + System.currentTimeMillis());
			logger.debug("type: " + type);
			if ("addReactantToNew".equals(type))
			{
				reaction.addReactant(model.getBuilder().newMolecule(newContainer));
				reactionSet.addReaction(reaction);
			} else if ("addReactantToExisting".equals(type))
			{
				if (reactionSet.getReactionCount() == 0)
				{
					logger.warn("Cannot add to reaction if no one exists");
					// FIXME: give feedback to user
					return;
				} else
				{
//					XXX needs fixing
					Object[] ids = getReactionIDs(reactionSet);
					
					String s = (String) JOptionPane.showInputDialog(
							//jcpPanel.getFrame(),
                            null,
							"Reaction Chooser",
							"Choose reaction to add reaction to",
							JOptionPane.PLAIN_MESSAGE,
							null,
							ids,
							ids[0]
							);
					//String s2 = "";

					if ((s != null) && (s.length() > 0))
					{
						String selectedReactionID = s;
						reaction = getReaction(reactionSet, selectedReactionID);
						reaction.addReactant(model.getBuilder().newMolecule(newContainer));
					} else
					{
						logger.error("No reaction selected");
					}
				}
			} else if ("addProductToNew".equals(type))
			{
				reaction.addProduct(model.getBuilder().newMolecule(newContainer));
				reactionSet.addReaction(reaction);
			} else if ("addProductToExisting".equals(type))
			{
				if (reactionSet.getReactionCount() == 0)
				{
					logger.warn("Cannot add to reaction if no one exists");
					// FIXME: give feedback to user
					return;
				} else
				{
					//XXX needs fixing
					
					Object[] ids = getReactionIDs(reactionSet);
					String s = (String) JOptionPane.showInputDialog(
                            //jcpPanel.getFrame(),
							null,
                            "Reaction Chooser",
							"Choose reaction to add reaction to",
							JOptionPane.PLAIN_MESSAGE,
							null,
							ids,
							ids[0]
							);
					//String s2 = "";

					if ((s != null) && (s.length() > 0))
					{
						String selectedReactionID = s;
						reaction = getReaction(reactionSet, selectedReactionID);
						reaction.addProduct(model.getBuilder().newMolecule(newContainer));
					} else
					{
						logger.error("No reaction selected");
					}
				}
			} else
			{
				logger.warn("Don't know about this action type: " + type);
				return;
			}
		}
		model.setReactionSet(reactionSet);
	}


	/**
	 *  Gets the reactionIDs attribute of the CreateReactionAction object
	 *
	 *@param  reactionSet  Description of the Parameter
	 *@return              The reactionIDs value
	 */
	private Object[] getReactionIDs(IReactionSet reactionSet)
	{
		if (reactionSet != null)
		{
			Iterator reactionsI = reactionSet.reactions();
			String[] ids = new String[reactionSet.getReactionCount()];
			int count = 0;
			while(reactionsI.hasNext()){
				ids[count] = ((IReaction)reactionsI.next()).getID();
				count++;
			}
			return ids;
		} else
		{
			return new String[0];
		}
	}


	/**
	 *  Gets the reaction attribute of the CreateReactionAction object
	 *
	 *@param  reactionSet  Description of the Parameter
	 *@param  id           Description of the Parameter
	 *@return              The reaction value
	 */
	private IReaction getReaction(IReactionSet reactionSet, String id)
	{
		Iterator reactionsI = reactionSet.reactions();
		while(reactionsI.hasNext()){
			IReaction reaction = (IReaction)reactionsI.next();
			if (reaction.getID().equals(id))
			{
				return reaction;
			}
		}
		return null;
	}
}

