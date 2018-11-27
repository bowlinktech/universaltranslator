package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Entity
@Table(name = "REL_TRANSPORTFTPDETAILS")
public class configurationFTPFields {

    @Transient
    private CommonsMultipartFile file;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "TRANSPORTID", nullable = false)
    private int transportId = 0;

    @Column(name = "IP", nullable = true)
    private String ip = null;

    @NoHtml
    @Column(name = "DIRECTORY", nullable = true)
    private String directory = null;

    @Column(name = "USERNAME", nullable = true)
    private String username = null;

    @Column(name = "PASSWORD", nullable = true)
    private String password = null;

    @Column(name = "METHOD", nullable = false)
    private int method = 1;

    @Column(name = "PORT", nullable = true)
    private int port = 0;

    @NoHtml
    @Column(name = "PROTOCOL", nullable = true)
    private String protocol = "FTP";

    @Column(name = "CERTIFICATION", nullable = true)
    private String certification = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int gettransportId() {
        return transportId;
    }

    public void settransportId(int transportId) {
        this.transportId = transportId;
    }

    public String getip() {
        return ip;
    }

    public void setip(String ip) {
        this.ip = ip;
    }

    public String getdirectory() {
        return directory;
    }

    public void setdirectory(String directory) {
        this.directory = directory;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getpassword() {
        return password;
    }

    public void setpassword(String password) {
        this.password = password;
    }

    public int getmethod() {
        return method;
    }

    public void setmethod(int method) {
        this.method = method;
    }

    public int getport() {
        return port;
    }

    public void setport(int port) {
        this.port = port;
    }

    public String getprotocol() {
        return protocol;
    }

    public void setprotocol(String protocol) {
        this.protocol = protocol;
    }

    public String getcertification() {
        return certification;
    }

    public void setcertification(String certification) {
        this.certification = certification;
    }

    public CommonsMultipartFile getfile() {
        return file;
    }

    public void setfile(CommonsMultipartFile file) {
        this.file = file;
    }

}
