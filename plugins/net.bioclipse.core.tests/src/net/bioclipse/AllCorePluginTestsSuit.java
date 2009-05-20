/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse;
import net.bioclipse.core.ResourcePathTransformerTest;
import net.bioclipse.managers.tests.AllManagerMethodDispatcherTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { ResourcePathTransformerTest.class,
                         AllManagerMethodDispatcherTests.class } )
public class AllCorePluginTestsSuit {

}
