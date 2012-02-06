/* *****************************************************************************
 * Copyright (c) 2007-2009 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 ******************************************************************************/
package net.bioclipse.usermanager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * A wizard for handling the users third parts accounts
 *   
 * @author Klas Jšnsson
 *
 */
public class NewAccountWizard extends Wizard implements INewWizard {

	private NewAccountWizardPage mainPage;
	//private IStructuredSelection initSelection;
	
	public NewAccountWizard() {
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("Add an account to Bioclipse");
        setNeedsProgressMonitor(false);
	}
	
    public void addPages() {
        super.addPages();
        mainPage = new NewAccountWizardPage("mainPage");
        mainPage.setTitle("New Account");
        mainPage.setDescription("Add an third-part account to Bioclipse");
        addPage(mainPage);
        setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(this.getClass(),
        		   "BioclipseAccountLogo1_medium.png"));
    }

	@Override
	public boolean performFinish() {
		// TODO Add logic for creating account
		return false;
	}

}
