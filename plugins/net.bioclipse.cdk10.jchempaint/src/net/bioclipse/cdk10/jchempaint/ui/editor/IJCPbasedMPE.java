/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import org.openscience.cdk.interfaces.IChemModel;

/**
 * An interface for MultiPageEditors (MPEs) containing multiple ChemModels
 * For example, SDFEditor that contains multiple models for JCP
 * @author ola
 *
 */
public interface IJCPbasedMPE {

    public IChemModel getNextModel();
    public IChemModel getPrevModel();
    public IChemModel getModel( int i );

}
