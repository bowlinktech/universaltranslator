/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.jobs;

import com.hel.ut.service.transactionInManager;
import com.registryKit.messenger.emailManager;
import com.registryKit.messenger.emailMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 *
 * @author gchan
 */
public class MoveRRFiles implements Job {

    @Autowired
    private transactionInManager transactionInManager;

    @Autowired
    private emailManager emailmanager;
    
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        try {
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
            transactionInManager.moveRRFiles();
        } catch (Exception ex) {
        	try {
            	try {
                	/* Send Email to admin*/
                    emailMessage messageDetails = new emailMessage();
                    messageDetails.settoEmailAddress(System.getProperty("admin.email"));
                    messageDetails.setmessageSubject("MoveRRFiles job error - " + System.getProperty("server.identity"));
                    messageDetails.setmessageBody(ex.toString());
                    messageDetails.setfromEmailAddress("support@health-e-link.net");
                    emailmanager.sendEmail(messageDetails);
                	throw new Exception("Error occurred for MoveRRFiles  - schedule task",ex);
                } catch (Exception ex1) {
                    Logger.getLogger(MoveRRFiles.class.getName()).log(Level.SEVERE, null, ex1);
                }
            	
            	throw new Exception("Error occurred for MoveRRFiles job  - new type schedule task",ex);
            } catch (Exception ex1) {
                Logger.getLogger(MoveRRFiles.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
}