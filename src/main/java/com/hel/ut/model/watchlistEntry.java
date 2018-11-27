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
@Table(name = "dashboardwatchlistentries")
public class watchlistEntry {
    
    @Transient
    private int transportMethodId = 0;
    
    @Transient
    private String entryMessage = "";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;
    
    @Column(name = "watchlistentryId", nullable = false)
    private int watchlistentryId;

    @Column(name = "orgId", nullable = false)
    private int orgId;

    @Column(name = "configId", nullable = false)
    private int configId;

    @Column(name = "messageTypeId", nullable = true)
    private int messageTypeId;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();
    
    @Column(name = "watchListCompleted", nullable = true)
    private boolean watchListCompleted  = false;

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

    public int getMessageTypeId() {
	return messageTypeId;
    }

    public void setMessageTypeId(int messageTypeId) {
	this.messageTypeId = messageTypeId;
    }

    public int getWatchlistentryId() {
	return watchlistentryId;
    }

    public void setWatchlistentryId(int watchlistentryId) {
	this.watchlistentryId = watchlistentryId;
    }

    public Date getDateCreated() {
	return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }

    public int getTransportMethodId() {
	return transportMethodId;
    }

    public void setTransportMethodId(int transportMethodId) {
	this.transportMethodId = transportMethodId;
    }

    public String getEntryMessage() {
	return entryMessage;
    }

    public void setEntryMessage(String entryMessage) {
	this.entryMessage = entryMessage;
    }

    public boolean isWatchListCompleted() {
	return watchListCompleted;
    }

    public void setWatchListCompleted(boolean watchListCompleted) {
	this.watchListCompleted = watchListCompleted;
    }
    
    
}
