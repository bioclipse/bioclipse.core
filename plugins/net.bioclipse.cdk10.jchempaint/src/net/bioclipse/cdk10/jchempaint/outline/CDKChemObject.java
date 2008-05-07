/*******************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth
 *******************************************************************************/

package net.bioclipse.cdk10.jchempaint.outline;

import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;

import org.eclipse.ui.views.properties.IPropertySource;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Wraps an IChemObject as GUI object
 * 
 * 
 * @author ola
 *
 */
public class CDKChemObject extends BioObject implements IBioObject{

	private IChemObject chemobj;
//	private ChemObjectPropertySource propSource;
	private String name;

	private IPropertySource propSource;
	
	/**
	 * Used to look up e.g. the molecule of an Atom
	 */
	private IChemObject parentChemobj;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CDKChemObject(String name, IChemObject chemobj) {
		this.name=name;
		this.chemobj = chemobj;
	}

	//Set name=ID from chemobj
	public CDKChemObject(IChemObject chemobj) {
		this.chemobj = chemobj;
		name=chemobj.toString();
	}

	public IChemObject getChemobj() {
		return chemobj;
	}
	public void setChemobj(IChemObject chemobj) {
		this.chemobj = chemobj;
	}

	public IChemObject getParentChemobj() {
		return parentChemobj;
	}

	public void setParentChemobj(IChemObject parentChemobj) {
		this.parentChemobj = parentChemobj;
	}

	public Object getAdapter(Class adapter) {
		if (adapter ==IPropertySource.class){
			if (propSource ==null){
				propSource=new ChemObjectPropertySource(this);
			}
			return propSource;
		}
		return super.getAdapter(adapter);
	}

}
