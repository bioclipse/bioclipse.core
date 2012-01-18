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

import java.util.List;

import net.bioclipse.gist.business.IGistManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class AbstractGistManagerPluginTest {

    protected static IGistManager gist;
    
    protected static final String FILENAME = "84188.js";
    protected static final String PATH = "/Virtual/";
    protected static final int    GIST_NUM = 84188;
    
    protected static IFile file 
    = net.bioclipse.core.Activator.getVirtualProject()
         .getFile( new Path(PATH) );
    
    @BeforeClass
    public static void startUIPlugin() {
        net.bioclipse.ui.Activator.getDefault();
    }
    
    @Test
    public void downloadIntIFileHook() {
        throw new RuntimeException( "Not yet implemented" );
    }
    
    @Test
    public void downloadInt() {
        List<IFile> foo = gist.download(GIST_NUM);
        Assert.assertNotNull(foo);
        Assert.assertNotSame(0, foo.size());
    }
    
    @Test
    public void downloadIntHook() {
        throw new RuntimeException( "Not yet implemented" );
    }
}
