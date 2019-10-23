package com.hel.ut.dao.impl;

import com.hel.ut.dao.RestAPIDAO;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hel.ut.model.RestAPIMessagesIn;
import com.hel.ut.model.RestAPIMessagesOut;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchUploads;
import java.text.SimpleDateFormat;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

/**
 * The WebServicesDAOImpl class will implement the DAO access layer to handle updates for web services messages
 *
 *
 * @author gchan
 *
 */
@Repository
public class RestAPIDAOImpl implements RestAPIDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    private SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<RestAPIMessagesIn> getRestAPIMessagesInList(Date fromDate, Date toDate,Integer fetchSize, String batchName) throws Exception {

        Integer batchUploadId = 0;
	
	if(!"".equals(batchName)) {
	    Criteria findBatchUpload = sessionFactory.getCurrentSession().createCriteria(batchUploads.class);
	    findBatchUpload.add(Restrictions.eq("utBatchName",batchName));
	    findBatchUpload.setMaxResults(1);
	    
	    if(!findBatchUpload.list().isEmpty()) {
		batchUploads batchUploadDetails = (batchUploads) findBatchUpload.list().get(0);
		batchUploadId = batchUploadDetails.getId();
	    }
	}
	
	String sqlQuery = "select id, orgId, statusId, errorId, dateCreated, batchUploadId, configId "
		    + "from restapimessagesin "
		    + "where id > 0";
	
	if(!"".equals(fromDate)) {
	    sqlQuery += " and dateCreated >= '"+mysqlDateFormat.format(fromDate)+"'";
	}
	
	 if (!"".equals(toDate)) {
            sqlQuery += " and dateCreated < '"+mysqlDateFormat.format(toDate)+"'";
        }
	 
	if(batchUploadId > 0) {
	    sqlQuery += " and batchUploadId = '"+batchUploadId+"'";
	}
	 
	sqlQuery += " order by dateCreated desc";
	
	if(fetchSize > 0) {
	    sqlQuery += " limit " + fetchSize;
	}
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
		.addScalar("id", StandardBasicTypes.INTEGER)
                .addScalar("orgId", StandardBasicTypes.INTEGER)
                .addScalar("statusId", StandardBasicTypes.INTEGER)
                .addScalar("errorId", StandardBasicTypes.INTEGER)
                .addScalar("dateCreated", StandardBasicTypes.DATE)
                .addScalar("batchUploadId", StandardBasicTypes.INTEGER)
		.addScalar("configId", StandardBasicTypes.INTEGER)
		.setResultTransformer(Transformers.aliasToBean(RestAPIMessagesIn.class));

	List<RestAPIMessagesIn> apimessagesin = query.list();
	
