package net.bioclipse.encryption;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
public class EncryptedPassword {
    private String encryptedPassword;
    private EncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
    public static String encrypt(String plaintext)
                         throws IllegalStateException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
            md.update(plaintext.getBytes("UTF-8"));
        }
        catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        catch(UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        return new String( new Base64().encode(md.digest()) );
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
