package com.hel.ut.service.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import com.hel.ut.service.utilManager;

@Service
public class utilManagerImpl implements utilManager {

    public String encodeStringToBase64Binary(String str) throws IOException {
        try {
            byte[] bytes = str.getBytes(Charset.forName("UTF-8"));
            byte[] encoded = Base64.encodeBase64(bytes);
            String encodedString = new String(encoded);

            return encodedString;
        } catch (Exception ex) {
            System.err.println("encodeStringToBase64Binary -" + ex.getLocalizedMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public String decodeStringToBase64Binary(String str) throws IOException {
        try {
            byte[] bytes = str.getBytes(Charset.forName("UTF-8"));
            byte[] decoded = Base64.decodeBase64(bytes);
            String decodedString = new String(decoded);
            return decodedString;
        } catch (Exception ex) {
            System.err.println("decodeStringToBase64Binary -" + ex.getLocalizedMessage());
            ex.printStackTrace();
            return null;
        }
    }

}
