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
public class UserManagerTest {


	private final String NOTREQUIREDPROPERTYKEY  = "not required property"; 
	private final String SUPERUSER               = "superuser";
	private final String MASTERKEY               = "masterKey";
	private final String USERNAME                = "username";
	private final String KEY                     = "key";
	private final String REQUIREDPROPERTYKEY     = "url";
	private final String PROPERTYVALUE           = "theweb";
	private final String ACCOUNTID               = "accountId";
	private final AccountType ACCOUNTTYPE        = new AccountType("testAccountType");
	private final AccountType ACCOUNTTYPE2       = new AccountType("testAccountType2");
	
	private final HashMap<String, String> properties = new HashMap<String, String>();
	
	/*
	 * Remove the data file so that we get an empty keyring to start with
	 */
	static {
		File file = new File("keyRing.dat");
		file.delete();
	}
	
	public UserManagerTest() {
		properties.put( REQUIREDPROPERTYKEY, PROPERTYVALUE );
	}
	
	/*
	 * SETUP
	 */
	@Before
	public void addMockAccountType() {
		
		ACCOUNTTYPE.addProperty(  REQUIREDPROPERTYKEY,    true  );
		ACCOUNTTYPE.addProperty(  NOTREQUIREDPROPERTYKEY, false );
		ACCOUNTTYPE2.addProperty( REQUIREDPROPERTYKEY,    true  );
		ACCOUNTTYPE2.addProperty( NOTREQUIREDPROPERTYKEY, false );
		UserContainer.getInstance().availableAccountTypes.add(ACCOUNTTYPE);
		UserContainer.getInstance().availableAccountTypes.add(ACCOUNTTYPE2);
	}
	
	/*
	 * Tests
	 */
	@Test
	public void testCreateMasterKeyAndLogin() {
		
		assertFalse( "should not be logged in", UserContainer.getInstance().isLoggedIn() );
		
		createMasterKey();
		login();
		
		assertTrue( "should be logged in now",  UserContainer.getInstance().isLoggedIn() );
		assertEquals( SUPERUSER + " should be logged in", 
				      SUPERUSER, UserContainer.getInstance().getLoggedInUserName() );
		logout();
	}
	
	@Test
	public void testCreateAccount() {
		
		createMasterKey();
		login();
		createAccount();

		assertTrue( UserContainer.getInstance().accountExists(ACCOUNTID) );
		logout();
	}
		
	@Test
	public void testGetAccountProperties() {
		
		createMasterKey();
		login();
		createAccount();
		
		assertEquals( KEY,           UserContainer.getInstance().getPassword(ACCOUNTID)                      );
		assertEquals( USERNAME,      UserContainer.getInstance().getUserName(ACCOUNTID)                      );
		assertEquals( PROPERTYVALUE, UserContainer.getInstance().getProperty(ACCOUNTID, REQUIREDPROPERTYKEY) );
		logout();
	}
	
	@Test
	public void testGettingInfoWhenLoggedOut() {
				
		assertFalse( UserContainer.getInstance().isLoggedIn() );
		
		try {
			UserContainer.getInstance().getPassword(ACCOUNTID);
			fail("should have thrown IllegalstateException (not logged in)");
		}
		catch(IllegalStateException e) {
			//this is what we want
		}
		try {
			UserContainer.getInstance().getUserName(ACCOUNTID);
			fail("should have thrown IllegalstateException (not logged in)");
		}
		catch(IllegalStateException e) {
			//this is what we want
		}
		try {
			UserContainer.getInstance().getProperty(ACCOUNTID, REQUIREDPROPERTYKEY);
			fail("should have thrown IllegalstateException (not logged in)");
		}
		catch(IllegalStateException e) {
			//this is what we want
		}
		try {
			UserContainer.getInstance().accountExists(ACCOUNTID);
			fail("should have thrown IllegalstateException  (not logged in)");
		}
		catch(IllegalStateException e) {
			//this is what we want
		}
		try {
			UserContainer.getInstance().getLoggedInUserName();
			fail("should have thrown IllegalstateException  (not logged in)");
		}
		catch(IllegalStateException e) {
			//this is what we want
		}
	}
	
