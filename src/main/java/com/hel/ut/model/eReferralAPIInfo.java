/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author chadmccue
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class eReferralAPIInfo {
    
    private String fromDirectAddress;
    private String toDirectAddress;
    private String messageId;
    private String transitTime;
    private ArrayList toDirectAddressArray;
    
    @JsonProperty("messageInfo")
    private void setMessageInfo(Map<String, Object> messageInfo) {
        this.fromDirectAddress = messageInfo.get("fromDirectAddress").toString();
       
	if(messageInfo.containsKey("messageId")) {
	    this.messageId = messageInfo.get("messageId").toString();
	}
	if(messageInfo.containsKey("transitTime")) {
	    this.transitTime = messageInfo.get("transitTime").toString();
	}
        
        this.toDirectAddressArray = (ArrayList) messageInfo.get("toDirectAddress");
        if(!this.toDirectAddressArray.isEmpty()) {
            this.toDirectAddress = this.toDirectAddressArray.get(0).toString();
        }
    }

    public String getFromDirectAddress() {
        return fromDirectAddress;
    }

    public String getToDirectAddress() {
        return toDirectAddress;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTransitTime() {
        return transitTime;
    }
    
}
