package com.hel.ut.controller;

import com.hel.ut.model.utUser;
import com.hel.ut.model.mailMessage;
import com.hel.ut.restAPI.directManager;
import com.hel.ut.service.emailMessageManager;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.userManager;
import java.math.BigInteger;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


/**
 * The mainController class will handle all URL requests that fall outside of specific user or admin controllers
 *
 * eg. login, logout, about, etc
 *
 * @author chadmccue
 *
 */
@Controller
public class mainController {

    @Autowired
    private userManager usermanager;

    @Autowired
    private emailMessageManager emailMessageManager;
    
    @Autowired
    private directManager directManager;
    
    @Autowired
    private transactionInManager transactionInManager;
    
    
    /**
     * The '/', '/login' request will serve up the login page.
     *
     * @param request
     * @param response
     * @return	the login page view
     * @throws Exception
     */
    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
	public ModelAndView login() throws Exception {
	
	ModelAndView mav = new ModelAndView();
	mav.setViewName("/login");

	return mav;

    }

    /**
     * The '/loginfailed' request will serve up the login page displaying the login failed error message
     *
     * @param request
     * @param response
     * @return	the error object and the login page view
     * @throws Exception
     */
    @RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public ModelAndView loginerror(HttpServletRequest request, HttpServletResponse response) throws Exception {

	ModelAndView mav = new ModelAndView();
	mav.setViewName("/login");
	mav.addObject("error", "true");
	return mav;

    }
	
    /**
     * The '/userlogout' request will handle a user logging out of the system. The request will handle front-end users or administrators logging out.
     *
     * @param request
     * @param response
     * @param session
     * @return	the login page view
     * @throws Exception
     */
    @RequestMapping(value = "/userlogout", method = RequestMethod.GET)
    public ModelAndView userlogout(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
	 
	utUser userInfo = (utUser) session.getAttribute("userDetails");

	if (userInfo != null) {
	    usermanager.loguserout(userInfo.getId());

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null) {
		new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
	}

	ModelAndView mav = new ModelAndView(new RedirectView("/logout"));
	return mav;
    }

    /**
     * The '/' head request
     *
     * @param request
     * @param response
     * @return	the login page
     * @throws Exception
     */
    @RequestMapping(value = "/", method = {RequestMethod.HEAD})
	public ModelAndView headRequest() throws Exception {
	ModelAndView mav = new ModelAndView(new RedirectView("/login"));
	return mav;
    }

    /**
     * The '/forgotPassword' GET request will be used to display the forget password form (In a modal)
     *
     *
     * @param session
     * @return	The forget password form page
     *
     *
     */
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
	public ModelAndView forgotPassword(HttpSession session) throws Exception {

	ModelAndView mav = new ModelAndView();
	mav.setViewName("/forgotPassword");

	return mav;
    }

    /**
     * The '/forgotPassword.do' POST request will be used to find the account information for the user and send an email.
     *
     *
     */
    @RequestMapping(value = "/forgotPassword.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
    Integer findPassword(@RequestParam String identifier) throws Exception {

	Integer userId = usermanager.getUserByIdentifier(identifier);

	if (userId == null) {
	    return 0;
	} else {

	    return userId;
	}

    }

    /**
     * The '/sendPassword.do' POST request will be used to send the reset email to the user.
     *
     * @param userId The id of the return user.
     */
    @RequestMapping(value = "/sendPassword.do", method = RequestMethod.POST)
	public void sendPassword(@RequestParam Integer userId, HttpServletRequest request) throws Exception {

	String randomCode = generateRandomCode();

	utUser userDetails = usermanager.getUserById(userId);
	userDetails.setresetCode(randomCode);

	usermanager.updateUser(userDetails);

	/* Sent Reset Email */
	mailMessage messageDetails = new mailMessage();

	messageDetails.settoEmailAddress(userDetails.getEmail());
	messageDetails.setmessageSubject("Health-e-Link HDR Reset Password");

	String resetURL = request.getRequestURL().toString().replace("sendPassword.do", "resetPassword?b=");

	StringBuilder sb = new StringBuilder();

	sb.append("Dear " + userDetails.getFirstName() + ",<br />");
	sb.append("You have recently asked to reset your Health-e-Link HDR password.<br /><br />");
	sb.append("<a href='" + resetURL + randomCode + "'>Click here to reset your password.</a>");

	messageDetails.setmessageBody(sb.toString());
	messageDetails.setfromEmailAddress("support@health-e-link.net");

	emailMessageManager.sendEmail(messageDetails);

    }

    /**
     * The '/resetPassword' GET request will be used to display the reset password form
     *
     *
     * @return	The forget password form page
     *
     *
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public ModelAndView resetPassword(@RequestParam(value = "b", required = false) String resetCode, HttpSession session) throws Exception {

	ModelAndView mav = new ModelAndView();
	mav.setViewName("/resetPassword");
	mav.addObject("resetCode", resetCode);

	return mav;
    }

    /**
     * The '/resetPassword' POST request will be used to display update the users password
     *
     * @param resetCode The code that was set to reset a user for.
     * @param newPassword The password to update the user to
     *
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public ModelAndView resetPassword(@RequestParam String resetCode, @RequestParam String newPassword, HttpSession session, RedirectAttributes redirectAttr) throws Exception {

	utUser userDetails = usermanager.getUserByResetCode(resetCode);

	if (userDetails == null) {
	    redirectAttr.addFlashAttribute("msg", "notfound");

	    ModelAndView mav = new ModelAndView(new RedirectView("/login"));
	    return mav;
	} else {
	    userDetails.setresetCode(null);
	    userDetails.setPassword(newPassword);
	    userDetails = usermanager.encryptPW(userDetails);

	    usermanager.updateUser(userDetails);

	    redirectAttr.addFlashAttribute("msg", "updated");

	    ModelAndView mav = new ModelAndView(new RedirectView("/login"));
	    return mav;
	}

    }

    /**
     * The 'generateRandomCode' function will be used to generate a random access code to reset a users password. The function will call itself until it gets a unique code.
     *
     * @return This function returns a randomcode as a string
     */
    public String generateRandomCode() {

	Random random = new Random();
	String randomCode = new BigInteger(130, random).toString(32);

	/* Check to make sure there is not reset code already generated */
	utUser usedCode = usermanager.getUserByResetCode(randomCode);

	if (usedCode == null) {
	    return randomCode;
	} else {

	    return generateRandomCode();

	}

    }

}
