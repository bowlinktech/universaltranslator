package com.hel.ut.model.custom;

import java.util.List;

/**
 * This starts with a batch of transactions that are ready for insert We start with a batchId We sort them into configId We query the fieldNo if we need to split, fieldNo list for select, saveToTableCol, saveToTableName We will add a field to denote if the values are in a pair
 *
 * It is faster to bulk insert than to parse each row
 *
 *
 */
public class ConfigForInsert {

    int batchUploadId;
    int configId;
    String saveToTableName;
    String saveToTableCol;
    String singleValueFields;
    /**
     * when we concatenated the string and have to loop through each transaction to insert*
     */
    String splitFields;
    String checkForDelim;
    List<Integer> loopTransIds;
    List<Integer> blankValueTransId;

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

    public String getSaveToTableName() {
        return saveToTableName;
    }

    public void setSaveToTableName(String saveToTableName) {
        this.saveToTableName = saveToTableName;
    }

    public String getSaveToTableCol() {
        return saveToTableCol;
    }

    public void setSaveToTableCol(String saveToTableCol) {
        this.saveToTableCol = saveToTableCol;
    }

    public String getSingleValueFields() {
        return singleValueFields;
    }

    public void setSingleValueFields(String singleValueFields) {
        this.singleValueFields = singleValueFields;
    }

    public String getSplitFields() {
        return splitFields;
    }

    public void setSplitFields(String splitFields) {
        this.splitFields = splitFields;
    }

    public String getCheckForDelim() {
        return checkForDelim;
    }

    public void setCheckForDelim(String checkForDelim) {
        this.checkForDelim = checkForDelim;
    }

    public List<Integer> getLoopTransIds() {
        return loopTransIds;
    }

    public void setLoopTransIds(List<Integer> loopTransIds) {
        this.loopTransIds = loopTransIds;
    }

    public List<Integer> getBlankValueTransId() {
        return blankValueTransId;
    }

    public void setBlankValueTransId(List<Integer> blankValueTransId) {
        this.blankValueTransId = blankValueTransId;
    }

}
