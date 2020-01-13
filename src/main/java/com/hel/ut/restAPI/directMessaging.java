/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.restAPI;

import com.hel.ut.model.Organization;
import com.hel.ut.model.configurationFileDropFields;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.directmessagesin;
import com.hel.ut.model.hisps;
import com.hel.ut.model.eReferralAPIAttachmentList;
import com.hel.ut.model.eReferralAPIInfo;
import com.hel.ut.model.organizationDirectDetails;
import com.hel.ut.service.hispManager;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.transactionInManager;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/rest.api/direct")
public class directMessaging {
    
    @Autowired
    private utConfigurationTransportManager configurationtransportmanager;
    
    @Autowired
    private transactionInManager transactionInManager;
    
    @Autowired
    private hispManager hispmanager;
    
    @Autowired
    private organizationManager organizationmanager;
    
    @Resource(name = "myProps")
    private Properties myProps;
    
    @PostMapping("/post/ereferral")
    public ResponseEntity<?> consumeDirectMessageReferral(@RequestBody(required = false) String jsonSent,HttpServletResponse response,HttpServletRequest request) throws Exception {
        
        String authorization = request.getHeader("Authorization");
        
        if (authorization != null) {
	    String base64Credentials = authorization.substring("Basic".length()).trim();
	    
	    String credentials = new String(Base64.decodeBase64(base64Credentials), Charset.forName("UTF-8"));
	    
	    if (!"".equals(credentials)) {

		final String[] credvalues = credentials.split(":", 2);
		
		if(credvalues.length == 2) {
                    
                    if(jsonSent == null) {
                        return new ResponseEntity("Invalid JSON Payload!", HttpStatus.BAD_REQUEST);
                    }
                    else if(jsonSent.isEmpty()) {
                        return new ResponseEntity("Invalid JSON Payload!", HttpStatus.BAD_REQUEST);
                    }
                    else {
                        //Save a copy of the sent JSON message
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
                        Date date = new Date();
			
			String originalDirectMessageName = dateFormat.format(date)+".json";
                        String utRootDir = myProps.getProperty("ut.directory.utRootDir");
                        Path path = Paths.get(utRootDir + "medAlliesArchives/"+originalDirectMessageName);
                        Files.write(path, jsonSent.getBytes());
                        
                        eReferralAPIInfo messageInfo = null;
                        List<eReferralAPIAttachmentList> attachmentList = null;
                        
                        //Need to try and put JSON into medallies object
                        try {
                            Configuration conf = Configuration.builder().mappingProvider(new JacksonMappingProvider()).jsonProvider(new JacksonJsonProvider()).build();
                            TypeRef<eReferralAPIInfo> type = new TypeRef<eReferralAPIInfo>() {};
                            messageInfo = JsonPath.using(conf).parse(jsonSent).read("$", type);
                        }
                        catch (Exception ex) {
                            //Need to send email of error
                            return new ResponseEntity("Invalid JSON Payload!", HttpStatus.BAD_REQUEST);
                        }
                        
                        if(messageInfo != null) {
                            //Need to make sure there is an attachment array
                            try {
                                Configuration conf = Configuration.builder().mappingProvider(new JacksonMappingProvider()).jsonProvider(new JacksonJsonProvider()).build();
                                TypeRef<List<eReferralAPIAttachmentList>> type = new TypeRef<List<eReferralAPIAttachmentList>>() {};
                                attachmentList = JsonPath.using(conf).parse(jsonSent).read("$.messageAttachmentList.*", type);    
                            }
                            catch (Exception ex) {
                                //Need to send email of error
                                return new ResponseEntity("Invalid JSON Attachment List!", HttpStatus.BAD_REQUEST);
                            }
			    
			    //Need to create an entry in the received Direct Messages table
			    directmessagesin newDirectMessageIn = new directmessagesin();
			    newDirectMessageIn.setArchiveFileName("archivesIn/"+dateFormat.format(date)+".json");
			    newDirectMessageIn.setConfigId(0);
			    newDirectMessageIn.setFromDirectAddress(messageInfo.getFromDirectAddress());
			    newDirectMessageIn.setToDirectAddress(messageInfo.getToDirectAddress());
			    newDirectMessageIn.setHispId(0);
			    newDirectMessageIn.setStatusId(3);
			    newDirectMessageIn.setReferralFileName("");
			    newDirectMessageIn.setOrgId(0);
			    newDirectMessageIn.setSendingResponse("");
			    newDirectMessageIn.setOriginalDirectMessage(originalDirectMessageName);

			    Integer newDMMessageID = transactionInManager.insertDMMessage(newDirectMessageIn);

			    directmessagesin directMessageDetails = transactionInManager.getDirectAPIMessagesById(newDMMessageID);
                            
                            if(!attachmentList.isEmpty()) {
				
                                if(messageInfo.getToDirectAddress().contains("@")) {
                                
                                    if(messageInfo.getFromDirectAddress().contains("@")) {
					
                                        String DMDomain = messageInfo.getFromDirectAddress().substring(messageInfo.getFromDirectAddress().indexOf("@")+1,messageInfo.getFromDirectAddress().length());

                                        organizationDirectDetails directDetails = configurationtransportmanager.getDirectMessagingDetails(DMDomain);

                                        if(directDetails != null) {
                                            Organization orgDetails = organizationmanager.getOrganizationById(directDetails.getOrgId());

                                            if (!validateHISPCredentials(credvalues, directDetails)) {
						directMessageDetails.setOrgId(directDetails.getOrgId());
						directMessageDetails.setHispId(directDetails.getHispId());
						directMessageDetails.setSendingResponse("Invalid Credentials");
						transactionInManager.updateDirectAPIMessage(directMessageDetails);
						
                                                return new ResponseEntity("Invalid Credentials!", HttpStatus.UNAUTHORIZED);
                                            } 
                                            else {
                                                //Find the CCDA in the attachment list (will be the biggest file)
                                                String CCDAContent = null;
                                                String CCDATitle = "";
                                                Integer attachmentSize = 0;
                                                for(eReferralAPIAttachmentList attachment : attachmentList) {
                                                    if(Integer.parseInt(attachment.getAttachmentSize()) > attachmentSize) {
                                                        CCDAContent = attachment.getAttachmentContent();
                                                        attachmentSize = Integer.parseInt(attachment.getAttachmentSize());
                                                        CCDATitle = attachment.getAttachmentTitle();
                                                    }
                                                }

                                                if(CCDAContent != null) {
                                                    if(FilenameUtils.getExtension(CCDATitle).toLowerCase().equals(directDetails.getExpectedFileExt().toLowerCase())) {
							
							//Find the configuration by dm keyword from target direct address
							configurationTransport transportDetails = configurationtransportmanager.findConfigurationByDirectMessagKeyword(directDetails.getOrgId(), messageInfo.getToDirectAddress());
							    
							//File Drop directory
							List<configurationFileDropFields> fileDropFields = configurationtransportmanager.getTransFileDropDetails(transportDetails.getId());

							String fileDropDir = orgDetails.getcleanURL() + "/input files/";

							for(configurationFileDropFields dropField : fileDropFields){
							    if(dropField.getMethod() == 1) {
								fileDropDir = dropField.getDirectory();
							    }
							}
							
							FileOutputStream fos = new FileOutputStream(utRootDir + fileDropDir.replace("/HELProductSuite/universalTranslator/", "") + CCDATitle);
							
							//Check if content is base64 encoded
							if(Base64.isBase64(CCDAContent.replace("\n", ""))) {
							    fos.write(Base64.decodeBase64(CCDAContent.replace("\n", "")));
							}
							else {
							    fos.write(CCDAContent.replace("\n", "").getBytes());
							}
                                                        fos.close();
                                                        
                                                        Integer configId = 0;
							Integer statusId = 1;
							String sendingResponse = "Successfully received and saved your message.";
                                                        
                                                        //Check how we will find the configuration
                                                        if(directDetails.getDmFindConfig() == 1) {
                                                            
							    if(transportDetails != null) {
								configId = transportDetails.getconfigId();
							    }
							    else {
								statusId = 3;
								sendingResponse = "Failed to find configuration. OrgId: " + directDetails.getOrgId() + "; To Direct Address: " + messageInfo.getToDirectAddress();
							    }
                                                        }
							
                                                        
                                                        if(newDMMessageID > 0 && statusId == 1) {
							    
							    JSONObject responseObject = new JSONObject();
							    if(!"".equals(messageInfo.getMessageId())) {
								responseObject.put("messageId",messageInfo.getMessageId());
							    }
							    else {
								responseObject.put("messageId","N/A");
							    }
							    responseObject.put("status","SUCCESS");
							    responseObject.put("result","Successfully received and processed your message.");
							    
							    //Need to create an entry in the received Direct Messages table
							    directMessageDetails.setConfigId(configId);
							    directMessageDetails.setOrgId(directDetails.getOrgId());
							    directMessageDetails.setHispId(directDetails.getHispId());
							    directMessageDetails.setReferralFileName(CCDATitle);
							    directMessageDetails.setStatusId(statusId);
							    directMessageDetails.setSendingResponse(sendingResponse);
							    transactionInManager.updateDirectAPIMessage(directMessageDetails);
							    
							    //Call the method to start processing this message immediately
							    transactionInManager.processDirectAPIMessages();
							    
                                                            return new ResponseEntity(responseObject, HttpStatus.OK);
							    
                                                        }
                                                        else {
							    JSONObject responseObject = new JSONObject();
							    if(!"".equals(messageInfo.getMessageId())) {
								responseObject.put("messageId",messageInfo.getMessageId());
							    }
							    else {
								responseObject.put("messageId","N/A");
							    }
							    responseObject.put("status","FAILED");
							    responseObject.put("result","Failed to process your message.");
							    
							    //Need to create an entry in the received Direct Messages table
							    directMessageDetails.setConfigId(configId);
							    directMessageDetails.setOrgId(directDetails.getOrgId());
							    directMessageDetails.setHispId(directDetails.getHispId());
							    directMessageDetails.setReferralFileName(CCDATitle);
							    directMessageDetails.setSendingResponse(sendingResponse);
							    transactionInManager.updateDirectAPIMessage(directMessageDetails);
							    
                                                            return new ResponseEntity(responseObject, HttpStatus.EXPECTATION_FAILED);
                                                        }

                                                    }
                                                    else {
							JSONObject responseObject = new JSONObject();
							if(!"".equals(messageInfo.getMessageId())) {
							    responseObject.put("messageId",messageInfo.getMessageId());
							}
							else {
							    responseObject.put("messageId","N/A");
							}
							responseObject.put("status","FAILED");
							responseObject.put("result","Received attachment extension (" + FilenameUtils.getExtension(CCDATitle).toLowerCase()+ ") does not match the expected file extension - ." + directDetails.getExpectedFileExt());
                                                        
							directMessageDetails.setOrgId(directDetails.getOrgId());
							directMessageDetails.setHispId(directDetails.getHispId());
							directMessageDetails.setSendingResponse("Received attachment extension (" + FilenameUtils.getExtension(CCDATitle).toLowerCase()+ ") does not match the expected file extension - ." + directDetails.getExpectedFileExt());
							transactionInManager.updateDirectAPIMessage(directMessageDetails);
							
							return new ResponseEntity(responseObject, HttpStatus.BAD_REQUEST);
                                                    }
                                                }
                                                else {
						    JSONObject responseObject = new JSONObject();
						    if(!"".equals(messageInfo.getMessageId())) {
							responseObject.put("messageId",messageInfo.getMessageId());
						    }
						    else {
							responseObject.put("messageId","N/A");
						    }
						    responseObject.put("status","FAILED");
						    responseObject.put("result","missing message attachments");
						    
						    directMessageDetails.setOrgId(directDetails.getOrgId());
						    directMessageDetails.setHispId(directDetails.getHispId());
						    directMessageDetails.setSendingResponse("missing message attachments");
						    transactionInManager.updateDirectAPIMessage(directMessageDetails);
						    
                                                    return new ResponseEntity(responseObject, HttpStatus.BAD_REQUEST);
                                                }
                                            }
                                        }
                                        else {
					    JSONObject responseObject = new JSONObject();
					    if(!"".equals(messageInfo.getMessageId())) {
						responseObject.put("messageId",messageInfo.getMessageId());
					    }
					    else {
						responseObject.put("messageId","N/A");
					    }
					    responseObject.put("status","FAILED");
					    responseObject.put("result","sending direct message is invalid - " + messageInfo.getFromDirectAddress());
					    
					    directMessageDetails.setSendingResponse("sending direct message is invalid - " + messageInfo.getFromDirectAddress());
					    transactionInManager.updateDirectAPIMessage(directMessageDetails);
					    
                                            return new ResponseEntity(responseObject, HttpStatus.BAD_REQUEST);
                                        }
                                    }
                                    else {
					JSONObject responseObject = new JSONObject();
					if(!"".equals(messageInfo.getMessageId())) {
					    responseObject.put("messageId",messageInfo.getMessageId());
					}
					else {
					    responseObject.put("messageId","N/A");
					}
					responseObject.put("status","FAILED");
					responseObject.put("result","sending direct message is invalid - " + messageInfo.getFromDirectAddress());
					
					directMessageDetails.setSendingResponse("sending direct message is invalid - " + messageInfo.getFromDirectAddress());
					transactionInManager.updateDirectAPIMessage(directMessageDetails);
					
                                        return new ResponseEntity(responseObject, HttpStatus.BAD_REQUEST);
                                    }
                                }
                                else {
				    JSONObject responseObject = new JSONObject();
				    if(!"".equals(messageInfo.getMessageId())) {
					responseObject.put("messageId",messageInfo.getMessageId());
				    }
				    else {
					responseObject.put("messageId","N/A");
				    }
				    responseObject.put("status","FAILED");
				    responseObject.put("result","recipient direct message is invalid - " + messageInfo.getToDirectAddress());
				    
				    directMessageDetails.setSendingResponse("recipient direct message is invalid - " + messageInfo.getToDirectAddress());
				    transactionInManager.updateDirectAPIMessage(directMessageDetails);
				    
                                    return new ResponseEntity("recipient direct message is invalid - " + messageInfo.getToDirectAddress(), HttpStatus.BAD_REQUEST);
                                }
                            }
                            else {
				JSONObject responseObject = new JSONObject();
				if(!"".equals(messageInfo.getMessageId())) {
				    responseObject.put("messageId",messageInfo.getMessageId());
				}
				else {
				    responseObject.put("messageId","N/A");
				}
				responseObject.put("status","FAILED");
				responseObject.put("result","missing message attachments");
				
				directMessageDetails.setSendingResponse("missing message attachments");
				transactionInManager.updateDirectAPIMessage(directMessageDetails);
				
                                return new ResponseEntity(responseObject, HttpStatus.BAD_REQUEST);
                            }
                        }
                        else {
			    JSONObject responseObject = new JSONObject();
			    if(!"".equals(messageInfo.getMessageId())) {
				responseObject.put("messageId",messageInfo.getMessageId());
			    }
			    else {
				responseObject.put("messageId","N/A");
			    }
			    responseObject.put("status","FAILED");
			    responseObject.put("result","Invalid JSON Payload!");
                            return new ResponseEntity(responseObject, HttpStatus.BAD_REQUEST);
                        }

                    }
                }
                else {
		    JSONObject responseObject = new JSONObject();
		    responseObject.put("status","UNAUTHORIZED");
		    responseObject.put("result","Invalid Credentials!");
                    return new ResponseEntity(responseObject, HttpStatus.UNAUTHORIZED);
                }
            }
            else {
		JSONObject responseObject = new JSONObject();
		responseObject.put("status","UNAUTHORIZED");
		responseObject.put("result","Invalid Credentials!");
                return new ResponseEntity(responseObject, HttpStatus.UNAUTHORIZED);
            }
        }
        else {
	    JSONObject responseObject = new JSONObject();
	    responseObject.put("status","UNAUTHORIZED");
	    responseObject.put("result","Invalid Credentials!");
            return new ResponseEntity(responseObject, HttpStatus.UNAUTHORIZED);
        }
    }
	   
    
    /**
     * The 'validateHISPCredentials' function will verify the authorization parameters passed in with the 
     * the custom api call. This will validate the credentials and the direct message domain passed.
     * 
     * @param credvalues - api passed in authorization parameters.
     * @param directDetails - the direct message domain the message is being sent from
     * @return 
     */
    private boolean validateHISPCredentials(String[] credvalues,  organizationDirectDetails directDetails) throws Exception {
	
	boolean hispAuthenticated = false;
	
	if(directDetails != null) {
	    if(directDetails.getHispId() > 0) {
		hisps hispDetails = hispmanager.getHispById(directDetails.getHispId());
		
		if(hispDetails != null) {
		    if(hispDetails.getUtAPIUsername() != null && hispDetails.getUtAPIPassword() != null) {
			if(hispDetails.getUtAPIUsername().equals(credvalues[0]) && hispDetails.getUtAPIPassword().equals(credvalues[1])) {
			    hispAuthenticated = true;
			}
		    }
		}
	    }
	}
	
	return hispAuthenticated;

    }

}
