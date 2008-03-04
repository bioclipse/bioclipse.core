/*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.core.domain;

import java.util.UUID;

import org.eclipse.core.resources.IResource;

/**
 * Base implementation of the <code>IBioObject</Code> interface which is 
 * meant to be extended.
 * 
 * @author jonalv
 *
 */
public abstract class BioObject implements IBioObject {

	/**
	 * An as-good-as-unique ID.
	 */
	private final String id = UUID.randomUUID().toString();
	
	private IResource resource;
	
	public BioObject() {
		
	}
	
	/**
	 * Returns this the ID of this <code>BioObject</code>.
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Returns the resource behind this <code>BioObject</code>, or
	 * <code>null</code> if no such resource exists.
	 */
	public IResource getResource() {
		return resource;
	}
}
