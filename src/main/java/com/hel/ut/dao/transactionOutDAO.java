/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.dao;

import com.hel.ut.model.RestAPIMessagesOut;
import com.hel.ut.model.batchDLRetry;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationSchedules;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.targetOutputRunLogs;
import com.hel.ut.model.transactionOutRecords;
import com.hel.ut.model.custom.ConfigOutboundForInsert;
import com.hel.ut.model.custom.batchErrorSummary;
import com.hel.ut.model.directmessagesout;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

/**
 *
 * @author chadmccue
 */
@Repository
public interface transactionOutDAO {

    List<batchDownloads> getInboxBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception;

    batchDownloads getBatchDetails(int batchId) throws Exception;

    batchDownloads getBatchDetailsByBatchName(String batchName) throws Exception;

    List<transactionOutRecords> getTransactionRecords(Integer batchId, Integer configId, Integer totalFields) throws Exception;

    @SuppressWarnings("rawtypes")
    List getInternalStatusCodes();

    void updateTargetBatchStatus(Integer batchDLId, Integer statusId, String timeField) throws Exception;

    Integer submitBatchDownload(batchDownloads batchDownload);

    int findMergeableBatch(int orgId);

    void updateBatchOutputFileName(int batchId, String fileName);

    int getMaxFieldNo(int configId) throws Exception;

    List<batchDownloads> getdownloadableBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception;

    void updateLastDownloaded(int batchId) throws Exception;

    List<configurationSchedules> getScheduledConfigurations();

    void updateBatchStatus(Integer batchId, Integer statusId);

    void saveOutputRunLog(targetOutputRunLogs log) throws Exception;

    List<targetOutputRunLogs> getLatestRunLog(int configId) throws Exception;

    List<batchDownloads> getAllBatches(Date fromDate, Date toDate, String batchName) throws Exception;

    Integer writeOutputToTextFile(configurationTransport transportDetails, Integer batchDLId, String filePathAndName, String configFields);

    List<ConfigOutboundForInsert> setConfigOutboundForInsert(int configId, int batchDownloadId) throws Exception;

    String getConfigFieldsForOutput(Integer configId) throws Exception;

    void setSessionLength() throws Exception;

    BigInteger getRejectedCount(String fromDate, String toDate) throws Exception;

    String getCustomXMLFieldsForOutput(Integer configId) throws Exception;

    List getOutputForCustomTargetFile(configurationTransport transportDetails, Integer batchDownloadId, String fieldNos) throws Exception;

    List<batchDownloads> getBatchesByStatusIdsAndDate(Date fromDate, Date toDate, Integer fetchSize, List<Integer> statusIds) throws Exception;

    Integer insertRestApiMessage(RestAPIMessagesOut APIMessageOut) throws Exception;

    List<batchDownloads> getDLBatchesByStatusIds(List<Integer> statusIds) throws Exception;

    batchDLRetry getBatchDLRetryByDownloadId(Integer batchDownloadId, Integer statusId) throws Exception;

    void saveBatchDLRetry(batchDLRetry br) throws Exception;

    void clearBatchDLRetry(Integer batchDownloadId) throws Exception;

    void deleteRestAPIMessageByDownloadId(int batchDownloadId) throws Exception;

    void createTargetBatchTables(Integer batchDownloadId, Integer configId) throws Exception;
    
    void loadTargetBatchTables(Integer batchDownloadId, Integer batchUploadId, Integer configId, Integer uploadConfigId) throws Exception;
    
    void deleteBatchUploadTables(Integer batchUploadId) throws Exception;
    
    Integer clearBatchTransactionTables(Integer batchDownloadId) throws Exception;
    
    Integer insertFailedRequiredFields(configurationFormFields cff, Integer batchUploadId);
    
    void deleteBatchDownloadTables(Integer batchId) throws Exception;
    
    void deleteBatchDownloadTablesByBatchUpload(List<batchDownloads> batchDownloads) throws Exception;
    
    List<batchDownloads> getDLBatchesByBatchUploadId(Integer batchUploadId) throws Exception;
    
    List<batchDownloads> getPendingResetBatches(Integer batchUploadId) throws Exception;
    
    void submitBatchDownloadChanges(batchDownloads batchDownload) throws Exception;
    
    boolean chechForTransactionInTable(Integer batchUploadId) throws Exception;
    
    List<batchDownloads> getAllSentBatchesPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception;
    
    void insertDMMessage(directmessagesout newDirectMessageOut) throws Exception;
    
    void populateOutboundAuditReport(Integer configId, Integer batchDownloadId, Integer batchUploadId) throws Exception;
    
    List<batchErrorSummary> getBatchErrorSummary(int batchId) throws Exception;
}
