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
@Table(name = "organizationdirectdetails")
public class organizationDirectDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;
    
    @Column(name = "orgId", nullable = false)
    private int orgId = 0;
    
    @Column(name = "status", nullable = false)
    private boolean status = true;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "dateCreated", nullable = false)
    private Date dateCreated = new Date();
    
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "dateModified", nullable = false)
    private Date dateModified = new Date();
    
    @Column(name = "hispId", nullable = false)
    private int hispId = 0;
    
    @Column(name = "directDomain", nullable = false)
    private String directDomain;
    
    @Column(name = "fileTypeId", nullable = false)
    private int fileTypeId = 0;
    
    @Column(name = "expectedFileExt", nullable = false)
    private String expectedFileExt;

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

    public boolean isStatus() {
	return status;
    }

    public void setStatus(boolean status) {
	this.status = status;
    }

    public Date getDateCreated() {
	return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
	return dateModified;
    }

    public void setDateModified(Date dateModified) {
	this.dateModified = dateModified;
    }

    public int getHispId() {
	return hispId;
    }

    public void setHispId(int hispId) {
	this.hispId = hispId;
    }

    public String getDirectDomain() {
	return directDomain;
    }

    public void setDirectDomain(String directDomain) {
	this.directDomain = directDomain;
    }

    public int getFileTypeId() {
	return fileTypeId;
    }

    public void setFileTypeId(int fileTypeId) {
	this.fileTypeId = fileTypeId;
    }

    public String getExpectedFileExt() {
	return expectedFileExt;
    }

    public void setExpectedFileExt(String expectedFileExt) {
	this.expectedFileExt = expectedFileExt;
    }
    
    

}
