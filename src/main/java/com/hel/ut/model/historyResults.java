/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.util.Date;
import javax.persistence.Column;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author chadmccue
 */
public class historyResults {

    private int orgId;
    private String orgName = null;
    private int messageTypeId;
    private String messageType = null;
    private String msg = null;
    private int type;
    boolean showDetails = false;
    private String transportType = null;
    private Integer totalSent = 0;
    private String patientName = null;
    private String batchName = null;
    private String referralId = null;
    private String status = null;
    private int statusId = 0;
    private int transactionId;
    private String patientId = null;
    private int batchId = 0;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    public int getorgId() {
        return orgId;
    }

    public void setorgId(int orgId) {
        this.orgId = orgId;
    }

    public String getorgName() {
        return orgName;
    }

    public void setorgName(String orgName) {
        this.orgName = orgName;
    }

    public int getmessageTypeId() {
        return messageTypeId;
    }

    public void setmessageTypeId(int messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public String getmessageType() {
        return messageType;
    }

    public void setmessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getmsg() {
        return msg;
    }

    public void setmsg(String msg) {
        this.msg = msg;
    }

    public int gettype() {
        return type;
    }

    public void settype(int type) {
        this.type = type;
    }

    public boolean getshowDetails() {
        return showDetails;
    }

    public void setshowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Integer getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(Integer totalSent) {
        this.totalSent = totalSent;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public String getpatientName() {
        return patientName;
    }

    public void setpatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

}
