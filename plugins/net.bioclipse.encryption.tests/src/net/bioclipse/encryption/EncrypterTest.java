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
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author jonalv
 *
 */
public class EncrypterTest {
    private String encrypted;
    private final String text = "The text to be encrypted and decrypted";
    private final String PSWD = "secretest password";
    private Encrypter encrypter;
    @Test
    public void encrypterDoesSomething() {
        encrypted = encryptText(text);
        assertFalse( encrypted.equals( text ) );
    }
    @Test
    public void encryptAndDecrypt() {
        encrypterDoesSomething();
        assertEquals( text, encrypter.decrypt(encrypted) );
    }
    private String encryptText( String text ) {
        encrypter = new Encrypter(PSWD);
        return encrypter.encrypt(text);
    }
}
