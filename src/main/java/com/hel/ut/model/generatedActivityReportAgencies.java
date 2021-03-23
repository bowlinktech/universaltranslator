package com.hel.ut.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "generatedactivityreportagencies")
public class generatedActivityReportAgencies {
    
    @Transient
    private String orgName, helRegistrySchemaName;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;
    
    @Column(name = "orgId", nullable = true)
    private int orgId = 0;
    
    @Column(name = "reportId", nullable = true)
    private int reportId = 0;

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

    public int getReportId() {
	return reportId;
    }

    public void setReportId(int reportId) {
	this.reportId = reportId;
    }

    public String getOrgName() {
	return orgName;
    }

    public void setOrgName(String orgName) {
	this.orgName = orgName;
    }

    public String getHelRegistrySchemaName() {
	return helRegistrySchemaName;
    }

    public void setHelRegistrySchemaName(String helRegistrySchemaName) {
	this.helRegistrySchemaName = helRegistrySchemaName;
    }
    
}
