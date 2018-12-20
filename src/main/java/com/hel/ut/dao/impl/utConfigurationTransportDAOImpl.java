package com.hel.ut.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.hel.ut.model.TransportMethod;
import com.hel.ut.model.configurationFTPFields;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.configurationFileDropFields;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.configurationTransportMessageTypes;
import com.hel.ut.model.configurationWebServiceFields;
import com.hel.ut.model.configurationWebServiceSenders;

import java.util.Iterator;

import org.springframework.stereotype.Repository;
import com.hel.ut.dao.utConfigurationTransportDAO;

@Repository
public class utConfigurationTransportDAOImpl implements utConfigurationTransportDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * The 'getTransportDetails' function will return the details of the transport method for the passed in configuration id
     *
     * @param configId	Holds the id of the selected configuration
     *
     * @Return	This function will return a list of configurationTransport objects
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public configurationTransport getTransportDetails(int configId) throws Exception {
        Query query = sessionFactory.getCurrentSession().createQuery("from configurationTransport where configId = :configId");
        query.setParameter("configId", configId);

        return (configurationTransport) query.uniqueResult();
    }

    /**
     * The 'getTransportDetailsByTransportMethod' function will return the details of the transport method for the passed in configuration id and passed in transport method
     *
     * @param configId	Holds the id of the selected configuration
     * @param transportMethod	Holds the selected transport method
     *
     * @Return	This function will return a configurationTransport object
     */
    @Transactional(readOnly = true)
    public configurationTransport getTransportDetailsByTransportMethod(int configId, int transportMethod) {
        Query query = sessionFactory.getCurrentSession().createQuery("from configurationTransport where configId = :configId and transportMethodId = :transportMethod");
        query.setParameter("configId", configId);
        query.setParameter("transportMethod", transportMethod);

        return (configurationTransport) query.uniqueResult();
    }


    /**
     * The 'updateTransportDetails' function will update the configuration transport details
     *
     * @param	transportDetails	The details of the transport form
     * @param	clearFields	This will hold a variable that will determine if we clear out existing configuration details.
     *
     * @return	this function does not return anything
     */
    @Transactional(readOnly = false)
    public Integer updateTransportDetails(configurationTransport transportDetails) {

        if (transportDetails.getId() > 0) {
            sessionFactory.getCurrentSession().update(transportDetails);
            return transportDetails.getId();
        } else {
            int detailId = (Integer) sessionFactory.getCurrentSession().save(transportDetails);
            return detailId;
        }

    }

    /**
     * The 'getTransportMethods' function will return a list of available transport methods
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List getTransportMethods() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, transportMethod FROM ref_transportMethods where active = 1 order by transportMethod asc");

        return query.list();
    }
    
    /**
     * The 'getTransportMethodsByType' function will return a list of available transport methods
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List getTransportMethodsByType(Integer configurationType) {
	
	 Query query;
	 
	//Source configuration
	if(configurationType == 1) {
	    query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, transportMethod FROM ref_transportMethods where active = 1 and id in (1,3,4,6,9,10) order by transportMethod asc");
	}
	
	//Target configuration
	else {
	    query = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, transportMethod FROM ref_transportMethods where active = 1 and id in (3,6,8,9) order by transportMethod asc");
	}
	
        return query.list();
    }
    

    /**
     * The 'copyMessageTypeFields' function will copy the form fields for the selected message type for the selected configuration.
     *
     * @param transportId The id of the configured transport method
     * @param	configId	The id of the selected configuration
     * @param messageTypeId	The id of the selected message type to copy the form fields
     *
     * @return	This function does not return anything
     */
    @Transactional(readOnly = false)
    public void copyMessageTypeFields(int transportId, int configId, int messageTypeId) {

        // Check to see if there are any data translations for the passed in message type
        Query translationQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT id FROM rel_messageTypeDataTranslations where messageTypeId = :messageTypeId");
        translationQuery.setParameter("messageTypeId", messageTypeId);

        if (translationQuery.list().size() > 0) {
            // Get all the message type fields
            Query messageTypeFields = sessionFactory.getCurrentSession().createSQLQuery("SELECT id, messageTypeId FROM messageTypeFormFields where messageTypeId = :messageTypeId");
            messageTypeFields.setParameter("messageTypeId", messageTypeId);
            List fieldList = messageTypeFields.list();

            Iterator it = fieldList.iterator();
            int id;
            int max;
            while (it.hasNext()) {
                Object row[] = (Object[]) it.next();
                id = (Integer) row[0];
                Query query = sessionFactory.getCurrentSession().createSQLQuery("INSERT INTO configurationFormFields (messageTypeFieldId, configId, transportDetailId, fieldNo, fieldDesc, validationType, required, useField) SELECT id, :configId, :transportDetailId, fieldNo,  fieldDesc, validationType, required, 1 FROM messageTypeFormFields where messageTypeId = :messageTypeId and id = :id");
                query.setParameter("configId", configId);
                query.setParameter("messageTypeId", messageTypeId);
                query.setParameter("transportDetailId", transportId);
                query.setParameter("id", id);
                query.executeUpdate();

                //Get the max id
                Query maxId = sessionFactory.getCurrentSession().createSQLQuery("SELECT max(id), configId FROM configurationFormFields");
                List queryList = maxId.list();
                Iterator maxIt = queryList.iterator();
                while (maxIt.hasNext()) {
                    Object maxrow[] = (Object[]) maxIt.next();
                    max = (Integer) maxrow[0];
                    /* Check to see if there is a data translation for the current row */
                    Query copyTranslations = sessionFactory.getCurrentSession().createSQLQuery("INSERT INTO configurationDataTranslations (configId, fieldId, crosswalkId, macroId, processOrder) SELECT :configId, :fieldId, crosswalkId, 0, processOrder FROM rel_messageTypeDataTranslations where fieldId = :fieldId2");
                    copyTranslations.setParameter("configId", configId);
                    copyTranslations.setParameter("fieldId", max);
                    copyTranslations.setParameter("fieldId2", id);
                    copyTranslations.executeUpdate();
                }
            }
        } else {
	    
            Query query = sessionFactory.getCurrentSession().createSQLQuery("INSERT INTO configurationFormFields (messageTypeFieldId, configId, transportDetailId, fieldNo, fieldDesc, validationType, required, useField) SELECT id, :configId, :transportDetailId, fieldNo,  fieldDesc, validationType, required, 1 FROM messageTypeFormFields where messageTypeId = :messageTypeId");
            query.setParameter("configId", configId);
            query.setParameter("messageTypeId", messageTypeId);
            query.setParameter("transportDetailId", transportId);

            query.executeUpdate();
        }
    }

    /**
     * The 'getConfigurationFields' function will return a list of saved form fields for the selected configuration.
     *
     * @param	configId	Will hold the id of the configuration we want to return fields for
     * @param transporetDetailId The id of the selected transport method
     *
     * @return	This function will return a list of configuration form fields
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationFormFields> getConfigurationFields(int configId, int transportDetailId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFormFields.class)
            .add(Restrictions.eq("configId", configId));
		
	    if(transportDetailId > 0) {
	       criteria .add(Restrictions.eq("transportDetailId", transportDetailId));
	    }
	    criteria.addOrder(Order.asc("fieldNo"));

        return criteria.list();
    }

    /**
     * The 'getConfigurationFieldsByBucket' function will return a list of form fields for the selected configuration and selected Bucket (Section 1-4)
     *
     * @param configId The id of the selected configuration
     * @param transporetDetailId The id of the selected transport method
     * @param buckt The integer value of the bucket (Section) you want to return fields for (must be 1-4)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationFormFields> getConfigurationFieldsByBucket(int configId, int transportDetailId, int bucket) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFormFields.class)
                .add(Restrictions.eq("configId", configId))
                .add(Restrictions.eq("transportDetailId", transportDetailId))
                .add(Restrictions.eq("bucketNo", bucket))
                .add(Restrictions.eq("useField", true))
                .addOrder(Order.asc("bucketDspPos"));

        return criteria.list();
    }

    /**
     * The 'getConfigurationFieldsByFieldNo' function will return a list of form fields for the selected configuration and selected Field No
     *
     * @param configId The id of the selected configuration
     * @param transporetDetailId The id of the selected transport method
     * @param fieldNo The integer value of the field you want to return fields
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public configurationFormFields getConfigurationFieldsByFieldNo(int configId, int transportDetailId, int fieldNo) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFormFields.class)
                .add(Restrictions.eq("configId", configId))
                .add(Restrictions.eq("transportDetailId", transportDetailId))
                .add(Restrictions.eq("fieldNo", fieldNo));

        return (configurationFormFields) criteria.uniqueResult();
    }

    /**
     * The 'updateConfigurationFormFields' function will update the configuration form field settings
     *
     * @param formField	object that will hold the form field settings
     *
     * @return This function will not return anything
     */
    @Transactional(readOnly = false)
    public void updateConfigurationFormFields(configurationFormFields formField) {
        sessionFactory.getCurrentSession().saveOrUpdate(formField);
    }

    /**
     * The 'getTransportFTPDetails' function will return the FTP information for the passed in transportDetailId.
     *
     * @param transportDetailsId the id of the selected transport method
     *
     * @return This function will return a list of FTP details
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationFTPFields> getTransportFTPDetails(int transportDetailId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFTPFields.class)
                .add(Restrictions.eq("transportId", transportDetailId));

        return criteria.list();
    }

    /**
     * The 'getTransportFTPDetailsPush' function will return the PUSH FTP details for the passed in transportDetailsId.
     *
     * @param transportDetailsId The id of the selected transport method
     *
     * @return This function will return the PUSH FTP details
     */
    @Override
    @Transactional(readOnly = true)
    public configurationFTPFields getTransportFTPDetailsPush(int transportDetailId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFTPFields.class)
                .add(Restrictions.eq("transportId", transportDetailId))
                .add(Restrictions.eq("method", 2));

        return (configurationFTPFields) criteria.uniqueResult();

    }

    /**
     * The 'getTransportFTPDetailsPull' function will return the PULL FTP details for the passed in transportDetailsId.
     *
     * @param transportDetailsId The id of the selected transport method
     *
     * @return This function will return the PULL FTP details
     */
    @Override
    @Transactional(readOnly = true)
    public configurationFTPFields getTransportFTPDetailsPull(int transportDetailId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFTPFields.class)
                .add(Restrictions.eq("transportId", transportDetailId))
                .add(Restrictions.eq("method", 1));

        return (configurationFTPFields) criteria.uniqueResult();

    }

    /**
     * The 'saveTransportFTP' function will save the transport FTP information into the DB.
     *
     * @param FTPFields The FTP form fields
     *
     * @return this function will not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public void saveTransportFTP(configurationFTPFields FTPFields) {
        sessionFactory.getCurrentSession().saveOrUpdate(FTPFields);
    }

    /**
     * The 'getTransportMethodById' function will return the name of a transport method based on the id passed in.
     *
     * @param Id	This will hold the id of the transport method to retrieve
     *
     * @Return This function will return a string (transport Method).
     */
    @Override
    @Transactional(readOnly = true)
    public String getTransportMethodById(int Id) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT transportMethod FROM ref_transportMethods where id = :Id")
                .setParameter("Id", Id);

        String transportMethod = (String) query.uniqueResult();

        return transportMethod;
    }

    /**
     * The 'getTransportMessageTypes' function will return a list of configurations the current transport is configured to accept message types for.
     *
     * @param configTransportId The current transport id to search on
     *
     * @return This function will return a list of configurationTransportMessageType objects.
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationTransportMessageTypes> getTransportMessageTypes(int configTransportId) {
        Query query = sessionFactory.getCurrentSession().createQuery("FROM configurationTransportMessageTypes where configTransportId = :configTransportId");
        query.setParameter("configTransportId", configTransportId);

        return query.list();
    }

    /**
     * The 'deleteTransportMessageTypes' function will remove all associated message types for the passed in transport method;
     *
     * @param configTransportId The id for the selected config transport
     *
     * @retuern This function does not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteTransportMessageTypes(int configTransportId) {
        Query query = sessionFactory.getCurrentSession().createQuery("DELETE FROM configurationTransportMessageTypes where configTransportId = :configTransportId");
        query.setParameter("configTransportId", configTransportId);

        query.executeUpdate();
    }

    /**
     * The 'saveTransportMessageTypes' function will save the association between configuration transport and message types.
     *
     * @param messageType The configurationTransportMessageTypes object
     *
     * @return This function does not return anything.
     */
    @Transactional(readOnly = false)
    public void saveTransportMessageTypes(configurationTransportMessageTypes messageType) {
        sessionFactory.getCurrentSession().save(messageType);
    }

    /**
     * this method will returns a list of required form field for a configuration*
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationFormFields> getRequiredFieldsForConfig(Integer configId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFormFields.class)
                .add(Restrictions.eq("configId", configId))
                .add(Restrictions.eq("required", true))
                .addOrder(Order.asc("fieldNo"));

        return criteria.list();
    }

    /**
     * this method returns a list of cff by validation type if 0 is passed in, we get them all *
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationFormFields> getCffByValidationType(
            Integer configId, Integer validationTypeId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFormFields.class)
                .add(Restrictions.eq("configId", configId));
        if (validationTypeId != 0) {
            criteria.add(Restrictions.eq("validationTypeId", validationTypeId));
        }
        criteria.addOrder(Order.asc("fieldNo"));

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationTransport> getDistinctConfigTransportForOrg(Integer orgId, Integer transportMethodId) {
        try {

            String sql = ("select distinct delimChar, errorHandling, autoRelease, fileLocation, fileType, containsHeaderRow, "
                    + " transportMethodId, encodingId from configurationTransportDetails, ref_delimiters , configurationMessageSpecs "
                    + " where ref_delimiters.id = configurationTransportDetails.fileDelimiter "
                    + " and configurationMessageSpecs.configId = configurationTransportDetails.configId "
                    + " and transportMethodId = :transportMethodId and configurationTransportDetails.configId in "
                    + "(select id from configurations where orgId = :orgId and type = 1);");
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                    Transformers.aliasToBean(configurationTransport.class));
            query.setParameter("orgId", orgId);
            query.setParameter("transportMethodId", transportMethodId);

            List<configurationTransport> configurationTransports = query.list();

            return configurationTransports;

        } catch (Exception ex) {
            System.err.println("getDistinctConfigTransportForOrg " + ex.getCause());
            ex.printStackTrace();

            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationMessageSpecs> getConfigurationMessageSpecsForUserTransport(Integer userId, Integer transportMethodId, boolean getZeroMessageTypeCol) {
        try {

            String sql = ("select * from configurationMessageSpecs where configId in (select configId from configurationTransportDetails where configId in "
                    + "(select sourceconfigId from configurationconnectionsenders, configurationconnections where configurationconnectionsenders.connectionId = configurationconnections.id"
                    + " and userId  = :userId) and transportmethodId = :transportMethodId)");
            if (!getZeroMessageTypeCol) {
                sql = sql + " and messageTypeCol != 0";
            }
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                    Transformers.aliasToBean(configurationMessageSpecs.class));
            query.setParameter("userId", userId);
            query.setParameter("transportMethodId", transportMethodId);

            List<configurationMessageSpecs> configurationMessageSpecs = query.list();

            return configurationMessageSpecs;

        } catch (Exception ex) {
            System.err.println("getConfigurationMessageSpecsForUserTransport  " + ex.getCause());
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * The 'getConfigurationFieldsByFieldNo' function will return a list of form fields for the selected configuration and selected Field No
     *
     * @param configId The id of the selected configuration
     * @param fieldNo The integer value of the field you want to return fields
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public configurationFormFields getCFFByFieldNo(int configId, int fieldNo) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFormFields.class)
                .add(Restrictions.eq("configId", configId))
                .add(Restrictions.eq("fieldNo", fieldNo));

        return (configurationFormFields) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationMessageSpecs> getConfigurationMessageSpecsForOrgTransport(
            Integer orgId, Integer transportMethodId, boolean getZeroMessageTypeCol) {
        try {

            String sql = ("select * from configurationMessageSpecs where configId in ("
                    + "select configId from configurationTransportDetails where configId in (select id from configurations where orgId = :orgId)"
                    + " and transportmethodId = :transportMethodId)");
            if (!getZeroMessageTypeCol) {
                sql = sql + " and messageTypeCol != 0";
            }
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                    Transformers.aliasToBean(configurationMessageSpecs.class));
            query.setParameter("orgId", orgId);
            query.setParameter("transportMethodId", transportMethodId);

            List<configurationMessageSpecs> configurationMessageSpecs = query.list();

            return configurationMessageSpecs;

        } catch (Exception ex) {
            System.err.println("getConfigurationMessageSpecs  " + ex.getCause());
            ex.printStackTrace();

            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationTransport> getConfigTransportForFileExtAndPath(String fileExt, Integer transportMethodId, Integer status, Integer transportDetailsId) {
        try {
	    
	    String sql = "select b.delimChar, c.containsHeaderRow , a.fileDelimiter, a.fileLocation, a.encodingId "
		    + "from configurationTransportDetails a inner join "
		    + "ref_delimiters b on b.id = a.fileDelimiter inner join "
		    + "configurationMessageSpecs c on c.configId = a.configId inner join "
		    + "configurations d on d.id = a.configId "
		    + "where a.id = :transportDetailId and a.fileExt = :fileExt and a.transportMethodId = :transportMethodId "
		    + "and d.type = 1 and d.status = :status";
	  
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(configurationTransport.class));
            query.setParameter("fileExt", fileExt);
            query.setParameter("transportMethodId", transportMethodId);
            query.setParameter("status", status);
            query.setParameter("transportDetailId", transportDetailsId);

            List<configurationTransport> configurationTransports = query.list();

            return configurationTransports;

        } catch (Exception ex) {
            System.err.println("getConfigTransportForConfigIds " + ex.getCause());
            ex.printStackTrace();

            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationTransport> getTransportListForFileExtAndPath(String fileExt, Integer transportMethodId, Integer status, Integer transportDetailsId) {
       
	try {

            String sql = "select a.* "
		    + "from configurationtransportdetails a inner join "
		    + "configurations b on b.id = a.configId "
		    + "where a.id = :transportDetailsId and a.fileExt = :fileExt and a.transportMethodId = :transportMethodId and a.status = :status and b.type = 1";
	    
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(configurationTransport.class));
            query.setParameter("fileExt", fileExt);
            query.setParameter("transportMethodId", transportMethodId);
            query.setParameter("status", status);
            query.setParameter("transportDetailsId", transportDetailsId);

            List<configurationTransport> transportList = query.list();

            return transportList;

        } catch (Exception ex) {
            System.err.println("getTransportListForFileExtAndPath " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public configurationTransport getTransportDetailsByTransportId(Integer transportId) {
        try {
            Query query = sessionFactory.getCurrentSession().createQuery("from configurationTransport where id = :id");
            query.setParameter("id", transportId);
            return (configurationTransport) query.uniqueResult();
        } catch (Exception ex) {
            System.err.println("getTransportDetailsByTransportId " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getOrgIdForFTPPath(configurationFTPFields ftpInfo) throws Exception {
        try {
	    
	    String sql = ("select b.orgId "
		    + "from configurationtransportdetails a inner join "
		    + "configurations b on b.id = a.configId "
		    + "where a.id = :transportId");
	    
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
            query.setParameter("transportId", ftpInfo.gettransportId());

            Integer orgId = (Integer) query.list().get(0);

            return orgId;

        } catch (Exception ex) {
            System.err.println("getOrgIdForFTPPath  " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getMinMaxFileSize(String fileExt, Integer transportMethodId) {
        try {
            String sql = ("select min(maxFileSize) as filesize from configurationTransportDetails "
                    + " where transportmethodid = :transportMethodId and fileext = :fileExt");
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
            query.setParameter("fileExt", fileExt);
            query.setParameter("transportMethodId", transportMethodId);

            Integer fileSize = (Integer) query.list().get(0);

            return fileSize;

        } catch (Exception ex) {
            System.err.println("getMinMaxFileSize  " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationTransport> getCountContainsHeaderRow(String fileExt, Integer transportMethodId) {
        try {
            String sql = ("select distinct containsHeaderRow from configurationTransportDetails, ref_delimiters , configurationMessageSpecs "
                    + " where ref_delimiters.id = configurationTransportDetails.fileDelimiter "
                    + " and configurationMessageSpecs.configId = configurationTransportDetails.configId"
                    + " and fileext = :fileExt and transportmethodId = :transportMethodId"
                    + " and configurationTransportDetails.configId in (select id from configurations where type = 1)");
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                    Transformers.aliasToBean(configurationTransport.class));
            query.setParameter("fileExt", fileExt);
            query.setParameter("transportMethodId", transportMethodId);

            List<configurationTransport> headerRows = query.list();

            return headerRows;

        } catch (Exception ex) {
            System.err.println("getCountContainsHeaderRow  " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Integer> getConfigCount(String fileExt, Integer transportMethodId, Integer fileDelimiter) {
        try {
            String sql = (" select configId from configurationTransportDetails "
                    + " where transportmethodid = :transportMethodId and fileext = :fileExt "
                    + " and filedelimiter = :fileDelimiter");
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
            query.setParameter("fileExt", fileExt);
            query.setParameter("transportMethodId", transportMethodId);
            query.setParameter("fileDelimiter", fileDelimiter);

            List<Integer> configs = query.list();

            return configs;

        } catch (Exception ex) {
            System.err.println("getConfigCount  " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationTransport> getDistinctDelimCharForFileExt(String fileExt, Integer transportMethodId) {
        try {

            String sql = ("select distinct delimChar, fileDelimiter "
                    + " from configurationTransportDetails, ref_delimiters  "
                    + " where ref_delimiters.id = configurationTransportDetails.fileDelimiter "
                    + " and transportMethodId = :transportMethodId "
                    + " and configurationTransportDetails.fileExt = :fileExt"
                    + " and configurationTransportDetails.configId in (select id from configurations where type = 1)");
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                    Transformers.aliasToBean(configurationTransport.class));
            query.setParameter("transportMethodId", transportMethodId);
            query.setParameter("fileExt", fileExt);

            List<configurationTransport> configurationTransports = query.list();

            return configurationTransports;

        } catch (Exception ex) {
            System.err.println("getDistinctDelimCharForFileExt " + ex.getCause());
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * The 'saveTransportFileDrop' function will save the transport file drop information into the DB.
     *
     * @param fileDropFields The file drop form fields
     *
     * @return this function will not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public void saveTransportFileDrop(configurationFileDropFields fileDropFields) throws Exception {
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(fileDropFields);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("saveTransportFileDrop " + ex.getCause());
        }
    }

    /**
     * The 'getTransFileDropDetails' function will return the file drop information for the passed in transportDetailId.
     *
     *
     * @return This function will return a list of Rhapsody details
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationFileDropFields> getTransFileDropDetails(int transportDetailId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFileDropFields.class)
                .add(Restrictions.eq("transportId", transportDetailId));

        return criteria.list();
    }

    /**
     * The 'getTransFileDropDetailsPush' function will return the PUSH file drop details for the passed in transportDetailsId.
     *
     *
     * @return This function will return the PUSH file drop details
     */
    @Override
    @Transactional(readOnly = true)
    public configurationFileDropFields getTransFileDropDetailsPush(int transportDetailId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFileDropFields.class)
                .add(Restrictions.eq("transportId", transportDetailId))
                .add(Restrictions.eq("method", 2));

        return (configurationFileDropFields) criteria.uniqueResult();

    }

    /**
     * The 'configurationFileDropFields' function will return the PULL file drop details for the passed in transportDetailsId.
     *
     * @param transportDetailsId The id of the selected transport method
     *
     * @return This function will return the PULL file drop details
     */
    @Override
    @Transactional(readOnly = true)
    public configurationFileDropFields getTransFileDropDetailsPull(int transportDetailId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationFileDropFields.class)
                .add(Restrictions.eq("transportId", transportDetailId))
                .add(Restrictions.eq("method", 1));

        return (configurationFileDropFields) criteria.uniqueResult();

    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationTransport> getTransportEncoding(String fileExt, Integer transportMethodId) {
        try {
            String sql = ("select distinct encodingId from configurationTransportDetails "
                    + " where fileext = :fileExt and transportmethodId = :transportMethodId"
                    + " and configurationTransportDetails.configId in (select id from configurations where type = 1)");
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                    Transformers.aliasToBean(configurationTransport.class));
            query.setParameter("fileExt", fileExt);
            query.setParameter("transportMethodId", transportMethodId);

            List<configurationTransport> encodingIds = query.list();

            return encodingIds;

        } catch (Exception ex) {
            System.err.println("getTransportEncoding  " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * The 'getOrgIdForFileDropPath' method will try and find the organization tied to the passed in file drop details
     * 
     * @param fileDropInfo
     * @return
     * @throws Exception 
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getOrgIdForFileDropPath(configurationFileDropFields fileDropInfo) throws Exception {
	
        try {
             String sql = ("select b.orgId "
		    + "from configurationtransportdetails a inner join "
		    + "configurations b on b.id = a.configId "
		    + "where a.id = :transportId");
	    
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
            query.setParameter("transportId", fileDropInfo.getTransportId());
	    
            Integer orgId = (Integer) query.list().get(0);

            return orgId;

        } catch (Exception ex) {
            System.err.println("getOrgIdForFileDropPath  " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * The 'getTransportMethods' function will return a list of transport methods, it can be active, not active or both
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<TransportMethod> getTransportMethods(List<Integer> statusIds) {
        try {
            Query query = sessionFactory.getCurrentSession()
                    .createSQLQuery("SELECT id, transportMethod FROM ref_transportMethods where active in (:statusIds) order by transportMethod asc")
                    .setResultTransformer(
                            Transformers.aliasToBean(TransportMethod.class));
            query.setParameterList("statusIds", statusIds);
            return query.list();
        } catch (Exception ex) {
            System.err.println("getTransportMethods  " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationTransport> getConfigurationTransportFileExtByFileType(
            Integer orgId, Integer transportMethodId,
            List<Integer> fileTypeIds, List<Integer> statusIds,
            boolean distinctOnly, boolean foroutboundProcessing) {
        Integer configType = 1;
        if (foroutboundProcessing) {
            configType = 2;
        }
        try {
            String sql = "select";
            if (distinctOnly) {
                sql = sql + " distinct ";
            }
            sql = sql + "fileType from configurationTransportDetails where ";
            if (fileTypeIds != null) {
                sql = sql + " fileType in (:fileTypeIds) and ";
            }
            sql = sql + " status in (:statusIds) and  transportmethodid = :transportMethodId and configId "
                    + " in (select id from configurations where type = :configType and orgId = :orgId);";
            Query query = sessionFactory.getCurrentSession()
                    .createSQLQuery(sql)
                    .setResultTransformer(
                            Transformers.aliasToBean(configurationTransport.class));
            if (fileTypeIds != null) {
                query.setParameterList("fileTypeIds", fileTypeIds);
            }
            query.setParameterList("statusIds", statusIds);
            query.setParameter("transportMethodId", transportMethodId);
            query.setParameter("configType", configType);
            query.setParameter("orgId", orgId);

            return query.list();
        } catch (Exception ex) {
            System.err.println("getConfigurationTransportFileExtByFileType  " + ex.getCause());
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<configurationWebServiceFields> getTransWSDetails(int transportDetailId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationWebServiceFields.class)
                .add(Restrictions.eq("transportId", transportDetailId));

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = false)
    public void saveTransportWebService(configurationWebServiceFields wsFields) throws Exception {
        sessionFactory.getCurrentSession().saveOrUpdate(wsFields);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationTransport> getDistinctTransportDetailsForOrgByTransportMethodId(Integer transportMethodId, Integer status, Integer orgId) {
        try {

            String sql = ("select distinct fileExt, delimChar, containsHeaderRow , "
                    + " fileDelimiter, fileLocation, encodingId, configurationTransportDetails.id, maxFileSize "
                    + " from configurationTransportDetails, ref_delimiters , configurationMessageSpecs "
                    + " where ref_delimiters.id = configurationTransportDetails.fileDelimiter "
                    + " and configurationMessageSpecs.configId = configurationTransportDetails.configId"
                    + " and transportmethodId = :transportMethodId"
                    + " and configurationTransportDetails.configId in (select id from configurations where type = 1 and status = :status and orgId = :orgId)");
            if (transportMethodId == 5) {
                sql = sql + " and configurationTransportDetails.id in (select transportId from rel_configurationFileDropFields where method = 1) ";
            } else if (transportMethodId == 3) {
                sql = sql + " and configurationTransportDetails.id in (select transportId from rel_transportftpdetails where method = 1) ";
            } else if (transportMethodId == 6) {
                sql = sql + " and configurationTransportDetails.id in (select transportId from rel_transportWebServiceDetails where method = 1) ";
            }
	  
            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                    Transformers.aliasToBean(configurationTransport.class));
            query.setParameter("orgId", orgId);
            query.setParameter("transportMethodId", transportMethodId);
            query.setParameter("status", status);

            List<configurationTransport> configurationTransports = query.list();

            return configurationTransports;

        } catch (Exception ex) {
            System.err.println("getDistinctTransportDetailsForOrgByTransportMethodId " + ex.getCause());
            ex.printStackTrace();

            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationTransport> getCTForOrgByTransportMethodId(Integer transportMethodId, Integer status, Integer orgId) {
        try {

            String sql = ("select configurationTransportDetails.* "
                    + " from configurationTransportDetails, ref_delimiters , configurationMessageSpecs "
                    + " where ref_delimiters.id = configurationTransportDetails.fileDelimiter "
                    + " and configurationMessageSpecs.configId = configurationTransportDetails.configId"
                    + " and transportmethodId = :transportMethodId"
                    + " and configurationTransportDetails.configId in (select id from configurations where type = 1 and status = :status and orgId = :orgId)");
            if (transportMethodId == 5) {
                sql = sql + " and configurationTransportDetails.id in (select transportId from rel_configurationFileDropFields where method = 1) ";
            } else if (transportMethodId == 3) {
                sql = sql + " and configurationTransportDetails.id in (select transportId from rel_transportftpdetails where method = 1) ";
            } else if (transportMethodId == 6) {
                sql = sql + " and configurationTransportDetails.id in (select transportId from rel_transportWebServiceDetails where method = 1) ";
            }

            Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                    Transformers.aliasToBean(configurationTransport.class));
            query.setParameter("orgId", orgId);
            query.setParameter("transportMethodId", transportMethodId);
            query.setParameter("status", status);

            List<configurationTransport> configurationTransports = query.list();

            return configurationTransports;

        } catch (Exception ex) {
            System.err.println("getCTForOrgByTransportMethodId " + ex.getCause());
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * The 'getTransWSDetailsPush' function will return the PUSH web service details for the passed in transportDetailsId.
     *
     * @param transportDetailsId The id of the selected transport method
     *
     * @return This function will return the PUSH web service details
     */
    @Override
    @Transactional(readOnly = true)
    public configurationWebServiceFields getTransWSDetailsPush(int transportDetailId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationWebServiceFields.class)
                .add(Restrictions.eq("transportId", transportDetailId))
                .add(Restrictions.eq("method", 2));

        return (configurationWebServiceFields) criteria.uniqueResult();

    }

    /**
     * The 'getTransWSDetailsPull' function will return the PULL web service details for the passed in transportDetailsId.
     *
     * @param transportDetailsId The id of the selected transport method
     *
     * @return This function will return the PULL web service details
     */
    @Override
    @Transactional(readOnly = true)
    public configurationWebServiceFields getTransWSDetailsPull(int transportDetailId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationWebServiceFields.class)
                .add(Restrictions.eq("transportId", transportDetailId))
                .add(Restrictions.eq("method", 1));

        return (configurationWebServiceFields) criteria.uniqueResult();

    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationWebServiceSenders> getWSSenderList(int transportId)
            throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationWebServiceSenders.class)
                .add(Restrictions.eq("transportId", transportId));

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = false)
    public void saveWSSender(configurationWebServiceSenders wsSender)
            throws Exception {
        sessionFactory.getCurrentSession().saveOrUpdate(wsSender);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteWSSender(configurationWebServiceSenders wsSender)
            throws Exception {
        sessionFactory.getCurrentSession().delete(wsSender);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public boolean hasConfigsWithMasstranslations(
            Integer orgId, Integer transportMethodId) throws Exception {
        String sql = ("select masstranslation from configurationTransportDetails "
                + " where transportMethodId = :transportMethodId and masstranslation = true "
                + " and configurationTransportDetails.configId in "
                + "(select id from configurations where orgId = :orgId and type = 1);");
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.setParameter("orgId", orgId);
        query.setParameter("transportMethodId", transportMethodId);

        if (query.list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public configurationTransport validateAPICall(String apiCustomCall) throws Exception {
	
	String sql = ("select id, configId "
		+ "from configurationTransportDetails "
                + "where restAPIURL = :restAPIURL "
		+ "and configId in (select id from configurations where status = 1 and type = 1)");
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                    Transformers.aliasToBean(configurationTransport.class));
	query.setParameter("restAPIURL", apiCustomCall);
	
        if(query.list().isEmpty()) {
	    return null;
	}
	else if(query.list().size() > 1) {
	    return null;
	}
	else {
	    return (configurationTransport) query.uniqueResult();
	}
    }
    
    @Override
    @Transactional(readOnly = true)
    public configurationTransport validateAPIAuthentication(String[] credValue, String apiCustomCall) throws Exception {
	
	String sql = ("select id "
		+ "from configurationTransportDetails "
                + "where restAPIURL = :restAPIURL "
		+ "and restAPIUsername = :restAPIUsername "
		+ "and restAPIPassword = :restAPIPassword "
		+ "and configId in (select id from configurations where status = 1 and type = 1)");
	
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(
                    Transformers.aliasToBean(configurationTransport.class));
	query.setParameter("restAPIURL", apiCustomCall);
	query.setParameter("restAPIUsername", credValue[0]);
	query.setParameter("restAPIPassword", credValue[1]);
	
	if(query.list().isEmpty()) {
	    return null;
	}
	else if(query.list().size() > 1) {
	    return null;
	}
	else {
	    return (configurationTransport) query.uniqueResult();
	}

    }
    
    /**
     * The 'getRestAPIMethodName' function will return the name of a rest api method based on the id passed in.
     *
     * @param Id	This will hold the id of the rest api method to retrieve
     *
     * @Return This function will return a string (transport Method).
     */
    @Override
    @Transactional(readOnly = true)
    public String getRestAPIMethodName(Integer methodId) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("SELECT functionName FROM lu_availablerestapifunctions where id = :Id")
                .setParameter("Id", methodId);

        String transportMethod = (String) query.uniqueResult();

        return transportMethod;
    }

     /**
     * The 'saveConfigurationFormFields' function will update the configuration form field settings
     *
     * @param formField	object that will hold the form field settings
     *
     * @return This function will not return anything
     */
    @Transactional(readOnly = false)
    public Integer saveConfigurationFormFields(configurationFormFields formField) {
	
	Integer lastId;

        lastId = (Integer) sessionFactory.getCurrentSession().save(formField);
	
	return lastId;
    }
    
    /**
     * The 'getConfigurationFieldsToCopy' function will return a list of saved form fields for the selected configuration.
     *
     * @param	configId	Will hold the id of the configuration we want to return fields for
     * @param transporetDetailId The id of the selected transport method
     *
     * @return	This function will return a list of configuration form fields
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationFormFields> getConfigurationFieldsToCopy(int configId) {
        
	String sql = "select * "
	    + "from configurationformfields "
	    + "where configId = " + configId + " "
	    + "order by fieldNo asc";
	
	Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(configurationFormFields.class));
           
        return query.list();
    }
}
