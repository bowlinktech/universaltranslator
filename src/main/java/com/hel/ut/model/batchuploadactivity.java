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
 * @author cmccue
 */
@Entity
@Table(name = "batchuploadactivity")
public class batchuploadactivity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "batchUploadId", nullable = true)
    private Integer batchUploadId = 0;

    @Column(name = "activity", nullable = true)
    private String activity;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public Integer getBatchUploadId() {
	return batchUploadId;
    }

    public void setBatchUploadId(Integer batchUploadId) {
	this.batchUploadId = batchUploadId;
    }

    public String getActivity() {
	return activity;
    }

    public void setActivity(String activity) {
	this.activity = activity;
    }

    public Date getDateCreated() {
	return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }
    
    

}
