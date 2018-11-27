package com.hel.ut.dao;

import java.util.List;

import com.hel.ut.model.custom.LogoInfo;
import com.hel.ut.model.custom.LookUpTable;
import com.hel.ut.model.custom.TableData;
import com.hel.ut.model.lutables.lu_ProcessStatus;
import com.hel.ut.model.Macros;
import com.hel.ut.model.MoveFilesLog;
import com.hel.ut.model.mainHL7Details;
import com.hel.ut.model.mainHL7Elements;
import com.hel.ut.model.mainHL7Segments;

import org.springframework.stereotype.Repository;

@Repository
public interface sysAdminDAO {

    List<TableData> getDataList(String utTableName, String searchTerm);

    Integer findTotalDataRows(String utTableName);

    LookUpTable getTableInfo(String urlId);

    boolean deleteDataItem(String utTableName, int id);

    TableData getTableData(Integer id, String utTableName);

    void createTableDataHibernate(TableData tableData, String utTableName);

    boolean updateTableData(TableData tableData, String utTableName);

    List<Macros> getMarcoList(String searchTerm);

    Long findTotalMacroRows();

    Long findtotalHL7Entries();

    Long findtotalNewsArticles();

    boolean deleteMacro(int id);

    void createMacro(Macros macro);

    boolean updateMacro(Macros macro);

    void createProcessStatus(lu_ProcessStatus lu);

    lu_ProcessStatus getProcessStatusById(int id) throws Exception;

    void updateProcessStatus(lu_ProcessStatus lu);

    LogoInfo getLogoInfo();

    void updateLogoInfo(LogoInfo logoDetails);

    List<mainHL7Details> getHL7List() throws Exception;

    mainHL7Details getHL7Details(int hl7Id) throws Exception;

    List<mainHL7Segments> getHL7Segments(int hl7Id);

    List<mainHL7Elements> getHL7Elements(int hl7Id, int segmentId);

    int createHL7(mainHL7Details details);

    void updateHL7Details(mainHL7Details details);

    void updateHL7Segments(mainHL7Segments segment);

    void updateHL7Elements(mainHL7Elements element);

    int saveHL7Segment(mainHL7Segments newSegment);

    int saveHL7Element(mainHL7Elements newElement);

    List<lu_ProcessStatus> getAllProcessStatus() throws Exception;

    List<lu_ProcessStatus> getAllHistoryFormProcessStatus() throws Exception;

    Long findTotalUsers() throws Exception;
    
    List <MoveFilesLog> getMoveFilesLog (Integer statusId) throws Exception;
    
    void deleteMoveFilesLog(MoveFilesLog moveFilesLog) throws Exception;

}
