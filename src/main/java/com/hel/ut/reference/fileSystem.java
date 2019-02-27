package com.hel.ut.reference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class fileSystem {

    //Get the operating system
    String os = System.getProperty("os.name").toLowerCase();


    public void deleteOrgDirectories(String directory) {

        try {
            File orgDirectory = new File(directory);
	    delete(orgDirectory);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void creatFTPDirectory(String ftpDirectory) {
	
	try {
            File directory = new File(ftpDirectory);
	    if (!directory.exists()) {
		directory.mkdir();
	    }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createFileDroppedDirectory(String folderName) {
	try {
            File directory = new File(folderName);
	    if (!directory.exists()) {
		directory.mkdir();
	    }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void creatOrgDirectories(String orgDirectory) {

        try {
            File directory = new File(orgDirectory);
	    if (!directory.exists()) {
		directory.mkdir();
		new File(orgDirectory+ "/crosswalks").mkdirs();
		new File(orgDirectory + "/input files").mkdirs();
		new File(orgDirectory + "/output files").mkdirs();
		new File(orgDirectory + "/templates").mkdirs();
		new File(orgDirectory + "/attachments").mkdirs();
		new File(orgDirectory + "/certificates").mkdirs();
	    }
	    else {
		directory = new File(orgDirectory + "/crosswalks");
		if (!directory.exists()) {
		     directory.mkdir();
		}
		directory = new File(orgDirectory + "/input files");
		if (!directory.exists()) {
		     directory.mkdir();
		}
		directory = new File(orgDirectory + "/output files");
		if (!directory.exists()) {
		     directory.mkdir();
		}
		directory = new File(orgDirectory + "/templates");
		if (!directory.exists()) {
		     directory.mkdir();
		}
		directory = new File(orgDirectory + "/attachments");
		if (!directory.exists()) {
		     directory.mkdir();
		}
		directory = new File(orgDirectory + "/certificates");
		if (!directory.exists()) {
		     directory.mkdir();
		}
	    }
        } 
	catch (Exception e) {
        	System.out.println(String.valueOf(os) + " is os. ERROR AT creatOrgDirectories " + new Date());
            e.printStackTrace();
            try {
            	throw new Exception((new Date() + " os" + String.valueOf(os) + " Error creating directories for path " + orgDirectory), e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }

    }

    public static void delete(File file) throws IOException {

        if (file.isDirectory()) {

            //directory is empty, then delete it
            if (file.list().length == 0) {

                file.delete();

            } else {

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                }
            }

        } else {
            //if file, then delete it
            file.delete();
        }
    }

    /**
     * The checkFileDelimiters function will check to make sure the file contains data separated by the delimiter chosen when uploading the file.
     *
     * @param dir
     * @param fileName	The name of the file uploaded
     * @param delim	The delimiter chosen when uploading the file
     * @return 
     * @throws java.lang.Exception
     *
     * @returns The function will return 0 if no delimiter was found or the count of the delimiter
     */
    public Integer checkFileDelimiter(String dir, String fileName, String delim) throws Exception {

        int delimCount = 0;
        String errorMessage = "";

        FileInputStream fileInput = null;
        try {
            File file = new File(dir + fileName);
            fileInput = new FileInputStream(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(fileInput));

        try {
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    if (delim == "t") {
                        delimCount = line.split("\t", -1).length - 1;
                    } else {
                        delimCount = line.split("\\" + delim, -1).length - 1;
                    }
                    break;
                }
            } catch (IOException ex) {
                errorMessage = errorMessage + "<br/>" + ex.getMessage();
                Logger.getLogger(fileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                errorMessage = errorMessage + "<br/>" + e.getMessage();
                e.printStackTrace();
            }
        }
        /**
         * throw error message here because want to make sure file stream is closed *
         */
        if (!errorMessage.equalsIgnoreCase("")) {
            throw new Exception(errorMessage);
        }
        return delimCount;
    }

    public Integer checkFileDelimiter(File file, String delim) throws Exception {

        int delimCount = 0;
        String errorMessage = "";
        FileInputStream fileInput = null;
        try {
            fileInput = new FileInputStream(file);

        } catch (FileNotFoundException e) {
            errorMessage = e.getMessage();
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(fileInput));

        try {
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    if (delim == "t") {
                        delimCount = line.split("\t", -1).length - 1;
                    } else {
                        delimCount = line.split("\\" + delim, -1).length - 1;
                    }
                    break;
                }
            } catch (IOException ex) {
                errorMessage = errorMessage + "<br/>" + ex.getMessage();
                Logger.getLogger(fileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                errorMessage = errorMessage + "<br/>" + e.getMessage();
                e.printStackTrace();
            }
        }

        /**
         * throw error message here because want to make sure file stream is closed *
         */
        if (!errorMessage.equalsIgnoreCase("")) {
            throw new Exception(errorMessage);
        }

        return delimCount;
    }

    public byte[] loadFile(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

}
