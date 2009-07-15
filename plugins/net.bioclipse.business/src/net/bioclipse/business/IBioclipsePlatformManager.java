/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.business;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass("The Bioclipse Platform manager is used for providing some " +
		"textual information on the Bioclipse platform.")
public interface IBioclipsePlatformManager extends IBioclipseManager {

	@PublishedMethod(methodSummary="Opens Planet Bioclipse.")
	public void planet();

	@PublishedMethod(methodSummary="Opens the Bioclipse Bug Tracker.")
	public void bugTracker();

}
