/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
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
@Table(name = "CONFIGURATIONHL7ELEMENTVALUES")
public class HL7ElementComponents {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "ELEMENTID", nullable = false)
    private int elementId;

    @NoHtml
    @Column(name = "fieldDescriptor", nullable = true)
    private String fieldDescriptor = "";

    @NoHtml
    @Column(name = "fieldAppendText", nullable = true)
    private String fieldAppendText = "";

    @NoHtml
    @Column(name = "fieldValue", nullable = true)
    private String fieldValue = "";

    @Column(name = "displayPos", nullable = true)
    private int displayPos = 1;

    @NoHtml
    @Column(name = "defaultValue", nullable = true)
    private String defaultValue = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getelementId() {
        return elementId;
    }

    public void setelementId(int elementId) {
        this.elementId = elementId;
    }

    public String getfieldDescriptor() {
        return fieldDescriptor;
    }

    public void setfieldDescriptor(String fieldDescriptor) {
        this.fieldDescriptor = fieldDescriptor;
    }

    public String getFieldAppendText() {
        return fieldAppendText;
    }

    public void setFieldAppendText(String fieldAppendText) {
        this.fieldAppendText = fieldAppendText;
    }

    public String getfieldValue() {
        return fieldValue;
    }

    public void setfieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public int getdisplayPos() {
        return displayPos;
    }

    public void setdisplayPos(int displayPos) {
        this.displayPos = displayPos;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
