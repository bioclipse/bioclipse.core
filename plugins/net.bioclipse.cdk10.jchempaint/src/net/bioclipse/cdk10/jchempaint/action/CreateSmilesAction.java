/*
 *  $RCSfile: CreateSmilesAction.java,v $
 *  $Author: shk3 $
 *  $Date: 2005/12/09 17:03:43 $
 *  $Revision: 1.9 $
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

import javax.swing.JFrame;

import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.dialogs.TextViewDialog;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.HydrogenAdder;


/**
 * Creates a SMILES from the current model
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class CreateSmilesAction extends JCPAction
{

	TextViewDialog dialog = null;
	JFrame frame = null;


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
		logger.debug("Trying to create smile: " + type);
		if (dialog == null)
		{
//			dialog = new TextViewDialog(this.getContributor().getActiveEditorPart().getEditorSite().getShell(), 0, "Generated SMILES:", 40, 2);
			// FIXME: show SMILES
		}
		String smiles = "";
		String chiralsmiles ="";
		try
		{
			JChemPaintModel jcpModel = ((JCPPage)this.getContributor().getActiveEditorPart()).getJcpModel();
			ChemModel model = (ChemModel) jcpModel.getChemModel();
            SmilesGenerator generator = new SmilesGenerator();
			IAtomContainer container = model.getMoleculeSet().getAtomContainer(0);
			Molecule molecule = new Molecule(container);
			Molecule moleculewithh=(Molecule)molecule.clone();
			new HydrogenAdder().addExplicitHydrogensToSatisfyValency(moleculewithh);
			double bondLength = GeometryTools.getBondLengthAverage(container,jcpModel.getRendererModel().getRenderingCoordinates());
		    new HydrogenPlacer().placeHydrogens2D(moleculewithh, bondLength);
			smiles = generator.createSMILES(molecule);
			boolean[] bool=new boolean[moleculewithh.getBondCount()];
		    SmilesGenerator sg = new SmilesGenerator();
			for(int i=0;i<bool.length;i++){
		      if (sg.isValidDoubleBondConfiguration(moleculewithh, moleculewithh.getBond(i)))
				bool[i]=true;
			}
			chiralsmiles=generator.createChiralSMILES(moleculewithh,bool);
//			dialog.setMessage("SMILES: " + smiles+System.getProperty("line.separator")+"chiral SMILES: "+chiralsmiles);
		} catch (Exception exception)
		{
			String message = "Error while creating SMILES: " + exception.getMessage();
			logger.error(message);
			logger.debug(exception);
			dialog.setText("Error");
//			dialog.setMessage(message);
		}
//		dialog.open();
	}
}

