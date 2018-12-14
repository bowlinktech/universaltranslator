/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.restAPI;

import com.hel.ut.model.configurationTransport;
import com.hel.ut.service.transactionInManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.nio.charset.Charset;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.hel.ut.service.utConfigurationTransportManager;

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
    
    @RequestMapping(value = "/HFR2DHSI2", method = RequestMethod.GET)
    public void HFR2DHSI2() {
	Client client = Client.create();
	client.setConnectTimeout(5000);
	client.setReadTimeout(25000);
	client.addFilter(new HTTPBasicAuthFilter("dhishim", "DHISHIM2018"));
	
	WebResource webResource = client.resource("http://41.217.202.50:8080/dhis/api/sqlViews/kcv8lhQP0ZA/data");
	
	MultivaluedMap queryParams = new MultivaluedMapImpl();
	queryParams.add("var", "name:Muhimbili");
	queryParams.add("var", "code:105651-7");
	queryParams.add("var", "councilcode:TZdotETdotDSdotILdot2");
	queryParams.add("var", "facilitytypegroupcode:HOSP");
	queryParams.add("var", "facilitytypecode:NAT");
	queryParams.add("var", "ownershipgroupcode:Publ");
	queryParams.add("var", "ownershipcode:MoHCDGEC");
	queryParams.add("var", "latitude:-6dot801501");
	queryParams.add("var", "longitude:39dot274157");
	queryParams.add("var", "opendate:2013-08-20");
	queryParams.add("var", "openedtoclose:Y");
	
	try {
	    ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);
	
	} 
	catch (ClientHandlerException | UniformInterfaceException ex) {
	    System.out.println(ex.getMessage());
	}
	
	client.destroy();
    }
    
    @RequestMapping(value = "/post/JSON/sprintRestTest", method = RequestMethod.GET)
    public void sprintRestTest() {
	
	String jsonContentAsString = "{\n" +
"  \"ilNumber\": \"NjIzNTguNDUyLjI4MTI=\",\n" +
"	\"values\": [\n" +
"    {\n" +
"      \"Plant\": \"DR3\",\n" +
"      \"PartNum\": \"10010001MD\",\n" +
"      \"UOM\": \"1000TB\",\n" +
"      \"PartDescription\": \"ACETYLSALICYLIC ACID (ASPIRIN) TABLETS 300MG\",\n" +
"      \"OnHandQty\": \"2.00\",\n" +
"      \"Date\": \"Apr 3 2018 2:55PM\",\n" +
"      \"MonthOfStock\": \"\"\n" +
"    },\n" +
"    {\n" +
"      \"Plant\": \"DR\",\n" +
"      \"PartNum\": \"10010001SP\",\n" +
"      \"UOM\": \"1000TB\",\n" +
"      \"PartDescription\": \"Ferrous Sulfate 200mg + Folic Acid 0.4mg,1000 Tab Jar\",\n" +
"      \"OnHandQty\": \"0.00\",\n" +
"      \"Date\": \"Apr 3 2018 2:55PM\",\n" +
"      \"MonthOfStock\": \"\"\n" +
"    }\n" +
"  ]\n" +
"}";
	
	Client client = Client.create();
	client.setConnectTimeout(5000);
	client.setReadTimeout(25000);
	client.addFilter(new HTTPBasicAuthFilter("hassan", "8819rukia"));
	
	WebResource webResource = client.resource("https://uat.tz.elmis-dev.org/rest-api/msd-stock-status.json");
	
	try {
	    ClientResponse response = webResource.type("application/json").header(HttpHeaders.CONNECTION,"Keep-Alive: timeout=15, max=100").post(ClientResponse.class, jsonContentAsString.replaceAll(",$", ""));
	
	} 
	catch (ClientHandlerException | UniformInterfaceException ex) {
	    System.out.println(ex.getMessage());
	}
	
	client.destroy();
	
    }
   
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
	 
	 //response.setStatus((HttpServletResponse.SC_BAD_REQUEST));
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
    @RequestMapping(value = "/post/JSON/{apiCustomCall}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
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
	
				//response.setStatus((HttpServletResponse.SC_UNAUTHORIZED));
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
				    Integer newRestAPIMessageID = transactionInManager.insertRestApiMessage(transportDetails.getconfigId(), jsonSent);
				
				    if(newRestAPIMessageID > 0) {
					//User and custom api call is validated send response and proceed
					obj.put("status", new Integer(HttpServletResponse.SC_OK));
					obj.put("Message", "Successfully received and processed your message.");
					//response.setStatus((HttpServletResponse.SC_OK));
				    }
				    else {
					obj.put("status", new Integer(HttpServletResponse.SC_EXPECTATION_FAILED));
					obj.put("Message", "Failed to process your message.");
					//response.setStatus((HttpServletResponse.SC_EXPECTATION_FAILED));
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
