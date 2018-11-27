package com.hel.ut.dao;

import java.util.List;

import com.hel.ut.model.User;
import com.hel.ut.model.UserActivity;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.userLogin;

import org.springframework.stereotype.Repository;

@Repository
public interface userDAO {

    Integer createUser(User user);

    void updateUser(User user);

    User getUserById(int userId);

    List<User> getUsersByOrganization(int orgId);

    User getUserByUserName(String username);

    Long findTotalLogins(int orgId);

    void setLastLogin(String username);

    List<User> getOrganizationContact(int orgId, int mainContact);

    Integer getUserByIdentifier(String identifier);

    User getUserByResetCode(String resetCode);

    void insertUserLog(UserActivity userActivity);

    UserActivity getUAById(Integer uaId);

    List<User> getUserByTypeByOrganization(int orgId);

    List<User> getSendersForConfig(List<Integer> configIds);

    List<User> getOrgUsersForConfig(List<Integer> configIds);

    List<User> getUserConnectionListSending(Integer configId);

    List<User> getUserConnectionListReceiving(Integer configId);

    List<User> getAllUsers();

    void updateUserActivity(UserActivity userActivity);

    List<String> getUserRoles(User user) throws Exception;

    void updateUserOnly(User user) throws Exception;

    List<User> getUsersByStatuRolesAndOrg(boolean status, List<Integer> rolesToExclude, List<Integer> orgs, boolean include) throws Exception;

    List<Integer> getUserAllowedTargets(int userId, List<configurationConnectionSenders> connections) throws Exception;

    List<Integer> getUserAllowedMessageTypes(int userId, List<configurationConnectionSenders> connections) throws Exception;

    List<configurationConnectionSenders> configurationConnectionSendersByUserId(int userId) throws Exception;
    
    void loguserout(int userId) throws Exception;
    
    List<User> getUsersByOrganizationWithLogins(int orgId);
    
    List<userLogin> getUserLogins(int userId);
    
    List<User> getAllUsersByOrganization(int orgId);
    
    List<User> getSuccessEmailSendersForConfig(Integer targetConfigId);
    
    List<User> getSuccessEmailReceiversForConfig(Integer targetConfigId);
}
