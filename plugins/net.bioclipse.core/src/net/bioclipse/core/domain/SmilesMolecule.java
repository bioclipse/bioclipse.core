package net.bioclipse.core.domain;

import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.resources.IResource;

/**
 * A leightweight implementation of IMolecule that only consists of a Smiles
 * string.
 * @author jonalv
 *
 */
public class SmilesMolecule extends BioObject implements IMolecule {

    String smiles;
    
    public String getSmiles() throws BioclipseException {
        return smiles;
    }

    /**
     * A molecule with only SMILES cannot produce CML other than via CDK
     */ 
    public String getCML() {
        return null;
    }
    
}
