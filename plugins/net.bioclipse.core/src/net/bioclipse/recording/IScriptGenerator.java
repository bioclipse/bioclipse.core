/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.recording;

/**
 * @author jonalv
 *
 */
public interface IScriptGenerator {

    /**
     * Generates a script from the given records
     * 
     * @param records
     * @return the script commands
     */
    public String[] generateScript(IRecord[] records);

}