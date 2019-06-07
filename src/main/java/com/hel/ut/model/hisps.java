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
@Table(name = "hisps")
public class hisps {

   
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "dateCreated", nullable = false)
    private Date dateCreated = new Date();
    
    @Column(name = "status", nullable = false)
    private boolean status = true;

    @Column(name = "hispName", nullable = false)
    private String hispName;
    
    @Column(name = "utAPIUsername", nullable = true)
    private String utAPIUsername;
    
    @Column(name = "utAPIPassword", nullable = true)
    private String utAPIPassword;
    
    @Column(name = "hispAPIUsername", nullable = true)
    private String hispAPIUsername;
    
    @Column(name = "hispAPIPassword", nullable = true)
    private String hispAPIPassword;
    
    @Column(name = "hispAPIURL", nullable = true)
    private String hispAPIURL;
    
    @Column(name = "primaryContact", nullable = true)
    private String primaryContact;
    
    @Column(name = "primaryContactEmail", nullable = true)
    private String primaryContactEmail;
    
    @Column(name = "primaryContactPhone", nullable = true)
    private String primaryContactPhone;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public Date getDateCreated() {
	return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }

    public boolean isStatus() {
	return status;
    }

    public void setStatus(boolean status) {
	this.status = status;
    }

    public String getHispName() {
	return hispName;
    }

    public void setHispName(String hispName) {
	this.hispName = hispName;
    }

    public String getUtAPIUsername() {
	return utAPIUsername;
    }

    public void setUtAPIUsername(String utAPIUsername) {
	this.utAPIUsername = utAPIUsername;
    }

    public String getUtAPIPassword() {
	return utAPIPassword;
    }

    public void setUtAPIPassword(String utAPIPassword) {
	this.utAPIPassword = utAPIPassword;
    }

    public String getHispAPIUsername() {
	return hispAPIUsername;
    }

    public void setHispAPIUsername(String hispAPIUsername) {
	this.hispAPIUsername = hispAPIUsername;
    }

    public String getHispAPIPassword() {
	return hispAPIPassword;
    }

    public void setHispAPIPassword(String hispAPIPassword) {
	this.hispAPIPassword = hispAPIPassword;
    }

    public String getHispAPIURL() {
	return hispAPIURL;
    }

    public void setHispAPIURL(String hispAPIURL) {
	this.hispAPIURL = hispAPIURL;
    }

    public String getPrimaryContact() {
	return primaryContact;
    }

    public void setPrimaryContact(String primaryContact) {
	this.primaryContact = primaryContact;
    }

    public String getPrimaryContactEmail() {
	return primaryContactEmail;
    }

    public void setPrimaryContactEmail(String primaryContactEmail) {
	this.primaryContactEmail = primaryContactEmail;
    }

    public String getPrimaryContactPhone() {
	return primaryContactPhone;
    }

    public void setPrimaryContactPhone(String primaryContactPhone) {
	this.primaryContactPhone = primaryContactPhone;
    }
    
    

}
