/*******************************************************************************
 * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.ds.signsig.bursi;

import org.openscience.cdk.interfaces.IAtom;

import net.bioclipse.ds.model.SubStructureMatch;

/**
 * A signsichit is colored red if negative or green if positive
 * @author ola
 *
 */
public class SignSicHit extends SubStructureMatch{

    private boolean isPositive;

    public boolean isPositive() {
        return isPositive;
    }
    public void setPositive( boolean isPositive ) {
        this.isPositive = isPositive;
    }
    
    @Override
    public java.awt.Color getHighlightingColor( IAtom atom ) {
        
        if (isPositive)
            return java.awt.Color.GREEN;
        else
            return java.awt.Color.RED;
    }
    
}
