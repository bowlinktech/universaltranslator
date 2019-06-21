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
public class medAlliesReferralAttachmentList {
    
    private String attachmentClass;
    private String attachmentContent;
    private String attachmentHash;
    private String attachmentSize;
    private String attachmentTitle;
    private String attachmentTypeCode;
    private String attachmentTypeCodeText;
    
    public String getAttachmentClass() {
        return attachmentClass;
    }

    @JsonProperty("attachmentClass")
    public void setAttachmentClass(String attachmentClass) {
        this.attachmentClass = attachmentClass;
    }

    public String getAttachmentContent() {
        return attachmentContent;
    }

    @JsonProperty("attachmentContent")
    public void setAttachmentContent(String attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    public String getAttachmentHash() {
        return attachmentHash;
    }

    @JsonProperty("attachmentHash")
    public void setAttachmentHash(String attachmentHash) {
        this.attachmentHash = attachmentHash;
    }

    public String getAttachmentSize() {
        return attachmentSize;
    }

    @JsonProperty("attachmentSize")
    public void setAttachmentSize(String attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    public String getAttachmentTitle() {
        return attachmentTitle;
    }

    @JsonProperty("attachmentTitle")
    public void setAttachmentTitle(String attachmentTitle) {
        this.attachmentTitle = attachmentTitle;
    }

    public String getAttachmentTypeCode() {
        return attachmentTypeCode;
    }

    @JsonProperty("attachmentTypeCode")
    public void setAttachmentTypeCode(String attachmentTypeCode) {
        this.attachmentTypeCode = attachmentTypeCode;
    }

    public String getAttachmentTypeCodeText() {
        return attachmentTypeCodeText;
    }

    @JsonProperty("attachmentTypeCodeText")
    public void setAttachmentTypeCodeText(String attachmentTypeCodeText) {
        this.attachmentTypeCodeText = attachmentTypeCodeText;
    }
}
