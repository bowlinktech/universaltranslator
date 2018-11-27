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
 * @author chadmccue
 */
@Entity
@Table(name = "REFERRALACTIVITYEXPORTS")
public class referralActivityExports {

    @Transient
    private String createdByName = "";

    @Transient
    private String statusName = "";

    @Transient
    private String encryptedId = null;

    @Transient
    private String encryptedSecret = null;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "SELDATERANGE", nullable = true)
    private String selDateRange = "";

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATESUBMITTED", nullable = false)
    private Date dateSubmitted = new Date();

    @Column(name = "CREATEDBY", nullable = false)
    private Integer createdBy = 0;

    @Column(name = "FILENAME", nullable = true)
    private String fileName = "";
    /**
     * 1 - Requested 2 - in process 3 - ready for viewing 4 - picked up 5 - deleted 6 - no records
     *
     */
    @Column(name = "statusId", nullable = false)
    private Integer statusId = 1;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "fromDate", nullable = true)
    private Date fromDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "toDate", nullable = true)
    private Date toDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "runStart", nullable = true)
    private Date runStart = new Date();

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "runEnd", nullable = true)
    private Date runEnd = new Date();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSelDateRange() {
        return selDateRange;
    }

    public void setSelDateRange(String selDateRange) {
        this.selDateRange = selDateRange;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getRunStart() {
        return runStart;
    }

    public void setRunStart(Date runStart) {
        this.runStart = runStart;
    }

    public Date getRunEnd() {
        return runEnd;
    }

    public void setRunEnd(Date runEnd) {
        this.runEnd = runEnd;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getEncryptedId() {
        return encryptedId;
    }

    public void setEncryptedId(String encryptedId) {
        this.encryptedId = encryptedId;
    }

    public String getEncryptedSecret() {
        return encryptedSecret;
    }

    public void setEncryptedSecret(String encryptedSecret) {
        this.encryptedSecret = encryptedSecret;
    }

}
