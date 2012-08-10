/* *****************************************************************************
 *Copyright (c) 2008-2009 The Bioclipse Team and others.
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
import java.util.Iterator;
import java.util.List;

import net.bioclipse.usermanager.AccountType;
import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.User;
import net.bioclipse.usermanager.UserContainer;
import net.bioclipse.usermanager.UserManagerEvent;

import org.apache.log4j.Logger;
import net.bioclipse.core.util.LogUtils;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class UserManager implements IUserManager {
    
    ArrayList<String> failedLogin = new ArrayList<String>(); 
    // <AccountType, Listener>
    HashMap<String, IUserManagerListener> listnerIds = 
            new HashMap<String, IUserManagerListener>();
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

    public boolean logIn(String username, String password) {
        userContainer.signIn(username, password, null);
        return fireLogin();
    }

    public boolean signInWithProgressBar( String username, 
                                       String password,
                                       SubProgressMonitor monitor ) {
        SubProgressMonitor subMonitor 
            = monitor == null ? null 
                              : new SubProgressMonitor(monitor, 10);
        userContainer.signIn(username, password, subMonitor);
        subMonitor = monitor == null ? null 
                                     : new SubProgressMonitor(monitor, 90);
        return fireLoginWithProgressBar( subMonitor );
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
        persist();
    }
    
    public boolean signInToAccount(String accountId) {
        String accountType = userContainer.getAccountType( accountId ).getName();
        return fireLoggin( accountType );
    }
    
    public boolean signInToAccount(AccountType accountType) {
        return fireLoggin( accountType.getName() );
    }
    /**
     * This method log-in to a specific thired-part account, if the user is
     * logged in. If the user isn't logged in it returns false.
     *  
     * @param AccountId The id of the account.
     * 
     * @return True If the log-in is successfully.
     */
    /* The listener should not know the AccountId, but how give it the right 
     * log-in properties if the user have more than one account per account 
     * type in Bioclipse?*/
    private boolean fireLoggin(String accountType) {
        if (userContainer.isLoggedIn()) {
            return listnerIds.get( accountType ).receiveUserManagerEvent( UserManagerEvent.LOGIN );
        } else
            return false;
    }
    
    /**
     *  Fires a login event
     */
    public boolean fireLogin() {
        return fireLoginWithProgressBar(null);
    }
    
    private boolean fireLoginWithProgressBar( SubProgressMonitor monitor ) {
        boolean usingMonitor = monitor != null;
        boolean loginOK = false;
        int ticks = 100;
        String name;
        
        try {
            for( IUserManagerListener listener : listeners) {
                loginOK = listener.receiveUserManagerEvent( UserManagerEvent.LOGIN );
                if (!loginOK) {
                    name = listener.getClass().getName();
                    failedLogin.add( name
                                     .substring( 0, name.lastIndexOf( '.' ) ) );
                }
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
        return loginOK;
    }
    
    /**
     * This method returns the name of the plug-ins that has failed to log-in 
     * to e.g. an online account. If non failed it returns an empty list.
     * 
     * @return An ArrayList with the names of the failed plug-ins
     */
    public ArrayList<String> getFailedLogins() {
        return failedLogin;
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
        listnerIds.put( listener.getAccountType(), listener );
        listeners.add(listener);
    }
}
