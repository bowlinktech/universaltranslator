package com.hel.ut.service;

import java.util.List;

import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.messageType;
import com.hel.ut.model.messageTypeDataTranslations;
import com.hel.ut.model.messageTypeFormFields;
import com.hel.ut.model.validationType;

public interface messageTypeManager {

    Integer createMessageType(messageType messagetype) throws Exception;

    void updateMessageType(messageType messagetype);

    void deleteMessageType(int messageTypeId);

    messageType getMessageTypeById(int messageTypeId);

    messageType getMessageTypeByName(String name);

    List<messageType> getMessageTypes();

    List<messageType> getLatestMessageTypes(int maxResults);

    List<messageType> getActiveMessageTypes();

    List<messageType> getAvailableMessageTypes(int orgId);

    Long findTotalMessageTypes();

    List<messageTypeFormFields> getMessageTypeFields(int messageTypeId);

    void updateMessageTypeFields(messageTypeFormFields formField);

    void saveMessageTypeFields(messageTypeFormFields formField);

    @SuppressWarnings("rawtypes")
    List getInformationTables();

    @SuppressWarnings("rawtypes")
    List getAllTables();

    @SuppressWarnings("rawtypes")
    List getTableColumns(String tableName);

    @SuppressWarnings("rawtypes")
    List getValidationTypes();

    @SuppressWarnings("rawtypes")
    String getValidationById(int id);

    @SuppressWarnings("rawtypes")
    List getDelimiters();

    @SuppressWarnings("rawtypes")
    List getFieldTypes();

    Long getTotalFields(int messageTypeId);

    List<Crosswalks> getCrosswalks(int page, int maxResults, int orgId);

    Integer createCrosswalk(Crosswalks crosswalkDetails) throws Exception;

    double findTotalCrosswalks(int orgId);

    Crosswalks getCrosswalk(int cwId);

    @SuppressWarnings("rawtypes")
    List getCrosswalkData(int cwId);

    void saveDataTranslations(messageTypeDataTranslations translations);

    List<messageTypeDataTranslations> getMessageTypeTranslations(int messageTypeId);

    String getFieldName(int fieldId);

    String getCrosswalkName(int cwId);

    Long checkCrosswalkName(String name, int orgId);

    void deleteDataTranslations(int messageTypeId);

    List<validationType> getValidationTypes1();

    List<messageType> getAssociatedMessageTypes(int orgId);
    
    Integer uploadNewFileForCrosswalk(Crosswalks crosswalkDetails) throws Exception;
    
    String getDelimiterChar(int delimId) throws Exception;

}
