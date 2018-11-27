package com.hel.ut.model.custom;

/**
 * This is what the SP Sample UPDATE transactionTranslatedOut tto JOIN (select tt.id as transactiontargetid, message_patients.transactioninid, group_concat(COALESCE(sourcePatientId, 'NULL') order by message_patients.id separator '^^^^^' ) as sourcePatientId, group_concat(COALESCE(dob, 'NULL') order by message_patients.id separator '^^^^^' ) as dob, group_concat(COALESCE(genderId, 'NULL') order by message_patients.id separator '^^^^^' ) as genderId, group_concat(COALESCE(raceId, 'NULL') order by message_patients.id separator '^^^^^' ) as raceId, group_concat(COALESCE(hispanicId, 'NULL') order by message_patients.id separator '^^^^^' ) as hispanicId, group_concat(COALESCE(englishProficient, 'NULL') order by message_patients.id separator '^^^^^' ) as englishProficient from message_patients , transactiontarget tt where tt.transactionInId = message_patients.transactionInId and BatchDLId = 1301 and statusId = 37 group by message_patients.transactionInId) selectTbl ON tto.transactiontargetid = selectTbl.transactiontargetid SET tto.f3 = selectTbl.sourcePatientId, tto.f4 = selectTbl.dob, tto.f5 = selectTbl.genderId, tto.f6 = selectTbl.raceId, tto.f7 = selectTbl.hispanicId, tto.f15 = selectTbl.englishProficient WHERE tto.transactiontargetid = selectTbl.transactiontargetid;
 *
 *
 */
public class ConfigOutboundForInsert {

    int batchDownloadId;
    int configId;
    String fieldNos;
    String saveToCols;
    String saveToTableName;
    String selectFields;
    String updateFields;

    public int getBatchDownloadId() {
        return batchDownloadId;
    }

    public void setBatchDownloadId(int batchDownloadId) {
        this.batchDownloadId = batchDownloadId;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public String getFieldNos() {
        return fieldNos;
    }

    public void setFieldNos(String fieldNos) {
        this.fieldNos = fieldNos;
    }

    public String getSaveToCols() {
        return saveToCols;
    }

    public void setSaveToCols(String saveToCols) {
        this.saveToCols = saveToCols;
    }

    public String getSaveToTableName() {
        return saveToTableName;
    }

    public void setSaveToTableName(String saveToTableName) {
        this.saveToTableName = saveToTableName;
    }

    public String getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(String selectFields) {
        this.selectFields = selectFields;
    }

    public String getUpdateFields() {
        return updateFields;
    }

    public void setUpdateFields(String updateFields) {
        this.updateFields = updateFields;
    }

}
