/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.cdk10.jchempaint.outline;

import net.bioclipse.cdk10.jchempaint.Activator;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * A LabelProvider for JCPOutline
 * @author ola
 *
 */
public class StructureLabelProvider extends LabelProvider {

	// cached images
	private final static Image carbonImage 
		= Activator.imageDescriptorFromPlugin(
			Activator.PLUGIN_ID, "icons/atom_c.gif").createImage();
	private final static Image hydrogenImage 
	= Activator.imageDescriptorFromPlugin(
		Activator.PLUGIN_ID, "icons/atom_h.gif").createImage();
	private final static Image nitrogenImage 
	= Activator.imageDescriptorFromPlugin(
		Activator.PLUGIN_ID, "icons/atom_n.gif").createImage();
	private final static Image oxygenImage 
	= Activator.imageDescriptorFromPlugin(
		Activator.PLUGIN_ID, "icons/atom_o.gif").createImage();

	
	public String getText(Object obj) {
		
		if (obj instanceof Container) {
			Container c=(Container)obj;
			return c!=null ? c.getName() : "???";
		}
		else if (obj instanceof CDKChemObject) {
			CDKChemObject co = (CDKChemObject) obj;
			return co.getName()!=null ? co.getName() : "???";
		}

		return obj.toString();
	}
	
	public Image getImage(Object element) {

			if (element instanceof CDKChemObject) {
				CDKChemObject obj=(CDKChemObject)element;

				IChemObject chemobj=obj.getChemobj();

				if (chemobj instanceof IAtom) {
					IAtom atom = (IAtom) chemobj;
					if (atom.getSymbol().compareTo("C")==0){
						return carbonImage;
					}
					else if (atom.getSymbol().compareTo("H")==0){
						return hydrogenImage;
					}
					else if (atom.getSymbol().compareTo("N")==0){
						return nitrogenImage;
					}
					else if (atom.getSymbol().compareTo("O")==0){
						return oxygenImage;
					}
				}
				if (chemobj instanceof IBond) {
					//TODO: add image for bonds
					return null;
				}

			}

			//No match, no image
			return null;
	}
}

class NameSorter extends ViewerSorter {
}
