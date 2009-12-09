/*******************************************************************************
 * Copyright (c) 2008-2009 The Bioclipse Project and others.
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
 * An interface to represent a Reaction that can be represented with 
 * Smiles notation. Since we can get SMILES, we can also get 2D coordinates.
 * Interface also requires the IReaction to be able to be serialized as CML.
 * 
 * @author Miguel Rojas
 *
 */
public interface IReaction extends IBioObject{
	
	public enum Property {
        USE_CACHED,
        USE_CACHED_OR_CALCULATED,
        USE_CALCULATED
    }
	/**
     * @return the SMILES string for this IReaction
     * @throws BioclipseException is SMILES can not be returned
     */
    public String getSMILES(Property urgency) throws BioclipseException;

    /**
     * @return the IReaction serialized to CML
     * @throws BioclipseException if serialization fails
     */
    public String getCML() throws BioclipseException;
    
}
