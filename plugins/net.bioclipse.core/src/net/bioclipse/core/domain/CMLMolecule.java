/* *****************************************************************************
 *Copyright (c) 2010 The Bioclipse Team and others.
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
 * A lightweight implementation of IMolecule that only consists of a CML
 * string.
 * @author olas
 *
 */
public class CMLMolecule extends BioObject implements IMolecule {

    String cml;
    
    /*
     * Needed by Spring for proxying
     */
    CMLMolecule() {

    }
    
    public CMLMolecule(String cml) {
        this.cml=cml;
    }

    public String toCML() throws BioclipseException {
        return cml;
    }

    /**
     * A molecule with only CML cannot produce SMILES other than via CDK, hence 
     * always throws BioclipseException.
     * @throws BioclipseException 
     */ 
    public String toSMILES() throws BioclipseException {
        throw new UnsupportedOperationException("CMLMolecule can not " +
        		"return SMILES");
    }

    public List<IMolecule> getConformers() {
        throw new UnsupportedOperationException("CMLMolecule can not " +
        "return conformers");
    }
    
}
