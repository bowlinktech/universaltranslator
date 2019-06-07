/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.restAPI;

import com.hel.ut.model.Organization;
import com.hel.ut.model.hisps;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


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
	   
    /**
     * The 'consumeDIRECTAPICall' POST method will allow a REST API POST message to be sent for
     * direct messaging. The post call will take in attachments (referral files) and also have
     * basic authentication for the API.
     * 
     * @param messageFiles - array of attachments.
     * @param response - return a valid Http response depending on the validation results.
     * @param request  - request will hold the authorization parameters.
     * @return 
     * @throws java.lang.Exception 
     */
    @PostMapping("/post/referral")
    public ResponseEntity<?> consumeDIRECTAPICall(@RequestParam("messageFiles") MultipartFile[] messageFiles,HttpServletResponse response,HttpServletRequest request) throws Exception {
	
	String authorization = request.getHeader("Authorization");
	String sendingDirectAddress = request.getHeader("senderDMAddress");
	
	//Make sure the direct message sending address is present
	if (sendingDirectAddress != null) {
	    
	    //Make sure the direct message sending address is valid
	    if(sendingDirectAddress.contains("@")) {
		
		String DMDomain = sendingDirectAddress.substring(sendingDirectAddress.indexOf("@")+1,sendingDirectAddress.length());
		
		organizationDirectDetails directDetails = configurationtransportmanager.getDirectMessagingDetails(DMDomain);
		
		if(directDetails != null) {
		    
		    Organization orgDetails = organizationmanager.getOrganizationById(directDetails.getOrgId());
		    
		    if (authorization != null) {
			String base64Credentials = authorization.substring("Basic".length()).trim();

			String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));

			if (!"".equals(credentials)) {

			    final String[] credvalues = credentials.split(":", 2);

			    if(credvalues.length == 2) {
				if (!validateHISPCredentials(credvalues, directDetails)) {
				    return new ResponseEntity("Invalid Credentials!", HttpStatus.UNAUTHORIZED);
				} 
				else {

				    //Make sure the message contains attachment(s)
				    List<MultipartFile> files = Arrays.asList(messageFiles);

				    if(files.isEmpty()) {
					return new ResponseEntity("missing message attachments", HttpStatus.BAD_REQUEST);
				    }
				    else {
					String utRootDir = myProps.getProperty("ut.directory.utRootDir");

					//Need to make sure the file ext in the settings match the file received
					if(files.size() > 1) {
					    String validReferralFileName = "";
					    byte[] bytes = null;

					    //CCDA will always be the largest file size.
					    long fileSize = 0;
					    for (MultipartFile file : files) {
						if(file.getSize() > fileSize) {
						    fileSize = file.getSize();
						    validReferralFileName = file.getOriginalFilename();
						    bytes = file.getBytes();
						}
					    }
					    
					    if(!"".equals(validReferralFileName) && bytes != null && FilenameUtils.getExtension(validReferralFileName).toLowerCase().equals(directDetails.getExpectedFileExt().toLowerCase())) {
						Path path = Paths.get(utRootDir + "directMessages/" + orgDetails.getcleanURL() + "/" + validReferralFileName);
						Files.write(path, bytes);

						return new ResponseEntity("Successfully Received", HttpStatus.OK);
					    }
					    else {
					       return new ResponseEntity("Received attachment does not match the expected file extension - ." + directDetails.getExpectedFileExt(), HttpStatus.BAD_REQUEST);
					    }
					}
					else {
					    if(FilenameUtils.getExtension(files.get(0).getOriginalFilename().toLowerCase()).equals(directDetails.getExpectedFileExt().toLowerCase())) {
						byte[] bytes = files.get(0).getOriginalFilename().getBytes();
						Path path = Paths.get(utRootDir + "directMessages/" + orgDetails.getcleanURL() + "/" + files.get(0).getOriginalFilename());
						Files.write(path, bytes);

						return new ResponseEntity("Successfully Received", HttpStatus.OK);
					    }
					    else {
					       return new ResponseEntity("Received attachment does not match the expected file extension - ." + directDetails.getExpectedFileExt(), HttpStatus.BAD_REQUEST);
					    }
					}
				    } 
				}
			    } else {
				return new ResponseEntity("Invalid Credentials!", HttpStatus.UNAUTHORIZED);
			    }
			} else {
			    return new ResponseEntity("Invalid Credentials!", HttpStatus.UNAUTHORIZED);
			}
		    } else {
			return new ResponseEntity("Invalid Credentials!", HttpStatus.UNAUTHORIZED);
		    }
		}
		else {
		    return new ResponseEntity("sending direct message is invalid - " + sendingDirectAddress, HttpStatus.BAD_REQUEST);
		}
	    }
	    else {
		return new ResponseEntity("sending direct message is invalid - " + sendingDirectAddress, HttpStatus.BAD_REQUEST);
	    }
	}
	else {
	    return new ResponseEntity("missing sending direct message", HttpStatus.BAD_REQUEST);
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
