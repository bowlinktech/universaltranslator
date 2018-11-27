/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model.custom;

import java.util.List;

/**
 *
 * @author gchan
 */
public class ConfigErrorInfo {

    private Integer batchId = 0;
    private Integer configId = 0;
    private String configName;
    private String messageTypeName;
    private Integer errorViewId = 0;

    /**
     * labels stays the same for each config *
     */
    private List<TransErrorDetail> transErrorDetails;

    private String rptFieldHeading1 = " ";
    private String rptFieldHeading2 = " ";
    private String rptFieldHeading3 = " ";
    private String rptFieldHeading4 = " ";

    private Integer rptField1;
    private Integer rptField2;
    private Integer rptField3;
    private Integer rptField4;

    public Integer getRptField1() {
        return rptField1;
    }

    public void setRptField1(Integer rptField1) {
        this.rptField1 = rptField1;
    }

    public Integer getRptField2() {
        return rptField2;
    }

    public void setRptField2(Integer rptField2) {
        this.rptField2 = rptField2;
    }

    public Integer getRptField3() {
        return rptField3;
    }

    public void setRptField3(Integer rptField3) {
        this.rptField3 = rptField3;
    }

    public Integer getRptField4() {
        return rptField4;
    }

    public void setRptField4(Integer rptField4) {
        this.rptField4 = rptField4;
    }

    public String getRptFieldHeading1() {
        return rptFieldHeading1;
    }

    public void setRptFieldHeading1(String rptFieldHeading1) {
        this.rptFieldHeading1 = rptFieldHeading1;
    }

    public String getRptFieldHeading2() {
        return rptFieldHeading2;
    }

    public void setRptFieldHeading2(String rptFieldHeading2) {
        this.rptFieldHeading2 = rptFieldHeading2;
    }

    public String getRptFieldHeading3() {
        return rptFieldHeading3;
    }

    public void setRptFieldHeading3(String rptFieldHeading3) {
        this.rptFieldHeading3 = rptFieldHeading3;
    }

    public String getRptFieldHeading4() {
        return rptFieldHeading4;
    }

    public void setRptFieldHeading4(String rptFieldHeading4) {
        this.rptFieldHeading4 = rptFieldHeading4;
    }

    public Integer getErrorViewId() {
        return errorViewId;
    }

    public void setErrorViewId(Integer errorViewId) {
        this.errorViewId = errorViewId;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getMessageTypeName() {
        return messageTypeName;
    }

    public void setMessageTypeName(String messageTypeName) {
        this.messageTypeName = messageTypeName;
    }

    public List<TransErrorDetail> getTransErrorDetails() {
        return transErrorDetails;
    }

    public void setTransErrorDetails(List<TransErrorDetail> transErrorDetails) {
        this.transErrorDetails = transErrorDetails;
    }

}
