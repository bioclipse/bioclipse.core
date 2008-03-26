package net.bioclipse.usermanager.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import net.bioclipse.usermanager.AccountType;
import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.UserContainer;

import org.junit.Test;


public class UserManagerTest {

	private IUserManager userManager = new UserManager("filename");
	
	private final String      USER        = "superuser";
	private final String      MASTERKEY   = "masterKey";
	private final String      ACCOUNTID   = "accountId";
	private final AccountType ACCOUNTTYPE = new AccountType(
			                                	"testAccountType" );
	private final AccountType ACCOUNTTYPE2 = new AccountType(
			                                    "testAccountType2" );
	private final HashMap<String, String> properties 
		= new HashMap<String, String>();
	
	@Test
	public void testUsingSandBoxInstance() {
		
		createMasterKey();
		login();
		createAccount();

		UserContainer userContainer = new UserContainer("filename");
		String prefix = "sandBox-";
		userContainer.createUser( USER, MASTERKEY       );
		userContainer.signIn(     USER, MASTERKEY, null );
		userContainer.createAccount( prefix + ACCOUNTID, properties, 
				                     ACCOUNTTYPE2 );
		
		assertFalse( userManager.accountExists(prefix + ACCOUNTID) );
		userManager.switchUserContainer(userContainer);
		assertTrue( userManager.accountExists(prefix + ACCOUNTID) );
		userManager.reloadFromFile();
		assertTrue( userManager.accountExists(prefix + ACCOUNTID) );
		
		assertEquals( userContainer.getAccountType(ACCOUNTID), 
				      userManager.getAccountType(ACCOUNTID) );
		logout();
	}

	/*
	 * Helper methods
	 */
	private void logout() {
	
		userManager.signOut();
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
