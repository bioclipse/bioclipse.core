package net.bioclipse.usermanager;

import java.util.HashMap;


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
    public String getAccountType();
    
}
