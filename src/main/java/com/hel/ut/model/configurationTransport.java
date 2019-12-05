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
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Entity
@Table(name = "CONFIGURATIONTRANSPORTDETAILS")
public class configurationTransport {

    @Transient
    private List<configurationFormFields> fields = null;

    @Transient
    private List<configurationFTPFields> FTPfields = null;

    @Transient
    private List<configurationFileDropFields> fileDropFields = null;

    @Transient
    private List<configurationWebServiceFields> webServiceFields = null;
    
     @Transient
    private List<organizationDirectDetails> directMessageFields = null;

    @Transient
    private String delimChar = null;

    @Transient
    private boolean containsHeaderRow;

    @Transient
    private List<Integer> messageTypes = null;
    
    @Transient
    private int threshold = 100;

    @Transient
    private CommonsMultipartFile ccdTemplatefile = null, hl7PDFTemplatefile = null;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGID", nullable = false)
    private int configId;

    @Column(name = "TRANSPORTMETHODID", nullable = false)
    private int transportMethodId;

    @Column(name = "FILETYPE", nullable = true)
    private int fileType = 1;

    @Column(name = "FILEDELIMITER", nullable = true)
    private int fileDelimiter = 2;

    @Column(name = "STATUS", nullable = false)
    private boolean status = true;

    @NoHtml
    @Column(name = "TARGETFILENAME", nullable = true)
    private String targetFileName = null;

    @Column(name = "APPENDDATETIME", nullable = false)
    private boolean appendDateTime = false;

    @Column(name = "MAXFILESIZE", nullable = false)
    private int maxFileSize = 10;

    @Column(name = "CLEARRECORDS", nullable = false)
    private boolean clearRecords = true;

    @Column(name = "FILELOCATION", nullable = true)
    private String fileLocation = null;

    @Column(name = "AUTORELEASE", nullable = false)
    private boolean autoRelease = true;

    @Column(name = "ERRORHANDLING", nullable = false)
    private int errorHandling = 2;

    @Column(name = "MERGEBATCHES", nullable = false)
    private boolean mergeBatches = true;

    @Column(name = "COPIEDTRANSPORTID", nullable = false)
    private int copiedTransportId = 0;

    @Column(name = "massTranslation", nullable = false)
    private boolean massTranslation = true;

    @NoHtml
    @Column(name = "FILEEXT", nullable = false)
    private String fileExt = null;

    @Column(name = "encodingId", nullable = false)
    private int encodingId = 1;

    @Column(name = "ccdSampleTemplate", nullable = true)
    private String ccdSampleTemplate = null;

    @Column(name = "HL7PDFSampleTemplate", nullable = true)
    private String HL7PDFSampleTemplate = null;
    
    @Column(name = "ZIPPED", nullable = false)
    private boolean zipped = false;
    
    @Column(name = "zipType", nullable = true)
    private int zipType = 0;
    
    @Column(name = "restAPIURL", nullable = true)
    private String restAPIURL = null;
    
    @Column(name = "restAPIUsername", nullable = true)
    private String restAPIUsername = null;
    
    @Column(name = "restAPIPassword", nullable = true)
    private String restAPIPassword = null;
    
    @Column(name = "restAPIType", nullable = true)
    private int restAPIType = 1;
    
    @Column(name = "waitForResponse", nullable = false)
    private boolean waitForResponse = false;
    
    @Column(name = "restAPIFunctionId", nullable = true)
    private int restAPIFunctionId = 0;
    
    @Column(name = "jsonWrapperElement", nullable = true)
    private String jsonWrapperElement = "";
    
    @Column(name = "lineTerminator", nullable = true)
    private String lineTerminator = "\\n";
    
    @Column(name = "helRegistryConfigId", nullable = false)
    private Integer helRegistryConfigId = 0;
    
    @Column(name = "helSchemaName", nullable = false)
    private String helSchemaName = "";
    
    @Column(name = "helRegistryId", nullable = false)
    private Integer helRegistryId = 0;
    
    @Column(name = "dmConfigKeyword", nullable = true)
    private String dmConfigKeyword = "";
    
