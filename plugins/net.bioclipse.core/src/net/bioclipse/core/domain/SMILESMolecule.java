/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.core.domain;

import java.util.List;

import net.bioclipse.core.business.BioclipseException;

/**
 * A leightweight implementation of IMolecule that only consists of a Smiles
 * string.
 * @author jonalv, olas
 *
 */
public class SMILESMolecule extends BioObject implements IMolecule {

    String smiles;
    
    /*
     * Needed by Spring for proxying
     */
    SMILESMolecule() {

    }
    
    public SMILESMolecule(String smiles) {
        this.smiles=smiles;
    }

    public String getSMILES() throws BioclipseException {
        return smiles;
    }

    /**
     * A molecule with only SMILES cannot produce CML other than via CDK, hence 
     * always throws BioclipseException.
     * @throws BioclipseException 
     */ 
    public String getCML() throws BioclipseException {
        throw new UnsupportedOperationException("SmilesMolecule can not " +
        		"return CML");
    }

    public List<IMolecule> getConformers() {
        throw new UnsupportedOperationException("SmilesMolecule can not " +
        "return conformers");
    }
    
}
