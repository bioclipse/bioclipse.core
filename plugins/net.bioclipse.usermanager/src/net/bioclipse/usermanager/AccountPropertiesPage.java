/* *****************************************************************************
 * Copyright (c) 2007-2009 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 ******************************************************************************/package net.bioclipse.usermanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.bioclipse.usermanager.AccountType.Property;
import net.bioclipse.usermanager.business.IUserManager;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
	
	public AccountPropertiesPage(Composite parent, 
			AccountType accountType) {
		int noOfFields = 0, noOfSecretFields = 0, i = 0, contentMinHight;
		int rowSpace = 25;
		Iterator<Property> propertyIter;
		Property temp;
		GridData txtData= new GridData(GridData.FILL_HORIZONTAL);
		
		this.accountType = accountType;
		properties = accountType.getProperties();
		for (propertyIter = properties.iterator(); propertyIter.hasNext(); ) {
			if (propertyIter.next().isSecret())
				noOfSecretFields++;
		}
		noOfFields = properties.size() + noOfSecretFields;
		contentMinHight = noOfFields * rowSpace;
		
		accountComposite = new Composite(parent, SWT.NONE); 
		GridData gd_ac = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_ac.widthHint = 430;
		gd_ac.heightHint = contentMinHight;
		accountComposite.setLayoutData(gd_ac);
		accountComposite.setLayout(new GridLayout(2, false)); 
		// TODO Add a icon for the account using an icon provided by it self.
		
		accountLabels = new Label[noOfFields];
		accountTxt = new Text[noOfFields];
		
		propertyIter = properties.iterator();
		while (propertyIter.hasNext()) {
			temp = propertyIter.next();
			if (temp.isSecret()) {
				accountLabels[i] = new Label(accountComposite, SWT.NONE);
				accountLabels[i].setText(temp.getName() + ":");
				accountTxt[i] = new Text(accountComposite, 
					SWT.BORDER | SWT.PASSWORD);
				accountTxt[i].setToolTipText(accountLabels[i].getText());
				accountTxt[i].setLayoutData(txtData);
				if (temp.isRequired()) {
					setReqDeco(accountLabels[i]);
					requiredFields.add(accountTxt[i]);
				}
				i++;
				accountLabels[i] = new Label(accountComposite, SWT.NONE);
				accountLabels[i].setText("Repeat " + temp.getName() + ":");
				accountTxt[i] = new Text(accountComposite, 
						SWT.BORDER | SWT.PASSWORD);
				accountTxt[i].setLayoutData(txtData);
				accountTxt[i].setToolTipText(accountLabels[i].getText());
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
						} else {
							deco.hide();
							errorFlag = false;
						}
//						accountTxt[my_i].getParent().getAccessible().sendEvent(SWT.Selection, null);
//						accountComposite.getParent().getAccessible()
//						.sendEvent(SWT.Activate, null);
					}
					
					@Override
					public void keyPressed(KeyEvent e) {	
//						accountComposite.getParent().getAccessible()
//						.sendEvent(SWT.Activate, null);
					}
				});
				
				if (temp.isRequired()) {
					setReqDeco(accountLabels[i]);
					requiredFields.add(accountTxt[i]);
				}
			} else {
				accountLabels[i] = new Label(accountComposite, SWT.NONE);
				accountLabels[i].setText(temp.getName());
				accountTxt[i] = new Text(accountComposite, SWT.BORDER);
				accountTxt[i].setToolTipText(accountLabels[i].getText());
				accountTxt[i].setLayoutData(txtData);
				if (temp.isRequired()) {
					setReqDeco(accountLabels[i]);
					requiredFields.add(accountTxt[i]);
				}
			}
			i++;
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
		String accountId = "Account0";
//		IUserManager usermanager = Activator.getDefault().getUserManager();
//		HashMap<String, String> properties = new HashMap<String, String>();
		int i = 0;
//		while (usermanager.accountExists(accountId)){
//			i++;
//			accountId = "Account" + i;
//		}
		
		System.out.println("Account type: " + accountType.getName());
		System.out.println("Account id: "+ accountId);
		System.out.println("Account properties:");
		
		for (i = 0; i<accountLabels.length;i++) {
//			properties.put(accountLabels[i].getText(), accountTxt[i].getText());
		
			System.out.println(accountLabels[i].getText() +"\t" + 
			accountTxt[i].getText());
		
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
