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

    String dir = null;

    String directoryPath = System.getProperty("directory.utRootDir");

    public String getDir() {
        return dir;
    }

    public void setMessageTypeDir(String folderName) {
        //Windows
        if (os.indexOf("win") >= 0) {
            this.dir = directoryPath + folderName + "\\";
        } //Mac
        else if (os.indexOf("mac") >= 0) {
            this.dir = directoryPath + folderName + "/";
        } //Unix or Linux or Solarix
        else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 || os.indexOf("sunos") >= 0) {
            this.dir = directoryPath + folderName + "/";
        }
    }

    public void setMessageTypeCrosswalksDir(String folderName) {
        //Windows
        if (os.indexOf("win") >= 0) {
            this.dir = directoryPath + folderName + "\\crosswalks\\";
        } //Mac
        else if (os.indexOf("mac") >= 0) {
            this.dir = directoryPath + folderName + "/crosswalks/";
        } //Unix or Linux or Solarix
        else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 || os.indexOf("sunos") >= 0) {
            this.dir = directoryPath + folderName + "/crosswalks/";
        }
    }

    public void setDir(String orgName, String folderName) {

        //Windows
        if (os.indexOf("win") >= 0) {
            this.dir = directoryPath + orgName + "\\" + folderName + "\\";
        } //Mac
        else if (os.indexOf("mac") >= 0) {
            this.dir = directoryPath + orgName + "/" + folderName + "/";
        } //Unix or Linux or Solarix
        else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 || os.indexOf("sunos") >= 0) {
            this.dir = directoryPath + orgName + "/" + folderName + "/";
        }
    }

    public void setDirByName(String dirName) {
        //Windows
        if (os.indexOf("win") >= 0) {
            this.dir = directoryPath + dirName;
        } //Mac
        else if (os.indexOf("mac") >= 0) {
            this.dir = directoryPath + dirName;
        } //Unix or Linux or Solarix
        else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 || os.indexOf("sunos") >= 0) {
            this.dir = directoryPath + dirName;
        }
    }

    public void deleteOrgDirectories(String orgName) {

        try {
            //Windows
            if (os.indexOf("win") >= 0) {
                //C:/BowLink/
                String dir = directoryPath + orgName;
                File directory = new File(dir);
                delete(directory);
                
                directory = new File(directoryPath + "headerimages\\" + orgName);
                delete(directory);
                
            } //Mac
            else if (os.indexOf("mac") >= 0) {
                String dir = directoryPath + orgName;
                File directory = new File(dir);
                delete(directory);
                
                directory = new File(directoryPath + "headerimages/" + orgName);
                delete(directory);
             } //Unix or Linux or Solarix
            else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 || os.indexOf("sunos") >= 0) {
                String dir = directoryPath + orgName;
                File directory = new File(dir);
                delete(directory);
                
                directory = new File(directoryPath + "headerimages/" + orgName);
                delete(directory);
                
                
            } else {
                System.out.println("Your OS is not support!!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void creatFTPDirectory(String dirName) {
	
	try {
            //Windows
            if (os.indexOf("win") >= 0) {
               String[] directories = dirName.replace("/UT", "").replace("UT", "").split("/");
		
		String dir = directoryPath + "UT\\";
		
		for(int i=0;i<directories.length;i++) {
		    dir += directories[i] + "\\";
		    File directory = new File(dir);
		    if (!directory.exists()) {
			directory.mkdir();
		    }
		}
            } //Mac
            else if (os.indexOf("mac") >= 0) {
		String[] directories = dirName.replace("/UT", "").replace("UT", "").split("/");
		
		String dir = directoryPath + "UT/";
		
		for(int i=0;i<directories.length;i++) {
		    dir += directories[i] + "/";
		    File directory = new File(dir);
		    if (!directory.exists()) {
			directory.mkdir();
		    }
		}
            } //Unix or Linux or Solarix
            else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 || os.indexOf("sunos") >= 0) {
                String[] directories = dirName.replace("/UT", "").replace("UT", "").split("/");
		
		String dir = directoryPath + "UT/";
		
		for(int i=0;i<directories.length;i++) {
		    dir += directories[i] + "/";
		    File directory = new File(dir);
		    if (!directory.exists()) {
			directory.mkdir();
		    }
		}
            } else {
                System.out.println("Your OS is not support!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createOrgDirectory(String orgName, String dirName) {
        try {
            //Windows
            if (os.indexOf("win") >= 0) {
                //C:/BowLink/
                String dir = directoryPath + orgName + "\\" + dirName.replace("/", "\\");
                File directory = new File(dir);
                if (!directory.exists()) {
                    directory.mkdir();
                }
            } //Mac
            else if (os.indexOf("mac") >= 0) {
                String dir = directoryPath + orgName + "/" + dirName;
                File directory = new File(dir);
                if (!directory.exists()) {
                    directory.mkdir();
                }
            } //Unix or Linux or Solarix
            else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 || os.indexOf("sunos") >= 0) {
                String dir = directoryPath + orgName + "/" + dirName;
                File directory = new File(dir);
                if (!directory.exists()) {
                    directory.mkdir();
                }
            } else {
                System.out.println("Your OS is not support!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void creatOrgDirectories(String orgName) {

        try {
            //Windows
            if (os.indexOf("win") >= 0) {
                //C:/BowLink/
                String dir = directoryPath + orgName;
                File directory = new File(dir);
                if (!directory.exists()) {
                    directory.mkdir();
                    new File(directoryPath + orgName + "\\crosswalks").mkdirs();
                    new File(directoryPath + orgName + "\\input files").mkdirs();
                    new File(directoryPath + orgName + "\\output files").mkdirs();
                    new File(directoryPath + orgName + "\\templates").mkdirs();
                    new File(directoryPath + orgName + "\\attachments").mkdirs();
                    new File(directoryPath + orgName + "\\certificates").mkdirs();
                }
            } //Mac
            else if (os.indexOf("mac") >= 0) {
                String dir = directoryPath + orgName;
                File directory = new File(dir);
                if (!directory.exists()) {
                    directory.mkdir();
                    new File(directoryPath + orgName + "/crosswalks").mkdirs();
                    new File(directoryPath + orgName + "/input files").mkdirs();
                    new File(directoryPath + orgName + "/output files").mkdirs();
                    new File(directoryPath + orgName + "/templates").mkdirs();
                    new File(directoryPath + orgName + "/attachments").mkdirs();
                    new File(directoryPath + orgName + "/certificates").mkdirs();
                }
            } //Unix or Linux or Solarix
            else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 || os.indexOf("sunos") >= 0) {
                String dir = directoryPath + orgName;
                File directory = new File(dir);
                if (!directory.exists()) {
                    directory.mkdir();
                    new File(directoryPath + orgName + "/crosswalks").mkdirs();
                    new File(directoryPath + orgName + "/input files").mkdirs();
                    new File(directoryPath + orgName + "/output files").mkdirs();
                    new File(directoryPath + orgName + "/templates").mkdirs();
                    new File(directoryPath + orgName + "/attachments").mkdirs();
                    new File(directoryPath + orgName + "/certificates").mkdirs();
                }
            } else {
                System.out.println("Your OS is not support!!");
            }
        } catch (Exception e) {
        	System.out.println(String.valueOf(os) + " is os. ERROR AT creatOrgDirectories " + new Date());
            e.printStackTrace();
            try {
            	throw new Exception((new Date() + " os" + String.valueOf(os) + " Error creating directories for path " + directoryPath), e);
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
     * @param fileName	The name of the file uploaded
     * @param delim	The delimiter chosen when uploading the file
     *
     * @returns The function will return 0 if no delimiter was found or the count of the delimiter
     */
    public Integer checkFileDelimiter(fileSystem dir, String fileName, String delim) throws Exception {

        int delimCount = 0;
        String errorMessage = "";

        FileInputStream fileInput = null;
        try {
            File file = new File(dir.getDir() + fileName);
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

    public boolean isWinMachine() {

        boolean winMachine = false;
        //Windows
        if (os.indexOf("win") >= 0) {
            winMachine = true;
        }
        return winMachine;
    }

    public String setPath(String addOnPath) {
        String path = "";
        //Windows
        if (os.indexOf("win") >= 0) {
            path = directoryPath.replace("\\HELProductSuite\\universalTranslator\\", "") + addOnPath.replace("", "").replace("/", "\\");
        } //Mac
        else if (os.indexOf("mac") >= 0) {
            path = directoryPath.replace("/HELProductSuite/universalTranslator/", "") + addOnPath;
        } //Unix or Linux or Solarix
        else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 || os.indexOf("sunos") >= 0) {
            path = directoryPath.replace("/HELProductSuite/universalTranslator/", "") + addOnPath;
        }
        return path;
    }

    public String setPathFromRoot(String addOnPath) {

        String path = "";

        //Windows
        if (os.indexOf("win") >= 0) {
            path = addOnPath.replace("", "").replace("/", "\\");
        } //Mac
        else if (os.indexOf("mac") >= 0) {
            path = addOnPath;
        } //Unix or Linux or Solarix
        else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 || os.indexOf("sunos") >= 0) {
            path = addOnPath;
        }
        return path;
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
