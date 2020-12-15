/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.restAPI;

import com.hel.ut.model.Organization;
import com.hel.ut.model.RestAPIMessagesIn;
import com.hel.ut.model.configurationFileDropFields;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.eReferralAPIAttachmentList;
import com.hel.ut.model.restMessageInfo;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.utConfigurationManager;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.hel.ut.service.utConfigurationTransportManager;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import org.apache.commons.io.FilenameUtils;

/**
 * hfr-facilities/
 *
 * @author chadmccue
 */
@RestController
@RequestMapping("/rest.api")
public class restful {
    
    @Autowired
    private utConfigurationTransportManager configurationtransportmanager;
    
    @Autowired
    private transactionInManager transactionInManager;
    
    @Autowired
    private utConfigurationManager configurationManager;
    
    @Autowired
    private organizationManager organizationManager;
    
    @Resource(name = "myProps")
    private Properties myProps;
    
    private String medAlliesUsername = "testtest";
    private String medAlliesPassword = "testtest";
    
   
    /**
     * /post/{apiCustomCall} GET Method. Throw an invalid request call. Call should be a POST
     * @param response
     * @param request 
     */
    @RequestMapping(value = "/post/JSON/{apiCustomCall}", method = RequestMethod.GET)
     public void consumePostAPICall(HttpServletResponse response,HttpServletRequest request) throws Exception {
	 
	JSONObject obj = new JSONObject();
	obj.put("status", new Integer(HttpServletResponse.SC_UNAUTHORIZED));
	 obj.put("Message", "Invalid Credentials");
	    
	response.setContentType("application/json");
	response.setCharacterEncoding("UTF-8");
	response.getWriter().write(obj.toString());
     }
     
	   
    /**
     * /post/{apiCustomCall} POST Method. Take the incoming api POST call and run checks to validate the call
     * with the passed in username and password.
     * 
     * @param apiCustomCall - custom organization api call (used to validate the call)
     * @param jsonSent - JSON body that will contain the actual payload
     * @param response - return a valid Http response depending on the validation results.
     * @param request  - request will hold the authorization parameters.
     */
    @RequestMapping(value = "/post/{apiCustomCall}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void consumePostAPICall(
	    @PathVariable("apiCustomCall") String apiCustomCall,
	    @RequestBody(required = false) String jsonSent,
	    HttpServletResponse response,
	    HttpServletRequest request) throws Exception {
	
	JSONObject obj = new JSONObject();
        
	String authorization = request.getHeader("Authorization");

	if (authorization != null) {
	    String base64Credentials = authorization.substring("Basic".length()).trim();
	    
	    String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
	    
	    if (!"".equals(credentials)) {

		final String[] credvalues = credentials.split(":", 2);
		
		if(credvalues.length == 2) {
		    if (!"".equals(apiCustomCall)) {
			
			configurationTransport transportDetails = customAPICallExists(request.getRequestURL().toString());
			
			//Make sure the custom api url exists
			if (transportDetails != null) {
			    if (!isUserAuthenticated(credvalues, request.getRequestURL().toString())) {
				
				obj.put("status", new Integer(HttpServletResponse.SC_UNAUTHORIZED));
				obj.put("Message", "Invalid Credentials");
			    } 
			    else {
				
				if(jsonSent == null) {
				    obj.put("status", new Integer(HttpServletResponse.SC_BAD_REQUEST));
				    obj.put("Message", "Empty Message");
				}
				else if(jsonSent.isEmpty()) {
				    obj.put("status", new Integer(HttpServletResponse.SC_BAD_REQUEST));
				    obj.put("Message", "Empty Message");
				}
				else {
				    //Save a copy of the sent JSON message
				    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
				    Date date = new Date();
				    String utRootDir = myProps.getProperty("ut.directory.utRootDir");
				    Path path = Paths.get(utRootDir + "archivesIn/restMessage-"+dateFormat.format(date)+".json");
				    Files.write(path, jsonSent.getBytes());

				    restMessageInfo envelopeInfo = null;
				    List<eReferralAPIAttachmentList> attachmentList = null;
				    
				    //Need to try and put JSON into restMessageInfo object
				    try {
					Configuration conf = Configuration.builder().mappingProvider(new JacksonMappingProvider()).jsonProvider(new JacksonJsonProvider()).build();
					TypeRef<restMessageInfo> type = new TypeRef<restMessageInfo>() {};
					envelopeInfo = JsonPath.using(conf).parse(jsonSent).read("$", type);
				    }
				    catch (Exception ex) {
					obj.put("status", new Integer(HttpServletResponse.SC_BAD_REQUEST));
					obj.put("Message", "Invalid JSON Payload!");
				    }
				    
				    if(envelopeInfo != null) {
					//Need to make sure there is an attachment array
					try {
					    Configuration conf = Configuration.builder().mappingProvider(new JacksonMappingProvider()).jsonProvider(new JacksonJsonProvider()).build();
					    TypeRef<List<eReferralAPIAttachmentList>> type = new TypeRef<List<eReferralAPIAttachmentList>>() {};
					    attachmentList = JsonPath.using(conf).parse(jsonSent).read("$.messageAttachmentList.*", type);    
					}
					catch (Exception ex) {
					    obj.put("status", new Integer(HttpServletResponse.SC_BAD_REQUEST));
					    obj.put("Message", "Invalid JSON Attachment List");
					}
				    }
				    
				    if(!attachmentList.isEmpty()) {
					//Find the CCDA in the attachment list (will be the biggest file)
					String attachmentContent = null;
					String attachmentTitle = "";
					Integer attachmentSize = 0;
					for(eReferralAPIAttachmentList attachment : attachmentList) {
					    if(Integer.parseInt(attachment.getAttachmentSize()) > attachmentSize) {
						attachmentContent = attachment.getAttachmentContent();
						attachmentSize = Integer.parseInt(attachment.getAttachmentSize());
						attachmentTitle = attachment.getAttachmentTitle();
					    }
					}
					
					if(attachmentContent != null) {
					    
					    if(FilenameUtils.getExtension(attachmentTitle).toLowerCase().equals(transportDetails.getfileExt().toLowerCase())) {
						
						utConfiguration configDetails = configurationManager.getConfigurationById(transportDetails.getconfigId());
						Organization orgDetails = organizationManager.getOrganizationById(configDetails.getorgId());
						
						//File Drop directory
						List<configurationFileDropFields> fileDropFields = configurationtransportmanager.getTransFileDropDetails(transportDetails.getId());
						
						String fileDropDir = orgDetails.getcleanURL() + "/input files/";
							
						for(configurationFileDropFields dropField : fileDropFields){
						    if(dropField.getMethod() == 1) {
							fileDropDir = dropField.getDirectory();
						    }
						}
						
						FileOutputStream fos = new FileOutputStream(utRootDir + fileDropDir + attachmentTitle);

						//Check if content is base64 encoded
						if(org.apache.commons.codec.binary.Base64.isBase64(attachmentContent.replace("\n", ""))) {
						    fos.write(org.apache.commons.codec.binary.Base64.decodeBase64(attachmentContent.replace("\n", "")));
						}
						else {
						    fos.write(attachmentContent.replace("\n", "").getBytes());
						}
						fos.close();

						Integer configId = transportDetails.getconfigId();
						Integer statusId = 1;
						String sendingResponse = "Successfully received and saved your message.";
						
						//Create new restAPIMessage
						RestAPIMessagesIn newRestAPIMessage = new RestAPIMessagesIn();
						newRestAPIMessage.setOrgId(configDetails.getorgId());
						newRestAPIMessage.setArchiveFileName("archivesIn/"+dateFormat.format(date)+".json");
						newRestAPIMessage.setStatusId(1);
						newRestAPIMessage.setConfigId(configId);
						newRestAPIMessage.setMessageTitle(attachmentTitle);

						Integer newRestAPIMessageID = transactionInManager.insertRestApiMessage(newRestAPIMessage);

						if(newRestAPIMessageID > 0 && statusId == 1) {
						    obj.put("status", new Integer(HttpServletResponse.SC_OK));
						    obj.put("Message", "Successfully received and processed your message.");
						    
						    //Call the method to start processing this message immediately
						    transactionInManager.processRestAPIMessages();
						}
						else {
						   obj.put("status", new Integer(HttpServletResponse.SC_EXPECTATION_FAILED));
						   obj.put("Message", "Failed to process your message.");
						}

					    }
					    else {
						obj.put("status", new Integer(HttpServletResponse.SC_BAD_REQUEST));
						obj.put("Message", "Received attachment extension (" + FilenameUtils.getExtension(attachmentTitle).toLowerCase()+ ") does not match the expected file extension - .");
					    }
					}
					else {
					    obj.put("status", new Integer(HttpServletResponse.SC_BAD_REQUEST));
					    obj.put("Message", "Missing message attachments.");
					}
				    }
				    else {
					obj.put("status", new Integer(HttpServletResponse.SC_BAD_REQUEST));
					obj.put("Message", "sending rest message is invalid");
				    }
				}
			    }
			} else {
			    obj.put("status", new Integer(HttpServletResponse.SC_BAD_REQUEST));
			    obj.put("Message", "Bad Request");
			    //response.setStatus((HttpServletResponse.SC_BAD_REQUEST));
			}
		    } else {
			obj.put("status", new Integer(HttpServletResponse.SC_BAD_REQUEST));
			obj.put("Message", "Bad Request");
			//response.setStatus((HttpServletResponse.SC_BAD_REQUEST));
		    }
		} else {
		    obj.put("status", new Integer(HttpServletResponse.SC_UNAUTHORIZED));
		    obj.put("Message", "Invalid Credentials");
		    //response.setStatus((HttpServletResponse.SC_UNAUTHORIZED));
		}
	    } else {
		obj.put("status", new Integer(HttpServletResponse.SC_UNAUTHORIZED));
		obj.put("Message", "Invalid Credentials");
		//response.setStatus((HttpServletResponse.SC_UNAUTHORIZED));
	    }
	} else {
	    obj.put("status", new Integer(HttpServletResponse.SC_UNAUTHORIZED));
	    obj.put("Message", "Invalid Credentials");
	    //response.setStatus((HttpServletResponse.SC_UNAUTHORIZED));
	}
	
	response.setContentType("application/json");
	response.setCharacterEncoding("UTF-8");
	response.getWriter().write(obj.toString());
    }

