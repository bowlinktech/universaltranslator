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

import com.hel.ut.validator.NoHtml;

/**
 *
 * @author gchan
 */
@Entity
@Table(name = "wsmessagesout")
public class wsMessagesOut {

    @Transient
    private String orgName = null;

    @Transient
    private String batchName = null;

    @Transient
    private String mimeType = "";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "orgId", nullable = false)
    private int orgId = 0;

    @NoHtml
    @Column(name = "fromEmail", nullable = false)
    private String fromEmail;

    @NoHtml
    @Column(name = "toEmail", nullable = false)
    private String toEmail;

    @NoHtml
    @Column(name = "endPoint", nullable = true)
    private String endPoint;

    @Column(name = "batchDownloadId", nullable = false)
    private int batchDownloadId = 0;

    @Column(name = "soapMessage", nullable = true)
    private String soapMessage;

    @Column(name = "soapResponse", nullable = true)
    private String soapResponse;

    @Column(name = "messageResult", nullable = false)
    private String messageResult = ""; //set to failed

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public int getBatchDownloadId() {
        return batchDownloadId;
    }

    public void setBatchDownloadId(int batchDownloadId) {
        this.batchDownloadId = batchDownloadId;
    }

    public String getSoapMessage() {
        return soapMessage;
    }

    public void setSoapMessage(String soapMessage) {
        this.soapMessage = soapMessage;
    }

    public String getSoapResponse() {
        return soapResponse;
    }

    public void setSoapResponse(String soapResponse) {
        this.soapResponse = soapResponse;
    }

    public String getMessageResult() {
        return messageResult;
    }

    public void setMessageResult(String messageResult) {
        this.messageResult = messageResult;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
