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
public class TransErrorDetailDisplay {

    private Integer transactionInId = 0;
    private Integer transactionStatus = 0;
    private String rptField1Label = " ";
    private String rptField2Label = " ";
    private String rptField3Label = " ";
    private String rptField4Label = " ";

    private String transactionStatusValue = "N/A";

    private List<TransErrorDetail> tedList;

    public Integer getTransactionInId() {
        return transactionInId;
    }

    public void setTransactionInId(Integer transactionInId) {
        this.transactionInId = transactionInId;
    }

    public List<TransErrorDetail> getTedList() {
        return tedList;
    }

    public void setTedList(List<TransErrorDetail> tedList) {
        this.tedList = tedList;
    }

    public Integer getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(Integer transactionStatus) {
        this.transactionStatus = transactionStatus;
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

    public String getTransactionStatusValue() {
        return transactionStatusValue;
    }

    public void setTransactionStatusValue(String transactionStatusValue) {
        this.transactionStatusValue = transactionStatusValue;
    }

}
