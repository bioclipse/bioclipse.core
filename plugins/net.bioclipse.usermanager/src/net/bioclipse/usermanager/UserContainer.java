/*******************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *	Jonathan Alvarsson    
 *******************************************************************************/

package net.bioclipse.usermanager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.bioclipse.core.domain.BioObject;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * The <code>UserContainer</code> is responsible for the different 
 * accounts and their properties.
 * 
 * @author jonalv
 */
public class UserContainer extends BioObject {

 	private BasicPasswordEncryptor passwordEncryptor 
 		= new BasicPasswordEncryptor();
 	private BasicTextEncryptor textEncryptor;        
	
 	/*
	 * Package protected for testing purposes 
	 */
	User loggedInUser = null;
	
	private HashMap<String, User> superUsers;
	
	/*
	 * Package protected so we can fill it with MockObjects when testing
	 */
	List<AccountType> availableAccountTypes;
	private String dataFileName;
	
	public UserContainer( String dataFileName ) {

		availableAccountTypes = new ArrayList<AccountType>();

		this.dataFileName = dataFileName;
		
		ObjectInputStream in = null;
		try {
			 in = new ObjectInputStream( new FileInputStream(dataFileName) );
		} 
		catch (FileNotFoundException e) {
			//TODO: use logger instead
			System.out.println( "File not found: "
					            + dataFileName 
					            + ", a new will be created when needed");
		} 
		catch (IOException e) {
			System.err.println(e.getStackTrace() + "");
			throw new RuntimeException("Can't open file", e);
		}
		
		if(in == null) {
			superUsers = new HashMap<String, User>();
		}
		else {
			reloadFromFile();
		}
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		if(registry == null) {
			//TODO: use logger instead
			System.out.println( "Could not find extensionpoint." +
					            "If Bioclipse is not running this is normal. ");
			return;
		}

		IExtensionPoint extensionPoint 
			= registry.getExtensionPoint(
					"net.bioclipse.usermanager.accountType" );
		
		IExtension[] extensions = extensionPoint.getExtensions();
		
		for (IExtension extension : extensions) {
			IConfigurationElement[] configelements 
				= extension.getConfigurationElements();
			for (IConfigurationElement element : configelements) {
				AccountType accountType 
					= new AccountType(element.getAttribute("name"));
				for(IConfigurationElement subElement : element.getChildren()) {
					accountType.addProperty( subElement.getAttribute("name"), 
					                         Boolean.parseBoolean(
					                        		 subElement.getAttribute(
					                        				 "required" )) );
				}
				availableAccountTypes.add(accountType);
			}
		}
	}

	/**
	 * Signs in a user.
	 * 
	 * @param username the username of the user to be signed in
	 * @param password the users password
	 * @throws IllegalArgumentException if signIn not succesfull
	 */
	public void signIn( String username, 
			            String password, 
			            SubProgressMonitor monitor ) {
		
		boolean usingMonitor = monitor != null;
			
		try {
			User superUser = superUsers.get(username);
			
			if( superUser != null 
					&& passwordEncryptor.checkPassword( password, 
							                            superUser
							                            .getEncryptedPassWord() 
			                                           ) ) {
				
				loggedInUser = superUsers.get(username);
				textEncryptor = new BasicTextEncryptor();
				textEncryptor.setPassword(password);
				
			} else {
				throw new IllegalArgumentException( "Unrecognized username " +
						                            "or password");
			}
		}
		finally {
			if(usingMonitor) {
				monitor.done();
			}
		}
	}
	
	/**
	 * @return whether any superuser is logged in
	 */
	public boolean isLoggedIn() {
		return loggedInUser != null;
	}
	
	/**
	 *  Signs out the current superuser
	 */
	public void signOut() {
		loggedInUser  = null;
		textEncryptor = null;
	}

	/**
	 * Creates a new user which can have many accounts.
	 * 
	 * @param userName the username of the new superuser
	 * @param key the password for the superuser
	 */
	public void createUser(String userName, String key) {
		
		String encryptedKey = passwordEncryptor.encryptPassword(key);
		User user = new User(userName, encryptedKey);
		superUsers.put(userName, user);
	}

	/**
	 * @return the user currently logged in
	 */
	public String getLoggedInUserName() {

		if(isLoggedIn()) {
			return loggedInUser.getUserName();
		}
		throw new IllegalStateException("No user is logged in");
	}

