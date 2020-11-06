/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "batchuploaddroppedvalues")
public class batchUploadDroppedValues {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;
    
    @Column(name = "transactionInRecordsId", nullable = false)
    private int transactionInRecordsId = 0;
    
    @Column(name = "fieldNo", nullable = false)
    private int fieldNo = 0;
    
    @Column(name = "batchUploadId", nullable = false)
    private int batchUploadId = 0;
    
    @Column(name = "configId", nullable = false)
    private int configId = 0;
    
    @Column(name = "fieldName", nullable = false)
    private String fieldName;
    
    @Column(name = "fieldValue", nullable = false)
    private String fieldValue;
    
    @Column(name = "reportField1Data", nullable = false)
    private String reportField1Data;
    
    @Column(name = "reportField2Data", nullable = false)
    private String reportField2Data;
    
    @Column(name = "reportField3Data", nullable = false)
    private String reportField3Data;
    
    @Column(name = "reportField4Data", nullable = false)
    private String reportField4Data;
    
    @Column(name = "translatedReportField1Data", nullable = false)
    private String translatedReportField1Data;
    
    @Column(name = "fromOutboundConfig", nullable = false)
    private boolean fromOutboundConfig = false;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getTransactionInRecordsId() {
	return transactionInRecordsId;
    }

    public void setTransactionInRecordsId(int transactionInRecordsId) {
	this.transactionInRecordsId = transactionInRecordsId;
    }

    public int getFieldNo() {
	return fieldNo;
    }

    public void setFieldNo(int fieldNo) {
	this.fieldNo = fieldNo;
    }

    public int getBatchUploadId() {
	return batchUploadId;
    }

    public void setBatchUploadId(int batchUploadId) {
	this.batchUploadId = batchUploadId;
    }

    public int getConfigId() {
	return configId;
    }

    public void setConfigId(int configId) {
	this.configId = configId;
    }

    public String getFieldName() {
	return fieldName;
    }

    public void setFieldName(String fieldName) {
	this.fieldName = fieldName;
    }

    public String getFieldValue() {
	return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
	this.fieldValue = fieldValue;
    }

    public String getReportField1Data() {
	return reportField1Data;
    }

    public void setReportField1Data(String reportField1Data) {
	this.reportField1Data = reportField1Data;
    }

    public String getReportField2Data() {
	return reportField2Data;
    }

    public void setReportField2Data(String reportField2Data) {
	this.reportField2Data = reportField2Data;
    }

    public String getReportField3Data() {
	return reportField3Data;
    }

    public void setReportField3Data(String reportField3Data) {
	this.reportField3Data = reportField3Data;
    }

    public String getReportField4Data() {
	return reportField4Data;
    }

    public void setReportField4Data(String reportField4Data) {
	this.reportField4Data = reportField4Data;
    }

    public String getTranslatedReportField1Data() {
	return translatedReportField1Data;
    }

    public void setTranslatedReportField1Data(String translatedReportField1Data) {
	this.translatedReportField1Data = translatedReportField1Data;
    }

    public boolean isFromOutboundConfig() {
	    return fromOutboundConfig;
    }

    public void setFromOutboundConfig(boolean fromOutboundConfig) {
	    this.fromOutboundConfig = fromOutboundConfig;
    }
}
