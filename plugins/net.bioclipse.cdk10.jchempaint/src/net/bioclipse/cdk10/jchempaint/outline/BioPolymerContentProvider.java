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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.protein.data.PDBStrand;
import org.openscience.cdk.protein.data.PDBStructure;

public class BioPolymerContentProvider implements ITreeContentProvider {

	BioPolymerContentProvider() {}

	public Object[] getChildren(Object parentElement) {
//		logger.debug("Finding children of: " + parentElement.getClass().getName());

		// optionall unwrap IChemObject
		if (parentElement instanceof CDKChemObject) {
			parentElement = ((CDKChemObject)parentElement).getChemobj();
		}

		if (parentElement instanceof IBioPolymer) {
			IBioPolymer polymer = (IBioPolymer)parentElement;
			// childs of BioPolymer are it's Strands
//			logger.debug("Strand count: " + polymer.getStrandCount());
			int strandCount = polymer.getStrandCount();
			int structCount = 0;
			if (polymer instanceof PDBPolymer) 
				structCount = structCount + ((PDBPolymer)polymer).getStructures().size();
			Object[] strands = new Object[strandCount+structCount];
			Iterator names = polymer.getStrandNames().iterator();
			for (int i=0; i<strandCount; i++) {
				IStrand str=polymer.getStrand((String)names.next());
				logger.debug("Added strand: " + str.getStrandName());
				strands[i] = new CDKChemObject(str);
			}
			if (polymer instanceof PDBPolymer) {
				Iterator structs = ((PDBPolymer)polymer).getStructures().iterator();
				int i = 0;
				while (structs.hasNext()) {
					strands[strandCount+i] = new CDKPDBStructureObject((PDBStructure)structs.next());
					i++;
				}	
			}
			return strands;
		} else if (parentElement instanceof IStrand) {
			IStrand strand = (IStrand)parentElement;
			// childs of BioPolymer are it's Strands
//			logger.debug("Monomer count: " + strand.getMonomerCount());
			Object[] monomers = new Object[strand.getMonomerCount()];
			Iterator names;
			if (parentElement instanceof PDBStrand) {
				names = ((PDBStrand)parentElement).getMonomerNamesInSequentialOrder().iterator();
			} else {
				names = strand.getMonomerNames().iterator();
			}
			for (int i=0; i<monomers.length; i++) {
				monomers[i] = new CDKChemObject(strand.getMonomer((String)names.next()));
			}
			return monomers;
		}

		return new IChemObject[0];
	}

	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element) {
		return (getChildren(element).length > 0);
	}

	public Object[] getElements(Object inputElement) {
//		logger.debug("Finding elements of: " + inputElement.getClass().getName());
		if (inputElement instanceof CDKResource) {
			CDKResource cdkResource = (CDKResource) inputElement;
			if (cdkResource != null && cdkResource.getParsedResource() != null) {

				List atomContainerList = 
					ChemFileManipulator.getAllAtomContainers((ChemFile)resource);					
					
				Iterator iterator = atomContainerList.iterator();

//				IAtomContainer[] atomContainer = (getIAtomContainerFromChemFile((ChemFile) cdkResource.getParsedResource()));

				ArrayList objects = new ArrayList();
				while(iterator.hasNext()){
					IAtomContainer ac = (IAtomContainer)iterator.next();
					if (ac instanceof IBioPolymer) {
						objects.add(ac);
					}
				}
				return objects.toArray();
			}
		}
		return new IChemObject[0];
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
}
