package net.bioclipse.usermanager;

import java.util.HashMap;

/**
 * An interface for plugins that wants to use a button for test the properties 
 * used for logging in before the user create an account.
 *  
 * @author klasjonsson
 *
 */
public interface ITestAccountLogin {
    /** 
     * The account page doesn't know the names nor exactly which of the 
     * properties that are needed to login. This is some thing that the 
     * test-login-class has to sort out.
     * 
     * @param myProperites A hashMap with properties collected by the 
     *          wizard page.
     */
    public boolean login(HashMap<String, String> myProperites);
    /**
     * A method for identify the test-class. It should be the same name as the 
     * AccountType uses.
     * 
     * @return The name of the AccountType.
     */
    public String getAccountType();
    
}
