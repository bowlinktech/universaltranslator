package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "configurationconnectionfieldmappings")
public class configurationconnectionfieldmappings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "connectionId", nullable = false)
    private int connectionId = 0;

    @Column(name = "sourceConfigId", nullable = false)
    private int sourceConfigId;

    @Column(name = "targetConfigId", nullable = false)
    private int targetConfigId;

    @Column(name = "fieldNo", nullable = false)
    private int fieldNo;

    @NoHtml
    @Column(name = "fieldDesc", nullable = true)
    private String fieldDesc;


    @Column(name = "useField", nullable = false)
    private boolean useField = false;
    
    @Column(name="associatedFieldNo", nullable = false)
    private Integer associatedFieldNo = 0;
    
    @Column(name="populateErrorFieldNo", nullable = false)
    private Integer populateErrorFieldNo = 0;
    
    
    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getConnectionId() {
	return connectionId;
    }

    public void setConnectionId(int connectionId) {
	this.connectionId = connectionId;
    }

    public int getSourceConfigId() {
	return sourceConfigId;
    }

    public void setSourceConfigId(int sourceConfigId) {
	this.sourceConfigId = sourceConfigId;
    }

    public int getTargetConfigId() {
	return targetConfigId;
    }

    public void setTargetConfigId(int targetConfigId) {
	this.targetConfigId = targetConfigId;
    }

    public int getFieldNo() {
	return fieldNo;
    }

    public void setFieldNo(int fieldNo) {
	this.fieldNo = fieldNo;
    }

    public String getFieldDesc() {
	return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
	this.fieldDesc = fieldDesc;
    }

    public boolean isUseField() {
	return useField;
    }

    public void setUseField(boolean useField) {
	this.useField = useField;
    }

    public Integer getAssociatedFieldNo() {
	return associatedFieldNo;
    }

    public void setAssociatedFieldNo(Integer associatedFieldNo) {
	this.associatedFieldNo = associatedFieldNo;
    }

    public Integer getPopulateErrorFieldNo() {
	return populateErrorFieldNo;
    }

    public void setPopulateErrorFieldNo(Integer populateErrorFieldNo) {
	this.populateErrorFieldNo = populateErrorFieldNo;
    }

}
