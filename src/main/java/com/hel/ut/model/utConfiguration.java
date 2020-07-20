package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "CONFIGURATIONS")
public class utConfiguration {

    @Transient
    private String orgName = null, messageTypeName = null, transportMethod = null, fileDropLocation = null;

    @Transient
    private Integer transportDetailId = 0, scheduleType = 5;

    @Transient
    private List<configurationConnection> connections = null;
    
    @Transient
    private Date dateUpdated = null;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @NotNull(message = "The organization is a required field!")
    @Column(name = "orgId", nullable = false)
    private Integer orgId;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "STATUS", nullable = false)
    private boolean status = false;

    @Column(name = "TYPE", nullable = false)
    private Integer type = 1;

    @Column(name = "MESSAGETYPEID", nullable = false)
    private Integer messageTypeId = 1;

    @Column(name = "STEPSCOMPLETED", nullable = false)
    private Integer stepsCompleted = 0;

    @NoHtml
    @Column(name = "CONFIGNAME", nullable = false)
    private String configName;

    @Column(name = "THRESHOLD", nullable = false)
    private Integer threshold = 100;
    
    @Column(name = "configurationType", nullable = false)
    private Integer configurationType = 1;
    
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getorgId() {
        return orgId;
    }

    public void setorgId(Integer orgId) {
        this.orgId = orgId;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(Integer messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getMessageTypeName() {
        return messageTypeName;
    }

    public void setMessageTypeName(String messageTypeName) {
        this.messageTypeName = messageTypeName;
    }

    public Integer getstepsCompleted() {
        return stepsCompleted;
    }

    public void setstepsCompleted(Integer stepsCompleted) {
        this.stepsCompleted = stepsCompleted;
    }

    public String gettransportMethod() {
        return transportMethod;
    }

    public void settransportMethod(String transportMethod) {
        this.transportMethod = transportMethod;
    }

    public Integer gettransportDetailId() {
        return transportDetailId;
    }

    public void settransportDetailId(Integer transportDetailId) {
        this.transportDetailId = transportDetailId;
    }

    public List<configurationConnection> getconnections() {
        return connections;
    }

    public void setconnections(List<configurationConnection> connections) {
        this.connections = connections;
    }

    public void setconfigName(String configName) {
        this.configName = configName;
    }

    public String getconfigName() {
        return configName;
    }

    public Integer getScheduleType() {
	return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
	this.scheduleType = scheduleType;
    }

    public Integer getThreshold() {
	return threshold;
    }

    public void setThreshold(Integer threshold) {
	this.threshold = threshold;
    }

    public Integer getConfigurationType() {
	return configurationType;
    }

    public void setConfigurationType(Integer configurationType) {
	this.configurationType = configurationType;
    }

    public boolean isDeleted() {
	return deleted;
    }

    public void setDeleted(boolean deleted) {
	this.deleted = deleted;
    }

    public String getFileDropLocation() {
	return fileDropLocation;
    }

    public void setFileDropLocation(String fileDropLocation) {
	this.fileDropLocation = fileDropLocation;
    }

    public Date getDateUpdated() {
	return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
	this.dateUpdated = dateUpdated;
    }

    
}
