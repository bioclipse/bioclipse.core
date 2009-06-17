/*******************************************************************************
 * Copyright (c) 2008  The Bioclipse Project and others.
 *               2009  Egon Willighagen <egonw@users.sf.net>
 * 
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

import java.util.List;

import net.bioclipse.core.business.BioclipseException;

/**
 * An interface to represent a Small Molecule that can be represented with 
 * Smiles notation. Since we can get SMILES, we can also get 2D coordinates.
 * Interface also requires the IMolecule to be able to be serialized as CML.
 *
 * <p>Properties are cached, and when they are retrieved one can indicate
 * whether it should use: 1. the cached version and return if no cached version
 * was calculated yet (USE_CACHED): 2. the cached version or a newly
 * calculated one if it was not cached yet (USE_CACHED_OR_CALCULATED); 3. a
 * newly calculated value.
 *
 * @author ola
 */
public interface IMolecule extends IBioObject{

    public enum Property {
        
        /**
         * Use the cached value or null if no cashed value exists
         */
        USE_CACHED,
        /**
         * Use the value or calculate a new one if no cashed value exists
         */
        USE_CACHED_OR_CALCULATED,
        /**
         * Always calculate a new value
         */
        USE_CALCULATED
    }
    
    /**
     * @return a list of IMolecules representing the conformers for the molecule
     */
    public List<IMolecule> getConformers();

    /**
     * Returns a SMILES serialization of this {@link IMolecule}. It is
     * calculated when the method is called.
     *
     * @return the SMILES string for this IMolecule
     * @throws BioclipseException is SMILES can not be returned
     */
    public String toSMILES() throws BioclipseException;

    /**
     * Returns a Chemical Markup Language (CML) serialization of this
     * {@link IMolecule}. It is calculated when the method is called.
     *
     * @return the IMolecule serialized to CML
     * @throws BioclipseException if CML cannot be returned
     */
    public String toCML() throws BioclipseException;
    
}
