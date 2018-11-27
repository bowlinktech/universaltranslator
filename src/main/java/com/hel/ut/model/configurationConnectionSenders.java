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

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONCONNECTIONSENDERS")
public class configurationConnectionSenders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONNECTIONID", nullable = false)
    private int connectionId;

    @Column(name = "USERID", nullable = false)
    private int userId;

    @Column(name = "SENDEMAILALERT", nullable = false)
    private Boolean sendEmailAlert = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getconnectionId() {
        return connectionId;
    }

    public void setconnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public int getuserId() {
        return userId;
    }

    public void setuserId(int userId) {
        this.userId = userId;
    }

    public Boolean getSendEmailAlert() {
        return sendEmailAlert;
    }

    public void setSendEmailAlert(Boolean sendEmailAlert) {
        this.sendEmailAlert = sendEmailAlert;
    }

}
