/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONHL7ELEMENTS")
public class HL7Elements {

    @Transient
    private List<HL7ElementComponents> elementComponents = null;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "HL7ID", nullable = false)
    private int hl7Id;

    @Column(name = "segmentId", nullable = false)
    private int segmentId;

    @NoHtml
    @Column(name = "elementName", nullable = false)
    private String elementName = "";

    @NoHtml
    @Column(name = "defaultValue", nullable = true)
    private String defaultValue = "";

    @Column(name = "displayPos", nullable = true)
    private int displayPos = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int gethl7Id() {
        return hl7Id;
    }

    public void sethl7Id(int hl7Id) {
        this.hl7Id = hl7Id;
    }

    public int getsegmentId() {
        return segmentId;
    }

    public void setsegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public String getelementName() {
        return elementName;
    }

    public void setelementName(String elementName) {
        this.elementName = elementName;
    }

    public String getdefaultValue() {
        return defaultValue;
    }

    public void setdefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getdisplayPos() {
        return displayPos;
    }

    public void setdisplayPos(int displayPos) {
        this.displayPos = displayPos;
    }

    public List<HL7ElementComponents> getelementComponents() {
        return elementComponents;
    }

    public void setelementComponents(List<HL7ElementComponents> elementComponents) {
        this.elementComponents = elementComponents;
    }

}
