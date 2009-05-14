/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.usermanager.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.SubProgressMonitor;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.usermanager.AccountType;
import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.User;
import net.bioclipse.usermanager.UserContainer;

@TestClasses( "net.bioclipse.usermanager.business.CoverageTest," +
		      "net.bioclipse.usermanager.business.UserManagerTest," + 
		      "net.bioclipse.usermanager.UserContainerTest" )
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
    @TestMethods("testCreateMasterKeyAndLogin")
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
    public void signInWithProgressBar( String username,
                                       String password,
                                       SubProgressMonitor monitor );

    /**
     * @return whether any user is logged in
     */
    @TestMethods("testCreateMasterKeyAndLogin")
    @PublishedMethod (methodSummary = "Returns whether a user is logged in")
    public boolean isLoggedIn();

    /**
     *  Signs out the current user
     */
    @TestMethods("textChangeMasterKey")
    @Recorded
    @PublishedMethod (methodSummary = "Logs out the currently logged in user")
    public void logOut();

    /**
     * Creates a new user which can have many accounts.
     *
     * @param userName the username of the new superuser
     * @param key the password for the superuser
     */
    @TestMethods("testCreateAccount")
    @Recorded
    @PublishedMethod (params="String username, String password",
                      methodSummary = "Creates a new user with the " +
                                      "given username and password")
    public void createUser( String userName, String key );

    /**
     * @return the name of the user currently logged in
     */
    @TestMethods("testGettingInfoWhenLoggedOut,testCreateMasterKeyAndLogin")
    @PublishedMethod (methodSummary = "Gives the name of the currently " +
                                      "logged in user")
    public String getLoggedInUserName();

    /**
     * Creates a new account.
     *
     * @param accountid the id for the new account
     * @param properties any other properties of the new account
     *                   to be persisted
     * @param accountType the type of the account
     */
    @TestMethods("testCreateAccount")
    @Recorded
    @PublishedMethod (params = "String accountID, " +
                               "HashMap<String, String> properties, " +
                               "AccountType accountType",
                      methodSummary = "Creates a new account of the given " +
                                      "accountType and with the given " +
                                      "properties" )
    public void createAccount( String accountId,
                               HashMap<String, String> properties,
                               AccountType accountType );

    /**
     * @param accountId
     * @return whether an account with the given accountId exists
     */
    @TestMethods("testCreateAccount")
    @Recorded
    @PublishedMethod (methodSummary = "Wether an account with a given " +
                                      "account id exists", 
                      params = "String accountId")
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
    @TestMethods("testGetAccountProperties")
    @Recorded
    @PublishedMethod (params = "String accountId, String property",
                      methodSummary = "Gives the value of the given property " +
                                      "for the account with the given " +
                                      "account id")
    public String getProperty( String accountId, String propertyKey );

    /**
     * Writes all data to file
     */
    @TestMethods("testPersistAndLoadInfo")
    @Recorded
    @PublishedMethod (methodSummary = "Write all data to file")
    public void persist();

    /**
     * Reloads all data in the UserContainer from file
     */
    @TestMethods("testPersistAndLoadInfo")
    @Recorded
    @PublishedMethod (methodSummary = "Reloads all data from file " +
                                      "discarding changes")
    public void reloadFromFile();

    /**
     * @return the names of all users
     */
    @TestMethods("testDeleteUser")
    @Recorded
    @PublishedMethod (methodSummary = "Gives a list of all user names")
    public List<String> getUserNames();

    /**
     * @return the <code>User</code> currently logged in
     */
    @TestMethods("")
    @Recorded
    @PublishedMethod (methodSummary = "gives the logged in user")
    public User getLoggedInUser();

    /**
     * @param name of user to be deleted
     */
    @TestMethods("testDeleteUser")
    @Recorded
    @PublishedMethod (params = "String username",
                      methodSummary = "deletes the user with " +
                                      "the given username")
    public void deleteUser( String user );

    /**
     * Returns all the keys for a given accounts properties
     *
     * @param accountId
     * @return
     */
    @TestMethods("")
    @Recorded
    @PublishedMethod (params = "String accountId",
                      methodSummary = "gives the names of the properties for" +
                                      " an account with a given account id ")
    public Collection<String> getPropertyKeys( String accountId );

    /**
     * Removes all accounts for the currently logged in <code>User</code>
     */
    @TestMethods("testClearAccounts")
    @Recorded
    @PublishedMethod (methodSummary = "Removes all accounts for the " +
                                      "currently logged in user")
    public void clearAccounts();

    /**
     * Changes the password for a user.
     *
     * @param masterkey old password
     * @param newkey new password
     */
    @TestMethods("textChangeMasterKey")
    @PublishedMethod (params = "String oldPassword, String newPassword",
                      methodSummary = "Changes the password for " +
                                      "the logged in user if the oldPassword " +
                                      "is given correctly")
    public void changePassword( String oldkey, String newkey );

    /**
     * @return the names of the currently logged in users accounts
     */
    @TestMethods("testCloningAndPersisting")
    @Recorded
    @PublishedMethod (methodSummary = "Gives the account names of the " +
                                      "logged in user ")
    public Collection<String> getLoggedInUsersAccountNames();

    /**
     * @return the names of the available account types
     */
    @TestMethods("")
    @Recorded
    @PublishedMethod (methodSummary = "Gives the names of all available " +
                                      "account types")
    public String[] getAvailableAccountTypeNames();

    /**
     * @return the available account types
     */
    @TestMethods("")
    @Recorded
    @PublishedMethod (methodSummary = "Gives all available account types")
    public AccountType[] getAvailableAccountTypes();

    /**
     * Returns the account type for the account corresponding to a given
     * account id
     *
     * @param accountId
     * @return the accounts account type
     */
    @TestMethods("testPersistingAccountType")
    @Recorded
    @PublishedMethod (params = "String accountID",
                      methodSummary = "Gives the accounttype for account " +
                                      "with given account id")
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
