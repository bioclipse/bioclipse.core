/*******************************************************************************
 * Copyright (c) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.business.test;

import net.bioclipse.business.IBioclipsePlatformManager;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractBioclipsePlatformManagerPluginTest {

    protected static IBioclipsePlatformManager bioclipse;
    
    @Test public void testIsOnline() {
        Assert.assertTrue(bioclipse.isOnline());
    }

    @Test public void testDownload() throws Exception {
    	String downloadedContent = bioclipse.download(
    		"http://www.bioclipse.net"
    	);
        Assert.assertTrue(
        	downloadedContent.contains("Bioclipse")
        );
    }
}
