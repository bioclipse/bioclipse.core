/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *  
 * Copyright (c) 2012 Jonathan Alvarsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jonathan Alvarsson - Initial implementation
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package net.bioclipse.core.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;

public class SparseDatasetTest {

    @Test
    public void testToSparseString() {
        
        List<String> colHeaders = Arrays.asList( "c1", "c2", "c3", "c4" );
        List<String> rowHeaders = Arrays.asList( "r1", "r2", "r3", "r4" );
        LinkedHashMap<Point, Integer> values 
            = new LinkedHashMap<Point, Integer>() {{
                put( new Point( 1, 1 ), 1 );
                put( new Point( 1, 2 ), 12);
                put( new Point( 2, 2 ), 2);
                put( new Point( 3, 2 ), 32);
                put( new Point( 3, 3 ), 3);
        }};
        SparseDataset dataset = new SparseDataset( colHeaders, 
                                                   rowHeaders, 
                                                   values );
        assertEquals( "1:1,2:12\n2:2\n2:32,3:3\n", 
                      dataset.toSparseString(",") );
    }
}
