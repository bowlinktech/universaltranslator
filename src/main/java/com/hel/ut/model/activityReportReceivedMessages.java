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
public class activityReportReceivedMessages {
    
    private Integer rowId = 0;
    private Integer totalReceived = 0;
    private Integer messageTypeId = 0;
    private String messageTypeName = "";
    private Integer sourceOrgId = 0;
    private String orgName = "";
    private Integer totalOpen;
    private Integer totalClosed;
    private String batchIds = "";
    private String recevedBatchIds = "";
    private Integer totalTransactions = 0;

    public Integer getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(Integer totalReceived) {
        this.totalReceived = totalReceived;
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

    public Integer getSourceOrgId() {
        return sourceOrgId;
    }

    public void setSourceOrgId(Integer sourceOrgId) {
        this.sourceOrgId = sourceOrgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

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

    public String getBatchIds() {
        return batchIds;
    }

    public void setBatchIds(String batchIds) {
        this.batchIds = batchIds;
    }

    public String getRecevedBatchIds() {
        return recevedBatchIds;
    }

    public void setRecevedBatchIds(String recevedBatchIds) {
        this.recevedBatchIds = recevedBatchIds;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public Integer getTotalTransactions() {
	return totalTransactions;
    }

    public void setTotalTransactions(Integer totalTransactions) {
	this.totalTransactions = totalTransactions;
    }
    
}
