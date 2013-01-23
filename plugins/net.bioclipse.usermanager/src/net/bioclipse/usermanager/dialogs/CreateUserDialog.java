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
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Group;

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
    private Group group;
    private Label label;
    
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
        setMessage("Bioclipse wants to create an account for storing all your different passwords in one password encrypted file.");
        setTitleImage(SWTResourceManager.getImage(CreateUserDialog.class, "/net/bioclipse/usermanager/BioclipseAccountLogo3_medium.png"));
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
        formData_1.right = new FormAttachment(usernameLabel, 0, SWT.RIGHT);
        passwordLabel.setLayoutData(formData_1);
        passwordLabel.setText("Password:");

        repeatPasswordLabel = new Label(container, SWT.NONE);
        final FormData formData_2 = new FormData();
        formData_2.top = new FormAttachment(passwordLabel, 21);
        formData_2.right = new FormAttachment(usernameLabel, 0, SWT.RIGHT);
        repeatPasswordLabel.setLayoutData(formData_2);
        repeatPasswordLabel.setText("Repeat password:");

        userNameText = new Text(container, SWT.BORDER);
        formData.right = new FormAttachment(userNameText, -6);
        final FormData formData_3 = new FormData();
        formData_3.top = new FormAttachment(usernameLabel, -3, SWT.TOP);
        formData_3.left = new FormAttachment(0, 151);
        formData_3.right = new FormAttachment(100, -36);
        userNameText.setLayoutData(formData_3);

        passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
        formData_1.top = new FormAttachment(passwordText, 3, SWT.TOP);
        formData_1.left = new FormAttachment(passwordText, -64, SWT.LEFT);
        final FormData formData_4 = new FormData();
        formData_4.left = new FormAttachment(0, 151);
        formData_4.right = new FormAttachment(100, -37);
        formData_4.top = new FormAttachment(userNameText, 14);
        passwordText.setLayoutData(formData_4);

        repeatPasswordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
        final FormData formData_5 = new FormData();
        formData_5.left = new FormAttachment(repeatPasswordLabel, 6);
        formData_5.right = new FormAttachment(repeatPasswordLabel, 448, SWT.RIGHT);
        formData_5.top = new FormAttachment(passwordText, 16);
        repeatPasswordText.setLayoutData(formData_5);
        
        group = new Group(container, SWT.BORDER);
        formData.top = new FormAttachment(group, 20);
        FormData fd_group = new FormData();
        fd_group.right = new FormAttachment(0, 619);
        fd_group.top = new FormAttachment(0, 10);
        fd_group.left = new FormAttachment(0, 10);
        group.setLayoutData(fd_group);
        
        label = new Label(group, SWT.WRAP);
        label.setText("Bioclipse wants to store your usernames and passwords in a password encrypted file on your harddrive. This means that you only need to log in once to Bioclipse and then Bioclipse will take care of the rest.\n\nIn order to do this you will need to create a Bioclipse account on this computer. Please give a username and password for your new Bioclipse account.");
        label.setBounds(10, 10, 577, 82);
        container.setTabList(new Control[] { userNameText, 
                                             passwordText, 
                                             repeatPasswordText, 
                                             usernameLabel, 
                                             passwordLabel, 
                                             repeatPasswordLabel });
        
        setTitle("Create your Bioclipse Account");
        
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
        return new Point(629, 376);
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
		newShell.setText("Create your Bioclipse Account");
	}
}
