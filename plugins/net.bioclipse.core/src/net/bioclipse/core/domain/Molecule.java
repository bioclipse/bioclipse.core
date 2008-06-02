/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.core.domain;

import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.resources.IResource;


/**
 * @author jonalv
 *
 */
public class Molecule extends BioObject implements IMolecule {

    private String smiles;

    Molecule() {

    }
    
    public Molecule(String smiles) {
        this.smiles = smiles;
    }
    
    public String getSmiles() throws BioclipseException {
        return smiles;
    }

    /**
     * A molecule with only SMILES cannot produce CML other than via CDK, hence 
     * always throws BioclipseException.
     * @throws BioclipseException 
     */ 
    public String getCML() throws BioclipseException {
        throw new UnsupportedOperationException("Cannot get CML from SmilesMolecule");
    }
}
