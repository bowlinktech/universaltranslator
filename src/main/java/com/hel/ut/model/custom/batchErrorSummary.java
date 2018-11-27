/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model.custom;

/**
 *
 * @author chadmccue
 */
public class batchErrorSummary {
    
    private Integer totalErrors = 0, errorId = 0;
    private String errorDisplayText = "";

    public Integer getTotalErrors() {
	return totalErrors;
    }

    public void setTotalErrors(Integer totalErrors) {
	this.totalErrors = totalErrors;
    }

    public String getErrorDisplayText() {
	return errorDisplayText;
    }

    public void setErrorDisplayText(String errorDisplayText) {
	this.errorDisplayText = errorDisplayText;
    }

    public Integer getErrorId() {
	return errorId;
    }

    public void setErrorId(Integer errorId) {
	this.errorId = errorId;
    }
    
}
