package net.bioclipse.usermanager.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.bioclipse.usermanager.AccountType;
import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.User;
import net.bioclipse.usermanager.UserContainer;

import org.eclipse.core.runtime.SubProgressMonitor;

public class UserManager implements IUserManager {

	private UserContainer userContainer;
	
	public UserManager() {
		userContainer = new UserContainer("usermanager.dat");
	}
	
	@Override
	public boolean accountExists(String accountId) {
		return userContainer.accountExists(accountId);
	}

	@Override
	public void addListener(IUserManagerListener listener) {
		userContainer.addListener(listener);
	}

	@Override
	public void changePassword(String oldkey, String newkey) {
		userContainer.changePassword(oldkey, newkey);
	}

	@Override
	public void clearAccounts() {
		userContainer.clearAccounts();
	}

	@Override
	public void createAccount( String accountId,
			                   HashMap<String, String> properties, 
			                   AccountType accountType ) {
		userContainer.createAccount(accountId, properties, accountType);
	}

	@Override
	public void createUser(String userName, String key) {
		userContainer.createUser(userName, key);
	}

	@Override
	public void deleteUser(String user) {
		userContainer.deleteUser(user);
	}

	@Override
	public List<String> getAccountIdsByAccountTypeName(String accountTypeName) {
		return userContainer.getAccountIdsByAccountTypeName(accountTypeName);
	}

	@Override
	public AccountType getAccountType(String accountId) {
		return userContainer.getAccountType(accountId);
	}

	@Override
	public String[] getAvailableAccountTypeNames() {
		return userContainer.getAvailableAccountTypeNames();
	}

	@Override
	public AccountType[] getAvailableAccountTypes() {
		return userContainer.getAvailableAccountTypes();
	}

	@Override
	public User getLoggedInUser() {
		return userContainer.getLoggedInUser();
	}

	@Override
	public String getLoggedInUserName() {
		return userContainer.getLoggedInUserName();
	}

	@Override
	public Collection<String> getLoggedInUsersAccountNames() {
		return userContainer.getLoggedInUsersAccountNames();
	}

	@Override
	public String getProperty(String accountId, String propertyKey) {
		return userContainer.getProperty(accountId, propertyKey);
	}

	@Override
	public Collection<String> getPropertyKeys(String accountId) {
		return userContainer.getPropertyKeys(accountId);
	}

	@Override
	public List<String> getUserNames() {
		return userContainer.getUserNames();
	}

	@Override
	public boolean isLoggedIn() {
		return userContainer.isLoggedIn();
	}

	@Override
	public boolean isLoggedInWithAccountType(String accountTypeName) {
		return userContainer.isLoggedInWithAccountType(accountTypeName);
	}

	@Override
	public void persist() {
		userContainer.persist();
	}

	@Override
	public void reloadFromFile() {
		userContainer.reloadFromFile();
	}

	@Override
	public void removeListener(IUserManagerListener listener) {
		userContainer.removeListener(listener);
	}

	@Override
	public void signIn(String username, String password) {
		userContainer.signInWithProgressBar(username, password, null);
	}

	@Override
	public void signInWithProgressBar( String username, 
			                           String password,
			                           SubProgressMonitor monitor ) {
		userContainer.signInWithProgressBar(username, password, monitor);
	}

	@Override
	public void signOut() {
		userContainer.signOut();
	}

	@Override
	public String getNamespace() {
		return "userManager";
	}

}
