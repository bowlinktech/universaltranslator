package com.hel.ut.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hel.ut.dao.sysAdminDAO;
import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.custom.LookUpTable;
import com.hel.ut.model.custom.TableData;
import com.hel.ut.model.lutables.lu_ProcessStatus;
import com.hel.ut.model.Macros;
import com.hel.ut.model.MoveFilesLog;
import com.hel.ut.model.mainHL7Details;
import com.hel.ut.model.mainHL7Elements;
import com.hel.ut.model.mainHL7Segments;
import com.hel.ut.service.sysAdminManager;

@Service
public class sysAdminManagerImpl implements sysAdminManager {

    /**
     * about 90% of our tables falls into the standard table category, which is id, displayText, description, status and isCustom)
     *
     * @param utTableName
     * @param tableId
     */
    @Autowired
    private sysAdminDAO sysAdminDAO;

    @Override
    public List<TableData> getDataList(String utTableName, String searchTerm) {
        return sysAdminDAO.getDataList(utTableName, addWildCardSearch(searchTerm));
    }

    @Override
    public Integer findTotalDataRows(String utTableName) {
        return sysAdminDAO.findTotalDataRows(utTableName);
    }

    @Override
    public LookUpTable getTableInfo(String urlId) {
        return sysAdminDAO.getTableInfo(urlId);

    }

    @Override
    public boolean deleteDataItem(String utTableName, int id) {
        return sysAdminDAO.deleteDataItem(utTableName, id);
    }

    @Override
    public TableData getTableData(Integer id, String utTableName) {
        return sysAdminDAO.getTableData(id, utTableName);
    }

    @Override
    public boolean updateTableData(TableData tableData, String utTableName) {
        return sysAdminDAO.updateTableData(tableData, utTableName);
    }

    @Override
    public void createTableDataHibernate(TableData tableData, String utTableName) {
        sysAdminDAO.createTableDataHibernate(tableData, utTableName);
    }

    @Override
    public List<Macros> getMarcoList(String searchTerm) {
        return sysAdminDAO.getMarcoList(addWildCardSearch(searchTerm));
    }

    @Override
    public Long findTotalMacroRows() {
        return sysAdminDAO.findTotalMacroRows();
    }

    @Override
    public Long findtotalHL7Entries() {
        return sysAdminDAO.findtotalHL7Entries();
    }

    @Override
    public Long findtotalNewsArticles() {
        return sysAdminDAO.findtotalNewsArticles();
    }

    @Override
    public String addWildCardSearch(String searchTerm) {

        if (!searchTerm.startsWith("%")) {
            searchTerm = "%" + searchTerm;
        }
        if (!searchTerm.endsWith("%")) {
            searchTerm = searchTerm + "%";
        }

        return searchTerm;
    }

    @Override
    public String addWildCardLUSearch(String searchTerm) {
        /**
         * all look up tables must begin with lu_ *
         */
        if (searchTerm.toLowerCase().startsWith("%")) {
            searchTerm = "lu_" + searchTerm;
        } else if (!searchTerm.toLowerCase().startsWith("lu_")) {
            searchTerm = "lu_%" + searchTerm;
        }
        if (!searchTerm.endsWith("%")) {
            searchTerm = searchTerm + "%";
        }
        return searchTerm;
    }

    @Override
    public boolean deleteMacro(int id) {
        return sysAdminDAO.deleteMacro(id);
    }

    @Override
    public void createMacro(Macros macro) {
        sysAdminDAO.createMacro(macro);
    }

    @Override
    public boolean updateMacro(Macros macro) {
        return sysAdminDAO.updateMacro(macro);

    }

    @Override
    public void createProcessStatus(lu_ProcessStatus lu) {
        sysAdminDAO.createProcessStatus(lu);

    }

    @Override
    public lu_ProcessStatus getProcessStatusById(int id) throws Exception {
        return sysAdminDAO.getProcessStatusById(id);
    }

    @Override
    public void updateProcessStatus(lu_ProcessStatus lu) {
        sysAdminDAO.updateProcessStatus(lu);
    }

    @Override
    public boolean writeFile(String fileName, InputStream inputStream, String directory) {
        boolean writeError = false;
        OutputStream outputStream = null;

        try {

            File newFile = null;

            newFile = new File(directory + fileName);

            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            writeError = true;
        }
        return writeError;
    }

    @Override
    public String getDeployedPath(HttpServletRequest request) {
        ServletContext servletContext = request.getSession().getServletContext();
        String relativeWebPath = "../../../img/admin/health-e-link/sp-health-e-link.png";
        String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);
        int firstIndex = absoluteDiskPath.indexOf("../");
        return absoluteDiskPath.substring(0, firstIndex);

    }

    @Override
    public List<mainHL7Details> getHL7List() throws Exception {
        return sysAdminDAO.getHL7List();
    }

    @Override
    public mainHL7Details getHL7Details(int hl7Id) throws Exception {
        return sysAdminDAO.getHL7Details(hl7Id);
    }

    @Override
    public List<mainHL7Segments> getHL7Segments(int hl7Id) {
        return sysAdminDAO.getHL7Segments(hl7Id);
    }

    @Override
    public List<mainHL7Elements> getHL7Elements(int hl7Id, int segmentId) {
        return sysAdminDAO.getHL7Elements(hl7Id, segmentId);
    }

    @Override
    public void updateHL7Details(mainHL7Details details) {
        sysAdminDAO.updateHL7Details(details);
    }

    @Override
    public void updateHL7Segments(mainHL7Segments segment) {
        sysAdminDAO.updateHL7Segments(segment);
    }

    @Override
    public void updateHL7Elements(mainHL7Elements element) {
        sysAdminDAO.updateHL7Elements(element);
    }

    @Override
    public int createHL7(mainHL7Details details) {
        return sysAdminDAO.createHL7(details);
    }

    @Override
    public int saveHL7Segment(mainHL7Segments newSegment) {
        return sysAdminDAO.saveHL7Segment(newSegment);
    }

    @Override
    public int saveHL7Element(mainHL7Elements newElement) {
        return sysAdminDAO.saveHL7Element(newElement);
    }

    @Override
    public List<lu_ProcessStatus> getAllProcessStatus() throws Exception {
        return sysAdminDAO.getAllProcessStatus();
    }

    @Override
    public List<lu_ProcessStatus> getAllHistoryFormProcessStatus() throws Exception {
        return sysAdminDAO.getAllHistoryFormProcessStatus();
    }

    @Override
    public Long findTotalUsers() throws Exception {
        return sysAdminDAO.findTotalUsers();
    }
    
	@Override
    public List<MoveFilesLog> getMoveFilesLog(Integer statusId) throws Exception {
        return sysAdminDAO.getMoveFilesLog(statusId);
    }
    
    @Override
    public void deleteMoveFilesLog(MoveFilesLog moveFilesLog) throws Exception {
    	sysAdminDAO.deleteMoveFilesLog(moveFilesLog);
    }
	
    @Override
    public Long findTotalStandardCrosswalks() throws Exception {
        return sysAdminDAO.findTotalStandardCrosswalks();
    }
    
    @Override
    public List<Crosswalks> getStandardCrosswalks() throws Exception {
	return sysAdminDAO.getStandardCrosswalks();
    }
}
