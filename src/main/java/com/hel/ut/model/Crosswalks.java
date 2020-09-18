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

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Entity
@Table(name = "CROSSWALKS")
public class Crosswalks {

    @Transient
    private CommonsMultipartFile file;
    
    @Transient
    private int dtsId = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @NotEmpty
    @NoHtml
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "FILEDELIMITER", nullable = false)
    private int fileDelimiter = 0;

    @NoHtml
    @Column(name = "FILENAME", nullable = false)
    private String fileName;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "ORGID", nullable = true)
    private int orgId = 0;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "lastUpdated", nullable = true)
    private Date lastUpdated = null;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFileDelimiter() {
        return fileDelimiter;
    }

    public void setFileDelimiter(int fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public String getfileName() {
        return fileName;
    }

    public void setfileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }

    public int getDtsId() {
	return dtsId;
    }

    public void setDtsId(int dtsId) {
	this.dtsId = dtsId;
    }

    public Date getLastUpdated() {
	return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
	this.lastUpdated = lastUpdated;
    }

    
}
