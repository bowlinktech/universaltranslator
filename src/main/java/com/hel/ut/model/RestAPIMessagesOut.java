/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author gchan
 */
@Entity
@Table(name = "restapimessagesout")
public class RestAPIMessagesOut {
    
    @Transient
    private Integer totalMessages = 0;

    @Transient
    private String errorDisplayText = null, batchName = null, statusName = null, orgName = null;
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "orgId", nullable = true)
    private int orgId = 0;

    @Column(name = "payload", nullable = true)
    private String payload;

    @Column(name = "statusId", nullable = false)
    private int statusId = 3; //set to reject

    @Column(name = "errorId", nullable = true)
    private Integer errorId = 0;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    @Column(name = "batchDownloadId", nullable = true)
    private int batchDownloadId = 0;

    @Column(name = "configId", nullable = true)
    private int configId = 0;
    
    @Column(name = "responseStatus", nullable = true)
    private int responseStatus = 0;

    @Column(name = "responseMessage", nullable = true)
    private String responseMessage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Integer getErrorId() {
        return errorId;
    }

    public void setErrorId(Integer errorId) {
        this.errorId = errorId;
    }


    public int getBatchDownloadId() {
        return batchDownloadId;
    }

    public void setBatchDownloadId(int batchDownloadId) {
        this.batchDownloadId = batchDownloadId;
    }

    public int getConfigId() {
	return configId;
    }

    public void setConfigId(int configId) {
	this.configId = configId;
    }

    public String getOrgName() {
	return orgName;
    }

    public void setOrgName(String orgName) {
	this.orgName = orgName;
    }

    public String getStatusName() {
	return statusName;
    }

    public void setStatusName(String statusName) {
	this.statusName = statusName;
    }

    public String getErrorDisplayText() {
	return errorDisplayText;
    }

    public void setErrorDisplayText(String errorDisplayText) {
	this.errorDisplayText = errorDisplayText;
    }

    public String getBatchName() {
	return batchName;
    }

    public void setBatchName(String batchName) {
	this.batchName = batchName;
    }

    public int getResponseStatus() {
	return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
	this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
	return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
	this.responseMessage = responseMessage;
    }

    public Integer getTotalMessages() {
	return totalMessages;
    }

    public void setTotalMessages(Integer totalMessages) {
	this.totalMessages = totalMessages;
    }

}
