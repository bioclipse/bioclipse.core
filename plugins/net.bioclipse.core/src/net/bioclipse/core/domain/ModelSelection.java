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

import java.util.ArrayList;

/**
 * Used to select a frame in a view.
 * For example in Jmol, to select a frame for the model
 * @author ola
 *
 */
public class ModelSelection implements IChemicalSelection {

    ArrayList<Integer> frames;

    public ArrayList<Integer> getFrames() {
        return frames;
    }
    public void setFrames( ArrayList<Integer> frames ) {
        this.frames = frames;
    }

    /**
     * Constructor for a ModelSelection.
     * @param frames ArrayList<Integer> with the models to display
     */
    public ModelSelection(ArrayList<Integer> frames) {
        this.frames = frames;
    }

    /**
     * Create new ModelSelection with only one index
     * @param modelIndex
     */
    public ModelSelection(int modelIndex) {
        this.frames = new ArrayList<Integer>();
        frames.add( new Integer(modelIndex) );
    }
    public ArrayList<Integer> getSelection() {
        return frames;
    }


}
