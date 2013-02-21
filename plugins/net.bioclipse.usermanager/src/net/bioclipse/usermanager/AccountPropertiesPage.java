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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import net.bioclipse.usermanager.AccountType.Property;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Klas J�nsson (aka "konditorn")
 *	
 */
public class AccountPropertiesPage {
    /* I've left some out-comment code in this class. That code adds an extra 
     * field for properties marked as secret*/
	private Composite accountComposite, propComposite;
	private Collection<Property> properties;
	private Label[] accountLabels;
	private Text[] accountTxt;
	private Label accountNameLabel;
	private Text accountNameTxt;
//	private Button testLoginButton;
	private ArrayList<Text> requiredFields = new ArrayList<Text>();
	private Image reqImage = FieldDecorationRegistry.getDefault()
			.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED)
			.getImage();
	private AccountType accountType;
	private NewAccountWizardPage mainPage;
	private UserContainer sandbox;
	private String accountId = "";
	private HashMap<String, String> accountProperties = new HashMap<String, String>();
	private Group accountPropGroup;
	
	public AccountPropertiesPage(Composite parent, 
			AccountType accountType, NewAccountWizardPage nawp, UserContainer sandbox) {
	    
	    this.sandbox = sandbox;
		int noOfFields = 0, i = 0;
		mainPage = nawp;
		Iterator<Property> propertyIter;
		Property temp;
		this.accountType = accountType;
		properties = accountType.getProperties();
//		for (propertyIter = properties.iterator(); propertyIter.hasNext(); ) {
//			if (propertyIter.next().isSecret())
//				noOfSecretFields++;
//		}
		noOfFields = properties.size();// + noOfSecretFields;

		accountComposite = new Composite(parent, SWT.NONE);			
		accountComposite.setLayout(new GridLayout(1, false));

		GridData txtData = new GridData(SWT.FILL, SWT.NONE, true, true);
		
		Composite accountNameComposite = new Composite(accountComposite, SWT.NONE);           
        accountNameComposite.setLayout(new GridLayout(2, false));
		accountNameLabel = new Label(accountNameComposite, SWT.NONE);
		accountNameLabel.setText( "Account name: " );
		accountNameTxt = new Text(accountNameComposite, SWT.BORDER);
		accountNameTxt.setLayoutData( txtData );
		accountNameTxt.setText( accountType.getName() );
		accountNameTxt.addModifyListener( new ModifyListener() {
		    
		    @Override
		    public void modifyText( ModifyEvent e ) {
		        accountId = accountNameTxt.getText();
		        mainPage.setPageComplete( isAllRequierdPropertiesFilledIn() );
		    }
		} );
		
		accountPropGroup = new Group(accountComposite, SWT.SHADOW_ETCHED_IN);
		accountPropGroup.setText( "Third-part account properties" );
		accountLabels = new Label[noOfFields];
		accountTxt = new Text[noOfFields];
	    txtData.widthHint = 220;
	    
		if (accountType.hasLogo()) {
		    accountPropGroup.setLayout(new GridLayout(2, false));
		} else 
		    accountPropGroup.setLayout(new GridLayout(1, false));
		
		propComposite = new Composite( accountPropGroup, SWT.NONE );
		propComposite.setLayout( new GridLayout( 2, false ) );
		
		propertyIter = properties.iterator();
		while (propertyIter.hasNext()) {
			temp = propertyIter.next();
			if (temp.isSecret()) {
				addComponents(i, SWT.BORDER | SWT.PASSWORD, temp.isRequired(),
						temp.getName(), temp.getDefaultValue());
//				i++;
//				addComponents(i, SWT.BORDER | SWT.PASSWORD, temp.isRequired(),
//						"Repeat " + temp.getName());
//				final int my_i = i;
//				final ControlDecoration deco = new ControlDecoration(
//								accountTxt[my_i], SWT.TOP | SWT.RIGHT);
//				deco.setDescriptionText("The value has to be the same as " +
//						"the value above");
//				deco.setImage(errImage);
//				deco.setShowOnlyOnFocus(false);
//				deco.hide();
//				accountTxt[i].addKeyListener(new KeyListener() {
//					
//					@Override
//					public void keyReleased(KeyEvent e) {
//						if (!(accountTxt[my_i-1].getText().equals(
//								accountTxt[my_i].getText()))) {							
//							deco.show();
//							errorFlag = true;
//							mainPage.setErrorMessage("The value has to be " +
//									"the same as the value above");
//						} else {
//							deco.hide();
//							if (isAllRequierdPropertiesFilledIn()) {
//								mainPage.setErrorMessage(null);
//								errorFlag = false;
//							} else {
//								errorFlag = true;
//								createMissingFieldsError();
//							}
//							
//						}
//
//					}
//					
//					@Override
//					public void keyPressed(KeyEvent e) {	
//					}
//				});
			} else {
				addComponents(i, SWT.BORDER, temp.isRequired(), temp.getName(), temp.getDefaultValue() );
			}
			i++;
		}
		Label logo = new Label(accountPropGroup, SWT.TRAIL);
		ImageDescriptor imDesc = ImageDescriptor
		        .createFromURL(accountType.getLogoPath());
		Image im = imDesc.createImage();
		logo.setImage(im);
		
		// TODO Make the logic behind this button work properly 
//		new Label(accountComposite, SWT.NONE);
//		if (accountType.hasLogo()) {
//		    new Label(accountComposite, SWT.NONE);
//		}
//		testLoginButton = new Button(accountComposite, SWT.PUSH);
//		testLoginButton.setText( "Test log-in" );
//		testLoginButton.addSelectionListener( new SelectionListener() {
//            
//            @Override
//            public void widgetSelected( SelectionEvent e ) {
//                accountLoggIn();                          
//            }
//            
//            @Override
//            public void widgetDefaultSelected( SelectionEvent e ) {
//           
//            }
//            
//        } );
		
		mainPage.setPageComplete( isAllRequierdPropertiesFilledIn() );
		
		accountPropGroup.setFocus();
		
	}
	
	/**
	 * A help method to reuse code instead of writing it several times.
	 * 
	 * @param index The index of accountLabels and acccountTxt arrays
	 * @param style The style of the text-field, e.g. SWT.PASSWORD
	 * @param required A boolean to set i true if the field is required to be 
	 * 		filled in
	 * @param labelTxt The text shown by the label in front of the text-field
	 */
	private void addComponents(int index, int style, boolean required, 
			String labelTxt, String defaultValue) {
		GridData txtData = new GridData( SWT.FILL, SWT.NONE, true, true );
		txtData.widthHint = 220;
		accountLabels[index] = new Label( propComposite, SWT.NONE | SWT.RIGHT);
		accountLabels[index].setText( labelTxt + ":" );
		accountTxt[index] = new Text( propComposite, style );
		accountTxt[index].setToolTipText( accountLabels[index].getText()
				.substring( 0, ( accountLabels[index].getText().length()-1) ) );
		accountTxt[index].setLayoutData(txtData);
		accountTxt[index].setText( defaultValue );
		accountTxt[index].addModifyListener( new ModifyListener() {  
            @Override
            public void modifyText( ModifyEvent e ) {
                if (isAllRequierdPropertiesFilledIn()) {
                    mainPage.setErrorMessage( null );
                } else {
                    createMissingFieldsError();
                }
                mainPage.setPageComplete( isAllRequierdPropertiesFilledIn() );
            }
        } );
		if (required) {
			setReqDeco(accountLabels[index]);
			requiredFields.add(accountTxt[index]);
		}
	}
	
	/**
	 * This method create an error-message to let the user know which of the 
	 * required fields that isn't fill in.
	 */
	public void createMissingFieldsError() {
		String errorMessage = "Please fill in the following field";
		ArrayList<String> unfilledFields = getRequiredPropertiesLeft();
		if (unfilledFields.size()==1)
			errorMessage += ": " + unfilledFields.get(0);
		else if(unfilledFields.size() > 1) {
			errorMessage += "s:\n" + createErrorMessage(unfilledFields);
		} else if (!isAllRequierdPropertiesFilledIn())
			errorMessage = "Now you're way of...";
		else
			errorMessage = "WTF?";
		mainPage.setErrorMessage(errorMessage);
	}
	
	/**
	 * An recursive method that to get the names of the required fields that 
	 * aren't filled in yet.
	 * 
	 * @param unfilledFields An ArrayList containing the names of the un-filled 
	 * 			fields.
	 * @return the names of the un-filled (required) fields
	 */
	private String createErrorMessage(ArrayList<String> unfilledFields) {
		if (unfilledFields.size() == 2) 
			return unfilledFields.get(0) + " and " + unfilledFields.get(1);
		else {
			String field = unfilledFields.get(0);
			unfilledFields.remove(0);
			return field + ", " + createErrorMessage(unfilledFields);
		}			
	}
	
	/**
	 * Just a help method to avoid writing this stuff every time a field is
	 * required to fill-in.
	 * @param req The label that wants a required image
	 */
	private void setReqDeco(Label req) {
		ControlDecoration deco = new ControlDecoration(req, 
				SWT.TOP | SWT.RIGHT);
		deco.setDescriptionText("This value has to be filed in.");
		deco.setImage(reqImage);
		deco.setShowOnlyOnFocus(false);
	}
	
	/**
	 * This is get the composite created in the constructor.
	 * 
	 * @return A composite with the fields necessary to create an account
	 */
	public Composite getAccountPropertiesPage() {
		return accountComposite;
	}
	
	/**
	 * Set the focus to the first Text-field of the composite.
	 */
	public void giveFocus() {
		if (accountTxt.length > 0)
			accountTxt[0].forceFocus();
	}
	
	private String createAccountId() {
//	    String accountId = "";
//	    int i = 0;
//	    if (!sandbox.isLoggedIn())
//	        return accountId;
//	    
//	    accountId = accountType.getName() + "_" + 0;
//        while (sandbox.accountExists(accountId)){
//            i++;
//            accountId = accountType.getName() + "_" + i;
//        }
        return accountType.getName();
	}
	
	protected void upDateAccountName() {
	    if (accountId.isEmpty())
	        accountId = createAccountId();
	    
	    accountNameTxt.setText( accountId );
	}
	
	/**
	 * Create an account with the properties values that has been written in the 
	 * respective text-field. 
	 */
	public boolean createAccount() {
	    if ( sandbox.accountExists( getAccountId() ) ) {
	        MessageDialog
            .openInformation( PlatformUI
                              .getWorkbench()
                              .getActiveWorkbenchWindow()
                              .getShell(),
                              "Name trouble",
                              "You already have an account named " +
                              getAccountId()+", please choose an other name." );
	        
	        return false;
	    }
//	    Collection<Account> accounts = sandbox.getLoggedInUser().getAccounts().values();
//	    Iterator<Account> itr = accounts.iterator();
//	    Account account;
//	    while (itr.hasNext()) {
//	       account = itr.next();
//	       if ( account.getAccountType().equals( accountType ) ) {
//	           MessageDialog.openInformation( PlatformUI.getWorkbench()
//	                                         .getActiveWorkbenchWindow()
//	                                         .getShell(),
//	                                         "Account type already used",
//	                                         "There is already an account of " +
//	                                         "that type." );
//	             return false;
//	       }
//	    }
	    
	    int i = 0;
        HashMap<String, String> properties = new HashMap<String, String>();
	    if (accountId.isEmpty()) {
	       accountId = createAccountId();
	    } 
		
		for (i = 0; i<accountLabels.length;i++) {
			/* This if-statement make sure that repeated fields (e.g. "Repeat 
			 * password") don't end up as a property of the new account.*/
			if (!(accountLabels[i].getText().startsWith("Repeat ")) ) {
				properties.put(accountLabels[i].getText().substring(0, 
				accountLabels[i].getText().length()-1), 
				accountTxt[i].getText());		
			} 
		}
		sandbox.createAccount(accountId, properties, accountType);

		return true;
	}
	
	/**
	 * This method reports if all of the necessary fields are filled in. 
	 *	
	 * @return True if all the the necessary fields are filled in
	 */
	public Boolean isAllRequierdPropertiesFilledIn() {
		Iterator<Text> itr = requiredFields.iterator();
		while (itr.hasNext()) {
			if (itr.next().getText().isEmpty()) {
			    return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the names of the required fields that hasn't been filed in yet.
	 * If all of them are filled in it returns null.
	 * 
	 * @return the names of the required fields that hasn't been filed in yet
	 */
	public ArrayList<String> getRequiredPropertiesLeft() {
		Iterator<Text> itr = requiredFields.iterator();
		ArrayList<String> requiredPropertiesLeft = new ArrayList<String>();
		while (itr.hasNext()) {
			Text temp = itr.next();
			if (temp.getText().isEmpty())
				requiredPropertiesLeft.add(temp.getToolTipText());
		}	
		
		return requiredPropertiesLeft;
	}

	public void dispose() {
		dispose();
	}
	
	protected AccountType getAccountType() {
	    return accountType;
	}
	
	protected String getAccountId() {
	    return accountId;
	}
	
	protected HashMap<String, String> getProperties() {
	    return accountProperties;
	}
	
}
