package com.hel.ut.controller;


import com.hel.ut.model.CrosswalkData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

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
import com.hel.ut.model.appenedNewconfigurationFormFields;
import com.hel.ut.model.utUser;
import com.hel.ut.model.configurationCCDElements;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationFTPFields;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.configurationSchedules;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.messageTypeManager;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.configurationTransportMessageTypes;
import com.hel.ut.model.configurationUpdateLogs;
import com.hel.ut.model.mainHL7Details;
import com.hel.ut.model.mainHL7Elements;
import com.hel.ut.model.mainHL7Segments;
import com.hel.ut.reference.fileSystem;
import com.hel.ut.service.sysAdminManager;
import com.hel.ut.service.userManager;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.hel.ut.model.configurationWebServiceFields;
import com.hel.ut.model.configurationconnectionfieldmappings;
import com.hel.ut.model.hisps;
import com.hel.ut.model.mailMessage;
import com.hel.ut.model.organizationDirectDetails;
import com.hel.ut.model.validationType;
import com.hel.ut.service.emailMessageManager;
import com.hel.ut.service.hispManager;
import com.hel.ut.service.impl.transactionInManagerImpl;
import com.hel.ut.service.transactionInManager;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.registryKit.registry.configurations.configuration;
import com.registryKit.registry.configurations.configurationManager;
import com.registryKit.registry.helRegistry;
import com.registryKit.registry.helRegistryManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/administrator/configurations")

public class adminConfigController {
    
    @Value("${siteTimeZone}")
    private String siteTimeZone; 
    
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
    
    @Autowired
    private helRegistryManager helregistrymanager;
    
    @Autowired
    private emailMessageManager emailMessageManager;
    
    @Autowired
    private transactionInManager transactioninmanager;
    
    @Resource(name = "myProps")
    private Properties myProps;
    
    @Autowired
    ThreadPoolTaskExecutor executor;
    
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
     * @param authentication
     * @return	The utConfiguration page list
     *
     * @Objects	(1) An object containing all the found configurations
     *
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listConfigurations(Authentication authentication) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/list");
	
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	
	//Get all source configurations
        List<utConfiguration> sourceconfigurations = utconfigurationmanager.getAllSourceConfigurations();
	
	//Get all target configurations
        List<utConfiguration> targetconfigurations = utconfigurationmanager.getAllTargetConfigurations();

        Organization org;
        configurationTransport transportDetails;
	
	Calendar cal = Calendar.getInstance();
	
	TimeZone timeZone = TimeZone.getTimeZone(siteTimeZone);
	DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	requiredFormat.setTimeZone(timeZone);
	String dateinTZ = "";
	
	List<utConfiguration> validSourceConfigurations = new ArrayList<>();
	
        for (utConfiguration config : sourceconfigurations) {
	    dateinTZ = requiredFormat.format(config.getDateCreated());
	    
	    config.setDateCreated(dft.parse(dateinTZ));
	    
	    if(config.getDateUpdated() != null) {
		dateinTZ = requiredFormat.format(config.getDateUpdated());
	    }
	    config.setDateUpdated(dft.parse(dateinTZ));
	    
            org = organizationmanager.getOrganizationById(config.getorgId());
            config.setOrgName(org.getOrgName());
	    
            transportDetails = utconfigurationTransportManager.getTransportDetails(config.getId());
            if (transportDetails != null) {
                config.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
		
		configurationFileDropFields fileDropLocation = utconfigurationTransportManager.getTransFileDropDetailsPull(transportDetails.getId());
		
		if(fileDropLocation != null) {
		    //Make sure the configuration has at least 1 connection before we allow file upload
		    List<configurationConnection> getConectionsByConfiguration = utconfigurationmanager.getConnectionsByConfiguration(config.getId(), 0);
		    if(!getConectionsByConfiguration.isEmpty()) {
			config.setFileDropLocation(fileDropLocation.getDirectory());
		    }
		    
		}
            }
	    
	    configurationSchedules scheduleDetails = utconfigurationmanager.getScheduleDetails(config.getId());
	    
	    if(scheduleDetails != null && !config.isDeleted() && config.getStatus() && ("admin".equalsIgnoreCase(userDetails.getFirstName()) || "grace".equalsIgnoreCase(userDetails.getFirstName()) || "chad".equalsIgnoreCase(userDetails.getFirstName()))) {
		config.setAllowExport(true);
	    }
	    
	    if(!"bowlinktest".equals(org.getCleanURL().trim().toLowerCase())) {
		validSourceConfigurations.add(config);
	    }
	    
        }
	mav.addObject("sourceconfigurations", validSourceConfigurations);
	
	List<utConfiguration> validTargetConfigurations = new ArrayList<>();
	
	for (utConfiguration config : targetconfigurations) {
	    dateinTZ = requiredFormat.format(config.getDateCreated());
	    
	    config.setDateCreated(dft.parse(dateinTZ));
	    
	    if(config.getDateUpdated() != null) {
		dateinTZ = requiredFormat.format(config.getDateUpdated());
	    }
	    config.setDateUpdated(dft.parse(dateinTZ));
	    
            org = organizationmanager.getOrganizationById(config.getorgId());
            config.setOrgName(org.getOrgName());
	    
            transportDetails = utconfigurationTransportManager.getTransportDetails(config.getId());
            if (transportDetails != null) {
                config.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
            }
	    
	    configurationSchedules scheduleDetails = utconfigurationmanager.getScheduleDetails(config.getId());
	    
	    if(scheduleDetails != null && !config.isDeleted() && config.getStatus()) {
		config.setAllowExport(true);
	    }
	    
	    if(!"bowlinktest".equals(org.getCleanURL().trim().toLowerCase())) {
		validTargetConfigurations.add(config);
	    }
        }
	mav.addObject("targetconfigurations", validTargetConfigurations);
	
	if("admin".equalsIgnoreCase(userDetails.getFirstName()) || "grace".equalsIgnoreCase(userDetails.getFirstName()) || "chad".equalsIgnoreCase(userDetails.getFirstName())){
	    mav.addObject("allowConfigImport", true);
	}

