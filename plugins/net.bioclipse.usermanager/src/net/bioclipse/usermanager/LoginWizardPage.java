/* *****************************************************************************
 * Copyright (c) 2007-2009 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.usermanager;

import net.bioclipse.usermanager.dialogs.DialogArea;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * This is the wizard page for log-in into Bioclipse.
 * 
 * @author Klas J�nsson (aka "konditorn") 
 *
 */
public class LoginWizardPage extends WizardPage {

 private DialogArea loginDialogArea;
	
	protected LoginWizardPage(String pageName, UserContainer userContainer) {
		super(pageName);
		this.loginDialogArea = new DialogArea(userContainer ,true, this);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = loginDialogArea.getLoginArea(parent);
		setControl(container);
		setErrorMessage(loginDialogArea.getErrorMessage());
	}

	@Override
	public boolean isPageComplete() {
		return !loginDialogArea.getErrorFlag();
	}
	
	/**
	 * This method just passes on the username written in the text-field in the 
	 * login dialog.
	 * 
	 * @return The username
	 */
	public String getUsername() {
		return loginDialogArea.getUsername();
	}
	/**
	 * This method just passes on the password written in the text-field in the 
	 * login dialog.
	 * 
	 * @return The password
	 */
	public String getPassword() {
		return loginDialogArea.getPassword();
				
	}
	
}

