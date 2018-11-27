package com.hel.ut.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.hel.ut.validator.NoHtml;

@Entity
@Table(name = "moveFilesLog")
public class MoveFilesLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "transportMethodId", nullable = true)
    private int transportMethodId;

    @Column(name = "transportId", nullable = true)
    private int transportId;

    @Column(name = "statusId", nullable = false)
    private int statusId;

    @Column(name = "method", nullable = false)
    private int method;

    @NoHtml
    @Column(name = "folderPath", nullable = true)
    private String folderPath;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "startDateTime", nullable = true)
    private Date startDateTime = new Date();

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "endDateTime", nullable = true)
    private Date endDateTime = new Date();

    @NoHtml
    @Column(name = "notes", nullable = true)
    private String notes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransportId() {
        return transportId;
    }

    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public int getTransportMethodId() {
        return transportMethodId;
    }

    public void setTransportMethodId(int transportMethodId) {
        this.transportMethodId = transportMethodId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
