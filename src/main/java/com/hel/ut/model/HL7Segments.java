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
@Table(name = "CONFIGURATIONHL7SEGMENTS")
public class HL7Segments {

    @Transient
    private List<HL7Elements> HL7Elements = null;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "HL7ID", nullable = false)
    private int hl7Id;

    @NoHtml
    @Column(name = "segmentName", nullable = false)
    private String segmentName = "";

    @Column(name = "displayPos", nullable = false)
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

    public String getsegmentName() {
        return segmentName;
    }

    public void setsegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public int getdisplayPos() {
        return displayPos;
    }

    public void setdisplayPos(int displayPos) {
        this.displayPos = displayPos;
    }

    public List<HL7Elements> getHL7Elements() {
        return HL7Elements;
    }

    public void setHL7Elements(List<HL7Elements> HL7Elements) {
        this.HL7Elements = HL7Elements;
    }

}
