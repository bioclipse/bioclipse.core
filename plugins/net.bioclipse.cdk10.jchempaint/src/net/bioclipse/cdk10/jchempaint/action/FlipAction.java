/* $RCSfile: FlipAction.java,v $
 * $Author: shk3 $
 * $Date: 2006/01/10 12:04:07 $
 * $Revision: 1.9.2.1 $
 * 
 * Copyright (C) 2005  The JChemPaint project
 * 
 * Contact: jchempaint-devel@lists.sourceforge.net
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
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.undo.UndoableEdit;
import javax.vecmath.Point2d;

import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.FlipEdit;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * Action to copy/paste structures.
 *
 * @cdk.module jchempaint
 * @author     Egon Willighagen <e.willighagen@science.ru.nl>
 */
public class FlipAction extends JCPAction {

	public void run() {
		run(null);
	}
    public void run(ActionEvent e) {
        logger.info("  type  " + type);
//        logger.debug("  source ", e.getSource());
        HashMap atomCoordsMap = new HashMap();
        JChemPaintModel jcpModel = ((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getJcpModel();
        Renderer2DModel renderModel = jcpModel.getRendererModel();
        boolean horiz = "horizontal".equals(type);
        IAtomContainer object = null;
        if (renderModel.getSelectedPart() != null) {
        	object = renderModel.getSelectedPart();
        }
        //if nothing selected flip the whole structure
        else {
        	object = jcpModel.getChemModel().getMoleculeSet().getAtomContainer(0);
        }
        if (object != null && (horiz || "vertical".equals(type))) {
            IAtomContainer toflip = object;
            Point2d center = GeometryTools.get2DCenter(toflip,renderModel.getRenderingCoordinates());
            Iterator atomsI = toflip.atoms();
            while(atomsI.hasNext()){
            	IAtom at = (IAtom)atomsI.next();
            	Point2d atom = renderModel.getRenderingCoordinate(at);
//                Point2d atom = atoms[i].getPoint2d();
                Point2d oldCoord = new Point2d(atom.x, atom.y);
                if (horiz) {
                    atom.y = 2.0*center.y - atom.y;
                } else {
                    atom.x = 2.0*center.x - atom.x;
                }
                Point2d newCoord = atom;
                if (!oldCoord.equals(newCoord)) {
                    Point2d[] coords = new Point2d[2];
                    coords[0] = newCoord;
                    coords[1] = oldCoord;
                    atomCoordsMap.put(at, coords);
                }
            }
            UndoableEdit  edit = new FlipEdit(atomCoordsMap);
//            UndoableAction.pushToUndoRedoStack(edit,jcpModel,((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getUndoContext(), ((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getDrawingPanel());
            // fire a change so that the view gets updated
            jcpModel.fireChange();
//            DrawingPanel drawingPanel = ((IJCPBasedEditor)this.getContributor().getActiveEditorPart()).getDrawingPanel();
//            drawingPanel.repaint();
        }
    }

}

