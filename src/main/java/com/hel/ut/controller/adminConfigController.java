package com.hel.ut.controller;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.HL7Details;
import com.hel.ut.model.HL7ElementComponents;
import com.hel.ut.model.HL7Elements;
import com.hel.ut.model.HL7Segments;
import com.hel.ut.model.Macros;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationDataTranslations;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationFileDropFields;
import com.hel.ut.model.configurationWebServiceSenders;
import com.hel.ut.model.Organization;
import com.hel.ut.model.utUser;
import com.hel.ut.model.configurationCCDElements;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationConnectionReceivers;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.configurationFTPFields;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.configurationSchedules;
import com.hel.ut.service.organizationManager;
import com.hel.ut.model.messageType;
import com.hel.ut.service.messageTypeManager;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.configurationTransportMessageTypes;
import com.hel.ut.model.mainHL7Details;
import com.hel.ut.model.mainHL7Elements;
import com.hel.ut.model.mainHL7Segments;
import com.hel.ut.reference.fileSystem;
import com.hel.ut.service.sysAdminManager;
import com.hel.ut.service.userManager;

import java.io.File;
import java.io.FileInputStream;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.hel.ut.model.configurationWebServiceFields;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import com.registryKit.registry.configurations.configuration;
import com.registryKit.registry.configurations.configurationManager;

@Controller
@RequestMapping("/administrator/configurations")

