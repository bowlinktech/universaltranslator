/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.restAPI;

import com.hel.ut.model.Organization;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.directmessagesin;
import com.hel.ut.model.hisps;
import com.hel.ut.model.medAlliesReferralAttachmentList;
import com.hel.ut.model.medAlliesReferralInfo;
import com.hel.ut.model.organizationDirectDetails;
import com.hel.ut.service.hispManager;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.transactionInManager;
import java.nio.charset.Charset;
import java.util.Base64;
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
import org.apache.commons.io.FilenameUtils;
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
    
    @PostMapping("/medallies/post/ereferral")
    public ResponseEntity<?> consumeMedAllieseReferral(@RequestBody(required = false) String jsonSent,HttpServletResponse response,HttpServletRequest request) throws Exception {
        
        String authorization = request.getHeader("Authorization");
        
        if (authorization != null) {
	    String base64Credentials = authorization.substring("Basic".length()).trim();
	    
	    String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
	    
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
                        String utRootDir = myProps.getProperty("ut.directory.utRootDir");
                        Path path = Paths.get(utRootDir + "medAlliesArchives/"+dateFormat.format(date)+".json");
                        Files.write(path, jsonSent.getBytes());
                        
                        medAlliesReferralInfo envelopeInfo = null;
                        List<medAlliesReferralAttachmentList> attachmentList = null;
                        
                        //Need to try and put JSON into medallies object
                        try {
                            Configuration conf = Configuration.builder().mappingProvider(new JacksonMappingProvider()).jsonProvider(new JacksonJsonProvider()).build();
                            TypeRef<medAlliesReferralInfo> type = new TypeRef<medAlliesReferralInfo>() {};
                            envelopeInfo = JsonPath.using(conf).parse(jsonSent).read("$", type);
                        }
                        catch (Exception ex) {
                            //Need to send email of error
                            return new ResponseEntity("Invalid JSON Payload!", HttpStatus.BAD_REQUEST);
                        }
                        
                        if(envelopeInfo != null) {
                            //Need to make sure there is an attachment array
                            try {
                                Configuration conf = Configuration.builder().mappingProvider(new JacksonMappingProvider()).jsonProvider(new JacksonJsonProvider()).build();
                                TypeRef<List<medAlliesReferralAttachmentList>> type = new TypeRef<List<medAlliesReferralAttachmentList>>() {};
                                attachmentList = JsonPath.using(conf).parse(jsonSent).read("$.emailAttachmentList.*", type);    
                            }
                            catch (Exception ex) {
                                //Need to send email of error
                                return new ResponseEntity("Invalid JSON Attachment List!", HttpStatus.BAD_REQUEST);
                            }
                            
                            if(!attachmentList.isEmpty()) {
                                
                                if(envelopeInfo.getToDirectAddress().contains("@")) {
                                
                                    if(envelopeInfo.getFromDirectAddress().contains("@")) {
                                        String DMDomain = envelopeInfo.getFromDirectAddress().substring(envelopeInfo.getFromDirectAddress().indexOf("@")+1,envelopeInfo.getFromDirectAddress().length());

                                        organizationDirectDetails directDetails = configurationtransportmanager.getDirectMessagingDetails(DMDomain);

                                        if(directDetails != null) {
                                            Organization orgDetails = organizationmanager.getOrganizationById(directDetails.getOrgId());

                                            if (!validateHISPCredentials(credvalues, directDetails)) {
                                                return new ResponseEntity("Invalid Credentials!", HttpStatus.UNAUTHORIZED);
                                            } 
                                            else {
                                                //Find the CCDA in the attachment list (will be the biggest file)
                                                String CCDAContent = null;
                                                String CCDATitle = "";
                                                Integer attachmentSize = 0;
                                                for(medAlliesReferralAttachmentList attachment : attachmentList) {
                                                    if(Integer.parseInt(attachment.getAttachmentSize()) > attachmentSize) {
                                                        CCDAContent = attachment.getAttachmentContent();
                                                        attachmentSize = Integer.parseInt(attachment.getAttachmentSize());
                                                        CCDATitle = attachment.getAttachmentTitle();
                                                    }
                                                }

                                                if(CCDAContent != null) {
                                                    if(FilenameUtils.getExtension(CCDATitle).toLowerCase().equals(directDetails.getExpectedFileExt().toLowerCase())) {
                                                        FileOutputStream fos = new FileOutputStream(utRootDir + "directMessages/" + orgDetails.getcleanURL() + "/" + CCDATitle);
                                                        fos.write(Base64.getDecoder().decode(CCDAContent.replace("\n", "")));
                                                        fos.close();
                                                        
                                                        Integer configId = 0;
                                                        
                                                        //Check how we will find the configuration
                                                        if(directDetails.getDmFindConfig() == 1) {
                                                            //Find the configuration by dm keyword from target direct address
                                                            configurationTransport transportDetails = configurationtransportmanager.findConfigurationByDirectMessagKeyword(directDetails.getOrgId(), envelopeInfo.getToDirectAddress());
                                                            configId = transportDetails.getconfigId();
                                                        }
                                                        
                                                        //Need to create an entry in the received Direct Messages table
                                                        directmessagesin newDirectMessageIn = new directmessagesin();
                                                        newDirectMessageIn.setArchiveFileName("medAlliesArchives/"+dateFormat.format(date)+".json");
                                                        newDirectMessageIn.setConfigId(configId);
                                                        newDirectMessageIn.setFromDirectAddress(envelopeInfo.getFromDirectAddress());
                                                        newDirectMessageIn.setToDirectAddress(envelopeInfo.getToDirectAddress());
                                                        newDirectMessageIn.setHispId(directDetails.getHispId());
                                                        newDirectMessageIn.setStatusId(1);
                                                        newDirectMessageIn.setReferralFileName(CCDATitle);
                                                        newDirectMessageIn.setOrgId(directDetails.getOrgId());
                                                        
                                                        Integer newDMMessageID = transactionInManager.insertDMMessage(newDirectMessageIn);
				
                                                        if(newDMMessageID > 0) {
                                                            return new ResponseEntity("Successfully received and processed your message.", HttpStatus.OK);
                                                        }
                                                        else {
                                                            return new ResponseEntity("Failed to process your message.", HttpStatus.EXPECTATION_FAILED);
                                                        }

                                                    }
                                                    else {
                                                        return new ResponseEntity("Received attachment extension (" + FilenameUtils.getExtension(CCDATitle).toLowerCase()+ ") does not match the expected file extension - ." + directDetails.getExpectedFileExt(), HttpStatus.BAD_REQUEST);
                                                    }
                                                }
                                                else {
                                                    return new ResponseEntity("missing message attachments", HttpStatus.BAD_REQUEST);
                                                }
                                            }
                                        }
                                        else {
                                            return new ResponseEntity("sending direct message is invalid - " + envelopeInfo.getFromDirectAddress(), HttpStatus.BAD_REQUEST);
                                        }
                                    }
                                    else {
                                        return new ResponseEntity("sending direct message is invalid - " + envelopeInfo.getFromDirectAddress(), HttpStatus.BAD_REQUEST);
                                    }
                                }
                                else {
                                    return new ResponseEntity("recipient direct message is invalid - " + envelopeInfo.getToDirectAddress(), HttpStatus.BAD_REQUEST);
                                }
                            }
                            else {
                                return new ResponseEntity("missing message attachments", HttpStatus.BAD_REQUEST);
                            }
                        }
                        else {
                            return new ResponseEntity("Invalid JSON Payload!", HttpStatus.BAD_REQUEST);
                        }

                    }
                }
                else {
                    return new ResponseEntity("Invalid Credentials!", HttpStatus.UNAUTHORIZED);
                }
            }
            else {
                return new ResponseEntity("Invalid Credentials!", HttpStatus.UNAUTHORIZED);
            }
        }
        else {
            return new ResponseEntity("Invalid Credentials!", HttpStatus.UNAUTHORIZED);
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
