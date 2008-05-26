/*
 *  $RCSfile: AdjustBondOrdersAction.java,v $
 *  $Author: shk3 $
 *  $Date: 2005/12/07 13:52:29 $
 *  $Revision: 1.12 $
 *
 *  Copyright (C) 2003-2005  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sf.net
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

import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.AdjustBondOrdersEdit;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;


/**
 * Triggers the adjustment of BondOrders
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class AdjustBondOrdersAction extends JCPAction
{

	
	public void run() {
		run(null);
	}
	
	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of the Parameter
	 */
	public void run(ActionEvent e)
	{
        HashMap changedBonds = null;
        JChemPaintModel jcpModel=null;
        if ( this.getContributor().getActiveEditorPart() instanceof IJCPBasedEditor ) {
            IJCPBasedEditor ed = (IJCPBasedEditor) this.getContributor().getActiveEditorPart();
            jcpModel = ed.getJcpModel();
        }
        else if (this.getContributor().getActiveEditorPart() instanceof JCPPage ){
            JCPPage ed = (JCPPage) this.getContributor().getActiveEditorPart();
            jcpModel = ed.getJcpModel();
        }

        ChemModel model = (ChemModel) jcpModel.getChemModel();
		logger.debug("Adjusting bondorders: " + type);
		if (type.equals("clear"))
		{
			try
			{
				SaturationChecker satChecker = new SaturationChecker();
                changedBonds = new HashMap();
				Iterator containersI = ChemModelManipulator.getAllAtomContainers(model).iterator();
				while(containersI.hasNext()){
					IAtomContainer container = (IAtomContainer) containersI.next();
                    IAtomContainer containerCopy = (IAtomContainer) container.clone();
					satChecker.unsaturate(container);
                     for (int j=0; j<containerCopy.getBondCount(); j++) {
                    	 IBond bondCopy = containerCopy.getBond(j);
                    	 IBond bond = container.getBond(j);
                            if (bond.getOrder() != bondCopy.getOrder()) {
                                double[] bondOrders = new double[2];
                                bondOrders[0] = bond.getOrder();
                                bondOrders[1] = bondCopy.getOrder();
                                changedBonds.put(bond, bondOrders);
                            }
                        }
				}
				jcpModel.fireChange();
			} catch (Exception exc)
			{
				String error = "Could not adjust bondorders.";
				logger.error(error);
				logger.debug(exc);
				//TODO
//				JOptionPane.showMessageDialog(jcpPanel, error);
			}
		} else
		{
			try
			{
				SaturationChecker satChecker = new SaturationChecker();
                changedBonds = new HashMap();
                Iterator containersI = ChemModelManipulator.getAllAtomContainers(model).iterator();
                while(containersI.hasNext()){
                	IAtomContainer container = (IAtomContainer) containersI.next();
                   AtomContainer containerCopy = (AtomContainer) container.clone();
					satChecker.saturate(container);
                    for (int j=0; j<containerCopy.getBondCount(); j++) {
                    	IBond bondCopy = containerCopy.getBond(j);
                    	IBond bond = container.getBond(j);
                        if (bond.getOrder() != bondCopy.getOrder()) {
                            double[] bondOrders = new double[2];
                            bondOrders[0] = bond.getOrder();
                            bondOrders[1] = bondCopy.getOrder();
                            changedBonds.put(bond, bondOrders);
                        }
                    }
				}
				jcpModel.fireChange();
			} catch (Exception exc)
			{
				String error = "Could not adjust bondorders.";
				logger.error(error);
				logger.debug(exc);
				//TODO
//				JOptionPane.showMessageDialog(jcpPanel, error);
			}
		}
        UndoableEdit  edit = new AdjustBondOrdersEdit(changedBonds);
        JChemPaintModel jcpmodel = ((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getJcpModel();
//        UndoableAction.pushToUndoRedoStack(edit,jcpmodel,((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getUndoContext(), ((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getDrawingPanel());
	}
}

