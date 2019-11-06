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

import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author gchan
 */
@Entity
@Table(name = "directmessagesout")
public class directmessagesout {
    
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
    
    @Column(name = "outputFileName", nullable = false)
    private String outputFileName;
    
    @Column(name = "statusId", nullable = false)
    private int statusId = 1; //set to reject

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    @Column(name = "batchUploadId", nullable = true)
    private int batchUploadId = 0;
    
    @Column(name = "batchDownloadId", nullable = true)
    private int batchDownloadId = 0;

    @Column(name = "configId", nullable = true)
    private int configId = 0;
    
    @Column(name = "orgId", nullable = true)
    private int orgId = 0;
    
    @Column(name = "responseStatus", nullable = true)
    private int responseStatus;
    
    @Column(name = "responseMessage", nullable = true)
    private String responseMessage;

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

    public String getOutputFileName() {
	return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
	this.outputFileName = outputFileName;
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

    public int getOrgId() {
	return orgId;
    }

    public void setOrgId(int orgId) {
	this.orgId = orgId;
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

    
}
