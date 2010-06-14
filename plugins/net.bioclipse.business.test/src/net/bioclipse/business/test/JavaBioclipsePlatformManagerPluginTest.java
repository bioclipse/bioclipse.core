/*******************************************************************************
 * Copyright (c) 2010  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/    
 ******************************************************************************/
package net.bioclipse.business.test;

import org.junit.BeforeClass;

public class JavaBioclipsePlatformManagerPluginTest
    extends AbstractBioclipsePlatformManagerPluginTest {

    @BeforeClass public static void setup() {
        bioclipse = net.bioclipse.business.Activator.getDefault()
            .getJavaManager();
    }

}
