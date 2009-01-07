/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.recording;

import static org.junit.Assert.*;

import net.bioclipse.core.domain.BioObject;

import org.junit.Test;

public class RecordTest {

    public class TestObject extends BioObject {
        public Object getParsedResource() {
            return null;
        }
    }
    
    @Test
    public void testToString() {
        MethodRecord r = new ManagerObjectRecord( "methodName",
                                            "serviceObjectName", 
                                            new String[] {"", ""}, 
                                            new TestObject() );
        assertEquals( "serviceObjectName.methodName( \"\", \"\" )", 
                      r.toString() );
    }
    
    @Test
    public void testNullParam() {
        MethodRecord r = new ManagerObjectRecord( "methodName",
                                            "serviceObjectName", 
                                            new String[] {null, null}, 
                                            new TestObject() );
        assertEquals( "serviceObjectName.methodName( null, null )", 
                  r.toString() );
    }
}
