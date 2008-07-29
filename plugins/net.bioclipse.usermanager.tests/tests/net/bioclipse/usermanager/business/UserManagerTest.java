/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.usermanager.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import net.bioclipse.usermanager.AccountType;
import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.UserContainer;
import net.bioclipse.usermanager.UserContainerModifier;

import org.junit.Before;
import org.junit.Test;


public class UserManagerTest {

    private IUserManager userManager = new UserManager("filename");
    
    private final String      USER                    = "superuser";
    private final String      NOTREQUIREDPROPERTYKEY  = "not required property"; 
    private final String      MASTERKEY               = "masterKey";
    private final String      ACCOUNTID               = "accountId";
    private final String      USERNAME                = "username";
    private final String      KEY                     = "key";
    private final String      REQUIREDPROPERTYKEY     = "url";
    private final String      PROPERTYVALUE           = "theweb";
    
    private final AccountType ACCOUNTTYPE = new AccountType(
                                                "testAccountType" );
    private final AccountType ACCOUNTTYPE2 = new AccountType(
                                                "testAccountType2" );
    private final HashMap<String, String> properties 
        = new HashMap<String, String>();

    public UserManagerTest() {
        properties.put( REQUIREDPROPERTYKEY, PROPERTYVALUE );
        properties.put("username", USERNAME);
        properties.put("password", KEY);
    }
    
    /*
     * SETUP
     */
    @Before
    public void addMockAccountType() {
        ACCOUNTTYPE.addProperty(  REQUIREDPROPERTYKEY,    true  );
        ACCOUNTTYPE.addProperty(  NOTREQUIREDPROPERTYKEY, false );
        ACCOUNTTYPE.addProperty( "username",              true  );
        ACCOUNTTYPE.addProperty( "password",              true  );
        ACCOUNTTYPE2.addProperty( "username",             true  );
        ACCOUNTTYPE2.addProperty( "password",             true  );
        ACCOUNTTYPE2.addProperty( REQUIREDPROPERTYKEY,    true  );
        ACCOUNTTYPE2.addProperty( NOTREQUIREDPROPERTYKEY, false );
        
        UserContainerModifier.addAccountType( 
                ((UserManager)userManager).userContainer, 
                ACCOUNTTYPE  );
        UserContainerModifier.addAccountType( 
                ((UserManager)userManager).userContainer, 
                ACCOUNTTYPE2  );
    }
    
    @Test
    public void testUsingSandBoxInstance() {
        
        createMasterKey();
        login();
        createAccount();

        UserContainer userContainer = new UserContainer("filename");
        String prefix = "sandBox-";
        UserContainerModifier.addAccountType( 
                userContainer, 
                ACCOUNTTYPE2  );
        userContainer.createUser( USER, MASTERKEY       );
        userContainer.signIn(     USER, MASTERKEY, null );
        userContainer.createAccount( prefix + ACCOUNTID, properties, 
                                     ACCOUNTTYPE2 );
        assertTrue( userContainer.accountExists(prefix + ACCOUNTID) );
        assertFalse( userManager.accountExists(prefix + ACCOUNTID) );
        userManager.switchUserContainer(userContainer);
        assertTrue( userManager.accountExists(prefix + ACCOUNTID) );
        userManager.persist();
        userManager.reloadFromFile();
        assertTrue( userManager.accountExists(prefix + ACCOUNTID) );
        
        assertEquals( 1, userContainer.getLoggedInUsersAccountNames().size() );
        assertEquals( userContainer.getAccountType(prefix + ACCOUNTID), 
                      userManager.getAccountType(prefix + ACCOUNTID) );
        logout();
    }

    /*
     * Helper methods
     */
    private void logout() {
    
        userManager.logOut();
    }
    
    private void createAccount() {
        userManager.createAccount( ACCOUNTID, properties, ACCOUNTTYPE );
    }

    private void login() {
        userManager.signInWithProgressBar( USER, MASTERKEY, null );
    }

    private void createMasterKey() {
        userManager.createUser( USER, MASTERKEY );
    }
}
