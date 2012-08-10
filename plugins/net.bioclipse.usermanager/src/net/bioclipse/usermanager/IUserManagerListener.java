/* *****************************************************************************
 * Copyright (c) 2007-2009 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.usermanager;

/**
 * @author jonalv
 *
 */
public interface IUserManagerListener {
    /* The listener should not know the AccountId, but how give it the right 
     * log-in properties if the user have more than one account per account 
     * type in Bioclipse?*/
    public final static String MY_ACCOUNT_TYPE = "";
    public boolean receiveUserManagerEvent( UserManagerEvent event );
    public String getAccountType();
}
