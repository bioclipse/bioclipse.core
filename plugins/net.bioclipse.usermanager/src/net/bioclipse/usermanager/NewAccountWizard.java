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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.bioclipse.usermanager.business.IUserManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;

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
	private CreateUserWizardPage createUserPage;
	private boolean manipulateUserContainer;
	
	public NewAccountWizard(UserContainer userContainer, boolean manipulateUserContainer) {
	    sandbox = userContainer;
	    this.manipulateUserContainer = manipulateUserContainer;
//	    getShell().setData("org.eclipse.ui.help", "net.bioclipse.ui.accountWizardHelp");
//	    PlatformUI.getWorkbench().getHelpSystem().setHelp( getShell(), "accountWizardHelp" );
//	    getShell().setData("org.eclipse.ui.help", "net.bioclipse.ui.accountWizardHelp");
//	    PlatformUI.getWorkbench().getHelpSystem().setHelp( getShell(), "accountWizardHelp" );
//	    setHelpAvailable( true );
	}
	
	public NewAccountWizard() {
	    usermanager = Activator.getDefault().getUserManager();
	    sandbox = usermanager.getSandBoxUserContainer();
	    manipulateUserContainer = true;
//	    getShell().setData("org.eclipse.ui.help", "net.bioclipse.ui.accountWizardHelp");
//	    PlatformUI.getWorkbench().getHelpSystem().setHelp( getShell(), "accountWizardHelp" );
//	    getShell().setData("org.eclipse.ui.help", "net.bioclipse.ui.accountWizardHelp");
//	    PlatformUI.getWorkbench().getHelpSystem().setHelp( getShell(), "accountWizardHelp" );
//	    viewHelpButton( true );
//	    setHelpAvailable( true );
//	    getContainer().updateButtons();
//	    TrayDialog.setDialogHelpAvailable( true );

//		if ( usermanager.getUserNames().size() == 0) {
//			CreateUserDialog dialog 
//			= new CreateUserDialog( PlatformUI.getWorkbench()
//					.getActiveWorkbenchWindow()
//					.getShell(), sandbox );
//			dialog.open();
//			if (dialog.getReturnCode() == Window.OK) {
//				usermanager.switchUserContainer( sandbox );
//			}
//			else if (dialog.getReturnCode() == Window.CANCEL) {
//			    this.performCancel();
//			    
//			}
//		}
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Add an account to Bioclipse");
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		super.addPages();
		if ( usermanager != null && usermanager.getUserNames().size() == 0) {
		   createUserPage = new CreateUserWizardPage("Create user", sandbox);
		   createUserPage. setTitle("Create Bioclipse Account");
		   createUserPage.setDescription("Bioclipse wants to create an account " +
		   		"for storing all your different passwords in one password " +
		   		"encrypted file.");
		   addPage( createUserPage );
		} else if ( !sandbox.isLoggedIn() ) {	
			loginPage = new LoginWizardPage("loginPage", sandbox);
			loginPage.setTitle("Log In To Your Bioclipse Account");
			loginPage.setDescription("Before adding an acount you have to " +
					"loggin or create a new acount.");
			addPage(loginPage);
		}
		
		addAccountPage = new NewAccountWizardPage("addAccountPage", sandbox);
		addAccountPage.setTitle("New Account");
		addAccountPage.setDescription("Add a third-part account to " +
				"Bioclipse.\nAll fields denoted with an asterisk (*) are " +
				"required.");
		addPage(addAccountPage);


		setDefaultPageImageDescriptor(ImageDescriptor
				.createFromFile(this.getClass(),
						"BioclipseAccountLogo3_medium.png"));

		PlatformUI
		.getWorkbench()
		.getHelpSystem()
		.setHelp( getShell(), "net.bioclipse.ui.accountWizardHelp" );
	}

	@Override
	public boolean performFinish() {
	    // TODO Make use of the viewHelpButton(boolean)...
	    if ( !sandbox.isLoggedIn() ) {
	        if (usermanager == null)
	            usermanager = Activator.getDefault().getUserManager();	        
	        usermanager.logIn(loginPage.getUsername(), loginPage.getPassword());
	    }
	    addAccountPage.fireUpdate();
	    if (manipulateUserContainer) {
	        Collection<Account> accounts = sandbox.getLoggedInUser().getAccounts().values();
	        Iterator<Account> itr = accounts.iterator();
	        Account account;
	        while (itr.hasNext()) {
	            account = itr.next();
	            if ( account.getAccountType().equals( addAccountPage.getAccountType() ) ) {
	                MessageDialog.openInformation( PlatformUI.getWorkbench()
	                                               .getActiveWorkbenchWindow()
	                                               .getShell(),
	                                               "Account type already used",
	                                               "There is already an account of " +
	                        "that type." );
	                return false;
	            }
	        }

	        if (addAccountPage.createAccount()) {
	            if (usermanager != null)
	                usermanager.switchUserContainer( sandbox );
	            dispose();
	            return true;
	        } else
	            return false;
	    } else
	        return addAccountPage.createAccount();
	    
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