        return apimessagesin;

    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public RestAPIMessagesIn getRestAPIMessagesIn(Integer messageId) throws Exception {
        Criteria findApiMessageIn = sessionFactory.getCurrentSession().createCriteria(RestAPIMessagesIn.class);
        findApiMessageIn.add(Restrictions.eq("id", messageId));
        List<RestAPIMessagesIn> apiMessageList = findApiMessageIn.list();
        if (apiMessageList.size() == 1) {
            return apiMessageList.get(0);
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<RestAPIMessagesOut> getRestAPIMessagesOutList(Date fromDate, Date toDate,Integer fetchSize, String batchName) throws Exception {

        Integer batchDownloadId = 0;
	
	if(!"".equals(batchName)) {
	    Criteria findBatchDownload = sessionFactory.getCurrentSession().createCriteria(batchDownloads.class);
	    findBatchDownload.add(Restrictions.eq("utBatchName",batchName));
	    findBatchDownload.setMaxResults(1);
	    
	    if(!findBatchDownload.list().isEmpty()) {
		batchDownloads batchDownloadDetails = (batchDownloads) findBatchDownload.list().get(0);
		batchDownloadId = batchDownloadDetails.getId();
	    }
	}
	
	Criteria findRestOut = sessionFactory.getCurrentSession().createCriteria(RestAPIMessagesOut.class);

        if (!"".equals(fromDate)) {
            findRestOut.add(Restrictions.ge("dateCreated", fromDate));
        }

        if (!"".equals(toDate)) {
            findRestOut.add(Restrictions.lt("dateCreated", toDate));
        }
	
	if(batchDownloadId > 0) {
	    findRestOut.add(Restrictions.eq("batchDownloadId",batchDownloadId));
	}

        findRestOut.addOrder(Order.desc("dateCreated"));

        if (fetchSize > 0) {
            findRestOut.setMaxResults(fetchSize);
        }
        return findRestOut.list();

    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<RestAPIMessagesIn> getRestAPIMessagesInListPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
	
	String dateSQLString = "";
	String dateSQLStringTotal = "";
	
	if(!"".equals(fromDate)) {
	    dateSQLString += "a.dateCreated between '"+mysqlDateFormat.format(fromDate)+" 00:00:00' ";
	    dateSQLStringTotal += "dateCreated between '"+mysqlDateFormat.format(fromDate)+" 00:00:00' ";
	    
	    if(!"".equals(toDate)) {
		dateSQLString += "AND '"+mysqlDateFormat.format(toDate)+" 00:00:00'";
		dateSQLStringTotal += "AND '"+mysqlDateFormat.format(toDate)+" 00:00:00'";
	    }
	    else {
		dateSQLString += "AND '"+mysqlDateFormat.format(fromDate)+" 23:59:59'";
		dateSQLStringTotal += "AND '"+mysqlDateFormat.format(fromDate)+" 23:59:59'";
	    }
	}
	else {
	    if(!"".equals(toDate)) {
		dateSQLString += "a.dateCreated between '"+mysqlDateFormat.format(toDate)+" 00:00:00' ";
		dateSQLString += "AND '"+mysqlDateFormat.format(toDate)+" 23:59:59'";
	    }
	    else {
		dateSQLString += "a.id > 0";
	    }
	}
	
	
	String sqlQuery = "select id, statusName, errorDisplayText, orgName, dateCreated, configId, batchUploadId, batchName, totalMessages "
		+ "from ("
		+ "select a.id, a.batchUploadId, a.dateCreated, a.configId, c.orgName,"
		+ "CASE WHEN a.statusId = 1 THEN 'To be processed' WHEN a.statusId = 2 THEN 'Processed' ELSE 'Rejected' END as statusName,"
		+ "IFNULL(b.displayText, \"N/A\") as errorDisplayText, IFNULL(d.utBatchName,\"\") as batchName,"
		+ "(select count(id) as total from restapimessagesin where "+dateSQLStringTotal+") as totalMessages "
		+ "FROM restapimessagesin a left outer join "
		+ "lu_errorCodes b on b.id = a.errorId inner join "
		+ "organizations c on c.id = a.orgId left outer join  "
		+ "batchuploads d on d.id = a.batchUploadId "
		+ "where " + dateSQLString + ") as messagesIn ";
	
	if(!"".equals(searchTerm)){
	    sqlQuery += " where ("
	    + "id like '%"+searchTerm+"%' "
	    + "OR configId like '%"+searchTerm+"%' "
	    + "OR orgName like '%"+searchTerm+"%' "
	    + "OR batchName like '%"+searchTerm+"%' "
	    + "OR statusName like '%"+searchTerm+"%' "
	    + "OR dateCreated like '%"+searchTerm+"%' "
	    + "OR errorDisplayText like '%"+searchTerm+"%'"
	    + ") ";
	}	
	
	sqlQuery += "order by "+sortColumnName+" "+sortDirection;
        sqlQuery += " limit " + displayStart + ", " + displayRecords;
	
	System.out.println(sqlQuery);
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
	    .addScalar("id", StandardBasicTypes.INTEGER)
	    .addScalar("statusName", StandardBasicTypes.STRING)
	    .addScalar("errorDisplayText", StandardBasicTypes.STRING)
	    .addScalar("orgName", StandardBasicTypes.STRING)
	    .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
	    .addScalar("batchUploadId", StandardBasicTypes.INTEGER)
	    .addScalar("configId", StandardBasicTypes.INTEGER)
	    .addScalar("batchName", StandardBasicTypes.STRING)
	    .addScalar("totalMessages", StandardBasicTypes.INTEGER)
	    .setResultTransformer(Transformers.aliasToBean(RestAPIMessagesIn.class));
	
	List<RestAPIMessagesIn> apimessagesin = query.list();
	
        return apimessagesin;

    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<RestAPIMessagesOut> getRestAPIMessagesOutListPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
	
	String dateSQLString = "";
	String dateSQLStringTotal = "";
	
	if(!"".equals(fromDate)) {
	    dateSQLString += "a.dateCreated between '"+mysqlDateFormat.format(fromDate)+" 00:00:00' ";
	    dateSQLStringTotal += "dateCreated between '"+mysqlDateFormat.format(fromDate)+" 00:00:00' ";
	    
	    if(!"".equals(toDate)) {
		dateSQLString += "AND '"+mysqlDateFormat.format(toDate)+" 00:00:00'";
		dateSQLStringTotal += "AND '"+mysqlDateFormat.format(toDate)+" 00:00:00'";
	    }
	    else {
		dateSQLString += "AND '"+mysqlDateFormat.format(fromDate)+" 23:59:59'";
		dateSQLStringTotal += "AND '"+mysqlDateFormat.format(fromDate)+" 23:59:59'";
	    }
	}
	else {
	    if(!"".equals(toDate)) {
		dateSQLString += "a.dateCreated between '"+mysqlDateFormat.format(toDate)+" 00:00:00' ";
		dateSQLString += "AND '"+mysqlDateFormat.format(toDate)+" 23:59:59'";
	    }
	    else {
		dateSQLString += "a.id > 0";
	    }
	}
	
	
	String sqlQuery = "select id, statusName, errorDisplayText, orgName, dateCreated, configId, batchDownloadId, batchName, totalMessages "
		+ "from ("
		+ "select a.id, a.batchDownloadId, a.dateCreated, a.configId, c.orgName,"
		+ "CASE WHEN a.statusId = 1 THEN 'To be processed' WHEN a.statusId = 2 THEN 'Processed' ELSE 'Rejected' END as statusName,"
		+ "IFNULL(b.displayText, \"N/A\") as errorDisplayText, IFNULL(d.utBatchName,\"\") as batchName,"
		+ "(select count(id) as total from restapimessagesout where "+dateSQLStringTotal+") as totalMessages "
		+ "FROM restapimessagesout a left outer join "
		+ "lu_errorCodes b on b.id = a.errorId inner join "
		+ "organizations c on c.id = a.orgId left outer join  "
		+ "batchdownloads d on d.id = a.batchDownloadId "
		+ "where " + dateSQLString + ") as messagesIn ";
	
	if(!"".equals(searchTerm)){
	    sqlQuery += " where ("
	    + "id like '%"+searchTerm+"%' "
	    + "OR configId like '%"+searchTerm+"%' "
	    + "OR orgName like '%"+searchTerm+"%' "
	    + "OR batchName like '%"+searchTerm+"%' "
	    + "OR statusName like '%"+searchTerm+"%' "
	    + "OR dateCreated like '%"+searchTerm+"%' "
	    + "OR errorDisplayText like '%"+searchTerm+"%'"
	    + ") ";
	}	
	
	sqlQuery += "order by "+sortColumnName+" "+sortDirection;
        sqlQuery += " limit " + displayStart + ", " + displayRecords;
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
	    .addScalar("id", StandardBasicTypes.INTEGER)
	    .addScalar("statusName", StandardBasicTypes.STRING)
	    .addScalar("errorDisplayText", StandardBasicTypes.STRING)
	    .addScalar("orgName", StandardBasicTypes.STRING)
	    .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
	    .addScalar("batchDownloadId", StandardBasicTypes.INTEGER)
	    .addScalar("configId", StandardBasicTypes.INTEGER)
	    .addScalar("batchName", StandardBasicTypes.STRING)
	    .addScalar("totalMessages", StandardBasicTypes.INTEGER)
	    .setResultTransformer(Transformers.aliasToBean(RestAPIMessagesOut.class));
	
	List<RestAPIMessagesOut> apimessagesout = query.list();
	
        return apimessagesout;

    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public RestAPIMessagesOut getRestAPIMessagesOut(Integer messageId) throws Exception {
        Criteria findApiMessageOut = sessionFactory.getCurrentSession().createCriteria(RestAPIMessagesOut.class);
        findApiMessageOut.add(Restrictions.eq("id", messageId));
        List<RestAPIMessagesOut> apiMessageList = findApiMessageOut.list();
        if (apiMessageList.size() == 1) {
            return apiMessageList.get(0);
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public RestAPIMessagesIn getRestAPIMessagesInByBatchId(Integer batchId) throws Exception {
        Criteria findApiMessageIn = sessionFactory.getCurrentSession().createCriteria(RestAPIMessagesIn.class);
        findApiMessageIn.add(Restrictions.eq("batchUploadId", batchId));
        List<RestAPIMessagesIn> apiMessageList = findApiMessageIn.list();
        if (apiMessageList.size() == 1) {
            return apiMessageList.get(0);
        } else {
            return null;
        }
    }

}