	@Test
	public void testIllegalLogins() {
		
		try {
			UserContainer.getInstance().signIn( SUPERUSER, "wrong password");
			fail("Should have thrown Illegalstateexception (Unrecognized username or password)");
		}
		catch(IllegalArgumentException e) {
			//this is what we want
		}
		
		try {
			UserContainer.getInstance().signIn( "wrong user", MASTERKEY);
			fail("Should have thrown Illegalstateexception (Unrecognized username or password)");
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
			UserContainer.getInstance().getPassword("unknown accountid");
			fail("should have thrown IllegalArgumentException (unknown accountid)");
		}
		catch(IllegalArgumentException e) {
			//this is what we want
		}
		try {
			UserContainer.getInstance().getUserName("unknown accountid");
			fail("should have thrown IllegalArgumentException (unknown accountid)");
		}
		catch(IllegalArgumentException e) {
			//this is what we want
		}
		try {
			UserContainer.getInstance().getProperty("unknown accountid", "url");
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
		
		UserContainer.getInstance().persist();
		UserContainer.getInstance().reloadFromFile();
		testGetAccountProperties();

		login();
		String accountId = "NotToBeSavedAccount";
		UserContainer.getInstance().createAccount( accountId, "name", "key", properties, ACCOUNTTYPE2 );
		UserContainer.getInstance().reloadFromFile();
		login();
		assertFalse("the acccount should not exist", UserContainer.getInstance().accountExists(accountId));
		logout();
	}
	
	@Test
	public void testUsingSandBoxInstance() {
		
		createMasterKey();
		login();
		createAccount();
		
		UserContainer sandBox = UserContainer.getSandBoxInstance();
		String prefix = "sandBox-";
		sandBox.createAccount( prefix + ACCOUNTID, 
				               prefix + USERNAME, 
				               prefix + KEY, properties, 
				               ACCOUNTTYPE2 );
		
		assertFalse( UserContainer.getInstance().accountExists(prefix + ACCOUNTID) );
		UserContainer.replaceWithSandBoxInstance(sandBox);
		assertTrue( UserContainer.getInstance().accountExists(prefix + ACCOUNTID) );
		UserContainer.getInstance().reloadFromFile();
		assertTrue( UserContainer.getInstance().accountExists(prefix + ACCOUNTID) );
		
		assertEquals( UserContainer.getInstance().getAccountType(ACCOUNTID), 
				      sandBox.getAccountType(ACCOUNTID) );
		logout();
	}
	
	@Test
	public void testDeleteUser() {
		
		createMasterKey();
		assertTrue( UserContainer.getInstance().getUserNames().contains(SUPERUSER) );
		
		UserContainer.getInstance().deleteUser(SUPERUSER);
		
		assertFalse( UserContainer.getInstance().getUserNames().contains(SUPERUSER) );
	}
	
	@Test
	public void testClearAccounts() {
		
		createMasterKey();
		login();
		createAccount();
		
		assertTrue( UserContainer.getInstance().accountExists(ACCOUNTID) );
		UserContainer.getInstance().clearAccounts();
		assertFalse( UserContainer.getInstance().accountExists(ACCOUNTID) );
		
		logout();
	}
	
	@Test
	public void textChangeMasterKey() {
		
		createMasterKey();
		login();
		createAccount();
		
		final String NEWKEY= "newkey";
		try {
			UserContainer.getInstance().changePassword( "wrong key", NEWKEY );
			fail("should not get to change masterKey without giving a correct old key");
		}
		catch( IllegalArgumentException e ) {
			//this is what we want
		}
		
		UserContainer.getInstance().changePassword( MASTERKEY, NEWKEY );
		
		logout();
		try {
			login();
			fail("Shuold not get to log in with old password");
		}
		catch(IllegalArgumentException e) {
			//this is what we want
		}
		UserContainer.getInstance().signIn( SUPERUSER, NEWKEY );
		//Test reading an encrypted field
		assertEquals( USERNAME, UserContainer.getInstance().getUserName(ACCOUNTID) );
	}
	
	@Test
	public void testThatOnlyAccountsWithAvailableAccountTypesAreShown() {
		
		createMasterKey();
		login();
		createAccount();
		
		final String testAccountId = "testAccountId";
		AccountType testAccountType = new AccountType("unavailableAcccount");
        testAccountType.addProperty( REQUIREDPROPERTYKEY, true );
		
		try {
			UserContainer.getInstance().createAccount( "other" + testAccountId,
                                                     USERNAME,
                                                     KEY,
                                                     properties,
                                                     ACCOUNTTYPE2 );
		}
		catch (IllegalStateException e) {
			fail("should not throw exception upon creating account with unavailable accountType");
		}
		finally {
			assertTrue(
                    "should create account with unavailable accountType",
                    UserContainer.getInstance()
                    	.loggedInUser.getAccounts().containsKey("other" + testAccountId) );
		}
		
		UserContainer.getInstance().availableAccountTypes.add(testAccountType);
		
		UserContainer.getInstance().createAccount( testAccountId,
                                                 USERNAME,
                                                 KEY,
                                                 properties,
                                                 testAccountType );
		
		assertTrue( UserContainer.getInstance().accountExists(testAccountId) );
		
		UserContainer.getInstance().availableAccountTypes.remove(testAccountType);
		assertFalse( UserContainer.getInstance().accountExists(testAccountId) );
		
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
			UserContainer.getInstance().createAccount( ACCOUNTID, USERNAME, KEY, properties, ACCOUNTTYPE );
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
			UserContainer.getInstance().createAccount( ACCOUNTID, USERNAME, KEY, properties, ACCOUNTTYPE );
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
            UserContainer.getInstance().createAccount( ACCOUNTID, USERNAME, KEY, properties, ACCOUNTTYPE );
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
		UserContainer.getInstance().createAccount( testString + ACCOUNTID, 
				                                 testString + USERNAME, 
				                                 testString + KEY, properties, 
				                                 ACCOUNTTYPE2 );
		
		assertEquals( ACCOUNTTYPE, UserContainer.getInstance().getAccountType(ACCOUNTID) );
		
		UserContainer.getInstance().persist();
		UserContainer.getInstance().reloadFromFile();

		assertEquals( ACCOUNTTYPE, UserContainer.getInstance().getAccountType(ACCOUNTID) );
		
		logout();
	}
	
	@Test
	public void testCreatingMoreThanOneAccountOfSameAccountType() {
		
		createMasterKey();
		login();
		createAccount();
		
		int before = UserContainer.getInstance().getLoggedInUsersAccountNames().size();
		try {
			createAccount();
			fail("Should not get to create two accounts with the same account id");
		}
		catch( IllegalArgumentException e ) {
			//this is what we want
		}
		try {
			createAccount("unused accountid");
		}
		catch( IllegalArgumentException e) {
			fail("Should get to create a new account of a used accounttype but with a new id");
		}
		assertEquals( before+1, UserContainer.getInstance().getLoggedInUsersAccountNames().size() );
	}
	
	@Test
	public void testByAccountTypeGetters() {
		
		createMasterKey();
		login();
		createAccount();
		
//		fail("This needs to be done differently since more that one account od each type should be allowed");
		
		try {
			UserContainer.getInstance().getPropertyByAccountType("wrong account type", "property");
			fail("There is no such account type");
		}
		catch( IllegalArgumentException e) {
			//this is what we want
		}
		
		assertEquals( PROPERTYVALUE, UserContainer.getInstance().getPropertyByAccountType( ACCOUNTTYPE.getName(), 
				                                                                     this.REQUIREDPROPERTYKEY) );
		
		try {
			UserContainer.getInstance().getUserNameByAccountType("wrong account type");
			fail("There is no such account type");
		}
		catch( IllegalArgumentException e) {
			//expected exception
		}
		
		assertEquals( USERNAME, UserContainer.getInstance().getUserNameByAccountType( ACCOUNTTYPE.getName() ) );
		
		createAccount("anotherAccountid");
		
		try {
			UserContainer.getInstance().getUserNameByAccountType( ACCOUNTTYPE.getName() );
			fail("Should throw Illlegalstateexception: there are many accounts with that account type");
		}
		catch( IllegalStateException e) {
			//expected exception
		}
	}
	
	@Test
	public void testAccountIdsByAccountTypeName() {
		assertTrue( UserContainer.getInstance().getAccountIdsByAccountTypeName(ACCOUNTTYPE.getName()).contains(ACCOUNTID) );
	}
	
	/*
	 * Helper methods
	 */
	private void logout() {
	
		UserContainer.getInstance().signOut();
	}
	
	private void login() {

		UserContainer.getInstance().signIn( SUPERUSER, MASTERKEY );
	}

	private void createMasterKey() {
		
		UserContainer.getInstance().createLocalUser( SUPERUSER, MASTERKEY );
	}
	
	private void createAccount() {
		
		UserContainer.getInstance().createAccount( ACCOUNTID, USERNAME, KEY, properties, ACCOUNTTYPE );
	}
	
	private void createAccount(String accountId) {
		UserContainer.getInstance().createAccount( accountId, USERNAME, KEY, properties, ACCOUNTTYPE );
	}
}
