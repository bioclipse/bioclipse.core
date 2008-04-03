/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.core.domain;

import net.bioclipse.core.business.BioclipseException;

/**
 * An interface to represent a Small Molecule that can be represented with 
 * Smiles notation. Since we can get SMILES, we can also get 2D coordinates.
 * @author ola
 *
 */
public interface IMolecule extends IBioObject{

	public String getSmiles() throws BioclipseException;
	public boolean has3dCoords();
	
}
