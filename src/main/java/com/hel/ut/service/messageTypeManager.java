package com.hel.ut.service;

import com.hel.ut.model.CrosswalkData;
import java.util.List;

import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.validationType;

public interface messageTypeManager {

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

    Long getTotalFields(int messageTypeId);

    List<Crosswalks> getCrosswalks(int page, int maxResults, int orgId);

    Integer createCrosswalk(Crosswalks crosswalkDetails) throws Exception;

    double findTotalCrosswalks(int orgId);

    Crosswalks getCrosswalk(int cwId);

    @SuppressWarnings("rawtypes")
    List getCrosswalkData(int cwId);

    String getFieldName(int fieldId);

    String getCrosswalkName(int cwId);

    Long checkCrosswalkName(String name, int orgId);

    List<validationType> getValidationTypes1();

    Integer uploadNewFileForCrosswalk(Crosswalks crosswalkDetails) throws Exception;
    
    String getDelimiterChar(int delimId) throws Exception;
    
    List<Crosswalks> getCrosswalksForConfig(int page, int maxCrosswalks, int orgId, int configId, boolean inUseOnly);
    
    void deleteCrosswalk(Integer cwId) throws Exception;
    
    List getConfigCrosswalksWithData(Integer orgId, Integer configId);
    
    void saveCrosswalkData(CrosswalkData cwData);
    
    void executeSQLStatement(String sqlStatement);
}
