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
import com.hel.ut.service.utilManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
    
    @Autowired
    private utilManager utilmanager;

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
	    
	    Integer batchStatusId = 0;

	    if (transportDetails != null) {
		clearRecords = transportDetails.getclearRecords();
	    }
	    
	    // get the batch download details
	    batchDownloads batchDownloadDetails = transactionoutmanager.getBatchDetails(batchDownloadId);
	    
	    //get the associated batch upload details
	    batchUploads batchUploadDetails = transactionInManager.getBatchDetails(batchDownloadDetails.getBatchUploadId());
	    
	    Integer batchUploadId = batchUploadDetails.getId();
	    
	    if(batchUploadDetails.getAssociatedBatchId() > 0) {
		batchUploadDetails = transactionInManager.getBatchDetails(batchUploadDetails.getAssociatedBatchId());
	    }
	    
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
		
		String line;
		StringBuilder jsonContent = new StringBuilder();
		while ((line = reader.readLine()) != null) {
		    line = line.trim();
		    jsonContent.append(line);
		}
		
		String jsonContentAsString = jsonContent.toString().replace("\\","\\\\");
		String encodedContent = utilmanager.encodeStringToBase64Binary(jsonContentAsString);
		
		JSONArray emailAttachmentListArray = new JSONArray();
		
		JSONObject emailAttachmentListObject = new JSONObject();
		emailAttachmentListObject.put("attachmentClass","text/xml");
		emailAttachmentListObject.put("attachmentContent", encodedContent);
		emailAttachmentListObject.put("attachmentTitle",batchDownloadDetails.getOutputFileName().replaceAll("\\|", ""));
		
		emailAttachmentListArray.add(emailAttachmentListObject);
		
		JSONObject jsonObjectToSend = new JSONObject();
		jsonObjectToSend.put("emailAttachmentList", emailAttachmentListArray);
		
		JSONObject envelopeInfoObject = new JSONObject();
		
		UUID uuid = UUID.randomUUID();
		
		envelopeInfoObject.put("fromDirectAddress",batchUploadDetails.getRecipientEmail());
		envelopeInfoObject.put("messageId","urn:uuid:"+uuid);
		envelopeInfoObject.put("toDirectAddress",batchUploadDetails.getSenderEmail());
		
		jsonObjectToSend.put("envelopeInfo", envelopeInfoObject);
		
		final ClientConfig config = new DefaultClientConfig();
		final Client client = Client.create(config);
		
		client.setConnectTimeout(120000);
		client.setReadTimeout(120000);
		client.addFilter(new HTTPBasicAuthFilter(hispDetails.getHispAPIUsername(), hispDetails.getHispAPIPassword()));

		WebResource webResource = client.resource(hispDetails.getHispAPIURL());
		
		try {
		    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, jsonObjectToSend);
		    directMessageOut.setResponseStatus(response.getStatus());
		    
		    StringBuilder apiResponse = new StringBuilder();
		    apiResponse.append("Status: ").append(response.getStatus()).append(System.getProperty("line.separator"));
		    apiResponse.append("Response: ").append(System.getProperty("line.separator"));
		    apiResponse.append(response.getEntity(String.class)); 
		    
		    responseMessage = apiResponse.toString();
		    
		    directMessageOut.setResponseMessage(responseMessage);
		    
		    jsonContentAsString = "";

		    if (response.getStatus() == 200) {
			if (transportDetails.isWaitForResponse()) {
			    batchStatusId = 59;
			} else {
			    batchStatusId = 28;
			}
			directMessageOut.setStatusId(2);
		    } else {
			batchStatusId = 58;
			directMessageOut.setStatusId(3);
		    }
		    
		    response.close();
		    client.destroy();
		} 
		catch (ClientHandlerException | UniformInterfaceException ex) {
		    batchStatusId = 58;
		    directMessageOut.setStatusId(3);
		    responseMessage = ex.getMessage();
		    directMessageOut.setResponseMessage(responseMessage);
		    client.destroy();
		}
		
	    }
	    else {
		batchStatusId = 58;
		directMessageOut.setResponseStatus(0);
		directMessageOut.setStatusId(3);
		directMessageOut.setResponseMessage("No File Sent because file (" + myProps.getProperty("ut.directory.utRootDir") + filelocation + fileName + ") was not Found");
	    }
	    
	    transactionoutmanager.updateTargetBatchStatus(batchDownloadId, batchStatusId, "endDateTime");
	    transactionOutDAO.insertDMMessage(directMessageOut);
	    
	    if(batchStatusId == 28) {
		//Delete all transaction target tables
		transactionInManager.deleteBatchTransactionTables(batchUploadId);
		transactionOutDAO.deleteBatchDownloadTables(batchDownloadId);
	    }
	    
	}
	catch (Exception e) {
	    System.out.println(e.getMessage());
	    throw new Exception("Error occurred trying to send out a direct message to MedAllies. batch Download Id: " + batchDownloadId, e);
	}
    }
    

}
