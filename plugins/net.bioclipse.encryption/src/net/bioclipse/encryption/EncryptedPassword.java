package net.bioclipse.encryption;


public class EncryptedPassword {
    private EncryptedPassword(String encryptedPassword) {
        
    }
    
    public boolean matches(String cleartextPassword) {
        return false;
    }
    
    public static EncryptedPassword fromEncryptedPasswordString(String s) {
        return null;
    }
    
    public static EncryptedPassword fromPlainTextPasswordString(String s) {
        return null;
    }
    
    public String toString() {
        return "";
    }
}
