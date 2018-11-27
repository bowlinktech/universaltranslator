/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.errorHandling;

import com.hel.ut.model.User;
import com.hel.ut.model.mailMessage;
import com.hel.ut.service.emailMessageManager;
import com.hel.ut.service.userManager;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author chadmccue
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    @Resource(name = "myProps")
    private Properties myProps;

    @Autowired
    private emailMessageManager emailMessageManager;

    @Autowired
    private userManager usermanager;

    @ExceptionHandler(Exception.class)
    public ModelAndView exception(HttpSession session, Exception e, HttpServletRequest request,
            Authentication authentication) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/exception");
        try {
            mailMessage messageDetails = new mailMessage();

            messageDetails.settoEmailAddress(myProps.getProperty("admin.email"));
            messageDetails.setfromEmailAddress("support@health-e-link.net");
            messageDetails.setmessageSubject("Exception Error " + " " + myProps.getProperty("server.identity"));

            StringBuilder sb = new StringBuilder();

            //we log page with error and ip of remote client if possible
            try {
                if (request.getHeader("HTTP_X_FORWARDED_FOR") != null) {
                    sb.append("HTTP_X_FORWARDED_FOR: " + request.getHeader("HTTP_X_FORWARDED_FOR") + "<br/>");
                }
                sb.append("Remote Address: " + request.getRemoteAddr() + "<br/>");
                sb.append("Web Page: " + request.getRequestURL() + "<br/>");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            /* If a user is logged in then send along the user details */
            if (session.getAttribute("userDetails") != null || authentication != null) {
                User userInfo = (User) session.getAttribute("userDetails");

                if (userInfo == null && authentication != null) {
                    // see if it is an admin that is logged in
                    userInfo = usermanager.getUserByUserName(authentication.getName());
                }
                if (userInfo != null) {
                    sb.append("Logged in User: " + userInfo.getFirstName() + " " + userInfo.getLastName() + " (ID: " + userInfo.getId() + ")");
                    sb.append(System.getProperty("line.separator"));
                    sb.append("User OrgId: " + userInfo.getOrgId());
                    sb.append(System.getProperty("line.separator"));
                }
            }

            sb.append("Error: " + e);
            sb.append("<br /><br />");
            sb.append("Time: " + new Date());
            sb.append("<br /><br />");
            sb.append("Message: " + e.getMessage());
            sb.append("<br /><br />");
            sb.append("Stack Trace: " + Arrays.toString(e.getStackTrace()));

            messageDetails.setmessageBody(sb.toString());
            emailMessageManager.sendEmail(messageDetails);
            /*mav.addObject("messageBody",sb.toString());*/
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.toString() + " error at exception");
        }

        return mav;
    }
}
