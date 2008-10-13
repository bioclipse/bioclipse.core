/*******************************************************************************
 * Copyright (c) 2006 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rob Schellhorn
 ******************************************************************************/

package net.bioclipse.compute;

/**
 * This interface only contains constants and is not intended to be implemented.
 * 
 * @author Rob Schellhorn
 */
public interface IComputationContextIds {

	static final String PREFIX = Activator.ID + ".";

	/**
	 * 
	 */
	public static final String COMPUTATION_WIZARD = PREFIX
			+ "computation_wizard";

	/**
	 * 
	 */
	public static final String SELECT_COMPUTATION_PAGE = PREFIX
			+ "select_computation_page";
}