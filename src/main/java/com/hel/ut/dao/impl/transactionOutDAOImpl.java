/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.dao.impl;

import com.hel.ut.dao.transactionOutDAO;
import com.hel.ut.model.RestAPIMessagesOut;
import com.hel.ut.model.batchDLRetry;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationConnectionReceivers;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationSchedules;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.custom.ConfigOutboundForInsert;
import com.hel.ut.model.targetOutputRunLogs;
import com.hel.ut.model.transactionOutRecords;
import com.hel.ut.service.sysAdminManager;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.userManager;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.hel.ut.service.utConfigurationTransportManager;

/**
 *
 * @author chadmccue
 */
@Repository
public class transactionOutDAOImpl implements transactionOutDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private sysAdminManager sysAdminManager;

    @Autowired
    private userManager usermanager;

    @Autowired
    private transactionInManager transactionInManager;
    
    @Autowired
    private utConfigurationTransportManager configurationTransportManager;

    //list of final status - these records we skip
    private List<Integer> transRELId = Arrays.asList(11, 12, 13, 16, 18, 20);

    private SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //list of final status - these batches are considered generated
    private List<Integer> batchFinalStatuses = Arrays.asList(28, 41, 59);

    /**
     * The 'submitBatchDownload' function will submit the new batch.
     *
     * @param batchDownload The object that will hold the new batch info
     *
     * @table batchDownloadd
     *
     * @return This function returns the batchId for the newly inserted batch
     */
    @Override
    @Transactional(readOnly = false)
    public Integer submitBatchDownload(batchDownloads batchDownload) {

	Integer batchId = null;

	batchId = (Integer) sessionFactory.getCurrentSession().save(batchDownload);

	return batchId;

    }


    /**
     * The 'findMergeableBatch' function will check for any batches created for the target org that are mergable and have not yet been picked up or viewed.
     *
     * @param orgId The id of the organization to look for.
     *
     * @return This function will return the id of a mergeable batch or 0 if no batches are found.
     */
    @Override
    @Transactional(readOnly = true)
    public int findMergeableBatch(int orgId) {

	Query query = sessionFactory.getCurrentSession().createQuery("select id FROM batchDownloads where orgId = :orgId and mergeable = 1 and (statusId = 23 OR statusId = 28)");
	query.setParameter("orgId", orgId);

	Integer batchId = (Integer) query.uniqueResult();

	if (batchId == null) {
	    batchId = 0;
	}

	return batchId;

    }

    /**
     * The 'getInboxBatches' will return a list of received batches for the logged in user.
     *
     * @param userId The id of the logged in user trying to view received batches
     * @param orgId The id of the organization the user belongs to
     *
     * @return The function will return a list of received batches
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("UnusedAssignment")
    public List<batchDownloads> getInboxBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception {

	return findInboxBatches(userId, orgId, 0, 0, fromDate, toDate);

    }

    /**
     * The 'findInboxBatches' will return a list of received batches for the logged in user.
     *
     * @param userId The id of the logged in user trying to view received batches
     * @param orgId The id of the organization the user belongs to
     *
     * @return The function will return a list of received batches
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("UnusedAssignment")
    public List<batchDownloads> findInboxBatches(int userId, int orgId, int fromOrgId, int messageTypeId, Date fromDate, Date toDate) throws Exception {
	int firstResult = 0;

	/* Get a list of connections the user has access to */
	Criteria connections = sessionFactory.getCurrentSession().createCriteria(configurationConnectionReceivers.class);
	connections.add(Restrictions.eq("userId", userId));
	List<configurationConnectionReceivers> userConnections = connections.list();

	List<Integer> messageTypeList = new ArrayList<Integer>();
	List<Integer> sourceOrgList = new ArrayList<Integer>();

	if (userConnections.isEmpty()) {
	    messageTypeList.add(0);
	    sourceOrgList.add(0);
	} else {

	    for (configurationConnectionReceivers userConnection : userConnections) {
		Criteria connection = sessionFactory.getCurrentSession().createCriteria(configurationConnection.class);
		connection.add(Restrictions.eq("id", userConnection.getconnectionId()));

		configurationConnection connectionInfo = (configurationConnection) connection.uniqueResult();

		/* Get the message type for the utConfiguration */
		Criteria targetconfigurationQuery = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
		targetconfigurationQuery.add(Restrictions.eq("id", connectionInfo.gettargetConfigId()));

		utConfiguration configDetails = (utConfiguration) targetconfigurationQuery.uniqueResult();

		/* Add the message type to the message type list */
		if (messageTypeId == 0) {
		    messageTypeList.add(configDetails.getMessageTypeId());
		} else {
		    if (messageTypeId == configDetails.getMessageTypeId()) {
			messageTypeList.add(configDetails.getMessageTypeId());
		    }
		}


		/* Get the list of source orgs */
		Criteria sourceconfigurationQuery = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
		sourceconfigurationQuery.add(Restrictions.eq("id", connectionInfo.getsourceConfigId()));
		utConfiguration sourceconfigDetails = (utConfiguration) sourceconfigurationQuery.uniqueResult();

		/* Add the target org to the target organization list */
		if (fromOrgId == 0) {
		    sourceOrgList.add(sourceconfigDetails.getorgId());
		} else {
		    if (fromOrgId == sourceconfigDetails.getorgId()) {
			sourceOrgList.add(sourceconfigDetails.getorgId());
		    }
		}
	    }
	}

	if (messageTypeList.isEmpty()) {
	    messageTypeList.add(0);
	}

	Criteria findBatches = sessionFactory.getCurrentSession().createCriteria(batchDownloads.class);
	findBatches.add(Restrictions.eq("transportMethodId", 2));
	findBatches.add(Restrictions.and(
		Restrictions.ne("statusId", 29), /* Submission Processed Errored */
		Restrictions.ne("statusId", 30), /* Target Creation Errored */
		Restrictions.ne("statusId", 32) /* Submission Cancelled */
	));

	if (!"".equals(fromDate) && fromDate != null) {
	    findBatches.add(Restrictions.ge("dateCreated", fromDate));
	}

	if (!"".equals(toDate) && toDate != null) {
	    findBatches.add(Restrictions.lt("dateCreated", toDate));
	}

	findBatches.addOrder(Order.desc("dateCreated"));

	return findBatches.list();

    }

    /**
     * The 'getAllBatches' function will return a list of batches for the admin in the processing activities section.
     *
     * @param fromDate
     * @param toDate
     * @return This function will return a list of batch uploads
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getAllBatches(Date fromDate, Date toDate, String batchName) throws Exception {

	int firstResult = 0;

	Criteria findBatches = sessionFactory.getCurrentSession().createCriteria(batchDownloads.class);

	if (fromDate != null) {
	    if (!"".equals(fromDate)) {
		findBatches.add(Restrictions.ge("dateCreated", fromDate));
	    }
	}

	if (toDate != null) {
	    if (!"".equals(toDate)) {
		findBatches.add(Restrictions.lt("dateCreated", toDate));
	    }
	}

	if (!"".equals(batchName)) {
	    findBatches.add(Restrictions.eq("utBatchName", batchName));
	}

	findBatches.addOrder(Order.desc("dateCreated"));

	return findBatches.list();

    }


    /**
     * The 'getBatchDetails' function will return the batch details for the passed in batch id.
     *
     * @param batchId The id of the batch to return.
     */
    @Override
    @Transactional(readOnly = true)
    public batchDownloads getBatchDetails(int batchId) throws Exception {
	return (batchDownloads) sessionFactory.getCurrentSession().get(batchDownloads.class, batchId);

    }

    /**
     * The 'getBatchDetailsByBatchName' will return a batch by name
     *
     * @param batchName The name of the batch to search form.
     *
     * @return This function will return a batchUpload object
     */
    @Override
    @Transactional(readOnly = true)
    public batchDownloads getBatchDetailsByBatchName(String batchName) throws Exception {
	Query query = sessionFactory.getCurrentSession().createQuery("from batchDownloads where utBatchName = :batchName");
	query.setParameter("batchName", batchName);

	if (query.list().size() > 1) {
	    return null;
	} else {
	    return (batchDownloads) query.uniqueResult();
	}

    }

    /**
     * The 'getTransactionRecords' function will return the transaction TARGET records for the passed in transactionId.
     *
     * @param transactionTargetId The id of the transaction records to return
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<transactionOutRecords> getTransactionRecords(Integer batchId, Integer configId, Integer totalFields) throws Exception {
	
	totalFields = totalFields + 10;
	
	String sql = "select ";
		
	for (int i = 1; i <= totalFields; i++) {
	    sql += "F" + i + ",";
	}	
	sql += "id FROM transactiontranslatedout_" + batchId + " order by id asc";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
		Transformers.aliasToBean(transactionOutRecords.class));

	List<transactionOutRecords> records = query.list();
	
	return records;
    }

    /**
     * The 'getInternalStatusCodes' function will query and return the list of active internal status codes that can be set to a message.
     *
     * @return This function will return a list of internal status codes
     */
    @Override
    @Transactional(readOnly = true)
    public List getInternalStatusCodes() {
	Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, displaytext FROM lu_internalMessageStatus order by displayText asc");
	return query.list();
    }

    /**
     * The 'updateTargetBatchStatus' function will update the status of the passed in batch downloadId
     *
     * @param batchDLId The id of the batch download
     * @param statusId The id of the new status
     * @param timeField
     */
    @Override
    @Transactional(readOnly = false)
    public void updateTargetBatchStatus(Integer batchDLId, Integer statusId, String timeField) throws Exception {

	String sql = "update batchDownloads set statusId = :statusId ";
	if (!timeField.equalsIgnoreCase("")) {
	    sql = sql + ", " + timeField + " = CURRENT_TIMESTAMP";
	} else {
	    // we reset time
	    sql = sql + ", startDateTime = null, endDateTime = null";
	}
	sql = sql + " where id = :id ";
	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("statusId", statusId)
		.setParameter("id", batchDLId);
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("updateTargetBatchStatus failed." + ex);
	}
    }


    /**
     * The 'updateBatchOutputFileName' function will update the outputFileName with the finalized generated file name. This will contain the appropriate extension.
     *
     * @param batchId The id of the batch to update
     * @param fileName The new file name to update.
     */
    @Override
    @Transactional(readOnly = false)
    public void updateBatchOutputFileName(int batchId, String fileName) {
	String sql = "update BatchDownloads "
		+ " set outputFileName = :fileName "
		+ " where id = :batchId";

	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("batchId", batchId)
		.setParameter("fileName", fileName);

	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("update Batch outputfile name failed." + ex);
	}
    }

    /**
     * The 'getMaxFieldNo' function will return the max field number for the passed in utConfiguration.
     *
     * @param configId The id of the utConfiguration to find out how many fields it has
     *
     * @return This function will return the max field number.
     */
    @Override
    @Transactional(readOnly = true)
    public int getMaxFieldNo(int configId) throws Exception {

	String sql = "select max(fieldNo) as maxFieldNo from configurationFormFields where configId = :configId";

	/* Need to make sure no duplicates */
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).addScalar("maxFieldNo", StandardBasicTypes.INTEGER);
	query.setParameter("configId", configId);

	return (Integer) query.list().get(0);

    }

    /**
     * The 'getdownloadableBatches' will return a list of received batches for the logged in user that are ready to be downloaded
     *
     * @param userId The id of the logged in user trying to view downloadable batches
     * @param orgId The id of the organization the user belongs to
     *
     * @return The function will return a list of downloadable batches
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("UnusedAssignment")
    public List<batchDownloads> getdownloadableBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception {
	int firstResult = 0;

	/* Get a list of connections the user has access to */
	Criteria connections = sessionFactory.getCurrentSession().createCriteria(configurationConnectionReceivers.class);
	connections.add(Restrictions.eq("userId", userId));
	List<configurationConnectionReceivers> userConnections = connections.list();

	List<Integer> messageTypeList = new ArrayList<Integer>();
	List<Integer> sourceOrgList = new ArrayList<Integer>();

	if (userConnections.isEmpty()) {
	    messageTypeList.add(0);
	    sourceOrgList.add(0);
	} else {

	    for (configurationConnectionReceivers userConnection : userConnections) {
		Criteria connection = sessionFactory.getCurrentSession().createCriteria(configurationConnection.class);
		connection.add(Restrictions.eq("id", userConnection.getconnectionId()));

		configurationConnection connectionInfo = (configurationConnection) connection.uniqueResult();

		/* Get the message type for the utConfiguration */
		Criteria targetconfigurationQuery = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
		targetconfigurationQuery.add(Restrictions.eq("id", connectionInfo.gettargetConfigId()));

		utConfiguration configDetails = (utConfiguration) targetconfigurationQuery.uniqueResult();

		/* Need to make sure only file download configurations are displayed */
		Criteria transportDetailsQuery = sessionFactory.getCurrentSession().createCriteria(configurationTransport.class);
		transportDetailsQuery.add(Restrictions.eq("configId", configDetails.getId()));

		configurationTransport transportDetails = (configurationTransport) transportDetailsQuery.uniqueResult();

		if (transportDetails.gettransportMethodId() == 1
			|| transportDetails.gettransportMethodId() == 3
			|| transportDetails.gettransportMethodId() == 5
			|| transportDetails.gettransportMethodId() == 6
			|| transportDetails.gettransportMethodId() == 9) {
		    /* Add the message type to the message type list */
		    messageTypeList.add(configDetails.getMessageTypeId());
		}

		/* Get the list of source orgs */
		Criteria sourceconfigurationQuery = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
		sourceconfigurationQuery.add(Restrictions.eq("id", connectionInfo.getsourceConfigId()));
		utConfiguration sourceconfigDetails = (utConfiguration) sourceconfigurationQuery.uniqueResult();

		/* Add the target org to the target organization list */
		sourceOrgList.add(sourceconfigDetails.getorgId());
	    }
	}

	if (messageTypeList.isEmpty()) {
	    messageTypeList.add(0);
	}

	Criteria findBatches = sessionFactory.getCurrentSession().createCriteria(batchDownloads.class);
	findBatches.add(Restrictions.or(
		Restrictions.eq("transportMethodId", 1),
		Restrictions.eq("transportMethodId", 3),
		Restrictions.eq("transportMethodId", 5),
		Restrictions.eq("transportMethodId", 6),
		Restrictions.eq("transportMethodId", 9)
	));
	findBatches.add(Restrictions.or(
		Restrictions.eq("statusId", 22),
		Restrictions.eq("statusId", 23),
		Restrictions.eq("statusId", 28)
	));

	if (!"".equals(fromDate)) {
	    findBatches.add(Restrictions.ge("dateCreated", fromDate));
	}

	if (!"".equals(toDate)) {
	    findBatches.add(Restrictions.lt("dateCreated", toDate));
	}

	findBatches.addOrder(Order.desc("dateCreated"));

	return findBatches.list();

    }

    /**
     * The 'updateLastDownloaded' function will update the last downloaded field for the passed in batch
     *
     * @param batchId The id of the batch to update.
     *
     * @return this function will not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public void updateLastDownloaded(int batchId) throws Exception {

	String sql = "update BatchDownloads "
		+ " set lastDownloaded = CURRENT_TIMESTAMP "
		+ " where id = :batchId";

	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("batchId", batchId);

	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("update Batch last downloaded date failed." + ex);
	}

    }

    /**
     * The 'getScheduledConfigurations' function will return a list of configurations that have a Daily, Weekly or Monthly schedule setting
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationSchedules> getScheduledConfigurations() {

	Query query = sessionFactory.getCurrentSession().createQuery("from configurationSchedules where type = 2 or type = 3 or type = 4");

	List<configurationSchedules> scheduledConfigList = query.list();
	return scheduledConfigList;

    }

    /**
     * The 'updateBatchStatus' function will update the status of the passed in batch
     *
     * @param batchId The id of the batch to update
     * @param statusId The status to update the batch to
     * @param timeField
     */
    @Override
    @Transactional(readOnly = false)
    public void updateBatchStatus(Integer batchId, Integer statusId) {

	String sql = "update batchDownloads set statusId = :statusId ";
	sql = sql + " where id = :batchId ";

	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("statusId", statusId)
		.setParameter("batchId", batchId);
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("updateBatch download Status failed." + ex);
	}

    }

    /**
     * The 'saveOutputRunLog' function will insert the latest run log for the batch.
     *
     * @param log The output run log to save.
     */
    @Override
    @Transactional(readOnly = false)
    public void saveOutputRunLog(targetOutputRunLogs log) throws Exception {
	sessionFactory.getCurrentSession().save(log);
    }

    /**
     * The 'targetOutputRunLogs' function will return the latest output run log for the passed in utConfiguration Id
     *
     * @param configId = The utConfiguration to find the latest log.
     *
     * @return This function will return the latest log
     */
    @Override
    @Transactional(readOnly = true)
    public List<targetOutputRunLogs> getLatestRunLog(int configId) throws Exception {

	Criteria latestLogQuery = sessionFactory.getCurrentSession().createCriteria(targetOutputRunLogs.class);
	latestLogQuery.add(Restrictions.eq("configId", configId));
	latestLogQuery.addOrder(Order.desc("lastRunTime"));

	return latestLogQuery.list();

    }

    @Override
    @Transactional(readOnly = false)
    public Integer writeOutputToTextFile(configurationTransport transportDetails, Integer batchDownloadId, String filePathAndName, String fieldNos) {

	String sql = "";

	//If file type == JSON
	if (transportDetails.getfileType() == 12) {
	    sql = ("call getJSONForConfig(:configId, :batchDownloadId, :filePathAndName, :jsonWrapperElement);");
	} 
	else {
           
	    //we use utConfiguration info 
	    //build this sql
	    sql = "SELECT " + fieldNos + " "
		+ "FROM transactionTranslatedOut_" + batchDownloadId + " "
		+ "where statusId = 9 and configId = :configId "
		+ "INTO OUTFILE  '" + filePathAndName + "' "
		+ "FIELDS TERMINATED BY '" + transportDetails.getDelimChar()+"' LINES TERMINATED BY '\\n';";
	}

	if (!"".equals(sql)) {
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.setParameter("configId", transportDetails.getconfigId());

	    if (transportDetails.getfileType() == 12) {
		query.setParameter("filePathAndName", filePathAndName);
		query.setParameter("jsonWrapperElement", transportDetails.getJsonWrapperElement());
	    }
           
            try {
		query.list();
	    } catch (Exception ex) {
		System.out.println(ex.getMessage());
	    }
	}

	return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = false)
    public List<ConfigOutboundForInsert> setConfigOutboundForInsert(
	    int configId, int batchDownloadId) throws Exception {
	String sql = ("call setSqlForOutboundConfig(:configId, :batchDownloadId);");
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.addScalar("batchDownloadId", StandardBasicTypes.INTEGER)
		.addScalar("fieldNos", StandardBasicTypes.STRING)
		.addScalar("saveToCols", StandardBasicTypes.STRING)
		.addScalar("saveToTableName", StandardBasicTypes.STRING)
		.addScalar("selectFields", StandardBasicTypes.STRING)
		.addScalar("updateFields", StandardBasicTypes.STRING)
		.addScalar("configId", StandardBasicTypes.INTEGER)
		.setResultTransformer(
			Transformers.aliasToBean(ConfigOutboundForInsert.class))
		.setParameter("configId", configId)
		.setParameter("batchDownloadId", batchDownloadId);

	List<ConfigOutboundForInsert> configOutboundForInsertList = query.list();

	return configOutboundForInsertList;
    }

    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = false)
    public String getConfigFieldsForOutput(Integer configId) throws Exception {
	String sql = ""
		+ "select group_concat('REPLACE(REPLACE(ifnull(F', fieldNo, ',\"\") , ''\\n'', ''''), ''\\r'', '''')') as fieldNos "
		+ " from configurationFormFields where configId = :configId order by fieldNo";
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	query.setParameter("configId", configId);
	List<String> fieldNos = query.list();
	if (query.list().size() == 1) {
	    return fieldNos.get(0);
	} else {
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void setSessionLength() throws Exception {
	Query query1 = sessionFactory.getCurrentSession().createSQLQuery("SET SESSION group_concat_max_len = 9999999;");
	query1.executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public BigInteger getRejectedCount(String fromDate, String toDate) throws Exception {

	String sql = "select count(id) as totalReferrals "
		+ "from batchdownloads "
		+ "where (dateCreated >= '" + fromDate + "' and dateCreated < '" + toDate + "')  "
		+ "and statusId = 41";

	Query getRejectedCount = sessionFactory.getCurrentSession().createSQLQuery(sql);

	return (BigInteger) getRejectedCount.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public String getCustomXMLFieldsForOutput(Integer configId) throws Exception {
	String sql = "select group_concat('REPLACE(REPLACE(ifnull(F', fieldValue, ',\"\") , ''\\n'', ''''), ''\\r'', '''')') as fieldNos "
		+ " from configurationccdelements where configId = :configId order by id asc";
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	query.setParameter("configId", configId);
	List<String> fieldNos = query.list();
	if (query.list().size() == 1) {
	    return fieldNos.get(0);
	} else {
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = true)
    public List getOutputForCustomTargetFile(configurationTransport transportDetails, Integer batchDownloadId, String fieldNos) {
	
	String sql = "SELECT " + fieldNos
		+ " FROM transactiontranslatedout_"+batchDownloadId + " "
		+ "where statusId = 9 and configId = :configId";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	query.setParameter("configId", transportDetails.getconfigId());
	try {
	    return query.list();
	} catch (Exception ex) {
	    System.out.println(ex.getMessage());
	    return null;
	}
    }

    
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getBatchesByStatusIdsAndDate(Date fromDate,
	    Date toDate, Integer fetchSize, List<Integer> statusIds)
	    throws Exception {
	String sql = "select case when orgName is null then 'No Org Match' else orgName end orgName, "
		+ " a.dateCreated, transportMethod, utBatchName, a.statusId, b.displayCode as statusValue"
		+ " from batchdownloads a left join (select id as statusId, displayCode from lu_processstatus  "
		+ " where id in (:statusIds)) b on a.statusId = b.statusId "
		+ " left join organizations c on a.orgId = c.id "
		+ " left join ref_transportmethods d on d.id = a.transportmethodId "
		+ " where  a.statusId in (:statusIds)";
	if (!"".equals(fromDate) || (!"".equals(toDate))) {
	    sql = sql + " and ";

	    if (!"".equals(fromDate)) {
		sql = sql + " a.dateCreated >= :fromDate  ";
	    }

	    if (!"".equals(fromDate) && (!"".equals(toDate))) {
		sql = sql + " and  ";
	    }
	    if (!"".equals(toDate)) {
		sql = sql + " a.dateCreated <= :toDate  ";
	    }
	}
	sql = sql + " order by dateCreated desc ";

	if (fetchSize > 0) {
	    sql = sql + " limit " + fetchSize;
	}

	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
		Transformers.aliasToBean(batchDownloads.class));

	if (!"".equals(fromDate)) {
	    query.setParameter("fromDate", mysqlDateFormat.format(fromDate));

	}

	if (!"".equals(toDate)) {
	    query.setParameter("toDate", mysqlDateFormat.format(toDate));
	}

	query.setParameterList("statusIds", statusIds);

	List<batchDownloads> batchList = query.list();

	return batchList;
    }

    @Override
    @Transactional(readOnly = false)
    public Integer insertRestApiMessage(RestAPIMessagesOut APIMessageOut) throws Exception {
	Integer newRestAPIMessageId = null;

	newRestAPIMessageId = (Integer) sessionFactory.getCurrentSession().save(APIMessageOut);

	return newRestAPIMessageId;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getDLBatchesByStatusIds(List<Integer> statusIds) throws Exception {
	Criteria dlBatches = sessionFactory.getCurrentSession().createCriteria(batchDownloads.class);
	dlBatches.add(Restrictions.in("statusId", statusIds));
	List<batchDownloads> dlBatchesList = dlBatches.list();
	return dlBatchesList;
    }

    @Override
    @Transactional(readOnly = true)
    public batchDLRetry getBatchDLRetryByDownloadId(Integer batchDownloadId,Integer statusId) throws Exception {
	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(batchDLRetry.class);
	criteria.add(Restrictions.eq("batchDownloadId", batchDownloadId));
	if (statusId > 0) {
	    criteria.add(Restrictions.eq("fromStatusId", statusId));
	}
	criteria.addOrder(Order.desc("dateCreated"));
	List<batchDLRetry> brList = criteria.list();

	if (brList.size() > 0) {
	    return brList.get(0);
	} else {
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void saveBatchDLRetry(batchDLRetry br) throws Exception {
	sessionFactory.getCurrentSession().save(br);

    }

    @Override
    @Transactional(readOnly = false)
    public void clearBatchDLRetry(Integer batchDownloadId) throws Exception {
	Query delBatch = sessionFactory.getCurrentSession().createQuery("delete from batchDLRetry where batchDownloadId = :batchDownloadId");
	delBatch.setParameter("batchDownloadId", batchDownloadId);
	delBatch.executeUpdate();

    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRestAPIMessageByDownloadId(int batchDownloadId)
	    throws Exception {
	Query deletNote = sessionFactory.getCurrentSession().createQuery("delete from RestAPIMessagesOut where batchDownloadId = :batchDownloadId");
	deletNote.setParameter("batchDownloadId", batchDownloadId);
	deletNote.executeUpdate();
    }
   
    @Override
    @Transactional(readOnly = false)
    public void createTargetBatchTables(Integer batchDownloadId, Integer configId) throws Exception {
	try {

	    List<configurationFormFields> configFormFields = configurationTransportManager.getConfigurationFields(configId, 0);

	    Integer totalFields = 50;

	    if (configFormFields != null) {
		if (!configFormFields.isEmpty()) {
		    totalFields = configFormFields.size() + 10;
		}
	    }

	    //Create the transactionoutrecords_batchDownloadId table
	    String transactionOutRecordsTable = "DROP TABLE IF EXISTS `transactionoutrecords_" + batchDownloadId + "`; CREATE TABLE `transactionoutrecords_" + batchDownloadId + "` (";
		
	    for (int i = 1; i <= totalFields; i++) {
		transactionOutRecordsTable += "F" + i + " text,";
	    }

	    transactionOutRecordsTable += "id int(11) NOT NULL AUTO_INCREMENT," 
		    + "batchDownloadId int(11) DEFAULT NULL," 
		    + "configId int(11) NOT NULL,"
		    + "dateCreated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
		    + "PRIMARY KEY (`id`),"
		    + "KEY `torFK_idx` (`batchDownloadId`)"
		    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    Query query = sessionFactory.getCurrentSession().createSQLQuery(transactionOutRecordsTable);
	    query.executeUpdate();

	    //Create the transactiontranslatedout_batchDownloadId table
	    String transactionTranslatedOutTable = "DROP TABLE IF EXISTS `transactiontranslatedout_" + batchDownloadId + "`; CREATE TABLE `transactiontranslatedout_" + batchDownloadId + "` ("
		    + "id int(11) NOT NULL AUTO_INCREMENT,"
		    + "transactionOutRecordsId int(11) NOT NULL,"
		    + "configId int(11) NOT NULL,"
		    + "batchDownloadId int(11) DEFAULT NULL,"
		    + "statusId int(11) DEFAULT NULL,"
		    + "dateCreated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
		    + "forCW text,";

	    for (int i = 1; i <= totalFields; i++) {
		transactionTranslatedOutTable += "F" + i + " text,";
	    }

	    transactionTranslatedOutTable += "PRIMARY KEY (`id`),"
		    + "UNIQUE KEY `transactionOutRecordsId_UNIQUE` (`transactionOutRecordsId`),"
		    + "KEY `ttoConfigId_idx` (`configId`,`batchDownloadId`),"
		    + "KEY `ttobatchId` (`batchDownloadId`)"
		    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createSQLQuery(transactionTranslatedOutTable);
	    query.executeUpdate();

	    //Create the transactionouterrors_batchDownloadId table
	    String transactionOutErrorsTable = "DROP TABLE IF EXISTS `transactionouterrors_" + batchDownloadId + "`; CREATE TABLE `transactionouterrors_" + batchDownloadId + "` ("
		    + "`id` int(11) NOT NULL AUTO_INCREMENT,"
		    + "`batchDownloadId` int(11) NOT NULL,"
		    + "`configId` int(11) DEFAULT NULL,"
		    + "`transactionOutRecordsId` int(11) DEFAULT NULL,"
		    + "`fieldNo` int(11) DEFAULT NULL,"
		    + "`fieldLabel` varchar(255) DEFAULT NULL,"
		    + "`fieldValue` varchar(255) DEFAULT NULL,"
		    + "`required` bit(1) DEFAULT NULL,"
		    + "`errorId` int(11) DEFAULT NULL,"
		    + "`cwId` int(11) DEFAULT NULL,"
		    + "`macroId` int(11) DEFAULT NULL,"
		    + "`validationTypeId` int(11) DEFAULT NULL,"
		    + "`stackTrace` text,"
		    + "`dateCreated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
		    + "PRIMARY KEY (`id`),"
		    + "KEY `batchDownloadIdFK_idx` (`batchDownloadId`),"
		    + "KEY `configFFFKId_idx` (`fieldNo`),"
		    + "KEY `errorIdFK_idx` (`errorId`),"
		    + "KEY `toeVTFK_idx` (`validationTypeId`),"
		    + "KEY `toeMacroFK_idx` (`macroId`),"
		    + "KEY `toeCWIDFK_idx` (`cwId`),"
		    + "KEY `transOutId` (`transactionOutRecordsId`),"
		    + "CONSTRAINT `batchDownloadId_"+batchDownloadId+"_FK` FOREIGN KEY (`batchDownloadId`) REFERENCES `batchdownloads` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
		    + "CONSTRAINT `toeCWID_"+batchDownloadId+"_FK` FOREIGN KEY (`cwId`) REFERENCES `crosswalks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
		    + "CONSTRAINT `toeMacro_"+batchDownloadId+"_FK` FOREIGN KEY (`macroId`) REFERENCES `macro_names` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
		    + "CONSTRAINT `toeVT_"+batchDownloadId+"_FK` FOREIGN KEY (`validationTypeId`) REFERENCES `ref_validationtypes` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
		    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createSQLQuery(transactionOutErrorsTable);
	    query.executeUpdate();

	    //Create the transactiontranslatedlistout_batchDownloadId table
	    String transactionTranslatedListOutTable = "DROP TABLE IF EXISTS `transactiontranslatedlistout_" + batchDownloadId + "`; CREATE TABLE `transactiontranslatedlistout_" + batchDownloadId + "` ("
		    + "`id` int(11) NOT NULL AUTO_INCREMENT,"
		    + "`batchDownloadId` int(11) NOT NULL,"
		    + "`transactionOutRecordsId` int(11) NOT NULL,"
		    + "`concatKey` varchar(75) DEFAULT NULL,"
		    + "`inValue` text,"
		    + "`translatedValue` text,"
		    + "`translateIdToKeep` int(11) DEFAULT NULL,"
		    + "`fCol` int(11) DEFAULT NULL,"
		    + "`srcField` text,"
		    + "`fieldA` text,"
		    + "`fieldB` text,"
		    + "PRIMARY KEY (`id`),"
		    + "KEY `outConcatKey` (`concatKey`)"
		    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createSQLQuery(transactionTranslatedListOutTable);
	    query.executeUpdate();
	    
	    //Create the transactionoutjsontable_batchDownloadId table
	    String transactionOutJSONTable = "DROP TABLE IF EXISTS `transactionoutjsontable_" + batchDownloadId + "`; CREATE TABLE `transactionoutjsontable_" + batchDownloadId + "` ("
		    + "`id` int(11) NOT NULL AUTO_INCREMENT,"
		    + "`batchDownloadId` int(11) NOT NULL,"
		    + "`transactionOutRecordsId` int(11) NOT NULL,"
		    + "`configId` int(11) DEFAULT NULL,"
		    + "`jsonString` text,"
		    + "`dateCreated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
		    + " PRIMARY KEY (`id`),"
		    + " KEY `ttojsonBatchId` (`batchDownloadId`),"
		    + " KEY `ttoJsonConfigId` (`configId`)"
		    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createSQLQuery(transactionOutJSONTable);
	    query.executeUpdate();
	    
	    //Create the transactionoutdetailauditerrors_batchUploadId table
	    String transactionoutdetailauditerrorsTable = "DROP TABLE IF EXISTS `transactionoutdetailauditerrors_" + batchDownloadId + "`; CREATE TABLE `transactionoutdetailauditerrors_" + batchDownloadId + "` (" 
		    + "`id` int(11) NOT NULL AUTO_INCREMENT," 
		    + "`batchDownloadId` int(11) NOT NULL," 
		    + "`configId` int(11) NOT NULL," 
		    + "`transactionOutRecordsId` int(11) NOT NULL," 
		    + "`fieldNo` int(11) NOT NULL," 
		    + "`fieldName` varchar(45) DEFAULT NULL," 
		    + "`errorId` int(11) NOT NULL," 
		    + "`errorDetails` varchar(200) DEFAULT NULL COMMENT 'This field is used to update cw name, validation type name, macro name'," 
		    + "`errorData` text," 
		    + "`reportField1Data` varchar(45) DEFAULT NULL," 
		    + "`reportField2Data` varchar(45) DEFAULT NULL," 
		    + "`reportField3Data` varchar(45) DEFAULT NULL," 
		    + "`reportField4Data` varchar(45) DEFAULT NULL," 
		    + "`transactionOutErrorId` int(11) DEFAULT '0'," 
		    + " PRIMARY KEY (`id`)," 
		    + " KEY `ttoauditKeyError_idx` (`batchDownloadId`)," 
		    + " CONSTRAINT `ttoauditErrorKey_"+batchDownloadId+"_FK` FOREIGN KEY (`batchDownloadId`) REFERENCES `batchDownloads` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION" 
		    +") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createSQLQuery(transactionoutdetailauditerrorsTable);
	    query.executeUpdate();
	    

	} catch (Exception ex) {
	    System.err.println("Create Batch Download tables for batch (Id: " + batchDownloadId +") "+ ex.getCause());
	}
    }
    
    @Override
    @Transactional(readOnly = false)
    public void loadTargetBatchTables(Integer batchDownloadId, Integer batchUploadId, Integer configId, Integer uploadConfigId) throws Exception {
	
	List<configurationFormFields> configFormFields = configurationTransportManager.getConfigurationFieldsToCopy(configId);
	
	List<configurationFormFields> uploadconfigFormFields = configurationTransportManager.getConfigurationFieldsToCopy(uploadConfigId);

	Integer totalFields = 50;
	
	if (uploadconfigFormFields != null) {
	    if (!uploadconfigFormFields.isEmpty()) {
		totalFields = uploadconfigFormFields.size() + 10;
	    }
	}
        
        //Need to create the temp translated in table
        String temptransactionTranslatedInTable = "DROP TABLE IF EXISTS `temp_transactiontranslatedin_" + batchUploadId + "`; CREATE TABLE `temp_transactiontranslatedin_" + batchUploadId + "` ("
            + "id int(11) NOT NULL AUTO_INCREMENT,"
            + "transactionInRecordsId int(11) NOT NULL,"
            + "configId int(11) NOT NULL,"
            + "batchUploadId int(11) DEFAULT NULL,"
            + "statusId int(11) DEFAULT NULL,"
            + "dateCreated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "forCW text,";

        for (int i = 1; i <= totalFields; i++) {
            temptransactionTranslatedInTable += "F" + i + " text,";
        }

        temptransactionTranslatedInTable += "PRIMARY KEY (`id`),"
                + "UNIQUE KEY `temp_transactionInRecordsId_UNIQUE` (`transactionInRecordsId`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

        Query query = sessionFactory.getCurrentSession().createSQLQuery(temptransactionTranslatedInTable);
        query.executeUpdate();

	//Need to copy transaction in tables into temp table
	String sqlinsert = "INSERT INTO temp_transactiontranslatedin_"+batchUploadId+" "
	+ "(id,transactionInRecordsId,configId,statusId,";

	for (int i = 1; i <= totalFields; i++) {
	    sqlinsert += "F" + i + ",";
	}

	sqlinsert += "batchUploadId) SELECT id,transactionInRecordsId,configId,statusId,";
	for (int i = 1; i <= totalFields; i++) {
	    sqlinsert += "F" + i + ",";
	}
	sqlinsert+= "batchUploadId from transactiontranslatedin_"+batchUploadId;
        
	query = sessionFactory.getCurrentSession().createSQLQuery(sqlinsert);
	query.executeUpdate();
	
	if(uploadconfigFormFields != null) {
	    if(!uploadconfigFormFields.isEmpty()) {
		boolean changesMade = false;

		String sqlUpdateMapped = "";

		for(configurationFormFields formFields : uploadconfigFormFields) {
		    if(formFields.getFieldNo() != formFields.getMappedToField()) {
			changesMade = true;
			sqlUpdateMapped += "update temp_transactiontranslatedin_" + batchUploadId + " set F"+formFields.getMappedToField()+" = (select F"+formFields.getFieldNo()+" from transactiontranslatedin_"+batchUploadId+" where id = temp_transactiontranslatedin_"+batchUploadId+".id);";
		    }
		}

		if(changesMade) {
		    query = sessionFactory.getCurrentSession().createSQLQuery(sqlUpdateMapped);
		    query.executeUpdate();
		}
	    }
	}
	    
	totalFields = 50;
	Integer extraFields = 10;

	if (configFormFields != null) {
	    if (!configFormFields.isEmpty()) {
		totalFields = configFormFields.size() + extraFields;
	    }
	}
	
	Integer extraTargetFields = (totalFields-uploadconfigFormFields.size());

	try {
	    
	    String sql = "insert into transactionoutrecords_"+batchDownloadId+" "
	    + "(";

	    for (int i = 1; i <= totalFields; i++) {
		sql += "F" + i + ",";
	    }

	    sql+= "batchDownloadId, configId) ";

	    sql+= "select ";

	    for(configurationFormFields formField : configFormFields) {
		sql += "F" + formField.getMappedToField() + ",";
	    }
	    
	    for (int i = 1; i <= extraFields; i++) {
		sql += "null,";
	    }

	    sql+= batchDownloadId + ","+configId +" from temp_transactiontranslatedin_"+batchUploadId+" where statusId = 9;";
	    
	    query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.executeUpdate();
	    
	    
	    sql = "insert into transactiontranslatedout_"+batchDownloadId+" "
	    + "(statusId, configId, transactionOutRecordsId,";

	    for (int i = 1; i <= totalFields; i++) {
		sql += "F" + i + ",";
	    }

	    sql+= "batchDownloadId)";

	    sql+= "select 9, configId, id, ";

	    for (int i = 1; i <= totalFields; i++) {
		sql += "F" + i + ",";
	    }

	    sql+= "batchDownloadId from transactionoutrecords_"+batchDownloadId+";";
	    
	    query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.executeUpdate();
            
            //Delete the temp table
            sql = "DROP TABLE IF EXISTS `temp_transactiontranslatedin_" + batchUploadId + "`;";
            query = sessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
	    
	} catch (Exception ex) {
	    System.err.println("loadTransactionTranslatedIn for Batch (id: "+batchUploadId+") "+ ex.getCause());
	}
	
    }
    
    @Override
    @Transactional(readOnly = false)
    public void deleteBatchUploadTables(Integer batchId) throws Exception {

	/* Delete all the stored records */
	String deleteSQL = "";
	Query deleteQuery;
	deleteSQL += "DROP TABLE IF EXISTS `transactionindetailauditerrors_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedlistin_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactioninrecords_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactioninerrors_" + batchId + "`;";

	deleteQuery = sessionFactory.getCurrentSession().createSQLQuery(deleteSQL);
	deleteQuery.executeUpdate();

    }
    
    @Override
    @Transactional(readOnly = false)
    public Integer clearBatchTransactionTables(Integer batchDownloadId) {
	
	String clearSQL = "";
	clearSQL += "truncate TABLE `transactionoutdetailauditerrors_" + batchDownloadId + "`;";
	clearSQL += "truncate TABLE `transactionouterrors_" + batchDownloadId + "`;";
	clearSQL += "truncate TABLE `transactiontranslatedlistout_" + batchDownloadId + "`;";
	clearSQL += "truncate TABLE `transactiontranslatedout_" + batchDownloadId + "`;";
	clearSQL += "delete from `transactionoutrecords_" + batchDownloadId + "`;";
	
	Query clearData = sessionFactory.getCurrentSession().createSQLQuery(clearSQL);

	try {
	    clearData.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    System.err.println("clearBatchTransactionTables_" + batchDownloadId + " " + ex.getCause());
	    return 1;
	}
    }

     /**
     * errorId = 1 is required field missing* we do not re-check REL records
     */
    @Override
    @Transactional(readOnly = false)
    public Integer insertFailedRequiredFields(configurationFormFields cff, Integer batchDownloadId) {
	
	try {
	    
	    String sql = "insert into transactionouterrors_"+batchDownloadId
		+ " (batchDownloadId, configId, transactionOutRecordsId, fieldNo, errorid) "
		+ "select " + batchDownloadId + ", " + cff.getconfigId() + ", transactionOutRecordsId, " + cff.getFieldNo()
		+ ",1 from transactiontranslatedout_"+batchDownloadId + " where configId = :configId "
		+ "and (F" + cff.getFieldNo()+" is null "
		+ "or length(trim(F" + cff.getFieldNo() + ")) = 0 "
		+ "or length(REPLACE(REPLACE(F" + cff.getFieldNo() + ", '\n', ''), '\r', '')) = 0) "
		+ "and configId = :configId and (statusId is null or statusId not in (:transRELId));";
	    
	    Query insertData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("configId", cff.getconfigId())
		.setParameterList("transRELId", transRELId);
	    
	    insertData.executeUpdate();
	    return 0;
	    
	} catch (Exception ex) {
	    System.err.println("insertFailedRequiredFields  failed for outbound batch - " + batchDownloadId + " " + ex.getCause());
	    ex.printStackTrace();
	    
	    return 1;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBatchDownloadTables(Integer batchId) throws Exception {

	/* Delete all the stored records */
	String deleteSQL = "";
	Query deleteQuery;
	deleteSQL += "DROP TABLE IF EXISTS `transactionoutdetailauditerrors_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedlistout_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactionoutrecords_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactionouterrors_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactionoutjsontable_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedout_" + batchId + "`;";

	deleteQuery = sessionFactory.getCurrentSession().createSQLQuery(deleteSQL);
	deleteQuery.executeUpdate();

    }
    
    
    @Override
    @Transactional(readOnly = false)
    public void deleteBatchDownloadTablesByBatchUpload(List<batchDownloads> batchDownloads) throws Exception {

	/* Delete all the stored records */
	String deleteSQL = "";
	Query deleteQuery;
	
	if(batchDownloads != null) {
	    if(!batchDownloads.isEmpty()) {
		for(batchDownloads batch : batchDownloads) {
		    deleteSQL += "DROP TABLE IF EXISTS `transactionoutdetailauditerrors_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedlistout_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactionoutrecords_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactionouterrors_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactionoutjsontable_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedout_" + batch.getId() + "`;";
		}
		
		if(!"".equals(deleteSQL)) {
		    deleteQuery = sessionFactory.getCurrentSession().createSQLQuery(deleteSQL);
		    deleteQuery.executeUpdate();
		}
	    }
	}
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getDLBatchesByBatchUploadId(Integer batchUploadId) throws Exception {
	
	Criteria dlBatches = sessionFactory.getCurrentSession().createCriteria(batchDownloads.class);
	
	dlBatches.add(Restrictions.eq("batchUploadId", batchUploadId));
	
	List<batchDownloads> dlBatchesList = dlBatches.list();
	return dlBatchesList;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getPendingResetBatches(Integer batchUploadId) throws Exception {
	
	Criteria dlBatches = sessionFactory.getCurrentSession().createCriteria(batchDownloads.class);
	
	dlBatches.add(Restrictions.eq("batchUploadId", batchUploadId));
	dlBatches.add(Restrictions.eq("statusId",66));
	
	List<batchDownloads> dlBatchesList = dlBatches.list();
	return dlBatchesList;
    }
    
    /**
     * The 'submitBatchDownloadChanges' function will submit the batch changes.
     *
     * @param batchDownload The object that will hold the new batch info
     *
     * @table batchDownloads
     *
     * @return This function does not return anything
     */
    @Override
    @Transactional(readOnly = false)
    public void submitBatchDownloadChanges(batchDownloads batchDownload) throws Exception {
	sessionFactory.getCurrentSession().update(batchDownload);
    }
}
