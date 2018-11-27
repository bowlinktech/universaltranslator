package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rel_configexceldetails")
public class configurationExcelDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "orgId", nullable = false)
    private int orgId = 0;

    @Column(name = "configId", nullable = false)
    private int configId = 0;

    @NoHtml
    @Column(name = "startRow", nullable = false)
    private int startRow = 0;

    @Column(name = "discardLastRows", nullable = false)
    private int discardLastRows = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getDiscardLastRows() {
        return discardLastRows;
    }

    public void setDiscardLastRows(int discardLastRows) {
        this.discardLastRows = discardLastRows;
    }

}
