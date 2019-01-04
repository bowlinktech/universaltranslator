/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.controller;

import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.transactionOutManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.hel.ut.service.utConfigurationManager;

/**
 *
 * @author chadmccue
 */
@Controller
@RequestMapping("/scheduleTask")
public class scheduledTaskController {

    @Autowired
    private transactionOutManager transactionOutManager;

    @Autowired
    private utConfigurationManager configurationManager;

    @Autowired
    private transactionInManager transactionInManager;

    

}
