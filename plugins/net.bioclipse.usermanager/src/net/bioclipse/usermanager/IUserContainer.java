package net.bioclipse.usermanager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.SubProgressMonitor;

public interface IUserContainer {

	/**
	 * Signs in a user.
	 * 
	 * @param username the username of the user to be signed in
	 * @param password the users password
	 * @throws IllegalArgumentException if signIn not succesfull
	 */
	public void signIn(String username, String password);

	public void signInWithProgressBar(String username, String password,
			SubProgressMonitor monitor);

	/**
	 * @return whether any superuser is logged in
	 */
	public boolean isLoggedIn();

	/**
	 *  Signs out the current superuser
	 */
	public void signOut();

	/**
	 * Creates a new superuser which can have many accounts.
	 * 
	 * @param userName the username of the new superuser
	 * @param key the password for the superuser
	 */
	public void createLocalUser(String userName, String key);

	/**
	 * @return the user currently logged in
	 */
	public String getLoggedInUserName();

	/**
	 * Creates a new account.
	 * 
	 * @param accountid the id for the new account
	 * @param username the username associated with the new account
	 * @param key the password associated with the new account
	 * @param properties any other properties of the new account 
	 *                   to be persisted 
	 */
	public void createAccount(String accountId, String username, String key,
			HashMap<String, String> properties, AccountType accountType);

	/**
	 * @param accountId
	 * @return whether an account with the given accountId exists
	 */
	public boolean accountExists(String accountId);

	/**
	 * Returns a decrypted key for the account identified by the accountId.
	 * 
	 * @param accountid
	 * @return The password associated with the account corresponding 
	 *         to the given accountId
	 */
	public String getPassword(String accountId);

	/**
	 * Returns the username for the account identified by the accountId.
	 * 
	 * @param accountid
	 * @return The username associated with the account corresponding 
	 *         to the given accountId
	 */
	public String getUserName(String accountId);

	/**
	 * Returns the value of a property identified by the given propertykey 
	 * for a given account.
	 * 
	 * @param propertykey identifies the property
	 * @param acccountId  identifies an account 
	 * 
	 * @return the value of a property
	 */
	public String getProperty(String accountId, String propertykey);

	/**
	 * Writes all data to file
	 */
	public void persist();

	/**
	 * Reloads all data in the UserContainer from file
	 */
	public void reloadFromFile();

	/**
	 * @return the names of all Keyring users
	 */
	public List<String> getUserNames();

	/**
	 * @return the <code>User</code> currently logged in
	 */
	public User getLoggedInUser();

	/**
	 * @param superuser to be deleted
	 */
	public void deleteUser(String superuser);

	/**
	 * Returns all the keys for a given accounts properties
	 * 
	 * @param accountId
	 * @return
	 */
	public Collection<String> getPropertyKeys(String accountId);

	/**
	 * Removes all accounts for the currently logged in <code>User</code>
	 */
	public void clearAccounts();

	/**
	 * Changes the password for a Keyring user.
	 * 
	 * @param masterkey old password 
	 * @param newkey new password
	 */
	public void changePassword(String masterkey, String newkey);

	/**
	 * @return the names of the currently logged in users accounts
	 */
	public Collection<String> getLoggedInUsersAccountNames();

	/**
	 * Returns the names of the available account types
	 * 
	 * @return
	 */
	public String[] getAvailableAccountTypeNames();

	/**
	 * @return the available account types 
	 */
	public AccountType[] getAvailableAccountTypes();

	/**
	 * Returns the account type for the an account corresponding to a given 
	 * account id 
	 * 
	 * @param accountId
	 * @return the accounts account type
	 */
	public AccountType getAccountType(String accountId);

	/**
	 *  Fires a login event
	 */
	public void fireLogin();

	/**
	 * Fires a logout event
	 */
	public void fireLogout();

	/**
	 * Fires an update event
	 */
	public void fireUpdate();

	/**
	 * Adds a listener for KeyringEvents
	 * 
	 * @param listener to be added
	 */
	public void removeListener(IUserManagerListener listener);

	/**
	 * Removes a listener for KeyringEvents
	 * 
	 * @param listener to be removed
	 */
	public void addListener(IUserManagerListener listener);

	/**
	 * Returns a property by name of an account of an account type 
	 * identified by name
	 * 
	 * @param accountTypeName
	 * @param propertyName
	 * @return
	 */
	public String getPropertyByAccountType(String accountTypeName,
			String propertyName);

	/**
	 * Returns the decrypted username for an account of a given account type
	 * 
	 * @param accountTypeName
	 * @return
	 */
	public String getUserNameByAccountType(String accountTypeName);

	/**
	 * Returns the decrypted key for an account of a given account type
	 * 
	 * @param accountTypeName
	 * @return
	 */
	public String getKeyByAccountType(String accountTypeName);

	/**
	 * Returns true if there is user logged in that have an account of a type
	 * with the given account type name
	 * 
	 * @param accountTypeName
	 * @return
	 */
	public boolean isLoggedInWithAccountType(String accountTypeName);

	public List<String> getAccountIdsByAccountTypeName(String string);

	public Object getParsedResource();

}