/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class ScriptingTestCase {

    private static ScriptingEnvironment env;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        env = new JsEnvironment();
    }

    @Test
    public void scriptingWorks() {
        assertEquals( "the scripting class can be called and returns the"
                      + " right answer",
                      env.eval("2+2"),
                      "4" );
    }
}
