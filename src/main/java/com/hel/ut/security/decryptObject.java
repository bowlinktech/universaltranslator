package com.hel.ut.security;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author chadmccue
 */
public class decryptObject {

    private String keyString = "abq@#$72#PBI@a^BOSj)(*SBN@#$PHX#@LKjCHSdSFOj<.,098CRP";

    /**
     * Decrypts the String and serializes the object
     *
     * @param base64Data
     * @param base64IV
     * @return
     * @throws Exception
     */
    public Object decryptObject(String base64Data, String base64IV) throws Exception {
        // Decode the data
        byte[] encryptedData = Base64.decodeBase64(base64Data.getBytes());

        // Decode the Init Vector
        byte[] rawIV = Base64.decodeBase64(base64IV.getBytes());

        // Configure the Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(rawIV);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(keyString.getBytes());
        byte[] key = new byte[16];
        System.arraycopy(digest.digest(), 0, key, 0, key.length);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // Decrypt the data..
        byte[] decrypted = cipher.doFinal(encryptedData);

        // Deserialize the object
        ByteArrayInputStream stream = new ByteArrayInputStream(decrypted);

        ObjectInput in = new ObjectInputStream(stream);
        Object obj = null;
        try {
            obj = in.readObject();
        } finally {
            stream.close();
            in.close();
        }
        return obj;
    }

}
