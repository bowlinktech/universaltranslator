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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author chadmccue
 */
@Service
public class hl7toTxt {

    @Autowired
    private organizationManager organizationmanager;
    
    @Autowired
    private userManager usermanager;
    
    @Autowired
    private utConfigurationManager configurationManager;
    
    @Resource(name = "myProps")
    private Properties myProps;

    @Autowired
    private utConfigurationTransportManager configurationtransportmanager;

    public String TranslateHl7toTxt(String fileLocation, String fileName, int orgId, int configId, Integer batchId) throws Exception {

        Organization orgDetails = organizationmanager.getOrganizationById(orgId);

        String directory = myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/templates/";

        String templatefileName = "";
	String newfileName = "";
	
	if(configId > 0) {
	    configurationMessageSpecs configurationParsingScript = configurationManager.getMessageSpecs(configId);
	    
	    if(configurationParsingScript.getParsingTemplate() != null) {
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
	    ua.setActivity("HL7 Parsing Template found: " + directory + templatefileName);
	    ua.setBatchUploadId(batchId);
	    usermanager.insertUserLog(ua);
	    
	    URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file://" + directory + templatefileName)});

	    // Remove the .class extension
	    Class cls = loader.loadClass(templatefileName.substring(0, templatefileName.lastIndexOf('.')));

	    Constructor constructor = cls.getConstructor();

	    Object HL7Obj = constructor.newInstance();

	    Method myMethod = cls.getMethod("HL7toTxt", new Class[]{File.class});

	    /* Get the uploaded HL7 File */
	    fileLocation = fileLocation.replace("/Applications/HELProductSuite/universalTranslator/", "").replace("/home/HELProductSuite/universalTranslator/", "").replace("/HELProductSuite/universalTranslator/", "");
	    directory = myProps.getProperty("ut.directory.utRootDir") + fileLocation;

	    File hl7File = new File(directory + fileName + ".hr");

	    /* Create the output file */
	    newfileName = new StringBuilder().append(hl7File.getName().substring(0, hl7File.getName().lastIndexOf("."))).append(".").append("txt").toString();

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
		} catch (IOException e) {
		    e.printStackTrace();
		}

	    } else {
		newFile.createNewFile();
		newfileName = newFile.getName();

	    }
	    try {

		FileWriter fw = new FileWriter(newFile, true);

		String fileRecords = (String) myMethod.invoke(HL7Obj, new Object[]{hl7File});
		
		if (fileRecords.equalsIgnoreCase("")) {
		    newfileName = "ERRORERRORERROR";
		    
		    //log batch activity
		    ua = new utUserActivity();
		    ua.setUserId(0);
		    ua.setFeatureId(0);
		    ua.setAccessMethod("System");
		    ua.setActivity("Error loading the HL7 Parsing Template: " + directory + templatefileName + " Error: fileRecords was empty.");
		    ua.setBatchUploadId(batchId);
		    usermanager.insertUserLog(ua);
		}

		fw.write(fileRecords);

		fw.close();
	    } catch (Exception ex) {
		
		//log batch activity
		ua = new utUserActivity();
		ua.setUserId(0);
		ua.setFeatureId(0);
		ua.setAccessMethod("System");
		ua.setActivity("Error loading the HL7 Parsing Template: " + directory + templatefileName + " Error: " + ex.getMessage());
		ua.setBatchUploadId(batchId);
		usermanager.insertUserLog(ua);
		
		ex.printStackTrace();
		newfileName = "ERRORERRORERROR";
		PrintStream ps = new PrintStream(newFile);
		ex.printStackTrace(ps);
		ps.close();
	    }
	}
	else {
	    newfileName = "ERRORERRORERROR";
	    
	    //log batch activity
	    utUserActivity ua = new utUserActivity();
	    ua.setUserId(0);
	    ua.setFeatureId(0);
	    ua.setAccessMethod("System");
	    ua.setActivity("No HL7 parsing template was set up for configId:"+configId);
	    ua.setBatchUploadId(batchId);
	    usermanager.insertUserLog(ua);
	}
        
        return newfileName;
    }

}
