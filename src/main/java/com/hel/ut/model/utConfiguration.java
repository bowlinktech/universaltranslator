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
    private String orgName = null, messageTypeName = null, transportMethod = null;

    @Transient
    private Integer transportDetailId = 0, scheduleType = 5;

    @Transient
    private List<configurationConnection> connections = null;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @NotNull(message = "The organization is a required field!")
    @Column(name = "orgId", nullable = false)
    private int orgId;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "STATUS", nullable = false)
    private boolean status = false;

    @Column(name = "TYPE", nullable = false)
    private int type = 1;

    @NotNull(message = "The message type is a required field!")
    @Column(name = "MESSAGETYPEID", nullable = false)
    private int messageTypeId = 0;

    @Column(name = "STEPSCOMPLETED", nullable = false)
    private int stepsCompleted = 0;

    @NoHtml
    @Column(name = "CONFIGNAME", nullable = false)
    private String configName;

    @Column(name = "SOURCETYPE", nullable = false)
    private int sourceType = 1;

    @Column(name = "ASSOCIATEDMESSAGETYPEID", nullable = false)
    private int associatedMessageTypeId = 0;
    
    @Column(name = "THRESHOLD", nullable = false)
    private int threshold = 100;
    
    @Column(name = "configurationType", nullable = false)
    private int configurationType = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getorgId() {
        return orgId;
    }

    public void setorgId(int orgId) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(int messageTypeId) {
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

    public int getstepsCompleted() {
        return stepsCompleted;
    }

    public void setstepsCompleted(int stepsCompleted) {
        this.stepsCompleted = stepsCompleted;
    }

    public String gettransportMethod() {
        return transportMethod;
    }

    public void settransportMethod(String transportMethod) {
        this.transportMethod = transportMethod;
    }

    public int gettransportDetailId() {
        return transportDetailId;
    }

    public void settransportDetailId(int transportDetailId) {
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

    public void setsourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public int getsourceType() {
        return sourceType;
    }

    public void setassociatedMessageTypeId(int associatedMessageTypeId) {
        this.associatedMessageTypeId = associatedMessageTypeId;
    }

    public int getassociatedMessageTypeId() {
        return associatedMessageTypeId;
    }

    public Integer getScheduleType() {
	return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
	this.scheduleType = scheduleType;
    }

    public int getThreshold() {
	return threshold;
    }

    public void setThreshold(int threshold) {
	this.threshold = threshold;
    }

    public int getConfigurationType() {
	return configurationType;
    }

    public void setConfigurationType(int configurationType) {
	this.configurationType = configurationType;
    }

}
