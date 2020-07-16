/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hel.ut.model.WSMessagesIn;
import com.hel.ut.model.activityReportList;
import com.hel.ut.model.Organization;
import com.hel.ut.model.RestAPIMessagesIn;
import com.hel.ut.model.RestAPIMessagesOut;
import com.hel.ut.model.TransportMethod;
import com.hel.ut.model.batchDownloadDroppedValues;
import com.hel.ut.model.utUser;
import com.hel.ut.model.utUserActivity;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchUploadDroppedValues;
import com.hel.ut.model.batchUploads;
import com.hel.ut.model.batchdownloadactivity;
import com.hel.ut.model.batchuploadactivity;
import com.hel.ut.model.configurationFTPFields;
import com.hel.ut.model.configurationFileDropFields;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.wsMessagesOut;
import com.hel.ut.model.custom.TableData;
import com.hel.ut.model.custom.batchErrorSummary;
import com.hel.ut.model.custom.searchParameters;
import com.hel.ut.model.directmessagesin;
import com.hel.ut.model.directmessagesout;
import com.hel.ut.model.fieldSelectOptions;
import com.hel.ut.model.lutables.lu_ProcessStatus;
import com.hel.ut.model.mailMessage;
import com.hel.ut.model.referralActivityExports;
import com.hel.ut.model.systemSummary;
import com.hel.ut.model.transactionOutRecords;
import com.hel.ut.model.transactionRecords;
import com.hel.ut.model.watchlistEntry;
import com.hel.ut.restAPI.directManager;
import com.hel.ut.restAPI.restfulManager;
import com.hel.ut.security.decryptObject;
import com.hel.ut.security.encryptObject;
import com.hel.ut.service.emailMessageManager;
import com.hel.ut.service.fileManager;
import com.hel.ut.service.messageTypeManager;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.sysAdminManager;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.transactionOutManager;
import com.hel.ut.service.userManager;
import com.hel.ut.webServices.WSManager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;
import javax.annotation.Resource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.FileCopyUtils;

/**
 *
 * @author chadmccued
 */
@Controller
@RequestMapping("/administrator/processing-activity")
public class adminProcessingActivity {
    
    @Autowired
    private transactionInManager transactionInManager;

    @Autowired
    private transactionOutManager transactionOutManager;

    @Autowired
    private sysAdminManager sysAdminManager;

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private utConfigurationTransportManager configurationTransportManager;

    @Autowired
    private messageTypeManager messagetypemanager;

    @Autowired
    private utConfigurationManager configurationManager;

    @Autowired
    private userManager usermanager;

    @Autowired
    private fileManager filemanager;

    @Autowired
    private WSManager wsmanager;
    
    @Autowired
    private restfulManager restfulmanager;
    
    @Autowired
    private directManager directmanager;
    
    @Autowired
    private emailMessageManager emailMessageManager;

    private String topSecret = "Hello123JavaTomcatMysqlDPHSystem2016";

    /**
     * The private maxResults variable will hold the number of results to show per list page.
     */
    private static int maxResults = 10;
    
    @Resource(name = "myProps")
    private Properties myProps;
    
    /**
     *
     * @param session
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/activityReport", method = RequestMethod.GET)
    public ModelAndView activityReport(HttpSession session) throws Exception {
        
        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/activityReport");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        if ("".equals(searchParameters.getsection()) || !"activityReport".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("activityReport");
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
        }

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);

        /* Get the list of batches for the passed in dates */
        List<Integer> batchIds = transactionInManager.getBatchesForReport(fromDate, toDate);
	
        /* Get totals */
        Integer totalMessagesReceived = 0;
	if(batchIds != null) {
	    if(!batchIds.isEmpty()) {
		totalMessagesReceived = batchIds.size();
	    }
	}
        mav.addObject("totalMessagesReceived", totalMessagesReceived);
	
        BigInteger totalMessagesDelivered = transactionInManager.getMessagesSent(fromDate, toDate);
        mav.addObject("totalMessagesDelivered", totalMessagesDelivered);

        BigInteger totalRejected = transactionInManager.getRejectedCount(fromDate, toDate);
        mav.addObject("totalRejected", totalRejected);
	
	BigInteger totalRejectedReceived = transactionInManager.getRejectedReceivedCount(fromDate, toDate);
        mav.addObject("totalRejectedReceived", totalRejectedReceived);
	
	BigInteger totalDeliveredRejected = transactionOutManager.getRejectedCount(fromDate, toDate);
        mav.addObject("totalDeliveredRejected", totalDeliveredRejected);

        /* Get Referral List */
        List<activityReportList> referralList = transactionInManager.getReferralList(fromDate, toDate);
	
        mav.addObject("referralList", referralList);

