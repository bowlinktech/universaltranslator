package com.hel.ut.model.custom;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * This is a logo for the table, goal is to make replacing a logo as portable as we can.
 *
 */
@Entity
@Table(name = "logoInfo")
public class LogoInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "frontEndLogoName", nullable = true)
    private String frontEndLogoName;

    @Column(name = "backEndLogoName", nullable = true)
    private String backEndLogoName;

    @Column(name = "DATECreated", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "DATEModified", nullable = true)
    private Date dateModified = new Date();

    @Transient
    private String backEndLocalPath;

    @Transient
    private String frontEndLocalPath;

    @Transient
    private CommonsMultipartFile frontEndFile;

    @Transient
    private CommonsMultipartFile backEndFile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrontEndLogoName() {
        return frontEndLogoName;
    }

    public void setFrontEndLogoName(String frontEndLogoName) {
        this.frontEndLogoName = frontEndLogoName;
    }

    public String getBackEndLogoName() {
        return backEndLogoName;
    }

    public void setBackEndLogoName(String backEndLogoName) {
        this.backEndLogoName = backEndLogoName;
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

    public String getBackEndLocalPath() {
        return backEndLocalPath;
    }

    public void setBackEndLocalPath(String backEndLocalPath) {
        this.backEndLocalPath = backEndLocalPath;
    }

    public String getFrontEndLocalPath() {
        return frontEndLocalPath;
    }

    public void setFrontEndLocalPath(String frontEndLocalPath) {
        this.frontEndLocalPath = frontEndLocalPath;
    }

    public CommonsMultipartFile getFrontEndFile() {
        return frontEndFile;
    }

    public void setFrontEndFile(CommonsMultipartFile frontEndFile) {
        this.frontEndFile = frontEndFile;
    }

    public CommonsMultipartFile getBackEndFile() {
        return backEndFile;
    }

    public void setBackEndFile(CommonsMultipartFile backEndFile) {
        this.backEndFile = backEndFile;
    }

}
