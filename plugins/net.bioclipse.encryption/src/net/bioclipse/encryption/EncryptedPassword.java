package net.bioclipse.encryption;

public class EncryptedPassword {
    private String encryptedPassword;
    
    private EncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
    
    private static String encrypt(String cleartext) {
        return "s1kkr1t" + cleartext;
    }
    
    public boolean matches(String cleartextPassword) {
        return encryptedPassword != null
               && encryptedPassword.equals( encrypt(cleartextPassword) );
    }
    
    public static EncryptedPassword fromAlreadyEncryptedPassword(String p) {
        return new EncryptedPassword(p);
    }
    
    public static EncryptedPassword fromPlaintextPassword(String p) {
        return new EncryptedPassword( encrypt(p) );
    }
    
    public String toString() {
        return encryptedPassword;
    }
}