        return mav;
    }

    /**
     * The '/create' GET request will serve up the create new utConfiguration page
     *
     * @param session
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
	List<Organization> validOrganizations = new ArrayList<>();
	
	for(Organization org : organizations) {
	    if(!"bowlinktest".equals(org.getCleanURL().trim().toLowerCase())) {
		validOrganizations.add(org);
	    }
	}
	
        mav.addObject("organizations", validOrganizations);

        mav.addObject("mappings", 1);
	
	session.setAttribute("showAllConfigOptions",true);
	mav.addObject("showAllConfigOptions", session.getAttribute("showAllConfigOptions"));
	
        return mav;
    }

    /**
     * The '/create' POST request will submit the new utConfiguration once all required fields are checked, the system will also check to make sure the utConfiguration name is not already in use.
     *
     * @param session
     * @param configurationDetails	The object holding the utConfiguration details form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     * @param action	The variable that holds which button was pressed
     * @param authentication     *
     * @return	Will return the utConfiguration details page on "Save" Will return the utConfiguration create page on error
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView saveNewConfiguration(HttpSession session,@ModelAttribute(value = "configurationDetails") utConfiguration configurationDetails, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action, Authentication authentication) throws Exception {

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
	
	
	configurationDetails.setstepsCompleted(1);
	
        Integer id = utconfigurationmanager.createConfiguration(configurationDetails);

        session.setAttribute("manageconfigId", id);
	
	//Log the update
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	configurationUpdateLogs updateLog = new configurationUpdateLogs();
	updateLog.setConfigId(id);
	updateLog.setUserId(userDetails.getId());
	updateLog.setUpdateMade("Configuration Created");
	
	utconfigurationmanager.saveConfigurationUpdateLog(updateLog);
	
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
     * @param session
     * @param id
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
	
	List<Organization> validOrganizations = new ArrayList<>();
	for(Organization org : organizations) {
	    if("bowlinkTest".equals(configurationDetails.getconfigName()) || "Bowlink Test Target".equals(configurationDetails.getconfigName()) || (!"bowlinktest".equals(org.getCleanURL().trim().toLowerCase()) && !"bowlinkTest".equals(configurationDetails.getconfigName()))) {
		validOrganizations.add(org);
	    }
	}
	
        mav.addObject("organizations", validOrganizations);

        //Need to get a list of organization users 
        List<utUser> users = userManager.getUsersByOrganization(configurationDetails.getorgId());
        mav.addObject("users", users);

        mav.addObject("id", configId);

        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
        if (transportDetails != null) {
            configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
	    session.setAttribute("configmappings", 1);
	    
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
     * @param session
     * @param configurationDetails
     * @param result
     * @param redirectAttr
     * @param action
     * @param authentication
     * @return	Will return the utConfiguration details page.
     *
     * @Objects	(1) The object containing all the information for the clicked configuration (2) The 'id' of the clicked configuration that will be used in the menu and action bar
     *
     * @throws Exception
     *
     */
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public ModelAndView updateConfigurationDetails(HttpSession session,@ModelAttribute(value = "configurationDetails") utConfiguration configurationDetails, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action, Authentication authentication) throws Exception {

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizations();

        //Need to get a list of organization users 
        List<utUser> users = userManager.getUsersByOrganization(configurationDetails.getorgId());
	
	boolean configNameChanged = false;
	boolean organizationChanged = false;
	
	utConfiguration currentConfigDetails = utconfigurationmanager.getConfigurationById(configurationDetails.getId());
	
	if(!currentConfigDetails.getconfigName().trim().equals(configurationDetails.getconfigName().trim())) {
	    configNameChanged = true;
	}
	
	if(currentConfigDetails.getorgId() != configurationDetails.getorgId()) {
	    organizationChanged = true;
	}
	
        //submit the updates
	utconfigurationmanager.updateConfiguration(configurationDetails);
	
	if(configNameChanged || organizationChanged) {
	    configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configurationDetails.getId());
	    
	    if(transportDetails != null) {
		//get file drop fields
		List<configurationFileDropFields> fileDropFields = utconfigurationTransportManager.getTransFileDropDetails(transportDetails.getId());
		
		Organization orgDetails = organizationmanager.getOrganizationById(configurationDetails.getorgId());
		
		if(organizationChanged) {
		    transportDetails.setfileLocation(orgDetails.getcleanURL() + "/input files/");
		    utconfigurationTransportManager.updateTransportDetails(configurationDetails, transportDetails);
		}
		
		if(!fileDropFields.isEmpty()) {
		    String fileLocationConfigName = "";
		    try {
			for(configurationFileDropFields fileDropField : fileDropFields) {
			    if(fileDropField.getDirectory().contains(orgDetails.getcleanURL() + "/input files/") || organizationChanged) {
				fileLocationConfigName = fileDropField.getDirectory().substring(fileDropField.getDirectory().lastIndexOf("/input files/"), fileDropField.getDirectory().length()-1);
				fileLocationConfigName = fileLocationConfigName.replace("/input files/","");
				
				if(!"".equals(fileLocationConfigName)) {
				    if(!fileLocationConfigName.equals(configurationDetails.getconfigName().toLowerCase().replace(" ", "")) || organizationChanged) {
					//Create new directory
					String directory = myProps.getProperty("ut.directory.utRootDir");
					fileSystem dir = new fileSystem();
					dir.createFileDroppedDirectory(directory.replace("/home/","/") + orgDetails.getcleanURL() + "/input files/"+configurationDetails.getconfigName().toLowerCase().replace(" ", "")+"/");
					
					//Remove old directory
					dir.deleteDirectory(directory.replace("/home/","/") + fileDropField.getDirectory().trim());

					//Update file drop location
					fileDropField.setDirectory(orgDetails.getcleanURL() + "/input files/"+configurationDetails.getconfigName().toLowerCase().replace(" ", "")+"/");
					utconfigurationTransportManager.saveTransportFileDrop(fileDropField);
				    }
				}
			    }
			}
		    }
		    catch (Exception ex) {}
		}
	    }
	}
	
	//Log the update
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	configurationUpdateLogs updateLog = new configurationUpdateLogs();
	updateLog.setConfigId(configurationDetails.getId());
	updateLog.setUserId(userDetails.getId());
	updateLog.setUpdateMade("Configuration Details Updated");
	
	utconfigurationmanager.saveConfigurationUpdateLog(updateLog);
        
        //If the "Save" button was pressed 
        if (action.equals("save")) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/configurations/details");

            mav.addObject("organizations", organizations);
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
     * @param session
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
	mav.addObject("messageTypeId", configurationDetails.getMessageTypeId());

        // Get organization directory name
        Organization orgDetails = organizationmanager.getOrganizationById(configurationDetails.getorgId());
	
	configurationDetails.setOrgName(orgDetails.getOrgName());
	
	String helRegistryFolderName = "";
	
	//This is for eReferral configurations only
	if(configurationDetails.getMessageTypeId() == 1) {
	    if(orgDetails.getHelRegistryOrgId() > 0 || (orgDetails.getHelRegistryOrgId() == 0 && orgDetails.getHelRegistryId() > 0)) {
		List<helRegistry> helRegistries = helregistrymanager.getAllActiveRegistries();

		if(!helRegistries.isEmpty()) {
		    for(helRegistry registry : helRegistries) {
			if(registry.getId() == orgDetails.getHelRegistryId()) {
			    helRegistryFolderName = registry.getRegistryName().replace(" ", "-").toLowerCase();
			    mav.addObject("helRegistryFolderName", helRegistryFolderName);
			}
		    }
		}
	    }
	}
	mav.addObject("helRegistryFolderName", helRegistryFolderName);
	
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
        if (transportDetails == null) {
            transportDetails = new configurationTransport();

            if (configurationDetails.getType() == 1) {
                transportDetails.setfileLocation(orgDetails.getcleanURL() + "/input files/");
            } else {
                transportDetails.setfileLocation(orgDetails.getcleanURL() + "/output files/");
            }

            List<Integer> assocMessageTypes = new ArrayList<Integer>();
            assocMessageTypes.add(configurationDetails.getId());
            transportDetails.setmessageTypes(assocMessageTypes);
        }
	else {
	    transportDetails.setfileLocation(transportDetails.getfileLocation());
	}
	
	transportDetails.setHelRegistryId(orgDetails.getHelRegistryId());
	transportDetails.setHelSchemaName(orgDetails.getHelRegistrySchemaName());
	
	if(transportDetails.getHelRegistryConfigId() == null) {
	    transportDetails.setHelRegistryConfigId(0);
	}
 
        // Need to get any FTP fields
        List<configurationFTPFields> ftpFields = utconfigurationTransportManager.getTransportFTPDetails(transportDetails.getId());

        if (ftpFields.isEmpty()) {

            List<configurationFTPFields> emptyFTPFields = new ArrayList<configurationFTPFields>();
            configurationFTPFields pushFTPFields = new configurationFTPFields();
            pushFTPFields.setmethod(1);
            pushFTPFields.setdirectory("");

            configurationFTPFields getFTPFields = new configurationFTPFields();
            getFTPFields.setmethod(2);
            getFTPFields.setdirectory("");

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
	    
	    if(configurationDetails.getMessageTypeId() == 2 && configurationDetails.getType() == 2) {
		 pushRFields.setDirectory("/bowlink/");
	    }
	    else if(configurationDetails.getMessageTypeId() == 1) { 
		 pushRFields.setDirectory(orgDetails.getcleanURL() + "/input files/"+configurationDetails.getconfigName().toLowerCase().replace(" ", "")+"/");
	    }
	    else {
		pushRFields.setDirectory(orgDetails.getcleanURL() + "/input files/"+configurationDetails.getconfigName().toLowerCase().replace(" ", "")+"/");
	    }

	    configurationFileDropFields getRFields = new configurationFileDropFields();
            getRFields.setMethod(2);
	    if(configurationDetails.getMessageTypeId() == 2 && configurationDetails.getType() == 2) {
		getRFields.setDirectory("/bowlink/");
	    }
	    else if(configurationDetails.getMessageTypeId() == 1) { 
		getRFields.setDirectory(orgDetails.getcleanURL() + "/output files/"+configurationDetails.getconfigName().toLowerCase().replace(" ", "")+"/");
	    }
	    else {
		getRFields.setDirectory("/");
	    }

            emptyFileDropFields.add(pushRFields);
            emptyFileDropFields.add(getRFields);

            transportDetails.setFileDropFields(emptyFileDropFields);
        } else {
	   
	    //Check that the file drop location has the current configuration name
	    String fileLocationConfigName = "";
	    try {
		for(configurationFileDropFields fileDropField : fileDropFields) {
		    
		    if(fileDropField.getDirectory().contains(orgDetails.getcleanURL()+"/input files/")) {
			fileLocationConfigName = fileDropField.getDirectory().substring(fileDropField.getDirectory().lastIndexOf("/input files/"), fileDropField.getDirectory().length()-1);
			fileLocationConfigName = fileLocationConfigName.replace("/input files/","");
			if(!"".equals(fileLocationConfigName)) {
			    if(!fileLocationConfigName.equals(configurationDetails.getconfigName().toLowerCase().replace(" ", ""))) {
				//Create new directory
				String directory = myProps.getProperty("ut.directory.utRootDir");
				fileSystem dir = new fileSystem();
				dir.createFileDroppedDirectory(directory.replace("/home/","/") + orgDetails.getcleanURL() + "/input files/"+configurationDetails.getconfigName().toLowerCase().replace(" ", "")+"/");
				
				//Remove old directory
				dir.deleteDirectory(directory.replace("/home/","/") + orgDetails.getcleanURL() + "/input files/"+fileLocationConfigName);
				
				//Update file drop location
				fileDropField.setDirectory(orgDetails.getcleanURL() + "/input files/"+configurationDetails.getconfigName().toLowerCase().replace(" ", "")+"/");
				utconfigurationTransportManager.saveTransportFileDrop(fileDropField);
			    }
			}
		    }
		}
	    }
	    catch (Exception ex) {}
	    
            transportDetails.setFileDropFields(fileDropFields);
        }

	//get direct messaging fields
	organizationDirectDetails  directMessageDetails = utconfigurationTransportManager.getDirectMessagingDetailsById(configurationDetails.getorgId());

	List<organizationDirectDetails> directMessageFields = new ArrayList<>();
	
	if(directMessageDetails == null) {
	    directMessageDetails = new organizationDirectDetails();
	    directMessageDetails.setOrgId(configurationDetails.getorgId());
	    
	    directMessageFields.add(directMessageDetails);
	}
	else {
	    directMessageFields.add(directMessageDetails);
	}
	
	transportDetails.setDirectMessageFields(directMessageFields);
	
	mav.addObject("transportDetails", transportDetails);

        transportDetails.setconfigId(configId);
	transportDetails.setThreshold(configurationDetails.getThreshold());
        
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
	mav.addObject("cleanOrgURL",orgDetails.getCleanURL());
	
        configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Get the list of available transport methods
        List transportMethods = utconfigurationTransportManager.getTransportMethodsByType(configurationDetails);
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
	
	//Get a list of availbale HISPs
	List<hisps> hisps = hispManager.getAllActiveHisps();
	mav.addObject("hisps", hisps);
	
	TimeZone timeZone = TimeZone.getTimeZone(siteTimeZone);
	DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	requiredFormat.setTimeZone(timeZone);
	String dateinTZ = "";
	
	//Get latest configuration note
	List<configurationUpdateLogs> configNotes = utconfigurationmanager.getConfigurationUpdateLogs(configId);
	if(!configNotes.isEmpty()) {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configNotes.get(0).getDateCreated())));
	}
	else {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configurationDetails.getDateCreated())));
	}

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
     * @param authentication
     *
     * @return	This function will either return to the transport details screen or redirect to the next step (Field Mappings)
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/transport", method = RequestMethod.POST)
    public ModelAndView updateTransportDetails(HttpSession session, @Valid @ModelAttribute(value = "transportDetails") configurationTransport transportDetails, BindingResult result, RedirectAttributes redirectAttr,
            @RequestParam String action, @RequestParam(value = "domain1", required = false) String domain1, Authentication authentication
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
	
        // submit the updates
        Integer transportId = utconfigurationTransportManager.updateTransportDetails(configurationDetails,transportDetails);
	
	configurationDetails.setThreshold(transportDetails.getThreshold());
	utconfigurationmanager.updateConfiguration(configurationDetails);
	
	//Log the update
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	configurationUpdateLogs updateLog = new configurationUpdateLogs();
	updateLog.setConfigId(configurationDetails.getId());
	updateLog.setUserId(userDetails.getId());
	updateLog.setUpdateMade("Configuration Transport Method Updated");
	utconfigurationmanager.saveConfigurationUpdateLog(updateLog);
	
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
	
	if(configurationDetails.getType() == 2) {
	    //Need to set up the FTP information if any has been entered
	    if(transportDetails.gettransportMethodId() != 3) {
		utconfigurationmanager.deleteConfigurationFTPInformation(transportId);
	    }
	    else {
		if (transportDetails.gettransportMethodId() == 3 && !transportDetails.getFTPFields().isEmpty()) {
		    for (configurationFTPFields ftpFields : transportDetails.getFTPFields()) {
			if(ftpFields.getip() != null) {
			    if(!"".equals(ftpFields.getip())) {
				ftpFields.settransportId(transportId);
				utconfigurationTransportManager.saveTransportFTP(configurationDetails.getorgId(), ftpFields);
			    }
			}
		    }
		}
	    }
	}
	else {
	    if (!transportDetails.getFTPFields().isEmpty()) {
		for (configurationFTPFields ftpFields : transportDetails.getFTPFields()) {
		    if(ftpFields.getip() != null) {
			if(!"".equals(ftpFields.getip())) {
			    ftpFields.settransportId(transportId);
			    utconfigurationTransportManager.saveTransportFTP(configurationDetails.getorgId(), ftpFields);
			}
		    }
		}
	    }
	}
	
        // need to get file drop info if any has been entered 
	if (!transportDetails.getFileDropFields().isEmpty()) {
	    fileSystem dir = new fileSystem();
	    
	    String directory = myProps.getProperty("ut.directory.utRootDir");
	    
	    for (configurationFileDropFields fileDropFields : transportDetails.getFileDropFields()) {
		
	       dir.createFileDroppedDirectory(directory+fileDropFields.getDirectory());
		
               fileDropFields.setTransportId(transportId);
               utconfigurationTransportManager.saveTransportFileDrop(fileDropFields);
            }
        }
	
	//Direct Message Transport
	if(transportDetails.getDirectMessageFields() != null) {
	    if(!transportDetails.getDirectMessageFields().isEmpty()) {
		if(transportDetails.getDirectMessageFields().get(0).getHispId() > 0) {
		    transportDetails.getDirectMessageFields().get(0).setFileTypeId(transportDetails.getfileType());
		    transportDetails.getDirectMessageFields().get(0).setExpectedFileExt(transportDetails.getfileExt());
		    transportDetails.getDirectMessageFields().get(0).setStatus(true);
		    transportDetails.getDirectMessageFields().get(0).setDateModified(new Date());
		    
		    utconfigurationTransportManager.saveTransportDirectMessageDetails(transportDetails.getDirectMessageFields().get(0));
		    
		}
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
	
	//If transport method == 10 (From HEL Registry online form) we can prepoulate the fields from
	//the selected configuration. No need to have a custom template uploaded. The file submitted 
	//to UT with this transport method will always have the same fields set up.
	if(((transportDetails.gettransportMethodId() == 13 && configurationDetails.getType() == 2 && configurationDetails.getMessageTypeId() == 1 && transportDetails.getHelRegistryId() > 0) || transportDetails.gettransportMethodId() == 10)) {
	    
	    List<configurationFormFields> existingFormFields = utconfigurationTransportManager.getConfigurationFieldsToCopy(transportDetails.getconfigId());
	    
	    if(existingFormFields.isEmpty()) {
		if(transportDetails.getHelRegistryConfigId() != null && transportDetails.getHelSchemaName() != null) {
		    if(transportDetails.getHelRegistryConfigId() > 0 && !"".equals(transportDetails.getHelSchemaName())) {
			 utconfigurationTransportManager.populateFieldsFromHELConfiguration(transportDetails.getconfigId(), transportDetails.getId(),transportDetails.getHelRegistryConfigId(),transportDetails.getHelSchemaName(),false);
		    }
		}
	    }
	}
	
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
		
		//
		if(transportDetails.gettransportMethodId() == 8) {
		    ModelAndView mav = new ModelAndView(new RedirectView("mappings"));
		    return mav;
		}
		//Check if passthru
		else if(configurationDetails.getConfigurationType() == 2) {
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
     * @param session
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
	
	//Get the utConfiguration details for the selected config
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	
	// Get organization directory name
        Organization orgDetails = organizationmanager.getOrganizationById(configurationDetails.getorgId());
	
        mav.setViewName("/administrator/configurations/messagespecs");

        configurationMessageSpecs messageSpecs = utconfigurationmanager.getMessageSpecs(configId);
	
        if (messageSpecs == null) {
            messageSpecs = new configurationMessageSpecs();
            messageSpecs.setconfigId(configId);
        }
	else {
	    DateFormat finalDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	    
	    if(messageSpecs.gettemplateFile() == null) {
		mav.addObject("lastUploadedDate", finalDateFormat.format(configurationDetails.getDateCreated()));
	    }
	    else {
		
		//Check if file exists
		File templateFile = new File( myProps.getProperty("ut.directory.utRootDir") + orgDetails.getCleanURL() + "/templates/"+messageSpecs.gettemplateFile());
		
		if(templateFile.exists()) {
		    if(messageSpecs.gettemplateFile().contains("-")) {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
			try {
			    String dateUploaded = messageSpecs.gettemplateFile().split("-")[0];
			    mav.addObject("lastUploadedDate", finalDateFormat.format(dateFormat.parse(dateUploaded)));
			}
			catch (Exception ex) {
			    try {
				mav.addObject("lastUploadedDate", finalDateFormat.format(new Date(templateFile.lastModified())));
			    }
			    catch (Exception ex2) {
				mav.addObject("lastUploadedDate", finalDateFormat.format(configurationDetails.getDateCreated()));
			    }
			}
		    }
		    else {
			try {
			    mav.addObject("lastUploadedDate", finalDateFormat.format(new Date(templateFile.lastModified())));
			}
			catch (Exception ex) {
			    mav.addObject("lastUploadedDate", finalDateFormat.format(configurationDetails.getDateCreated()));
			}
		    }
		}
		else {
		    mav.addObject("lastUploadedDate", finalDateFormat.format(configurationDetails.getDateCreated()));
		}
	    }
	}
        mav.addObject("messageSpecs", messageSpecs);

        //Need to pass the selected transport Type
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
        mav.addObject("transportType", transportDetails.gettransportMethodId());
	mav.addObject("fileType", transportDetails.getfileType());
	mav.addObject("transportDetails", transportDetails);

        //Set the variable id to hold the current utConfiguration id
        mav.addObject("id", configId);
	mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));
	
	mav.addObject("cleanOrgURL",orgDetails.getCleanURL());
	
	configurationDetails.setOrgName(orgDetails.getOrgName());
	
	configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);
	
        //Need to get all available fields that can be used for the reportable fields
        List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
        mav.addObject("availableFields", fields);
	
	TimeZone timeZone = TimeZone.getTimeZone(siteTimeZone);
	DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	requiredFormat.setTimeZone(timeZone);
	String dateinTZ = "";
	
	//Get latest configuration note
	List<configurationUpdateLogs> configNotes = utconfigurationmanager.getConfigurationUpdateLogs(configId);
	if(!configNotes.isEmpty()) {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configNotes.get(0).getDateCreated())));
	}
	else {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configurationDetails.getDateCreated())));
	}

        return mav;
    }

    /**
     * The '/messagespecs' POST request submit all the utConfiguration message specs.
     *
     * @param session
     * @param messageSpecs Will contain the contents of the utConfiguration message spec form.
     * @param result
     * @param redirectAttr
     * @param action
     *
     * @return	This function will either return to the message spec details screen or redirect to the next step (Field Mappings)
     *
     * @throws Exception
     *
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/messagespecs", method = RequestMethod.POST)
    public ModelAndView updateMessageSpecs(HttpSession session,
	    @Valid @ModelAttribute(value = "messageSpecs") configurationMessageSpecs messageSpecs, 
	    BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action, Authentication authentication) throws Exception {

	//Need to pass the selected transport Type
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(messageSpecs.getconfigId());
	
	utConfiguration configDetails = utconfigurationmanager.getConfigurationById(messageSpecs.getconfigId());

        //Save/Update the configuration message specs
	try {
	    utconfigurationmanager.updateMessageSpecs(messageSpecs, transportDetails.getId(), transportDetails.getfileType(), messageSpecs.isHasHeader(), messageSpecs.getFileLayout());
	}
	catch (Exception ex) {
	    if(ex.getMessage().contains("The uploaded template")) {
		redirectAttr.addFlashAttribute("templateError", ex.getMessage().replace("java.lang.Exception:", ""));
		ModelAndView mav = new ModelAndView(new RedirectView("messagespecs"));
		return mav;
	    }
	}
	
        redirectAttr.addFlashAttribute("savedStatus", "updated");
	
	if(configDetails.getstepsCompleted() < 3) {
	    configDetails.setstepsCompleted(3);
	    utconfigurationmanager.updateConfiguration(configDetails);
	}
	
	//If excel enter in the ref_configexceldetails
	if(transportDetails.getfileType() == 11) {
	    utconfigurationmanager.updateExcelConfigDetails(configDetails.getorgId(),messageSpecs);
	}
	
	//Log the update
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	configurationUpdateLogs updateLog = new configurationUpdateLogs();
	updateLog.setConfigId(configDetails.getId());
	updateLog.setUserId(userDetails.getId());
	updateLog.setUpdateMade("Configuration Message Specs Updated");
	utconfigurationmanager.saveConfigurationUpdateLog(updateLog);

        //If the "Save" button was pressed
        if (action.equals("save")) {
            ModelAndView mav = new ModelAndView(new RedirectView("messagespecs"));
            return mav;
        } else {
	   ModelAndView mav = new ModelAndView(new RedirectView("mappings"));
	   return mav;
        }
    }

    /**
     * The '/mappings' GET request will determine based on the selected transport method what page to display.Either the choose fields page if 'online form' is selected or 'mappings' if a custom file is being uploaded.
     * @param session
     * @return 
     * @throws java.lang.Exception 
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
	
	TimeZone timeZone = TimeZone.getTimeZone(siteTimeZone);
	DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	requiredFormat.setTimeZone(timeZone);
	String dateinTZ = "";
	
	//Get latest configuration note
	List<configurationUpdateLogs> configNotes = utconfigurationmanager.getConfigurationUpdateLogs(configId);
	if(!configNotes.isEmpty()) {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configNotes.get(0).getDateCreated())));
	}
	else {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configurationDetails.getDateCreated())));
	}

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
	    RedirectAttributes redirectAttr, @RequestParam String action, @RequestParam int transportMethod, @RequestParam int errorHandling, Authentication authentication) throws Exception {

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
	
	//For a target configuration we need to check to see if default values were added/changed
	//if so we need to modify the association connection fields so the defaultValues are also updated
	if(configurationDetails.getType() == 2) {
	    //Get a list of connection field mappings
	    List<configurationconnectionfieldmappings> connectionFieldMappings = utconfigurationTransportManager.getTargetConfigurationFieldsToCopy(configId);
	    
	    //Get a list of fields
	    List<configurationFormFields> configurationFields = utconfigurationTransportManager.getConfigurationFields(configId, transportDetails.getId());
	    
	    if(!connectionFieldMappings.isEmpty() && !configurationFields.isEmpty()) {
		String updateSQLStatement = "";
		for(configurationFormFields configField : configurationFields) {
		    for(configurationconnectionfieldmappings connectionField : connectionFieldMappings) {
			if(connectionField.getAssociatedFieldNo() == 0) {
			    if(connectionField.getFieldNo() == configField.getFieldNo()) {
				if("".equals(configField.getDefaultValue()) && connectionField.getDefaultValue() != null) {
				    updateSQLStatement += "update configurationconnectionfieldmappings set defaultValue = null where id = " + connectionField.getId() + ";";
				}
				else if(!"".equals(configField.getDefaultValue()) && (connectionField.getDefaultValue() == null || !connectionField.getDefaultValue().equals(configField.getDefaultValue()))) {
				    updateSQLStatement += "update configurationconnectionfieldmappings set defaultValue = " + configField.getDefaultValue() + " where id = " + connectionField.getId() + ";";
				}
			    }
			}
		    }
		}
		
		if(!"".equals(updateSQLStatement)) {
		    try {
			utconfigurationTransportManager.executeConfigTransportSQL(updateSQLStatement);
		    }
		    catch (Exception ex) {
			
		    }
		}
	    }
	}
	
	//Log the update
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	configurationUpdateLogs updateLog = new configurationUpdateLogs();
	updateLog.setConfigId(configId);
	updateLog.setUserId(userDetails.getId());
	updateLog.setUpdateMade("Configuration Field Settings Updated");
	utconfigurationmanager.saveConfigurationUpdateLog(updateLog);

        //If the "Save" button was pressed 
        if (action.equals("save")) {
            return 1;
        } 
	else {
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
        List<Crosswalks> crosswalks = messagetypemanager.getCrosswalksForConfig(1, 0, configurationDetails.getorgId(),configurationDetails.getId(), false);
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
	
	TimeZone timeZone = TimeZone.getTimeZone(siteTimeZone);
	DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	requiredFormat.setTimeZone(timeZone);
	String dateinTZ = "";
	
	//Get latest configuration note
	List<configurationUpdateLogs> configNotes = utconfigurationmanager.getConfigurationUpdateLogs(configId);
	if(!configNotes.isEmpty()) {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configNotes.get(0).getDateCreated())));
	}
	else {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configurationDetails.getDateCreated())));
	}

        return mav;
    }

    /**
     * The '/getMacroDetails.do' function will display the modal window for the selected macro form.
     *
     * @param macroId The id of the selected macro
     * @param session
     * @return 
     * @throws java.lang.Exception
     *
     */
    @RequestMapping(value = "/getMacroDetails.do", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getMacroDetails(@RequestParam(value = "macroId", required = true) Integer macroId, HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/macroDetails");

        Macros macroDetails = utconfigurationmanager.getMacroById(macroId);

	boolean questionContainsCW = false;
	boolean con1ContainsCW = false;
	boolean con2ContainsCW = false;
	boolean fieldAContainsCW = false;
	boolean fieldBContainsCW = false;
	
	if(macroDetails.getcon1Question() != null) {
	    if(macroDetails.getcon1Question().contains("crosswalk")) {
		questionContainsCW = true;
		con1ContainsCW = true;
		macroDetails.setCon1Question("Please select the crosswalk");
	    }
	}
	
	if(macroDetails.getcon2Question() != null) {
	    if(macroDetails.getcon2Question().contains("crosswalk")) {
		questionContainsCW = true;
		con2ContainsCW = true;
		macroDetails.setCon2Question("Please select the crosswalk");
	    }
	}
	
	if(macroDetails.getfieldAQuestion() != null) {
	    if(macroDetails.getfieldAQuestion().contains("crosswalk")) {
		questionContainsCW = true;
		fieldAContainsCW = true;
		macroDetails.setfieldAQuestion("Please select the crosswalk");
	    }
	}
	
	if(macroDetails.getfieldBQuestion() != null) {
	    if(macroDetails.getfieldBQuestion().contains("crosswalk")) {
		questionContainsCW = true;
		fieldBContainsCW = true;
		macroDetails.setfieldBQuestion("Please select the crosswalk");
	    }
	}
	
	mav.addObject("fieldA_Question", macroDetails.getfieldAQuestion().trim());
        mav.addObject("fieldB_Question", macroDetails.getfieldBQuestion().trim());
        mav.addObject("Con1_Question", macroDetails.getcon1Question().trim());
        mav.addObject("Con2_Question", macroDetails.getcon2Question().trim());
        mav.addObject("populateFieldA", macroDetails.isPopulateFieldA());
	mav.addObject("con1ContainsCW", con1ContainsCW);
	mav.addObject("con2ContainsCW", con2ContainsCW);
	mav.addObject("fieldAContainsCW", fieldAContainsCW);
	mav.addObject("fieldBContainsCW", fieldBContainsCW);
	
	if(questionContainsCW) {
	   Integer configId = (Integer) session.getAttribute("manageconfigId");
	
	   utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);

	   //Return a list of available crosswalks
	   List<Crosswalks> crosswalks = messagetypemanager.getCrosswalksForConfig(1, 0, configurationDetails.getorgId(),configurationDetails.getId(), false); 
	    
	   mav.addObject("crosswalks", crosswalks);
	}
	
        return mav;
    }

    /**
     * The '/translations' POST request will submit the selected data translations and save it to the data base.
     *
     * @param session
     * @param categoryId
     * @param authentication
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/translations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer submitDataTranslations(HttpSession session, @RequestParam(value = "categoryId", required = true) Integer categoryId, Authentication authentication) throws Exception {

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
	
	//Log the update
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	configurationUpdateLogs updateLog = new configurationUpdateLogs();
	updateLog.setConfigId(configId);
	updateLog.setUserId(userDetails.getId());
	if(categoryId == 1) {
	    updateLog.setUpdateMade("Configuration Data Translations Updated");
	}
	else if(categoryId == 2) {
	     updateLog.setUpdateMade("Configuration Pre-Process Macros Updated");
	}
	else {
	     updateLog.setUpdateMade("Configuration Post-Process Macros Updated");
	}
	utconfigurationmanager.saveConfigurationUpdateLog(updateLog);
       
        return 1;
    }

    /**
     * The '/getTranslations.do' function will return the list of existing translations set up for the selected utConfiguration/transportMethod.
     *
     * @param session
     * @param reload
     * @param categoryId
     * @return 
     * @throws java.lang.Exception 
     * @Return list of translations
     */
    @RequestMapping(value = "/getTranslations.do", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getTranslations(HttpSession session,@RequestParam(value = "reload", required = true) boolean reload, @RequestParam(value = "categoryId", required = true) Integer categoryId) throws Exception {

        ModelAndView mav = new ModelAndView();
	
	Integer configId = (Integer) session.getAttribute("manageconfigId");
        mav.setViewName("/administrator/configurations/existingTranslations");
	
	List<configurationDataTranslations> translations;
	if(null == categoryId) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
	}
	else switch (categoryId) {
	    case 1:
		translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
		break;
	    case 2:
		translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPreProcessingTranslastions");
		break;
	    case 3:
		translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPostProcessingTranslastions");
		break;
	    default:
		translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
		break;
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
	    Integer cwId = 0;
	    
            for (configurationDataTranslations translation : existingTranslations) {
		cwId = 0;
		
                //Get the field name by id
                fieldName = utconfigurationmanager.getFieldName(translation.getFieldId());
                translation.setfieldName(fieldName);
		
		if (translation.getCrosswalkId() != 0) {
		    cwId = translation.getCrosswalkId();
		}
		
		//Get the macro name by id
                if (translation.getMacroId() > 0) {
                    Macros macroDetails = utconfigurationmanager.getMacroById(translation.getMacroId());
                    macroName = macroDetails.getMacroName();
                    if (macroName.contains("DATE")) {
                        macroName = macroDetails.getMacroName()+ " " + macroDetails.getdateDisplay();
                    }
                    translation.setMacroName(macroName);
		    
		    if(macroDetails.getcon1Question() != null) {
			if(macroDetails.getcon1Question().contains("crosswalk")) {
			    if(translation.getConstant1() != null) {
				if(!"".equals(translation.getConstant1())) {
				    cwId = Integer.parseInt(translation.getConstant1());
				}
			    }
			}
		    }

		    if(macroDetails.getcon2Question() != null) {
			if(macroDetails.getcon2Question().contains("crosswalk")) {
			    if(translation.getConstant2() != null) {
				if(!"".equals(translation.getConstant2())) {
				    cwId = Integer.parseInt(translation.getConstant2());
				}
			    }
			}
		    }

		    if(macroDetails.getfieldAQuestion() != null) {
			if(macroDetails.getfieldAQuestion().contains("crosswalk")) {
			    if(translation.getFieldA() != null) {
				if(!"".equals(translation.getFieldA())) {
				    cwId = Integer.parseInt(translation.getFieldA());
				}
			    }
			}
		    }

		    if(macroDetails.getfieldBQuestion() != null) {
			if(macroDetails.getfieldBQuestion().contains("crosswalk")) {
			    if(translation.getFieldB() != null) {
				if(!"".equals(translation.getFieldB())) {
				    cwId = Integer.parseInt(translation.getFieldB());
				}
			    }
			}
		    }
                }
		
                //Get the crosswalk name by id
                if (cwId > 0) {
                    defaultValues = new HashMap<>();
                    crosswalkName = messagetypemanager.getCrosswalkName(cwId);
                    translation.setcrosswalkName(crosswalkName);

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
            }
        }
	
	if(translations != null) {
	    switch(categoryId) {
		case 1:
		    session.setAttribute("confgirationDataTranslastions", translations);
		case 2:
		    session.setAttribute("confgirationDataPreProcessingTranslastions", translations);
		case 3:
		    session.setAttribute("confgirationDataPostProcessingTranslastions", translations);
		default:
		    session.setAttribute("confgirationDataTranslastions", translations);
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
	
	if(null == categoryId) {
	    translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPostProcessingTranslastions");
	}
	else switch (categoryId) {
	    case 1:
		translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataTranslastions");
		break;
	    case 2:
		translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPreProcessingTranslastions");
		break;
	    default:
		translations = (List<configurationDataTranslations>) session.getAttribute("confgirationDataPostProcessingTranslastions");
		break;
	}
	
	Integer processOrder = 0;
	
	if(translations == null) {
	    processOrder = 1;
	}
	else if(translations.isEmpty()) {
	    processOrder = 1;
	}
	else {
	    processOrder = translations.size() + 1;
	}

        if (macroId == null) {
            macroId = 0;
            macroName = null;
        }
        if (cwId == null) {
            cwId = 0;
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
     * @param categoryId
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
	    if(translation.getProcessOrder() >= newProcessOrder && translation.getProcessOrder() < currProcessOrder) {
		translation.setProcessOrder(translation.getProcessOrder()+1);
	    }
	    else if(translation.getProcessOrder() <= newProcessOrder && translation.getProcessOrder() > currProcessOrder) {
		translation.setProcessOrder(translation.getProcessOrder()-1);
	    }
	    else if(translation.getProcessOrder() == currProcessOrder) {
		translation.setProcessOrder(newProcessOrder);
	    }
	}
        
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
	
	TimeZone timeZone = TimeZone.getTimeZone(siteTimeZone);
	DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	requiredFormat.setTimeZone(timeZone);
	String dateinTZ = "";
	
	//Get latest configuration note
	List<configurationUpdateLogs> configNotes = utconfigurationmanager.getConfigurationUpdateLogs(configId);
	if(!configNotes.isEmpty()) {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configNotes.get(0).getDateCreated())));
	}
	else {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configurationDetails.getDateCreated())));
	}
	
        return mav;
    }

    /**
     * The '/scheduling' POST request will submit the scheduling settings for the selected utConfiguration.
     *
     * @param session
     * @param scheduleDetails The object that will hold the scheduling form fields
     * @param redirectAttr
     * @param action
     * @param authentication
     *
     * @return This method will redirect the user back to the scheduling form page.
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/scheduling", method = RequestMethod.POST)
    public ModelAndView submitConfigurationSchedules(HttpSession session,@ModelAttribute(value = "scheduleDetails") configurationSchedules scheduleDetails, RedirectAttributes redirectAttr, @RequestParam String action, Authentication authentication) throws Exception {

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
	
	//Log the update
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	configurationUpdateLogs updateLog = new configurationUpdateLogs();
	updateLog.setConfigId(configId);
	updateLog.setUserId(userDetails.getId());
	updateLog.setUpdateMade("Configuration Schedule Updated");
	utconfigurationmanager.saveConfigurationUpdateLog(updateLog);

        redirectAttr.addFlashAttribute("savedStatus", "updated");
	
	boolean HL7Val = false;
	boolean CCDVal = false;
	
	if(session.getAttribute("configHL7") != null) {
	    HL7Val = (boolean) session.getAttribute("configHL7");
	}
	
	if(session.getAttribute("configCCD") != null) {
	    CCDVal = (boolean) session.getAttribute("configCCD");
	}
	
        if ("save".equals(action)) {
	    
	    if(configurationDetails.getConfigurationType() == 2) {
		ModelAndView mav = new ModelAndView(new RedirectView("/administrator/configurations/list?msg=updated"));
		return mav;
	    }
	    else {
		 ModelAndView mav = new ModelAndView(new RedirectView("scheduling"));
		return mav;
	    }
           
        } 
	else if (HL7Val) {
            ModelAndView mav = new ModelAndView(new RedirectView("HL7"));
            return mav;
        } 
	else if (CCDVal) {
            ModelAndView mav = new ModelAndView(new RedirectView("CCD"));
            return mav;
        } 
	else {
	    ModelAndView mav = new ModelAndView(new RedirectView("/administrator/configurations/list?msg=updated"));
            //ModelAndView mav = new ModelAndView(new RedirectView("preprocessing"));
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
        configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

        //pass the utConfiguration detail object back to the page.
        mav.addObject("configurationDetails", configurationDetails);

        //Set the variable to hold the number of completed steps for this utConfiguration;
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        HL7Details hl7Details = utconfigurationmanager.getHL7Details(configId);
        int HL7Id = 0;

        // If null then create an empty HL7 Detail object
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
     * @throws java.lang.Exception
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
     * @param HL7Details
     * @param redirectAttr
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/HL7", method = RequestMethod.POST)
    public ModelAndView saveHL7Customization(@ModelAttribute(value = "HL7Details") HL7Details HL7Details, RedirectAttributes redirectAttr, Authentication authentication) throws Exception {

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
        } catch (Exception e) {}
	
	//Log the update
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	configurationUpdateLogs updateLog = new configurationUpdateLogs();
	updateLog.setConfigId(HL7Details.getconfigId());
	updateLog.setUserId(userDetails.getId());
	updateLog.setUpdateMade("Configuration HL7 section Updated");
	utconfigurationmanager.saveConfigurationUpdateLog(updateLog);

        redirectAttr.addFlashAttribute("savedStatus", "updated");
        ModelAndView mav = new ModelAndView(new RedirectView("HL7"));
        return mav;
    }

    /**
     * The '/newHL7Segment' GET request will be used to display the blank new HL7 Segment screen (In a modal)
     *
     *
     * @param hl7Id
     * @param nextPos
     * @return	The HL7 Segment blank form page
     * @throws java.lang.Exception
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
     * @param hl7Id
     * @param segmentId
     * @param nextPos
     * @return	The HL7 Segment Element blank form page
     * @throws java.lang.Exception
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
     * @param session
     * @param elementId
     * @param nextPos
     * @return	The HL7 Element Component blank form page
     * @throws java.lang.Exception
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
     * The '/preprocessing' GET request will display the utConfiguration preprocessing page
     * @param session
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/preprocessing", method = RequestMethod.GET)
    public ModelAndView getPreProcessing(HttpSession session) throws Exception {
	
	List<configurationDataTranslations> preProcessingTranslations = new CopyOnWriteArrayList<>();
	session.setAttribute("confgirationDataPreProcessingTranslastions", preProcessingTranslations);

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
        List<Integer> macroLookUpList = new ArrayList<>();
	macros.stream().filter((macro) -> (macro.getfieldAQuestion() != null || macro.getfieldBQuestion() != null || macro.getcon1Question() != null || macro.getcon2Question() != null)).forEachOrdered((macro) -> {
	    macroLookUpList.add(macro.getId());
	});
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
     * @param session
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/postprocessing", method = RequestMethod.GET)
    public ModelAndView getPostProcessing(HttpSession session) throws Exception {
	
	List<configurationDataTranslations> postProcessingTranslations = new CopyOnWriteArrayList<>();
	session.setAttribute("confgirationDataPostProcessingTranslastions", postProcessingTranslations);

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
        List<Integer> macroLookUpList = new ArrayList<>();
	macros.stream().filter((macro) -> (macro.getfieldAQuestion() != null || macro.getfieldBQuestion() != null || macro.getcon1Question() != null || macro.getcon2Question() != null)).forEachOrdered((macro) -> {
	    macroLookUpList.add(macro.getId());
	});
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
     * @return 
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
     * @param elementId
     * @return 
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
     * @return 
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
     * @param session
     * @return 
     * @throws java.lang.Exception
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
     * @param session
     * @return This function will display the new ccd element overlay
     * @throws java.lang.Exception
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
     * @param session
     * @param elementId
     * @return This function will display the new ccd element overlay
     * @throws java.lang.Exception
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
     * @param ccdElement
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
     * @param transportId
     * @return The method will return a 1 back to the calling ajax function.
     * @throws java.lang.Exception
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
     * @param request
     * @param cwsf
     * @return The method will an updated configurationWebServiceFields containing new sender domains
     * @throws java.lang.Exception
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
     * @param configId
     * @return 
     * @throws java.lang.Exception
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
	newConfig.setconfigName(configDetails.getconfigName() + " - COPY");
	newConfig.setThreshold(configDetails.getThreshold());
	
	//Save new Configuration
	Integer id = (Integer) utconfigurationmanager.createConfiguration(newConfig);
	
	utConfiguration newconfigDetails = utconfigurationmanager.getConfigurationById(id);
	
	//Get the existing transport details
	configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
	
	//Save new transport Details
	configurationTransport newTransportDetails = new configurationTransport();
	newTransportDetails.setconfigId(id);
	newTransportDetails.settransportMethodId(transportDetails.gettransportMethodId());
	newTransportDetails.setfileType(transportDetails.getfileType());
	newTransportDetails.setfileDelimiter(transportDetails.getfileDelimiter());
	newTransportDetails.setstatus(true);
	newTransportDetails.settargetFileName(transportDetails.gettargetFileName());
	newTransportDetails.setappendDateTime(transportDetails.getappendDateTime());
	newTransportDetails.setmaxFileSize(transportDetails.getmaxFileSize());
	newTransportDetails.setclearRecords(true);
	newTransportDetails.setfileLocation(transportDetails.getfileLocation());
	newTransportDetails.setautoRelease(true);
	newTransportDetails.seterrorHandling(transportDetails.geterrorHandling());
	newTransportDetails.setmergeBatches(transportDetails.getmergeBatches());
	newTransportDetails.setfileExt(transportDetails.getfileExt());
	newTransportDetails.setEncodingId(transportDetails.getEncodingId());
	newTransportDetails.setCcdSampleTemplate(transportDetails.getCcdSampleTemplate());
	newTransportDetails.setHL7PDFSampleTemplate(transportDetails.getHL7PDFSampleTemplate());
	newTransportDetails.setMassTranslation(true);
	newTransportDetails.setZipped(transportDetails.isZipped());
	newTransportDetails.setZipType(transportDetails.getZipType());
	newTransportDetails.setRestAPIURL(transportDetails.getRestAPIURL());
	newTransportDetails.setRestAPIUsername(transportDetails.getRestAPIUsername());
	newTransportDetails.setRestAPIPassword(transportDetails.getRestAPIPassword());
	newTransportDetails.setRestAPIType(transportDetails.getRestAPIType());
	newTransportDetails.setRestAPIFunctionId(transportDetails.getRestAPIFunctionId());
	newTransportDetails.setJsonWrapperElement(transportDetails.getJsonWrapperElement());
	newTransportDetails.setLineTerminator(transportDetails.getLineTerminator());
	newTransportDetails.setDmConfigKeyword(transportDetails.getDmConfigKeyword());
	newTransportDetails.setPopulateInboundAuditReport(transportDetails.isPopulateInboundAuditReport());
	newTransportDetails.setHelRegistryConfigId(transportDetails.getHelRegistryConfigId());
	newTransportDetails.setHelSchemaName(transportDetails.getHelSchemaName());
	newTransportDetails.setHelRegistryId(transportDetails.getHelRegistryId());
	
	Integer transportDetailId = utconfigurationTransportManager.updateTransportDetails(newconfigDetails,newTransportDetails);
	
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
     * @param sourceConfigId
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
     * @param configId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/deleteConfiguration.do", method = RequestMethod.POST)
    public @ResponseBody
    Integer deleteConfiguration(@RequestParam int configId) throws Exception {

        utConfiguration configDetails = utconfigurationmanager.getConfigurationById(configId);
	configDetails.setDeleted(true);
	
	utconfigurationmanager.updateConfiguration(configDetails);
	
        return 1;
    }
    
    /**
     * The '/getCrosswalks.do' function will return all the available crosswalks.
     *
     * @param page
     * @param orgId
     * @param maxCrosswalks
     * @param configId
     * @param inUseOnly
     * @return 
     * @throws java.lang.Exception 
     * @Return list of crosswalks
     */
    @RequestMapping(value = "/getCrosswalks.do", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getCrosswalks(@RequestParam(value = "page", required = false) Integer page, 
	    @RequestParam(value = "orgId", required = false) Integer orgId, 
	    @RequestParam(value = "maxCrosswalks", required = false) Integer maxCrosswalks,
	    @RequestParam(value = "configId", required = false) Integer configId,
	    @RequestParam(value = "inUseOnly", required = false) boolean inUseOnly) throws Exception {

        if (page == null) {
            page = 1;
        }

        if (orgId == null) {
            orgId = 0;
        }

        if (maxCrosswalks == null) {
            maxCrosswalks = 4;
        }
	
        double maxCrosswalkVal = maxCrosswalks;

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/crosswalks");
        mav.addObject("orgId", orgId);

        //Need to return a list of crosswalks
        List<Crosswalks> crosswalks = messagetypemanager.getCrosswalksForConfig(page, maxCrosswalks, orgId, configId, inUseOnly);
	if(!crosswalks.isEmpty()) {
	   for(Crosswalks crosswalk : crosswalks) {
	       if(crosswalk.getLastUpdated() == null) {
		   crosswalk.setLastUpdated(crosswalk.getDateCreated());
	       }
	   }
	}
	
        mav.addObject("availableCrosswalks", crosswalks);

        //Find out the total number of crosswalks
        double totalCrosswalks = messagetypemanager.findTotalCrosswalks(orgId);

        Integer totalPages = (int) Math.ceil((double) totalCrosswalks / maxCrosswalkVal);
	
        mav.addObject("totalPages", totalPages);
        mav.addObject("currentPage", page);

        return mav;
    }
    
    /**
     * The '/newCrosswalk' GET request will be used to return a blank crosswalk form.
     *
     *
     * @param orgId
     * @return	The crosswalk details page
     * @throws java.lang.Exception
     *
     * @Objects	(1) An object that will hold all the details of the clicked crosswalk
     *
     */
    @RequestMapping(value = "/newCrosswalk", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView newCrosswalk(@RequestParam(value = "orgId", required = false) Integer orgId) throws Exception {

        if (orgId == null) {
            orgId = 0;
        }

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/crosswalkDetails");

        Crosswalks crosswalkDetails = new Crosswalks();
        mav.addObject("crosswalkDetails", crosswalkDetails);
        mav.addObject("btnValue", "Create");
	mav.addObject("actionValue", "Create");
        mav.addObject("orgId", orgId);

        //Get the list of available file delimiters
        @SuppressWarnings("rawtypes")
        List delimiters = messagetypemanager.getDelimiters();
        mav.addObject("delimiters", delimiters);

        return mav;
    }

    /**
     * The '/createCrosswalk' function will be used to create a new crosswalk
     *
     * @param crosswalkDetails
     * @param result
     * @param redirectAttr
     * @param orgId
     * @return 
     * @throws java.lang.Exception
     * @Return The function will either return the crosswalk form on error or redirect to the data translation page.
     */
    @RequestMapping(value = "/createCrosswalk", method = RequestMethod.POST)
    public @ResponseBody
    int createCrosswalk(@ModelAttribute(value = "crosswalkDetails") Crosswalks crosswalkDetails, BindingResult result, RedirectAttributes redirectAttr, @RequestParam int orgId) throws Exception {
	
	crosswalkDetails.setOrgId(orgId);
        int lastId = messagetypemanager.createCrosswalk(crosswalkDetails);

	return lastId;
    }
    
     /**
     * The '/UploadNewFile' function will be used to upload a new file for an existing crosswalk.
     *
     * @param crosswalkDetails
     * @param result
     * @param redirectAttr
     * @param orgId
     * @return 
     * @throws java.lang.Exception 
     * @Return The function will either return the crosswalk form on error or redirect to the data translation page.
     */
    @RequestMapping(value = "/uploadnewfileCrosswalk", method = RequestMethod.POST)
    public @ResponseBody 
    int uploadnewfileCrosswalk(@ModelAttribute(value = "crosswalkDetails") Crosswalks crosswalkDetails, BindingResult result, RedirectAttributes redirectAttr, @RequestParam int orgId) throws Exception {

        crosswalkDetails.setLastUpdated(new Date());
	int lastId = messagetypemanager.uploadNewFileForCrosswalk(crosswalkDetails);
	
	return lastId;
    }

    /**
     *
     * @param name
     * @param orgId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/checkCrosswalkName.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Long checkCrosswalkName(@RequestParam(value = "name", required = true) String name, @RequestParam(value = "orgId", required = false) Integer orgId) throws Exception {

        if (orgId == null) {
            orgId = 0;
        }

        Long nameExists = messagetypemanager.checkCrosswalkName(name, orgId);

        return nameExists;
    }

    
    /**
     * The '/viewCrosswalk{params}' function will return the details of the selected crosswalk.The results will be displayed in the overlay.
     *
     * @param cwId
     * @return 
     * @throws java.lang.Exception
     * @Param	i	This will hold the id of the selected crosswalk
     *
     * @Return	This function will return the crosswalk details view.
     */
    @RequestMapping(value = "/viewCrosswalk{params}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView viewCrosswalk(@RequestParam(value = "i", required = true) Integer cwId) throws Exception {
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/crosswalkDetails");

        //Get the details of the selected crosswalk
        Crosswalks crosswalkDetails = messagetypemanager.getCrosswalk(cwId);
        mav.addObject("crosswalkDetails", crosswalkDetails);
	
	if(crosswalkDetails.getOrgId() > 0) {
	    Organization organizationDetails = organizationmanager.getOrganizationById(crosswalkDetails.getOrgId());
	    String cwURL = organizationDetails.getCleanURL() + "/crosswalks";
	    mav.addObject("cleanOrgURL",Base64.getEncoder().encodeToString(cwURL.getBytes()));
	}

        //Get the data associated with the selected crosswalk
        @SuppressWarnings("rawtypes")
        List crosswalkData = messagetypemanager.getCrosswalkData(cwId);
        mav.addObject("crosswalkData", crosswalkData);

        //Get the list of available file delimiters
        @SuppressWarnings("rawtypes")
        List delimiters = messagetypemanager.getDelimiters();
        mav.addObject("delimiters", delimiters);
	
	mav.addObject("btnValue", "Upload New File");
        mav.addObject("actionValue", "UploadNewFile");
	
	mav.addObject("orgId", crosswalkDetails.getOrgId());

        return mav;
    }
    
    
    /**
     * 
     * @param configurationId
     * @param transportDetailsId
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/reloadConfigurationFields", method = RequestMethod.GET)
    public @ResponseBody String reloadConfigurationFields(
	    @RequestParam(value = "configurationId", required = true) Integer configurationId,
	    @RequestParam(value = "transportDetailsId", required = true) Integer transportDetailsId) throws Exception {
	
	configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configurationId);
	
	utconfigurationTransportManager.populateFieldsFromHELConfiguration(configurationId, transportDetailsId,transportDetails.getHelRegistryConfigId(),transportDetails.getHelSchemaName(), true);
	
	return "1";
    }
    
    /**
     * The 'createDataTranslationDownload' GET request will return modal fro choosing a tier to create the crosswalk form.
     *
     * @param configId The id of the clicked configuration
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/createDataTranslationDownload", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView createDataTranslationDownload(
	    @RequestParam(value = "configId", required = false) Integer configId,HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/dtDownloadForm");
        mav.addObject("configId", configId);

        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	
	DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
	Date date = new Date();
	String fileName = new StringBuilder().append("configId").append("-").append(configId).append("-").append(configurationDetails.getconfigName().trim().replaceAll(" ","")).append("-").append("dt").append("-").append(dateFormat.format(date)).toString();
        
	mav.addObject("fileName", fileName.toLowerCase());

        return mav;
    }
    
    /**
     * 
     * @param configId
     * @param fileName
     * @param session
     * @param response
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/dataTranslationsDownload", method = RequestMethod.GET)
    @ResponseBody
    public String dataTranslationsDownload(
	@RequestParam(value = "configId", required = true) Integer configId, @RequestParam(value = "fileName", required = true) String fileName, HttpSession session,HttpServletResponse response) throws Exception {
	
	utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	
	if(configurationDetails != null) {
	   
	    File dtFile = new File ("/tmp/" + fileName.replaceAll("\\s+","") + ".csv");
	    
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(dtFile))) {
		
		List dataTranslations = utconfigurationmanager.getDataTranslationsForDownload(configId);
		
		if(!dataTranslations.isEmpty()) {
		    StringBuilder sb;
		
		    Iterator dtDataIt = dataTranslations.iterator();

		    sb = new StringBuilder();
		    sb.append("Config Name").append(",")
		    .append("Field #").append(",")
		    .append("Field Label").append(",")
		    .append("Macro Id").append(",")
		    .append("Macro Name").append(",")
		    .append("Crosswalk Id").append(",")
		    .append("Crosswalk Name").append(",")
		    .append("Pass/Clear").append(",")
		    .append("Field A").append(",")
		    .append("Field B").append(",")
		    .append("Constant 1").append(",")
		    .append("Constant 2").append(",")
		    .append("Process Order");

		    writer.write(sb.toString());
		    if(dtDataIt.hasNext()) {
			writer.write("\r\n");
		    }

		    while (dtDataIt.hasNext()) {
			sb = new StringBuilder();

			Object dtDatarow[] = (Object[]) dtDataIt.next();

			sb.append(dtDatarow[0]).append(",")
			.append(dtDatarow[12]).append(",")
			.append(dtDatarow[2]).append(",")
			.append(dtDatarow[3]).append(",")
			.append("\"").append(dtDatarow[4]).append("\"").append(",")
			.append(dtDatarow[5]).append(",")
			.append(dtDatarow[6]).append(",")
			.append(dtDatarow[7]).append(",")
			.append("\"").append(dtDatarow[8]).append("\"").append(",")
			.append("\"").append(dtDatarow[9]).append("\"").append(",")
			.append("\"").append(dtDatarow[10]).append("\"").append(",")
			.append("\"").append(dtDatarow[11]).append("\"").append(",")
			.append(dtDatarow[1]);

			writer.write(sb.toString());
			if(dtDataIt.hasNext()) {
			    writer.write("\r\n");
			}
		    }
		    
		     return fileName.replaceAll("\\s+","");
		}
		else {
		    return "";
		}
	    }
	    catch (Exception ex) {
		System.out.println(ex.getMessage());
		return "";
	    }
	}
	else {
	    return "";
	}
    } 
    
    /**
     * The 'createCrosswalkDownload' GET request will return modal for downloading the crosswalks.
     *
     * @param configId The id of the clicked configuration
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/createCrosswalkDownload", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView createCrosswalkDownload(@RequestParam(value = "configId", required = false) Integer configId,HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/cwDownloadForm");
        mav.addObject("configId", configId);

        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	
	Organization orgDetails = organizationmanager.getOrganizationById(configurationDetails.getorgId());
	
	String orgName = orgDetails.getOrgName().toLowerCase().trim().replaceAll(" ", "-");
	String configName = configurationDetails.getconfigName().toLowerCase().trim().replaceAll(" ", "-");
	
	DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
	Date date = new Date();
	String fileName = new StringBuilder().append(orgName).append("-").append(configName).append("-crosswalks-").append(dateFormat.format(date)).toString();
	//String fileName = new StringBuilder().append("configId").append("-").append(configId).append("-").append("cw").append("-").append(dateFormat.format(date)).toString();
        
	mav.addObject("fileName", fileName.toLowerCase());

        return mav;
    }
    
    /**
     * 
     * @param configId
     * @param fileName
     * @param session
     * @param response
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/crosswalksDownload", method = RequestMethod.GET)
    @ResponseBody
    public String crosswalksDownload(
	@RequestParam(value = "configId", required = true) Integer configId, @RequestParam(value = "fileName", required = true) String fileName, HttpSession session,HttpServletResponse response) throws Exception {
	
	utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	
	if(configurationDetails != null) {
	   
	    File dtFile = new File ("/tmp/" + fileName.replaceAll("\\s+","") + ".csv");
	    
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(dtFile))) {
		
		List crosswalks = utconfigurationmanager.getCrosswalksForDownload(configId, true);
		
		if(!crosswalks.isEmpty()) {
		    StringBuilder sb;
		
		    Iterator cwDataIt = crosswalks.iterator();

		    sb = new StringBuilder();
		    sb.append("Crosswalk Name").append(",")
		    .append("Crosswalk Id").append(",")
		    .append("Source Value").append(",")
		    .append("Target Value").append(",")
		    .append("Desc Value");

		    writer.write(sb.toString());
		    if(cwDataIt.hasNext()) {
			writer.write("\r\n");
		    }

		    while (cwDataIt.hasNext()) {
			sb = new StringBuilder();

			Object cwDatarow[] = (Object[]) cwDataIt.next();

			sb.append(cwDatarow[0]).append(",")
			.append(cwDatarow[1]).append(",")
			.append("\"").append(cwDatarow[2]).append("\"").append(",")
			.append("\"").append(cwDatarow[3]).append("\"").append(",")
			.append("\"").append(cwDatarow[4]).append("\"");

			writer.write(sb.toString());
			if(cwDataIt.hasNext()) {
			    writer.write("\r\n");
			}
		    }
		    
		    return fileName.replaceAll("\\s+","");
		}
		else {
		    return "";
		}
	    }
	    catch (Exception ex) {
		return "";
	    }
	}
	else {
	    return "";
	}
    } 
    
    @RequestMapping(value = "/downloadDTCWFile/{file}", method = RequestMethod.GET)
    public void downloadDTCWFile(@PathVariable("file") String file,HttpServletResponse response
    ) throws Exception {
	
	File dtFile = new File ("/tmp/" + file + ".csv");
	InputStream is = new FileInputStream(dtFile);

	response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".csv\"");
	FileCopyUtils.copy(is, response.getOutputStream());
	
	is.close();

	//Delete the file
	dtFile.delete();

	 // close stream and return to view
	response.flushBuffer();
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
    List<utConfiguration> getAvailableConfigurations(@RequestParam(value = "orgId", required = true) int orgId) throws Exception {

        List<utConfiguration> configurations = utconfigurationmanager.getActiveConfigurationsByOrgId(orgId);

        if (configurations != null) {
            for (utConfiguration configuration : configurations) {
                configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configuration.getId());

                configuration.setOrgName(organizationmanager.getOrganizationById(configuration.getorgId()).getOrgName());
		configuration.setMessageTypeName("N/A");
		
		if(transportDetails != null) {
		    configuration.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
		}
		else {
		    configuration.settransportMethod("N/A");
		}
                
            }
        }

        return configurations;
    }
    
    /**
     * The '/macroDefinitions' GET request will be used to return a the macro definition page.
     *
     * @param macroCategory
     * @return	The macro definition page
     * @throws java.lang.Exception
     *
     */
    @RequestMapping(value = "/macroDefinitions", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView macroDefinitions(@RequestParam(value = "macroCategory", required = true) Integer macroCategory) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/macroDefinitions");
	
        List<Macros> macros = utconfigurationmanager.getMacrosByCategory(macroCategory);
        mav.addObject("macros", macros);

        return mav;
    }
    
    /**
     * The 'appendConfigurationFields' GET request will return modal for appending new fields to the existing configuration field list.
     *
     * @param configId The id of the clicked configuration
     * @param configTransportId
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/appendConfigurationFields", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView appendConfigurationFields(
	    @RequestParam(value = "configId", required = true) Integer configId,
	    @RequestParam(value = "configTransportId", required = true) Integer configTransportId,HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/appendNewFields");
	
	configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
	
	List<configurationFormFields> currentFields = utconfigurationTransportManager.getConfigurationFields(configId, configTransportId);
	
	Integer nextFieldNo = 1;
	
	if(currentFields != null) {
	    if(!currentFields.isEmpty()) {
		configurationFormFields lastField = currentFields.get(currentFields.size()-1);
		nextFieldNo = lastField.getFieldNo()+1;
		
	    }
	}
	
	List<appenedNewconfigurationFormFields> newFields = new ArrayList<>();
	
	for(int i = 0; i<10;i++) {
	    appenedNewconfigurationFormFields newFormField = new appenedNewconfigurationFormFields();
	    newFormField.setFieldNo(nextFieldNo);
	    newFormField.setConfigId(configId);
	    newFormField.setTransportDetailId(configTransportId);
	    newFields.add(newFormField);
	    
	    nextFieldNo++;
	}
	
	transportDetails.setNewfields(newFields);
        
	mav.addObject("transportDetails", transportDetails);
	
	List validationTypes = messagetypemanager.getValidationTypes();
        mav.addObject("validationTypes", validationTypes);

        return mav;
    }
    
    /**
     *
     * @param transportDetails
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/saveNewConfigurationFields", method = RequestMethod.POST)
    public ModelAndView saveNewConfigurationFields(@ModelAttribute(value = "transportDetails") configurationTransport transportDetails) throws Exception {
	
	//Get the list of fields
        List<appenedNewconfigurationFormFields> fields = transportDetails.getNewfields();
	
	if(fields != null) {
	    if(!fields.isEmpty()) {
		for(appenedNewconfigurationFormFields field : fields) {
		    if(field.isUseField() && !"".equals(field.getFieldDesc())) {
			configurationFormFields newFormField = new configurationFormFields();
			newFormField.setAssociatedFieldId(0);
			newFormField.setconfigId(field.getConfigId());
			newFormField.settransportDetailId(field.getTransportDetailId());
			newFormField.setFieldNo(field.getFieldNo());
			newFormField.setFieldDesc(field.getFieldDesc());
			newFormField.setValidationType(field.getValidationType());
			newFormField.setRequired(field.isRequired());
			newFormField.setSampleData(field.getSampleData());
			newFormField.setUseField(true);
			
			utconfigurationTransportManager.saveConfigurationFormFields(newFormField);
		    }
		    
		}
	    }
	}

        ModelAndView mav = new ModelAndView(new RedirectView("mappings"));
        return mav;
    }
    
    /**
     * The 'createConfigPrintPDF.do' method will copy the selected utConfiguration.
     * @param configId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/createConfigPrintPDF.do", method = RequestMethod.GET)
    @ResponseBody
    public String printConfiguration(@RequestParam int configId) throws Exception {

        utConfiguration configDetails = utconfigurationmanager.getConfigurationById(configId);
	Organization orgDetails = organizationmanager.getOrganizationById(configDetails.getorgId());
	
	String configDetailFile = "/tmp/configDetails-" + configId + ".txt";
	String configPrintFile = "/tmp/" + configDetails.getconfigName().toLowerCase().replaceAll(" ", "-") + ".pdf";
	
	File detailsFile = new File(configDetailFile);
	detailsFile.delete();
	
	File printFile = new File(configPrintFile);
	printFile.delete();
	
	Document document = new Document(PageSize.A4);
	
	StringBuffer reportBody = new StringBuffer();
	
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(configDetailFile, true)));
	out.println("<html><body>");
	
	
	reportBody.append(utconfigurationmanager.printDetailsSection(configDetails,orgDetails,siteTimeZone));
	//reportBody.append(utconfigurationmanager.printConfigurationNotesSection(configDetails,siteTimeZone));
	reportBody.append(utconfigurationmanager.printTransportMethodSection(configDetails));
	reportBody.append(utconfigurationmanager.printMessageSpecsSection(configDetails));
	reportBody.append(utconfigurationmanager.printFieldSettingsSection(configDetails));
	reportBody.append(utconfigurationmanager.printDataTranslationsSection(configDetails,siteTimeZone));
	
	out.println(reportBody.toString());
	
	out.println("</body></html>");
	
	out.close();
	
	FileOutputStream os =  new FileOutputStream(configPrintFile);
	PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
			  
	document.open();
			    
	XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

	//replace with actual code to generate html info
	//we get image location here 
	FileInputStream fis = new FileInputStream(configDetailFile);
	worker.parseXHtml(pdfWriter, document, fis);

	fis.close();
	document.close();
	pdfWriter.close();
	os.close();
	
	File configDetailsFile = new File(configDetailFile);
	configDetailsFile.delete();

	return configDetails.getconfigName().toLowerCase().replaceAll(" ", "-");
    }

    @RequestMapping(value = "/printConfig/{file}", method = RequestMethod.GET)
    public void printConfig(@PathVariable("file") String file,HttpServletResponse response) throws Exception {
	
	File configPrintFile = new File ("/tmp/" + file + ".pdf");
	InputStream is = new FileInputStream(configPrintFile);

	response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".pdf\"");
	FileCopyUtils.copy(is, response.getOutputStream());
	
	is.close();

	//Delete the file
	configPrintFile.delete();

	 // close stream and return to view
	response.flushBuffer();
    } 
    
    /**
     * The 'configFileUpload' GET request will return modal for downloading the crosswalks.
     *
     * @param configId
     * @param fileDropLocation 
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/configFileUpload", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView configFileUpload(@RequestParam(value = "configId", required = true) Integer configId,
	    @RequestParam(value = "fileDropLocation", required = true) String fileDropLocation,HttpSession session) throws Exception {
	
	configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/configUploadFile");
        mav.addObject("fileDropLocation", fileDropLocation);
	mav.addObject("expectedExt", transportDetails.getfileExt());
        return mav;
    }
    
    /**
     * The '/submitConfigFileForProcessing' function will be used to upload a new file for an existing crosswalk.
     *
     * @param configFile
     * @param fileDropLocation
     * @return 
     * @throws java.lang.Exception 
     * @Return The function will either return the crosswalk form on error or redirect to the data translation page.
     */
    @RequestMapping(value = "/submitConfigFileForProcessing", method = RequestMethod.POST)
    public @ResponseBody 
    int submitConfigFileForProcessing(@RequestParam(value = "fileDropLocation", required = true) String fileDropLocation, 
	@RequestParam(value = "configFile", required = true)  MultipartFile configFile) throws Exception {

	Integer returnVal = 1;
	
	if(fileDropLocation != null) {
	    if(!"".equals(fileDropLocation)) {
		MultipartFile file = configFile;
		String fileName = file.getOriginalFilename();

		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
		    inputStream = file.getInputStream();
		    File newFile = null;
		    
		    newFile = new File(myProps.getProperty("ut.directory.utRootDir") + fileDropLocation + fileName);
		    newFile.createNewFile();

		    outputStream = new FileOutputStream(newFile);
		    int read = 0;
		    byte[] bytes = new byte[1024];

		    while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		    }
		    outputStream.close();
		    
		    //Call the method to start processing
		    transactioninmanager.moveFileDroppedFiles();
			    
		} catch (IOException e) {
		    returnVal = 0;
		    e.printStackTrace();
		}
	    }
	    else {
		returnVal = 0;
	    }
	}
	else {
	    returnVal = 0;
	}
	
	return returnVal;
    }
    
    /**
     * The 'createNewFieldSettingsTemplateVertical.do' method will create a new template file from fields that are saved (Vertical column format).
     * @param configId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/createNewFieldSettingsTemplateVertical.do", method = RequestMethod.GET)
    @ResponseBody
    public String createNewFieldSettingsTemplateVertical(@RequestParam int configId) throws Exception {
	
	String fileName = "";
	
	try {
	    utConfiguration configDetails = utconfigurationmanager.getConfigurationById(configId);
	    Organization orgDetails = organizationmanager.getOrganizationById(configDetails.getorgId());

	    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
	    Date date = new Date();

	    fileName = dateFormat.format(date) + "-" + configDetails.getconfigName().toLowerCase().replace(" ", "-");

	    File file = new File("/tmp/" + fileName + ".xlsx");
	    file.createNewFile();

	    FileWriter fw = null;

	    try {
		fw = new FileWriter(file, true);
	    } catch (IOException ex) {

	    }

	    configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configDetails.getId());

	    List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configDetails.getId(), transportDetails.getId());

	    List<validationType> validations = messagetypemanager.getValidationTypes1();

	    StringBuilder exportRow = new StringBuilder();

	    String required = "";
	    String usefield = "Y";
	    String validationValue = "None";

	    Workbook wb = new SXSSFWorkbook();
	    Sheet sheet = wb.createSheet("sheet1");

	    Integer rowNum = 0;
	    Integer cellNum = 0;

	    Row currentRow = sheet.createRow(rowNum);
	    currentRow.createCell(cellNum).setCellValue("Field Description");
	    cellNum++;
	    currentRow.createCell(cellNum).setCellValue("R/O/D");
	    cellNum++;
	    currentRow.createCell(cellNum).setCellValue("Sample Data");
	    cellNum++;
	    currentRow.createCell(cellNum).setCellValue("Use Field");
	    cellNum++;
	    currentRow.createCell(cellNum).setCellValue("Field Validation");

	    if(fields != null) {
		if(!fields.isEmpty()) {
		    for(configurationFormFields field : fields) {
			rowNum++;
			currentRow = sheet.createRow(rowNum);
			cellNum = 0;

			if(field.getDefaultValue() != null) {
			    if(!field.getDefaultValue().isEmpty()) {
				required = "D";
			    }
			}

			if(!required.equals("D")) {
			    if(field.getRequired()) {
				required = "R";
			    }
			    else {
				required = "O";
			    }
			}

			if(field.getUseField()) {
			    usefield = "Y";
			}
			else {
			    usefield = "N";
			}

			if(!validations.isEmpty() && field.getValidationType() > 0) {
			    for(validationType validation : validations) {
				if(validation.getId().equals(field.getValidationType())) {
				    validationValue = validation.getValidationType();
				}
			    }
			}

			currentRow.createCell(cellNum).setCellValue(field.getFieldDesc());
			cellNum++;
			currentRow.createCell(cellNum).setCellValue(required);
			cellNum++;
			currentRow.createCell(cellNum).setCellValue(field.getSampleData());
			cellNum++;
			currentRow.createCell(cellNum).setCellValue(usefield);
			cellNum++;
			currentRow.createCell(cellNum).setCellValue(validationValue);

		    }
		}
	    }

	    try (OutputStream stream = new FileOutputStream(file)) {
		wb.write(stream);
		stream.close();
		wb.close();
	    }
	}
	catch (Exception ex) {
	    //we notify admin
	    mailMessage mail = new mailMessage();
	    mail.settoEmailAddress(myProps.getProperty("admin.email"));
	    mail.setfromEmailAddress("support@health-e-link.net");
	    mail.setmessageSubject("Error creating template from config field settings - " + " " + myProps.getProperty("server.identity"));
	    StringBuilder emailBody = new StringBuilder();
	    emailBody.append("There was an error creating a template from the configuration field settings page.");
	    emailBody.append("<br/>Configuration Id: " + configId);
	    emailBody.append("<br/><br/>" + ex.getMessage());
	    emailBody.append("<br/><br/>" + ex.getStackTrace());
	    mail.setmessageBody(emailBody.toString());
	    emailMessageManager.sendEmail(mail);
	    fileName = "";
	}

	return fileName;
    }
    
    @RequestMapping(value = "/printNewFieldSettingsTemplate/{file}", method = RequestMethod.GET)
    public void printNewFieldSettingsTemplate(@PathVariable("file") String file,HttpServletResponse response
    ) throws Exception {
	
	File templatePrintFile = new File ("/tmp/" + file + ".xlsx");
	InputStream is = new FileInputStream(templatePrintFile);

	response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".xlsx\"");
	FileCopyUtils.copy(is, response.getOutputStream());
	
	is.close();

	//Delete the file
	templatePrintFile.delete();

	 // close stream and return to view
	response.flushBuffer();
    } 
    
    /**
     * The 'createNewFieldSettingsTemplate.do' method will create a new template file from fields that are saved. (Horizontal Row format)
     * @param configId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/createNewFieldSettingsTemplate.do", method = RequestMethod.GET)
    @ResponseBody
    public String createNewFieldSettingsTemplate(@RequestParam int configId) throws Exception {
	
	String fileName = "";
	
	try {
	    utConfiguration configDetails = utconfigurationmanager.getConfigurationById(configId);
	    Organization orgDetails = organizationmanager.getOrganizationById(configDetails.getorgId());

	    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
	    Date date = new Date();

	    fileName = dateFormat.format(date) + "-" + configDetails.getconfigName().replace(" ", "-");

	    File file = new File("/tmp/" + fileName + ".xlsx");
	    file.createNewFile();

	    FileWriter fw = null;

	    try {
		fw = new FileWriter(file, true);
	    } catch (IOException ex) {

	    }

	    configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configDetails.getId());

	    List<configurationFormFields> fields = utconfigurationTransportManager.getConfigurationFields(configDetails.getId(), transportDetails.getId());

	    List<validationType> validations = messagetypemanager.getValidationTypes1();

	    StringBuilder exportRow = new StringBuilder();

	    String required = "";
	    String usefield = "Y";
	    String validationValue = "None";

	    Workbook wb = new SXSSFWorkbook();
	    Sheet sheet = wb.createSheet("sheet1");

	    Integer rowNum = 0;
	    Integer cellNum = 0;

	    Row currentRow = sheet.createRow(rowNum);
	    
	    if(fields != null) {
		if(!fields.isEmpty()) {
		    for(configurationFormFields field : fields) {
			currentRow.createCell(cellNum).setCellValue(field.getFieldDesc());
			cellNum++;
		    }
		    rowNum++;
		    currentRow = sheet.createRow(rowNum);
		    cellNum = 0;
		    for(configurationFormFields field : fields) {
			if(!field.getDefaultValue().equals("")) {
			    currentRow.createCell(cellNum).setCellValue(field.getDefaultValue());
			}
			else {
			    currentRow.createCell(cellNum).setCellValue(field.getSampleData());
			}
			cellNum++;
		    }
		    rowNum++;
		    currentRow = sheet.createRow(rowNum);
		    cellNum = 0;
		    for(configurationFormFields field : fields) {
			if(field.getDefaultValue() != null) {
			    if(!field.getDefaultValue().isEmpty()) {
				required = "D";
			    }
			}

			if(!required.equals("D")) {
			    if(field.getRequired()) {
				required = "R";
			    }
			    else {
				required = "O";
			    }
			}
			
			currentRow.createCell(cellNum).setCellValue(required);
			cellNum++;
		    }
		    rowNum++;
		    currentRow = sheet.createRow(rowNum);
		    cellNum = 0;
		    for(configurationFormFields field : fields) {
			if(field.getUseField()) {
			    usefield = "Y";
			}
			else {
			    usefield = "N";
			}
			
			currentRow.createCell(cellNum).setCellValue(usefield);
			cellNum++;
		    }
		    rowNum++;
		    currentRow = sheet.createRow(rowNum);
		    cellNum = 0;
		    for(configurationFormFields field : fields) {
			validationValue = "X";
			if(!validations.isEmpty() && field.getValidationType() > 0) {
			    for(validationType validation : validations) {
				if(validation.getId().equals(field.getValidationType())) {
				    validationValue = validation.getValidationType();
				}
			    }
			}
			
			if("Date (yyyy-mm-dd)".equals(validationValue)) {
			    validationValue = "D";
			}
			else if("None".equals(validationValue)) {
			    validationValue = "X";
			}
			else if("Email".equals(validationValue)) {
			    validationValue = "E";
			}
			else if("Phone Number".equals(validationValue)) {
			    validationValue = "P";
			}
			else if("Numeric".equals(validationValue)) {
			    validationValue = "N";
			}
			else if("URL".equals(validationValue)) {
			    validationValue = "U";
			}
			
			currentRow.createCell(cellNum).setCellValue(validationValue);
			cellNum++;
		    }
		}
	    }

	    try (OutputStream stream = new FileOutputStream(file)) {
		wb.write(stream);
		stream.close();
		wb.close();
	    }
	}
	catch (Exception ex) {
	    //we notify admin
	    mailMessage mail = new mailMessage();
	    mail.settoEmailAddress(myProps.getProperty("admin.email"));
	    mail.setfromEmailAddress("support@health-e-link.net");
	    mail.setmessageSubject("Error creating template from config field settings - " + " " + myProps.getProperty("server.identity"));
	    StringBuilder emailBody = new StringBuilder();
	    emailBody.append("There was an error creating a template from the configuration field settings page.");
	    emailBody.append("<br/>Configuration Id: " + configId);
	    emailBody.append("<br/><br/>" + ex.getMessage());
	    emailBody.append("<br/><br/>" + ex.getStackTrace());
	    mail.setmessageBody(emailBody.toString());
	    emailMessageManager.sendEmail(mail);
	    fileName = "";
	}

	return fileName;
    }
    
    /**
     *
     * @param cwId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/deleteCrosswalk.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer deleteCrosswalk(@RequestParam(value = "cwId", required = true) Integer cwId) throws Exception {

	 messagetypemanager.deleteCrosswalk(cwId);
	 return 1;
    }
    
    /**
     * The '/notes' GET request will display all the notes for a configuration
     *
     * @return	The utConfiguration note list
     *
     * @Objects	(1) An object containing all the found configuration notes
     *
     * @throws Exception
     */
    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    public ModelAndView configurationNotes(HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
	
	Integer configId = 0;
	
	if(session.getAttribute("manageconfigId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    configId = (Integer) session.getAttribute("manageconfigId");
	}
	
        mav.setViewName("/administrator/configurations/notes");

        //Get the utConfiguration details for the selected config
        utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	mav.addObject("messageTypeId", configurationDetails.getMessageTypeId());
	
        // Get organization directory name
        Organization orgDetails = organizationmanager.getOrganizationById(configurationDetails.getorgId());
	
	configurationDetails.setOrgName(orgDetails.getOrgName());
	
	//Get the transport details by configid and selected transport method
        configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
	configurationDetails.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));

	
	mav.addObject("configurationDetails", configurationDetails);
	mav.addObject("id", configId);
	
	//Get a list of configuration notes
	List<configurationUpdateLogs> configurationNotes = utconfigurationmanager.getConfigurationUpdateLogs(configId);
	
	TimeZone timeZone = TimeZone.getTimeZone(siteTimeZone);
	DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	requiredFormat.setTimeZone(timeZone);
	String dateinTZ = "";
	
	if(!configurationNotes.isEmpty()) {
	    for(configurationUpdateLogs note : configurationNotes) {
		dateinTZ = requiredFormat.format(note.getDateCreated());
		note.setDateCreated(dft.parse(dateinTZ));
	    }
	}
	
	mav.addObject("configurationNotes", configurationNotes);
	
	//Get latest configuration note
	List<configurationUpdateLogs> configNotes = utconfigurationmanager.getConfigurationUpdateLogs(configId);
	if(!configNotes.isEmpty()) {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configNotes.get(0).getDateCreated())));
	}
	else {
	    mav.addObject("lastConfigUpdate", dft.parse(requiredFormat.format(configurationDetails.getDateCreated())));
	}

        return mav;
    }
    
    /**
     * The '/newConfigurationNote' function will return the configuration note form.
     *
     * @param configId
     * @param authentication
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/newConfigurationNote", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView newConfigurationNote(Authentication authentication, @RequestParam(value = "configId", required = true) Integer configId) throws Exception {
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/notes/noteDetails");
	
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	
        //Get the details of the selected configuration note
        configurationUpdateLogs configurationNote = new configurationUpdateLogs();
	configurationNote.setConfigId(configId);
	configurationNote.setUserId(userDetails.getId());
	
        mav.addObject("configurationNote", configurationNote);
	
        return mav;
    }
    
    /**
     * The '/editConfigurationNote' function will return the details of the selected configuration note.
     *
     * @param noteId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/editConfigurationNote", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView editConfigurationNote(@RequestParam(value = "noteId", required = true) Integer noteId) throws Exception {
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/notes/noteDetails");
	
        //Get the details of the selected configuration note
        configurationUpdateLogs configurationNote = utconfigurationmanager.getConfigurationUpdateLog(noteId);
        mav.addObject("configurationNote", configurationNote);
	
        return mav;
    }
    
    /**
     *
     * @param noteId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/deleteConfigurationNote.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer deleteConfigurationNote(@RequestParam(value = "noteId", required = true) Integer noteId) throws Exception {
	 utconfigurationmanager.deletConfigurationNote(noteId);
	 return 1;
    }
    
    /**
     * The '/newConfigurationNote' POST request will submit the new configuration note.
     *
     * @param session
     * @param configurationNote	
     * @param redirectAttr	
     * @throws Exception
     */
    @RequestMapping(value = "/newConfigurationNote", method = RequestMethod.POST)
    public ModelAndView saveNewConfigurationNote(HttpSession session,@ModelAttribute(value = "configurationNote") configurationUpdateLogs configurationNote, RedirectAttributes redirectAttr) throws Exception {

	utconfigurationmanager.saveConfigurationUpdateLog(configurationNote);
	
	redirectAttr.addFlashAttribute("savedStatus", "created");
	ModelAndView mav = new ModelAndView(new RedirectView("notes"));
	return mav;
    }
    
    /**
     * The '/editConfigurationNote' POST request will submit the configuration note.
     *
     * @param session
     * @param configurationNote	
     * @param redirectAttr	
     * @throws Exception
     */
    @RequestMapping(value = "/editConfigurationNote", method = RequestMethod.POST)
    public ModelAndView editConfigurationNote(HttpSession session,@ModelAttribute(value = "configurationNote") configurationUpdateLogs configurationNote, RedirectAttributes redirectAttr) throws Exception {

	utconfigurationmanager.updateConfigurationUpdateLog(configurationNote);
	
	redirectAttr.addFlashAttribute("savedStatus", "updated");
	ModelAndView mav = new ModelAndView(new RedirectView("notes"));
	return mav;
    }
    
    /**
     * 
     * @param configId
     * @param fileName
     * @param fileType
     * @param inUseOnly
     * @param session
     * @param response
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/crosswalksExcelFileDownload", method = RequestMethod.GET)
    @ResponseBody
    public String crosswalksExcelFileDownload(@RequestParam(value = "configId", required = true) Integer configId, 
	@RequestParam(value = "fileName", required = true) String fileName, 
	@RequestParam(value = "fileType", required = true) Integer fileType, 
	@RequestParam(value = "inUseOnly", required = false) boolean inUseOnly, 
	HttpSession session,HttpServletResponse response) throws Exception {
	
	utConfiguration configurationDetails = utconfigurationmanager.getConfigurationById(configId);
	
	if(configurationDetails != null) {
	    
	    String fileExt = "xlsx";
	    
	    if(fileType != null) {
		if(fileType == 2) {
		    fileExt = "txt";
		}
	    }
	   
	    File dtFile = new File ("/tmp/" + fileName.replaceAll("\\s+","") + "." + fileExt);
	    
	    List crosswalks = utconfigurationmanager.getCrosswalksForDownload(configId,inUseOnly);
	    
	    if(fileExt.equals("txt")) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(dtFile))) {
		    if(!crosswalks.isEmpty()) {
			StringBuilder sb;

			Iterator cwDataIt = crosswalks.iterator();

			String cwName = "";

			while (cwDataIt.hasNext()) {
			    sb = new StringBuilder();

			    Object cwDatarow[] = (Object[]) cwDataIt.next();

			    if("".equals(cwName) || !cwName.equals(cwDatarow[0].toString())) {
				
				if(!"".equals(cwName)) {
				    writer.write("\r\n");
				    writer.write("\r\n");
				}
				
				sb.append("Crosswalk Name: ").append(cwDatarow[0].toString());
				writer.write(sb.toString());
				writer.write("\r\n");
				
				sb = new StringBuilder();
				sb.append("Crosswalk id: ").append(cwDatarow[1].toString());
				writer.write(sb.toString());
				writer.write("\r\n");
				writer.write("\r\n");
				
				sb = new StringBuilder();
				sb.append("Source Value").append(",").append("Target Value").append(",").append("Desc Value");
				writer.write(sb.toString());
				cwName = cwDatarow[0].toString();
			    }
			    
			    sb = new StringBuilder();
			    sb.append(cwDatarow[2].toString().trim()).append("|")
			    .append(cwDatarow[3].toString().trim()).append("|")
			    .append(StringEscapeUtils.unescapeCsv(cwDatarow[4].toString().trim()));
			    writer.write("\r\n");

			    writer.write(sb.toString());
			}

			return fileName.replaceAll("\\s+","");
		    }
		    else {
			return "";
		    }
		}
		catch (Exception ex) {
		    return "";
		}
	    }
	    else {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(dtFile))) {
		    
		    if(!crosswalks.isEmpty()) {

			XSSFWorkbook workbook;
			XSSFFont boldFont;
			CellStyle boldCenterFont;
			CellStyle normalLeftFont;
			CellStyle boldLeftFont;
			CellStyle normalCenterFont;
			CellStyle dateStyle;
			XSSFCreationHelper createHelper;
			Map <String, CellStyle> cellStyleMap;
			XSSFSheet sheet = null;

			workbook = new XSSFWorkbook();

			boldFont = workbook.createFont();
			boldFont.setBold(true);
			XSSFFont normalFont = workbook.createFont();
			normalFont.setBold(false);

			boldCenterFont = workbook.createCellStyle();
			boldCenterFont.setFont(boldFont);
			boldCenterFont.setAlignment(HorizontalAlignment.CENTER);
			boldCenterFont.setWrapText(true);

			normalLeftFont = workbook.createCellStyle();
			normalLeftFont.setFont(normalFont);
			normalLeftFont.setAlignment(HorizontalAlignment.LEFT);
			normalLeftFont.setWrapText(true);

			boldLeftFont = workbook.createCellStyle();
			boldLeftFont.setFont(boldFont);
			boldLeftFont.setAlignment(HorizontalAlignment.LEFT);
			boldLeftFont.setWrapText(true);

			normalCenterFont = workbook.createCellStyle();
			normalCenterFont.setFont(normalFont);
			normalCenterFont.setAlignment(HorizontalAlignment.CENTER);
			normalCenterFont.setWrapText(true);

			dateStyle = workbook.createCellStyle();
			createHelper = workbook.getCreationHelper();
			dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy"));

			cellStyleMap = new HashMap<String, CellStyle>();
			cellStyleMap.put("boldCenterFont", boldCenterFont);
			cellStyleMap.put("normalLeftFont", normalLeftFont);
			cellStyleMap.put("boldLeftFont", boldLeftFont);
			cellStyleMap.put("normalCenterFont", normalCenterFont);
			cellStyleMap.put("dateStyle", dateStyle);

			CellStyle rowHeaderstyle = workbook.createCellStyle();
			rowHeaderstyle.setFont(boldFont);
			rowHeaderstyle.setAlignment(HorizontalAlignment.LEFT);
			rowHeaderstyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			rowHeaderstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Iterator cwDataIt = crosswalks.iterator();
			String crosswalkName = "";
			String sheetName = "Sheet One";

			XSSFRow row;
			XSSFCell cell;

			Integer rowCount = 0;
			Integer cellCount = 0;

			while (cwDataIt.hasNext()) {

			    Object cwDatarow[] = (Object[]) cwDataIt.next();

			    if("".equals(crosswalkName.trim()) || !cwDatarow[0].toString().equals(crosswalkName.trim())) {
				rowCount = 0;
				cellCount = 0;
				sheetName = cwDatarow[0].toString();
				crosswalkName = cwDatarow[0].toString();
				sheet = workbook.createSheet(sheetName);
				row = sheet.createRow(rowCount);
				cell = row.createCell(cellCount);
				cell.setCellValue("Crosswalk Name: " + crosswalkName);
				cell.setCellStyle(cellStyleMap.get("boldLeftFont"));
				sheet.autoSizeColumn(cellCount);

				rowCount++;
				row = sheet.createRow(rowCount);
				cell = row.createCell(cellCount);
				cell.setCellValue("Crosswalk Id: " + cwDatarow[1].toString());
				cell.setCellStyle(cellStyleMap.get("boldLeftFont"));
				sheet.autoSizeColumn(cellCount);

				rowCount++;
				rowCount++;
				row = sheet.createRow(rowCount);
				cell = row.createCell(cellCount);
				cell.setCellValue("Source Value");
				cell.setCellStyle(cellStyleMap.get("boldLeftFont"));
				sheet.autoSizeColumn(cellCount);
				cellCount++;
				cell = row.createCell(cellCount);
				cell.setCellValue("Target Value");
				cell.setCellStyle(cellStyleMap.get("boldLeftFont"));
				sheet.autoSizeColumn(cellCount);
				cellCount++;
				cell = row.createCell(cellCount);
				cell.setCellValue("Desc Value");
				cell.setCellStyle(cellStyleMap.get("boldLeftFont"));
				sheet.autoSizeColumn(cellCount);
			    }

			    rowCount++;
			    cellCount = 0;
			    row = sheet.createRow(rowCount);
			    cell = row.createCell(cellCount);
			    cell.setCellValue(cwDatarow[2].toString());
			    cell.setCellStyle(cellStyleMap.get("normalLeftFont"));
			    sheet.autoSizeColumn(cellCount);
			    cellCount++;
			    cell = row.createCell(cellCount);
			    cell.setCellValue(cwDatarow[3].toString());
			    cell.setCellStyle(cellStyleMap.get("normalLeftFont"));
			    sheet.autoSizeColumn(cellCount);
			    cellCount++;
			    cell = row.createCell(cellCount);
			    cell.setCellValue(cwDatarow[4].toString());
			    cell.setCellStyle(cellStyleMap.get("normalLeftFont"));
			    sheet.autoSizeColumn(cellCount);

			}

			FileOutputStream fileOut = new FileOutputStream("/tmp/" + fileName + ".xlsx");
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();

			return fileName.replaceAll("\\s+","");
		    }
		    else {
			return "";
		    }
		}
		catch (Exception ex) {
		    return "";
		}
	    }
	}
	else {
	    return "";
	}
    } 
    
    @RequestMapping(value = "/downloadDTCWExcelFile/{file}", method = RequestMethod.GET)
    public void downloadDTCWExcelFile(@PathVariable("file") String file,HttpServletResponse response) throws Exception {
	
	File cwFile = new File ("/tmp/" + file + ".xlsx");
	
	String fileExt = ".xlsx";
	
	if(!cwFile.exists()) {
	    fileExt = ".txt";
	    cwFile = new File ("/tmp/" + file + ".txt");
	}
	
	InputStream is = new FileInputStream(cwFile);

	response.setHeader("Content-Disposition", "attachment; filename=\"" + file + fileExt + "\"");
	FileCopyUtils.copy(is, response.getOutputStream());
	
	is.close();

	//Delete the file
	cwFile.delete();

	 // close stream and return to view
	response.flushBuffer();
    } 
    
    /**
     * The 'deleteConfigurationFTPInformation.do' method will remove the FTP information for the passed in configuration.
     * @param transportId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/deleteConfigurationFTPInformation.do", method = RequestMethod.POST)
    public @ResponseBody
    Integer deleteConfigurationFTPInformation(@RequestParam int transportId) throws Exception {

        utconfigurationmanager.deleteConfigurationFTPInformation(transportId);
        return 1;
    }
    
    /**
     * The 'createConfigExportFile.do' method will create the export file for the selected utConfiguration.
     * @param session
     * @param configId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/createConfigExportFile.do", method = RequestMethod.GET)
    @ResponseBody
    public String createConfigExportFile(HttpSession session, @RequestParam int configId) throws Exception {

        utConfiguration configDetails = utconfigurationmanager.getConfigurationById(configId);
	Organization orgDetails = organizationmanager.getOrganizationById(configDetails.getorgId());
	
	//Get transport details
	configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configId);
	
	String configType = "srcConfigExport";
	
	if(configDetails.getType() == 2) {
	    configType = "tgtConfigExport";
	}
	
	String configDetailFile = "/tmp/"+configType+"-"+configDetails.getconfigName().toLowerCase().replaceAll(" ", "-") + ".txt";
	
	File detailsFile = new File(configDetailFile);
	detailsFile.delete();
	
	StringBuffer reportBody = new StringBuffer();
	
	//Create a session to hold the email message body.
	StringBuffer emailBodySB = new StringBuffer();
	if(configDetails.getType() == 1) {
	    emailBodySB.append("The source configuration has been successfully exported.<br /><br />");
	}
	else {
	    emailBodySB.append("The target configuration has been successfully exported.<br /><br />");
	}
	emailBodySB.append("Configuration Name: ").append(configDetails.getconfigName().trim());
	emailBodySB.append("<br />Configuration Type: ");
	if(configDetails.getType() == 1) {
	    emailBodySB.append("Source Configuration");
	}
	else {
	    emailBodySB.append("Target Configuration");
	}
	emailBodySB.append("<br /><br />Please complete the following:<br />");
	
	if(configDetails.getType() == 1) {
	    emailBodySB.append("<br />Export the associated target configuration (if not already on the system)<br />");
	}
	
	session.setAttribute("emailBody", emailBodySB);
	
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(configDetailFile, true)));
	
	reportBody.append(utconfigurationmanager.exportConfigOrgSection(orgDetails));
	reportBody.append(System.getProperty("line.separator"));
	reportBody.append(utconfigurationmanager.exportConfigDetailsSection(configDetails));
	reportBody.append(System.getProperty("line.separator"));
	reportBody.append(utconfigurationmanager.exportConfigTransportSection(session,transportDetails));
	reportBody.append(System.getProperty("line.separator"));
	reportBody.append(utconfigurationmanager.exportConfigMessageSpecSection(session,configDetails.getId()));
	reportBody.append(System.getProperty("line.separator"));
	reportBody.append(utconfigurationmanager.exportConfigFieldsSection(configDetails.getId(),transportDetails.getId()));
	reportBody.append(System.getProperty("line.separator"));
	reportBody.append(utconfigurationmanager.exportConfigSchedulingSection(configDetails.getId()));
	reportBody.append(System.getProperty("line.separator"));
	reportBody.append(utconfigurationmanager.exportOrgCrosswalks(orgDetails.getId()));
	
	out.println(reportBody.toString());
	
	out.close();
	
	//Check to see if an email needs to be sent
	emailBodySB = (StringBuffer) session.getAttribute("emailBody");
	
	if(!"".equals(emailBodySB.toString())) {
	    mailMessage mail = new mailMessage();
	    mail.settoEmailAddress(myProps.getProperty("admin.email"));
	    mail.setfromEmailAddress("support@health-e-link.net");
	    mail.setmessageSubject("UT Configuration Import Script has been Created");
	    mail.setmessageBody(emailBodySB.toString());
	    emailMessageManager.sendEmail(mail);
	    
	    //Delete email body
	    session.removeAttribute("emailBody");
	}
	
	return configType+"-"+configDetails.getconfigName().toLowerCase().replaceAll(" ", "-");
    }

    @RequestMapping(value = "/printConfigExport/{file}", method = RequestMethod.GET)
    public void printConfigExport(@PathVariable("file") String file,HttpServletResponse response) throws Exception {
	
	File configExportFile = new File ("/tmp/" + file + ".txt");
	InputStream is = new FileInputStream(configExportFile);

	response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".txt\"");
	FileCopyUtils.copy(is, response.getOutputStream());
	
	is.close();

	//Delete the file
	configExportFile.delete();

	 // close stream and return to view
	response.flushBuffer();
    } 
    
    /**
     * The 'configImportUpload' GET request will return modal for uploading a configuration import script.
     *
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/configImportUpload", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView configImportUpload(HttpSession session) throws Exception {
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/configImportFile");
	mav.addObject("expectedExt", "txt");
        return mav;
    }
    
    /**
     * The '/submitConfigurationImportFile' function will be used to upload a new configuration import script.
     *
     * @param importConfigFile
     * @param session
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/submitConfigurationImportFile", method = RequestMethod.POST)
    public @ResponseBody 
    int submitConfigurationImportFile(@RequestParam(value = "importConfigFile", required = true)  MultipartFile importConfigFile, HttpSession session) throws Exception {

	Integer returnVal = 1;
	
	String fileName = importConfigFile.getOriginalFilename();
	
	try {
	    
	    Path filepath = Paths.get("/tmp/", importConfigFile.getOriginalFilename());

	    try (OutputStream os = Files.newOutputStream(filepath)) {
		os.write(importConfigFile.getBytes());
	    }
	   
	    BufferedReader br = null;
	    String strLine = "";
	    try {
	    
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream("/tmp/"+fileName), "UTF-8"));
		while (((strLine = reader.readLine()) != null) && reader.getLineNumber() <= 1){
		    if(!strLine.contains("[orgDetails")) {
			returnVal = 2;
		    }
		}
	        reader.close();
	    } catch (FileNotFoundException e) {
		returnVal = 0;
	    } catch (IOException e) {
		 returnVal = 0;
	    }
	    
	} catch (IOException e) {
	    returnVal = 0;
	}
	
	if(returnVal == 1) {
	    executor.execute(new Runnable() {
		@Override
		public void run() {
		    try {
			loadConfigurationImportFile(fileName,session);
		    } catch (Exception ex) {
			Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
		    }
		}
	    });
	}
	return returnVal;
    }
    
    /**
     * 
     * @param fileName
     * @param session
     * @throws Exception 
     */
    public void loadConfigurationImportFile(String fileName, HttpSession session) throws Exception {
	
	BufferedReader br = null;
	String strLine = "";
	 
	try {
	    
	    LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream("/tmp/"+fileName), "UTF-8"));
	    String[] strArrayValues;
	    Integer orgId = 0;
	    Integer configId = 0;
	    Integer configTransportId = 0;
	    String configName = "";
	    Integer fieldId = 0;
	    String cwIds = "";
	    boolean configImportSuccessful = true;
	    
	    while (((strLine = reader.readLine()) != null)){
		strLine = strLine.replace("[", "").replace("]", "");
		strArrayValues = strLine.split("\\|", -1);
		
		if(strArrayValues[0].equals("orgDetails")) {
		    orgId = processImportOrgDetails(strArrayValues);
		}
		else if(strArrayValues[0].equals("configDetails")) {
		    configName = strArrayValues[6];
		    try {
			configId = processImportConfigDetails(strArrayValues,orgId);
		    }
		    catch (Exception ex) {
			processImportConfigError("Adding Config Details",configId,0,orgId,configName,ex);
			configImportSuccessful = false;
			break;
		    }
		    
		    if(configId == 0) {
			break;
		    }
		}
		else if(strArrayValues[0].equals("transportDetails")) {
		    try {
			configTransportId = processImportConfigTransportDetails(strArrayValues,configId);
		    }
		    catch (Exception ex) {
			processImportConfigError("Adding Transport Details",configId,0,orgId,configName,ex);
			configImportSuccessful = false;
			break;
		    }
		}
		else if(strArrayValues[0].equals("fileDropDetails") && strArrayValues.length > 1) {
		    try {
			processImportConfigFileDropDetails(strArrayValues,configTransportId);
		    }
		    catch (Exception ex) {
			processImportConfigError("Adding File Drop Details",configId,configTransportId,orgId,configName,ex);
			configImportSuccessful = false;
			break;
		    }
		}
		else if(strArrayValues[0].equals("ftpDropDetails") && strArrayValues.length > 1) {
		    try {
			processImportConfigFTPDetails(strArrayValues,configTransportId);
		    }
		    catch (Exception ex) {
			processImportConfigError("Adding FTP Details",configId,configTransportId,orgId,configName,ex);
			configImportSuccessful = false;
			break;
		    }
		}
		else if(strArrayValues[0].equals("messageSpecDetails")) {
		    try {
			processImportConfigMessageSpecs(strArrayValues,configId);
		    }
		    catch (Exception ex) {
			processImportConfigError("Adding Messpace Specs",configId,configTransportId,orgId,configName,ex);
			configImportSuccessful = false;
			break;
		    }
		}
		else if(strArrayValues[0].equals("fields")) {
		    try {
			fieldId = processImportConfigFields(strArrayValues,configId,configTransportId);
		    }
		    catch (Exception ex) {
			processImportConfigError("Adding Fields",configId,configTransportId,orgId,configName,ex);
			configImportSuccessful = false;
			break;
		    }
		}
		else if(strArrayValues[0].equals("fieldDTS")) {
		    try {
			processImportConfigFieldDTS(strArrayValues,configId,fieldId);
		    }
		    catch (Exception ex) {
			processImportConfigError("Adding Data Translations",configId,configTransportId,orgId,configName,ex);
			configImportSuccessful = false;
			break;
		    }
		}
		else if(strArrayValues[0].equals("scheduleDetails")) {
		    try {
			processImportConfigSchedule(strArrayValues,configId);
		    }
		    catch (Exception ex) {
			processImportConfigError("Adding Schedule",configId,configTransportId,orgId,configName,ex);
			configImportSuccessful = false;
			break;
		    }
		}
		else if(strArrayValues[0].equals("crosswalks")) {
		    try {
			cwIds = processImportConfigCrosswalks(strArrayValues,configId, orgId, cwIds);
		    }
		    catch (Exception ex) {
			processImportConfigError("Adding Croswalks",configId,configTransportId,orgId,configName,ex);
			configImportSuccessful = false;
			break;
		    }
		}
	    }
	    reader.close();
	    
	    StringBuilder emailBody = new StringBuilder();
	    
	    Organization orgDetails = organizationmanager.getOrganizationById(orgId);
	    
	    if(configId == 0) {
		
		String emailSubject = "The Configuration Import was Not Successful";
		
		emailBody.append("The following configuration import has failed.");
		emailBody.append("<br /><br />");
		emailBody.append("Organization Name: ").append(orgDetails.getOrgName());
		emailBody.append("<br />Configuration Name: ").append(configName);
		emailBody.append("<br/><br />Reason<br />The configuration was already found on the system.");
		
		mailMessage mail = new mailMessage();
		mail.settoEmailAddress(myProps.getProperty("admin.email"));
		mail.setfromEmailAddress("support@health-e-link.net");
		mail.setmessageSubject(emailSubject);
		mail.setmessageBody(emailBody.toString());
		emailMessageManager.sendEmail(mail);
	    }
	    else if(configImportSuccessful) {
		
		utConfiguration configDetails = utconfigurationmanager.getConfigurationById(configId);
		
		String configType = "Source";
		
		if(configDetails.getType() == 2) {
		    configType = "Target";
		}
		
		String emailSubject = "The " + configType + " Configuration Import has been Completed";
		
		emailBody.append("The following ").append(configType.toLowerCase()).append(" configuration import has been completed and is now available.");
		emailBody.append("<br /><br />");
		emailBody.append("Organization Name: ").append(orgDetails.getOrgName());
		emailBody.append("<br />Configuration Type: ").append(configType).append(" Configuration");
		emailBody.append("<br />Configuration Name: ").append(configName);
		emailBody.append("<br />Configuration Id: ").append(configId);
		
		//Turn the configuration on
		configDetails.setStatus(true);
		utconfigurationmanager.updateConfiguration(configDetails);
		
		mailMessage mail = new mailMessage();
		mail.settoEmailAddress(myProps.getProperty("admin.email"));
		mail.setfromEmailAddress("support@health-e-link.net");
		mail.setmessageSubject(emailSubject);
		mail.setmessageBody(emailBody.toString());
		emailMessageManager.sendEmail(mail);
	    }
	    
	} catch (FileNotFoundException e) {
	    Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, e);
	} catch (IOException e) {
	     Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, e);
	}
    }
    
    /**
     * The 'processImportOrgDetails' method will process the organization information from the configuration import script.
     * @param strArrayValues 
     * 
     * @return The method will return the new organization id
     */
    public Integer processImportOrgDetails(String[] strArrayValues) {
	
	Integer orgId = 0;
	
	String orgName = strArrayValues[1];
	String orgCleanURL = strArrayValues[10];
	
	List<Organization> orgs = organizationmanager.getOrganizationByName(orgCleanURL);
	
	if(orgs.isEmpty()) {
	    Organization newOrg = new Organization();
	    newOrg.setOrgName(orgName);
	    if(!"null".equals(strArrayValues[2])) {
		newOrg.setAddress(strArrayValues[2]);
	    }
	    if(!"null".equals(strArrayValues[3])) {
		newOrg.setAddress2(strArrayValues[3]);
	    }
	    if(!"null".equals(strArrayValues[4])) {
		newOrg.setCity(strArrayValues[4]);
	    }
	    if(!"null".equals(strArrayValues[5])) {
		newOrg.setState(strArrayValues[5]);
	    }
	    if(!"null".equals(strArrayValues[6])) {
		newOrg.setPostalCode(strArrayValues[6]);
	    }
	    if(!"null".equals(strArrayValues[7])) {
		newOrg.setPhone(strArrayValues[7]);
	    }
	    if(!"null".equals(strArrayValues[8])) {
		newOrg.setFax(strArrayValues[8]);
	    }
	    newOrg.setStatus(true);
	    newOrg.setCleanURL(orgCleanURL);
	    if(!"null".equals(strArrayValues[11]) && !"''".equals(strArrayValues[11])) {
		newOrg.setparsingTemplate(strArrayValues[11]);
	    }
	    if(!"null".equals(strArrayValues[12])) {
		newOrg.setOrgDesc(strArrayValues[12]);
	    }
	    newOrg.setOrgType(Integer.parseInt(strArrayValues[13]));
	    if(!"null".equals(strArrayValues[14])) {
		newOrg.setTown(strArrayValues[14]);
	    }
	    if(!"null".equals(strArrayValues[15])) {
		newOrg.setCounty(strArrayValues[15]);
	    }
	    if(!"null".equals(strArrayValues[16])) {
		newOrg.setInfoURL(strArrayValues[16]);
	    }
	    if(!"null".equals(strArrayValues[17])) {
		newOrg.setCountry(strArrayValues[17]);
	    }
	    newOrg.setHelRegistryId(Integer.parseInt(strArrayValues[18]));
	    newOrg.setHelRegistryOrgId(Integer.parseInt(strArrayValues[19]));
	    if(!"null".equals(strArrayValues[20])) {
		newOrg.setHelRegistrySchemaName(strArrayValues[20]);
	    }
	    if(!"null".equals(strArrayValues[21])) {
		newOrg.setPrimaryContactEmail(strArrayValues[21]);
	    }
	    newOrg.setParentOrgId(Integer.parseInt(strArrayValues[22]));
	    if(!"null".equals(strArrayValues[23])) {
		newOrg.setPrimaryContactName(strArrayValues[23]);
	    }
	    if(!"null".equals(strArrayValues[24])) {
		newOrg.setPrimaryTechContactEmail(strArrayValues[24]);
	    }
	    if(!"null".equals(strArrayValues[25])) {
		newOrg.setPrimaryTechContactName(strArrayValues[25]);
	    }
	    
	    orgId = organizationmanager.createOrganization(newOrg);
	}
	else {
	    for(Organization org : orgs) {
		if(org.getOrgName().toLowerCase().trim().equals(orgName.toLowerCase())) {
		    orgId = org.getId();
		    break;
		}
	    }
	    
	    //Need to make sure the correct folders are found
	    File OrganizationDir = new File(myProps.getProperty("ut.directory.utRootDir") + orgCleanURL);
	    
	    if(!OrganizationDir.exists()) {
		fileSystem dir = new fileSystem();
		dir.creatOrgDirectories(myProps.getProperty("ut.directory.utRootDir") + orgCleanURL);
	    }
	}
	
	return orgId;
    }
    
    /**
     * The 'processImportConfigDetails' method will process the configuration information from the configuration import script.
     * 
     * @param strArrayValues 
     * @param orgId 
     * 
     * @return The method will return the new configuration id
     */
    public Integer processImportConfigDetails(String[] strArrayValues, Integer orgId) {
	
	Integer configId = 0;
	
	String configName = strArrayValues[6];
	
	utConfiguration configDetails = utconfigurationmanager.getConfigurationByName(configName, orgId);
	
	if(configDetails == null) {
	    utConfiguration newConfig = new utConfiguration();
	    newConfig.setorgId(orgId);
	    newConfig.setStatus(false);
	    newConfig.setType(Integer.parseInt(strArrayValues[3]));
	    newConfig.setMessageTypeId(Integer.parseInt(strArrayValues[4]));
	    newConfig.setstepsCompleted(6);
	    newConfig.setconfigName(strArrayValues[6]);
	    newConfig.setThreshold(Integer.parseInt(strArrayValues[7]));
	    newConfig.setConfigurationType(Integer.parseInt(strArrayValues[8]));
	    newConfig.setDeleted(false);
	    
	    configId = utconfigurationmanager.createConfiguration(newConfig);
	}
	
	return configId;
    }
    
    /**
     * The 'processImportConfigTransportDetails' method will process the configuration transport details from the configuration import script.
     * @param strArrayValues 
     * @param configId 
     * 
     * @return The method will return the new transportId
     */
    public Integer processImportConfigTransportDetails(String[] strArrayValues, Integer configId) {
	
	Integer configTransportId = 0; 
	
	configurationTransport newConfigTransport = new configurationTransport();
	newConfigTransport.setconfigId(configId);
	newConfigTransport.settransportMethodId(Integer.parseInt(strArrayValues[2]));
	newConfigTransport.setfileType(Integer.parseInt(strArrayValues[3]));
	newConfigTransport.setfileDelimiter(Integer.parseInt(strArrayValues[4]));
	newConfigTransport.setstatus(true);
	if(!"null".equals(strArrayValues[6])) {
	    newConfigTransport.settargetFileName(strArrayValues[6]);
	}
	if(strArrayValues[7].equals("true")) {
	    newConfigTransport.setappendDateTime(true);
	}
	else {
	    newConfigTransport.setappendDateTime(false);
	}
	newConfigTransport.setmaxFileSize(Integer.parseInt(strArrayValues[8]));
	if(strArrayValues[9].equals("true")) {
	    newConfigTransport.setclearRecords(true);
	}
	else {
	    newConfigTransport.setclearRecords(false);
	}
	newConfigTransport.setfileLocation(strArrayValues[10]);
	newConfigTransport.setautoRelease(true);
	newConfigTransport.seterrorHandling(Integer.parseInt(strArrayValues[12]));
	newConfigTransport.setmergeBatches(true);
	newConfigTransport.setcopiedTransportId(Integer.parseInt(strArrayValues[14]));
	newConfigTransport.setfileExt(strArrayValues[15]);
	newConfigTransport.setEncodingId(Integer.parseInt(strArrayValues[16]));
	if(!"null".equals(strArrayValues[17])) {
	    newConfigTransport.setCcdSampleTemplate(strArrayValues[17]);
	}
	if(!"null".equals(strArrayValues[18])) {
	    newConfigTransport.setHL7PDFSampleTemplate(strArrayValues[18]);
	}
	newConfigTransport.setMassTranslation(true);
	if(strArrayValues[20].equals("true")) {
	    newConfigTransport.setZipped(true);
	}
	else {
	    newConfigTransport.setZipped(false);
	}
	newConfigTransport.setZipType(Integer.parseInt(strArrayValues[21]));
	
	if(!"null".equals(strArrayValues[22])) {
	    newConfigTransport.setRestAPIURL(strArrayValues[22]);
	}
	if(!"null".equals(strArrayValues[23])) {
	    newConfigTransport.setRestAPIUsername(strArrayValues[23]);
	}
	if(!"null".equals(strArrayValues[24])) {
	    newConfigTransport.setRestAPIPassword(strArrayValues[24]);
	}
	if("null".equals(strArrayValues[22]) || "".equals(strArrayValues[22])) {
	    newConfigTransport.setRestAPIType(0);
	}
	else {
	    newConfigTransport.setRestAPIType(Integer.parseInt(strArrayValues[25]));
	}
	if(strArrayValues[26].equals("true")) {
	    newConfigTransport.setWaitForResponse(true);
	}
	else {
	    newConfigTransport.setWaitForResponse(false);
	}
	newConfigTransport.setRestAPIFunctionId(Integer.parseInt(strArrayValues[27]));
	newConfigTransport.setJsonWrapperElement(strArrayValues[28]);
	newConfigTransport.setLineTerminator(strArrayValues[29]);
	if(!"null".equals(strArrayValues[30])) {
	    newConfigTransport.setHelRegistryConfigId(Integer.parseInt(strArrayValues[30]));
	}
	newConfigTransport.setHelSchemaName(strArrayValues[31]);
	newConfigTransport.setHelRegistryId(Integer.parseInt(strArrayValues[32]));
	newConfigTransport.setDmConfigKeyword(strArrayValues[33]);
	if(strArrayValues[34].equals("true")) {
	    newConfigTransport.setErgFileDownload(true);
	}
	else {
	    newConfigTransport.setErgFileDownload(false);
	}
	if(strArrayValues[35].equals("true")) {
	    newConfigTransport.setPopulateInboundAuditReport(true);
	}
	else {
	    newConfigTransport.setPopulateInboundAuditReport(false);
	}
	if(strArrayValues[36].equals("true")) {
	    newConfigTransport.setAddTargetFileHeaderRow(true);
	}
	else {
	    newConfigTransport.setAddTargetFileHeaderRow(false);
	}
	if(!"null".equals(strArrayValues[37])) {
	    newConfigTransport.setErrorEmailAddresses(strArrayValues[37]);
	}
	
	try {
	    configTransportId = utconfigurationTransportManager.updateTransportDetails(null,newConfigTransport);
	}
	catch (Exception ex) {}
	
	//Make sure file location folder exists if not create it
	if(!"".equals(strArrayValues[10].toString())) {
	    File fileLocationDir = new File(myProps.getProperty("ut.directory.utRootDir") + strArrayValues[10]);
	    
	    if(!fileLocationDir.exists()) {
		fileLocationDir.mkdirs();
	    }
	}
	
	return configTransportId;
    }
    
    /**
     * The 'processImportConfigFileDropDetails' method will process the configuration file Drop details from the configuration import script.
     * 
     * @param strArrayValues 
     * @param transportId 
     */
    public void processImportConfigFileDropDetails(String[] strArrayValues, Integer transportId) {
	
	configurationFileDropFields newFileDrop = new configurationFileDropFields();
	newFileDrop.setTransportId(transportId);
	newFileDrop.setDirectory(strArrayValues[2]);
	newFileDrop.setMethod(Integer.parseInt(strArrayValues[3]));
	
	try {
	    utconfigurationTransportManager.saveTransportFileDrop(newFileDrop);

	    //Makes sure directory exists
	    if(!"/".equals(strArrayValues[2])) {
		File fileDropLocationDir = new File(myProps.getProperty("ut.directory.utRootDir") + strArrayValues[2]);

		if(!fileDropLocationDir.exists()) {
		    fileDropLocationDir.mkdirs();
		}
	    }
	}
	catch (Exception ex) {}
    }
    
    /**
     * The 'processImportConfigFTPDetails' method will process the configuration FTP details from the configuration import script.
     * 
     * @param strArrayValues 
     * @param transportId 
     */
    public void processImportConfigFTPDetails(String[] strArrayValues, Integer transportId) {
	
	configurationFTPFields newFTP = new configurationFTPFields();
	newFTP.settransportId(transportId);
	newFTP.setip(strArrayValues[2]);
	newFTP.setdirectory(strArrayValues[3]);
	newFTP.setusername(strArrayValues[4]);
	newFTP.setpassword(strArrayValues[5]);
	newFTP.setmethod(Integer.parseInt(strArrayValues[6]));
	newFTP.setport(Integer.parseInt(strArrayValues[7]));
	newFTP.setprotocol(strArrayValues[8]);
	newFTP.setcertification(strArrayValues[9]);
	
	try {
	    utconfigurationTransportManager.saveTransportFTP(0,newFTP);
	}
	catch (Exception ex) {}
    }
    
    /**
     * The 'processImportConfigMessageSpecs' method will process the configuration message specs from the configuration import script.
     * 
     * @param strArrayValues 
     * @param configId 
     */
    public void processImportConfigMessageSpecs(String[] strArrayValues, Integer configId) {
	
	configurationMessageSpecs messageSpecs = new configurationMessageSpecs();
	messageSpecs.setconfigId(configId);
	if(!"null".equals(strArrayValues[2])) {
	    messageSpecs.settemplateFile(strArrayValues[2]);
	}
	messageSpecs.setmessageTypeCol(Integer.parseInt(strArrayValues[3]));
	if(!"null".equals(strArrayValues[4])) {
	    messageSpecs.setmessageTypeVal(strArrayValues[4]);
	}
	messageSpecs.settargetOrgCol(Integer.parseInt(strArrayValues[5]));
	if(strArrayValues[6].equals("true")) {
	    messageSpecs.setcontainsHeaderRow(true);
	}
	else {
	    messageSpecs.setcontainsHeaderRow(false);
	}
	messageSpecs.setrptField1(Integer.parseInt(strArrayValues[7]));
	messageSpecs.setrptField2(Integer.parseInt(strArrayValues[8]));
	messageSpecs.setrptField3(Integer.parseInt(strArrayValues[9]));
	messageSpecs.setrptField4(Integer.parseInt(strArrayValues[10]));
	messageSpecs.setSourceSubOrgCol(Integer.parseInt(strArrayValues[11]));
	messageSpecs.setExcelstartrow(Integer.parseInt(strArrayValues[12]));
	messageSpecs.setExcelskiprows(Integer.parseInt(strArrayValues[13]));
	if(!"null".equals(strArrayValues[14])) {
	    messageSpecs.setParsingTemplate(strArrayValues[14]);
	}
	if(!"null".equals(strArrayValues[15])) {
	    messageSpecs.setFileNameConfigHeader(strArrayValues[15]);
	}
	messageSpecs.setTotalHeaderRows(Integer.parseInt(strArrayValues[16]));
	
	try {
	    utconfigurationmanager.updateMessageSpecs(messageSpecs);
	}
	catch (Exception ex) {}
    }
    
    /**
     * The 'processImportConfigFields' method will process the configuration fields from the configuration import script.
     * 
     * @param strArrayValues 
     * @param configId 
     * @param transportId 
     * 
     * @return  The method will return the new fieldId
     */
    public Integer processImportConfigFields(String[] strArrayValues, Integer configId, Integer transportId) {
	
	Integer fieldId = 0;
	
	configurationFormFields field = new configurationFormFields();
	field.setAssociatedFieldId(Integer.parseInt(strArrayValues[1]));
	field.setconfigId(configId);
	field.settransportDetailId(transportId);
	field.setFieldNo(Integer.parseInt(strArrayValues[4]));
	field.setFieldDesc(strArrayValues[5]);
	field.setValidationType(Integer.parseInt(strArrayValues[6]));
	if(strArrayValues[7].equals("true")) {
	    field.setRequired(true);
	}
	else {
	    field.setRequired(false);
	}
	if(strArrayValues[8].equals("true")) {
	    field.setUseField(true);
	}
	else {
	    field.setUseField(false);
	}
	if(strArrayValues[9] == null) {
	   field.setAssociatedFieldNo(0); 
	}
	else if (!"null".equals(strArrayValues[9])) {
	  field.setAssociatedFieldNo(Integer.parseInt(strArrayValues[9])); 
	}
	if (!"null".equals(strArrayValues[10])) {
	    field.setDefaultValue(strArrayValues[10]);
	}
	if (!"null".equals(strArrayValues[11])) {
	    field.setSampleData(strArrayValues[11]);
	}
	
	try {
	    fieldId = utconfigurationTransportManager.saveConfigurationFormFields(field);
	}
	catch (Exception ex) {}
	
	return fieldId;
    }
    
    /**
     * The 'processImportConfigFieldDTS' method will process the configuration field data translation from the configuration import script.
     * 
     * @param strArrayValues 
     * @param configId 
     * @param fieldId 
     * 
     */
    public void processImportConfigFieldDTS(String[] strArrayValues, Integer configId, Integer fieldId) {
	
	configurationDataTranslations dts = new configurationDataTranslations();
	dts.setconfigId(configId);
	dts.setFieldId(fieldId);
	dts.setCrosswalkId(Integer.parseInt(strArrayValues[3]));
	dts.setMacroId(Integer.parseInt(strArrayValues[4]));
	dts.setPassClear(Integer.parseInt(strArrayValues[5]));
	dts.setFieldA(strArrayValues[6]);
	dts.setFieldB(strArrayValues[7]);
	dts.setConstant1(strArrayValues[8]);
	dts.setConstant2(strArrayValues[9]);
	dts.setProcessOrder(Integer.parseInt(strArrayValues[10]));
	dts.setCategoryId(Integer.parseInt(strArrayValues[11]));
	
	if(!"null".equals(strArrayValues[12])) {
	    dts.setDefaultValue(strArrayValues[12]);
	}
	
	utconfigurationmanager.saveDataTranslations(dts);
    }
    
    
    /**
     * The 'processImportConfigSchedule' method will process the configuration schedule from the configuration import script.
     * 
     * @param strArrayValues 
     * @param configId 
     * 
     */
    public void processImportConfigSchedule(String[] strArrayValues, Integer configId) {
	
	configurationSchedules configSchedule = new configurationSchedules();
	configSchedule.setconfigId(configId);
	configSchedule.settype(Integer.parseInt(strArrayValues[2]));
	configSchedule.setprocessingType(Integer.parseInt(strArrayValues[3]));
	configSchedule.setnewfileCheck(Integer.parseInt(strArrayValues[4]));
	configSchedule.setprocessingDay(Integer.parseInt(strArrayValues[5]));
	configSchedule.setprocessingTime(Integer.parseInt(strArrayValues[6]));
	
	utconfigurationmanager.saveSchedule(configSchedule);
	
    }
    
     /**
     * The 'processImportConfigCrosswalks' method will process the configuration crosswalks from the configuration import script.
     * 
     * @param strArrayValues 
     * @param configId 
     * @param orgId 
     * @param cwIds 
     * @return  
     * 
     */
    public String processImportConfigCrosswalks(String[] strArrayValues, Integer configId, Integer orgId, String cwIds) {
	
	String returnCWids = "";
	boolean addNewCW = false;
	
	if("".equals(cwIds)) {
	    addNewCW = true;
	}
	else {
	    
	    String oldCWId = cwIds.split("\\-")[0];
	    Integer newCWId = Integer.parseInt(cwIds.split("\\-")[1]);
	    
	    if(oldCWId.equals(strArrayValues[1])) {
		CrosswalkData cwData = new CrosswalkData();
		cwData.setCrosswalkId(newCWId);
		cwData.setSourceValue(strArrayValues[6]);
		cwData.setTargetValue(strArrayValues[7]);
		cwData.setDescValue(strArrayValues[8]);
		messagetypemanager.saveCrosswalkData(cwData);
		
		returnCWids = strArrayValues[1] + "-" + newCWId;
	    }
	    else {
		addNewCW = true;
	    }
	}
	
	if(addNewCW) {
	    Crosswalks cw = new Crosswalks();
	    
	    String cwName = strArrayValues[2].trim();
	    Integer cwId = 0;
	    boolean newCW = false;
	    
	    cw.setFileDelimiter(Integer.parseInt(strArrayValues[3]));
	    cw.setfileName(strArrayValues[4]);
	    
	    if(Integer.parseInt(strArrayValues[5]) > 0) {
		if(cwName.contains("_")) {
		    String[] cwNameArray = cwName.split("\\_");
		    cw.setName(configId+"_"+cwNameArray[1]);
		    cwName = cwNameArray[1];
		}
		else {
		    cw.setName(configId+"_"+cwName);
		}
		cw.setOrgId(orgId);
	    }
	    else {
		orgId = 0;
		cw.setOrgId(orgId);
		cw.setName(cwName);
	    }
	    
	    //Need to check if the generic CW already exists
	    Crosswalks cwDetails = messagetypemanager.getCrosswalkByNameAndOrg(cwName,orgId,strArrayValues[4].trim());

	    if(cwDetails != null) {
		if(cwDetails.getId() > 0) {
		    cwId = cwDetails.getId();
		}
	    }
	
	    if(cwId == 0) {
		newCW = true;
		try {
		    cwId = messagetypemanager.createCrosswalk(cw);
		    returnCWids = strArrayValues[1] + "-" + cwId;
		}
		catch (Exception cwEx) {
		    cwEx.printStackTrace();
		}
	    }
	    
	    if(newCW) {
		CrosswalkData cwData = new CrosswalkData();
		cwData.setCrosswalkId(cwId);
		cwData.setSourceValue(strArrayValues[6]);
		cwData.setTargetValue(strArrayValues[7]);
		cwData.setDescValue(strArrayValues[8]);
		messagetypemanager.saveCrosswalkData(cwData);
	    }
	    
	    if(cwId > 0) {
		//Update all DTS that has the old crosswalk id with the new one
		String sqlUpdate = "update configurationdatatranslations set crosswalkId = " + cwId + " where crosswalkId = " + Integer.parseInt(strArrayValues[1]) + " and configId = " + configId;
		messagetypemanager.executeSQLStatement(sqlUpdate);

		sqlUpdate = "update configurationdatatranslations set constant1 = " + cwId + " where constant1 = " + Integer.parseInt(strArrayValues[1]) + " and macroId = 129 and configId = " + configId;
		messagetypemanager.executeSQLStatement(sqlUpdate);

		sqlUpdate = "update configurationdatatranslations set constant1 = " + cwId + " where constant1 = " + Integer.parseInt(strArrayValues[1]) + " and macroId = 160 and configId = " + configId;
		messagetypemanager.executeSQLStatement(sqlUpdate);

		sqlUpdate = "update configurationdatatranslations set constant1 = " + cwId + " where constant1 = " + Integer.parseInt(strArrayValues[1]) + " and macroId = 177 and configId = " + configId;
		messagetypemanager.executeSQLStatement(sqlUpdate);

		sqlUpdate = "update configurationdatatranslations set constant1 = " + cwId + " where constant1 = " + Integer.parseInt(strArrayValues[1]) + " and macroId = 195 and configId = " + configId;
		messagetypemanager.executeSQLStatement(sqlUpdate);

		sqlUpdate = "update configurationdatatranslations set constant2 = " + cwId + " where constant2 = " + Integer.parseInt(strArrayValues[1]) + " and macroId = 199 and configId = " + configId;
		messagetypemanager.executeSQLStatement(sqlUpdate);
	    }
	}
	
	return returnCWids;
    }
    
    /**
     * 
     * @param importSection
     * @param configId
     * @param configTransportId
     * @param orgId
     * @param configName
     * @param ex 
     */
    public void processImportConfigError(String importSection, Integer configId, Integer configTransportId, Integer orgId, String configName, Exception ex) throws Exception {
	
	//back out the configuration import details
	String backOutConfigSQL = "";
	
	if(configTransportId > 0) {
	    backOutConfigSQL += "delete from rel_transportfiledropdetails where transportId = " + configTransportId + ";";
	    backOutConfigSQL += "delete from rel_transportftpdetails where transportId = " + configTransportId + ";";
	}
	
	if(orgId > 0 && configId > 0) {
	    backOutConfigSQL += "delete from rel_crosswalkdata where crosswalkId in (select id from crosswalks "
		+ "where orgId = " + orgId + " and id in (select crosswalkId from configurationdatatranslations where configId = "+configId+"));";
	    backOutConfigSQL += "delete from crosswalks where orgId = " + orgId + " and id in (select crosswalkId from configurationdatatranslations where configId = "+configId+");";
	}
	
	if(configId > 0) {
	    backOutConfigSQL += "delete from configurationdatatranslations where configId = " + configId + ";";
	    backOutConfigSQL += "delete from configurationformfields where configId = " + configId + ";";
	    backOutConfigSQL += "delete from configurationmessagespecs where configId = " + configId + ";";
	    backOutConfigSQL += "delete from configurationschedule where configId = " + configId + ";";
	    backOutConfigSQL += "delete from configurationtransportdetails where configId = " + configId + ";";
	    backOutConfigSQL += "delete from configurations where id = " + configId + ";";
	}
	
	if(!"".equals(backOutConfigSQL)) {
	    messagetypemanager.executeSQLStatement(backOutConfigSQL);
	}
	
	String emailSubject = "Configuration Import has been Backed out due to an error.";
	StringBuilder emailBody = new StringBuilder();

	Organization orgDetails = organizationmanager.getOrganizationById(orgId);

	emailBody.append("The following configuration import has failed.");
	emailBody.append("<br /><br />");
	emailBody.append("Organization Name: ").append(orgDetails.getOrgName());
	emailBody.append("<br />Configuration Name: ").append(configName);
	emailBody.append("<br />Import Section: ").append(importSection);
	emailBody.append("<br/><br />Reason for failure:<br />").append(ExceptionUtils.getStackTrace(ex));

	mailMessage mail = new mailMessage();
	mail.settoEmailAddress(myProps.getProperty("admin.email"));
	mail.setfromEmailAddress("support@health-e-link.net");
	mail.setmessageSubject(emailSubject);
	mail.setmessageBody(emailBody.toString());
	emailMessageManager.sendEmail(mail);
    }
}