package com.hel.ut.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONSCHEDULE")
public class configurationSchedules {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGID", nullable = false)
    private int configId;

    @Column(name = "TYPE", nullable = false)
    private int type = 5;

    @Column(name = "PROCESSINGTYPE", nullable = false)
    private int processingType = 0;

    @Column(name = "NEWFILECHECK", nullable = false)
    private int newfileCheck = 0;

    @Column(name = "PROCESSINGDAY", nullable = false)
    private int processingDay = 0;

    @Column(name = "PROCESSINGTIME", nullable = false)
    private int processingTime = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getconfigId() {
        return configId;
    }

    public void setconfigId(int configId) {
        this.configId = configId;
    }

    public int gettype() {
        return type;
    }

    public void settype(int type) {
        this.type = type;
    }

    public int getprocessingType() {
        return processingType;
    }

    public void setprocessingType(int processingType) {
        this.processingType = processingType;
    }

    public int getnewfileCheck() {
        return newfileCheck;
    }

    public void setnewfileCheck(int newfileCheck) {
        this.newfileCheck = newfileCheck;
    }

    public int getprocessingDay() {
        return processingDay;
    }

    public void setprocessingDay(int processingDay) {
        this.processingDay = processingDay;
    }

    public int getprocessingTime() {
        return processingTime;
    }

    public void setprocessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

}
