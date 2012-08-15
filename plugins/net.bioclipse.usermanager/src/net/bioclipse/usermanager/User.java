/* *****************************************************************************
 * Copyright (c) 2007-2009 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.usermanager;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The local user with username and password for 
 * reaching the info stored in accounts.
 * 
 * @author jonalv
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1424921617301316765L;
    
    private HashMap<String, Account> accounts;
    private HashMap<String, Boolean> loggedInAccounts = new HashMap<String, Boolean>();
    private String encryptedKey;
    private String userName;
    
    /**
     * Constructor for creating User without accounts
     * 
     * @param userName the User's username
     * @param encryptedKey the User's encrypted password
     */
    User(String userName, String encryptedKey) {
        
        this.encryptedKey = encryptedKey;
        this.userName = userName;
        accounts = new HashMap<String, Account>();
//        loggedInAccounts = new HashMap<String, Boolean>();
    }

    /**
     * Constructor that creates a copy of a given User
     * 
     * @param user the User to copy
     */
    User(User user) {
        
        this.userName = user.userName;
        this.encryptedKey = user.encryptedKey;
//        this.loggedInAccounts = new HashMap<String, Boolean>();//user.getLoggedInAccounts();
        this.accounts = new HashMap<String, Account>();
        for( String accountId : user.accounts.keySet() ) {
            this.accounts.put( accountId, 
                               new Account(user.accounts.get(accountId)) );
        }
    }

    String getEncryptedPassWord() {
        return encryptedKey;
    }

    String getUserName() {
        return userName;
    }

    HashMap<String, Account> getAccounts() {
        return accounts;
    }

    void addAccount(Account account) {
        accounts.put( account.getAccountId(), account );
    }
    
    public String toString() {
        return this.userName;
    }

    void clearAccounts() {
        
        this.accounts = new HashMap<String, Account>();
    }

    void setEncryptedPassWord(String encryptedPassword) {

        this.encryptedKey = encryptedPassword;
    }
    
    /**
     * Add an account that the user as tried to login to.
     * 
     * @param name The name of the account
     * @param logInSucceeded True if the user is logged in
     */
    public void addLoggedInAccount(String name, boolean logInSucceeded) {
        loggedInAccounts.put( name, logInSucceeded );
    }
    
    /**
     * Get the hash map with the information about the which account the user is
     * logged in to.
     * 
     * @return The accounts the user tried to log in to
     */
    public HashMap<String, Boolean> getLoggedInAccounts() {
        return loggedInAccounts;
    }
    
    /**
     * Clears the hash map with information about the account that the user has
     * tried to login to. To be used when the user are logging out.
     */
    public void clearLoggedInAccounts() {
        loggedInAccounts.clear();
    }
}
