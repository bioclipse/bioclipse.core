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

	public boolean has3dCoords() {
		return false;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	
}
