/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author chadmccue
 */
public class Transaction {

    private int orgId;
    private int sourceSubOrgId;
    private int userId;
    private int configId;
    private String batchName = null;
    private String frombatchName = null;
    private String fileName = null;
    private int transportMethodId;
    private String originalFileName = null;
    private int statusId;
    private String statusValue;
    private int messageTypeId;
    private int transactionStatusId;
    private int targetOrgId;
    private int targetSubOrgId = 0;
    private List<Integer> targetConfigId;
    private boolean autoRelease = true;
    private Date dateSubmitted = null;
    private String messageTypeName = null;
    private int batchId = 0;
    private int transactionId = 0;
    private int transactionRecordId = 0;
    private int transactionTargetId = 0;
    private int sourceType = 1;
    private int internalStatusId = 0;
    private int orginialTransactionId = 0;
    private String reportableField1 = null;
    private String reportableField2 = null;
    private String reportableField3 = null;
    private String reportableField4 = null;
    private String reportableFieldHeading1 = null;
    private String reportableFieldHeading2 = null;
    private String reportableFieldHeading3 = null;
    private String reportableFieldHeading4 = null;

    private List<transactionRecords> sourceOrgFields = null;
    private List<transactionRecords> sourceProviderFields = null;
    private List<transactionRecords> targetOrgFields = null;
    private List<transactionRecords> targetProviderFields = null;
    private List<transactionRecords> patientFields = null;
    private List<transactionRecords> detailFields = null;
    private Integer attachmentLimit;
    private Boolean attachmentRequired = false;
    private String attachmentNote = "";

    private Integer messageStatus = 1;
    private String activityStatus = "";
    private String activityStatusName = "";

    private String srcConfigName;
    private String srcOrgName;
    private String targetConfigName;
    private String targetOrgName;
    private Integer targetConfigId1;
    private String srcSiteName;
    private Integer parentOrgId = 0;
    
    /*New Transaction Inbox / Sent Fields */
    private String patientInfo = "";
    private String sendingOrgInfo = "";
    private String targetOrgInfo = "";
    private String openClosed = "";
    private String referralId = "";
    private int totalRecordCount = 0;
    private int totalErrorCount = 0;

    public int getorgId() {
        return orgId;
    }

    public void setorgId(int orgId) {
        this.orgId = orgId;
    }

    public int getuserId() {
        return userId;
    }

    public void setuserId(int userId) {
        this.userId = userId;
    }

    public int getconfigId() {
        return configId;
    }

    public void setconfigId(int configId) {
        this.configId = configId;
    }

    public String getbatchName() {
        return batchName;
    }

    public void setbatchName(String batchName) {
        this.batchName = batchName;
    }

    public int gettransportMethodId() {
        return transportMethodId;
    }

    public void settransportMethodId(int transportMethodId) {
        this.transportMethodId = transportMethodId;
    }

    public String getoriginalFileName() {
        return originalFileName;
    }

    public void setoriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public int getstatusId() {
        return statusId;
    }

    public void setstatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getstatusValue() {
        return statusValue;
    }

    public void setstatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public int getmessageTypeId() {
        return messageTypeId;
    }

