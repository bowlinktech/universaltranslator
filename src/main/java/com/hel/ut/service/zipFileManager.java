/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.springframework.stereotype.Service;

/**
 *
 * @author chadmccue
 */
@Service
public class zipFileManager {
    
    public File unzipFile(File folder, String zippedFile, String newFile, String newFileName, int zipType, String fileExt) {
	
	 byte[] buffer = new byte[1024];
	 
	 File unzippedFile;
	 
	 try{

	   FileInputStream fileIn = new FileInputStream(zippedFile);
	   
	   //GZip
	   if(zipType == 1 || "gz".equals(fileExt)) {
	       
	       GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);

	       FileOutputStream fileOutputStream = new FileOutputStream(newFile.replace(".gz", ""));

	       int bytes_read;
	       
	       while((bytes_read = gZIPInputStream.read(buffer)) != -1){
		    fileOutputStream.write(buffer, 0, bytes_read);
		}

	       fileOutputStream.close();
	       gZIPInputStream.close();
	       
	       File[] files = folder.listFiles(new FilenameFilter() {
		   public boolean accept(File dir, String name) {
		       return name.toLowerCase().contains(newFileName.replace(".gz", ""));
		   }
	       });

	       if(files.length > 0) {
		   unzippedFile = files[0];
		   return unzippedFile;
	       }
	       else {
		   return null;
	       }
	   }
	   else {
	       return null;
	   }

	}
	catch (IOException ex) {
	   return null;
	}
    }
    
    public File zipFile(File folder, String fileToZip, String newFile, String newFileName, int zipType, String fileExt) {
	
	 byte[] buffer = new byte[1024];
	 
	 File zippedFile;
	 
	 try{

	   FileInputStream fileIn = new FileInputStream(fileToZip);
	   
	   //GZip
	   if(zipType == 1) {
	       
	       final String zippedFileName = newFileName.replace(fileExt,"gz");
	       
	       FileOutputStream fileOutputStream = new FileOutputStream(newFile.replace(fileExt,"gz"));
	       
	       GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(fileOutputStream);

	       int bytes_read;
	       
	       while((bytes_read = fileIn.read(buffer)) != -1){
		    gZIPOutputStream.write(buffer, 0, bytes_read);
		}
	       
	       File[] files = folder.listFiles((File dir, String name) -> name.toLowerCase().contains(zippedFileName.toLowerCase()));
	       
	       gZIPOutputStream.close();
	       fileOutputStream.close();
	       
	       if(files.length > 0) {
		   zippedFile = files[0];
		   fileIn.close();
		   return zippedFile;
	       }
	       else {
		   fileIn.close();
		   return null;
	       }
	   }
	   else {
	       fileIn.close();
	       return null;
	   }

	}
	catch (IOException ex) {
	   return null;
	}
    }
    
}
