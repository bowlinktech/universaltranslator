package com.hel.ut.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hel.ut.dao.WebServicesDAO;
import com.hel.ut.model.WSMessagesIn;
import com.hel.ut.model.wsMessagesOut;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The WebServicesDAOImpl class will implement the DAO access layer to handle updates for web services messages
 *
 *
 * @author gchan
 *
 */
@Repository
public class WebServicesDAOImpl implements WebServicesDAO {

    @Autowired
    @Qualifier("ilsessionFactory")
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public List<WSMessagesIn> getWSMessagesInList(Date fromDate, Date toDate,
            Integer fetchSize) throws Exception {

        Criteria findWSIn = sessionFactory.getCurrentSession().createCriteria(WSMessagesIn.class);

        if (!"".equals(fromDate)) {
            findWSIn.add(Restrictions.ge("dateCreated", fromDate));
        }

        if (!"".equals(toDate)) {
            findWSIn.add(Restrictions.lt("dateCreated", toDate));
        }

        findWSIn.addOrder(Order.desc("dateCreated"));

        if (fetchSize > 0) {
            findWSIn.setMaxResults(fetchSize);
        }
        return findWSIn.list();

    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public WSMessagesIn getWSMessagesIn(Integer wsId) throws Exception {
        Criteria findWSIn = sessionFactory.getCurrentSession().createCriteria(WSMessagesIn.class);
        findWSIn.add(Restrictions.eq("id", wsId));
        List<WSMessagesIn> wsList = findWSIn.list();
        if (wsList.size() == 1) {
            return wsList.get(0);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void saveWSMessagesOut(wsMessagesOut wsMessagesOut) throws Exception {
        sessionFactory.getCurrentSession().saveOrUpdate(wsMessagesOut);
    }

    /**
     * this method get a list of outbound web messages restricted by Date range
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public List<wsMessagesOut> getWSMessagesOutList(Date fromDate, Date toDate,
            Integer fetchSize) throws Exception {

        Criteria findWSOut = sessionFactory.getCurrentSession().createCriteria(wsMessagesOut.class);

        if (!"".equals(fromDate)) {
            findWSOut.add(Restrictions.ge("dateCreated", fromDate));
        }

        if (!"".equals(toDate)) {
            findWSOut.add(Restrictions.lt("dateCreated", toDate));
        }

        findWSOut.addOrder(Order.desc("dateCreated"));

        if (fetchSize > 0) {
            findWSOut.setMaxResults(fetchSize);
        }
        return findWSOut.list();
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public wsMessagesOut getWSMessagesOut(Integer wsId) throws Exception {
        Criteria findWSOut = sessionFactory.getCurrentSession().createCriteria(wsMessagesOut.class);
        findWSOut.add(Restrictions.eq("id", wsId));
        List<wsMessagesOut> wsList = findWSOut.list();
        if (wsList.size() == 1) {
            return wsList.get(0);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public List<wsMessagesOut> getWSMessagesOutByBatchId(Integer batchId)
            throws Exception {
        Criteria findWSOut = sessionFactory.getCurrentSession().createCriteria(wsMessagesOut.class);
        findWSOut.add(Restrictions.eq("batchDownloadId", batchId));
        List<wsMessagesOut> wsList = findWSOut.list();
        return wsList;
    }

    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public List<WSMessagesIn> getWSMessagesInByBatchId(Integer batchId)
            throws Exception {
        Criteria findWS = sessionFactory.getCurrentSession().createCriteria(WSMessagesIn.class);
        findWS.add(Restrictions.eq("batchUploadId", batchId));
        List<WSMessagesIn> wsList = findWS.list();
        return wsList;
    }

    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void saveWSMessagesIn(WSMessagesIn wsIn) throws Exception {
        sessionFactory.getCurrentSession().saveOrUpdate(wsIn);
    }

}
