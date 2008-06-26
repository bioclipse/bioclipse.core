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
 * Used to select a series of atoms by index
 * @author ola
 *
 */
public class AtomIndexSelection implements IChemicalSelection{

    int[] selection;
    
    public int[] getSelection() {

        return selection;
    }

    
    public void setSelection( int[] selection ) {
    
        this.selection = selection;
    }


    public AtomIndexSelection(int[] selection) {

        super();
        this.selection = selection;
    }

}
