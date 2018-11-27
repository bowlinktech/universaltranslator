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
 * @author gchan
 */
@Entity
@Table(name = "dashboardwatchlist")
public class watchlist {
    
    @Transient
    private String orgName, configName, messageTypeName, transportMethod, expectedTimeAMPM = "AM";

    @Transient
    private int expectedTimeHour = 12, expectedTimeMinute = 0, messageTypeId = 0;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "orgId", nullable = false)
    private int orgId;

    @Column(name = "configId", nullable = false)
    private int configId;

    @Column(name = "expected", nullable = true)
    private String expected = "Daily";
    
    @Column(name = "expectFirstFile", nullable = true)
    private String expectFirstFile;
    
    @Column(name = "expectFirstFileTime", nullable = true)
    private String expectFirstFileTime;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();
    
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(name = "nextInsertDate", nullable = true)
    private Date nextInsertDate = new Date();
    
    @Column(name = "entryMessage", nullable = true)
    private String entryMessage;

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

    public String getMessageTypeName() {
	return messageTypeName;
    }

    public void setMessageTypeName(String messageTypeName) {
	this.messageTypeName = messageTypeName;
    }

    public String getTransportMethod() {
	return transportMethod;
    }

    public void setTransportMethod(String transportMethod) {
	this.transportMethod = transportMethod;
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

    public int getConfigId() {
	return configId;
    }

    public void setConfigId(int configId) {
	this.configId = configId;
    }

    public String getExpected() {
	return expected;
    }

    public void setExpected(String expected) {
	this.expected = expected;
    }

    public String getExpectFirstFile() {
	return expectFirstFile;
    }

    public void setExpectFirstFile(String expectFirstFile) {
	this.expectFirstFile = expectFirstFile;
    }

    public Date getDateCreated() {
	return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }

    public String getExpectFirstFileTime() {
	return expectFirstFileTime;
    }

    public void setExpectFirstFileTime(String expectFirstFileTime) {
	this.expectFirstFileTime = expectFirstFileTime;
    }

    public int getExpectedTimeHour() {
	return expectedTimeHour;
    }

    public void setExpectedTimeHour(int expectedTimeHour) {
	this.expectedTimeHour = expectedTimeHour;
    }

    public int getExpectedTimeMinute() {
	return expectedTimeMinute;
    }

    public void setExpectedTimeMinute(int expectedTimeMinute) {
	this.expectedTimeMinute = expectedTimeMinute;
    }

    public String getExpectedTimeAMPM() {
	return expectedTimeAMPM;
    }

    public void setExpectedTimeAMPM(String expectedTimeAMPM) {
	this.expectedTimeAMPM = expectedTimeAMPM;
    }

    public Date getNextInsertDate() {
	return nextInsertDate;
    }

    public void setNextInsertDate(Date nextInsertDate) {
	this.nextInsertDate = nextInsertDate;
    }

    public int getMessageTypeId() {
	return messageTypeId;
    }

    public void setMessageTypeId(int messageTypeId) {
	this.messageTypeId = messageTypeId;
    }

    public String getEntryMessage() {
	return entryMessage;
    }

    public void setEntryMessage(String entryMessage) {
	this.entryMessage = entryMessage;
    }

    
}
