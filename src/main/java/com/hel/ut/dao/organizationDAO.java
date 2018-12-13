package com.hel.ut.dao;

import java.util.List;

import com.hel.ut.model.Organization;
import com.hel.ut.model.utUser;
import org.springframework.stereotype.Repository;

@Repository
public interface organizationDAO {

    Integer createOrganization(Organization organization);

    void updateOrganization(Organization organization);

    Organization getOrganizationById(int orgId);

    List<Organization> getOrganizationByName(String cleanURL);

    List<Organization> getOrganizations();

    List<Organization> getLatestOrganizations(int maxResults);

    List<Organization> getAllActiveOrganizations();

    Long findTotalOrgs();

    Long findTotalUsers(int orgId);

    Long findTotalConfigurations(int orgId);

    List<utUser> getOrganizationUsers(int orgId);

    void deleteOrganization(int orgId);

    List<Organization> getAssociatedOrgs(int orgId);

}
