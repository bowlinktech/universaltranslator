package com.hel.ut.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "REL_MESSAGETYPEDATATRANSLATIONS")
public class messageTypeDataTranslations {

    @Transient
    String fieldName = null;

    @Transient
    String crosswalkName = null;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "MESSAGETYPEID", nullable = false)
    private int messageTypeId;

    @Column(name = "FIELDID", nullable = false)
    private int fieldId;

    @Column(name = "CROSSWALKID", nullable = false)
    private int crosswalkId;

    @Column(name = "PROCESSORDER", nullable = false)
    private int processOrder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(int messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getCrosswalkId() {
        return crosswalkId;
    }

    public void setCrosswalkId(int crosswalkId) {
        this.crosswalkId = crosswalkId;
    }

    public int getProcessOrder() {
        return processOrder;
    }

    public void setProcessOrder(int processOrder) {
        this.processOrder = processOrder;
    }

    public void setfieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getfieldName() {
        return fieldName;
    }

    public void setcrosswalkName(String crosswalkName) {
        this.crosswalkName = crosswalkName;
    }

    public String getcrosswalkName() {
        return crosswalkName;
    }

}
