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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * This class creates an object that can provide the components and 
 * the logic for handling them for e.g. a login dialog or creating a Bioclipse 
 * account
 *   
 * @author Klas J�nsson (aka "konditorn")
 * 
 */
public class DialogArea implements Listener {
	
	private UserContainer userContainer=null;
	private Label usernameLabel;
	private Label passwordLabel;
	private Label repeatPasswordLabel;
	private Text passwordText;
	private Text usernameText;
	private Text repeatPasswordText;
	private boolean userContainerEdited;
	private boolean createNewAccountButton;
	private boolean errorFlag;
	private WizardPage page;
	
	/**
	 * The constructor.
	 * 
	 * @param userContainer
	 * @param createNewAccountButton Set to true if this button is wanted.
	 * @param page If this is a part of a wizard add it here if not set it to 
	 * null
	 */
	public DialogArea(UserContainer userContainer, 
			Boolean createNewAccountButton, WizardPage page) {
		this.userContainer = userContainer;
		this.userContainerEdited = false;
		this.createNewAccountButton = createNewAccountButton;
		this.errorFlag = true;
		this.page = page;
	}
	
	/**
	 * This method is used when ever a dialog for login in Bioclipse is wanted.
	 * 
	 * @param parent The component where it will live 
	 * @return A composite with the different SWT-components that are needed.
	 */
	public Composite getLoginArea(Composite parent) {			
		Composite container = new Composite(parent, SWT.NONE);
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

        usernameText = new Text(container, SWT.BORDER);
        formData.bottom = new FormAttachment(usernameText, 0, SWT.BOTTOM);
        formData.right = new FormAttachment(usernameText, -5, SWT.LEFT);
        final FormData formData_2 = new FormData();
        formData_2.right = new FormAttachment(usernameText, 0, SWT.RIGHT);
        formData_2.left = new FormAttachment(usernameText, 0, SWT.LEFT);
        final FormData formData_3 = new FormData();
        formData_3.left = new FormAttachment(0, 140);
        formData_3.right = new FormAttachment(100, -34);
        formData_3.top = new FormAttachment(0, 53);
        usernameText.setLayoutData(formData_3);
        usernameText.addListener(SWT.CHANGED, this);
        
        passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
        formData_1.bottom = new FormAttachment(passwordText, 0, SWT.BOTTOM);
        formData_1.right = new FormAttachment(passwordText, -5, SWT.LEFT);
        formData_2.top = new FormAttachment(0, 93);
        passwordText.setLayoutData(formData_2);
        passwordText.addListener(SWT.CHANGED, this);
        
        if (createNewAccountButton) {
        	Button createNewKeyringButton = new Button(container, SWT.NONE);
        	createNewKeyringButton.addSelectionListener(new SelectionAdapter() {
        		/*
        		 * CREATE NEW USER 
        		 */
        		public void widgetSelected(SelectionEvent e) {

        			CreateUserDialog createDialog = 
        					new CreateUserDialog( PlatformUI
        							.getWorkbench()
        							.getActiveWorkbenchWindow()
        							.getShell(),
        							userContainer );
        			createDialog.open();
        			if(createDialog.getReturnCode() == Window.OK) {
        				createDialog.close();
        				EditUserDialog dialog = 
        						new EditUserDialog( PlatformUI
        								.getWorkbench()
        								.getActiveWorkbenchWindow()
        								.getShell(), 
        								userContainer );
        				dialog.open();
        				if(dialog.getReturnCode() == Window.OK) {
        					userContainerEdited = true;
        				}
        			}
        		}
        	});
        	final FormData formData_4 = new FormData();
        	formData_4.right = new FormAttachment(100, -34);
        	formData_4.top = new FormAttachment(0, 136);
        	createNewKeyringButton.setLayoutData(formData_4);
        	createNewKeyringButton.setText("Create new Account...");
        	container.setTabList(new Control[] { usernameText, 
        			passwordText, 
        			passwordLabel, 
        			usernameLabel, 
        			createNewKeyringButton });
        } else {
        	repeatPasswordLabel = new Label(container, SWT.NONE);
        	final FormData formData_4 = new FormData();
            repeatPasswordLabel.setLayoutData(formData_2);
            repeatPasswordLabel.setText("Repeat password:");
            
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
            repeatPasswordText.addListener(SWT.CHANGED, this);
            
            container.setTabList(new Control[] { usernameText, 
                                                 passwordText, 
                                                 repeatPasswordText, 
                                                 usernameLabel, 
                                                 passwordLabel, 
                                                 repeatPasswordLabel });
        }
        
        Label icon = new Label(container, SWT.NONE);
        ImageDescriptor imgDesc = ImageDescriptor
        		.createFromFile(this.getClass(),
        				"../../../../../icons/login_16.png");
        Image img = imgDesc.createImage();
        icon.setImage(img);
        final FormData formData_6 = new FormData();
        formData_6.bottom = new FormAttachment(usernameText, 0, SWT.RIGHT);
        formData_6.left = new FormAttachment(usernameText, 0, SWT.RIGHT);
        icon.setLayoutData(formData_6);
        
        usernameText.setFocus();
        
        return container;
	}
	
	public String getUsername() {
		return usernameText.getText();
	}
	
	public String getPassword() {
		return passwordText.getText();
	}
	
    public boolean isUserContainerEdited() {
        return userContainerEdited;
    }
    
	/**
	 * A method to see if all fields are filled in.
	 * 
	 * @return True if all fields are filled in.
	 */
    public boolean isFilledIn() {
    	if (createNewAccountButton) {
    		return ( !usernameText.getText().isEmpty() && 
    			!passwordText.getText().isEmpty() );
    	} else
     		return ( !usernameText.getText().isEmpty() && 
        			!passwordText.getText().isEmpty() && 
        			!repeatPasswordText.getText().isEmpty() );
    }
    
    /**
     * Returns a string that explains what's wrong, if nothing is wrong it 
     * returns null.
     *   
     * @return The error-message
     */
    public String getErrorMessage() {
    	String message = null;
    	if (!isFilledIn()) {
    		if (createNewAccountButton) {
    			message = "Please fill in all fields.\n" + 
        				"Fill in your username and your password.";
    		} else {
    		message = "Please fill in all fields.\n" + 
    				"Fill in your username and your password (twice).";
    		}
    	} else if (!createNewAccountButton && 
    			!passwordText.getText().equals(repeatPasswordText.getText()))
    		message = "Not same password:\n The repeated password doesnt" +
    				" match the password";
    	return message;
    }
    
    /**
     * This method reports if there's any error.
     *  
     * @return True if the exists an error.
     */
    public boolean getErrorFlag() {
    	return errorFlag;
    }

	@Override
	public void handleEvent(Event event) {
		if (!isFilledIn())
    		errorFlag = true;
    	else if (!createNewAccountButton && 
    			!passwordText.getText().equals(repeatPasswordText.getText()) )
    		errorFlag = true;
    	else
    		errorFlag = false;
		if (page != null) {
			page.setPageComplete(!errorFlag);
			page.getWizard().getContainer().updateButtons();
			page.setErrorMessage(getErrorMessage());
			page.getWizard().getContainer().updateMessage();
		}
	}
    
    
}
