/*******************************************************************************
 * Copyright (c) 2008  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.gist.test;

import net.bioclipse.core.tests.AbstractNewWorldOrderManagerTest;
import net.bioclipse.gist.business.GistManager;
import net.bioclipse.managers.business.IBioclipseManager;

public class GistManagerTest extends AbstractNewWorldOrderManagerTest {

    GistManager gist;

    //Do not use SPRING OSGI for this manager
    //since we are only testing the implementations of the manager methods
    public GistManagerTest() {
        gist = new GistManager();
    }

    public IBioclipseManager getManager() {
        return gist;
    }

}
