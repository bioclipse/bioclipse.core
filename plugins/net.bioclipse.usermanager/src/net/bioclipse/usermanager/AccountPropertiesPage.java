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
import net.bioclipse.usermanager.business.IUserManager;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Klas Jšnsson (aka "konditorn")
 *	
 */
public class AccountPropertiesPage {

	private Composite accountComposite;
	private Collection<Property> properties;
	private Label[] accountLabels;
	private Text[] accountTxt;
	private ArrayList<Text> requiredFields = new ArrayList<Text>();
	private Image reqImage = FieldDecorationRegistry.getDefault()
			.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED)
			.getImage();
	private Image errImage = FieldDecorationRegistry.getDefault()
			.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
			.getImage();
	private AccountType accountType;
	private Boolean errorFlag = false;
	private NewAccountWizardPage mainPage;
	
	public AccountPropertiesPage(Composite parent, 
			AccountType accountType, NewAccountWizardPage nawp) {
		int noOfFields = 0, noOfSecretFields = 0, i = 0;
		mainPage = nawp;
		Iterator<Property> propertyIter;
		Property temp;
		
		this.accountType = accountType;
		properties = accountType.getProperties();
		for (propertyIter = properties.iterator(); propertyIter.hasNext(); ) {
			if (propertyIter.next().isSecret())
				noOfSecretFields++;
		}
		noOfFields = properties.size() + noOfSecretFields;

		accountComposite = new Composite(parent, SWT.NONE);			
		accountLabels = new Label[noOfFields];
		accountTxt = new Text[noOfFields];
		
		if (accountType.hasLogo()) {
			accountComposite.setLayout(new GridLayout(3, false));
			new Label(accountComposite, SWT.NONE);
			new Label(accountComposite, SWT.NONE);
			Label logo = new Label(accountComposite, SWT.TRAIL);
			ImageDescriptor imDesc = ImageDescriptor
					.createFromURL(accountType.getLogoPath());
			Image im = imDesc.createImage();
			logo.setImage(im);
		} else
			accountComposite.setLayout(new GridLayout(2, false));
			
		propertyIter = properties.iterator();
		while (propertyIter.hasNext()) {
			temp = propertyIter.next();
			if (temp.isSecret()) {
				addComponents(i, SWT.BORDER | SWT.PASSWORD, temp.isRequired(),
						temp.getName());
				i++;
				addComponents(i, SWT.BORDER | SWT.PASSWORD, temp.isRequired(),
						"Repeat " + temp.getName());
				final int my_i = i;
				final ControlDecoration deco = new ControlDecoration(
								accountTxt[my_i], SWT.TOP | SWT.RIGHT);
				deco.setDescriptionText("The value has to be the same as " +
						"the value above");
				deco.setImage(errImage);
				deco.setShowOnlyOnFocus(false);
				deco.hide();
				accountTxt[i].addKeyListener(new KeyListener() {
					
					@Override
					public void keyReleased(KeyEvent e) {
						if (!(accountTxt[my_i-1].getText().equals(
								accountTxt[my_i].getText()))) {							
							deco.show();
							errorFlag = true;
							mainPage.setErrorMessage("The value has to be " +
									"the same as the value above");
						} else {
							deco.hide();
							if (isAllRequierdPropertiesFilledIn()) {
								mainPage.setErrorMessage(null);
								errorFlag = false;
							} else {
								errorFlag = true;
								createMissingFieldsError();
							}
							
						}

					}
					
					@Override
					public void keyPressed(KeyEvent e) {	
					}
				});
			} else {
				addComponents(i, SWT.BORDER, temp.isRequired(), temp.getName());
			}
			i++;
		}
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
			String labelTxt) {
		GridData txtData = new GridData(SWT.FILL, SWT.NONE, true, true);
		txtData.widthHint = 220;
		accountLabels[index] = new Label(accountComposite, SWT.NONE);
		accountLabels[index].setText(labelTxt + ":");
		accountTxt[index] = new Text(accountComposite, style);
		accountTxt[index].setToolTipText(accountLabels[index].getText()
				.substring(0, (accountLabels[index].getText().length()-1)
						));

		accountTxt[index].setLayoutData(txtData);
		if (required) {
			setReqDeco(accountLabels[index]);
			requiredFields.add(accountTxt[index]);
		}
		if (accountType.hasLogo())
			new Label(accountComposite, SWT.NONE);
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
		} else if (!isFieldsProperlyFilled())
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
	
	/**
	 * Create an account with the properties values that has been written in the 
	 * respective text-field. 
	 */
	public void createAccount() {
		// TODO un-comment so it acutely do what it's supposed to do and remove
		// the printing...
		String accountId = accountType.getName() + "_0";
//		IUserManager usermanager = Activator.getDefault().getUserManager();
//		HashMap<String, String> properties = new HashMap<String, String>();
		int i = 0;
//		while (usermanager.accountExists(accountId)){
//			i++;
//			accountId = accountType.getName() + "_" + i;
//		}
		
		System.out.println("Account type: " + accountType.getName());
		System.out.println("Account id: "+ accountId);
		System.out.println("Account properties:");
		
		for (i = 0; i<accountLabels.length;i++) {
			// This if-statement make sure that repeated fields (e.g. 
			// "Repeat password") don't end up as a property of the new account.
			if (!(accountLabels[i].getText().startsWith("Repeat ")) ) {
//				properties.put(accountLabels[i].getText().substring(0, 
//				accountLabels[i].getText().length()-1), 
//				accountTxt[i].getText());		
				System.out.println(accountLabels[i].getText().substring(0, 
						accountLabels[i].getText().length()-1) +"\t" + 
						accountTxt[i].getText());
			} 
		}
//		usermanager.createAccount(accountId, properties, accountType);
	}
	
	/**
	 * This method reports if all of the necessary fields are filled in. 
	 *	
	 * @return True if all the the necessary fields are filled in
	 */
	public Boolean isAllRequierdPropertiesFilledIn() {
		Iterator<Text> itr = requiredFields.iterator();
		while (itr.hasNext()) {
			if (itr.next().getText().isEmpty())
				return false;
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
	
	/**
	 * A method to check that all the fields are valid.
	 * 
	 * @return True if everything is ok 
	 */
	public Boolean isFieldsProperlyFilled() {		
		return !errorFlag;
	}

	public void dispose() {
		dispose();
	}
}
