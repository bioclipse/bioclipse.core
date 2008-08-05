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
 * Used to select a frame (=model by index) in a view.
 * For example in Jmol, to select a frame for the model
 * @author ola
 *
 */
public class ModelSelection extends AbstractChemicalSelection {

    ArrayList<Integer> frames;

    public ArrayList<Integer> getFrames() {
        return frames;
    }
    public void setFrames( ArrayList<Integer> frames ) {
        this.frames = frames;
    }

    public ArrayList<Integer> getSelection() {
        return frames;
    }

    /**
     * Constructor for a ModelSelection.
     * @param frames ArrayList<Integer> with the models to display
     */
    public ModelSelection(ArrayList<Integer> frames) {
        this.frames = frames;
    }

    /**
     * Constructor for a ModelSelection.
     * @param frames ArrayList<Integer> with the models to display and a chemical model
     * @param chemicalModel the model object = molecule
     */
    public ModelSelection(ArrayList<Integer> frames, Object chemicalModel) {
        this.frames = frames;
        setChemicalModel(chemicalModel);
    }

    /**
     * Create new ModelSelection with only one index
     * @param modelIndex
     */
    public ModelSelection(int modelIndex) {
        this.frames = new ArrayList<Integer>();
        frames.add( new Integer(modelIndex) );
    }

    /**
     * Create new ModelSelection with only one index and a chemical model
     * @param modelIndex
     * @param chemicalModel the model object = molecule
     */
    public ModelSelection(int modelIndex, Object chemicalModel) {
        this.frames = new ArrayList<Integer>();
        frames.add( new Integer(modelIndex) );
        setChemicalModel(chemicalModel);
    }


}
