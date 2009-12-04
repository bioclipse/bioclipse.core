/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org/epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.ui.business.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Unit tests to be run as normal JUnit tests. See also the plugin tests
 * in {@link AllUiManagerPluginTests}.
 *
 * @author egonw
 */
@RunWith(value=Suite.class)
@SuiteClasses({
    UIManagerTest.class,
    CoverageTest.class } )
public class AllUiManagerTests {

}
