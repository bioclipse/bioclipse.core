/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Ola Spjuth
 *
 ******************************************************************************/
package net.bioclipse.core.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.bioclipse.core.domain.props.BioObjectPropertySource;

import org.eclipse.core.resources.IResource;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Base implementation of the <code>IBioObject</Code> interface which is 
 * meant to be extended.
 * 
 * @author jonalv, ola
 *
 */
public abstract class BioObject implements IBioObject {

    /**
     * An as-good-as-unique ID.
     */
    private final String uid = UUID.randomUUID().toString();

    /**
     * The underlying IResource, may be null
     */
    private IResource resource;

    /**
     * The PropertySource available as adapter
     */
    private IPropertySource propertySource;

    private Set<IModelChangedListener> changeListeners 
        = new HashSet<IModelChangedListener>();
    
    public BioObject() {
        
    }
    
    /**
     * Returns this the ID of this <code>BioObject</code>.
     */
    public String getUID() {
        return uid;
    }

    /**
     * Returns the resource behind this <code>BioObject</code>, or
     * <code>null</code> if no such resource exists.
     */
    public IResource getResource() {
        return resource;
    }

    /**
     * Basic properties. Should be overridden by subclasses.
     */
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class){
            return propertySource!=null 
                ? propertySource : new BioObjectPropertySource(this);
        }
        return null;
    }
    
    public void addChangeListener( IModelChangedListener listener ) {
    
        changeListeners.add( listener );
    }
    
    public void removeChangeListener( IModelChangedListener listener ) {
    
        changeListeners.remove( listener );
    }
    
    public void fireChangeEvent() {
        for ( IModelChangedListener l : changeListeners ) {
            l.modelChanged( this );
        }
    }
}
