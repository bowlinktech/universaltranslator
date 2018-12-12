/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.restAPI;

import com.hel.ut.dao.RestAPIDAO;
import com.hel.ut.dao.transactionOutDAO;
import com.hel.ut.model.RestAPIMessagesIn;
import com.hel.ut.model.RestAPIMessagesOut;
import com.hel.ut.model.User;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.configuration;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.mailMessage;
import com.hel.ut.reference.fileSystem;
import com.hel.ut.service.configurationManager;
import com.hel.ut.service.configurationTransportManager;
import com.hel.ut.service.emailMessageManager;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.transactionOutManager;
import com.hel.ut.service.userManager;
import com.hel.ut.service.zipFileManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author chadmccue
 */
@Service
public class restfulManager {

    @Autowired
    private transactionOutDAO transactionOutDAO;

    @Autowired
    private transactionOutManager transactionoutmanager;

    @Autowired
    private transactionInManager transactionInManager;

    @Autowired
    private configurationTransportManager configurationTransportManager;

    @Autowired
    private zipFileManager zipFileManager;

    @Autowired
    private RestAPIDAO RestAPIDAO;

    @Autowired
    private configurationManager configurationmanager;

    @Autowired
    private emailMessageManager emailManager;

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private userManager usermanager;

    @Resource(name = "myProps")
    private Properties myProps;

    /**
     * The 'sendOutJSONRestAPI' function will receive the request and send out the Rest API Call with the JSON Object
     *
     * @param batchId
     * @param transportDetails
     * @throws Exception
     */
    @Async
    public void sendOutJSONRestAPI(int batchId, configurationTransport transportDetails) throws Exception {
	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        
	try {

	    Integer batchStatusId = 0;
	    Integer transStatusId = 0;

	    boolean clearRecords = false;

	    if (transportDetails != null) {
		clearRecords = transportDetails.getclearRecords();
	    }

	    /* get the batch details */
	    batchDownloads batchFileInfo = transactionoutmanager.getBatchDetails(batchId);

	    //Get REST API Details
	    String restAPIURL = transportDetails.getRestAPIURL();
	    String restAPIUsername = transportDetails.getRestAPIUsername();
	    String restAPIPassword = transportDetails.getRestAPIPassword();

	    String fileName = null;

	    int findExt = batchFileInfo.getoutputFIleName().lastIndexOf(".");

	    if (findExt >= 0) {
		fileName = batchFileInfo.getoutputFIleName();
	    } else {
		fileName = new StringBuilder().append(batchFileInfo.getoutputFIleName()).append(".").append(transportDetails.getfileExt()).toString();
	    }
	    
	    //Submit the restAPImessageOut
	    RestAPIMessagesOut apiMessageOut = new RestAPIMessagesOut();
	    apiMessageOut.setConfigId(transportDetails.getconfigId());
	    apiMessageOut.setBatchDownloadId(batchId);
	    apiMessageOut.setOrgId(batchFileInfo.getOrgId());

	    //Set the directory to save the brochures to
	    fileSystem dir = new fileSystem();

	    String filelocation = transportDetails.getfileLocation();
	    filelocation = filelocation.replace("/HELProductSuite/universalTranslator/", "");
	    dir.setDirByName(filelocation);

	    File file = new File(dir.getDir() + fileName);

	    boolean sendEmail = false;

	    String responseMessage = "";
	    
	    if (file.exists()) {

		InputStream fileInput = new FileInputStream(file);

		BufferedReader reader = new BufferedReader(new InputStreamReader(fileInput));

		StringBuilder jsonContent = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
		    //line = line.trim().replace("\"","\\\"");
		    line = line.trim();
		    jsonContent.append(line);
		}
		
		//String jsonContentAsString = jsonContent.toString();
		String jsonContentAsString = jsonContent.toString().replace("\\","\\\\");
		//System.out.println(jsonContentAsString);
		
		apiMessageOut.setPayload("See generated target file for sent payload.");
		
		final ClientConfig config = new DefaultClientConfig();
		final Client client = Client.create(config);
		
		client.setConnectTimeout(120000);
		client.setReadTimeout(120000);
		client.addFilter(new HTTPBasicAuthFilter(restAPIUsername, restAPIPassword));

		WebResource webResource = client.resource(restAPIURL);
		
		try {
		    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, jsonContentAsString.replaceAll(",$", ""));
		    apiMessageOut.setResponseStatus(response.getStatus());
		    
		    StringBuilder apiResponse = new StringBuilder();
		    apiResponse.append("Status: ").append(response.getStatus()).append(System.getProperty("line.separator"));
		    apiResponse.append("Response: ").append(System.getProperty("line.separator"));
		    apiResponse.append(response.getEntity(String.class));

		    responseMessage = apiResponse.toString();
		    
		    apiMessageOut.setResponseMessage(responseMessage);
		    
		    jsonContentAsString = "";

		    if (response.getStatus() == 200) {
			sendEmail = true;

			if (transportDetails.isWaitForResponse()) {
			    batchStatusId = 59;
			} else {
			    batchStatusId = 28;
			}
			transStatusId = 20;
			apiMessageOut.setStatusId(2);
		    } else {
			batchStatusId = 58;
			transStatusId = 14;
			apiMessageOut.setStatusId(3);
		    }
		    
		    response.close();
		    client.destroy();
		} catch (ClientHandlerException | UniformInterfaceException ex) {
		   batchStatusId = 58;
		    transStatusId = 14;
		    apiMessageOut.setStatusId(3);
		    responseMessage = ex.getMessage();
		    apiMessageOut.setResponseMessage(responseMessage);
		    client.destroy();
		}

	    } else {
		transStatusId = 14;
		batchStatusId = 58;
		apiMessageOut.setResponseStatus(0);
		apiMessageOut.setStatusId(3);
		apiMessageOut.setResponseMessage("No File Sent because file (" + dir.getDir() + fileName + ") was not Found");
	    }
	    
