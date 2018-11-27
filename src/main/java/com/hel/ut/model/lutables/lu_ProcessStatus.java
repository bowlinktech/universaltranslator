package com.hel.ut.model.lutables;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.hel.ut.validator.NoHtml;

@Entity
@Table(name = "lu_ProcessStatus")
public class lu_ProcessStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @NotEmpty
    @NoHtml
    @Column(name = "displayText", nullable = false)
    private String displayText;

    @NotEmpty
    @NoHtml
    @Column(name = "displayCode", nullable = false)
    private String displayCode;

    @NoHtml
    @Column(name = "description", nullable = true)
    private String description;

    @NotEmpty
    @NoHtml
    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "dateCreated", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "isCustom", nullable = true)
    private boolean custom = false;

    @Column(name = "status", nullable = true)
    private boolean status = true;

    @NoHtml
    @Column(name = "endUserDisplayText", nullable = false)
    private String endUserDisplayText;

    @NoHtml
    @Column(name = "endUserDisplayCode", nullable = false)
    private String endUserDisplayCode;

    @NoHtml
    @Column(name = "endUserDescription", nullable = true)
    private String endUserDescription;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayCode() {
        return displayCode;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getEndUserDisplayText() {
        return endUserDisplayText;
    }

    public void setEndUserDisplayText(String endUserDisplayText) {
        this.endUserDisplayText = endUserDisplayText;
    }

    public String getEndUserDisplayCode() {
        return endUserDisplayCode;
    }

    public void setEndUserDisplayCode(String endUserDisplayCode) {
        this.endUserDisplayCode = endUserDisplayCode;
    }

    public String getEndUserDescription() {
        return endUserDescription;
    }

    public void setEndUserDescription(String endUserDescription) {
        this.endUserDescription = endUserDescription;
    }

}
