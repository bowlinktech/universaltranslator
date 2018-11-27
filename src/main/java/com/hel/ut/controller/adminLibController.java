package com.hel.ut.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.messageType;
import com.hel.ut.model.messageTypeDataTranslations;
import com.hel.ut.model.messageTypeFormFields;
import com.hel.ut.service.messageTypeManager;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/administrator/library")
public class adminLibController {

    @Autowired
    private messageTypeManager messagetypemanager;

    /**
     * The private maxResults variable will hold the number of results to show per list page.
     */
    private static int maxResults = 10;

    /**
     * The '/list' GET request will serve up the existing list of message types in the system
     *
     * @param page	The page parameter will hold the page to view when pagination is built.
     * @return	The message type page list
     *
     * @Objects	(1) An object containing all the found message types
     *
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listMessageTypes() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/messageTypeLibrary/list");

        List<messageType> messageTypes = messagetypemanager.getMessageTypes();
        mav.addObject("messageTypesList", messageTypes);

        return mav;
    }

    /**
     * The '/create' GET request will serve up the create new message type page
     *
     * @return	The create new message type form
     *
     * @Objects	(1) An object with a new message type
     *
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createMessageType() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/messageTypeLibrary/details");
        mav.addObject("messageTypeDetails", new messageType());
        return mav;

    }

    /**
     * The '/create' POST request will be used to submit the new message type.
     *
     * @param	messageTypeDetails The messageType object
     *
     * @Objects	(1) An object will be returned holding the message type
     *
     * @Returns The function will return the message type edit page.
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView createMessageType(HttpSession session, @Valid @ModelAttribute(value = "messageTypeDetails") messageType messageTypeDetails, BindingResult result, @RequestParam String action, RedirectAttributes redirectAttr) throws Exception {

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/messageTypeLibrary/details");
            return mav;
        }

        messageType existing = messagetypemanager.getMessageTypeByName(messageTypeDetails.getName());

        if (existing != null) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/messageTypeLibrary/details");
            mav.addObject("existingType", "Message type " + messageTypeDetails.getName().trim() + " already exists.");
            return mav;
        }

        Integer id = (Integer) messagetypemanager.createMessageType(messageTypeDetails);

        redirectAttr.addFlashAttribute("savedStatus", "created");

        /**
         * Set the private variable to hold the id of the new message type.
         */
	session.setAttribute("messageTypeId",id);

