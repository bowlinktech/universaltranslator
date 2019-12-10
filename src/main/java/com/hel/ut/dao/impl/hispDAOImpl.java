package com.hel.ut.dao.impl;

import com.hel.ut.dao.hispDAO;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.hel.ut.model.hisps;
import java.util.List;
import org.hibernate.Query;

/**
 * The userDAOImpl class will implement the DAO access layer to handle updates for organization system users
 *
 *
 * @author chadmccue
 *
 */
@Repository
public class hispDAOImpl implements hispDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    /**
     * The 'getAllActiveHisps' function will return all active hisps;
     *
     *
     * @return The function will return a list of hisp objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<hisps> getAllActiveHisps() {
	
        Query query = sessionFactory.getCurrentSession().createQuery("from hisps where status = 1 order by hispName asc");
	
	List<hisps> hisps = query.list();

        return hisps;

    }

    
    /**
     * The 'getHispById' function will return a single hisp object based on the hispId passed in.
     *
     * @param	hispId	This will be used to find the specifc hisp
     *
     * @return	The function will return a hisp object
     */
    @Override
    @Transactional(readOnly = true)
    public hisps getHispById(Integer hispId) throws Exception {
        return (hisps) sessionFactory.getCurrentSession().get(hisps.class, hispId);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveHisp(hisps hispDetails) throws Exception {
	sessionFactory.getCurrentSession().saveOrUpdate(hispDetails);
    }
}
