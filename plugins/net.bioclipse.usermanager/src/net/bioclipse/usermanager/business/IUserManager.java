package net.bioclipse.usermanager.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.SubProgressMonitor;

import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.usermanager.AccountType;
import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.User;

public interface IUserManager extends IBioclipseManager {

	/**
	 * Signs in a user.
	 * 
	 * @param username the username of the user to be signed in
	 * @param password the users password
	 * @throws IllegalArgumentException if signIn not succesfull
	 */
	public void signIn( String username, String password );

	/**
	 * Signs in a user while updating a monitor
	 * 
	 * @param username the username of the user to be signed in
	 * @param password the users password
	 * @param monitor a progressmonitor
	 * @throws IllegalArgumentException if signIn not succesfull
	 */
	public void signInWithProgressBar( String username, 
			                           String password,
			                           SubProgressMonitor monitor );

	/**
	 * @return whether any user is logged in
	 */
	public boolean isLoggedIn();

	/**
	 *  Signs out the current user
	 */
	public void signOut();

	/**
	 * Creates a new user which can have many accounts.
	 * 
	 * @param userName the username of the new superuser
	 * @param key the password for the superuser
	 */
	public void createUser( String userName, String key );

	/**
	 * @return the name of the user currently logged in
	 */
	public String getLoggedInUserName();

	/**
	 * Creates a new account.
	 * 
	 * @param accountid the id for the new account
	 * @param properties any other properties of the new account 
	 *                   to be persisted 
	 * @param accountType the type of the account
	 */
	public void createAccount( String accountId, 
			                   HashMap<String, String> properties, 
			                   AccountType accountType );

	/**
	 * @param accountId
	 * @return whether an account with the given accountId exists
	 */
	public boolean accountExists( String accountId );

	/**
	 * Returns the value of a property identified by the given property key 
	 * for a given account.
	 * 
	 * @param propertykey identifies the property
	 * @param acccountId  identifies an account 
	 * 
	 * @return the value of a property
	 */
	public String getProperty( String accountId, String propertyKey );

	/**
	 * Writes all data to file
	 */
	public void persist();

	/**
	 * Reloads all data in the UserContainer from file
	 */
	public void reloadFromFile();

	/**
	 * @return the names of all users
	 */
	public List<String> getUserNames();

	/**
	 * @return the <code>User</code> currently logged in
	 */
	public User getLoggedInUser();

	/**
	 * @param name of user to be deleted
	 */
	public void deleteUser( String user );

	/**
	 * Returns all the keys for a given accounts properties
	 * 
	 * @param accountId
	 * @return
	 */
	public Collection<String> getPropertyKeys( String accountId );

	/**
	 * Removes all accounts for the currently logged in <code>User</code>
	 */
	public void clearAccounts();

	/**
	 * Changes the password for a user.
	 * 
	 * @param masterkey old password 
	 * @param newkey new password
	 */
	public void changePassword( String oldkey, String newkey );

	/**
	 * @return the names of the currently logged in users accounts
	 */
	public Collection<String> getLoggedInUsersAccountNames();

	/**
	 * @return the names of the available account types
	 */
	public String[] getAvailableAccountTypeNames();

	/**
	 * @return the available account types 
	 */
	public AccountType[] getAvailableAccountTypes();

	/**
	 * Returns the account type for the account corresponding to a given 
	 * account id 
	 * 
	 * @param accountId
	 * @return the accounts account type
	 */
	public AccountType getAccountType(String accountId);

	/**
	 * @param listener to be added
	 */
	public void removeListener(IUserManagerListener listener);

	/**
	 * @param listener to be removed
	 */
	public void addListener(IUserManagerListener listener);

	/**
	 * Returns true if there is user logged in that have an account of a type
	 * with the given account type name
	 * 
	 * @param accountTypeName
	 * @return
	 */
	public boolean isLoggedInWithAccountType(String accountTypeName);

	/**
	 * Returns the IDs for all accounts of a named account type for the 
	 * logged in user 
	 * 
	 * @param accountTypeName
	 * @return
	 */
	public List<String> getAccountIdsByAccountTypeName(String accountTypeName);
}
