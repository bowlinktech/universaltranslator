package com.hel.ut.model.custom;

import java.util.Date;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * This is a custom object as it is not tied to any tables. 90% of our look up tables have the same format, we don't want to have a million objects that we probably don't need. *
 */
public class TableData {

    private int id;

    @NotEmpty
    private String displayText;

    private String description;

    private boolean custom = false;

    private boolean status = true;

    private String urlId;

    private Date dateCreated;
    private Date dateModified;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

}
