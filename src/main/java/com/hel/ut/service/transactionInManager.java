/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service;

import com.hel.ut.model.activityReportList;
import com.hel.ut.model.MoveFilesLog;
import com.hel.ut.model.CrosswalkData;
import com.hel.ut.model.Macros;
import com.hel.ut.model.RestAPIMessagesIn;
import com.hel.ut.model.Transaction;
import com.hel.ut.model.utUser;
import com.hel.ut.model.WSMessagesIn;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchRetry;
import com.hel.ut.model.batchUploadDroppedValues;
import com.hel.ut.model.batchUploads;
import com.hel.ut.model.batchuploadactivity;
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
import com.hel.ut.model.systemSummary;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author chadmccue
 */
public interface transactionInManager {

    String getFieldValue(String tableName, String tableCol, String idCol, int idValue);

    List<fieldSelectOptions> getFieldSelectOptions(int fieldId, int configId);

    Integer submitBatchUpload(batchUploads batchUpload) throws Exception;

    void submitBatchUploadChanges(batchUploads batchUpload) throws Exception;

    List<batchUploads> getsentBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception;

    batchUploads getBatchDetails(int batchId) throws Exception;

    batchUploads getBatchDetailsByBatchName(String batchName) throws Exception;

    String uploadAttachment(MultipartFile fileUpload, String orgName) throws Exception;

    List<ConfigForInsert> setConfigForInsert(int configId, int batchUploadId);

    List<batchUploads> getuploadedBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception;

    List<batchUploads> getuploadedBatches(int userId, int orgId, Date fromDate, Date toDate, List<Integer> excludedStatusIds) throws Exception;

    boolean processBatch(int batchUploadId, boolean doNotClearErrors) throws Exception;

    void processBatches();

    void updateBatchStatus(Integer batchUploadId, Integer statusId, String timeField) throws Exception;

    boolean setDoNotProcess(Integer batchUploadId);

    Integer clearTransactionTables(Integer batchUploadId, Integer configId) throws Exception;

    void flagAndEmailAdmin(Integer batchUploadId);

    List<configurationFormFields> getRequiredFieldsForConfig(Integer configId);

    Integer insertFailedRequiredFields(configurationFormFields cff, Integer batchUploadId);
 
    void updateStatusForErrorTrans(Integer batchId, Integer statusId, boolean foroutboundProcessing) throws Exception;

    Integer runValidations(Integer batchUploadId, Integer configId);

    Integer genericValidation(configurationFormFields cff, Integer validationTypeId, Integer batchUploadId, String regEx);

    List<Integer> getFeedbackReportConnection(int configId, int targetorgId);

    String formatDateForDB(Date date);

    Integer processCrosswalk(Integer configId, Integer batchId, configurationDataTranslations translation, boolean foroutboundProcessing);

    Integer processMacro(Integer configId, Integer batchId, configurationDataTranslations translation, boolean foroutboundProcessing);

    void nullForCWCol(Integer configId, Integer batchId, boolean foroutboundProcessing);

    void updateFieldNoWithCWData(Integer configId, Integer batchId, Integer fieldNo, Integer passClear, boolean foroutboundProcessing);

    Integer executeMacro(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing, Macros macro);

    Integer flagCWErrors(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing, boolean isFieldRequired);

    Integer flagMacroErrors(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing);

    List<configurationTransport> getHandlingDetailsByBatch(int batchId);

    void insertProcessingError(Integer errorId, Integer configId, Integer batchId, Integer fieldNo, Integer macroId, Integer cwId, Integer validationTypeId, boolean required, boolean foroutboundProcessing, String errorCause);

    void insertProcessingError(Integer errorId, Integer configId, Integer batchId, Integer fieldNo, Integer macroId, Integer cwId, Integer validationTypeId, boolean required, boolean foroutboundProcessing, String errorCause, Integer transactionId);

