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
@Table(name = "BATCHUPLOADS")
public class batchUploads {

    @Transient
    private Integer totalTransactions = 0,transTotalNotFinal = 10, totalOpen = 0, totalClosed = 0, threshold = 100, watchListEntryId = 0, 
	    inboundBatchConfigurationType = 1, totalMessages = 0, totalErrorRows = 0;

    @Transient
    private String statusValue, usersName, endUserDisplayText = "",
	    tgtorgName, orgName, transportMethod, configName, uploadType = "",
	    referringBatch = "", dashboardRowColor = "table-secondary", 
	    entryMessage = "", relatedBatchDownloadIds, dmConfigKeyWord;

    @Transient
    private boolean watchListCompleted = false;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "ORGID", nullable = false)
    private int orgId;

    @Column(name = "USERID", nullable = false)
    private int userId;

    @Column(name = "UTBATCHCONFNAME", nullable = true)
    private String utBatchConfName = null;

    @Column(name = "UTBATCHNAME", nullable = false)
    private String utBatchName;

    @Column(name = "TRANSPORTMETHODID", nullable = false)
    private int transportMethodId;

    @NoHtml
    @Column(name = "ORIGINALFILENAME", nullable = false)
    private String originalFileName;

    @Column(name = "STATUSID", nullable = false)
    private int statusId;

    @Column(name = "STARTDATETIME", nullable = true)
    private Date startDateTime = null;

    @Column(name = "ENDDATETIME", nullable = true)
    private Date endDateTime = null;

    @Column(name = "TOTALRECORDCOUNT", nullable = false)
    private int totalRecordCount = 0;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted = false;

    @Column(name = "ERRORRECORDCOUNT", nullable = false)
    private int errorRecordCount = 0;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATESUBMITTED", nullable = false)
    private Date dateSubmitted = new Date();

    @Column(name = "configId", nullable = true)
    private Integer configId;

    @Column(name = "CONTAINSHEADERROW", nullable = false)
    private boolean containsHeaderRow = false;

    @Column(name = "delimChar", nullable = true)
    private String delimChar;

    @Column(name = "fileLocation", nullable = true)
    private String fileLocation;

    @NoHtml
    @Column(name = "originalFolder", nullable = false)
    private String originalFolder;

    @Column(name = "encodingId", nullable = true)
    private Integer encodingId = 1;

    @NoHtml
    @Column(name = "senderEmail", nullable = false)
    private String senderEmail;
    
    @NoHtml
    @Column(name = "recipientEmail", nullable = false)
    private String recipientEmail;
    
    @Column(name = "associatedBatchId", nullable = true)
    private Integer associatedBatchId = 0;

    public Integer getTotalTransactions() {
	return totalTransactions;
    }

    public void setTotalTransactions(Integer totalTransactions) {
	this.totalTransactions = totalTransactions;
    }

    public Integer getTransTotalNotFinal() {
	return transTotalNotFinal;
    }

    public void setTransTotalNotFinal(Integer transTotalNotFinal) {
	this.transTotalNotFinal = transTotalNotFinal;
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

    public Integer getThreshold() {
	return threshold;
    }

    public void setThreshold(Integer threshold) {
	this.threshold = threshold;
    }

    public Integer getWatchListEntryId() {
	return watchListEntryId;
    }

    public void setWatchListEntryId(Integer watchListEntryId) {
	this.watchListEntryId = watchListEntryId;
    }

    public Integer getInboundBatchConfigurationType() {
	return inboundBatchConfigurationType;
    }

    public void setInboundBatchConfigurationType(Integer inboundBatchConfigurationType) {
	this.inboundBatchConfigurationType = inboundBatchConfigurationType;
    }

    public Integer getTotalMessages() {
	return totalMessages;
    }

    public void setTotalMessages(Integer totalMessages) {
	this.totalMessages = totalMessages;
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

    public String getTgtorgName() {
	return tgtorgName;
    }

    public void setTgtorgName(String tgtorgName) {
	this.tgtorgName = tgtorgName;
    }

    public String getOrgName() {
	return orgName;
    }

    public void setOrgName(String orgName) {
	this.orgName = orgName;
    }

    public String getTransportMethod() {
	return transportMethod;
    }

    public void setTransportMethod(String transportMethod) {
	this.transportMethod = transportMethod;
    }

    public String getConfigName() {
	return configName;
    }

    public void setConfigName(String configName) {
	this.configName = configName;
    }

    public String getUploadType() {
	return uploadType;
    }

    public void setUploadType(String uploadType) {
	this.uploadType = uploadType;
    }

    public String getReferringBatch() {
	return referringBatch;
    }

    public void setReferringBatch(String referringBatch) {
	this.referringBatch = referringBatch;
    }

    public String getDashboardRowColor() {
	return dashboardRowColor;
    }

    public void setDashboardRowColor(String dashboardRowColor) {
	this.dashboardRowColor = dashboardRowColor;
    }

    public String getEntryMessage() {
	return entryMessage;
    }

    public void setEntryMessage(String entryMessage) {
	this.entryMessage = entryMessage;
    }

    public String getRelatedBatchDownloadIds() {
	return relatedBatchDownloadIds;
    }

    public void setRelatedBatchDownloadIds(String relatedBatchDownloadIds) {
	this.relatedBatchDownloadIds = relatedBatchDownloadIds;
    }

    public boolean isWatchListCompleted() {
	return watchListCompleted;
    }

    public void setWatchListCompleted(boolean watchListCompleted) {
	this.watchListCompleted = watchListCompleted;
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

    public String getUtBatchConfName() {
	return utBatchConfName;
    }

    public void setUtBatchConfName(String utBatchConfName) {
	this.utBatchConfName = utBatchConfName;
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

    public String getOriginalFileName() {
	return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
	this.originalFileName = originalFileName;
    }

    public int getStatusId() {
	return statusId;
    }

    public void setStatusId(int statusId) {
	this.statusId = statusId;
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

    public int getTotalRecordCount() {
	return totalRecordCount;
    }

    public void setTotalRecordCount(int totalRecordCount) {
	this.totalRecordCount = totalRecordCount;
    }

    public boolean isDeleted() {
	return deleted;
    }

    public void setDeleted(boolean deleted) {
	this.deleted = deleted;
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

    public Integer getConfigId() {
	return configId;
    }

    public void setConfigId(Integer configId) {
	this.configId = configId;
    }

    public boolean isContainsHeaderRow() {
	return containsHeaderRow;
    }

    public void setContainsHeaderRow(boolean containsHeaderRow) {
	this.containsHeaderRow = containsHeaderRow;
    }

    public String getDelimChar() {
	return delimChar;
    }

    public void setDelimChar(String delimChar) {
	this.delimChar = delimChar;
    }

    public String getFileLocation() {
	return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
	this.fileLocation = fileLocation;
    }

    public String getOriginalFolder() {
	return originalFolder;
    }

    public void setOriginalFolder(String originalFolder) {
	this.originalFolder = originalFolder;
    }

    public Integer getEncodingId() {
	return encodingId;
    }

    public void setEncodingId(Integer encodingId) {
	this.encodingId = encodingId;
    }

    public String getSenderEmail() {
	return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
	this.senderEmail = senderEmail;
    }

    public String getEndUserDisplayText() {
	return endUserDisplayText;
    }

    public void setEndUserDisplayText(String endUserDisplayText) {
	this.endUserDisplayText = endUserDisplayText;
    }

    public String getRecipientEmail() {
	return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
	this.recipientEmail = recipientEmail;
    }

    public String getDmConfigKeyWord() {
	return dmConfigKeyWord;
    }

    public void setDmConfigKeyWord(String dmConfigKeyWord) {
	this.dmConfigKeyWord = dmConfigKeyWord;
    }

    public Integer getAssociatedBatchId() {
	return associatedBatchId;
    }

    public void setAssociatedBatchId(Integer associatedBatchId) {
	this.associatedBatchId = associatedBatchId;
    }

    public Integer getTotalErrorRows() {
	return totalErrorRows;
    }

    public void setTotalErrorRows(Integer totalErrorRows) {
	this.totalErrorRows = totalErrorRows;
    }

}
