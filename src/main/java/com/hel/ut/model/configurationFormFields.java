package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "CONFIGURATIONFORMFIELDS")
public class configurationFormFields {

    @Transient
    private String fieldValue = null, associatedFieldDetails;
    
    @Transient
    private Integer copiedId = 0, mappedToField = 0, mappedErrorField = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "associatedFieldId", nullable = false)
    private int associatedFieldId = 0;

    @Column(name = "CONFIGID", nullable = false)
    private int configId;

    @Column(name = "TRANSPORTDETAILID", nullable = false)
    private int transportDetailId;

    @Column(name = "FIELDNO", nullable = false)
    private int fieldNo;

    @NoHtml
    @Column(name = "FIELDDESC", nullable = true)
    private String fieldDesc;

    @Column(name = "VALIDATIONTYPE", nullable = true)
    private int validationType = 1;

    @Column(name = "REQUIRED", nullable = false)
    private boolean required = false;

    @Column(name = "USEFIELD", nullable = false)
    private boolean useField = false;
    
    @Column(name="associatedFieldNo", nullable = false)
    private Integer associatedFieldNo = 0;

    @Column(name = "defaultValue", nullable = true)
    private String defaultValue;
    
    @Column(name = "sampleData", nullable = true)
    private String sampleData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssociatedFieldId() {
	return associatedFieldId;
    }

    public void setAssociatedFieldId(int associatedFieldId) {
	this.associatedFieldId = associatedFieldId;
    }

    public int getconfigId() {
        return configId;
    }

    public void setconfigId(int configId) {
        this.configId = configId;
    }

    public int gettransportDetailId() {
        return transportDetailId;
    }

    public void settransportDetailId(int transportDetailId) {
        this.transportDetailId = transportDetailId;
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

    public int getValidationType() {
        return validationType;
    }

    public void setValidationType(int validationType) {
        this.validationType = validationType;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean getUseField() {
        return useField;
    }

    public void setUseField(boolean useField) {
        this.useField = useField;
    }

    public String getfieldValue() {
        return fieldValue;
    }

    public void setfieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public int getCopiedId() {
	return copiedId;
    }

    public void setCopiedId(int copiedId) {
	this.copiedId = copiedId;
    }

    public int getMappedToField() {
	return mappedToField;
    }

    public void setMappedToField(int mappedToField) {
	this.mappedToField = mappedToField;
    }

    public Integer getAssociatedFieldNo() {
	return associatedFieldNo;
    }

    public void setAssociatedFieldNo(Integer associatedFieldNo) {
	this.associatedFieldNo = associatedFieldNo;
    }

    public String getAssociatedFieldDetails() {
	return associatedFieldDetails;
    }

    public void setAssociatedFieldDetails(String associatedFieldDetails) {
	this.associatedFieldDetails = associatedFieldDetails;
    }

    public Integer getMappedErrorField() {
	return mappedErrorField;
    }

    public void setMappedErrorField(Integer mappedErrorField) {
	this.mappedErrorField = mappedErrorField;
    }

    public String getDefaultValue() {
	return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
	this.defaultValue = defaultValue;
    }

    public String getSampleData() {
	return sampleData;
    }

    public void setSampleData(String sampleData) {
	this.sampleData = sampleData;
    }

    
}
