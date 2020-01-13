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
@Table(name = "directmessagesin")
public class directmessagesin {
    
    @Transient
    private String orgName = null, statusName = null, batchName = null;

    @Transient
    private Integer totalMessages = 0;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "hispId", nullable = false)
    private int hispId = 0;

    @Column(name = "fromDirectAddress", nullable = false)
    private String fromDirectAddress;
    
    @Column(name = "toDirectAddress", nullable = false)
    private String toDirectAddress;
    
    @Column(name = "archiveFileName", nullable = false)
    private String archiveFileName;
    
    @Column(name = "referralFileName", nullable = false)
    private String referralFileName;
    
    /**
     * 1 - not processed 2 - processed 3 - rejected
     *
     */
    @Column(name = "statusId", nullable = false)
    private int statusId = 1; //set to reject

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    @Column(name = "batchUploadId", nullable = true)
    private int batchUploadId = 0;

    @Column(name = "configId", nullable = true)
    private int configId = 0;
    
    @Column(name = "orgId", nullable = true)
    private int orgId = 0;
    
    @Column(name = "sendingResponse", nullable = true)
    private String sendingResponse;
     
    @Column(name = "originalDirectMessage", nullable = false)
    private String originalDirectMessage;

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

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHispId() {
        return hispId;
    }

    public void setHispId(int hispId) {
        this.hispId = hispId;
    }

    public String getFromDirectAddress() {
        return fromDirectAddress;
    }

    public void setFromDirectAddress(String fromDirectAddress) {
        this.fromDirectAddress = fromDirectAddress;
    }

    public String getToDirectAddress() {
        return toDirectAddress;
    }

    public void setToDirectAddress(String toDirectAddress) {
        this.toDirectAddress = toDirectAddress;
    }

    public String getArchiveFileName() {
        return archiveFileName;
    }

    public void setArchiveFileName(String archiveFileName) {
        this.archiveFileName = archiveFileName;
    }

    public String getReferralFileName() {
        return referralFileName;
    }

    public void setReferralFileName(String referralFileName) {
        this.referralFileName = referralFileName;
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

    public int getBatchUploadId() {
        return batchUploadId;
    }

    public void setBatchUploadId(int batchUploadId) {
        this.batchUploadId = batchUploadId;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getSendingResponse() {
	return sendingResponse;
    }

    public void setSendingResponse(String sendingResponse) {
	this.sendingResponse = sendingResponse;
    }

    public String getOriginalDirectMessage() {
	return originalDirectMessage;
    }

    public void setOriginalDirectMessage(String originalDirectMessage) {
	this.originalDirectMessage = originalDirectMessage;
    }

    public Integer getTotalMessages() {
	return totalMessages;
    }

    public void setTotalMessages(Integer totalMessages) {
	this.totalMessages = totalMessages;
    }
    
}
