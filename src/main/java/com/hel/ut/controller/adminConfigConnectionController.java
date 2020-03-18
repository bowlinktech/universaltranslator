package com.hel.ut.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.Organization;
import com.hel.ut.model.utUser;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationConnectionReceivers;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.messageTypeManager;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.configurationconnectionfieldmappings;
import com.hel.ut.service.sysAdminManager;
import com.hel.ut.service.userManager;


import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.hel.ut.service.hispManager;

import javax.servlet.http.HttpSession;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.registryKit.registry.configurations.configurationManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/administrator/configurations/connections")

public class adminConfigConnectionController {

    @Autowired
    private utConfigurationManager utconfigurationmanager;

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private messageTypeManager messagetypemanager;

    @Autowired
    private userManager userManager;

    @Autowired
    private utConfigurationTransportManager utconfigurationTransportManager;

    @Autowired
    private sysAdminManager sysAdminManager;
    
    @Autowired
    private configurationManager registryconfigurationmanager;
    
    @Autowired
    private hispManager hispManager;
    
    @Resource(name = "myProps")
    private Properties myProps;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(1024);
    }

    /**
     * The private maxResults variable will hold the number of results to show per list page.
     */
    private static int maxResults = 20;


    /**
     * The '' function will handle displaying the utConfiguration connections screen. The function will pass the existing connection objects for the selected utConfiguration.
     *
     * @param session
     * @return 
     * @throws java.lang.Exception 
     * @Return the connection view and the following objects.
     *
     * organizations - list of available active organizations to connect to this list will not contain any currently associated organizations.
     *
     * connections - list of currently associated organizations
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView getConnections(HttpSession session) throws Exception {
	
	Integer configId = 0;
	
	ModelAndView mav = new ModelAndView();
	
        mav.setViewName("/administrator/configurations/connections");
	
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));

        /* get a list of all connections in the sysetm */
        List<configurationConnection> connections = utconfigurationmanager.getAllConnections();

        Long totalConnections = (long) 0;

        /* Loop over the connections to get the utConfiguration details */
        if (connections != null) {
	    
            for (configurationConnection connection : connections) {
                /* Array to holder the users */
                List<utUser> connectionSenders = new ArrayList<utUser>();
                List<utUser> connectonReceivers = new ArrayList<utUser>();

                utConfiguration srcconfigDetails = utconfigurationmanager.getConfigurationById(connection.getsourceConfigId());
                configurationTransport srctransportDetails = utconfigurationTransportManager.getTransportDetails(srcconfigDetails.getId());

                srcconfigDetails.setOrgName(organizationmanager.getOrganizationById(srcconfigDetails.getorgId()).getOrgName());
		
                srcconfigDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(srctransportDetails.gettransportMethodId()));
                if (srctransportDetails.gettransportMethodId() == 1 && srcconfigDetails.getType() == 2) {
                    srcconfigDetails.settransportMethod("File Download");
                } else {
                    srcconfigDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(srctransportDetails.gettransportMethodId()));
                }

                connection.setsrcConfigDetails(srcconfigDetails);

                utConfiguration tgtconfigDetails = utconfigurationmanager.getConfigurationById(connection.gettargetConfigId());
                configurationTransport tgttransportDetails = utconfigurationTransportManager.getTransportDetails(tgtconfigDetails.getId());

                tgtconfigDetails.setOrgName(organizationmanager.getOrganizationById(tgtconfigDetails.getorgId()).getOrgName());
		
                if (tgttransportDetails.gettransportMethodId() == 1 && tgtconfigDetails.getType() == 2) {
                    tgtconfigDetails.settransportMethod("File Download");
                } else {
                    tgtconfigDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(tgttransportDetails.gettransportMethodId()));
                }

                connection.settgtConfigDetails(tgtconfigDetails);
            }

            /* Return the total list of connections */
            totalConnections = (long) connections.size();
        }

        mav.addObject("connections", connections);

        /* Set the variable to hold the number of completed steps for this utConfiguration */
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        return mav;
    }

    /**
     * The '/details' function will handle displaying the create utConfiguration connection screen.
     *
     * @param id
     * @return This function will display the new connection overlay
     */
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public ModelAndView connectionDetails(@RequestParam(value = "i", required = false) Integer id) throws Exception {
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connections/details");
	
	Integer connectionId = 0;
	Integer sourceOrgId = 0;
	Integer sourceConfigId = 0;
	Integer targetOrgId = 0;
	Integer targetConfigId = 0;
	
	if(id != null) {
	    configurationConnection connectionDetails = utconfigurationmanager.getConnection(id);
	    
	    sourceOrgId = utconfigurationmanager.getConfigurationById(connectionDetails.getsourceConfigId()).getorgId();
	    sourceConfigId = connectionDetails.getsourceConfigId();
	    
	    utConfiguration targetConfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.gettargetConfigId());
	    targetOrgId = targetConfigDetails.getorgId();
	   targetConfigId = connectionDetails.gettargetConfigId();
	    connectionId = connectionDetails.getId();
	}
	
	mav.addObject("connectionId", connectionId);
	mav.addObject("sourceOrgId", sourceOrgId);
	mav.addObject("sourceConfigId", sourceConfigId);
	mav.addObject("targetOrgId", targetOrgId);
	mav.addObject("targetConfigId", targetConfigId);

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();
        mav.addObject("organizations", organizations);

        return mav;
    }

    /**
     * The '/editConnection' function will handle displaying the edit utConfiguration connection screen.
     *
     * @param connectionId The id of the clicked utConfiguration connection
     *
     * @return This function will display the edit connection overlay
     */
    @RequestMapping(value = "/editConnection", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView editConnectionForm(@RequestParam(value = "connectionId", required = true) int connectionId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connectionDetails");

        configurationConnection connectionDetails = utconfigurationmanager.getConnection(connectionId);

        utConfiguration srcconfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.getsourceConfigId());
        srcconfigDetails.setorgId(organizationmanager.getOrganizationById(srcconfigDetails.getorgId()).getId());
        connectionDetails.setsrcConfigDetails(srcconfigDetails);

        utConfiguration tgtconfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.gettargetConfigId());
        tgtconfigDetails.setorgId(organizationmanager.getOrganizationById(tgtconfigDetails.getorgId()).getId());
        connectionDetails.settgtConfigDetails(tgtconfigDetails);

        
        mav.addObject("connectionDetails", connectionDetails);

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();
        mav.addObject("organizations", organizations);

        return mav;
    }

    /**
     * The '/getAvailableSendingContacts.do' function will return a list of users that are associated to the selected organization.
     *
     * @param orgId The organization selected in the drop down
     *
     * @return users The available users
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableSendingContacts.do", method = RequestMethod.GET)
    public @ResponseBody ModelAndView getAvailableSendingContacts(@RequestParam(value = "orgId", required = true) int orgId, @RequestParam(value = "connectionId", required = true) int connectionId) {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connectionSendingContacts");

        Organization organizationDetails = organizationmanager.getOrganizationById(orgId);
	
	List<configurationConnectionSenders> sendingContacts = new ArrayList<>();
	
	//Get a list of saved sending contacts
	List<configurationConnectionSenders> savedSendingContacts = utconfigurationmanager.getConnectionSenders(connectionId);
	
	if(!"".equals(organizationDetails.getPrimaryContactEmail())) {
	    if(savedSendingContacts != null) {
		if(!savedSendingContacts.isEmpty()) {
		    boolean primaryEmailFound = false;
		    configurationConnectionSenders primarySendingContact = null;
		    
		    for(configurationConnectionSenders savedSendingContact : savedSendingContacts) {
			if(savedSendingContact.getEmailAddress().equals(organizationDetails.getPrimaryContactEmail())) {
			    primaryEmailFound = true;
			    savedSendingContact.setContactType("Primary");
			    primarySendingContact = savedSendingContact;
			}
		    }
		    
		    if(!primaryEmailFound) {
			configurationConnectionSenders sendingContact = new configurationConnectionSenders();
			sendingContact.setContactType("Primary");
			sendingContact.setConnectionId(connectionId);
			sendingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
			sendingContact.setSendEmailNotifications(false);
			sendingContacts.add(sendingContact);
		    }
		    else {
			if(primarySendingContact != null) {
			    sendingContacts.add(primarySendingContact);
			}
		    }
		}
		else {
		    configurationConnectionSenders sendingContact = new configurationConnectionSenders();
		    sendingContact.setContactType("Primary");
		    sendingContact.setConnectionId(connectionId);
		    sendingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
		    sendingContact.setSendEmailNotifications(false);
		    sendingContacts.add(sendingContact);
		}
	    }
	    else {
		configurationConnectionSenders sendingContact = new configurationConnectionSenders();
		sendingContact.setContactType("Primary");
		sendingContact.setConnectionId(connectionId);
		sendingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
		sendingContact.setSendEmailNotifications(false);
		sendingContacts.add(sendingContact);
	    }
	}
	
	
        mav.addObject("sendingContacts", sendingContacts);

        return mav;
    }

    /**
     * The '/getAvailableReceivingContacts.do' function will return a list of users that are associated to the selected organization.
     *
     * @param orgId The organization selected in the drop down
     *
     * @return users The available users
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableReceivingContacts.do", method = RequestMethod.GET)
    public @ResponseBody ModelAndView getAvailableReceivingContacts(@RequestParam(value = "orgId", required = true) int orgId,@RequestParam(value = "connectionId", required = true) int connectionId) {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connectionReceivingContacts");

        Organization organizationDetails = organizationmanager.getOrganizationById(orgId);
	
	List<configurationConnectionReceivers> receivingContacts = new ArrayList<>();
	
	//Get a list of saved receiving contacts
	List<configurationConnectionReceivers> savedReceivingContacts = utconfigurationmanager.getConnectionReceivers(connectionId);
	
	if(!"".equals(organizationDetails.getPrimaryContactEmail())) {
	    if(savedReceivingContacts != null) {
		if(!savedReceivingContacts.isEmpty()) {
		    boolean primaryEmailFound = false;
		    configurationConnectionReceivers primaryReceivingContact = null;
		    
		    for(configurationConnectionReceivers savedReceivingContact : savedReceivingContacts) {
			if(savedReceivingContact.getEmailAddress().equals(organizationDetails.getPrimaryContactEmail())) {
			    primaryEmailFound = true;
			    savedReceivingContact.setContactType("Primary");
			    primaryReceivingContact = savedReceivingContact;
			}
		    }
		    
		    if(!primaryEmailFound) {
			configurationConnectionReceivers receivingContact = new configurationConnectionReceivers();
			receivingContact.setContactType("Primary");
			receivingContact.setConnectionId(connectionId);
			receivingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
			receivingContact.setSendEmailNotifications(false);
			receivingContacts.add(receivingContact);
		    }
		    else {
			if(primaryReceivingContact != null) {
			    receivingContacts.add(primaryReceivingContact);
			}
		    }
		}
		else {
		    configurationConnectionReceivers receivingContact = new configurationConnectionReceivers();
		    receivingContact.setContactType("Primary");
		    receivingContact.setConnectionId(connectionId);
		    receivingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
		    receivingContact.setSendEmailNotifications(false);
		    receivingContacts.add(receivingContact);
		}
	    }
	    else {
		configurationConnectionReceivers receivingContact = new configurationConnectionReceivers();
		receivingContact.setContactType("Primary");
		receivingContact.setConnectionId(connectionId);
		receivingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
		receivingContact.setSendEmailNotifications(false);
		receivingContacts.add(receivingContact);
	    }
	}
	
        mav.addObject("receivingContacts", receivingContacts);

        return mav;
    }

    /**
     * The '/getAvailableConfigurations.do' function will return a list of utConfiguration that have been set up for the passed in organization.
     *
     * @param orgId The organization selected in the drop down
     * @param selectBoxId
     *
     * @return configurations The available configurations
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableConfigurations.do", method = RequestMethod.GET)
    public @ResponseBody
    List<utConfiguration> getAvailableConfigurations(@RequestParam(value = "orgId", required = true) Integer orgId,@RequestParam(value = "selectBoxId", required = true) String selectBoxId) throws Exception {

        List<utConfiguration> configurations = utconfigurationmanager.getActiveConfigurationsByOrgId(orgId);
	
	List<utConfiguration> availableConfigurations = new ArrayList<>();

        if (configurations != null) {
            for (utConfiguration configuration : configurations) {
                configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configuration.getId());

                configuration.setOrgName(organizationmanager.getOrganizationById(configuration.getorgId()).getOrgName());
		
                configuration.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
		
		if(configuration.getType() == 1 && "srcConfig".equals(selectBoxId)) {
		    availableConfigurations.add(configuration);
		}
		else if(configuration.getType() == 2 && "tgtConfig".equals(selectBoxId)) {
		    availableConfigurations.add(configuration);
		}
            }
        }
        return availableConfigurations;
    }

    /**
     * The '/addConnection.do' POST request will create the connection between the passed in organization and the utConfiguration.
     *
     * @param connectionDetails
     * @param srcEmailNotifications
     * @param tgtEmailNotifications
     * @param redirectAttr
     *
     * @return	The method will return a 1 back to the calling ajax function which will handle the page load.
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/addConnection.do", method = RequestMethod.POST)
    public ModelAndView addConnection(
            @ModelAttribute(value = "connectionDetails") configurationConnection connectionDetails,
            @RequestParam(value = "srcEmailNotifications", required = false) List<String> srcEmailNotifications,
            @RequestParam(value = "tgtEmailNotifications", required = false) List<String> tgtEmailNotifications,
            RedirectAttributes redirectAttr) throws Exception {

        Integer connectionId;

        if (connectionDetails.getId() == 0) {
            connectionDetails.setStatus(true);
            connectionId = utconfigurationmanager.saveConnection(connectionDetails);
            redirectAttr.addFlashAttribute("savedStatus", "created");
        } 
	else {
            connectionId = connectionDetails.getId();
            utconfigurationmanager.updateConnection(connectionDetails);

            /* Delete existing senders and receivers */
            utconfigurationmanager.removeConnectionSenders(connectionId);
            utconfigurationmanager.removeConnectionReceivers(connectionId);
            redirectAttr.addFlashAttribute("savedStatus", "updated");
        }
	
	if(srcEmailNotifications != null) {
	    if(!srcEmailNotifications.isEmpty()) {
		for (String srcEmail : srcEmailNotifications) {
		    configurationConnectionSenders senderInfo = new configurationConnectionSenders();
		    senderInfo.setConnectionId(connectionId);
		    senderInfo.setEmailAddress(srcEmail);
		    senderInfo.setSendEmailNotifications(true);
		    utconfigurationmanager.saveConnectionSenders(senderInfo);
		}
	    }
	}
	
	
	if(tgtEmailNotifications != null) {
	    if(!tgtEmailNotifications.isEmpty()) {
		for (String tgtEmail : tgtEmailNotifications) {
		    configurationConnectionReceivers receiverInfo = new configurationConnectionReceivers();
		    receiverInfo.setConnectionId(connectionId);
		    receiverInfo.setEmailAddress(tgtEmail);
		    receiverInfo.setSendEmailNotifications(true);
		    utconfigurationmanager.saveConnectionReceivers(receiverInfo);
		}
	    }
	}

        ModelAndView mav = new ModelAndView(new RedirectView("connections"));

        return mav;

    }

    /**
     * The '/changeConnectionStatus.do' POST request will update the passed in connection status.
     *
     * @param connectionId The id for the connection to update the status for
     * @param statusVal The new status for the connection
     *
     * @return The method will return a 1 back to the calling ajax function.
     */
    @RequestMapping(value = "/changeConnectionStatus.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer changeConnectionStatus(@RequestParam boolean statusVal, @RequestParam int connectionId) throws Exception {

        configurationConnection connection = utconfigurationmanager.getConnection(connectionId);
        connection.setStatus(statusVal);
        utconfigurationmanager.updateConnection(connection);

        return 1;
    }

    /**
     * The '/getAvailableSendingContacts.do' function will return a list of users that are associated to the selected organization.
     *
     * @param selConfigId
     * @param sourceConfigId
     * @param connectionId
     * @param section
     *
     * @return users The available users
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getConfigurationDataElements", method = RequestMethod.GET)
    public @ResponseBody ModelAndView getConfigurationDataElements(
	    @RequestParam(value = "selConfigId", required = true) Integer selConfigId, 
	    @RequestParam(value = "sourceConfigId", required = true) Integer sourceConfigId, 
	    @RequestParam(value = "connectionId", required = true) Integer connectionId, 
	    @RequestParam(value = "section", required = true) String section) throws Exception {

        ModelAndView mav = new ModelAndView();
	
	List<configurationFormFields> configurationDataElements = utconfigurationTransportManager.getConfigurationFields(selConfigId, 0);
	
	if("src".equals(section)) {
	   mav.setViewName("/administrator/configurations/connections/sourceConfigurationDataElements"); 
	   mav.addObject("sourceConfigurationDataElements",configurationDataElements);
	   
	}
	else {
	    boolean showErrorField = false;
	    mav.setViewName("/administrator/configurations/connections/targetConfigurationDataElements");
	    List<configurationFormFields> sourceconfigurationDataElements = utconfigurationTransportManager.getConfigurationFields(sourceConfigId, 0);
	    
	     
	    configurationTransport targetConfigTransportDetails = utconfigurationTransportManager.getTransportDetails(selConfigId);
	    
	    if(targetConfigTransportDetails.isPopulateInboundAuditReport()) {
		showErrorField = true;
	    }
	    
	    if(connectionId > 0) {
		List<configurationconnectionfieldmappings> fieldMappings = utconfigurationTransportManager.getConnectionFieldMappings(selConfigId, sourceConfigId);
		
		if(!fieldMappings.isEmpty()) {
		    for(configurationconnectionfieldmappings fieldMapping : fieldMappings) {
			for(configurationFormFields tgtDataElements : configurationDataElements) {
			    if(fieldMapping.getFieldNo() == tgtDataElements.getFieldNo() && !fieldMapping.isUseField()) {
				tgtDataElements.setUseField(false);
			    }
			    if(fieldMapping.getFieldNo() == tgtDataElements.getFieldNo()) {
				tgtDataElements.setMappedErrorField(fieldMapping.getPopulateErrorFieldNo());
				tgtDataElements.setMappedToField(fieldMapping.getAssociatedFieldNo());
			    }
			}
		    }
		}
	    }
	    mav.addObject("targetConfigurationDataElements",configurationDataElements);
	    mav.addObject("sourceconfigurationDataElements",sourceconfigurationDataElements);
	    mav.addObject("showErrorField",showErrorField);
	}
        
        return mav;
    }
    
    
    @RequestMapping(value = "/saveConnectionElementMappings", method = RequestMethod.POST)
    public @ResponseBody String saveConnectionElementMappings (
	    @RequestParam(value = "connectionId", required = true) Integer connectionId, 
	    @RequestParam(value = "sourceConfigId", required = true) Integer sourceConfigId, 
	    @RequestParam(value = "targetConfigId", required = true) Integer targetConfigId, 
	    @RequestParam(value = "mappedFields[]", required = true) String mappedFields,
	    @RequestParam(value = "mappedErrorFields[]", required = false) String mappedErrorFields) throws Exception {
	
	
	if(connectionId == 0) {
	    configurationConnection newConnection = new configurationConnection();
	    newConnection.setsourceConfigId(sourceConfigId);
	    newConnection.settargetConfigId(targetConfigId);
	    newConnection.setStatus(true);
	    
	    connectionId = utconfigurationmanager.saveConnection(newConnection);
	}
	else {
	    //delete existing mapped fields
	    utconfigurationTransportManager.deleteConnectionMappedFields(connectionId);
	}
	
	String[] mappedFieldsAsArray = mappedFields.split(",");
	
	for (int i = 0; i < mappedFieldsAsArray.length; i++) { 
	    configurationconnectionfieldmappings newFieldMapping = new configurationconnectionfieldmappings();
	    newFieldMapping.setConnectionId(connectionId);
	    newFieldMapping.setSourceConfigId(sourceConfigId);
	    newFieldMapping.setTargetConfigId(targetConfigId);
	    
	    String[] mappedDetails = mappedFieldsAsArray[i].split("\\|");
	    
	    newFieldMapping.setFieldNo(Integer.parseInt(mappedDetails[0]));
	    newFieldMapping.setFieldDesc(mappedDetails[1]);
	    newFieldMapping.setUseField(Boolean.parseBoolean(mappedDetails[2]));
	    
	    if(mappedDetails[3].contains("~")) {
		String[] matchingFieldDetails = mappedDetails[3].split("\\~");
		newFieldMapping.setAssociatedFieldNo(0);
		newFieldMapping.setDefaultValue(matchingFieldDetails[1]);
	    }
	    else {
		newFieldMapping.setAssociatedFieldNo(Integer.parseInt(mappedDetails[3]));
	    }
	    
	    
	    if(mappedErrorFields != null) {
		String[] mappedErrorFieldsAsArray = mappedErrorFields.split(",");
		newFieldMapping.setPopulateErrorFieldNo(Integer.parseInt(mappedErrorFieldsAsArray[i]));
	    }
	    else {
		newFieldMapping.setPopulateErrorFieldNo(Integer.parseInt(mappedDetails[3]));
	    }
	    
	    
	    utconfigurationTransportManager.saveConnectionFieldMapping(newFieldMapping);
	}
	
	
	return connectionId.toString();
    }

    
    /**
     * The '/deleteConnection.do' POST request will remove the passed in connection.
     *
     * @param connectionId The id for the connection to update the status for
     *
     * @return The method will return a 1 back to the calling ajax function.
     */
    @RequestMapping(value = "/deleteConnection.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Integer deleteConnection(@RequestParam Integer connectionId) throws Exception {
	
	utconfigurationmanager.removeConnectionReceivers(connectionId);
	utconfigurationmanager.removeConnectionSenders(connectionId);
	utconfigurationTransportManager.deleteConnectionMappedFields(connectionId);
	utconfigurationmanager.removeConnection(connectionId);

        return 1;
    }
    
    /**
     * The 'createConnectionPrintPDF.do' method will print the selected connection.
     * @param connectionId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/createConnectionPrintPDF.do", method = RequestMethod.GET)
    @ResponseBody
    public String printConfiguration(@RequestParam int connectionId) throws Exception {
	
	configurationConnection connectionDetails = utconfigurationmanager.getConnection(connectionId);

        utConfiguration srcconfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.getsourceConfigId());
        utConfiguration tgtconfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.gettargetConfigId());
        
	String connectionDetailFile = "/tmp/connectionId-" + connectionId + ".txt";
	String connectionPrintFile = "/tmp/UT-connection-" + connectionId + ".pdf";
	
	File detailsFile = new File(connectionDetailFile);
	detailsFile.delete();
	
	File printFile = new File(connectionPrintFile);
	printFile.delete();
	
	Document document = new Document(PageSize.A4);
	
	StringBuffer reportBody = new StringBuffer();
	
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(connectionDetailFile, true)));
	out.println("<html><body>");
	
	reportBody.append(utconfigurationmanager.printConnectionDetails(srcconfigDetails,tgtconfigDetails));
	
	out.println(reportBody.toString());
	
	out.println("</body></html>");
	
	out.close();
	
	PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(connectionPrintFile));
			  
	document.open();
			    
	XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

	//replace with actual code to generate html info
	//we get image location here 
	FileInputStream fis = new FileInputStream(connectionDetailFile);
	worker.parseXHtml(pdfWriter, document, fis);
;
	fis.close();
	document.close();
	pdfWriter.close();
	
	File connectionDetailsFile = new File(connectionDetailFile);
	connectionDetailsFile.delete();

	return "UT-connection-" + connectionId;
    }

    
    @RequestMapping(value = "/printConfig/{file}", method = RequestMethod.GET)
    public void printConfig(@PathVariable("file") String file,HttpServletResponse response
    ) throws Exception {
	
	File connectionPrintFile = new File ("/tmp/" + file + ".pdf");
	InputStream is = new FileInputStream(connectionPrintFile);

	response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".pdf\"");
	FileCopyUtils.copy(is, response.getOutputStream());

	//Delete the file
	connectionPrintFile.delete();

	 // close stream and return to view
	response.flushBuffer();
    } 
}
