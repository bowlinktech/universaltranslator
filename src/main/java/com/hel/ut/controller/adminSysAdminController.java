package com.hel.ut.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import com.hel.ut.reference.TestCategoryList;
import com.hel.ut.reference.USStateList;
import com.hel.ut.reference.ProcessCategoryList;
import com.hel.ut.service.configurationManager;
import com.hel.ut.service.sysAdminManager;
import com.hel.ut.service.userManager;
import com.hel.ut.model.Macros;
import com.hel.ut.model.MoveFilesLog;
import com.hel.ut.model.User;
import com.hel.ut.model.UserActivity;
import com.hel.ut.model.custom.LogoInfo;
import com.hel.ut.model.custom.LookUpTable;
import com.hel.ut.model.custom.TableData;
import com.hel.ut.model.lutables.lu_ProcessStatus;
import com.hel.ut.model.userLogin;

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

@Controller
@RequestMapping("/administrator/sysadmin")
public class adminSysAdminController {

    @Autowired
    private sysAdminManager sysAdminManager;

    @Autowired
    private userManager usermanager;

    @Autowired
    private configurationManager configurationmanager;

    @Autowired
    private ServletContext servletContext;


    /**
     * This shows a dashboard with info for sysadmin components. *
     * @param request
     * @param response
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView dashboard(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/dashboard");
        /**
         * set totals*
         */
        Long totalMacroRows = sysAdminManager.findTotalMacroRows();
        Long totalHL7Entries = sysAdminManager.findtotalHL7Entries();
        Long totalUsers = sysAdminManager.findTotalUsers();
        Integer filePaths = sysAdminManager.getMoveFilesLog(1).size();
	
	//Get a list of system admin users
	List<User> systemAdmins = usermanager.getAllUsersByOrganization(1);
	mav.addObject("systemAdmins", systemAdmins.size());
        
        mav.addObject("totalMacroRows", totalMacroRows);
        mav.addObject("totalHL7Entries", totalHL7Entries);
        mav.addObject("totalUsers", totalUsers);
        mav.addObject("filePaths", filePaths);
        
