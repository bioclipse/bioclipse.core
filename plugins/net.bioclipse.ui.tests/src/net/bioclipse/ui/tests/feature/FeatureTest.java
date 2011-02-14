/* *****************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package net.bioclipse.ui.tests.feature;

import org.junit.Ignore;
import org.junit.Test;

import net.bioclipse.core.tests.coverage.AbstractFeatureTest;

/**
 * JUnit tests for checking the copyright and license header.
 * 
 * @author egonw
 */
public class FeatureTest extends AbstractFeatureTest {

    @Override
    public String getFeatureName() {
        return "net.bioclipse.core_feature";
    }

    @Ignore("Until all copyright violations are fixed: http://pele.farmbio.uu.se/bugzilla36/show_bug.cgi?id=3027")
    public void testCopyrightPresent() {
    	
    }
}
