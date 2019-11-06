/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.restAPI;

import com.hel.ut.dao.RestAPIDAO;
import com.hel.ut.dao.transactionOutDAO;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchUploads;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.directmessagesout;
import com.hel.ut.model.hisps;
import com.hel.ut.service.emailMessageManager;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.transactionOutManager;
import com.hel.ut.service.userManager;
import com.hel.ut.service.zipFileManager;
import java.io.File;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author chadmccue
 */
@Service
public class directManager {

    @Autowired
    private transactionOutDAO transactionOutDAO;

    @Autowired
    private transactionOutManager transactionoutmanager;

    @Autowired
    private transactionInManager transactionInManager;

    @Autowired
    private utConfigurationTransportManager configurationTransportManager;

    @Autowired
    private zipFileManager zipFileManager;

    @Autowired
    private RestAPIDAO RestAPIDAO;

    @Autowired
    private utConfigurationManager configurationmanager;

    @Autowired
    private emailMessageManager emailManager;

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private userManager usermanager;

    @Resource(name = "myProps")
    private Properties myProps;

    /**
     * 
     * @param batchDownloadId
     * @param transportDetails
     * @param hispDetails
     * @throws Exception 
     */
    @Async
    public void senddirectOutmedallies(Integer batchDownloadId, configurationTransport transportDetails, hisps hispDetails) throws Exception {
	try {
	    boolean clearRecords = false;

	    if (transportDetails != null) {
		clearRecords = transportDetails.getclearRecords();
	    }
	    
	    // get the batch download details
	    batchDownloads batchDownloadDetails = transactionoutmanager.getBatchDetails(batchDownloadId);
	    
	    //get the associated batch upload details
	    batchUploads batchUploadDetails = transactionInManager.getBatchDetails(batchDownloadDetails.getBatchUploadId());
	    
	    String directAPIURL = hispDetails.getHispAPIURL();
	    String directAPIUsername = hispDetails.getHispAPIUsername();
	    String directAPIPassword = hispDetails.getHispAPIPassword();
	    
	    String fileName = null;

	    int findExt = batchDownloadDetails.getOutputFileName().lastIndexOf(".");

	    if (findExt >= 0) {
		fileName = batchDownloadDetails.getOutputFileName();
	    } else {
		fileName = new StringBuilder().append(batchDownloadDetails.getOutputFileName()).append(".").append(transportDetails.getfileExt()).toString();
	    }
	    
	    //Submit the restAPImessageOut
	    directmessagesout directMessageOut = new directmessagesout();
	    directMessageOut.setConfigId(transportDetails.getconfigId());
	    directMessageOut.setBatchDownloadId(batchDownloadId);
	    directMessageOut.setBatchUploadId(batchDownloadDetails.getBatchUploadId());
	    directMessageOut.setOrgId(batchDownloadDetails.getOrgId());
	    directMessageOut.setOutputFileName(batchDownloadDetails.getOutputFileName());
	    directMessageOut.setFromDirectAddress(batchUploadDetails.getRecipientEmail());
	    directMessageOut.setToDirectAddress(batchUploadDetails.getSenderEmail());
	    directMessageOut.setHispId(hispDetails.getId());

	    String filelocation = transportDetails.getfileLocation();
	    filelocation = filelocation.replace("/HELProductSuite/universalTranslator/", "");
	    
	    File file = new File(myProps.getProperty("ut.directory.utRootDir") + filelocation + fileName);
	   
	    String responseMessage = "";
	    
	    if (file.exists()) {
		
		InputStream fileInput = new FileInputStream(file);

		BufferedReader reader = new BufferedReader(new InputStreamReader(fileInput));
		
		System.out.println("Send back to direct address: " + batchUploadDetails.getSenderEmail());
		System.out.println("Send from direct address: " + batchUploadDetails.getRecipientEmail());
		
		String line;
		while ((line = reader.readLine()) != null) {
		    System.out.println(line);
		}
	    }
	    
	     transactionOutDAO.insertDMMessage(directMessageOut);
	    
	}
	catch (Exception e) {
	    throw new Exception("Error occurred trying to send out a direct message to MedAllies. batch Download Id: " + batchDownloadId, e);
	}
    }
    

}