    void updateRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing, String colNameToUpdate);

    Integer getRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing);

    Integer getRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing, boolean inStatusIds);

    Integer insertLoadData(Integer batchId, Integer configId, String delimChar, String fileWithPath, String loadTableName, boolean containsHeaderRow, String lineTerminator);

    Integer updateConfigIdForBatch(Integer batchId, Integer configId);

    Integer loadTransactionTranslatedIn(Integer batchId, Integer configId);
    
    List<configurationConnection> getPassThruBatchTargets(Integer batchId, boolean active);

    List<configurationConnection> getBatchTargets(Integer batchId, boolean active);

    void loadBatch(Integer batchId) throws Exception;

    List<batchUploads> getBatchesByStatusIds(List<Integer> statusIds);

    void setBatchToError(Integer batchId, String errorMessage) throws Exception;

    Integer rejectInvalidTargetOrg(Integer batchId, configurationConnection confConn);

    Integer rejectNoConnections(batchUploads batch);

    List<batchUploads> getAllUploadedBatches(Date fromDate, Date toDate) throws Exception;

    List<batchUploads> getAllUploadedBatches(Date fromDate, Date toDate, Integer fetchSize, String batchName) throws Exception;

    boolean searchTransactions(Transaction transaction, String searchTerm) throws Exception;

    systemSummary generateSystemInboundSummary();

    boolean checkPermissionForBatch(utUser userInfo, batchUploads batchInfo);

    ConfigErrorInfo getHeaderForConfigErrorInfo(Integer batchId, ConfigErrorInfo configErrorInfo);

    boolean hasPermissionForBatch(batchUploads batchInfo, utUser userInfo, boolean hasConfigurations);

    boolean allowBatchClear(Integer batchUploadId);

    List<batchuploadactivity> getBatchActivities(batchUploads batchInfo);

    Integer insertSFTPRun(MoveFilesLog sftpJob);

    void updateSFTPRun(MoveFilesLog sftpJob) throws Exception;

    List<batchUploads> getsentBatchesHistory(int userId, int orgId, int toOrgId, int messageTypeId, Date fromDate, Date toDate) throws Exception;

    Integer moveSFTPFiles();

    Integer moveFilesByPath(String rootPath, String cnofigPath, Integer transportMethodId, Integer orgId, Integer transportId);

    List<configurationFTPFields> getFTPInfoForJob(Integer method);

    String newFileName(String path, String fileName);

    String copyUplaodedPath(configurationTransport transportDetails, MultipartFile fileUpload);

    Integer moveFileDroppedFiles();

    List<configurationFileDropFields> getFileDropInfoForJob(Integer method);

    List<Integer> checkCWFieldForList(Integer configId, Integer batchId,configurationDataTranslations cdt, boolean foroutboundProcessing);

    Integer processMultiValueCWData(Integer configId, Integer batchId, configurationDataTranslations cdt, List<CrosswalkData> cdList, boolean foroutboundProcessing);

    List<IdAndFieldValue> getIdAndValuesForConfigField(Integer configId, Integer batchId,configurationDataTranslations cdt, boolean foroutboundProcessing);

    Integer updateFieldValue(Integer batchId, String fieldValue, Integer fieldNo, Integer transactionId, boolean foroutboundProcessing);

    void trimFieldValues(Integer batchId, boolean foroutboundProcessing, Integer transactionId, boolean trimAll);

    void sendEmailToAdmin(String message, String subject) throws Exception;

    Integer updateBatchDLStatusByUploadBatchId(Integer batchUploadId, Integer fromStatusId, Integer toStatusId, String timeField);

    Integer clearBatchDownloads(List<Integer> batchDownloadIDs);

    String getTransactionInIdsFromBatch(Integer batchUploadId);
    
    List<WSMessagesIn> getWSMessagesByStatusId(List<Integer> statusIds);

    WSMessagesIn getWSMessagesById(Integer wsMessageId);

    Integer updateWSMessage(WSMessagesIn wsMessage);

    List<Integer> getErrorCodes(List<Integer> codesToIgnore);

    Integer rejectInvalidSourceSubOrg(batchUploads batch, configurationConnection confConn, boolean nofinalStatus);

    List<Integer> getBatchesForReport(Date fromDate, Date toDate) throws Exception;

    BigInteger getMessagesSent(Date fromDate, Date toDate) throws Exception;

    BigInteger getRejectedCount(Date fromDate, Date toDate) throws Exception;
    
    BigInteger getRejectedReceivedCount(Date fromDate, Date toDate) throws Exception;

    List<activityReportList> getReferralList(Date fromDate, Date toDate) throws Exception;

    List<referralActivityExports> getReferralActivityExports() throws Exception;

    List<batchUploads> getAllRejectedBatches(Date fromDate, Date toDate, Integer fetchSize) throws Exception;

    void clearMultipleTargets(Integer batchId) throws Exception;

    void sendRejectNotification(batchUploads batch) throws Exception;

    List<Transaction> getTransactionsByStatusId(Integer batchId, List<Integer> statusIds, Integer howMany) throws Exception;

    List<Transaction> setTransactionInInfoByStatusId(Integer batchId, List<Integer> statusIds, Integer howMany) throws Exception;

    void loadMassBatches() throws Exception;

    void processMassBatches() throws Exception;

    List<CrosswalkData> getCrosswalkDataForBatch(configurationDataTranslations cdt, Integer batchId, boolean foroutboundProcessing) throws Exception;

    List<referralActivityExports> getReferralActivityExportsByStatus(List<Integer> statusIds, Integer howMany) throws Exception;

    public void updateReferralActivityExport(referralActivityExports activityExport) throws Exception;

    void sendExportEmail(utUser userDetails) throws Exception;

    public void saveReferralActivityExport(referralActivityExports activityExport) throws Exception;

    List<referralActivityExports> getReferralActivityExportsWithUserNames(List<Integer> statusIds) throws Exception;

    referralActivityExports getReferralActivityExportById(Integer exportId) throws Exception;

    void populateAuditReport(Integer batchUploadId, configurationMessageSpecs cms) throws Exception;

    List<Integer> getErrorFieldNos(Integer batchUploadId) throws Exception;

    void populateFieldError(Integer batchUploadId, Integer fieldNo, configurationMessageSpecs cms) throws Exception;

    void cleanAuditErrorTable(Integer batchUploadId) throws Exception;

    void executeCWDataForSingleFieldValue(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing);

    void deleteMoveFileLogsByStatus(Integer statusId, Integer transportMethodId) throws Exception;

    void deleteLoadTableRows(Integer howMany, String ascOrDesc, String laodTableName) throws Exception;
    
    BigInteger getUserRejectedCount(Integer userId, Integer orgId, String fromDate, String toDate) throws Exception;
    
    List<batchErrorSummary> getBatchErrorSummary(int batchId, String inboundOutbound) throws Exception;
    
    List getErrorDataBySQLStmt(String sqlStmt) throws Exception;
    
    List getErrorReportField(Integer batchId) throws Exception;
    
    List<batchUploads> getBatchesByStatusIdsAndDate(Date fromDate, Date toDate, Integer fetchSize, List <Integer> statusIds) throws Exception;
    
    batchRetry getBatchRetryByUploadId (Integer batchUploadId, Integer statusId) throws Exception;
    
    void saveBatchRetry (batchRetry br) throws Exception;
    
    void clearBatchRetry (Integer batchUploadId) throws Exception;
    
    Integer insertRestApiMessage(RestAPIMessagesIn newRestAPIMessage) throws Exception;
    
    void processRestAPIMessages();
    
    List<RestAPIMessagesIn> getRestAPIMessagesByStatusId(List<Integer> statusIds);

    RestAPIMessagesIn getRestAPIMessagesById(Integer RestAPIMessageId);
    
    Integer updateRestAPIMessage(RestAPIMessagesIn APIMessage);
    
    void updateBatchClearAfterDeliveryByBatchUploadId(Integer batchUploadId, Integer newStatusId) throws Exception;
    
    Integer clearBatchClearAfterDeliveryByBatchUploadId(Integer batchUploadId) throws Exception;

    boolean assignBatchDLId (Integer batchUploadId, Integer configId) throws Exception;
    
    List<Integer> getTargetConfigsForUploadBatch(Integer batchUploadId, Integer configId) throws Exception;
    
    Integer checkClearAfterDeliveryBatch (int batchUploadId) throws Exception;
    
    Integer removeLoadTableBlankRows(Integer batchUploadId, String loadTableName) throws Exception;

    Integer getLoadTransactionCount(String loadTableName) throws Exception;
    
    void deleteBatch(Integer batchId) throws Exception;
    
    Integer getRecordCountForTable(String tableName, String colName, int matchId) throws Exception;   

    void updateBatchUpload(batchUploads batchUpload) throws Exception;
    
    List<Integer> getConfigIdsForBatchOnly(int batchUploadId) throws Exception;
    
    void deleteBatchTransactionTables(Integer batchUploadId) throws Exception;
    
    List<batchDownloads> findBatchesToCleanUp() throws Exception;
    
    void batchUploadTableCleanUp() throws Exception;
    
    void resetTransactionCounts(Integer batchUploadId) throws Exception;
    
    Integer insertDMMessage(directmessagesin newDirectMessageIn) throws Exception;
    
    void processDirectAPIMessages();
    
    List<directmessagesin> getDirectAPIMessagesByStatusId(List<Integer> statusIds);

    directmessagesin getDirectAPIMessagesById(Integer directMessageId);
    
    directmessagesin getDirectAPIMessagesByBatchUploadId(Integer BatchUploadId);
    
    Integer updateDirectAPIMessage(directmessagesin directMessage);
    
    List<batchUploads> getAllUploadBatchesPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception;
    
    void updateRecordCountsFromAuditErrorTable(Integer batchUploadId) throws Exception;
    
    void checkRemoteSFTPConfigurations() throws Exception;
    
    void insertCWDroppedValues(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) throws Exception;
    
    void populateDroppedValues(Integer batchUploadId, Integer configId, boolean foroutboundProcessing) throws Exception;

    List<batchUploadDroppedValues> getBatchDroppedValues(Integer batchUploadId) throws Exception;
    
    Integer getTotalErroredRows(Integer batchUploadId) throws Exception;
    
    List<batchErrorSummary> getBatchSystemErrorSummary(int batchId, String inboundOutbound) throws Exception;
    
    void clearBatchActivityLogTable(Integer batchId) throws Exception;
}
