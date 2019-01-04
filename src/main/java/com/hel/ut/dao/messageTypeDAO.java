package com.hel.ut.dao;

import java.util.List;

import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.validationType;
import org.springframework.stereotype.Repository;

@Repository
public interface messageTypeDAO {

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

    Long getTotalFields(int messageTypeId);

    List<Crosswalks> getCrosswalks(int page, int maxResults, int orgId);

    Integer createCrosswalk(Crosswalks crosswalkDetails);

    Long checkCrosswalkName(String name, int orgId);

    double findTotalCrosswalks(int orgId);

    Crosswalks getCrosswalk(int cwId);

    @SuppressWarnings("rawtypes")
    List getCrosswalkData(int cwId);

    String getFieldName(int fieldId);

    String getCrosswalkName(int cwId);

    String getDelimiterChar(int id);

    List<validationType> getValidationTypes1();

    void executeSQLStatement(String sqlStmt);
    
    void updateCrosswalk(Crosswalks crosswalkDetails);
    
}
