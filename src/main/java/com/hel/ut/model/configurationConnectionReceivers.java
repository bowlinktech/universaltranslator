/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONCONNECTIONRECEIVERS")
public class configurationConnectionReceivers {
    
    @Transient
    private String contactType;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONNECTIONID", nullable = false)
    private int connectionId;

    @Column(name = "emailAddress", nullable = false)
    private String emailAddress;

    @Column(name = "sendEmailNotifications", nullable = false)
    private Boolean sendEmailNotifications = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConnectionId() {
	return connectionId;
    }

    public void setConnectionId(int connectionId) {
	this.connectionId = connectionId;
    }

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public Boolean getSendEmailNotifications() {
	return sendEmailNotifications;
    }

    public void setSendEmailNotifications(Boolean sendEmailNotifications) {
	this.sendEmailNotifications = sendEmailNotifications;
    }

    public String getContactType() {
	return contactType;
    }

    public void setContactType(String contactType) {
	this.contactType = contactType;
    }

    
}
