/* *****************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.ui.business;

import net.bioclipse.core.domain.IBioObject;


/**
 * @author jonalv
 *
 */
public interface IBioObjectFromStringBuilder {

    /**
     * Builds a BioObject from the given string if this builder knows how 
     * otherwise throws IllegalArgumentException. Use recognize to check if 
     * possible.
     * 
     * @param s
     * @return a BioObject built from the string
     */
    public IBioObject fromString(String s);
    
    
    /**
     * @param s
     * @return true if this builder knows how to build a BioObect from the 
     * given String otherwise false. 
     */
    public boolean recognize(String s);
    
}
