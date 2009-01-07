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

import org.eclipse.core.runtime.Status;

/**
 * @author Rob Schellhorn
 */
public class WarningStatus extends Status {

	/**
	 * @param pluginId
	 * @param message
	 */
	public WarningStatus(String pluginId, String message) {
		this(pluginId, message, null);
	}

	/**
	 * @param pluginId
	 * @param message
	 * @param cause
	 */
	public WarningStatus(String pluginId, String message, Throwable cause) {
		super(WARNING, pluginId, OK, message, cause);
	}
}