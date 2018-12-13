package com.hel.ut.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import com.hel.ut.model.utUser;
import com.hel.ut.model.utUserActivity;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.utUserLogin;

public interface userManager {

    Integer createUser(utUser user);

    void updateUser(utUser user);

    utUser getUserById(int userId);

    List<utUser> getUsersByOrganization(int orgId);

    utUser getUserByUserName(String username);

    Long findTotalLogins(int userId);

    void setLastLogin(String username);

    List<utUser> getOrganizationContact(int orgId, int mainContact);

    Integer getUserByIdentifier(String identifier);

    utUser getUserByResetCode(String resetCode);

    void insertUserLog(utUserActivity userActivity);

    List<utUser> getUserByTypeByOrganization(int orgId);

    utUserActivity getUAById(Integer uaId);

    List<utUser> getSendersForConfig(List<Integer> configId);

    List<utUser> getOrgUsersForConfig(List<Integer> configId);

    List<utUser> getUserConnectionListSending(Integer configId);

    List<utUser> getUserConnectionListReceiving(Integer configId);

    List<utUser> getAllUsers();

    void updateUserActivity(utUserActivity userActivity);

    byte[] generateSalt() throws NoSuchAlgorithmException;

    byte[] getEncryptedPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException;

    boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException;

    utUser encryptPW(utUser user) throws Exception;

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
