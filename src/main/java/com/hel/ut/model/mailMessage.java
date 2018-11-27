/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

/**
 *
 * @author chad
 */
public class mailMessage {

    String messageSubject = null;
    String toEmailAddress = null;
    String[] ccEmailAddress = null;
    String[] bccEmailAddress = null;
    String fromEmailAddress = null;
    String messageBody = null;

    public void setmessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }

    public String getmessageSubject() {
        return messageSubject;
    }

    public void settoEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

    public String gettoEmailAddress() {
        return toEmailAddress;
    }

    public void setfromEmailAddress(String fromEmailAddress) {
        this.fromEmailAddress = fromEmailAddress;
    }

    public String getfromEmailAddress() {
        return fromEmailAddress;
    }

    public void setmessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getmessageBody() {
        return messageBody;
    }

    public void setccEmailAddress(String[] ccEmailAddress) {
        this.ccEmailAddress = ccEmailAddress;
    }

    public String[] getccEmailAddress() {
        return ccEmailAddress;
    }

    public String[] getBccEmailAddress() {
	return bccEmailAddress;
    }

    public void setBccEmailAddress(String[] bccEmailAddress) {
	this.bccEmailAddress = bccEmailAddress;
    }
    
    

}
