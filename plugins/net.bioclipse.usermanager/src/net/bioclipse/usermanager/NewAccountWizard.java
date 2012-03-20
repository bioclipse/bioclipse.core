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

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.usermanager.business.IUserManager;
import net.bioclipse.usermanager.dialogs.CreateUserDialog;
import net.bioclipse.usermanager.dialogs.LoginDialog;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * A wizard for handling the users third parts accounts
 *   
 * @author Klas Jšnsson (aka "konditorn")
 * 
 */
public class NewAccountWizard extends Wizard implements INewWizard {

	private static final Logger logger 
    	= Logger.getLogger(LoginDialog.class);
	
	private NewAccountWizardPage addAccountPage;
	private LoginWizardPage loginPage;
	
	public NewAccountWizard() {
		IUserManager usermanager = Activator.getDefault().getUserManager();
		if (usermanager.getUserNames().size() == 0) {
			UserContainer sandbox = usermanager.getSandBoxUserContainer();
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
		IUserManager usermanager = Activator.getDefault().getUserManager();
		if ( !usermanager.isLoggedIn() ) {
			UserContainer sandbox = usermanager.getSandBoxUserContainer();
			loginPage = new LoginWizardPage("loginPage", sandbox);
			loginPage.setTitle("Log In To Your Bioclipse Account");
			loginPage.setDescription("Before adding an acount you have to " +
					"loggin or create a new acount.");
			addPage(loginPage);
		}
		
		addAccountPage = new NewAccountWizardPage("addAccountPage");
		addAccountPage.setTitle("New Account");
		addAccountPage.setDescription("Add a third-part account to Bioclipse");
		addPage(addAccountPage);


		setDefaultPageImageDescriptor(ImageDescriptor
				.createFromFile(this.getClass(),
						"BioclipseAccountLogo3_medium.png"));
	}

	@Override
	public boolean performFinish() {
		IUserManager usermanager = Activator.getDefault().getUserManager();
		if ( !usermanager.isLoggedIn() ) {
			performLogin();
		}
		
		if (addAccountPage.createAccount()) {
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
	
	/**
	 * This method log in the user.
	 */
	private void performLogin() {
		 final String username = loginPage.getUsername();
         final String password = loginPage.getPassword();
         Job job = new Job("Signing in " + username) {
             
             @Override
             protected IStatus run( IProgressMonitor monitor ) {

                 try {
                     int scale = 1000;
                     monitor.beginTask( "Signing in...", 
                                        IProgressMonitor.UNKNOWN );
                     Activator.getDefault().getUserManager()
                              .signInWithProgressBar( 
                                   username,
                                   password, 
                                   new SubProgressMonitor(
                                           monitor, 
                                           1 * scale) );
                 }
                 catch ( final Exception e ) {
                     Display.getDefault().asyncExec(new Runnable() {

                         public void run() {
                             MessageDialog.openInformation( 
                                        PlatformUI
                                        .getWorkbench()
                                        .getActiveWorkbenchWindow()
                                        .getShell(), 
                                        "Could not sign in "
                                        + username, 
                                        e.getMessage() );
                             try {
                                 ((IHandlerService) 
                                 PlatformUI.getWorkbench()
                                     .getActiveWorkbenchWindow()
                                     .getService(IHandlerService.class) )
                                     .executeCommand(
                                         "net.bioclipse.usermanager" +
                                             ".commands.login", 
                                         null );
                             }
                             catch ( Exception e ) {
                                 LogUtils.handleException( 
                                      e, 
                                      logger, 
                                      "net.bioclipse.usermanager" );
                             }
                         }
                     });
                 }
                 finally {
                     monitor.done();
                 }
                 return Status.OK_STATUS;
             }
         };
         job.setUser( true );
         job.schedule();
	}
}
