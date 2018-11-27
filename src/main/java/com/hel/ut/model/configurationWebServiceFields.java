package com.hel.ut.model;

import java.util.List;
import com.hel.ut.validator.NoHtml;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "rel_transportWebServiceDetails")
public class configurationWebServiceFields {

    @Transient
    private List<configurationWebServiceSenders> senderDomainList = null;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "TRANSPORTID", nullable = false)
    private int transportId = 0;

    @NoHtml
    @Column(name = "email", nullable = true)
    private String email = null;

    /**
     * 1 = toUT\n2 = fromUT *
     */
    @Column(name = "Method", nullable = true)
    private int method = 0;

    @Column(name = "tagPosition", nullable = true)
    private int tagPosition = 1;

    @NoHtml
    @Column(name = "tagName", nullable = true)
    private String tagName = null;

    @Column(name = "textInAttachment", nullable = true)
    private String textInAttachment = null;

    @Column(name = "mimeType", nullable = true)
    private String mimeType = "text/xml";

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

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTagPosition() {
        return tagPosition;
    }

    public void setTagPosition(int tagPosition) {
        this.tagPosition = tagPosition;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTextInAttachment() {
        return textInAttachment;
    }

    public void setTextInAttachment(String textInAttachment) {
        this.textInAttachment = textInAttachment;
    }

    public List<configurationWebServiceSenders> getSenderDomainList() {
        return senderDomainList;
    }

    public void setSenderDomainList(
            List<configurationWebServiceSenders> senderDomainList) {
        this.senderDomainList = senderDomainList;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
