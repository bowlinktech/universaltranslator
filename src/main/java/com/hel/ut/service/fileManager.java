package com.hel.ut.service;

import java.io.File;
import java.io.IOException;

public interface fileManager {

    String encodeFileToBase64Binary(File file) throws IOException;

    String decodeFileToBase64Binary(File file) throws IOException;

    byte[] fileToBytes(File file) throws IOException;

    String readTextFile(String fileName);

    void writeFile(String strFileName, String strFile);

    void decode(String sourceFile, String targetFile) throws Exception;

    void writeByteArraysToFile(String fileName, byte[] content) throws IOException;

    byte[] loadFileAsBytesArray(String fileName) throws Exception;
    
    void copyFile(String sourceFile, String targetFile) throws Exception;
    
    boolean isFileBase64Encoded(File file) throws Exception;
}
