/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Egon Willighagen
 ******************************************************************************/
package net.bioclipse.compute.wizards;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author Egon Willighagen
 */
public interface IComputationWizardPage extends IWizardPage {
	
	public void loadDefaultPreferences();
	public void loadPreferences();
	public void storePreferences();
	
}