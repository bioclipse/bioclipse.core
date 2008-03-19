package net.bioclipse.usermanager.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.bioclipse.usermanager.UserContainer;

import org.junit.Test;


public class UserManagerTest {

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
		
		assertFalse( userContainer.accountExists(prefix + ACCOUNTID) );
		UserContainer.replaceWithSandBoxInstance(sandBox);
		assertTrue( userContainer.accountExists(prefix + ACCOUNTID) );
		userContainer.reloadFromFile();
		assertTrue( userContainer.accountExists(prefix + ACCOUNTID) );
		
		assertEquals( userContainer.getAccountType(ACCOUNTID), 
				      sandBox.getAccountType(ACCOUNTID) );
		logout();
	}
}
