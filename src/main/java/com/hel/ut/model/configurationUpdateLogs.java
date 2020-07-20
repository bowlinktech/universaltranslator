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
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author chad
 */
@Entity
@Table(name = "configurationUpdateLogs")
public class configurationUpdateLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id;", nullable = false)
    private int id;

    @Column(name = "configId", nullable = false)
    private int configId;
    
    @Column(name = "userId", nullable = false)
    private int userId;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "dateCreated", nullable = true)
    private Date dateCreated = new Date();
    
    @Column(name = "updateMade", nullable = true)
    private String updateMade;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getConfigId() {
	return configId;
    }

    public void setConfigId(int configId) {
	this.configId = configId;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public Date getDateCreated() {
	return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }

    public String getUpdateMade() {
	return updateMade;
    }

    public void setUpdateMade(String updateMade) {
	this.updateMade = updateMade;
    }

}
