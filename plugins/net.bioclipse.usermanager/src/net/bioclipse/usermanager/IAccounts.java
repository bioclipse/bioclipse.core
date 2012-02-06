package net.bioclipse.usermanager;

import org.eclipse.swt.widgets.Composite;

public interface IAccounts {
	/**
	 * This method creates a composite with the components that are needed to create this specific 
	 * account type.
	 * 
	 * @param shell The shell that the composite shall be associated with.
	 * @return A composite containing the necessary components.
	 */
	 public Composite createComposite(Composite container); 
	 
	 /**
	  * A method used to identify the account type in e.g. a combobox.
	  * 
	  * @return E.g. the name of the account type 
	  */
	 public String getName();
	 
	 /**
	  * This is the method that creates the account. The idea is that it it 
	  * should bee hailed just before the dialog is deposed.
	  * The components that holds the information needed to create an account
	  * has to be instance variables and returns true if the account was 
	  * successfully created, else false.
	  */
	 public Boolean createAccount();
	 
	 /**
	  * This method is is to be used to set one of the components in the 
	  * plug-ins composite in focus. This component has to be an instance 
	  * variable
	  */
	 public void setFocus();
}
