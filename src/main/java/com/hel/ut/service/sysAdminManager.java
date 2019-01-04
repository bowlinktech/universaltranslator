package com.hel.ut.service;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hel.ut.model.custom.LookUpTable;
import com.hel.ut.model.custom.TableData;
import com.hel.ut.model.lutables.lu_ProcessStatus;
import com.hel.ut.model.Macros;
import com.hel.ut.model.MoveFilesLog;
import com.hel.ut.model.mainHL7Details;
import com.hel.ut.model.mainHL7Elements;
import com.hel.ut.model.mainHL7Segments;

/**
 * 1. sysAdminManager should handle the adding, deleting and modifying lu_ table items 2. It should
 *
 * @author gchan
 *
 */
public interface sysAdminManager {


    LookUpTable getTableInfo(String urlId);

    List<TableData> getDataList(String utTableName, String searchTerm);

    Integer findTotalDataRows(String utTableName);

    boolean deleteDataItem(String utTableName, int id);

    TableData getTableData(Integer id, String utTableName);

    void createTableDataHibernate(TableData tableData, String utTableName);

    boolean updateTableData(TableData tableData, String utTableName);

    List<Macros> getMarcoList(String searchTerm);

    Long findTotalMacroRows();

    Long findtotalHL7Entries();

    Long findtotalNewsArticles();

    String addWildCardSearch(String searchTerm);

    String addWildCardLUSearch(String searchTerm);

    boolean deleteMacro(int id);

    void createMacro(Macros macro);

    boolean updateMacro(Macros macro);

    void createProcessStatus(lu_ProcessStatus lu);

    lu_ProcessStatus getProcessStatusById(int id) throws Exception;

    void updateProcessStatus(lu_ProcessStatus lu);

    boolean writeFile(String path, InputStream inputStream, String directory);

    String getDeployedPath(HttpServletRequest request);

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
