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
import com.hel.ut.model.configuration;
import com.hel.ut.model.messageType;
import com.hel.ut.model.messageTypeDataTranslations;
import com.hel.ut.model.messageTypeFormFields;
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
     * The 'createMessageType" function will create the new brochure
     *
     * @Table	messageTypes
     *
     * @param	messageType	This will hold the messageType object from the form
     *
     * @return The function will return the id of the new messageType
     *
     */
    @Override
    @Transactional(readOnly = false)
    public Integer createMessageType(messageType messageType) {
        Integer lastId = null;

        lastId = (Integer) sessionFactory.getCurrentSession().save(messageType);

        return lastId;
    }

    /**
     * The 'updateMessageType' function will update the selected message type with the changes entered into the form.
     *
     * @param	messageType	This will hold the messagetype object from the message type form
     *
     * @return the function does not return anything
     */
    @Override
    @Transactional(readOnly = false)
    public void updateMessageType(messageType messageType) {
        sessionFactory.getCurrentSession().saveOrUpdate(messageType);
    }

    /**
     * The 'deleteMessageType' function will delete the selected brochure
     *
     * @param	messageTypeId	This will hold the id of the message type to delete
     *
     * @return the function does not return anything
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteMessageType(int messageTypeId) {
        Query deleteMessageType = sessionFactory.getCurrentSession().createQuery("delete from messageType where id = :messageTypeId");
        deleteMessageType.setParameter("messageTypeId", messageTypeId);
        deleteMessageType.executeUpdate();
    }

    /**
     * The 'getMessageTypeById' function will return a single message type object based on the messageTypeId passed in.
     *
     * @param	messageTypeId	This will be id to find the specific message type
     *
     * @return	The function will return a messageType object
     */
    @Override
    @Transactional(readOnly = true)
    public messageType getMessageTypeById(int messageTypeId) {
        return (messageType) sessionFactory.getCurrentSession().get(messageType.class, messageTypeId);
    }

    /**
     * The 'getMessageTypeById' function will return a single message type object based on the messageTypeId passed in.
     *
     * @param	messageTypeId	This will be id to find the specific message type
     *
     * @return	The function will return a messageType object
     */
    @Override
    @Transactional(readOnly = true)
    public messageType getMessageTypeByName(String name) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(messageType.class);
        criteria.add(Restrictions.like("name", name));
        return (messageType) criteria.uniqueResult();
    }

    /**
     * The 'getMessageTypes' function will return the list of message types in the system.
     *
     * @Table	messageTypes
     *
     * @Param	page	This will hold the current page to view maxResults	This will hold the total number of results to return back to the list page
     *
     * @Return	This function will return a list of message type objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<messageType> getMessageTypes() {

        Query query = sessionFactory.getCurrentSession().createQuery("from messageType order by name asc");

        return query.list();

    }

    /**
     * The 'getAvailableMessageTypes' function will return the list of message types in the system that have not already been set up for the passed in orgId.
     *
     * @Table	messageTypes
     *
     * @Param	orgId	This will hold the id of the selected organization
     *
     * @Return	This function will return a list of message type objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<messageType> getAvailableMessageTypes(int orgId) {

        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, name FROM messageTypes where status = 1 and id not in (select messageTypeId from configurations where orgId = :orgId)");
        query.setParameter("orgId", orgId);

        return query.list();
    }

    /**
     * The 'getAssociatedMessageTypes' function will return the list of message types in the system that have been set up for the passed in orgId.
     *
     * @Table	messageTypes
     *
     * @Param	orgId	This will hold the id of the selected organization
     *
     * @Return	This function will return a list of message type objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<messageType> getAssociatedMessageTypes(int orgId) {

        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, name FROM messageTypes where status = 1 and id in (select messageTypeId from configurations where orgId = :orgId) order by name asc");
        query.setParameter("orgId", orgId);

        return query.list();
    }

    /**
     * The 'getLatestMessageTypes' function will return the list of the latest message types in the system.
     *
     * @Table	messageTypes
     *
     * @Param	maxResults	This will hold the total number of results to return back to the page
     *
     * @Return	This function will return a list of message type objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<messageType> getLatestMessageTypes(int maxResults) {

        Query query = sessionFactory.getCurrentSession().createQuery("from messageType order by dateCreated desc");

        //Set the max results to display
        query.setMaxResults(maxResults);

        return query.list();

    }

    /**
     * The 'getActiveMessageTypes' function will return the list of the active message types in the system.
     *
     * @Table	messageTypes
     *
     * @Return	This function will return a list of active message type objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<messageType> getActiveMessageTypes() {

        Query query = sessionFactory.getCurrentSession().createQuery("from messageType where status = 1 order by name asc");

        return query.list();
    }

    /**
     * The 'findTotalMessageTypes' function will return the total number of message types in the system
     *
     * @Table	messageTypes
     *
     *
     * @Return	This function will return the total number of message types set up in the system
     */
    @Override
    @Transactional(readOnly = true)
    public Long findTotalMessageTypes() {

        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalMessageTypes from messageType");

        Long totalMessageTypes = (Long) query.uniqueResult();

        return totalMessageTypes;
    }

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
     * The 'getMessageTypeFields' function will return all the form fields associated to the selected message type.
     *
     * @Table messageTypeFormFields
     *
     * @Return This function will return a list of fields
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<messageTypeFormFields> getMessageTypeFields(int messageTypeId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(messageTypeFormFields.class)
                .add(Restrictions.eq("messageTypeId", messageTypeId))
                .addOrder(Order.asc("id"));

        return criteria.list();
    }

    /**
     * The 'updateMessageTypeFields' function will update the selected message type field mappings
     *
     * @Table messagTypeFormFields
     *
     * @Return This function does not return anything
     */
    @Override
    @Transactional(readOnly = false)
    public void updateMessageTypeFields(messageTypeFormFields formField) {
        sessionFactory.getCurrentSession().update(formField);
    }

    /**
     * The 'saveMessageTypeFields' function will save a new message type field. The function will also search for all configurations that is using this message type and add the field as NOT USED in the online form transport method. It would be up to the administrator to go in and mark the field as USED for what ever configuration will be using this new field.
     *
     * @Table messageTypeFormFields
     *
     * @Return This function does not return anything
     */
    @Override
    @Transactional(readOnly = false)
    public void saveMessageTypeFields(messageTypeFormFields formField) {
        Integer lastId = (Integer) sessionFactory.getCurrentSession().save(formField);

        //Need to find out all configurations that use this message type and add the new
        //form field to the online form configuration.
        Query query = sessionFactory.getCurrentSession().createQuery("from configuration where messageTypeId = :messageTypeId");
        query.setParameter("messageTypeId", formField.getMessageTypeId());

        List<configuration> configurations = query.list();

        for (configuration configuration : configurations) {
            //Need to get the transport detail id
            Query transportDetails = sessionFactory.getCurrentSession().createQuery("select id from configurationTransport where configId = :configId");
            transportDetails.setParameter("configId", configuration.getId());

            Integer transportDetailId = (Integer) transportDetails.uniqueResult();

            //Bulk insert the new fieldinto the configurationTransportDetails table for the online form
            Query bulkInsert = sessionFactory.getCurrentSession().createSQLQuery("INSERT INTO configurationFormFields (messageTypeFieldId, configId, transportDetailId, fieldNo, fieldDesc, fieldLabel, validationType, required, bucketNo, bucketDspPos, useField, saveToTableName, saveToTableCol, autoPopulateTableName, autoPopulateTableCol, fieldType) SELECT id, :configId, :transportDetailId, fieldNo,  fieldDesc, fieldLabel, validationType, required, bucketNo, bucketDspPos, 0, saveToTableName, saveToTableCol, autoPopulateTableName, autoPopulateTableCol, fieldType FROM messageTypeFormFields where id = :newfieldId");
            bulkInsert.setParameter("configId", configuration.getId());
            bulkInsert.setParameter("transportDetailId", transportDetailId);
            bulkInsert.setParameter("newfieldId", lastId);

            bulkInsert.executeUpdate();
        }
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
     * The 'getFieldTypes' function will return a list of available field types
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public List getFieldTypes() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, fieldType FROM ref_fieldTypes order by id asc");

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
     * The 'saveDataTranslations' function will save the submitted translations for the selected message type
     *
     * @param translations	the messagetypedatatranslations object
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void saveDataTranslations(messageTypeDataTranslations translations) {
        sessionFactory.getCurrentSession().save(translations);
    }

    /**
     * The 'deleteDataTranslations' function will remove all data translations for the passed in message type.
     *
     * @param	messageTypeId	The id of the message type to remove associated translations
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteDataTranslations(int messageTypeId) {
        Query deleteTranslations = sessionFactory.getCurrentSession().createQuery("delete from messageTypeDataTranslations where messageTypeId = :messageTypeId");
        deleteTranslations.setParameter("messageTypeId", messageTypeId);
        deleteTranslations.executeUpdate();
    }

    /**
     * The 'getMessgeTypeTranslations' function will return a list of data translations saved for the passed in message type.
     *
     * @param	messageTypeId	The id of the message type we want to return associated translations for.
     *
     * @return	This function will return a list of translations
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<messageTypeDataTranslations> getMessageTypeTranslations(int messageTypeId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from messageTypeDataTranslations where messageTypeId = :messageTypeId order by processOrder asc");
        query.setParameter("messageTypeId", messageTypeId);

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
    
    @Transactional(readOnly = false)
    public void loadExcelContents(Integer id, String fileName) {
	try {
            
            //Set the initial value of the field number (0);
            Integer fieldNo = 0;

            //Set the directory that will hold the message type library excel files
            fileSystem dir = new fileSystem();
            dir.setMessageTypeDir("libraryFiles");

            //Create Workbook instance holding reference to .xlsx file
            OPCPackage pkg = null;
            XSSFWorkbook workbook = null;

            FileInputStream file = new FileInputStream(new File(dir.getDir() + fileName));

            try {
                pkg = OPCPackage.open(new File(dir.getDir() + fileName));

                workbook = new XSSFWorkbook(pkg);

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            //Get first/desired sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
	    
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                //Check to see if empty spacer row
                Cell firstcell = row.getCell(1);

               
		//For each row, iterate through all the columns
		Iterator<Cell> cellIterator = row.cellIterator();
		boolean required = false;
		String fieldDesc = "";

		//Increase the field number by 1
		fieldNo++;

		while (cellIterator.hasNext()) {
		    Cell cell = cellIterator.next();

		    switch (cell.getColumnIndex()) {
			case 0:
			    fieldDesc = cell.getStringCellValue();
			    break;
			case 1:
			    required = cell.getBooleanCellValue();
			    break;
			default:
			    break;
		    }
		}
		    
		try {
		    //Need to insert all the fields into the message type Form Fields table
		    Query query = sessionFactory.getCurrentSession().createSQLQuery("INSERT INTO messageTypeFormFields (messageTypeId, fieldNo, fieldDesc, validationType, required)"
			    + "VALUES (:messageTypeId, :fieldNo, :fieldDesc,  1, :required)")
			    .setParameter("messageTypeId", id)
			    .setParameter("fieldNo", fieldNo)
			    .setParameter("fieldDesc", fieldDesc)
			    .setParameter("required", required);

		    query.executeUpdate();
		}
		catch (Exception ex) {
		    System.out.println(ex.getMessage());
		}
            }
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
