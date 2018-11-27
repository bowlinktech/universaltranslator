package com.hel.ut.dao.impl;

import java.util.List;
import java.util.Properties;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hel.ut.dao.sysAdminDAO;
import com.hel.ut.dao.UtilitiesDAO;
import com.hel.ut.model.Macros;
import com.hel.ut.model.custom.LogoInfo;
import com.hel.ut.model.custom.LookUpTable;
import com.hel.ut.model.custom.TableData;
import com.hel.ut.model.lutables.lu_ProcessStatus;
import com.hel.ut.model.MoveFilesLog;
import com.hel.ut.model.mainHL7Details;
import com.hel.ut.model.mainHL7Elements;
import com.hel.ut.model.mainHL7Segments;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @see com.hel.ut.dao.sysAdminDAO
 * @author gchan
 */
@Repository
public class sysAdminDAOImpl implements sysAdminDAO {

    @Autowired
    private UtilitiesDAO udao;

    @Autowired
    @Qualifier("ilsessionFactory")
    private SessionFactory sessionFactory;

    @Resource(name = "myProps")
    private Properties myProps;

    


    /**
     * this method takes the table name and searchTerm (if there is one) and return the data in the table
     *
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public List<TableData> getDataList(String utTableName, String searchTerm) {

        String sql = "select id, displayText, description, "
                + " isCustom as custom, status as status, dateCreated as dateCreated from "
                + utTableName + " where (displayText like :searchTerm or description like :searchTerm) order by id";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
                .addScalar("id", StandardBasicTypes.INTEGER)
                .addScalar("displayText", StandardBasicTypes.STRING)
                .addScalar("description", StandardBasicTypes.STRING)
                .addScalar("custom", StandardBasicTypes.BOOLEAN)
                .addScalar("status", StandardBasicTypes.BOOLEAN)
                .addScalar("dateCreated", StandardBasicTypes.DATE)
                .setResultTransformer(Transformers.aliasToBean(TableData.class))
                .setParameter("searchTerm", searchTerm);

        List<TableData> dataList = query.list();
        // TODO
        /**
         * add codes for paging *
         */

