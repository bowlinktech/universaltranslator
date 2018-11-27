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
    private Integer totalTransactions = 0, threshold = 100;;

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
    private String outputFIleName = null;

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

    public int getuserId() {
        return userId;
    }

    public void setuserId(int userId) {
        this.userId = userId;
    }

    public String getutBatchName() {
        return utBatchName;
    }

    public void setutBatchName(String utBatchName) {
        this.utBatchName = utBatchName;
    }

    public int gettransportMethodId() {
        return transportMethodId;
    }

    public void settransportMethodId(int transportMethodId) {
        this.transportMethodId = transportMethodId;
    }

    public String getoutputFIleName() {
        return outputFIleName;
    }

    public void setoutputFIleName(String outputFIleName) {
        this.outputFIleName = outputFIleName;
    }

    public int getstatusId() {
        return statusId;
    }

    public void setstatusId(int statusId) {
        this.statusId = statusId;
    }

    public Date getstartDateTime() {
        return startDateTime;
    }

    public void setstartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getendDateTime() {
        return endDateTime;
    }

    public void setendDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public int gettotalRecordCount() {
        return totalRecordCount;
    }

    public void settotalRecordCount(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public boolean getdeleted() {
        return deleted;
    }

    public void setdeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int gettotalErrorCount() {
        return totalErrorCount;
    }

    public void settotalErrorCount(int totalErrorCount) {
        this.totalErrorCount = totalErrorCount;
    }

    public Date getdateCreated() {
        return dateCreated;
    }

    public void setdateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void settotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int gettotalTransactions() {
        return totalTransactions;
    }

    public String getstatusValue() {
        return statusValue;
    }

    public void setstatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public String getusersName() {
        return usersName;
    }

    public void setusersName(String usersName) {
        this.usersName = usersName;
    }

    public boolean getmergeable() {
        return mergeable;
    }

    public void setmergeable(boolean mergeable) {
        this.mergeable = mergeable;
    }

    public Date getlastDownloaded() {
        return lastDownloaded;
    }

    public void setlastDownloaded(Date lastDownloaded) {
        this.lastDownloaded = lastDownloaded;
    }

    public String getorgName() {
        return orgName;
    }

    public void setorgName(String orgName) {
        this.orgName = orgName;
    }

    public String gettransportMethod() {
        return transportMethod;
    }

    public void settransportMethod(String transportMethod) {
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

    public int getFromOrgId() {
        return fromOrgId;
    }

    public void setFromOrgId(int fromOrgId) {
        this.fromOrgId = fromOrgId;
    }

    public String getConfigName() {
	return configName;
    }

    public void setConfigName(String configName) {
	this.configName = configName;
    }

    public String getTgtorgName() {
	return tgtorgName;
    }

    public void setTgtorgName(String tgtorgName) {
	this.tgtorgName = tgtorgName;
    }

    public Date getLastDownloaded() {
	return lastDownloaded;
    }

    public void setLastDownloaded(Date lastDownloaded) {
	this.lastDownloaded = lastDownloaded;
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

    public String getOriginalFileName() {
	return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
	this.originalFileName = originalFileName;
    }

    public Integer getThreshold() {
	return threshold;
    }

    public void setThreshold(Integer threshold) {
	this.threshold = threshold;
    }

    public String getDashboardRowColor() {
	return dashboardRowColor;
    }

    public void setDashboardRowColor(String dashboardRowColor) {
	this.dashboardRowColor = dashboardRowColor;
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
    
    
}
