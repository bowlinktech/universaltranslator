package com.hel.ut.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hel.ut.model.utUser;
import com.hel.ut.service.userManager;

/**
 *
 * @author chadmccue
 */

@Controller
public class profileController {
    
    @Autowired
    private userManager usermanager;

	
    /**
     * The '/profile' request will serve up the profile (account) dashboard after a successful login.
     *
     * @param request
     * @param response
     * @return	the administrator dashboard view
     * @throws Exception
     */
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView viewProfile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/profile/index");
        
        return mav;
    }
    
    @RequestMapping(value = "/settings", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    ModelAndView settings(Authentication authentication) throws Exception {
        
        ModelAndView mav = new ModelAndView();
        utUser userDetails = usermanager.getUserByUserName(authentication.getName());
        mav.addObject("btnValue", "Update");
        mav.addObject("userdetails", userDetails);
        mav.setViewName("/profile/details");
        
        return mav;
    }
    
    
    @RequestMapping(value = "/settings", method = {RequestMethod.POST})
    public @ResponseBody
    ModelAndView updateSettings(HttpServletRequest request,@Valid @ModelAttribute(value = "userdetails") utUser userdetails, 
    		Authentication authentication, BindingResult result) throws Exception {
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/profile/details");
        
        utUser currentUser = usermanager.getUserByUserName(authentication.getName());
        boolean okToChangeUserName = false;
        /** check user name**/
        if (!currentUser.getUsername().trim().equals(userdetails.getUsername().trim())) {
            utUser existing = usermanager.getUserByUserName(userdetails.getUsername());
            if (existing != null) {
                mav.setViewName("/profile/details");
                mav.addObject("btnValue", "Update");
                mav.addObject("existingUsername", "Username " + userdetails.getUsername().trim() + " already exists.");
                return mav;
            } else {
            	okToChangeUserName = true;
            }
        }
        
        /** we update user **/
        currentUser.setFirstName(userdetails.getFirstName());
        currentUser.setLastName(userdetails.getLastName());
        currentUser.setEmail(userdetails.getEmail());
        currentUser.setUsername(userdetails.getUsername());        
        if (!request.getParameter("password").trim().equalsIgnoreCase("")) {
        	currentUser.setRandomSalt(usermanager.generateSalt());
        	currentUser.setEncryptedPw(usermanager.getEncryptedPassword(request.getParameter("password"), currentUser.getRandomSalt()));
        }
        
        usermanager.updateUserOnly(currentUser);
        
        if (okToChangeUserName) {
        	//we need to update security context
        	Authentication authentication1 = new UsernamePasswordAuthenticationToken(currentUser.getUsername(), "", authentication.getAuthorities());
        	SecurityContextHolder.getContext().setAuthentication(authentication1);
        }
        
        
        currentUser.setPassword("");
        mav.addObject("btnValue", "Update");
        mav.addObject("userdetails", currentUser);
        mav.addObject("success", "Your settings have been updated.");
        
        
        return mav;
    }
    
}
