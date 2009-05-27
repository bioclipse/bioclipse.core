/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
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
import net.bioclipse.core.util.LogUtils;

import org.eclipse.core.runtime.SubProgressMonitor;

public class UserManager implements IUserManager {

    private static final Logger logger = Logger.getLogger(UserManager.class)
    ;
    //Package protected for testing...
    UserContainer userContainer;
    private List<IUserManagerListener> listeners;
    
    UserManager() {
        
    }
    
    public UserManager(String filename) {
        super();
        userContainer = new UserContainer(filename);
        listeners = new ArrayList<IUserManagerListener>();
    }
    
    public boolean accountExists(String accountId) {
        return userContainer.accountExists(accountId);
    }

    public void changePassword(String oldkey, String newkey) {
        userContainer.changePassword(oldkey, newkey);
    }

    public void clearAccounts() {
        userContainer.clearAccounts();
    }

    public void createAccount( String accountId,
                               HashMap<String, String> properties, 
                               AccountType accountType ) {
        userContainer.createAccount(accountId, properties, accountType);
    }

    public void createUser(String userName, String key) {
        userContainer.createUser(userName, key);
    }

    public void deleteUser(String user) {
        userContainer.deleteUser(user);
    }

    public List<String> getAccountIdsByAccountTypeName(String accountTypeName) {
        return userContainer.getAccountIdsByAccountTypeName(accountTypeName);
    }

    public AccountType getAccountType(String accountId) {
        return userContainer.getAccountType(accountId);
    }

    public String[] getAvailableAccountTypeNames() {
        return userContainer.getAvailableAccountTypeNames();
    }

    public AccountType[] getAvailableAccountTypes() {
        return userContainer.getAvailableAccountTypes();
    }

    public User getLoggedInUser() {
        return userContainer.getLoggedInUser();
    }

    public String getLoggedInUserName() {
        return userContainer.getLoggedInUserName();
    }

    public Collection<String> getLoggedInUsersAccountNames() {
        return userContainer.getLoggedInUsersAccountNames();
    }

    public String getProperty(String accountId, String propertyKey) {
        return userContainer.getProperty(accountId, propertyKey);
    }

    public Collection<String> getPropertyKeys(String accountId) {
        return userContainer.getPropertyKeys(accountId);
    }

    public List<String> getUserNames() {
        return userContainer.getUserNames();
    }

    public boolean isLoggedIn() {
        return userContainer.isLoggedIn();
    }

    public boolean isLoggedInWithAccountType(String accountTypeName) {
        return userContainer.isLoggedInWithAccountType(accountTypeName);
    }

    public void persist() {
        userContainer.persist();
    }

    public void reloadFromFile() {
        userContainer.reloadFromFile();
    }

    public void logIn(String username, String password) {
        userContainer.signIn(username, password, null);
        fireLogin();
    }

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

    public void logOut() {
        userContainer.signOut();
        fireLogout();
    }

    public String getManagerName() {
        return "userManager";
    }

    public UserContainer getSandBoxUserContainer() {
        return userContainer.clone();
    }

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
            LogUtils.debugTrace(logger, e);
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
//            TODO FIXME: show statusline somehow. Curently it fails because there isn't always an activeWorkbenchWindow  
//            IViewPart vp = (IViewPart)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart();
//            IStatusLineManager ism = vp.getViewSite().getActionBars().getStatusLineManager();
//            ism.setMessage(textToSet);
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
