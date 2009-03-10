/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.usermanager.dialogs;

import net.bioclipse.usermanager.UserContainer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Dialog for creating a user
 * 
 * @author jonalv
 *
 */
public class CreateUserDialog extends TitleAreaDialog {

    private Label repeatPasswordLabel;
    private Label passwordLabel;
    private Label usernameLabel;
    private Text  repeatPasswordText;
    private Text  passwordText;
    private Text  userNameText;
    private UserContainer userContainer;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public CreateUserDialog( Shell parentShell, 
                             UserContainer userContainer ) {
        super(parentShell);
        this.userContainer = userContainer;
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayout(new FormLayout());
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        usernameLabel = new Label(container, SWT.NONE);
        final FormData formData = new FormData();
        usernameLabel.setLayoutData(formData);
        usernameLabel.setText("Username:");

        passwordLabel = new Label(container, SWT.NONE);
        final FormData formData_1 = new FormData();
        passwordLabel.setLayoutData(formData_1);
        passwordLabel.setText("Password:");

        repeatPasswordLabel = new Label(container, SWT.NONE);
        final FormData formData_2 = new FormData();
        repeatPasswordLabel.setLayoutData(formData_2);
        repeatPasswordLabel.setText("Repeat password:");

        userNameText = new Text(container, SWT.BORDER);
        formData.bottom = new FormAttachment(userNameText, 0, SWT.BOTTOM);
        formData.right = new FormAttachment(userNameText, -5, SWT.LEFT);
        final FormData formData_3 = new FormData();
        formData_3.top = new FormAttachment(0, 19);
        userNameText.setLayoutData(formData_3);

        passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
        formData_3.left = new FormAttachment(passwordText, -317, SWT.RIGHT);
        formData_3.right = new FormAttachment(passwordText, 0, SWT.RIGHT);
        formData_1.bottom = new FormAttachment(passwordText, 0, SWT.BOTTOM);
        formData_1.right = new FormAttachment(passwordText, -5, SWT.LEFT);
        final FormData formData_4 = new FormData();
        formData_4.top = new FormAttachment(0, 63);
        passwordText.setLayoutData(formData_4);

        repeatPasswordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
        formData_2.bottom = new FormAttachment(repeatPasswordText, 0, SWT.BOTTOM);
        formData_2.right = new FormAttachment(repeatPasswordText, -5, SWT.LEFT);
        formData_4.left = new FormAttachment(repeatPasswordText, -316, SWT.RIGHT);
        formData_4.right = new FormAttachment(repeatPasswordText, 0, SWT.RIGHT);
        final FormData formData_5 = new FormData();
        formData_5.top = new FormAttachment(0, 107);
        formData_5.left = new FormAttachment(0, 154);
        formData_5.right = new FormAttachment(0, 470);
        repeatPasswordText.setLayoutData(formData_5);
        container.setTabList(new Control[] { userNameText, 
                                             passwordText, 
                                             repeatPasswordText, 
                                             usernameLabel, 
                                             passwordLabel, 
                                             repeatPasswordLabel });
        
        setTitle("Create User");
        
        return area;
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
        return new Point(500, 318);
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if( "".equals( userNameText.getText()       ) ||
                "".equals( passwordText.getText()       ) ||
                "".equals( repeatPasswordText.getText() ) ) {
                
                MessageDialog.openInformation( PlatformUI
                                               .getWorkbench()
                                               .getActiveWorkbenchWindow()
                                               .getShell(), 
                           "Please fill in all fields", 
                           "Fill in your username and your password (twice)");
                return;
            }
            if( !passwordText.getText().equals(repeatPasswordText.getText()) ) {
                MessageDialog.openInformation( PlatformUI
                                               .getWorkbench()
                                               .getActiveWorkbenchWindow()
                                               .getShell(), 
                                               "Not same password", 
                                               "The repeated password doesnt "
                                               + "match the password");
                return;
            }
            userContainer.createUser( userNameText.getText(), 
                                                passwordText.getText() );
            userContainer.signIn( userNameText.getText(), 
                                       passwordText.getText(),
                                       null );
        }
        super.buttonPressed(buttonId);
    }
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create User");
	}
}
