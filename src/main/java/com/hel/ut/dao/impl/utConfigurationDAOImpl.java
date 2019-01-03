package com.hel.ut.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.hel.ut.model.CrosswalkData;
import com.hel.ut.model.HL7Details;
import com.hel.ut.model.HL7ElementComponents;
import com.hel.ut.model.HL7Elements;
import com.hel.ut.model.HL7Segments;
import com.hel.ut.model.Macros;
import com.hel.ut.model.configexceldetails;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationCCDElements;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationConnectionReceivers;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.configurationDataTranslations;
import com.hel.ut.model.configurationExcelDetails;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.configurationSchedules;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.messageType;
import com.hel.ut.model.watchlist;
import com.hel.ut.model.watchlistEntry;
import com.hel.ut.reference.fileSystem;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Repository;
import com.hel.ut.dao.utConfigurationDAO;

@Repository
public class utConfigurationDAOImpl implements utConfigurationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * The 'createConfiguration' function will create a new utConfiguration
     *
     * @Table	configurations
     *
     * @param	configuration	Will hold the utConfiguration object from the form
     *
     * @return The function will return the id of the created utConfiguration
     */
    @Override
    @Transactional(readOnly = false)
    public Integer createConfiguration(utConfiguration configuration) {
        Integer lastId;

        lastId = (Integer) sessionFactory.getCurrentSession().save(configuration);
        
        return lastId;
    }

    /**
     * The 'updateConfiguration' function will update a selected utConfiguration details
     *
     * @Table	configurations
     *
     * @param	configuration	Will hold the utConfiguration object from the field
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void updateConfiguration(utConfiguration configuration) {
        sessionFactory.getCurrentSession().update(configuration);
    }

    /**
     * The 'getConfigurationById' function will return a utConfiguration based on the id passed in.
     *
     * @Table configurations
     *
     * @param	configId	This will hold the utConfiguration id to find
     *
     * @return	This function will return a single utConfiguration object
     */
    @Override
    @Transactional(readOnly = true)
    public utConfiguration getConfigurationById(int configId) {
        return (utConfiguration) sessionFactory.getCurrentSession().get(utConfiguration.class, configId);
    }

    /**
     * The 'getConfigurationsByOrgId' function will return a list of configurations for the organization id passed in
     *
     * @Table configurations
     *
     * @param	orgId	This will hold the organization id to find
     *
     * @return	This function will return a list of utConfiguration object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<utConfiguration> getConfigurationsByOrgId(int orgId, String searchTerm) {

        if (!"".equals(searchTerm)) {

            //get a list of message type id's that match the term passed in
            List<Integer> msgTypeIdList = new ArrayList<Integer>();
            Criteria findMsgTypes = sessionFactory.getCurrentSession().createCriteria(messageType.class);
            findMsgTypes.add(Restrictions.like("name", "%" + searchTerm + "%"));
            List<messageType> msgTypes = findMsgTypes.list();

            for (messageType msgType : msgTypes) {
                msgTypeIdList.add(msgType.getId());
            }

            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);

            if (msgTypeIdList.isEmpty()) {
                msgTypeIdList.add(0);
            }

            criteria.add(Restrictions.eq("orgId", orgId));
            criteria.add(Restrictions.or(
                    Restrictions.in("messageTypeId", msgTypeIdList)
            )
            )
                    .addOrder(Order.desc("dateCreated"));

            return criteria.list();
        } else {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
            criteria.add(Restrictions.eq("orgId", orgId));
	    criteria.add(Restrictions.eq("deleted", false));
            criteria.addOrder(Order.desc("dateCreated"));
            return criteria.list();
        }
    }

    /**
     * The 'getActiveConfigurationsByOrgId' function will return a list of active configurations for the organization id passed in
     *
     * @Table configurations
     *
     * @param	orgId	This will hold the organization id to find
     *
     * @return	This function will return a list of utConfiguration object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<utConfiguration> getActiveConfigurationsByOrgId(int orgId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
        criteria.add(Restrictions.eq("orgId", orgId));
        criteria.add(Restrictions.eq("status", true));
	criteria.add(Restrictions.eq("deleted", false));

        return criteria.list();
    }

    /**
     * The 'getConfigurationByName' function will return a single utConfiguration based on the name passed in.
     *
     * @Table	configurations
     *
     * @param	configName	Will hold the utConfiguration name to search on
     *
     * @return	This function will return a single utConfiguration object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public utConfiguration getConfigurationByName(String configName, int orgId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
        criteria.add(Restrictions.like("configName", configName));
        criteria.add(Restrictions.eq("orgId", orgId));
	criteria.add(Restrictions.eq("deleted", false));

        return (utConfiguration) criteria.uniqueResult();
    }

    /**
     * The 'getConfigurations' function will return a list of the configurations in the system
     *
     * @Table	configurations
     *
     * @return	This function will return a list of configuration objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<utConfiguration> getConfigurations() {
        Query query = sessionFactory.getCurrentSession().createQuery("from utConfiguration where deleted = 0 order by dateCreated desc");

        List<utConfiguration> configurationList = query.list();
        return configurationList;
    }

    /**
     * The 'getDataTranslations' function will return a list of data translations saved for the passed in utConfiguration/transport method.
     *
     * @param	configId	The id of the utConfiguration we want to return associated translations for.
     *
     * @return	This function will return a list of translations
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationDataTranslations> getDataTranslations(int configId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from configurationDataTranslations where configId = :configId order by processOrder asc");
        query.setParameter("configId", configId);

        return query.list();
    }

    /**
     * The 'totalConfigs' function will return the total number of active configurations in the system. This will be used for pagination when viewing the list of configurations
     *
     * @Table configurations
     *
     * @return This function will return the total configurations
     */
    @Override
    @Transactional(readOnly = true)
    public Long findTotalConfigs() {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalConfigs from utConfiguration where deleted = 0");

        Long totalConfigs = (Long) query.uniqueResult();

        return totalConfigs;
    }

    /**
     * The 'getLatestConfigurations' function will return a list of the latest configurations that have been added to the system and activated.
     *
     * @Table	organizations
     *
     * @param	maxResults	This will hold the value of the maximum number of results we want to send back to the page
     *
     * @return	This function will return a list of organization objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<utConfiguration> getLatestConfigurations(int maxResults) {
        Query query = sessionFactory.getCurrentSession().createQuery("from utConfiguration where deleted = 0 order by dateCreated desc");

        //Set the max results to display
        query.setMaxResults(maxResults);

        List<utConfiguration> configurationList = query.list();
        return configurationList;
    }

    /**
     * The 'getTotalConnections' function will return the total number of active connections set up for a utConfiguration.
     *
     * @Table configurationCrosswalks
     *
     * @param	configId The id of the utConfiguration to find connections for.
     *
     * @return	The total number of active connections set up for a configurations
     *
     */
    @Override
    @Transactional(readOnly = true)
    public Long getTotalConnections(int configId) {

        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT count(id) FROM configurationConnections where deleted = 0 and configId = :configId and status = 1")
                .setParameter("configId", configId);

        BigInteger totalCount = (BigInteger) query.uniqueResult();

        Long totalConnections = totalCount.longValue();

        return totalConnections;
    }

    /**
     * The 'updateCompletedSteps' function will update the steps completed for a passe in configurations. This column will be used to determine when you can activate a utConfiguration and when you can access certain steps in the utConfiguration creation process.
     *
     * @param	configId	This will hold the id of the utConfiguration to update stepCompleted	This will hold the completed step number
     * @param stepCompleted
     */
    @Override
    @Transactional(readOnly = false)
    public void updateCompletedSteps(int configId, int stepCompleted) {

        Query query = sessionFactory.getCurrentSession().createSQLQuery("UPDATE configurations set stepsCompleted = :stepCompleted where id = :configId")
                .setParameter("stepCompleted", stepCompleted)
                .setParameter("configId", configId);

        query.executeUpdate();
    }

    /**
     * The 'getFileTypes' function will return a list of available file types
     *
     * @return 
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public List getFileTypes() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, fileType FROM ref_fileTypes where active = 1 order by id asc");

        return query.list();
    }

    /**
     * The 'getFileTypesById' function will return the fileType by the id passed in
     *
     * @param id The fileTypeId
     *
     * @table ref_fileTypes
     *
     * @return This function will return the file type
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public String getFileTypesById(int id) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT fileType FROM ref_fileTypes where id = :id")
                .setParameter("id", id);

        String fileType = (String) query.uniqueResult();

        return fileType;
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
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT fieldDesc FROM configurationFormFields where id = :fieldId")
                .setParameter("fieldId", fieldId);

        String fieldName = (String) query.uniqueResult();

        return fieldName;
    }

    /**
     * The 'deleteDataTranslations' function will remove all data translations for the passed in utConfiguration / transport method.
     *
     * @param	configId	The id of the utConfiguration to remove associated translations
     * @param categoryId The id of the category
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteDataTranslations(int configId, int categoryId) {
        Query deleteTranslations = sessionFactory.getCurrentSession().createQuery("delete from configurationDataTranslations where configId = :configId and categoryId = :categoryId");
        deleteTranslations.setParameter("configId", configId);
        deleteTranslations.setParameter("categoryId", categoryId);
        deleteTranslations.executeUpdate();
    }

    /**
     * The 'saveDataTranslations' function will save the submitted translations for the selected message type
     *
     * @param translations	the configurationDataTranslations object
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void saveDataTranslations(configurationDataTranslations translations) {
        sessionFactory.getCurrentSession().save(translations);
    }

    /**
     * The 'getMacrosByCategory' function will return a list of available Pre and Post macros.
     *
     * @param categoryId
     * @return list of macros
     */
    @Override
    @Transactional(readOnly = true)
    public List<Macros> getMacrosByCategory(int categoryId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Macros where categoryId = :categoryId order by macro_short_name asc");
        query.setParameter("categoryId", categoryId);

        List<Macros> macros = query.list();

        return macros;
    }

    /**
     * The 'getMacros' function will return a list of available system macros.
     *
     * @return list of macros
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<Macros> getMacros() {
        Query query = sessionFactory.getCurrentSession().createQuery("from Macros where categoryId = 1 order by macro_short_name asc");
        return query.list();
    }

    /**
     * The 'getMacroById' function will return the macro details for the passed in macro id.
     *
     * @param macroId The value of the macro to retrieve details
     *
     * @return macros object
     */
    @Transactional(readOnly = true)
    @Override
    public Macros getMacroById(int macroId) {
        return (Macros) sessionFactory.getCurrentSession().get(Macros.class, macroId);
    }

    /**
     * The 'getAllConnections' function will return the list of utConfiguration connections in the system.
     *
     * @Table	configurationConnections
     *
     *
     * @return	This function will return a list of configurationConnection objects
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getAllConnections() {
        Query query = sessionFactory.getCurrentSession().createQuery("from configurationConnection order by dateCreated desc");

        List<configurationConnection> connections = query.list();
        return connections;
    }

    /**
     * The 'getLatestConnections' function will return the list of utConfiguration connections in the system.
     *
     * @Table	configurationConnections
     *
     * @param maxResults This will hold the value of the maximum number of results we want to send back to the list page
     *
     * @return	This function will return a list of configurationConnection objects
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getLatestConnections(int maxResults) {
        Query query = sessionFactory.getCurrentSession().createQuery("from configurationConnection order by dateCreated desc");

        //Set the max results to display
        query.setMaxResults(maxResults);

        List<configurationConnection> connections = query.list();
        return connections;
    }

    /**
     * The 'getConnectionsByConfiguration' will return a list of target connections for a passed in utConfiguration;
     *
     * @param configId The id of the utConfiguration to search connections for.
     * @param userId
     *
     * @return This function will return a list of configurationConnection objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationConnection> getConnectionsByConfiguration(int configId, int userId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from configurationConnection where sourceConfigId = :configId and id in (select connectionId from configurationConnectionSenders where userId = :userId)");
        query.setParameter("configId", configId);
        query.setParameter("userId", userId);

        List<configurationConnection> connections = query.list();
        return connections;
    }

    /**
     * The 'getConnectionsByTargetConfiguration' will return a list of target connections for a passed in utConfiguration;
     *
     * @param configId The id of the utConfiguration to search connections for.
     *
     * @return This function will return a list of configurationConnection objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getConnectionsByTargetConfiguration(int configId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from configurationConnection where targetConfigId = :configId");
        query.setParameter("configId", configId);

        List<configurationConnection> connections = query.list();
        return connections;
    }

    /**
     * The 'saveConnection' function will save the new connection
     *
     * @param connection The object holding the new connection
     *
     * @return This function does not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public Integer saveConnection(configurationConnection connection) {
        Integer connectionId;

        connectionId = (Integer) sessionFactory.getCurrentSession().save(connection);

        return connectionId;

    }

    /**
     * The 'saveConnectionSenders' function will save the list of users selected to be authorized to send transactions for a utConfiguration connection.
     *
     * @table configurationConnectionSenders
     *
     * @param senders The configurationConnectionSenders object
     */
    @Override
    @Transactional(readOnly = false)
    public void saveConnectionSenders(configurationConnectionSenders senders) {
        sessionFactory.getCurrentSession().save(senders);
    }

    /**
     * The 'saveConnectionReceivers' function will save the list of users selected to be authorized to receive transactions for a utConfiguration connection.
     *
     * @table configurationConnectionReceivers
     *
     * @param receivers The configurationConnectionSenders object
     */
    @Override
    @Transactional(readOnly = false)
    public void saveConnectionReceivers(configurationConnectionReceivers receivers) {
        sessionFactory.getCurrentSession().save(receivers);
    }

    /**
     * The 'getConnection' function will return a connection based on the id passed in.
     *
     * @param connectionId
     * @Table configurationConnections
     *
     * @return	This function will return a single connection object
     */
    @Override
    @Transactional(readOnly = true)
    public configurationConnection getConnection(int connectionId) {
        return (configurationConnection) sessionFactory.getCurrentSession().get(configurationConnection.class, connectionId);
    }

    /**
     * The 'getConnectionSenders' function will return a list of authorized users who are set up to create new messages for the passed in connectionId
     *
     * @param connectionId The id of the utConfiguration connection to get a list of users
     *
     * @return This function will return a list of configurationConnectionSenders objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnectionSenders> getConnectionSenders(int connectionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationConnectionSenders.class);
        criteria.add(Restrictions.eq("connectionId", connectionId));

        return criteria.list();
    }

    /**
     * The 'getConnectionReceivers' function will return a list of authorized users who are set up to receive messages for the passed in connectionId
     *
     * @param connectionId The id of the utConfiguration connection to get a list of users
     *
     * @return This function will return a list of configurationConnectionSenders objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnectionReceivers> getConnectionReceivers(int connectionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationConnectionReceivers.class);
        criteria.add(Restrictions.eq("connectionId", connectionId));

        return criteria.list();
    }

    /**
     * The 'removeConnectionSenders' function will remove the authorized senders for the passed in connectionId.
     *
     * @param connectionId The connection Id to remove senders for
     */
    @Override
    @Transactional(readOnly = false)
    public void removeConnectionSenders(int connectionId) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("DELETE from configurationConnectionSenders where connectionId = :connectionId")
                .setParameter("connectionId", connectionId);

        query.executeUpdate();
    }

    /**
     * The 'removeConnectionReceivers' function will remove the authorized receivers for the passed in connectionId.
     *
     * @param connectionId The connection Id to remove receivers for
     */
    @Override
    @Transactional(readOnly = false)
    public void removeConnectionReceivers(int connectionId) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("DELETE from configurationConnectionReceivers where connectionId = :connectionId")
                .setParameter("connectionId", connectionId);

        query.executeUpdate();
    }

    /**
     * The 'updateConnection' function will update the status of the passed in connection
     *
     * @param connection The object holding the connection
     */
    @Override
    @Transactional(readOnly = false)
    public void updateConnection(configurationConnection connection) {
        sessionFactory.getCurrentSession().update(connection);
    }

    /**
     * The 'getScheduleDetails' function will return the details of the schedule for the passed in utConfiguration id and transport method.
     *
     * @param configId The id for the utConfiguration
     *
     * @return The function will return a configurationSchedules object containing the details for the schedule.
     */
    @Transactional(readOnly = true)
    @Override
    public configurationSchedules getScheduleDetails(int configId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from configurationSchedules where configId = :configId");
        query.setParameter("configId", configId);

        configurationSchedules scheduleDetails;

        if (query.list().size() > 1) {
            scheduleDetails = (configurationSchedules) query.list().get(0);

            return scheduleDetails;
        } else {
            scheduleDetails = (configurationSchedules) query.uniqueResult();

            return scheduleDetails;
        }

    }

    /**
     * The 'saveSchedule' function will create or update the utConfiguration schedule passed in.
     *
     * @param scheduleDetails The object that holds the utConfiguration schedule
     */
    @Transactional(readOnly = false)
    @Override
    public void saveSchedule(configurationSchedules scheduleDetails) {
        sessionFactory.getCurrentSession().saveOrUpdate(scheduleDetails);
    }

    /**
     * The 'getMessageSpecs' function will return the message specs for the passing utConfiguration ID.
     *
     * @param configId The utConfiguration Id to find message specs for
     *
     * @return This function will return the configuartionMessageSpec object.
     */
    @Override
    @Transactional(readOnly = true)
    public configurationMessageSpecs getMessageSpecs(int configId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from configurationMessageSpecs where configId = :configId");
        query.setParameter("configId", configId);

        return (configurationMessageSpecs) query.uniqueResult();
    }

    /**
     * The 'updateMessageSpecs' function will save/update the utConfiguration message specs.
     *
     * @param messageSpecs The object that will hold the values from the message spec form
     * @param transportDetailId
     * @param clearFields
     */
    @Override
    @Transactional(readOnly = false)
    public void updateMessageSpecs(configurationMessageSpecs messageSpecs, int transportDetailId, int clearFields) {

        //if clearFields == 1 then we need to clear out the utConfiguration form fields, mappings and data
        //translations. This will allow the admin to change the utConfiguration transport method after
        //one was previously selected. This will only be available while the utConfiguration is not active.
        if (clearFields == 1) {
            //Delete the existing data translactions
            Query deleteTranslations = sessionFactory.getCurrentSession().createSQLQuery("DELETE from configurationDataTranslations where configId = :configId");
            deleteTranslations.setParameter("configId", messageSpecs.getconfigId());
            deleteTranslations.executeUpdate();

            //Delete the existing form fields
            Query deleteFields = sessionFactory.getCurrentSession().createSQLQuery("DELETE from configurationFormFields where configId = :configId and transportDetailId = :transportDetailId");
            deleteFields.setParameter("configId", messageSpecs.getconfigId());
            deleteFields.setParameter("transportDetailId", transportDetailId);
            deleteFields.executeUpdate();
        }

        sessionFactory.getCurrentSession().saveOrUpdate(messageSpecs);

    }

    
    /**
     * The 'getActiveConfigurationsByUserId' function will return a list of configurations set up the passed in userId and passed in transport method
     *
     * @param userId The id of the logged in user
     * @param transportMethod The id of the transport method to find configurations 1 = File Upload 2 = ERG
     *
     * @return This function will return a list of ERG configurations.
     * @throws java.lang.Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<utConfiguration> getActiveConfigurationsByUserId(int userId, int transportMethod) throws Exception {

        /* Find all SENDER connections for the passed in user */
        Criteria findAuthConnections = sessionFactory.getCurrentSession().createCriteria(configurationConnectionSenders.class);
        findAuthConnections.add(Restrictions.eq("userId", userId));

        /* This variables (senderConnections) will hold the list of authorized connections */
        List<configurationConnectionSenders> senderConnections = findAuthConnections.list();

        /* 
         Create an emtpy array that will hold the list of configurations associated to the
         found connections.
         */
        List<Integer> senderConfigList = new ArrayList<Integer>();

        if (senderConnections.isEmpty()) {
            senderConfigList.add(0);
        } else {
            /* Search the connections by connectionId to pull the sourceConfigId */
            for (configurationConnectionSenders connection : senderConnections) {
                Criteria findConnectionDetails = sessionFactory.getCurrentSession().createCriteria(configurationConnection.class);
                findConnectionDetails.add(Restrictions.eq("id", connection.getConnectionId()));
                configurationConnection connectionDetails = (configurationConnection) findConnectionDetails.uniqueResult();

                /* Add the sourceConfigId to the array */
                senderConfigList.add(connectionDetails.getsourceConfigId());
                findConnectionDetails = null;
            }
        }

        /* 
         Query to get a list of all ERG configurations that the logged in
         user is authorized to create
         */
        List<Integer> ergConfigList = new ArrayList<Integer>();
        Criteria findERGConfigs = sessionFactory.getCurrentSession().createCriteria(configurationTransport.class);
        findERGConfigs.add(Restrictions.eq("transportMethodId", transportMethod)
        ).add(Restrictions.and(Restrictions.in("configId", senderConfigList)));

        List<configurationTransport> ergConfigs = findERGConfigs.list();

        for (configurationTransport config : ergConfigs) {
            ergConfigList.add(config.getconfigId());
        }

        if (ergConfigList.isEmpty()) {
            ergConfigList.add(0);
        }

        /*
         Finally query the utConfiguration table to get all configurations in the authorized list
         of utConfiguration Ids.
         */
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
        criteria.add(Restrictions.eq("status", true));
	criteria.add(Restrictions.eq("deleted", false));
        criteria.add(Restrictions.eq("sourceType", 1));
        criteria.add(Restrictions.and(
                Restrictions.in("id", ergConfigList)
        ));

        return criteria.list();

    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationDataTranslations> getDataTranslationsWithFieldNo(int configId, int categoryId) {
	
        Query query = sessionFactory
	    .getCurrentSession()
	    .createSQLQuery(
		    "select configurationDataTranslations.*, configurationFormFields.associatedFieldNo as fieldNo "
		    + "from configurationDataTranslations inner join "
		    + "configurationFormFields on configurationFormFields.id = configurationDataTranslations.fieldId " 
		    + "where configurationDataTranslations.configId = :configId "
		    + "and configurationDataTranslations.categoryId = :categoryId "
		    + "order by configurationDataTranslations.processorder asc;")
	    .setResultTransformer(
		    Transformers.aliasToBean(configurationDataTranslations.class))
	    .setParameter("categoryId", categoryId).setParameter("configId", configId);

        List<configurationDataTranslations> cdtList = query.list();

        return cdtList;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<CrosswalkData> getCrosswalkData(int cwId) {
        try {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CrosswalkData.class);
            criteria.add(Restrictions.eq("crosswalkId", cwId));

            return criteria.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * The 'getHL7Details' function will the HL7 details for the passed in utConfiguration.
     *
     * @Table configurationHL7Details
     *
     * @param	configId This will hold the utConfiguration id to find
     *
     * @return	This function will return a HL7Details object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public HL7Details getHL7Details(int configId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HL7Details.class);
        criteria.add(Restrictions.eq("configId", configId));

        if (criteria.uniqueResult() == null) {
            return null;
        } else {
            return (HL7Details) criteria.uniqueResult();
        }

    }

    /**
     * The 'getHL7Segments' function will return the list of segments for a specific HL7 Message.
     *
     * @Table configurationHL7Segments
     *
     * @return This function will return a list of HL7Segment objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<HL7Segments> getHL7Segments(int hl7Id) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HL7Segments.class);
        criteria.add(Restrictions.eq("hl7Id", hl7Id));
        criteria.addOrder(Order.asc("displayPos"));

        return criteria.list();
    }

    /**
     * The 'getHL7Elements' function will return the list of elements for a specific HL7 Message segment.
     *
     * @Table configurationHL7Elements
     *
     * @return This function will return a list of HL7Elements objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<HL7Elements> getHL7Elements(int hl7Id, int segmentId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HL7Elements.class);
        criteria.add(Restrictions.eq("hl7Id", hl7Id));
        criteria.add(Restrictions.eq("segmentId", segmentId));
        criteria.addOrder(Order.asc("displayPos"));

        return criteria.list();
    }

    /**
     * The 'getHL7ElementComponents' function will return any components associated to the passed in element id
     *
     * @param elementId
     *
     * @return This function will return a list of element component objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<HL7ElementComponents> getHL7ElementComponents(int elementId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HL7ElementComponents.class);
        criteria.add(Restrictions.eq("elementId", elementId));
        criteria.addOrder(Order.asc("displayPos"));

        return criteria.list();
    }

    /**
     * the 'updateHL7Details' funciton will update/save the details of the HL7 message
     *
     * @param details The Hl7 details object
     */
    @Override
    @Transactional(readOnly = false)
    public void updateHL7Details(HL7Details details) {
        sessionFactory.getCurrentSession().update(details);

    }

    /**
     * The 'updateHL7Segments' function will update the segment passed to the function.
     *
     * @param segment The segment object to update
     */
    @Override
    @Transactional(readOnly = false)
    public void updateHL7Segments(HL7Segments segment) {
        sessionFactory.getCurrentSession().update(segment);
    }

    /**
     * The 'updateHL7Elements' function will update the segment element passed to the function.
     *
     * @param element The segment element object to update.
     */
    @Override
    @Transactional(readOnly = false)
    public void updateHL7Elements(HL7Elements element) {
        sessionFactory.getCurrentSession().update(element);
    }

    /**
     * The 'updateHL7ElementComponent' function will update the segment element components
     *
     * @param component The element component object to update.
     */
    @Override
    @Transactional(readOnly = false)
    public void updateHL7ElementComponent(HL7ElementComponents component) {
        sessionFactory.getCurrentSession().update(component);
    }

    /**
     * The 'saveHL7Details' function will save the new HL7 Segment
     *
     * @param newSegment The object holding the new HL7 Object
     */
    @Override
    @Transactional(readOnly = false)
    public int saveHL7Details(HL7Details details) {
        Integer lastId;

        lastId = (Integer) sessionFactory.getCurrentSession().save(details);

        return lastId;
    }

    /**
     * The 'saveHL7Segment' function will save the new HL7 Segment
     *
     * @param newSegment The object holding the new HL7 Object
     */
    @Override
    @Transactional(readOnly = false)
    public int saveHL7Segment(HL7Segments newSegment) {
        Integer lastId;

        lastId = (Integer) sessionFactory.getCurrentSession().save(newSegment);

        return lastId;
    }

    /**
     * The 'saveHL7Element' function will save the new HL7 Segment Element
     *
     * @param newElement The object holding the new HL7 Element Object
     */
    @Override
    @Transactional(readOnly = false)
    public int saveHL7Element(HL7Elements newElement) {
        Integer lastId;

        lastId = (Integer) sessionFactory.getCurrentSession().save(newElement);

        return lastId;
    }

    /**
     * The 'saveHL7Component' function will save the new HL7 Element Component
     *
     * @param newcomponent The object holding the new HL7 Element Component Object
     */
    @Override
    @Transactional(readOnly = false)
    public void saveHL7Component(HL7ElementComponents newcomponent) {
        sessionFactory.getCurrentSession().save(newcomponent);
    }

    @Override
    @Transactional(readOnly = true)
    public String getMessageTypeNameByConfigId(Integer configId) {
        try {
            String sql = ("select name from messageTypes where id in (select messageTypeId from configurations where id = :configId);");
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).addScalar("name", StandardBasicTypes.STRING);
            query.setParameter("configId", configId);
	    
	    String mtName = "N/A";
	    
	    if(query.list().isEmpty()) {
		String configSQL = ("select configName from configurations where id = :configId");
		Query configQuery = sessionFactory.getCurrentSession().createSQLQuery(configSQL).addScalar("configName", StandardBasicTypes.STRING);
		configQuery.setParameter("configId", configId);
		
		mtName = (String) configQuery.list().get(0);
	    }
	    else {
		mtName = (String) query.list().get(0);
	    }
	   
            return mtName;
        } catch (Exception ex) {
            System.err.println("getMessageTypeNameByConfigId " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * The 'getEncodings' function will return a list of available encodings
     *
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public List getEncodings() {
        try {
            Query query = sessionFactory.getCurrentSession().createSQLQuery("select id, encoding from ref_encoding order by id asc");
            return query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("getEncodings - " + ex.getCause());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void removeHL7ElementComponent(Integer componentId) {
        Query deleteComponent = sessionFactory.getCurrentSession().createQuery("delete from HL7ElementComponents where id = :componentId");
        deleteComponent.setParameter("componentId", componentId);
        deleteComponent.executeUpdate();
    }

    @Override
    @Transactional(readOnly = false)
    public void removeHL7Element(Integer elementId) {
        Query deleteComponents = sessionFactory.getCurrentSession().createQuery("delete from HL7ElementComponents where elementId = :elementId");
        deleteComponents.setParameter("elementId", elementId);
        deleteComponents.executeUpdate();

        Query deleteElement = sessionFactory.getCurrentSession().createQuery("delete from HL7Elements where id = :elementId");
        deleteElement.setParameter("elementId", elementId);
        deleteElement.executeUpdate();
    }

    @Override
    @Transactional(readOnly = false)
    public void removeHL7Segment(Integer segmentId) {
        Query deleteComponents = sessionFactory.getCurrentSession().createSQLQuery("delete from configurationhl7elementvalues where elementId in (select id from configurationhl7elements where segmentId = :segmentId)");
        deleteComponents.setParameter("segmentId", segmentId);
        deleteComponents.executeUpdate();

        Query deleteElement = sessionFactory.getCurrentSession().createQuery("delete from HL7Elements where segmentId = :segmentId");
        deleteElement.setParameter("segmentId", segmentId);
        deleteElement.executeUpdate();

        Query deleteSegment = sessionFactory.getCurrentSession().createQuery("delete from HL7Segments where id = :segmentId");
        deleteSegment.setParameter("segmentId", segmentId);
        deleteSegment.executeUpdate();
    }

    /**
     * The 'getCCDElements' function will return the CCD elements for the passed in utConfiguration.
     *
     * @Table configurationCCDElements
     *
     * @param	configId This will hold the utConfiguration id to find
     *
     * @return	This function will return a configurationCCDElements object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationCCDElements> getCCDElements(Integer configId) throws Exception {

        Query query = sessionFactory
                .getCurrentSession()
                .createSQLQuery(
                        "select configurationCCDElements.*, configurationFormFields.fieldDesc as fieldLabel "
			+ "from configurationCCDElements LEFT OUTER JOIN configurationFormFields on "
                        + "configurationFormFields.configId = configurationCCDElements.configId and configurationFormFields.fieldNo = configurationCCDElements.fieldValue"
                        + " where configurationCCDElements.configId = :configId order by id asc")
                .setResultTransformer(
                        Transformers.aliasToBean(configurationCCDElements.class))
                .setParameter("configId", configId);

        List<configurationCCDElements> elements = query.list();

        return elements;

    }

    /**
     * The 'getCCDElement' function will return the configurationCCDElement object for the passed in elementId
     *
     * @param elementId The id of the selected element.
     *
     * @return This function will return a single configurationCCDElement
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public configurationCCDElements getCCDElement(Integer elementId) throws Exception {
        return (configurationCCDElements) sessionFactory.
                getCurrentSession().
                get(configurationCCDElements.class, elementId);
    }

    /**
     * The 'saveCCDElement' function will save the new CCD element.
     *
     * @param ccdElement This will hold the new ccdElement object
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = false)
    public void saveCCDElement(configurationCCDElements ccdElement) throws Exception {
        sessionFactory.getCurrentSession().saveOrUpdate(ccdElement);
    }

    @Override
    @Transactional(readOnly = true)
    public configurationExcelDetails getExcelDetails(Integer configId, Integer orgId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationExcelDetails.class);
        criteria.add(Restrictions.eq("configId", configId));
        criteria.add(Restrictions.eq("orgId", orgId));

        if (criteria.list().size() > 0) {
            return (configurationExcelDetails) criteria.list().get(0);
        } else {
            return null;
        }

    }
    
    /**
     * The 'loadExcelContents' will take the contents of the uploaded excel template file and populate the corresponding utConfiguration form fields table. This function will split up the contents into the appropriate buckets. Buckets (1 - 4) will be separated by spacer rows with in the excel file.
     *
     * @param id value of the latest added utConfiguration
     * @param fileName	file name of the uploaded excel file.
     * @param dir	the directory of the uploaded file
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void loadExcelContents(int id, int transportDetailId, String fileName, fileSystem dir) throws Exception {
        String errorMessage = "";
        try {
           
            //Set the initial value of the field number (0);
            Integer fieldNo = new Integer(0);

            //Create Workbook instance holding reference to .xlsx file
            OPCPackage pkg = null;
            XSSFWorkbook workbook = null;

            try {
                pkg = OPCPackage.open(new File(dir.getDir() + fileName));

                workbook = new XSSFWorkbook(pkg);

            } catch (Exception e1) {
                e1.printStackTrace();
                errorMessage = errorMessage + "<br/>" + e1.getMessage();
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

		    //Check the cell type and format accordingly
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

		//Need to insert all the fields into the message type Form Fields table
		Query query = sessionFactory.getCurrentSession().createSQLQuery("INSERT INTO configurationFormFields (configId, transportDetailId, fieldNo, fieldDesc, validationType, required, useField)"
			+ "VALUES (:configId, :transportDetailId, :fieldNo, :fieldDesc, 1, :required, 1)")
			.setParameter("configId", id)
			.setParameter("transportDetailId", transportDetailId)
			.setParameter("fieldNo", fieldNo)
			.setParameter("fieldDesc", fieldDesc)
			.setParameter("required", required);

		query.executeUpdate();
            }
            try {
                pkg.close();
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = errorMessage + "<br/>" + e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = errorMessage + "<br/>" + e.getMessage();
        }

        /**
         * throw error message here because want to make sure file stream is closed *
         */
        if (!errorMessage.equalsIgnoreCase("")) {
            throw new Exception(errorMessage);
        }

    }
    
    @Override
    @Transactional(readOnly = false)
    public void updateExcelConfigDetails(Integer orgId, configurationMessageSpecs messageSpecs) throws Exception {
	
	//Delete existing entry
	Query deleteTranslations = sessionFactory.getCurrentSession().createQuery("delete from configexceldetails where configId = :configId and orgId = :orgId");
        deleteTranslations.setParameter("configId", messageSpecs.getconfigId());
        deleteTranslations.setParameter("orgId", orgId);
        deleteTranslations.executeUpdate();
	
	//Insert new entry
	configexceldetails configexceldetails = new configexceldetails();
	configexceldetails.setConfigId(messageSpecs.getconfigId());
	configexceldetails.setOrgId(orgId);
	configexceldetails.setStartRow(messageSpecs.getExcelstartrow());
	configexceldetails.setDiscardLastRows(messageSpecs.getExcelskiprows());
	
	sessionFactory.getCurrentSession().save(configexceldetails);
	
    }
    
    /**
     * The 'getFieldCrosswalkIdByFieldName' function will return the fieldId by the configId passed and a field name
     *
     * @param id The fileTypeId
     *
     * @table ref_fileTypes
     *
     * @return This function will return the file type
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = false)
    public Integer getFieldCrosswalkIdByFieldName(int configId, String fieldName) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT crosswalkId from configurationdatatranslations where fieldId in (select id FROM configurationformfields where configId = :configId and fieldDesc = :fieldName)")
                .setParameter("configId", configId)
                .setParameter("fieldName", fieldName);
        
        Integer crosswalkId = 0;
        
        if(query.list() != null && query.list().size() > 0) {
            crosswalkId = (Integer) query.list().get(0);
        } 

        return crosswalkId;
    }  
    
   
    /**
     * The 'getActiveConfigurationsByTransportType' function will return a list of configurations set up the passed in userId and passed in transport method
     *
     * @param userId The id of the logged in user
     * @param transportMethod The id of the transport method to find configurations 1 = File Upload 2 = ERG
     *
     * @return This function will return a list of ERG configurations.
     */
    @Override
    @Transactional(readOnly = true)
    public List<utConfiguration> getActiveConfigurationsByTransportType(int userId, List<Integer> transportMethods) throws Exception {

        /* Find all SENDER connections for the passed in user */
        Criteria findAuthConnections = sessionFactory.getCurrentSession().createCriteria(configurationConnectionSenders.class);
        findAuthConnections.add(Restrictions.eq("userId", userId));

        /* This variables (senderConnections) will hold the list of authorized connections */
        List<configurationConnectionSenders> senderConnections = findAuthConnections.list();

        /* 
         Create an emtpy array that will hold the list of configurations associated to the
         found connections.
         */
        List<Integer> senderConfigList = new ArrayList<Integer>();

        if (senderConnections.isEmpty()) {
            senderConfigList.add(0);
        } else {
            /* Search the connections by connectionId to pull the sourceConfigId */
            for (configurationConnectionSenders connection : senderConnections) {
                Criteria findConnectionDetails = sessionFactory.getCurrentSession().createCriteria(configurationConnection.class);
                findConnectionDetails.add(Restrictions.eq("id", connection.getConnectionId()));
                configurationConnection connectionDetails = (configurationConnection) findConnectionDetails.uniqueResult();

                /* Add the sourceConfigId to the array */
                senderConfigList.add(connectionDetails.getsourceConfigId());
                findConnectionDetails = null;
            }
        }

        /* 
         Query to get a list of all ERG configurations that the logged in
         user is authorized to create
         */
        List<Integer> ergConfigList = new ArrayList<Integer>();
        Criteria findERGConfigs = sessionFactory.getCurrentSession().createCriteria(configurationTransport.class);
        findERGConfigs.add(Restrictions.in("transportMethodId", transportMethods)
        ).add(Restrictions.and(Restrictions.in("configId", senderConfigList)));

        List<configurationTransport> ergConfigs = findERGConfigs.list();

        for (configurationTransport config : ergConfigs) {
            ergConfigList.add(config.getconfigId());
        }

        if (ergConfigList.isEmpty()) {
            ergConfigList.add(0);
        }

        /*
         Finally query the utConfiguration table to get all configurations in the authorized list
         of utConfiguration Ids.
         */
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
        criteria.add(Restrictions.eq("status", true));
	criteria.add(Restrictions.eq("deleted", false));
        criteria.add(Restrictions.eq("sourceType", 1));
        criteria.add(Restrictions.and(
                Restrictions.in("id", ergConfigList)
        ));

        return criteria.list();

    }
    
    /**
     * The 'getZipTypes' function will return a list of available zip types
     *
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public List getZipTypes() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, zipType FROM lu_ziptypes order by id asc");

        return query.list();
    }
    
    /**
     * The 'getrestAPITypes' function will return a list of available zip types
     *
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public List getrestAPITypes() {
	Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, apiType FROM lu_restapitypes order by id asc");

	return query.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getConnectionsBySrcAndTargetConfigurations(int sourceConfigId, int targetConfigId) {
	
	Query query = sessionFactory.getCurrentSession().createQuery("from configurationConnection where sourceConfigId = :sourceConfigId and targetConfigId = :targetConfigId and status = TRUE");
	query.setParameter("sourceConfigId", sourceConfigId);
	query.setParameter("targetConfigId", targetConfigId);

	List<configurationConnection> connections = query.list();
	return connections;
    }
    
    /**
     * The 'getrestAPIFunctions' function will return a list of available api functions
     *
     * @param orgId
     * @return 
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public List getrestAPIFunctions(Integer orgId) {
	
	String sql = "select id, functionName from lu_availablerestapifunctions where forOrgId = 0 or forOrgId = :orgId order by id asc";
	
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
	query.setParameter("orgId", orgId);

        return query.list();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<watchlist> getDashboardWatchList() throws Exception {
	
	String sql = "select a.entryMessage, a.id, b.orgName, c.configName, m.name as messageTypeName, e.transportMethod, a.expected, a.expectFirstFile, a.expectFirstFileTime "
		+ "from dashboardwatchlist a left outer join "
		+ "organizations b on a.orgId = b.id left outer join "
		+ "configurations c on a.configId = c.id left outer join " 
		+ "configurationtransportdetails d on a.configId = d.configId left outer join " 
		+ "ref_transportmethods e on d.transportMethodId = e.id left outer join " 
		+ "messagetypes m on c.messageTypeId = m.id "
		+ "order by a.dateCreated desc";
	
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                        Transformers.aliasToBean(watchlist.class));

        return query.list();
    }
    
    @Override
    @Transactional(readOnly = true)
    public watchlist getDashboardWatchListById(int watchId) throws Exception {
	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(watchlist.class);
        criteria.add(Restrictions.eq("id", watchId));
	
	return (watchlist) criteria.uniqueResult();
    }
    
    /**
     * The 'saveDashboardWatchListEntry' function will save the new watchlist
     *
     * @param watchListEntry The object holding the new watchlist
     *
     * @return This function does not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public Integer saveDashboardWatchListEntry(watchlist watchListEntry) {
        Integer watchListEntryId;

        watchListEntryId = (Integer) sessionFactory.getCurrentSession().save(watchListEntry);

        return watchListEntryId;
    }
    
    /**
     * The 'updateDashboardWatchListEntry' function will update the watchlist entry
     *
     * @param watchListEntry The object holding the watchlist
     *
     * @return This function does not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public void updateDashboardWatchListEntry(watchlist watchListEntry) {
        sessionFactory.getCurrentSession().update(watchListEntry);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<watchlist> getDashboardWatchListToInsert() throws Exception {
	
	String sql = "SELECT a.*, IFNULL(c.messageTypeId, 0) as messageTypeId FROM dashboardwatchlist a left outer join configurations c on c.id = a.configId where nextInsertDate <= now();";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.addScalar("id", StandardBasicTypes.INTEGER)
		.addScalar("orgId", StandardBasicTypes.INTEGER)
		.addScalar("configId", StandardBasicTypes.INTEGER)
		.addScalar("expected", StandardBasicTypes.STRING)
		.addScalar("expectFirstFile", StandardBasicTypes.STRING)
		.addScalar("dateCreated", StandardBasicTypes.DATE)
		.addScalar("expectFirstFileTime", StandardBasicTypes.STRING)
		.addScalar("nextInsertDate", StandardBasicTypes.DATE)
		.addScalar("entryMessage", StandardBasicTypes.STRING)
		.addScalar("messageTypeId", StandardBasicTypes.INTEGER)
		.setResultTransformer(Transformers.aliasToBean(watchlist.class));

        return query.list();
	
    }
    
    /**
     * The 'insertDashboardWatchListEntry' function will save the new watchlist
     *
     * @param watchListEntry The object holding the new watchlist
     *
     * @return This function does not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public void insertDashboardWatchListEntry(watchlistEntry watchListEntry) {
        sessionFactory.getCurrentSession().saveOrUpdate(watchListEntry);
    }
    
    /**
     * The 'getAllUploadedBatches' function will return a list of batches for the admin in the processing activities section.
     *
     * @param fromDate
     * @param toDate
     * @return This function will return a list of batch uploads
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<watchlistEntry> getWatchListEntries(Date fromDate, Date toDate) throws Exception {

	int firstResult = 0;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	String sql = "select a.*, b.transportMethodId from dashboardwatchlistentries a "
		+ "inner join configurationtransportdetails b on b.configId = a.configId "
		+ "where a.dateCreated >= '" + sdf.format(fromDate) + " 00:00:00' and a.dateCreated < '" + sdf.format(toDate) + " 23:59:59' order by dateCreated desc";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setResultTransformer(Transformers.aliasToBean(watchlistEntry.class));

	return query.list();
    }
    
    @Override
    @Transactional(readOnly = false)
    public void deleteWatchEntry(Integer watchId) throws Exception {
	
	String sql = "delete from dashboardwatchlistentries where watchlistentryid = "+watchId+"; delete from dashboardwatchlist where id = "+watchId+";";
	
	Query deleteWatchEntry = sessionFactory.getCurrentSession().createSQLQuery(sql);
        deleteWatchEntry.executeUpdate();
    }
    
    /**
     * The 'getGenericWatchListEntries' function will return a list of generic watch list entries.
     *
     * @param fromDate
     * @param toDate
     * @return This function will return a list of generic watch list entries
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<watchlistEntry> getGenericWatchListEntries(Date fromDate, Date toDate) throws Exception {

	int firstResult = 0;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	String sql = "select a.*, b.entryMessage from dashboardwatchlistentries a inner join dashboardwatchlist b on a.watchlistentryId = b.id "
		+ "where a.orgId = 0 and a.configId = 0 and ((a.watchListCompleted = 0) or (a.dateCreated >= '" + sdf.format(fromDate) + " 00:00:00' and a.dateCreated < '" + sdf.format(toDate) + " 23:59:59')) order by dateCreated desc";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setResultTransformer(Transformers.aliasToBean(watchlistEntry.class));

	return query.list();
    }
    
    @Override
    @Transactional(readOnly = true)
    public watchlistEntry getWatchListEntry(Integer entryId) throws Exception {
	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(watchlistEntry.class);
        criteria.add(Restrictions.eq("id", entryId));
	
	return (watchlistEntry) criteria.uniqueResult();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<utConfiguration>  getAllActiveSourceConfigurations() throws Exception {
	
	String sql = "select a.id, a.configName, b.orgName "
		+ "from configurations a inner join "
		+ "organizations b on b.id = a.orgId "
		+ "where a.type = 1 and a.deleted = 0 and a.status = 1";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
		.setResultTransformer(Transformers.aliasToBean(utConfiguration.class));
	
        return query.list();
    }
    
    /**
     * The 'getConnectionsBySourceConfiguration' will return a list of source connections for a passed in utConfiguration;
     *
     * @param configId The id of the utConfiguration to search connections for.
     *
     * @return This function will return a list of configurationConnection objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getConnectionsBySourceConfiguration(Integer configId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from configurationConnection where sourceConfigId = :configId");
        query.setParameter("configId", configId);

        List<configurationConnection> connections = query.list();
        return connections;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<utConfiguration>  getAllSourceConfigurations() throws Exception {
	Query query = sessionFactory.getCurrentSession().createQuery("from utConfiguration where deleted = 0 and type = 1");

        List<utConfiguration> sourceConfigurations = query.list();
        return sourceConfigurations;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<utConfiguration>  getAllTargetConfigurations() throws Exception {
	Query query = sessionFactory.getCurrentSession().createQuery("from utConfiguration where deleted = 0 and type = 2");

        List<utConfiguration> targetConfigurations = query.list();
        return targetConfigurations;
    }
}
