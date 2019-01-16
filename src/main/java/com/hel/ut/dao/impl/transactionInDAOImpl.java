/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.dao.impl;

import com.hel.ut.model.activityReportList;
import com.hel.ut.dao.transactionInDAO;
import com.hel.ut.model.CrosswalkData;
import com.hel.ut.model.Macros;
import com.hel.ut.model.MoveFilesLog;
import com.hel.ut.model.RestAPIMessagesIn;
import com.hel.ut.model.Transaction;
import com.hel.ut.model.utUser;
import com.hel.ut.model.utUserActivity;
import com.hel.ut.model.WSMessagesIn;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchRetry;
import com.hel.ut.model.batchUploads;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationConnectionSenders;
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
import com.hel.ut.model.referralActivityExports;
import com.hel.ut.service.sysAdminManager;
import com.hel.ut.service.userManager;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class transactionInDAOImpl implements transactionInDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private sysAdminManager sysAdminManager;

    @Autowired
    private userManager usermanager;

    @Autowired
    private utConfigurationTransportManager configurationtransportmanager;

    //list of final status - these records we skip
    private List<Integer> transRELId = Arrays.asList(11, 12, 13, 16);

    private int processingSysErrorId = 5;

    private SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * The 'getFieldValue' function will return the value saved for the passed in tableName, tableCol and idValue.
     *
     * @param	tableName	The name of the table to query
     * @param tableCol The column name of to return
     * @param idValue The id value of the row to search
     *
     * @return The function will return a String
     */
    @Override
    @Transactional(readOnly = true)
    public String getFieldValue(String tableName, String tableCol, String idCol, int idValue) {

	String sql = ("select " + tableCol + " from " + tableName + " where " + idCol + " = :id");

	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	query.setParameter("id", idValue);

	String tableValue = String.valueOf(query.uniqueResult());

	/* Check if null */
	if (tableValue == null || "null".equals(tableValue)) {
	    tableValue = "";
	} /* Check if date */ 
	else if (tableValue.length() == 10 && tableValue.contains("-")) {
	    try {
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(tableValue);
		tableValue = new SimpleDateFormat("MM/dd/yyyy").format(date);
	    } catch (ParseException ex) {
		Logger.getLogger(transactionInDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	return tableValue;

    }

    /**
     * The 'getFieldSelectOptions' function will return a list of values to populate the field select box.
     *
     * @param fieldId The fieldId to search on
     * @param configId The utConfiguration Id to search on
     *
     * @return The function will return a list of select box options
     */
    @Override
    @Transactional(readOnly = true)
    public List<fieldSelectOptions> getFieldSelectOptions(int fieldId, int configId) {

	List<fieldSelectOptions> fieldSelectOptions = new ArrayList<fieldSelectOptions>();

	Query findCrosswalks = sessionFactory.getCurrentSession().createSQLQuery("SELECT crosswalkId, id, defaultValue FROM configurationDataTranslations where configId = :configId and fieldId = :fieldId");
	findCrosswalks.setParameter("configId", configId);
	findCrosswalks.setParameter("fieldId", fieldId);
	List crosswalks = findCrosswalks.list();

	Iterator it = crosswalks.iterator();
	int crosswalkId;
	String optionDesc;
	String optionValue;
	String defaultValue;
	fieldSelectOptions fieldOptions = null;

	while (it.hasNext()) {
	    Object row[] = (Object[]) it.next();
	    crosswalkId = (Integer) row[0];
	    defaultValue = (String) row[2];

	    Query crosswalkData = sessionFactory.getCurrentSession().createSQLQuery("SELECT sourceValue, descValue FROM rel_crosswalkData where crosswalkId = :crosswalkId");
	    crosswalkData.setParameter("crosswalkId", crosswalkId);
	    List crosswalkDataList = crosswalkData.list();

	    Iterator cwDataIt = crosswalkDataList.iterator();
	    while (cwDataIt.hasNext()) {
		Object cwDatarow[] = (Object[]) cwDataIt.next();
		optionDesc = (String) cwDatarow[1];
		optionValue = (String) cwDatarow[0];

		fieldOptions = new fieldSelectOptions();
		fieldOptions.setoptionDesc(optionDesc);
		fieldOptions.setoptionValue(optionValue);
		fieldOptions.setDefaultValue(defaultValue);
		fieldSelectOptions.add(fieldOptions);
	    }

	}

	return fieldSelectOptions;
    }

    /**
     * The 'submitBatchUpload' function will submit the new batch.
     *
     * @param batchUpload The object that will hold the new batch info
     *
     * @table batchUploads
     *
     * @return This function returns the batchId for the newly inserted batch
     */
    @Override
    @Transactional(readOnly = false)
    public Integer submitBatchUpload(batchUploads batchUpload) throws Exception {

	Integer batchId = null;

	batchId = (Integer) sessionFactory.getCurrentSession().save(batchUpload);

	return batchId;

    }


    /**
     * The 'submitBatchUploadChanges' function will submit the batch changes.
     *
     * @param batchUpload The object that will hold the new batch info
     *
     * @table batchUploads
     *
     * @return This function does not return anything
     */
    @Override
    @Transactional(readOnly = false)
    public void submitBatchUploadChanges(batchUploads batchUpload) throws Exception {
	sessionFactory.getCurrentSession().update(batchUpload);
    }

    
    /**
     * The 'getsentBatches' function will return a list of sent batches for the organization passed in.
     *
     * @param orgId The organization Id to find pending transactions for.
     *
     * @return The function will return a list of sent transactions
     */
    @Override
    @Transactional(readOnly = true)
    public List<batchUploads> getsentBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception {

	return findsentBatches(userId, orgId, 0, 0, fromDate, toDate);
    }

    /**
     * The 'getsentBatchesHistory' function will return a list of sent batches for the organization passed in.
     *
     * @param orgId The organization Id to find pending transactions for.
     *
     * @return The function will return a list of sent transactions
     */
    @Override
    @Transactional(readOnly = true)
    public List<batchUploads> getsentBatchesHistory(int userId, int orgId, int toOrgId, int messageTypeId, Date fromDate, Date toDate) throws Exception {

	return findsentBatches(userId, orgId, toOrgId, messageTypeId, fromDate, toDate);
    }

    /**
     * The 'findsentBatches' function will return a list of sent batches for the organization passed in.
     *
     * @param orgId The organization Id to find pending transactions for.
     *
     * @return The function will return a list of sent transactions
     */
    @Transactional(readOnly = true)
    public List<batchUploads> findsentBatches(int userId, int orgId, int toOrgId, int messageTypeId, Date fromDate, Date toDate) throws Exception {

	/* Get a list of connections the user has access to */
	Criteria connections = sessionFactory.getCurrentSession().createCriteria(configurationConnectionSenders.class);
	connections.add(Restrictions.eq("userId", userId));
	List<configurationConnectionSenders> userConnections = connections.list();

	List<Integer> messageTypeList = new ArrayList<Integer>();
	List<Integer> targetOrgList = new ArrayList<Integer>();

	if (userConnections.isEmpty()) {
	    messageTypeList.add(0);
	    targetOrgList.add(0);
	} else {

	    for (configurationConnectionSenders userConnection : userConnections) {
		Criteria connection = sessionFactory.getCurrentSession().createCriteria(configurationConnection.class);
		connection.add(Restrictions.eq("id", userConnection.getConnectionId()));

		configurationConnection connectionInfo = (configurationConnection) connection.uniqueResult();

		/* Get the message type for the utConfiguration */
		Criteria sourceconfigurationQuery = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
		sourceconfigurationQuery.add(Restrictions.eq("id", connectionInfo.getsourceConfigId()));

		utConfiguration configDetails = (utConfiguration) sourceconfigurationQuery.uniqueResult();

		/* Add the message type to the message type list */
		if (messageTypeId == 0) {
		    messageTypeList.add(configDetails.getMessageTypeId());
		} else if (messageTypeId == configDetails.getMessageTypeId()) {
		    messageTypeList.add(configDetails.getMessageTypeId());
		}

		/* Get the list of target orgs */
		Criteria targetconfigurationQuery = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
		targetconfigurationQuery.add(Restrictions.eq("id", connectionInfo.gettargetConfigId()));
		utConfiguration targetconfigDetails = (utConfiguration) targetconfigurationQuery.uniqueResult();

		/* Add the target org to the target organization list */
		if (toOrgId == 0) {
		    targetOrgList.add(targetconfigDetails.getorgId());
		} else if (toOrgId == targetconfigDetails.getorgId()) {
		    targetOrgList.add(targetconfigDetails.getorgId());
		}
	    }
	}


	Criteria findBatches = sessionFactory.getCurrentSession().createCriteria(batchUploads.class);
	findBatches.add(Restrictions.or(
		Restrictions.eq("statusId", 4), /* Submission Being Processed */
		Restrictions.eq("statusId", 22), /* Submission Delivery Locked */
		Restrictions.eq("statusId", 23), /* Submission Delivery Completed */
		Restrictions.eq("statusId", 24), /* Submission Processing Completed */
		Restrictions.eq("statusId", 25), /* Target Batch Creation in process */
		Restrictions.eq("statusId", 28), /* Target Batch Creation in process */
		Restrictions.eq("statusId", 29), /* Submission Processed Errored */
		Restrictions.eq("statusId", 30), /* Target Creation Errored */
		Restrictions.eq("statusId", 32) /* Submission Cancelled */
	)
	);

	if (!"".equals(fromDate)) {
	    findBatches.add(Restrictions.ge("dateSubmitted", fromDate));
	}

	if (!"".equals(toDate)) {
	    findBatches.add(Restrictions.lt("dateSubmitted", toDate));
	}

	findBatches.addOrder(Order.desc("dateSubmitted"));

	return findBatches.list();
    }

    /**
     * The 'getBatchDetails' function will return the batch details for the passed in batch id.
     *
     * @param batchId The id of the batch to return.
     */
    @Override
    @Transactional(readOnly = true)
    public batchUploads getBatchDetails(int batchId) throws Exception {
	return (batchUploads) sessionFactory.getCurrentSession().get(batchUploads.class, batchId);
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
    public batchUploads getBatchDetailsByBatchName(String batchName) throws Exception {
	Query query = sessionFactory.getCurrentSession().createQuery("from batchUploads where utBatchName = :batchName");
	query.setParameter("batchName", batchName);

	if (query.list().size() > 1) {
	    return null;
	} else {
	    return (batchUploads) query.uniqueResult();
	}

    }

    @Override
    @Transactional(readOnly = false)
    @SuppressWarnings("unchecked")
    public List<ConfigForInsert> setConfigForInsert(int configId, int batchUploadId) {
	String sql = ("call setSqlForConfig(:id, :batchUploadId);");
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.addScalar("saveToTableName", StandardBasicTypes.STRING)
		.addScalar("saveToTableCol", StandardBasicTypes.STRING)
		.addScalar("batchUploadId", StandardBasicTypes.INTEGER)
		.addScalar("configId", StandardBasicTypes.INTEGER)
		.addScalar("singleValueFields", StandardBasicTypes.STRING)
		.addScalar("splitFields", StandardBasicTypes.STRING)
		.addScalar("checkForDelim", StandardBasicTypes.STRING)
		.setResultTransformer(
			Transformers.aliasToBean(ConfigForInsert.class))
		.setParameter("id", configId)
		.setParameter("batchUploadId", batchUploadId);

	List<ConfigForInsert> configListForInsert = query.list();

	return configListForInsert;
    }

    /**
     * The 'getuploadedBatches' function will return a list of batches that were uploaded by the logged in user.
     *
     * @param userId The id of the logged in user
     * @param orgId The id of the organization the logged in user belongs to
     *
     * @return This function will return a list of batches.
     *
     * added the ability to exclude selected statusIds. Original method only exclude statusId of 1 for uploadBatch
     */
    @Override
    @Transactional(readOnly = true)
    public List<batchUploads> getuploadedBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception {
	return getuploadedBatches(userId, orgId, fromDate, toDate, Arrays.asList(1));
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<batchUploads> getuploadedBatches(int userId, int orgId, Date fromDate, Date toDate, List<Integer> excludedStatusIds) throws Exception {

	/* Get a list of connections the user has access to */
	Criteria connections = sessionFactory.getCurrentSession().createCriteria(configurationConnectionSenders.class);
	connections.add(Restrictions.eq("userId", userId));
	List<configurationConnectionSenders> userConnections = connections.list();

	List<Integer> configIdList = new ArrayList<Integer>();

	if (userConnections.isEmpty()) {
	    configIdList.add(0);
	} else {

	    for (configurationConnectionSenders userConnection : userConnections) {

		Criteria connection = sessionFactory.getCurrentSession().createCriteria(configurationConnection.class);
		connection.add(Restrictions.eq("id", userConnection.getConnectionId()));

		configurationConnection connectionInfo = (configurationConnection) connection.uniqueResult();

		if (!configIdList.contains(connectionInfo.getsourceConfigId())) {
		    configIdList.add(connectionInfo.getsourceConfigId());
		}
	    }
	}
	// multiconfig is 0 so we need to add
	configIdList.add(0);

	List<Integer> batchIdList = new ArrayList<Integer>();

	SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");

	String batchsql = "select bu.*, concat(u.firstName,' ',u.lastName) as usersName, ps.displayCode as statusValue "
		+ "from batchUploads bu "
		+ "inner join lu_processstatus ps on ps.id = bu.statusId "
		+ "inner join users u on u.id = bu.userId "
		+ "where bu.orgId = " + orgId + " "
		+ "and configId in (" + configIdList.toString().replace("[", "").replace("]", "") + ") "
		+ "and transportMethodId != 2 "
		+ "and statusId not in (" + excludedStatusIds.toString().replace("[", "").replace("]", "") + ") ";

	if (!"".equals(fromDate)) {
	    Date date = (Date) dateFormat.parse(fromDate.toString());
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    batchsql += "and dateSubmitted >= '" + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE) + "' ";
	}

	if (!"".equals(toDate)) {
	    Date date = (Date) dateFormat.parse(toDate.toString());
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    batchsql += "and dateSubmitted < '" + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE) + "' ";
	}

	batchsql += "order by dateSubmitted desc";

	Query batchquery = sessionFactory.getCurrentSession().createSQLQuery(batchsql)
		.setResultTransformer(Transformers.aliasToBean(batchUploads.class));

	return batchquery.list();

    }

    @Override
    @Transactional(readOnly = true)
    public List<batchUploads> getAllUploadedBatches(Date fromDate, Date toDate) throws Exception {
	return getAllUploadedBatches(fromDate, toDate, 0, "");
    }

    /**
     * The 'getAllRejectedBatches' function will return a list of batches for the admin in the processing activities section.
     *
     * @param fromDate
     * @param toDate
     * @return This function will return a list of batch uploads
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<batchUploads> getAllRejectedBatches(Date fromDate, Date toDate, Integer fetchSize) throws Exception {


	Criteria findBatches = sessionFactory.getCurrentSession().createCriteria(batchUploads.class);
	findBatches.add(Restrictions.ne("errorRecordCount", 0));

	if (fromDate != null) {
	    if (!"".equals(fromDate)) {
		findBatches.add(Restrictions.ge("dateSubmitted", fromDate));
	    }
	}

	if (toDate != null) {
	    if (!"".equals(toDate)) {
		findBatches.add(Restrictions.lt("dateSubmitted", toDate));
	    }
	}

	findBatches.addOrder(Order.desc("dateSubmitted"));

	if (fetchSize > 0) {
	    findBatches.setMaxResults(fetchSize);
	}

	return findBatches.list();
    }

    /**
     * The 'getAllUploadedBatches' function will return a list of batches for the admin in the processing activities section.
     *
     * @param fromDate
     * @param toDate
     * @param fetchSize
     * @param batchName
     * @return This function will return a list of batch uploads
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<batchUploads> getAllUploadedBatches(Date fromDate, Date toDate, Integer fetchSize, String batchName) throws Exception {

	int firstResult = 0;

	Criteria findBatches = sessionFactory.getCurrentSession().createCriteria(batchUploads.class);
	findBatches.add(Restrictions.ge("totalRecordCount", 0));
	
	if (!"".equals(batchName)) {
	    findBatches.add(Restrictions.eq("utBatchName", batchName));
	}
	else {
	    if (!"".equals(fromDate)) {
		findBatches.add(Restrictions.ge("dateSubmitted", fromDate));
	    }

	    if (!"".equals(toDate)) {
		findBatches.add(Restrictions.lt("dateSubmitted", toDate));
	    }
	}

	findBatches.addOrder(Order.desc("dateSubmitted"));

	if (fetchSize > 0) {
	    findBatches.setMaxResults(fetchSize);
	}
	return findBatches.list();
    }

    

    @Override
    @Transactional(readOnly = false)
    public void updateBatchStatus(Integer batchUploadId, Integer statusId, String timeField) throws Exception {

	String sql = "update batchUploads set statusId = :statusId ";
	if (timeField.equalsIgnoreCase("startover")) {
	    // we reset time
	    sql = sql + ", startDateTime = null, endDateTime = null";
	} else if (!timeField.equalsIgnoreCase("")) {
	    sql = sql + ", " + timeField + " = CURRENT_TIMESTAMP";
	} else {
	    sql = sql + ", startDateTime = CURRENT_TIMESTAMP, endDateTime = CURRENT_TIMESTAMP";
	}
	sql = sql + " where id = :id ";

	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("statusId", statusId)
		.setParameter("id", batchUploadId);
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("updateBatchStatus " + ex.getCause());
	}

    }

    @Override
    @Transactional(readOnly = true)
    public boolean allowBatchClear(Integer batchUploadId) {
	String sql
		= "select count(id) as rowCount from batchUploads where id = :id and statusId in (22,23,1);";
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).addScalar("rowCount", StandardBasicTypes.INTEGER);
	query.setParameter("id", batchUploadId);
	Integer rowCount = (Integer) query.list().get(0);
	if (rowCount == 0) {
	    return true;
	}
	return false;
    }

    @Override
    @Transactional(readOnly = false)
    public Integer clearBatchTransactionTables(Integer batchUploadId, Integer configId) {
	
	String clearSQL = "";
	clearSQL += "delete from batchuploadauditerrors where batchUploadId = " + batchUploadId + ";";
	Query clearData = sessionFactory.getCurrentSession().createSQLQuery(clearSQL);
	
	createBatchTables(batchUploadId, configId);

	try {
	    clearData.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    System.err.println("clearBatchTransactionTables_" + batchUploadId + " " + ex.getCause());
	    return 1;
	}
    }


    /**
     * errorId = 1 is required field missing* we do not re-check REL records
     */
    @Override
    @Transactional(readOnly = false)
    public Integer insertFailedRequiredFields(configurationFormFields cff, Integer batchUploadId) {
	
	try {
	    
	    String sql = "insert into transactioninerrors_"+batchUploadId
		+ " (batchUploadId, configId, transactionInRecordsId, fieldNo, errorid) "
		+ "select " + batchUploadId + ", " + cff.getconfigId() + ", transactionInRecordsId, " + cff.getFieldNo()
		+ ",1 from transactiontranslatedin_"+batchUploadId + " where configId = :configId "
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
	    System.err.println("insertFailedRequiredFields failed for batch - " + batchUploadId + " " + ex.getCause());
	    ex.printStackTrace();
	    
	    return 1;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer genericValidation(configurationFormFields cff, Integer validationTypeId, Integer batchUploadId, String regEx) {

	String sql = "call insertValidationErrors(:vtType, :fieldNo, :batchUploadId, :configId, :transactionId)";

	Query insertError = sessionFactory.getCurrentSession().createSQLQuery(sql);
	insertError.setParameter("vtType", cff.getValidationType());
	insertError.setParameter("fieldNo", cff.getFieldNo());
	insertError.setParameter("batchUploadId", batchUploadId);
	insertError.setParameter("configId", cff.getconfigId());
	insertError.setParameter("transactionId", 0);

	try {
	    insertError.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    System.err.println("genericValidation " + ex.getCause());
	    ex.printStackTrace();
	    insertProcessingError(processingSysErrorId, cff.getconfigId(), batchUploadId, cff.getFieldNo(),
		    null, null, validationTypeId, false, false, ("-" + ex.getCause().toString()));
	    return 1; //we return error count of 1 when error
	}
    }

    /**
     * The 'getFeedbackReportConnection' method will return a list of connections for the clicked feedback report.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Integer> getFeedbackReportConnection(int configId, int targetorgId) {

	Criteria configurationConnections = sessionFactory.getCurrentSession().createCriteria(configurationConnection.class);
	configurationConnections.add(Restrictions.eq("sourceConfigId", configId));
	configurationConnections.addOrder(Order.asc("dateCreated"));

	List<configurationConnection> connections = configurationConnections.list();

	List<Integer> connectionId = new ArrayList<Integer>();

	if (!connections.isEmpty()) {

	    for (configurationConnection connection : connections) {
		Criteria configurations = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
		configurations.add(Restrictions.eq("id", connection.gettargetConfigId()));

		utConfiguration configDetails = (utConfiguration) configurations.uniqueResult();

		if (configDetails.getorgId() == targetorgId) {
		    connectionId.add(connection.getId());
		}

	    }

	}

	return connectionId;

    }

    
    @Override
    @Transactional(readOnly = false)
    public void updateFieldNoWithCWData(Integer configId, Integer batchId, Integer fieldNo, Integer passClear, boolean foroutboundProcessing) {
	
	String inboundOutbound = "Inbound";
	
	try {
	    String sql;
	    Integer id = batchId;

	    if (foroutboundProcessing == false) {
		sql = "update transactiontranslatedin_"+batchId+" "
		    + "JOIN (SELECT id from transactioninrecords_"+batchId+" WHERE configId = :configId) "
		    + "as ti ON transactiontranslatedin_"+batchId+".transactionInRecordsId = ti.id "
		    + "SET transactiontranslatedin_"+batchId+".F" + fieldNo + " = forcw "
		    + "where (statusId is null or statusId not in (:transRELId))";

		if (passClear == 1) {
		    // 1 is pass, we leave original values in fieldNo alone
		    sql += " and forcw is not null;";
		}
	    } 
	    else {
		inboundOutbound = "Outbound";
		
		sql = "update transactiontranslatedout_"+batchId+" "
		    + "JOIN (SELECT id from transactionoutrecords_"+batchId+" WHERE configId = :configId) "
		    + "as ti ON transactiontranslatedout_"+batchId+".transactionOutRecordsId = ti.id "
		    + "SET transactiontranslatedout_"+batchId+".F" + fieldNo + " = forcw "
		    + "where (statusId is null or statusId not in (:transRELId))";

		if (passClear == 1) {
		    // 1 is pass, we leave original values in fieldNo alone
		    sql += " and forcw is not null;";
		}
	    } 

	    Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("configId", configId)
		.setParameterList("transRELId", transRELId);
		
	    updateData.executeUpdate();
	    
	} catch (Exception ex) {
	    System.err.println("updateFieldNoWithCWData for "+inboundOutbound + " batch (Id: " + batchId + ") " + ex.getCause());
	    ex.printStackTrace();
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void flagCWErrors(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) {

	String sql;
	Integer id = batchId;
	String inboundOutbound = "Inbound";

	if (foroutboundProcessing == false) {
	    sql = "insert into transactioninerrors_"+batchId+" (batchUploadId, configId, "
		+ "transactionInRecordsId, fieldNo, errorid, cwId, fieldValue)"
		+ " select " + batchId + ", " + configId + ",a.transactionInRecordsId, " + cdt.getFieldNo()
		+ ", 3,  " + cdt.getCrosswalkId() + ", b.F"+cdt.getFieldNo()+" from transactiontranslatedin_"+batchId+" a "
		+ "inner join transactioninrecords_"+batchId+" b on a.transactionInRecordsId = b.id where "
		+ "a.configId = :configId "
		+ "and (a.F" + cdt.getFieldNo() + " is not null and length(a.F" + cdt.getFieldNo() + ") != 0  and a.forcw is null)"
		+ "and a.transactionInRecordsId in (select id from transactioninrecords_"+batchId+ " "
		+ "where configId = :configId and (statusId is null or statusId not in (:transRELId)));";
	    
	} 
	else {
	    inboundOutbound = "Outbound";
	    
	    sql = "insert into transactionouterrors_"+batchId+" (batchDownloadId, configId, "
		+ "transactionOutRecordsId, fieldNo, errorid, cwId, fieldValue)"
		+ " select " + batchId + ", " + configId + ",a.transactionOutRecordsId, " + cdt.getFieldNo()
		+ ", 3,  " + cdt.getCrosswalkId() + ", b.F"+cdt.getFieldNo()+" from transactiontranslatedout_"+batchId+" a "
		+ "inner join transactionoutrecords_"+batchId+" b on a.transactionOutRecordsId = b.id where "
		+ "a.configId = :configId "
		+ "and (a.F" + cdt.getFieldNo() + " is not null and length(a.F" + cdt.getFieldNo() + ") != 0  and a.forcw is null)"
		+ "and a.transactionOutRecordsId in (select id from transactionoutrecords_"+batchId+ " "
		+ "where configId = :configId and (statusId is null or statusId not in (:transRELId)));";
	} 

	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
	    .setParameter("configId", configId)
	    .setParameterList("transRELId", transRELId);
	
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("flagCWErrors for " + inboundOutbound + " batch (Id: " + batchId + ") " + ex.getCause());
	    ex.printStackTrace();
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer flagMacroErrors(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) {
	
	String inboundOutbound = "Inbound";
	
	try {
	    String sql;
	    Integer id = batchId;
	    if (foroutboundProcessing == false) {
		sql = "insert into transactioninerrors_"+batchId+" (batchUploadId, configId, "
		    + "transactionInRecordsId, fieldNo, errorid, macroId, fieldValue) "
		    + "select " + batchId + ", " + configId + ", a.transactionInRecordsId, " + cdt.getFieldNo()
		    + ", 4, " + cdt.getMacroId() + ",b.F"+cdt.getFieldNo()+" from transactiontranslatedin_"+batchId+" a inner join "
		    + "transactioninrecords_"+batchId+" b on a.transactionInRecordsId = b.id "
		    + "where a.configId = :configId "
		    + "and a.forcw = 'MACRO_ERROR' and (a.statusId is null or a.statusId not in (:transRELId)) "
		    + "and a.transactionInRecordsId in (select id from transactioninrecords_"+batchId
		    + " where configId = :configId);";
	    } 
	    else { 
		sql = "insert into transactionouterrors_"+batchId+" (batchDownloadId, configId, "
		    + "transactionOutRecordsId, fieldNo, errorid, macroId, fieldValue) "
		    + "select " + batchId + ", " + configId + ", a.transactionOutRecordsId, " + cdt.getFieldNo()
		    + ", 4, " + cdt.getMacroId() + ",b.F"+cdt.getFieldNo()+" from transactiontranslatedout_"+batchId+" a inner join "
		    + "transactionoutrecords_"+batchId+" b on a.transactionOutRecordsId = b.id "
		    + "where a.configId = :configId "
		    + "and a.forcw = 'MACRO_ERROR' and (a.statusId is null or a.statusId not in (:transRELId)) "
		    + "and a.transactionOutRecordsId in (select id from transactionoutrecords_"+batchId
		    + " where configId = :configId);";
	    } 

	    Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("configId", configId)
		.setParameterList("transRELId", transRELId);
	    
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    
	    System.err.println("flagMacroErrors for " + inboundOutbound + " batch (Id: " + batchId + ") " + ex.getCause());
	    ex.printStackTrace();
	}
	return 0;
    }


    /**
     * This method looks for fieldA, fieldB, con1 and con2, fieldNo in the configurationDataTranslations and passes it to the formula (SP) stored in Macros
     *
     * All macros will take the following parameter - configId, batchId, srcField, fieldA, fieldB, con1, con2, macroId, foroutboundProcessing, errorId
     *
     * *
     * @param configId
     * @param batchId
     * @param cdt
     * @param foroutboundProcessing
     * @param macro
     * @return
     */
    @Override
    @Transactional(readOnly = false)
    public Integer executeMacro(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing,Macros macro) {

	String inboundOutbound = "Inbound";
	
	if(foroutboundProcessing) {
	    inboundOutbound = "Outbound";
	}
	
	try {
	    
	    String sql = ("CALL " + macro.getFormula() + " (:configId, :batchId, :srcField, "
		    + ":fieldA, :fieldB, :con1, :con2, :macroId, :foroutboundProcessing, :passClear, 0);");

	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.setParameter("configId", configId);
	    query.setParameter("batchId", batchId);
	    query.setParameter("srcField", ("F" + cdt.getFieldNo()));

	    if (!cdt.getFieldA().equalsIgnoreCase("")) {
		query.setParameter("fieldA", ("F" + cdt.getFieldA()));
	    } else {
		query.setParameter("fieldA", ("F" + cdt.getFieldNo()));
	    }
	    query.setParameter("fieldB", ("F" + cdt.getFieldB()));
	    query.setParameter("con1", (cdt.getConstant1()));
	    query.setParameter("con2", cdt.getConstant2());
	    query.setParameter("macroId", cdt.getMacroId());
	    query.setParameter("foroutboundProcessing", foroutboundProcessing);
	    query.setParameter("passClear", cdt.getPassClear());

	    List<String> macroResults = query.list();
	    
	    /**
	     * we return '' with data manipulation macros and we return continue or stop with macros*
	     */
	    if (macroResults.get(0).equalsIgnoreCase("")) {
		return 0;
	    } else if (macroResults.get(0).equalsIgnoreCase("continue")) {
		return 0;
	    } else if (macroResults.get(0).equalsIgnoreCase("stop")) {
		return 1;
	    }
	    
	    return 0;
	} catch (Exception ex) {

	    //insert system error
	    insertProcessingError(processingSysErrorId, configId, batchId, cdt.getFieldNo(),cdt.getMacroId(), null, null,false, foroutboundProcessing, ("executeMacro " + ex.getCause().toString()));
	    System.err.println("executeMacro -"+ macro.getFormula() + " for " + inboundOutbound + " batch (Id: " + batchId + ") " + ex.getCause());
	    ex.printStackTrace();
	    return 1;
	}

    }

    @Override
    @Transactional(readOnly = false)
    public void insertProcessingError(Integer errorId, Integer configId, Integer batchId,
	    Integer fieldNo, Integer macroId, Integer cwId, Integer validationTypeId,
	    boolean required, boolean foroutboundProcessing, String stackTrace) {
	insertProcessingError(errorId, configId, batchId,
		fieldNo, macroId, cwId, validationTypeId,
		required, foroutboundProcessing, stackTrace, null);

    }

    @Override
    @Transactional(readOnly = false)
    public void insertProcessingError(Integer errorId, Integer configId, Integer batchId,
	    Integer fieldNo, Integer macroId, Integer cwId, Integer validationTypeId,
	    boolean required, boolean foroutboundProcessing, String stackTrace, Integer transactionId) {

	String tableName = "transactionInErrors_" + batchId;
	String batchType = "batchUploadId";
	String transactionColName = "transactionInRecordsId";

	if (foroutboundProcessing) {
	    tableName = "transactionOutErrors_" + batchId;
	    batchType = "batchDownloadId";
	    transactionColName = "transactionOutRecordsId";
	}
	String sql = " INSERT INTO " + tableName + " (errorId, " + batchType + ", configId, "
		+ "fieldNo, required,  "
		+ "cwId, macroId, validationTypeId, stackTrace, " + transactionColName + ") "
		+ "VALUES (:errorId,:batchId,:configId,:fieldNo,:required,"
		+ ":cwId,:macroId,:validationTypeId,:stackTrace,:transactionId);";

	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("errorId", errorId)
		.setParameter("batchId", batchId)
		.setParameter("configId", configId)
		.setParameter("fieldNo", fieldNo)
		.setParameter("required", required)
		.setParameter("validationTypeId", validationTypeId)
		.setParameter("cwId", cwId)
		.setParameter("macroId", macroId)
		.setParameter("stackTrace", stackTrace.toString())
		.setParameter("transactionId", transactionId);
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("insertProcessingError " + ex.getCause());
	}
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationTransport> getHandlingDetailsByBatch(int batchId) throws Exception {
	
	try {
	    String sql = ("select distinct clearRecords, autoRelease, errorHandling "
		    + " from configurationtransportdetails where configId in "
		    + "(select distinct configId from batchUploads where id = "+batchId+");");
	    
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		    .setResultTransformer(Transformers.aliasToBean(configurationTransport.class));

	    List<configurationTransport> ct = query.list();
	    return ct;
	} catch (Exception ex) {
	    System.err.println("getHandlingDetailsByBatch " + ex.getCause());
	    return null;
	}

    }

    @Override
    @Transactional(readOnly = false)
    public void updateRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing, String colNameToUpdate) {
	String sql = "";
	
	if (!foroutboundProcessing) {
	    
	    if("errorRecordCount".equals(colNameToUpdate)) {
		 sql = "update batchUploads set " + colNameToUpdate + " = "
		    + "(select count(id) as total from transactioninerrors_"+batchId+") "
		    + "where id = " + batchId;
	    }
	    else {
		 sql = "update batchUploads set " + colNameToUpdate + " = "
		    + "(select count(id) as total from transactiontranslatedin_"+batchId+") "
		    + "where id = " + batchId;
	    }
	} 
	else {
	    if("totalErrorCount".equals(colNameToUpdate)) {
		 sql = "update batchDownloads set " + colNameToUpdate + " = "
		    + "(select count(id) as total from transactionouterrors_"+batchId+") "
		    + "where id = " + batchId;
	    }
	    else {
		 sql = "update batchDownloads set " + colNameToUpdate + " = "
		    + "(select count(id) as total from transactiontranslatedout_"+batchId+") "
		    + "where id = " + batchId;
	    }
	}
	
	try {
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.executeUpdate();
	    
	} catch (Exception ex) {
	    System.err.println("updateRecordCounts " + ex.getCause());
	}
    }

    @Override
    public Integer getRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing) {
	return getRecordCounts(batchId, statusIds, foroutboundProcessing, true);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing, boolean inStatusIds) {
	
	String tableName = "transactiontranslatedin_"+batchId;
	if (foroutboundProcessing) {
	    tableName = "transactiontranslatedout_"+batchId;
	}
	String sql = "select count(id) as total from " + tableName;
	if (statusIds.size() > 0) {
	    sql += " where statusId ";
	    if (!inStatusIds) {
		sql += "not ";
	    }
	    sql += "in (:statusIds)";
	}

	try {
	    Query query = sessionFactory
		.getCurrentSession()
		.createSQLQuery(sql).addScalar("total", StandardBasicTypes.INTEGER);

	    if (statusIds.size() > 0) {
		query.setParameterList("statusIds", statusIds);
	    }

	    return (Integer) query.list().get(0);
	    
	} catch (Exception ex) {
	    System.err.println("getRecordCounts " + ex.getCause());
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer insertLoadData(Integer batchId, Integer configId,String delimChar, String fileWithPath, String loadTableName, boolean containsHeaderRow, String lineTerminator) {

	try {

	    String sql1 = ("SET @a" + batchId + "/*'*/:=/*'*/0;");

	    Query query1 = sessionFactory.getCurrentSession().createSQLQuery(sql1);
	    query1.executeUpdate();

	    String ignoreSyntax = "";

	    if (containsHeaderRow) {
		ignoreSyntax = "  IGNORE 1 LINES ";
	    }
	    String sql = ("LOAD DATA LOCAL INFILE '" + fileWithPath + "' INTO TABLE " + loadTableName + " fields terminated by '" + delimChar + "' "
		    + " optionally ENCLOSED BY '\"' ESCAPED BY '\\b' LINES TERMINATED BY '" + lineTerminator + "'  " + ignoreSyntax
		    + " set batchUploadId = " + batchId + ", configId = " + configId + ";");
	    
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.executeUpdate();
	     
	    
	    return 0;
	    
	} catch (Exception ex) {
	    System.err.println("insertLoadData " + ex.getCause());
	    ex.printStackTrace();
	    return 1;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer updateConfigIdForBatch(Integer batchId, Integer configId) {
	
	try {
	    String sql = ("update transactioninrecords_"+batchId+" set configId = :configId");
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setParameter("configId", configId);
	    query.executeUpdate();
	    return 0;

	} catch (Exception ex) {
	    System.err.println("updateConfigIdForBatch for table transactioninrecords_" + batchId + " " + ex.getCause());
	    return 1;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer loadTransactionTranslatedIn(Integer batchId, Integer configId) {
	
	List<configurationFormFields> configFormFields = configurationtransportmanager.getConfigurationFields(configId,0);

	StringBuilder selectFields = new StringBuilder();
	
	configFormFields.forEach(field -> {
	    selectFields.append("F").append(field.getFieldNo()).append(",");
	});

	try {
	    String sql = "insert into transactiontranslatedin_"+batchId+" "
	    + "(statusId, configId, transactionInRecordsId,"+selectFields+"batchUploadId)";

	    sql+= "select 9, configId, id, "+selectFields;

	    sql+= "batchUploadId from transactioninrecords_"+batchId+" "
	    + "where configId is not null;";
	    
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    System.err.println("loadTransactionTranslatedIn for table transactiontranslatedin_"+batchId+" "+ ex.getCause());
	    return 1;
	}
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationConnection> getBatchTargets(Integer batchId, boolean active) {
	
	try {
	    
	    String sql = "select a.sourceConfigId, a.targetConfigId, a.id, c.targetOrgCol, c.sourceSubOrgCol, b.orgId as targetOrgId, b.messageTypeId, d.transportMethodId " 
		+ "from configurationconnections a inner join " 
		+ "configurations b on a.targetConfigId = b.id inner join " 
		+ "configurationMessageSpecs c on a.sourceConfigId = c.configId inner join " 
		+ "configurationtransportdetails d on a.targetConfigId = d.configId " 
		+ "where sourceconfigId in (select distinct(configId) from transactioninrecords_"+batchId+") ";
	    
	    if (active) {
		sql +=  "and b.status = 1 and a.status = 1 and (b.messageTypeId = 0 or b.messageTypeId in (select id from messageTypes where status = 1)) ";
	    }
	    sql += "order by a.sourceConfigId;";

	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
		    Transformers.aliasToBean(configurationConnection.class));

	    List<configurationConnection> cc = query.list();
	    
	    return cc;
	} catch (Exception ex) {
	    System.err.println("getBatchTargets " + ex.getCause());
	    return null;
	}
    }


    /**
     * getBatchesByStatusIds - return uploaded batch info for specific statusIds
     *
     * @param list of statusIds
     * @return This function will return a list of batches.
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<batchUploads> getBatchesByStatusIds(List<Integer> statusIds) {
	try {
	    /* Get a list of uploaded batches for these statuses */
	    Criteria findBatches = sessionFactory.getCurrentSession().createCriteria(batchUploads.class);
	    findBatches.add(Restrictions.in("statusId", statusIds));
	    findBatches.addOrder(Order.asc("dateSubmitted"));
	    return findBatches.list();
	} catch (Exception ex) {
	    System.err.println("getBatchesByStatusIds " + ex.getCause().getMessage());
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer rejectInvalidTargetOrg(Integer batchId, configurationConnection bt) {
	try {
	    //error Id 9 - invalid target org
	    String sql = ("insert into transactioninerrors_"+batchId+" (batchUploadId, configId, transactionInRecordsId, errorId, fieldNo) "
		    + "select " + batchId + ", " + bt.getsourceConfigId() + ", transactionInRecordsId, 9,  " + bt.getTargetOrgCol()+" "
		    + "from transactiontranslatedin_"+batchId+" where configId = :sourceConfigId "
		    + "and transactionInRecordsId in (select id from transactioninrecords_"+batchId+") "
		    + "and f" + bt.getTargetOrgCol() + " not in (select orgId from configurationConnections cc,"
		    + "configurations c WHERE cc.targetConfigId = c.id and sourceConfigId = :sourceConfigId) "
		    + "and f" + bt.getTargetOrgCol() + " is not null and f" + bt.getTargetOrgCol() + " != 0 "
		    + "and transactionInRecordsId not in (select transactionInRecordsId from transactioninerrors_"+batchId+" where errorId = 9);");
	    
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.setParameter("sourceConfigId", bt.getsourceConfigId());

	    query.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    System.err.println("rejectInvalidTargetOrg " + ex.getCause());
	    ex.printStackTrace();
	    return 1;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer rejectNoConnections(batchUploads batch) {
	try {
	    //error Id 10 - no connections for source config
	    String sql = ("insert into transactioninerrors_"+batch.getId()+" "
		    + "(batchUploadId, configId, transactionInRecordsId, errorId) "
		    + "select " + batch.getId() + ",configId,id,10 from transactioninrecords_"+batch.getId()+" "
		    + "where configId not in "
		    + "(select id from configurations where orgId = :orgId "
		    + "and "
		    + "id in (select sourceconfigId from configurationconnections))");
	    
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.setParameter("orgId", batch.getOrgId());
	    query.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    System.err.println("rejectNoConnections " + ex.getCause());
	    ex.printStackTrace();
	    return 1;
	}
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkPermissionForBatch(utUser userInfo, batchUploads batchInfo) {

	try {
	    String sql = ("select count(id) as idCount from configurationConnectionSenders where "
		    + "connectionid in (select id from configurationConnections "
		    + "where sourceConfigId = :batchConfigId) and userId = :userId");
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).addScalar("idCount", StandardBasicTypes.INTEGER);
	    query.setParameter("batchConfigId", batchInfo.getConfigId());
	    query.setParameter("userId", userInfo.getId());

	    Integer idCount = (Integer) query.list().get(0);

	    if (idCount > 0) {
		return true;
	    }
	} catch (Exception ex) {
	    System.err.println("checkPermissionForBatch " + ex.getCause());
	    ex.printStackTrace();
	}
	return false;
    }

    @Override
    @Transactional(readOnly = false)
    public ConfigErrorInfo setConfigErrorInfo(Integer batchId, Integer errorCode, ConfigErrorInfo configErrorInfo) {

	//depending on errorCode
	return configErrorInfo;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public ConfigErrorInfo getHeaderForConfigErrorInfo(Integer batchId, ConfigErrorInfo configErrorInfo, List<Integer> rptFieldArray) {

	String sql = "select fieldDesc from configurationFormFields "
		+ " where fieldNo in (:rptFieldArray) and configId = :configId order by field(fieldNo, :rptFieldArray)";

	try {
	    Query query = sessionFactory
		    .getCurrentSession()
		    .createSQLQuery(sql).addScalar("fieldDesc", StandardBasicTypes.STRING);

	    query.setParameterList("rptFieldArray", rptFieldArray);
	    query.setParameter("configId", configErrorInfo.getConfigId());

	    List<String> labels = query.list();
	    if (rptFieldArray.get(0) != 0) {
		configErrorInfo.setRptFieldHeading1(labels.get(0));
	    }
	    if (rptFieldArray.get(1) != 0) {
		configErrorInfo.setRptFieldHeading2(labels.get(1));
	    }
	    if (rptFieldArray.get(2) != 0) {
		configErrorInfo.setRptFieldHeading3(labels.get(2));
	    }
	    if (rptFieldArray.get(3) != 0) {
		configErrorInfo.setRptFieldHeading4(labels.get(3));
	    }

	    return configErrorInfo;
	} catch (Exception ex) {
	    System.err.println("getErrorConfigForBatch " + ex.getCause());
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<utUserActivity> getBatchUserActivities(batchUploads batchInfo, boolean foroutboundProcessing) {
	String batchColName = "batchUploadId";
	if (foroutboundProcessing) {
	    batchColName = "batchDownloadId";
	}

	String sql = " select  users.firstName as userFirstName, organizations.orgname as orgName, "
		+ " organizations.id as orgId, users.lastName as userLastName, userActivity.* "
		+ " from useractivity left join users on users.id = userActivity.userId left join organizations on"
		+ " users.orgId = organizations.id where " + batchColName + "= :batchId order by dateCreated desc, userId";

	try {
	    Query query = sessionFactory
		    .getCurrentSession()
		    .createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(utUserActivity.class));

	    query.setParameter("batchId", batchInfo.getId());

	    List<utUserActivity> uas = query.list();

	    return uas;

	} catch (Exception ex) {
	    System.err.println("getBatchUserActivities " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer insertSFTPRun(MoveFilesLog sftpJob) {
	try {
	    Integer lastId = (Integer) sessionFactory.getCurrentSession().save(sftpJob);
	    return lastId;
	} catch (Exception ex) {
	    System.err.println("insertSFTPRun " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void updateSFTPRun(MoveFilesLog sftpJob) {
	try {
	    sessionFactory.getCurrentSession().update(sftpJob);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("updateSFTPRun " + ex.getCause());
	}
    }

    @Override
    @Transactional(readOnly = true)
    public List<configurationFTPFields> getFTPInfoForJob(Integer method) {
	try {
	    String sql = ("select rel_TransportFTPDetails.id, directory, ip, username, password, method, port, protocol, certification, transportId "
		    + " from configurationTransportDetails, rel_TransportFTPDetails "
		    + " where method = :method and configurationTransportDetails.id = rel_TransportFTPDetails.transportId "
		    + " and configId in (select id from configurations where status = 1) and "
		    + " directory not in (select folderPath from moveFilesLog where statusId = 1 and method = :method)"
		    + " group by directory order by configId;");

	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		    .setResultTransformer(Transformers.aliasToBean(configurationFTPFields.class))
		    .setParameter("method", method);

	    List<configurationFTPFields> ftpPaths = query.list();

	    return ftpPaths;
	} catch (Exception ex) {
	    System.err.println("getFTPInfoForJob " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationFileDropFields> getFileDropInfoForJob(Integer method) {
	
	try {
	    String sql = ("select rel_transportfiledropdetails.id, directory, method, transportId "
		    + " from configurationTransportDetails, rel_transportfiledropdetails "
		    + " where method = :method and configurationTransportDetails.id = rel_transportfiledropdetails.transportId "
		    + " and configId in (select id from configurations where status = 1 and type = 1) and  "
		    + " directory not in (select folderPath from moveFilesLog where statusId = 1 and method = :method) "
		    + " group by directory order by configId;");

	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		    .setResultTransformer(Transformers.aliasToBean(configurationFileDropFields.class))
		    .setParameter("method", method);

	    List<configurationFileDropFields> directories = query.list();

	    return directories;
	} catch (Exception ex) {
	    System.err.println("getFileDropInfoForJob " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Integer> checkCWFieldForList(Integer configId, Integer batchId,configurationDataTranslations cdt, boolean foroutboundProcessing) {
	
	try {
	    String sql = "";
	    Integer id = batchId;
	    
	    //we look for field values with UT delimiter
	    if (!foroutboundProcessing) {
		sql = "select transactionInRecordsId from transactiontranslatedin_"+batchId+" "
		    + "where F" + cdt.getFieldNo() + " like '%^^^^^%' "
		    + "and transactionInRecordsId "
		    + "in (select id from transactioninrecords_"+batchId+" where "
		    + "configId = :configId) "
		    + "and (statusId is null or statusId not in (:transRELId));";
		
	    } else {

		sql = "select transactionOutRecordsId from transactiontranslatedout_"+batchId+" "
		    + "where F" + cdt.getFieldNo() + " like '%^^^^^%' "
		    + "and transactionOutRecordsId "
		    + "in (select id from transactionoutrecords_"+batchId+" where "
		    + "configId = :configId) "
		    + "and (statusId is null or statusId not in (:transRELId));";
		
	    }
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.setParameter("configId", configId);
	    query.setParameterList("transRELId", transRELId);

	    List<Integer> transId = query.list();

	    return transId;

	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("checkCWFieldForList " + ex.getCause());
	    return null;
	}

    }

    @Override
    @Transactional(readOnly = false)
    @SuppressWarnings("unchecked")
    public List<IdAndFieldValue> getIdAndValuesForConfigField(Integer configId,Integer batchId, configurationDataTranslations cdt,boolean foroutboundProcessing) {
	
	try {
	    String sql = "";
	    Integer id = batchId;
	    if (!foroutboundProcessing) {
		sql = "select transactionInRecordsId as transactionId, F" + cdt.getFieldNo() + " as fieldValue "
		+ "from transactiontranslatedin_"+batchId+" "
		+ "where  configId = :configId and length(trim(F" + cdt.getFieldNo() + ")) != 0"
		+ " and length(REPLACE(REPLACE(F" + cdt.getFieldNo() + ", '\n', ''), '\r', '')) != 0"
		+ " and transactionInRecordsId "
		+ "in (select id from transactioninrecords_"+batchId+" where ";
		if (configId != 0) {
		    sql += "configId = :configId and ";
		}
		sql += "(statusId is null or statusId not in (:transRELId)));";
	    } 
	    else {

		sql = "select transactionOutRecordsId as transactionId, F" + cdt.getFieldNo() + " as fieldValue "
		+ "from transactiontranslatedout_"+batchId+" "
		+ "where  configId = :configId and length(trim(F" + cdt.getFieldNo() + ")) != 0"
		+ " and length(REPLACE(REPLACE(F" + cdt.getFieldNo() + ", '\n', ''), '\r', '')) != 0"
		+ " and transactionOutRecordsId "
		+ "in (select id from transactionoutrecords_"+batchId+" where ";
		if (configId != 0) {
		    sql += "configId = :configId and ";
		}
		sql += "(statusId is null or statusId not in (:transRELId)));";
		
	    }
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setResultTransformer(Transformers.aliasToBean(IdAndFieldValue.class))
		.setParameter("configId", configId)
		.setParameter("id", id);
	    
	    List<IdAndFieldValue> valueList = query.list();

	    return valueList;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("getIdAndValuesForConfigField " + ex.getCause());
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer updateFieldValue(Integer batchId, String fieldValue, Integer fieldNo, Integer transactionId, boolean foroutboundProcessing) {
	try {
	    String sql = "";

	    if (!foroutboundProcessing) {
		sql = "update transactiontranslatedin_"+batchId+" set F" + fieldNo + " = :fieldValue where transactionInRecordsId = :id";
	    } else {
		sql = "update transactiontranslatedout_"+batchId+" set F" + fieldNo + " = :fieldValue where transactionOutRecordsId = :id";
	    }

	    Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		    .setParameter("fieldValue", fieldValue)
		    .setParameter("id", transactionId);

	    updateData.executeUpdate();

	    return 0;
	} catch (Exception ex) {
	    System.err.println("updateFieldValue " + ex.getCause());
	    ex.printStackTrace();
	    return 1;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void trimFieldValues(Integer batchId, boolean foroutboundProcessing, Integer configId, boolean trimAll) {
	
	String sql = "";
	
	List<configurationFormFields> configFormFields = configurationtransportmanager.getConfigurationFields(configId, 0);
	
	StringBuilder tableFields = new StringBuilder();
	
	configFormFields.forEach(field -> {
	    if(field.getUseField()) {
		tableFields.append("F").append(field.getFieldNo()).append(" = LTRIM(RTRIM(F").append(field.getFieldNo()).append(")),");
	    }
	});

	String tableName = "transactiontranslatedin_"+batchId;

	
	if(!foroutboundProcessing) {
	    sql = "update "+tableName + " set " + tableFields;
	    sql += "configId = LTRIM(RTRIM(configId))";
	}
	
	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql);

	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("trimFieldValues for table " +tableName+ " " + ex.getCause());
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer updateBatchDLStatusByUploadBatchId(Integer batchUploadId, Integer fromStatusId, Integer toStatusId, String timeField) {
	try {
	    String sql = "update batchDownloads set statusId = :toStatusId ";
	    
	    if (timeField.equalsIgnoreCase("startover")) {
		// we reset time
		sql = sql + ",startDateTime = null, endDateTime = null";
	    } else if (!timeField.equalsIgnoreCase("")) {
		sql = sql + "," + timeField + " = CURRENT_TIMESTAMP";
	    } else {
		sql = sql + ",startDateTime = CURRENT_TIMESTAMP, endDateTime = CURRENT_TIMESTAMP";
	    }
	    sql = sql + "  where batchUploadId = :batchUploadId";
	    
	    if (fromStatusId > 0) {
		sql = sql + " and statusId = :fromStatusId";
	    }

	    Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("toStatusId", toStatusId)
		.setParameter("batchUploadId", batchUploadId);
	    
	    if (fromStatusId > 0) {
		updateData.setParameter("fromStatusId", fromStatusId);
	    }

	    updateData.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("updateBatchDLStatusByUploadBatchId " + ex.getCause());
	    return 1;
	}
    }


    @Override
    @Transactional(readOnly = false)
    public Integer clearBatchDownloads(List<Integer> batchIds) {
	String sql = "delete from batchDownloads where id in (:batchIds);";
	try {
	    Query deleteTable = sessionFactory.getCurrentSession().createSQLQuery(sql).setParameterList("batchIds", batchIds);
	    deleteTable.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("clearBatchDownloads " + ex.getCause().getMessage());
	    return 1;

	}
    }

    @Override
    @Transactional(readOnly = true)
    public String getTransactionInIdsFromBatch(Integer batchUploadId) {
	try {
	    String sql = "select concat('', min(id),' to ', max(id)) as idList from transactioninrecords_"+batchUploadId+" "
		    + "order by id;";
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);

	    List<String> tiIdList = query.list();
	    if (tiIdList.size() > 0) {
		return tiIdList.get(0);
	    } else {
		return " ";
	    }

	} catch (Exception ex) {
	    System.err.println("getTransactionInIdsFromBatch for batch " + batchUploadId + " " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<WSMessagesIn> getWSMessagesByStatusId(List<Integer> statusIds) {
	//1 if list of statusId is null, we get all
	try {
	    Criteria findWSMessages = sessionFactory.getCurrentSession().createCriteria(WSMessagesIn.class);
	    if (statusIds.size() != 0) {
		findWSMessages.add(Restrictions.in("statusId", statusIds));
	    }

	    List<WSMessagesIn> wsMessages = findWSMessages.list();
	    return wsMessages;
	} catch (Exception ex) {
	    System.err.println("getWSMessagesByStatusId " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}

    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public WSMessagesIn getWSMessagesById(Integer wsMessageId) {
	//1 if list of statusId is null, we get all
	try {
	    Criteria findWSMessage = sessionFactory.getCurrentSession().createCriteria(WSMessagesIn.class);
	    findWSMessage.add(Restrictions.eq("id", wsMessageId));

	    List<WSMessagesIn> wsMessages = findWSMessage.list();
	    if (wsMessages.size() > 0) {
		return wsMessages.get(0);
	    }
	} catch (Exception ex) {
	    System.err.println("getWSMessagesById " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
	return null;
    }

    @Override
    @Transactional(readOnly = false)
    public Integer updateWSMessage(WSMessagesIn wsMessage) {
	try {
	    sessionFactory.getCurrentSession().update(wsMessage);
	    return 0;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("updateWSMessage " + ex.getCause());
	    return 1;
	}

    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getErrorCodes(List<Integer> codesToIgnore) {
	try {
	    String sql = "select id from lu_errorcodes";
	    if (codesToIgnore.size() != 0) {
		sql = sql + " where id not in (:codesToIgnore);";
	    }

	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    if (codesToIgnore.size() != 0) {
		query.setParameterList("codesToIgnore", codesToIgnore);
	    }

	    List<Integer> errorCodes = query.list();
	    return errorCodes;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("getErrorCodes " + ex.getCause());
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer rejectInvalidSourceSubOrg(batchUploads batch,configurationConnection bt, boolean nofinalStatus) {
	
	try {
	    //error Id 23 - invalid SourceSubOrg
	    String sql = "insert into transactioninerrors_"+batch.getId() + " "
		+ "(batchUploadId,configId,transactionInRecordsId,errorId,fieldNo) "
		+ "select " + batch.getId() + "," + bt.getsourceConfigId() + ",transactionInRecordsId,23," + bt.getSourceSubOrgCol() + " "
		+ "from transactiontranslatedin_"+batch.getId()+" where configId = :sourceConfigId ";
	    
	    if (nofinalStatus) {
		sql += "and (statusId is null or statusId not in (:transRELId)) ";
	    }
	    
	    sql += "and f" + bt.getSourceSubOrgCol() + " not in (select id from organizations where parentid = :parentOrg) "
		+ "and f" + bt.getSourceSubOrgCol() + " is not null "
		+ "and transactionInRecordsId not in (select transactionInRecordsId "
		+ "from transactioninerrors_"+batch.getId()+" where errorId = 23);";

	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    query.setParameter("parentOrg", batch.getOrgId());
	    query.setParameter("sourceConfigId", bt.getsourceConfigId());
	    
	    if (nofinalStatus) {
		query.setParameterList("transRELId", transRELId);
	    }

	    query.executeUpdate();
	    return 0;
	    
	} catch (Exception ex) {
	    System.err.println("rejectInvalidSubSourceOrg for batch " + batch.getId() + " " + ex.getCause());
	    ex.printStackTrace();
	    return 1;
	}
    }

    /**
     * The 'geBatchesIdsForReport' function will return a list of batches for the admin in the processing activities section.
     *
     * @param fromDate
     * @param toDate
     * @return This function will return a list of batch uploads
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<Integer> geBatchesIdsForReport(String fromDate, String toDate) throws Exception {

	String sql = "select id from batchUploads a "
		+ "where (a.dateSubmitted >= '" + fromDate + "' and a.dateSubmitted < '" + toDate + "') "
		+ "and statusId in (2,3,4,5,6,22,23,24,25,28,36,38,41,42,43,59,64) "
		+ "order by dateSubmitted desc";

	Query findBatches = sessionFactory.getCurrentSession().createSQLQuery(sql);
	List batches = findBatches.list();

	List<Integer> batchIds = findBatches.list();

	return batchIds;
    }

    @Override
    @Transactional(readOnly = true)
    public BigInteger getMessagesSent(String fromDate, String toDate) throws Exception {

	String sql = "select count(a.id) as totalMessagesSent "
		+ "from batchdownloads a inner join "
		+ "batchUploads b on a.batchUploadId = b.id "
		+ "where a.statusId = 28 and (b.dateSubmitted >= '" + fromDate + "' and b.dateSubmitted < '" + toDate + "')";

	Query getMessagesSentCount = sessionFactory.getCurrentSession().createSQLQuery(sql);

	return (BigInteger) getMessagesSentCount.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public BigInteger getRejectedCount(String fromDate, String toDate) throws Exception {

	String sql = "select count(a.id) as totalErrors "
		+ "from batchuploadauditerrors a inner join "
		+ "batchUploads b on a.batchUploadId = b.id "
		+ "where (b.dateSubmitted >= '" + fromDate + "' and b.dateSubmitted < '" + toDate + "')";

	Query getRejectedCount = sessionFactory.getCurrentSession().createSQLQuery(sql);

	return (BigInteger) getRejectedCount.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<activityReportList> getReferralList(String fromDate, String toDate) throws Exception {

	String sql = ("select a.configId, c.orgname as orgName, b.configName as messageType,"
		+ "(select count(Id) from batchuploads where configId = a.configId and dateSubmitted >= '" + fromDate + "' and dateSubmitted < '" + toDate + "') as total "
		+ "from batchuploads a "
		+ "inner join configurations b on b.id = a.configId "
		+ "inner join organizations c on c.id = a.orgId "
		+ "where a.dateSubmitted >= '" + fromDate + "' and a.dateSubmitted < '" + toDate + "' "
		+ "group by a.configId "
		+ "order by orgName asc");
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.addScalar("configId", StandardBasicTypes.INTEGER)
		.addScalar("orgName", StandardBasicTypes.STRING)
		.addScalar("messageType", StandardBasicTypes.STRING)
		.addScalar("total", StandardBasicTypes.BIG_INTEGER)
		.setResultTransformer(Transformers.aliasToBean(activityReportList.class));

	List<activityReportList> activityList = query.list();

	return activityList;
    }

    /**
     * The 'getReferralActivityExports' function will return a list of generated exports
     *
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<referralActivityExports> getReferralActivityExports() throws Exception {

	Query query = sessionFactory.getCurrentSession().createQuery("from referralActivityExports order by dateSubmitted desc");

	/**
	 * Only return the top one for right now *
	 */
	query.setMaxResults(1);

	return query.list();
    }

    /**
     * The 'saveReferralActivityExport' function will create a new activity export
     *
     * @param activityExport
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = false)
    public void saveReferralActivityExport(referralActivityExports activityExport) throws Exception {
	sessionFactory.getCurrentSession().save(activityExport);
    }

    /**
     * The 'getReportActivityStatusValueById' function will return the value of the activity status for the id passed in.
     *
     * @param activityStatusId
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public String getReportActivityStatusValueById(Integer activityStatusId) throws Exception {
	String sql = "select descValue as statusValue from rel_crosswalkdata where crosswalkId = 19 and targetValue = " + activityStatusId;

	Query getFieldvalue = sessionFactory.getCurrentSession().createSQLQuery(sql);

	return (String) getFieldvalue.uniqueResult();
    }

    /**
     * The 'getActivityStatusValueById' function will return the value of the activity status for the id passed in.
     *
     * @param activityStatusId
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public String getActivityStatusValueById(Integer activityStatusId) throws Exception {
	String sql = "select displayText as statusValue from lu_internalmessagestatus where id = " + activityStatusId;

	Query getFieldvalue = sessionFactory.getCurrentSession().createSQLQuery(sql);

	return (String) getFieldvalue.uniqueResult();
    }

    /**
     * The 'clearMultipleTargets' function will remove the multiple targets set for a utConfiguration.
     *
     * @param batchId
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = false)
    public void clearMultipleTargets(Integer batchId) throws Exception {
	Query deletMultipleTargets = sessionFactory.getCurrentSession().createQuery("delete from batchMultipleTargets where batchId = :batchId");
	deletMultipleTargets.setParameter("batchId", batchId);
	deletMultipleTargets.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = false)
    public List<Transaction> setTransactionInInfoByStatusId(Integer batchId, List<Integer> statusIds, Integer howMany) throws Exception {

	String sql = "select c.configName as srcConfigName, "
		+ "b.orgId, b.configId, ti.transactionInRecordsId as transactionId, b.id as batchId, d.orgName as srcOrgName "
		+ "from transactiontranslatedin_"+batchId+" ti inner join "
		+ "batchUploads b on ti.batchUploadId = b.id inner join "
		+ "configurations c on b.configId = c.id inner join "
		+ "organizations d on d.id = b.orgId "
		+ "where ti.statusid in (:statusIds) "
		+ "limit :howMany";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.addScalar("srcConfigName", StandardBasicTypes.STRING)
		.addScalar("srcOrgName", StandardBasicTypes.STRING)
		.addScalar("orgId", StandardBasicTypes.INTEGER)
		.addScalar("transactionId", StandardBasicTypes.INTEGER)
		.addScalar("configId", StandardBasicTypes.INTEGER)
		.addScalar("batchId", StandardBasicTypes.INTEGER)
		.setResultTransformer(Transformers.aliasToBean(Transaction.class))
		.setParameter("howMany", howMany)
		.setParameterList("statusIds", statusIds);

	List<Transaction> transactions = query.list();

	return transactions;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<CrosswalkData> getCrosswalkDataForBatch(configurationDataTranslations cdt, Integer batchId, boolean foroutboundProcessing) throws Exception {
	
	//look for value in corresponding f column
	String sql = "select distinct * from rel_crosswalkdata "
		+ " where crosswalkid = :crosswalkId and sourcevalue in "
		+ " (select f" + cdt.getFieldNo() + " ";

	Integer id = batchId;
	if (!foroutboundProcessing) {
	    sql += " from transactiontranslatedin_"+batchId+" where transactioninRecordsId "
		+ " in  (select id from transactioninrecords_"+batchId+"))";

	} else {
	     sql += " from transactiontranslatedout_"+batchId+" where transactionoutRecordsId "
		+ " in  (select id from transactionoutrecords_"+batchId+"))";
	}

	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setResultTransformer(Transformers.aliasToBean(CrosswalkData.class));
	    query.setParameter("id", id);
	    query.setParameter("crosswalkId", cdt.getCrosswalkId());

	List<CrosswalkData> trs = query.list();

	return trs;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<referralActivityExports> getReferralActivityExportsByStatus(
	    List<Integer> statusIds, Integer howMany) throws Exception {
	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(referralActivityExports.class);
	if (statusIds.size() > 0) {
	    criteria.add(Restrictions.in("statusId", statusIds));
	}
	if (howMany > 0) {
	    criteria.setMaxResults(howMany);
	}
	return criteria.list();
    }

    /**
     * The 'updateReferralActivityExport' function will save update to a activity export
     *
     * @param activityExport
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = false)
    public void updateReferralActivityExport(referralActivityExports activityExport) throws Exception {
	sessionFactory.getCurrentSession().update(activityExport);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<referralActivityExports> getReferralActivityExportsWithUserNames(
	    List<Integer> statusIds) throws Exception {

	String sql = "SELECT referralactivityexports.statusId, referralactivityexports.selDateRange, fileName, dateSubmitted,  "
		+ " referralactivityexports.id, concat(users.firstname, ' ', users.lastname) as createdByName, "
		+ " case referralactivityexports.statusId when  1 then 'Requested' when 2 then 'In Process' "
		+ " when 3 then 'Ready for Viewing' when 4 then 'Viewed' when 5 then 'Deleted' when 6 then 'No Referrals Found' end as statusName"
		+ " from  referralactivityexports, users "
		+ " where users.id = referralactivityexports.createdBy  and referralactivityexports.statusId in (:statusId) "
		+ " order by dateSubmitted desc;";
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
		Transformers.aliasToBean(referralActivityExports.class));
	query.setParameterList("statusId", statusIds);

	List<referralActivityExports> exports = query.list();
	return exports;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public referralActivityExports getReferralActivityExportById(Integer exportId) throws Exception {
	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(referralActivityExports.class);
	criteria.add(Restrictions.eq("id", exportId));
	List<referralActivityExports> exports = criteria.list();
	if (exports.size() > 0) {
	    return exports.get(0);
	} else {
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void populateAuditReport(Integer batchUploadId, Integer configId)
	    throws Exception {
	String sql = "call populateAuditReport(:configId, :batchUploadId);";
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	query.setParameter("configId", configId);
	query.setParameter("batchUploadId", batchUploadId);
	query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<Integer> getErrorFieldNos(Integer batchUploadId) throws Exception {
	
	String sql = "select distinct fieldNo from transactionIndetailauditerrors_"+batchUploadId;

	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	List<Integer> fieldNoList = query.list();
	return fieldNoList;
    }

    @Override
    @Transactional(readOnly = false)
    public void populateFieldError(Integer batchUploadId, Integer fieldNo, configurationMessageSpecs cms) throws Exception {
	
	String sql = "UPDATE  transactionIndetailauditerrors_"+batchUploadId + " "
		+ " JOIN (select f" + fieldNo + " as errorData,";
	
	if (cms.getrptField1() != 0) {
	    sql += "f" + cms.getrptField1() + " as reportField1Data,";
	}
	if (cms.getrptField2() != 0) {
	    sql += "f" + cms.getrptField2() + " as reportField2Data,";
	}
	if (cms.getrptField3() != 0) {
	    sql += "f" + cms.getrptField3() + " as reportField3Data,";
	}
	if (cms.getrptField4() != 0) {
	    sql += "f" + cms.getrptField4() + " as reportField4Data,";
	}
	sql = sql + "transactionInRecordsId as matchId from transactiontranslatedin_"+batchUploadId+" "
	    + "where statusId in (13)) tbl_concat "
	    + "ON transactionIndetailauditerrors_"+batchUploadId +".transactionInRecordsId = tbl_concat.matchid"
	    + " SET transactionIndetailauditerrors_"+batchUploadId+".errorData = tbl_concat.errorData ";
	
	if (cms.getrptField1() != 0) {
	    sql = sql + ", transactionIndetailauditerrors_"+batchUploadId+".reportField1Data = tbl_concat.reportField1Data";
	}
	if (cms.getrptField2() != 0) {
	    sql = sql + ", transactionIndetailauditerrors_"+batchUploadId+".reportField2Data = tbl_concat.reportField2Data";
	}
	if (cms.getrptField3() != 0) {
	    sql = sql + ", transactionIndetailauditerrors_"+batchUploadId+".reportField3Data = tbl_concat.reportField3Data";
	}
	if (cms.getrptField4() != 0) {
	    sql = sql + ", transactionIndetailauditerrors_"+batchUploadId+".reportField4Data = tbl_concat.reportField4Data";
	}
	sql = sql + " WHERE transactionIndetailauditerrors_"+batchUploadId+".fieldNo = :fieldNo"
		+ "  and transactionIndetailauditerrors_"+batchUploadId+".configId = :configId";

	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	query.setParameter("configId", cms.getconfigId());
	query.setParameter("fieldNo", fieldNo);
	query.executeUpdate();

    }

    @Override
    @Transactional(readOnly = false)
    public void cleanAuditErrorTable(Integer batchUploadId) throws Exception {
	Query clearRecords = sessionFactory.getCurrentSession().createSQLQuery("DELETE from batchuploadauditerrors where batchUploadId = :batchUploadId");
	clearRecords.setParameter("batchUploadId", batchUploadId);
	clearRecords.executeUpdate();
    }

    @Override
    @Transactional(readOnly = false)
    public Integer executeCWDataForSingleFieldValue(Integer configId, Integer batchId,
	    configurationDataTranslations cdt, boolean foroutboundProcessing) {

	String sql;
	Integer id = batchId;
	String inboundOutbound = "Inbound";
	
	if (foroutboundProcessing == false) {
	    sql = "update transactiontranslatedin_"+batchId+" JOIN (select sourcevalue as matchid, targetvalue as label   "
		+ " from rel_crosswalkdata where crosswalkId = :crosswalkId) tbl_concat "
		+ " ON REPLACE(REPLACE(trim(F" + cdt.getFieldNo() + "), '\n', ''), '\r', '') = tbl_concat.matchid   "
		+ " SET transactiontranslatedin_"+batchId+".forCW = tbl_concat.label  "
		+ " where configId = :configId "
		+ " and (statusId is null or statusId not in (:transRELId));";
	    
	} else {
	    inboundOutbound = "Outbound";
	    sql = "update transactiontranslatedout_"+batchId+" JOIN (select sourcevalue as matchid, targetvalue as label   "
		+ " from rel_crosswalkdata where crosswalkId = :crosswalkId) tbl_concat "
		+ " ON REPLACE(REPLACE(trim(F" + cdt.getFieldNo() + "), '\n', ''), '\r', '') = tbl_concat.matchid   "
		+ " SET transactiontranslatedout_"+batchId+".forCW = tbl_concat.label  "
		+ " where configId = :configId "
		+ " and (statusId is null or statusId not in (:transRELId));";
	    
	}

	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
	    .setParameter("crosswalkId", cdt.getCrosswalkId())
	    .setParameter("configId", configId)
	    .setParameterList("transRELId", transRELId);
	
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("executeCWDataForSingleFieldValue for " + inboundOutbound + " batch (Id: " + batchId + ") " + ex.getCause());
	    ex.printStackTrace();
	    insertProcessingError(processingSysErrorId, configId, batchId, cdt.getFieldNo(),
		    null, cdt.getCrosswalkId(), null,
		    false, foroutboundProcessing, ("executeCWDataForSingleFieldValue for " + inboundOutbound + " batch (Id: " + batchId + ") " + ex.getCause().toString()));
	}
	
	return 1;

    }

    @Override
    @Transactional(readOnly = false)
    public void deleteMoveFileLogsByStatus(Integer statusId, Integer transportMethodId) throws Exception {
	Query deletMoveFilesLog = sessionFactory.getCurrentSession().createQuery("delete from MoveFilesLog where statusId = :statusId and transportMethodId = :transportMethodId");
	deletMoveFilesLog.setParameter("statusId", statusId);
	deletMoveFilesLog.setParameter("transportMethodId", transportMethodId);

	deletMoveFilesLog.executeUpdate();

    }

    @Override
    @Transactional(readOnly = false)
    public void deleteLoadTableRows(Integer howMany, String ascOrDesc, String laodTableName) throws Exception {
	String sql = "delete from " + laodTableName + " order by id " + ascOrDesc + " limit " + howMany;
	Query deleteTable = sessionFactory.getCurrentSession().createSQLQuery(sql);
	deleteTable.executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public BigInteger getUserRejectedCount(Integer userId, Integer orgId, String fromDate, String toDate) throws Exception {

	String sql = "select count(id) as totalReferrals "
		+ "from batchUploads where "
		+ "(dateSubmitted >= '" + fromDate + "' and dateSubmitted < '" + toDate + "') and "
		+ "statusId in (1,7,29,30,33,39,41) and "
		+ "orgId = " + orgId + " and ("
		+ "configId in ("
		+ "select sourceConfigId "
		+ "from configurationconnections "
		+ "where id in (select connectionId from configurationconnectionsenders where userId = " + userId + "))) ";

	Query getRejectedCount = sessionFactory.getCurrentSession().createSQLQuery(sql);

	return (BigInteger) getRejectedCount.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<batchErrorSummary> getBatchErrorSummary(int batchId) throws Exception {
	try {
	    String sql = ("select count(e.id) as totalErrors, e.errorId, c.displayText as errorDisplayText "
		    + "from batchuploadauditerrors e "
		    + "inner join lu_errorcodes c on c.id = e.errorId "
		    + "where e.batchUploadId = :batchId group by e.errorId");

	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).addScalar("errorDisplayText", StandardBasicTypes.STRING).addScalar("errorId", StandardBasicTypes.INTEGER).addScalar("totalErrors", StandardBasicTypes.INTEGER).setResultTransformer(
		    Transformers.aliasToBean(batchErrorSummary.class));
	    query.setParameter("batchId", batchId);

	    List<batchErrorSummary> batchErrorSummaries = query.list();

	    return batchErrorSummaries;

	} catch (Exception ex) {
	    System.err.println("getBatchErrorSummary " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = true)
    public List getErrorDataBySQLStmt(String sqlStmt) throws Exception {

	if (!"".equals(sqlStmt)) {
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlStmt);

	    return query.list();
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List getErrorReportField(Integer batchUploadId)
	    throws Exception {
	String sql = "select rptField1.rptLabel1 ,rptField2.rptLabel2,rptField3.rptLabel3,rptField4.rptLabel4 "
		+ "from batchuploadauditerrors e inner join "
		+ "configurationmessagespecs cs on e.configId = cs.configId inner join "
		+ "("
		+ "select fieldDesc as rptLabel1, fieldNo, configId "
		+ "from configurationformfields"
		+ ") rptField1 on rptField1.fieldNo = cs.rptField1 and rptField1.configId = e.configId inner join "
		+ "("
		+ "select fieldDesc as rptLabel2, fieldNo, configId "
		+ "from configurationformfields"
		+ ") rptField2 on rptField2.fieldNo = cs.rptField2 and rptField2.configId = e.configId inner join "
		+ "("
		+ "select fieldDesc as rptLabel3, fieldNo, configId "
		+ "from configurationformfields"
		+ ") rptField3 on rptField3.fieldNo = cs.rptField3 and rptField3.configId = e.configId inner join "
		+ "("
		+ "select fieldDesc as rptLabel4, fieldNo, configId "
		+ "from configurationformfields"
		+ ") rptField4 on rptField4.fieldNo = cs.rptField4 and rptField4.configId = e.configId "
		+ "where e.batchUploadId = :batchUploadId limit 1";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	query.setParameter("batchUploadId", batchUploadId);

	return query.list();
    }

    /**
     * The 'getBatchesByStatusIdsAndDate' function will return a list of batches for the admin in the processing activities section.
     *
     * @param fromDate
     * @param toDate
     * @return This function will return a list of batch uploads
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<batchUploads> getBatchesByStatusIdsAndDate(Date fromDate, Date toDate, Integer fetchSize, List<Integer> statusIds) throws Exception {
	
	
	Criteria findBatches = sessionFactory.getCurrentSession().createCriteria(batchUploads.class);
	findBatches.add(Restrictions.in("statusId",statusIds));

	if (fromDate != null) {
	    if (!"".equals(fromDate)) {
		findBatches.add(Restrictions.ge("dateSubmitted", fromDate));
	    }
	}

	if (toDate != null) {
	    if (!"".equals(toDate)) {
		findBatches.add(Restrictions.lt("dateSubmitted", toDate));
	    }
	}

	findBatches.addOrder(Order.desc("dateSubmitted"));

	if (fetchSize > 0) {
	    findBatches.setMaxResults(fetchSize);
	}

	return findBatches.list();
	
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public batchRetry getBatchRetryByUploadId(Integer batchUploadId, Integer statusId)
	    throws Exception {
	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(batchRetry.class);
	criteria.add(Restrictions.eq("batchUploadId", batchUploadId));
	if (statusId > 0) {
	    criteria.add(Restrictions.eq("fromStatusId", statusId));
	}
	criteria.addOrder(Order.desc("dateCreated"));
	List<batchRetry> brList = criteria.list();

	if (brList.size() > 0) {
	    return brList.get(0);
	} else {
	    return null;
	}

    }

    @Override
    @Transactional(readOnly = false)
    public void saveBatchRetry(batchRetry br) throws Exception {
	sessionFactory.getCurrentSession().save(br);
    }

    //we call this when batch is being reset, not when batch is being re-process
    @Override
    @Transactional(readOnly = false)
    public void clearBatchRetry(Integer batchUploadId) throws Exception {
	Query delBatch = sessionFactory.getCurrentSession().createQuery("delete from batchRetry where batchUploadId = :batchUploadId");
	delBatch.setParameter("batchUploadId", batchUploadId);
	delBatch.executeUpdate();
    }

    @Override
    @Transactional(readOnly = false)
    public Integer insertRestApiMessage(RestAPIMessagesIn newRestAPIMessage) throws Exception {
	Integer newRestAPIMessageId = null;

	newRestAPIMessageId = (Integer) sessionFactory.getCurrentSession().save(newRestAPIMessage);

	return newRestAPIMessageId;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RestAPIMessagesIn> getRestAPIMessagesByStatusId(List<Integer> statusIds) {
	//1 if list of statusId is null, we get all
	try {
	    Criteria findRestAPIMessages = sessionFactory.getCurrentSession().createCriteria(RestAPIMessagesIn.class);
	    if (!statusIds.isEmpty()) {
		findRestAPIMessages.add(Restrictions.in("statusId", statusIds));
	    }

	    List<RestAPIMessagesIn> restAPIMessages = findRestAPIMessages.list();
	    return restAPIMessages;

	} catch (Exception ex) {
	    System.err.println("getRestAPIMessagesByStatusId " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}

    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public RestAPIMessagesIn getRestAPIMessagesById(Integer restAPIMessageId) {
	//1 if list of statusId is null, we get all
	try {
	    Criteria findRestAPIMessage = sessionFactory.getCurrentSession().createCriteria(RestAPIMessagesIn.class);
	    findRestAPIMessage.add(Restrictions.eq("id", restAPIMessageId));

	    List<RestAPIMessagesIn> restAPIMessages = findRestAPIMessage.list();
	    if (!restAPIMessages.isEmpty()) {
		return restAPIMessages.get(0);
	    }
	} catch (Exception ex) {
	    System.err.println("getRestAPIMessagesById " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
	return null;
    }

    @Override
    @Transactional(readOnly = false)
    public Integer updateRestAPIMessage(RestAPIMessagesIn APIMessage) {
	try {
	    sessionFactory.getCurrentSession().update(APIMessage);
	    return 0;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("updateRestAPIMessage " + ex.getCause());
	    return 1;
	}

    }

    @Override
    @Transactional(readOnly = false)
    public void updateBatchClearAfterDeliveryByBatchUploadId(Integer batchUploadId, Integer newStatusId) throws Exception {
	
	String sql = "update batchClearAfterDelivery set statusId = :newStatusId where batchUploadId = :batchUploadId";

	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
	    .setParameter("batchUploadId", batchUploadId)
	    .setParameter("newStatusId", newStatusId);
	
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("updateBatchClearAfterDeliveryByBatchUploadId " + ex.getCause());
	}
    }

    @Override
    @Transactional(readOnly = false)
    public Integer clearBatchClearAfterDeliveryByBatchUploadId(Integer batchUploadId) throws Exception {
	Query clearRecords = sessionFactory.getCurrentSession().createSQLQuery("DELETE from batchClearAfterDelivery where batchUploadId = :batchUploadId");
	clearRecords.setParameter("batchUploadId", batchUploadId);
	try {
	    clearRecords.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    System.err.println("clearBatchClearAfterDeliveryByBatchUploadId " + ex.getCause());
	    return 1;
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<Integer> getTargetConfigsForUploadBatch(Integer batchUploadId, Integer configId) throws Exception {
	
	String sql = "select distinct targetconfigId from configurationconnections "
		+ " where sourceConfigId = :configId";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
	    .setParameter("configId", configId);

	List<Integer> configIds = query.list();
	return configIds;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public Integer checkClearAfterDeliveryBatch(int batchUploadId)
	    throws Exception {
	
	String sql = "select case when dlbatchIds = dlbatches then 1 else 0 end as doneDLBatch from ("
	    + "select group_concat(distinct batchDLId order by batchDLId) as dlbatchIds from batchclearafterdelivery "
	    + "where batchUploadId = :batchUploadId) bcad "
	    + "inner join (select group_concat(distinct Id order by Id) as dlbatches from batchdownloads "
	    + "where statusId = 28 and batchUploadId = :batchUploadId) batches ";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
	    .addScalar("doneDLBatch", StandardBasicTypes.INTEGER)
	    .setParameter("batchUploadId", batchUploadId);

	List<Integer> bcad = query.list();
	if (bcad.size() == 0) {
	    return 0;
	} else {
	    return bcad.get(0);
	}
    }


    @Override
    @Transactional
    public Integer removeLoadTableBlankRows(Integer batchUploadId,String loadTableName) throws Exception {
	
	String sql = "delete from " + loadTableName + " where F1 IS NULL";
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);

	try {
	    query.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    System.err.println("removeLoadTableBlankRows from "+ loadTableName + " errored");
	    ex.printStackTrace();
	    return 1;
	}
    }

    @Override
    @Transactional
    public Integer getLoadTransactionCount(String loadTableName) throws Exception {
	String sql = "select count(id) as rowCount from " + loadTableName + ";";
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).addScalar("rowCount", StandardBasicTypes.INTEGER);
	Integer rowCount = (Integer) query.list().get(0);
	return rowCount;
    }

    @Override
    @Transactional(readOnly = false)
    public void updateStatusForErrorTrans(Integer batchId, Integer statusId, boolean foroutboundProcessing) throws Exception {
	
	String inboundOutbound = "Outbound";
	
	try {
	    String sql;
	    Integer id = batchId;
	    String joinTable = "transactionouterrors_"+batchId;
	    String updateTable = "transactiontranslatedout_"+batchId;
	    String matchId = "transactionOutRecordsId";

	    if (foroutboundProcessing == false) {
		inboundOutbound = "Inbound";
		joinTable = "transactioninerrors_"+batchId;
		updateTable = "transactiontranslatedin_"+batchId;
		matchId = "transactionInRecordsId";
	    }

	    /**
	     * original update transactionIn set statusId = 14 where id in (select distinct transactionInId from transactionInErrors where batchUploadId = 1216) and statusId not in (11, 12, 13, 16);
	     */
	    sql = "update " + updateTable + " updatetable inner join "
		+ "(select distinct " + matchId + " from " + joinTable + ") jointable on "
		+ "jointable." + matchId + " = updatetable.id "
		+ "set statusId = :statusId "
		+ "where (statusId is null or statusId not in (:transRELId));";
	  
	    Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setParameter("statusId", statusId)
		.setParameterList("transRELId", transRELId);

	    updateData.executeUpdate();

	} catch (Exception ex) {
	    System.err.println("updateStatusForErrorTrans for " + inboundOutbound + " Batch (Id: " + batchId + ") " + ex.getCause());
	    ex.printStackTrace();
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void nullForCWCol(Integer configId, Integer batchId, boolean foroutboundProcessing) {

	String sql;
	Integer id = batchId;
	String updateTable = "transactiontranslatedout_"+batchId;
	String joinTable = "transactionoutrecords_"+batchId;

	if (foroutboundProcessing == false) {
	    updateTable = "transactiontranslatedin_"+batchId;
	    joinTable = "transactioninrecords_"+batchId;
	}
	
	sql = "update " + updateTable + " "
	    + "set forcw = null "
	    + "where configId = :configId and (statusId is null or statusId not in (:transRELId))";
	
	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
	    .setParameterList("transRELId", transRELId)
	    .setParameter("configId", configId);
	
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("nullForCWCol " + ex.getCause());
	    ex.printStackTrace();
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBatch(Integer batchId) throws Exception {

	/* Delete all the stored records */
	String deleteSQL = "";
	Query deleteQuery;
	deleteSQL += "DROP TABLE IF EXISTS `transactionindetailauditerrors_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedlistin_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedin_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactioninrecords_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactioninerrors_" + batchId + "`;";
	deleteSQL += "delete from restapimessagesin where batchUploadId = " + batchId + ";";
	deleteSQL += "delete from wsmessagesin where batchUploadId = " + batchId + ";";
	deleteSQL += "delete from useractivity where batchUploadId = " + batchId + ";";
	deleteSQL += "delete from uploadbatchrunlogs where batchId = " + batchId + ";";
	deleteSQL += "delete from batchdownloads where batchUploadId = " + batchId + ";";
	deleteSQL += "delete from batchretry where batchUploadId = " + batchId + ";";
	deleteSQL += "delete a.* from batchdlretry a inner join batchdownloads b on a.batchDownloadId = b.id where b.batchUploadId = " + batchId + ";";
	deleteSQL += "delete a.* from restapimessagesout a inner join batchdownloads b on a.batchDownloadId = b.id where b.batchUploadId = " + batchId + ";";
	deleteSQL += "delete a.* from wsmessagesout a inner join batchdownloads b on a.batchDownloadId = b.id where b.batchUploadId = " + batchId + ";";
	deleteSQL += "delete from batchuploadauditerrors where batchUploadId = " + batchId + ";";
	deleteSQL += "delete from batchUploads where id = " + batchId + ";";
	
	deleteQuery = sessionFactory.getCurrentSession().createSQLQuery(deleteSQL);
	deleteQuery.executeUpdate();

    }
 
   @Override
    @Transactional(readOnly = true)
    public Integer getRecordCountForTable(String tableName, String colName, int matchId) throws Exception {
	String sql = "select count(id) as total from " + tableName + " where " + colName + " = :matchId limit 1";
	Query query = sessionFactory
		.getCurrentSession()
		.createSQLQuery(sql).addScalar("total", StandardBasicTypes.INTEGER);
	query.setParameter("matchId", matchId);
	return (Integer) query.list().get(0);

    }

    /**
     * The 'updateBatchUpload' function will update the new batch.
     *
     * @param batchUpload The object that will hold the new batch info
     *
     * @table batchUploads
     *
     * @return This function returns the batchId for the newly inserted batch
     */
    @Override
    @Transactional(readOnly = false)
    public void updateBatchUpload(batchUploads batchUpload) throws Exception {
	sessionFactory.getCurrentSession().update(batchUpload);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Integer> getConfigIdsForBatchOnly(int batchUploadId) {
	try {
	    String sql = "select distinct configId from transactioninrecords_"+batchUploadId;
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	    List<Integer> configIds = query.list();
	    return configIds;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("getConfigIdsForBatchOnly " + ex.getCause());
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void createBatchTables(int batchUploadId, int configId) {
	try {

	    List<configurationFormFields> configFormFields = configurationtransportmanager.getConfigurationFields(configId, 0);

	    StringBuilder tableFields = new StringBuilder();
	
	    if (configFormFields != null) {
		if (!configFormFields.isEmpty()) {
		     configFormFields.forEach(field -> {
			tableFields.append("F").append(field.getFieldNo()).append(" text").append(",");
		    });
		}
	    }

	    //Create the transactioninrecords_batchUploadId table
	    String transactionInRecordsTable = "DROP TABLE IF EXISTS `transactioninrecords_" + batchUploadId + "`; CREATE TABLE `transactioninrecords_" + batchUploadId + "` (";
	    transactionInRecordsTable += tableFields;

	    transactionInRecordsTable += "id int(11) NOT NULL AUTO_INCREMENT," 
		    + "batchUploadId int(11) DEFAULT NULL," 
		    + "configId int(11) NOT NULL,"
		    + "dateCreated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
		    + "PRIMARY KEY (`id`),"
		    + "KEY `tirFK_idx` (`batchUploadId`)"
		    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    Query query = sessionFactory.getCurrentSession().createSQLQuery(transactionInRecordsTable);
	    query.executeUpdate();
	    
	    //Create the transactiontranslatedin_batchUploadId table
	    String transactionTranslatedInTable = "DROP TABLE IF EXISTS `transactiontranslatedin_" + batchUploadId + "`; CREATE TABLE `transactiontranslatedin_" + batchUploadId + "` ("
		    + "id int(11) NOT NULL AUTO_INCREMENT,"
		    + "transactionInRecordsId int(11) NOT NULL,"
		    + "configId int(11) NOT NULL,"
		    + "batchUploadId int(11) DEFAULT NULL,"
		    + "statusId int(11) DEFAULT NULL,"
		    + "dateCreated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
		    + "forCW text," + tableFields;

	    transactionTranslatedInTable += "PRIMARY KEY (`id`),"
		    + "UNIQUE KEY `transactionInRecordsId_UNIQUE` (`transactionInRecordsId`),"
		    + "KEY `ttiConfigId_idx` (`configId`,`batchUploadId`),"
		    + "KEY `ttibatchId` (`batchUploadId`)"
		    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createSQLQuery(transactionTranslatedInTable);
	    query.executeUpdate();
	    
	    //Create the transactioninerrors_batchUploadId table
	    String transactionInErrorsTable = "DROP TABLE IF EXISTS `transactioninerrors_" + batchUploadId + "`; CREATE TABLE `transactioninerrors_" + batchUploadId + "` ("
		    + "`id` int(11) NOT NULL AUTO_INCREMENT,"
		    + "`batchUploadId` int(11) NOT NULL,"
		    + "`configId` int(11) DEFAULT NULL,"
		    + "`transactionInRecordsId` int(11) DEFAULT NULL,"
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
		    + "KEY `batchUploadIdFK_idx` (`batchUploadId`),"
		    + "KEY `configFFFKId_idx` (`fieldNo`),"
		    + "KEY `errorIdFK_idx` (`errorId`),"
		    + "KEY `tieVTFK_idx` (`validationTypeId`),"
		    + "KEY `tieMacroFK_idx` (`macroId`),"
		    + "KEY `tieCWIDFK_idx` (`cwId`),"
		    + "KEY `transInId` (`transactionInRecordsId`),"
		    + "CONSTRAINT `batchUploadId_"+batchUploadId+"_FK` FOREIGN KEY (`batchUploadId`) REFERENCES `batchuploads` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
		    + "CONSTRAINT `tieCWID_"+batchUploadId+"_FK` FOREIGN KEY (`cwId`) REFERENCES `crosswalks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
		    + "CONSTRAINT `tieMacro_"+batchUploadId+"_FK` FOREIGN KEY (`macroId`) REFERENCES `macro_names` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
		    + "CONSTRAINT `tieVT_"+batchUploadId+"_FK` FOREIGN KEY (`validationTypeId`) REFERENCES `ref_validationtypes` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
		    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createSQLQuery(transactionInErrorsTable);
	    query.executeUpdate();

	    //Create the transactiontranslatedlistintable_batchUploadId table
	    String transactionTranslatedListInTable = "DROP TABLE IF EXISTS `transactiontranslatedlistin_" + batchUploadId + "`; CREATE TABLE `transactiontranslatedlistin_" + batchUploadId + "` ("
		    + "`id` int(11) NOT NULL AUTO_INCREMENT,"
		    + "`batchUploadId` int(11) NOT NULL,"
		    + "`configId` int(11) DEFAULT NULL,"
		    + "`transactionInRecordsId` int(11) NOT NULL,"
		    + "`concatKey` varchar(75) DEFAULT NULL,"
		    + "`inValue` text,"
		    + "`translatedValue` text,"
		    + "`translateIdToKeep` int(11) DEFAULT NULL,"
		    + "`fCol` int(11) DEFAULT NULL,"
		    + " PRIMARY KEY (`id`),"
		    + " KEY `inConcatKey` (`concatKey`),"
		    + " KEY `ttliBatchId` (`batchUploadId`)"
		    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createSQLQuery(transactionTranslatedListInTable);
	    query.executeUpdate();
	    
	    //Create the transactionindetailauditerrors_batchUploadId table
	    String transactionindetailauditerrorsTable = "DROP TABLE IF EXISTS `transactionindetailauditerrors_" + batchUploadId + "`; CREATE TABLE `transactionindetailauditerrors_" + batchUploadId + "` (" 
		    + "`id` int(11) NOT NULL AUTO_INCREMENT," 
		    + "`batchUploadId` int(11) NOT NULL," 
		    + "`configId` int(11) NOT NULL," 
		    + "`transactionInRecordsId` int(11) NOT NULL," 
		    + "`fieldNo` int(11) NOT NULL," 
		    + "`fieldName` varchar(45) DEFAULT NULL," 
		    + "`errorId` int(11) NOT NULL," 
		    + "`errorDetails` varchar(200) DEFAULT NULL COMMENT 'This field is used to update cw name, validation type name, macro name'," 
		    + "`errorData` text," 
		    + "`reportField1Data` varchar(45) DEFAULT NULL," 
		    + "`reportField2Data` varchar(45) DEFAULT NULL," 
		    + "`reportField3Data` varchar(45) DEFAULT NULL," 
		    + "`reportField4Data` varchar(45) DEFAULT NULL," 
		    + "`transactionInErrorId` int(11) DEFAULT '0'," 
		    + " PRIMARY KEY (`id`)," 
		    + " KEY `auditKeyError_idx` (`batchUploadId`)," 
		    + " CONSTRAINT `auditErrorKey_"+batchUploadId+"_FK` FOREIGN KEY (`batchUploadId`) REFERENCES `batchuploads` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION" 
		    +") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createSQLQuery(transactionindetailauditerrorsTable);
	    query.executeUpdate();
	    

	} catch (Exception ex) {
	    System.err.println("Create Batch Upload tables for batch (Id: " + batchUploadId +") "+ ex.getCause());
	}
    }
    
    @Override
    @Transactional(readOnly = false)
    public void deleteBatchTransactionTables(Integer batchId) throws Exception {

	/* Delete all the stored records */
	String deleteSQL = "";
	Query deleteQuery;
	deleteSQL += "DROP TABLE IF EXISTS `transactionindetailauditerrors_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedlistin_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedin_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactioninrecords_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactioninerrors_" + batchId + "`;";

	deleteQuery = sessionFactory.getCurrentSession().createSQLQuery(deleteSQL);
	deleteQuery.executeUpdate();

    }
    
    @Override
    @Transactional(readOnly = true) 
    public List<batchDownloads> findBatchesToCleanUp() throws Exception {
	
	String sql = "select * from ("
		+ "select count(a.id) as totalBatchDownloads, deliveredBatches.totalDelivered, a.* "
		+ "from batchdownloads a inner join information_schema.tables join ("
		+ "select count(id) as totalDelivered, batchUploadId "
		+ "from batchdownloads where statusId = 28 or statusId = 32 or statusId = 21 group by batchUploadId) deliveredBatches "
		+ "on deliveredBatches.batchUploadId = a.batchUploadId "
		+ "where table_name = concat('transactiontranslatedin_',a.batchUploadId) "
		+ "group by a.batchUploadId ) as batchesToClear "
		+ "where totalBatchDownloads = totalDelivered";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.addScalar("batchUploadId", StandardBasicTypes.INTEGER)
		.addScalar("totalBatchDownloads", StandardBasicTypes.INTEGER)
		.setResultTransformer(Transformers.aliasToBean(batchDownloads.class));
	
	return query.list();
    }
    
    @Override
    @Transactional(readOnly = false)
    public void batchUploadTableCleanUp(List<batchDownloads> batchesToCleanup) throws Exception {
	
	if(batchesToCleanup != null) {
	    
	    if(!batchesToCleanup.isEmpty()) {
		
		String deleteSQL = "";
		Query deleteQuery;
		
		for(batchDownloads batch : batchesToCleanup) {
		     
		    if(batch.getBatchUploadId() > 0) {
			deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedin_" + batch.getBatchUploadId() + "`;";
			deleteSQL += "DROP TABLE IF EXISTS `transactionindetailauditerrors_" + batch.getBatchUploadId() + "`;";
			deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedlistin_" + batch.getBatchUploadId() + "`;";
			deleteSQL += "DROP TABLE IF EXISTS `transactioninrecords_" + batch.getBatchUploadId() + "`;";
			deleteSQL += "DROP TABLE IF EXISTS `transactioninerrors_" + batch.getBatchUploadId() + "`;";
		    }
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
    @SuppressWarnings("unchecked")
    public List<configurationConnection> getPassThruBatchTargets(Integer batchId, boolean active) {
	try {
	    String sql = ("select sourceConfigId, targetConfigId, configurationconnections.id "
		+ "from configurations, configurationconnections "
		+ "where sourceconfigId in (select configId from batchUploads where id = :batchId) "
		+ "and targetConfigId = configurations.id");
	    
	    if (active) {
		sql = sql + " and configurations.status = 1 and configurationconnections.status = 1 and messageTypeId = 0";
	    }
	    sql = sql + " order by sourceConfigId;";
	    
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
		    Transformers.aliasToBean(configurationConnection.class));
	    query.setParameter("batchId", batchId);

	    List<configurationConnection> cc = query.list();
	    return cc;
	} catch (Exception ex) {
	    System.err.println("getBatchTargets " + ex.getCause());
	    return null;
	}
    }
    
    @Override
    @Transactional(readOnly = false)
    public void resetTransactionCounts(Integer batchUploadId) throws Exception {
	String sql = "update batchUploads set totalRecordCount = 0, errorRecordCount = 0 where id = :batchUploadId";
	
	Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql).setParameter("batchUploadId", batchUploadId);
	
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("resetTransactionCounts " + ex.getCause());
	}
    }
    
    @Override
    @Transactional(readOnly = false)
    public void deleteMoveFileLogById(Integer logId) throws Exception {
	Query deletMoveFilesLog = sessionFactory.getCurrentSession().createQuery("delete from MoveFilesLog where id = :id");
	deletMoveFilesLog.setParameter("id", logId);

	deletMoveFilesLog.executeUpdate();

    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MoveFilesLog> existingMoveFileLogs(Integer statusId, Integer methodId) throws Exception {
	
	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MoveFilesLog.class);
	criteria.add(Restrictions.eq("statusId", statusId));
	criteria.add(Restrictions.eq("transportMethodId", methodId));
	return criteria.list();
    }
    
    /**
     * The 'getBatchDetailsByOriginalFileName' will return a batch by name
     *
     * @param originalFileName The name of the batch to search form.
     *
     * @return This function will return a batchUpload object
     */
    @Override
    @Transactional(readOnly = true)
    public batchUploads getBatchDetailsByOriginalFileName(String originalFileName) throws Exception {
	Query query = sessionFactory.getCurrentSession().createQuery("from batchUploads where originalFileName = :originalFileName");
	query.setParameter("originalFileName", originalFileName);

	if (query.list().size() > 1) {
	    return null;
	} else {
	    return (batchUploads) query.uniqueResult();
	}

    }
}
