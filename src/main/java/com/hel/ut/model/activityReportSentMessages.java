/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

/**
 *
 * @author chadmccue
 */
public class activityReportSentMessages {
    
    private Integer rowId = 0;
    private Integer totalSent = 0;
    private Integer messageTypeId = 0;
    private String messageTypeName = "";
    private Integer targetOrgId = 0;
    private String orgName = "";
    private String sentBatchIds = "";
    private Integer totalOpen;
    private Integer totalClosed;

    public Integer getTotalOpen() {
        return totalOpen;
    }

    public void setTotalOpen(Integer totalOpen) {
        this.totalOpen = totalOpen;
    }

    public Integer getTotalClosed() {
        return totalClosed;
    }

    public void setTotalClosed(Integer totalClosed) {
        this.totalClosed = totalClosed;
    }
    
    

    public Integer getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(Integer totalSent) {
        this.totalSent = totalSent;
    }

    public Integer getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(Integer messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public String getMessageTypeName() {
        return messageTypeName;
    }

    public void setMessageTypeName(String messageTypeName) {
        this.messageTypeName = messageTypeName;
    }

    public Integer getTargetOrgId() {
        return targetOrgId;
    }

    public void setTargetOrgId(Integer targetOrgId) {
        this.targetOrgId = targetOrgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getSentBatchIds() {
        return sentBatchIds;
    }

    public void setSentBatchIds(String sentBatchIds) {
        this.sentBatchIds = sentBatchIds;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }
    
}
