/*******************************************************************************
 * Copyright (c) 2008-2009  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package net.bioclipse.core.tests;

import net.bioclipse.core.business.IBioclipseManager;

/**
 * Tests basic API patterns for New World Order {@link IBioclipseManager}s.
 * 
 * @author egonw
 */
public abstract class AbstractNewWorldOrderManagerTest
extends AbstractManagerTest {

    public abstract IBioclipseManager getManager();

}
