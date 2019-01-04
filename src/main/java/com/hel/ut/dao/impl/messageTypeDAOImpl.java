package com.hel.ut.dao.impl;

import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.hel.ut.dao.messageTypeDAO;
import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.validationType;
import com.hel.ut.reference.fileSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.criterion.Disjunction;

/**
 * The brochureDAOImpl class will implement the DAO access layer to handle updates for organization brochures
 *
 *
 * @author chadmccue
 *
 */
@Repository
public class messageTypeDAOImpl implements messageTypeDAO {

    @Resource(name = "myProps")
    private Properties myProps;

    @Autowired
    private SessionFactory sessionFactory;

    
    /**
     * The 'findTotalCrosswalks' function will return the total number of generic crosswalks in the system
     *
     * @param orgId Will pass the orgId this will help determine if I want all crosswalks or generic system only crosswalks
     *
     * @Table	crosswalks
     *
     *
     * @Return	This function will return the total number of generic crosswalks set up in the system
     */
    @Override
    @Transactional(readOnly = true)
    public double findTotalCrosswalks(int orgId) {

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Crosswalks.class);

        if (orgId == 0) {
            criteria.add(Restrictions.eq("orgId", 0));
        } else {
            Disjunction or = Restrictions.disjunction();
            or.add(Restrictions.eq("orgId", 0));
            or.add(Restrictions.eq("orgId", orgId));
            criteria.add(or);

        }

        double totalCrosswalks = (double) criteria.list().size();

