package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "USERS")
public class utUser {


    @Transient
    private String orgName, password, roleType, lastLogInDate = "";

    @Transient
    private Date dateOrgWasCreated = null;

    @Transient
    private Integer orgType, timesloggedIn, totalTimeLoggedIn, totalLogins;

    @Transient
    private boolean connectionAssociated = false, sendSentEmail = false, sendReceivedEmail = false, uploadFiles = false, downloadFiles = false;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "STATUS", nullable = false)
    private boolean status = false;

    @Column(name = "ORGID", nullable = false)
    private int orgId;

    @NotEmpty
    @NoHtml
    @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;

    @NotEmpty
    @NoHtml
    @Column(name = "LASTNAME", nullable = true)
    private String lastName;

    /**
     * @NotEmpty @NoHtml @Column(name = "PASSWORD", nullable = false) private String password;
     *
     */
    @NotEmpty
    @NoHtml
    @Size(min = 4, max = 15)
    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "ROLEID", nullable = false)
    private int roleId = 2;

    @Column(name = "MAINCONTACT", nullable = false)
    private int mainContact = 0;

    @Column(name = "SENDEMAILALERT", nullable = true)
    private boolean sendEmailAlert = false;

    @Column(name = "RECEIVEEMAILALERT", nullable = true)
    private boolean receiveEmailAlert = false;

    @Email
    @NoHtml
    @Column(name = "EMAIL", nullable = false)
    private String email;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "USERTYPE", nullable = false)
    private int userType = 1;

    @Column(name = "DELIVERAUTHORITY", nullable = false)
    private boolean deliverAuthority = false;

    @Column(name = "EDITAUTHORITY", nullable = false)
    private boolean editAuthority = false;

    @Column(name = "CREATEAUTHORITY", nullable = false)
    private boolean createAuthority = false;

    @Column(name = "CANCELAUTHORITY", nullable = false)
    private boolean cancelAuthority = false;

    @NoHtml
    @Column(name = "RESETCODE", nullable = true)
    private String resetCode = null;

    @Column(name = "randomSalt", nullable = true)
    private byte[] randomSalt;

    @Column(name = "encryptedPw", nullable = true)
    private byte[] encryptedPw;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getMainContact() {
        return mainContact;
    }

    public void setMainContact(int mainContact) {
        this.mainContact = mainContact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getuserType() {
        return userType;
    }

    public void setuserType(int userType) {
        this.userType = userType;
    }

    public boolean getdeliverAuthority() {
        return deliverAuthority;
    }

    public void setdeliverAuthority(boolean deliverAuthority) {
        this.deliverAuthority = deliverAuthority;
    }

    public boolean geteditAuthority() {
        return editAuthority;
    }

    public void seteditAuthority(boolean editAuthority) {
        this.editAuthority = editAuthority;
    }

    public boolean getcreateAuthority() {
        return createAuthority;
    }

    public void setcreateAuthority(boolean createAuthority) {
        this.createAuthority = createAuthority;
    }

    public boolean getcancelAuthority() {
        return cancelAuthority;
    }

    public void setcancelAuthority(boolean cancelAuthority) {
        this.cancelAuthority = cancelAuthority;
    }

    public Date getdateOrgWasCreated() {
        return dateOrgWasCreated;
    }

    public void setdateOrgWasCreated(Date dateOrgWasCreated) {
        this.dateOrgWasCreated = dateOrgWasCreated;
    }

    public String getresetCode() {
        return resetCode;
    }

    public void setresetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public byte[] getRandomSalt() {
        return randomSalt;
    }

    public void setRandomSalt(byte[] randomSalt) {
        this.randomSalt = randomSalt;
    }

    public byte[] getEncryptedPw() {
        return encryptedPw;
    }

    public void setEncryptedPw(byte[] encryptedPw) {
        this.encryptedPw = encryptedPw;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    public boolean isConnectionAssociated() {
        return connectionAssociated;
    }

    public void setConnectionAssociated(boolean connectionAssociated) {
        this.connectionAssociated = connectionAssociated;
    }

    public boolean isSendSentEmail() {
        return sendSentEmail;
    }

    public void setSendSentEmail(boolean sendSentEmail) {
        this.sendSentEmail = sendSentEmail;
    }

    public boolean isSendReceivedEmail() {
        return sendReceivedEmail;
    }

    public void setSendReceivedEmail(boolean sendReceivedEmail) {
        this.sendReceivedEmail = sendReceivedEmail;
    }

    public Date getDateOrgWasCreated() {
        return dateOrgWasCreated;
    }

    public void setDateOrgWasCreated(Date dateOrgWasCreated) {
        this.dateOrgWasCreated = dateOrgWasCreated;
    }

    public boolean isSendEmailAlert() {
        return sendEmailAlert;
    }

    public void setSendEmailAlert(boolean sendEmailAlert) {
        this.sendEmailAlert = sendEmailAlert;
    }

    public boolean isReceiveEmailAlert() {
        return receiveEmailAlert;
    }

    public void setReceiveEmailAlert(boolean receiveEmailAlert) {
        this.receiveEmailAlert = receiveEmailAlert;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public boolean isDeliverAuthority() {
        return deliverAuthority;
    }

    public void setDeliverAuthority(boolean deliverAuthority) {
        this.deliverAuthority = deliverAuthority;
    }

    public boolean isEditAuthority() {
        return editAuthority;
    }

    public void setEditAuthority(boolean editAuthority) {
        this.editAuthority = editAuthority;
    }

    public boolean isCreateAuthority() {
        return createAuthority;
    }

    public void setCreateAuthority(boolean createAuthority) {
        this.createAuthority = createAuthority;
    }

    public boolean isCancelAuthority() {
        return cancelAuthority;
    }

    public void setCancelAuthority(boolean cancelAuthority) {
        this.cancelAuthority = cancelAuthority;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public boolean isUploadFiles() {
	return uploadFiles;
    }

    public void setUploadFiles(boolean uploadFiles) {
	this.uploadFiles = uploadFiles;
    }

    public boolean isDownloadFiles() {
	return downloadFiles;
    }

    public void setDownloadFiles(boolean downloadFiles) {
	this.downloadFiles = downloadFiles;
    }

    public Integer getTimesloggedIn() {
	return timesloggedIn;
    }

    public void setTimesloggedIn(Integer timesloggedIn) {
	this.timesloggedIn = timesloggedIn;
    }

    public Integer getTotalTimeLoggedIn() {
	return totalTimeLoggedIn;
    }

    public void setTotalTimeLoggedIn(Integer totalTimeLoggedIn) {
	this.totalTimeLoggedIn = totalTimeLoggedIn;
    }

    public Integer getTotalLogins() {
	return totalLogins;
    }

    public void setTotalLogins(Integer totalLogins) {
	this.totalLogins = totalLogins;
    }

    public String getRoleType() {
	return roleType;
    }

    public void setRoleType(String roleType) {
	this.roleType = roleType;
    }

    public String getLastLogInDate() {
	return lastLogInDate;
    }

    public void setLastLogInDate(String lastLogInDate) {
	this.lastLogInDate = lastLogInDate;
    }
    
}
