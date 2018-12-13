package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.hel.ut.validator.Phone;
import javax.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Entity
@Table(name = "ORGANIZATIONS")
public class Organization {

    @Transient
    private CommonsMultipartFile file = null;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @NotEmpty
    @NoHtml
    @Column(name = "ORGNAME", nullable = false)
    private String orgName;

    @NoHtml
    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @NoHtml
    @Column(name = "ADDRESS2", nullable = true)
    private String address2;

    @NoHtml
    @Column(name = "CITY", nullable = false)
    private String city;

    @NoHtml
    @Column(name = "STATE", nullable = false)
    private String state;

    @NoHtml
    @Column(name = "POSTALCODE", nullable = false)
    private String postalCode;

    @Phone
    @Column(name = "PHONE", nullable = false)
    private String phone;

    @NoHtml
    @Column(name = "FAX", nullable = true)
    private String fax;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "STATUS", nullable = false)
    private boolean status = true;

    @NoHtml
    @Column(name = "CLEANURL", nullable = false)
    private String cleanURL;

    @Column(name = "parsingTemplate", nullable = true)
    private String parsingTemplate = null;

    @Column(name = "ORGTYPE", nullable = false)
    private Integer orgType = 1;

    @Column(name = "COUNTY", nullable = true)
    private String county = "";

    @Column(name = "TOWN", nullable = true)
    private String town = "";

    @Column(name = "ORGDESC", nullable = true)
    private String orgDesc = "";

    @Column(name = "INFOURL", nullable = true)
    private String infoURL = "";
    
    @Column(name = "COUNTRY", nullable = true)
    private String country = "United States of America";
    
    @Column(name = "helRegistryId", nullable = true)
    private int helRegistryId = 0;
    
    @Column(name = "helRegistryOrgId", nullable = true)
    private int helRegistryOrgId = 0;
    
    @Column(name = "helRegistrySchemaName", nullable = true)
    private String helRegistrySchemaName = "";
    
    @Column(name = "primaryContactEmail", nullable = true)
    private String primaryContactEmail = "";
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getparsingTemplate() {
        return parsingTemplate;
    }

    public void setparsingTemplate(String parsingTemplate) {
        this.parsingTemplate = parsingTemplate;
    }

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }

    public String getParsingTemplate() {
        return parsingTemplate;
    }

    public void setParsingTemplate(String parsingTemplate) {
        this.parsingTemplate = parsingTemplate;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    public String getcleanURL() {
        return cleanURL;
    }

    public void setcleanURL(String cleanURL) {
        this.cleanURL = cleanURL;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getOrgDesc() {
        return orgDesc;
    }

    public void setOrgDesc(String orgDesc) {
        this.orgDesc = orgDesc;
    }

    public String getInfoURL() {
        return infoURL;
    }

    public void setInfoURL(String infoURL) {
        this.infoURL = infoURL;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCleanURL() {
	return cleanURL;
    }

    public void setCleanURL(String cleanURL) {
	this.cleanURL = cleanURL;
    }

    public int getHelRegistryId() {
	return helRegistryId;
    }

    public void setHelRegistryId(int helRegistryId) {
	this.helRegistryId = helRegistryId;
    }

    public int getHelRegistryOrgId() {
	return helRegistryOrgId;
    }

    public void setHelRegistryOrgId(int helRegistryOrgId) {
	this.helRegistryOrgId = helRegistryOrgId;
    }

    public String getPrimaryContactEmail() {
	return primaryContactEmail;
    }

    public void setPrimaryContactEmail(String primaryContactEmail) {
	this.primaryContactEmail = primaryContactEmail;
    }

    public String getHelRegistrySchemaName() {
	return helRegistrySchemaName;
    }

    public void setHelRegistrySchemaName(String helRegistrySchemaName) {
	this.helRegistrySchemaName = helRegistrySchemaName;
    }

    
}