        return totalCrosswalks;
    }

    /**
     * The 'getInformationTables' function will return a list of all available information tables where we can associate fields to an actual table and column.
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public List getInformationTables() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT distinct table_name FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + myProps.getProperty("schemaNameIL") + "' and TABLE_NAME LIKE 'message\\_%'");

        return query.list();
    }

    /**
     * The 'getAllTables' function will return a list of all available tables where we can use to select which table to auto populate a form field.
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public List getAllTables() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT distinct table_name FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + myProps.getProperty("schemaNameIL") + "'");

        return query.list();
    }

    /**
     * The 'getTableColumns' function will return a list of columns from the passed in table name
     *
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public List getTableColumns(String tableName) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + myProps.getProperty("schemaNameIL") + "' AND TABLE_NAME = :tableName and COLUMN_NAME not in ('id', 'dateCreated', 'transactionInId') order by COLUMN_NAME")
                .setParameter("tableName", tableName);

        return query.list();
    }

    /**
     * The 'getValidationTypes' function will return a list of available field validation types
     *
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public List getValidationTypes() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, validationType FROM ref_validationTypes order by id asc");

        return query.list();
    }

    /**
     * The 'getValidationById' function will return a validation by the passed in Id.
     *
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public String getValidationById(int id) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT validationType FROM ref_validationTypes where id = :id");
        query.setParameter("id", id);

        String validationType = (String) query.uniqueResult();

        return validationType;
    }

    /**
     * The 'getDelimiters' function will return a list of available file delimiters
     *
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public List getDelimiters() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, delimiter FROM ref_delimiters order by delimiter asc");

        return query.list();
    }

    /**
     * The 'getDelimiterChar' will return the actual character of the delimiter for the id passed into the function
     *
     * @param id	The id will hold the delimiter ID to retrieve its associated character
     *
     * @returns string
     */
    @Transactional(readOnly = true)
    public String getDelimiterChar(int id) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT delimChar FROM ref_delimiters where id = :id");
        query.setParameter("id", id);

        String delimChar = (String) query.uniqueResult();

        return delimChar;
    }

    /**
     * The 'getTotalFields' function will return the number of fields for a passed in message type.
     *
     * @Param messageTypeId	The message type to search
     *
     * @Return	Long	The total number of fields for the message type
     */
    @Transactional(readOnly = true)
    public Long getTotalFields(int messageTypeId) {

        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalFields from messageTypeFormFields where messageTypeId = :messageTypeId")
                .setParameter("messageTypeId", messageTypeId);

        Long totalFields = (Long) query.uniqueResult();

        return totalFields;

    }

    /**
     * The 'getCrosswalks' function will return the list of available crosswalks to associate a message types to. This function will only return crosswalks not associated to a specific organization.
     *
     * @param page	The current crosswalk page
     * @param	maxResults	The maximum number of crosswalks to return from each query
     * @param	orgId	The organization id (default 0)
     *
     * @Table	crosswalks
     *
     * @Return	This function will return a list of crosswalks
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Crosswalks> getCrosswalks(int page, int maxResults, int orgId) {

        Query query = null;

        if (orgId == 0) {
            query = sessionFactory.getCurrentSession().createQuery("from Crosswalks where orgId = 0 order by name asc");
        } else {
            query = sessionFactory.getCurrentSession().createQuery("from Crosswalks where (orgId = 0 or orgId = :orgId) order by name asc");
            query.setParameter("orgId", orgId);
        }

        int firstResult = 0;

        //Set the parameters for paging
        //Set the page to load
        if (page > 1) {
            firstResult = (maxResults * (page - 1));
        }
        query.setFirstResult(firstResult);

        //Set the max results to display
        //If 0 is passed then we want all crosswalks
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        return query.list();

    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public Long checkCrosswalkName(String name, int orgId) {
        Query query = null;

        if (orgId > 0) {
            query = sessionFactory.getCurrentSession().createQuery("select count(id) as total from Crosswalks where name = :name and orgId = :orgId");
            query.setParameter("name", name);
            query.setParameter("orgId", orgId);
        } else {
            query = sessionFactory.getCurrentSession().createQuery("select count(id) as total from Crosswalks where name = :name");
            query.setParameter("name", name);
        }

        Long cwId = (Long) query.uniqueResult();

        return cwId;
    }

    /**
     * The 'createCrosswalk" function will create the new crosswalk
     *
     * @Table	crosswalks
     *
     * @param	crosswalkDetails	This will hold the crosswalk object from the form
     *
     * @return The function will return the id of the new crosswalk
     *
     */
    @Override
    @Transactional(readOnly = false)
    public Integer createCrosswalk(Crosswalks crosswalkDetails) {
        Integer lastId = null;

        lastId = (Integer) sessionFactory.getCurrentSession().save(crosswalkDetails);

        return lastId;
    }

    /**
     * The 'getCrosswalk' function will return a single crosswalk object based on the id passed in.
     *
     * @param	cwId	This will be id to find the specific crosswalk
     *
     * @return	The function will return a crosswalk object
     */
    @Override
    @Transactional(readOnly = true)
    public Crosswalks getCrosswalk(int cwId) {
        return (Crosswalks) sessionFactory.getCurrentSession().get(Crosswalks.class, cwId);
    }

    /**
     * The 'getDelimiters' function will return a list of available file delimiters
     *
     * @param	cwId	This will be the id of the crosswalk to return the associated data elements for
     *
     * @return	The function will return a list of data objects for the crosswalk
     *
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public List getCrosswalkData(int cwId) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT sourceValue, targetValue, descValue FROM rel_crosswalkData where crosswalkId = :crosswalkid order by id asc");
        query.setParameter("crosswalkid", cwId);

        return query.list();
    }


    /**
     * The 'getFieldName' function will return the name of a field based on the fieldId passed in. This is used for display purposes to show the actual field lable instead of a field name.
     *
     * @param fieldId	This will hold the id of the field to retrieve
     *
     * @Return This function will return a string (field name)
     */
    @Override
    @Transactional(readOnly = true)
    public String getFieldName(int fieldId) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT fieldDesc FROM messageTypeFormFields where id = :fieldId")
                .setParameter("fieldId", fieldId);

        String fieldName = (String) query.uniqueResult();

        return fieldName;
    }

    /**
     * The 'getCrosswalkName' function will return the name of a crosswalk based on the id passed in.
     *
     * @param cwId	This will hold the id of the crosswalk to retrieve
     *
     * @Return This function will return a string (crosswalk name).
     */
    @Override
    @Transactional(readOnly = true)
    public String getCrosswalkName(int cwId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Crosswalks.class);
        criteria.add(Restrictions.eq("id", cwId));

        Crosswalks cwDetails = (Crosswalks) criteria.uniqueResult();

        String cwName = "";

        if (cwDetails.getOrgId() > 0) {
            cwName = cwDetails.getName() + " (Org Specific)";
        } else {
            cwName = cwDetails.getName();
        }

        return cwName;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<validationType> getValidationTypes1() {
        Query query = sessionFactory.getCurrentSession().createQuery("from validationType order by id asc");
        return query.list();
    }
    
    @Transactional(readOnly = false)
    public void executeSQLStatement(String sqlStmt) {
	if(sqlStmt != null) {
	    if(!"".equals(sqlStmt)) {
		 //Need to insert all the fields into the crosswalk data Fields table
                Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlStmt);

                query.executeUpdate();

	    }
	}
    }
    
    
    /**
     * The 'updateCrosswalk" function will update the existing crosswalk
     *
     * @Table	crosswalks
     *
     * @param	crosswalkDetails	This will hold the crosswalk object from the form
     *
     * @return The function will return the id of the new crosswalk
     *
     */
    @Transactional(readOnly = false)
    public void updateCrosswalk(Crosswalks crosswalkDetails) {
        sessionFactory.getCurrentSession().update(crosswalkDetails);
    }

}