	/**
	 * Creates a new account.
	 * 
	 * @param accountid the id for the new account
	 * @param properties any other properties of the new account 
	 *                   to be persisted 
	 */
	public void createAccount( String accountId, 
			                   HashMap<String, String> properties,
			                   AccountType accountType ) {
		
        for ( String name : properties.keySet() )
            if ( !accountType.hasProperty( name ) )
                throw new IllegalArgumentException( "Can not create " +
                		"an account with an unrecognized property: " + name );
        
        for(AccountType.Property property : accountType.getRequiredProperties())
            if ( !properties.containsKey( property.getName() ) ||
                 "".equals( properties.get( property.getName() )) )
                throw new IllegalArgumentException(	"Can not create an " +
                		"account without required property: " +
                        property.getName() );
        
        for ( Account account : loggedInUser.getAccounts().values() ) {
        	if( account.getAccountId().equals(accountId) ) 
        		throw new IllegalArgumentException(
        				"Already exists an account with the accountid: " 
        				+ accountId);
        }
        
		HashMap<String, String> encryptedProperties 
			= new HashMap<String, String>();
		for( String hashKey : properties.keySet() ) {
			encryptedProperties.put( hashKey, 
					                 textEncryptor.encrypt(
					                		 properties.get(hashKey) ) );
		}
		
		Account account = new Account( accountId, 
				                       encryptedProperties, 
				                       accountType );
		loggedInUser.addAccount( account );
	}

	/**
	 * @param accountId
	 * @return whether an account with the given accountId exists
	 */
	public boolean accountExists(String accountId) {
		
		if(!isLoggedIn()) {
			throw new IllegalStateException("not logged in");
		}
        
        Account account = loggedInUser.getAccounts().get(accountId);
        if ( account != null 
        	 && !accountTypeIsAvailable( account.getAccountType() ) )
            return false;
        
		return loggedInUser.getAccounts().get(accountId) != null;
	}

	/**
	 * Returns the value of a property identified by the given propertykey 
	 * for a given account.
	 * 
	 * @param propertykey identifies the property
	 * @param acccountId  identifies an account 
	 * 
	 * @return the value of a property
	 */
	public String getProperty(String accountId, String propertykey) {
		
		return textEncryptor.decrypt( getAccount(accountId)
				                      .getPropertyValue(propertykey) );
	}
	
