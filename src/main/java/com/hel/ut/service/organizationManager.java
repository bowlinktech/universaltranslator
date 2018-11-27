package com.hel.ut.service;

import java.util.List;

import com.hel.ut.model.Organization;
import com.hel.ut.model.User;

public interface organizationManager {

    Integer createOrganization(Organization organization);

    void updateOrganization(Organization organization) throws Exception;

    Organization getOrganizationById(int orgId);

    List<Organization> getOrganizationByName(String cleanURL);

    List<Organization> getOrganizations();

    List<Organization> getLatestOrganizations(int maxResults);

    List<Organization> getAllActiveOrganizations();

    Long findTotalOrgs();

    Long findTotalUsers(int orgId);

    Long findTotalConfigurations(int orgId);

    List<User> getOrganizationUsers(int orgId);

    void deleteOrganization(int orgId);

    List<Organization> getAssociatedOrgs(int orgId);

}
