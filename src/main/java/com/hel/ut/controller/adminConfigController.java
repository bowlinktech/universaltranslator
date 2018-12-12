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
import com.hel.ut.model.configuration;
import com.hel.ut.model.configurationDataTranslations;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationRhapsodyFields;
import com.hel.ut.model.configurationWebServiceSenders;
import com.hel.ut.model.messageTypeFormFields;
import com.hel.ut.service.configurationManager;
import com.hel.ut.model.Organization;
import com.hel.ut.model.User;
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
import com.hel.ut.service.configurationTransportManager;
import com.hel.ut.service.sysAdminManager;
import com.hel.ut.service.userManager;

import java.io.File;
import java.io.FileInputStream;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.hel.ut.model.configurationWebServiceFields;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/administrator/configurations")

public class adminConfigController {

    @Autowired
    private configurationManager configurationmanager;

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private messageTypeManager messagetypemanager;

    @Autowired
    private userManager userManager;

    @Autowired
    private configurationTransportManager configurationTransportManager;

    @Autowired
    private sysAdminManager sysAdminManager;

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
     * @return	The configuration page list
     *
     * @Objects	(1) An object containing all the found configurations
     *
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listConfigurations() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/list");

        List<configuration> configurations = configurationmanager.getConfigurations();

        Organization org;
        messageType messagetype;
        configurationTransport transportDetails;

        for (configuration config : configurations) {
            org = organizationmanager.getOrganizationById(config.getorgId());
            config.setOrgName(org.getOrgName());
	    
	    if(config.getMessageTypeId() > 0) {
		messagetype = messagetypemanager.getMessageTypeById(config.getMessageTypeId());
		config.setMessageTypeName(messagetype.getName());
	    }
	    else {
		config.setMessageTypeName("N/A");
	    }
            
            transportDetails = configurationTransportManager.getTransportDetails(config.getId());
            if (transportDetails != null) {
                config.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
            }

        }

        mav.addObject("configurationList", configurations);

