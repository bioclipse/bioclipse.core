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

import net.bioclipse.usermanager.business.IUserManager;
import net.bioclipse.usermanager.dialogs.CreateUserDialog;
import net.bioclipse.usermanager.dialogs.LoginDialog;

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

	private NewAccountWizardPage mainPage;
	
	public NewAccountWizard() {
	    boolean cancel = false;
	    IUserManager usermanager = Activator.getDefault().getUserManager();
	    if (usermanager.getUserNames().size() == 0) {
	        UserContainer sandbox = usermanager.getSandBoxUserContainer();
	        CreateUserDialog dialog 
	            = new CreateUserDialog( PlatformUI.getWorkbench()
	                                              .getActiveWorkbenchWindow()
	                                              .getShell(), sandbox );
	        dialog.open();
	        if (dialog.getReturnCode() == dialog.OK) {
	            usermanager.switchUserContainer( sandbox );
	        }
	        else if (dialog.getReturnCode() == dialog.CANCEL) {
	            cancel = true;
	        }
	    }
	    
	    if ( !cancel && !usermanager.isLoggedIn() ) {
	        UserContainer sandbox = usermanager.getSandBoxUserContainer();
	        LoginDialog loginDialog 
                    = new LoginDialog( PlatformUI.getWorkbench()
	                                             .getActiveWorkbenchWindow()
	                                             .getShell(),
	                                   sandbox );

            loginDialog.open();
            if ( loginDialog.getReturnCode() == LoginDialog.OK ) {
                if ( loginDialog.isUserContainerEdited() ) {
                    Activator.getDefault().getUserManager()
                             .switchUserContainer(sandbox);
                }
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
        // TODO add the Bioclipse create-account and log-in dialogs as pages
        // are the any more to be added?
        mainPage = new NewAccountWizardPage("mainPage");
        mainPage.setTitle("New Account");
        mainPage.setDescription("Add a third-part account to Bioclipse");
        addPage(mainPage);
        setDefaultPageImageDescriptor(ImageDescriptor
        		.createFromFile(this.getClass(),
        				"BioclipseAccountLogo3_medium.png"));
    }
    
	@Override
	public boolean performFinish() {
		if (mainPage.createAccount()) {
			dispose();
			return true;
		} else
			return false;
	}
	
	@Override
	public boolean canFinish() {
		return mainPage.isPageComplete();
	}

}
