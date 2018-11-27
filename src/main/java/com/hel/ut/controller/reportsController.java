/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author chadmccue
 */
@Controller
@RequestMapping("/reports")
public class reportsController {

    /**
     * The '' GET request will serve up the report request page.
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView requestRedirect(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        /*
        Send the user to the "Request" page
         */
        ModelAndView mav = new ModelAndView(new RedirectView("/reports/request"));
        return mav;

    }

    /**
     * The '/request' GET request will serve up the report request page.
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public ModelAndView viewReportRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/reports/request");

        return mav;

    }

    /**
     * The '/requestedReports' GET request will serve up the requested report list page.
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/requestedReports", method = RequestMethod.GET)
    public ModelAndView requestedReports(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/reports/requestedReports");

        return mav;

    }

}
