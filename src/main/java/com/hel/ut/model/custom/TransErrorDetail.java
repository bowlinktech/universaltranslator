/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model.custom;

/**
 *
 * @author gchan
 */
public class TransErrorDetail {

    private Integer id = 0;

    private Integer configId = 0;

    private Integer transactionInId = 0;

    private String rptField1Value = " ";
    private String rptField2Value = " ";
    private String rptField3Value = " ";
    private String rptField4Value = " ";

    private String rptField1Label = " ";
    private String rptField2Label = " ";
    private String rptField3Label = " ";
    private String rptField4Label = " ";

    private Integer errorFieldNo;

    private Integer errorCode;
    private String errorDisplayText;

    private String errorInfo; //macro name, cw name, validation type etc

    private String errorFieldLabel;
    private String errorData; // from stack trace if error code

    private Integer validationTypeId;
    private Integer cwId;
    private Integer macroId;

    private Integer transactionStatus = 0;

    private String transactionStatusValue = "N/A";

    public Integer getValidationTypeId() {
        return validationTypeId;
    }

    public void setValidationTypeId(Integer validationTypeId) {
        this.validationTypeId = validationTypeId;
    }

    public Integer getCwId() {
        return cwId;
    }

    public void setCwId(Integer cwId) {
        this.cwId = cwId;
    }

    public Integer getMacroId() {
        return macroId;
    }

    public void setMacroId(Integer macroId) {
        this.macroId = macroId;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTransactionInId() {
        return transactionInId;
    }

    public void setTransactionInId(Integer transactionInId) {
        this.transactionInId = transactionInId;
    }

    public String getRptField1Value() {
        return rptField1Value;
    }

    public void setRptField1Value(String rptField1Value) {
        this.rptField1Value = rptField1Value;
    }

    public String getRptField2Value() {
        return rptField2Value;
    }

    public void setRptField2Value(String rptField2Value) {
        this.rptField2Value = rptField2Value;
    }

    public String getRptField3Value() {
        return rptField3Value;
    }

    public void setRptField3Value(String rptField3Value) {
        this.rptField3Value = rptField3Value;
    }

    public String getRptField4Value() {
        return rptField4Value;
    }

    public void setRptField4Value(String rptField4Value) {
        this.rptField4Value = rptField4Value;
    }

    public Integer getErrorFieldNo() {
        return errorFieldNo;
    }

    public void setErrorFieldNo(Integer errorFieldNo) {
        this.errorFieldNo = errorFieldNo;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDisplayText() {
        return errorDisplayText;
    }

    public void setErrorDisplayText(String errorDisplayText) {
        this.errorDisplayText = errorDisplayText;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getErrorFieldLabel() {
        return errorFieldLabel;
    }

    public void setErrorFieldLabel(String errorFieldLabel) {
        this.errorFieldLabel = errorFieldLabel;
    }

    public String getErrorData() {
        return errorData;
    }

    public void setErrorData(String errorData) {
        this.errorData = errorData;
    }

    public Integer getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(Integer transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionStatusValue() {
        return transactionStatusValue;
    }

    public void setTransactionStatusValue(String transactionStatusValue) {
        this.transactionStatusValue = transactionStatusValue;
    }

    public String getRptField1Label() {
        return rptField1Label;
    }

    public void setRptField1Label(String rptField1Label) {
        this.rptField1Label = rptField1Label;
    }

    public String getRptField2Label() {
        return rptField2Label;
    }

    public void setRptField2Label(String rptField2Label) {
        this.rptField2Label = rptField2Label;
    }

    public String getRptField3Label() {
        return rptField3Label;
    }

    public void setRptField3Label(String rptField3Label) {
        this.rptField3Label = rptField3Label;
    }

    public String getRptField4Label() {
        return rptField4Label;
    }

    public void setRptField4Label(String rptField4Label) {
        this.rptField4Label = rptField4Label;
    }

}
