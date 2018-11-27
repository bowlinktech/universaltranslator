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
@Table(name = "CONFIGURATIONHL7DETAILS")
public class HL7Details {

    @Transient
    private List<HL7Segments> HL7Segments = null;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGID", nullable = false)
    private int configId;

    @NoHtml
    @Column(name = "fieldSeparator", nullable = false)
    private String fieldSeparator = "|";

    @NoHtml
    @Column(name = "componentSeparator", nullable = false)
    private String componentSeparator = "^";

    @NoHtml
    @Column(name = "EscapeChar", nullable = false)
    private String EscapeChar = "";

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

    public String getfieldSeparator() {
        return fieldSeparator;
    }

    public void setfieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public String getEscapeChar() {
        return EscapeChar;
    }

    public void setEscapeChar(String EscapeChar) {
        this.EscapeChar = EscapeChar;
    }

    public String getcomponentSeparator() {
        return componentSeparator;
    }

    public void setcomponentSeparator(String componentSeparator) {
        this.componentSeparator = componentSeparator;
    }

    public List<HL7Segments> getHL7Segments() {
        return HL7Segments;
    }

    public void setHL7Segments(List<HL7Segments> HL7Segments) {
        this.HL7Segments = HL7Segments;
    }

}
