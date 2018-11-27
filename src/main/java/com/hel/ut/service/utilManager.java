package com.hel.ut.service;

import java.io.IOException;

public interface utilManager {

    String encodeStringToBase64Binary(String str) throws IOException;

    String decodeStringToBase64Binary(String str) throws IOException;

}