    public void setmessageTypeId(int messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public int gettransactionStatusId() {
        return transactionStatusId;
    }

    public void settransactionStatusId(int transactionStatusId) {
        this.transactionStatusId = transactionStatusId;
    }

    public int gettargetOrgId() {
        return targetOrgId;
    }

    public void settargetOrgId(int targetOrgId) {
        this.targetOrgId = targetOrgId;
    }

    public List<Integer> gettargetConfigId() {
        return targetConfigId;
    }

    public void settargetConfigId(List<Integer> targetConfigId) {
        this.targetConfigId = targetConfigId;
    }

    public List<transactionRecords> getsourceOrgFields() {
        return sourceOrgFields;
    }

    public void setsourceOrgFields(List<transactionRecords> sourceOrgFields) {
        this.sourceOrgFields = sourceOrgFields;
    }

    public List<transactionRecords> getsourceProviderFields() {
        return sourceProviderFields;
    }

    public void setsourceProviderFields(List<transactionRecords> sourceProviderFields) {
        this.sourceProviderFields = sourceProviderFields;
    }

    public List<transactionRecords> gettargetOrgFields() {
        return targetOrgFields;
    }

    public void settargetOrgFields(List<transactionRecords> targetOrgFields) {
        this.targetOrgFields = targetOrgFields;
    }

    public List<transactionRecords> gettargetProviderFields() {
        return targetProviderFields;
    }

    public void settargetProviderFields(List<transactionRecords> targetProviderFields) {
        this.targetProviderFields = targetProviderFields;
    }

    public List<transactionRecords> getpatientFields() {
        return patientFields;
    }

    public void setpatientFields(List<transactionRecords> patientFields) {
        this.patientFields = patientFields;
    }

    public List<transactionRecords> getdetailFields() {
        return detailFields;
    }

    public void setdetailFields(List<transactionRecords> detailFields) {
        this.detailFields = detailFields;
    }

    public boolean getautoRelease() {
        return autoRelease;
    }

    public void setautoRelease(boolean autoRelease) {
        this.autoRelease = autoRelease;
    }

    public Date getdateSubmitted() {
        return dateSubmitted;
    }

    public void setdateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getmessageTypeName() {
        return messageTypeName;
    }

    public void setmessageTypeName(String messageTypeName) {
        this.messageTypeName = messageTypeName;
    }

    public int gettransactionRecordId() {
        return transactionRecordId;
    }

    public void settransactionRecordId(int transactionRecordId) {
        this.transactionRecordId = transactionRecordId;
    }

    public int getbatchId() {
        return batchId;
    }

    public void setbatchId(int batchId) {
        this.batchId = batchId;
    }

    public int gettransactionId() {
        return transactionId;
    }

    public void settransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int gettransactionTargetId() {
        return transactionTargetId;
    }

    public void settransactionTargetId(int transactionTargetId) {
        this.transactionTargetId = transactionTargetId;
    }

    public int getsourceType() {
        return sourceType;
    }

    public void setsourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public int getinternalStatusId() {
        return internalStatusId;
    }

    public void setinternalStatusId(int internalStatusId) {
        this.internalStatusId = internalStatusId;
    }

    public int getorginialTransactionId() {
        return orginialTransactionId;
    }

    public void setorginialTransactionId(int orginialTransactionId) {
        this.orginialTransactionId = orginialTransactionId;
    }

    public String getreportableField1() {
        return reportableField1;
    }

    public void setreportableField1(String reportableField1) {
        this.reportableField1 = reportableField1;
    }

    public String getreportableField2() {
        return reportableField2;
    }

    public void setreportableField2(String reportableField2) {
        this.reportableField2 = reportableField2;
    }

    public String getreportableField3() {
        return reportableField3;
    }

    public void setreportableField3(String reportableField3) {
        this.reportableField3 = reportableField3;
    }

    public String getreportableField4() {
        return reportableField4;
    }

    public void setreportableField4(String reportableField4) {
        this.reportableField4 = reportableField4;
    }

    public String getreportableFieldHeading1() {
        return reportableFieldHeading1;
    }

    public void setreportableFieldHeading1(String reportableFieldHeading1) {
        this.reportableFieldHeading1 = reportableFieldHeading1;
    }

    public String getreportableFieldHeading2() {
        return reportableFieldHeading2;
    }

    public void setreportableFieldHeading2(String reportableFieldHeading2) {
        this.reportableFieldHeading2 = reportableFieldHeading2;
    }

    public String getreportableFieldHeading3() {
        return reportableFieldHeading3;
    }

    public void setreportableFieldHeading3(String reportableFieldHeading3) {
        this.reportableFieldHeading3 = reportableFieldHeading3;
    }

    public String getreportableFieldHeading4() {
        return reportableFieldHeading4;
    }

    public void setreportableFieldHeading4(String reportableFieldHeading4) {
        this.reportableFieldHeading4 = reportableFieldHeading4;
    }

    public int getmessageStatus() {
        return messageStatus;
    }

    public void setmessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public int getTargetSubOrgId() {
        return targetSubOrgId;
    }

    public void setTargetSubOrgId(int targetSubOrgId) {
        this.targetSubOrgId = targetSubOrgId;
    }

    public int getSourceSubOrgId() {
        return sourceSubOrgId;
    }

    public void setSourceSubOrgId(int sourceSubOrgId) {
        this.sourceSubOrgId = sourceSubOrgId;
    }

    public Integer getAttachmentLimit() {
        return attachmentLimit;
    }

    public void setAttachmentLimit(Integer attachmentLimit) {
        this.attachmentLimit = attachmentLimit;
    }

    public Boolean getAttachmentRequired() {
        return attachmentRequired;
    }

    public void setAttachmentRequired(Boolean attachmentRequired) {
        this.attachmentRequired = attachmentRequired;
    }

    public String getAttachmentNote() {
        return attachmentNote;
    }

    public void setAttachmentNote(String attachmentNote) {
        this.attachmentNote = attachmentNote;
    }

    public List<Integer> getTargetConfigId() {
        return targetConfigId;
    }

    public void setTargetConfigId(List<Integer> targetConfigId) {
        this.targetConfigId = targetConfigId;
    }

    public String getSrcConfigName() {
        return srcConfigName;
    }

    public void setSrcConfigName(String srcConfigName) {
        this.srcConfigName = srcConfigName;
    }

    public String getSrcOrgName() {
        return srcOrgName;
    }

    public void setSrcOrgName(String srcOrgName) {
        this.srcOrgName = srcOrgName;
    }

    public String getTargetConfigName() {
        return targetConfigName;
    }

    public void setTargetConfigName(String targetConfigName) {
        this.targetConfigName = targetConfigName;
    }

    public String getTargetOrgName() {
        return targetOrgName;
    }

    public void setTargetOrgName(String targetOrgName) {
        this.targetOrgName = targetOrgName;
    }

    public Integer getTargetConfigId1() {
        return targetConfigId1;
    }

    public void setTargetConfigId1(Integer targetConfigId1) {
        this.targetConfigId1 = targetConfigId1;
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

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
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

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public int getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(int messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public int getTransactionStatusId() {
        return transactionStatusId;
    }

    public void setTransactionStatusId(int transactionStatusId) {
        this.transactionStatusId = transactionStatusId;
    }

    public int getTargetOrgId() {
        return targetOrgId;
    }

    public void setTargetOrgId(int targetOrgId) {
        this.targetOrgId = targetOrgId;
    }

    public boolean isAutoRelease() {
        return autoRelease;
    }

    public void setAutoRelease(boolean autoRelease) {
        this.autoRelease = autoRelease;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getMessageTypeName() {
        return messageTypeName;
    }

    public void setMessageTypeName(String messageTypeName) {
        this.messageTypeName = messageTypeName;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getTransactionRecordId() {
        return transactionRecordId;
    }

    public void setTransactionRecordId(int transactionRecordId) {
        this.transactionRecordId = transactionRecordId;
    }

    public int getTransactionTargetId() {
        return transactionTargetId;
    }

    public void setTransactionTargetId(int transactionTargetId) {
        this.transactionTargetId = transactionTargetId;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public int getInternalStatusId() {
        return internalStatusId;
    }

    public void setInternalStatusId(int internalStatusId) {
        this.internalStatusId = internalStatusId;
    }

    public int getOrginialTransactionId() {
        return orginialTransactionId;
    }

    public void setOrginialTransactionId(int orginialTransactionId) {
        this.orginialTransactionId = orginialTransactionId;
    }

    public String getReportableField1() {
        return reportableField1;
    }

    public void setReportableField1(String reportableField1) {
        this.reportableField1 = reportableField1;
    }

    public String getReportableField2() {
        return reportableField2;
    }

    public void setReportableField2(String reportableField2) {
        this.reportableField2 = reportableField2;
    }

    public String getReportableField3() {
        return reportableField3;
    }

    public void setReportableField3(String reportableField3) {
        this.reportableField3 = reportableField3;
    }

    public String getReportableField4() {
        return reportableField4;
    }

    public void setReportableField4(String reportableField4) {
        this.reportableField4 = reportableField4;
    }

    public String getReportableFieldHeading1() {
        return reportableFieldHeading1;
    }

    public void setReportableFieldHeading1(String reportableFieldHeading1) {
        this.reportableFieldHeading1 = reportableFieldHeading1;
    }

    public String getReportableFieldHeading2() {
        return reportableFieldHeading2;
    }

    public void setReportableFieldHeading2(String reportableFieldHeading2) {
        this.reportableFieldHeading2 = reportableFieldHeading2;
    }

    public String getReportableFieldHeading3() {
        return reportableFieldHeading3;
    }

    public void setReportableFieldHeading3(String reportableFieldHeading3) {
        this.reportableFieldHeading3 = reportableFieldHeading3;
    }

    public String getReportableFieldHeading4() {
        return reportableFieldHeading4;
    }

    public void setReportableFieldHeading4(String reportableFieldHeading4) {
        this.reportableFieldHeading4 = reportableFieldHeading4;
    }

    public List<transactionRecords> getSourceOrgFields() {
        return sourceOrgFields;
    }

    public void setSourceOrgFields(List<transactionRecords> sourceOrgFields) {
        this.sourceOrgFields = sourceOrgFields;
    }

    public List<transactionRecords> getSourceProviderFields() {
        return sourceProviderFields;
    }

    public void setSourceProviderFields(
            List<transactionRecords> sourceProviderFields) {
        this.sourceProviderFields = sourceProviderFields;
    }

    public List<transactionRecords> getTargetOrgFields() {
        return targetOrgFields;
    }

    public void setTargetOrgFields(List<transactionRecords> targetOrgFields) {
        this.targetOrgFields = targetOrgFields;
    }

    public List<transactionRecords> getTargetProviderFields() {
        return targetProviderFields;
    }

    public void setTargetProviderFields(
            List<transactionRecords> targetProviderFields) {
        this.targetProviderFields = targetProviderFields;
    }

    public List<transactionRecords> getPatientFields() {
        return patientFields;
    }

    public void setPatientFields(List<transactionRecords> patientFields) {
        this.patientFields = patientFields;
    }

    public List<transactionRecords> getDetailFields() {
        return detailFields;
    }

    public void setDetailFields(List<transactionRecords> detailFields) {
        this.detailFields = detailFields;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getSrcSiteName() {
        return srcSiteName;
    }

    public void setSrcSiteName(String srcSiteName) {
        this.srcSiteName = srcSiteName;
    }

    public Integer getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(Integer parentOrgId) {
        this.parentOrgId = parentOrgId;
    }
    
    public String getPatientInfo() {
        return patientInfo;
    }

    public void setPatientInfo(String patientInfo) {
        this.patientInfo = patientInfo;
    }

    public String getSendingOrgInfo() {
        return sendingOrgInfo;
    }

    public void setSendingOrgInfo(String sendingOrgInfo) {
        this.sendingOrgInfo = sendingOrgInfo;
    }

    public String getTargetOrgInfo() {
        return targetOrgInfo;
    }

    public void setTargetOrgInfo(String targetOrgInfo) {
        this.targetOrgInfo = targetOrgInfo;
    }

    public String getOpenClosed() {
        return openClosed;
    }

    public void setOpenClosed(String openClosed) {
        this.openClosed = openClosed;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }
    
    public String getActivityStatusName() {
        return activityStatusName;
    }

    public void setActivityStatusName(String activityStatusName) {
        this.activityStatusName = activityStatusName;
    }

    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    public String getFrombatchName() {
	return frombatchName;
    }

    public void setFrombatchName(String frombatchName) {
	this.frombatchName = frombatchName;
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
    
    
}
