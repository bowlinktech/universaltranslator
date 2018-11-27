/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service.impl;

import com.hel.ut.model.mailMessage;
import com.hel.ut.service.emailMessageManager;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author chad
 */
@Service
public class emailMessageManagerImpl implements emailMessageManager {

    private JavaMailSender mailSender;

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(mailMessage messageDetails) throws Exception {

        MimeMessage msg = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            helper.setFrom(messageDetails.getfromEmailAddress());
            helper.setTo(messageDetails.gettoEmailAddress());
	    
            if (messageDetails.getccEmailAddress() != null) {
		helper.setCc(messageDetails.getccEmailAddress());
            }
	    
	    if(messageDetails.getBccEmailAddress() != null) {
		helper.setBcc(messageDetails.getBccEmailAddress());
	    }
	   
            helper.setSubject(messageDetails.getmessageSubject());
	    
            helper.setText("", messageDetails.getmessageBody());
            helper.setReplyTo(messageDetails.getfromEmailAddress());

            mailSender.send(msg);

        } catch (Exception e) {
            throw new Exception(e);
        }

    }
}
