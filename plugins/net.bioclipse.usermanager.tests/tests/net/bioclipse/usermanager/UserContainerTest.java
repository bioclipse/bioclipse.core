package net.bioclipse.usermanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;

/**
 * Testclass for the UserContainer
 * 
 * @author jonalv
 *
 */
public class UserContainerTest {


	private final String NOTREQUIREDPROPERTYKEY  = "not required property"; 
	private final String SUPERUSER               = "superuser";
	private final String MASTERKEY               = "masterKey";
	private final String USERNAME                = "username";
	private final String KEY                     = "key";
	private final String REQUIREDPROPERTYKEY     = "url";
	private final String PROPERTYVALUE           = "theweb";
	private final String ACCOUNTID               = "accountId";
	private final AccountType ACCOUNTTYPE        = new AccountType(
			                                           "testAccountType" );
	private final AccountType ACCOUNTTYPE2       = new AccountType(
			                                           "testAccountType2" );
	private final HashMap<String, String> properties 
		= new HashMap<String, String>();
	
	private UserContainer userContainer = new UserContainer("userManager.dat");
	
	static {
		File file = new File("userManager.dat");
		file.delete();
	}
	
	public UserContainerTest() {
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
		userContainer.availableAccountTypes.add( ACCOUNTTYPE  );
		userContainer.availableAccountTypes.add( ACCOUNTTYPE2 );
	}
	
	/*
	 * Tests
	 */
	@Test
	public void testCreateMasterKeyAndLogin() {
		
		assertFalse( "should not be logged in", userContainer.isLoggedIn() );
		
		createMasterKey();
		login();
		
		assertTrue( "should be logged in now",  userContainer.isLoggedIn() );
		assertEquals( SUPERUSER + " should be logged in", 
				      SUPERUSER, userContainer.getLoggedInUserName() );
		logout();
	}
	
	@Test
	public void testCreateAccount() {
		
		createMasterKey();
		login();
		createAccount();

		assertTrue( userContainer.accountExists(ACCOUNTID) );
		logout();
	}
		
	@Test
	public void testGetAccountProperties() {
		
		createMasterKey();
		login();
		createAccount();
		
		assertEquals( PROPERTYVALUE, 
				      userContainer.getProperty( ACCOUNTID, 
				    		                     REQUIREDPROPERTYKEY) );
		logout();
	}
	
	@Test
	public void testGettingInfoWhenLoggedOut() {
				
		assertFalse( userContainer.isLoggedIn() );
		
		try {
			userContainer.getProperty(ACCOUNTID, REQUIREDPROPERTYKEY);
			fail("should have thrown IllegalstateException (not logged in)");
		}
		catch(IllegalStateException e) {
			//this is what we want
		}
		try {
			userContainer.accountExists(ACCOUNTID);
			fail("should have thrown IllegalstateException  (not logged in)");
		}
		catch(IllegalStateException e) {
			//this is what we want
		}
		try {
			userContainer.getLoggedInUserName();
			fail("should have thrown IllegalstateException  (not logged in)");
		}
		catch(IllegalStateException e) {
			//this is what we want
		}
	}
	
	@Test
	public void testIllegalLogins() {
		
		try {
			userContainer.signIn( SUPERUSER, "wrong password", null );
			fail( "Should have thrown Illegalstateexception " +
				  "(Unrecognized username or password)" );
		}
		catch(IllegalArgumentException e) {
			//this is what we want
		}
		
		try {
			userContainer.signIn( "wrong user", MASTERKEY, null);
			fail( "Should have thrown Illegalstateexception " +
				  "(Unrecognized username or password)" );
		}
		catch(IllegalArgumentException e) {
			//this is what we want
		}
	}
	
	@Test
	public void testGettingInfoForUnknownAccount() {
		
		createMasterKey();
		login();
		createAccount();
		
		try {
			userContainer.getProperty("unknown accountid", "url");
			fail("should have thrown IllegalArgumentException (unknown accountid)");
		}
		catch(IllegalArgumentException e) {
			//this is what we want
		}
		
		logout();
	}
	
	@Test
	public void testPersistAndLoadInfo() {
		
		createMasterKey();
		login();
		createAccount();
		logout();
		
		userContainer.persist();
		userContainer.reloadFromFile();
		testGetAccountProperties();

		login();
		String accountId = "NotToBeSavedAccount";
		userContainer.createAccount( accountId, properties, ACCOUNTTYPE2 );
		userContainer.reloadFromFile();
		login();
		assertFalse( "the acccount should not exist", 
				     userContainer.accountExists(accountId) );
		logout();
	}
	
	@Test
	public void testDeleteUser() {
		
		createMasterKey();
		assertTrue( userContainer.getUserNames().contains(SUPERUSER) );
		
		userContainer.deleteUser(SUPERUSER);
		
		assertFalse( userContainer.getUserNames().contains(SUPERUSER) );
	}
	
	@Test
	public void testClearAccounts() {
		
		createMasterKey();
		login();
		createAccount();
		
		assertTrue( userContainer.accountExists(ACCOUNTID) );
		userContainer.clearAccounts();
		assertFalse( userContainer.accountExists(ACCOUNTID) );
		
		logout();
	}
	
	@Test
	public void textChangeMasterKey() {
		
		createMasterKey();
		login();
		createAccount();
		
		final String NEWKEY= "newkey";
		try {
			userContainer.changePassword( "wrong key", NEWKEY );
			fail("should not get to change masterKey without giving a correct old key");
		}
		catch( IllegalArgumentException e ) {
			//this is what we want
		}
		
		userContainer.changePassword( MASTERKEY, NEWKEY );
		
		logout();
		try {
			login();
			fail("Shuold not get to log in with old password");
		}
		catch(IllegalArgumentException e) {
			//this is what we want
		}
		userContainer.signIn( SUPERUSER, NEWKEY, null );
		//Test reading an encrypted field
		assertEquals( USERNAME, 
				      userContainer.getProperty( ACCOUNTID, "username") );
	}
	
