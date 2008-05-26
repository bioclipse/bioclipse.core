/*
 *  $RCSfile$
 *  $Author: tohel $
 *  $Date: 2006-06-26 10:42:54 +0200 (Mon, 26 Jun 2006) $
 *  $Revision: 1053 $
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

import javax.swing.undo.UndoableEdit;

import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.ChangeAtomSymbolEdit;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.controller.Controller2DModel;


/**
 * changes the atom symbol
 * @cdk.module jchempaint
 * @author     Egon Willighagen
 */
public class ChangeAtomSymbolAction extends JCPAction
{

	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	public void run(ActionEvent event) 
	{
		logger.debug("About to change atom type of relevant atom!");
		
    JChemPaintModel jcpm=null;
    if ( this.getContributor().getActiveEditorPart() instanceof IJCPBasedEditor ) {
        IJCPBasedEditor ed = (IJCPBasedEditor) this.getContributor().getActiveEditorPart();
        jcpm = ed.getJcpModel();
    }
    else if (this.getContributor().getActiveEditorPart() instanceof JCPPage ){
        JCPPage ed = (JCPPage) this.getContributor().getActiveEditorPart();
        jcpm = ed.getJcpModel();
    }
	
		
		if (jcpm != null)
		{
			Controller2DModel c2dm = jcpm.getControllerModel();
			Atom atomInRange = null;
			ChemObject object = getSource(event);
			logger.debug("Source of call: " +  object);
			if (object instanceof Atom)
			{
				atomInRange = (Atom) object;
			} else
			{
				atomInRange = (Atom) jcpm.getRendererModel().getHighlightedAtom();
			}
            String formerSymbol = atomInRange.getSymbol();
			String s = event.getActionCommand();
			String symbol = s.substring(s.indexOf("@") + 1);
            atomInRange.setSymbol(symbol);
			// modify the current atom symbol
			c2dm.setDrawElement(symbol);
			// configure the atom, so that the atomic number matches the symbol
			try
			{
				IsotopeFactory.getInstance(atomInRange.getBuilder()).configure(atomInRange);
			} catch (Exception exception)
			{
				logger.error("Error while configuring atom");
				logger.debug(exception);
			}
            UndoableEdit  edit = new ChangeAtomSymbolEdit(atomInRange, formerSymbol, symbol);
//            UndoableAction.pushToUndoRedoStack(edit,jcpm,((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getUndoContext(), ((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getDrawingPanel());
			jcpm.fireChange();
		}
	}

}

