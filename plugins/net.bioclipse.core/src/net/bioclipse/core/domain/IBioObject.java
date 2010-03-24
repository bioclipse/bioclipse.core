/* *****************************************************************************
 * Copyright (c) 2007-2009 The Bioclipse Project and others.
 *                    2010 Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Ola Spjuth
 *     Carl Mäsak
 *     
 ******************************************************************************/
package net.bioclipse.core.domain;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Definitions of the basic functionality of all domain objects.
 * 
 * @author jonalv, ola
 */
public interface IBioObject extends IAdaptable {
    
    /**
     * Returns the Eclipse {@link IResource} associated with this
     * <code>IBioObject</code>.
     *
     * @return an Eclipse {@link IResource}
     */
    public IResource getResource();
    
    /**
     * Associate an Eclipse {@link IResource} for this
     * <code>IBioObject</code>.
     *
     * @param resource the newly associated {@link IResource}
     */
    public void setResource(IResource resource);

    /**
     * Returns a unique identifier for this object.
     *
     * @return an unique id for the object
     */
    public String getUID();
}
