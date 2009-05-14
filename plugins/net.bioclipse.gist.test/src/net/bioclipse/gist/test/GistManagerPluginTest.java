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

import net.bioclipse.gist.business.IGistManager;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GistManagerPluginTest {

    private static IGistManager gist;
    
    @BeforeClass public static void setup() {
        // the next line is needed to ensure the OSGI loader properly start
        // the org.springframework.bundle.osgi.extender, so that the manager
        // can be loaded too. Otherwise, it will fail with a time out.
        net.bioclipse.ui.Activator.getDefault();

        gist = net.bioclipse.gist.Activator.getDefault().getJavaManager();
    }
    
    @Test public void testDownload() throws Exception {
        String target = "/Virtual/84188.js";
        String foo = gist.download(84188, target);
        Assert.assertEquals(target, foo);
    }
}
