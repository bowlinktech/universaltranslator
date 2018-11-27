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
@Table(name = "wsMessagesIn")
public class WSMessagesIn {

    @Transient
    private String orgName = null;

    @Transient
    private String statusName = null;

    @Transient
    private String errorDisplayText = null;

    @Transient
    private String batchName = null;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "orgId", nullable = true)
    private int orgId = 0;

    @Column(name = "fromAddress", nullable = true)
    private String fromAddress;

    @Column(name = "payload", nullable = true)
    private String payload;

    /**
     * 1 - not processed 2 - processed 3 - rejected
     *
     */
    @Column(name = "statusId", nullable = false)
    private int statusId = 3; //set to reject

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    @Column(name = "errorId", nullable = true)
    private Integer errorId = 0;

    @Column(name = "domain", nullable = true)
    private String domain;

    @Column(name = "batchUploadId", nullable = true)
    private int batchUploadId = 0;

    @Column(name = "foundPosition", nullable = true)
    private int foundPosition = 0;

    @Column(name = "positionMatched", nullable = true)
    private boolean positionMatched = false;

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

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getBatchUploadId() {
        return batchUploadId;
    }

    public void setBatchUploadId(int batchUploadId) {
        this.batchUploadId = batchUploadId;
    }

    public int getFoundPosition() {
        return foundPosition;
    }

    public void setFoundPosition(int foundPosition) {
        this.foundPosition = foundPosition;
    }

    public boolean isPositionMatched() {
        return positionMatched;
    }

    public void setPositionMatched(boolean positionMatched) {
        this.positionMatched = positionMatched;
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

}
