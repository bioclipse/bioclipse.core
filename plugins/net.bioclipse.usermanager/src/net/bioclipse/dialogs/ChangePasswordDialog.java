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

package net.bioclipse.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog used to change the password for an user
 * 
 * @author jonalv
 *
 */
public class ChangePasswordDialog extends Dialog {

    private Text newPasswordText;
    private Text oldPasswordText;
    private String oldPassword;
    private String newPassword;
    /**
     * Create the dialog
     * @param parentShell
     */
    public ChangePasswordDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FormLayout());

        final Label oldPasswordLabel = new Label(container, SWT.NONE);
        final FormData formData = new FormData();
        formData.top = new FormAttachment(0, 50);
        formData.left = new FormAttachment(0, 35);
        oldPasswordLabel.setLayoutData(formData);
        oldPasswordLabel.setText("Old password:");

        final Label newPasswordLabel = new Label(container, SWT.NONE);
        final FormData formData_1 = new FormData();
        formData_1.top = new FormAttachment(0, 110);
        formData_1.left = new FormAttachment(oldPasswordLabel, 0, SWT.LEFT);
        newPasswordLabel.setLayoutData(formData_1);
        newPasswordLabel.setText("New password:");

        oldPasswordText = new Text(container, SWT.BORDER);
        final FormData formData_2 = new FormData();
        formData_2.right = new FormAttachment(100, -35);
        formData_2.bottom = new FormAttachment(oldPasswordLabel, 0, SWT.BOTTOM);
        oldPasswordText.setLayoutData(formData_2);

        newPasswordText = new Text(container, SWT.BORDER);
        formData_2.left = new FormAttachment(newPasswordText, 0, SWT.LEFT);
        final FormData formData_3 = new FormData();
        formData_3.left = new FormAttachment(newPasswordLabel, 5, SWT.RIGHT);
        formData_3.right = new FormAttachment(100, -35);
        formData_3.bottom = new FormAttachment(newPasswordLabel, 0, SWT.BOTTOM);
        newPasswordText.setLayoutData(formData_3);
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
        return new Point(500, 261);
    }
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Change password for Keyring user");
    }
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            
            this.oldPassword = oldPasswordText.getText();
            this.newPassword = newPasswordText.getText();
        }
        super.buttonPressed(buttonId);
    }

    /**
     * @return the new password
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * @return the old password
     */
    public String getOldPassword() {
        return oldPassword;
    }
}