        return mav;

    }

    /**
     * The '/create' GET request will serve up the create new configuration page
     *
     * @return	The create new configuration form
     *
     * @Objects	(1) An object with a new configuration
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createConfiguration(HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/details");
        mav.addObject("configurationDetails", new configuration());

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();
        mav.addObject("organizations", organizations);

        //Need to get a list of active message types
        List<messageType> messageTypes = messagetypemanager.getActiveMessageTypes();
        mav.addObject("messageTypes", messageTypes);
	
	session.setAttribute("showAllConfigOptions",true);
	mav.addObject("showAllConfigOptions", session.getAttribute("showAllConfigOptions"));
       
        return mav;

    }

    /**
     * The '/getAvailableUsers.do' function will return a list of users that are associated to the selected organization.
     *
     * @param orgId The organization selected in the drop down
     *
     * @return users The available users
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableUsers.do", method = RequestMethod.GET)
    public @ResponseBody
    List<User> getUsers(@RequestParam(value = "orgId", required = true) int orgId) {

        List<User> users = userManager.getUsersByOrganization(orgId);

        for (User user : users) {
            user.setOrgName(organizationmanager.getOrganizationById(user.getOrgId()).getOrgName());
        }

        return users;
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
     * The '/create' POST request will submit the new configuration once all required fields are checked, the system will also check to make sure the configuration name is not already in use.
     *
     * @param configurationDetails	The object holding the configuration details form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     * @param action	The variable that holds which button was pressed
     *
     * @return	Will return the configuration details page on "Save" Will return the configuration create page on error
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView saveNewConfiguration(HttpSession session,@ModelAttribute(value = "configurationDetails") configuration configurationDetails, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

        /* Need to make sure the name isn't already taken for the org selected */
        configuration existing = configurationmanager.getConfigurationByName(configurationDetails.getconfigName(), configurationDetails.getorgId());

        if (existing != null) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/configurations/details");

            mav.addObject("configurationDetails", configurationDetails);

            //Need to get a list of active organizations.
            List<Organization> organizations = organizationmanager.getAllActiveOrganizations();
            mav.addObject("organizations", organizations);

            //Need to get a list of active message types
            List<messageType> messageTypes = messagetypemanager.getActiveMessageTypes();
            mav.addObject("messageTypes", messageTypes);

            mav.addObject("existingName", "The configuration name " + configurationDetails.getconfigName().trim() + " already exists.");
            return mav;
        }

        Integer id = (Integer) configurationmanager.createConfiguration(configurationDetails);

        session.setAttribute("manageconfigId", id);

        session.setAttribute("configStepsCompleted", 1);

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
     * The '/details' GET request will display the clicked configuration details page.
     *
     * @return	Will return the configuration details page.
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

        configuration configurationDetails = configurationmanager.getConfigurationById(configId);
        mav.addObject("configurationDetails", configurationDetails);

        //Set the variable to hold the number of completed steps for this configuration;
	session.setAttribute("configStepsCompleted", configurationDetails.getstepsCompleted());
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();
        mav.addObject("organizations", organizations);

        //Need to get a list of active message types
        List<messageType> messageTypes = messagetypemanager.getActiveMessageTypes();
        mav.addObject("messageTypes", messageTypes);
        
        //Need to get a list of organization users 
        List<User> users = userManager.getUsersByOrganization(configurationDetails.getorgId());
        mav.addObject("users", users);

        mav.addObject("id", configId);

        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);
        if (transportDetails != null) {
            configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

	    //Need to set the mappings static variable
            if (transportDetails.gettransportMethodId() == 2) {
		session.setAttribute("configmappings", 2);
            } else {
                if (transportDetails.geterrorHandling() == 1) {
                    session.setAttribute("configmappings", 3);
                } else {
                    session.setAttribute("configmappings", 1);
                }
            }
            
            if (transportDetails.getfileType() == 4 && configurationDetails.getType() == 2) {
		session.setAttribute("configHL7", true);
		session.setAttribute("configCCD", false);
            } else if ((transportDetails.getfileType() == 9 || transportDetails.getfileType() == 12) && configurationDetails.getType() == 2) {
                session.setAttribute("configHL7", false);
		session.setAttribute("configCCD", true);
            } else {
		session.setAttribute("configHL7", false);
		session.setAttribute("configCCD", false);
            }
	    
	    if(transportDetails.getRestAPIType() == 2) {
		session.setAttribute("showAllConfigOptions",false);
	    }
	    else {
		session.setAttribute("showAllConfigOptions",true);
	    }
        }

        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions", session.getAttribute("showAllConfigOptions"));

        return mav;
	
    }

    /**
     * The '/details' POST request will display the clicked configuration details page.
     *
     * @return	Will return the configuration details page.
     *
     * @Objects	(1) The object containing all the information for the clicked configuration (2) The 'id' of the clicked configuration that will be used in the menu and action bar
     *
     * @throws Exception
     *
     */
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public ModelAndView updateConfigurationDetails(HttpSession session,@ModelAttribute(value = "configurationDetails") configuration configurationDetails, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();

        //Need to get a list of active message types
        List<messageType> messageTypes = messagetypemanager.getActiveMessageTypes();

        //Need to get a list of organization users 
        List<User> users = userManager.getUsersByOrganization(configurationDetails.getorgId());

        //submit the updates
        configurationmanager.updateConfiguration(configurationDetails);

        /**
         * Need to update the configuration completed step
         *
         */
        if ((Integer) session.getAttribute("configStepsCompleted") < 1) {
            configurationmanager.updateCompletedSteps(configurationDetails.getId(), 1);
	    session.setAttribute("configStepsCompleted", 1);
        }
	

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
	    
            return mav;
        } //If the "Next Step" button was pressed.
        else {
            redirectAttr.addFlashAttribute("savedStatus", "updated");
            ModelAndView mav = new ModelAndView(new RedirectView("transport"));
            return mav;
        }

    }

    /**
     * The '/transport' GET request will display the clicked configuration transport details form.
     *
     * @return	Will return the configuration transport details form
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

        //Get the configuration details for the selected config
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        /* Get organization directory name */
        Organization orgDetails = organizationmanager.getOrganizationById(configurationDetails.getorgId());

        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);
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

        } else {
            /* Need to set the associated message types 
            List<configurationTransportMessageTypes> messageTypes = configurationTransportManager.getTransportMessageTypes(transportDetails.getId());*/
            List<Integer> assocMessageTypes = new ArrayList<Integer>();
            /*if(messageTypes != null) {
                for(configurationTransportMessageTypes messageType : messageTypes) {
                    assocMessageTypes.add(messageType.getconfigId());
                }
                transportDetails.setmessageTypes(assocMessageTypes);
            }
            else {*/
            assocMessageTypes.add(configurationDetails.getId());
            transportDetails.setmessageTypes(assocMessageTypes);
            /*}*/

        }

        /* Need to get any FTP fields */
        List<configurationFTPFields> ftpFields = configurationTransportManager.getTransportFTPDetails(transportDetails.getId());

        if (ftpFields.isEmpty()) {

            List<configurationFTPFields> emptyFTPFields = new ArrayList<configurationFTPFields>();
            configurationFTPFields pushFTPFields = new configurationFTPFields();
            pushFTPFields.setmethod(1);
            pushFTPFields.setdirectory("/UTSFTP/" + orgDetails.getcleanURL() + "/input/");

            configurationFTPFields getFTPFields = new configurationFTPFields();
            getFTPFields.setmethod(2);
            getFTPFields.setdirectory("/UTSFTP/" + orgDetails.getcleanURL() + "/output/");

            emptyFTPFields.add(pushFTPFields);
            emptyFTPFields.add(getFTPFields);

            transportDetails.setFTPFields(emptyFTPFields);
        } else {
            transportDetails.setFTPFields(ftpFields);
        }

        //get rhaposody fields
        List<configurationRhapsodyFields> rhapsodyFields = configurationTransportManager.getTransRhapsodyDetails(transportDetails.getId());

        if (rhapsodyFields.isEmpty()) {

            List<configurationRhapsodyFields> emptyRhapsodyFields = new ArrayList<configurationRhapsodyFields>();
            configurationRhapsodyFields pushRFields = new configurationRhapsodyFields();
            pushRFields.setMethod(1);
            pushRFields.setDirectory("/UT/" + orgDetails.getcleanURL() + "/input/");

            configurationRhapsodyFields getRFields = new configurationRhapsodyFields();
            getRFields.setMethod(2);
            getRFields.setDirectory("/UT/" + orgDetails.getcleanURL() + "/output/");

            emptyRhapsodyFields.add(pushRFields);
            emptyRhapsodyFields.add(getRFields);

            transportDetails.setRhapsodyFields(emptyRhapsodyFields);
        } else {
            transportDetails.setRhapsodyFields(rhapsodyFields);
        }

        //get WS fields
        List<configurationWebServiceFields> wsFields = configurationTransportManager.getTransWSDetails(transportDetails.getId());

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

        //Need to get a list of all configurations for the current organization
        List<configuration> configurations = configurationmanager.getConfigurationsByOrgId(configurationDetails.getorgId(), "");

        for (configuration config : configurations) {
            configurationTransport transDetails = configurationTransportManager.getTransportDetails(config.getId());
	    
	    if(config.getMessageTypeId() > 0) {
		config.setMessageTypeName(messagetypemanager.getMessageTypeById(config.getMessageTypeId()).getName());
	    }
	    else {
		config.setMessageTypeName("N/A");
	    }

            if (transDetails != null) {
                config.settransportDetailId(transDetails.getId());
                config.settransportMethod(configurationTransportManager.getTransportMethodById(transDetails.gettransportMethodId()));
            }

        }
        mav.addObject("availConfigurations", configurations);

        transportDetails.setconfigId(configId);
	transportDetails.setThreshold(configurationDetails.getThreshold());
        mav.addObject("transportDetails", transportDetails);
	
	if(transportDetails.getRestAPIType() == 2) {
	    session.setAttribute("showAllConfigOptions",false);
	}
	else {
	    session.setAttribute("showAllConfigOptions",true);
	}
	
        //Set the variable id to hold the current configuration id
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions", session.getAttribute("showAllConfigOptions"));
	
        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
	if(configurationDetails.getMessageTypeId() > 0) {
	     configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
	}
	else {
	    configurationDetails.setMessageTypeName("N/A");
	}
        
        configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the configuration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Set the variable to hold the number of completed steps for this configuration;
	mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        //Get the list of available transport methods
        List transportMethods = configurationTransportManager.getTransportMethods();
        mav.addObject("transportMethods", transportMethods);

        //Get the list of available file delimiters
        List delimiters = messagetypemanager.getDelimiters();
        mav.addObject("delimiters", delimiters);

        //Get the list of available file types
        List fileTypes = configurationmanager.getFileTypes();
        mav.addObject("fileTypes", fileTypes);

        //Get the list of available encodings
        List encodings = configurationmanager.getEncodings();
        mav.addObject("encodings", encodings);
	
	//Get the list of available file types
        List zipTypes = configurationmanager.getZipTypes();
        mav.addObject("zipTypes", zipTypes);
	
	//Get the list of available rest api types
        List restAPITypes = configurationmanager.getrestAPITypes();
        mav.addObject("restAPITypes", restAPITypes);
	
	//Get the list of available rest api types
        List restAPIFunctions = configurationmanager.getrestAPIFunctions(configurationDetails.getorgId());
        mav.addObject("restAPIFunctions", restAPIFunctions);

        return mav;
    }

    /**
     * The '/copyExistingTransportMethod.do' POST request will copy the existing transport settings for the passed transport method to the new configuration passed in.
     *
     */
    @RequestMapping(value = "/copyExistingTransportMethod.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer copyExistingTransportMethod(@RequestParam(value = "detailId", required = true) Integer detailId, @RequestParam(value = "configId", required = true) Integer configId) throws Exception {

        configurationTransportManager.copyExistingTransportMethod(detailId, configId);

        return 1;
    }

    /**
     * The '/transport' POST request will submit the transport details
     *
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
	
        /**
         * if transport method = ERG (2) then set up the online form OR if transport method is not ERG but the error handling is set to fix errors via ERG set up the online form
         */
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        /* submit the updates */
        Integer transportId = (Integer) configurationTransportManager.updateTransportDetails(transportDetails);
	
	configurationDetails.setThreshold(transportDetails.getThreshold());
	configurationmanager.updateConfiguration(configurationDetails);
	
        /**
         * if it is a new transport, for web services, we add the domain sender if not, it is handled with add/edit already *
         */
        if (transportDetails.gettransportMethodId() == 6) { //copied or edit
            //we see if domain list is 0, if so, we add, else it has been taken care of via add/edit already
            //we add domain1
            if (configurationDetails.getType() == 1) {
                if (configurationTransportManager.getWSSenderList(transportId).size() == 0) {
                    configurationWebServiceSenders confWSSender = new configurationWebServiceSenders();
                    confWSSender.setTransportId(transportId);
                    confWSSender.setDomain(domain1);
                    configurationTransportManager.saveWSSender(confWSSender);
                }
            }
        }
	
        if (currTransportId == 0) {
	    configurationTransportManager.setupOnlineForm(transportId, configId, configurationDetails.getMessageTypeId());
	}
	
	//Need to set the mappings static variable
	if (transportDetails.gettransportMethodId() == 2) {
	    session.setAttribute("configmappings", 2);
	} else {
	    if (transportDetails.geterrorHandling() == 1) {
		session.setAttribute("configmappings", 3);
	    } else {
		session.setAttribute("configmappings", 1);
	    }
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
                configurationTransportManager.saveTransportFTP(configurationDetails.getorgId(), ftpFields);
            }
        }
	
        /**
         * need to get rhapsody info if any has been entered *
         */
	if (transportDetails.gettransportMethodId() == 5 && !transportDetails.getRhapsodyFields().isEmpty()) {
	    for (configurationRhapsodyFields rhapsodyFields : transportDetails.getRhapsodyFields()) {
                rhapsodyFields.setTransportId(transportId);
                configurationTransportManager.saveTransportRhapsody(rhapsodyFields);
            }
        }
	
        if (transportDetails.gettransportMethodId() == 6 && !transportDetails.getWebServiceFields().isEmpty()) {
            if (configurationDetails.getType() == 2) {
                transportDetails.getWebServiceFields().get(1).setTransportId(transportId);
                configurationTransportManager.saveTransportWebService(transportDetails.getWebServiceFields().get(1));
            } else {
                transportDetails.getWebServiceFields().get(0).setTransportId(transportId);
                configurationTransportManager.saveTransportWebService(transportDetails.getWebServiceFields().get(0));
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
        configurationTransportManager.deleteTransportMessageTypes(transportId);

        /**
         * Step 2:
         */
        if (transportDetails.getmessageTypes() != null) {
            configurationTransportMessageTypes messageType;
            for (Integer selconfigId : transportDetails.getmessageTypes()) {
                messageType = new configurationTransportMessageTypes();
                messageType.setconfigId(selconfigId);
                messageType.setconfigTransportId(transportId);
                configurationTransportManager.saveTransportMessageTypes(messageType);
            }
        }

        redirectAttr.addFlashAttribute("savedStatus", "updated");
	
        //If the "Save" button was pressed 
        if (action.equals("save") || transportDetails.getRestAPIType() == 2) {
            /**
             * Need to update the configuration completed step
             *
             */
            if ((Integer) session.getAttribute("configStepsCompleted") < 2) {
                configurationmanager.updateCompletedSteps(configId, 2);
		session.setAttribute("configStepsCompleted", 2);
            }
            ModelAndView mav = new ModelAndView(new RedirectView("transport"));
            return mav;
        } else {
            //If the type of configuration is for a source then send to message specs
            if (configurationDetails.getType() == 1) {
                
		//Check if passthru
		if(configurationDetails.getConfigurationType() == 2) {
		    configurationmanager.updateCompletedSteps(configId, 2);
		    session.setAttribute("configStepsCompleted", 2);
		    ModelAndView mav = new ModelAndView(new RedirectView("scheduling"));
		    return mav;
		}
		else {
		     if ((Integer) session.getAttribute("configStepsCompleted") < 2) {
			configurationmanager.updateCompletedSteps(configId, 2);
			session.setAttribute("configStepsCompleted", 2);
		    }
		    ModelAndView mav = new ModelAndView(new RedirectView("messagespecs"));
		    return mav;
		}
               
            } else {
                /**
                 * If transport method is ERG send to the ERG Customization page
                 */
                if (transportDetails.gettransportMethodId() == 2) {
                    //Check if passthru
		    if(configurationDetails.getConfigurationType() == 2) {
			configurationmanager.updateCompletedSteps(configId, 2);
			session.setAttribute("configStepsCompleted", 2);
			ModelAndView mav = new ModelAndView(new RedirectView("scheduling"));
			return mav;
		    }
		    else {
			if ((Integer) session.getAttribute("configStepsCompleted") < 2) {
			    configurationmanager.updateCompletedSteps(configId, 3);
			    session.setAttribute("configStepsCompleted", 3);
			}
			ModelAndView mav = new ModelAndView(new RedirectView("ERGCustomize"));
			return mav;
		    }
                } /**
                 * Otherwise send to the field mappings page
                 */
                else {
                    //Check if passthru
		    if(configurationDetails.getConfigurationType() == 2) {
			configurationmanager.updateCompletedSteps(configId, 2);
			session.setAttribute("configStepsCompleted", 2);
			ModelAndView mav = new ModelAndView(new RedirectView("scheduling"));
			return mav;
		    }
		    else {
			if ((Integer) session.getAttribute("configStepsCompleted") < 2) {
			    configurationmanager.updateCompletedSteps(configId, 2);
			    session.setAttribute("configStepsCompleted", 2);
			}
			ModelAndView mav = new ModelAndView(new RedirectView("messagespecs"));
			return mav;
		    }
                }
            }
        }

    }

    /**
     * The '/messagespecs' GET request will display the configuration message specs form.
     *
     * @return	Will return the configuration message spec details form
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

        configurationMessageSpecs messageSpecs = configurationmanager.getMessageSpecs(configId);
        if (messageSpecs == null) {
            messageSpecs = new configurationMessageSpecs();
            messageSpecs.setconfigId(configId);
        }
        mav.addObject("messageSpecs", messageSpecs);

        //Need to pass the selected transport Type
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);
        mav.addObject("transportType", transportDetails.gettransportMethodId());
	mav.addObject("fileType", transportDetails.getfileType());

        //Set the variable id to hold the current configuration id
        mav.addObject("id", configId);
	mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));
	
	
        //Get the configuration details for the selected config
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
	
	configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
	if(configurationDetails.getMessageTypeId() > 0) {
	     configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
	}
	else {
	    configurationDetails.setMessageTypeName("N/A");
	}
	
        configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the configuration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Need to get all available fields that can be used for the reportable fields
        List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        mav.addObject("availableFields", fields);

        //Set the variable to hold the number of completed steps for this configuration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        return mav;
    }

    /**
     * The '/messagespecs' POST request submit all the configuration message specs.
     *
     * @param messageSpecs Will contain the contents of the configuration message spec form.
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
         * Need to update the configuration completed step
         *
         */
        if ((Integer) session.getAttribute("configStepsCompleted") < 3) {
            configurationmanager.updateCompletedSteps(messageSpecs.getconfigId(), 3);
            session.setAttribute("configStepsCompleted", 3);
        }
	

        /**
         * Need to pass the selected transport Type
         */
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(messageSpecs.getconfigId());

        /**
         * Save/Update the configuration message specs
         */
        configurationmanager.updateMessageSpecs(messageSpecs, transportDetails.getId(), transportDetails.getfileType());

        redirectAttr.addFlashAttribute("savedStatus", "updated");
	
	//If excel enter in the ref_configexceldetails
	if(transportDetails.getfileType() == 11) {
	    configuration configDetails = configurationmanager.getConfigurationById(messageSpecs.getconfigId());
	    configurationmanager.updateExcelConfigDetails(configDetails.getorgId(),messageSpecs);
	}

        /**
         * If the "Save" button was pressed
         */
        if (action.equals("save")) {
            ModelAndView mav = new ModelAndView(new RedirectView("messagespecs"));
            return mav;
        } else {
            /**
             * If transport method is ERG send to the ERG Customization page
             */
            if (transportDetails.gettransportMethodId() == 2) {
                ModelAndView mav = new ModelAndView(new RedirectView("ERGCustomize"));
                return mav;
            } /**
             * Otherwise send to the field mappings page
             */
            else {
                ModelAndView mav = new ModelAndView(new RedirectView("mappings"));
                return mav;
            }

        }

    }

    /**
     * The '/ERGCustomize' GET request will display the configuration ERG Customization form.
     *
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/ERGCustomize", method = RequestMethod.GET)
    public ModelAndView viewERGCustomization(HttpSession session) throws Exception {
	
	Integer configId = 0;
	
	ModelAndView mav = new ModelAndView();
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/ERGCustomize");
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));
	
	
        //Get the configuration details for the selected config
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);

        //Get the transport fields
        List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("transportDetails", transportDetails);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
        configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
        configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the configuration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Set the variable to hold the number of completed steps for this configuration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        //Get the list of available field validation types
        List validationTypes = messagetypemanager.getValidationTypes();
        mav.addObject("validationTypes", validationTypes);

        //Get the list of field types
        List fieldTypes = messagetypemanager.getFieldTypes();
        mav.addObject("fieldTypes", fieldTypes);

        return mav;
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

        //Get the completed steps for the selected configuration;
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
        configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
        configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the configuration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the transport fields
        List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("transportDetails", transportDetails);

        //Need to get the template fields
        List<messageTypeFormFields> templateFields = messagetypemanager.getMessageTypeFields(configurationDetails.getMessageTypeId());
        mav.addObject("templateFields", templateFields);

        mav.addObject("selTransportMethod", transportDetails.gettransportMethodId());

        List validationTypes = messagetypemanager.getValidationTypes();
        mav.addObject("validationTypes", validationTypes);

        //Get the list of field types
        List fieldTypes = messagetypemanager.getFieldTypes();
        mav.addObject("fieldTypes", fieldTypes);

        //Set the variable to hold the number of completed steps for this configuration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        return mav;
    }

    /**
     * The 'saveFields' POST method will submit the changes to the form field settings for the selected configuration. This method is only for configurations set for 'Online Form' as the data transportation method.
     *
     * @param	transportDetails	The field details from the form action	The field that will hold which button was pressed "Save" or "Next Step"
     *
     * @return	This method will either redirect back to the Choose Fields page or redirect to the next step data translations page.
     */
    @RequestMapping(value = "/saveFields", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer saveFormFields(HttpSession session,@ModelAttribute(value = "transportDetails") configurationTransport transportDetails, RedirectAttributes redirectAttr, @RequestParam String action, @RequestParam int transportMethod, @RequestParam int errorHandling) throws Exception {

        /**
         * Need to update the configuration completed step
         *
         */
        if ((Integer) session.getAttribute("configStepsCompleted") < 4) {
            configurationmanager.updateCompletedSteps(transportDetails.getconfigId(), 4);
            session.setAttribute("configStepsCompleted", 4);
        }
	
        //Get the list of fields
        List<configurationFormFields> fields = transportDetails.getFields();

        if (null != fields && fields.size() > 0) {
            for (configurationFormFields formfield : fields) {
                if (formfield.getmessageTypeFieldId() == 0) {
                    formfield.setUseField(false);
                } else {
                    formfield.setUseField(formfield.getUseField());
                }
                configurationTransportManager.updateConfigurationFormFields(formfield);
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
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/translations", method = RequestMethod.GET)
    public ModelAndView getConfigurationTranslations(HttpSession session) throws Exception {

        //Set the data translations array to get ready to hold data=
        List<configurationDataTranslations> translations = new CopyOnWriteArrayList<configurationDataTranslations>();
	session.setAttribute("confgirationDataTranslastions", translations);
	
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

        //Get the completed steps for the selected configuration;
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
        configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
        configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the configuration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the transport fields
        List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        //Return a list of available crosswalks
        List<Crosswalks> crosswalks = messagetypemanager.getCrosswalks(1, 0, configurationDetails.getorgId());
        mav.addObject("crosswalks", crosswalks);
        mav.addObject("orgId", configurationDetails.getorgId());

        //Return a list of available macros
        List<Macros> macros = configurationmanager.getMacros();
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

        //Set the variable to hold the number of completed steps for this configuration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

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

        Macros macroDetails = configurationmanager.getMacroById(macroId);

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
	
	/**
         * Need to update the configuration completed step
         *
         */
        if ((Integer) session.getAttribute("configStepsCompleted") < 5) {
            configurationmanager.updateCompletedSteps(configId, 5);
            session.setAttribute("configStepsCompleted", 5);
        }
	
        //Delete all the data translations before creating
        //This will help with the jquery removing translations
        configurationmanager.deleteDataTranslations(configId, categoryId);
	
	List<configurationDataTranslations> translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");

        //Loop through the list of translations
        for (configurationDataTranslations translation : translations) {
            configurationmanager.saveDataTranslations(translation);
        }

        return 1;
    }

    /**
     * The '/getTranslations.do' function will return the list of existing translations set up for the selected configuration/transportMethod.
     *
     * @Return list of translations
     */
    @RequestMapping(value = "/getTranslations.do", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getTranslations(HttpSession session,@RequestParam(value = "reload", required = true) boolean reload, @RequestParam(value = "categoryId", required = true) Integer categoryId) throws Exception {

        ModelAndView mav = new ModelAndView();
	
	Integer configId = (Integer) session.getAttribute("manageconfigId");
	
        mav.setViewName("/administrator/configurations/existingTranslations");
	
	List<configurationDataTranslations> translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");

        //only get the saved translations if reload == 0
        //We only want to retrieve the saved ones on initial load
        if (reload == false) {
            //Need to get a list of existing translations
            List<configurationDataTranslations> existingTranslations = configurationmanager.getDataTranslationsWithFieldNo(configId, categoryId);

            String fieldName;
            String crosswalkName;
            String macroName;
            Map<String, String> defaultValues;
            String optionDesc;
            String optionValue;

            for (configurationDataTranslations translation : existingTranslations) {
                //Get the field name by id
                fieldName = configurationmanager.getFieldName(translation.getFieldId());
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
                    Macros macroDetails = configurationmanager.getMacroById(translation.getMacroId());
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
     * The '/setTranslations{params}' function will handle taking in a selected field and a selected crosswalk and add it to an array of translations. This array will be used when the form is submitted to associate to the existing configuration / trasnort method combination.
     *
     * @param f	This will hold the id of the selected field cw	This will hold the id of the selected crosswalk fText	This will hold the text value of the selected field (used for display purposes) CWText	This will hold the text value of the selected crosswalk (used for display purposes)
     *
     * @Return	This function will return the existing translations view that will display the table of newly selected translations
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
	
	List<configurationDataTranslations> translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");

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
            Map<String, String> defaultValues = new HashMap<>();;
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
     * @param	fieldId This will hold the field that is being removed
     * @param	processOrder	This will hold the process order of the field to be removed so we remove the correct field number as the same field could be in the list with different crosswalks
     *
     * @Return	1	The function will simply return a 1 back to the ajax call
     */
    @RequestMapping(value = "/removeTranslations{params}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer removeTranslation(HttpSession session, @RequestParam(value = "fieldId", required = true) Integer fieldId, @RequestParam(value = "processOrder", required = true) Integer processOrder) throws Exception {

	List<configurationDataTranslations> translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
	
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
     * @param	fieldId This will hold the field that is being removed
     * @param	processOrder	This will hold the process order of the field to be removed so we remove the correct field number as the same field could be in the list with different crosswalks
     *
     * @Return	1	The function will simply return a 1 back to the ajax call
     */
    @RequestMapping(value = "/updateTranslationProcessOrder{params}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer updateTranslationProcessOrder(HttpSession session,@RequestParam(value = "currProcessOrder", required = true) Integer currProcessOrder, @RequestParam(value = "newProcessOrder", required = true) Integer newProcessOrder) throws Exception {

        List<configurationDataTranslations> translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
	
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
     * The 'updateDefaultValue{params}' function will handle setting the crosswalk default value.
     *
     * @param	fieldId This will hold the field that is being set
     * @param	selValue	The selected default value
     *
     * @Return	1	The function will simply return a 1 back to the ajax call
     */
    @RequestMapping(value = "/updateDefaultValue{params}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer updateDefaultValue(HttpSession session,@RequestParam(value = "fieldId", required = true) Integer fieldId, @RequestParam(value = "selValue", required = true) String selValue) throws Exception {

        List<configurationDataTranslations> translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
	
	Iterator<configurationDataTranslations> it = translations.iterator();

        while (it.hasNext()) {
            configurationDataTranslations translation = it.next();
            if (translation.getId() == fieldId) {
                translation.setDefaultValue(selValue);
            }
        }

        return 1;
    }

    /**
     * The '/connections' function will handle displaying the configuration connections screen. The function will pass the existing connection objects for the selected configuration.
     *
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
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/connections");
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));

        /* get a list of all connections in the sysetm */
        List<configurationConnection> connections = configurationmanager.getAllConnections();

        Long totalConnections = (long) 0;

        /* Loop over the connections to get the configuration details */
        if (connections != null) {
            for (configurationConnection connection : connections) {
                /* Array to holder the users */
                List<User> connectionSenders = new ArrayList<User>();
                List<User> connectonReceivers = new ArrayList<User>();

                configuration srcconfigDetails = configurationmanager.getConfigurationById(connection.getsourceConfigId());
                configurationTransport srctransportDetails = configurationTransportManager.getTransportDetails(srcconfigDetails.getId());

                srcconfigDetails.setOrgName(organizationmanager.getOrganizationById(srcconfigDetails.getorgId()).getOrgName());
		
		if(srcconfigDetails.getMessageTypeId() > 0) {
		    srcconfigDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(srcconfigDetails.getMessageTypeId()).getName());
		}
		else {
		    srcconfigDetails.setMessageTypeName("N/A");
		}
		
                srcconfigDetails.settransportMethod(configurationTransportManager.getTransportMethodById(srctransportDetails.gettransportMethodId()));
                if (srctransportDetails.gettransportMethodId() == 1 && srcconfigDetails.getType() == 2) {
                    srcconfigDetails.settransportMethod("File Download");
                } else {
                    srcconfigDetails.settransportMethod(configurationTransportManager.getTransportMethodById(srctransportDetails.gettransportMethodId()));
                }

                connection.setsrcConfigDetails(srcconfigDetails);

                configuration tgtconfigDetails = configurationmanager.getConfigurationById(connection.gettargetConfigId());
                configurationTransport tgttransportDetails = configurationTransportManager.getTransportDetails(tgtconfigDetails.getId());

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
                    tgtconfigDetails.settransportMethod(configurationTransportManager.getTransportMethodById(tgttransportDetails.gettransportMethodId()));
                }

                /* Get the list of connection senders */
 /*List<configurationConnectionSenders> senders = configurationmanager.getConnectionSenders(connection.getId());
                
                for(configurationConnectionSenders sender : senders) {
                    User userDetail = userManager.getUserById(sender.getuserId());
                    userDetail.setOrgName(organizationmanager.getOrganizationById(userDetail.getOrgId()).getOrgName());
                    connectionSenders.add(userDetail);
                }
                connection.setconnectionSenders(connectionSenders);*/
 /* Get the list of connection receivers */
 /*List<configurationConnectionReceivers> receivers = configurationmanager.getConnectionReceivers(connection.getId());
                
                for(configurationConnectionReceivers receiver : receivers) {
                    User userDetail = userManager.getUserById(receiver.getuserId());
                    userDetail.setOrgName(organizationmanager.getOrganizationById(userDetail.getOrgId()).getOrgName());
                    connectonReceivers.add(userDetail);
                }
                connection.setconnectionReceivers(connectonReceivers);*/
                connection.settgtConfigDetails(tgtconfigDetails);
            }

            /* Return the total list of connections */
            totalConnections = (long) connections.size();
        }

        mav.addObject("connections", connections);

        /* Set the variable to hold the number of completed steps for this configuration */
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        return mav;
    }

    /**
     * The '/createConnection' function will handle displaying the create configuration connection screen.
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
     * The '/editConnection' funtion will handle displaying the edit configuration connection screen.
     *
     * @param connectionId The id of the clicked configuration connection
     *
     * @return This function will display the edit connection overlay
     */
    @RequestMapping(value = "/editConnection", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView editConnectionForm(@RequestParam(value = "connectionId", required = true) int connectionId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connectionDetails");

        configurationConnection connectionDetails = configurationmanager.getConnection(connectionId);

        configuration srcconfigDetails = configurationmanager.getConfigurationById(connectionDetails.getsourceConfigId());
        srcconfigDetails.setorgId(organizationmanager.getOrganizationById(srcconfigDetails.getorgId()).getId());
        connectionDetails.setsrcConfigDetails(srcconfigDetails);

        configuration tgtconfigDetails = configurationmanager.getConfigurationById(connectionDetails.gettargetConfigId());
        tgtconfigDetails.setorgId(organizationmanager.getOrganizationById(tgtconfigDetails.getorgId()).getId());
        connectionDetails.settgtConfigDetails(tgtconfigDetails);

        /*List<configurationConnectionSenders> senders = configurationmanager.getConnectionSenders(connectionId);
        
        // Array to holder the users 
        List<User> connectionSenders = new ArrayList<User>();
        List<User> connectonReceivers = new ArrayList<User>();
                
        for(configurationConnectionSenders sender : senders) {
            User userDetail = userManager.getUserById(sender.getuserId());
            userDetail.setOrgName(organizationmanager.getOrganizationById(userDetail.getOrgId()).getOrgName());
            connectionSenders.add(userDetail);
        }
        connectionDetails.setconnectionSenders(connectionSenders);

        // Get the list of connection receivers 
        List<configurationConnectionReceivers> receivers = configurationmanager.getConnectionReceivers(connectionId);

        for(configurationConnectionReceivers receiver : receivers) {
            User userDetail = userManager.getUserById(receiver.getuserId());
            userDetail.setOrgName(organizationmanager.getOrganizationById(userDetail.getOrgId()).getOrgName());
            connectonReceivers.add(userDetail);
        }
        connectionDetails.setconnectionReceivers(connectonReceivers);
         */
        mav.addObject("connectionDetails", connectionDetails);

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();
        mav.addObject("organizations", organizations);

        return mav;
    }

    /**
     * The '/getAvailableSendingUsers.do' function will return a list of users that are associated to the selected organization.
     *
     * @param orgId The organization selected in the drop down
     *
     * @return users The available users
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableSendingUsers.do", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getAvailableSendingUsers(
            @RequestParam(value = "orgId", required = true) int orgId,
            @RequestParam(value = "connectionId", required = true) int connectionId) {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connectionSendingUsers");

        List<User> users = userManager.getUsersByOrganization(orgId);

        List<configurationConnectionSenders> senders = configurationmanager.getConnectionSenders(connectionId);

        List<User> connectionSenders = new ArrayList<User>();

        for (User user : users) {
            boolean associated = false;
            boolean sendSentEmail = false;
            boolean sendReceivedEmail = false;
            if (senders != null && senders.size() > 0) {
                for (configurationConnectionSenders sender : senders) {
                    if (sender.getuserId() == user.getId()) {
                        associated = true;

                        if (sender.getSendEmailAlert() == true) {
                            sendSentEmail = true;
                        }
                    }
                }
            }
            user.setOrgName(organizationmanager.getOrganizationById(user.getOrgId()).getOrgName());
            user.setConnectionAssociated(associated);
            user.setSendSentEmail(sendSentEmail);
            user.setSendReceivedEmail(sendReceivedEmail);

            connectionSenders.add(user);
        }

        mav.addObject("connectionSenders", connectionSenders);

        return mav;
    }

    /**
     * The '/getAvailableReceivingUsers.do' function will return a list of users that are associated to the selected organization.
     *
     * @param orgId The organization selected in the drop down
     *
     * @return users The available users
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableReceivingUsers.do", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getAvailableReceivingUsers(
            @RequestParam(value = "orgId", required = true) int orgId,
            @RequestParam(value = "connectionId", required = true) int connectionId) {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connectionReceivingUsers");

        List<User> users = userManager.getUsersByOrganization(orgId);

        List<configurationConnectionReceivers> receivers = configurationmanager.getConnectionReceivers(connectionId);

        List<User> connectonReceivers = new ArrayList<User>();

        for (User user : users) {
            boolean associated = false;
            boolean sendSentEmail = false;
            boolean sendReceivedEmail = false;
            if (receivers != null && receivers.size() > 0) {
                for (configurationConnectionReceivers receiver : receivers) {
                    if (receiver.getuserId() == user.getId()) {
                        associated = true;

                        if (receiver.getSendEmailAlert() == true) {
                            sendReceivedEmail = true;
                        }
                    }
                }
            }
            user.setOrgName(organizationmanager.getOrganizationById(user.getOrgId()).getOrgName());
            user.setConnectionAssociated(associated);
            user.setSendSentEmail(sendSentEmail);
            user.setSendReceivedEmail(sendReceivedEmail);

            connectonReceivers.add(user);
        }

        mav.addObject("connectonReceivers", connectonReceivers);

        return mav;
    }

    /**
     * The '/getAvailableConfigurations.do' function will return a list of configuration that have been set up for the passed in organization.
     *
     * @param orgId The organization selected in the drop down
     *
     * @return configurations The available configurations
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableConfigurations.do", method = RequestMethod.GET)
    public @ResponseBody
    List<configuration> getAvailableConfigurations(@RequestParam(value = "orgId", required = true) int orgId) throws Exception {

        List<configuration> configurations = configurationmanager.getActiveConfigurationsByOrgId(orgId);

        if (configurations != null) {
            for (configuration configuration : configurations) {
                configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configuration.getId());

                configuration.setOrgName(organizationmanager.getOrganizationById(configuration.getorgId()).getOrgName());
		
		if(configuration.getMessageTypeId() > 0) {
		    configuration.setMessageTypeName(messagetypemanager.getMessageTypeById(configuration.getMessageTypeId()).getName());
		}
		else {
		    configuration.setMessageTypeName("N/A");
		}
		
                
                configuration.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
            }
        }

        return configurations;
    }

    /**
     * The '/addConnection.do' POST request will create the connection between the passed in organization and the configuration.
     *
     * @param	srcConfig The selected source configuration
     * @param tgtConfig The selected target configuration
     *
     * @return	The method will return a 1 back to the calling ajax function which will handle the page load.
     */
    @RequestMapping(value = "/addConnection.do", method = RequestMethod.POST)
    public ModelAndView addConnection(
            @ModelAttribute(value = "connectionDetails") configurationConnection connectionDetails,
            @RequestParam List<Integer> srcUsers,
            @RequestParam(value = "srcUsersSendEmail", required = false) List<Integer> srcUsersSendEmail,
            @RequestParam List<Integer> tgtUsers,
            @RequestParam(value = "tgtUsersSendEmail", required = false) List<Integer> tgtUsersSendEmail,
            RedirectAttributes redirectAttr) throws Exception {

        Integer connectionId;

        if (connectionDetails.getId() == 0) {
            connectionDetails.setStatus(true);
            connectionId = configurationmanager.saveConnection(connectionDetails);
            redirectAttr.addFlashAttribute("savedStatus", "created");
        } else {
            connectionId = connectionDetails.getId();
            configurationmanager.updateConnection(connectionDetails);

            /* Delete existing senders and receivers */
            configurationmanager.removeConnectionSenders(connectionId);
            configurationmanager.removeConnectionReceivers(connectionId);
            redirectAttr.addFlashAttribute("savedStatus", "updated");
        }

        for (Integer sender : srcUsers) {
            configurationConnectionSenders senderInfo = new configurationConnectionSenders();
            senderInfo.setconnectionId(connectionId);
            senderInfo.setuserId(sender);
            boolean sendEmail = false;
            if (srcUsersSendEmail != null && srcUsersSendEmail.size() > 0) {
                for (Integer sendEmailForUser : srcUsersSendEmail) {
                    if (Objects.equals(sendEmailForUser, sender)) {
                        sendEmail = true;
                    }
                }
            }
            senderInfo.setSendEmailAlert(sendEmail);
            configurationmanager.saveConnectionSenders(senderInfo);
        }

        for (Integer receiver : tgtUsers) {
            configurationConnectionReceivers receiverInfo = new configurationConnectionReceivers();
            receiverInfo.setconnectionId(connectionId);
            receiverInfo.setuserId(receiver);
            boolean sendEmail = false;
            if (tgtUsersSendEmail != null && tgtUsersSendEmail.size() > 0) {
                for (Integer sendEmailForUser : tgtUsersSendEmail) {
                    if (Objects.equals(sendEmailForUser, receiver)) {
                        sendEmail = true;
                    }
                }
            }
            receiverInfo.setSendEmailAlert(sendEmail);
            configurationmanager.saveConnectionReceivers(receiverInfo);
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

        configurationConnection connection = configurationmanager.getConnection(connectionId);
        connection.setStatus(statusVal);
        configurationmanager.updateConnection(connection);

        return 1;
    }

    /**
     * The '/scheduling' GET request will display the scheduling page for the selected transport Method
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

        //Get the completed steps for the selected configuration;
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
	
	if(configurationDetails.getMessageTypeId() > 0) {
	    configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
	}
	else {
	    configurationDetails.setMessageTypeName("N/A");
	}
	
        configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the configuration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the schedule for the configuration and selected transport method
        configurationSchedules scheduleDetails = configurationmanager.getScheduleDetails(configId);

        if (scheduleDetails == null) {
            scheduleDetails = new configurationSchedules();
            scheduleDetails.setconfigId(configId);
        }
        mav.addObject("scheduleDetails", scheduleDetails);

        //Set the variable to hold the number of completed steps for this configuration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        return mav;
    }

    /**
     * The '/scheduling' POST request will submit the scheduling settings for the selected configuration.
     *
     * @param scheduleDetails The object that will hold the scheduling form fields
     *
     * @return This method will redirect the user back to the scheduling form page.
     */
    @RequestMapping(value = "/scheduling", method = RequestMethod.POST)
    public ModelAndView submitConfigurationSchedules(HttpSession session,@ModelAttribute(value = "scheduleDetails") configurationSchedules scheduleDetails, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

	Integer configId = (Integer) session.getAttribute("manageconfigId");
	
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

        configurationmanager.saveSchedule(scheduleDetails);

        //Update the configuration completed step
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);
        if (configurationDetails.getstepsCompleted() < 6) {
            configurationmanager.updateCompletedSteps(configId, 6);
        }

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

        //Get the completed steps for the selected configuration;
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
        configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
        configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the configuration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Set the variable to hold the number of completed steps for this configuration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        HL7Details hl7Details = configurationmanager.getHL7Details(configId);
        int HL7Id = 0;

        /* If null then create an empty HL7 Detail object */
        if (hl7Details == null) {
            /* Get a list of available HL7 Sepcs */
            List<mainHL7Details> HL7Specs = sysAdminManager.getHL7List();
            mav.addObject("HL7Specs", HL7Specs);
        } else {
            HL7Id = hl7Details.getId();

            /* Get a list of HL7 Segments */
            List<HL7Segments> HL7Segments = configurationmanager.getHL7Segments(HL7Id);

            /* Get a list of HL7Elements */
            if (!HL7Segments.isEmpty()) {
                for (HL7Segments segment : HL7Segments) {

                    List<HL7Elements> HL7Elments = configurationmanager.getHL7Elements(HL7Id, segment.getId());

                    if (!HL7Elments.isEmpty()) {

                        for (HL7Elements element : HL7Elments) {
                            List<HL7ElementComponents> components = configurationmanager.getHL7ElementComponents(element.getId());
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
        List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        return mav;
    }

    /**
     * The '/loadHL7Spec' will load the configuration HL7 specs from one that was chosen from the list of standard hl7 specs.
     *
     * @param configId The id of the configuration to attach the HL7 spec to
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

        int hl7Id = configurationmanager.saveHL7Details(newHL7);

        List<mainHL7Segments> segments = sysAdminManager.getHL7Segments(hl7SpecId);

        for (mainHL7Segments segment : segments) {

            HL7Segments newHL7Segment = new HL7Segments();
            newHL7Segment.sethl7Id(hl7Id);
            newHL7Segment.setsegmentName(segment.getsegmentName());
            newHL7Segment.setdisplayPos(segment.getdisplayPos());

            int segmentId = configurationmanager.saveHL7Segment(newHL7Segment);

            List<mainHL7Elements> elements = sysAdminManager.getHL7Elements(hl7SpecId, segment.getId());

            for (mainHL7Elements element : elements) {

                HL7Elements newHL7Element = new HL7Elements();
                newHL7Element.sethl7Id(hl7Id);
                newHL7Element.setsegmentId(segmentId);
                newHL7Element.setelementName(element.getelementName());
                newHL7Element.setdefaultValue(element.getdefaultValue());
                newHL7Element.setdisplayPos(element.getdisplayPos());

                configurationmanager.saveHL7Element(newHL7Element);
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
        configurationmanager.updateHL7Details(HL7Details);

        List<HL7Segments> segments = HL7Details.getHL7Segments();

        try {
            if (null != segments && segments.size() > 0) {

                for (HL7Segments segment : segments) {

                    /* Update each segment */
                    configurationmanager.updateHL7Segments(segment);

                    /* Get the list of segment elements */
                    List<HL7Elements> elements = segment.getHL7Elements();

                    if (null != elements && elements.size() > 0) {

                        for (HL7Elements element : elements) {
                            configurationmanager.updateHL7Elements(element);


                            /* Get the list of segment element components */
                            List<HL7ElementComponents> components = element.getelementComponents();

                            if (null != components && components.size() > 0) {
                                for (HL7ElementComponents component : components) {
                                    configurationmanager.updateHL7ElementComponent(component);
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

        configurationmanager.saveHL7Segment(HL7SegmentDetails);

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

        configurationmanager.saveHL7Element(HL7ElementDetails);

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
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);
        List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
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

        configurationmanager.saveHL7Component(HL7ComponentDetails);

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

        Organization orgDetails = organizationmanager.getOrganizationById(configurationmanager.getConfigurationById(configId).getorgId());

        /* get the FTP Details */
        configurationFTPFields ftpDetails;
        if (method == 1) {
            ftpDetails = configurationTransportManager.getTransportFTPDetailsPull(id);
        } else {
            ftpDetails = configurationTransportManager.getTransportFTPDetailsPush(id);
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
     * The '/preprocessing' GET request will display the configuration preprocessing page
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

        //Get the completed steps for the selected configuration;
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
        configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
        configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the configuration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the transport fields
        List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        //Return a list of available macros
        List<Macros> macros = configurationmanager.getMacrosByCategory(2);
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

        //Set the variable to hold the number of completed steps for this configuration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));
	
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
     * The '/postprocessing' GET request will display the configuration post processing page
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

        //Get the completed steps for the selected configuration;
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
        configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
        configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the configuration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the transport fields
        List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        transportDetails.setFields(fields);

        mav.addObject("fields", fields);

        //Return a list of available macros
        List<Macros> macros = configurationmanager.getMacrosByCategory(3);
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

        //Set the variable to hold the number of completed steps for this configuration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));
	
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

        configurationmanager.removeHL7ElementComponent(componentId);

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

        configurationmanager.removeHL7Element(elementId);

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

        configurationmanager.removeHL7Segment(segmentId);

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
	

        //Set the variable to hold the number of completed steps for this configuration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        //Get the completed steps for the selected configuration;
        configuration configurationDetails = configurationmanager.getConfigurationById(configId);

        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);

        configurationDetails.setOrgName(organizationmanager.getOrganizationById(configurationDetails.getorgId()).getOrgName());
        configurationDetails.setMessageTypeName(messagetypemanager.getMessageTypeById(configurationDetails.getMessageTypeId()).getName());
        configurationDetails.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the configuration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        List<configurationCCDElements> ccdElements = configurationmanager.getCCDElements(configId);
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
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);
        List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
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

        configurationCCDElements ccdElement = configurationmanager.getCCDElement(elementId);
        mav.addObject("ccdElement", ccdElement);

        //Get the transport fields
        //Get the transport details by configid and selected transport method
        configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);
        List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
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

        configurationmanager.saveCCDElement(ccdElement);

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
        cwsf.setSenderDomainList(configurationTransportManager.getWSSenderList(transportId));
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
                configurationTransportManager.saveWSSender(confWSSender);
                success = "Updated!";
                senders = senders + confWSSender.getDomain() + ",";
            } else if (confWSSender.getDomain().length() == 0 && confWSSender.getId() != 0) {
                configurationTransportManager.deleteWSSender(confWSSender);
                success = "Updated!";
            }
        }
        if (senders.length() != 0) {
            senders = senders.substring(0, senders.length() - 1);
        }

        configurationWebServiceFields cwsfNew = new configurationWebServiceFields();
        cwsfNew.setTransportId(cwsf.getTransportId());
        cwsfNew.setSenderDomainList(configurationTransportManager.getWSSenderList(cwsf.getTransportId()));

        mav.addObject("cwsf", cwsfNew);
        mav.addObject("success", success);
        mav.addObject("senders", senders);
        return mav;

    }
    
    
    /**
     * The 'copyConfiguration.do' method will copy the selected configuration.
     */
    @RequestMapping(value = "/copyConfiguration.do", method = RequestMethod.POST)
    public @ResponseBody
    Integer copyConfiguration(@RequestParam int configId) throws Exception {

        configuration configDetails = configurationmanager.getConfigurationById(configId);
	
	//New Configuration
	configuration newConfig = new configuration();
	newConfig.setorgId(configDetails.getorgId());
	newConfig.setStatus(false);
	newConfig.setType(configDetails.getType());
	newConfig.setMessageTypeId(configDetails.getMessageTypeId());
	newConfig.setstepsCompleted(6);
	newConfig.setconfigName(configDetails.getconfigName() + " (COPY)");
	newConfig.setsourceType(configDetails.getsourceType());
	newConfig.setassociatedMessageTypeId(configDetails.getassociatedMessageTypeId());
	newConfig.setThreshold(configDetails.getThreshold());
	
	//Save new Configuration
	Integer id = (Integer) configurationmanager.createConfiguration(newConfig);
	
	//Get the existing transport details
	configurationTransport transportDetails = configurationTransportManager.getTransportDetails(configId);
	
	//Save new transport Details
	configurationTransport newTransportDetails = new configurationTransport();
	newTransportDetails.setconfigId(id);
	newTransportDetails.setfileLocation(transportDetails.getfileLocation());
	newTransportDetails.setautoRelease(true);
	newTransportDetails.setstatus(true);
	newTransportDetails.setclearRecords(true);
	newTransportDetails.setmaxFileSize(transportDetails.getmaxFileSize());
	newTransportDetails.setMassTranslation(true);
	
	Integer transportDetailId = configurationTransportManager.updateTransportDetails(newTransportDetails);
	
	configurationTransportMessageTypes newConfigTransportMessageType = new configurationTransportMessageTypes();
	newConfigTransportMessageType.setconfigId(id);
	newConfigTransportMessageType.setconfigTransportId(transportDetailId);
	
	configurationTransportManager.saveTransportMessageTypes(newConfigTransportMessageType);
		
	//Get the message specs
	configurationMessageSpecs messageSpecs = configurationmanager.getMessageSpecs(configId);
	
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
	
	// Save/Update the configuration message specs
        configurationmanager.updateMessageSpecs(newMessageSpecs);
	
	//Need to get a list of existing translations
        List<configurationDataTranslations> existingTranslations = configurationmanager.getDataTranslationsWithFieldNo(configId, 1);

	//Get form fields
	List<configurationFormFields> fields = configurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        
	if(fields != null) {
	    if(!fields.isEmpty()) {
		for(configurationFormFields field : fields) {
		    Integer currFormFieldId = field.getId();
		    
		    configurationFormFields newFormField = new configurationFormFields();
		    newFormField.setconfigId(id);
		    newFormField.setmessageTypeFieldId(field.getmessageTypeFieldId());
		    newFormField.settransportDetailId(transportDetailId);
		    newFormField.setFieldNo(field.getFieldNo());
		    newFormField.setFieldDesc(field.getFieldDesc());
		    newFormField.setValidationType(field.getValidationType());
		    newFormField.setRequired(field.getRequired());
		    newFormField.setUseField(field.getUseField());
		    
		    Integer formFieldId = configurationTransportManager.saveConfigurationFormFields(newFormField);
		    
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

				    configurationmanager.saveDataTranslations(newTranslation);
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
	
	configurationmanager.saveSchedule(newSchedule);

        return id;

    }

}