    @Column(name = "ergFileDownload", nullable = false)
    private boolean ergFileDownload = false;
    
    @Column(name = "populateInboundAuditReport", nullable = false)
    private boolean populateInboundAuditReport = false;
    
    
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

    public int gettransportMethodId() {
        return transportMethodId;
    }

    public void settransportMethodId(int transportMethodId) {
        this.transportMethodId = transportMethodId;
    }

    public int getfileType() {
        return fileType;
    }

    public void setfileType(int fileType) {
        this.fileType = fileType;
    }

    public int getfileDelimiter() {
        return fileDelimiter;
    }

    public void setfileDelimiter(int fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public List<configurationFormFields> getFields() {
        return fields;
    }

    public void setFields(List<configurationFormFields> fields) {
        this.fields = fields;
    }

    public List<configurationFTPFields> getFTPFields() {
        return FTPfields;
    }

    public void setFTPFields(List<configurationFTPFields> FTPFields) {
        this.FTPfields = FTPFields;
    }

    public boolean getstatus() {
        return status;
    }

    public void setstatus(boolean status) {
        this.status = status;
    }

    public String gettargetFileName() {
        return targetFileName;
    }

    public void settargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public boolean getappendDateTime() {
        return appendDateTime;
    }

    public void setappendDateTime(boolean appendDateTime) {
        this.appendDateTime = appendDateTime;
    }

    public int getmaxFileSize() {
        return maxFileSize;
    }

    public void setmaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public boolean getclearRecords() {
        return clearRecords;
    }

    public void setclearRecords(boolean clearRecords) {
        this.clearRecords = clearRecords;
    }

    public String getfileLocation() {
        return fileLocation;
    }

    public void setfileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public boolean getautoRelease() {
        return autoRelease;
    }

    public void setautoRelease(boolean autoRelease) {
        this.autoRelease = autoRelease;
    }

    public int geterrorHandling() {
        return errorHandling;
    }

    public void seterrorHandling(int errorHandling) {
        this.errorHandling = errorHandling;
    }

    public boolean getmergeBatches() {
        return mergeBatches;
    }

    public void setmergeBatches(boolean mergeBatches) {
        this.mergeBatches = mergeBatches;
    }

    public List<Integer> getmessageTypes() {
        return messageTypes;
    }

    public void setmessageTypes(List<Integer> messageTypes) {
        this.messageTypes = messageTypes;
    }

    public int getcopiedTransportId() {
        return copiedTransportId;
    }

    public void setcopiedTransportId(int copiedTransportId) {
        this.copiedTransportId = copiedTransportId;
    }

    public String getDelimChar() {
        return delimChar;
    }

    public void setDelimChar(String delimChar) {
        this.delimChar = delimChar;
    }

    public boolean getContainsHeaderRow() {
        return containsHeaderRow;
    }

    public void setContainsHeaderRow(boolean containsHeaderRow) {
        this.containsHeaderRow = containsHeaderRow;
    }

    public String getfileExt() {
        return fileExt;
    }

    public void setfileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public int getEncodingId() {
        return encodingId;
    }

    public void setEncodingId(int encodingId) {
        this.encodingId = encodingId;
    }

    public List<configurationFileDropFields> getFileDropFields() {
        return fileDropFields;
    }

    public void setFileDropFields(List<configurationFileDropFields> fileDropFields) {
        this.fileDropFields = fileDropFields;
    }

    public List<configurationWebServiceFields> getWebServiceFields() {
        return webServiceFields;
    }

    public void setWebServiceFields(
            List<configurationWebServiceFields> webServiceFields) {
        this.webServiceFields = webServiceFields;
    }

    public String getCcdSampleTemplate() {
        return ccdSampleTemplate;
    }

    public void setCcdSampleTemplate(String ccdSampleTemplate) {
        this.ccdSampleTemplate = ccdSampleTemplate;
    }

    public CommonsMultipartFile getCcdTemplatefile() {
        return ccdTemplatefile;
    }

    public void setCcdTemplatefile(CommonsMultipartFile ccdTemplatefile) {
        this.ccdTemplatefile = ccdTemplatefile;
    }

    public String getHL7PDFSampleTemplate() {
        return HL7PDFSampleTemplate;
    }

    public void setHL7PDFSampleTemplate(String HL7PDFSampleTemplate) {
        this.HL7PDFSampleTemplate = HL7PDFSampleTemplate;
    }

    public CommonsMultipartFile getHl7PDFTemplatefile() {
        return hl7PDFTemplatefile;
    }

    public void setHl7PDFTemplatefile(CommonsMultipartFile hl7PDFTemplatefile) {
        this.hl7PDFTemplatefile = hl7PDFTemplatefile;
    }

    public boolean isMassTranslation() {
        return massTranslation;
    }

    public void setMassTranslation(boolean massTranslation) {
        this.massTranslation = massTranslation;
    }

    public boolean isZipped() {
	return zipped;
    }

    public void setZipped(boolean zipped) {
	this.zipped = zipped;
    }

    public int getZipType() {
	return zipType;
    }

    public void setZipType(int zipType) {
	this.zipType = zipType;
    }

    public String getRestAPIURL() {
	return restAPIURL;
    }

    public void setRestAPIURL(String restAPIURL) {
	this.restAPIURL = restAPIURL;
    }

    public String getRestAPIUsername() {
	return restAPIUsername;
    }

    public void setRestAPIUsername(String restAPIUsername) {
	this.restAPIUsername = restAPIUsername;
    }

    public String getRestAPIPassword() {
	return restAPIPassword;
    }

    public void setRestAPIPassword(String restAPIPassword) {
	this.restAPIPassword = restAPIPassword;
    }

    public int getRestAPIType() {
	return restAPIType;
    }

    public void setRestAPIType(int restAPIType) {
	this.restAPIType = restAPIType;
    }

    public boolean isWaitForResponse() {
	return waitForResponse;
    }

    public void setWaitForResponse(boolean waitForResponse) {
	this.waitForResponse = waitForResponse;
    }

    public int getRestAPIFunctionId() {
	return restAPIFunctionId;
    }

    public void setRestAPIFunctionId(int restAPIFunctionId) {
	this.restAPIFunctionId = restAPIFunctionId;
    }

    public String getJsonWrapperElement() {
	return jsonWrapperElement;
    }

    public void setJsonWrapperElement(String jsonWrapperElement) {
	this.jsonWrapperElement = jsonWrapperElement;
    }

    public String getLineTerminator() {
	return lineTerminator;
    }

    public void setLineTerminator(String lineTerminator) {
	this.lineTerminator = lineTerminator;
    }

    public int getThreshold() {
	return threshold;
    }

    public void setThreshold(int threshold) {
	this.threshold = threshold;
    }

    public Integer getHelRegistryConfigId() {
	return helRegistryConfigId;
    }

    public void setHelRegistryConfigId(Integer helRegistryConfigId) {
	this.helRegistryConfigId = helRegistryConfigId;
    }

    public String getHelSchemaName() {
	return helSchemaName;
    }

    public void setHelSchemaName(String helSchemaName) {
	this.helSchemaName = helSchemaName;
    }

    public Integer getHelRegistryId() {
	return helRegistryId;
    }

    public void setHelRegistryId(Integer helRegistryId) {
	this.helRegistryId = helRegistryId;
    }

    public List<organizationDirectDetails> getDirectMessageFields() {
	return directMessageFields;
    }

    public void setDirectMessageFields(List<organizationDirectDetails> directMessageFields) {
	this.directMessageFields = directMessageFields;
    }

    public String getDmConfigKeyword() {
        return dmConfigKeyword;
    }

    public void setDmConfigKeyword(String dmConfigKeyword) {
        this.dmConfigKeyword = dmConfigKeyword;
    }

    public boolean isErgFileDownload() {
	return ergFileDownload;
    }

    public void setErgFileDownload(boolean ergFileDownload) {
	this.ergFileDownload = ergFileDownload;
    }

    public boolean isPopulateInboundAuditReport() {
	return populateInboundAuditReport;
    }

    public void setPopulateInboundAuditReport(boolean populateInboundAuditReport) {
	this.populateInboundAuditReport = populateInboundAuditReport;
    }
    
}
