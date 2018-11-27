package com.hel.ut.controller;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hel.ut.model.Organization;
import com.hel.ut.service.organizationManager;
import com.hel.ut.model.User;
import com.hel.ut.service.userManager;
import com.hel.ut.model.configuration;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.messageType;
import com.hel.ut.reference.CountryList;
import com.hel.ut.reference.USStateList;
import com.hel.ut.service.configurationManager;
import com.hel.ut.service.configurationTransportManager;
import com.hel.ut.service.messageTypeManager;
import com.hel.rrKit.hierarchy.hierarchyManager;
import com.hel.rrKit.hierarchy.programHierarchyDetails;
import com.hel.rrKit.hierarchy.programOrgHierarchy;
import com.hel.rrKit.program.program;
import com.hel.rrKit.program.programManager;

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
    private userManager userManager;


    @Autowired
    private configurationManager configurationmanager;

    @Autowired
    private messageTypeManager messagetypemanager;

    @Autowired
    private configurationTransportManager configurationTransportManager;
    
    @Autowired
    private programManager programmanager;
    
    @Autowired
    private hierarchyManager hierarchymanager;

    /**
     * The private maxResults variable will hold the number of results to show per list page.
     */
    private static int maxResults = 20;

    /**
     * The '/list' GET request will serve up the existing list of organizations in the system
     *
     * @param page	The page parameter will hold the page to view when pagination is built.
     * @return	The organization page list
     *
     * @Objects	(1) An object containing all the found organizations (2) An object will be returned that hold the organiationManager so we can run some functions on each returned org in the list
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listOrganizations() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/listOrganizations");

        List<Organization> organizations = organizationManager.getOrganizations();
        mav.addObject("orgFunctions", organizationManager);
        mav.addObject("organizationList", organizations);

        return mav;

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

        List<Organization> organizations = organizationManager.getOrganizations();
        mav.addObject("organizationList", organizations);
	
	//Get the list of rapid registry programs
	List<program> registries = programmanager.getActivePrograms();
	mav.addObject("registries", registries);
	
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

        Integer id = null;
        id = (Integer) organizationManager.createOrganization(organization);

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

        List<Organization> organizations = organizationManager.getOrganizations();
        mav.addObject("organizationList", organizations);
	
	//Get the list of rapid registry programs
	List<program> registries = programmanager.getActivePrograms();
	mav.addObject("registries", registries);

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

        if (!currentOrg.getcleanURL().trim().equals(organization.getcleanURL().trim())) {
            List<Organization> existing = organizationManager.getOrganizationByName(organization.getcleanURL());
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

        //Update the organization
        organizationManager.updateOrganization(organization);

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
     * ORGANIZATION CONFIGUARATION FUNCTIONS ********************************************************
     */
    /**
     * The '/{cleanURL/configurations' GET request will display the list of configurations set up for the selected organization.
     *
     * @param cleanURL	The variable that holds the organization that is being viewed
     *
     * @return	Will return the organization configuration list page
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

        List<configuration> configurations = configurationmanager.getConfigurationsByOrgId(orgDetails.getId(), "");

        mav.addObject("orgName", orgDetails.getOrgName());
        mav.addObject("id", orgDetails.getId());
        mav.addObject("selOrgType", orgDetails.getOrgType());
        mav.addObject("configs", configurations);

        messageType messagetype;
        configurationTransport transportDetails;

        for (configuration config : configurations) {
            messagetype = messagetypemanager.getMessageTypeById(config.getMessageTypeId());
            config.setMessageTypeName(messagetype.getName());

            transportDetails = configurationTransportManager.getTransportDetails(config.getId());
            if (transportDetails != null) {
                config.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
            }
        }

        return mav;
    }

    /**
     * *********************************************************
     * ORGANIZATION USER FUNCTIONS ********************************************************
     */
    /**
     * The '/{cleanURL/users' GET request will display the list of system users for the selected organization.
     *
     * @param cleanURL	The variable that holds the organization that is being viewed
     *
     * @return	Will return the organization user list page
     *
     * @Objects	(1) An object that holds users found for the organization (2) The userManager object so we can run some functions on each user returned. (3)	The orgId used for the menu and action bar
     *
     * @throws Exception
     */
    @RequestMapping(value = "/{cleanURL}/users", method = RequestMethod.GET)
    public ModelAndView listOrganizationUsers(@PathVariable String cleanURL) throws Exception {
	
	List<Organization> organization = organizationManager.getOrganizationByName(cleanURL);
        Organization orgDetails = organization.get(0);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/users");

        mav.addObject("orgName", orgDetails.getOrgName());

        List<User> users = organizationManager.getOrganizationUsers(orgDetails.getId());
        mav.addObject("id", orgDetails.getId());
        mav.addObject("selOrgType", orgDetails.getOrgType());
        mav.addObject("userFunctions", userManager);
        mav.addObject("userList", users);

        return mav;

    }

    /**
     * The '/{cleanURL}/users/newSystemUser' GET request will be used to display the blank new system user screen (In a modal)
     *
     *
     * @return	The organization user blank form page
     *
     * @Objects	(1) An object that will hold all the form fields of a new user (2) An object to hold the button value "Create"
     *
     */
    @RequestMapping(value = "/{cleanURL}/newSystemUser", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView newSystemUser(@PathVariable String cleanURL) throws Exception {
	
	List<Organization> organization = organizationManager.getOrganizationByName(cleanURL);
        Organization orgDetails = organization.get(0);
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/users/details");
        User userdetails = new User();

        //Set the id of the organization for the new user
        userdetails.setOrgId(orgDetails.getId());
        mav.addObject("btnValue", "Create");
        mav.addObject("userdetails", userdetails);

        //Get All Available user sections
        //List<siteSections> sections = userManager.getSections();
        //mav.addObject("sections", sections);

        return mav;
    }

    /**
     * The '/{cleanURL}/create' POST request will handle submitting the new organization system user.
     *
     * @param user	The object containing the system user form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     *
     * @return	Will return the system user list page on "Save" Will return the system user form page on error
     *
     * @Objects	(1) The object containing all the information for the clicked org
     * @throws Exception
     */
    @RequestMapping(value = "/{cleanURL}/create", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView createsystemUser(@Valid @ModelAttribute(value = "userdetails") User userdetails, BindingResult result, RedirectAttributes redirectAttr, @PathVariable String cleanURL) throws Exception {
	
        /*if (userdetails.getsectionList() == null) {
            ModelAndView mav = new ModelAndView();
            List<siteSections> sections = userManager.getSections();
            mav.addObject("sections", sections);
            mav.addObject("sectionListError", true);
            mav.setViewName("/administrator/organizations/users/details");
            mav.addObject("btnValue", "Create");
            return mav;
        }*/

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/organizations/users/details");
            //List<siteSections> sections = userManager.getSections();
            //mav.addObject("sections", sections);
            mav.addObject("btnValue", "Create");
            return mav;
        }

        User existing = userManager.getUserByUserName(userdetails.getUsername());

        if (existing != null) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/organizations/users/details");
            //List<siteSections> sections = userManager.getSections();
            //mav.addObject("sections", sections);
            mav.addObject("btnValue", "Create");
            mav.addObject("existingUsername", "Username " + userdetails.getUsername().trim() + " already exists.");
            return mav;
        }

        userdetails = userManager.encryptPW(userdetails);
        userManager.createUser(userdetails);

        ModelAndView mav = new ModelAndView("/administrator/organizations/users/details");
        mav.addObject("success", "userCreated");
        return mav;
    }

    /**
     * The '/{cleanURL}/users/update' POST request will handle submitting changes for the selected organization system user.
     *
     * @param user	The object containing the system user form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     *
     * @return	Will return the system user list page on "Save" Will return the system user form page on error
     *
     * @Objects	(1) The object containing all the information for the clicked org
     * @throws Exception
     */
    @RequestMapping(value = "/{cleanURL}/update", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView updatesystemUser(@Valid @ModelAttribute(value = "userdetails") User userdetails, BindingResult result, RedirectAttributes redirectAttr, @PathVariable String cleanURL) throws Exception {

        /*if (userdetails.getsectionList() == null) {
            ModelAndView mav = new ModelAndView();
            List<siteSections> sections = userManager.getSections();
            mav.addObject("sections", sections);
            mav.addObject("sectionListError", true);
            mav.setViewName("/administrator/organizations/users/details");
            mav.addObject("btnValue", "Update");
            return mav;
        }*/
	
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            //List<siteSections> sections = userManager.getSections();
            //mav.addObject("sections", sections);
            mav.setViewName("/administrator/organizations/users/details");
            mav.addObject("btnValue", "Update");
            return mav;
        }

        User currentUser = userManager.getUserById(userdetails.getId());

        if (!currentUser.getUsername().trim().equals(userdetails.getUsername().trim())) {
            User existing = userManager.getUserByUserName(userdetails.getUsername());
            if (existing != null) {
                ModelAndView mav = new ModelAndView();
                mav.setViewName("/administrator/organizations/users/details");
                //List<siteSections> sections = userManager.getSections();
                //mav.addObject("sections", sections);
                mav.addObject("btnValue", "Update");
                mav.addObject("existingUsername", "Username " + userdetails.getUsername().trim() + " already exists.");
                return mav;
            }
        }

        /**
         * need to check user's password, if blank, we do not change *
         */
        //here we get salt and redo password
        if (!userdetails.getPassword().equalsIgnoreCase("")) {
            userdetails = userManager.encryptPW(userdetails);
        } else {
            userdetails.setRandomSalt(currentUser.getRandomSalt());
            userdetails.setEncryptedPw(currentUser.getEncryptedPw());
        }

        userManager.updateUser(userdetails);

        ModelAndView mav = new ModelAndView("/administrator/organizations/users/details");
        mav.addObject("success", "userUpdated");
        return mav;
    }

    /**
     * The '/{cleanURL}/user/{person}?i=##' GET request will be used to return the details of the selected user.
     *
     * @param i	The id of the user selected
     *
     * @return	The organization user details page
     *
     * @Objects	(1) An object that will hold all the details of the clicked user (2) An object that will hold all the available sections the user can have access to
     *
     */
    @RequestMapping(value = "/{cleanURL}/user/{person}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView viewUserDetails(@RequestParam(value = "i", required = true) Integer userId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/users/details");

        //Get all the details for the clicked user
        User userDetails = userManager.getUserById(userId);

        mav.addObject("userId", userDetails.getId());
        mav.addObject("btnValue", "Update");
        mav.addObject("userdetails", userDetails);

        //Get All Available user sections
        /*List<siteSections> sections = userManager.getSections();
        mav.addObject("sections", sections);

        //Return the sections for the clicked user
        List<userAccess> userSections = userManager.getuserSections(userId);
        List<Integer> userSectionList = new ArrayList<Integer>();

        for (int i = 0; i < userSections.size(); i++) {
            userSectionList.add(userSections.get(i).getFeatureId());
        }

        userDetails.setsectionList(userSectionList);*/

        return mav;

    }

    /**
     * login as response body *
     */
    @RequestMapping(value = "/{cleanURL}/loginAs", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView loginAs(@PathVariable String cleanURL, HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/users/login");

        //Set the id of the organization for the new provider
        List<Organization> organization = organizationManager.getOrganizationByName(cleanURL);
        Organization orgDetails = organization.get(0);
        
        String loginAsUser = request.getParameter("loginAsUser");
        List<User> usersList = userManager.getUsersByStatuRolesAndOrg(true, Arrays.asList(1), Arrays.asList(orgDetails.getId()), true);
        mav.addObject("usersList", usersList);
        mav.addObject("loginAsUser", loginAsUser);
        return mav;
    }

    /**
     * login as post check - response body *
     */
    @RequestMapping(value = "/{cleanURL}/loginAsCheck", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView checkLoginAsPW(@PathVariable String cleanURL, HttpServletRequest request,
            Authentication authentication) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/users/login");

        User user = userManager.getUserByUserName(authentication.getName());
        boolean okToLoginAs = false;

        /**
         * we verify existing password *
         */
        if (user.getRoleId() == 1 || user.getRoleId() == 4) {
            try {
                okToLoginAs = userManager.authenticate(request.getParameter("j_password"), user.getEncryptedPw(), user.getRandomSalt());
            } catch (Exception ex) {
                okToLoginAs = false;
            }
        }

        if (okToLoginAs) {
            mav.addObject("msg", "pwmatched");
        } else {
            //Set the id of the organization for the new provider
            List<Organization> organization = organizationManager.getOrganizationByName(cleanURL);
            Organization orgDetails = organization.get(0);

            String loginAsUser = request.getParameter("loginAsUser");
            List<User> usersList = userManager.getUsersByStatuRolesAndOrg(true, Arrays.asList(1), Arrays.asList(orgDetails.getId()), true);
            mav.addObject("usersList", usersList);
            mav.addObject("loginAsUser", loginAsUser);

        }
        return mav;
    }

    /**
     * The '/getRegistryEntities.do' GET request will return a list of Tier 2 entities
     * for the passed in registry
     *
     * @param registryId
     *
     * @return The function will return a list of program upload types.
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = {"/{cleanURL}/getRegistryEntities.do", "/getRegistryEntities.do"}, method = RequestMethod.GET)
    public @ResponseBody
    List<programHierarchyDetails> getRegistryEntities(@RequestParam(value = "registryId", required = true) String registryId) throws Exception {
	
	List<programOrgHierarchy> entities = hierarchymanager.getProgramOrgHierarchy(Integer.parseInt(registryId));
	List<programHierarchyDetails> registryEntities = new ArrayList<programHierarchyDetails>();
	
	if(entities != null) {
	    if(entities.size() > 0) {
		List<programHierarchyDetails> tier2Entities = hierarchymanager.getProgramHierarchyItems(entities.get(1).getId());
		
		if(tier2Entities != null) {
		    if(tier2Entities.size() > 0) {
			for(programHierarchyDetails tier2Entity : tier2Entities) {
			    if(tier2Entity.isStatus() == true) {
				registryEntities.add(tier2Entity);
			    }
			}
		    }
		}
	    }
	}
	
        return registryEntities;
    }


}
