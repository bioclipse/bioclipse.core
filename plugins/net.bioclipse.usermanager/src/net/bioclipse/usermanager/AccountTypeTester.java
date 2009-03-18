package net.bioclipse.usermanager;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;


public class AccountTypeTester extends PropertyTester {

    public AccountTypeTester() {

    }

    public boolean test( Object receiver, String property, Object[] args,
                         Object expectedValue ) {

        if ("accountTypeExists".equalsIgnoreCase(property)){

            if (!(expectedValue instanceof Boolean)) return false;
            
            //Initialize implementations via extension points
            IExtensionRegistry registry = Platform.getExtensionRegistry();

            if ( registry == null )
                throw new RuntimeException("Registry is null, no services can " +
                "be read. Workbench not started?");
            // it likely means that the Eclipse workbench has not
            // started, for example when running tests

            /*
             * service objects
             */
            IExtensionPoint serviceObjectExtensionPoint = registry
                    .getExtensionPoint("net.bioclipse.usermanager.accountType");
            if (serviceObjectExtensionPoint==null) return false;
            
            IExtension[] serviceObjectExtensions
            = serviceObjectExtensionPoint.getExtensions();

            if (serviceObjectExtensions!=null && serviceObjectExtensions.length>0){
                return true;
            }

        }
        
        return false;

    }

}
