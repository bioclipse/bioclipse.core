package net.bioclipse.core.domain;

import java.util.List;

import net.bioclipse.core.business.BioclipseException;

/**
 * A leightweight implementation of IMolecule that only consists of a Smiles
 * string.
 * @author jonalv, olas
 *
 */
public class SmilesMolecule extends BioObject implements IMolecule {

    String smiles;
    
    public SmilesMolecule(String smiles) {
        this.smiles=smiles;
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
        throw new UnsupportedOperationException("SmilesMolecule can not " +
        		"return CML");
    }

    public List<IMolecule> getConformers() {
        throw new UnsupportedOperationException("SmilesMolecule can not " +
        "return conformers");
    }
    
}
