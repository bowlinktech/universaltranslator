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
import org.json.JSONArray;

/**
 *
 * @author chadmccue
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class medAlliesReferralInfo {
    
    private String fromDirectAddress;
    private String toDirectAddress;
    private String messageId;
    private String transitTime;
    private ArrayList toDirectAddressArray;
    
    @JsonProperty("envelopeInfo")
    private void setEnvelopeInfo(Map<String, Object> envelopeInfo) {
        this.fromDirectAddress = envelopeInfo.get("fromDirectAddress").toString();
        this.messageId = envelopeInfo.get("messageId").toString();
        this.transitTime = envelopeInfo.get("transitTime").toString();
        this.toDirectAddressArray = (ArrayList) envelopeInfo.get("toDirectAddress");
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
