/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
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
 * Events fired by the Keyring
 * 
 * @author jonalv
 *
 */
public enum UserManagerEvent {
    /**
     * Event fired when logging in
     */
    LOGIN (),
    /**
     * Event fired when logging out 
     */
    LOGOUT(),
    /**
     * Event fired when properties of the Keyring has been updated
     */
    UPDATE();
    private UserManagerEvent() {
    }
}