public class adminConfigController {

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
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(1024);
    }

    /**
     * The private maxResults variable will hold the number of results to show per list page.
     */
    private static int maxResults = 20;

    /**
     * The '/list' GET request will serve up the existing list of configurations in the system
     *
     * @param page	The page parameter will hold the page to view when pagination is built.
     * @return	The utConfiguration page list
     *
     * @Objects	(1) An object containing all the found configurations
     *
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listConfigurations() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/list");

        List<utConfiguration> configurations = utconfigurationmanager.getConfigurations();

        Organization org;
        messageType messagetype;
        configurationTransport transportDetails;

        for (utConfiguration config : configurations) {
            org = organizationmanager.getOrganizationById(config.getorgId());
            config.setOrgName(org.getOrgName());
	   
            transportDetails = utconfigurationTransportManager.getTransportDetails(config.getId());
            if (transportDetails != null) {
                config.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
            }

        }

        mav.addObject("configurationList", configurations);

        return mav;

    }

    /**
     * The '/create' GET request will serve up the create new utConfiguration page
     *
     * @return	The create new utConfiguration form
     *
     * @Objects	(1) An object with a new configuration
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createConfiguration(HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/details");
        mav.addObject("configurationDetails", new utConfiguration());

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();
        mav.addObject("organizations", organizations);

        //Need to get a list of active message types
        List<messageType> messageTypes = messagetypemanager.getActiveMessageTypes();
        mav.addObject("messageTypes", messageTypes);
	mav.addObject("mappings", 1);
	
	session.setAttribute("showAllConfigOptions",true);
	mav.addObject("showAllConfigOptions", session.getAttribute("showAllConfigOptions"));
	
	//Get a list of other active sourceconfigurations
	//These will show only for a target configuration
	List<utConfiguration> sourceConfigurations = utconfigurationmanager.getAllActiveSourceConfigurations();
	mav.addObject("sourceConfigurations", sourceConfigurations);
       
        return mav;

    }

    /**
     * The '/getAvailableMessageTypes.do' function will return a list of message types that have not been already set up for the passed in organization.
     *
     * @param orgId The organization selected in the drop down
     *
     * @return messageTypes The available message types
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableMessageTypes.do", method = RequestMethod.GET)
    public @ResponseBody
    List<messageType> getMessageTypes(@RequestParam(value = "orgId", required = true) int orgId) {

        List<messageType> messageTypes = messagetypemanager.getAvailableMessageTypes(orgId);

        return messageTypes;
    }

    /**
     * The '/create' POST request will submit the new utConfiguration once all required fields are checked, the system will also check to make sure the utConfiguration name is not already in use.
     *
     * @param session
     * @param configurationDetails	The object holding the utConfiguration details form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     * @param action	The variable that holds which button was pressed
     *
     * @return	Will return the utConfiguration details page on "Save" Will return the utConfiguration create page on error
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView saveNewConfiguration(HttpSession session,@ModelAttribute(value = "configurationDetails") utConfiguration configurationDetails, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

        // Need to make sure the name isn't already taken for the org selected
        utConfiguration existing = utconfigurationmanager.getConfigurationByName(configurationDetails.getconfigName(), configurationDetails.getorgId());

        if (existing != null) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/configurations/details");

            mav.addObject("configurationDetails", configurationDetails);

            //Need to get a list of active organizations.
            List<Organization> organizations = organizationmanager.getAllActiveOrganizations();
            mav.addObject("organizations", organizations);

            mav.addObject("existingName", "The configuration name " + configurationDetails.getconfigName().trim() + " already exists.");
            return mav;
        }
	
	if(configurationDetails.getMessageTypeId() == null) {
	    configurationDetails.setMessageTypeId(0);
	}
	configurationDetails.setstepsCompleted(1);
	
        Integer id = (Integer) utconfigurationmanager.createConfiguration(configurationDetails);

        session.setAttribute("manageconfigId", id);
	
	//If configuration is a target then create the connection
	if(id > 0 && configurationDetails.getType() == 2 && configurationDetails.getAssociatedSourceConfigId() > 0) {
	    configurationConnection newConnection = new configurationConnection();
	    newConnection.setsourceConfigId(configurationDetails.getAssociatedSourceConfigId());
	    newConnection.settargetConfigId(id);
	    newConnection.setStatus(true);
	    
	    Integer newConnectionId = utconfigurationmanager.saveConnection(newConnection);
	    
	    //Get the sending organization details
	    utConfiguration sendingConfigDetails = utconfigurationmanager.getConfigurationById(configurationDetails.getAssociatedSourceConfigId());
	    Organization sendingOrgDetails = organizationmanager.getOrganizationById(sendingConfigDetails.getorgId());
	    
	    if(!sendingOrgDetails.getPrimaryContactEmail().equals("")) {
		configurationConnectionSenders newConnectionSender = new configurationConnectionSenders();
		newConnectionSender.setConnectionId(newConnectionId);
		newConnectionSender.setEmailAddress(sendingOrgDetails.getPrimaryContactEmail());
		newConnectionSender.setSendEmailNotifications(false);
		
		utconfigurationmanager.saveConnectionSenders(newConnectionSender);
	    }
	    
	    //Get the receiving organziation details
	    Organization receivingOrgDetails = organizationmanager.getOrganizationById(configurationDetails.getorgId());
	   
	    if(!receivingOrgDetails.getPrimaryContactEmail().equals("")) {
		configurationConnectionReceivers newConnectionReceiver = new configurationConnectionReceivers();
		newConnectionReceiver.setConnectionId(newConnectionId);
		newConnectionReceiver.setEmailAddress(receivingOrgDetails.getPrimaryContactEmail());
		newConnectionReceiver.setSendEmailNotifications(false);
		
		utconfigurationmanager.saveConnectionReceivers(newConnectionReceiver);
	    }
	    
	}
	
        //If the "Save" button was pressed 
        if (action.equals("save")) {
            redirectAttr.addFlashAttribute("savedStatus", "created");
            ModelAndView mav = new ModelAndView(new RedirectView("details"));
            return mav;

        } //If the "Next Step" button was pressed.
        else {
            redirectAttr.addFlashAttribute("savedStatus", "created");
            ModelAndView mav = new ModelAndView(new RedirectView("transport"));
            return mav;
        }

    }

    /**
     * The '/details' GET request will display the clicked utConfiguration details page.
     *
     * @return	Will return the utConfiguration details page.
     *
     * @Objects	(1) The object containing all the information for the clicked configuration (2) The 'id' of the clicked configuration that will be used in the menu and action bar
     *
     * @throws Exception
     *
     */
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public ModelAndView viewConfigurationDetails(HttpSession session,@RequestParam(value = "i", required = false) Integer id) throws Exception {

        Integer configId = 0;
	
	ModelAndView mav = new ModelAndView();
	
        //Set the static variable messageTypeId to hold the passed in id
        if (id == null && session.getAttribute("manageconfigId") == null) {
            mav = new ModelAndView(new RedirectView("list"));
            return mav;
        }
	else if(id == null && session.getAttribute("manageconfigId") != null) {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	else {
	    session.setAttribute("manageconfigId", id);
	    configId = id;
	}
        mav.setViewName("/administrator/configurations/details");

        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
        mav.addObject("configurationDetails", configurationDetails);

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();
        mav.addObject("organizations", organizations);

        //Need to get a list of organization users 
        List<utUser> users = userManager.getUsersByOrganization(configurationDetails.getorgId());
        mav.addObject("users", users);

        mav.addObject("id", configId);

        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
        if (transportDetails != null) {
            configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
	    session.setAttribute("configmappings", 1);
        }
	
	//Get a list of other active sourceconfigurations
	//These will show only for a target configuration
	List<utConfiguration> sourceConfigurations = utconfigurationmanager.getAllActiveSourceConfigurations();
	mav.addObject("sourceConfigurations", sourceConfigurations);

        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions", session.getAttribute("showAllConfigOptions"));

        return mav;
	
    }

    /**
     * The '/details' POST request will display the clicked utConfiguration details page.
     *
     * @return	Will return the utConfiguration details page.
     *
     * @Objects	(1) The object containing all the information for the clicked configuration (2) The 'id' of the clicked configuration that will be used in the menu and action bar
     *
     * @throws Exception
     *
     */
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public ModelAndView updateConfigurationDetails(HttpSession session,@ModelAttribute(value = "configurationDetails") utConfiguration configurationDetails, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();

        //Need to get a list of active message types
        List<messageType> messageTypes = messagetypemanager.getActiveMessageTypes();

        //Need to get a list of organization users 
        List<utUser> users = userManager.getUsersByOrganization(configurationDetails.getorgId());

        //submit the updates
	utconfigurationmanager.updateConfiguration(configurationDetails);
        

        //If the "Save" button was pressed 
        if (action.equals("save")) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/configurations/details");

            mav.addObject("organizations", organizations);
            mav.addObject("messageTypes", messageTypes);
            mav.addObject("users", users);
            mav.addObject("id", configurationDetails.getId());
            mav.addObject("mappings", session.getAttribute("configmappings"));
            mav.addObject("savedStatus", "updated");
            mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));
	    
	    //Get a list of other active sourceconfigurations
	    //These will show only for a target configuration
	    List<utConfiguration> sourceConfigurations = utconfigurationmanager.getAllActiveSourceConfigurations();
	    mav.addObject("sourceConfigurations", sourceConfigurations);
	    
            return mav;
        } //If the "Next Step" button was pressed.
        else {
            redirectAttr.addFlashAttribute("savedStatus", "updated");
            ModelAndView mav = new ModelAndView(new RedirectView("transport"));
            return mav;
        }

    }

    /**
     * The '/transport' GET request will display the clicked utConfiguration transport details form.
     *
     * @return	Will return the utConfiguration transport details form
     *
     * @Objects	transportDetails will hold a empty object or an object containing the existing transport details for the selected configuration
     *
     * @throws Exception
     *
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/transport", method = RequestMethod.GET)
    public ModelAndView viewTransportDetails(HttpSession session) throws Exception {
	
        ModelAndView mav = new ModelAndView();
	
	Integer configId = 0;
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/transport");

        //Get the utConfiguration details for the selected config
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);

        // Get organization directory name
        Organization orgDetails = organizationmanager.getOrganizationById(configurationDetails.getorgId());
	
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
        if (transportDetails == null) {
            transportDetails = new configurationTransport();

            if (configurationDetails.getType() == 1) {
                transportDetails.setfileLocation("/HELProductSuite/universalTranslator/" + orgDetails.getcleanURL() + "/input files/");
            } else {
                transportDetails.setfileLocation("/HELProductSuite/universalTranslator/" + orgDetails.getcleanURL() + "/output files/");
            }

            List<Integer> assocMessageTypes = new ArrayList<Integer>();
            assocMessageTypes.add(configurationDetails.getId());
            transportDetails.setmessageTypes(assocMessageTypes);
        }
	
	transportDetails.setHelRegistryId(orgDetails.getHelRegistryId());
	transportDetails.setHelSchemaName(orgDetails.getHelRegistrySchemaName());

        // Need to get any FTP fields
        List<configurationFTPFields> ftpFields = utconfigurationTransportManager.getTransportFTPDetails(transportDetails.getId());

        if (ftpFields.isEmpty()) {

            List<configurationFTPFields> emptyFTPFields = new ArrayList<configurationFTPFields>();
            configurationFTPFields pushFTPFields = new configurationFTPFields();
            pushFTPFields.setmethod(1);
            pushFTPFields.setdirectory("/sFTP/" + orgDetails.getcleanURL() + "/input/");

            configurationFTPFields getFTPFields = new configurationFTPFields();
            getFTPFields.setmethod(2);
            getFTPFields.setdirectory("/sFTP/" + orgDetails.getcleanURL() + "/output/");

            emptyFTPFields.add(pushFTPFields);
            emptyFTPFields.add(getFTPFields);

            transportDetails.setFTPFields(emptyFTPFields);
        } else {
            transportDetails.setFTPFields(ftpFields);
        }

        //get file drop fields
        List<configurationFileDropFields> fileDropFields = utconfigurationTransportManager.getTransFileDropDetails(transportDetails.getId());

        if (fileDropFields.isEmpty()) {

            List<configurationFileDropFields> emptyFileDropFields = new ArrayList<>();
            configurationFileDropFields pushRFields = new configurationFileDropFields();
            pushRFields.setMethod(1);
            pushRFields.setDirectory("/HELProductSuite/universalTranslator/" + orgDetails.getcleanURL() + "/input files/");

           configurationFileDropFields getRFields = new configurationFileDropFields();
            getRFields.setMethod(2);
            getRFields.setDirectory("/HELProductSuite/universalTranslator/" + orgDetails.getcleanURL() + "/output files/");

            emptyFileDropFields.add(pushRFields);
            emptyFileDropFields.add(getRFields);

            transportDetails.setFileDropFields(emptyFileDropFields);
        } else {
            transportDetails.setFileDropFields(fileDropFields);
        }

        //get WS fields
        List<configurationWebServiceFields> wsFields = utconfigurationTransportManager.getTransWSDetails(transportDetails.getId());

        if (wsFields.isEmpty()) {

            List<configurationWebServiceFields> emptyWSFields = new ArrayList<configurationWebServiceFields>();
            configurationWebServiceFields inboundWSFields = new configurationWebServiceFields();
            inboundWSFields.setMethod(1);
            List<configurationWebServiceSenders> inboundWSDomainList = new ArrayList<configurationWebServiceSenders>();
	    
            //need to modify to set domain
            inboundWSFields.setSenderDomainList(inboundWSDomainList);

            configurationWebServiceFields outboundWSFields = new configurationWebServiceFields();
            outboundWSFields.setMethod(2);

            emptyWSFields.add(inboundWSFields);
            emptyWSFields.add(outboundWSFields);

            transportDetails.setWebServiceFields(emptyWSFields);
        } else {
            transportDetails.setWebServiceFields(wsFields);
        }

        
        transportDetails.setconfigId(configId);
	transportDetails.setThreshold(configurationDetails.getThreshold());
        mav.addObject("transportDetails", transportDetails);
	
	if(transportDetails.getRestAPIType() == 2) {
	    session.setAttribute("showAllConfigOptions",false);
	}
	else {
	    session.setAttribute("showAllConfigOptions",true);
	}
	
        //Set the variable id to hold the current utConfiguration id
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions", session.getAttribute("showAllConfigOptions"));
	
        configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the list of available transport methods
        List transportMethods = utconfigurationTransportManager.getTransportMethodsByType(configurationDetails.getType());
	mav.addObject("transportMethods", transportMethods);

        //Get the list of available file delimiters
        List delimiters = messagetypemanager.getDelimiters();
        mav.addObject("delimiters", delimiters);

        //Get the list of available file types
        List fileTypes = utconfigurationmanager.getFileTypes();
        mav.addObject("fileTypes", fileTypes);

        //Get the list of available encodings
        List encodings = utconfigurationmanager.getEncodings();
        mav.addObject("encodings", encodings);
	
	//Get the list of available file types
        List zipTypes = utconfigurationmanager.getZipTypes();
        mav.addObject("zipTypes", zipTypes);
	
	//Get the list of available rest api types
        List restAPITypes = utconfigurationmanager.getrestAPITypes();
        mav.addObject("restAPITypes", restAPITypes);
	
	//Get the list of available rest api types
        List restAPIFunctions = utconfigurationmanager.getrestAPIFunctions(configurationDetails.getorgId());
        mav.addObject("restAPIFunctions", restAPIFunctions);

        return mav;
    }

    /**
     * The '/transport' POST request will submit the transport details
     *
     * @param session
     * @param	transportDetails	Will contain the contents of the transport form
     * @param result
     * @param redirectAttr
     * @param action
     * @param domain1
     *
     * @return	This function will either return to the transport details screen or redirect to the next step (Field Mappings)
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/transport", method = RequestMethod.POST)
    public ModelAndView updateTransportDetails(HttpSession session, @Valid @ModelAttribute(value = "transportDetails") configurationTransport transportDetails, BindingResult result, RedirectAttributes redirectAttr,
            @RequestParam String action, @RequestParam(value = "domain1", required = false) String domain1
    ) throws Exception {
	
	Integer configId = 0;
	
	if(session.getAttribute("manageconfigId") == null){  
	    ModelAndView mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        Integer currTransportId = transportDetails.getId();
	
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	if(configurationDetails.getstepsCompleted() < 2) {
	    configurationDetails.setstepsCompleted(2);
	    utconfigurationmanager.updateConfiguration(configurationDetails);
	}
	
        /* submit the updates */
        Integer transportId = (Integer) utconfigurationTransportManager.updateTransportDetails(transportDetails);
	
	configurationDetails.setThreshold(transportDetails.getThreshold());
	utconfigurationmanager.updateConfiguration(configurationDetails);
	
        /**
         * if it is a new transport, for web services, we add the domain sender if not, it is handled with add/edit already *
         */
        if (transportDetails.gettransportMethodId() == 6) { 
            if (configurationDetails.getType() == 1) {
                if (utconfigurationTransportManager.getWSSenderList(transportId).size() == 0) {
                    configurationWebServiceSenders confWSSender = new configurationWebServiceSenders();
                    confWSSender.setTransportId(transportId);
                    confWSSender.setDomain(domain1);
                    utconfigurationTransportManager.saveWSSender(confWSSender);
                }
            }
        }
	
	
	//No mappings needed for source configurations
	if(configurationDetails.getType() == 1) {
	    session.setAttribute("configmappings", 0);
	}
	else {
	    session.setAttribute("configmappings", 1);
	}
	
        if (transportDetails.getfileType() == 4 && configurationDetails.getType() == 2) {
            session.setAttribute("configHL7", true);
            session.setAttribute("configCCD", false);
        } else {
            session.setAttribute("configHL7", false);
            session.setAttribute("configCCD", false);
        }

        if ((transportDetails.getfileType() == 9 || transportDetails.getfileType() == 12) && configurationDetails.getType() == 2) {
	    session.setAttribute("configHL7", false);
	    session.setAttribute("configCCD", true);
        } else {
            session.setAttribute("configHL7", false);
            session.setAttribute("configCCD", false);
        }
	
	if(transportDetails.getRestAPIType() == 2) {
	     session.setAttribute("showAllConfigOptions", false);
	}
	
        /**
         * Need to set up the FTP information if any has been entered
         */
        if (transportDetails.gettransportMethodId() == 3 && !transportDetails.getFTPFields().isEmpty()) {
            for (configurationFTPFields ftpFields : transportDetails.getFTPFields()) {
                ftpFields.settransportId(transportId);
                utconfigurationTransportManager.saveTransportFTP(configurationDetails.getorgId(), ftpFields);
            }
        }
	
        /**
         * need to get file drop info if any has been entered *
         */
	if (transportDetails.gettransportMethodId() == 10 && !transportDetails.getFileDropFields().isEmpty()) {
	    fileSystem dir = new fileSystem();
	    
	     for (configurationFileDropFields fileDropFields : transportDetails.getFileDropFields()) {
		
		dir.createFileDroppedDirectory(fileDropFields.getDirectory().replace("/HELProductSuite/universalTranslator/",""));
		
                fileDropFields.setTransportId(transportId);
                utconfigurationTransportManager.saveTransportFileDrop(fileDropFields);
            }
        }
	
        if (transportDetails.gettransportMethodId() == 6 && !transportDetails.getWebServiceFields().isEmpty()) {
            if (configurationDetails.getType() == 2) {
                transportDetails.getWebServiceFields().get(1).setTransportId(transportId);
                utconfigurationTransportManager.saveTransportWebService(transportDetails.getWebServiceFields().get(1));
            } else {
                transportDetails.getWebServiceFields().get(0).setTransportId(transportId);
                utconfigurationTransportManager.saveTransportWebService(transportDetails.getWebServiceFields().get(0));
            }
        }

        /**
         * Need to set the associated messages types
         *
         * step 1: Remove all associations step 2: Loop through the selected message Types
         */
        /**
         * Step 1:
         */
        utconfigurationTransportManager.deleteTransportMessageTypes(transportId);

        /**
         * Step 2:
         */
        if (transportDetails.getmessageTypes() != null) {
            configurationTransportMessageTypes messageType;
            for (Integer selconfigId : transportDetails.getmessageTypes()) {
                messageType = new configurationTransportMessageTypes();
                messageType.setconfigId(selconfigId);
                messageType.setconfigTransportId(transportId);
                utconfigurationTransportManager.saveTransportMessageTypes(messageType);
            }
        }

        redirectAttr.addFlashAttribute("savedStatus", "updated");
	
        //If the "Save" button was pressed 
        if (action.equals("save") || transportDetails.getRestAPIType() == 2) {
            ModelAndView mav = new ModelAndView(new RedirectView("transport"));
            return mav;
        } else {
            //If the type of utConfiguration is for a source then send to message specs
            if (configurationDetails.getType() == 1) {
                
		//Check if passthru
		if(configurationDetails.getConfigurationType() == 2) {
		    ModelAndView mav = new ModelAndView(new RedirectView("scheduling"));
		    return mav;
		}
		else {
		    ModelAndView mav = new ModelAndView(new RedirectView("messagespecs"));
		    return mav;
		}
               
            } else {
		//Check if passthru
		if(configurationDetails.getConfigurationType() == 2) {
		    ModelAndView mav = new ModelAndView(new RedirectView("scheduling"));
		    return mav;
		}
		else {
		    ModelAndView mav = new ModelAndView(new RedirectView("messagespecs"));
		    return mav;
		}
            }
        }

    }

    /**
     * The '/messagespecs' GET request will display the utConfiguration message specs form.
     *
     * @return	Will return the utConfiguration message spec details form
     *
     * @Objects	transportDetails will hold a empty object or an object containing the existing transport details for the selected configuration
     *
     * @throws Exception
     *
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/messagespecs", method = RequestMethod.GET)
    public ModelAndView viewMessageSpecDetails(HttpSession session) throws Exception {
	
	Integer configId = 0;
	
	ModelAndView mav = new ModelAndView();
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/messagespecs");

        configurationMessageSpecs messageSpecs = utconfigurationmanager.getMessageSpecs(configId);
        if (messageSpecs == null) {
            messageSpecs = new configurationMessageSpecs();
            messageSpecs.setconfigId(configId);
        }
        mav.addObject("messageSpecs", messageSpecs);

        //Need to pass the selected transport Type
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
        mav.addObject("transportType", transportDetails.gettransportMethodId());
	mav.addObject("fileType", transportDetails.getfileType());

        //Set the variable id to hold the current utConfiguration id
        mav.addObject("id", configId);
	mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));
	
	
        //Get the utConfiguration details for the selected config
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	
	configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Need to get all available fields that can be used for the reportable fields
        List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        mav.addObject("availableFields", fields);

        return mav;
    }

    /**
     * The '/messagespecs' POST request submit all the utConfiguration message specs.
     *
     * @param messageSpecs Will contain the contents of the utConfiguration message spec form.
     *
     * @return	This function will either return to the message spec details screen or redirect to the next step (Field Mappings)
     *
     * @throws Exception
     *
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/messagespecs", method = RequestMethod.POST)
    public ModelAndView updateMessageSpecs(HttpSession session,@Valid @ModelAttribute(value = "messageSpecs") configurationMessageSpecs messageSpecs, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

       
        /**
         * Need to pass the selected transport Type
         */
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(messageSpecs.getconfigId());

        /**
         * Save/Update the configuration message specs
         */
        utconfigurationmanager.updateMessageSpecs(messageSpecs, transportDetails.getId(), transportDetails.getfileType());

        redirectAttr.addFlashAttribute("savedStatus", "updated");
	
	utConfiguration configDetails = utconfigurationmanager.getConfigurationById(messageSpecs.getconfigId());
	if(configDetails.getstepsCompleted() < 3) {
	    configDetails.setstepsCompleted(3);
	    utconfigurationmanager.updateConfiguration(configDetails);
	}
	
	//If excel enter in the ref_configexceldetails
	if(transportDetails.getfileType() == 11) {
	    utconfigurationmanager.updateExcelConfigDetails(configDetails.getorgId(),messageSpecs);
	}

        /**
         * If the "Save" button was pressed
         */
        if (action.equals("save")) {
            ModelAndView mav = new ModelAndView(new RedirectView("messagespecs"));
            return mav;
        } else {
	   ModelAndView mav = new ModelAndView(new RedirectView("mappings"));
	   return mav;
        }

    }

    /**
     * The '/mappings' GET request will determine based on the selected transport method what page to display. Either the choose fields page if 'online form' is selected or 'mappings' if a custom file is being uploaded.
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/mappings", method = RequestMethod.GET)
    public ModelAndView getConfigurationMappings(HttpSession session) throws Exception {
	
	Integer configId = 0;
	
	ModelAndView mav = new ModelAndView();
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/mappings");
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));

        //Get the completed steps for the selected utConfiguration;
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	
        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);

	Organization orgDetails = organizationmanager.getOrganizationById(configurationDetails.getorgId());
        configurationDetails.setOrgName(orgDetails.getOrgName());
       
        configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
	mav.addObject("configurationDetails", configurationDetails);

	
        //Get the transport fields
        List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("transportDetails", transportDetails);

        
        mav.addObject("selTransportMethod", transportDetails.gettransportMethodId());

        List validationTypes = messagetypemanager.getValidationTypes();
        mav.addObject("validationTypes", validationTypes);


        return mav;
    }

    /**
     * The 'saveFields' POST method will submit the changes to the form field settings for the selected utConfiguration. This method is only for configurations set for 'Online Form' as the data transportation method.
     *
     * @param session
     * @param	transportDetails	The field details from the form action	The field that will hold which button was pressed "Save" or "Next Step"
     * @param redirectAttr
     * @param transportMethod
     * @param action
     * @param errorHandling
     *
     * @return	This method will either redirect back to the Choose Fields page or redirect to the next step data translations page.
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/saveFields", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer saveFormFields(HttpSession session,
	    @ModelAttribute(value = "transportDetails") configurationTransport transportDetails, 
	    RedirectAttributes redirectAttr, @RequestParam String action, @RequestParam int transportMethod, @RequestParam int errorHandling) throws Exception {

	Integer configId = (Integer) session.getAttribute("manageconfigId");
	
	utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	if(configurationDetails.getstepsCompleted() < 4) {
	    configurationDetails.setstepsCompleted(4);
	    utconfigurationmanager.updateConfiguration(configurationDetails);
	}
	
        //Get the list of fields
        List<configurationFormFields> fields = transportDetails.getFields();
	
	if(fields != null) {
	    if(!fields.isEmpty()) {
		fields.stream().map((formField) -> {
		    if(formField.getAssociatedFieldDetails() != null) {
			if(formField.getAssociatedFieldDetails().contains("-")) {
			    String[] associatedFieldValues = formField.getAssociatedFieldDetails().split("-");
			    Integer associatedFieldId = Integer.parseInt(associatedFieldValues[0]);
			    Integer associatedFieldNo = Integer.parseInt(associatedFieldValues[1]);
			    formField.setAssociatedFieldId(associatedFieldId);
			    formField.setAssociatedFieldNo(associatedFieldNo);
			}
		    }
		    return formField;		    
		}).map((formField) -> {
		    if(formField.getAssociatedFieldDetails() != null) {
			if(formField.getAssociatedFieldId() == 0) {
			    formField.setUseField(false);
			}
			else {
			    formField.setUseField(formField.getUseField());
			}
		    }
		    else {
			formField.setUseField(formField.getUseField());
		    }
		    
		    return formField;		    
		}).forEachOrdered((formField) -> {
		    utconfigurationTransportManager.updateConfigurationFormFields(formField);
		});
	    }
	}

        //If the "Save" button was pressed 
        if (action.equals("save")) {
            return 1;

        } else {
            if (errorHandling == 1) {
                return 2;
            } else {
                return 1;
            }

        }

    }

    /**
     * The '/translations' GET request will display the data translations page for the selected transport Method
     * @param session
     * @return 
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/translations", method = RequestMethod.GET)
    public ModelAndView getConfigurationTranslations(HttpSession session) throws Exception {

        //Set the data translations array to get ready to hold data=
        List<configurationDataTranslations> translations = new CopyOnWriteArrayList<>();
	session.setAttribute("confgirationDataTranslastions", translations);
	
	List<configurationDataTranslations> preProcessingTranslations = new CopyOnWriteArrayList<>();
	session.setAttribute("confgirationDataPreProcessingTranslastions", preProcessingTranslations);
	
	List<configurationDataTranslations> postProcessingTranslations = new CopyOnWriteArrayList<>();
	session.setAttribute("confgirationDataPostProcessingTranslastions", postProcessingTranslations);
	
	Integer configId = 0;
	
	ModelAndView mav = new ModelAndView();
	
	if(session.getAttribute("manageconfigId") == null){  
	   mav = new ModelAndView(new RedirectView("list"));
           return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/translations");
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));

        //Get the completed steps for the selected utConfiguration;
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
	configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the transport fields
        List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        //Return a list of available crosswalks
        List<Crosswalks> crosswalks = messagetypemanager.getCrosswalks(1, 0, configurationDetails.getorgId());
        mav.addObject("crosswalks", crosswalks);
        mav.addObject("orgId", configurationDetails.getorgId());

        //Return a list of available macros
        List<Macros> macros = utconfigurationmanager.getMacros();
        mav.addObject("macros", macros);

        //Loop through list of macros to mark the ones that need
        //fields filled in
        List<Integer> macroLookUpList = new ArrayList<Integer>();
        for (Macros macro : macros) {
            if (macro.getfieldAQuestion() != null || macro.getfieldBQuestion() != null || macro.getcon1Question() != null || macro.getcon2Question() != null) {
                macroLookUpList.add(macro.getId());
            }
        }
        mav.addObject("macroLookUpList", macroLookUpList);

        return mav;
    }

    /**
     * The '/getMacroDetails.do' function will display the modal window for the selected macro form.
     *
     * @param macroId The id of the selected macro
     *
     */
    @RequestMapping(value = "/getMacroDetails.do", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getMacroDetails(@RequestParam(value = "macroId", required = true) Integer macroId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/macroDetails");

        Macros macroDetails = utconfigurationmanager.getMacroById(macroId);

        mav.addObject("fieldA_Question", macroDetails.getfieldAQuestion());
        mav.addObject("fieldB_Question", macroDetails.getfieldBQuestion());
        mav.addObject("Con1_Question", macroDetails.getcon1Question());
        mav.addObject("Con2_Question", macroDetails.getcon2Question());
        mav.addObject("populateFieldA", macroDetails.isPopulateFieldA());

        return mav;
    }

    /**
     * The '/translations' POST request will submit the selected data translations and save it to the data base.
     *
     */
    @RequestMapping(value = "/translations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer submitDataTranslations(HttpSession session, @RequestParam(value = "categoryId", required = true) Integer categoryId) throws Exception {

        Integer configId = 0;
	
	configId = (Integer) session.getAttribute("manageconfigId");
	
	utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	if(categoryId == 2) {
	    if(configurationDetails.getstepsCompleted() < 10) {
		configurationDetails.setstepsCompleted(10);
		utconfigurationmanager.updateConfiguration(configurationDetails);
	    }
	}
	else if(categoryId == 3) {
	    if(configurationDetails.getstepsCompleted() < 11) {
		configurationDetails.setstepsCompleted(11);
		utconfigurationmanager.updateConfiguration(configurationDetails);
	    }
	}
	else {
	    if(configurationDetails.getstepsCompleted() < 5) {
		configurationDetails.setstepsCompleted(5);
		utconfigurationmanager.updateConfiguration(configurationDetails);
	    }
	}
	
        //Delete all the data translations before creating
        //This will help with the jquery removing translations
        utconfigurationmanager.deleteDataTranslations(configId, categoryId);
	
	List<configurationDataTranslations> translations;
	
	if(categoryId == 1) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
	}
	else if(categoryId == 2) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPreProcessingTranslastions");
	}
	else {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPostProcessingTranslastions");
	}
	
	if(translations != null) {
	    if(!translations.isEmpty()) {
		 //Loop through the list of translations
		for (configurationDataTranslations translation : translations) {
		    utconfigurationmanager.saveDataTranslations(translation);
		}
	    }
	}
       
        return 1;
    }

    /**
     * The '/getTranslations.do' function will return the list of existing translations set up for the selected utConfiguration/transportMethod.
     *
     * @Return list of translations
     */
    @RequestMapping(value = "/getTranslations.do", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getTranslations(HttpSession session,@RequestParam(value = "reload", required = true) boolean reload, @RequestParam(value = "categoryId", required = true) Integer categoryId) throws Exception {

        ModelAndView mav = new ModelAndView();
	
	Integer configId = (Integer) session.getAttribute("manageconfigId");
        mav.setViewName("/administrator/configurations/existingTranslations");
	
	
	List<configurationDataTranslations> translations;
	if(categoryId == 1) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
	}
	else if(categoryId == 2) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPreProcessingTranslastions");
	}
	else {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPostProcessingTranslastions");
	}
	
        //only get the saved translations if reload == 0
        //We only want to retrieve the saved ones on initial load
        if (reload == false) {
            //Need to get a list of existing translations
            List<configurationDataTranslations> existingTranslations = utconfigurationmanager.getDataTranslationsWithFieldNo(configId, categoryId);

            String fieldName;
            String crosswalkName;
            String macroName;
            Map<String, String> defaultValues;
            String optionDesc;
            String optionValue;
	    
            for (configurationDataTranslations translation : existingTranslations) {
                //Get the field name by id
                fieldName = utconfigurationmanager.getFieldName(translation.getFieldId());
                translation.setfieldName(fieldName);

                //Get the crosswalk name by id
                if (translation.getCrosswalkId() != 0) {
                    defaultValues = new HashMap<>();
                    crosswalkName = messagetypemanager.getCrosswalkName(translation.getCrosswalkId());
                    translation.setcrosswalkName(crosswalkName);

                    /* Get values of crosswalk */
                    List crosswalkdata = messagetypemanager.getCrosswalkData(translation.getCrosswalkId());

                    Iterator cwDataIt = crosswalkdata.iterator();
                    while (cwDataIt.hasNext()) {
                        Object cwDatarow[] = (Object[]) cwDataIt.next();
                        optionDesc = (String) cwDatarow[2];
                        optionValue = (String) cwDatarow[0];

                        defaultValues.put(optionValue, optionDesc);

                    }

                    translation.setDefaultValues(defaultValues);
                }

                //Get the macro name by id
                if (translation.getMacroId() > 0) {
                    Macros macroDetails = utconfigurationmanager.getMacroById(translation.getMacroId());
                    macroName = macroDetails.getmacroShortName();
                    if (macroName.contains("DATE")) {
                        macroName = macroDetails.getmacroShortName() + " " + macroDetails.getdateDisplay();
                    }
                    translation.setMacroName(macroName);
                }

                translations.add(translation);
            }
        }
	
        mav.addObject("dataTranslations", translations);

        return mav;

    }

    /**
     * The '/setTranslations{params}' function will handle taking in a selected field and a selected crosswalk and add it to an array of translations. This array will be used when the form is submitted to associate to the existing utConfiguration / trasnort method combination.
     *
     * @param session
     * @param field
     * @param cwId
     * @param fieldText
     * @param cwText
     * @param macroId
     * @param fieldA
     * @param macroName
     * @param fieldB
     * @param passClear
     * @param constant2
     * @param constant1
     * @param categoryId
     * @return 
     * @throws java.lang.Exception 
     *
     */
    @RequestMapping(value = "/setTranslations{params}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView setTranslations(HttpSession session,
            @RequestParam(value = "f", required = true) Integer field, @RequestParam(value = "cw", required = true) Integer cwId, @RequestParam(value = "fText", required = true) String fieldText,
            @RequestParam(value = "CWText", required = true) String cwText, @RequestParam(value = "macroId", required = true) Integer macroId,
            @RequestParam(value = "macroName", required = true) String macroName, @RequestParam(value = "fieldA", required = false) String fieldA,
            @RequestParam(value = "fieldB") String fieldB, @RequestParam(value = "constant1") String constant1,
            @RequestParam(value = "constant2", required = false) String constant2, @RequestParam(value = "passClear") Integer passClear,
            @RequestParam(value = "categoryId", required = true) Integer categoryId
    ) throws Exception {
	
	Integer configId = (Integer) session.getAttribute("manageconfigId");
	
	List<configurationDataTranslations> translations;
	if(categoryId == 1) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
	}
	else if(categoryId == 2) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPreProcessingTranslastions");
	}
	else {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPostProcessingTranslastions");
	}
	
        int processOrder = translations.size() + 1;

        if (macroId == null) {
            macroId = 0;
            macroName = null;
        }
        if (cwId == null) {
            cwId = 0;
            cwText = null;
        }

        configurationDataTranslations translation = new configurationDataTranslations();
        translation.setconfigId(configId);
        translation.setFieldId(field);
        translation.setfieldName(fieldText);
        translation.setMacroId(macroId);
        translation.setMacroName(macroName);
        translation.setCrosswalkId(cwId);
        translation.setcrosswalkName(cwText);
        translation.setFieldA(fieldA);
        translation.setFieldB(fieldB);
        translation.setConstant1(constant1);
        translation.setConstant2(constant2);
        translation.setProcessOrder(processOrder);
        translation.setPassClear(passClear);
        translation.setCategoryId(categoryId);

        if (cwId > 0) {
            Map<String, String> defaultValues = new HashMap<>();
            String optionDesc;
            String optionValue;

            /* Get values of crosswalk */
            List crosswalkdata = messagetypemanager.getCrosswalkData(cwId);

            Iterator cwDataIt = crosswalkdata.iterator();
            while (cwDataIt.hasNext()) {
                Object cwDatarow[] = (Object[]) cwDataIt.next();
                optionDesc = (String) cwDatarow[2];
                optionValue = (String) cwDatarow[0];

                defaultValues.put(optionValue, optionDesc);

            }

            translation.setDefaultValues(defaultValues);
        }

        translations.add(translation);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/existingTranslations");
        mav.addObject("dataTranslations", translations);

        return mav;
    }

    /**
     * The 'removeTranslations{params}' function will handle removing a translation from translations array.
     *
     * @param session
     * @param	fieldId This will hold the field that is being removed
     * @param	processOrder	This will hold the process order of the field to be removed so we remove the correct field number as the same field could be in the list with different crosswalks
     * @param categoryId
      *
     * @return	1	The function will simply return a 1 back to the ajax call
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/removeTranslations{params}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer removeTranslation(HttpSession session,
	    @RequestParam(value = "fieldId", required = true) Integer fieldId, 
	    @RequestParam(value = "processOrder", required = true) Integer processOrder,
	    @RequestParam(value = "categoryId", required = true) Integer categoryId) throws Exception {

	List<configurationDataTranslations> translations;
	if(categoryId == 1) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
	}
	else if(categoryId == 2) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPreProcessingTranslastions");
	}
	else {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPostProcessingTranslastions");
	}
	
        Iterator<configurationDataTranslations> it = translations.iterator();
        int currProcessOrder;

        while (it.hasNext()) {
            configurationDataTranslations translation = it.next();
            if (translation.getFieldId() == fieldId && translation.getProcessOrder() == processOrder) {
                translations.remove(translation);
            } else if (translation.getProcessOrder() > processOrder) {
                currProcessOrder = translation.getProcessOrder();
                translation.setProcessOrder(currProcessOrder - 1);
            }
        }

        return 1;
    }

    /**
     * The 'updateTranslationProcessOrder{params}' function will handle removing a translation from translations array.
     *
     * @param session
     * @param currProcessOrder
     * @param newProcessOrder
     * @return 
     * @throws java.lang.Exception 
     *
     * @Return	1	The function will simply return a 1 back to the ajax call
     */
    @RequestMapping(value = "/updateTranslationProcessOrder{params}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer updateTranslationProcessOrder(HttpSession session,
	    @RequestParam(value = "currProcessOrder", required = true) Integer currProcessOrder, 
	    @RequestParam(value = "newProcessOrder", required = true) Integer newProcessOrder,
	    @RequestParam(value = "categoryId", required = true) Integer categoryId) throws Exception {

        List<configurationDataTranslations> translations;
	if(categoryId == 1) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
	}
	else if(categoryId == 2) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPreProcessingTranslastions");
	}
	else {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPostProcessingTranslastions");
	}
	
	Iterator<configurationDataTranslations> it = translations.iterator();

        while (it.hasNext()) {
            configurationDataTranslations translation = it.next();
            if (translation.getProcessOrder() == currProcessOrder) {
                translation.setProcessOrder(newProcessOrder);
            } else if (translation.getProcessOrder() == newProcessOrder) {
                translation.setProcessOrder(currProcessOrder);
            }
        }

        return 1;
    }

    

    /**
     * The '/connections' function will handle displaying the utConfiguration connections screen. The function will pass the existing connection objects for the selected utConfiguration.
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
    @RequestMapping(value = "/connections", method = RequestMethod.GET)
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
		
		if(srcconfigDetails.getMessageTypeId() > 0) {
		    srcconfigDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(srcconfigDetails.getMessageTypeId()).getName());
		}
		else {
		    srcconfigDetails.setMessageTypeName("N/A");
		}
		
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
		
		if(tgtconfigDetails.getMessageTypeId() > 0) {
		    tgtconfigDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(tgtconfigDetails.getMessageTypeId()).getName());
		}
		else {
		    tgtconfigDetails.setMessageTypeName("N/A");
		}
		
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
     * The '/createConnection' function will handle displaying the create utConfiguration connection screen.
     *
     * @return This function will display the new connection overlay
     */
    @RequestMapping(value = "/createConnection", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView createNewConnectionForm() throws Exception {
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connectionDetails");

        configurationConnection connectionDetails = new configurationConnection();
        mav.addObject("connectionDetails", connectionDetails);

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();
        mav.addObject("organizations", organizations);

        return mav;
    }

    /**
     * The '/editConnection' funtion will handle displaying the edit utConfiguration connection screen.
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
     *
     * @return configurations The available configurations
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableConfigurations.do", method = RequestMethod.GET)
    public @ResponseBody
    List<utConfiguration> getAvailableConfigurations(@RequestParam(value = "orgId", required = true) int orgId) throws Exception {

        List<utConfiguration> configurations = utconfigurationmanager.getActiveConfigurationsByOrgId(orgId);

        if (configurations != null) {
            for (utConfiguration configuration : configurations) {
                configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configuration.getId());

                configuration.setOrgName(organizationmanager.getOrganizationById(configuration.getorgId()).getOrgName());
		
		if(configuration.getMessageTypeId() > 0) {
		    configuration.setMessageTypeName(messagetypemanager.getMessageTypeById(configuration.getMessageTypeId()).getName());
		}
		else {
		    configuration.setMessageTypeName("N/A");
		}
		
                
                configuration.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
            }
        }

        return configurations;
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
     * The '/scheduling' GET request will display the scheduling page for the selected transport Method
     * @param session
     * @return 
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/scheduling", method = RequestMethod.GET)
    public ModelAndView getConfigurationSchedules(HttpSession session) throws Exception {

       Integer configId = 0;
	
	ModelAndView mav = new ModelAndView();
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/schedule");
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));

        //Get the completed steps for the selected utConfiguration;
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
	configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the schedule for the utConfiguration and selected transport method
        configurationSchedules scheduleDetails = utconfigurationmanager.getScheduleDetails(configId);

        if (scheduleDetails == null) {
            scheduleDetails = new configurationSchedules();
            scheduleDetails.setconfigId(configId);
        }
        mav.addObject("scheduleDetails", scheduleDetails);

        return mav;
    }

    /**
     * The '/scheduling' POST request will submit the scheduling settings for the selected utConfiguration.
     *
     * @param scheduleDetails The object that will hold the scheduling form fields
     *
     * @return This method will redirect the user back to the scheduling form page.
     */
    @RequestMapping(value = "/scheduling", method = RequestMethod.POST)
    public ModelAndView submitConfigurationSchedules(HttpSession session,@ModelAttribute(value = "scheduleDetails") configurationSchedules scheduleDetails, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

	Integer configId = (Integer) session.getAttribute("manageconfigId");
	
	utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	if(configurationDetails.getstepsCompleted() < 6) {
	    configurationDetails.setstepsCompleted(6);
	    utconfigurationmanager.updateConfiguration(configurationDetails);
	}
	
	//Set default values based on what schedule type is selected
        //This will help in case the user was switching around selecting
        //values before they saved
        //Manually
        if (scheduleDetails.gettype() == 1 || scheduleDetails.gettype() == 5) {
            scheduleDetails.setprocessingType(0);
            scheduleDetails.setnewfileCheck(0);
            scheduleDetails.setprocessingDay(0);
            scheduleDetails.setprocessingTime(0);
        } //Daily
        else if (scheduleDetails.gettype() == 2) {
            scheduleDetails.setprocessingDay(0);
            if (scheduleDetails.getprocessingType() == 1) {
                scheduleDetails.setnewfileCheck(0);
            } else {
                scheduleDetails.setprocessingTime(0);
            }
        } //Weekly
        else if (scheduleDetails.gettype() == 3) {
            scheduleDetails.setprocessingType(0);
            scheduleDetails.setnewfileCheck(0);
        } //Monthly
        else if (scheduleDetails.gettype() == 3) {
            scheduleDetails.setprocessingType(0);
            scheduleDetails.setnewfileCheck(0);
            scheduleDetails.setprocessingDay(0);
        }

        utconfigurationmanager.saveSchedule(scheduleDetails);

        redirectAttr.addFlashAttribute("savedStatus", "updated");
	
	boolean HL7Val = (boolean) session.getAttribute("configHL7");

        if ("save".equals(action)) {
	    
	    if(configurationDetails.getConfigurationType() == 2) {
		 ModelAndView mav = new ModelAndView(new RedirectView("/administrator/configurations/list?msg=updated"));
		return mav;
	    }
	    else {
		 ModelAndView mav = new ModelAndView(new RedirectView("scheduling"));
		return mav;
	    }
           
        } else if (HL7Val) {
            ModelAndView mav = new ModelAndView(new RedirectView("HL7"));
            return mav;
        } else {
            ModelAndView mav = new ModelAndView(new RedirectView("preprocessing"));
            return mav;
        }

    }

    /**
     * The '/HL7' GET request will display the HL7 customization form.
     */
    @RequestMapping(value = "/HL7", method = RequestMethod.GET)
    public ModelAndView getHL7Form(HttpSession session) throws Exception {

        Integer configId = 0;
	
	ModelAndView mav = new ModelAndView();
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/HL7");
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));

        //Get the completed steps for the selected utConfiguration;
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
        configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
        configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Set the variable to hold the number of completed steps for this utConfiguration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        HL7Details hl7Details = utconfigurationmanager.getHL7Details(configId);
        int HL7Id = 0;

        /* If null then create an empty HL7 Detail object */
        if (hl7Details == null) {
            /* Get a list of available HL7 Sepcs */
            List<mainHL7Details> HL7Specs = sysAdminManager.getHL7List();
            mav.addObject("HL7Specs", HL7Specs);
        } else {
            HL7Id = hl7Details.getId();

            /* Get a list of HL7 Segments */
            List<HL7Segments> HL7Segments = utconfigurationmanager.getHL7Segments(HL7Id);

            /* Get a list of HL7Elements */
            if (!HL7Segments.isEmpty()) {
                for (HL7Segments segment : HL7Segments) {

                    List<HL7Elements> HL7Elments = utconfigurationmanager.getHL7Elements(HL7Id, segment.getId());

                    if (!HL7Elments.isEmpty()) {

                        for (HL7Elements element : HL7Elments) {
                            List<HL7ElementComponents> components = utconfigurationmanager.getHL7ElementComponents(element.getId());
                            element.setelementComponents(components);
                        }

                        segment.setHL7Elements(HL7Elments);
                    }

                }
            }
            hl7Details.setHL7Segments(HL7Segments);

            mav.addObject("HL7Details", hl7Details);

        }

        //Get the transport fields
        List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        return mav;
    }

    /**
     * The '/loadHL7Spec' will load the utConfiguration HL7 specs from one that was chosen from the list of standard hl7 specs.
     *
     * @param configId The id of the utConfiguration to attach the HL7 spec to
     * @param hl7SpecId The id of the selected hl7 standard spec
     *
     * @return This function will return a 1 back to the calling jquery call.
     */
    @RequestMapping(value = "/loadHL7Spec", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer loadHL7Spec(@RequestParam int configId, @RequestParam int hl7SpecId) throws Exception {

        mainHL7Details hl7Specs = sysAdminManager.getHL7Details(hl7SpecId);

        HL7Details newHL7 = new HL7Details();
        newHL7.setconfigId(configId);
        newHL7.setfieldSeparator(hl7Specs.getfieldSeparator());
        newHL7.setcomponentSeparator(hl7Specs.getcomponentSeparator());
        newHL7.setEscapeChar(hl7Specs.getEscapeChar());

        int hl7Id = utconfigurationmanager.saveHL7Details(newHL7);

        List<mainHL7Segments> segments = sysAdminManager.getHL7Segments(hl7SpecId);

        for (mainHL7Segments segment : segments) {

            HL7Segments newHL7Segment = new HL7Segments();
            newHL7Segment.sethl7Id(hl7Id);
            newHL7Segment.setsegmentName(segment.getsegmentName());
            newHL7Segment.setdisplayPos(segment.getdisplayPos());

            int segmentId = utconfigurationmanager.saveHL7Segment(newHL7Segment);

            List<mainHL7Elements> elements = sysAdminManager.getHL7Elements(hl7SpecId, segment.getId());

            for (mainHL7Elements element : elements) {

                HL7Elements newHL7Element = new HL7Elements();
                newHL7Element.sethl7Id(hl7Id);
                newHL7Element.setsegmentId(segmentId);
                newHL7Element.setelementName(element.getelementName());
                newHL7Element.setdefaultValue(element.getdefaultValue());
                newHL7Element.setdisplayPos(element.getdisplayPos());

                utconfigurationmanager.saveHL7Element(newHL7Element);
            }

        }

        return 1;

    }

    /**
     * The '/HL7' POST request save all the hl7 custom settings
     */
    @RequestMapping(value = "/HL7", method = RequestMethod.POST)
    public ModelAndView saveHL7Customization(@ModelAttribute(value = "HL7Details") HL7Details HL7Details, RedirectAttributes redirectAttr) throws Exception {

        /* Update the details of the hl7 */
        utconfigurationmanager.updateHL7Details(HL7Details);

        List<HL7Segments> segments = HL7Details.getHL7Segments();

        try {
            if (null != segments && segments.size() > 0) {

                for (HL7Segments segment : segments) {

                    /* Update each segment */
                    utconfigurationmanager.updateHL7Segments(segment);

                    /* Get the list of segment elements */
                    List<HL7Elements> elements = segment.getHL7Elements();

                    if (null != elements && elements.size() > 0) {

                        for (HL7Elements element : elements) {
                            utconfigurationmanager.updateHL7Elements(element);


                            /* Get the list of segment element components */
                            List<HL7ElementComponents> components = element.getelementComponents();

                            if (null != components && components.size() > 0) {
                                for (HL7ElementComponents component : components) {
                                    utconfigurationmanager.updateHL7ElementComponent(component);
                                }
                            }

                        }

                    }

                }
            }
        } catch (Exception e) {

        }

        redirectAttr.addFlashAttribute("savedStatus", "updated");
        ModelAndView mav = new ModelAndView(new RedirectView("HL7"));
        return mav;

    }

    /**
     * The '/newHL7Segment' GET request will be used to display the blank new HL7 Segment screen (In a modal)
     *
     *
     * @return	The HL7 Segment blank form page
     *
     * @Objects	An object that will hold all the form fields of a new HL7 Segment
     *
     */
    @RequestMapping(value = "/newHL7Segment", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView newHL7Segment(@RequestParam(value = "hl7Id", required = true) int hl7Id, @RequestParam(value = "nextPos", required = true) int nextPos) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/HL7Segment");

        HL7Segments segmentDetails = new HL7Segments();
        segmentDetails.sethl7Id(hl7Id);
        segmentDetails.setdisplayPos(nextPos);

        mav.addObject("HL7SegmentDetails", segmentDetails);

        return mav;
    }

    /**
     * The '/saveHL7Segment' POST request will handle submitting the new HL7 Segment
     *
     * @param HL7SegmentDetails	The object containing the HL7 Segment form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     *
     * @return	Will return the HL7 Customization page on "Save"
     *
     * @throws Exception
     */
    @RequestMapping(value = "/saveHL7Segment", method = RequestMethod.POST)
    public ModelAndView saveHL7Segment(@ModelAttribute(value = "HL7SegmentDetails") HL7Segments HL7SegmentDetails, RedirectAttributes redirectAttr) throws Exception {

        utconfigurationmanager.saveHL7Segment(HL7SegmentDetails);

        redirectAttr.addFlashAttribute("savedStatus", "savedSegment");
        ModelAndView mav = new ModelAndView(new RedirectView("HL7"));
        return mav;
    }

    /**
     * The '/newHL7Element' GET request will be used to display the blank new HL7 Segment Element screen (In a modal)
     *
     *
     * @return	The HL7 Segment Element blank form page
     *
     * @Objects	An object that will hold all the form fields of a new HL7 Segment Element
     *
     */
    @RequestMapping(value = "/newHL7Element", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView newHL7Element(@RequestParam(value = "hl7Id", required = true) int hl7Id, @RequestParam(value = "segmentId", required = true) int segmentId, @RequestParam(value = "nextPos", required = true) int nextPos) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/HL7Element");

        HL7Elements elementDetails = new HL7Elements();
        elementDetails.sethl7Id(hl7Id);
        elementDetails.setsegmentId(segmentId);
        elementDetails.setdisplayPos(nextPos);

        mav.addObject("HL7ElementDetails", elementDetails);

        return mav;
    }

    /**
     * The '/saveHL7Element' POST request will handle submitting the new HL7 Segment Element
     *
     * @param HL7ElementDetails	The object containing the HL7 Segment Element form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     *
     * @return	Will return the HL7 Customization page on "Save"
     *
     * @throws Exception
     */
    @RequestMapping(value = "/saveHL7Element", method = RequestMethod.POST)
    public ModelAndView saveHL7Element(@ModelAttribute(value = "HL7ElementDetails") HL7Elements HL7ElementDetails, RedirectAttributes redirectAttr) throws Exception {

        utconfigurationmanager.saveHL7Element(HL7ElementDetails);

        redirectAttr.addFlashAttribute("savedStatus", "savedElement");
        ModelAndView mav = new ModelAndView(new RedirectView("HL7"));
        return mav;
    }

    /**
     * The '/newHL7Component' GET request will be used to display the blank new HL7 Element Component screen (In a modal)
     *
     *
     * @return	The HL7 Element Component blank form page
     *
     * @Objects	An object that will hold all the form fields of a new HL7 Element Component
     *
     */
    @RequestMapping(value = "/newHL7Component", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView newHL7Component(HttpSession session,@RequestParam(value = "elementId", required = true) int elementId, @RequestParam(value = "nextPos", required = true) int nextPos) throws Exception {

        Integer configId = (Integer) session.getAttribute("manageconfigId");
	
	ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/HL7Component");

        HL7ElementComponents componentDetails = new HL7ElementComponents();
        componentDetails.setelementId(elementId);
        componentDetails.setdisplayPos(nextPos);

        mav.addObject("HL7ComponentDetails", componentDetails);

        //Get the transport fields
        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
        List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        return mav;
    }

    /**
     * The '/saveHL7Component' POST request will handle submitting the new HL7 Segment Element
     *
     * @param HL7ComponentDetails The object containing the HL7 Element Component form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     *
     * @return	Will return the HL7 Customization page on "Save"
     *
     * @throws Exception
     */
    @RequestMapping(value = "/saveHL7Component", method = RequestMethod.POST)
    public ModelAndView saveHL7Component(@ModelAttribute(value = "HL7ComponentDetails") HL7ElementComponents HL7ComponentDetails, RedirectAttributes redirectAttr) throws Exception {

        utconfigurationmanager.saveHL7Component(HL7ComponentDetails);

        redirectAttr.addFlashAttribute("savedStatus", "savedComponent");
        ModelAndView mav = new ModelAndView(new RedirectView("HL7"));
        return mav;
    }

    /**
     * The 'testFTPConnection.do' method will test the FTP connection paramenters.
     */
    @RequestMapping(value = "/testFTPConnection.do", method = RequestMethod.GET)
    public @ResponseBody
    String testFTPConnection(@RequestParam int method, @RequestParam int id, @RequestParam int configId) throws Exception {

        Organization orgDetails = organizationmanager.getOrganizationById(utconfigurationmanager.getConfigurationById(configId).getorgId());

        /* get the FTP Details */
        configurationFTPFields ftpDetails;
        if (method == 1) {
            ftpDetails = utconfigurationTransportManager.getTransportFTPDetailsPull(id);
        } else {
            ftpDetails = utconfigurationTransportManager.getTransportFTPDetailsPush(id);
        }

        String connectionResponse = null;

        /* SFTP */
        if ("SFTP".equals(ftpDetails.getprotocol())) {

            JSch jsch = new JSch();
            Session session = null;
            ChannelSftp channel = null;
            FileInputStream localFileStream = null;

            String user = ftpDetails.getusername();
            int port = ftpDetails.getport();
            String host = ftpDetails.getip();

            try {
                if (ftpDetails.getcertification() != null && !"".equals(ftpDetails.getcertification())) {

                    File newFile = null;

                    fileSystem dir = new fileSystem();
                    dir.setDir(orgDetails.getcleanURL(), "certificates");

                    jsch.addIdentity(new File(dir.getDir() + ftpDetails.getcertification()).getAbsolutePath());
                    session = jsch.getSession(user, host, port);
                } else if (ftpDetails.getpassword() != null && !"".equals(ftpDetails.getpassword())) {
                    session = jsch.getSession(user, host, port);
                    session.setPassword(ftpDetails.getpassword());
                }

                session.setConfig("StrictHostKeyChecking", "no");
                session.setTimeout(2000);

                session.connect();

                channel = (ChannelSftp) session.openChannel("sftp");

                try {
                    channel.connect();

                    if (ftpDetails.getdirectory() != null && !"".equals(ftpDetails.getdirectory())) {
                        try {
                            channel.cd(ftpDetails.getdirectory());
                            connectionResponse = "Connected to the Directory " + ftpDetails.getdirectory();
                        } catch (Exception e) {
                            connectionResponse = "The Directory " + ftpDetails.getdirectory() + " was not found";
                        }
                    } else {
                        connectionResponse = "connected";
                    }

                    channel.disconnect();
                    session.disconnect();
                } catch (Exception e) {
                    connectionResponse = "Connecton not valid";
                    channel.disconnect();
                    session.disconnect();
                }

            } catch (Exception e) {
                connectionResponse = "Connecton not valid";
                session.disconnect();
            }

        } 
	/* FTP OR FTPS */ 
	else {
	    
	    try {
		JSch jsch = new JSch();
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		
		Session ftpsession =  jsch.getSession(ftpDetails.getusername(), ftpDetails.getip(), ftpDetails.getport());
		ftpsession.setConfig(config);
		ftpsession.setPassword(ftpDetails.getpassword());
		ftpsession.connect();
		connectionResponse = "SFTP Connection Success";
	    }
	    catch (Exception ex) {
		connectionResponse = "SFTP Failed - " + ex.getMessage();
	    }
        }

        return connectionResponse;

    }

    /**
     * The '/preprocessing' GET request will display the utConfiguration preprocessing page
     */
    @RequestMapping(value = "/preprocessing", method = RequestMethod.GET)
    public ModelAndView getPreProcessing(HttpSession session) throws Exception {

       
        ModelAndView mav = new ModelAndView();
	Integer configId = 0;
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/preprocessing");
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));

        //Get the completed steps for the selected utConfiguration;
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
	configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the transport fields
        List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        //Return a list of available macros
        List<Macros> macros = utconfigurationmanager.getMacrosByCategory(2);
        mav.addObject("macros", macros);

        //Loop through list of macros to mark the ones that need
        //fields filled in
        List<Integer> macroLookUpList = new ArrayList<Integer>();
        for (Macros macro : macros) {
            if (macro.getfieldAQuestion() != null || macro.getfieldBQuestion() != null || macro.getcon1Question() != null || macro.getcon2Question() != null) {
                macroLookUpList.add(macro.getId());
            }
        }
        mav.addObject("macroLookUpList", macroLookUpList);

	if(transportDetails.getRestAPIType() == 2) {
	    session.setAttribute("showAllConfigOptions", false);
	}
	else {
	    session.setAttribute("showAllConfigOptions", true);
	}
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));

        return mav;
    }

    /**
     * The '/postprocessing' GET request will display the utConfiguration post processing page
     */
    @RequestMapping(value = "/postprocessing", method = RequestMethod.GET)
    public ModelAndView getPostProcessing(HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
	Integer configId = 0;
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/postprocessing");
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));

        //Get the completed steps for the selected utConfiguration;
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
	configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the transport fields
        List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        //Return a list of available macros
        List<Macros> macros = utconfigurationmanager.getMacrosByCategory(3);
        mav.addObject("macros", macros);

        //Loop through list of macros to mark the ones that need
        //fields filled in
        List<Integer> macroLookUpList = new ArrayList<Integer>();
        for (Macros macro : macros) {
            if (macro.getfieldAQuestion() != null || macro.getfieldBQuestion() != null || macro.getcon1Question() != null || macro.getcon2Question() != null) {
                macroLookUpList.add(macro.getId());
            }
        }
        mav.addObject("macroLookUpList", macroLookUpList);

	if(transportDetails.getRestAPIType() == 2) {
	    session.setAttribute("showAllConfigOptions", false);
	}
	else {
	    session.setAttribute("showAllConfigOptions", true);
	}
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));

        return mav;
    }

    /**
     * The '/removeElementComponent.do' function will remove the selected HL7 element component
     *
     * @param componentId The selected id of the element component
     *
     */
    @RequestMapping(value = "/removeElementComponent.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer removeElementComponent(@RequestParam(value = "componentId", required = true) int componentId) {

        utconfigurationmanager.removeHL7ElementComponent(componentId);

        return 1;
    }

    /**
     * The '/removeElement.do' function will remove the selected HL7 element
     *
     * @param componentId The selected id of the element
     *
     */
    @RequestMapping(value = "/removeElement.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer removeElement(@RequestParam(value = "elementId", required = true) int elementId) {

        utconfigurationmanager.removeHL7Element(elementId);

        return 1;
    }

    /**
     * The '/removeSegment.do' function will remove the selected HL7 segment
     *
     * @param segmentId The selected id of the segment
     *
     */
    @RequestMapping(value = "/removeSegment.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer removeSegment(@RequestParam(value = "segmentId", required = true) int segmentId) {

        utconfigurationmanager.removeHL7Segment(segmentId);

        return 1;
    }

    /**
     * The '/CCD' GET request will display the CCD customization form.
     */
    @RequestMapping(value = "/CCD", method = RequestMethod.GET)
    public ModelAndView getCCDForm(HttpSession session) throws Exception {

        Integer configId = 0;
	
	ModelAndView mav = new ModelAndView();
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/CCD");
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	

        //Set the variable to hold the number of completed steps for this utConfiguration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        //Get the completed steps for the selected utConfiguration;
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
        configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
        configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        List<configurationCCDElements> ccdElements = utconfigurationmanager.getCCDElements(configId);
        mav.addObject("ccdElements", ccdElements);
	
	if(transportDetails.getRestAPIType() == 2) {
	    session.setAttribute("showAllConfigOptions", false);
	}
	else {
	    session.setAttribute("showAllConfigOptions", true);
	}
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));

        return mav;

    }

    /**
     * The '/createNewCCDElement' function will handle displaying the create CCD Element screen.
     *
     * @return This function will display the new ccd element overlay
     */
    @RequestMapping(value = "/createNewCCDElement", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView createNewCCDElement(HttpSession session) throws Exception {
	
	Integer configId = (Integer) session.getAttribute("manageconfigId");
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/ccdElement");

        configurationCCDElements ccdElement = new configurationCCDElements();
        ccdElement.setConfigId(configId);
        mav.addObject("ccdElement", ccdElement);

        //Get the transport fields
        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
        List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        return mav;
    }

    /**
     * The '/editCCDElement' function will handle displaying the edit CCD Element screen.
     *
     * @return This function will display the new ccd element overlay
     */
    @RequestMapping(value = "/editCCDElement", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView editCCDElement(HttpSession session,@RequestParam(value = "elementId", required = true) int elementId) throws Exception {

        Integer configId = (Integer) session.getAttribute("manageconfigId");
	
	ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/ccdElement");

        configurationCCDElements ccdElement = utconfigurationmanager.getCCDElement(elementId);
        mav.addObject("ccdElement", ccdElement);

        //Get the transport fields
        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
        List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        return mav;
    }

    /**
     * The '/saveCCDElement' POST request will handle submitting the new HL7 Segment Element
     *
     * @param configurationCCDElements The object containing the CCD Element form fields
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     *
     * @return	Will return the CCD Customization page on "Save"
     *
     * @throws Exception
     */
    @RequestMapping(value = "/saveCCDElement", method = RequestMethod.POST)
    public ModelAndView saveCCDElement(@ModelAttribute(value = "ccdElement") configurationCCDElements ccdElement, RedirectAttributes redirectAttr) throws Exception {

        utconfigurationmanager.saveCCDElement(ccdElement);

        redirectAttr.addFlashAttribute("savedStatus", "savedElement");
        ModelAndView mav = new ModelAndView(new RedirectView("CCD"));
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
    @RequestMapping(value = "/getDomainSenders.do", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView getDomainSenders(@RequestParam int transportId) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/domainSenders");
        configurationWebServiceFields cwsf = new configurationWebServiceFields();
        cwsf.setTransportId(transportId);
        cwsf.setSenderDomainList(utconfigurationTransportManager.getWSSenderList(transportId));
        mav.addObject("cwsf", cwsf);
        return mav;

    }

    /**
     * The '/saveDomainSenders.do' POST request will update or add new senders.
     *
     * @param configurationWebServiceFields It will have the transportId and the list of sender domains
     *
     * @return The method will an updated configurationWebServiceFields containing new sender domains
     */
    @RequestMapping(value = "/saveDomainSenders.do", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView saveDomainSenders(HttpServletRequest request, @ModelAttribute(value = "cwsf") configurationWebServiceFields cwsf) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/domainSenders");
        List<configurationWebServiceSenders> domainList = cwsf.getSenderDomainList();
        String success = "";
        String senders = "";
        for (configurationWebServiceSenders confWSSender : domainList) {
            if (confWSSender.getDomain().length() != 0) {
                confWSSender.setTransportId(cwsf.getTransportId());
                utconfigurationTransportManager.saveWSSender(confWSSender);
                success = "Updated!";
                senders = senders + confWSSender.getDomain() + ",";
            } else if (confWSSender.getDomain().length() == 0 && confWSSender.getId() != 0) {
                utconfigurationTransportManager.deleteWSSender(confWSSender);
                success = "Updated!";
            }
        }
        if (senders.length() != 0) {
            senders = senders.substring(0, senders.length() - 1);
        }

        configurationWebServiceFields cwsfNew = new configurationWebServiceFields();
        cwsfNew.setTransportId(cwsf.getTransportId());
        cwsfNew.setSenderDomainList(utconfigurationTransportManager.getWSSenderList(cwsf.getTransportId()));

        mav.addObject("cwsf", cwsfNew);
        mav.addObject("success", success);
        mav.addObject("senders", senders);
        return mav;

    }
    
    
    /**
     * The 'copyConfiguration.do' method will copy the selected utConfiguration.
     */
    @RequestMapping(value = "/copyConfiguration.do", method = RequestMethod.POST)
    public @ResponseBody
    Integer copyConfiguration(@RequestParam int configId) throws Exception {

        utConfiguration configDetails = utconfigurationmanager.getConfigurationById(configId);
	
	//New Configuration
	utConfiguration newConfig = new utConfiguration();
	newConfig.setorgId(configDetails.getorgId());
	newConfig.setStatus(false);
	newConfig.setType(configDetails.getType());
	newConfig.setMessageTypeId(configDetails.getMessageTypeId());
	newConfig.setstepsCompleted(6);
	newConfig.setconfigName(configDetails.getconfigName() + " (COPY)");
	newConfig.setsourceType(configDetails.getsourceType());
	newConfig.setThreshold(configDetails.getThreshold());
	
	//Save new Configuration
	Integer id = (Integer) utconfigurationmanager.createConfiguration(newConfig);
	
	//Get the existing transport details
	configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
	
	//Save new transport Details
	configurationTransport newTransportDetails = new configurationTransport();
	newTransportDetails.setconfigId(id);
	newTransportDetails.setfileLocation(transportDetails.getfileLocation());
	newTransportDetails.setautoRelease(true);
	newTransportDetails.setstatus(true);
	newTransportDetails.setclearRecords(true);
	newTransportDetails.setmaxFileSize(transportDetails.getmaxFileSize());
	newTransportDetails.setMassTranslation(true);
	
	Integer transportDetailId = utconfigurationTransportManager.updateTransportDetails(newTransportDetails);
	
	configurationTransportMessageTypes newConfigTransportMessageType = new configurationTransportMessageTypes();
	newConfigTransportMessageType.setconfigId(id);
	newConfigTransportMessageType.setconfigTransportId(transportDetailId);
	
	utconfigurationTransportManager.saveTransportMessageTypes(newConfigTransportMessageType);
		
	//Get the message specs
	configurationMessageSpecs messageSpecs = utconfigurationmanager.getMessageSpecs(configId);
	
	configurationMessageSpecs newMessageSpecs = new configurationMessageSpecs();
	newMessageSpecs.setconfigId(id);
	newMessageSpecs.settemplateFile(messageSpecs.gettemplateFile());
	newMessageSpecs.setmessageTypeCol(messageSpecs.getmessageTypeCol());
	newMessageSpecs.setmessageTypeVal(messageSpecs.getmessageTypeVal());
	newMessageSpecs.settargetOrgCol(messageSpecs.gettargetOrgCol());
	newMessageSpecs.setcontainsHeaderRow(messageSpecs.getcontainsHeaderRow());
	newMessageSpecs.setrptField1(messageSpecs.getrptField1());
	newMessageSpecs.setrptField2(messageSpecs.getrptField2());
	newMessageSpecs.setrptField3(messageSpecs.getrptField3());
	newMessageSpecs.setrptField4(messageSpecs.getrptField4());
	newMessageSpecs.setSourceSubOrgCol(messageSpecs.getSourceSubOrgCol());
	newMessageSpecs.setExcelstartrow(messageSpecs.getExcelstartrow());
	newMessageSpecs.setExcelskiprows(messageSpecs.getExcelskiprows());
	newMessageSpecs.setParsingTemplate(messageSpecs.getParsingTemplate());
	
	// Save/Update the utConfiguration message specs
        utconfigurationmanager.updateMessageSpecs(newMessageSpecs);
	
	//Need to get a list of existing translations
        List<configurationDataTranslations> existingTranslations = utconfigurationmanager.getDataTranslationsWithFieldNo(configId, 1);

	//Get form fields
	List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        
	if(fields != null) {
	    if(!fields.isEmpty()) {
		for(configurationFormFields field : fields) {
		    Integer currFormFieldId = field.getId();
		    
		    configurationFormFields newFormField = new configurationFormFields();
		    newFormField.setconfigId(id);
		    newFormField.setAssociatedFieldId(field.getAssociatedFieldId());
		    newFormField.settransportDetailId(transportDetailId);
		    newFormField.setFieldNo(field.getFieldNo());
		    newFormField.setFieldDesc(field.getFieldDesc());
		    newFormField.setValidationType(field.getValidationType());
		    newFormField.setRequired(field.getRequired());
		    newFormField.setUseField(field.getUseField());
		    
		    Integer formFieldId = utconfigurationTransportManager.saveConfigurationFormFields(newFormField);
		    
		    if(existingTranslations != null) {
			if(!existingTranslations.isEmpty()) {
			    for(configurationDataTranslations translation : existingTranslations) {
				if(currFormFieldId.equals(translation.getFieldId())) {
				    configurationDataTranslations newTranslation = new configurationDataTranslations();
				    newTranslation.setconfigId(id);
				    newTranslation.setFieldId(formFieldId);
				    newTranslation.setCrosswalkId(translation.getCrosswalkId());
				    newTranslation.setMacroId(translation.getMacroId());
				    newTranslation.setPassClear(translation.getPassClear());
				    newTranslation.setFieldA(translation.getFieldA());
				    newTranslation.setFieldB(translation.getFieldB());
				    newTranslation.setConstant1(translation.getConstant1());
				    newTranslation.setConstant2(translation.getConstant2());
				    newTranslation.setProcessOrder(translation.getProcessOrder());
				    newTranslation.setCategoryId(translation.getCategoryId());
				    newTranslation.setDefaultValue(translation.getDefaultValue());

				    utconfigurationmanager.saveDataTranslations(newTranslation);
				}
			    }
			}
		    }
		}
	    }
	}
	
	//Save new schedule
	configurationSchedules newSchedule = new configurationSchedules();
	newSchedule.setconfigId(id);
	newSchedule.settype(5);
	
	utconfigurationmanager.saveSchedule(newSchedule);

        return id;

    }

    /**
     * The '/getHELRegistryConfigurations' GET request will return a list of registry configurations 
     *
     *
     * @return The function will return a list of active registry configurations
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/getHELRegistryConfigurations", method = RequestMethod.GET)
    public @ResponseBody List<configuration> getHELRegistryOrganizations() throws Exception {
	
	List<configuration> registryConfigurations = registryconfigurationmanager.getAllActiveConfigurations();
	
	return registryConfigurations;
    }
    
    
    /**
     * The '/getSourceConfigurationFields' GET request will return a list of available fields for the 
     * passed in HEL registry configuration
     *
     * @param helConfigId
     * @return The function will return a list of available fields for the passed in registry configurations
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/getSourceConfigurationFields", method = RequestMethod.GET)
    public @ResponseBody ModelAndView getSourceConfigurationFields(@RequestParam Integer sourceConfigId) throws Exception {
	
	ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/sourceConfigurationFields");
	List<configurationFormFields> sourceConfigurationFields = utconfigurationTransportManager.getConfigurationFieldsToCopy(sourceConfigId);
	mav.addObject("sourceConfigurationFields",sourceConfigurationFields);
	
	return mav;
    }
    
    /**
     * The 'deleteConfiguration.do' method will makke the passed in configuration as deleted.
     */
    @RequestMapping(value = "/deleteConfiguration.do", method = RequestMethod.POST)
    public @ResponseBody
    Integer deleteConfiguration(@RequestParam int configId) throws Exception {

        utConfiguration configDetails = utconfigurationmanager.getConfigurationById(configId);
	configDetails.setDeleted(true);
	
	utconfigurationmanager.updateConfiguration(configDetails);
	
        return 1;

    }
}
