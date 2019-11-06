/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service;

import com.hel.ut.model.Organization;
import com.hel.ut.model.configurationMessageSpecs;
import java.io.File;
import java.io.FileWriter;
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
public class CCDtoTxt {

    @Autowired
    private organizationManager organizationmanager;
    
    @Autowired
    private utConfigurationManager configurationManager;
    
    @Resource(name = "myProps")
    private Properties myProps;

    @Autowired
    private utConfigurationTransportManager configurationTransportManager;

    public String TranslateCCDtoTxt(String fileLocation, String ccdFileName, int orgId, int configId, String targetOrgName) throws Exception {
	
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
	    
	    URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file://" + directory + templatefileName)});
	    
	    Class cls = null;
	    
	    // Remove the .class extension
	    try {
		cls = loader.loadClass(templatefileName.substring(0, templatefileName.lastIndexOf('.')));
	    }
	    catch (Exception ex) {
		ex.printStackTrace();
	    }
	    
	    Constructor constructor = cls.getConstructor();
	    
	    Object CCDObj = constructor.newInstance();
	    
	    Method myMethod = cls.getMethod("CCDtoTxt", new Class[]{File.class, String.class});
	    
	    /* Get the uploaded CCD File */
	    fileLocation = fileLocation.replace("/Applications/HELProductSuite/universalTranslator/", "").replace("/home/HELProductSuite/universalTranslator/", "").replace("/HELProductSuite/universalTranslator/", "");
	    directory = myProps.getProperty("ut.directory.utRootDir") + fileLocation;
	    
	    File ccdFile = new File(directory + ccdFileName + ".xml");
	    
	    /* Create the txt file that will hold the CCD fields */
	    newfileName = new StringBuilder().append(ccdFile.getName().substring(0, ccdFile.getName().lastIndexOf("."))).append(".").append("txt").toString();

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
		
		String fileRecords = (String) myMethod.invoke(CCDObj, new Object[]{ccdFile, targetOrgName});
		if (fileRecords.equalsIgnoreCase("")) {
		    newfileName = "FILE IS NOT XML ERROR";
		}
		
		fw.write(fileRecords);

		fw.close();
	    } catch (Exception ex) {
		ex.printStackTrace();
		newfileName = "ERRORERRORERROR";
		PrintStream ps = new PrintStream(newFile);
		ex.printStackTrace(ps);
		ps.close();
	    }
	}
	else {
	    newfileName = "ERRORERRORERROR";
	}
        
        return newfileName;

    }

}
