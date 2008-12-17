/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.encryption;
import static org.junit.Assert.*;
import org.junit.Test;
/**
 * @author jonalv
 *
 */
public class EncryptedPasswordTest {
    @Test
    public void test() throws Exception {
        final String PSWD = "secretest password"; 
        EncryptedPassword encryptedPassword 
            = EncryptedPassword.fromPlaintextPassword( PSWD );
        assertTrue( encryptedPassword.matches( PSWD ) );
        String pswd = encryptedPassword.toString();
        assertFalse( pswd.equals( PSWD ) );
        encryptedPassword
            = EncryptedPassword.fromAlreadyEncryptedPassword( pswd );
        assertTrue( encryptedPassword.matches( PSWD ) );
    }
}
