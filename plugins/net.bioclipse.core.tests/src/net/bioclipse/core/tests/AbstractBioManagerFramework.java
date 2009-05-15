/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package net.bioclipse.core.tests;

import net.bioclipse.managers.business.IBioclipseManager;

/**
 * Framework defining methods stable manager plugin tests should implement
 * to allow testing for common patterns, expected by Bioclipse.
 * 
 * @author egonw
 */
public abstract class AbstractBioManagerFramework {

    @Deprecated
    public abstract IBioclipseManager getManager();

    // FIXME: need to make this method abstract
    public Class<? extends IBioclipseManager> getManagerInterface() {
        throw new RuntimeException(
            "The getManagerInterface() method must be overwritten."
        );
    }

}