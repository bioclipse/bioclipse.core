/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.dialogs;

import net.bioclipse.usermanager.AccountType;
import net.bioclipse.usermanager.UserContainer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Dialog for inputing an account name
 * 
 * @author jonalv
 *
 */
public class CreateAccountDialog extends Dialog {

	private Combo combo;
	private Text text;
	private String accountName;
	private AccountType accountType;
	
	private UserContainer sandBoxUserManager;
	
	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public CreateAccountDialog( Shell parentShell, 
			                    UserContainer sandBoxUserManager ) {
		
		super(parentShell);
		this.sandBoxUserManager = sandBoxUserManager;
	}

	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());

		final Label accountNameLabel = new Label(container, SWT.NONE);
		final FormData formData = new FormData();
		formData.left = new FormAttachment(0, 15);
		accountNameLabel.setLayoutData(formData);
		accountNameLabel.setText("Account name:");

		text = new Text(container, SWT.BORDER);
		formData.bottom = new FormAttachment(text, 0, SWT.BOTTOM);
		final FormData formData_1 = new FormData();
		formData_1.right = new FormAttachment(100, -28);
		formData_1.bottom = new FormAttachment(0, 45);
		formData_1.left = new FormAttachment(accountNameLabel, 5, SWT.RIGHT);
		text.setLayoutData(formData_1);

		final Label accountTypeLabel = new Label(container, SWT.NONE);
		final FormData formData_2 = new FormData();
		formData_2.bottom = new FormAttachment(0, 92);
		formData_2.top = new FormAttachment(0, 75);
		formData_2.left = new FormAttachment(accountNameLabel, 0, SWT.LEFT);
		accountTypeLabel.setLayoutData(formData_2);
		accountTypeLabel.setText("Account type:");

		combo = new Combo(container, SWT.NONE|SWT.READ_ONLY);
		final FormData formData_3 = new FormData();
		formData_3.right = new FormAttachment(100, -28);
		formData_3.left = new FormAttachment(accountNameLabel, 5, SWT.RIGHT);
		formData_3.bottom = new FormAttachment(accountTypeLabel, 0, SWT.BOTTOM);
		combo.setLayoutData(formData_3);
		combo.setItems(sandBoxUserManager.getAvailableAccountTypeNames());
		if(combo.getItemCount() > 0) {
			combo.select(0);
		}
		//
		return container;
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(505, 203);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Input new account name");
	}
	
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if( "".equals(text.getText()) ) {
				MessageDialog.openInformation(PlatformUI
						                      .getWorkbench()
						                      .getActiveWorkbenchWindow()
						                      .getShell(),
						                      "Please name your account", 
						                      "Write a name for your account");
				return;
			}
			this.accountName = text.getText();
			this.accountType 
				= sandBoxUserManager
				  .getAvailableAccountTypes()[combo.getSelectionIndex()];
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * @return the account name
	 */
	public String getAccountName() {
		return accountName;
	}
	
	/**
	 * @return the chosen accountType
	 */
	public AccountType getAccountType() {
		return accountType;
	}
}
