/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
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
 * @author chadmccue
 */
@Entity
@Table(name = "BATCHDOWNLOADS")
public class batchDownloads {

    @Transient
    private Integer totalTransactions = 0, threshold = 100, totalBatchDownloads = 0, totalDelivered = 0, totalMessages = 0;

    @Transient
    private String statusValue, usersName, orgName, configName, transportMethod,fromBatchName,fromBatchFile, tgtorgName, originalFileName, dashboardRowColor = "table-secondary";

    @Transient
    private int fromOrgId, errorRecordCount = 0;
    
    @Transient
    private Date dateSubmitted;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "ORGID", nullable = false)
    private int orgId;

    @Column(name = "USERID", nullable = false)
    private int userId;

    @NoHtml
    @Column(name = "UTBATCHNAME", nullable = false)
    private String utBatchName;

    @Column(name = "TRANSPORTMETHODID", nullable = false)
    private int transportMethodId;

    @NoHtml
    @Column(name = "OUTPUTFILENAME", nullable = true)
    private String outputFileName = null;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    @Column(name = "STARTDATETIME", nullable = true)
    private Date startDateTime = null;

    @Column(name = "ENDDATETIME", nullable = true)
    private Date endDateTime = null;

    @Column(name = "STATUSID", nullable = false)
    private int statusId;

    @Column(name = "TOTALRECORDCOUNT", nullable = false)
    private int totalRecordCount = 0;

    @Column(name = "TOTALERRORCOUNT", nullable = false)
    private int totalErrorCount = 0;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted = false;

    @Column(name = "MERGEABLE", nullable = false)
    private boolean mergeable = false;

    @Column(name = "LASTDOWNLOADED", nullable = true)
    private Date lastDownloaded = null;
    
    @Column(name = "configId", nullable = false)
    private int configId;
    
    @Column(name = "batchUploadId", nullable = false)
    private int batchUploadId;

    public Integer getTotalTransactions() {
	return totalTransactions;
    }

    public void setTotalTransactions(Integer totalTransactions) {
	this.totalTransactions = totalTransactions;
    }

    public Integer getThreshold() {
	return threshold;
    }

    public void setThreshold(Integer threshold) {
	this.threshold = threshold;
    }

    public Integer getTotalBatchDownloads() {
	return totalBatchDownloads;
    }

    public void setTotalBatchDownloads(Integer totalBatchDownloads) {
	this.totalBatchDownloads = totalBatchDownloads;
    }

    public Integer getTotalDelivered() {
	return totalDelivered;
    }

    public void setTotalDelivered(Integer totalDelivered) {
	this.totalDelivered = totalDelivered;
    }

    public String getStatusValue() {
	return statusValue;
    }

    public void setStatusValue(String statusValue) {
	this.statusValue = statusValue;
    }

    public String getUsersName() {
	return usersName;
    }

    public void setUsersName(String usersName) {
	this.usersName = usersName;
    }

    public String getOrgName() {
	return orgName;
    }

    public void setOrgName(String orgName) {
	this.orgName = orgName;
    }

    public String getConfigName() {
	return configName;
    }

    public void setConfigName(String configName) {
	this.configName = configName;
    }

    public String getTransportMethod() {
	return transportMethod;
    }

    public void setTransportMethod(String transportMethod) {
	this.transportMethod = transportMethod;
    }

    public String getFromBatchName() {
	return fromBatchName;
    }

    public void setFromBatchName(String fromBatchName) {
	this.fromBatchName = fromBatchName;
    }

    public String getFromBatchFile() {
	return fromBatchFile;
    }

    public void setFromBatchFile(String fromBatchFile) {
	this.fromBatchFile = fromBatchFile;
    }

    public String getTgtorgName() {
	return tgtorgName;
    }

    public void setTgtorgName(String tgtorgName) {
	this.tgtorgName = tgtorgName;
    }

    public String getOriginalFileName() {
	return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
	this.originalFileName = originalFileName;
    }

    public String getDashboardRowColor() {
	return dashboardRowColor;
    }

    public void setDashboardRowColor(String dashboardRowColor) {
	this.dashboardRowColor = dashboardRowColor;
    }

    public int getFromOrgId() {
	return fromOrgId;
    }

    public void setFromOrgId(int fromOrgId) {
	this.fromOrgId = fromOrgId;
    }

    public int getErrorRecordCount() {
	return errorRecordCount;
    }

    public void setErrorRecordCount(int errorRecordCount) {
	this.errorRecordCount = errorRecordCount;
    }

    public Date getDateSubmitted() {
	return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
	this.dateSubmitted = dateSubmitted;
    }

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

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public String getUtBatchName() {
	return utBatchName;
    }

    public void setUtBatchName(String utBatchName) {
	this.utBatchName = utBatchName;
    }

    public int getTransportMethodId() {
	return transportMethodId;
    }

    public void setTransportMethodId(int transportMethodId) {
	this.transportMethodId = transportMethodId;
    }

    public String getOutputFileName() {
	return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
	this.outputFileName = outputFileName;
    }

    public Date getDateCreated() {
	return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }

    public Date getStartDateTime() {
	return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
	this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
	return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
	this.endDateTime = endDateTime;
    }

    public int getStatusId() {
	return statusId;
    }

    public void setStatusId(int statusId) {
	this.statusId = statusId;
    }

    public int getTotalRecordCount() {
	return totalRecordCount;
    }

    public void setTotalRecordCount(int totalRecordCount) {
	this.totalRecordCount = totalRecordCount;
    }

    public int getTotalErrorCount() {
	return totalErrorCount;
    }

    public void setTotalErrorCount(int totalErrorCount) {
	this.totalErrorCount = totalErrorCount;
    }

    public boolean isDeleted() {
	return deleted;
    }

    public void setDeleted(boolean deleted) {
	this.deleted = deleted;
    }

    public boolean isMergeable() {
	return mergeable;
    }

    public void setMergeable(boolean mergeable) {
	this.mergeable = mergeable;
    }

    public Date getLastDownloaded() {
	return lastDownloaded;
    }

    public void setLastDownloaded(Date lastDownloaded) {
	this.lastDownloaded = lastDownloaded;
    }

    public int getConfigId() {
	return configId;
    }

    public void setConfigId(int configId) {
	this.configId = configId;
    }

    public int getBatchUploadId() {
	return batchUploadId;
    }

    public void setBatchUploadId(int batchUploadId) {
	this.batchUploadId = batchUploadId;
    }

    public Integer getTotalMessages() {
	return totalMessages;
    }

    public void setTotalMessages(Integer totalMessages) {
	this.totalMessages = totalMessages;
    }
    
}
