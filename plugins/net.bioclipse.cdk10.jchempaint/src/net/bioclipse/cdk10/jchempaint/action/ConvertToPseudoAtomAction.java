/* $RCSfile$
 * $Author: kaihartmann $
 * $Date: 2006-06-06 10:55:35 +0200 (Di, 06 Jun 2006) $
 * $Revision: 6346 $
 *
 * Copyright (C) 2003-2006  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sf.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.bioclipse.cdk10.jchempaint.action;

import java.awt.event.ActionEvent;

import javax.swing.undo.UndoableEdit;

import net.bioclipse.cdk10.jchempaint.ui.editor.MDLMolfileEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.Atom;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.ConvertToPseudoAtomEdit;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
;

/**
 * @cdk.module jchempaint
 */
public class ConvertToPseudoAtomAction extends JCPAction {

	private static final long serialVersionUID = -598284013998335002L;

	public void run(ActionEvent event) {
        logger.debug("Converting to: " + type);
        IChemObject object = getSource(event);
        JChemPaintModel jcpmodel = ((MDLMolfileEditor)this.getContributor().getActiveEditorPart()).getJcpModel();
        org.openscience.cdk.interfaces.IChemModel model = jcpmodel.getChemModel();
        if (object != null) {
            if (object instanceof Atom) {
                Atom atom = (Atom)object;
                IAtomContainer relevantContainer = ChemModelManipulator.getRelevantAtomContainer(model, atom);
                PseudoAtom pseudo = new PseudoAtom(atom);
                pseudo.setLabel(type);
                AtomContainerManipulator.replaceAtomByAtom(relevantContainer, 
                    atom, pseudo);
                UndoableEdit  edit = new ConvertToPseudoAtomEdit(relevantContainer, 
                        atom, pseudo);
//                UndoableAction.pushToUndoRedoStack(edit,jcpmodel,((MDLMolfileEditor)this.getContributor().getActiveEditorPart()).getUndoContext(), ((MDLMolfileEditor)this.getContributor().getActiveEditorPart()).getDrawingPanel());
            } else {
                logger.error("Object not an Atom! Cannot convert into a PseudoAtom!");
            }
        } else {
            logger.warn("Cannot convert a null object!");
        }
        jcpmodel.fireChange();
    }
}