	    transactionOutDAO.insertRestApiMessage(apiMessageOut);

	    transactionoutmanager.updateTargetBatchStatus(batchId, batchStatusId, "endDateTime");
	    
	    if(batchStatusId == 28) {
		//Delete all transaction target tables
		transactionInManager.deleteBatchTransactionTables(batchFileInfo.getBatchUploadId());
		transactionOutDAO.deleteBatchDownloadTables(batchId);
	    }
	   
	    if (sendEmail) {
		try {
		    //Check to see if we need to send out an email that the message was sent.
		    List<User> receivingusers = usermanager.getSuccessEmailReceiversForConfig(transportDetails.getconfigId());

		    //Check to see if we need to send out an email that the message was sent.
		    List<User> users = usermanager.getSuccessEmailSendersForConfig(transportDetails.getconfigId());

		    List<configurationConnection> connections = configurationmanager.getConnectionsByTargetConfiguration(transportDetails.getconfigId());

		    if (connections != null) {

			if (users != null || receivingusers != null) {

			    // Get the configuration details
			    configuration configDetails = configurationmanager.getConfigurationById(transportDetails.getconfigId());

			    String message = "";

			    if (configDetails != null) {
				//Get the sending org configuration details
				configuration sendingConfigDetails = configurationmanager.getConfigurationById(connections.get(0).getsourceConfigId());

				String sendingOrgName = "";

				if (sendingConfigDetails != null) {
				    sendingOrgName = organizationmanager.getOrganizationById(sendingConfigDetails.getorgId()).getOrgName();
				}

				//build message
				message = "A new file has been sent to: " + restAPIURL;
				message = message + "<br/><br/>Environment: " + myProps.getProperty("server.identity");

				message = message + "<br/><br/>Please see details below.";

				message = message + "<br/><br/>Transaction Type: " + configDetails.getconfigName();

				message = message + "<br/><br/>Sending Organization: " + sendingOrgName;

				message = message + "<br/><br/>Total Transactions: " + batchFileInfo.gettotalRecordCount();

				if (!"".equals(responseMessage)) {
				    message = message + "<br/><br/>API Response Message: " + responseMessage;
				}
			    }

			    boolean sentBcc = false;

			    if (!receivingusers.isEmpty()) {
				if (!"".equals(message)) {

				    mailMessage mail = new mailMessage();

				    mail.setfromEmailAddress(myProps.getProperty("admin.email"));

				    List<String> ccAddresses = new ArrayList<String>();

				    String toEmail = receivingusers.get(0).getEmail();
				    Integer firstUserId = receivingusers.get(0).getId();

				    if (receivingusers.size() > 1) {
					for (User user : receivingusers) {
					    if (!firstUserId.equals(user.getId())) {
						ccAddresses.add(user.getEmail());
					    }
					}
				    }

				    List<String> bccAddresses = new ArrayList<String>();
				    //bccAddresses.add("monitor@health-e-link.net");

				    mail.setmessageBody(message);
				    mail.setmessageSubject(configDetails.getconfigName() + " message submitted on the " + myProps.getProperty("server.identity") + " environment");
				    mail.settoEmailAddress(toEmail);

				    if (!ccAddresses.isEmpty()) {
					String[] ccEmailAddresses = new String[ccAddresses.size()];
					ccEmailAddresses = ccAddresses.toArray(ccEmailAddresses);
					mail.setccEmailAddress(ccEmailAddresses);
				    }

				    if (!bccAddresses.isEmpty()) {
					String[] bccEmailAddresses = new String[bccAddresses.size()];
					bccEmailAddresses = bccAddresses.toArray(bccEmailAddresses);
					mail.setBccEmailAddress(bccEmailAddresses);
				    }

				    sentBcc = true;

				    emailManager.sendEmail(mail);

				}
			    }

			    if (!users.isEmpty()) {

				if (!"".equals(message)) {

				    mailMessage mail = new mailMessage();

				    mail.setfromEmailAddress(myProps.getProperty("admin.email"));

				    List<String> ccAddresses = new ArrayList<String>();

				    String toEmail = users.get(0).getEmail();
				    Integer firstUserId = users.get(0).getId();

				    if (users.size() > 1) {
					for (User user : users) {
					    if (!firstUserId.equals(user.getId())) {
						ccAddresses.add(user.getEmail());
					    }
					}
				    }

				    List<String> bccAddresses = new ArrayList<String>();

				    if (!sentBcc) {
					//bccAddresses.add("monitor@health-e-link.net");
				    }

				    mail.setmessageBody(message);
				    mail.setmessageSubject(configDetails.getconfigName() + " message submitted on the " + myProps.getProperty("server.identity") + " environment");
				    mail.settoEmailAddress(toEmail);

				    if (!ccAddresses.isEmpty()) {
					String[] ccEmailAddresses = new String[ccAddresses.size()];
					ccEmailAddresses = ccAddresses.toArray(ccEmailAddresses);
					mail.setccEmailAddress(ccEmailAddresses);
				    }

				    if (!sentBcc) {
					if (!bccAddresses.isEmpty()) {
					    String[] bccEmailAddresses = new String[bccAddresses.size()];
					    bccEmailAddresses = bccAddresses.toArray(bccEmailAddresses);
					    mail.setBccEmailAddress(bccEmailAddresses);
					}
				    }

				    emailManager.sendEmail(mail);

				}
			    }
			}
		    }
		} catch (Exception ex) {

		}
	    }

	} catch (Exception e) {
	    throw new Exception("Error occurred trying to FTP a batch target. batchId: " + batchId, e);
	}

    }

    /**
     * The 'sendOutJSONFileRestAPI' function will receive the request and send out the Rest API Call with the JSON FIle
     *
     * @param batchId
     * @param transportDetails
     * @throws Exception
     */
    @Async
    public void sendOutJSONFileRestAPI(int batchId, configurationTransport transportDetails) throws Exception {

	try {

	    Integer batchStatusId = 0;
	    Integer transStatusId = 0;

	    boolean clearRecords = false;

	    if (transportDetails != null) {
		clearRecords = transportDetails.getclearRecords();
	    }

	    /* get the batch details */
	    batchDownloads batchFileInfo = transactionoutmanager.getBatchDetails(batchId);

	    //Get REST API Details
	    String restAPIURL = transportDetails.getRestAPIURL();
	    String restAPIUsername = transportDetails.getRestAPIUsername();
	    String restAPIPassword = transportDetails.getRestAPIPassword();

	    String fileName = null;

	    int findExt = batchFileInfo.getoutputFIleName().lastIndexOf(".");

	    if (findExt >= 0) {
		fileName = batchFileInfo.getoutputFIleName();
	    } else {
		fileName = new StringBuilder().append(batchFileInfo.getoutputFIleName()).append(".").append(transportDetails.getfileExt()).toString();
	    }

	    //Set the directory to save the brochures to
	    fileSystem dir = new fileSystem();

	    String filelocation = transportDetails.getfileLocation();
	    filelocation = filelocation.replace("/HELProductSuite/universalTranslator/", "");
	    dir.setDirByName(filelocation);

	    File file = new File(dir.getDir() + fileName);
	    File folder = new File(dir.getDir());

	    //Submit the restAPImessageOut
	    RestAPIMessagesOut apiMessageOut = new RestAPIMessagesOut();
	    apiMessageOut.setConfigId(transportDetails.getconfigId());
	    apiMessageOut.setBatchDownloadId(batchId);
	    apiMessageOut.setOrgId(batchFileInfo.getOrgId());

	    boolean sendEmail = false;

	    String responseMessage = "";

	    if (file.exists()) {
		boolean sendfile = false;

		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);

		if (transportDetails.isZipped()) {

		    String unzippedFile = dir.getDir() + fileName;

		    try {
			File zippedFile = zipFileManager.zipFile(folder, unzippedFile.replace("//", "/"), unzippedFile.replace("//", "/"), fileName, transportDetails.getZipType(), fileExt);

			if (zippedFile != null) {
			    file = zippedFile;
			    sendfile = true;
			}
		    } catch (Exception e) {
		    }

		} else {
		    sendfile = true;
		}

		apiMessageOut.setPayload("See generated target file for sent payload.");

		if (sendfile) {

		    final ClientConfig config = new DefaultClientConfig();
		    final Client client = Client.create(config);

		    client.setConnectTimeout(25000);
		    client.setReadTimeout(25000);
		    client.addFilter(new HTTPBasicAuthFilter(restAPIUsername, restAPIPassword));

		    if (transportDetails.isZipped()) {
			//GZIP
			if (transportDetails.getZipType() == 1) {
			    client.addFilter(new GZIPContentEncodingFilter(true));
			}
		    }

		    final WebResource resource = client.resource(restAPIURL);
		    
		    try {
			ClientResponse response = resource.type("application/xml").post(ClientResponse.class, file);

			Integer responseStatus = response.getStatus();

			responseMessage = response.getEntity(String.class);

			apiMessageOut.setResponseStatus(responseStatus);
			apiMessageOut.setResponseMessage(responseMessage);

			if (responseStatus == 200) {
			    sendEmail = true;

			    if (transportDetails.isWaitForResponse()) {
				batchStatusId = 59;
				transStatusId = 20;
				apiMessageOut.setStatusId(2);
			    } else {
				try {
				    String entityAsString = response.getEntity(String.class);

				    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				    InputSource src = new InputSource();
				    src.setCharacterStream(new StringReader(entityAsString));

				    Document doc = builder.parse(src);

				    String status = doc.getElementsByTagName("status").item(0).getTextContent();

				    if (status != null) {
					if ("success".equals(status.toLowerCase())) {
					    batchStatusId = 28;
					    transStatusId = 20;
					    apiMessageOut.setStatusId(2);
					} else {
					    batchStatusId = 58;
					    transStatusId = 14;
					    apiMessageOut.setStatusId(3);
					}
				    }
				} catch (ClientHandlerException | UniformInterfaceException | IOException | ParserConfigurationException | DOMException | SAXException ex) {
				    batchStatusId = 28;
				    transStatusId = 20;
				    apiMessageOut.setStatusId(2);
				}
			    }
			} else {
			    batchStatusId = 58;
			    transStatusId = 14;
			    apiMessageOut.setStatusId(3);
			}
			
			client.destroy();
		    
		    } catch (ClientHandlerException | UniformInterfaceException ex) {
			ex.printStackTrace();
			batchStatusId = 58;
			transStatusId = 14;
			apiMessageOut.setStatusId(3);
			responseMessage = ex.getMessage();
			apiMessageOut.setResponseMessage(responseMessage);
			client.destroy();
		    }
		} else {
		    transStatusId = 14;
		    batchStatusId = 58;
		    apiMessageOut.setResponseStatus(0);
		    apiMessageOut.setStatusId(3);
		    apiMessageOut.setResponseMessage("No File Sent because file was not Found");
		}
	    } else {
		transStatusId = 14;
		batchStatusId = 58;
		apiMessageOut.setResponseStatus(0);
		apiMessageOut.setStatusId(3);
		apiMessageOut.setResponseMessage("No File Sent because file (" + dir.getDir() + fileName + ") was not Found");
	    }

	    Integer restApiId = transactionOutDAO.insertRestApiMessage(apiMessageOut);

	    /* Update the downloaded batch transaction status */
	    transactionoutmanager.updateTargetBatchStatus(batchId, batchStatusId, "endDateTime");
	    
	    if(batchStatusId == 28) {
		//Delete all transaction target tables
		transactionInManager.deleteBatchTransactionTables(batchFileInfo.getBatchUploadId());
		transactionOutDAO.deleteBatchDownloadTables(batchId);
	    }
	    
	    if (sendEmail) {
		try {
		    //Check to see if we need to send out an email that the message was sent.
		    List<User> receivingusers = usermanager.getSuccessEmailReceiversForConfig(transportDetails.getconfigId());

		    //Check to see if we need to send out an email that the message was sent.
		    List<User> users = usermanager.getSuccessEmailSendersForConfig(transportDetails.getconfigId());

		    List<configurationConnection> connections = configurationmanager.getConnectionsByTargetConfiguration(transportDetails.getconfigId());

		    if (connections != null) {

			if (users != null || receivingusers != null) {

			    // Get the configuration details
			    configuration configDetails = configurationmanager.getConfigurationById(transportDetails.getconfigId());

			    String message = "";

			    if (configDetails != null) {
				//Get the sending org configuration details
				configuration sendingConfigDetails = configurationmanager.getConfigurationById(connections.get(0).getsourceConfigId());

				String sendingOrgName = "";

				if (sendingConfigDetails != null) {
				    sendingOrgName = organizationmanager.getOrganizationById(sendingConfigDetails.getorgId()).getOrgName();
				}

				//build message
				message = "A new file has been sent to: " + restAPIURL;
				message = message + "<br/><br/>Environment: " + myProps.getProperty("server.identity");

				message = message + "<br/><br/>Please see details below.";

				message = message + "<br/><br/>Transaction Type: " + configDetails.getconfigName();

				message = message + "<br/><br/>Sending Organization: " + sendingOrgName;

				message = message + "<br/><br/>Total Transactions: " + batchFileInfo.gettotalRecordCount();

				if (!"".equals(responseMessage)) {
				    message = message + "<br/><br/>API Response Message: " + responseMessage;
				}
			    }

			    boolean sentBcc = false;

			    if (!receivingusers.isEmpty()) {
				if (!"".equals(message)) {

				    mailMessage mail = new mailMessage();

				    mail.setfromEmailAddress(myProps.getProperty("admin.email"));

				    List<String> ccAddresses = new ArrayList<String>();

				    String toEmail = receivingusers.get(0).getEmail();
				    Integer firstUserId = receivingusers.get(0).getId();

				    if (receivingusers.size() > 1) {
					for (User user : receivingusers) {
					    if (!firstUserId.equals(user.getId())) {
						ccAddresses.add(user.getEmail());
					    }
					}
				    }

				    List<String> bccAddresses = new ArrayList<String>();
				    //bccAddresses.add("monitor@health-e-link.net");

				    mail.setmessageBody(message);
				    mail.setmessageSubject(configDetails.getconfigName() + " message submitted on the " + myProps.getProperty("server.identity") + " environment");
				    mail.settoEmailAddress(toEmail);

				    if (!ccAddresses.isEmpty()) {
					String[] ccEmailAddresses = new String[ccAddresses.size()];
					ccEmailAddresses = ccAddresses.toArray(ccEmailAddresses);
					mail.setccEmailAddress(ccEmailAddresses);
				    }

				    if (!bccAddresses.isEmpty()) {
					String[] bccEmailAddresses = new String[bccAddresses.size()];
					bccEmailAddresses = bccAddresses.toArray(bccEmailAddresses);
					mail.setBccEmailAddress(bccEmailAddresses);
				    }

				    sentBcc = true;

				    emailManager.sendEmail(mail);

				}
			    }

			    if (!users.isEmpty()) {

				if (!"".equals(message)) {

				    mailMessage mail = new mailMessage();

				    mail.setfromEmailAddress(myProps.getProperty("admin.email"));

				    List<String> ccAddresses = new ArrayList<String>();

				    String toEmail = users.get(0).getEmail();
				    Integer firstUserId = users.get(0).getId();

				    if (users.size() > 1) {
					for (User user : users) {
					    if (!firstUserId.equals(user.getId())) {
						ccAddresses.add(user.getEmail());
					    }
					}
				    }

				    List<String> bccAddresses = new ArrayList<String>();

				    if (!sentBcc) {
					//bccAddresses.add("monitor@health-e-link.net");
				    }

				    mail.setmessageBody(message);
				    mail.setmessageSubject(configDetails.getconfigName() + " message submitted on the " + myProps.getProperty("server.identity") + " environment");
				    mail.settoEmailAddress(toEmail);

				    if (!ccAddresses.isEmpty()) {
					String[] ccEmailAddresses = new String[ccAddresses.size()];
					ccEmailAddresses = ccAddresses.toArray(ccEmailAddresses);
					mail.setccEmailAddress(ccEmailAddresses);
				    }

				    if (!sentBcc) {
					if (!bccAddresses.isEmpty()) {
					    String[] bccEmailAddresses = new String[bccAddresses.size()];
					    bccEmailAddresses = bccAddresses.toArray(bccEmailAddresses);
					    mail.setBccEmailAddress(bccEmailAddresses);
					}
				    }

				    emailManager.sendEmail(mail);

				}
			    }
			}
		    }
		} catch (Exception ex) {

		}
	    }
	} catch (Exception e) {
	    throw new Exception("Error occurred trying to FTP a batch target. batchId: " + batchId, e);
	}
    }
    
    /**
     * The 'sendOutJSONRestAPIURL' function will receive the request and send out the Rest API Call via url paramenters
     *
     * @param batchId
     * @param transportDetails
     * @throws Exception
     */
    @Async
    public void sendOutJSONRestAPIURL(int batchId, configurationTransport transportDetails) throws Exception {
	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        
	try {

	    Integer batchStatusId = 0;
	    Integer transStatusId = 0;

	    boolean clearRecords = false;

	    if (transportDetails != null) {
		clearRecords = transportDetails.getclearRecords();
	    }

	   
	    /* get the batch details */
	    batchDownloads batchFileInfo = transactionoutmanager.getBatchDetails(batchId);

	    //Get REST API Details
	    String restAPIURL = transportDetails.getRestAPIURL();
	    String restAPIUsername = transportDetails.getRestAPIUsername();
	    String restAPIPassword = transportDetails.getRestAPIPassword();

	    String fileName = null;

	    int findExt = batchFileInfo.getoutputFIleName().lastIndexOf(".");

	    if (findExt >= 0) {
		fileName = batchFileInfo.getoutputFIleName();
	    } else {
		fileName = new StringBuilder().append(batchFileInfo.getoutputFIleName()).append(".").append(transportDetails.getfileExt()).toString();
	    }
	    
	    //Submit the restAPImessageOut
	    RestAPIMessagesOut apiMessageOut = new RestAPIMessagesOut();
	    apiMessageOut.setConfigId(transportDetails.getconfigId());
	    apiMessageOut.setBatchDownloadId(batchId);
	    apiMessageOut.setOrgId(batchFileInfo.getOrgId());

	    //Set the directory to save the brochures to
	    fileSystem dir = new fileSystem();

	    String filelocation = transportDetails.getfileLocation();
	    filelocation = filelocation.replace("/HELProductSuite/universalTranslator/", "");
	    dir.setDirByName(filelocation);

	    File file = new File(dir.getDir() + fileName);

	    boolean sendEmail = false;

	    String responseMessage = "";
	    
	    if (file.exists()) {

		InputStream fileInput = new FileInputStream(file);

		BufferedReader reader = new BufferedReader(new InputStreamReader(fileInput));
		String line;
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		
		
		while ((line = reader.readLine()) != null) {
		    line = line.trim();
		    
		    if(line.contains("|")) {
			String[] urlParams = line.split("\\|");
			if(urlParams.length > 0) {
			    for(String urlParam : urlParams) {
				queryParams.add("var", urlParam);
			    }
			}
		    }
		}
	
		if(!queryParams.isEmpty()) {
		    apiMessageOut.setPayload("See generated target file for sent payload.");
		
		    final ClientConfig config = new DefaultClientConfig();
		    final Client client = Client.create(config);

		    client.setConnectTimeout(120000);
		    client.setReadTimeout(120000);
		    client.addFilter(new HTTPBasicAuthFilter(restAPIUsername, restAPIPassword));

		    WebResource webResource = client.resource(restAPIURL);

		    try {
			ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);
			apiMessageOut.setResponseStatus(response.getStatus());
			
			StringBuilder apiResponse = new StringBuilder();
			apiResponse.append("Status: ").append(response.getStatus()).append(System.getProperty("line.separator"));
			apiResponse.append("Response: ").append(System.getProperty("line.separator"));
			apiResponse.append(response.getEntity(String.class));

			responseMessage = apiResponse.toString();

			apiMessageOut.setResponseMessage(responseMessage);

			if (response.getStatus() == 200) {
			    sendEmail = true;

			    if (transportDetails.isWaitForResponse()) {
				batchStatusId = 59;
			    } else {
				batchStatusId = 28;
			    }
			    transStatusId = 20;
			    apiMessageOut.setStatusId(2);
			} else {
			    batchStatusId = 58;
			    transStatusId = 14;
			    apiMessageOut.setStatusId(3);
			}

			client.destroy();
		    } catch (ClientHandlerException | UniformInterfaceException ex) {
			batchStatusId = 58;
			transStatusId = 14;
			apiMessageOut.setStatusId(3);
			responseMessage = ex.getMessage();
			apiMessageOut.setResponseMessage(responseMessage);
			client.destroy();
		    }
		}
 		
	    } else {
		transStatusId = 14;
		batchStatusId = 58;
		apiMessageOut.setResponseStatus(0);
		apiMessageOut.setStatusId(3);
		apiMessageOut.setResponseMessage("No File Sent because file (" + dir.getDir() + fileName + ") was not Found");
	    }
	    
	    transactionOutDAO.insertRestApiMessage(apiMessageOut);

	    transactionoutmanager.updateTargetBatchStatus(batchId, batchStatusId, "endDateTime");
	   
	    if (sendEmail) {
		try {
		    //Check to see if we need to send out an email that the message was sent.
		    List<User> receivingusers = usermanager.getSuccessEmailReceiversForConfig(transportDetails.getconfigId());

		    //Check to see if we need to send out an email that the message was sent.
		    List<User> users = usermanager.getSuccessEmailSendersForConfig(transportDetails.getconfigId());

		    List<configurationConnection> connections = configurationmanager.getConnectionsByTargetConfiguration(transportDetails.getconfigId());

		    if (connections != null) {

			if (users != null || receivingusers != null) {

			    // Get the configuration details
			    configuration configDetails = configurationmanager.getConfigurationById(transportDetails.getconfigId());

			    String message = "";

			    if (configDetails != null) {
				//Get the sending org configuration details
				configuration sendingConfigDetails = configurationmanager.getConfigurationById(connections.get(0).getsourceConfigId());

				String sendingOrgName = "";

				if (sendingConfigDetails != null) {
				    sendingOrgName = organizationmanager.getOrganizationById(sendingConfigDetails.getorgId()).getOrgName();
				}

				//build message
				message = "A new file has been sent to: " + restAPIURL;
				message = message + "<br/><br/>Environment: " + myProps.getProperty("server.identity");

				message = message + "<br/><br/>Please see details below.";

				message = message + "<br/><br/>Transaction Type: " + configDetails.getconfigName();

				message = message + "<br/><br/>Sending Organization: " + sendingOrgName;

				message = message + "<br/><br/>Total Transactions: " + batchFileInfo.gettotalRecordCount();

				if (!"".equals(responseMessage)) {
				    message = message + "<br/><br/>API Response Message: " + responseMessage;
				}
			    }

			    boolean sentBcc = false;

			    if (!receivingusers.isEmpty()) {
				if (!"".equals(message)) {

				    mailMessage mail = new mailMessage();

				    mail.setfromEmailAddress(myProps.getProperty("admin.email"));

				    List<String> ccAddresses = new ArrayList<String>();

				    String toEmail = receivingusers.get(0).getEmail();
				    Integer firstUserId = receivingusers.get(0).getId();

				    if (receivingusers.size() > 1) {
					for (User user : receivingusers) {
					    if (!firstUserId.equals(user.getId())) {
						ccAddresses.add(user.getEmail());
					    }
					}
				    }

				    List<String> bccAddresses = new ArrayList<String>();
				    //bccAddresses.add("monitor@health-e-link.net");

				    mail.setmessageBody(message);
				    mail.setmessageSubject(configDetails.getconfigName() + " message submitted on the " + myProps.getProperty("server.identity") + " environment");
				    mail.settoEmailAddress(toEmail);

				    if (!ccAddresses.isEmpty()) {
					String[] ccEmailAddresses = new String[ccAddresses.size()];
					ccEmailAddresses = ccAddresses.toArray(ccEmailAddresses);
					mail.setccEmailAddress(ccEmailAddresses);
				    }

				    if (!bccAddresses.isEmpty()) {
					String[] bccEmailAddresses = new String[bccAddresses.size()];
					bccEmailAddresses = bccAddresses.toArray(bccEmailAddresses);
					mail.setBccEmailAddress(bccEmailAddresses);
				    }

				    sentBcc = true;

				    emailManager.sendEmail(mail);

				}
			    }

			    if (!users.isEmpty()) {

				if (!"".equals(message)) {

				    mailMessage mail = new mailMessage();

				    mail.setfromEmailAddress(myProps.getProperty("admin.email"));

				    List<String> ccAddresses = new ArrayList<String>();

				    String toEmail = users.get(0).getEmail();
				    Integer firstUserId = users.get(0).getId();

				    if (users.size() > 1) {
					for (User user : users) {
					    if (!firstUserId.equals(user.getId())) {
						ccAddresses.add(user.getEmail());
					    }
					}
				    }

				    List<String> bccAddresses = new ArrayList<String>();

				    if (!sentBcc) {
					//bccAddresses.add("monitor@health-e-link.net");
				    }

				    mail.setmessageBody(message);
				    mail.setmessageSubject(configDetails.getconfigName() + " message submitted on the " + myProps.getProperty("server.identity") + " environment");
				    mail.settoEmailAddress(toEmail);

				    if (!ccAddresses.isEmpty()) {
					String[] ccEmailAddresses = new String[ccAddresses.size()];
					ccEmailAddresses = ccAddresses.toArray(ccEmailAddresses);
					mail.setccEmailAddress(ccEmailAddresses);
				    }

				    if (!sentBcc) {
					if (!bccAddresses.isEmpty()) {
					    String[] bccEmailAddresses = new String[bccAddresses.size()];
					    bccEmailAddresses = bccAddresses.toArray(bccEmailAddresses);
					    mail.setBccEmailAddress(bccEmailAddresses);
					}
				    }

				    emailManager.sendEmail(mail);

				}
			    }
			}
		    }
		} catch (Exception ex) {

		}
	    }

	} catch (Exception e) {
	    throw new Exception("Error occurred trying to FTP a batch target. batchId: " + batchId, e);
	}

    }

    public List<RestAPIMessagesIn> getRestAPIMessagesInList(Date fromDate, Date toDate, Integer fetchSize, String batchName) throws Exception {
	return RestAPIDAO.getRestAPIMessagesInList(fromDate, toDate, fetchSize, batchName);
    }

    public RestAPIMessagesIn getRestAPIMessagesIn(Integer messageId) throws Exception {
	return RestAPIDAO.getRestAPIMessagesIn(messageId);
    }
    
    public RestAPIMessagesIn getRestAPIMessagesInByBatchId(Integer batchId) throws Exception {
	return RestAPIDAO.getRestAPIMessagesInByBatchId(batchId);
    }

    public List<RestAPIMessagesOut> getRestAPIMessagesOutList(Date fromDate, Date toDate, Integer fetchSize, String batchName) throws Exception {
	return RestAPIDAO.getRestAPIMessagesOutList(fromDate, toDate, fetchSize, batchName);
    }

    public RestAPIMessagesOut getRestAPIMessagesOut(Integer messageId) throws Exception {
	return RestAPIDAO.getRestAPIMessagesOut(messageId);
    }

}
