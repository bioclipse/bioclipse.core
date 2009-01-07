/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.usermanager;

import net.bioclipse.usermanager.UserContainer;

/**
 * This class does evil stuff with package protected variables for 
 * testing purposes
 * 
 * Motivation: An ugly test i better than no test at all...
 * 
 * @author jonalv
 *
 */
public abstract class UserContainerModifier {

    public static void addAccountType( UserContainer userContainer,
                                       AccountType accountType ) {
        userContainer.availableAccountTypes.add(accountType);
    }
}
