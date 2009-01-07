/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth
 *     Jonathan Alvarsson
 *
 ******************************************************************************/
package net.bioclipse.ui.business.tests;

import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.tests.AbstractManagerTest;
import net.bioclipse.ui.business.IUIManager;
import net.bioclipse.ui.business.UIManager;

public class UIManagerTest extends AbstractManagerTest {

    IUIManager ui;

    //Do not use SPRING OSGI for this manager
    //since we are only testing the implementations of the manager methods
    public UIManagerTest() {
        ui = new UIManager();
    }

    public IBioclipseManager getManager() {
        return ui;
    }

}
