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
 * Used to select a frame in a view.
 * For example in Jmol, to select a frame for the model
 * @author ola
 *
 */
public class ModelSelection implements IChemicalSelection {

    int frame;

    
    public int getFrame() {
    
        return frame;
    }

    
    public void setFrame( int frame ) {
    
        this.frame = frame;
    }


    public ModelSelection(int frame) {

        super();
        this.frame = frame;
    }


    public Integer getSelection() {
        return frame;
    }


}