        return dataList;

    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public Integer findTotalDataRows(String utTableName) {
        String sql = "select count(id) as rowCount from " + utTableName;
        Query query = sessionFactory
                .getCurrentSession()
                .createSQLQuery(sql).addScalar("rowCount", StandardBasicTypes.INTEGER);
        Integer rowCount = (Integer) query.list().get(0);

        return rowCount;
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public LookUpTable getTableInfo(String urlId) {

        LookUpTable lookUpTable = new LookUpTable();
        Query query = sessionFactory.getCurrentSession().createSQLQuery(""
                + "select utTableName, "
                + "displayText as displayName, "
                + "urlId, description, "
                + "dateCreated from lookUpTables where urlId = :urlId")
                .addScalar("utTableName", StandardBasicTypes.STRING)
                .addScalar("displayName", StandardBasicTypes.STRING)
                .addScalar("urlId", StandardBasicTypes.STRING)
                .addScalar("description", StandardBasicTypes.STRING)
                .addScalar("dateCreated", StandardBasicTypes.DATE).setResultTransformer(
                Transformers.aliasToBean(LookUpTable.class)).setParameter("urlId", urlId);

        if (query.list().size() == 1) {
            lookUpTable = (LookUpTable) query.list().get(0);
        }

        return lookUpTable;

    }

    /**
     * this method deletes the data item in the table*
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public boolean deleteDataItem(String utTableName, int id) {
        String sql = "delete from " + utTableName + " where id = :id";
        Query deleteTable = sessionFactory.getCurrentSession().createSQLQuery(sql)
                .addScalar("id", StandardBasicTypes.INTEGER).setParameter("id", id);
        try {
            deleteTable.executeUpdate();
            return true;
        } catch (Throwable ex) {
            System.err.println("deleteDataItem failed." + ex);
            return false;

        }
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public TableData getTableData(Integer id, String utTableName) {
        //we create sql, we transform
        TableData tableData = new TableData();
        String sql = ("select id, displayText, description, isCustom as custom, "
                + "status "
                + " from " + utTableName + " where id = :id");
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
                .addScalar("id", StandardBasicTypes.INTEGER)
                .addScalar("displayText", StandardBasicTypes.STRING)
                .addScalar("description", StandardBasicTypes.STRING)
                .addScalar("custom", StandardBasicTypes.BOOLEAN)
                .addScalar("status", StandardBasicTypes.BOOLEAN).setResultTransformer(
                Transformers.aliasToBean(TableData.class)).setParameter("id", id);

        if (query.list().size() == 1) {
            tableData = (TableData) query.list().get(0);
        }
        return tableData;

    }

    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public boolean updateTableData(TableData tableData, String utTableName) {
        boolean updated = false;
        String sql = "update " + utTableName
                + " set displayText = :displayText, "
                + "description = :description, "
                + "status = :status, "
                + "isCustom = :isCustom "
                + "where id = :id ";
        Query updateData = sessionFactory.getCurrentSession().createSQLQuery(sql)
                .addScalar("displayText", StandardBasicTypes.STRING)
                .addScalar("description", StandardBasicTypes.STRING)
                .addScalar("isCustom", StandardBasicTypes.BOOLEAN)
                .addScalar("status", StandardBasicTypes.BOOLEAN)
                .addScalar("id", StandardBasicTypes.INTEGER)
                .setParameter("displayText", tableData.getDisplayText())
                .setParameter("description", tableData.getDescription())
                .setParameter("isCustom", tableData.isCustom())
                .setParameter("status", tableData.isStatus())
                .setParameter("id", tableData.getId());
        try {
            updateData.executeUpdate();
            updated = true;
        } catch (Throwable ex) {
            System.err.println("update table data failed." + ex);
        }
        return updated;

    }

    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void createTableDataHibernate(TableData tableData, String utTableName) {

        String sql = "insert into " + utTableName + " (displayText, description, isCustom, status) "
                + "values (:displayText, :description, :isCustom, :status)";
        Query insertData = sessionFactory.getCurrentSession().createSQLQuery(sql)
                .addScalar("displayText", StandardBasicTypes.STRING)
                .addScalar("description", StandardBasicTypes.STRING)
                .addScalar("isCustom", StandardBasicTypes.BOOLEAN)
                .addScalar("status", StandardBasicTypes.BOOLEAN)
                .setParameter("displayText", tableData.getDisplayText())
                .setParameter("description", tableData.getDescription())
                .setParameter("isCustom", tableData.isCustom())
                .setParameter("status", tableData.isStatus());
        try {
            insertData.executeUpdate();
        } catch (Throwable ex) {
            System.err.println("insert table data failed." + ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public List<Macros> getMarcoList(String searchTerm) {

        Query query = sessionFactory.getCurrentSession().createQuery("from Macros where "
                + "macro_short_name like :searchTerm "
                + "order by categoryId, macro_short_name asc");
        query.setParameter("searchTerm", searchTerm);

        return query.list();
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public Long findTotalMacroRows() {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalMacros from Macros");
        Long totalMacros = (Long) query.uniqueResult();
        return totalMacros;
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public Long findtotalHL7Entries() {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalHL7 from mainHL7Details");
        Long totalHL7Entries = (Long) query.uniqueResult();
        return totalHL7Entries;
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public Long findtotalNewsArticles() {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalArticles from newsArticle");
        Long totalNewsArticles = (Long) query.uniqueResult();
        return totalNewsArticles;
    }

    /**
     * this method deletes the macro in the table*
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public boolean deleteMacro(int id) {
        Query deletMarco = sessionFactory.getCurrentSession().createQuery("delete from Macros where id = :macroId)");
        deletMarco.setParameter("macroId", id);
        deletMarco.executeUpdate();
        try {
            deletMarco.executeUpdate();
            return true;
        } catch (Throwable ex) {
            System.err.println("delete macro failed." + ex);
            return false;
        }
    }

    /**
     * this method adds a macro*
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void createMacro(Macros macro) {
        try {
            sessionFactory.getCurrentSession().save(macro);
        } catch (Throwable ex) {
            System.err.println("create macro failed." + ex);

        }
    }

    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public boolean updateMacro(Macros macro) {
        try {
            sessionFactory.getCurrentSession().update(macro);
            return true;
        } catch (Throwable ex) {
            System.err.println("update macro failed." + ex);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void createProcessStatus(lu_ProcessStatus lu) {
        try {
            sessionFactory.getCurrentSession().save(lu);
        } catch (Throwable ex) {
            System.err.println("create ProcessStatus failed." + ex);
        }
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public lu_ProcessStatus getProcessStatusById(int id) throws Exception {
        try {
            return (lu_ProcessStatus) sessionFactory.getCurrentSession().get(lu_ProcessStatus.class, id);
        } catch (Throwable ex) {
            System.err.println("get ProcessStatus failed." + ex);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void updateProcessStatus(lu_ProcessStatus lu) {
        try {
            sessionFactory.getCurrentSession().update(lu);
        } catch (Throwable ex) {
            System.err.println("update ProcessStatus failed." + ex);
        }
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public LogoInfo getLogoInfo() {
        LogoInfo logoInfo = new LogoInfo();
        try {
            Query logos = sessionFactory.getCurrentSession().createQuery("from LogoInfo order by id desc ").setMaxResults(1);
            List<LogoInfo> li = logos.list();
            if (li.size() != 0) {
                logoInfo = li.get(0);
            }
        } catch (Throwable ex) {
            System.err.println("get LogoInfo failed." + ex);
        }
        return logoInfo;
    }

    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void updateLogoInfo(LogoInfo logoDetails) {
        try {
            sessionFactory.getCurrentSession().update(logoDetails);
        } catch (Throwable ex) {
            System.err.println("update LogoInfo failed." + ex);
        }
    }

    /**
     * The 'getHL7List' function will return the list of saved hl7 standard versions.
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public List<mainHL7Details> getHL7List() throws Exception {

        Query query = sessionFactory.getCurrentSession().createQuery("from mainHL7Details order by id desc");

        List<mainHL7Details> HL7List = query.list();
        return HL7List;
    }

    /**
     * The 'getHL7Details' function will the HL7 details for the passed in hl7 version.
     *
     * @Table HL7Specs
     *
     * @param	hl7Id This will hold the id to find
     *
     * @return	This function will return a HL7Details object
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public mainHL7Details getHL7Details(int hl7Id) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mainHL7Details.class);
        criteria.add(Restrictions.eq("id", hl7Id));

        if (criteria.uniqueResult() == null) {
            return null;
        } else {
            return (mainHL7Details) criteria.uniqueResult();
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
    @Transactional(readOnly = true, value = "iltransactionManager")
    public List<mainHL7Segments> getHL7Segments(int hl7Id) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mainHL7Segments.class);
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
    @Transactional(readOnly = true, value = "iltransactionManager")
    public List<mainHL7Elements> getHL7Elements(int hl7Id, int segmentId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mainHL7Elements.class);
        criteria.add(Restrictions.eq("hl7Id", hl7Id));
        criteria.add(Restrictions.eq("segmentId", segmentId));
        criteria.addOrder(Order.asc("displayPos"));

        return criteria.list();
    }

    /**
     * the 'updateHL7Details' funciton will update/save the details of the HL7 message
     *
     * @param details The Hl7 details object
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void updateHL7Details(mainHL7Details details) {
        sessionFactory.getCurrentSession().saveOrUpdate(details);
    }

    /**
     * The 'updateHL7Segments' function will update the segment passed to the function.
     *
     * @param segment The segment object to update
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void updateHL7Segments(mainHL7Segments segment) {
        sessionFactory.getCurrentSession().update(segment);
    }

    /**
     * The 'updateHL7Elements' function will update the segment element passed to the function.
     *
     * @param element The segment element object to update.
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void updateHL7Elements(mainHL7Elements element) {
        sessionFactory.getCurrentSession().update(element);
    }

    /**
     * The 'createHL7' function will save the new HL7 Segment
     *
     * @param HL7Details The object holding the new HL7 Object
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public int createHL7(mainHL7Details HL7Details) {
        Integer lastId;

        lastId = (Integer) sessionFactory.getCurrentSession().save(HL7Details);

        return lastId;
    }

    /**
     * The 'saveHL7Segment' function will save the new HL7 Segment
     *
     * @param newSegment The object holding the new HL7 Object
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public int saveHL7Segment(mainHL7Segments newSegment) {
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
    @Transactional(readOnly = false, value = "iltransactionManager")
    public int saveHL7Element(mainHL7Elements newElement) {
        Integer lastId;

        lastId = (Integer) sessionFactory.getCurrentSession().save(newElement);

        return lastId;
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public List<lu_ProcessStatus> getAllProcessStatus() throws Exception {

        Criteria statusList = sessionFactory.getCurrentSession().createCriteria(lu_ProcessStatus.class);
        statusList.add(Restrictions.eq("status", true));
        statusList.addOrder(Order.asc("category"));
        statusList.addOrder(Order.asc("displayText"));

        return statusList.list();

    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public List<lu_ProcessStatus> getAllHistoryFormProcessStatus() throws Exception {

        Criteria statusList = sessionFactory.getCurrentSession().createCriteria(lu_ProcessStatus.class);
        statusList.add(Restrictions.eq("status", true));
        statusList.add(Restrictions.in("id", new Integer[]{17, 31, 21, 14, 11, 9, 16, 20, 15, 25, 29, 8, 3, 23, 37, 33, 19, 12, 10}));
        statusList.addOrder(Order.asc("category"));
        statusList.addOrder(Order.asc("displayText"));

        return statusList.list();

    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public Long findTotalUsers() throws Exception {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalUsers from User");
        Long totalUsers = (Long) query.uniqueResult();
        return totalUsers;
    }
    
    
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
	public List<MoveFilesLog> getMoveFilesLog(Integer statusId) throws Exception {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MoveFilesLog.class);
		if (statusId != null) {
			criteria.add(Restrictions.eq("statusId",statusId));
		}
       
		List<MoveFilesLog> moveLogList = criteria.list();
        return moveLogList;
	}

    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void deleteMoveFilesLog(MoveFilesLog moveFileLog) throws Exception {
        Query deleteFields = sessionFactory.getCurrentSession().createQuery("delete from MoveFilesLog where id = :moveFilePathId");
        deleteFields.setParameter("moveFilePathId", moveFileLog.getId());
        deleteFields.executeUpdate();
	
	}
    
    

}
