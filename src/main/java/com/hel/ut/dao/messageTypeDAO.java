package com.hel.ut.dao;

import java.util.List;

import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.messageType;
import com.hel.ut.model.messageTypeDataTranslations;
import com.hel.ut.model.messageTypeFormFields;
import com.hel.ut.model.validationType;
import org.springframework.stereotype.Repository;

@Repository
public interface messageTypeDAO {

    Integer createMessageType(messageType messagetype);

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

    String getValidationById(int id);

    @SuppressWarnings("rawtypes")
    List getDelimiters();

    @SuppressWarnings("rawtypes")
    List getFieldTypes();

    Long getTotalFields(int messageTypeId);

    List<Crosswalks> getCrosswalks(int page, int maxResults, int orgId);

    Integer createCrosswalk(Crosswalks crosswalkDetails);

    Long checkCrosswalkName(String name, int orgId);

    double findTotalCrosswalks(int orgId);

    Crosswalks getCrosswalk(int cwId);

    @SuppressWarnings("rawtypes")
    List getCrosswalkData(int cwId);

    void saveDataTranslations(messageTypeDataTranslations translations);

    List<messageTypeDataTranslations> getMessageTypeTranslations(int messageTypeId);

    String getFieldName(int fieldId);

    String getCrosswalkName(int cwId);

    String getDelimiterChar(int id);

    void deleteDataTranslations(int messageTypeId);

    List<validationType> getValidationTypes1();

    List<messageType> getAssociatedMessageTypes(int orgId);
    
    void loadExcelContents(Integer id, String fileName);
    
    void executeSQLStatement(String sqlStmt);
    
    void updateCrosswalk(Crosswalks crosswalkDetails);
    
}
