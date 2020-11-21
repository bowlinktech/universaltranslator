package com.hel.ut.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

import com.hel.ut.model.Organization;
import com.hel.ut.service.organizationManager;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.reference.CountryList;
import com.hel.ut.reference.USStateList;
import com.hel.ut.reference.fileSystem;
import com.hel.ut.service.messageTypeManager;
import com.registryKit.registry.helRegistry;
import com.registryKit.registry.helRegistryManager;
import com.registryKit.registry.tiers.tierManager;
import com.registryKit.registry.tiers.tierOrganizationDetails;
import com.registryKit.registry.tiers.tiers;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import java.io.File;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/**
 * The adminOrgController class will handle all URL requests that fall inside of the '/administrator/organizations' url path.
 *
 * This path will be used when the administrator is managing and existing organization or creating a new organization
 *
 * @author chadmccue
 *
 */
@Controller
@RequestMapping("/administrator/organizations")
public class adminOrgContoller {

    @Autowired
    private organizationManager organizationManager;

    @Autowired
    private utConfigurationManager configurationmanager;

    @Autowired
    private messageTypeManager messagetypemanager;

    @Autowired
    private utConfigurationTransportManager configurationTransportManager;
    
    @Autowired
    private helRegistryManager helregistrymanager;
    
    @Autowired
    private tierManager tiermanager;
    
    @Resource(name = "myProps")
    private Properties myProps;
    

    /**
     * The private maxResults variable will hold the number of results to show per list page.
     */
    private static int maxResults = 20;

    /**
     * The '/list' GET request will serve up the existing list of organizations in the system
     *
     * @return	The organization page list
     *
     * @Objects	(1) An object containing all the found organizations (2) An object will be returned that hold the organiationManager so we can run some functions on each returned org in the list
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listOrganizations() throws Exception {
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/listOrganizations");

	//List<Organization> organizations = organizationManager.getOrganizations();
	//mav.addObject("organizationList",organizations);
	
        return mav;
    }
    
    @RequestMapping(value = "/ajax/getOrganizations", method = RequestMethod.GET)
    @ResponseBody
    public String getOrganizations(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
	
	Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();
	Integer iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
        Integer iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
        String sortColumn = request.getParameter("iSortCol_0");
        String sortColumnName = request.getParameter("mDataProp_"+sortColumn);
        String searchTerm = request.getParameter("sSearch").toLowerCase();
        String sEcho = request.getParameter("sEcho");
        String sortDirection = request.getParameter("sSortDir_0");
        Integer totalRecords = 0;
	
	
	List<Organization> organizations = organizationManager.getOrganizationsPaged(iDisplayStart, iDisplayLength, searchTerm, sortColumnName, sortDirection);
	List<Organization> totalOrgs = organizationManager.getOrganizations();
	
	for(Organization org : totalOrgs) {
	    if(!"bowlinktest".equals(org.getCleanURL().trim().toLowerCase())) {
		totalRecords++;
	    }
	}
	
	jsonResponse.addProperty("sEcho", sEcho);
        jsonResponse.addProperty("iTotalRecords", totalRecords);
        jsonResponse.addProperty("iTotalDisplayRecords", totalRecords);
        jsonResponse.add("aaData", gson.toJsonTree(organizations));
        
        return jsonResponse.toString();
    }
    

    /**
     * The '/create' GET request will serve up the create new organization page
     *
     * @return	The create new organization form
     *
     * @Objects	(1) An object with a new organization
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createOrganization() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/organizationDetails");
        mav.addObject("organization", new Organization());

        //Get a list of states
        USStateList stateList = new USStateList();

        //Get the object that will hold the states
        mav.addObject("stateList", stateList.getStates());
        
        //Get a list of countries
        CountryList countryList = new CountryList();

        //Get the object that will hold the countries
        mav.addObject("countryList", countryList.getCountries());
	
	List<Organization> organizations = organizationManager.getAllActiveOrganizations();
	mav.addObject("organizationList",organizations);

        return mav;
    }

    /**
     * The '/create' POST request will submit the new organization once all required fields are checked, the system will also check to make sure the organziation name is not already in use.
     *
     * @param organization	The object holding the organization form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     * @param action	The variable that holds which button was pressed
     *
     * @return	Will return the organization list page on "Save & Close" Will return the organization details page on "Save" Will return the organization create page on error
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView saveNewOrganization(@Valid Organization organization, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

        //Get a list of states
        USStateList stateList = new USStateList();
        
        //Get a list of countries
        CountryList countryList = new CountryList();
	
       if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/organizations/organizationDetails");
            //Get the object that will hold the states
            mav.addObject("stateList", stateList.getStates());
            mav.addObject("countryList", countryList.getCountries());
            return mav;
        }
       
        List<Organization> existing = organizationManager.getOrganizationByName(organization.getcleanURL());
        if (!existing.isEmpty()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/organizations/organizationDetails");
            mav.addObject("id", organization.getId());
            mav.addObject("existingOrg", "Organization " + organization.getOrgName() + " already exists.");
            //Get the object that will hold the states
            mav.addObject("stateList", stateList.getStates());
            mav.addObject("countryList", countryList.getCountries());
            return mav;
        }
	
        Integer id = organizationManager.createOrganization(organization);

        //Get the organization name that was just added
        Organization latestorg = organizationManager.getOrganizationById(id);

        redirectAttr.addFlashAttribute("savedStatus", "created");

        if (action.equals("save")) {
            ModelAndView mav = new ModelAndView(new RedirectView(latestorg.getcleanURL() + "/"));
            return mav;
        } else {
            ModelAndView mav = new ModelAndView(new RedirectView("list"));
            return mav;
        }

    }

    /**
     * The '/{cleanURL}' GET request will display the clicked organization details page.
     *
     * @param cleanURL	The {clearnURL} will be the organizations name with spaces removed. This was set when the organization was created.
     *
     * @return	Will return the organization details page.
     *
     * @Objects	(1) The object containing all the information for the clicked org (2) The 'id' of the clicked org that will be used in the menu and action bar
     *
     * @throws Exception
     *
     */
    @RequestMapping(value = "/{cleanURL}", method = RequestMethod.GET)
    public ModelAndView viewOrganizationDetails(@PathVariable String cleanURL) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/organizationDetails");