        if (action.equals("save")) {

            ModelAndView mav = new ModelAndView(new RedirectView("details?i=" + id));
            return mav;
        } else {
            ModelAndView mav = new ModelAndView(new RedirectView("mappings"));
            return mav;
        }
    }

    /**
     * The '/details?i={num}' GET request will be used to return the details of the selected message type.
     *
     * @param	i	The id of the selected message type
     *
     * @Objects	(1) An object will be returned holding the message type
     *
     * @Returns The function will return the message type edit page.
     */
    @RequestMapping(value = "/details{num}", method = RequestMethod.GET)
    public ModelAndView viewMessageTypeDetails(HttpSession session,@RequestParam(value = "i", required = false) Integer Id) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/messageTypeLibrary/details");

       Integer messageTypeId = 0;
	
        //Set the static variable messageTypeId to hold the passed in id
        if (Id == null && session.getAttribute("messageTypeId") == null) {
            mav = new ModelAndView(new RedirectView("list"));
            return mav;
        }
	else if(Id == null && session.getAttribute("messageTypeId") != null) {
	    messageTypeId = (Integer) session.getAttribute("messageTypeId");
	}
	else {
	    session.setAttribute("messageTypeId", Id);
	    messageTypeId = Id;
	}

        //Get the message type
        messageType messageTypeDetails = messagetypemanager.getMessageTypeById(messageTypeId);
        mav.addObject("messageTypeDetails", messageTypeDetails);
        mav.addObject("id", messageTypeId);
        return mav;
    }

    /**
     * The '/details?i={num}' POST request will be used to submit the details of the selected message type.
     *
     * @param	i	The id of the selected message type messageTypeDetails The messageType object
     *
     * @Objects	(1) An object will be returned holding the message type
     *
     * @Returns The function will return the message type edit page.
     */
    @RequestMapping(value = "/details{num}", method = RequestMethod.POST)
    public ModelAndView updateMessageType(@Valid @ModelAttribute(value = "messageTypeDetails") messageType messageTypeDetails, BindingResult result) throws Exception {

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/messageTypeLibrary/details");
            mav.addObject("id", messageTypeDetails.getId());
            return mav;
        }

        messagetypemanager.updateMessageType(messageTypeDetails);

        ModelAndView mav = new ModelAndView("/administrator/messageTypeLibrary/details");
        mav.addObject("savedStatus", "updated");
        mav.addObject("id", messageTypeDetails.getId());
        return mav;
    }

    /**
     * *********************************************************
     * MESSAGE TYPE LIBRARY MAPPING FUNCTIONS 
     * *********************************************************
     */
    
    /**
     * The '/mappings' GET request will display the Field Mappings page for the selected message type.
     *
     */
    @RequestMapping(value = "/mappings", method = RequestMethod.GET)
    public ModelAndView getFieldMappings(HttpSession session) throws Exception {
	
	ModelAndView mav = new ModelAndView();
	Integer messageTypeId = 0;
	
	if(session.getAttribute("messageTypeId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    messageTypeId = (Integer) session.getAttribute("messageTypeId");
	}

        mav.setViewName("/administrator/messageTypeLibrary/mappings");
        mav.addObject("id", messageTypeId);

        //Need to get the name of the message type
        messageType messageTypeDetails = messagetypemanager.getMessageTypeById(messageTypeId);

        //Need to return a list of associated fields for the selected message type
        List<messageTypeFormFields> fieldList = messagetypemanager.getMessageTypeFields(messageTypeId);
        messageTypeDetails.setFields(fieldList);

        mav.addObject("messageTypeDetails", messageTypeDetails);

        //Get the list of available information tables
        @SuppressWarnings("rawtypes")
        List infoTables = messagetypemanager.getInformationTables();
        mav.addObject("infoTables", infoTables);

        //Get the list of available field validation types
        @SuppressWarnings("rawtypes")
        List validationTypes = messagetypemanager.getValidationTypes();
        mav.addObject("validationTypes", validationTypes);

        return mav;

    }

    /**
     * The '/mappings' POST request will submit the fields mapping form from the selected message type.
     *
     * #param	messageTypeDetails
     *
     * @Return	This function will redirect the user back to the mappings display page. A redirect is being used so we can show the fields in the appropriate display order.
     *
     */
    @RequestMapping(value = "/mappings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer submitFieldMappings(@ModelAttribute(value = "messageTypeDetails") messageType messageTypeDetails, RedirectAttributes redirectAttr) throws Exception {
        List<messageTypeFormFields> fieldList = messageTypeDetails.getFields();

        if (null != fieldList && fieldList.size() > 0) {
            for (messageTypeFormFields formfield : fieldList) {
                //Update each mappings
                messagetypemanager.updateMessageTypeFields(formfield);
            }
        }

        return 1;

    }

    /**
     * The '/addNewField' GET request will return the new field module
     *
     * @param bucketNo The bucket number the new field is going to be added to
     * @param displayPOS The last display position used for the bucket
     * @param fieldNo The last field number used for the message type
     *
     * @return The function returns the new field module with a empty messageTypeFormFields object
     */
    @RequestMapping(value = "/addNewField", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView addNewField(HttpSession session,@RequestParam(value = "maxfieldNo", required = true) int maxfieldNo) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/messageTypeLibrary/addNewField");

        int newFieldNo = maxfieldNo + 1;
	
	Integer messageTypeId = (Integer) session.getAttribute("messageTypeId");

        //Create a blank messageTypeFormField object
        messageTypeFormFields formField = new messageTypeFormFields();
        formField.setMessageTypeId(messageTypeId);
        formField.setFieldNo(newFieldNo);
        formField.setFieldDesc("");
        formField.setValidationType(1);
        formField.setRequired(false);

        mav.addObject("messageTypeFormFields", formField);

        //Get the list of available field validation types
        @SuppressWarnings("rawtypes")
        List validationTypes = messagetypemanager.getValidationTypes();
        mav.addObject("validationTypes", validationTypes);

        return mav;
    }

    /**
     * The '/addNewField' POST request will submit the new field module
     *
     * @param messageTypeFormFields The object containing the new field
     *
     * @return The function will reload the mappings page showing the new field after the field has been successfully added.
     */
    @RequestMapping(value = "/addNewField", method = RequestMethod.POST)
    public ModelAndView submitNewField(@ModelAttribute(value = "messageTypeFormFields") messageTypeFormFields messageTypeFormFields, RedirectAttributes redirectAttr) throws Exception {

        messagetypemanager.saveMessageTypeFields(messageTypeFormFields);

        redirectAttr.addFlashAttribute("savedStatus", "fieldcreated");
        ModelAndView mav = new ModelAndView(new RedirectView("mappings"));
        return mav;
    }

    
    /**
     * *********************************************************
     * MESSAGE TYPE LIBRARY DATA TRANSLATIONS FUNCTIONS *********************************************************
     */
    /**
     * The '/translations' GET request will display the Field Mappings page for the selected message type.
     *
     */
    @RequestMapping(value = "/translations", method = RequestMethod.GET)
    public ModelAndView getDataTranslations(HttpSession session) throws Exception {
	
	ModelAndView mav = new ModelAndView();
	Integer messageTypeId = 0;
	
	if(session.getAttribute("messageTypeId") == null){  
	    mav = new ModelAndView(new RedirectView("list"));
            return mav;
	}
	else {
	    messageTypeId = (Integer) session.getAttribute("messageTypeId");
	}

        //Set the data translations array to get ready to hold data
        List<messageTypeDataTranslations> translations = new CopyOnWriteArrayList<messageTypeDataTranslations>();
	session.setAttribute("messageTypeDataTranslastions", translations);

        mav.setViewName("/administrator/messageTypeLibrary/translations");
        mav.addObject("id", messageTypeId);

        //Need to return a list of associated fields for the selected message type
        List<messageTypeFormFields> fieldList = messagetypemanager.getMessageTypeFields(messageTypeId);
        mav.addObject("fields", fieldList);

        //Return a list of available crosswalks
        List<Crosswalks> crosswalks = messagetypemanager.getCrosswalks(1, 0, 0);
        mav.addObject("crosswalks", crosswalks);

        return mav;
    }

    /**
     * The '/translations' POST request will submit the selected data translations and save it to the data base.
     *
     */
    @RequestMapping(value = "/translations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer submitDataTranslations(HttpSession session,@RequestParam(value = "id", required = false) Integer messageTypeId) throws Exception {

        List<messageTypeDataTranslations> translations = (List<messageTypeDataTranslations>) session.getAttribute("messageTypeDataTranslastions");

	//Delete all the data translations before creating
        //This will help with the jquery removing tranlsations
        messagetypemanager.deleteDataTranslations(messageTypeId);

        //Loop through the list of translations
        for (messageTypeDataTranslations translation : translations) {
            messagetypemanager.saveDataTranslations(translation);
        }

        return 1;
    }

    /**
     * The '/getCrosswalks.do' function will return all the available crosswalks.
     *
     * @Return list of crosswalks
     */
    @RequestMapping(value = "/getCrosswalks.do", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getCrosswalks(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "orgId", required = false) Integer orgId, @RequestParam(value = "maxCrosswalks", required = false) Integer maxCrosswalks) throws Exception {

        if (page == null) {
            page = 1;
        }

        if (orgId == null) {
            orgId = 0;
        }

        if (maxCrosswalks == null) {
            maxCrosswalks = 4;
        }

        double maxCrosswalkVal = maxCrosswalks;

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/messageTypeLibrary/crosswalks");
        mav.addObject("orgId", orgId);

        //Need to return a list of crosswalks
        List<Crosswalks> crosswalks = messagetypemanager.getCrosswalks(page, maxCrosswalks, orgId);
        mav.addObject("availableCrosswalks", crosswalks);

        //Find out the total number of crosswalks
        double totalCrosswalks = messagetypemanager.findTotalCrosswalks(orgId);

        Integer totalPages = (int) Math.ceil((double) totalCrosswalks / maxCrosswalkVal);
        mav.addObject("totalPages", totalPages);
        mav.addObject("currentPage", page);

        return mav;

    }

    /**
     * The '/getTranslations.do' function will return the list of existing translations set up for the selected message type..
     *
     * @Return list of translations
     */
    @RequestMapping(value = "/getTranslations.do", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getTranslations(HttpSession session,@RequestParam(value = "reload", required = true) boolean reload) throws Exception {

	Integer messageTypeId = (Integer) session.getAttribute("messageTypeId");
	
	ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/messageTypeLibrary/existingTranslations");
	
	List<messageTypeDataTranslations> translations = (List<messageTypeDataTranslations>) session.getAttribute("messageTypeDataTranslastions");

        //only get the saved translations if reload == 0
        //We only want to retrieve the saved ones on initial load
        if (reload == false) {
            //Need to get a list of existing translations
            List<messageTypeDataTranslations> existingTranslations = messagetypemanager.getMessageTypeTranslations(messageTypeId);

            String fieldName;
            String crosswalkName;

            for (messageTypeDataTranslations translation : existingTranslations) {
                //Get the field name by id
                fieldName = messagetypemanager.getFieldName(translation.getFieldId());
                translation.setfieldName(fieldName);

                //Get the crosswalk name by id
                crosswalkName = messagetypemanager.getCrosswalkName(translation.getCrosswalkId());
                translation.setcrosswalkName(crosswalkName);

                translations.add(translation);
            }
        }

        mav.addObject("dataTranslations", translations);

        return mav;

    }

    /**
     * The '/newCrosswalk' GET request will be used to return a blank crosswalk form.
     *
     *
     * @return	The crosswalk details page
     *
     * @Objects	(1) An object that will hold all the details of the clicked crosswalk
     *
     */
    @RequestMapping(value = "/newCrosswalk", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView newCrosswalk(@RequestParam(value = "orgId", required = false) Integer orgId) throws Exception {

        if (orgId == null) {
            orgId = 0;
        }

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/messageTypeLibrary/crosswalkDetails");

        Crosswalks crosswalkDetails = new Crosswalks();
        mav.addObject("crosswalkDetails", crosswalkDetails);
        mav.addObject("btnValue", "Create");
	mav.addObject("actionValue", "Create");
        mav.addObject("orgId", orgId);

        //Get the list of available file delimiters
        @SuppressWarnings("rawtypes")
        List delimiters = messagetypemanager.getDelimiters();
        mav.addObject("delimiters", delimiters);

        return mav;
    }

    /**
     * The '/createCrosswalk' function will be used to create a new crosswalk
     *
     * @Return The function will either return the crosswalk form on error or redirect to the data translation page.
     */
    @RequestMapping(value = "/createCrosswalk", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView createCrosswalk(@ModelAttribute(value = "crosswalkDetails") Crosswalks crosswalkDetails, BindingResult result, RedirectAttributes redirectAttr, @RequestParam int orgId) throws Exception {

        crosswalkDetails.setOrgId(orgId);
        int lastId = messagetypemanager.createCrosswalk(crosswalkDetails);

        if (lastId == 0) {
            redirectAttr.addFlashAttribute("savedStatus", "error");
        } else {
            redirectAttr.addFlashAttribute("savedStatus", "created");
        }

        //if orgId > 0 then need to send back to the configurations page
        //otherwise send back to the message type libarary translation page.
        if (orgId > 0) {
            ModelAndView mav = new ModelAndView(new RedirectView("../configurations/translations"));
            return mav;
        } else {
            ModelAndView mav = new ModelAndView(new RedirectView("translations"));
            return mav;
        }
    }
    
    /**
     * The '/UploadNewFile' function will be used to upload a new file for an existing crosswalk.
     *
     * @param crosswalkDetails
     * @param result
     * @param redirectAttr
     * @return 
     * @throws java.lang.Exception 
     * @Return The function will either return the crosswalk form on error or redirect to the data translation page.
     */
    @RequestMapping(value = "/uploadnewfileCrosswalk", method = RequestMethod.POST)
    public @ResponseBody 
        ModelAndView uploadnewfileCrosswalk(
            @ModelAttribute(value = "crosswalkDetails") Crosswalks crosswalkDetails, 
            BindingResult result, RedirectAttributes redirectAttr, @RequestParam int orgId
    ) throws Exception {

        
        int lastId = messagetypemanager.uploadNewFileForCrosswalk(crosswalkDetails);

        if (lastId == 0) {
            redirectAttr.addFlashAttribute("savedStatus", "error");
        } else {
            redirectAttr.addFlashAttribute("savedStatus", "updated");
        }

        //if orgId > 0 then need to send back to the configurations page
        //otherwise send back to the message type libarary translation page.
        if (orgId > 0) {
            ModelAndView mav = new ModelAndView(new RedirectView("../configurations/translations"));
            return mav;
        } else {
            ModelAndView mav = new ModelAndView(new RedirectView("translations"));
            return mav;
        }
    }

    /**
     *
     */
    @RequestMapping(value = "/checkCrosswalkName.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Long checkCrosswalkName(@RequestParam(value = "name", required = true) String name, @RequestParam(value = "orgId", required = false) Integer orgId) throws Exception {

        if (orgId == null) {
            orgId = 0;
        }

        Long nameExists = (Long) messagetypemanager.checkCrosswalkName(name, orgId);

        return nameExists;

    }

    /**
     * The '/viewCrosswalk{params}' function will return the details of the selected crosswalk. The results will be displayed in the overlay.
     *
     * @Param	i	This will hold the id of the selected crosswalk
     *
     * @Return	This function will return the crosswalk details view.
     */
    @RequestMapping(value = "/viewCrosswalk{params}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView viewCrosswalk(@RequestParam(value = "i", required = true) Integer cwId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/messageTypeLibrary/crosswalkDetails");

        //Get the details of the selected crosswalk
        Crosswalks crosswalkDetails = messagetypemanager.getCrosswalk(cwId);
        mav.addObject("crosswalkDetails", crosswalkDetails);

        //Get the data associated with the selected crosswalk
        @SuppressWarnings("rawtypes")
        List crosswalkData = messagetypemanager.getCrosswalkData(cwId);
        mav.addObject("crosswalkData", crosswalkData);

        //Get the list of available file delimiters
        @SuppressWarnings("rawtypes")
        List delimiters = messagetypemanager.getDelimiters();
        mav.addObject("delimiters", delimiters);
	
	mav.addObject("btnValue", "Upload New File");
        mav.addObject("actionValue", "UploadNewFile");
	
	mav.addObject("orgId", crosswalkDetails.getOrgId());

        return mav;
    }

    /**
     * The '/setTranslations{params}' function will handle taking in a selected field and a selected crosswalk and add it to an array of translations. This array will be used when the form is submitted to associate to the existing message type.
     *
     * @param f	This will hold the id of the selected field cw	This will hold the id of the selected crosswalk fText	This will hold the text value of the selected field (used for display purposes) CWText	This will hold the text value of the selected crosswalk (used for display purposes)
     *
     * @Return	This function will return the existing translations view that will display the table of newly selected translations
     */
    @RequestMapping(value = "/setTranslations{params}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView setTranslations(HttpSession session,@RequestParam(value = "f", required = true) Integer field, @RequestParam(value = "cw", required = true) Integer cwId, @RequestParam(value = "fText", required = true) String fieldText, @RequestParam(value = "CWText", required = true) String cwText) throws Exception {

        List<messageTypeDataTranslations> translations = (List<messageTypeDataTranslations>) session.getAttribute("messageTypeDataTranslastions");

	Integer messageTypeId = (Integer) session.getAttribute("messageTypeId");
	
	int processOrder = translations.size() + 1;

        messageTypeDataTranslations translation = new messageTypeDataTranslations();
        translation.setMessageTypeId(messageTypeId);
        translation.setFieldId(field);
        translation.setfieldName(fieldText);
        translation.setCrosswalkId(cwId);
        translation.setcrosswalkName(cwText);
        translation.setProcessOrder(processOrder);

        translations.add(translation);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/messageTypeLibrary/existingTranslations");
        mav.addObject("dataTranslations", translations);

        return mav;

    }

    /**
     * The 'removeTranslations{params}' function will handle removing a translation from translations array.
     *
     * @param	fieldId This will hold the field that is being removed
     * @param	processOrder	This will hold the process order of the field to be removed so we remove the correct field number as the same field could be in the list with different crosswalks
     *
     * @Return	1	The function will simply return a 1 back to the ajax call
     */
    @RequestMapping(value = "/removeTranslations{params}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer removeTranslation(HttpSession session,@RequestParam(value = "fieldId", required = true) Integer fieldId, @RequestParam(value = "processOrder", required = true) Integer processOrder) throws Exception {

        List<messageTypeDataTranslations> translations = (List<messageTypeDataTranslations>) session.getAttribute("messageTypeDataTranslastions");

	Iterator<messageTypeDataTranslations> it = translations.iterator();
        int currProcessOrder;

        while (it.hasNext()) {
            messageTypeDataTranslations translation = it.next();
            if (translation.getFieldId() == fieldId && translation.getProcessOrder() == processOrder) {
                translations.remove(translation);
            } else if (translation.getProcessOrder() > processOrder) {
                currProcessOrder = translation.getProcessOrder();
                translation.setProcessOrder(currProcessOrder - 1);
            }
        }

        return 1;
    }

    /**
     * The 'updateTranslationProcessOrder{params}' function will handle removing a translation from translations array.
     *
     * @param	fieldId This will hold the field that is being removed
     * @param	processOrder	This will hold the process order of the field to be removed so we remove the correct field number as the same field could be in the list with different crosswalks
     *
     * @Return	1	The function will simply return a 1 back to the ajax call
     */
    @RequestMapping(value = "/updateTranslationProcessOrder{params}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer updateTranslationProcessOrder(HttpSession session,@RequestParam(value = "currProcessOrder", required = true) Integer currProcessOrder, @RequestParam(value = "newProcessOrder", required = true) Integer newProcessOrder) throws Exception {

        List<messageTypeDataTranslations> translations = (List<messageTypeDataTranslations>) session.getAttribute("messageTypeDataTranslastions");

	Iterator<messageTypeDataTranslations> it = translations.iterator();

        while (it.hasNext()) {
            messageTypeDataTranslations translation = it.next();
            if (translation.getProcessOrder() == currProcessOrder) {
                translation.setProcessOrder(newProcessOrder);
            } else if (translation.getProcessOrder() == newProcessOrder) {
                translation.setProcessOrder(currProcessOrder);
            }
        }

        return 1;
    }

}
