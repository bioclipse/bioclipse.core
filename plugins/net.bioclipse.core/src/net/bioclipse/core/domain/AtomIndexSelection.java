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
 * Used to select a series of atoms by index only. 
 * Can hold an (optional) chemical model (IMolecule or similar) 
 * @author ola
 *
 */
public class AtomIndexSelection extends AbstractChemicalSelection{
    private int[] selection;
    /**
     * Constructor to select an array of atoms by indices, (chemicalModel=null)
     * @param selection
     */
    public AtomIndexSelection(int[] selection) {
        this(selection, null);
    }
    /**
     * Constructor to select an array of atoms by indices in a model (=molecule)
     * @param selection
     * @param chemicalModel the model object = molecule
     */
    public AtomIndexSelection(int[] selection, Object chemicalModel) {
        this.selection = selection;
        setChemicalModel(chemicalModel);
    }
    public int[] getSelection() {
        return selection;
    }
    public void setSelection( int[] selection ) {
        this.selection = selection;
    }
}
