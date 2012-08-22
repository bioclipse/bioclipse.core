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

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.usermanager.dialogs.DialogArea;
import net.bioclipse.usermanager.dialogs.LoginDialog;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * This is the wizard page for log-in into Bioclipse.
 * 
 * @author Klas Jšnsson (aka "konditorn") 
 *
 */
public class LoginWizardPage extends WizardPage {

    private static final Logger logger 
    = Logger.getLogger(LoginDialog.class);
    private DialogArea loginDialogArea;
	private UserContainer sandbox;
	private boolean stop = false;
	
	protected LoginWizardPage(String pageName, UserContainer userContainer) {
		super(pageName);
		this.loginDialogArea = new DialogArea(userContainer ,true, this);
		this.sandbox = userContainer;
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
	
	@Override
	public void setVisible(boolean visible) {  
	    if ( !visible && loginDialogArea.isFilledIn() ) 
	        try {
	            sandbox.signIn( getUsername(), getPassword(), null );
	            getNextPage().setVisible( true );
	            super.setVisible( visible );
	       } catch (IllegalArgumentException e) {
	           MessageDialog.openInformation( PlatformUI.getWorkbench()
	                                         .getActiveWorkbenchWindow()
	                                         .getShell(), "Could not sign in "
	                                         + getUsername(),  e.getMessage() );
	           getWizard().getContainer().showPage( this );
	           getWizard().getContainer().updateButtons();
	       }
	    else
	        super.setVisible( visible );  
	}

	@Override
	public void performHelp() {
	    System.out.println("Perform help in LoginWizardPage");
	    PlatformUI.getWorkbench().getHelpSystem().displayHelpResource( "./html/AccountHelp.html" );
	}
}