        return mav;
    }
    
    /**
     * MACROS *
     */
    @RequestMapping(value = "/macros", method = RequestMethod.GET)
    public ModelAndView listMacros() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/macros");

        //Return a list of available macros
        List<Macros> macroList = sysAdminManager.getMarcoList("%");
        mav.addObject("macroList", macroList);

        return mav;
    }

    @RequestMapping(value = "/macros/delete", method = RequestMethod.GET)
    public ModelAndView deleteMacro(@RequestParam(value = "i", required = true) int macroId,
            RedirectAttributes redirectAttr) throws Exception {

        boolean suceeded = sysAdminManager.deleteMacro(macroId);
        String returnMessage = "deleted";

        if (!suceeded) {
            returnMessage = "notDeleted";
        }
        //This variable will be used to display the message on the details form
        redirectAttr.addFlashAttribute("savedStatus", returnMessage);

        ModelAndView mav = new ModelAndView(new RedirectView("../macros?msg=" + returnMessage));
        return mav;
    }

    /**
     * The '/{urlId}/data.create' GET request will be used to create a new data for selected table
     *
     */
    @RequestMapping(value = "/macros/create", method = RequestMethod.GET)
    public ModelAndView newMacroForm() throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/macro/details");

        //create a macro
        Macros macro = new Macros();
        macro.setId(0);
        mav.addObject("macroDetails", macro);
        mav.addObject("btnValue", "Create");
        return mav;
    }

    @RequestMapping(value = "/macros/create", method = RequestMethod.POST)
    public ModelAndView createMacro(
            @Valid @ModelAttribute(value = "macroDetails") Macros macroDetails,
            BindingResult result) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/macro/details");
        /**
         * check for error *
         */
        if (result.hasErrors()) {
            mav.addObject("macroDetails", macroDetails);
            mav.addObject("btnValue", "Create");
            return mav;
        }
        //now we save
        sysAdminManager.createMacro(macroDetails);
        mav.addObject("success", "macroCreated");
        mav.addObject("btnValue", "Update");
        return mav;
    }

    /**
     * The '/macros/view' GET request will be used to create a new data for selected table
     *
     */
    @RequestMapping(value = "/macros/view", method = RequestMethod.GET)
    public ModelAndView viewMacroDetails(@RequestParam(value = "i", required = false) Integer i) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/macro/details");
        //get macro info here
        Macros macroDetails = configurationmanager.getMacroById(i);
        mav.addObject("macroDetails", macroDetails);
        mav.addObject("btnValue", "Update");
        return mav;
    }

    /**
     * UPDATE macros *
     */
    @RequestMapping(value = "/macros/update", method = RequestMethod.POST)
    public ModelAndView updateMacro(
            @Valid @ModelAttribute(value = "macroDetails") Macros macroDetails,
            BindingResult result) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/macro/details");

        if (result.hasErrors()) {
            mav.addObject("macroDetails", macroDetails);
            mav.addObject("btnValue", "Update");
            return mav;
        }

        //get macro info here
        boolean updated = sysAdminManager.updateMacro(macroDetails);

        if (updated) {
            mav.addObject("success", "macroUpdated");
        } else {
            mav.addObject("success", "Error!");
        }
        mav.addObject("macroDetails", macroDetails);
        mav.addObject("btnValue", "Update");
        return mav;
    }

    /**
     * END MACROS *
     */
   

    /**
     * The '/{urlId}/data.create' GET request will be used to create a new data for selected table
     *
     */
    @RequestMapping(value = "/data/std/{urlId}/create", method = RequestMethod.GET)
    public ModelAndView newTableDataForm(@PathVariable String urlId) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/data/std/details");

        LookUpTable tableInfo = sysAdminManager.getTableInfo(urlId);
        //create a table data
        TableData tableData = new TableData();
        tableData.setId(0);
        tableData.setUrlId(urlId);
        mav.addObject("stdForm", "stdForm");
        mav.addObject("tableDataDetails", tableData);
        mav.addObject("tableInfo", tableInfo);
        mav.addObject("objectType", "tableData");
        mav.addObject("formId", "tabledataform");
        mav.addObject("btnValue", "Create");
        mav.addObject("submitBtnValue", "Create");
        return mav;
    }

    /**
     * The '/{urlId}/create' POST request will handle submitting the new provider.
     *
     * @param tableDataForm	The object containing the tableData form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     * @Objects	(1) The object containing all the information for the new data item (2) We will extract table from web address
     * @throws Exception
     */
    @RequestMapping(value = "/data/std/create", method = RequestMethod.POST)
    public ModelAndView createTableData(
            @Valid @ModelAttribute(value = "tableDataDetails") TableData tableData,
            BindingResult result) throws Exception {

        LookUpTable tableInfo = sysAdminManager.getTableInfo(tableData.getUrlId());

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/data/std/details");
        mav.addObject("objectType", "tableData");
        mav.addObject("formId", "tabledataform");
        /**
         * check for error *
         */
        if (result.hasErrors()) {
            mav.addObject("stdForm", "stdForm");
            mav.addObject("tableInfo", tableInfo);
            mav.addObject("btnValue", "Create");
            mav.addObject("submitBtnValue", "Create");
            return mav;
        }

        //now we save
        sysAdminManager.createTableDataHibernate(tableData, tableInfo.getUtTableName());
        mav.addObject("success", "dataCreated");
        mav.addObject("btnValue", "Update");
        mav.addObject("submitBtnValue", "Update");
        return mav;
    }

    /**
     * The '/data/std/{urlId}/tableData?i=' GET request will be used to create a new data for selected table
     *
     */
    @RequestMapping(value = "/data/std/{urlId}/tableData", method = RequestMethod.GET)
    public ModelAndView viewTableData(@PathVariable String urlId,
            @RequestParam(value = "i", required = false) Integer i) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/data/std/details");

        LookUpTable tableInfo = sysAdminManager.getTableInfo(urlId);
        TableData tableData = sysAdminManager.getTableData(i, tableInfo.getUtTableName());
        tableData.setUrlId(urlId);
        mav.addObject("tableDataDetails", tableData);
        mav.addObject("tableInfo", tableInfo);
        mav.addObject("objectType", "tableData");
        mav.addObject("stdForm", "stdForm");
        mav.addObject("formId", "tabledataform");
        mav.addObject("btnValue", "Update");
        mav.addObject("submitBtnValue", "Update");
        return mav;
    }

    /**
     * The '/data/std/update' POST request will be used to update a look up data item
     *
     */
    @RequestMapping(value = "/data/std/update", method = RequestMethod.POST)
    public ModelAndView updateTableData(
            @Valid @ModelAttribute(value = "tableDataDetails") TableData tableData,
            BindingResult result)
            throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/data/std/details");
        mav.addObject("objectType", "tableData");
        mav.addObject("formId", "tabledataform");
        mav.addObject("submitBtnValue", "Update");
        mav.addObject("btnValue", "Update");

        LookUpTable tableInfo = sysAdminManager.getTableInfo(tableData.getUrlId());

        /**
         * check for error *
         */
        if (result.hasErrors()) {
            mav.addObject("tableInfo", tableInfo);
            mav.addObject("stdForm", "stdForm");
            return mav;
        }

        // now we update
        boolean updated = sysAdminManager.updateTableData(tableData, tableInfo.getUtTableName());

        // This variable will be used to display the message on the details
        if (updated) {
            mav.addObject("success", "dataUpdated");
        } else {
            mav.addObject("success", "- There is an error.");
        }

        return mav;
    }

    /**
     * here we have the views for the look up tables that do not have the standard 7 columns, these have models in UT
     *
     */
    @RequestMapping(value = "/data/nstd/{urlId}", method = RequestMethod.GET)
    public ModelAndView listTableDataNStd(@PathVariable String urlId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/data/std");

        /**
         * we query data for look up table, this view returns all data from table, hence the search term will be %
         *
         */
        LookUpTable tableInfo = sysAdminManager.getTableInfo(urlId);
        List<TableData> dataList = sysAdminManager.getDataList(tableInfo.getUtTableName(), "%");
        mav.addObject("dataList", dataList);
        mav.addObject("tableInfo", tableInfo);

        mav.addObject("goToURL", tableInfo.getUtTableName());
        mav.addObject("urlIdInfo", tableInfo.getUrlId());
        return mav;
    }

    /**
     * End of Tests*
     */
    /**
     * Start of ProcessStatus *
     */
    @RequestMapping(value = "/data/nstd/lu_ProcessStatus/create", method = RequestMethod.GET)
    public ModelAndView newProcessStatusForm() throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/data/std/details");

        //create the object
        lu_ProcessStatus lu = new lu_ProcessStatus();
        lu.setId(0);
        mav.addObject("tableDataDetails", lu);
        //Get a list of process status categories
        ProcessCategoryList categoryList = new ProcessCategoryList();
        mav.addObject("categoryList", categoryList.getCategories());
        mav.addObject("objectType", "lu_ProcessStatus");
        mav.addObject("formId", "tabledataform");
        mav.addObject("btnValue", "lu_ProcessStatus/create");
        mav.addObject("submitBtnValue", "Create");
        return mav;
    }

    @RequestMapping(value = "/data/nstd/lu_processstatus/create", method = RequestMethod.POST)
    public ModelAndView createProcessStatus(
            @Valid @ModelAttribute(value = "tableDataDetails") lu_ProcessStatus lu,
            BindingResult result) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/data/std/details");
        mav.addObject("objectType", "lu_ProcessStatus");
        //Get a list of process status categories
        ProcessCategoryList categoryList = new ProcessCategoryList();
        mav.addObject("categoryList", categoryList.getCategories());
        mav.addObject("formId", "tabledataform");
        // check for error
        if (result.hasErrors()) {
            mav.addObject("btnValue", "lu_ProcessStatus/create");
            mav.addObject("submitBtnValue", "Create");
            return mav;
        }

        //now we save
        sysAdminManager.createProcessStatus(lu);
        mav.addObject("success", "dataCreated");
        mav.addObject("btnValue", "lu_ProcessStatus/update");
        mav.addObject("submitBtnValue", "Update");
        return mav;
    }

    @RequestMapping(value = "/data/nstd/lu_ProcessStatus/tableData", method = RequestMethod.GET)
    public ModelAndView viewProcessStatus(@RequestParam(value = "i", required = false) Integer i)
            throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/data/std/details");

        lu_ProcessStatus lu = sysAdminManager.getProcessStatusById(i);
        mav.addObject("tableDataDetails", lu);
        ProcessCategoryList categoryList = new ProcessCategoryList();
        mav.addObject("categoryList", categoryList.getCategories());
        mav.addObject("objectType", "lu_ProcessStatus");
        mav.addObject("formId", "tabledataform");
        mav.addObject("btnValue", "lu_ProcessStatus/update");
        mav.addObject("submitBtnValue", "Update");
        return mav;
    }

    @RequestMapping(value = "/data/nstd/lu_processstatus/update", method = RequestMethod.POST)
    public ModelAndView updateProcessStatus(
            @Valid @ModelAttribute(value = "tableDataDetails") lu_ProcessStatus lu,
            BindingResult result) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/data/std/details");
        mav.addObject("objectType", "lu_ProcessStatus");
        mav.addObject("formId", "tabledataform");
        ProcessCategoryList categoryList = new ProcessCategoryList();
        mav.addObject("categoryList", categoryList.getCategories());
        /**
         * check for error *
         */
        if (result.hasErrors()) {
            mav.addObject("btnValue", "lu_ProcessStatus/update");
            mav.addObject("submitBtnValue", "Update");
            return mav;
        }

        //now we save
        sysAdminManager.updateProcessStatus(lu);
        mav.addObject("success", "dataUpdated");
        mav.addObject("btnValue", "lu_ProcessStatus/update");
        mav.addObject("submitBtnValue", "Update");
        return mav;
    }

    /**
     * End of ProcessStatus*
     */
    /**
     * Logos
     */
    @RequestMapping(value = "/logos", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView updateLogosForm(HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/logos");
        LogoInfo logoDetails = sysAdminManager.getLogoInfo();
        /**
         * we had a logo from before *
         */
        if (logoDetails.getBackEndLogoName().indexOf("backEndLogo") == 0) {
            sysAdminManager.copyBELogo(request, logoDetails);
        }
        if (logoDetails.getFrontEndLogoName().indexOf("frontEndLogo") == 0) {
            sysAdminManager.copyFELogo(request, logoDetails);
        }
        mav.addObject("btnValue", "Update");
        mav.addObject("logoDetails", logoDetails);
        return mav;
    }

    @RequestMapping(value = "/logos", method = RequestMethod.POST)
    public ModelAndView updateLogos(@ModelAttribute(value = "logoDetails") LogoInfo logoDetails,
            RedirectAttributes redirectAttr) throws Exception {

        sysAdminManager.updateLogoInfo(logoDetails);
        ModelAndView mav = new ModelAndView(new RedirectView("logos?msg=updated"));
        redirectAttr.addFlashAttribute("savedStatus", "updated");
        return mav;
    }

    /**
     * modify admin profile *
     * @param adminId
     * @param request
     * @param authentication
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/adminInfo", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView displayAdminInfo(@RequestParam(value = "adminId", required = false) Integer adminId, HttpServletRequest request, Authentication authentication) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/adminInfo/profile");
	
	User userDetails;
	
	if(adminId == 0) {
	    userDetails = new User();
	    userDetails.setOrgId(1);
	    mav.addObject("btnValue", "Create");
	}
	else {
	   userDetails = usermanager.getUserById(adminId);
	   mav.addObject("btnValue", "Update");
	}
	
        mav.addObject("userdetails", userDetails);
        return mav;
    }

    @RequestMapping(value = "/adminInfo", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView updateAdminInfo(HttpServletRequest request, @ModelAttribute(value = "userdetails") User userdetails,
            Authentication authentication, BindingResult result) throws Exception {

        ModelAndView mav = new ModelAndView();

        mav.setViewName("/administrator/sysadmin/adminInfo/profile");

	boolean usernameNotTaken = true;
	
	//Check for another system admin with the same username
	try {
	    List<User> systemAdmins = usermanager.getUserByTypeByOrganization(1);
	    
	    if(systemAdmins != null) {
		for(User systemAdmin : systemAdmins) {
		    if((systemAdmin.getId() != userdetails.getId()) && (systemAdmin.getUsername().equals(userdetails.getUsername()))) {
			usernameNotTaken = false;
		    }
		}
	    }
	}
	catch (Exception ex) {
	    usernameNotTaken = false;
	}

        if (usernameNotTaken) {
	  
	   if (!request.getParameter("newPassword").trim().equalsIgnoreCase("")) {
	       userdetails.setRandomSalt(usermanager.generateSalt());
	       userdetails.setEncryptedPw(usermanager.getEncryptedPassword(request.getParameter("newPassword"), userdetails.getRandomSalt()));
	   }
	   try {
	       if(userdetails.getId() > 0) {
		    usermanager.updateUserOnly(userdetails);
	       }
	       else {
		    usermanager.createUser(userdetails);
	       }
	   } catch (Exception ex) {
	       ex.printStackTrace();
	   }
        }

        if (!usernameNotTaken) {
	    mav.addObject("failed", "There is another system administrator with the same username.  Profile is not updated.");
	    if(userdetails.getId() > 0) {
		mav.addObject("btnValue", "Update");
	    }
	    else {
		mav.addObject("btnValue", "Create");
	    }
	}
	else {
            mav.addObject("userdetails", userdetails);
            mav.addObject("success", "Profile is updated");
            mav.addObject("btnValue", "Update");
        }

        return mav;
    }

    /**
     * login as portion *
     */
    @RequestMapping(value = "/loginAs", method = RequestMethod.GET)
    public ModelAndView loginAs() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/loginAs");

        //get all active users
        List<User> usersList = usermanager.getUsersByStatuRolesAndOrg(true, Arrays.asList(1), Arrays.asList(1), false);
        mav.addObject("usersList", usersList);

        return mav;
    }

    @RequestMapping(value = "/loginAs", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView checkAdminPW(HttpServletRequest request, Authentication authentication) throws Exception {

        ModelAndView mav = new ModelAndView();
        User user = usermanager.getUserByUserName(authentication.getName());

        mav.setViewName("/administrator/sysadmin/loginAs");

        boolean okToLoginAs = false;

        /**
         * we verify existing password *
         */
        if (user.getRoleId() == 1 || user.getRoleId() == 4) {
            try {
                okToLoginAs = usermanager.authenticate(request.getParameter("j_password"), user.getEncryptedPw(), user.getRandomSalt());
            } catch (Exception ex) {
                okToLoginAs = false;
            }
        }

        if (!okToLoginAs) {
            mav.addObject("msg", "Your credentials are invalid.");
        } else {
            mav.addObject("msg", "pwmatched");
        }

        return mav;
    }

    @RequestMapping(value = "/getLog", method = {RequestMethod.GET})
    public void getLog(HttpSession session, HttpServletResponse response, Authentication authentication) throws Exception {
    	
    	User userInfo = usermanager.getUserByUserName(authentication.getName());
    	//log user activity
 	   UserActivity ua = new UserActivity();
 	   ua.setUserId(userInfo.getId());
 	   ua.setAccessMethod("GET");
 	   ua.setPageAccess("/getLog");
 	   ua.setActivity("Download Tomcat Log");
 	   usermanager.insertUserLog(ua);
 	   
    	File logFileDir = new File(System.getProperty("catalina.home"), "logs");
        File logFile = new File(logFileDir, "catalina.out");
        // get your file as InputStream
	   InputStream is = new FileInputStream(logFile);
	   String mimeType = "application/octet-stream";
	            		  response.setContentType(mimeType);
	            		  response.setHeader("Content-Transfer-Encoding", "binary");
	                      response.setHeader("Content-Disposition", "attachment;filename=catalina.out");
	                      org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
	                      response.flushBuffer();
	            	      is.close();
	   
} 
    
    @RequestMapping(value = "/moveFilePaths", method = RequestMethod.GET)
    public ModelAndView moveFilePaths(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session, RedirectAttributes redirectAttr) throws Exception {

    	ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/moveFilePaths");
        //we get list of programs
        List<MoveFilesLog> pathList = sysAdminManager.getMoveFilesLog(1);
        mav.addObject("pathList", pathList);
        
        return mav;
    }
    
    @RequestMapping(value = "/moveFilePaths", method = RequestMethod.POST)
    @ResponseBody
    public String associateEntity(@RequestParam(value = "pathId", required = true) Integer pathId) throws Exception {
        
    	MoveFilesLog moveFilesLog = new MoveFilesLog();
    	moveFilesLog.setId(pathId);
    	sysAdminManager.deleteMoveFilesLog(moveFilesLog);
        
        return "deleted";
    }   
    
    
    /**
     * systemAdmins *
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/systemAdmins", method = RequestMethod.GET)
    public ModelAndView listsystemAdmins() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/systemadmins");

	List<User> systemAdmins = usermanager.getUsersByOrganizationWithLogins(1);
	mav.addObject("systemAdmins", systemAdmins);

        return mav;
    }

 
    /**
     * modify admin profile *
     * @param adminId
     * @param request
     * @param authentication
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/systemAdminLogins", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView displayAdminLogins(@RequestParam(value = "adminId", required = false) Integer adminId, HttpServletRequest request, Authentication authentication) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/adminInfo/logins");
	
	List<userLogin> systemAdminLogins = usermanager.getUserLogins(adminId);
	
        mav.addObject("systemAdminLogins", systemAdminLogins);
        return mav;
    }
}
