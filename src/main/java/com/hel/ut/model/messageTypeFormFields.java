package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "MESSAGETYPEFORMFIELDS")
public class messageTypeFormFields {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "MESSAGETYPEID", nullable = false)
    private int messageTypeId;

    @Column(name = "FIELDNO", nullable = false)
    private int fieldNo;

    @NoHtml
    @Column(name = "FIELDDESC", nullable = true)
    private String fieldDesc;

    @Column(name = "VALIDATIONTYPE", nullable = true)
    private int validationType = 1;

    @Column(name = "REQUIRED", nullable = false)
    private boolean required = false;

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

    public int getFieldNo() {
	return fieldNo;
    }

    public void setFieldNo(int fieldNo) {
	this.fieldNo = fieldNo;
    }

    public String getFieldDesc() {
	return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
	this.fieldDesc = fieldDesc;
    }

    public int getValidationType() {
	return validationType;
    }

    public void setValidationType(int validationType) {
	this.validationType = validationType;
    }

    public boolean isRequired() {
	return required;
    }

    public void setRequired(boolean required) {
	this.required = required;
    }

}
