/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.security;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author chadmccue
 */
public class encryptObject {

    private String keyString = "abq@#$72#PBI@a^BOSj)(*SBN@#$PHX#@LKjCHSdSFOj<.,098CRP";

    /**
     * Encrypts and encodes the Object and IV for url inclusion
     *
     * @param input
     * @return
     * @throws Exception
     */
    public String[] encryptObject(Object obj) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(stream);
        try {
            // Serialize the object
            out.writeObject(obj);
            byte[] serialized = stream.toByteArray();
            // Setup the cipher and Init Vector
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[cipher.getBlockSize()];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            // Hash the key with SHA-256 and trim the output to 128-bit for the key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(keyString.getBytes());
            byte[] key = new byte[16];
            System.arraycopy(digest.digest(), 0, key, 0, key.length);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            // encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            // Encrypt & Encode the input
            byte[] encrypted = cipher.doFinal(serialized);
            byte[] base64Encoded = Base64.encodeBase64(encrypted);
            String base64String = new String(base64Encoded);
            String urlEncodedData = URLEncoder.encode(base64String, "UTF-8");
            // Encode the Init Vector
            byte[] base64IV = Base64.encodeBase64(iv);
            String base64IVString = new String(base64IV);
            String urlEncodedIV = URLEncoder.encode(base64IVString, "UTF-8");

            return new String[]{urlEncodedData, urlEncodedIV};
        } finally {
            stream.close();
            out.close();
        }
    }

}