	/**
	 * Writes all data to file
	 */
	public void persist() {
		
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream( new FileOutputStream(dataFileName) );
			out.writeObject(superUsers);
			out.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException( "Could not save UserContainer " +
					                    "properties to file", ex );
		}
	}

	/**
	 * Reloads all data in the UserContainer from file
	 */
	public void reloadFromFile() {
		
		ObjectInputStream in = null;
		
		try {
			in = new ObjectInputStream( new FileInputStream(dataFileName) );
			superUsers = (HashMap<String, User>)in.readObject();
		} catch (FileNotFoundException e) {
			throw new RuntimeException( "There is no file with persisted " +
					                    "UserContainer data", e );
		} catch (IOException e) {
			throw new RuntimeException( "Could not load persisted data " +
					                    "for the UserContainer", e );
		} catch (ClassNotFoundException e) {
			throw new RuntimeException( "Could not load persisted data " +
				                        "for the UserContainer", e);
		}
		finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns a copy of the UserContainer instance.
	 * 
	 * @return a copy of the UserContainer instance
	 */
	public UserContainer clone() {
		
		UserContainer copy = new UserContainer(dataFileName);
		
		copy.passwordEncryptor = passwordEncryptor;
		copy.textEncryptor     = textEncryptor;
		
		copy.availableAccountTypes = new ArrayList<AccountType>();
		for( AccountType accountType : availableAccountTypes ) {
			copy.availableAccountTypes.add( 
					new AccountType(accountType) );
		}
		
		copy.superUsers = new HashMap<String, User>();
		for( String userName : superUsers.keySet() ) {
			copy.superUsers.put( userName, 
					new User(superUsers.get(userName)));
		}
		
		if( loggedInUser != null ) {
			copy.loggedInUser = copy.superUsers.get( 
					getLoggedInUserName() );			
		}
		
		return copy;
	}
	
	/**
	 * @return the names of all users
	 */
	public List<String> getUserNames() {
		
		return new ArrayList<String>( superUsers.keySet() );
	}

	/**
	 * @return the <code>User</code> currently logged in
	 */
	public User getLoggedInUser() {

		if(this.loggedInUser != null) {
			return this.loggedInUser;
		}
		throw new IllegalStateException("not logged in");
	}

	/**
	 * @param superuser to be deleted
	 */
	public void deleteUser(String superuser) {

		this.superUsers.remove(superuser);
	}
	
	/**
	 * Returns all the keys for a given accounts properties
	 * 
	 * @param accountId
	 * @return
	 */
	public Collection<String> getPropertyKeys(String accountId) {
		return loggedInUser.getAccounts().get(accountId)
		       .getPropertiesHashMap().keySet(); 
	}

	/**
	 * Removes all accounts for the currently logged in <code>User</code>
	 */
	public void clearAccounts() {
		
		this.loggedInUser.clearAccounts();
	}

	/**
	 * Changes the password for a user.
	 * 
	 * @param masterkey old password 
	 * @param newkey new password
	 */
	public void changePassword(String masterkey, String newkey) {
		
		if ( passwordEncryptor.checkPassword( masterkey, 
			 loggedInUser.getEncryptedPassWord() ) ) {
			
			loggedInUser.setEncryptedPassWord( 
					passwordEncryptor.encryptPassword(newkey) );
		} else {
			throw new IllegalArgumentException("Unrecognized password");
		}
		
		BasicTextEncryptor oldTextEncryptor = textEncryptor; 
		textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(newkey);
		
		HashMap<String, Account> accounts = loggedInUser.getAccounts();
		clearAccounts();
		
		for( String accountId : accounts.keySet() ) {
			
			/*
			 * create a hashmap with unencrypted properties 
			 */
			HashMap<String, String> unEncryptedProperties 
				= new HashMap<String, String>();
			for( String propertyKey : accounts.get(accountId)
					                          .getPropertiesHashMap()
					                          .keySet() ) {
				
				String reEncryptedValue = 
					oldTextEncryptor.decrypt( 
							accounts.get( accountId)
							              .getPropertyValue(propertyKey) );
				unEncryptedProperties.put( propertyKey, reEncryptedValue );
			}
			
			createAccount( accountId, 
			               unEncryptedProperties, 
			               accounts.get(accountId).getAccountType() );
		}
	}

	/**
	 * @return the names of the currently logged in users accounts
	 */
	public Collection<String> getLoggedInUsersAccountNames() {

		if( loggedInUser == null ) {
			throw new IllegalStateException("Not logged in");
		}
		return loggedInUser.getAccounts().keySet();
	}
	
	private Account getAccount(String accountId) {
		
		if( !isLoggedIn() ) {
			throw new IllegalStateException("not logged in");
		}
		Account account = loggedInUser.getAccounts().get(accountId); 
		if( account == null) {
			throw new IllegalArgumentException( "No account with accountid:"
					                            + accountId );
		}
		return account;
	}

	/**
	 * Returns the names of the available account types
	 * 
	 * @return
	 */
	public String[] getAvailableAccountTypeNames() {

		String[] array = new String[availableAccountTypes.size()];
		for (int i = 0; i < availableAccountTypes.size(); i++) {
			array[i] = availableAccountTypes.get(i).getName();
		}
		return array;
	}

	
	/**
	 * @return the available account types 
	 */
	public AccountType[] getAvailableAccountTypes() {
		return availableAccountTypes.toArray(new AccountType[0]);
	}

    private boolean accountTypeIsAvailable( AccountType accountType ) {
        return availableAccountTypes.contains( accountType );
    }
    
    /**
     * Returns the account type for the an account corresponding to a given 
     * account id 
     * 
     * @param accountId
     * @return the accounts account type
     */
    public AccountType getAccountType(String accountId) {

		return loggedInUser.getAccounts().get(accountId).getAccountType();
	}

	/**
	 * Returns a property by name of an account of an account type 
	 * identified by name
	 * 
	 * @param accountTypeName
	 * @param propertyName
	 * @return
	 */
	public String getPropertyByAccountType( String accountTypeName, 
			                                String propertyName) {

		for ( Account a : loggedInUser.getAccounts().values() ) {
			if( accountTypeName.equals( a.getAccountType().getName() ) ) {
				return getProperty(a.getAccountId(), propertyName);
			}
		}
		throw new IllegalArgumentException("There is no such account type");
	}
	
	/**
	 * Returns true if there is user logged in that have an account of a type
	 * with the given account type name
	 * 
	 * @param accountTypeName
	 * @return
	 */
	public boolean isLoggedInWithAccountType(String accountTypeName) {
		
		if( isLoggedIn() ) {
			for( Account account : loggedInUser.getAccounts().values() ) {
				if( account.getAccountType().getName().equals(
						accountTypeName ) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<String> getAccountIdsByAccountTypeName(String string) {
		if(!isLoggedIn()) {
			throw new IllegalStateException("Not logged in");
		}
		List<String> result = new ArrayList<String>();
		for( Account a : loggedInUser.getAccounts().values() ) {
			if( a.getAccountType().getName().equals(string) ) {
				result.add(a.getAccountId());
			}
		}
		return result;
	}
}

