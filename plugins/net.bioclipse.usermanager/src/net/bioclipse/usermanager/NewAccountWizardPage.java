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
import net.bioclipse.usermanager.business.IUserManager;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * The wizard page that handles the different parts accounts. 
 * 
 * @author Klas Jšnsson
 * TODO Make sure the created SWT-components are removed
 */
public class NewAccountWizardPage extends WizardPage implements Listener {
		
	private ArrayList<Composite> accountComposites = new ArrayList<Composite>();
	private Combo accountTypeCombo;
	private Composite accountSettings;
	private StackLayout accountStack;
	private ArrayList<AccountPropertiesPage> addedAccounts = 
			new ArrayList<AccountPropertiesPage>();
	private IUserManager usermanager = Activator.getDefault().getUserManager();
	
	protected NewAccountWizardPage(String pageName) {
		super(pageName);
		
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		
		Composite accountType = new Composite(container, SWT.NONE);
		GridData gd_accountType = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_accountType.widthHint = 430;
		gd_accountType.heightHint = 40;
		accountType.setLayoutData(gd_accountType);
				
		Label accountTypeTxt = new Label(accountType, SWT.NONE);
		accountTypeTxt.setBounds(10, 10, 120, 14);
		accountTypeTxt.setText("Select account type:");
		
		accountTypeCombo = new Combo(accountType, SWT.NONE | SWT.READ_ONLY);
		accountTypeCombo.setBounds(130, 10, 280, 22);
		accountTypeCombo.addListener(SWT.Selection, this);
		
		accountSettings = new Composite(container, SWT.NONE);
		GridData gd_accountSettings = new GridData(GridData.FILL_BOTH);
		gd_accountSettings.grabExcessVerticalSpace = false;
		gd_accountSettings.verticalAlignment = SWT.TOP;
		gd_accountSettings.heightHint = 48;
		accountType.setLayoutData(gd_accountSettings);
		accountStack = new StackLayout();
		accountSettings.setLayout(accountStack);
		
		// Adding the availably account-types to the combobox and a composite 
		// with the account specific fields to the array list of account 
		// composites.
		AccountType[] accountTypes = usermanager.getAvailableAccountTypes();
		for (int i = 0; i < accountTypes.length; i++) {
			accountTypeCombo.add(accountTypes[i].getName());
			addedAccounts.add(new AccountPropertiesPage(accountSettings, 
					accountTypes[i]));
			accountComposites.add(addedAccounts.get(i)
					.getAccountPropertiesPage());
		}
				
		if (accountComposites.size() > 0) {
			accountStack.topControl = accountComposites.get(0);
			accountTypeCombo.select(0);
			giveFocus();
		}
		setControl(container);
	}
	
		
	/**
	 * This method handles the combobox, i.e. it shows the selected account's
	 * composite below the combobox.
	 */
	@Override
	public void handleEvent(Event event) {
		if (event.widget == accountTypeCombo) {
			if (accountTypeCombo.getSelectionIndex() == -1){
				System.out.println("Please select an account-type");
			} else if (accountTypeCombo.getSelectionIndex() < 
					accountComposites.size()) { 
				accountStack.topControl = accountComposites.get(
						accountTypeCombo.getSelectionIndex());
				accountSettings.layout();
				giveFocus();
			}
		}	
	}
	
	/**
	 * This method gives focus to the component of the account composite that is 
	 * chosen by the accounts focus method.
	 */
	private void giveFocus() {
		addedAccounts.get(accountTypeCombo.getSelectionIndex()).giveFocus();
	}
	
	/**
	 * A method that creates a composite to be used when there isn't any 
	 * plug-ins found.
	 * 
	 * @param text A message to be showed in the composite 
	 * @return A composite with a text in it.
	 */
	private Composite empty(Composite parent) {
		Composite emptyComposite = new Composite(parent, SWT.NONE); 
		GridData gd_as = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_as.widthHint = 430;
		gd_as.heightHint = 161;
		emptyComposite.setLayoutData(gd_as);
		GridLayout gl = new GridLayout(2,false);
		emptyComposite.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		new Label(emptyComposite, SWT.CENTER).setText("Cant find any accounts");
				
		return emptyComposite;
	}
	
	/**
	 * This method tell the selected plug-in to create an account.
	 */
	protected Boolean createAccount(){
		AccountPropertiesPage account = 
				addedAccounts.get(accountTypeCombo.getSelectionIndex());
		if (account.isAllRequierdPropertiesFilledIn()) {
			account.createAccount();
			return true;
		} else {
			// TODO If the error-messages gets to large it is cut of.
			String errorMessage = "Please fill in the following field";
			ArrayList<String> unfilledFields = account
					.getRequiredPropertiesLeft();
			if (unfilledFields.size()==1)
				errorMessage += ": " + unfilledFields.get(0);
			else if(unfilledFields.size() > 1) {
				errorMessage += "s:\n" + createErrorMessage(unfilledFields);
			} else
				errorMessage = "WTF? All requierd fileds aren't filled in...";
			
			setErrorMessage(errorMessage);
			return false;
		} 
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
}