    /**
     * The 'customAPICallExists' function will check that the apiCustomCall exists for a configuration
     * in our system. If confirmed it will return the configuration transport.
     * 
     * @param apiCustomCall - custom api call passed in from the organization
     * @return 
     */
    private configurationTransport customAPICallExists(String apiCustomCall) throws Exception {
	
	configurationTransport transportDetails = configurationtransportmanager.validateAPICall(apiCustomCall);
	
	if(transportDetails != null) {
	    if(transportDetails.getId() > 0) {
		return transportDetails;
	    }
	    else {
		return null;
	    }
	}
	else {
	    return null;
	}
    }

    /**
     * The 'isUserAuthenticated' function will verify the authorization parameters passed in with the 
     * the custom api call. This will validate the api call and continue to process the message
     * 
     * @param credvalues - api passed in authorization parameters.
     * @param apiCustomCall - custom api call to validate the authrorization
     * @return 
     */
    private boolean isUserAuthenticated(String[] credvalues, String apiCustomCall) throws Exception {
	
	boolean userAuthenticated = false;
	
	configurationTransport transportDetails = configurationtransportmanager.validateAPIAuthentication(credvalues,apiCustomCall);

	if(transportDetails != null) {
	    if(transportDetails.getId() > 0) {
		userAuthenticated = true;
	    }
	}
	
	return userAuthenticated;

    }

}
