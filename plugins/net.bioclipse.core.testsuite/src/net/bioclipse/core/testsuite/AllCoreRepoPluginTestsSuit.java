package net.bioclipse.core.testsuite;
/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/


import net.bioclipse.AllCorePluginTestsSuit;
import net.bioclipse.browser.tests.AllBrowserManagerPluginTests;
import net.bioclipse.business.test.JavaBioclipsePlatformManagerPluginTest;
import net.bioclipse.business.test.JavaScriptBioclipsePlatformManagerPluginTest;
import net.bioclipse.scripting.AllScriptingTestsSuite;
import net.bioclipse.scripting.ui.tests.CoverageTest;
import net.bioclipse.scripting.ui.tests.JsConsoleManagerTest;
import net.bioclipse.ui.business.tests.AllUiManagerPluginTests;
import net.bioclipse.ui.business.tests.JavaScriptUiManagerPluginTest;
import net.bioclipse.ui.business.tests.JavaUiManagerPluginTest;
import net.bioclipse.ui.tests.AllUIPluginTestSuite;
import net.bioclipse.webservices.tests.AllWebservicesManagerPluginTests;
import net.bioclipse.webservices.tests.JavaScriptWebservicesManagerPluginTest;
import net.bioclipse.webservices.tests.JavaWebservicesManagerPluginTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { AllBrowserManagerPluginTests.class,
                         AllCorePluginTestsSuit.class,
                         AllScriptingTestsSuite.class,
                         AllUIPluginTestSuite.class,
                         AllUiManagerPluginTests.class,
                         JavaScriptUiManagerPluginTest.class,
                         JavaUiManagerPluginTest.class,
                         CoverageTest.class,
                         JsConsoleManagerTest.class,
                         AllWebservicesManagerPluginTests.class,
                         JavaScriptWebservicesManagerPluginTest.class,
                         JavaWebservicesManagerPluginTest.class,
                         JavaBioclipsePlatformManagerPluginTest.class,
                         JavaScriptBioclipsePlatformManagerPluginTest.class} )
public class AllCoreRepoPluginTestsSuit {

}
