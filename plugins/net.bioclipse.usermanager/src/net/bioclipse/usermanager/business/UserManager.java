package net.bioclipse.usermanager.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.bioclipse.usermanager.AccountType;
import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.User;
import net.bioclipse.usermanager.UserContainer;
import net.bioclipse.usermanager.UserManagerEvent;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.SubProgressMonitor;

public class UserManager implements IUserManager {

	//Package protected for testing...
	UserContainer userContainer;
	private List<IUserManagerListener> listeners;
	
	public UserManager(String filename) {
		super();
		userContainer = new UserContainer(filename);
		listeners = new ArrayList<IUserManagerListener>();
	}
	
	@Override
	public boolean accountExists(String accountId) {
		return userContainer.accountExists(accountId);
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
	public void signIn(String username, String password) {
		userContainer.signIn(username, password, null);
	}

	@Override
	public void signInWithProgressBar( String username, 
			                           String password,
			                           SubProgressMonitor monitor ) {
		SubProgressMonitor subMonitor 
			= monitor == null ? null 
		                      : new SubProgressMonitor(monitor, 10);
		userContainer.signIn(username, password, subMonitor);
		subMonitor = monitor == null ? null 
	                                 : new SubProgressMonitor(monitor, 90);
		fireLoginWithProgressBar( subMonitor );
	}

	@Override
	public void signOut() {
		userContainer.signOut();
		fireLogout();
	}

	@Override
	public String getNamespace() {
		return "userManager";
	}

	@Override
	public UserContainer getSandBoxUserContainer() {
		return userContainer.clone();
	}

	@Override
	public void switchUserContainer(UserContainer userContainer) {
		this.userContainer = userContainer;
		fireUpdate();
	}
	
	/**
     *  Fires a login event
     */
    public void fireLogin() {
    	fireLoginWithProgressBar(null);
	}
    
    private void fireLoginWithProgressBar( SubProgressMonitor monitor ) {
    	boolean usingMonitor = monitor != null;
    	int ticks = 100;
    	try {
	    	for( IUserManagerListener listener : listeners) {
	    		listener.receiveUserManagerEvent( UserManagerEvent.LOGIN );
	    		if(usingMonitor) {
	    			monitor.beginTask("signing in", ticks);
	    			monitor.worked( ticks/listeners.size() );
	    		}
	    	}
	    	if( isLoggedIn() ) {
	    		setStatusLinetext( "Logged in as: " 
	    				           + getLoggedInUserName() );
	    	}
    	}
    	catch (RuntimeException e) {
    		e.printStackTrace();
    		throw e;
    	}
    	finally {
    		if(usingMonitor) {
    			monitor.done();
    		}
    	}
	}
    
    private void setStatusLinetext( String textToSet) {
		try {
//			TODO FIXME: show statusline somehow. Curently it fails because there isn't always an activeWorkbenchWindow  
//			IViewPart vp = (IViewPart)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart();
//			IStatusLineManager ism = vp.getViewSite().getActionBars().getStatusLineManager();
//			ism.setMessage(textToSet);
		}
		catch(IllegalStateException e) {
			System.out.println( e.getMessage() 
					            + " -- If Bioclipse is not running no " 
					            + "workbench should have been created."  );
		}
	}
    
    /**
     * Fires a logout event
     */
    public void fireLogout() {
    	for( IUserManagerListener listener : listeners)
    		listener.receiveUserManagerEvent( UserManagerEvent.LOGOUT );
		setStatusLinetext("not logged in");
	}
    
    /**
     * Fires an update event
     */
    public void fireUpdate() {
    	for( IUserManagerListener listener : listeners)
    		listener.receiveUserManagerEvent( UserManagerEvent.UPDATE );
    }
	
	/**
	 * @param listener to be added
	 */
	public void removeListener(IUserManagerListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * @param listener to be removed
	 */
	public void addListener(IUserManagerListener listener) {
		listeners.add(listener);
	}
}