        return mav;

    }

    /**
     *
     * @param fromDate
     * @param toDate
     * @param request
     * @param session
     * @param response
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/activityReport", method = RequestMethod.POST)
    public ModelAndView activityReport(@RequestParam Date fromDate, @RequestParam Date toDate, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/activityReport");

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("activityReport");
	searchParameters.setsearchTerm("");

        /* Get the list of batches for the passed in dates */
        List<Integer> batchIds = transactionInManager.getBatchesForReport(fromDate, toDate);
	
	/* Get totals */
        /* Get totals */
        Integer totalMessagesReceived = 0;
	if(batchIds != null) {
	    if(!batchIds.isEmpty()) {
		totalMessagesReceived = batchIds.size();
	    }
	}
        mav.addObject("totalMessagesReceived", totalMessagesReceived);

        BigInteger totalMessagesDelivered = transactionInManager.getMessagesSent(fromDate, toDate);
        mav.addObject("totalMessagesDelivered", totalMessagesDelivered);

        BigInteger totalRejected = transactionInManager.getRejectedCount(fromDate, toDate);
        mav.addObject("totalRejected", totalRejected);
	
	BigInteger totalRejectedReceived = transactionInManager.getRejectedReceivedCount(fromDate, toDate);
        mav.addObject("totalRejectedReceived", totalRejectedReceived);
	
	BigInteger totalDeliveredRejected = transactionOutManager.getRejectedCount(fromDate, toDate);
        mav.addObject("totalDeliveredRejected", totalDeliveredRejected);

        /* Get Referral List */
        List<activityReportList> referralList = transactionInManager.getReferralList(fromDate, toDate);
        mav.addObject("referralList", referralList);

        return mav;
    }

    /**
     * The '/inbound' GET request will serve up the existing list of generated referrals and feedback reports
     *
     * @param pathVariables
     * @param session
     * @return The list of inbound batch list
     *
     * @Objects	(1) An object containing all the found batches
     *
     * @throws Exception
     */
    @RequestMapping(value={ "/inbound", "/inbound/{batchName}" }, method = RequestMethod.GET)
    public ModelAndView listInBoundBatches(@PathVariable Map<String, String> pathVariables, HttpSession session) throws Exception {

        utUser userInfo = (utUser) session.getAttribute("userDetails");
	
	int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/inbound");
	
	String searchTerm = "";
	
        if ("".equals(searchParameters.getsection()) || !"inbound".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("inbound");
	    searchParameters.setsearchTerm("");
	    session.setAttribute("searchParameters", searchParameters);
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
	    searchTerm = searchParameters.getsearchTerm().trim();
	    
	    searchParameters.setsearchTerm("");
	    session.setAttribute("searchParameters", searchParameters);
        }
	
	
	mav.addObject("searchFilter", searchTerm);
        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);
	mav.addObject("batchName", pathVariables.get("batchName"));
	mav.addObject("userRole", userInfo.getRoleId());

        /* Get system inbound summary */
        systemSummary summaryDetails = transactionInManager.generateSystemInboundSummary();
        mav.addObject("summaryDetails", summaryDetails);

        return mav;
    }
    
    @RequestMapping(value = "/ajax/getBatchUploads", method = RequestMethod.GET)
    @ResponseBody
    public String getBatchUploads(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam Date fromDate, @RequestParam Date toDate, @RequestParam String batchName) throws Exception {
	
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
	
	//Retrieve search parameters from session 
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("inbound");
	
	if(!"".equals(batchName)) {
	    searchTerm = batchName;
	    fromDate = null;
	    toDate = null;
	}
	
        // Get all ws in 
        List<batchUploads> batchUploadList = transactionInManager.getAllUploadBatchesPaged(fromDate, toDate,iDisplayStart, iDisplayLength, searchTerm, sortColumnName, sortDirection);
	
	if(batchUploadList.isEmpty()) {
	    totalRecords = 0;
	}
	else {
	    totalRecords = batchUploadList.get(0).getTotalMessages();
	}
	
	jsonResponse.addProperty("sEcho", sEcho);
        jsonResponse.addProperty("iTotalRecords", totalRecords);
        jsonResponse.addProperty("iTotalDisplayRecords", totalRecords);
        jsonResponse.add("aaData", gson.toJsonTree(batchUploadList));
	
        return jsonResponse.toString();
    }
    
    /**
     * The '/outbound' GET request will serve up the existing list of generated referrals and feedback reports to for the target
     *
     * @param pathVariables
     * @param session
     * @return The list of inbound batch list
     *
     * @Objects	(1) An object containing all the found batches
     *
     * @throws Exception
     */
    @RequestMapping(value={ "/outbound", "/outbound/{batchName}" }, method = RequestMethod.GET)
    public ModelAndView listOutBoundBatches(@PathVariable Map<String, String> pathVariables, HttpSession session) throws Exception {
	
	utUser userInfo = (utUser) session.getAttribute("userDetails");
	
	int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/outbound");
	
	String searchTerm = "";
	
        if ("".equals(searchParameters.getsection()) || !"outbound".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("outbound");
	    searchParameters.setsearchTerm("");
	    session.setAttribute("searchParameters", searchParameters);
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
	    searchTerm = searchParameters.getsearchTerm().trim();
	    
	    searchParameters.setsearchTerm("");
	    session.setAttribute("searchParameters", searchParameters);
        }
	
	mav.addObject("searchFilter", searchTerm);
        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);
	mav.addObject("batchName", pathVariables.get("batchName"));
	mav.addObject("userRole", userInfo.getRoleId());

        /* Get system oubound summary */
        systemSummary summaryDetails = transactionOutManager.generateSystemOutboundSummary();
        mav.addObject("summaryDetails", summaryDetails);

        return mav;

    }

    @RequestMapping(value = "/ajax/getBatchDownloads", method = RequestMethod.GET)
    @ResponseBody
    public String getBatchDownloads(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam Date fromDate, @RequestParam Date toDate, @RequestParam String batchName) throws Exception {
	
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
	
	//Retrieve search parameters from session 
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("outbound");
	
	if(!"".equals(batchName)) {
	    searchTerm = batchName;
	    fromDate = null;
	    toDate = null;
	}
	
        // Get all ws in 
        List<batchDownloads> batchDownloadList = transactionOutManager.getAllSentBatchesPaged(fromDate, toDate,iDisplayStart, iDisplayLength, searchTerm, sortColumnName, sortDirection);
	
	if(batchDownloadList.isEmpty()) {
	    totalRecords = 0;
	}
	else {
	    totalRecords = batchDownloadList.get(0).getTotalMessages();
	}
	
	jsonResponse.addProperty("sEcho", sEcho);
        jsonResponse.addProperty("iTotalRecords", totalRecords);
        jsonResponse.addProperty("iTotalDisplayRecords", totalRecords);
        jsonResponse.add("aaData", gson.toJsonTree(batchDownloadList));
	
        return jsonResponse.toString();
    }


    /**
     * The '/viewStatus{statusId}' function will return the details of the selected status. The results will be displayed in the overlay.
     *
     * @Param	statusId This will hold the id of the selected status
     *
     * @Return	This function will return the status details view.
     */
    @RequestMapping(value = "/viewStatus{statusId}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView viewStatus(@PathVariable int statusId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/statusDetails");

        /* Get the details of the selected status */
        lu_ProcessStatus processStatus = sysAdminManager.getProcessStatusById(statusId);
        mav.addObject("statusDetails", processStatus);

        return mav;
    }

    /**
     * @param filter START for start date of month e.g. Nov 01, 2013 END for end date of month e.g. Nov 30, 2013
     * @return
     */
    public Date getMonthDate(String filter) {

        String MM_DD_YYYY = "yyyy-mm-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(MM_DD_YYYY);
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        sdf.format(GregorianCalendar.getInstance().getTime());

        Calendar cal = GregorianCalendar.getInstance();
        int date = cal.getActualMinimum(Calendar.DATE);
	
	if("START-MONTH".equals(filter)) {
	    cal.set(Calendar.DAY_OF_MONTH, 1);
	}
	else if("END-MONTH".equals(filter)) {
	    cal.add(Calendar.MONTH, 1);  
	    cal.set(Calendar.DAY_OF_MONTH, 1);  
	    cal.add(Calendar.DATE, -1);  
	}
	else if ("END".equalsIgnoreCase(filter)) {
            date = cal.getActualMaximum(Calendar.DATE);
            cal.set(Calendar.DATE, date);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 0);
        } 
	else if ("START-TODAY".equalsIgnoreCase(filter)) {
	   cal.set(Calendar.HOUR_OF_DAY, 0); // same for minutes and seconds
        } 
	else if ("END-TODAY".equalsIgnoreCase(filter)) {
	   cal.set(Calendar.HOUR_OF_DAY, 0); // same for minutes and seconds
        } 
	else if ("START-WEEK".equalsIgnoreCase(filter)) {
	   cal.set(Calendar.DAY_OF_WEEK,cal.getActualMinimum(Calendar.DAY_OF_WEEK));
        } 
	else if ("END-WEEK".equalsIgnoreCase(filter)) {
	   cal.set(Calendar.DAY_OF_WEEK,cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        }
	else if(filter.contains("LAST30")) {
	    Date today = new Date();
	    cal.setTime(today);
	    cal.add(Calendar.DAY_OF_MONTH, -30);
	}
	else if(filter.contains("END+")) {
	    Integer howmanydaystoadd = Integer.parseInt(filter.split("\\+")[1]);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.add(Calendar.DATE, howmanydaystoadd);
	}
	else {
            cal.set(Calendar.DATE, date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }

        return cal.getTime();
    }


    /**
     * The 'setOrgDetails' function will set the field values to the passed in orgId if the organization information wasn't collected with the file upload.
     *
     * @param orgId The organization id to get the details for
     *
     * @return
     */
    public List<transactionRecords> setOrgDetails(int orgId) {

        List<transactionRecords> fields = new ArrayList<transactionRecords>();

        /* Get the organization Details */
        Organization orgDetails = organizationmanager.getOrganizationById(orgId);

        transactionRecords namefield = new transactionRecords();

        namefield.setFieldValue(orgDetails.getOrgName());
        fields.add(namefield);

        transactionRecords addressfield = new transactionRecords();

        addressfield.setFieldValue(orgDetails.getAddress());
        fields.add(addressfield);

        transactionRecords address2field = new transactionRecords();
        address2field.setFieldValue(orgDetails.getAddress2());
        fields.add(address2field);

        transactionRecords cityfield = new transactionRecords();
        cityfield.setFieldValue(orgDetails.getCity());
        fields.add(cityfield);

        transactionRecords statefield = new transactionRecords();
        statefield.setFieldValue(orgDetails.getState());
        fields.add(statefield);

        transactionRecords zipfield = new transactionRecords();
        zipfield.setFieldValue(orgDetails.getPostalCode());
        fields.add(zipfield);

        transactionRecords phonefield = new transactionRecords();
        phonefield.setFieldValue(orgDetails.getPhone());
        fields.add(phonefield);

        transactionRecords faxfield = new transactionRecords();
        faxfield.setFieldValue(orgDetails.getFax());
        fields.add(faxfield);

        return fields;

    }

    /**
     * The 'setInboxFormFields' will create and populate the form field object
     *
     * @param formfields The list of form fields
     * @param records The values of the form fields to populate with.
     *
     * @return This function will return a list of transactionRecords fields with the correct data
     *
     * @throws NoSuchMethodException
     */
    public List<transactionRecords> setInboxFormFields(List<configurationFormFields> formfields, transactionOutRecords records, int configId, boolean readOnly, int transactionInId) throws NoSuchMethodException {

        List<transactionRecords> fields = new ArrayList<transactionRecords>();

        for (configurationFormFields formfield : formfields) {
            transactionRecords field = new transactionRecords();
            field.setfieldNo(formfield.getFieldNo());
            field.setrequired(formfield.getRequired());
            field.setfieldLabel(formfield.getFieldDesc());
            field.setreadOnly(readOnly);
            field.setfieldValue(null);

            /* Get the validation */
            if (formfield.getValidationType() > 1) {
                field.setvalidation(messagetypemanager.getValidationById(formfield.getValidationType()).toString());
            }

            if (records != null) {
                String colName = new StringBuilder().append("f").append(formfield.getFieldNo()).toString();
                try {
                    field.setfieldValue(BeanUtils.getProperty(records, colName));
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(adminProcessingActivity.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(adminProcessingActivity.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 

            if (configId > 0) {
                /* See if any fields have crosswalks associated to it */
                List<fieldSelectOptions> fieldSelectOptions = transactionInManager.getFieldSelectOptions(formfield.getId(), configId);
                field.setfieldSelectOptions(fieldSelectOptions);
            }

            fields.add(field);
        }

        return fields;
    }


    /**
     * The '/{path}/batchActivities/{batchName}' GET request will retrieve a list of user activities that are associated to the clicked batch
     *
     * @param path
     * @param batchName	The name of the batch to retrieve transactions for
     * @return The list of inbound batch user activities
     *
     * @Objects	(1) An object containing all the found user activities
     *
     * @throws Exception
     */
    @RequestMapping(value = "/{path}/batchActivities/{batchName}", method = RequestMethod.GET)
    public ModelAndView listBatchActivities(@PathVariable String path, @PathVariable String batchName) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/batchActivities");
        mav.addObject("page", path);
	
	if("inbound".equals(path) || "invalidIn".equals(path) || "rejected".equals(path)) {
	    // Get the details of the batch
	    batchUploads batchDetails = transactionInManager.getBatchDetailsByBatchName(batchName);

	    if (batchDetails != null) {

		Organization orgDetails = organizationmanager.getOrganizationById(batchDetails.getOrgId());
		batchDetails.setOrgName(orgDetails.getOrgName());

		mav.addObject("batchDetails", batchDetails);

		try {
		    List<batchuploadactivity> batchActivities = transactionInManager.getBatchActivities(batchDetails);
		    mav.addObject("batchActivities", batchActivities);

		} catch (Exception e) {
		    throw new Exception("(Admin) Error occurred in getting batch activities for an inbound batch. batchId: " + batchDetails.getId() + " ERROR: " + e.getMessage(), e);
		}
	    }
	}
	else {
	    batchDownloads batchDetails = transactionOutManager.getBatchDetailsByBatchName(batchName);
	    
	    if (batchDetails != null) {

		Organization orgDetails = organizationmanager.getOrganizationById(batchDetails.getOrgId());
		batchDetails.setOrgName(orgDetails.getOrgName());

		mav.addObject("batchDetails", batchDetails);

		try {
		    List<batchdownloadactivity> batchActivities = transactionOutManager.getBatchActivities(batchDetails);
		    mav.addObject("batchActivities", batchActivities);

		} catch (Exception e) {
		    throw new Exception("(Admin) Error occurred in getting batch activities for an outbound batch. batchId: " + batchDetails.getId() + " ERROR: " + e.getMessage(), e);
		}
	    }
	}

        return mav;
    }

    /**
     * The '/ViewUATransactionList' function will return the list of transaction ids for a batch activity that was too long to display The results will be displayed in the overlay.
     *
     * @param uaId
     * @param type
     * @return 
     * @throws java.lang.Exception 
     * @Param	uaId This will hold the id of the user activity
     * @Param	type 1 = inbound 2 = outbound
     *
     * @Return	This function will return the transactionList for that user activity.
     */
    @RequestMapping(value = "/ViewUATransactionList", method = RequestMethod.GET)
    public ModelAndView viewUATransactionList(@RequestParam(value = "uaId", required = true) Integer uaId,
            @RequestParam(value = "Type", required = true) Integer type)
            throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/transactionList");

        /* Get the details of the selected status */
        utUserActivity userActivity = usermanager.getUAById(uaId);

        /* Get the details of the batch */
        batchUploads batchDetails = new batchUploads();
        if (type == 1) {
            batchDetails = transactionInManager.getBatchDetails(userActivity.getBatchUploadId());
        } else {
            batchDetails = transactionInManager.getBatchDetails(userActivity.getBatchDownloadId());
        }

        mav.addObject("userActivity", userActivity);
        mav.addObject("batchDetails", batchDetails);

        return mav;
    }

    /**
     * The '/inbound/auditReport/{batchName}' GET request will retrieve the audit report that is associated to the clicked batch
     *
     * @param batchName	The name of the batch to retrieve transactions for
     * @return The audit report for the batch
     *
     * @Objects	(1) An object containing all the errored transactions
     *
     * @throws Exception
     */
    @RequestMapping(value = {"/inbound/auditReport/{batchName}", "/invalidIn/auditReport/{batchName}", "/rejected/auditReport/{batchName}"}, method = RequestMethod.GET)
    public ModelAndView viewInboundAuditReport(@PathVariable String batchName, HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/auditReport");
        boolean canCancel = false;
        boolean canReset = false;
        boolean canEdit = false;
        boolean canSend = false;
	boolean showButtons = true;
	
	Integer totalErroredRows = 0;
	
	String page = "inbound";
	if(request.getRequestURL().toString().contains("invalidIn")) {
	    page = "invalid";
	}
	else if(request.getRequestURL().toString().contains("rejected")) {
	    page = "rejected";
	}

        /* Get the details of the batch */
        batchUploads batchDetails = transactionInManager.getBatchDetailsByBatchName(batchName);

        if (batchDetails != null) {
	    
	    utConfiguration configDetails = configurationManager.getConfigurationById(batchDetails.getConfigId());
	    mav.addObject("configDetails",configDetails);
	    
	    configurationTransport transportDetails = configurationTransportManager.getTransportDetails(batchDetails.getConfigId());
	    
	    String transportMethod = "";
	    if(transportDetails != null) {
		if(!"".equals(transportDetails.getDmConfigKeyword())) {
		    transportMethod = "File Drop (Direct)";
		}

		if(!"".equals(transportDetails.getRestAPIUsername())) {
		    transportMethod = "File Drop (Rest)";
		}
	    }
	    
	    mav.addObject("transportMethod",transportMethod);
	    
	    Organization orgDetails = organizationmanager.getOrganizationById(batchDetails.getOrgId());
            batchDetails.setOrgName(orgDetails.getOrgName());
	    
	    List<batchDownloads> associatedDownloadBatches = transactionOutManager.getDownloadBatchesByBatchUploadId(batchDetails.getId());
	    
	    if(associatedDownloadBatches != null) {
		if(!associatedDownloadBatches.isEmpty()) {
		    
		    for(batchDownloads batchDownload : associatedDownloadBatches) {
			Organization tgtOrgDetails = organizationmanager.getOrganizationById(batchDownload.getOrgId());
			File targetFile = null;
			//Check if target file has been generated
			if(batchDownload.getOutputFileName() != null) {
			    if(!"".equals(batchDownload.getOutputFileName())) {
				targetFile = new File(myProps.getProperty("ut.directory.utRootDir") + tgtOrgDetails.getCleanURL() + "/output files/" + batchDownload.getOutputFileName());
				if(targetFile.exists()) {
				    batchDownload.setTargetFileExists(true);
				}
			    }
			}
		    }
		    
		    batchDetails.setRelatedBatchDownloads(associatedDownloadBatches);
		    
		}
	    }
	    
            lu_ProcessStatus processStatus = sysAdminManager.getProcessStatusById(batchDetails.getStatusId());
            batchDetails.setStatusValue(processStatus.getDisplayCode());

            List<Integer> cancelStatusList = Arrays.asList(21, 22, 23, 1, 8, 35, 28);
            if (!cancelStatusList.contains(batchDetails.getStatusId())) {
                canCancel = true;
            }

            List<Integer> resetStatusList = Arrays.asList(2, 22, 23, 1, 8, 35, 28); //DNP (21) is not a final status for admin
	    //if (!resetStatusList.contains(batchDetails.getStatusId()) && configDetails.getMessageTypeId() != 2) {
            if (!resetStatusList.contains(batchDetails.getStatusId())) {
                canReset = true;
            }

            if (batchDetails.getStatusId() == 5 || batchDetails.getStatusId() == 64) {
                // now we check so we don't have to make a db hit if batch status is not 5 
                if (transactionInManager.getRecordCounts(batchDetails.getId(), Arrays.asList(11, 12, 13, 16), false, false) == 0) {
                    canSend = true;
                }
            }

            if (batchDetails.getStatusId() == 5 && transactionInManager.getRecordCounts(batchDetails.getId(), Arrays.asList(14), false, true) > 0) {
                canEdit = true;
            }

            /**
             * we need to check sbp (4), tbc (25) status, 38 SBL - if server is restarted and somehow the file hangs in SBP, we want to give them option to reset if sbp/tbc start time is about two hours, that should be sufficient indication that a file is stuck we don't want to reset or cancel in the middle of the processing
             */
            if (batchDetails.getStatusId() == 4 || batchDetails.getStatusId() == 25 || batchDetails.getStatusId() == 38) {
                Date d1 = batchDetails.getStartDateTime();
                Date d2 = new Date();
                //in milliseconds
                long diff = d2.getTime() - d1.getTime();

                long diffHours = diff / (60 * 60 * 1000) % 24;
                if (diffHours < 2) {
                    canReset = false;
                    canCancel = false;
                }
            }

            if (batchDetails.getConfigId() != 0) {
                batchDetails.setConfigName(configDetails.getconfigName());
            } else {
                batchDetails.setConfigName("Multiple Message Types");
            }
            mav.addObject("batchDetails", batchDetails);
	    
            if (batchDetails.getErrorRecordCount() > 0) {
		List<batchErrorSummary> batchErrorSummary = transactionInManager.getBatchErrorSummary(batchDetails.getId(),"inbound");
		mav.addObject("batchErrorSummary", batchErrorSummary);
		
		totalErroredRows = transactionInManager.getTotalErroredRows(batchDetails.getId());
	    }
	    
	    //Check to see if we have any dropped values
	    List<batchUploadDroppedValues> droppedValues = transactionInManager.getBatchDroppedValues(batchDetails.getId());
            mav.addObject("batchDroppedValues", droppedValues);
	    
	    //If allowed to cancel check if the outbound targets have already been sent
	    if(canCancel) {
		if(associatedDownloadBatches != null) {
		    if(!associatedDownloadBatches.isEmpty()) {
			for(batchDownloads batchDownload : associatedDownloadBatches) {
			    if(batchDownload.getStatusId() == 28) {
			       canCancel = false;
			   } 
			}
		    }
		}
	    }
	   
	    if(batchDetails.getStatusId() == 7) {
		List<batchErrorSummary> batchSystemErrors = transactionInManager.getBatchSystemErrorSummary(batchDetails.getId(),"inbound");
		mav.addObject("batchSystemErrors", batchSystemErrors);
	    }
	    
	    
        } else {
            mav.addObject("doesNotExist", true);
        }
	
        mav.addObject("canCancel", canCancel);
        mav.addObject("canReset", canReset);
        mav.addObject("canEdit", canEdit);
        mav.addObject("canSend", canSend);
	mav.addObject("batchDownload",false);
	mav.addObject("totalErroredRows", totalErroredRows);
	mav.addObject("page", page);
	
	if(canReset || canCancel || canEdit || canSend || batchDetails.getStatusId() == 2 || batchDetails.getStatusId() == 3 || batchDetails.getStatusId() == 36) {
	    showButtons = true;
	}
	
	mav.addObject("showButtons", showButtons);

        return mav;
    }

    /**
     * The 'inboundBatchOptions' function will process the batch according to the option submitted by admin
     * @param session
     * @param transactionInId
     * @param batchId
     * @param authentication
     * @param batchOption
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/inboundBatchOptions", method = RequestMethod.POST)
    public @ResponseBody
    boolean inboundBatchOptions(HttpSession session, @RequestParam(value = "tId", required = false) Integer transactionInId,
            @RequestParam(value = "batchId", required = true) Integer batchId, Authentication authentication,
            @RequestParam(value = "batchOption", required = true) String batchOption) throws Exception {

        String strBatchOption = "";
        utUser userInfo = usermanager.getUserByUserName(authentication.getName());
        batchUploads batchDetails = transactionInManager.getBatchDetails(batchId);

        if (userInfo != null && batchDetails != null) {
            
	    if (batchOption.equalsIgnoreCase("processBatch")) {
		//Clear transaction counts
		transactionInManager.resetTransactionCounts(batchId);
		
		//Delete batch transaction tables
		transactionInManager.deleteBatchTransactionTables(batchId);
                
		transactionInManager.updateBatchStatus(batchId, 42, "startDateTime");
            } 
	    else if (batchOption.equalsIgnoreCase("cancel")) {
                strBatchOption = "Cancelled Batch";
                transactionInManager.updateBatchStatus(batchId, 4, "startDateTime");
                transactionInManager.updateBatchStatus(batchId, 32, "endDateTime");
                //need to cancel targets also
                transactionInManager.updateBatchDLStatusByUploadBatchId(batchId, 0, 32, "endDateTime");
		
		//Delete batch transaction tables
		transactionInManager.deleteBatchTransactionTables(batchId);
		
		//Delete batch target tables
		transactionOutManager.deleteBatchDownloadTablesByBatchUpload(batchId);

            } 
	    else if (batchOption.equalsIgnoreCase("reset")) {
                strBatchOption = "Reset Batch";
		
		//Clear transaction counts
		transactionInManager.resetTransactionCounts(batchId);
		
		//Delete batch transaction tables
		transactionInManager.deleteBatchTransactionTables(batchId);
                transactionInManager.updateBatchStatus(batchId, 42, "startDateTime");
            } 
	    else if (batchOption.equalsIgnoreCase("releaseBatch")) {
                strBatchOption = "Released Batch";
		
		if(batchDetails.getStatusId() == 64) {
		    transactionInManager.updateBatchStatus(batchId, 42, "startDateTime");
		}
		else if (batchDetails.getStatusId() == 5) {
                    transactionInManager.updateBatchStatus(batchId, 4, "startDateTime");
                    //check once again to make sure all transactions are in final status
                    if (transactionInManager.getRecordCounts(batchId, Arrays.asList(11, 12, 13, 16), false, false) == 0) {
                        transactionInManager.updateBatchStatus(batchId, 6, "endDateTime");
                    } else {
                        transactionInManager.updateBatchStatus(batchId, 5, "endDateTime");
                    }
                }
            } 
	    else if (batchOption.equalsIgnoreCase("rejectMessage")) {
                strBatchOption = "Rejected Transaction";
                if (batchDetails.getStatusId() == 5) {
		    //Delete batch transaction tables
		    transactionInManager.deleteBatchTransactionTables(batchId);
		    
                    transactionInManager.updateBatchStatus(batchId, 7, "endDateTime");
		    
		    //Delete batch target tables
                }
		
            }
        }

        //log user activity
        utUserActivity ua = new utUserActivity();
        ua.setUserId(userInfo.getId());
        ua.setAccessMethod("POST");
        ua.setPageAccess("/inboundBatchOptions");
        ua.setActivity("Admin - " + strBatchOption);
        ua.setBatchUploadId(batchId);
        if (transactionInId != null) {
            ua.setTransactionInIds(transactionInId.toString());
        }
        usermanager.insertUserLog(ua);
        return true;
    }

    
    
    /**
     * The '/referralActivityExport' GET request will return the latest export created
     *
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/referralActivityExport", method = RequestMethod.GET)
    public ModelAndView referralActivityExport(HttpSession session) throws Exception {
        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/referralActivityExport");

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);

        List<referralActivityExports> exports = transactionInManager.getReferralActivityExportsWithUserNames(Arrays.asList(1, 2, 3, 4, 6));
        encryptObject encrypt = new encryptObject();
        Map<String, String> map;
        for (referralActivityExports export : exports) {
            //Encrypt the use id to pass in the url
            map = new HashMap<String, String>();
            map.put("id", Integer.toString(export.getId()));
            map.put("topSecret", topSecret);

            String[] encrypted = encrypt.encryptObject(map);
            export.setEncryptedId(encrypted[0]);
            export.setEncryptedSecret(encrypted[1]);
        }
        mav.addObject("exports", exports);

        return mav;
    }

    /**
     * The '/referralActivityExport' POST method will generate add an entry into the existing table.
     *
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/referralActivityExport", method = RequestMethod.POST)
    public ModelAndView referralActivityExport(@RequestParam Date fromDate, @RequestParam Date toDate, RedirectAttributes redirectAttr, HttpSession session) throws Exception {
        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        utUser userInfo = (utUser) session.getAttribute("userDetails");

        /**
         * insert a new export *
         */
        referralActivityExports export = new referralActivityExports();

        export.setCreatedBy(userInfo.getId());
        export.setToDate(toDate);
        export.setFromDate(fromDate);

        DateFormat selDateRangeFormat = new SimpleDateFormat("MM/dd/yyyy");
        export.setSelDateRange(selDateRangeFormat.format(fromDate) + " - " + selDateRangeFormat.format(toDate));
        export.setStatusId(1);
        transactionInManager.saveReferralActivityExport(export);

        ModelAndView mav = new ModelAndView(new RedirectView("referralActivityExport"));
        return mav;

    }

    /**
     * The '/wsmessage' GET request will serve up the list of inbound web services messages
     *
     *
     * @Objects	(1) An object containing all the found wsMessagesIn
     *
     * @throws Exception
     */
    @RequestMapping(value = "/wsmessage", method = RequestMethod.GET)
    public ModelAndView listInBoundWSmessages(HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/wsmessage");

        if ("".equals(searchParameters.getsection()) || !"inbound".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("inbound");
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
        }

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);

        /* Get all ws messages */
        try {

            Integer fetchCount = 0;
            List<WSMessagesIn> wsMessagesList = wsmanager.getWSMessagesInList(fromDate, toDate, fetchCount);

            if (!wsMessagesList.isEmpty()) {

                //we can map the process status so we only have to query once
                List<TableData> errorCodeList = sysAdminManager.getDataList("lu_ErrorCodes", "");
                Map<Integer, String> errorMap = new HashMap<Integer, String>();
                for (TableData error : errorCodeList) {
                    errorMap.put(error.getId(), error.getDisplayText());
                }

                //ws status map
                Map<Integer, String> statusMap = new HashMap<Integer, String>();
                statusMap.put(1, "To be processed");
                statusMap.put(2, "Processed");
                statusMap.put(3, "Rejected");
                statusMap.put(4, "Being Processed");

                //if we have lots of organization in the future we can tweak this to narrow down to orgs with batches
                List<Organization> organizations = organizationmanager.getOrganizations();
                Map<Integer, String> orgMap = new HashMap<Integer, String>();
                for (Organization org : organizations) {
                    orgMap.put(org.getId(), org.getOrgName());
                }

                for (WSMessagesIn wsIn : wsMessagesList) {
                    //set error text
                    wsIn.setErrorDisplayText(errorMap.get(wsIn.getErrorId()));
                    //set org name
                    if (wsIn.getOrgId() == 0) {
                        wsIn.setOrgName("No Org Match");
                    } else {
                        wsIn.setOrgName(orgMap.get(wsIn.getOrgId()));
                    }
                    //set status
                    wsIn.setStatusName(statusMap.get(wsIn.getStatusId()));

                    if (wsIn.getBatchUploadId() != 0) {
                        wsIn.setBatchName(transactionInManager.getBatchDetails(wsIn.getBatchUploadId()).getUtBatchName());
                    }
                }
            }

            mav.addObject("wsMessages", wsMessagesList);

        } catch (Exception e) {
            throw new Exception("Error occurred viewing the all web service messages.", e);
        }

        return mav;

    }

    /**
     * The '/wsMessage' POST request will serve up a list of WSMessages received by the system.
     *
     * @param page	The page parameter will hold the page to view when pagination is built.
     * @return The list of wsMessages
     *
     * @Objects	(1) An object containing all the found wsMessages
     *
     * @throws Exception
     */
    @RequestMapping(value = "/wsmessage", method = RequestMethod.POST)
    public ModelAndView listWSMessages(@RequestParam Date fromDate, @RequestParam Date toDate,
            HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/wsmessage");

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("inbound");

        /* Get all ws in  */
        try {
            Integer fetchCount = 0;
            List<WSMessagesIn> wsMessagesList = wsmanager.getWSMessagesInList(fromDate, toDate, fetchCount);

            if (!wsMessagesList.isEmpty()) {

                //we can map the process status so we only have to query once
                List<TableData> errorCodeList = sysAdminManager.getDataList("lu_ErrorCodes", "");
                Map<Integer, String> errorMap = new HashMap<Integer, String>();
                for (TableData error : errorCodeList) {
                    errorMap.put(error.getId(), error.getDisplayText());
                }

                //ws status map
                Map<Integer, String> statusMap = new HashMap<Integer, String>();
                statusMap.put(1, "To be processed");
                statusMap.put(2, "Processed");
                statusMap.put(3, "Rejected");

                //if we have lots of organization in the future we can tweak this to narrow down to orgs with batches
                List<Organization> organizations = organizationmanager.getOrganizations();
                Map<Integer, String> orgMap = new HashMap<Integer, String>();
                for (Organization org : organizations) {
                    orgMap.put(org.getId(), org.getOrgName());
                }

                for (WSMessagesIn wsIn : wsMessagesList) {
                    //set error text
                    wsIn.setErrorDisplayText(errorMap.get(wsIn.getErrorId()));
                    //set org name
                    if (wsIn.getOrgId() == 0) {
                        wsIn.setOrgName("No Org Match");
                    } else {
                        wsIn.setOrgName(orgMap.get(wsIn.getOrgId()));
                    }
                    //set status
                    wsIn.setStatusName(statusMap.get(wsIn.getStatusId()));
                    if (wsIn.getBatchUploadId() != 0) {
                        wsIn.setBatchName(transactionInManager.getBatchDetails(wsIn.getBatchUploadId()).getUtBatchName());
                    }

                }
            }

            mav.addObject("wsMessages", wsMessagesList);

        } catch (Exception e) {
            throw new Exception("Error occurred viewing the all uploaded batches.", e);
        }

        return mav;
    }

    /**
     * this displays the payload*
     * @param wsId
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/wsmessage/viewPayload.do", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView viewPayload(@RequestParam Integer wsId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/viewPayload");
        WSMessagesIn wsMessage = wsmanager.getWSMessagesIn(wsId);
        String payload = "";
        if (wsMessage != null) {
            payload = wsMessage.getPayload();
        }

        mav.addObject("payload", payload);

        return mav;

    }

    /**
     * The '/rejected' GET request will serve up the existing list of referrals with at least one rejected transaction.
     *
     * @param session
     * @return The list of batches with rejected transactions
     *
     * @Objects	(1) An object containing all the found batches
     *
     * @throws Exception
     */
    @RequestMapping(value = "/rejected", method = RequestMethod.GET)
    public ModelAndView listRejectedBatches(HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/rejected");
	
	/* Get system inbound summary */
        systemSummary summaryDetails = transactionInManager.generateSystemInboundSummary();
        mav.addObject("summaryDetails", summaryDetails);

	String searchTerm = "";
	
        if ("".equals(searchParameters.getsection()) || !"rejected".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
	    searchParameters.setsearchTerm("");
            searchParameters.setsection("rejected");
	    session.setAttribute("searchParameters", searchParameters);
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
	    searchTerm = searchParameters.getsearchTerm();
	    searchParameters.setsearchTerm("");
	    session.setAttribute("searchParameters", searchParameters);
        }
	
	mav.addObject("searchFilter", searchTerm);
        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);
	
	try {

            Integer fetchCount = 0;
	    
	    List<batchUploads> rejectedBatches = transactionInManager.getAllRejectedBatches(fromDate, toDate, fetchCount);

            if (!rejectedBatches.isEmpty()) {
		
		//we can map the process status so we only have to query once
                List<utConfiguration> configurationList = configurationManager.getConfigurations();
                Map<Integer, String> cMap = new HashMap<>();
		configurationList.forEach((c) -> {
		    cMap.put(c.getId(), c.getconfigName());
		});
		
                //we can map the process status so we only have to query once
                List<lu_ProcessStatus> processStatusList = sysAdminManager.getAllProcessStatus();
                Map<Integer, String> psMap = new HashMap<>();
		processStatusList.forEach((ps) -> {
		    psMap.put(ps.getId(), ps.getDisplayCode());
		});

                //same with transport method names
                List<TransportMethod> transporthMethods = configurationTransportManager.getTransportMethods(Arrays.asList(0, 1));
                Map<Integer, String> tmMap = new HashMap<>();
		transporthMethods.forEach((tms) -> {
		    tmMap.put(tms.getId(), tms.getTransportMethod());
		});

                //if we have lots of organization in the future we can tweak this to narrow down to orgs with batches
                List<Organization> organizations = organizationmanager.getOrganizations();
                Map<Integer, String> orgMap = new HashMap<>();
		organizations.forEach((org) -> {
		    orgMap.put(org.getId(), org.getOrgName());
		});

                //same goes for users
                List<utUser> users = usermanager.getAllUsers();
                Map<Integer, String> userMap = new HashMap<>();
		users.forEach((user) -> {
		    userMap.put(user.getId(), (user.getFirstName() + " " + user.getLastName()));
		});
		
		Calendar cal = Calendar.getInstance();

		rejectedBatches.stream().map((batch) -> {
		    cal.setTime(batch.getStartDateTime());
		    cal.add(Calendar.HOUR,-1);
		    batch.setStartDateTime(cal.getTime());
		    cal.setTime(batch.getEndDateTime());
		    cal.add(Calendar.HOUR,-1);
		    batch.setEndDateTime(cal.getTime());
		    //the count is in totalRecordCount already, can skip re-count
		    // batch.settotalTransactions(transactionInManager.getRecordCounts(batch.getId(), statusIds, false, false));
		    batch.setStatusValue(psMap.get(batch.getStatusId()));
		    return batch;
		}).map((batch) -> {
		    batch.setOrgName(orgMap.get(batch.getOrgId()));
		    return batch;
		}).map((batch) -> {
		    batch.setTransportMethod(tmMap.get(batch.getTransportMethodId()));
		    return batch;
		}).map((batch) -> {
		    batch.setUsersName(userMap.get(batch.getUserId()));
		    return batch;
		}).forEachOrdered((batch) -> {
		    batch.setConfigName(cMap.get(batch.getConfigId()));
		});
            }

            mav.addObject("batches", rejectedBatches);

        } catch (Exception e) {
            throw new Exception("Error occurred viewing the all uploaded batches.", e);
        }

        return mav;

    }

    /**
     * The '/rejected' POST request will serve up the existing list of referrals with at least one rejected transaction.
     *
     * @param fromDate
     * @param toDate
     * @param request
     * @param response
     * @param session
     * @return The list of batches with rejected transactions
     *
     * @Objects	(1) An object containing all the found batches
     *
     * @throws Exception
     */
    @RequestMapping(value = "/rejected", method = RequestMethod.POST)
    public ModelAndView listRejectedBatches(@RequestParam Date fromDate, @RequestParam Date toDate, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/rejected");

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("rejected");

        try {

            Integer fetchCount = 0;
	    
	    List<batchUploads> rejectedBatches = transactionInManager.getAllRejectedBatches(fromDate, toDate, fetchCount);

            if (!rejectedBatches.isEmpty()) {
		
		//we can map the process status so we only have to query once
                List<utConfiguration> configurationList = configurationManager.getConfigurations();
                Map<Integer, String> cMap = new HashMap<>();
		configurationList.forEach((c) -> {
		    cMap.put(c.getId(), c.getconfigName());
		});
		
                //we can map the process status so we only have to query once
                List<lu_ProcessStatus> processStatusList = sysAdminManager.getAllProcessStatus();
                Map<Integer, String> psMap = new HashMap<>();
		processStatusList.forEach((ps) -> {
		    psMap.put(ps.getId(), ps.getDisplayCode());
		});

                //same with transport method names
                List<TransportMethod> transporthMethods = configurationTransportManager.getTransportMethods(Arrays.asList(0, 1));
                Map<Integer, String> tmMap = new HashMap<>();
		transporthMethods.forEach((tms) -> {
		    tmMap.put(tms.getId(), tms.getTransportMethod());
		});

                //if we have lots of organization in the future we can tweak this to narrow down to orgs with batches
                List<Organization> organizations = organizationmanager.getOrganizations();
                Map<Integer, String> orgMap = new HashMap<>();
		organizations.forEach((org) -> {
		    orgMap.put(org.getId(), org.getOrgName());
		});

                //same goes for users
                List<utUser> users = usermanager.getAllUsers();
                Map<Integer, String> userMap = new HashMap<>();
		users.forEach((user) -> {
		    userMap.put(user.getId(), (user.getFirstName() + " " + user.getLastName()));
		});

		rejectedBatches.stream().map((batch) -> {
		    //the count is in totalRecordCount already, can skip re-count
		    // batch.settotalTransactions(transactionInManager.getRecordCounts(batch.getId(), statusIds, false, false));
		    batch.setStatusValue(psMap.get(batch.getStatusId()));
		    return batch;
		}).map((batch) -> {
		    batch.setOrgName(orgMap.get(batch.getOrgId()));
		    return batch;
		}).map((batch) -> {
		    batch.setTransportMethod(tmMap.get(batch.getTransportMethodId()));
		    return batch;
		}).map((batch) -> {
		    batch.setUsersName(userMap.get(batch.getUserId()));
		    return batch;
		}).forEachOrdered((batch) -> {
		    batch.setConfigName(cMap.get(batch.getConfigId()));
		});
            }

            mav.addObject("batches", rejectedBatches);

        } catch (Exception e) {
            throw new Exception("Error occurred viewing the all uploaded batches.", e);
        }

        return mav;
    }

    /**
     * The '/wsmessageOut' GET request will serve up the list of outbound web services messages
     *
     *
     * @param session
     * @return 
     * @Objects	(1) An object containing all the found wsMessagesOut
     *
     * @throws Exception
     */
    @RequestMapping(value = "/wsmessageOut", method = RequestMethod.GET)
    public ModelAndView listInBoundWSmessagesOut(HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/wsmessageOut");

        if ("".equals(searchParameters.getsection()) || !"inbound".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("inbound");
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
        }

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);

        /* Get all ws messages */
        try {

            Integer fetchCount = 0;
            List<wsMessagesOut> wsMessagesList = wsmanager.getWSMessagesOutList(fromDate, toDate, fetchCount);

            if (!wsMessagesList.isEmpty()) {
                //if we have lots of organization in the future we can tweak this to narrow down to orgs with batches
                List<Organization> organizations = organizationmanager.getOrganizations();
                Map<Integer, String> orgMap = new HashMap<>();
		organizations.forEach((org) -> {
		    orgMap.put(org.getId(), org.getOrgName());
		});

                for (wsMessagesOut wsOut : wsMessagesList) {
                    //set org name
                    if (wsOut.getOrgId() == 0) {
                        wsOut.setOrgName("No Org Match");
                    } else {
                        wsOut.setOrgName(orgMap.get(wsOut.getOrgId()));
                    }

                    if (wsOut.getBatchDownloadId() != 0) {
                        wsOut.setBatchName(transactionOutManager.getBatchDetails(wsOut.getBatchDownloadId()).getUtBatchName());
                    }
                }
            }

            mav.addObject("wsMessages", wsMessagesList);

        } catch (Exception e) {
            throw new Exception("Error occurred viewing the all outbound web service messages.", e);
        }

        return mav;

    }

    /**
     * The '/wsMessageOut' POST request will serve up a list of outbound WSMessages received by the system.
     *
     * @param fromDate
     * @param toDate
     * @param response
     * @param request
     * @param session
     * @return The list of wsMessages
     *
     * @Objects	(1) An object containing all the found wsMessages
     *
     * @throws Exception
     */
    @RequestMapping(value = "/wsmessageOut", method = RequestMethod.POST)
    public ModelAndView listWSMessagesOut(@RequestParam Date fromDate, @RequestParam Date toDate,
            HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/wsmessageOut");

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("inbound");

        /* Get all ws in  */
        try {
            Integer fetchCount = 0;
            List<wsMessagesOut> wsMessagesList = wsmanager.getWSMessagesOutList(fromDate, toDate, fetchCount);

            if (!wsMessagesList.isEmpty()) {

                //if we have lots of organization in the future we can tweak this to narrow down to orgs with batches
                List<Organization> organizations = organizationmanager.getOrganizations();
                Map<Integer, String> orgMap = new HashMap<>();
		organizations.forEach((org) -> {
		    orgMap.put(org.getId(), org.getOrgName());
		});

		wsMessagesList.forEach((ws) -> {
		    if (ws.getOrgId() == 0) {
			ws.setOrgName("No Org Match");
		    } else {
			ws.setOrgName(orgMap.get(ws.getOrgId()));
		    }
		});
            }

            mav.addObject("wsMessages", wsMessagesList);

        } catch (Exception e) {
            throw new Exception("Error occurred viewing the all outbound ws.", e);
        }

        return mav;
    }

    /**
     * this displays the soap message*
     * @param wsId
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/wsmessage/viewSoapMessage.do", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView viewSoapMessage(@RequestParam Integer wsId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/viewSoapMessage");
        wsMessagesOut wsMessage = wsmanager.getWSMessagesOut(wsId);
        mav.addObject("wsMessage", wsMessage);

        return mav;

    }

    /**
     * this displays the soap response*
     * @param wsId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/wsmessage/viewSoapResponse.do", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView viewSoapResponse(@RequestParam Integer wsId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/viewSoapResponse");
        wsMessagesOut wsMessage = wsmanager.getWSMessagesOut(wsId);
        mav.addObject("wsMessage", wsMessage);

        return mav;

    }

    /**
     * The '/wsmessageOut' GET request will serve up the list of outbound web services messages
     *
     *
     * @param session
     * @param batchName
     * @return 
     * @Objects	(1) An object containing all the found wsMessagesOut
     *
     * @throws Exception
     */
    @RequestMapping(value = "/wsmessageOut/{batchName}", method = RequestMethod.GET)
    public ModelAndView listSingleWSmessagesOut(HttpSession session, @PathVariable String batchName) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/wsmessageOut");

        if ("".equals(searchParameters.getsection()) || !"inbound".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("inbound");
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
        }

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);

        /* Get all ws messages */
        try {

            /* Get the details of the batch */
            batchDownloads batchDetails = transactionOutManager.getBatchDetailsByBatchName(batchName);

            List<wsMessagesOut> wsMessagesList = wsmanager.getWSMessagesOutByBatchId(batchDetails.getId());

            if (!wsMessagesList.isEmpty()) {
                for (wsMessagesOut wsOut : wsMessagesList) {
                    //set org name
                    if (wsOut.getOrgId() == 0) {
                        wsOut.setOrgName("No Org Match");
                    } else {
                        wsOut.setOrgName(organizationmanager.getOrganizationById(wsOut.getOrgId()).getOrgName());
                    }

                    if (wsOut.getBatchDownloadId() != 0) {
                        wsOut.setBatchName(transactionOutManager.getBatchDetails(wsOut.getBatchDownloadId()).getUtBatchName());
                    }
                }
            }

            mav.addObject("wsMessages", wsMessagesList);

        } catch (Exception e) {
            throw new Exception("Error occurred viewing the all web service outbound messages.", e);
        }

        return mav;

    }

    /**
     * The '/wsmessage' GET request will serve up the list of inbound web services messages
     *
     *
     * @param session
     * @param batchName
     * @return 
     * @Objects	(1) An object containing all the found wsMessagesIn
     *
     * @throws Exception
     */
    @RequestMapping(value = "/wsmessage/{batchName}", method = RequestMethod.GET)
    public ModelAndView listInBoundOneWSmessages(HttpSession session, @PathVariable String batchName) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/wsmessage");

        if ("".equals(searchParameters.getsection()) || !"inbound".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("inbound");
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
        }

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);

        /* Get all ws messages */
        try {

            batchUploads batchDetails = transactionInManager.getBatchDetailsByBatchName(batchName);
            List<WSMessagesIn> wsMessagesList = wsmanager.getWSMessagesInByBatchId(batchDetails.getId());

            if (!wsMessagesList.isEmpty()) {

                //we can map the process status so we only have to query once
                List<TableData> errorCodeList = sysAdminManager.getDataList("lu_ErrorCodes", "");
                Map<Integer, String> errorMap = new HashMap<>();
		errorCodeList.forEach((error) -> {
		    errorMap.put(error.getId(), error.getDisplayText());
		});

                //ws status map
                Map<Integer, String> statusMap = new HashMap<>();
                statusMap.put(1, "To be processed");
                statusMap.put(2, "Processed");
                statusMap.put(3, "Rejected");
                statusMap.put(4, "Being Process");

                for (WSMessagesIn wsIn : wsMessagesList) {
                    //set error text
                    wsIn.setErrorDisplayText(errorMap.get(wsIn.getErrorId()));
                    //set org name
                    if (wsIn.getOrgId() == 0) {
                        wsIn.setOrgName("No Org Match");
                    } else {
                        wsIn.setOrgName(organizationmanager.getOrganizationById(wsIn.getOrgId()).getOrgName());
                    }
                    //set status
                    wsIn.setStatusName(statusMap.get(wsIn.getStatusId()));

                    if (wsIn.getBatchUploadId() != 0) {
                        wsIn.setBatchName(transactionInManager.getBatchDetails(wsIn.getBatchUploadId()).getUtBatchName());
                    }
                }
            }

            mav.addObject("wsMessages", wsMessagesList);

        } catch (Exception e) {
            throw new Exception("Error occurred viewing the single inbound web service messages.", e);
        }

        return mav;

    }

    @RequestMapping(value = "/dlExport", method = {RequestMethod.GET})
    public void dlExport(@RequestParam String i, @RequestParam String v,
            HttpSession session, HttpServletResponse response) throws Exception {

        utUser userDetails = new utUser();
        Integer exportId = 0;

        boolean canViewReport = false;
        if (session.getAttribute("userDetails") != null) {
            userDetails = (utUser) session.getAttribute("userDetails");
            //1 decrpt and get the reportId
            decryptObject decrypt = new decryptObject();
            Object obj = decrypt.decryptObject(i, v);
            String[] result = obj.toString().split((","));
            exportId = Integer.parseInt(result[0].substring(4));

            //now we get the report details
            referralActivityExports export = transactionInManager.getReferralActivityExportById(exportId);

            if (export != null) {
                //we check permission and program
                if (userDetails.getRoleId() != 2) {
                    canViewReport = true;
                }
            }
            //we log them, grab report for them to download
            //if report doesn't exist we send them back to list with a message
            utUserActivity ua = new utUserActivity();
            ua.setUserId(userDetails.getId());
            ua.setAccessMethod("POST");
            ua.setPageAccess("/dlReport");

            if (!canViewReport) {
                //log user activity
                ua.setActivity("Tried to View Export - " + exportId);
                usermanager.insertUserLog(ua);
            } else {
                ua.setActivity("Viewed Export - " + exportId);
                usermanager.insertUserLog(ua);

                //generate the report for user to download
                //need to get report path
                /*fileSystem dir = new fileSystem();
                dir.setDirByName("referralActivityExports/");
                String filePath = dir.getDir();
                String fileName = export.getFileName();
                try {
                    File f = new File(dir.getDir() + export.getFileName());

                    if (!f.exists()) {
                        throw new Exception("Error with File " + dir.getDir() + export.getFileName());
                    }
                } catch (Exception e) {
                    try {
                        //update file to error
                        export.setStatusId(5);
                        transactionInManager.updateReferralActivityExport(export);
                        throw new Exception("File does not exists " + dir.getDir() + export.getFileName());
                    } catch (Exception ex1) {
                        throw new Exception("File does not exists " + dir.getDir() + export.getFileName() + ex1);
                    }

                }

                try {
                    // get your file as InputStream
                    InputStream is = new FileInputStream(filePath + fileName);
                    // copy it to response's OutputStream

                    String mimeType = "application/octet-stream";
                    response.setContentType(mimeType);
                    response.setHeader("Content-Transfer-Encoding", "binary");
                    response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
                    org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
                    response.flushBuffer();
                    is.close();

                    //update status
                    if (export.getStatusId() == 3) {
                        export.setStatusId(4);
                        transactionInManager.updateReferralActivityExport(export);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.println("Error writing file to output stream. Filename was '{}'" + fileName + ex);
                    try {
                        //update file to error
                        export.setStatusId(5);
                        transactionInManager.updateReferralActivityExport(export);
                        throw new Exception("Error with File " + filePath + fileName + ex);
                    } catch (Exception e) {
                        throw new Exception("Error with File " + filePath + fileName + ex);
                    }
                }*/
            }

        } else {
            //someone somehow got to this link, we just log
            //we log who is accessing 
            //now we have report id, we check to see which program it belongs to and if the user has permission
            utUserActivity ua = new utUserActivity();
            ua.setUserId(userDetails.getId());
            ua.setAccessMethod("POST");
            ua.setPageAccess("/dlReport");
            ua.setActivity("Tried to view export - " + exportId);
            usermanager.insertUserLog(ua);
            throw new Exception("invalid export view - " + exportId);
        }

    }

    @RequestMapping(value = "/delExport", method = {RequestMethod.GET})
    public ModelAndView delExport(@RequestParam String i, @RequestParam String v,
            HttpSession session, HttpServletResponse response) throws Exception {

        utUser userDetails = new utUser();
        Integer exportId = 0;

        boolean canDeleteReport = false;
        if (session.getAttribute("userDetails") != null) {
            userDetails = (utUser) session.getAttribute("userDetails");
            //1 decrpt and get the reportId
            decryptObject decrypt = new decryptObject();
            Object obj = decrypt.decryptObject(i, v);
            String[] result = obj.toString().split((","));
            exportId = Integer.parseInt(result[0].substring(4));

            //now we get the report details
            referralActivityExports export = transactionInManager.getReferralActivityExportById(exportId);

            if (export != null) {
                //we check permission and program
                if (userDetails.getRoleId() != 2) {
                    canDeleteReport = true;
                }
            }
            //we log them, grab report for them to download
            //if report doesn't exist we send them back to list with a message
            utUserActivity ua = new utUserActivity();
            ua.setUserId(userDetails.getId());
            ua.setAccessMethod("GET");
            ua.setPageAccess("/delReport");

            if (!canDeleteReport) {
                //log user activity
                ua.setActivity("Tried to Delete Export - " + exportId);
                usermanager.insertUserLog(ua);
            } else {
                ua.setActivity("Deleted Export - " + exportId);
                usermanager.insertUserLog(ua);
                export.setStatusId(5);
                transactionInManager.updateReferralActivityExport(export);
            }

        } else {
            //someone somehow got to this link, we just log
            //we log who is accessing 
            //now we have report id, we check to see which program it belongs to and if the user has permission
            utUserActivity ua = new utUserActivity();
            ua.setUserId(userDetails.getId());
            ua.setAccessMethod("GET");
            ua.setPageAccess("/dlReport");
            ua.setActivity("Tried to delete export - " + exportId);
            usermanager.insertUserLog(ua);
            throw new Exception("invalid delete export view - " + exportId);
        }

        ModelAndView mav = new ModelAndView(new RedirectView("referralActivityExport"));
        return mav;

    }
    
    /**
     * The '/deleteBatch' POST method will remove both the inbound and outbound transactions that are associated to the passed in
     * batchName
     *NjM2NjguMjYxLjE3OTk=
     *
     * @param batchName
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/deleteBatch.do", method = RequestMethod.POST)
    public @ResponseBody String deleteBatch(@RequestParam(value = "batchName", required = true) String batchName) throws Exception {
        
        //Make sure the passed in batch is valid
        batchUploads batchDetails = transactionInManager.getBatchDetailsByBatchName(batchName);
	
	//programImport existingProgramImport = null;
	
	if(batchDetails != null) {
	    
	    if(batchDetails.getOriginalFileName() != null) {
		if(batchDetails.getOriginalFileName().lastIndexOf('.') > 0) {
		    /* Need to check to see if uploaded file exists in RR program uploads */
		    //existingProgramImport = importmanager.getProgramImportByAssignedName(batchDetails.getoriginalFileName().substring(0, batchDetails.getoriginalFileName().lastIndexOf('.')), 0);
		}
	    }
	    
	    /*if(existingProgramImport != null) {
		existingProgramImport.setStatusId(32);
		importmanager.updateImport(existingProgramImport);
	    }*/
	    
	    transactionInManager.deleteBatch(batchDetails.getId()); 
	}
	
        return "1";
        
    }
    
    /**
     * this displays the payload*
     * @param batchId
     * @param errorId
     * @param totalErrors
     * @param indexVal
     * @param type
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/loadErrors.do", method = RequestMethod.GET)
    public @ResponseBody ModelAndView loadErrors(
	    @RequestParam Integer batchId,
	    @RequestParam Integer errorId,
	    @RequestParam Integer totalErrors, @RequestParam Integer indexVal,
	    @RequestParam String type) throws Exception {

	ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/auditReportErrorDetails");
	mav.addObject("indexVal", indexVal);
	
	List<String> customCols = new ArrayList<>();
	
	String sql = "";
	
	customCols.add("From Outbound");
	customCols.add("Row No.");
	customCols.add("Field No.");
	
	List reportableFields = null;
	
	if("inbound".equals(type)) {
	    reportableFields = transactionInManager.getErrorReportField(batchId);
	}
	else {
	    reportableFields = transactionOutManager.getErrorReportField(batchId);
	}
	
	customCols.add("Column Name");
	
	//Set the custom columns based on the error selected
	switch(errorId) {
	    case 1:
		
		if("inbound".equals(type)) {
		    customCols.add("Field Value");
		
		    sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
			+ "from batchuploadauditerrors a left outer  join "
			+ "configurationmessagespecs b on a.configId = b.configId "
			+ "where a.batchUploadId = " + batchId + " and a.errorId = " + errorId + " order by a.rownumber asc";
		}
		else {
		    customCols.add("Field Value");
		
		    sql = "select 'false' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
			+ "from batchdownloadauditerrors a left outer  join "
			+ "configurationmessagespecs b on a.configId = b.configId "
			+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + errorId + " order by a.rownumber asc";
		}
		
		break;
		
	    case 2:
		customCols.add("Validation Type");
		
		if("inbound".equals(type)) {
		    customCols.add("Field Value");

		    sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name, a.errorDetails as validation_type, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
			+ "from batchuploadauditerrors a left outer  join "
			+ "configurationmessagespecs b on a.configId = b.configId "
			+ "where a.batchUploadId = " + batchId + " and a.errorId = " + errorId + " order by a.rownumber asc";
		}
		else {
		    customCols.add("Field Value");

		    sql = "select 'false' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name, a.errorDetails as validation_type, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
			+ "from batchdownloadauditerrors a left outer  join "
			+ "configurationmessagespecs b on a.configId = b.configId "
			+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + errorId + " order by a.rownumber asc";
		}
		
		break;
		
	    case 3:
		customCols.add("Crosswalk");
		
		if("inbound".equals(type)) {
		    customCols.add("Field Value");	

		    sql = "select fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name, a.errorDetails as crosswalk, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
			+ "from batchuploadauditerrors a left outer join "
			+ "configurationmessagespecs b on a.configId = b.configId "
			+ "where a.batchUploadId = " + batchId + " and a.errorId = " + errorId + " order by a.rownumber asc";
		}
		else {
		    customCols.add("Field Value");	
		    
		    sql = "select 'false' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name, a.errorDetails as crosswalk, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
			+ "from batchdownloadauditerrors a left outer join "
			+ "configurationmessagespecs b on a.configId = b.configId "
			+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + errorId + " order by a.rownumber asc";
		}
		break;
		
	    case 4:
		customCols.add("Macro");
		
		if("inbound".equals(type)) {
		    customCols.add("Field Value");

		    sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.errorDetails as macro, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
			+ "from batchuploadauditerrors a left outer  join "
			+ "configurationmessagespecs b on a.configId = b.configId "
			+ "where a.batchUploadId = " + batchId + " and a.errorId = " + errorId + " order by a.rownumber asc";
		}
		else {
		    customCols.add("Field Value");

		    sql = "select 'false' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.errorDetails as macro, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
			+ "from batchdownloadauditerrors a left outer  join "
			+ "configurationmessagespecs b on a.configId = b.configId "
			+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + errorId + " order by a.rownumber asc";
		}
		
		break;
		
	    default:
		if("inbound".equals(type)) {
		    customCols.add("Field Value");

		    sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
			+ "from batchuploadauditerrors a left outer  join "
			+ "configurationmessagespecs b on a.configId = b.configId "
			+ "where a.batchUploadId = " + batchId + " and a.errorId = " + errorId + " order by a.rownumber asc";
		}
		else {
		    customCols.add("Field Value");

		    sql = "select 'false' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
			+ "from batchdownloadauditerrors a left outer  join "
			+ "configurationmessagespecs b on a.configId = b.configId "
			+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + errorId + " order by a.rownumber asc";
		}
		
		break;
	}
	
	if(reportableFields != null) {
	    Iterator reportableFieldsIt = reportableFields.iterator();
	
	    while (reportableFieldsIt.hasNext()) {
		Object rptFieldrow[] = (Object[]) reportableFieldsIt.next();
		customCols.add(rptFieldrow[0].toString());
		customCols.add(rptFieldrow[1].toString());
		customCols.add(rptFieldrow[2].toString());
		customCols.add(rptFieldrow[3].toString());
	    }
	}
		
	mav.addObject("customCols", customCols);
	mav.addObject("totalErrors",totalErrors);
	
	List errors = transactionInManager.getErrorDataBySQLStmt(sql);
	
	mav.addObject("errors", errors);
        
        return mav;

    }
    
    /**
     * The '/invalidIn' GET request will serve up the list of inbound batches that errored
     *
     *
     * @param fromDate
     * @param toDate
     * @param request
     * @param response
     * @param session
     * @return 
     * @Objects	(1) An object containing all the found invalidIn
     *
     * @throws Exception
     */
    @RequestMapping(value = "/invalidIn", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView listInvalidInBatches(@RequestParam(value = "fromDate", required=false) Date fromDate,
    		@RequestParam(value = "toDate", required=false) Date toDate,
            HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);
        
        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
	
        if (fromDate == null) {
	    if(searchParameters.getfromDate() != null) {
		fromDate = searchParameters.getfromDate();
	    }
	    else {
		fromDate = getMonthDate("LAST30");
	    }
        }
        if (toDate == null) {
	    if(searchParameters.getfromDate() != null) {
		toDate = searchParameters.gettoDate();
	    }
	    else {
		toDate = getMonthDate("END-TODAY");
	    }
        } 
	searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
       
	String searchTerm = "";
	
        if ("".equals(searchParameters.getsection()) || !"invalidIn".equals(searchParameters.getsection())) {
            searchParameters.setsection("invalidIn");
	    searchParameters.setsearchTerm("");
	    session.setAttribute("searchParameters", searchParameters);
        } else {
	    searchTerm = searchParameters.getsearchTerm().trim();
	    
	    searchParameters.setsearchTerm("");
	    session.setAttribute("searchParameters", searchParameters);
        }
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/invalidIn");
	mav.addObject("searchFilter", searchTerm);
        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);
	
	/* Get system inbound summary */
        systemSummary summaryDetails = transactionInManager.generateSystemInboundSummary();
        mav.addObject("summaryDetails", summaryDetails);

       
       try {

            Integer fetchCount = 0;
	    
	    List<batchUploads> invalidInboundBatches = transactionInManager.getBatchesByStatusIdsAndDate(fromDate, toDate, fetchCount, Arrays.asList(1, 7, 25, 29, 30, 39));
       
            if (!invalidInboundBatches.isEmpty()) {
		
		//we can map the process status so we only have to query once
                List<utConfiguration> configurationList = configurationManager.getConfigurations();
                Map<Integer, String> cMap = new HashMap<>();
		configurationList.forEach((c) -> {
		    cMap.put(c.getId(), c.getconfigName());
		});
		
                //we can map the process status so we only have to query once
                List<lu_ProcessStatus> processStatusList = sysAdminManager.getAllProcessStatus();
                Map<Integer, String> psMap = new HashMap<>();
		processStatusList.forEach((ps) -> {
		    psMap.put(ps.getId(), ps.getDisplayCode());
		});

                //same with transport method names
                List<TransportMethod> transporthMethods = configurationTransportManager.getTransportMethods(Arrays.asList(0, 1));
                Map<Integer, String> tmMap = new HashMap<>();
		transporthMethods.forEach((tms) -> {
		    tmMap.put(tms.getId(), tms.getTransportMethod());
		});

                //if we have lots of organization in the future we can tweak this to narrow down to orgs with batches
                List<Organization> organizations = organizationmanager.getOrganizations();
                Map<Integer, String> orgMap = new HashMap<>();
		organizations.forEach((org) -> {
		    orgMap.put(org.getId(), org.getOrgName());
		});

                //same goes for users
                List<utUser> users = usermanager.getAllUsers();
                Map<Integer, String> userMap = new HashMap<>();
		users.forEach((user) -> {
		    userMap.put(user.getId(), (user.getFirstName() + " " + user.getLastName()));
		});
		
		Calendar cal = Calendar.getInstance();

		invalidInboundBatches.stream().map((batch) -> {
		    cal.setTime(batch.getStartDateTime());
		    cal.add(Calendar.HOUR,-1);
		    batch.setStartDateTime(cal.getTime());
		    cal.setTime(batch.getEndDateTime());
		    cal.add(Calendar.HOUR,-1);
		    batch.setEndDateTime(cal.getTime());
		    //the count is in totalRecordCount already, can skip re-count
		    // batch.settotalTransactions(transactionInManager.getRecordCounts(batch.getId(), statusIds, false, false));
		    batch.setStatusValue(psMap.get(batch.getStatusId()));
		    return batch;
		}).map((batch) -> {
		    batch.setOrgName(orgMap.get(batch.getOrgId()));
		    return batch;
		}).map((batch) -> {
		    batch.setTransportMethod(tmMap.get(batch.getTransportMethodId()));
		    return batch;
		}).map((batch) -> {
		    batch.setUsersName(userMap.get(batch.getUserId()));
		    return batch;
		}).forEachOrdered((batch) -> {
		    batch.setConfigName(cMap.get(batch.getConfigId()));
		});
            }

            mav.addObject("batches", invalidInboundBatches);

        } catch (Exception e) {
            throw new Exception("Error occurred viewing the invalid inbound batches.", e);
        }
       
        return mav;

    }
    
    /**
     * The '/invalidOut' GET / Post request will serve up the list of inbound batches that errored
     *
     *
     * @param fromDate
     * @param toDate
     * @param request
     * @param response
     * @param session
     * @return 
     * @Objects	(1) An object containing all the found invalidIn
     *
     * @throws Exception
     */
    @RequestMapping(value = "/invalidOut", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView listInvalidOutBatches(
    		@RequestParam(value = "fromDate", required=false) Date fromDate,
    		@RequestParam(value = "toDate", required=false) Date toDate,
            HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

    	int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);
        
        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
         if (fromDate == null) {
	    fromDate = getMonthDate("LAST30");
        }
        if (toDate == null) {
	    toDate = getMonthDate("END-TODAY");
        } 
	searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
       
	String searchTerm = "";
	
        if ("".equals(searchParameters.getsection()) || !"invalidOut".equals(searchParameters.getsection())) {
            searchParameters.setsection("invalidOut");
	    searchParameters.setsearchTerm("");
	    session.setAttribute("searchParameters", searchParameters);
        } else {
	    searchTerm = searchParameters.getsearchTerm().trim();
	    
	    searchParameters.setsearchTerm("");
	    session.setAttribute("searchParameters", searchParameters);
        }

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/invalidOut");
	mav.addObject("searchFilter", searchTerm);
        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);
	
	/* Get system inbound summary */
        systemSummary summaryDetails = transactionInManager.generateSystemInboundSummary();
        mav.addObject("summaryDetails", summaryDetails);
	
	try {
	    Integer fetchCount = 0;
	    List<batchDownloads> invalidOutboundBatches = transactionOutManager.getBatchesByStatusIdsAndDate(fromDate, toDate, fetchCount, Arrays.asList(1, 7, 25, 29, 30, 39));

	     List<Integer> statusIds = new ArrayList();

            if (!invalidOutboundBatches.isEmpty()) {
		
		//we can map the process status so we only have to query once
                List<utConfiguration> configurationList = configurationManager.getConfigurations();
                Map<Integer, String> cMap = new HashMap<>();
		configurationList.forEach((c) -> {
		    cMap.put(c.getId(), c.getconfigName());
		});

                //we can map the process status so we only have to query once
                List<lu_ProcessStatus> processStatusList = sysAdminManager.getAllProcessStatus();
                Map<Integer, String> psMap = new HashMap<>();
		processStatusList.forEach((ps) -> {
		    psMap.put(ps.getId(), ps.getDisplayCode());
		});

                //same with transport method names
                List<TransportMethod> transporthMethods = configurationTransportManager.getTransportMethods(Arrays.asList(0, 1));
                Map<Integer, String> tmMap = new HashMap<>();
		transporthMethods.forEach((tms) -> {
		    tmMap.put(tms.getId(), tms.getTransportMethod());
		});

                //if we have lots of organization in the future we can tweak this to narrow down to orgs with batches
                List<Organization> organizations = organizationmanager.getOrganizations();
                Map<Integer, String> orgMap = new HashMap<>();
		organizations.forEach((org) -> {
		    orgMap.put(org.getId(), org.getOrgName());
		});

                //same goes for users
                List<utUser> users = usermanager.getAllUsers();
                Map<Integer, String> userMap = new HashMap<>();
		users.forEach((user) -> {
		    userMap.put(user.getId(), (user.getFirstName() + " " + user.getLastName()));
		});

                for (batchDownloads batch : invalidOutboundBatches) {

		    String fileDownloadExt = batch.getOutputFileName().substring(batch.getOutputFileName().lastIndexOf(".") + 1);
		    String newfileName = new StringBuilder().append(batch.getUtBatchName()).append(".").append(fileDownloadExt).toString();
		    
		    batch.setOutputFileName(newfileName);

                    batch.setStatusValue(psMap.get(batch.getStatusId()));

                    batch.setOrgName(orgMap.get(batch.getOrgId()));

                    batch.setTransportMethod(tmMap.get(batch.getTransportMethodId()));

                    batch.setUsersName(userMap.get(batch.getUserId()));
		    
		    batchUploads batchUploadDetails = transactionInManager.getBatchDetails(batch.getBatchUploadId());

		    batch.setFromBatchName(batchUploadDetails.getUtBatchName());
		    if (batchUploadDetails.getTransportMethodId() == 5 || batchUploadDetails.getTransportMethodId() == 1) {
			String fileExt = batchUploadDetails.getOriginalFileName().substring(batchUploadDetails.getOriginalFileName().lastIndexOf(".") + 1);
			String newsrcfileName = new StringBuilder().append(batchUploadDetails.getUtBatchName()).append(".").append(fileExt).toString();
			batch.setFromBatchFile(newsrcfileName);
		    }
		    batch.setFromOrgId(batchUploadDetails.getOrgId());

		    batch.setConfigName(cMap.get(batch.getConfigId()));
                }
            }

            mav.addObject("batches", invalidOutboundBatches);

        } catch (Exception e) {
            throw new Exception("Error occurred viewing the all downloaded batches. Error:" + e.getMessage(), e);
        }
       
        
        return mav;

    }
    
    /**
     * The '/apimessages' GET request will serve up the list of inbound rest api messages
     *
     *
     * @param pathVariables
     * @param session
     * @return 
     * @Objects	(1) An object containing all the found RestAPIMessagesIn
     *
     * @throws Exception
     */
    @RequestMapping(value={ "/apimessages", "/apimessages/{batchName}" }, method = RequestMethod.GET)
    public ModelAndView listInBoundRestAPIMessages(@PathVariable Map<String, String> pathVariables, HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/apimessages");

        if ("".equals(searchParameters.getsection()) || !"inbound".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("inbound");
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
        }

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);
	mav.addObject("batchName", pathVariables.get("batchName"));

        return mav;
    }
    
    @RequestMapping(value = "/ajax/getAPIMessagesIn", method = RequestMethod.GET)
    @ResponseBody
    public String getAPIMessagesIn(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam Date fromDate, @RequestParam Date toDate, @RequestParam String batchName) throws Exception {
	
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
	
	//Retrieve search parameters from session 
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("inbound");
	
	if(!"".equals(batchName)) {
	    searchTerm = batchName;
	}
	
        // Get all ws in 
        List<RestAPIMessagesIn> restAPIMessagesList = restfulmanager.getRestAPIMessagesInListPaged(fromDate, toDate,iDisplayStart, iDisplayLength, searchTerm, sortColumnName, sortDirection);
	
	if(restAPIMessagesList.isEmpty()) {
	    totalRecords = 0;
	}
	else {
	    totalRecords = restAPIMessagesList.get(0).getTotalMessages();
	}
	
	jsonResponse.addProperty("sEcho", sEcho);
        jsonResponse.addProperty("iTotalRecords", totalRecords);
        jsonResponse.addProperty("iTotalDisplayRecords", totalRecords);
        jsonResponse.add("aaData", gson.toJsonTree(restAPIMessagesList));
	
        return jsonResponse.toString();
    }
    
    /**
     * The '/apimessagesOut' GET request will serve up the list of outbound rest api messages
     *
     *
     * @param pathVariables
     * @param session
     * @return 
     * @Objects	(1) An object containing all the found RestAPIMessagesIn
     *
     * @throws Exception
     */
    @RequestMapping(value={ "/apimessagesOut", "/apimessagesOut/{batchName}" }, method = RequestMethod.GET)
    public ModelAndView listOutBoundRestAPIMessages(@PathVariable Map<String, String> pathVariables, HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/apimessagesOut");

        if ("".equals(searchParameters.getsection()) || !"inbound".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("inbound");
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
        }

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);
	mav.addObject("batchName", pathVariables.get("batchName"));

        return mav;

    }

    @RequestMapping(value = "/ajax/getAPIMessagesOut", method = RequestMethod.GET)
    @ResponseBody
    public String getAPIMessagesOut(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam Date fromDate, @RequestParam Date toDate, @RequestParam String batchName) throws Exception {
	
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
	
	//Retrieve search parameters from session 
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("inbound");
	
	if(!"".equals(batchName)) {
	    searchTerm = batchName;
	}
	
        List<RestAPIMessagesOut> restAPIMessagesList = restfulmanager.getRestAPIMessagesOutListPaged(fromDate, toDate,iDisplayStart, iDisplayLength, searchTerm, sortColumnName, sortDirection);
	
	if(restAPIMessagesList.isEmpty()) {
	    totalRecords = 0;
	}
	else {
	    totalRecords = restAPIMessagesList.get(0).getTotalMessages();
	}
	
	jsonResponse.addProperty("sEcho", sEcho);
        jsonResponse.addProperty("iTotalRecords", totalRecords);
        jsonResponse.addProperty("iTotalDisplayRecords", totalRecords);
        jsonResponse.add("aaData", gson.toJsonTree(restAPIMessagesList));
	
        return jsonResponse.toString();
    }

    /**
     * this displays the payload*
     * @param messageId
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/apimessage/viewPayload.do", method = RequestMethod.POST)
    public @ResponseBody
    String viewRestAPIPayload(@RequestParam Integer messageId) throws Exception {

        RestAPIMessagesIn restAPIMessage = restfulmanager.getRestAPIMessagesIn(messageId);
        String payload = "";
        if (restAPIMessage != null) {
            //payload = restAPIMessage.getPayload();
        }

        return payload;
    }
    
    /**
     * this displays the message returned headers*
     * @param messageId
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/apimessageOut/viewHeader.do", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView viewRestAPIHeaders(@RequestParam Integer messageId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/viewHeader");
        RestAPIMessagesOut restAPIMessage = restfulmanager.getRestAPIMessagesOut(messageId);
        String headers = "";
        if (restAPIMessage != null) {
            headers = restAPIMessage.getResponseMessage();
        }

        mav.addObject("headers", headers);

        return mav;

    }
    
    
    
    /**
     * The '/dashboardInBoundBatches' POST request will serve up the existing list of generated referrals and feedback reports based on a search or date
     *
     * @param request
     * @param response
     * @param toDate
     * @param session
     * @param fromDate
     * @return The list of inbound batch list
     *
     * @Objects	(1) An object containing all the found batches
     *
     * @throws Exception
     */
    @RequestMapping(value = "/dashboardInBoundBatches", method = RequestMethod.GET)
    public @ResponseBody String dashboardInBoundBatches(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam Date fromDate, @RequestParam Date toDate) throws Exception {

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
	
	// Retrieve search parameters from session
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
	
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("himdashboard");
	
        /* Get all inbound transactions */
        toDate = DateUtils.addDays(toDate, 1);
	
	List<batchUploads> batchUploadList = transactionInManager.getAllUploadBatchesPaged(fromDate, toDate,iDisplayStart, iDisplayLength, searchTerm, sortColumnName, sortDirection);
	
	List<batchUploads> batchUploadsToReturn = new ArrayList<>();
	
	if(batchUploadList.isEmpty()) {
	    totalRecords = 0;
	}
	else {
	    totalRecords = batchUploadList.get(0).getTotalMessages();
	    
	    for(batchUploads batchUpload : batchUploadList) {
		batchUploadsToReturn.add(batchUpload);
	    }
	}
	
	List<watchlistEntry> watchListEntries = configurationManager.getWatchListEntries(fromDate, toDate);
	
	if(!watchListEntries.isEmpty()) {
	    totalRecords = totalRecords + watchListEntries.size();
	    
	    for(watchlistEntry entry : watchListEntries) {
		batchUploads watchlistEntry = new batchUploads();
		watchlistEntry.setOrgName(entry.getOrgName());
		watchlistEntry.setTransportMethod(entry.getTransportMethod());
		watchlistEntry.setConfigName(entry.getConfigName());
		watchlistEntry.setDateSubmitted(entry.getDateCreated());
		watchlistEntry.setUploadType("Watch List Entry");

		batchUploadsToReturn.add(watchlistEntry);
	    }
	}
	
	jsonResponse.addProperty("sEcho", sEcho);
        jsonResponse.addProperty("iTotalRecords", totalRecords);
        jsonResponse.addProperty("iTotalDisplayRecords", totalRecords);
        jsonResponse.add("aaData", gson.toJsonTree(batchUploadsToReturn));
	
        return jsonResponse.toString();
    }
    
    /**
     * The '/dashboardOutBoundBatches' POST request will serve up the existing list of generated referrals and feedback reports based on a search or date
     *
     * @param fromDate
     * @param toDate
     * @param request
     * @param response
     * @param session
     * @return The list of inbound batch list
     *
     * @Objects	(1) An object containing all the found batches
     *
     * @throws Exception
     */
    @RequestMapping(value = "/dashboardOutBoundBatches", method = RequestMethod.GET)
    public @ResponseBody String dashboardOutBoundBatches(@RequestParam Date fromDate, @RequestParam Date toDate, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

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
	
	// Retrieve search parameters from session
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
	
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("himdashboard");
	
        /* Get all inbound transactions */
        toDate = DateUtils.addDays(toDate, 1);
	
	List<batchDownloads> outboundBatches = transactionOutManager.getAllSentBatchesPaged(fromDate, toDate,iDisplayStart, iDisplayLength, searchTerm, sortColumnName, sortDirection);
	
	if(outboundBatches.isEmpty()) {
	    totalRecords = 0;
	}
	else {
	    totalRecords = outboundBatches.get(0).getTotalMessages();
	}
	
	jsonResponse.addProperty("sEcho", sEcho);
        jsonResponse.addProperty("iTotalRecords", totalRecords);
        jsonResponse.addProperty("iTotalDisplayRecords", totalRecords);
        jsonResponse.add("aaData", gson.toJsonTree(outboundBatches));
	
        return jsonResponse.toString();
    }
    
    /**
     * The '/dashboardGenericBatches' POST request will serve up the existing list of generated referrals and feedback reports based on a search or date
     *
     * @param fromDate
     * @param toDate
     * @param request
     * @param response
     * @param session
     * @return The list of inbound batch list
     *
     * @Objects	(1) An object containing all the found batches
     *
     * @throws Exception
     */
    @RequestMapping(value = "/dashboardGenericBatches", method = RequestMethod.POST)
    public @ResponseBody ModelAndView dashboardGenericBatches(@RequestParam Date fromDate, @RequestParam Date toDate, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/genericdashboard");
	
	/* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
	
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("himdashboard");
	
        /* Get all inbound transactions */
        toDate = DateUtils.addDays(toDate, 1);
	
	//try {
	    
	    List<batchUploads> dashboardUploads = new ArrayList<>();

	    //Need to get any watch list entries
	    List<watchlistEntry> watchlistEntries = configurationManager.getGenericWatchListEntries(fromDate, toDate);
	    
	    if(watchlistEntries != null) {
		if(!watchlistEntries.isEmpty()) {
		    watchlistEntries.stream().map((entry) -> {
			batchUploads watchlistEntry = new batchUploads();
			watchlistEntry.setDateSubmitted(entry.getDateCreated());
			if(entry.isWatchListCompleted()) {
			    watchlistEntry.setDashboardRowColor("table-success");
			}
			else {
			    watchlistEntry.setDashboardRowColor("table-primary");
			}
			watchlistEntry.setId(entry.getId());
			watchlistEntry.setUploadType("Watch List Entry");
			watchlistEntry.setEntryMessage(entry.getEntryMessage());
			watchlistEntry.setWatchListCompleted(entry.isWatchListCompleted());
			watchlistEntry.setWatchListEntryId(entry.getWatchlistentryId());
			return watchlistEntry;			
		    }).forEachOrdered((watchlistEntry) -> {
			dashboardUploads.add(watchlistEntry);
		    });
		}
	    }

            mav.addObject("genericbatches", dashboardUploads);

        //} catch (Exception e) {
            //throw new Exception("Error occurred viewing the dashboard inbound messages.", e);
       // }

        return mav;
    }
    
    /**
     * The 'completeGenericWatchList' function will process the batch according to the option submitted by admin
     * @param session
     * @param entryId
     * @param isChecked
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/completeGenericWatchList", method = RequestMethod.POST)
    public @ResponseBody
    boolean completeGenericWatchList(HttpSession session, @RequestParam(value = "entryId", required = true) Integer entryId,
            @RequestParam(value = "isChecked", required = true) boolean isChecked) throws Exception {

         watchlistEntry entryDetails = configurationManager.getWatchListEntry(entryId);
	 
	 if(isChecked) {
	     entryDetails.setWatchListCompleted(true);
	 }
	 else {
	     entryDetails.setWatchListCompleted(false);
	 }
	 configurationManager.insertDashboardWatchListEntry(entryDetails);
	 
        return true;
    }
    
    
    /**
     * The '/outbound/auditReport/{batchName}' GET request will retrieve the audit report that is associated to the clicked batch
     *
     * @param batchName	The name of the batch to retrieve transactions for
     * @return The audit report for the batch
     *
     * @Objects	(1) An object containing all the errored transactions
     *
     * @throws Exception
     */
    @RequestMapping(value = "/outbound/auditReport/{batchName}", method = RequestMethod.GET)
    public ModelAndView viewOutboundAuditReport(@PathVariable String batchName) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/outbound/auditReport");
        boolean canCancel = false;
        boolean canReset = false;
	boolean canEdit = false;
	boolean canSend = false;
        boolean showButtons = true;

        /* Get the details of the batch */
	batchDownloads batchDetails = transactionOutManager.getBatchDetailsByBatchName(batchName);
	
	/* Get the details of the batch */
	batchUploads batchUploadDetails = transactionInManager.getBatchDetails(batchDetails.getBatchUploadId());
       
        if (batchDetails != null) {
	    
	    utConfiguration configDetails = configurationManager.getConfigurationById(batchDetails.getConfigId());
	    mav.addObject("configDetails",configDetails);
	    
	    Organization orgDetails = organizationmanager.getOrganizationById(batchDetails.getOrgId());
            batchDetails.setTgtorgName(orgDetails.getOrgName());
	    
	    Organization srcorgDetails = organizationmanager.getOrganizationById(batchUploadDetails.getOrgId());
            batchDetails.setOrgName(srcorgDetails.getOrgName());
	    
            lu_ProcessStatus processStatus = sysAdminManager.getProcessStatusById(batchDetails.getStatusId());
            batchDetails.setStatusValue(processStatus.getDisplayCode());

            List<Integer> cancelStatusList = Arrays.asList(25,30,61,64,59);
            if (cancelStatusList.contains(batchDetails.getStatusId())) {
                canCancel = true;
            }

            List<Integer> resetStatusList = Arrays.asList(28,30,31,41,58); //DNP (21) is not a final status for admin
	    if (!resetStatusList.contains(batchDetails.getStatusId()) && configDetails.getMessageTypeId() != 2) {
                canReset = true;
            }
	    
	    if(batchDetails.getStatusId() == 64 || batchDetails.getStatusId() == 59) {
		canSend = true;
	    }
	    
            batchDetails.setConfigName(configurationManager.getMessageTypeNameByConfigId(batchDetails.getConfigId()));
            
            mav.addObject("batchDetails", batchDetails);
	    
            if (batchDetails.getTotalErrorCount() > 0) {
		List<batchErrorSummary> batchErrorSummary = transactionInManager.getBatchErrorSummary(batchDetails.getId(),"outbound");
		mav.addObject("batchErrorSummary", batchErrorSummary);
	    }
	    
	    //Check to see if we have any dropped values
	    List<batchDownloadDroppedValues> droppedValues = transactionOutManager.getBatchDroppedValues(batchDetails.getId());
            mav.addObject("batchDroppedValues", droppedValues);
	   
	    
        } else {
            mav.addObject("doesNotExist", true);
        }
	
        mav.addObject("canCancel", canCancel);
        mav.addObject("canReset", canReset);
        mav.addObject("canEdit", canEdit);
        mav.addObject("canSend", canSend);
	mav.addObject("batchDownload",true);
	
	if(canReset || canCancel || canEdit || canSend) {
	    showButtons = true;
	}
	
	mav.addObject("showButtons", showButtons);

        return mav;
    }
    
    /**
     * The 'outboundBatchOptions' function will process the batch according to the option submitted by admin
     * @param session
     * @param batchId
     * @param authentication
     * @param batchOption
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/outboundBatchOptions", method = RequestMethod.POST)
    public @ResponseBody
    boolean outboundBatchOptions(HttpSession session,
            @RequestParam(value = "batchId", required = true) Integer batchId, Authentication authentication,
            @RequestParam(value = "batchOption", required = true) String batchOption) throws Exception {

        String strBatchOption = "";
        utUser userInfo = usermanager.getUserByUserName(authentication.getName());
	
	batchDownloads batchDetails = transactionOutManager.getBatchDetails(batchId);

        if (userInfo != null && batchDetails != null) {
	    
	    //Release a manual target batch
	    if(batchOption.equalsIgnoreCase("releaseBatch")) {
		strBatchOption = "Release Outbound Batch";
		transactionOutManager.updateTargetBatchStatus(batchId, 61, "startDateTime");
	    }
	    
	    else if (batchOption.equalsIgnoreCase("cancel")) {
		strBatchOption = "Cancelled Outbound Batch";
		
		//need to cancel target batch
                transactionOutManager.updateTargetBatchStatus(batchId, 32, "startDateTime");
		
		//Delete batch target tables
		transactionOutManager.deleteBatchDownloadTables(batchId);

            } 
	    else if (batchOption.equalsIgnoreCase("reset")) {
                strBatchOption = "Reset Outbound Batch";
		
		//Delete all target tables
		transactionOutManager.deleteBatchDownloadTables(batchId);
		
		//Rest total record and error counts
		batchDetails.setTotalRecordCount(0);
		batchDetails.setTotalErrorCount(0);
		transactionOutManager.submitBatchDownloadChanges(batchDetails);
		
		//Need to check to see if the transactiontranslatedin_BatchUploadId table exists
		boolean transactionInTableForBatchExists = transactionOutManager.chechForTransactionInTable(batchDetails.getBatchUploadId());
		
		if(transactionInTableForBatchExists) {
		    //Reset status to 61 to start the outbound process over
		    transactionOutManager.updateTargetBatchStatus(batchId, 61, "startDateTime");
		}
		else {
		    transactionOutManager.updateTargetBatchStatus(batchId, 66, "startDateTime");
		    
		    //Clear transaction counts
		    transactionInManager.resetTransactionCounts(batchDetails.getBatchUploadId());
		    
		    batchUploads batchUploadDetails = transactionInManager.getBatchDetails(batchDetails.getBatchUploadId());
		    
		    //Need to move the archive file back to the loading directory
		    File archiveFile = new File(myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + "archive_" + batchUploadDetails.getUtBatchName() + batchUploadDetails.getOriginalFileName().substring(batchUploadDetails.getOriginalFileName().lastIndexOf(".")));
		    File archiveDecFile = new File(myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + batchUploadDetails.getUtBatchName() + "_dec" + batchUploadDetails.getOriginalFileName().substring(batchUploadDetails.getOriginalFileName().lastIndexOf(".")));
		   
		    //Need to get the configuration details and transport method
		    configurationTransport transportDetails = configurationTransportManager.getTransportDetails(batchUploadDetails.getConfigId());
		    
		    File encodedUploadedFile = new File(myProps.getProperty("ut.directory.utRootDir") + transportDetails.getfileLocation().replace("/HELProductSuite/universalTranslator/","") + "encoded_" + batchUploadDetails.getUtBatchName());
		    
		    //File Dropped
		    if(transportDetails.gettransportMethodId() == 10 || transportDetails.gettransportMethodId() == 13) {
			List<configurationFileDropFields> fileDropDetails = configurationTransportManager.getTransFileDropDetails(transportDetails.getId());
			
			if(fileDropDetails != null) {
			    for(configurationFileDropFields fileDropDetail : fileDropDetails) {
				if(fileDropDetail.getMethod() == 1) {
				    
				    if(archiveFile.renameTo(new File(myProps.getProperty("ut.directory.utRootDir") + fileDropDetail.getDirectory().replace("/HELProductSuite/universalTranslator/","") + batchUploadDetails.getOriginalFileName()))) {
					archiveFile.delete();
					
					if(archiveDecFile.exists()) {
					    archiveDecFile.delete();
					}
					if(encodedUploadedFile.exists()) {
					    encodedUploadedFile.delete();
					}
				    }
				}
			    }
			}
		    }
		    //SFTP
		    else if(transportDetails.gettransportMethodId() == 8) {
			List<configurationFTPFields> ftpDetails = configurationTransportManager.getTransportFTPDetails(transportDetails.getId());
			
			if(ftpDetails != null) {
			    for(configurationFTPFields ftpDetail : ftpDetails) {
				if(ftpDetail.getmethod()== 1) {
				    
				    if(archiveFile.renameTo(new File(myProps.getProperty("ut.directory.utRootDir") + ftpDetail.getdirectory().replace("/sFTP","sFTP").replace("/HELProductSuite/universalTranslator/","") + batchUploadDetails.getOriginalFileName()))) {
					archiveFile.delete();
					
					if(archiveDecFile.exists()) {
					    archiveDecFile.delete();
					}
					if(encodedUploadedFile.exists()) {
					    encodedUploadedFile.delete();
					}
				    }
				}
			    }
			}
		    }
		    else {
			if(archiveFile.renameTo(new File(myProps.getProperty("ut.directory.utRootDir") + transportDetails.getfileLocation().replace("/HELProductSuite/universalTranslator/","") + batchUploadDetails.getOriginalFileName()))) {
			    archiveFile.delete();

			    if(archiveDecFile.exists()) {
				archiveDecFile.delete();
			    }
			    if(encodedUploadedFile.exists()) {
				encodedUploadedFile.delete();
			    }
			}
		    }
		    
		    //log user activity
		    utUserActivity ua = new utUserActivity();
		    ua.setUserId(0);
		    ua.setFeatureId(0);
		    ua.setAccessMethod("System");
		    ua.setPageAccess("/outnboundBatchOptions");
		    ua.setActivity("Inbound batchId:"+batchDetails.getId() + " will be reprocessed due to outbound batch being reset and inbound batch tables have been removed.");
		    ua.setBatchUploadId(batchDetails.getBatchUploadId());
		    usermanager.insertUserLog(ua);
		    
		    transactionInManager.updateBatchStatus(batchDetails.getBatchUploadId(), 35, "startDateTime");
		}
            } 
        }

        //log user activity
        utUserActivity ua = new utUserActivity();
        ua.setUserId(userInfo.getId());
        ua.setAccessMethod("POST");
        ua.setPageAccess("/outnboundBatchOptions");
        ua.setActivity("Admin - " + strBatchOption);
	ua.setBatchDownloadId(batchId);
        usermanager.insertUserLog(ua);
        return true;
    }
    
    
    /**
     * The '/viewDirectDetails{batchUploadId}' function will return the details of the selected batch uploaded message received from a HISP via DIRECT.The results will be displayed in the overlay.
     *
     * @param batchUploadId
     * @return 
     * @throws java.lang.Exception
     * @Param	batchUploadId This will hold the id of the selected batch uploaded message
     *
     * @Return	This function will return the direct details view.
     */
    @RequestMapping(value = "/viewDirectDetails{batchUploadId}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView viewDirectDetails(@PathVariable int batchUploadId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/directHISPDetails");

        directmessagesin directMessageDetails = transactionInManager.getDirectAPIMessagesByBatchUploadId(batchUploadId);
	mav.addObject("directMessageDetails",directMessageDetails);

        return mav;
    }
    
    /**
     * The '/directmessages' GET request will serve up the list of inbound direct messages
     *
     *
     * @param pathVariables
     * @param session
     * @return 
     * @Objects	(1) An object containing all the found directmessagesIn
     *
     * @throws Exception
     */
    @RequestMapping(value={ "/directmessages", "/directmessages/{batchName}" }, method = RequestMethod.GET)
    public ModelAndView listInBoundDirectMessages(@PathVariable Map<String, String> pathVariables, HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/directmessages");

        if ("".equals(searchParameters.getsection()) || !"inbound".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("inbound");
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
        }

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);
	mav.addObject("batchName", pathVariables.get("batchName"));

        return mav;
    }
    
    @RequestMapping(value = "/ajax/getDirectMessagesIn", method = RequestMethod.GET)
    @ResponseBody
    public String getDirectMessagesIn(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam Date fromDate, @RequestParam Date toDate, @RequestParam String batchName) throws Exception {
	
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
	
	//Retrieve search parameters from session 
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("inbound");
	
	if(!"".equals(batchName)) {
	    searchTerm = batchName;
	}
	
        // Get all direct messages in 
        List<directmessagesin> directMessagesList = directmanager.getDirectMessagesInListPaged(fromDate, toDate,iDisplayStart, iDisplayLength, searchTerm, sortColumnName, sortDirection);
	
	if(directMessagesList.isEmpty()) {
	    totalRecords = 0;
	}
	else {
	    totalRecords = directMessagesList.get(0).getTotalMessages();
	}
	
	jsonResponse.addProperty("sEcho", sEcho);
        jsonResponse.addProperty("iTotalRecords", totalRecords);
        jsonResponse.addProperty("iTotalDisplayRecords", totalRecords);
        jsonResponse.add("aaData", gson.toJsonTree(directMessagesList));
	
        return jsonResponse.toString();
    }
    
    /**
     * The '/directmessagesOut' GET request will serve up the list of outbound direct messages
     *
     *
     * @param pathVariables
     * @param session
     * @return 
     * @Objects	(1) An object containing all the found directmessagesOut
     *
     * @throws Exception
     */
    @RequestMapping(value={ "/directmessagesOut", "/directmessagesOut/{batchName}" }, method = RequestMethod.GET)
    public ModelAndView listOutBoundDirectMessages(@PathVariable Map<String, String> pathVariables, HttpSession session) throws Exception {

        int year = 114;
        int month = 0;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("LAST30");
        Date toDate = getMonthDate("END-TODAY");

        /* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activity/directmessagesOut");

        if ("".equals(searchParameters.getsection()) || !"outbound".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("outbound");
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
        }

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);
	mav.addObject("batchName", pathVariables.get("batchName"));

        return mav;

    }

    @RequestMapping(value = "/ajax/getDirectMessagesOut", method = RequestMethod.GET)
    @ResponseBody
    public String getDirectMessagesOut(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam Date fromDate, @RequestParam Date toDate, @RequestParam String batchName) throws Exception {
	
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
	
	//Retrieve search parameters from session 
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
        searchParameters.setfromDate(fromDate);
        searchParameters.settoDate(toDate);
        searchParameters.setsection("inbound");
	
	if(!"".equals(batchName)) {
	    searchTerm = batchName;
	}
	
        List<directmessagesout> directMessagesList = directmanager.getDirectMessagesOutListPaged(fromDate, toDate,iDisplayStart, iDisplayLength, searchTerm, sortColumnName, sortDirection);
	
	if(directMessagesList.isEmpty()) {
	    totalRecords = 0;
	}
	else {
	    totalRecords = directMessagesList.get(0).getTotalMessages();
	}
	
	jsonResponse.addProperty("sEcho", sEcho);
        jsonResponse.addProperty("iTotalRecords", totalRecords);
        jsonResponse.addProperty("iTotalDisplayRecords", totalRecords);
        jsonResponse.add("aaData", gson.toJsonTree(directMessagesList));
	
        return jsonResponse.toString();
    }
    
    /**
     * The '/viewDirectDetailsById{directMessageId}' function will return the details of the selected batch uploaded message received from a HISP via DIRECT.The results will be displayed in the overlay.
     *
     * @param directMessageId
     * @return 
     * @throws java.lang.Exception
     * @Param	batchUploadId This will hold the id of the selected batch uploaded message
     *
     * @Return	This function will return the direct details view.
     */
    @RequestMapping(value = "/viewDirectDetailsById{directMessageId}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView viewDirectDetailsById(@PathVariable int directMessageId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/directHISPDetails");

        directmessagesin directMessageDetails = transactionInManager.getDirectAPIMessagesById(directMessageId);
	mav.addObject("directMessageDetails",directMessageDetails);

        return mav;
    }
    
    /**
     * The '/viewDirectDetailsOutById{directMessageId}' function will return the details of the selected batch uploaded message received from a HISP via DIRECT.The results will be displayed in the overlay.
     *
     * @param directMessageId
     * @return 
     * @throws java.lang.Exception
     * @Param	batchUploadId This will hold the id of the selected batch uploaded message
     *
     * @Return	This function will return the direct details view.
     */
    @RequestMapping(value = "/viewDirectDetailsOutById{directMessageId}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView viewDirectDetailsOutById(@PathVariable int directMessageId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/directOutDetails");

        directmessagesout directMessageDetails = transactionOutManager.getDirectAPIMessagesById(directMessageId);
	mav.addObject("directMessageDetails",directMessageDetails);

        return mav;
    }
    
    /**
     * this displays the payload*
     * @param batchId
     * @param totalErrors
     * @param type
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/loadDroppedValues.do", method = RequestMethod.GET)
    public @ResponseBody ModelAndView loadDroppedValues(
	    @RequestParam Integer batchId,
	    @RequestParam Integer totalErrors,
	    @RequestParam String type) throws Exception {

	ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/auditReportDroppedValues");
	
	List<String> customCols = new ArrayList<>();
	
	String sql = "";
	
	customCols.add("Row No.");
	customCols.add("Field No.");
	
	List reportableFields = null;
	
	if("inbound".equals(type)) {
	    reportableFields = transactionInManager.getErrorReportField(batchId);
	}
	else {
	    reportableFields = transactionOutManager.getErrorReportField(batchId);
	}
	
	customCols.add("Column Name");
	customCols.add("Client Identifier");
	customCols.add("Field Value");
	
	if("inbound".equals(type)) {
	    sql = "select a.transactionInRecordsId as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name,a.entity3Id as clientIdentifier,a.fieldValue as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
	    + "from batchuploaddroppedvalues a "
	    + "where a.batchUploadId = " + batchId + " order by a.id asc limit 50 ";
	}
	else {
	    sql = "select a.transactionOutRecordsId as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.entity3Id as clientIdentifier,a.fieldValue as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
	    + "from batchdownloaddroppedvalues a "
	    + "where a.batchDownloadId = " + batchId + " order by a.id asc limit 50 ";
	}
	
	if(reportableFields != null) {
	    Iterator reportableFieldsIt = reportableFields.iterator();
	
	    while (reportableFieldsIt.hasNext()) {
		Object rptFieldrow[] = (Object[]) reportableFieldsIt.next();
		customCols.add(rptFieldrow[0].toString());
		customCols.add(rptFieldrow[1].toString());
		customCols.add(rptFieldrow[2].toString());
		customCols.add(rptFieldrow[3].toString());
	    }
	}
		
	mav.addObject("customCols", customCols);
	mav.addObject("totalErrors",totalErrors);
	
	List errors = transactionInManager.getErrorDataBySQLStmt(sql);
	
	mav.addObject("errors", errors);
        
        return mav;

    }
    
    /**
     * The 'createAuditErrorsToExcel.do' method will create an excel file containing all dropped values, system errors and transaction errors for
     * the passed in batch.
     * @param batchName
     * @param type
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/createAuditErrorsToExcel.do", method = RequestMethod.GET)
    @ResponseBody
    public String createAuditErrorsToExcel(@RequestParam String batchName, @RequestParam String type) throws Exception {
	
	String fileName = "";
	
	try {
	    List<batchErrorSummary> batchErrorSummary = null;
	    List<batchErrorSummary> batchSystemErrors = null;
	    List<batchUploadDroppedValues> inboundDroppedValues = null;
	    List<batchDownloadDroppedValues> outboundDroppedValues = null;
	    List reportableFields = null;
	    Integer batchId = 0;
	    
	    if("inbound".equals(type)) {
		
		/* Get the details of the batch */
		batchUploads batchDetails = transactionInManager.getBatchDetailsByBatchName(batchName);
		batchId = batchDetails.getId();
		
		reportableFields = transactionInManager.getErrorReportField(batchId);

		if (batchDetails.getErrorRecordCount() > 0) {
		    batchErrorSummary = transactionInManager.getBatchErrorSummary(batchId,"inbound");
		}

		//Check to see if we have any dropped values
		inboundDroppedValues = transactionInManager.getBatchDroppedValues(batchId);

	    }
	    else {
		batchDownloads batchDetails = transactionOutManager.getBatchDetailsByBatchName(batchName);
		batchId = batchDetails.getId();
		
		reportableFields = transactionOutManager.getErrorReportField(batchId);
		
		if (batchDetails.getTotalErrorCount() > 0) {
		    batchErrorSummary = transactionInManager.getBatchErrorSummary(batchDetails.getId(),"outbound");
		}

		//Check to see if we have any dropped values
		outboundDroppedValues = transactionOutManager.getBatchDroppedValues(batchDetails.getId());
	    }
	    
	    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
	    Date date = new Date();

	    fileName = batchName + "-auditErrors";
	    
	    File file = new File("/tmp/" + fileName + ".xlsx");
	    file.createNewFile();

	    FileInputStream fileInput = null;
	    fileInput = new FileInputStream(file);

	    FileWriter fw = null;

	    try {
		fw = new FileWriter(file, true);
	    } catch (IOException ex) {

	    }

	    StringBuilder exportRow = new StringBuilder();

	    String required = "";
	    String usefield = "Y";
	    String validationValue = "None";

	    Workbook wb = new XSSFWorkbook();
	    Sheet sheet = wb.createSheet("sheet1");

	    Integer rowNum = 0;
	    Integer cellNum = 0;

	    String sql = "";
	    String errorType = "";
	    List errors = null;
	    Row currentRow;
	    
	    if(inboundDroppedValues != null) {
		if(!inboundDroppedValues.isEmpty()) {
		    errorType = "Dropped Crosswalk Values";
		    
		    sql = "select 'source' as fromOutboundConfig, a.transactionInRecordsId as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name,a.entity3Id as clientIdentifier,a.fieldValue as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
		    + "from batchuploaddroppedvalues a "
		    + "where a.batchUploadId = " + batchId + " order by a.id asc";
		    
		    currentRow = sheet.createRow(rowNum);
		    currentRow.createCell(cellNum).setCellValue("Error Type");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("S/T Config");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("Row No.");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("Field No.");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("Column Name");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("Client Identifier");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("Field Value");

		    if(reportableFields != null) {
			Iterator reportableFieldsIt = reportableFields.iterator();

			while (reportableFieldsIt.hasNext()) {
			    Object rptFieldrow[] = (Object[]) reportableFieldsIt.next();
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue(rptFieldrow[0].toString());
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue(rptFieldrow[1].toString());
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue(rptFieldrow[2].toString());
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue(rptFieldrow[3].toString());
			}
		    }

		    if(!"".equals(sql)) {
			errors = transactionInManager.getErrorDataBySQLStmt(sql);

			 if(errors != null) {
			     if(!errors.isEmpty()) {
				 Iterator errorsIt = errors.iterator();

				 while (errorsIt.hasNext()) {
				     rowNum++;
				     currentRow = sheet.createRow(rowNum);
				     cellNum = 0;

				     Object errorsRow[] = (Object[]) errorsIt.next();
				     currentRow.createCell(cellNum).setCellValue(errorType);
				     cellNum++;
				     currentRow.createCell(cellNum).setCellValue(errorsRow[0].toString());
				     cellNum++;
				     currentRow.createCell(cellNum).setCellValue(errorsRow[1].toString());
				     cellNum++;
				     currentRow.createCell(cellNum).setCellValue(errorsRow[2].toString());
				     cellNum++;
				     currentRow.createCell(cellNum).setCellValue(errorsRow[3].toString());
				     cellNum++;
				     currentRow.createCell(cellNum).setCellValue(errorsRow[4].toString());
				     cellNum++;
				     
				     if(errorsRow[5] != null) {
					currentRow.createCell(cellNum).setCellValue(errorsRow[5].toString());
				     }
				     else {
					currentRow.createCell(cellNum).setCellValue("");
				     }
				     cellNum++;
				     
				     if(errorsRow[6] != null) {
					currentRow.createCell(cellNum).setCellValue(errorsRow[6].toString());
				     }
				     else {
					currentRow.createCell(cellNum).setCellValue("");
				     }
				     cellNum++;
				     
				     if(errorsRow[7] != null) {
					currentRow.createCell(cellNum).setCellValue(errorsRow[7].toString());
				     }
				     else {
					currentRow.createCell(cellNum).setCellValue("");
				     }
				     cellNum++;
				     
				     if(errorsRow[8] != null) {
					currentRow.createCell(cellNum).setCellValue(errorsRow[8].toString());
				     }
				     else {
					currentRow.createCell(cellNum).setCellValue("");
				     }
				     cellNum++;
				     
				     if(errorsRow[9] != null) {
					currentRow.createCell(cellNum).setCellValue(errorsRow[9].toString());
				     }
				     else {
					currentRow.createCell(cellNum).setCellValue("");
				     }
				}
				 
				rowNum++;
				rowNum++;
				rowNum++;
			     }
			 }
		     }

		}
	    }
	    
	    if(outboundDroppedValues != null) {
		if(!outboundDroppedValues.isEmpty()) {
		    errorType = "Dropped Crosswalk Values";
		    
		    sql = "select 'target' as fromOutboundConfig, a.transactionInRecordsId as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name,a.entity3Id as clientIdentifier,a.fieldValue as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
		    + "from batchdownloaddroppedvalues a "
		    + "where a.batchDownloadId = " + batchId + " order by a.id asc";
		    
		    currentRow = sheet.createRow(rowNum);
		    currentRow.createCell(cellNum).setCellValue("Error Type");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("S/T Config");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("Row No.");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("Field No.");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("Column Name");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("Client Identifier");
		    cellNum++;
		    currentRow.createCell(cellNum).setCellValue("Field Value");

		    if(reportableFields != null) {
			Iterator reportableFieldsIt = reportableFields.iterator();

			while (reportableFieldsIt.hasNext()) {
			    Object rptFieldrow[] = (Object[]) reportableFieldsIt.next();
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue(rptFieldrow[0].toString());
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue(rptFieldrow[1].toString());
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue(rptFieldrow[2].toString());
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue(rptFieldrow[3].toString());
			}
		    }

		    if(!"".equals(sql)) {
			errors = transactionInManager.getErrorDataBySQLStmt(sql);

			 if(errors != null) {
			     if(!errors.isEmpty()) {
				 Iterator errorsIt = errors.iterator();

				 while (errorsIt.hasNext()) {
				     rowNum++;
				     currentRow = sheet.createRow(rowNum);
				     cellNum = 0;

				     Object errorsRow[] = (Object[]) errorsIt.next();
				     currentRow.createCell(cellNum).setCellValue(errorType);
				     cellNum++;
				     currentRow.createCell(cellNum).setCellValue(errorsRow[0].toString());
				     cellNum++;
				     currentRow.createCell(cellNum).setCellValue(errorsRow[1].toString());
				     cellNum++;
				     currentRow.createCell(cellNum).setCellValue(errorsRow[2].toString());
				     cellNum++;
				     currentRow.createCell(cellNum).setCellValue(errorsRow[3].toString());
				     cellNum++;
				     currentRow.createCell(cellNum).setCellValue(errorsRow[4].toString());
				     cellNum++;
				     
				     if(errorsRow[5] != null) {
					currentRow.createCell(cellNum).setCellValue(errorsRow[5].toString());
				     }
				     else {
					currentRow.createCell(cellNum).setCellValue("");
				     }
				     cellNum++;
				     
				     if(errorsRow[6] != null) {
					currentRow.createCell(cellNum).setCellValue(errorsRow[6].toString());
				     }
				     else {
					currentRow.createCell(cellNum).setCellValue("");
				     }
				     cellNum++;
				     
				     if(errorsRow[7] != null) {
					currentRow.createCell(cellNum).setCellValue(errorsRow[7].toString());
				     }
				     else {
					currentRow.createCell(cellNum).setCellValue("");
				     }
				     cellNum++;
				     
				     if(errorsRow[8] != null) {
					currentRow.createCell(cellNum).setCellValue(errorsRow[8].toString());
				     }
				     else {
					currentRow.createCell(cellNum).setCellValue("");
				     }
				     cellNum++;
				     
				     if(errorsRow[9] != null) {
					currentRow.createCell(cellNum).setCellValue(errorsRow[9].toString());
				     }
				     else {
					currentRow.createCell(cellNum).setCellValue("");
				     }
				}
				 
				rowNum++;
				rowNum++;
				rowNum++;
			     }
			 }
		    }
		}
	    }
	    
	    if(batchErrorSummary != null) {
		if(!batchErrorSummary.isEmpty()) {
		    errors = null;
		    Integer errorId = 0;
		    
		    for(batchErrorSummary error : batchErrorSummary) {
			
			if(errorId == 0 || errorId != error.getErrorId()) {
			    
			    if(errorId > 0) {
				rowNum++;
				rowNum++;
				rowNum++;
			    }
			    
			    errorId = error.getErrorId();
			    cellNum = 0;

			    currentRow = sheet.createRow(rowNum);
			    currentRow.createCell(cellNum).setCellValue("Error Type");
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue("S/T Config");
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue("Row No.");
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue("Field No.");
			    cellNum++;
			    currentRow.createCell(cellNum).setCellValue("Column Name");
			    cellNum++;
			    if(errorId == 2) {
				currentRow.createCell(cellNum).setCellValue("Validation");
				cellNum++;
			    }
			    else if(errorId == 3) {
				currentRow.createCell(cellNum).setCellValue("Crosswalk Name");
				cellNum++;
			    }
			    else if(errorId == 4) {
				currentRow.createCell(cellNum).setCellValue("Macro Name");
				cellNum++;
			    }
			    else {
				currentRow.createCell(cellNum).setCellValue("");
				cellNum++;
			    }
			    currentRow.createCell(cellNum).setCellValue("Field Value");

			    if(reportableFields != null) {
				Iterator reportableFieldsIt = reportableFields.iterator();

				while (reportableFieldsIt.hasNext()) {
				    Object rptFieldrow[] = (Object[]) reportableFieldsIt.next();
				    cellNum++;
				    currentRow.createCell(cellNum).setCellValue(rptFieldrow[0].toString());
				    cellNum++;
				    currentRow.createCell(cellNum).setCellValue(rptFieldrow[1].toString());
				    cellNum++;
				    currentRow.createCell(cellNum).setCellValue(rptFieldrow[2].toString());
				    cellNum++;
				    currentRow.createCell(cellNum).setCellValue(rptFieldrow[3].toString());
				}
			    }
			}
			
			
			//Set the custom columns based on the error selected
			switch(errorId) {
			    case 1:
				errorType = "Required Field Error";

				if("inbound".equals(type)) {
				    sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchuploadauditerrors a left outer  join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				else {
				    sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType,a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchdownloadauditerrors a left outer  join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				
				break;

			    case 2:
				errorType = "Validation Error";
				
				if("inbound".equals(type)) {
				    sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name, a.errorDetails as validation_type, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchuploadauditerrors a left outer  join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				else {

				    sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name, a.errorDetails as validation_type, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchdownloadauditerrors a left outer  join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				
				break;

			    case 3:
				errorType = "Crosswalk Error";

				if("inbound".equals(type)) {	

				    sql = "select fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name, a.errorDetails as crosswalk, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchuploadauditerrors a left outer join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				else {
				    sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name, a.errorDetails as crosswalk, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchdownloadauditerrors a left outer join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				
				break;

			    case 4:
				errorType = "Macro Error";
				
				if("inbound".equals(type)) {
				    sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.errorDetails as macro, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchuploadauditerrors a left outer  join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				else {
				    sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.errorDetails as macro, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchdownloadauditerrors a left outer  join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				
				break;
				
			    case 41:
				errorType = "Zip Code Check";

				if("inbound".equals(type)) {
				    sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchuploadauditerrors a left outer  join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				else {
				    sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType,a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchdownloadauditerrors a left outer  join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				
				break;
			
			    case 46:
				errorType = "Invalid Date Format";

				if("inbound".equals(type)) {
				    sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchuploadauditerrors a left outer  join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				else {
				    sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType,a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
					+ "from batchdownloadauditerrors a left outer  join "
					+ "configurationmessagespecs b on a.configId = b.configId "
					+ "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
				}
				
				break;
			}
			
			if(!"".equals(sql)) {
			   errors = transactionInManager.getErrorDataBySQLStmt(sql);
			   
			    if(errors != null) {
				if(!errors.isEmpty()) {
				    
				    Iterator errorsIt = errors.iterator();

				    while (errorsIt.hasNext()) {
					rowNum++;
					currentRow = sheet.createRow(rowNum);
					cellNum = 0;
					
					Object errorsRow[] = (Object[]) errorsIt.next();
					
					currentRow.createCell(cellNum).setCellValue(errorType);
					cellNum++;
					
					if(errorsRow[0].toString().equals("true")) {
					    currentRow.createCell(cellNum).setCellValue("target");
					}
					else {
					    currentRow.createCell(cellNum).setCellValue("source");
					}
					cellNum++;
					currentRow.createCell(cellNum).setCellValue(errorsRow[1].toString());
					cellNum++;
					currentRow.createCell(cellNum).setCellValue(errorsRow[2].toString());
					cellNum++;
					currentRow.createCell(cellNum).setCellValue(errorsRow[3].toString());
					cellNum++;
					if(errorId != 1 && errorId != 41 && errorId != 46) {
					    currentRow.createCell(cellNum).setCellValue(errorsRow[4].toString());
					    cellNum++;
					}
					else {
					    currentRow.createCell(cellNum).setCellValue("");
					    cellNum++;
					}
					
					if(errorsRow[5] != null) {
					    currentRow.createCell(cellNum).setCellValue(errorsRow[5].toString());
					}
					else {
					    currentRow.createCell(cellNum).setCellValue("");
					}
					cellNum++;
					
					if(errorsRow[6] != null) {
					    currentRow.createCell(cellNum).setCellValue(errorsRow[6].toString());
					}
					else {
					    currentRow.createCell(cellNum).setCellValue("");
					}
					cellNum++;
					
					if(errorsRow[7] != null) {
					    currentRow.createCell(cellNum).setCellValue(errorsRow[7].toString());
					}
					else {
					    currentRow.createCell(cellNum).setCellValue("");
					}
					cellNum++;
					
					if(errorsRow[8] != null) {
					    currentRow.createCell(cellNum).setCellValue(errorsRow[8].toString());
					}
					else {
					    currentRow.createCell(cellNum).setCellValue("");
					}
					cellNum++;
					
					if(errorsRow[9] != null) {
					    currentRow.createCell(cellNum).setCellValue(errorsRow[9].toString());
					}
					else {
					    currentRow.createCell(cellNum).setCellValue("");
					}
				    }
				}
			    }
			}
		    }
		    
		}
	    }
	    
	    try (OutputStream stream = new FileOutputStream(file)) {
		wb.write(stream);
	    }
	}
	catch (Exception ex) {
	    //we notify admin
	    mailMessage mail = new mailMessage();
	    mail.settoEmailAddress(myProps.getProperty("admin.email"));
	    mail.setfromEmailAddress("support@health-e-link.net");
	    mail.setmessageSubject("Error printing out the excel audit report for a batch - " + " " + myProps.getProperty("server.identity"));
	    StringBuilder emailBody = new StringBuilder();
	    emailBody.append("There was an error creating a the excel audit report for a batch.");
	    emailBody.append("<br/>Batch Name: " + batchName);
	    emailBody.append("<br/>Type: " + type);
	    emailBody.append("<br/><br/>: " + ex.getMessage());
	    emailBody.append("<br/><br/>" + ex.getStackTrace());
	    mail.setmessageBody(emailBody.toString());
	    emailMessageManager.sendEmail(mail);
	    fileName = "";
	}

	return fileName;
    }
    
    @RequestMapping(value = "/printAuditErrorsToExcel/{file}", method = RequestMethod.GET)
    public void printAuditErrorsToExcel(@PathVariable("file") String file,HttpServletResponse response) throws Exception {
	
	File templatePrintFile = new File ("/tmp/" + file + ".xlsx");
	InputStream is = new FileInputStream(templatePrintFile);

	response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".xlsx\"");
	FileCopyUtils.copy(is, response.getOutputStream());

	//Delete the file
	templatePrintFile.delete();

	 // close stream and return to view
	response.flushBuffer();
    } 
    
    /**
     * The 'createAuditErrorsToPDF.do' method will create an PDF file containing all dropped values, system errors and transaction errors for
     * the passed in batch.
     * @param batchName
     * @param type
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/createAuditErrorsToPDF.do", method = RequestMethod.GET)
    @ResponseBody
    public String createAuditErrorsToPDF(@RequestParam String batchName, @RequestParam String type) throws Exception {

        List<batchErrorSummary> batchErrorSummary = null;
	List<batchErrorSummary> batchSystemErrors = null;
	List<batchUploadDroppedValues> inboundDroppedValues = null;
	List<batchDownloadDroppedValues> outboundDroppedValues = null;
	List reportableFields = null;
	Integer batchId = 0;

	if("inbound".equals(type)) {

	    // Get the details of the batch
	    batchUploads batchDetails = transactionInManager.getBatchDetailsByBatchName(batchName);
	    batchId = batchDetails.getId();

	    reportableFields = transactionInManager.getErrorReportField(batchId);

	    if (batchDetails.getErrorRecordCount() > 0) {
		batchErrorSummary = transactionInManager.getBatchErrorSummary(batchId,"inbound");
	    }

	    //Check to see if we have any dropped values
	    inboundDroppedValues = transactionInManager.getBatchDroppedValues(batchId);

	}
	else {
	    batchDownloads batchDetails = transactionOutManager.getBatchDetailsByBatchName(batchName);
	    batchId = batchDetails.getId();

	    reportableFields = transactionOutManager.getErrorReportField(batchId);

	    if (batchDetails.getTotalErrorCount() > 0) {
		batchErrorSummary = transactionInManager.getBatchErrorSummary(batchDetails.getId(),"outbound");
	    }

	    //Check to see if we have any dropped values
	    outboundDroppedValues = transactionOutManager.getBatchDroppedValues(batchDetails.getId());
	}
	
	String auditReportDetailFile = "/tmp/" + batchName + "-auditErrors.txt";
	String auditReportPrintFile = "/tmp/" + batchName + "-auditErrors.pdf";
	
	File detailsFile = new File(auditReportDetailFile);
	detailsFile.delete();
	
	File printFile = new File(auditReportPrintFile);
	printFile.delete();
	
	Document document = new Document(PageSize.A4);
	
	StringBuffer reportBody = new StringBuffer();
	
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(auditReportDetailFile, true)));
	out.println("<html><body>");
	
	List errors = null;
	String sql = "";
	
	if(inboundDroppedValues != null) {
	    if(!inboundDroppedValues.isEmpty()) {
		
		reportBody.append("<div style='padding-top:10px;'>");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Dropped Values</strong></span><br />");
		reportBody.append("</div>");
		
		reportBody.append("<div style='padding-top:10px;'>");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Error: Dropped Values</strong></span><br /><br />");
		reportBody.append("</div>");
		
		reportBody.append("<div><table border='1' cellpadding='1' cellspacing='1' width='100%'>").append("<thead><tr>")
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>S/T Config</th>")	
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Row No.</th>")	
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field No.</th>")
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Column Name</th>")
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Client Identifier</th>")
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field Value</th>");
		
		if(reportableFields != null) {
		    Iterator reportableFieldsIt = reportableFields.iterator();

		    while (reportableFieldsIt.hasNext()) {
			Object rptFieldrow[] = (Object[]) reportableFieldsIt.next();
			reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[0].toString()+"</th>")
			.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[1].toString()+"</th>")
			.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[2].toString()+"</th>")
			.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[3].toString()+"</th>");
		    }
		}
		reportBody.append("</tr></thead><tbody>");

		sql = "select 'source' as fromOutboundConfig, a.transactionInRecordsId as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name,a.entity3Id as clientIdentifier,a.fieldValue as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
		+ "from batchuploaddroppedvalues a "
		+ "where a.batchUploadId = " + batchId + " order by a.id asc";

		if(!"".equals(sql)) {
		    errors = transactionInManager.getErrorDataBySQLStmt(sql);

		     if(errors != null) {
			 if(!errors.isEmpty()) {
			     Iterator errorsIt = errors.iterator();

			     while (errorsIt.hasNext()) {
				Object errorsRow[] = (Object[]) errorsIt.next();

				reportBody.append("<tr><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				.append(errorsRow[0].toString())
				.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				.append(errorsRow[1].toString())
				.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				.append(errorsRow[2].toString())
				.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				.append(errorsRow[3].toString())
				.append("</td>")
				.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				.append(errorsRow[4].toString()).append("</td>")
				.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				
				if(errorsRow[5] != null) {
				   reportBody.append(errorsRow[5].toString());
				}
				else {
				   reportBody.append("");
				}
				
				reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				if(errorsRow[6] != null) {
				   reportBody.append(errorsRow[6].toString());
				}
				else {
				   reportBody.append("");
				}
				reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				if(errorsRow[7] != null) {
				   reportBody.append(errorsRow[7].toString());
				}
				else {
				   reportBody.append("");
				}
				reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				if(errorsRow[8] != null) {
				   reportBody.append(errorsRow[8].toString());
				}
				else {
				   reportBody.append("");
				}
				reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				if(errorsRow[9] != null) {
				   reportBody.append(errorsRow[9].toString()).append("</td></tr>");
				}
				else {
				   reportBody.append("").append("</td></tr>");
				}
			    }
			 }
		     }
		 }
		reportBody.append("</tbody></table></div><br />");
	    }
	}
	
	if(outboundDroppedValues != null) {
	    if(!outboundDroppedValues.isEmpty()) {
		
		reportBody.append("<div style='padding-top:10px;'>");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Dropped Values</strong></span><br />");
		reportBody.append("</div>");
		
		reportBody.append("<div style='padding-top:10px;'>");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Error: Dropped Values</strong></span><br /><br />");
		reportBody.append("</div>");
		
		reportBody.append("<div><table border='1' cellpadding='1' cellspacing='1' width='100%'>").append("<thead><tr>")
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>S/T Config</th>")	
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Row No.</th>")	
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field No.</th>")
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Column Name</th>")
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Client Identifier</th>")
		.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field Value</th>");
		
		if(reportableFields != null) {
		    Iterator reportableFieldsIt = reportableFields.iterator();

		    while (reportableFieldsIt.hasNext()) {
			Object rptFieldrow[] = (Object[]) reportableFieldsIt.next();
			reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[0].toString()+"</th>")
			.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[1].toString()+"</th>")
			.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[2].toString()+"</th>")
			.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[3].toString()+"</th>");
		    }
		}
		reportBody.append("</tr></thead><tbody>");

		sql = "select 'target' as fromOutboundConfig, a.transactionInRecordsId as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name,a.entity3Id as clientIdentifier,a.fieldValue as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
		+ "from batchdownloaddroppedvalues a "
		+ "where a.batchDownloadId = " + batchId + " order by a.id asc";

		if(!"".equals(sql)) {
		    errors = transactionInManager.getErrorDataBySQLStmt(sql);

		     if(errors != null) {
			 if(!errors.isEmpty()) {
			     Iterator errorsIt = errors.iterator();

			     while (errorsIt.hasNext()) {
				Object errorsRow[] = (Object[]) errorsIt.next();

				reportBody.append("<tr><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				.append(errorsRow[0].toString())
				.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				.append(errorsRow[1].toString())
				.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				.append(errorsRow[2].toString())
				.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				.append(errorsRow[3].toString())
				.append("</td>")
				.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				.append(errorsRow[4].toString()).append("</td>")
				.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				if(errorsRow[5] != null) {
				   reportBody.append(errorsRow[5].toString());
				}
				else {
				   reportBody.append("");
				}
				reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				if(errorsRow[6] != null) {
				    reportBody.append(errorsRow[6].toString());
				}
				else {
				    reportBody.append("");
				}
				reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				if(errorsRow[7] != null) {
				    reportBody.append(errorsRow[7].toString());
				}
				else {
				    reportBody.append("");
				}
				reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				if(errorsRow[8] != null) {
				    reportBody.append(errorsRow[8].toString());
				}
				else {
				    reportBody.append("");
				}
				reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				if(errorsRow[9] != null) {
				    reportBody.append(errorsRow[9].toString()).append("</td></tr>");
				}
				else {
				    reportBody.append("").append("</td></tr>");
				}
			    }
			 }
		     }
		 }
		reportBody.append("</tbody></table></div><br />");
	    }
	}
	
	if(batchErrorSummary != null) {
	    if(!batchErrorSummary.isEmpty()) {
		
		reportBody.append("<div style='padding-top:10px;'>");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Transaction Errors</strong></span><br />");
		reportBody.append("</div>");
		
		errors = null;
		Integer errorId = 0;

		for(batchErrorSummary error : batchErrorSummary) {

		    if(errorId == 0 || errorId != error.getErrorId()) {
			
			if(errorId > 0) {
			    reportBody.append("</tbody></table></div><br />");
			}
			
			errorId = error.getErrorId();
			
			if(errorId == 1) {
			    reportBody.append("<div style='padding-top:10px;'>");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Error: Invalid value for Required Field</strong></span><br /><br />");
			    reportBody.append("</div>");
			}
			else if(errorId == 2) {
			    reportBody.append("<div style='padding-top:10px;'>");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Error: Failed Validation</strong></span><br /><br />");
			    reportBody.append("</div>");
			}
			else if(errorId == 3) {
			    reportBody.append("<div style='padding-top:10px;'>");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Error: Failed Crosswalk</strong></span><br /><br />");
			    reportBody.append("</div>");
			}
			else if(errorId == 3) {
			    reportBody.append("<div style='padding-top:10px;'>");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Error: Failed Macro</strong></span><br /><br />");
			    reportBody.append("</div>");
			}
			else if(errorId == 41) {
			    reportBody.append("<div style='padding-top:10px;'>");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Error: Zip Code Check</strong></span><br /><br />");
			    reportBody.append("</div>");
			}
			else if(errorId == 46) {
			    reportBody.append("<div style='padding-top:10px;'>");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Error: Invalid Data Format</strong></span><br /><br />");
			    reportBody.append("</div>");
			}
			
			reportBody.append("<div><table border='1' cellpadding='1' cellspacing='1' width='100%'>");
			reportBody.append("<thead><tr>")
			.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>S/T Config</th>")	
			.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Row No.</th>")	
			.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field No.</th>")
			.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Column Name</th>");

			if(errorId == 2) {
			    reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Validation</th>");
			}
			else if(errorId == 3) {
			    reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Crosswalk</th>");
			}
			else if(errorId == 4) {
			    reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Macro</th>");
			}

			reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field Value</th>");
		
			if(reportableFields != null) {
			    Iterator reportableFieldsIt = reportableFields.iterator();

			    while (reportableFieldsIt.hasNext()) {
				Object rptFieldrow[] = (Object[]) reportableFieldsIt.next();
				reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[0].toString()+"</th>")
				.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[1].toString()+"</th>")
				.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[2].toString()+"</th>")
				.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>"+rptFieldrow[3].toString()+"</th>");
			    }
			}
			reportBody.append("</tr></thead><tbody>");
		    }
		    
		    //Set the custom columns based on the error selected
		    switch(errorId) {
			case 1:

			    if("inbound".equals(type)) {
				sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchuploadauditerrors a left outer  join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }
			    else {
				sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType,a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchdownloadauditerrors a left outer  join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }

			    break;

			case 2:

			    if("inbound".equals(type)) {
				sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name, a.errorDetails as validation_type, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchuploadauditerrors a left outer  join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }
			    else {

				sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber, a.fieldName as column_name, a.errorDetails as validation_type, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchdownloadauditerrors a left outer  join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }

			    break;

			case 3:

			    if("inbound".equals(type)) {	

				sql = "select fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name, a.errorDetails as crosswalk, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchuploadauditerrors a left outer join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }
			    else {
				sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name, a.errorDetails as crosswalk, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchdownloadauditerrors a left outer join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }

			    break;

			case 4:

			    if("inbound".equals(type)) {
				sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.errorDetails as macro, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchuploadauditerrors a left outer  join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }
			    else {
				sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,a.errorDetails as macro, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchdownloadauditerrors a left outer  join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }

			    break;
			
			case 41:

			    if("inbound".equals(type)) {
				sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchuploadauditerrors a left outer  join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }
			    else {
				sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType,a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchdownloadauditerrors a left outer  join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }

			    break;
			    
			case 46:

			    if("inbound".equals(type)) {
				sql = "select fromOutboundConfig, a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType, a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchuploadauditerrors a left outer  join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchUploadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }
			    else {
				sql = "select 'true' as fromOutboundConfig,a.rownumber as rownumber, a.fieldNo as fieldNumber,a.fieldName as column_name,'' as errorType,a.errorData as field_value,a.reportField1Data,a.reportField2Data,a.reportField3Data,a.reportField4Data "
				    + "from batchdownloadauditerrors a left outer  join "
				    + "configurationmessagespecs b on a.configId = b.configId "
				    + "where a.batchDownloadId = " + batchId + " and a.errorId = " + error.getErrorId() + " order by a.rownumber asc";
			    }

			    break;
		    }

		    if(!"".equals(sql)) {
		       errors = transactionInManager.getErrorDataBySQLStmt(sql);

			if(errors != null) {
			    if(!errors.isEmpty()) {
				Iterator errorsIt = errors.iterator();
				
				while (errorsIt.hasNext()) {
				    Object errorsRow[] = (Object[]) errorsIt.next();
				    
				    reportBody.append("<tr><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				    
				    if(errorsRow[0].toString().equals("true")) {
					reportBody.append("target");
				    }
				    else {
					reportBody.append("source");
				    }
				    
				    reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				    .append(errorsRow[1].toString())
				    .append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				    .append(errorsRow[2].toString())
				    .append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
				    .append(errorsRow[3].toString())
				    .append("</td>");
				    
				    if(errorId != 1 && errorId != 41 && errorId != 46) {
					reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>")
					.append(errorsRow[4].toString()).append("</td>");
				    }
				    reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				    if(errorsRow[5] != null) {
					reportBody.append(errorsRow[5].toString());
				    }
				    else {
					reportBody.append("");
				    }
				    reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				    if(errorsRow[6] != null) {
					reportBody.append(errorsRow[6].toString());
				    }
				    else {
					reportBody.append("");
				    }
				    reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				    if(errorsRow[7] != null) {
					reportBody.append(errorsRow[7].toString());
				    }
				    else {
					reportBody.append("");
				    }
				    reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				    if(errorsRow[8] != null) {
					reportBody.append(errorsRow[8].toString());
				    }
				    else {
					reportBody.append("");
				    }
				    reportBody.append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>");
				    
				    if(errorsRow[9] != null) {
					reportBody.append(errorsRow[9].toString()).append("</td></tr>");
				    }
				    else {
					reportBody.append("").append("</td></tr>");
				    }
				}
			    }
			}
		    }
		}
		reportBody.append("</tbody></table></div>");
	    }
	}
	
	out.println(reportBody.toString());
	
	out.println("</body></html>");
	
	out.close();
	
	PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(auditReportPrintFile));
			  
	document.open();
			    
	XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

	//replace with actual code to generate html info
	//we get image location here 
	FileInputStream fis = new FileInputStream(auditReportDetailFile);
	worker.parseXHtml(pdfWriter, document, fis);
;
	fis.close();
	document.close();
	pdfWriter.close();
	
	File auditReportDetailsFile = new File(auditReportDetailFile);
	auditReportDetailsFile.delete();

	return batchName + "-auditErrors";
    }
    
    @RequestMapping(value = "/printAuditErrorsToPDF/{file}", method = RequestMethod.GET)
    public void printAuditErrorsToPDF(@PathVariable("file") String file,HttpServletResponse response
    ) throws Exception {
	
	File configPrintFile = new File ("/tmp/" + file + ".pdf");
	InputStream is = new FileInputStream(configPrintFile);

	response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".pdf\"");
	FileCopyUtils.copy(is, response.getOutputStream());

	//Delete the file
	configPrintFile.delete();

	 // close stream and return to view
	response.flushBuffer();
    } 
    
}
