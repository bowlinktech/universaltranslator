/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.dao;

import com.hel.ut.model.Transaction;
import com.hel.ut.model.activityReportList;
import com.hel.ut.model.CrosswalkData;
import com.hel.ut.model.Macros;
import com.hel.ut.model.MoveFilesLog;
import com.hel.ut.model.RestAPIMessagesIn;
import com.hel.ut.model.utUser;
import com.hel.ut.model.utUserActivity;
import com.hel.ut.model.WSMessagesIn;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchRetry;
import com.hel.ut.model.batchUploads;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationDataTranslations;
import com.hel.ut.model.configurationFTPFields;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.configurationFileDropFields;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.fieldSelectOptions;
import com.hel.ut.model.custom.ConfigErrorInfo;
import com.hel.ut.model.custom.ConfigForInsert;
import com.hel.ut.model.custom.IdAndFieldValue;
import com.hel.ut.model.custom.batchErrorSummary;
import com.hel.ut.model.directmessagesin;
import com.hel.ut.model.referralActivityExports;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

/**
 *
 * @author chadmccue
 */
@Repository
public interface transactionInDAO {

    String getFieldValue(String tableName, String tableCol, String idCol, int idValue);

    List<fieldSelectOptions> getFieldSelectOptions(int fieldId, int configId);

    Integer submitBatchUpload(batchUploads batchUpload) throws Exception;

    void submitBatchUploadChanges(batchUploads batchUpload) throws Exception;

    List<batchUploads> getsentBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception;

    batchUploads getBatchDetails(int batchId) throws Exception;

    batchUploads getBatchDetailsByBatchName(String batchName) throws Exception;

    List<ConfigForInsert> setConfigForInsert(int configId, int batchUploadId);

    Integer genericValidation(configurationFormFields cff, Integer validationTypeId, Integer batchUploadId, String regEx);

    List<batchUploads> getuploadedBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception;

    List<batchUploads> getuploadedBatches(int userId, int orgId, Date fromDate, Date toDate, List<Integer> excludedStatusIds) throws Exception;

    void updateBatchStatus(Integer batchUploadId, Integer statusId, String timeField) throws Exception;

    boolean allowBatchClear(Integer batchUploadId);

    Integer clearBatchTransactionTables(Integer batchUploadId, Integer configId);

    Integer insertFailedRequiredFields(configurationFormFields cff, Integer batchUploadId);

    void updateStatusForErrorTrans(Integer batchId, Integer statusId, boolean foroutboundProcessing) throws Exception;

    List<Integer> getFeedbackReportConnection(int configId, int targetorgId);

    void nullForCWCol(Integer configId, Integer batchId, boolean foroutboundProcessing);

    void updateFieldNoWithCWData(Integer configId, Integer batchId, Integer fieldNo, Integer passClear, boolean foroutboundProcessing);

    void flagCWErrors(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing);

    Integer flagMacroErrors(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing);

    Integer executeMacro(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing, Macros macro);

    void insertProcessingError(Integer errorId, Integer configId, Integer batchId, Integer fieldNo, Integer macroId, Integer cwId, Integer validationTypeId, boolean required, boolean foroutboundProcessing, String errorCause);

    void insertProcessingError(Integer errorId, Integer configId, Integer batchId, Integer fieldNo, Integer macroId, Integer cwId, Integer validationTypeId, boolean required, boolean foroutboundProcessing, String errorCause, Integer transactionId);

    List<configurationTransport> getHandlingDetailsByBatch(int batchId) throws Exception;

