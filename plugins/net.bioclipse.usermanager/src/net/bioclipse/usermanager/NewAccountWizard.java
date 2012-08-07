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

import java.util.HashMap;

import net.bioclipse.usermanager.business.IUserManager;
import net.bioclipse.usermanager.dialogs.CreateUserDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * A wizard for handling the users third parts accounts
 *   
 * @author Klas Jšnsson (aka "konditorn")
 * 
 */
public class NewAccountWizard extends Wizard implements INewWizard {

	private NewAccountWizardPage addAccountPage;
	private LoginWizardPage loginPage;
	private IUserManager usermanager;
	private UserContainer sandbox;
	
	public NewAccountWizard(UserContainer userContainer) {
	    sandbox = userContainer;
	    
	}
	
	public NewAccountWizard() {
	    usermanager = Activator.getDefault().getUserManager();
	    sandbox = usermanager.getSandBoxUserContainer();
		if ( usermanager.getUserNames().size() == 0) {
			CreateUserDialog dialog 
			= new CreateUserDialog( PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow()
					.getShell(), sandbox );
			dialog.open();
			if (dialog.getReturnCode() == Window.OK) {
				usermanager.switchUserContainer( sandbox );
			}
			else if (dialog.getReturnCode() == Window.CANCEL) {
				dispose();
			}
		}
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Add an account to Bioclipse");
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		super.addPages();
		
		if ( !sandbox.isLoggedIn() ) {
			loginPage = new LoginWizardPage("loginPage", sandbox);
			loginPage.setTitle("Log In To Your Bioclipse Account");
			loginPage.setDescription("Before adding an acount you have to " +
					"loggin or create a new acount.");
			addPage(loginPage);
		}
		
		addAccountPage = new NewAccountWizardPage("addAccountPage", sandbox);
		addAccountPage.setTitle("New Account");
		addAccountPage.setDescription("Add a third-part account to Bioclipse");
		addPage(addAccountPage);


		setDefaultPageImageDescriptor(ImageDescriptor
				.createFromFile(this.getClass(),
						"BioclipseAccountLogo3_medium.png"));
	}

	@Override
	public boolean performFinish() {
	     
	    if ( !sandbox.isLoggedIn() ) {
	        if (usermanager == null)
	            usermanager = Activator.getDefault().getUserManager();	        
	        usermanager.logIn(loginPage.getUsername(), loginPage.getPassword());
	    }
	    
		if (addAccountPage.createAccount()) {
		    if (usermanager != null)
		        usermanager.switchUserContainer( sandbox );
			dispose();
			return true;
		} else
			return false;
	}

	@Override
	public boolean canFinish() {
		return addAccountPage.isPageComplete();
	}

	public boolean performCancel() {
		return true;
	}
	
	public String getAccountId() {
	    return addAccountPage.getAccountId();
	}
	
	public AccountType getAccountType() {
	    return addAccountPage.getAccountType();
	}
	
	public HashMap<String, String> getProperties() {
	    return addAccountPage.getProperties();
	}
	
}
