package com.hel.ut.model;


public class appenedNewconfigurationFormFields {

    private int fieldNo;
    private int configId;
    private int transportDetailId;
    private String fieldDesc, sampleData;
    private int validationType = 1;
    private boolean required = false;
    private boolean useField = false;

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

    public boolean isRequired() {
	return required;
    }

    public void setRequired(boolean required) {
	this.required = required;
    }

    public boolean isUseField() {
	return useField;
    }

    public void setUseField(boolean useField) {
	this.useField = useField;
    }

    public int getConfigId() {
	return configId;
    }

    public void setConfigId(int configId) {
	this.configId = configId;
    }

    public int getTransportDetailId() {
	return transportDetailId;
    }

    public void setTransportDetailId(int transportDetailId) {
	this.transportDetailId = transportDetailId;
    }
    
    public void setSampleData(String sampleData) {
	this.sampleData = sampleData;
    }

    public String getSampleData() {
	return sampleData;
    }
    
}
