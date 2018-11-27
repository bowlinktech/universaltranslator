package com.hel.ut.model.custom;

import java.util.Date;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * This is a custom object that defines what a look up table is. A look up table has a name it has a certain number of columns (from this we can send it to a custom object since medical services, procedure look up tables have more information.
 *
 * *
 */
public class LookUpTable {

    //item belong to a table Table_Name
    @NotEmpty
    private String utTableName;

    /**
     * display table name *
     */
    @NotEmpty
    private String displayName;

    @NotEmpty
    private String urlId;

    private int columnNum;

    private String description;

    /**
     * this is good to have, # of items in that table *
     */
    private int rowNum;

    private Date dateCreated;

    public String getUtTableName() {
        return utTableName;
    }

    public void setUtTableName(String utTableName) {
        this.utTableName = utTableName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    public int getColumnNum() {
        return columnNum;
    }

    public void setColumnNum(int columnNum) {
        this.columnNum = columnNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

}
