package com.hel.ut.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.hel.ut.dao.messageTypeDAO;
import com.hel.ut.dao.organizationDAO;
import com.hel.ut.service.messageTypeManager;
import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.Organization;
import com.hel.ut.model.validationType;
import com.hel.ut.reference.fileSystem;

@Service
public class messageTypeManagerImpl implements messageTypeManager {

    @Autowired
    private messageTypeDAO messageTypeDAO;

    @Autowired
    private organizationDAO organizationDAO;
    
    @Override
    public double findTotalCrosswalks(int orgId) {
        return messageTypeDAO.findTotalCrosswalks(orgId);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getInformationTables() {
        return messageTypeDAO.getInformationTables();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getAllTables() {
        return messageTypeDAO.getAllTables();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getTableColumns(String tableName) {
        return messageTypeDAO.getTableColumns(tableName);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getValidationTypes() {
        return messageTypeDAO.getValidationTypes();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String getValidationById(int id) {
        return messageTypeDAO.getValidationById(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getDelimiters() {
        return messageTypeDAO.getDelimiters();
    }

    @Override
    public Long getTotalFields(int messageTypeId) {
        return messageTypeDAO.getTotalFields(messageTypeId);
    }

    @Override
    public List<Crosswalks> getCrosswalks(int page, int maxResults, int orgId) {
        return messageTypeDAO.getCrosswalks(page, maxResults, orgId);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getCrosswalkData(int cwId) {
        return messageTypeDAO.getCrosswalkData(cwId);
    }

    @Override
    public String getFieldName(int fieldId) {
        return messageTypeDAO.getFieldName(fieldId);
    }

    @Override
    public String getCrosswalkName(int cwId) {
        return messageTypeDAO.getCrosswalkName(cwId);
    }

    @Override
    public Long checkCrosswalkName(String name, int orgId) {
        return messageTypeDAO.checkCrosswalkName(name, orgId);
    }

    @Override
    public Integer createCrosswalk(Crosswalks crosswalkDetails) throws Exception {
        Integer lastId = null;
        String cleanURL = null;

        MultipartFile file = crosswalkDetails.getFile();
        String fileName = file.getOriginalFilename();

        InputStream inputStream = null;
        OutputStream outputStream = null;
        fileSystem dir = new fileSystem();

        if (crosswalkDetails.getOrgId() > 0) {
            Organization orgDetails = organizationDAO.getOrganizationById(crosswalkDetails.getOrgId());
            cleanURL = orgDetails.getcleanURL();
            dir.setDir(cleanURL, "crosswalks");
        } else {
            //Set the directory to save the uploaded message type template to
            dir.setMessageTypeCrosswalksDir("libraryFiles");
        }

        File newFile = null;
        newFile = new File(dir.getDir() + fileName);

        try {
            if (!newFile.exists()) {
                newFile.createNewFile();
            }

            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();

            //Set the filename to the original file name
            crosswalkDetails.setfileName(fileName);

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        }

        //Need to get the actual delimiter character
        String delimChar = (String) messageTypeDAO.getDelimiterChar(crosswalkDetails.getFileDelimiter());

        //Check to make sure the file contains the selected delimiter
        //Set the directory that holds the crosswalk files
        int delimCount = (Integer) dir.checkFileDelimiter(dir, fileName, delimChar);

        if (delimCount > 0) {
            //Submit the new message type to the database
            lastId = (Integer) messageTypeDAO.createCrosswalk(crosswalkDetails);

            //Call the function that will load the content of the crosswalk text file
            //into the rel_crosswalkData table
            loadCrosswalkContents(lastId, fileName, delimChar, cleanURL);

            return lastId;
        } else {
            //Need to delete the file
            newFile.delete();

            //Need to return an error
            return 0;
        }
    }
    
    @Override
    public Integer uploadNewFileForCrosswalk(Crosswalks crosswalkDetails) throws Exception {
        Integer lastId = null;
        String cleanURL = null;
	
	MultipartFile file = crosswalkDetails.getFile();
        String fileName = file.getOriginalFilename();

        InputStream inputStream = null;
        OutputStream outputStream = null;
        fileSystem dir = new fileSystem();

        if (crosswalkDetails.getOrgId() > 0) {
            Organization orgDetails = organizationDAO.getOrganizationById(crosswalkDetails.getOrgId());
            cleanURL = orgDetails.getcleanURL();
            dir.setDir(cleanURL, "crosswalks");
        } else {
            //Set the directory to save the uploaded message type template to
            dir.setMessageTypeCrosswalksDir("libraryFiles");
        }

        File newFile = null;
        newFile = new File(dir.getDir() + fileName);

        try {
            inputStream = file.getInputStream();

            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();

            //Set the filename to the original file name
            crosswalkDetails.setfileName(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Need to get the actual delimiter character
        String delimChar = (String) messageTypeDAO.getDelimiterChar(crosswalkDetails.getFileDelimiter());

        //Check to make sure the file contains the selected delimiter
        //Set the directory that holds the crosswalk files
        int delimCount = (Integer) dir.checkFileDelimiter(dir, fileName, delimChar);

        if (delimCount > 0) {
            //Submit the new message type to the database
            messageTypeDAO.updateCrosswalk(crosswalkDetails);

            //Call the function that will load the content of the crosswalk text file
            //into the rel_crosswalkData table
            loadCrosswalkContents(crosswalkDetails.getId(), fileName, delimChar, cleanURL);

            return crosswalkDetails.getId();
        } else {
            //Need to delete the file
            newFile.delete();

            //Need to return an error
            return 0;
        }
    }

    @Override
    public Crosswalks getCrosswalk(int cwId) {
        return messageTypeDAO.getCrosswalk(cwId);
    }

    /**
     * The 'loadCrosswalkContents' will take the contents of the uploaded text template file and populate the rel_crosswalkData table.
     *
     * @param id id: value of the latest added crosswalk
     * @param fileName	fileName: file name of the uploaded text file.
     * @param delim	delim: the delimiter used in the file
     *
     */
    public void loadCrosswalkContents(int id, String fileName, String delim, String cleanURL) throws Exception {

        //Set the directory that holds the crosswalk files
        fileSystem dir = new fileSystem();

        if (cleanURL == null) {
            dir.setMessageTypeCrosswalksDir("libraryFiles");
        } else {
            dir.setDir(cleanURL, "crosswalks");
        }

        FileInputStream file = null;
        String[] lineValue = null;
        try {
            file = new FileInputStream(new File(dir.getDir() + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Exception(e);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(file));

        try {
	    String sqlStmt = "";
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception(e);
            }
	    
	    messageTypeDAO.executeSQLStatement("delete from rel_crosswalkData where crosswalkId = "+id);
	   
            while (line != null) {

                //Need to parse each line via passed in delimiter
                if (delim == "t") {
                    lineValue = line.split("\t");
                } else {
                    lineValue = line.split("\\" + delim);
                }
                String sourceValue = lineValue[0];
                String targetValue = lineValue[1];
                String descVal = lineValue[2];
		
		sqlStmt += "INSERT INTO rel_crosswalkData (crosswalkId, sourceValue, targetValue, descValue) VALUES ("+id+",'"+sourceValue+"','"+targetValue+"','"+descVal+"');";

                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new Exception(e);
                }
            }
	    
	    if(!"".equals(sqlStmt)) {
		messageTypeDAO.executeSQLStatement(sqlStmt);
	    }

        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception(e);
            }
        }

    }

    // this does the same thing as getValidationTypes except putting result in an object
    //TODO need to combine and test and replace getValidationTypes
    @Override
    public List<validationType> getValidationTypes1() {
        return messageTypeDAO.getValidationTypes1();
    }

    @Override
    public String getDelimiterChar(int delimId) {
        return messageTypeDAO.getDelimiterChar(delimId);
    }
}