	@Test
	public void testThatOnlyAccountsWithAvailableAccountTypesAreShown() {
		
		createMasterKey();
		login();
		createAccount();
		
		final String testAccountId = "testAccountId";
		AccountType testAccountType = new AccountType("unavailableAcccount");
        testAccountType.addProperty( NOTREQUIREDPROPERTYKEY, false );
		
		try {
			userContainer.createAccount( "other" + testAccountId,
                                         properties,
                                         ACCOUNTTYPE2 );
		}
		catch (Exception e) {
			fail( "should not throw exception upon creating account " +
				  "with unavailable accountType" );
		}
		finally {
			assertTrue(
                    "should create account with unavailable accountType",
                    userContainer
                    .loggedInUser
                    .getAccounts().containsKey("other" + testAccountId) );
		}
		
		userContainer.availableAccountTypes.add(testAccountType);
		
		userContainer.createAccount( testAccountId,
                                     new HashMap<String, String>(),
                                     testAccountType );
		
		assertTrue( userContainer.accountExists(testAccountId) );
		
		userContainer.availableAccountTypes.remove(testAccountType);
		assertFalse( userContainer.accountExists(testAccountId) );
		
		logout();
	}
	
	@Test
	public void testCreatingAccountWithDifferentProperties() {
		
		createMasterKey();
		login();
		createAccount();
		
		/*
		 *  Property not defined for account type
		 */
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put( "wrongKey", "wrong" );
		properties.putAll(this.properties);
		try {
			userContainer.createAccount( ACCOUNTID, properties, ACCOUNTTYPE );
			fail( "Should throw exception when creating account with " + 
                  "properties not defined for AccountType" );
		}
		catch(IllegalArgumentException e) {
			// this is what we want
		}
		
		/*
		 * Missing required property
		 */
        properties = new HashMap<String, String>();
		properties.put( NOTREQUIREDPROPERTYKEY, "value" );
		try {
			userContainer.createAccount( ACCOUNTID, properties, ACCOUNTTYPE );
			fail("Should not get to create account without required property");
		}
		catch(IllegalArgumentException e) {
			// this is what we want
		}

        /*
         * Properties with empty values are also counted as missing
         */
        properties = new HashMap<String, String>();
        properties.put( REQUIREDPROPERTYKEY, "" );
        try {
            userContainer.createAccount( ACCOUNTID, properties, ACCOUNTTYPE );
            fail("Should not get to create account without required property");
        }
        catch(IllegalArgumentException e) {
            // this is what we want
        }
}
	
	@Test
	public void testPersistingAccountType() {
		
		createMasterKey();
		login();
		createAccount();
		
		final String testString = "test";
		userContainer.createAccount( testString + ACCOUNTID, 
				                     properties, 
				                     ACCOUNTTYPE2 );
		
		assertEquals( ACCOUNTTYPE, userContainer.getAccountType(ACCOUNTID) );
		
		userContainer.persist();
		userContainer.reloadFromFile();

		assertEquals( ACCOUNTTYPE, userContainer.getAccountType(ACCOUNTID) );
		
		logout();
	}
	
	@Test
	public void testCreatingMoreThanOneAccountOfSameAccountType() {
		
		createMasterKey();
		login();
		createAccount();
		
		int before = userContainer.getLoggedInUsersAccountNames().size();
		try {
			createAccount();
			fail( "Should not get to create two " +
			      "accounts with the same account id" );
		}
		catch( IllegalArgumentException e ) {
			//this is what we want
		}
		try {
			createAccount("unused accountid");
		}
		catch( IllegalArgumentException e) {
			fail( "Should get to create a new account " +
				  "of a used accounttype but with a new id" );
		}
		assertEquals( before+1, 
				      userContainer.getLoggedInUsersAccountNames().size() );
	}
	
	@Test
	public void testByAccountTypeGetters() {
		
		createMasterKey();
		login();
		createAccount();
		
//		fail("This needs to be done differently since more that one account od each type should be allowed");
		
		try {
			userContainer.getPropertyByAccountType( "wrong account type", 
					                                "property" );
			fail("There is no such account type");
		}
		catch( IllegalArgumentException e) {
			//this is what we want
		}
		
		assertEquals( PROPERTYVALUE, 
				      userContainer
				      .getPropertyByAccountType( ACCOUNTTYPE.getName(), 
				                                 this.REQUIREDPROPERTYKEY) );
		
		createAccount("anotherAccountid");
	}
	
	@Test
	public void testAccountIdsByAccountTypeName() {
		login();
		assertTrue( userContainer
				    .getAccountIdsByAccountTypeName( ACCOUNTTYPE.getName() )
				    .contains(ACCOUNTID) );
	}
	
	@Test
	public void testCloning() {
		login();
		UserContainer clone = userContainer.clone();
		assertEquals( userContainer, clone );
		assertEquals( userContainer.getLoggedInUsersAccountNames(), 
				      clone.getLoggedInUsersAccountNames() );
	}
	
	/*
	 * Helper methods
	 */
	private void logout() {
	
		userContainer.signOut();
	}
	
	private void login() {

		userContainer.signIn( SUPERUSER, MASTERKEY, null );
	}

	private void createMasterKey() {
		
		userContainer.createUser( SUPERUSER, MASTERKEY );
	}
	
	private void createAccount() {
		
		userContainer.createAccount( ACCOUNTID, properties, ACCOUNTTYPE );
	}
	
	private void createAccount(String accountId) {
		userContainer.createAccount( accountId, properties, ACCOUNTTYPE );
	}
}
