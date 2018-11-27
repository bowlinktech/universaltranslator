/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.controller;

import com.hel.ut.model.HL7Elements;
import com.hel.ut.model.HL7Segments;
import com.hel.ut.model.mainHL7Details;
import com.hel.ut.model.mainHL7Elements;
import com.hel.ut.model.mainHL7Segments;
import com.hel.ut.service.sysAdminManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author chadmccue
 */
@Controller
@RequestMapping("/administrator/sysadmin/hl7")
public class adminSysHL7Controller {

    @Autowired
    private sysAdminManager sysAdminManager;

    /**
     * The "" method will return the list of hl7 versions saved in the system.
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView listHL7Versions() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/hl7");

        //Return a list of available macros
        List<mainHL7Details> hl7Versions = sysAdminManager.getHL7List();
        mav.addObject("hl7Versions", hl7Versions);

        return mav;
    }

    /**
     * The "/create" method will return the blank form to create a new HL7 version
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createHL7Spec() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/hl7/create");

        //Return a list of available macros
        mainHL7Details hl7Spec = new mainHL7Details();
        mav.addObject("HL7Details", hl7Spec);

        return mav;
    }

    /**
     * The '/create' POST request save all the hl7 custom settings
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView saveHL7(@ModelAttribute(value = "HL7Details") mainHL7Details HL7Details, RedirectAttributes redirectAttr) throws Exception {

        /* Update the details of the hl7 */
        int hl7Id = sysAdminManager.createHL7(HL7Details);

        redirectAttr.addFlashAttribute("savedStatus", "created");
        ModelAndView mav = new ModelAndView(new RedirectView("details?hl7Id=" + hl7Id));
        return mav;

    }

    /**
     * The '/details' GET request will display the HL7 detail form.
     */
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public ModelAndView getHL7Form(@RequestParam int hl7Id) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/sysadmin/hl7/details");

        mainHL7Details hl7Details = sysAdminManager.getHL7Details(hl7Id);
        int HL7Id = 0;

        HL7Id = hl7Details.getId();

        /* Get a list of HL7 Segments */
        List<mainHL7Segments> HL7Segments = sysAdminManager.getHL7Segments(HL7Id);

        /* Get a list of HL7Elements */
        if (!HL7Segments.isEmpty()) {
            for (mainHL7Segments segment : HL7Segments) {

                List<mainHL7Elements> HL7Elments = sysAdminManager.getHL7Elements(HL7Id, segment.getId());

                if (!HL7Elments.isEmpty()) {

                    segment.setHL7Elements(HL7Elments);
                }

            }
        }
        hl7Details.setHL7Segments(HL7Segments);

        mav.addObject("HL7Details", hl7Details);

        return mav;
    }

    /**
     * The '/details' POST request save all the hl7 custom settings
     */
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public ModelAndView saveHL7Details(@ModelAttribute(value = "HL7Details") mainHL7Details HL7Details, RedirectAttributes redirectAttr) throws Exception {

        /* Update the details of the hl7 */
        sysAdminManager.updateHL7Details(HL7Details);

        List<mainHL7Segments> segments = HL7Details.getHL7Segments();

        if (null != segments && segments.size() > 0) {

            for (mainHL7Segments segment : segments) {

                /* Update each segment */
                sysAdminManager.updateHL7Segments(segment);

                /* Get the list of segment elements */
                List<mainHL7Elements> elements = segment.getHL7Elements();

                if (null != elements && elements.size() > 0) {

                    for (mainHL7Elements element : elements) {
                        sysAdminManager.updateHL7Elements(element);

                    }
                }
            }
        }

        redirectAttr.addFlashAttribute("savedStatus", "updated");
        ModelAndView mav = new ModelAndView(new RedirectView("details?hl7Id=" + HL7Details.getId()));
        return mav;

    }

    /**
     * The '/newHL7Segment' GET request will be used to display the blank new HL7 Segment screen (In a modal)
     *
     *
     * @return	The HL7 Segment blank form page
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
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     *
     * @return	Will return the HL7 Customization page on "Save"
     *
     * @throws Exception
     */
    @RequestMapping(value = "/saveHL7Segment", method = RequestMethod.POST)
    public ModelAndView saveHL7Segment(@ModelAttribute(value = "HL7SegmentDetails") mainHL7Segments HL7SegmentDetails, RedirectAttributes redirectAttr) throws Exception {

        sysAdminManager.saveHL7Segment(HL7SegmentDetails);

        redirectAttr.addFlashAttribute("savedStatus", "savedSegment");
        ModelAndView mav = new ModelAndView(new RedirectView("details?hl7Id=" + HL7SegmentDetails.gethl7Id()));
        return mav;
    }

    /**
     * The '/newHL7Element' GET request will be used to display the blank new HL7 Segment Element screen (In a modal)
     *
     *
     * @return	The HL7 Segment Element blank form page
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
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     *
     * @return	Will return the HL7 Customization page on "Save"
     *
     * @throws Exception
     */
    @RequestMapping(value = "/saveHL7Element", method = RequestMethod.POST)
    public ModelAndView saveHL7Element(@ModelAttribute(value = "HL7ElementDetails") mainHL7Elements HL7ElementDetails, RedirectAttributes redirectAttr) throws Exception {

        sysAdminManager.saveHL7Element(HL7ElementDetails);

        redirectAttr.addFlashAttribute("savedStatus", "savedElement");
        ModelAndView mav = new ModelAndView(new RedirectView("details?hl7Id=" + HL7ElementDetails.gethl7Id()));
        return mav;
    }

}
