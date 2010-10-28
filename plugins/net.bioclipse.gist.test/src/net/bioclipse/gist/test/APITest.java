/* *****************************************************************************
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

import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.core.tests.AbstractManagerTest;
import net.bioclipse.gist.business.GistManager;
import net.bioclipse.gist.business.IGistManager;

public class APITest extends AbstractManagerTest {

    GistManager gist;

    @Override
    public IBioclipseManager getManager() {
        return gist;
    }

    @Override
    public Class<? extends IBioclipseManager> getManagerInterface() {
        return IGistManager.class;
    }

}
