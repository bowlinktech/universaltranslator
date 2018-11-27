package com.hel.ut.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hel.ut.dao.organizationDAO;
import com.hel.ut.model.Organization;
import com.hel.ut.model.User;
import com.hel.ut.model.configuration;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.reference.fileSystem;
import java.util.ArrayList;
import org.hibernate.criterion.Order;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * The organizationDAOImpl class will implement the DAO access layer to handle queries for an organization
 *
 *
 * @author chadmccue
 *
 */
@Repository
public class organizationDAOImpl implements organizationDAO {

    @Autowired
    @Qualifier("ilsessionFactory")
    private SessionFactory sessionFactory;

    /**
     * The 'createOrganziation' function will create a new organization
     *
     * @Table	organizations
     *
     * @param	organization	Will hold the organization object from the form
     *
     * @return The function will return the id of the created organization
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public Integer createOrganization(Organization organization) {
        Integer lastId = null;

        lastId = (Integer) sessionFactory.getCurrentSession().save(organization);

        return lastId;
    }

    /**
     * The 'updateOrganization' function will update a selected organization
     *
     * @Table	organizations
     *
     * @param	organization	Will hold the organization object from the field
     *
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    public void updateOrganization(Organization organization) {
        sessionFactory.getCurrentSession().update(organization);
    }

    /**
     * The 'getOrganizationById' function will return an organization based on organization id passed in.
     *
     * @Table organizations
     *
     * @param	orgId	This will hold the organization id to find
     *
     * @return	This function will return a single organization object
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public Organization getOrganizationById(int orgId) {
        return (Organization) sessionFactory.
                getCurrentSession().
                get(Organization.class, orgId);
    }

    /**
     * The 'getOrganizationByName' function will return a single organization based on the name passed in.
     *
     * @Table	organizations
     *
     * @param	cleanURL	Will hold the 'clean' organization name from the url
     *
     * @return	This function will return a single organization object
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public List<Organization> getOrganizationByName(String cleanURL) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Organization.class);
        criteria.add(Restrictions.like("cleanURL", cleanURL));
        return criteria.list();
    }

    /**
     * The 'findTotalOrgs' function will return the total number of organizations in the system. This will be used for pagination when viewing the list of organizations
     *
     * @Table organizations
     *
     * @return This function will return the total organizations
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public Long findTotalOrgs() {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalOrgs from Organization where cleanURL is not ''");

        Long totalOrgs = (Long) query.uniqueResult();

        return totalOrgs;
    }

    /**
     * The 'getOrganizations' function will return a list of the organizations in the system
     *
     * @Table	organizations
     *
     * @param	page	This will hold the value of page being viewed (used for pagination) maxResults	This will hold the value of the maxium number of results we want to send back to the list page
     *
     * @Return	This function will return a list of organization objects
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public List<Organization> getOrganizations() {
	
        Query query = sessionFactory.getCurrentSession().createQuery("from Organization where cleanURL is not '' order by orgName asc");

        List<Organization> organizationList = query.list();

        return organizationList;
    }

    /**
     * The 'getLatestOrganizations' function will return a list of the latest organizations added to the system. This function will only return active organizations.
     *
     * @Table	organizations
     *
     * @param	maxResults	This will hold the value of the maximum number of results we want to send back to the page
     *
     * @Return	This function will return a list of organization objects
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public List<Organization> getLatestOrganizations(int maxResults) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Organization where status = 1 order by dateCreated desc");

        //Set the max results to display
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        List<Organization> organizationList = query.list();
        return organizationList;
    }

    /**
     * The 'getAllActiveOrganizations' function will return all the active organizations in the system. The function will sort by organization name
     *
     * @Table	Organizations
     *
     * @Return	This function will return a list of organization objects
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public List<Organization> getAllActiveOrganizations() {
        Query query = sessionFactory.getCurrentSession().createQuery("from Organization where cleanURL is not '' and status = 1 order by orgName asc");

        List<Organization> organizationList = query.list();
        return organizationList;
    }

    /**
     * The 'findTotalUsers' function will return the total number of system users set up for a specific organization.
     *
     * @Table	users
     *
     * @Param	orgId	This will hold the organization id we want to search on
     *
     * @Return	This function will return the total number of users for the organization
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public Long findTotalUsers(int orgId) {

        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalUsers from User where orgId = :orgId");
        query.setParameter("orgId", orgId);

        Long totalUsers = (Long) query.uniqueResult();

        return totalUsers;

    }

    /**
     * The 'findTotalConfigurations' function will return the total number of configurations set up for a specific organization.
     *
     * @Table	configurations
     *
     * @Param	orgId	This will hold the organization id we want to search on
     *
     * @Return	This function will return the total number of configurations for the organization
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public Long findTotalConfigurations(int orgId) {

        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalConfigs from configuration where orgId = :orgId");
        query.setParameter("orgId", orgId);

        Long totalConfigs = (Long) query.uniqueResult();

        return totalConfigs;

    }

    /**
     * The 'getOrganizationUsers' function will return the list of users for a specific organization.
     *
     * @Table	users
     *
     * @Param	orgId	This will hold the organization id to search on page	This will hold the current page to view maxResults	This will hold the total number of results to return back to the list page
     *
     * @Return	This function will return a list of user objects
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    @SuppressWarnings("unchecked")
    public List<User> getOrganizationUsers(int orgId) {

        Query query = sessionFactory.getCurrentSession().createQuery("from User where orgId = :orgId order by lastName asc, firstName asc");
        query.setParameter("orgId", orgId);

        return query.list();

    }

    /**
     * The 'deleteOrganization' function will remove the organization and all other entities associated to the organization. (Users, Providers, Brochures, Configurations, etc). When deleting users the function will also remove anything associated to the users (Logins, Access, etc).
     *
     * @Param	orgId	This will hold the organization that will be deleted
     *
     * @Return This function will not return any values.
     */
    @Override
    @Transactional(readOnly = false, value = "iltransactionManager")
    @SuppressWarnings({"unchecked", "empty-statement"})
    public void deleteOrganization(int orgId) {
        
        //Delete the logins for the users associated to the organization to be deleted.
        Query findUsers = sessionFactory.getCurrentSession().createQuery("from User where orgId = :orgId");
        findUsers.setParameter("orgId", orgId);

        List<User> users = findUsers.list();

        if (users.size() > 0) {
            try {
                Query deleteLogins = sessionFactory.getCurrentSession().createQuery("delete from userLogin where userId in (select id from User where orgId = :orgId");
                deleteLogins.setParameter("orgId", orgId);
                deleteLogins.executeUpdate();
            } catch (SQLGrammarException ex) {
                throw ex;
            }

            //Delete the user access entries for the users associated to the organization to be deleted.
            try {
                Query deleteuserFeatures = sessionFactory.getCurrentSession().createQuery("delete from userAccess where userId in (select id from User where orgId = :orgId");
                deleteuserFeatures.setParameter("orgId", orgId);
                deleteuserFeatures.executeUpdate();
            } catch (SQLGrammarException ex) {
                throw ex;
            }
        }

        //Delete all users associated to the organization
        try {
            Query deleteUser = sessionFactory.getCurrentSession().createQuery("delete from User where orgId = :orgId");
            deleteUser.setParameter("orgId", orgId);
            deleteUser.executeUpdate();
        } catch (SQLGrammarException ex) {
            throw ex;
        }

        //Get the organization cleanURL
        Organization orgDetails = getOrganizationById(orgId);

        //Delete the organization folder within bowlink
        try {
            fileSystem dir = new fileSystem();
            dir.deleteOrgDirectories(orgDetails.getcleanURL());

            //Delete the organization
            Query deleteOrg = sessionFactory.getCurrentSession().createQuery("delete from Organization where id = :orgId");
            deleteOrg.setParameter("orgId", orgId);
            deleteOrg.executeUpdate();
        } catch (SQLGrammarException ex) {
            throw ex;
        }

    }

