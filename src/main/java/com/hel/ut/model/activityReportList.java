/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.math.BigInteger;

/**
 *
 * @author chadmccue
 */
public class activityReportList {

    String orgName, messageType, tgtOrgName;

    BigInteger openTotal, closedTotal, total, totalReceived;

    Integer configId, messageTypeId, targetOrgId;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String gettgtOrgName() {
        return tgtOrgName;
    }

    public void settgtOrgName(String tgtOrgName) {
        this.tgtOrgName = tgtOrgName;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public BigInteger getTotal() {
        return total;
    }

    public void setTotal(BigInteger total) {
        this.total = total;
    }

    public BigInteger getOpenTotal() {
        return openTotal;
    }

    public void setOpenTotal(BigInteger openTotal) {
        this.openTotal = openTotal;
    }

    public BigInteger getClosedTotal() {
        return closedTotal;
    }

    public void setClosedTotal(BigInteger closedTotal) {
        this.closedTotal = closedTotal;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public Integer getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(Integer messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public BigInteger getTotalReceived() {
	return totalReceived;
    }

    public void setTotalReceived(BigInteger totalReceived) {
	this.totalReceived = totalReceived;
    }

    public Integer getTargetOrgId() {
	return targetOrgId;
    }

    public void setTargetOrgId(Integer targetOrgId) {
	this.targetOrgId = targetOrgId;
    }

    
}
