/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * An account keeps properties (such as: username, password, url) in 
 * key-value pairs. 
 * 
 * @author jonalv
 *
 */
class Account implements Serializable {
    private static final long serialVersionUID = 8771912824726033314L;
    private String accountId;
    private String username;
    private String key;
    private HashMap<String, String> properties;
    private AccountType accountType;
    /**
     * Constructor for creating a new Account
     * 
     * @param accountId the account id
     * @param username the username of the account
     * @param key the password of the account
     * @param properties other properties of the account
     */
    Account( String accountId, 
             HashMap<String, String> properties, 
             AccountType accountType) {
        this.accountId   = accountId;
        this.username    = username;
        this.key         = key;
        this.properties  = new HashMap<String, String>( properties );
        this.accountType = accountType;
        //TODO Add check that the properties correspond to those defined for the AccountType 
    }
    /**
     * Contructor for creating a copy of an Account
     * 
     * @param account the Account to be copied
     */
    Account(Account account) {
        this.properties  = new HashMap<String, String>( account.properties );
        this.accountId   = account.accountId;
        this.key         = account.key;
        this.username    = account.username;
        this.accountType = new AccountType(account.accountType);
    }
    String getPropertyValue(String property) {
        return properties.get(property);
    }
    Collection<String> getProperties() {
        return properties.values();
    }
    Map<String, String> getPropertiesHashMap() {
        return properties;
    }
    protected String getAccountId() {
        return accountId;
    }
    protected String getKey() {
        return key;
    }
    protected String getUsername() {
        return username;
    }
    public String toString() {
        return this.accountId;
    }
    /**
     * @return the account type
     */
    public AccountType getAccountType() {
        return new AccountType(accountType);
    }
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result 
                 + ((accountId == null) ? 0 : accountId.hashCode());
        result = PRIME * result 
                 + ((accountType == null) ? 0 : accountType.hashCode());
        result = PRIME * result 
                 + ((key == null) ? 0 : key.hashCode());
        result = PRIME * result 
                 + ((properties == null) ? 0 : properties.hashCode());
        result = PRIME * result 
                 + ((username == null) ? 0 : username.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Account other = (Account) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        if (accountType == null) {
            if (other.accountType != null)
                return false;
        } else if (!accountType.equals(other.accountType))
            return false;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
}
