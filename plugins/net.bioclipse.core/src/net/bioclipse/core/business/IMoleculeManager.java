/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.core.business;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.domain.IMolecule;


/**
 * @author jonalv
 *
 */
@PublishedClass ("Manages light weight molecules")
public interface IMoleculeManager extends IBioclipseManager {

    /**
     * @param smiles
     * @return
     */
    @PublishedMethod (params="String smiles", 
                      methodSummary="creates a light weight molecule " +
                      		        "from smiles")
    public IMolecule fromSmiles(String smiles);
}
