/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.core.domain;


/**
 * Models used in Bioclipse should implement this interface. 
 * They must update their listeners when changed. The cache uses this 
 * to know when a model has been changed and must be removed from the 
 * cache.
 * 
 * @author jonalv
 *
 */
public interface ICachedModel {

    /**
     * @param listener to be added
     */
    public void addChangeListener(    IModelChangedListener listener );
    
    /**
     * @param listener to be removed
     */
    public void removeChangeListener( IModelChangedListener listener );
    
    /**
     * Causes all Listeners to be alerted that the object has been 
     * changed
     */
    public void fireChangeEvent();
}
