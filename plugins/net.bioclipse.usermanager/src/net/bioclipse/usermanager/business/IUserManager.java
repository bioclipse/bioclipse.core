package net.bioclipse.usermanager.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.SubProgressMonitor;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.usermanager.AccountType;
import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.User;
import net.bioclipse.usermanager.UserContainer;

@PublishedClass("Handles users and accounts in Bioclipse. " +
		        "Can store things like database passwords and usernames " +
		        "in an encrypted file.")
public interface IUserManager extends IBioclipseManager {

	/**
	 * Signs in a user.
	 * 
	 * @param username the username of the user to be signed in
	 * @param password the users password
	 * @throws IllegalArgumentException if signIn not succesfull
	 */
	@PublishedMethod( params = "String username, String password", 
			          methodSummary = "Logs in the user with the given "
			       	                + "username given that the given password "
			       	                + "matches the stored one." )
	@Recorded
	public void logIn( String username, String password );

	/**
	 * Signs in a user while updating a monitor
	 * 
	 * @param username the username of the user to be signed in
	 * @param password the users password
	 * @param monitor a progressmonitor
	 * @throws IllegalArgumentException if signIn not succesfull
	 */
	@Recorded
	public void signInWithProgressBar( String username, 
			                           String password,
			                           SubProgressMonitor monitor );

	/**
	 * @return whether any user is logged in
	 */
	@Recorded
	public boolean isLoggedIn();

	/**
	 *  Signs out the current user
	 */
	@Recorded
	public void signOut();

	/**
	 * Creates a new user which can have many accounts.
	 * 
	 * @param userName the username of the new superuser
	 * @param key the password for the superuser
	 */
	@Recorded
	public void createUser( String userName, String key );

	/**
	 * @return the name of the user currently logged in
	 */
	@Recorded
	public String getLoggedInUserName();

	/**
	 * Creates a new account.
	 * 
	 * @param accountid the id for the new account
	 * @param properties any other properties of the new account 
	 *                   to be persisted 
	 * @param accountType the type of the account
	 */
	@Recorded
	public void createAccount( String accountId, 
			                   HashMap<String, String> properties, 
			                   AccountType accountType );

	/**
	 * @param accountId
	 * @return whether an account with the given accountId exists
	 */
	@Recorded
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
	@Recorded
	public String getProperty( String accountId, String propertyKey );

	/**
	 * Writes all data to file
	 */
	@Recorded
	public void persist();

	/**
	 * Reloads all data in the UserContainer from file
	 */
	@Recorded
	public void reloadFromFile();

	/**
	 * @return the names of all users
	 */
	@Recorded
	public List<String> getUserNames();

	/**
	 * @return the <code>User</code> currently logged in
	 */
	@Recorded
	public User getLoggedInUser();

	/**
	 * @param name of user to be deleted
	 */
	@Recorded
	public void deleteUser( String user );

	/**
	 * Returns all the keys for a given accounts properties
	 * 
	 * @param accountId
	 * @return
	 */
	@Recorded
	public Collection<String> getPropertyKeys( String accountId );

	/**
	 * Removes all accounts for the currently logged in <code>User</code>
	 */
	@Recorded
	public void clearAccounts();

	/**
	 * Changes the password for a user.
	 * 
	 * @param masterkey old password 
	 * @param newkey new password
	 */
	@Recorded
	public void changePassword( String oldkey, String newkey );

	/**
	 * @return the names of the currently logged in users accounts
	 */
	@Recorded
	public Collection<String> getLoggedInUsersAccountNames();

	/**
	 * @return the names of the available account types
	 */
	@Recorded
	public String[] getAvailableAccountTypeNames();

	/**
	 * @return the available account types 
	 */
	@Recorded
	public AccountType[] getAvailableAccountTypes();

	/**
	 * Returns the account type for the account corresponding to a given 
	 * account id 
	 * 
	 * @param accountId
	 * @return the accounts account type
	 */
	@Recorded
	public AccountType getAccountType(String accountId);

	/**
	 * @param listener to be added
	 */
	@Recorded
	public void removeListener(IUserManagerListener listener);

	/**
	 * @param listener to be removed
	 */
	@Recorded
	public void addListener(IUserManagerListener listener);

	/**
	 * Returns true if there is user logged in that have an account of a type
	 * with the given account type name
	 * 
	 * @param accountTypeName
	 * @return
	 */
	@Recorded
	public boolean isLoggedInWithAccountType(String accountTypeName);

	/**
	 * Returns the IDs for all accounts of a named account type for the 
	 * logged in user 
	 * 
	 * @param accountTypeName
	 * @return
	 */
	@Recorded
	public List<String> getAccountIdsByAccountTypeName(String accountTypeName);

	/**
	 * @return a copy of the user container which can be edited and thrown away 
	 * or returned.
	 */
	@Recorded
	public UserContainer getSandBoxUserContainer();

	/**
	 * Use the given usercontainer instead
	 * 
	 * @param sandBoxUserContainer
	 */
	@Recorded
	public void switchUserContainer(UserContainer sandBoxUserContainer);
}
