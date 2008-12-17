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
import net.bioclipse.usermanager.Activator;
import net.bioclipse.usermanager.UserContainer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.PlatformUI;
/**
 * Dialog that prompts a user for password and logs the user in if 
 * the correct password is given
 * 
 * @author jonalv
 *
 */
public class PassWordPromptDialog extends Dialog {
    private Text text;
    private String username;
    private UserContainer sandboxUserContainer;
    /**
     * Create the dialog
     * @param parentShell
     */
    public PassWordPromptDialog( Shell parentShell, 
                                 String username,
                                 UserContainer sandboxUserContainer ) {
        super(parentShell);
        this.username = username;
        this.sandboxUserContainer = sandboxUserContainer;
    }
    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FormLayout());
        final Label passswordLabel = new Label(container, SWT.NONE);
        passswordLabel.setText("Password:");
        final FormData formData = new FormData();
        formData.top = new FormAttachment(0, 35);
        formData.left = new FormAttachment(0, 10);
        passswordLabel.setLayoutData(formData);
        text = new Text(container, SWT.BORDER | SWT.PASSWORD);
        final FormData formData_1 = new FormData();
        formData_1.left = new FormAttachment(passswordLabel, 5, SWT.RIGHT);
        formData_1.right = new FormAttachment(100, -10);
        formData_1.top = new FormAttachment(passswordLabel, -27, SWT.BOTTOM);
        text.setLayoutData(formData_1);
        final Label label = new Label(container, SWT.NONE);
        final FormData formData_2 = new FormData();
        formData_2.top = new FormAttachment(0, 85);
        formData_2.left = new FormAttachment(text, 0, SWT.LEFT);
        label.setLayoutData(formData_2);
        label.setText("Enter password for user: " + username);
        label.pack();
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
        return new Point(481, 151);
    }
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Password prompt");
    }
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            try {
                sandboxUserContainer.signOut();
                sandboxUserContainer.signIn( username, text.getText(), null );
            }
            catch(IllegalArgumentException e) {
                MessageDialog.openInformation( PlatformUI
                                               .getWorkbench()
                                               .getActiveWorkbenchWindow()
                                               .getShell(), 
                                               "Could not log in", 
                                               e.getMessage() );
                return;
            }
        }
        super.buttonPressed(buttonId);
    }
}
