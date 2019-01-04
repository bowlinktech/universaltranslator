package com.hel.ut.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.hel.ut.model.Organization;
import com.hel.ut.model.custom.searchParameters;
import com.hel.ut.model.watchlist;
import com.hel.ut.service.messageTypeManager;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.transactionOutManager;
import com.hel.ut.service.userManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;

/**
 * The adminController class will handle administrator page requests that fall outside specific sections.
 *
 *
 * @author chadmccue
 *
 */
@Controller
public class adminController {

    @Autowired
    private organizationManager organizationManager;

    @Autowired
    private messageTypeManager messagetypemanager;

    @Autowired
    private utConfigurationManager configurationmanager;

    @Autowired
    private utConfigurationTransportManager configurationTransportManager;

    @Autowired
    private userManager userManager;

    @Autowired
    private transactionOutManager transactionOutManager;

    private int maxResults = 3;
    
    /**
     * This shows a dashboard with info for sysadmin components. *
     * @param request
     * @param response
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/administrator", method = RequestMethod.GET)
    public ModelAndView HIMDashboard(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
	

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/himdashboard");
        
	int year = 118;
        int month = 5;
        int day = 1;
        Date originalDate = new Date(year, month, day);

        Date fromDate = getMonthDate("START-WEEK");
        Date toDate = getMonthDate("END-WEEK");
	
	/* Retrieve search parameters from session */
        searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters");
	
        if ("".equals(searchParameters.getsection()) || !"himdashboard".equals(searchParameters.getsection())) {
            searchParameters.setfromDate(fromDate);
            searchParameters.settoDate(toDate);
            searchParameters.setsection("himdashboard");
        } else {
            fromDate = searchParameters.getfromDate();
            toDate = searchParameters.gettoDate();
        }

        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);
        mav.addObject("originalDate", originalDate);
        
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
        if ("END".equalsIgnoreCase(filter)) {
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
	else if ("START-WEEK".equalsIgnoreCase(filter)) {
	   cal.set(Calendar.DAY_OF_WEEK,cal.getActualMinimum(Calendar.DAY_OF_WEEK));
        } 
	else if ("END-WEEK".equalsIgnoreCase(filter)) {
	   cal.set(Calendar.DAY_OF_WEEK,cal.getActualMaximum(Calendar.DAY_OF_WEEK));
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
     * This shows the watch list *
     * @param request
     * @param response
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/watchlist", method = RequestMethod.GET)
    public ModelAndView watchlist(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/watchlist");
        
	//Get all the dashboard watch list entries
	List<watchlist> dashboardwatchlist = configurationmanager.getDashboardWatchList();
	
        mav.addObject("watchList", dashboardwatchlist);
        return mav;
    }

    /**
     * The '/createWatchEntry' function will handle displaying the create dashboard watch list entry screen.
     *
     * @return This function will display the new dashboard watch list entry screen overlay
     */
    @RequestMapping(value = "/createWatchEntry", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView createNewWatchEntryForm() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/watchlist/details");

        watchlist watchlistEntry = new watchlist();
        mav.addObject("watchlistEntry", watchlistEntry);

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationManager.getAllActiveOrganizations();
        mav.addObject("organizations", organizations);

        return mav;
    }

    /**
     * The '/editWatchEntry' function will handle displaying the edit utConfiguration connection screen.
     *
     * @param connectionId The id of the clicked utConfiguration connection
     *
     * @return This function will display the edit connection overlay
     */
    @RequestMapping(value = "/editWatchEntry", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView editWatchEntryForm(@RequestParam(value = "entryId", required = true) int entryId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/watchlist/details");

        watchlist watchlistEntry = configurationmanager.getDashboardWatchListById(entryId);
	
	if(watchlistEntry != null) {
	    if(!"".equals(watchlistEntry.getExpectFirstFileTime())) {
		String[] timeValuesBeg = watchlistEntry.getExpectFirstFileTime().split(":");
		String[] timeValuesEnd = watchlistEntry.getExpectFirstFileTime().split(" ");
		watchlistEntry.setExpectedTimeHour(Integer.parseInt(timeValuesBeg[0]));
		watchlistEntry.setExpectedTimeMinute(Integer.parseInt(timeValuesBeg[1].replace(" AM", "").replace(" PM", "")));
		watchlistEntry.setExpectedTimeAMPM(timeValuesEnd[1]);
	    }
	}
	
        mav.addObject("watchlistEntry", watchlistEntry);

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationManager.getAllActiveOrganizations();
        mav.addObject("organizations", organizations);

        return mav;
    }
    
     /**
     * The '/addDashboardWatchList.do' POST request will create/edit the dashboard watch list entry.
    
     * @return	The method will return a 1 back to the calling ajax function which will handle the page load.
     */
    @RequestMapping(value = "/addDashboardWatchList.do", method = RequestMethod.POST)
    public ModelAndView addDashboardWatchList(
            @ModelAttribute(value = "watchlistEntry") watchlist watchlistEntry,
            RedirectAttributes redirectAttr) throws Exception {

        Integer watchListEntryId;
	
	String nextInsertDate;
	
	if(!"".equals(watchlistEntry.getExpectFirstFileTime())) {
	    nextInsertDate = watchlistEntry.getExpectFirstFile() + " " + watchlistEntry.getExpectFirstFileTime();
	}
	else {
	    nextInsertDate = watchlistEntry.getExpectFirstFile() + " 12:00 AM";
	}
	
	DateFormat format = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.ENGLISH);
	Date date = format.parse(nextInsertDate);
	watchlistEntry.setNextInsertDate(date);

        if (watchlistEntry.getId() == 0) {
            watchListEntryId = configurationmanager.saveDashboardWatchListEntry(watchlistEntry);
            redirectAttr.addFlashAttribute("savedStatus", "created");
        } 
	else {
            watchListEntryId = watchlistEntry.getId();
            configurationmanager.updateDashboardWatchListEntry(watchlistEntry);

            redirectAttr.addFlashAttribute("savedStatus", "updated");
        }

        ModelAndView mav = new ModelAndView(new RedirectView("watchlist"));

        return mav;

    }
    
    /**
     * The '/deleteWatchEntry' POST method will remove both the inbound and outbound transactions that are associated to the passed in
     * batchName
     *NjM2NjguMjYxLjE3OTk=
     *
     */
    @RequestMapping(value = "/deleteWatchEntry", method = RequestMethod.POST)
    public @ResponseBody String deleteWatchEntry(@RequestParam(value = "watchId", required = true) Integer watchId) throws Exception {
        
	if(watchId > 0) {
	    configurationmanager.deleteWatchEntry(watchId); 
	}
	
        return "1";
        
    }
}
