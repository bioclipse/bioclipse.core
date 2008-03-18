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

import org.eclipse.core.resources.IResource;

/**
 * Definitions of the basic functionality of all domain objects
 * 
 * @author jonalv
 *
 */
public interface IBioObject {
	
	/**
	 * @return Eclipse resource
	 */
	public IResource getResource();
	
	/**
	 * @return the parsed resource
	 * 
	 * @deprecated: No actual use of this yet so scheduled for termination.
	 */
	@Deprecated
	public Object getParsedResource();
	
	/**
	 * @return an unique id for the object
	 */
	public String getId();
}
