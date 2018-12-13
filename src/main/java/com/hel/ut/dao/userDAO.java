package com.hel.ut.dao;

import java.util.List;

import com.hel.ut.model.utUser;
import com.hel.ut.model.utUserActivity;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.utUserLogin;

import org.springframework.stereotype.Repository;

@Repository
public interface userDAO {

    Integer createUser(utUser user);

    void updateUser(utUser user);

    utUser getUserById(int userId);

    List<utUser> getUsersByOrganization(int orgId);

    utUser getUserByUserName(String username);

    Long findTotalLogins(int orgId);

    void setLastLogin(String username);

    List<utUser> getOrganizationContact(int orgId, int mainContact);

    Integer getUserByIdentifier(String identifier);

    utUser getUserByResetCode(String resetCode);

    void insertUserLog(utUserActivity userActivity);

    utUserActivity getUAById(Integer uaId);

    List<utUser> getUserByTypeByOrganization(int orgId);

    List<utUser> getSendersForConfig(List<Integer> configIds);

    List<utUser> getOrgUsersForConfig(List<Integer> configIds);

    List<utUser> getUserConnectionListSending(Integer configId);

    List<utUser> getUserConnectionListReceiving(Integer configId);

    List<utUser> getAllUsers();

    void updateUserActivity(utUserActivity userActivity);

    List<String> getUserRoles(utUser user) throws Exception;

    void updateUserOnly(utUser user) throws Exception;

    List<utUser> getUsersByStatuRolesAndOrg(boolean status, List<Integer> rolesToExclude, List<Integer> orgs, boolean include) throws Exception;

    List<Integer> getUserAllowedTargets(int userId, List<configurationConnectionSenders> connections) throws Exception;

    List<Integer> getUserAllowedMessageTypes(int userId, List<configurationConnectionSenders> connections) throws Exception;

    List<configurationConnectionSenders> configurationConnectionSendersByUserId(int userId) throws Exception;
    
    void loguserout(int userId) throws Exception;
    
    List<utUser> getUsersByOrganizationWithLogins(int orgId);
    
    List<utUserLogin> getUserLogins(int userId);
    
    List<utUser> getAllUsersByOrganization(int orgId);
    
    List<utUser> getSuccessEmailSendersForConfig(Integer targetConfigId);
    
    List<utUser> getSuccessEmailReceiversForConfig(Integer targetConfigId);
}
