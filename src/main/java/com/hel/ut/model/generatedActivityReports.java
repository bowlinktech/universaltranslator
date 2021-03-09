package com.hel.ut.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "generatedactivityreports")
public class generatedActivityReports {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;
    
    @Column(name = "userId", nullable = true)
    private int userId = 0;
    
    @Column(name = "reportType", nullable = true)
    private int reportType = 1;
    
    @Column(name = "registryType", nullable = true)
    private int registryType = 1;

    @Column(name = "dateRange", nullable = false)
    private String dateRange;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();
    
    @Column(name = "status", nullable = true)
    private int status = 1;
    
    @Column(name = "fileName", nullable = false)
    private String fileName;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public int getRegistryType() {
	return registryType;
    }

    public void setRegistryType(int registryType) {
	this.registryType = registryType;
    }

    public String getDateRange() {
	return dateRange;
    }

    public void setDateRange(String dateRange) {
	this.dateRange = dateRange;
    }

    public Date getDateCreated() {
	return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }
    
}
