/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service;

import com.hel.ut.model.Transaction;
import com.hel.ut.model.batchDLRetry;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchUploads;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.pendingDeliveryTargets;
import com.hel.ut.model.systemSummary;
import com.hel.ut.model.transactionOutRecords;
import com.hel.ut.model.custom.ConfigOutboundForInsert;
import com.hel.ut.model.custom.batchErrorSummary;
import com.hel.ut.model.directmessagesout;
import com.hel.ut.model.utUserActivity;
import java.io.File;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 *
 * @author chadmccue
 */
public interface transactionOutManager {

    List<batchDownloads> getInboxBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception;

    batchDownloads getBatchDetails(int batchId) throws Exception;

    batchDownloads getBatchDetailsByBatchName(String batchName) throws Exception;

    List<transactionOutRecords> getTransactionRecords(Integer batchId, Integer configId, Integer totalFields) throws Exception;

    List getInternalStatusCodes();

    void updateTargetBatchStatus(Integer batchDLId, Integer statusId, String timeField) throws Exception;

    Integer translateTargetRecords(int transactionTargetId, int configId, int batchId, int categoryId);

    List<batchDownloads> getdownloadableBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception;

    void updateLastDownloaded(int batchId) throws Exception;

    systemSummary generateSystemOutboundSummary();

    List<batchDownloads> getAllBatches(Date fromDate, Date toDate, String batchName) throws Exception;

    boolean searchTransactions(Transaction transaction, String searchTerm) throws Exception;

    boolean searchTransactionsByMessageType(pendingDeliveryTargets transaction, String searchTerm) throws Exception;

    boolean searchPendingTransactions(Transaction tran, String searchTerm) throws Exception;

    void processMassOutputBatches() throws Exception;

    Integer processMassOutputBatch(batchDownloads batchDownload) throws Exception;

    Integer writeOutputToTextFile(configurationTransport transportDetails, Integer batchDownloadId, String filePathAndName, String fieldNos, Integer batchUploadId) throws Exception;

    String generateDLBatchName(String utBatchName, configurationTransport transportDetails, utConfiguration configDetails, batchUploads batchUploadDetails, Date date) throws Exception;

    List<ConfigOutboundForInsert> setConfigOutboundForInsert(int configId, int batchDownloadId) throws Exception;

    String getConfigFieldsForOutput(Integer configId) throws Exception;

    void runValidations(Integer batchDownloadId, Integer configId) throws Exception;

    BigInteger getRejectedCount(Date fromDate, Date toDate) throws Exception;
    
    List<batchDownloads> getBatchesByStatusIdsAndDate(Date fromDate, Date toDate, Integer fetchSize, List<Integer> statusIds) throws Exception;
    
    List<batchDownloads> getDLBatchesByStatusIds(List<Integer> statusIds) throws Exception;
    
    void clearDownloadBatch (int batchDownloadId) throws Exception;
    
    void saveBatchDLRetry (batchDLRetry br) throws Exception;
    
    void clearBatchDLRetry (Integer batchDownloadId) throws Exception;
    
    void deleteRestAPIMessageByDownloadId(int batchDownloadId) throws Exception;
    
    Integer clearBatchTransactionTables(Integer batchDownloadId) throws Exception;
    
    Integer insertFailedRequiredFields(configurationFormFields cff, Integer batchUploadId);
    
    batchDLRetry getBatchDLRetryByDownloadId(Integer batchDownloadId, Integer statusId) throws Exception;

    void deleteBatchDownloadTables(Integer batchId) throws Exception;
    
    void deleteBatchDownloadTablesByBatchUpload(Integer batchId) throws Exception;
    
    List<batchDownloads> getPendingResetBatches(Integer batchUploadId) throws Exception;
    
    void submitBatchDownloadChanges(batchDownloads batchDownload) throws Exception;
	
    void sendPassThruFiles(batchUploads batchULDetails, batchDownloads batchDLDetails,configurationTransport transportDetails,File archiveFile) throws Exception;
    
    List<batchDownloads> getDownloadBatchesByBatchUploadId(Integer batchUploadId) throws Exception;
    
    void checkOutboundScheduledBatches() throws Exception;
    
    boolean chechForTransactionInTable(Integer batchUploadId) throws Exception;
    
    List<batchDownloads> getAllSentBatchesPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception;
    
    void insertDMMessage(directmessagesout newDirectMessageOut) throws Exception;
    
    List<batchErrorSummary> getBatchErrorSummary(int batchId) throws Exception;
    
    List<utUserActivity> getBatchActivities(batchDownloads batchInfo);
    
}
