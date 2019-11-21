/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 *
 * @author chadmccue
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class restMessageInfo {
    
    private String messageId;
    private String transitTime;
    
    @JsonProperty("envelopeInfo")
    private void setEnvelopeInfo(Map<String, Object> envelopeInfo) {
        this.messageId = envelopeInfo.get("messageId").toString();
        this.transitTime = envelopeInfo.get("transitTime").toString();
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTransitTime() {
        return transitTime;
    }
    
}
