/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service;

import com.hel.ut.model.Organization;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.utUserActivity;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import javax.annotation.Resource;
import org.json.simple.parser.JSONParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author chadmccue
 */
@Service
public class JSONtoTxt {

    @Autowired
    private organizationManager organizationmanager;
    
    @Autowired
    private userManager usermanager;
    
    @Autowired
    private utConfigurationManager configurationManager;
    
    @Resource(name = "myProps")
    private Properties myProps;

    @Autowired
    private utConfigurationTransportManager configurationTransportManager;

    public String TranslateJSONtoTxt(String fileLocation, String fileName, int orgId, int configId, Integer batchId) throws Exception {

        Organization orgDetails = organizationmanager.getOrganizationById(orgId);
        
	String directory = myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/templates/";
	
	String templatefileName = "";
	
	String newfileName = "";
	
	if(configId > 0) {
	    configurationMessageSpecs configurationParsingScript = configurationManager.getMessageSpecs(configId);
	    
	    if(configurationParsingScript.getParsingTemplate()!= null) {
		if(!"".equals(configurationParsingScript.getParsingTemplate())) {
		    templatefileName = configurationParsingScript.getParsingTemplate();
		}
		else {
		    templatefileName = orgDetails.getparsingTemplate();
		}
	    }
	    else {
		templatefileName = orgDetails.getparsingTemplate();
	    }
	}
	else {
	    templatefileName = orgDetails.getparsingTemplate();
	}
	
	if(!"".equals(templatefileName)) {
	    
	    //log batch activity
	    utUserActivity ua = new utUserActivity();
	    ua.setUserId(0);
	    ua.setFeatureId(0);
	    ua.setAccessMethod("System");
	    ua.setActivity("JSON Parsing Template found: " + directory + templatefileName);
	    ua.setBatchUploadId(batchId);
	    usermanager.insertUserLog(ua);
	    
	    URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file://" + directory + templatefileName)});
	    
	    // Remove the .class extension
	    Class cls = loader.loadClass(templatefileName.substring(0, templatefileName.lastIndexOf('.')));
	    Constructor constructor = cls.getConstructor();
	    
	    Object JSONObj = constructor.newInstance();
	    
	    Method myMethod = cls.getMethod("JSONtoTxt", String.class);
	    
	    /* Get the uploaded CCD File */
	    fileLocation = fileLocation.replace("/Applications/HELProductSuite/universalTranslator/", "").replace("/home/HELProductSuite/universalTranslator/", "").replace("/HELProductSuite/universalTranslator/", "");
	    directory = myProps.getProperty("ut.directory.utRootDir") + fileLocation;

	    File jsonFile = new File(directory + fileName + ".json");
	    
	    /* Create the txt file that will hold the CCD fields */
	    newfileName = new StringBuilder().append(jsonFile.getName().substring(0, jsonFile.getName().lastIndexOf("."))).append(".").append("txt").toString();

	    File newFile = new File(directory + newfileName);

	    if (newFile.exists()) {
		try {
		    if (newFile.exists()) {
			int i = 1;
			while (newFile.exists()) {
			    int iDot = newfileName.lastIndexOf(".");
			    newFile = new File(directory + newfileName.substring(0, iDot) + "_(" + ++i + ")" + newfileName.substring(iDot));
			}
			newfileName = newFile.getName();
			newFile.createNewFile();
		    } else {
			newFile.createNewFile();
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}

	    } else {
		newFile.createNewFile();
		newfileName = newFile.getName();
	    }

	    try {
		FileWriter fw = new FileWriter(newFile, true);
		
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(jsonFile));
		
		/* END */
		String fileRecords = (String) myMethod.invoke(JSONObj, obj.toString());
		if (fileRecords.equalsIgnoreCase("")) {
		    newfileName = "FILE IS NOT JSON ERROR";
		    
		    //log batch activity
		    ua = new utUserActivity();
		    ua.setUserId(0);
		    ua.setFeatureId(0);
		    ua.setAccessMethod("System");
		    ua.setActivity("Error loading the JSON Parsing Template: " + directory + templatefileName + " Error: fileRecords was empty.");
		    ua.setBatchUploadId(batchId);
		    usermanager.insertUserLog(ua);
		}
		fw.write(fileRecords);

		fw.close();
	    } catch (Exception ex) {
		ex.printStackTrace();
		newfileName = "ERRORERRORERROR";
		PrintStream ps = new PrintStream(newFile);
		ex.printStackTrace(ps);
		ps.close();
		
		//log batch activity
		ua = new utUserActivity();
		ua.setUserId(0);
		ua.setFeatureId(0);
		ua.setAccessMethod("System");
		ua.setActivity("Error loading the JSON Parsing Template: " + directory + templatefileName + " Error: " + ex.getMessage());
		ua.setBatchUploadId(batchId);
		usermanager.insertUserLog(ua);
	    }
	}
	else {
	    newfileName = "ERRORERRORERROR";
	    
	    //log batch activity
	    utUserActivity ua = new utUserActivity();
	    ua.setUserId(0);
	    ua.setFeatureId(0);
	    ua.setAccessMethod("System");
	    ua.setActivity("No JSON parsing template was set up for configId:"+configId);
	    ua.setBatchUploadId(batchId);
	    usermanager.insertUserLog(ua);
	}
        
        return newfileName;

    }

}
