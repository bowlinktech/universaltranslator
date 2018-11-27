/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONMESSAGESPECS")
public class configurationMessageSpecs {

    @Transient
    private CommonsMultipartFile file = null, parsingScriptFile = null;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGID", nullable = false)
    private int configId;

    @NoHtml
    @Column(name = "TEMPLATEFILE", nullable = true)
    private String templateFile = null;

    @Column(name = "MESSAGETYPECOL", nullable = false)
    private int messageTypeCol = 0;

    @NoHtml
    @Column(name = "MESSAGETYPEVAL", nullable = true)
    private String messageTypeVal = null;

    @Column(name = "TARGETORGCOL", nullable = false)
    private int targetOrgCol = 0;

    @Column(name = "sourceSubOrgCol", nullable = false)
    private int sourceSubOrgCol = 0;

    @Column(name = "CONTAINSHEADERROW", nullable = false)
    private boolean containsHeaderRow = false;

    @Column(name = "RPTFIELD1", nullable = false)
    private int rptField1 = 0;

    @Column(name = "RPTFIELD2", nullable = false)
    private int rptField2 = 0;

    @Column(name = "RPTFIELD3", nullable = false)
    private int rptField3 = 0;

    @Column(name = "RPTFIELD4", nullable = false)
    private int rptField4 = 0;
    
    @Column(name = "EXCELSTARTROW", nullable = false)
    private int excelstartrow = 1;
    
    @Column(name = "EXCELSKIPROWS", nullable = false)
    private int excelskiprows = 0;
    
    @NoHtml
    @Column(name = "PARSINGTEMPLATE", nullable = true)
    private String parsingTemplate = null;
    
    @Column(name = "fileNameConfigHeader", nullable = false)
    private String fileNameConfigHeader;
    
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

    public String gettemplateFile() {
        return templateFile;
    }

    public void settemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public int getmessageTypeCol() {
        return messageTypeCol;
    }

    public void setmessageTypeCol(int messageTypeCol) {
        this.messageTypeCol = messageTypeCol;
    }

    public String getmessageTypeVal() {
        return messageTypeVal;
    }

    public void setmessageTypeVal(String messageTypeVal) {
        this.messageTypeVal = messageTypeVal;
    }

    public int gettargetOrgCol() {
        return targetOrgCol;
    }

    public void settargetOrgCol(int targetOrgCol) {
        this.targetOrgCol = targetOrgCol;
    }

    public boolean getcontainsHeaderRow() {
        return containsHeaderRow;
    }

    public void setcontainsHeaderRow(boolean containsHeaderRow) {
        this.containsHeaderRow = containsHeaderRow;
    }

    public int getrptField1() {
        return rptField1;
    }

    public void setrptField1(int rptField1) {
        this.rptField1 = rptField1;
    }

    public int getrptField2() {
        return rptField2;
    }

    public void setrptField2(int rptField2) {
        this.rptField2 = rptField2;
    }

    public int getrptField3() {
        return rptField3;
    }

    public void setrptField3(int rptField3) {
        this.rptField3 = rptField3;
    }

    public int getrptField4() {
        return rptField4;
    }

    public void setrptField4(int rptField4) {
        this.rptField4 = rptField4;
    }

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }

    public int getSourceSubOrgCol() {
        return sourceSubOrgCol;
    }

    public void setSourceSubOrgCol(int sourceSubOrgCol) {
        this.sourceSubOrgCol = sourceSubOrgCol;
    }

    public int getExcelstartrow() {
	return excelstartrow;
    }

    public void setExcelstartrow(int excelstartrow) {
	this.excelstartrow = excelstartrow;
    }

    public int getExcelskiprows() {
	return excelskiprows;
    }

    public void setExcelskiprows(int excelskiprows) {
	this.excelskiprows = excelskiprows;
    }

    public String getParsingTemplate() {
	return parsingTemplate;
    }

    public void setParsingTemplate(String parsingTemplate) {
	this.parsingTemplate = parsingTemplate;
    }

    public CommonsMultipartFile getParsingScriptFile() {
	return parsingScriptFile;
    }

    public void setParsingScriptFile(CommonsMultipartFile parsingScriptFile) {
	this.parsingScriptFile = parsingScriptFile;
    }

    public String getFileNameConfigHeader() {
	return fileNameConfigHeader;
    }

    public void setFileNameConfigHeader(String fileNameConfigHeader) {
	this.fileNameConfigHeader = fileNameConfigHeader;
    }
    
}