    void updateRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing, String colNameToUpdate);

    Integer getRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing);

    Integer getRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing, boolean inStatusIds);

    Integer insertLoadData(Integer batchId, Integer configId, String delimChar, String fileWithPath, String tableName, boolean containsHeaderRow, String lineTerminator);

    Integer updateConfigIdForBatch(Integer batchId, Integer configId);

    Integer loadTransactionTranslatedIn(Integer batchId, Integer configId);

    List<configurationConnection> getBatchTargets(Integer batchId, boolean active);

    List<batchUploads> getBatchesByStatusIds(List<Integer> statusIds);

    Integer rejectInvalidTargetOrg(Integer batchId, configurationConnection batchTargets);

    Integer rejectNoConnections(batchUploads batch);

    List<batchUploads> getAllUploadedBatches(Date fromDate, Date toDate) throws Exception;

    List<batchUploads> getAllUploadedBatches(Date fromDate, Date toDate, Integer fetchSize, String batchName) throws Exception;

    boolean checkPermissionForBatch(utUser userInfo, batchUploads batchInfo);

    ConfigErrorInfo setConfigErrorInfo(Integer batchId, Integer errorCode, ConfigErrorInfo configErrorInfo);

    ConfigErrorInfo getHeaderForConfigErrorInfo(Integer batchId, ConfigErrorInfo configErrorInfo, List<Integer> rptFieldArray);

    List<utUserActivity> getBatchUserActivities(batchUploads batchInfo, boolean foroutboundProcessing);

    Integer insertSFTPRun(MoveFilesLog sftpJob);

    void updateSFTPRun(MoveFilesLog sftpJob) throws Exception;

    List<batchUploads> getsentBatchesHistory(int userId, int orgId, int toOrgId, int messageTypeId, Date fromDate, Date toDate) throws Exception;

    List<configurationFTPFields> getFTPInfoForJob(Integer method);

    List<configurationFileDropFields> getFileDropInfoForJob(Integer method);

    List<Integer> checkCWFieldForList(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing);

    List<IdAndFieldValue> getIdAndValuesForConfigField(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing);

    Integer updateFieldValue(Integer batchId, String fieldValue, Integer fieldNo, Integer transactionId, boolean foroutboundProcessing);

    void trimFieldValues(Integer batchId, boolean foroutboundProcessing, Integer configId, boolean trimAll);

    Integer updateBatchDLStatusByUploadBatchId(Integer batchUploadId, Integer fromStatusId, Integer toStatusId, String timeField);

    Integer clearBatchDownloads(List<Integer> batchDownloadIDs);

    String getTransactionInIdsFromBatch(Integer batchUploadId);

    List<WSMessagesIn> getWSMessagesByStatusId(List<Integer> statusIds);

    WSMessagesIn getWSMessagesById(Integer wsMessageId);

    Integer updateWSMessage(WSMessagesIn wsMessage);

    List<Integer> getErrorCodes(List<Integer> codesToIgnore);

    Integer rejectInvalidSourceSubOrg(batchUploads batch, configurationConnection confConn, boolean nofinalStatus);

    List<Integer> geBatchesIdsForReport(String fromDate, String toDate) throws Exception;

    BigInteger getMessagesSent(String fromDate, String toDate) throws Exception;

    List<activityReportList> getReferralList(String fromDate, String toDate) throws Exception;

    BigInteger getRejectedCount(String fromDate, String toDate) throws Exception;
    
    BigInteger getRejectedReceivedCount(String fromDate, String toDate) throws Exception;

    List<referralActivityExports> getReferralActivityExports() throws Exception;

    void saveReferralActivityExport(referralActivityExports activityExport) throws Exception;

    String getActivityStatusValueById(Integer activityStatusId) throws Exception;

    String getReportActivityStatusValueById(Integer activityStatusId) throws Exception;

    List<batchUploads> getAllRejectedBatches(Date fromDate, Date toDate, Integer fetchSize) throws Exception;

    void clearMultipleTargets(Integer batchId) throws Exception;

    List<Transaction> setTransactionInInfoByStatusId(Integer batchId, List<Integer> statusIds, Integer howMany) throws Exception;

    List<CrosswalkData> getCrosswalkDataForBatch(configurationDataTranslations cdt, Integer batchId, boolean foroutboundProcessing) throws Exception;

    List<referralActivityExports> getReferralActivityExportsByStatus(List<Integer> statusIds, Integer howMany) throws Exception;

    public void updateReferralActivityExport(referralActivityExports activityExport) throws Exception;

    List<referralActivityExports> getReferralActivityExportsWithUserNames(List<Integer> statusIds) throws Exception;

    referralActivityExports getReferralActivityExportById(Integer exportId) throws Exception;

    void populateAuditReport(Integer batchUploadId, Integer configId) throws Exception;

    List<Integer> getErrorFieldNos(Integer batchUploadId) throws Exception;

    void populateFieldError(Integer batchUploadId, Integer fieldNo, configurationMessageSpecs cms) throws Exception;

    void cleanAuditErrorTable(Integer batchUploadId) throws Exception;

    Integer executeCWDataForSingleFieldValue(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing);

    void deleteMoveFileLogsByStatus(Integer statusId, Integer transportMethodId) throws Exception;

    void deleteLoadTableRows(Integer howMany, String ascOrDesc, String laodTableName) throws Exception;

    BigInteger getUserRejectedCount(Integer userId, Integer orgId, String fromDate, String toDate) throws Exception;

    List<batchErrorSummary> getBatchErrorSummary(int batchId) throws Exception;

    List getErrorDataBySQLStmt(String sqlStmt) throws Exception;

    List getErrorReportField(Integer batchId) throws Exception;

    List<batchUploads> getBatchesByStatusIdsAndDate(Date fromDate, Date toDate, Integer fetchSize, List<Integer> statusIds) throws Exception;

    batchRetry getBatchRetryByUploadId(Integer batchUploadId, Integer statusId) throws Exception;

    void saveBatchRetry(batchRetry br) throws Exception;

    void clearBatchRetry(Integer batchUploadId) throws Exception;

    Integer insertRestApiMessage(RestAPIMessagesIn newRestAPIMessage) throws Exception;

    List<RestAPIMessagesIn> getRestAPIMessagesByStatusId(List<Integer> statusIds);

    RestAPIMessagesIn getRestAPIMessagesById(Integer RestAPIMessageId);

    Integer updateRestAPIMessage(RestAPIMessagesIn APIMessage);

    void updateBatchClearAfterDeliveryByBatchUploadId(Integer batchUploadId, Integer newStatusId) throws Exception;

    Integer clearBatchClearAfterDeliveryByBatchUploadId(Integer batchUploadId) throws Exception;

    List<Integer> getTargetConfigsForUploadBatch(Integer batchUploadId, Integer configId) throws Exception;

    Integer checkClearAfterDeliveryBatch(int batchUploadId) throws Exception;

    Integer removeLoadTableBlankRows(Integer batchUploadId, String loadTableName) throws Exception;

    Integer getLoadTransactionCount(String loadTableName) throws Exception;

    void deleteBatch(Integer batchId) throws Exception;

    Integer getRecordCountForTable(String tableName, String colName, int matchId) throws Exception;

    void updateBatchUpload(batchUploads batchUpload) throws Exception;

    List<Integer> getConfigIdsForBatchOnly(int batchUploadId) throws Exception;

    void createBatchTables(int batchUploadId, int configId);

    void deleteBatchTransactionTables(Integer batchUploadId) throws Exception;

    List<batchDownloads> findBatchesToCleanUp() throws Exception;

    void batchUploadTableCleanUp(List<batchDownloads> batchesToCleanup) throws Exception;

    List<configurationConnection> getPassThruBatchTargets(Integer batchId, boolean active);

    void resetTransactionCounts(Integer batchUploadId) throws Exception;
    
    List<MoveFilesLog> existingMoveFileLogs(Integer statusId, Integer methodId) throws Exception;
    
    void deleteMoveFileLogById(Integer logId) throws Exception;
    
    batchUploads getBatchDetailsByOriginalFileName(String batchName) throws Exception;
    
    Integer insertDMMessage(directmessagesin newDirectMessageIn) throws Exception;
    
    List<directmessagesin> getDirectAPIMessagesByStatusId(List<Integer> statusIds);

    directmessagesin getDirectAPIMessagesById(Integer directMessageId);
    
    Integer updateDirectAPIMessage(directmessagesin directMessage);
    
    List<batchUploads> getAllUploadBatchesPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception;
}