    /**
     * The 'getAssociatedOrgs' function will return a list of organizations that are associated to the passed in orgId
     *
     * @param orgId The id of the organization to find associated orgs
     *
     * @return This function will return a list or organization objects
     */
    @Override
    @Transactional(readOnly = true, value = "iltransactionManager")
    public List<Organization> getAssociatedOrgs(int orgId) {

        /* Get a list of configurations for the passed in org */
        List<Integer> configs = new ArrayList<Integer>();

        Criteria configurations = sessionFactory.getCurrentSession().createCriteria(configuration.class);
        configurations.add(Restrictions.eq("orgId", orgId));
        List<configuration> orgConfigs = configurations.list();

        if (orgConfigs.isEmpty()) {
            configs.add(0);
        } else {
            for (configuration config : orgConfigs) {
                configs.add(config.getId());
            }
        }

        /* Find all connections set up for the returned configurations */
        List<Integer> targetOrgIds = new ArrayList<Integer>();

        Criteria connections = sessionFactory.getCurrentSession().createCriteria(configurationConnection.class);
        connections.add(Restrictions.or(
                Restrictions.in("sourceConfigId", configs),
                Restrictions.in("targetConfigId", configs)
        ));
        List<configurationConnection> orgConnections = connections.list();

        /* Find all organiations associated to the returend connections */
        if (orgConnections.isEmpty()) {
            targetOrgIds.add(0);
        } else {
            for (configurationConnection connection : orgConnections) {

                Criteria getSrcConfigDetails = sessionFactory.getCurrentSession().createCriteria(configuration.class);
                getSrcConfigDetails.add(Restrictions.eq("id", connection.getsourceConfigId()));

                configuration srcconfigDetails = (configuration) getSrcConfigDetails.uniqueResult();

                if (srcconfigDetails.getorgId() != orgId && !targetOrgIds.contains(srcconfigDetails.getorgId())) {
                    targetOrgIds.add(srcconfigDetails.getorgId());
                }

                Criteria getTgtConfigDetails = sessionFactory.getCurrentSession().createCriteria(configuration.class);
                getTgtConfigDetails.add(Restrictions.eq("id", connection.gettargetConfigId()));

                configuration TgtconfigDetails = (configuration) getTgtConfigDetails.uniqueResult();

                if (TgtconfigDetails.getorgId() != orgId && !targetOrgIds.contains(TgtconfigDetails.getorgId())) {
                    targetOrgIds.add(TgtconfigDetails.getorgId());
                }

            }
        }

        Criteria orgs = sessionFactory.getCurrentSession().createCriteria(Organization.class);
        orgs.add(Restrictions.eq("status", true));
        orgs.add(Restrictions.eq("publicOrg", true));
        orgs.add(Restrictions.in("id", targetOrgIds));
        orgs.addOrder(Order.asc("orgName"));

        return orgs.list();

    }

}
