package net.bioclipse.usermanager;

import org.eclipse.core.expressions.PropertyTester;


public class LoggedInTester extends PropertyTester {

    public LoggedInTester() {
    }

    public boolean test( Object receiver, String property, Object[] args,
                         Object expectedValue ) {

        if ("isLoggedIn".equalsIgnoreCase(property)){

            if (!(expectedValue instanceof Boolean)) return false;

            boolean expected=(Boolean)expectedValue;
            boolean actual=Activator.getDefault().getUserManager().isLoggedIn();
            
            return (actual==expected);
        }
        
        return false;
    }

}
