/* *****************************************************************************
 * Copyright (c) 2012 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 ******************************************************************************/
package net.bioclipse.usermanager;

import org.eclipse.core.expressions.PropertyTester;

/**
 * A class that tests if there's any user accounts created.
 * 
 * @author Klas Jšnsson (klas.joensson@gmail.com)
 *
 */
public class UserAccountTester extends PropertyTester {

    public UserAccountTester() {    }
    
    @Override
    public boolean test( Object receiver, String property, Object[] args,
                         Object expectedValue ) {

        if ("userAccountsExists".equalsIgnoreCase(property)){

            if (!(expectedValue instanceof Boolean)) return false;

            int userCount = Activator.getDefault().getUserManager().getUserNames().size();

            if (userCount > 0)
                return true;

        }

        return false;
    }

}