        List<Organization> organization = organizationManager.getOrganizationByName(cleanURL);
        Organization orgDetails = organization.get(0);

        mav.addObject("id", orgDetails.getId());
        mav.addObject("selOrgType", orgDetails.getOrgType());
        mav.addObject("organization", orgDetails);

        //Get a list of states
        USStateList stateList = new USStateList();

        //Get the object that will hold the states
        mav.addObject("stateList", stateList.getStates());
        
        //Get a list of countries
        CountryList countryList = new CountryList();

        //Get the object that will hold the countries
        mav.addObject("countryList", countryList.getCountries());

	List<Organization> organizations = organizationManager.getAllActiveOrganizations();
	mav.addObject("organizationList",organizations);
	
        return mav;

    }

    /**
     * The '/{cleanURL}' POST request will handle submitting changes for the selected organization.
     *
     * @param organization	The object containing the organization form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     * @param action	The variable that holds which button was pressed
     *
     * @return	Will return the organization list page on "Save & Close" Will return the organization details page on "Save" Will return the organization create page on error
     *
     * @Objects	(1) The object containing all the information for the clicked org (2) The 'id' of the clicked org that will be used in the menu and action bar
     * @throws Exception
     */
    @RequestMapping(value = "/{cleanURL}", method = RequestMethod.POST)
    public ModelAndView updateOrganization(@Valid Organization organization, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

        //Get a list of states
        USStateList stateList = new USStateList();
        
        //Get a list of countries
        CountryList countryList = new CountryList();

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/organizations/organizationDetails");
            mav.addObject("id", organization.getId());
            mav.addObject("selOrgType", organization.getOrgType());
            //Get the object that will hold the states
            mav.addObject("stateList", stateList.getStates());
            mav.addObject("countryList", countryList.getCountries());
            return mav;
        }

        Organization currentOrg = organizationManager.getOrganizationById(organization.getId());
	
	boolean updatedName = false;
	
	//Update the organization
	String orgCleanURL = organization.getOrgName().replace(" ", "");
	
	if(!orgCleanURL.equals(organization.getCleanURL())) {
	    organization.setcleanURL(orgCleanURL);
	}
	
        if (!currentOrg.getcleanURL().trim().equals(organization.getcleanURL().trim())) {
	    List<Organization> existing = organizationManager.getOrganizationByName(organization.getcleanURL());
	    updatedName = true;
            if (!existing.isEmpty()) {
                ModelAndView mav = new ModelAndView();
                mav.setViewName("/administrator/organizations/organizationDetails");
                mav.addObject("id", organization.getId());
                mav.addObject("selOrgType", organization.getOrgType());
                mav.addObject("existingOrg", "Organization " + organization.getOrgName().trim() + " already exists.");
                //Get the object that will hold the states
                mav.addObject("stateList", stateList.getStates());
                mav.addObject("countryList", countryList.getCountries());
                return mav;
            }
        }
	
	//Make sure the organization folder name exists
	String UTDirectory = myProps.getProperty("ut.directory.utRootDir");
	File directory = new File(UTDirectory.replace("/home/","/") + organization.getcleanURL());
	if (!directory.exists()) {
	    updatedName = true;
	}

        organizationManager.updateOrganization(organization);
	
	//If updated name, need to check if any configurations are set up for this or
	if(updatedName) {
	    List<utConfiguration> configurations = configurationmanager.getActiveConfigurationsByOrgId(currentOrg.getId());
	    
	    if(configurations != null) {
		if(!configurations.isEmpty()) {
		    
		    List<Integer> configIds = configurations.stream().map(e -> e.getId()).collect(Collectors.toList());
		    
		    configurationmanager.updateConfigurationDirectories(configIds,currentOrg.getcleanURL().trim(),organization.getcleanURL().trim());
		}
	    }
	    
	    //Need to delete the old directory
	    File oldDirectory = new File(UTDirectory.replace("/home/","/") + currentOrg.getcleanURL());
	    if (directory.exists()) {
		fileSystem filesystem = new fileSystem();
		filesystem.deleteOrgDirectories(UTDirectory.replace("/home/","/") + currentOrg.getcleanURL());
	    }
	}
	

        //This variable will be used to display the message on the details form
        redirectAttr.addFlashAttribute("savedStatus", "updated");

        //If the "Save" button was pressed 
        if (action.equals("save")) {
            ModelAndView mav = new ModelAndView(new RedirectView("../" + organization.getcleanURL() + "/"));
            return mav;
        } //If the "Save & Close" button was pressed.
        else {
            ModelAndView mav = new ModelAndView(new RedirectView("../list"));
            return mav;
        }

    }

    /**
     * The '/{cleanURL}/delete POST request will remove the clicked organization and anything associated to it.
     *
     * @param id	The variable that holds the id of the clicked organization
     * @param redirectAttr
     * @return 
     * @throws java.lang.Exception
     *
     * @Return	Will return the organization list page
     *
     */
    @RequestMapping(value = "/{cleanURL}/delete", method = RequestMethod.POST)
    public ModelAndView deleteOrganization(@RequestParam int id, RedirectAttributes redirectAttr) throws Exception {

        organizationManager.deleteOrganization(id);

        //This variable will be used to display the message on the details form
        redirectAttr.addFlashAttribute("savedStatus", "deleted");
        ModelAndView mav = new ModelAndView(new RedirectView("../list"));
        return mav;
    }

    /**
     * *********************************************************
     * ORGANIZATION CONFIGUARATION FUNCTIONS 
     ********************************************************
     */
    /**
     * The '/{cleanURL/configurations' GET request will display the list of configurations set up for the selected organization.
     *
     * @param cleanURL	The variable that holds the organization that is being viewed
     *
     * @return	Will return the organization utConfiguration list page
     *
     * @Objects	(1) An object that holds configurations found for the organization (2) The orgId used for the menu and action bar
     *
     * @throws Exception
     */
    @RequestMapping(value = "/{cleanURL}/configurations", method = RequestMethod.GET)
    public ModelAndView listOrganizationConfigs(@PathVariable String cleanURL) throws Exception {
	
	List<Organization> organization = organizationManager.getOrganizationByName(cleanURL);
        Organization orgDetails = organization.get(0);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/configurations");

        List<utConfiguration> configurations = configurationmanager.getConfigurationsByOrgId(orgDetails.getId(), "");

        mav.addObject("orgName", orgDetails.getOrgName());
        mav.addObject("id", orgDetails.getId());
        mav.addObject("selOrgType", orgDetails.getOrgType());
        mav.addObject("configs", configurations);

        configurationTransport transportDetails;

        for (utConfiguration config : configurations) {
            transportDetails = configurationTransportManager.getTransportDetails(config.getId());
            if (transportDetails != null) {
                config.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
            }
        }

        return mav;
    }

    /**
     * The '/getHELRegistries' GET request will return a list of Health-e-Link registries
     * 
     *
     * @return The function will return a list of active health-e-link registries.
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = {"/{cleanURL}/getHELRegistries", "/getHELRegistries"}, method = RequestMethod.GET)
    public @ResponseBody List<helRegistry> getHealtheLinkRegistries() throws Exception {
	
	List<helRegistry> helRegistries = helregistrymanager.getAllActiveRegistries();
	
        return helRegistries;
    }
    
    
    /**
     * The '/getHELRegistryOrganizations' GET request will return a list of registry organizations saved at the
     * last set up tier.
     *
     *
     * @return The function will return a list of organizations
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = {"/{cleanURL}/getHELRegistryOrganizations", "/getHELRegistryOrganizations"}, method = RequestMethod.GET)
    public @ResponseBody List<tierOrganizationDetails> getHELRegistryOrganizations() throws Exception {
	
	tiers lastRegistryTier = tiermanager.getLastTier();
	
	if(lastRegistryTier != null) {
	    List<tierOrganizationDetails> tierOrganizations = tiermanager.getTierEntries(lastRegistryTier.getId());
	    
	    if(tierOrganizations != null) {
		return tierOrganizations;
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
     * The '/getHELRegistryOrganizationDetails' GET request will return the details of the selected registry
     * organization.
     *
     * @param selRegistryOrgId
     * @return The function will return the details of the selected organization
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = {"/{cleanURL}/getHELRegistryOrganizationDetails", "/getHELRegistryOrganizationDetails"}, method = RequestMethod.GET)
    public @ResponseBody tierOrganizationDetails getHELRegistryOrganizationDetails(@RequestParam(value = "selRegistryOrgId", required = true) Integer selRegistryOrgId) throws Exception {
	
	tierOrganizationDetails orgDetails = tiermanager.getTierEntryById(selRegistryOrgId);
	return orgDetails;
    }
    
}
