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
package net.bioclipse.core.domain;

/**
 * A base class for chemical selections with an Object as chemicalModel
 * @author ola
 *
 */
public abstract class AbstractChemicalSelection implements IChemicalSelection{

	private Object chemicalModel;

	public void setChemicalModel(Object chemicalModel) {
		this.chemicalModel = chemicalModel;
	}

	public Object getChemicalModel() {
		return chemicalModel;
	}


}
