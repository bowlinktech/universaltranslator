/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service.impl;

import com.hel.ut.model.activityReportList;
import com.hel.ut.dao.messageTypeDAO;
import com.hel.ut.dao.transactionInDAO;
import com.hel.ut.dao.transactionOutDAO;
import com.hel.ut.model.CrosswalkData;
import com.hel.ut.model.Macros;
import com.hel.ut.model.Organization;
import com.hel.ut.model.MoveFilesLog;
import com.hel.ut.model.RestAPIMessagesIn;
import com.hel.ut.model.Transaction;
import com.hel.ut.model.utUser;
import com.hel.ut.model.WSMessagesIn;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchRetry;
import com.hel.ut.model.batchUploadDroppedValues;
import com.hel.ut.model.batchUploads;
import com.hel.ut.model.batchuploadactivity;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.configurationDataTranslations;
import com.hel.ut.model.configurationExcelDetails;
import com.hel.ut.model.configurationFTPFields;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.configurationFileDropFields;
import com.hel.ut.model.configurationSchedules;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.fieldSelectOptions;
import com.hel.ut.model.mailMessage;
import com.hel.ut.model.custom.ConfigErrorInfo;
import com.hel.ut.model.custom.ConfigForInsert;
import com.hel.ut.model.custom.IdAndFieldValue;
import com.hel.ut.model.custom.batchErrorSummary;
import com.hel.ut.model.directmessagesin;
import com.hel.ut.model.referralActivityExports;
import com.hel.ut.model.systemSummary;
import com.hel.ut.reference.fileSystem;
import com.hel.ut.service.CCDtoTxt;
import com.hel.ut.service.JSONtoTxt;
import com.hel.ut.service.emailMessageManager;
import com.hel.ut.service.fileManager;
import com.hel.ut.service.hl7toTxt;
import com.hel.ut.service.messageTypeManager;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.sysAdminManager;
import com.hel.ut.service.transactionOutManager;
import com.hel.ut.service.userManager;
import com.hel.ut.service.utilManager;
import com.hel.ut.service.excelToTxt;
import com.hel.ut.service.xlsToTxt;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.zipFileManager;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Resource;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.registryKit.registry.fileUploads.fileUploadManager;
import com.registryKit.registry.fileUploads.uploadedFile;
import com.registryKit.registry.submittedMessages.submittedMessage;
import com.registryKit.registry.submittedMessages.submittedMessageManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author chadmccue
 */
@Service
public class transactionInManagerImpl implements transactionInManager {

    @Resource(name = "myProps")
    private Properties myProps;
    
    @Autowired
    private transactionInDAO transactionInDAO;

    @Autowired
    private messageTypeDAO messageTypeDAO;

    @Autowired
    private utConfigurationManager configurationManager;

    @Autowired
    private utConfigurationTransportManager configurationtransportmanager;

    @Autowired
    private messageTypeManager messagetypemanager;

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private transactionOutDAO transactionOutDAO;

    @Autowired
    private sysAdminManager sysAdminManager;

    @Autowired
    private userManager usermanager;

    @Autowired
    private fileManager filemanager;

    @Autowired
    private hl7toTxt hl7toTxt;

    @Autowired
    private CCDtoTxt ccdtotxt;

    @Autowired
    private excelToTxt exceltotxt;

    @Autowired
    private xlsToTxt xlstotxt;

    @Autowired
    private JSONtoTxt jsontotxt;

    @Autowired
    private emailMessageManager emailManager;

    @Autowired
    private utilManager utilmanager;

    @Autowired
    private zipFileManager zipFileManager;

    @Autowired
    private transactionOutManager transactionoutmanager;
    
    @Autowired
    private submittedMessageManager submittedmessagemanager;
    
    @Autowired
    fileUploadManager fileuploadmanager;
    
    @Autowired
    ThreadPoolTaskExecutor executor;

    private int processingSysErrorId = 5;

    //final status Ids
    private List<Integer> finalStatusIds = Arrays.asList(11, 12, 13, 16);

    //reject Ids
    private List<Integer> rejectIds = Arrays.asList(13, 14);
    

    private List<String> zipExtensions = Arrays.asList("gz", "zip");
    
    @Override
    public String getFieldValue(String tableName, String tableCol, String idCol, int idValue) {
	return transactionInDAO.getFieldValue(tableName, tableCol, idCol, idValue);
    }

    @Override
    public List<fieldSelectOptions> getFieldSelectOptions(int fieldId, int configId) {
	return transactionInDAO.getFieldSelectOptions(fieldId, configId);
    }

    @Override
    public Integer submitBatchUpload(batchUploads batchUpload) throws Exception {
	return transactionInDAO.submitBatchUpload(batchUpload);
    }

    @Override
    public void submitBatchUploadChanges(batchUploads batchUpload) throws Exception {
	transactionInDAO.submitBatchUploadChanges(batchUpload);
    }

    @Override
    public List<batchUploads> getsentBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception {
	return transactionInDAO.getsentBatches(userId, orgId, fromDate, toDate);
    }

    @Override
    public batchUploads getBatchDetails(int batchId) throws Exception {
	return transactionInDAO.getBatchDetails(batchId);
    }

    @Override
    public batchUploads getBatchDetailsByBatchName(String batchName) throws Exception {
	return transactionInDAO.getBatchDetailsByBatchName(batchName);
    }

    /**
     * The 'uploadAttachment' function will take in the file and orgName and upload the file to the appropriate file on the file system.
     *
     * @param fileUpload The file to be uploaded
     * @param orgName The organization name that is uploading the file. This will be the folder where to save the file to.
     */
    @Override
    public String uploadAttachment(MultipartFile fileUpload, String orgName) throws Exception {

	MultipartFile file = fileUpload;
	String fileName = file.getOriginalFilename();

	InputStream inputStream = null;
	OutputStream outputStream = null;

	try {
	    inputStream = file.getInputStream();
	    File newFile = null;

	    newFile = new File(myProps.getProperty("ut.directory.utRootDir") + orgName + "/attachments/" + fileName);

	    if (newFile.exists()) {
		int i = 1;
		while (newFile.exists()) {
		    int iDot = fileName.lastIndexOf(".");
		    newFile = new File(myProps.getProperty("ut.directory.utRootDir") + orgName + "/attachments/" + fileName.substring(0, iDot) + "_(" + ++i + ")" + fileName.substring(iDot));
		}
		fileName = newFile.getName();
	    } else {
		newFile.createNewFile();
	    }

	    outputStream = new FileOutputStream(newFile);
	    int read = 0;
	    byte[] bytes = new byte[1024];

	    while ((read = inputStream.read(bytes)) != -1) {
		outputStream.write(bytes, 0, read);
	    }
	    outputStream.close();

	    //Save the attachment
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return fileName;
    }

    @Override
    public List<ConfigForInsert> setConfigForInsert(int configId, int batchUploadId) {
	// we call sp and set the parameters here
	return transactionInDAO.setConfigForInsert(configId, batchUploadId);
    }

    /**
     * The 'getuploadedBatches' function calls getuploadedBatches(int userId, int orgId, Date fromDate, Date toDate, List<Integer> excludedStatusIds)
     *
     * It defaults excludedStatusIds to 1 as that is how the original fn is written. We wrote new method to pass in 1 as excludedStatusIds so we don't have to go back and modify every single method.
     *
     * @param userId
     * @param orgId
     * @param fromDate
     * @param todate
     *
     * It will return a list of batchUploads.
     */
    @Override
    public List<batchUploads> getuploadedBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception {
	return getuploadedBatches(userId, orgId, fromDate, toDate, Arrays.asList(1));
    }

    /**
     * The 'getuploadedBatches' function gets a list of batchUploads according to parameters being queried.
     *
     * @param userId
     * @param orgId
     * @param fromDate
     * @param todate
     * @param excludedStatusIds - statusIds for batches to exclude
     *
     * It will return a list of batchUploads.
     */
    @Override
    public List<batchUploads> getuploadedBatches(int userId, int orgId, Date fromDate, Date toDate, List<Integer> excludedStatusIds) throws Exception {
	return transactionInDAO.getuploadedBatches(userId, orgId, fromDate, toDate, excludedStatusIds);
    }

    /**
     * This
     */
    @Override
    public void processBatches() {
	//0. grab all batches with SSL (3) - Loaded or ready for Release SR (6)
	//1. get all batches with SSA
	try {
	    List<batchUploads> batches = getBatchesByStatusIds(Arrays.asList(3, 6, 36));
	    if (batches != null && batches.size() != 0) {
		//we loop and process
		for (batchUploads batch : batches) {
		    try {
			processBatch(batch.getId(), false);
		    } catch (Exception ex) {
			setBatchToError(batch.getId(), ("Errored at processBatches  " + ex.toString()));
			ex.printStackTrace();
		    }
		}
	    }
	} catch (Exception ex1) {
	    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ("processBatches error - " + ex1));
	}
    }
    
    

    @Override
    public void updateBatchStatus(Integer batchUploadId, Integer statusId, String timeField) throws Exception {

	batchUploads batchInfo = transactionInDAO.getBatchDetails(batchUploadId);

	/* Need to check to see if uploaded file exists in RR program uploads */
	//programImport existingProgramImport = importmanager.getProgramImportByAssignedName(batchInfo.getoriginalFileName().substring(0, batchInfo.getoriginalFileName().lastIndexOf('.')), 0);

	/*if (existingProgramImport != null) {
	    if (!existingProgramImport.getStatusId().equals(47)) {
		existingProgramImport.setStatusId(statusId);
		importmanager.updateImport(existingProgramImport);
	    }
	}*/
	transactionInDAO.updateBatchStatus(batchUploadId, statusId, timeField);
    }

    @Override
    public boolean setDoNotProcess(Integer batchId) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Integer clearTransactionTables(Integer batchUploadId, Integer configId) throws Exception {
	//clearBatchDLRetryByUploadBatch(batchUploadId);
	clearBatchRetry(batchUploadId);

	//we clear transactionTranslatedIn_batchUploadId
	Integer cleared = transactionInDAO.clearBatchTransactionTables(batchUploadId, configId);

	if (cleared > 0) {
	    flagAndEmailAdmin(batchUploadId);
	}
	return cleared;
    }

    @Override
    public void flagAndEmailAdmin(Integer batchUploadId) {
	// TODO Auto-generated method stub

    }

    @Override
    public List<configurationFormFields> getRequiredFieldsForConfig(Integer configId) {
	return configurationtransportmanager.getRequiredFieldsForConfig(configId);
    }

    @Override
    public Integer insertFailedRequiredFields(configurationFormFields cff, Integer batchUploadId) {
	return transactionInDAO.insertFailedRequiredFields(cff, batchUploadId);
    }

    /**
     * This method finds all error transactionInId in TransactionInErrors / TransactionOutErrors and update transactionIn with the appropriate error status It can be passed, reject and error
     *
     * @param batchId
     * @param statusId
     * @param foroutboundProcessing
     * @throws java.lang.Exception
     */
    @Override
    public void updateStatusForErrorTrans(Integer batchId, Integer statusId, boolean foroutboundProcessing) throws Exception {
	transactionInDAO.updateStatusForErrorTrans(batchId, statusId, foroutboundProcessing);
    }

    @Override
    public Integer runValidations(Integer batchUploadId, Integer configId) {
	
	Integer totalValidationErrors = 0;
	Integer errorCount = 0;
	
	List<configurationFormFields> configurationFormFields = configurationtransportmanager.getCffByValidationType(configId, 0);
	
	if(configurationFormFields != null) {
	    if(!configurationFormFields.isEmpty()) {
		String validation = "";
		Integer validationTypeId = 0; 
		
		for (configurationFormFields cff : configurationFormFields) {
		    errorCount = 0;
		    
		    validationTypeId = cff.getValidationType();
		    
		    try {
			switch (cff.getValidationType()) {
			    // no validation
			    case 1:
				break;
			    //email calling SQL to validation and insert - one statement
			    case 2:
				validation = "Email";
				errorCount = genericValidation(cff, validationTypeId, batchUploadId, "");
				break;
			    //phone  calling SP to validation and insert - one statement 
			    case 3:
				validation = "Phone Number";
				errorCount = genericValidation(cff, validationTypeId, batchUploadId, "");
				break;
			    // need to loop through each record / each field
			    case 4:
				validation = "Date";
				errorCount = genericValidation(cff, validationTypeId, batchUploadId, "");
				break;
			    //numeric   calling SQL to validation and insert - one statement      
			    case 5:
				validation = "Numeric";
				errorCount = genericValidation(cff, validationTypeId, batchUploadId, "");
				break;
			    //url - need to rethink as regExp is not validating correctly
			    case 6:
				validation = "URL";
				errorCount = genericValidation(cff, validationTypeId, batchUploadId, "");
				break;
			    //anything new we hope to only have to modify sp
			    default:
				validation = "";
				//errorCount = genericValidation(cff, validationTypeId, batchUploadId, "");
				break;
			}

			if(errorCount > 0) {
			    //Clear the value when we run into a validation error
			    insertValidationDroppedValues(batchUploadId, cff, false);
			    executePassClearLogicForValidationError(batchUploadId, cff, false);

			    totalValidationErrors = totalValidationErrors + errorCount;

			    //log batch activity
			    batchuploadactivity ba = new batchuploadactivity();
			    ba.setActivity("Validation Error. Validation Type:" + validation + " for configId:" + configId + " Field No: " + cff.getFieldNo());
			    ba.setBatchUploadId(batchUploadId);
			    transactionInDAO.submitBatchActivityLog(ba);
			}
		    }
		    catch (Exception ex) {
			ex.printStackTrace();
		    } 
		}
	    }
	}
	
	return totalValidationErrors;
    }

    @Override
    public Integer genericValidation(configurationFormFields cff, Integer validationTypeId, Integer batchUploadId, String regEx) {
	return transactionInDAO.genericValidation(cff, validationTypeId, batchUploadId, regEx);
    }

    @Override
    public List<Integer> getFeedbackReportConnection(int configId, int targetorgId) {
	return transactionInDAO.getFeedbackReportConnection(configId, targetorgId);
    }

    @Override
    public String formatDateForDB(Date date) {
	try {
	    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	    return dateformat.format(date);
	} catch (Exception e) {
	    return null;
	}
    }

    @Override
    public Integer processCrosswalk(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) {

	try {
	    Integer errors = 0;

	    //we null forcw column, we translate and insert there, we then replace
	    nullForCWCol(configId, batchId, foroutboundProcessing);

	    //we check to see if field value contains a list defined by UT delimiter
	    List<Integer> cwMultiList = checkCWFieldForList(configId, batchId, cdt, foroutboundProcessing);

	    if (cwMultiList.size() > 0) {
		List<CrosswalkData> cdList = getCrosswalkDataForBatch(cdt, batchId, foroutboundProcessing);
		// we loop through each field value in the list and apply cw
		errors = processMultiValueCWData(configId, batchId, cdt, cdList, foroutboundProcessing);
	    } 
	    else {
		executeCWDataForSingleFieldValue(configId, batchId, cdt, foroutboundProcessing);
		
		//flag errors, anything row that is not null in F[FieldNo] but null in forCW
		errors = flagCWErrors(configId, batchId, cdt, foroutboundProcessing, cdt.isRequiredField());
	
		//If field is required update transaction status if error is CW error is found
		if(cdt.isRequiredField() && errors > 0) {
		    //flag as error in transactionIn or transactionOut table (Only updating REQUIRED records from transactioninerrors)
		    updateStatusForErrorTrans(batchId, 14, foroutboundProcessing);
		}
		
		//If field is REQUIRED OR OPTIONAL and DTS says clear bad CW data then log dropped value.
		if(cdt.getPassClear() == 2) {
		    insertCWDroppedValues(configId, batchId, cdt, foroutboundProcessing);
		}
		
		//we replace original F[FieldNo] column with data in forcw, if clear then NULL will be set.
		updateFieldNoWithCWData(configId, batchId, cdt.getFieldNo(), cdt.getPassClear(), foroutboundProcessing);
	    }
	    return errors;
	} catch (Exception e) {
	    e.printStackTrace();
	    return 9999999;
	}

    }

    @Override
    public Integer processMacro(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) {

	// we clear forCW column for before we begin any translation
	nullForCWCol(configId, batchId, foroutboundProcessing);

	try {
	    Macros macro = configurationManager.getMacroById(cdt.getMacroId());
	    
	    if (macro != null) {
	    	cdt.setMacroName(macro.getmacroName());
	    } else {
	    	insertProcessingError(processingSysErrorId, configId, batchId, cdt.getFieldNo(),null, null, null,true, foroutboundProcessing, ("Macro " + cdt.getMacroId() + " doesn't exist in macro_names table"));
	    	return 0;
	    }
	    
	    int sysError = 0;
		
	    // we expect the target field back so we can figure out clear pass option
	    sysError = executeMacro(configId, batchId, cdt, foroutboundProcessing, macro);
	    
	    // insert macro errors
	    Integer macroErrors = flagMacroErrors(configId, batchId, cdt, foroutboundProcessing);
	    
	    if(cdt.getPassClear() == 2) {
	    	insertMacroDroppedValues(batchId, cdt, foroutboundProcessing);
	    	executePassClearLogic(batchId, cdt, foroutboundProcessing);
	    }

	    //flag as error in transactionIn or transactionOut table (Only updating REQUIRED records from transactioninerrors)
	    updateStatusForErrorTrans(batchId, 14, foroutboundProcessing);

	    return macroErrors;
	    
	} catch (Exception e) {
	   //e.printStackTrace();
	   return 9999999;
	}

    }

    @Override
    public void nullForCWCol(Integer configId, Integer batchId, boolean foroutboundProcessing) {
	transactionInDAO.nullForCWCol(configId, batchId, foroutboundProcessing);
    }

    @Override
    public void executeCWDataForSingleFieldValue(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) {
	transactionInDAO.executeCWDataForSingleFieldValue(configId, batchId, cdt, foroutboundProcessing);
    }

    @Override
    public void updateFieldNoWithCWData(Integer configId, Integer batchId, Integer fieldNo, Integer passClear, boolean foroutboundProcessing) {
	transactionInDAO.updateFieldNoWithCWData(configId, batchId, fieldNo, passClear, foroutboundProcessing);
    }

    @Override
    public Integer flagCWErrors(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing, boolean isFieldRequired) {
	return transactionInDAO.flagCWErrors(configId, batchId, cdt, foroutboundProcessing,isFieldRequired);
    }

    @Override
    public Integer flagMacroErrors(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) {
	return transactionInDAO.flagMacroErrors(configId, batchId, cdt, foroutboundProcessing);
    }

    @Override
    public Integer executeMacro(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing, Macros macro) {
	return transactionInDAO.executeMacro(configId, batchId, cdt, foroutboundProcessing, macro);
    }

    @Override
    public List<configurationTransport> getHandlingDetailsByBatch(int batchId) {
	try {
	    return transactionInDAO.getHandlingDetailsByBatch(batchId);
	} catch (Exception e) {
	    return null;
	}
    }

    @Override
    public void insertProcessingError(Integer errorId, Integer configId, Integer batchId, Integer fieldNo,
	    Integer macroId, Integer cwId, Integer validationTypeId, boolean required,
	    boolean foroutboundProcessing, String errorCause) {
	insertProcessingError(errorId, configId, batchId, fieldNo, macroId, cwId, validationTypeId, required, foroutboundProcessing, errorCause, null);

    }

    @Override
    public void insertProcessingError(Integer errorId, Integer configId, Integer batchId, Integer fieldNo,
	    Integer macroId, Integer cwId, Integer validationTypeId, boolean required,
	    boolean foroutboundProcessing, String errorCause, Integer transactionId) {
	transactionInDAO.insertProcessingError(errorId, configId, batchId, fieldNo, macroId, cwId, validationTypeId, required, foroutboundProcessing, errorCause, transactionId);

    }

    @Override
    public void updateRecordCounts(Integer batchId, List<Integer> statusIds,
	    boolean foroutboundProcessing, String colNameToUpdate) {
	transactionInDAO.updateRecordCounts(batchId, statusIds, foroutboundProcessing, colNameToUpdate);
    }

    @Override
    public Integer getRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing) {
	return transactionInDAO.getRecordCounts(batchId, statusIds, foroutboundProcessing, true);
    }
    
    @Override
    public Integer getRecordCounts(Integer batchId, List<Integer> statusIds, boolean foroutboundProcessing, boolean inStatusIds) {
	return transactionInDAO.getRecordCounts(batchId, statusIds, foroutboundProcessing, inStatusIds);
    }

    @Override
    public Integer insertLoadData(Integer batchId, Integer configId, String delimChar, String fileWithPath, String loadTableName, boolean containsHeaderRow, Integer totalHeaderRows, String lineTerminator) {
	return transactionInDAO.insertLoadData(batchId, configId, delimChar, fileWithPath, loadTableName, containsHeaderRow, totalHeaderRows, lineTerminator);
    }

    @Override
    public Integer updateConfigIdForBatch(Integer batchId, Integer configId) {
	return transactionInDAO.updateConfigIdForBatch(batchId, configId);
    }

    @Override
    public Integer loadTransactionTranslatedIn(Integer batchId, Integer configId) {
	return transactionInDAO.loadTransactionTranslatedIn(batchId, configId);
    }

    @Override
    public List<configurationConnection> getBatchTargets(Integer batchId, boolean active) {
	return transactionInDAO.getBatchTargets(batchId, active);
    }

    @Override
    public List<batchUploads> getBatchesByStatusIds(List<Integer> statusIds) {
	return transactionInDAO.getBatchesByStatusIds(statusIds);
    }

    @Override
    public void setBatchToError(Integer batchId, String errorMessage) throws Exception {
	try {
	    //TODO send email here
	    insertProcessingError(processingSysErrorId, null, batchId, null, null, null, null, false, false, errorMessage);
	    updateBatchStatus(batchId, 29, "endDateTime");
	} catch (Exception ex1) {
	    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ("loadBatch error at updating batch status - " + ex1));

	}

    }

    @Override
    public Integer rejectInvalidTargetOrg(Integer batchId, configurationConnection batchTargets) {
	return transactionInDAO.rejectInvalidTargetOrg(batchId, batchTargets);
    }

    @Override
    public Integer rejectNoConnections(batchUploads batch) {
	return transactionInDAO.rejectNoConnections(batch);
    }

    @Override
    public List<batchUploads> getAllUploadedBatches(Date fromDate, Date toDate) throws Exception {
	return transactionInDAO.getAllUploadedBatches(fromDate, toDate, 0, "");
    }

    @Override
    public List<batchUploads> getAllUploadedBatches(Date fromDate, Date toDate, Integer fetchSize, String batchName) throws Exception {
	return transactionInDAO.getAllUploadedBatches(fromDate, toDate, fetchSize, batchName);
    }

    @Override
    public List<batchUploads> getAllRejectedBatches(Date fromDate, Date toDate, Integer fetchSize) throws Exception {
	return transactionInDAO.getAllRejectedBatches(fromDate, toDate, fetchSize);
    }

    @Override
    public boolean searchTransactions(Transaction transaction, String searchTerm) throws Exception {

	boolean matchFound = false;

	if (transaction.getmessageTypeName().toLowerCase().matches(".*" + searchTerm + ".*")) {
	    matchFound = true;
	}

	if (transaction.getreportableField1() != null && transaction.getreportableField1().toLowerCase().matches(".*" + searchTerm + ".*")) {
	    matchFound = true;
	}

	if (transaction.getreportableField2() != null && transaction.getreportableField2().toLowerCase().matches(".*" + searchTerm + ".*")) {
	    matchFound = true;
	}

	if (transaction.getreportableField3() != null && transaction.getreportableField3().toLowerCase().matches(".*" + searchTerm + ".*")) {
	    matchFound = true;
	}

	if (transaction.getreportableField4() != null && transaction.getreportableField4().toLowerCase().matches(".*" + searchTerm + ".*")) {
	    matchFound = true;
	}

	if (transaction.getstatusValue().toLowerCase().matches(".*" + searchTerm + ".*")) {
	    matchFound = true;
	}

	if (transaction.gettargetOrgFields().size() > 0) {

	    for (int i = 0; i < transaction.gettargetOrgFields().size(); i++) {
		if (transaction.gettargetOrgFields().get(i).getFieldValue() != null && transaction.gettargetOrgFields().get(i).getFieldValue().toLowerCase().matches(".*" + searchTerm + ".*")) {
		    matchFound = true;
		}
	    }

	}

	return matchFound;

    }

    @Override
    public systemSummary generateSystemInboundSummary() {

	systemSummary systemSummary = new systemSummary();

	try {

	    /* Get batches submitted this hour */
	    Calendar thishour = new GregorianCalendar();
	    thishour.set(Calendar.MINUTE, 0);
	    thishour.set(Calendar.SECOND, 0);
	    thishour.set(Calendar.MILLISECOND, 0);

	    Calendar nexthour = new GregorianCalendar();
	    nexthour.set(Calendar.MINUTE, 0);
	    nexthour.set(Calendar.SECOND, 0);
	    nexthour.set(Calendar.MILLISECOND, 0);
	    nexthour.add(Calendar.HOUR_OF_DAY, 1);

	    //System.out.println("This Hour: " + thishour.getTime() + " Next Hour: " + nexthour.getTime());
	    Integer batchesThisHour = transactionInDAO.getAllUploadedBatches(thishour.getTime(), nexthour.getTime()).size();

	    /* Get batches submitted today */
	    Calendar starttoday = new GregorianCalendar();
	    starttoday.set(Calendar.HOUR_OF_DAY, 0);
	    starttoday.set(Calendar.MINUTE, 0);
	    starttoday.set(Calendar.SECOND, 0);
	    starttoday.set(Calendar.MILLISECOND, 0);

	    Calendar starttomorrow = new GregorianCalendar();
	    starttomorrow.set(Calendar.HOUR_OF_DAY, 0);
	    starttomorrow.set(Calendar.MINUTE, 0);
	    starttomorrow.set(Calendar.SECOND, 0);
	    starttomorrow.set(Calendar.MILLISECOND, 0);
	    starttomorrow.add(Calendar.DAY_OF_MONTH, 1);

	    //System.out.println("Today: " + starttoday.getTime() + " Tomorrow: " + starttomorrow.getTime());
	    Integer batchesToday = transactionInDAO.getAllUploadedBatches(starttoday.getTime(), starttomorrow.getTime()).size();

	    /* Get batches submitted this week */
	    Calendar thisweek = new GregorianCalendar();
	    thisweek.set(Calendar.HOUR_OF_DAY, 0);
	    thisweek.set(Calendar.MINUTE, 0);
	    thisweek.set(Calendar.SECOND, 0);
	    thisweek.set(Calendar.MILLISECOND, 0);
	    thisweek.set(Calendar.DAY_OF_WEEK, thisweek.getFirstDayOfWeek());

	    Calendar nextweek = new GregorianCalendar();
	    nextweek.set(Calendar.HOUR_OF_DAY, 0);
	    nextweek.set(Calendar.MINUTE, 0);
	    nextweek.set(Calendar.SECOND, 0);
	    nextweek.set(Calendar.MILLISECOND, 0);
	    nextweek.set(Calendar.DAY_OF_WEEK, thisweek.getFirstDayOfWeek());
	    nextweek.add(Calendar.WEEK_OF_YEAR, 1);

	    //System.out.println("This Week: " + thisweek.getTime() + " Next Week: " + nextweek.getTime());
	    Integer batchesThisWeek = transactionInDAO.getAllUploadedBatches(thisweek.getTime(), nextweek.getTime()).size();

	    systemSummary.setBatchesPastHour(batchesThisHour);
	    systemSummary.setBatchesToday(batchesToday);
	    systemSummary.setBatchesThisWeek(batchesThisWeek);

	    /* Get batches submitted yesterday */
	} catch (Exception ex) {
	    Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
	}

	return systemSummary;

    }

    @Override
    public boolean checkPermissionForBatch(utUser userInfo, batchUploads batchInfo) {
	return transactionInDAO.checkPermissionForBatch(userInfo, batchInfo);
    }

    @Override
    public ConfigErrorInfo getHeaderForConfigErrorInfo(Integer batchId, ConfigErrorInfo configErrorInfo) {
	//we create header string
	List<Integer> rptFieldArray = Arrays.asList(configErrorInfo.getRptField1(), configErrorInfo.getRptField2(), configErrorInfo.getRptField3(), configErrorInfo.getRptField4());
	return transactionInDAO.getHeaderForConfigErrorInfo(batchId, configErrorInfo, rptFieldArray);
    }

    @Override
    public boolean hasPermissionForBatch(batchUploads batchInfo, utUser userInfo, boolean hasConfigurations) {
	boolean hasPermission = false;
	/**
	 * user can view audit report if 1. uploaded by user 2. file type uploaded was for multiple types and user has configurations 3. user is in connection sender list for batch's configId
	 */
	try {
	    if (batchInfo.getUserId() == userInfo.getId()) {
		hasPermission = true;
	    } else if (batchInfo.getConfigId() == 0 && hasConfigurations) {
		hasPermission = true;
	    } else if (checkPermissionForBatch(userInfo, batchInfo)) {
		hasPermission = true;
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("checkPermissionForBatch " + ex.toString());
	}
	return hasPermission;
    }

    @Override
    public boolean allowBatchClear(Integer batchUploadId) {
	return transactionInDAO.allowBatchClear(batchUploadId);
    }

    @Override
    public List<batchuploadactivity> getBatchActivities(batchUploads batchInfo) {
	return transactionInDAO.getBatchActivities(batchInfo);
    }

    @Override
    public Integer insertSFTPRun(MoveFilesLog sftpJob) {
	return transactionInDAO.insertSFTPRun(sftpJob);
    }

    @Override
    public void updateSFTPRun(MoveFilesLog sftpJob) throws Exception {
	transactionInDAO.updateSFTPRun(sftpJob);
    }

    @Override
    public List<batchUploads> getsentBatchesHistory(int userId, int orgId, int toOrgId, int messageTypeId, Date fromDate, Date toDate) throws Exception {
	return transactionInDAO.getsentBatchesHistory(userId, orgId, toOrgId, messageTypeId, fromDate, toDate);
    }

    /**
     * The sftp move files will grab all unique active SFTP pull paths and check folders for file. It will check path to see how many configurations it is associated with. It will also get the delimiter, check if there is a headerRow, how the file is being release. *
     */
    @Override
    public Integer moveSFTPFiles() {
	Integer sysErrors = 0;
	try {
	    
	    //1 . get distinct ftp paths
	    List<configurationFTPFields> sftpPaths = getFTPInfoForJob(1);

	    //2 we clean up
	    //Find move files log by status Id and methodId
	    //Delete the records found
	    List<MoveFilesLog> existingSFTPMoveFileLogs = transactionInDAO.existingMoveFileLogs(2,3);
	    
	    if(existingSFTPMoveFileLogs != null) {
		if(!existingSFTPMoveFileLogs.isEmpty()) {
		    existingSFTPMoveFileLogs.forEach(moveLog -> {
			try {
			    transactionInDAO.deleteMoveFileLogById(moveLog.getId());
			} catch (Exception ex) {
			    Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
			}
		    });
		}
	    }

	    //get home folder of sFTP
	    String sftpHome = myProps.getProperty("ut.directory.sftpHome");

	    //loop ftp paths and check
	    if(sftpPaths != null) {
		if(!sftpPaths.isEmpty()) {
		    for (configurationFTPFields ftpPath : sftpPaths) {
			sysErrors = 0;

			//we insert job so if anything goes wrong or the scheduler overlaps, we won't be checking the same folder over and over
			MoveFilesLog sftpJob = new MoveFilesLog();
			sftpJob.setStatusId(1);
			sftpJob.setFolderPath(ftpPath.getdirectory());
			sftpJob.setTransportMethodId(3);
			sftpJob.setMethod(1);
			
			Integer lastId = insertSFTPRun(sftpJob);
			sftpJob.setId(lastId);

			// check if directory exists
			fileSystem fileSystem = new fileSystem();
			String inPath = sftpHome + ftpPath.getdirectory();
			File f = new File(inPath);
			if (!f.exists()) {
			    sftpJob.setNotes(("Directory " + inPath + " does not exist"));
			    sendEmailToAdmin("Check failed for " + inPath + " existence", "SFTP Job Error");
			    updateSFTPRun(sftpJob);
			    sysErrors = 1;
			}

			if (sysErrors == 0) {
			    //we look up org for this path
			    Integer orgId = configurationtransportmanager.getOrgIdForFTPPath(ftpPath);
			    sysErrors = sysErrors + moveFilesByPath(sftpHome, ftpPath.getdirectory(), 3, orgId, ftpPath.gettransportId());
			}
			if (sysErrors == 0) {
			    sftpJob.setStatusId(2);
			    sftpJob.setEndDateTime(new Date());
			    updateSFTPRun(sftpJob);
			}
		    }
		}
	    }
	    
	// if there are no errors, we release the folder path
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("moveSFTPFilesJob " + ex.toString());
	    try {
		sendEmailToAdmin((ex.toString() + "<br/>" + Arrays.toString(ex.getStackTrace())), "SFTP Job Error - main method errored");
	    } catch (Exception ex1) {
		System.err.println("moveSFTPFilesJob " + Arrays.toString(ex1.getStackTrace()));
	    }
	    return 1;
	}
	return sysErrors;
    }
    
    /**
     * The 'moveFilesPath' method will handle moving files found in the UT dropped configuration folders.
     * 
     * @param rootPath
     * @param configDroppedPath
     * @param transportMethodId
     * @param orgId
     * @param transportId
     * @return 
     */
    @Override
    public Integer moveFilesByPath(String rootPath, String configDroppedPath, Integer transportMethodId, Integer orgId, Integer transportId) {
	
	Integer sysErrors = 0;

	try {
	    
	    Organization orgDetails = organizationmanager.getOrganizationById(orgId);
	    
	    fileSystem fileSystem = new fileSystem();
	    String fileInPath = rootPath + configDroppedPath;
	    File folder = new File(fileInPath);

	    //Retrieve all files in the folder (we only list visible files)
	    File[] listOfFiles = folder.listFiles((FileFilter) HiddenFileFilter.VISIBLE);
	    Arrays.sort(listOfFiles, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
	    
	   //too many variables that could come into play regarding file types, will check files with one method
	   if(listOfFiles != null) {
	       
		if(listOfFiles.length > 0) {
		    
		    //loop through all found files
		    for (File file : listOfFiles) {
			String fileName = file.getName();
			
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
			Date date = new Date();

			String batchName = new StringBuilder().append(transportMethodId).append(orgId).append(dateFormat.format(date)).toString();
			
			if (!fileName.endsWith("_error") && !fileName.endsWith(".filepart") && file.isFile()) {

			    try {

				String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);

				Integer statusId = 4;
				Integer errorId = 0;

				if (zipExtensions.contains(fileExt)) {
				    String zippedFile = rootPath + configDroppedPath + "/" + fileName;
				    File unzippedFile = zipFileManager.unzipFile(folder, zippedFile.replace("//", "/"), zippedFile.replace("//", "/"), fileName, 0, fileExt);

				    if (unzippedFile == null) {
					//Unzip failed, submit error
					statusId = 7;
					errorId = 40;
				    } else {
					//Delete original zipped file
					file.delete();
					file = unzippedFile;
					fileName = file.getName();
					fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
				    }
				}
				
				//Check to see if the batch already exists (RESET)
				batchUploads batchDetails = transactionInDAO.getBatchDetailsByOriginalFileName(fileName);
				
				Integer batchId = 0;
				boolean newBatchRecord = true;
				
				if(batchDetails != null) {
				    
				    if(batchDetails.getStatusId() == 35) {
					newBatchRecord = false;
					
					batchId = batchDetails.getId();
					
					//Delete old archive file
					if(!"".equals(batchDetails.getUtBatchName())) {
					    File oldArchiveFile = new File(rootPath + orgDetails.getcleanURL() + "/input files/encoded_"+batchDetails.getUtBatchName() + fileName.substring(fileName.lastIndexOf(".")));
					    
					    if(oldArchiveFile.exists()) {
						oldArchiveFile.delete();
						
						//log user activity
						batchuploadactivity ba = new batchuploadactivity();
						ba.setActivity("Old archive file (encoded_" +batchDetails.getUtBatchName() + fileName.substring(fileName.lastIndexOf(".")) + ") was removed.");
						ba.setBatchUploadId(batchId);
						transactionInDAO.submitBatchActivityLog(ba);
					    }
					}
					
					//log user activity
					batchuploadactivity ba = new batchuploadactivity();
					ba.setActivity("New utBatchName: " + batchName + " was set for reset batchId: "+batchDetails.getId());
					ba.setBatchUploadId(batchId);
					transactionInDAO.submitBatchActivityLog(ba);
					
					batchDetails.setUtBatchName(batchName);
					transactionInDAO.submitBatchUploadChanges(batchDetails);
					
				    }
				}
				if(newBatchRecord) {
				    
				    batchUploads batchInfo = new batchUploads();
				    batchInfo.setOrgId(orgId);
				    batchInfo.setTransportMethodId(transportMethodId);
				    batchInfo.setStatusId(4);
				    //batchInfo.setStartDateTime(date);
				    batchInfo.setUtBatchName(batchName);
				    batchInfo.setOriginalFolder(rootPath + configDroppedPath);
				    batchInfo.setUserId(0);
				    batchInfo.setConfigId(0);
				    batchInfo.setOriginalFileName(fileName);
				    batchInfo.setFileLocation(rootPath + configDroppedPath);
				    
				    if(fileName.contains("|")) {
					String[] fileNameArray = fileName.split("\\|");
					if(fileNameArray.length == 3) {
					    Integer assocatedBatchUploadId = Integer.parseInt(fileNameArray[1]);
					    
					    if(assocatedBatchUploadId > 0) {
						batchInfo.setAssociatedBatchId(assocatedBatchUploadId);
					    }
					}
				    }
				    
				    batchId = submitBatchUpload(batchInfo);
				    
				    updateBatchStatus(batchId, 4, "startDateTime");
				    
				    //log batch activity
				    batchuploadactivity ba = new batchuploadactivity();
				    ba.setActivity("New Dropped File: "+fileName+" was found in "+ rootPath + configDroppedPath);
				    ba.setBatchUploadId(batchId);
				    transactionInDAO.submitBatchActivityLog(ba);
				    
				    //log batch activity
				    ba = new batchuploadactivity();
				    ba.setActivity("New inbound batch was created batchId:" + batchId + " utBatchName:" + batchName);
				    ba.setBatchUploadId(batchId);
				   transactionInDAO.submitBatchActivityLog(ba);
				}
				
				
				//figure out how many active transports are using fileExt method for this particular path, we need to remove the parent directory from input path
				List<configurationTransport> transportList = configurationtransportmanager.getTransportListForFileExtAndPath(fileExt, transportMethodId, 1, transportId);

				//figure out if files has distinct delimiters
				List<configurationTransport> transports = configurationtransportmanager.getConfigTransportForFileExtAndPath(fileExt, transportMethodId, 1, transportId);
				    
				batchDetails = transactionInDAO.getBatchDetails(batchId);

				String newFileName = "";

				Integer configId = 0;
				Integer fileSize = 0;
				Integer encodingId = 1;

				//Check to see if there was a transport found, if not create error
				if (transportList.isEmpty() || transports.isEmpty()) {

				    batchDetails.setEncodingId(encodingId);
				    transactionInDAO.updateBatchUpload(batchDetails);

				    //insert error
				    errorId = 13;
				    statusId = 7;
				    
				    //log batch activity
				    batchuploadactivity ba = new batchuploadactivity();
				    ba.setActivity("No valid configuration was found for the selected organization and the file extension of the uploaded file.");
				    ba.setBatchUploadId(batchId);
				    transactionInDAO.submitBatchActivityLog(ba);
				    
				} 
				else if (transports.size() == 1) {

				    if (errorId == 0) {
					encodingId = transports.get(0).getEncodingId();
					configurationTransport ct = configurationtransportmanager.getTransportDetailsByTransportId(transportId);
					fileSize = ct.getmaxFileSize();
					if (transportList.size() > 1) {
					    configId = 0;
					    fileSize = configurationtransportmanager.getMinMaxFileSize(fileExt, transportMethodId);
					    // here we need to check to see if there is a naming convention
					    for (configurationTransport cdt : transportList) {
						//get message specs
						configurationMessageSpecs messageSpecs = configurationManager.getMessageSpecs(cdt.getconfigId());
						if (fileName.toLowerCase().startsWith(messageSpecs.getFileNameConfigHeader().toLowerCase())) {
						    configId = messageSpecs.getconfigId();
						    fileSize = cdt.getmaxFileSize();
						    break;
						}
					    }

					} else {
					    configId = ct.getconfigId();
					}

					if(newBatchRecord) {
					    batchDetails.setConfigId(configId);
					    batchDetails.setContainsHeaderRow(transports.get(0).getContainsHeaderRow());
					    batchDetails.setDelimChar(transports.get(0).getDelimChar());
					    batchDetails.setFileLocation(ct.getfileLocation());
					    batchDetails.setOrgId(orgId);
					    batchDetails.setOriginalFileName(fileName);
					    batchDetails.setEncodingId(encodingId);
					    batchDetails.setUserId(0);

					    transactionInDAO.updateBatchUpload(batchDetails);
					}

					if (batchDetails.getConfigId() != 0) {
					    statusId = 42;
					}
				    }
				} 
				else if (transportList.size() > 1 && transports.size() > 1) {
				    //we loop though our delimiters for this type of fileExt
				    String delimiter = "";
				    Integer fileDelimiter = 0;
				    String fileLocation = "";
				    Integer userId = 0;

				    //get distinct delimiters
				    List<configurationTransport> delimList = configurationtransportmanager.getDistinctDelimCharForFileExt(fileExt, transportMethodId);
				    List<configurationTransport> encodings = configurationtransportmanager.getTransportEncoding(fileExt, transportMethodId);

				    //we reject file is multiple encodings/delimiters are found for extension type as we won't know how to decode it and read delimiter
				    if (encodings.size() != 1) {
					if(newBatchRecord) {
					    batchDetails.setUserId(usermanager.getUserByTypeByOrganization(orgId).get(0).getId());
					    transactionInDAO.updateBatchUpload(batchDetails);
					}
					statusId = 7;
					errorId = 16;
					
					//log batch activity
					batchuploadactivity ba = new batchuploadactivity();
					ba.setActivity("Can't determine the encoding type for the found file.");
					ba.setBatchUploadId(batchId);
					transactionInDAO.submitBatchActivityLog(ba);
					
				    } else {

					encodingId = encodings.get(0).getEncodingId();
					for (configurationTransport ctdelim : delimList) {
					    fileSystem dir = new fileSystem();
					    int delimCount = (Integer) dir.checkFileDelimiter(file, ctdelim.getDelimChar());
					    if (delimCount > 3) {
						delimiter = ctdelim.getDelimChar();
						fileDelimiter = ctdelim.getfileDelimiter();
						statusId = 2;
						fileLocation = ctdelim.getfileLocation();
						break;
					    }
					}
				    }

				    if (errorId > 0) {
					if(newBatchRecord) {
					    // some error detected from previous checks
					    userId = usermanager.getUserByTypeByOrganization(orgId).get(0).getId();
					    batchDetails.setConfigId(configId);
					    batchDetails.setFileLocation(rootPath + configDroppedPath);
					    batchDetails.setOrgId(orgId);
					    batchDetails.setOriginalFileName(fileName);
					    batchDetails.setUserId(0);
					    batchDetails.setEncodingId(encodingId);
					}

					if (batchDetails.getConfigId() != 0 && batchDetails.getStatusId() == 2) {
					   batchDetails.setStatusId(42);
					}

					transactionInDAO.updateBatchUpload(batchDetails);
				    } 
				    else if (statusId != 2) {
					//no vaild delimiter detected
					statusId = 7;
					userId = usermanager.getUserByTypeByOrganization(orgId).get(0).getId();
					if(newBatchRecord) {
					    batchDetails.setConfigId(configId);
					    batchDetails.setFileLocation(rootPath + configDroppedPath);
					    batchDetails.setOrgId(orgId);
					    batchDetails.setOriginalFileName(fileName);
					    batchDetails.setUserId(0);
					    batchDetails.setEncodingId(encodingId);

					    transactionInDAO.updateBatchUpload(batchDetails);
					}   

					errorId = 15;
					
					//log user activity
					batchuploadactivity ba = new batchuploadactivity();
					ba.setActivity("File: "+fileName+" did not have the correct delimiter that was selected for configuration id:" + configId);
					ba.setBatchUploadId(batchId);
					transactionInDAO.submitBatchActivityLog(ba);

				    } 
				    else if (statusId == 2) {
					encodingId = encodings.get(0).getEncodingId();

					//we check to see if there is multi header row, if so, we reject because we don't know what header rows value to look for
					List<configurationTransport> containsHeaderRowCount = configurationtransportmanager.getCountContainsHeaderRow(fileExt, transportMethodId);

					if (containsHeaderRowCount.size() != 1) {
					    if(newBatchRecord) {
						batchDetails.setUserId(usermanager.getUserByTypeByOrganization(orgId).get(0).getId());
						transactionInDAO.updateBatchUpload(batchDetails);
					    }
					    statusId = 7;
					    errorId = 14;
					    
					    //log batch activity
					    batchuploadactivity ba = new batchuploadactivity();
					    ba.setActivity("File with same extension, delimiter should be set up either contain headers or do not contain headers.");
					    ba.setBatchUploadId(batchId);
					    transactionInDAO.submitBatchActivityLog(ba);
					} 
					else {
					    List<Integer> totalConfigs = configurationtransportmanager.getConfigCount(fileExt, transportMethodId, fileDelimiter);

					    //set how many configs we have
					    if (totalConfigs.size() > 1) {
						configId = 0;
					    } else {
						configId = totalConfigs.get(0);
					    }

					    //get path
					    fileLocation = configurationtransportmanager.getTransportDetails(totalConfigs.get(0)).getfileLocation();
					    fileSize = configurationtransportmanager.getTransportDetails(totalConfigs.get(0)).getmaxFileSize();
					    List<utUser> users = usermanager.getSendersForConfig(totalConfigs);
					    if (users.size() == 0) {
						users = usermanager.getOrgUsersForConfig(totalConfigs);
					    }
					    userId = users.get(0).getId();

					    if(newBatchRecord) {
						batchDetails.setContainsHeaderRow(containsHeaderRowCount.get(0).getContainsHeaderRow());
						batchDetails.setDelimChar(delimiter);
						batchDetails.setConfigId(configId);
						batchDetails.setFileLocation(fileLocation);
						batchDetails.setOrgId(orgId);
						batchDetails.setOriginalFileName(fileName);
						batchDetails.setUserId(0);
						batchDetails.setEncodingId(encodingId);
					    }

					    if (batchDetails.getConfigId() != 0 && batchDetails.getStatusId() == 2) {
						batchDetails.setStatusId(42);
					    }

					    transactionInDAO.updateBatchUpload(batchDetails);
					}
				    }
				}

				createBatchTables(batchId, batchDetails.getConfigId());
				
				//log batch activity
				batchuploadactivity ba = new batchuploadactivity();
				ba.setActivity("Created all inbound batch tables for batchId:" + batchId);
				ba.setBatchUploadId(batchId);
				transactionInDAO.submitBatchActivityLog(ba);

				//we encoded the file if it is not
				File newFile = new File(rootPath + orgDetails.getcleanURL() + "/input files/encoded_"+batchName + fileName.substring(fileName.lastIndexOf(".")));
				
				//log batch activity
				ba = new batchuploadactivity();
				ba.setActivity("Created the encoded file. File Location/Name:" + rootPath + orgDetails.getcleanURL() + "/input files/encoded_"+batchName + fileName.substring(fileName.lastIndexOf(".")));
				ba.setBatchUploadId(batchId);
				transactionInDAO.submitBatchActivityLog(ba);

				// now we move file
				Path source = file.toPath();
				Path target = newFile.toPath();

				File archiveFile = new File(myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + "archive_"+batchName + fileName.substring(fileName.lastIndexOf(".")));
				Path archive = archiveFile.toPath();
				

				//we keep original file in archive folder
				try {
				    Files.copy(source, archive);
				    
				    //log batch activity
				    ba = new batchuploadactivity();
				    ba.setActivity("Moved archive file. File Location/Name:" + myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + "archive_"+batchName + fileName.substring(fileName.lastIndexOf(".")));
				    ba.setBatchUploadId(batchId);
				    transactionInDAO.submitBatchActivityLog(ba);
				    
				} catch (Exception exError) {
				    sendEmailToAdmin((source.toAbsolutePath() + " file could not be copied to " + archive.toAbsolutePath() + " moveFilesByPath - error message from tomcat - " + Arrays.toString(exError.getStackTrace())), "SFTP Job Error");
				    exError.printStackTrace();
				    insertProcessingError(5, 0, batchId, null, null, null, null, false, false, ((source.toAbsolutePath() + " copy file error ") + Arrays.toString(exError.getStackTrace())));
				    updateBatchStatus(batchId, 7, "endDateTime");
				    file.renameTo((new File(file.getAbsolutePath() + batchName + "_error")));
				    sysErrors = 1;
				    
				    //log batch activity
				    ba = new batchuploadactivity();
				    ba.setActivity("Error moving file to archives directory. Error: " + exError.getMessage());
				    ba.setBatchUploadId(batchId);
				    transactionInDAO.submitBatchActivityLog(ba);
				    
				    break;
				}

				//we check encoding here 
				//file is not encoded
				if (encodingId < 2 && !filemanager.isFileBase64Encoded(file)) { 
				    String encodedOldFile = filemanager.encodeFileToBase64Binary(file);
				    filemanager.writeFile(newFile.getAbsolutePath(), encodedOldFile);

				    try {
					Files.delete(source);
					
				    } catch (Exception exError) {
					sendEmailToAdmin((source.toAbsolutePath() + " file could not be deleted moveFilesByPath - error message from tomcat - " + Arrays.toString(exError.getStackTrace())), "SFTP Job Error");
					exError.printStackTrace();
					insertProcessingError(5, 0, batchId, null, null, null, null, false, false, ((source.toAbsolutePath() + "delete file error") + Arrays.toString(exError.getStackTrace())));
					updateBatchStatus(batchId, 7, "endDateTime");
					file.renameTo((new File(file.getAbsolutePath() + batchName + "_error")));
					sysErrors = 1;
					break;
				    }

				} else {

				    try {
					Files.move(source, target);
					
				    } catch (Exception exError) {
					sendEmailToAdmin((source.toAbsolutePath() + " source could not be moved to " + target.toAbsolutePath() + " file could not be moved moveFilesByPath - tomcat error message - " + Arrays.toString(exError.getStackTrace())), "SFTP Job Error");
					exError.printStackTrace();
					insertProcessingError(5, 0, batchId, null, null, null, null, false, false, ((source.toAbsolutePath() + "move file error") + Arrays.toString(exError.getStackTrace())));
					updateBatchStatus(batchId, 7, "endDateTime");
					file.renameTo((new File(file.getAbsolutePath() + batchName + "_error")));
					sysErrors = 1;
					break;
				    }
				}

				if (statusId == 42) {
				    //check file size if configId is 0 we go with the smallest file size *
				    long maxFileSize = fileSize * 1000000;
				    if (Files.size(target) > maxFileSize) {
					statusId = 7;
					errorId = 12;
					
					//log batch activity
					ba = new batchuploadactivity();
					ba.setActivity("Invalid file size. Uploaded file was "+ Files.size(target) + ". The configuration max file size was set to " + maxFileSize);
					ba.setBatchUploadId(batchId);
					transactionInDAO.submitBatchActivityLog(ba);
				    }
				}
				
				if (statusId != 42) {
				    insertProcessingError(errorId, 0, batchId, null, null, null, null, false, false, "");
				}

				updateBatchStatus(batchId, statusId, "");
				
				//log batch activity
				ba = new batchuploadactivity();
				ba.setActivity("Uploaded batchId:"+batchId+" status was set to " + statusId);
				ba.setBatchUploadId(batchId);
				transactionInDAO.submitBatchActivityLog(ba);

				// Check to see if the batch needs to be submitted to a Healt-e-link Registry
				if (batchDetails.getConfigId() != null) {
				    if (batchDetails.getConfigId() > 0) {

					//Need to check the schedule to see if this is an automatic process or manual process
					configurationSchedules configurationSchedule = configurationManager.getScheduleDetails(batchDetails.getConfigId());
					
					if(configurationSchedule != null) {
					    //If manual change the status of the batch so it does not process (Setting batch status to "Manual Processing Required" Id: 64)
					    if(configurationSchedule.gettype() == 1) {
						updateBatchStatus(batchId, 64, "");

						//log batch activity
						ba = new batchuploadactivity();
						ba.setActivity("Uploaded batchId:"+batchId+" configuration (configId:"+batchDetails.getConfigId()+") is set to manual process and is ready to be processed.");
						ba.setBatchUploadId(batchId);
						transactionInDAO.submitBatchActivityLog(ba);
					    }
					}
				    }
				}

			    } catch (Exception exAtFile) {
				exAtFile.printStackTrace();
				System.err.println("moveFilesByPath " + exAtFile.toString());
				sysErrors = 1;
				try {
				    sendEmailToAdmin((exAtFile.toString() + "<br/>" + Arrays.toString(exAtFile.getStackTrace())), "moveFilesByPath - at rename file to error ");
				    //we need to move that file out of the way
				    file.renameTo((new File(file.getAbsolutePath() + batchName + "_error")));
				    sysErrors = 1;
				} catch (Exception ex1) {
				    ex1.printStackTrace();
				    System.err.println("moveFilesByPath " + ex1.getMessage());
				    sysErrors = 1;
				}
			    }
			}

		    }
		}
	    }
	    
	} catch (Exception ex) {
	    ex.printStackTrace();
	    try {
		sendEmailToAdmin((ex.toString() + "<br/>" + Arrays.toString(ex.getStackTrace())), "moveFilesByPath - issue with looping folder files");
	    } catch (Exception ex1) {
		ex1.printStackTrace();
		System.err.println("moveFilesByPath " + ex1.getMessage());
	    }
	    return 1;
	}
	return sysErrors;
    }

    /**
     * this method grabs all distinct ftp path that need to be check for files *
     */
    @Override
    public List<configurationFTPFields> getFTPInfoForJob(Integer method) {
	return transactionInDAO.getFTPInfoForJob(method);
    }

    @Override
    public String newFileName(String path, String fileName) {
	try {
	    File newFile = new File(path + fileName);
	    if (newFile.exists()) {
		int i = 1;
		while (newFile.exists()) {
		    int iDot = fileName.lastIndexOf(".");
		    newFile = new File(path + fileName.substring(0, iDot) + "_(" + ++i + ")" + fileName.substring(iDot));
		}
		fileName = newFile.getName();
	    }
	    return fileName;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("newBatchName " + ex.toString());
	    return null;
	}
    }

    @Override
    public String copyUplaodedPath(configurationTransport transportDetails, MultipartFile fileUpload) {

	//save the file as is to input folder
	MultipartFile file = fileUpload;
	String fileName = file.getOriginalFilename();

	InputStream inputStream;
	OutputStream outputStream;

	try {
	    inputStream = file.getInputStream();
	    File newFile = null;
	    

	    String filelocation = transportDetails.getfileLocation().trim();
	    newFile = new File(myProps.getProperty("ut.directory.utRootDir") + filelocation + fileName);

	    if (newFile.exists()) {
		int i = 1;
		while (newFile.exists()) {
		    int iDot = fileName.lastIndexOf(".");
		    newFile = new File(myProps.getProperty("ut.directory.utRootDir") + filelocation + fileName.substring(0, iDot) + "_(" + ++i + ")" + fileName.substring(iDot));
		}
		fileName = newFile.getName();
		newFile.createNewFile();
	    } else {
		newFile.createNewFile();
	    }

	    //Save the attachment
	    outputStream = new FileOutputStream(newFile);
	    int read = 0;
	    byte[] bytes = new byte[1024];

	    while ((read = inputStream.read(bytes)) != -1) {
		outputStream.write(bytes, 0, read);
	    }
	    outputStream.close();

	    return fileName;
	} catch (IOException e) {
	    System.err.println("copyUplaodedPath " + e.getCause());
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * The 'moveFileDroppedFiles' method will look for files dropped in the UT folders based on the records saved in the 
     * rel_transportfiledropdetails table for configurations.
     * 
     * @return 
     */
    @Override
    public Integer moveFileDroppedFiles() {
	Integer sysErrors = 0;
	
	try {
	    
	    //1 . Find all configurations that have file dropped transport methods (Method = 1)
	    List<configurationFileDropFields> inputPaths = getFileDropInfoForJob(1);
	    
	    //2 we clean up, delete entries in the move files log table with statusId = 2 and transport method = 10 (File Drop)
	    //Find move files log by status Id and methodId
	    //Delete the records found
	    List<MoveFilesLog> existingMoveFileLogs = transactionInDAO.existingMoveFileLogs(2,10);
	    
	    if(existingMoveFileLogs != null) {
		if(!existingMoveFileLogs.isEmpty()) {
		    existingMoveFileLogs.forEach(moveLog -> {
			try {
			    transactionInDAO.deleteMoveFileLogById(moveLog.getId());
			} catch (Exception ex) {
			    Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
			}
		    });
		}
	    }

	    //loop through the file drop paths found and check for any files
	    
	    String directoryHome = myProps.getProperty("ut.directory.utRootDir");
	    
	    if(inputPaths != null) {
		if(!inputPaths.isEmpty()) {
		    
		    for (configurationFileDropFields fileDropInfo : inputPaths) {
			
			sysErrors = 0;
			
			//we insert mvoe log entry, so if anything goes wrong or the scheduler overlaps, we won't check the same folder over and over
			MoveFilesLog moveJob = new MoveFilesLog();
			moveJob.setStatusId(1);
			moveJob.setFolderPath(fileDropInfo.getDirectory());
			moveJob.setTransportMethodId(10);
			moveJob.setMethod(1);
			
			Integer lastMoveLogEntryId = insertSFTPRun(moveJob);

			// check if directory exists, if not throw error
			fileSystem fileSystem = new fileSystem();
			
			//paths are from root instead of /home
			String inPath = directoryHome + fileDropInfo.getDirectory();
			
			File f = new File(inPath);
			
			if (!f.exists()) {
			    moveJob.setNotes(("Directory " + directoryHome + fileDropInfo.getDirectory() + " does not exist"));
			    updateSFTPRun(moveJob);
			    //need to get out of loop since set up was not done properly, will try to throw error
			    sendEmailToAdmin((directoryHome + fileDropInfo.getDirectory() + " does not exist"), "File Drop Job Error");
			}
			//Folder exists lets proceed
			else {
			    
			    //Find the organization for this transportId 
			    Integer orgId = configurationtransportmanager.getOrgIdForFileDropPath(fileDropInfo);
			    
			    configurationTransport transportDetails = configurationtransportmanager.getTransportDetailsByTransportId(fileDropInfo.getTransportId());
			    
			    sysErrors = sysErrors + moveFilesByPath(directoryHome, fileDropInfo.getDirectory(), transportDetails.gettransportMethodId(), orgId, fileDropInfo.getTransportId());

			    if (sysErrors == 0) {
				moveJob.setStatusId(2);
				moveJob.setEndDateTime(new Date());
				updateSFTPRun(moveJob);
			    } 
			}
		    }
		}
	    }
	    
	    // if there are no errors, we release the folder path
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("moveFileDroppedFiles " + ex.toString());
	    try {
		sendEmailToAdmin((ex.toString() + "<br/>" + Arrays.toString(ex.getStackTrace())), "File Drop Job Error - main method errored");
	    } catch (Exception ex1) {
		ex1.printStackTrace();
		System.err.println("moveFileDroppedFiles " + ex1.toString());
	    }
	    return 1;
	}
	return sysErrors;
    }

    /**
     * this method grabs all distinct ftp path that need to be check for files *
     * @param method
     * @return 
     */
    @Override
    public List<configurationFileDropFields> getFileDropInfoForJob(Integer method) {
	return transactionInDAO.getFileDropInfoForJob(method);
    }

    @Override
    public List<Integer> checkCWFieldForList(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) {
	return transactionInDAO.checkCWFieldForList(configId, batchId, cdt, foroutboundProcessing);
    }

    @Override
    public Integer processMultiValueCWData(Integer configId, Integer batchId,
	    configurationDataTranslations cdt, List<CrosswalkData> cwdList, boolean foroutboundProcessing) {

	try {
	    Integer error = 0;
	    List<IdAndFieldValue> idAndValues = getIdAndValuesForConfigField(configId, batchId, cdt, foroutboundProcessing);
	    //we turn cwdList into a map
	    Map<String, String> cwMap = new HashMap<String, String>();
	    for (CrosswalkData cw : cwdList) {
		cwMap.put(cw.getSourceValue(), cw.getTargetValue());
	    }

	    //1. we get list of ids for field
	    for (IdAndFieldValue idAndValue : idAndValues) {
		Integer invalidCount = 0;
		Integer blankListLength = 0;
		List<String> values = new ArrayList<String>();

		List<String> fieldValues = Arrays.asList(idAndValue.getFieldValue().split("\\^\\^\\^\\^\\^", -1));
		//we loop through value and compare to cw
		for (String fieldValue : fieldValues) {
		    //sometimes user need to pass blank list, should not be count as an error
		    if (cwMap.containsKey(fieldValue.trim())) {
			values.add(cwMap.get(fieldValue.trim()));
		    } else //we pass value
		    if (cdt.getPassClear() == 1) {
			values.add(fieldValue.trim());
			invalidCount = invalidCount + 1;
			if (fieldValue.trim().length() != 0) {
			    blankListLength = blankListLength + 1;
			}
		    }
		}

		String newValue = StringUtils.collectionToDelimitedString(values, "^^^^^");
		error = updateFieldValue(batchId, newValue, cdt.getFieldNo(), idAndValue.getTransactionId(), foroutboundProcessing);

		//we insert error if no valid values were replaced
		if (invalidCount > 0 && blankListLength > 0) {
		    insertProcessingError(3, cdt.getconfigId(), batchId, cdt.getFieldNo(), null, cdt.getCrosswalkId(), null, false, foroutboundProcessing, "", idAndValue.getTransactionId());
		}
	    }

	    return error;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("processMultiValueCWData " + ex.toString());
	    return 1;

	}

    }

    @Override
    public List<IdAndFieldValue> getIdAndValuesForConfigField(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) {
	return transactionInDAO.getIdAndValuesForConfigField(configId, batchId, cdt, foroutboundProcessing);
    }

    @Override
    public Integer updateFieldValue(Integer batchId, String fieldValue, Integer fieldNo, Integer transactionId, boolean foroutboundProcessing) {
	return transactionInDAO.updateFieldValue(batchId, fieldValue, fieldNo, transactionId, foroutboundProcessing);
    }

    @Override
    public void trimFieldValues(Integer batchId, boolean foroutboundProcessing, Integer configId, boolean trimAll) {
	transactionInDAO.trimFieldValues(batchId, foroutboundProcessing, configId, trimAll);
    }

    @Override
    public void sendEmailToAdmin(String message, String subject) throws Exception {
	try {
	    mailMessage mail = new mailMessage();
	    mail.setfromEmailAddress("support@health-e-link.net");
	    mail.setmessageBody(message);
	    mail.setmessageSubject(subject + " " + myProps.getProperty("server.identity"));
	    mail.settoEmailAddress(myProps.getProperty("admin.email"));
	    emailManager.sendEmail(mail);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    throw new Exception(ex);
	}
    }

    @Override
    public Integer updateBatchDLStatusByUploadBatchId(Integer batchUploadId, Integer fromStatusId, Integer toStatusId, String timeField) {
	return transactionInDAO.updateBatchDLStatusByUploadBatchId(batchUploadId, fromStatusId, toStatusId, timeField);
    }

    @Override
    public Integer clearBatchDownloads(List<Integer> batchDownloadIDs) {
	return transactionInDAO.clearBatchDownloads(batchDownloadIDs);
    }

    @Override
    public String getTransactionInIdsFromBatch(Integer batchUploadId) {
	return transactionInDAO.getTransactionInIdsFromBatch(batchUploadId);
    }

    @Override
    public List<WSMessagesIn> getWSMessagesByStatusId(List<Integer> statusIds) {
	return transactionInDAO.getWSMessagesByStatusId(statusIds);
    }

    @Override
    public WSMessagesIn getWSMessagesById(Integer wsMessageId) {
	return transactionInDAO.getWSMessagesById(wsMessageId);
    }

    @Override
    public Integer updateWSMessage(WSMessagesIn wsMessage) {
	return transactionInDAO.updateWSMessage(wsMessage);
    }

    @Override
    public List<Integer> getErrorCodes(List<Integer> codesToIgnore) {
	return transactionInDAO.getErrorCodes(codesToIgnore);
    }

    @Override
    public Integer rejectInvalidSourceSubOrg(batchUploads batch,
	    configurationConnection confConn, boolean nofinalStatus) {
	return transactionInDAO.rejectInvalidSourceSubOrg(batch, confConn, nofinalStatus);
    }

    @Override
    public List<Integer> getBatchesForReport(Date fromDate, Date toDate) throws Exception {

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	String dateFrom = df.format(fromDate);
	String dateTo = df.format(toDate);

	List<Integer> batchIds = new ArrayList<Integer>();

	List<Integer> uploadedBatches = transactionInDAO.geBatchesIdsForReport(dateFrom, dateTo);

	if (!uploadedBatches.isEmpty()) {
	    for (Integer batch : uploadedBatches) {
		batchIds.add(batch);
	    }
	}

	return batchIds;
    }

    @Override
    public BigInteger getMessagesSent(Date fromDate, Date toDate) throws Exception {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	String dateFrom = df.format(fromDate);
	String dateTo = df.format(toDate);

	return transactionInDAO.getMessagesSent(dateFrom, dateTo);

    }

    @Override
    public BigInteger getRejectedCount(Date fromDate, Date toDate) throws Exception {

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	String dateFrom = df.format(fromDate);
	String dateTo = df.format(toDate);

	return transactionInDAO.getRejectedCount(dateFrom, dateTo);

    }
    
    @Override
    public BigInteger getRejectedReceivedCount(Date fromDate, Date toDate) throws Exception {

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	String dateFrom = df.format(fromDate);
	String dateTo = df.format(toDate);
	
	return transactionInDAO.getRejectedReceivedCount(dateFrom, dateTo);

    }

    @Override
    public List<activityReportList> getReferralList(Date fromDate, Date toDate) throws Exception {

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	String dateFrom = df.format(fromDate);
	String dateTo = df.format(toDate);

	List<activityReportList> reportList = transactionInDAO.getReferralList(dateFrom, dateTo);
	return reportList;
    }

    @Override
    public List<referralActivityExports> getReferralActivityExports() throws Exception {
	return transactionInDAO.getReferralActivityExports();
    }

    @Override
    public void clearMultipleTargets(Integer batchId) throws Exception {
	transactionInDAO.clearMultipleTargets(batchId);
    }

    @Override
    public void sendRejectNotification(batchUploads batch) throws Exception {
	
	mailMessage mail = new mailMessage();
	mail.setfromEmailAddress("support@health-e-link.net");

	//String[] ccAddresses = new String[2];
	List<String> ccAddresses = new ArrayList<String>();
	ccAddresses.add(myProps.getProperty("admin.email"));
	
	Organization orgDetails = organizationmanager.getOrganizationById(batch.getOrgId());
	
	//build message
	String message = "Uploaded File (Batch Id:" + batch.getUtBatchName() + ") contains " + batch.getErrorRecordCount() + " error(s).";

	message = message + "<br/><br/>Environment: " + myProps.getProperty("server.identity");
	message = message + "<br/><br/>Batch Id: " + batch.getUtBatchName();
	message = message + "<br/><br/>Total Transactions: " + batch.getTotalRecordCount();
	message = message + "<br/>Total Errors: " + batch.getErrorRecordCount();
	message += "<br /><br />Sending Organization: " + orgDetails.getOrgName();

	List<Transaction> transactions = getTransactionsByStatusId(batch.getId(), rejectIds, 5);
	
	//Need to get the connection details
	List<configurationConnection> connectionList = configurationManager.getConnectionsBySourceConfiguration(batch.getConfigId());
	
	if(connectionList != null) {
	    
	    configurationConnection connectionDetails = connectionList.get(0);
	    
	    if(connectionDetails != null) {
		List<configurationConnectionSenders> connectionSenders = configurationManager.getConnectionSenders(connectionDetails.getId());
		
		if(connectionSenders != null) {
		    if(!connectionSenders.isEmpty()) {
			connectionSenders.forEach(sender -> {
			    if(sender.getSendEmailNotifications()) {
				ccAddresses.add(sender.getEmailAddress());
			    }
			});
		    }
		}
	    }
	}

	mail.setmessageBody(message);
	mail.setmessageSubject("Uploaded File submitted on " + myProps.getProperty("server.identity") + " environment contains rejected transactions");
	mail.settoEmailAddress(myProps.getProperty("reject.email"));

	if (ccAddresses.size() > 0) {
	    String[] ccEmailAddresses = new String[ccAddresses.size()];
	    ccEmailAddresses = ccAddresses.toArray(ccEmailAddresses);
	    mail.setccEmailAddress(ccEmailAddresses);
	}

	emailManager.sendEmail(mail);
    }

    @Override
    public List<Transaction> getTransactionsByStatusId(Integer batchId, List<Integer> statusIds, Integer howMany) throws Exception {

	List<Transaction> transactions = setTransactionInInfoByStatusId(batchId, statusIds, howMany);

	return transactions;

    }

    @Override
    public List<Transaction> setTransactionInInfoByStatusId(Integer batchId, List<Integer> statusIds, Integer howMany) throws Exception {
	return transactionInDAO.setTransactionInInfoByStatusId(batchId, statusIds, howMany);
    }

    @Override
    public void loadMassBatches() throws Exception {
	/**
	 * need to update to reprocess the batch once if it is stuck for three hours
	 *
	 * 1. during first reprocess we need to notify admin 2. track it so we don't reprocess it again infinite times 3. if stuck again we set status to 58, NTR (Need to Review)
	 *
	 */
	//we check to see if anything is running first
	boolean run = true;
	
	List<batchUploads> batchInProcess = getBatchesByStatusIds(Arrays.asList(38));

	//we check time stamp to see how long that file has been processing
	//get the details
	if (!batchInProcess.isEmpty()) {
	    
	    //check how long first batch is going
	    //if more than 2 hours need to email 
	    LocalDateTime d1;
	    LocalDateTime d2;
	    long diffHours;
	    
	    for (batchUploads runningBatches : batchInProcess) {
		batchUploads stuckBatchDetails = getBatchDetails(runningBatches.getId());

		if (stuckBatchDetails != null) {
		    d1 = LocalDateTime.ofInstant(stuckBatchDetails.getStartDateTime().toInstant(), ZoneId.systemDefault());
		    d2 = LocalDateTime.now();

		    diffHours = java.time.Duration.between(d1, d2).toHours();
		    
		    if (diffHours >= 3) {
		
			try {
			    //log user activity
			    batchuploadactivity ba = new batchuploadactivity();
			    ba.setActivity("System Set to Status 58 - Loading");
			    ba.setBatchUploadId(stuckBatchDetails.getId());
			    transactionInDAO.submitBatchActivityLog(ba);
			} catch (Exception ex) {
			    ex.printStackTrace();
			    System.err.println("transactionId - insert user log" + ex.toString());
			}

			String subject = "Batch set to Need to Review - processMassBatches ";
			String  msgBody = "Batch " + stuckBatchDetails.getId() + " (" + stuckBatchDetails.getUtBatchName() + ") needs to be reviewed.";

			updateBatchStatus(stuckBatchDetails.getId(), 58, "endDateTime");

			//we notify admin
			//we also notify admin
			mailMessage mail = new mailMessage();
			mail.settoEmailAddress(myProps.getProperty("admin.email"));
			mail.setfromEmailAddress("support@health-e-link.net");
			mail.setmessageSubject(subject + " " + myProps.getProperty("server.identity"));
			StringBuilder emailBody = new StringBuilder();
			emailBody.append("<br/>Current Time " + d2.toString());
			emailBody.append("<br/><br/>" + msgBody + "<br/>File Name is  - " + stuckBatchDetails.getOriginalFileName() + ".");
			emailBody.append("<br/>" + batchInProcess.size() + " batch(es) with status 38 in queue.<br/>");
			mail.setmessageBody(emailBody.toString());
			emailManager.sendEmail(mail);
		    }
		}
	    }
	}
	
	if (run) {
	    List<batchUploads> batches = getBatchesByStatusIds(Arrays.asList(42, 2));
	    if (batches != null) {
		if (!batches.isEmpty()) {
		    
		    for (batchUploads batch : batches) {
			executor.execute(new Runnable() {
			    @Override
			    public void run() {
				try {
				    loadBatch(batch.getId());
				} catch (Exception ex) {
				    Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			    }
			});
		    }
		}
	    }
	}
    }
    
    @Override
    public void loadBatch(Integer batchId) throws Exception {
	
	//first thing we do is get details, then we set it to  38
	batchUploads batch = getBatchDetails(batchId);
	
	//Get a full list of macros
	List<Macros> macroList = configurationManager.getMacros();

	//we recheck status in case it was picked up in a loop
	if (batch.getStatusId() == 42) {
	    
	    Integer batchStatusId = 38;
	    List<Integer> errorStatusIds = Arrays.asList(11, 13, 14, 16);
	    String processFolderPath = "loadFiles/";

	    try {
		try {
		    //log batch activity
		    batchuploadactivity ba = new batchuploadactivity();
		    ba.setActivity("Scheduled job loadBatch was called to load the contents for this batch.");
		    ba.setBatchUploadId(batchId);
		    transactionInDAO.submitBatchActivityLog(ba);

		} catch (Exception ex) {
		    
		    //log batch activity
		    batchuploadactivity ba = new batchuploadactivity();
		    ba.setActivity("Scheduled job loadBatch was called to load the contents for this batch. But an error occured, error: " + ex.getMessage());
		    ba.setBatchUploadId(batchId);
		    transactionInDAO.submitBatchActivityLog(ba);
		    
		    ex.printStackTrace();
		    System.err.println("loadBatch - insert user log" + ex.toString());
		}
		
		//Create the batch tables
		createBatchTables(batchId,batch.getConfigId());
		
		//log batch activity
		batchuploadactivity ba = new batchuploadactivity();
		ba.setActivity("If not already done, all inbound batch tables were created for batchId:"+batchId);
		ba.setBatchUploadId(batchId);
		transactionInDAO.submitBatchActivityLog(ba);
		
		//Find all targets set up for this configuration
		List<configurationConnection> configurationConnections = configurationManager.getConnectionsBySourceConfiguration(batch.getConfigId());
		
		// set batch to SBL - 38
		//if could be that the process has picked this up
		updateBatchStatus(batchId, batchStatusId, "");
		
		//log batch activity
		ba = new batchuploadactivity();
		ba.setActivity("inbound batchId:"+batchId+" status was set to " + batchStatusId);
		ba.setBatchUploadId(batchId);
		transactionInDAO.submitBatchActivityLog(ba);

		Integer sysErrors = 0;
		sysErrors = sysErrors + clearTransactionTables(batchId, batch.getConfigId());
		
		//log batch activity
		ba = new batchuploadactivity();
		ba.setActivity("Cleared all tables for inbound batchId:"+batchId);
		ba.setBatchUploadId(batchId);
		transactionInDAO.submitBatchActivityLog(ba);
		
		Integer HELRegistryConfigId = 0;
		Integer HELRegistryId = 0;
		String HELSchemaName = "";
		
		if(configurationConnections != null) {
		    if(!configurationConnections.isEmpty()) {
			
			for(configurationConnection connection : configurationConnections) {
			    
			    configurationTransport targetTransportDetails = configurationtransportmanager.getTransportDetails(connection.gettargetConfigId());
			    
			    if(targetTransportDetails.getHelRegistryConfigId() != null) {
				if(targetTransportDetails.getHelRegistryConfigId() > 0) {
				    HELSchemaName = targetTransportDetails.getHelSchemaName();
				    HELRegistryId = targetTransportDetails.getHelRegistryId();
				    HELRegistryConfigId = targetTransportDetails.getHelRegistryConfigId();
				}
			    }
			}
		    }
		}
		
		if(HELRegistryConfigId != null) {
		    if (HELRegistryId > 0 && HELRegistryConfigId > 0 && !"".equals(HELSchemaName)) {
			boolean fromRegistryFileUpload = false;
			
			//Check if there is file upload (from HEL Registry) entry
			uploadedFile existingRegistryUploadedFile = fileuploadmanager.getUploadedFileBySQL(HELSchemaName,batch.getOriginalFileName());
			
			if (existingRegistryUploadedFile != null) {
			    fromRegistryFileUpload = true;
			    fileuploadmanager.updateUploadedFile(HELSchemaName,existingRegistryUploadedFile.getId(),23,batchId);
			    
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("Updated eReferral uploaded file entryId:"+existingRegistryUploadedFile.getId()+" to status:23 and batchId:"+batchId);
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			}
			
			if(!fromRegistryFileUpload) {
			    submittedMessage existingRegistrySubmittedMessage = submittedmessagemanager.getSubmittedMessageBySQL(HELSchemaName,batch.getOriginalFileName());

			    if (existingRegistrySubmittedMessage != null) {
				submittedmessagemanager.updateSubmittedMessage(HELSchemaName,existingRegistrySubmittedMessage.getId(),23,batchId);
				
				//log batch activity
				ba = new batchuploadactivity();
				ba.setActivity("Updated eReferral online form entryId:"+existingRegistrySubmittedMessage.getId()+" to status:23 and batchId:"+batchId);
				ba.setBatchUploadId(batchId);
				transactionInDAO.submitBatchActivityLog(ba);
			    }
			}

		    }
		}
		
		String errorMessage = "Load errors, please contact admin to review logs";
		
		// loading batch will take it all the way to loaded (9) status for
		if (sysErrors > 0) {
		    insertProcessingError(5, null, batchId, null, null, null, null, false, false, "Error cleaning out transaction tables.  Batch cannot be loaded.");
		    updateBatchStatus(batchId, 39, "endDateTime");
		    
		    //log batch activity
		    ba = new batchuploadactivity();
		    ba.setActivity("Error cleaning out transaction tables.  Batch cannot be loaded.");
		    ba.setBatchUploadId(batchId);
		    transactionInDAO.submitBatchActivityLog(ba);
		}

		//2. we load data with my sql
		String actualFileName = null;
		String newfilename = null;

		//decoded files will always be in loadFiles folder with UTBatchName 
		// all files are Base64 encoded at this point
		String filePath = myProps.getProperty("ut.directory.utRootDir") + batch.getFileLocation();
		String encodedFileName = "encoded_"+batch.getUtBatchName();
		
		if(!encodedFileName.contains(".")) {
		    encodedFileName += batch.getOriginalFileName().substring(batch.getOriginalFileName().lastIndexOf(".")).toLowerCase();
		}
		
		File encodedFile = new File(filePath + encodedFileName);
		
		String decodedFilePath = myProps.getProperty("ut.directory.utRootDir") + processFolderPath;
		String decodedFileName = batch.getUtBatchName();
		String decodedFileExt = batch.getOriginalFileName().substring(batch.getOriginalFileName().lastIndexOf(".")).toLowerCase();
		
		boolean fileFound = false;
		
		if(encodedFile.exists()) {
		    String decodedFile = decodedFilePath + decodedFileName + decodedFileExt;
		    
		    try {
			filemanager.decode((filePath + encodedFileName), decodedFile);
			fileFound = true;
		    } catch (Exception ex) {
			ex.printStackTrace();
			sysErrors = 1;
			processingSysErrorId = 17;
			
			//log batch activity
			ba = new batchuploadactivity();
			ba.setActivity("System was not able to decode the file: " + filePath + encodedFileName);
			ba.setBatchUploadId(batchId);
			transactionInDAO.submitBatchActivityLog(ba);
		    }
		}
		else {
		    String nonencodedFileName = batch.getUtBatchName();
		    
		    if(!nonencodedFileName.contains(".")) {
			nonencodedFileName += decodedFileExt;
		    }
		    
		    File nonencodedFile = new File(filePath + nonencodedFileName);
		    
		    if(nonencodedFile.exists()) {
			String decodedFile = decodedFilePath + decodedFileName + decodedFileExt;
			
			try {
			    filemanager.copyFile((filePath + nonencodedFileName), decodedFile);
			    fileFound = true;
			} catch (Exception ex) {
			    ex.printStackTrace();
			    sysErrors = 1;
			    processingSysErrorId = 17;
			    
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("System was not able to decode the file: " + filePath + nonencodedFileName);
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			}
		    }
		}
		
		if (fileFound) {

		    Integer delimId = 0;

		    actualFileName = (decodedFilePath + decodedFileName + decodedFileExt);

		    //If batch is set up for CCD input then we need to translate it to a pipe-delimited text file.
		    //here we need to check if we should change file to xml or hr for org sometimes org will send hl7 files over or .out or some other extension, they all need to be .hr all ccd file will need to end in xml
		    //so we check decodedFileName and change it to the proper extension if need be
		    String changeToExtension = "";
		    String processFileName = batch.getOriginalFileName();
		    String lineTerminator = "\\n";

		    //For configId of 0, we need to check to see if org has hr or ccd if configId is not 0, we pull up the extension type and rename file if we find more than one file extension set up for org we reject them them file extension will be 4 (hr) or 9 (ccd) info we have from batchUpload - transportMethodId, configId, orgId
		    if (batch.getConfigId() != 0) {
			configurationTransport ct = configurationtransportmanager.getTransportDetails(batch.getConfigId());
			switch (ct.getfileType()) {
			    case 9:
				changeToExtension = "xml";
				break;
			    case 4:
				changeToExtension = "hr";
				break;
			    case 12:
				changeToExtension = "json";
				break;
			    default:
				break;
			}
			delimId = ct.getfileDelimiter();
			lineTerminator = ct.getLineTerminator();

		    } 
		    else if (batch.getConfigId() == 0) {
			//should restrict this to only 4/9
			//see if the users has any 4/9 fileType, we don't need to worry about changing extension if org doesn't

			List<configurationTransport> ctList = configurationtransportmanager.getConfigurationTransportFileExtByFileType(batch.getOrgId(), batch.getTransportMethodId(), null, Arrays.asList(1), true, false);
			if (ctList.size() > 1) {
			    //it is ok to have multiple if they are not using file type 4/9, so we check again
			    List<configurationTransport> ctList2 = configurationtransportmanager.getConfigurationTransportFileExtByFileType(batch.getOrgId(), batch.getTransportMethodId(), Arrays.asList(4, 9), Arrays.asList(1), true, false);
			    if (!ctList2.isEmpty()) { //they have multiple file types defined along with hr or ccd, we fail them
				//clean up
				File tempLoadFile = new File(actualFileName);
				if (tempLoadFile.exists()) {
				    tempLoadFile.delete();
				}
				//log
				updateBatchStatus(batchId, 7, "endDateTime");
				insertProcessingError(18, null, batchId, null, null, null, null, false, false, "Multiple file types were found for transport method.");
				
				//log batch activity
				ba = new batchuploadactivity();
				ba.setActivity("Transport method contains both hr/ccd and another file type");
				ba.setBatchUploadId(batchId);
				transactionInDAO.submitBatchActivityLog(ba);
			    }
			} else if (ctList.size() == 1) {
			    if (ctList.get(0).getfileType() == 9) {
				changeToExtension = "xml";
			    } else if (ctList.get(0).getfileType() == 4) {
				changeToExtension = "hr";
			    } else if (ctList.get(0).getfileType() == 12) {
				changeToExtension = "json";
			    }
			    delimId = ctList.get(0).getfileDelimiter();
			    lineTerminator = ctList.get(0).getLineTerminator();
			}
		    }

		    if (!"".equals(changeToExtension)) {
			processFileName = batch.getUtBatchName() + "." + changeToExtension;
			//we overwrite file 
			//old file is here actualFileName;
			//new file is the same name with diff extension
			File actualFile = new File(actualFileName);
			File fileWithNewExtension = new File(decodedFilePath + processFileName);
			Path fileWithOldExtension = actualFile.toPath();
			Path renamedFile = fileWithNewExtension.toPath();
			Files.move(fileWithOldExtension, renamedFile, REPLACE_EXISTING);
		    }

		    if (processFileName.endsWith(".xml")) {
			
			String targetOrgName = "";
			
			//Check if the source transport method is direct
			if(batch.getTransportMethodId() == 13) {
			    directmessagesin directDetails = transactionInDAO.getDirectAPIMessagesByBatchUploadId(batch.getId());
			    
			    if(directDetails != null) {
				targetOrgName = directDetails.getToDirectAddress();
			    }
			}
			
			newfilename = ccdtotxt.TranslateCCDtoTxt(decodedFilePath, decodedFileName, batch.getOrgId(), batch.getConfigId(), targetOrgName, batchId);
			
			if (newfilename.equals("ERRORERRORERROR")) {
			    updateBatchStatus(batchId, 39, "endDateTime");
			    insertProcessingError(5, null, batchId, null, null, null, null, false, false, "Error at applying the parsing template");
			    
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("Error at applying parsing template. Could not translate CCD to TXT.");
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			    
			} else if (newfilename.equals("FILE IS NOT XML ERROR")) {
			    
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("XML format is invalid.");
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			    
			    updateBatchStatus(batchId, 7, "endDateTime");
			    insertProcessingError(22, null, batchId, null, null, null, null, false, false, "XML format is invalid.");
			    sendEmailToAdmin((new Date() + "<br/>Please login and review. Load batch failed.  <br/>Batch Id -  " + batch.getId() + "<br/> UT Batch Name " + batch.getUtBatchName() + " <br/>Original batch file name - " + batch.getOriginalFileName()), "Load xml Batch Failed");
			}
			else {
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("Parsing successfully parsed the inbound file and generated file location/name: " + decodedFilePath + newfilename);
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			}

			actualFileName = (decodedFilePath + newfilename);
			
			//we remove temp load file 
			File tempLoadFile = new File(decodedFilePath + processFileName);
			if (tempLoadFile.exists()) {
			    tempLoadFile.delete();
			}
			
		    } 
		    //if the original file name is a HL7 file (".hr") then we are going to translate it to a pipe-delimited text file.
		    else if (processFileName.endsWith(".hr")) {
			newfilename = hl7toTxt.TranslateHl7toTxt(decodedFilePath, decodedFileName, batch.getOrgId(), batch.getConfigId(),batchId);
			
			if (newfilename.equals("ERRORERRORERROR")) {
			    
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("HL7toTxt ran into a problem parsing the inbound file.");
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			    
			    updateBatchStatus(batchId, 39, "endDateTime");
			    insertProcessingError(5, null, batchId, null, null, null, null, false, false, "Error at applying jar template");
			    sendEmailToAdmin((new Date() + "<br/>Please login and review. Load batch failed.  <br/>Batch Id -  " + batch.getId() + "<br/> UT Batch Name " + batch.getUtBatchName() + " <br/>Original batch file name - " + batch.getOriginalFileName()), "Load .hr Batch Failed");
			}
			else {
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("HL7toTxt successfully parsed the inbound file and generated file location/name: " + decodedFilePath + newfilename);
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			}
			
			actualFileName = (decodedFilePath + newfilename);
			//we remove temp load file 
			File tempLoadFile = new File(decodedFilePath + processFileName);
			if (tempLoadFile.exists()) {
			    tempLoadFile.delete();
			}
			
			
		    } 
		    else if (processFileName.endsWith(".xlsx") || processFileName.endsWith(".xls")) {
			
			if (processFileName.endsWith(".xlsx")) {
			    newfilename = exceltotxt.TranslateXLSXtoTxt(decodedFilePath, decodedFileName, batch);
			} 
			else if (processFileName.endsWith(".xls")) {
			    newfilename = xlstotxt.TranslateXLStoTxt(decodedFilePath, decodedFileName, batch);
			}
			
			if (newfilename.equals("ERRORERRORERROR")) {
			    
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("Error translating xlsx / xls file");
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			    
			    updateBatchStatus(batchId, 39, "endDateTime");
			    insertProcessingError(5, null, batchId, null, null, null, null, false, false, "Error translating xlsx / xls file");
			    sendEmailToAdmin((new Date() + "<br/>Please login and review. Load batch failed.  <br/>Batch Id -  " + batch.getId() + "<br/> UT Batch Name " + batch.getUtBatchName() + " <br/>Original batch file name - " + batch.getOriginalFileName()), "Load Excel Batch Failed");
			} 
			else if (newfilename.equals("FILE IS NOT excel ERROR")) {
			    
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("File content is not valid for expected file type.. Excel format is invalid.");
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			    
			    updateBatchStatus(batchId, 7, "endDateTime");
			    insertProcessingError(22, null, batchId, null, null, null, null, false, false, "Excel format is invalid.");
			    sendEmailToAdmin((new Date() + "<br/>Please login and review. Load batch failed.  <br/>Batch Id -  " + batch.getId() + "<br/> UT Batch Name " + batch.getUtBatchName() + " <br/>Original batch file name - " + batch.getOriginalFileName()), "Load Excel Batch Failed");
			}
			else {
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("Successfully parsed the inbound Excel file and generated file location/name: " + decodedFilePath + newfilename);
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			}
			
			actualFileName = (decodedFilePath + newfilename);
			
			//we remove temp load file 
			File tempLoadFile = new File(decodedFilePath + processFileName);
			if (tempLoadFile.exists()) {
			    tempLoadFile.delete();
			}
			tempLoadFile = new File(decodedFilePath + decodedFileName + decodedFileExt);
			if (tempLoadFile.exists()) {
			    tempLoadFile.delete();
			}
			
		    } 
		    else if (processFileName.endsWith(".json")) {
			newfilename = jsontotxt.TranslateJSONtoTxt(decodedFilePath, decodedFileName, batch.getOrgId(), batch.getConfigId(), batchId);
			
			if (newfilename.equals("ERRORERRORERROR")) {
			    
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("Error at applying jar JSON parsing template");
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			    
			    updateBatchStatus(batchId, 39, "endDateTime");
			    insertProcessingError(5, null, batchId, null, null, null, null, false, false, "Error at applying jar template");
			} 
			else if (newfilename.equals("FILE IS NOT JSON ERROR")) {
			    
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("JSON format is invalid.");
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			    
			    updateBatchStatus(batchId, 7, "endDateTime");
			    insertProcessingError(22, null, batchId, null, null, null, null, false, false, "JSON format is invalid.");
			    sendEmailToAdmin((new Date() + "<br/>Please login and review. Load batch failed.  <br/>Batch Id -  " + batch.getId() + "<br/> UT Batch Name " + batch.getUtBatchName() + " <br/>Original batch file name - " + batch.getOriginalFileName()), "Load xml Batch Failed");
			}
			else {
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("Successfully parsed the inbound JSON file and generated file location/name: " + decodedFilePath + newfilename);
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			}

			actualFileName = (decodedFilePath + newfilename);
			
			//we remove temp load file 
			File tempLoadFile = new File(decodedFilePath + processFileName);
			if (tempLoadFile.exists()) {
			    tempLoadFile.delete();
			}
		    }

		    //at this point, hl7 and hr are in unencoded plain text
		    if (actualFileName.endsWith(".txt") || actualFileName.endsWith(".csv")) {
			String delimChar = "|";

			if (batch.getDelimChar() == null && delimId > 0) {
			    delimChar = messagetypemanager.getDelimiterChar(delimId);
			} else if ("".equals(batch.getDelimChar()) && delimId > 0) {
			    delimChar = messagetypemanager.getDelimiterChar(delimId);
			} else if (!"".equals(batch.getDelimChar())) {
			    delimChar = batch.getDelimChar();
			}

			if ("".equals(lineTerminator)) {
			    lineTerminator = "\\n";
			}
			
			configurationMessageSpecs configSpecs = configurationManager.getMessageSpecs(batch.getConfigId());
			
			Integer totalHeaderRows = 0;
			
			if(configSpecs != null) {
			    if(!"".equals(configSpecs.getTotalHeaderRows())) {
				totalHeaderRows = configSpecs.getTotalHeaderRows();
			    }
			}

			int errorHere = insertLoadData(batch.getId(), batch.getConfigId(), delimChar, actualFileName, "transactionInRecords_" + batch.getId(), batch.isContainsHeaderRow(), totalHeaderRows, lineTerminator);

			if (errorHere > 0) {
			    insertProcessingError(7, null, batchId, null, null, null, null, false, false, "insertLoadData, please login and check logs.");
			    try {
				sendEmailToAdmin(("load error for batch - " + batch.getOriginalFileName() + " - " + batch.getUtBatchName()), "insertLoadData Error");
			    } catch (Exception ex) {
				ex.printStackTrace();
			    }
			    sysErrors++;
			}

			//check how many records are loaded
			int numLoadTransactions = getLoadTransactionCount("transactionInRecords_" + batch.getId());
			if (numLoadTransactions < 1) {
			   
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("No records were found in table transactionInRecords_" + batchId);
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			    
			    //entire batch failed, we reject entire batch
			    updateBatchStatus(batchId, 39, "endDateTime");
			    //need to insert error on why we are rejecting
			    insertProcessingError(14, null, batchId, null, null, null, null, false, false, "No transactions were loaded into batch. Please check file and line terminator.");
			}
			else {
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("Loaded " + numLoadTransactions + " records from file: " + batch.getOriginalFileName());
			    ba.setBatchUploadId(batchId);
			    transactionInDAO.submitBatchActivityLog(ba);
			}

			File actualFile = new File(actualFileName);
		    }

		    //if excel files some files comes with random lines at the beginning and end, need to remove
		    if (processFileName.endsWith(".xlsx") || processFileName.endsWith(".xls")) {
			configurationExcelDetails excelDetails = configurationManager.getExcelDetails(batch.getConfigId(), batch.getOrgId());
			if (excelDetails != null) {
			    if (excelDetails.getStartRow() > 1) {
				deleteLoadTableRows(excelDetails.getStartRow() - 1, "asc", "transactionInRecords_" + batch.getId());
			    }
			    if (excelDetails.getDiscardLastRows() > 0) {
				deleteLoadTableRows(excelDetails.getDiscardLastRows(), " desc ", "transactionInRecords_" + batch.getId());
			    }
			}
		    }

		    //3. we update batchId, loadRecordId
		    //sysError = sysError + updateLoadTable(loadTableName, batch.getId());
		    //3.5 we delete blank rows
		    sysErrors = sysErrors + removeLoadTableBlankRows(batch.getId(), "transactionInRecords_" + batch.getId());
		    
		    //4. Check to see if we have a config, if not find it in the records
		    if (batch.getConfigId() == null || batch.getConfigId() == 0) {
			Integer foundConfigId = 0;
			String batchRecord = "";

			//1. we get all configs for user - user might not have permission to submit but someone else in org does
			List<configurationMessageSpecs> configurationMessageSpecs = configurationtransportmanager.getConfigurationMessageSpecsForOrgTransport(batch.getOrgId(), batch.getTransportMethodId(), false);
			
			if(configurationMessageSpecs != null) {
			    if(!configurationMessageSpecs.isEmpty()) {
				for(configurationMessageSpecs messageSpec : configurationMessageSpecs) {
				    if(messageSpec.getmessageTypeCol() > 0 && !"".equals(messageSpec.getmessageTypeVal())) {
					
					//Pull the first record for the batch
					String recordVal = transactionInDAO.getFieldValue("transactioninrecords_"+batch.getId(),"F"+messageSpec.getmessageTypeCol(), "batchUploadId", batch.getId());
					if(recordVal.trim().toLowerCase().equals(messageSpec.getmessageTypeVal().trim().toLowerCase())) {
					    foundConfigId = messageSpec.getconfigId();
					    break;
					}
				    }
				}
			    }
			    else {
				//log batch activity
				ba = new batchuploadactivity();
				ba.setActivity("No valid configurations were found for loading batch.");
				ba.setBatchUploadId(batchId);
				transactionInDAO.submitBatchActivityLog(ba);
				
				insertProcessingError(6, null, batchId, null, null, null, null, false, false, "No valid configurations were found for loading batch.");
				updateBatchStatus(batchId, 7, "endDateTime");
			    }
			}
			else {
			   //log batch activity
			   ba = new batchuploadactivity();
			   ba.setActivity("No valid configurations were found for loading batch.");
			   ba.setBatchUploadId(batchId);
			   transactionInDAO.submitBatchActivityLog(ba);

			   insertProcessingError(6, null, batchId, null, null, null, null, false, false, "No valid configurations were found for loading batch."); 
			   updateBatchStatus(batchId, 7, "endDateTime");
			}
			
			if(foundConfigId == 0) {
			   //log batch activity
			   ba = new batchuploadactivity();
			   ba.setActivity("No valid configurations were found for loading batch.");
			   ba.setBatchUploadId(batchId);
			   transactionInDAO.submitBatchActivityLog(ba);
			   
			   insertProcessingError(6, null, batchId, null, null, null, null, false, false, "No valid configurations were found for loading batch."); 
			   updateBatchStatus(batchId, 7, "endDateTime");
			}
			else {
			    batch.setConfigId(foundConfigId);
			    transactionInDAO.updateBatchUpload(batch);
			}
		    }
		    
		    if(batch.getConfigId() > 0) {

			//we populate transactionTranslatedIn
			sysErrors = sysErrors + loadTransactionTranslatedIn(batchId, batch.getConfigId());

			// we trim all values
			trimFieldValues(batchId, false, batch.getConfigId(), true);
			ba = new batchuploadactivity();
			ba.setActivity("All batch entries were trimmed for batchId: " + batchId);
			ba.setBatchUploadId(batchId);
			transactionInDAO.submitBatchActivityLog(ba);

			//now that we have our config, we will apply pre-processing cw and macros to manipulate our data
			//1. find all configs for batch, loop and process
			List<Integer> configIds = getConfigIdsForBatchOnly(batchId);

			for (Integer configId : configIds) {
			    //we need to run all checks before insert regardless 
			    //we are reordering 1. cw/macro, 2. required and 3. validate 
			    // 1. grab the configurationDataTranslations and run cw/macros
			    List<configurationDataTranslations> dataTranslations = configurationManager.getDataTranslationsWithFieldNo(configId, 2); //pre processing
			    Integer crosswalkErrors = 0;
			    Integer macroError = 0;
			    String macroName = "";
			    
			    for (configurationDataTranslations cdt : dataTranslations) {
				crosswalkErrors = 0;
				macroError = 0;
				
				if (cdt.getCrosswalkId() != 0) {
				    
				    crosswalkErrors = processCrosswalk(configId, batchId, cdt, false);
			    
				    if(crosswalkErrors == 9999999) {
					sysErrors++; 
				    }
				    else if(crosswalkErrors > 0) {
					//log batch activity
					ba = new batchuploadactivity();
					ba.setActivity("Crosswalk Error. CWId:" + cdt.getCrosswalkId() + " for configId:" + batch.getConfigId() + " total records with CW error: " + crosswalkErrors);
					ba.setBatchUploadId(batchId);
					transactionInDAO.submitBatchActivityLog(ba);
				    }
				    
				} else if (cdt.getMacroId() != 0) {
				    macroName = "";
				    macroError = processMacro(configId, batchId, cdt, false);

				    if(macroError == 9999999) {
					sysErrors++; 
				    }
				    else if(macroError > 0) {
					if(!macroList.isEmpty()) {
					    for(Macros macro : macroList) {
						if(macro.getId() == cdt.getMacroId()) {
						    macroName = macro.getMacroName().trim();
						}
					    }
					}

					//log batch activity
					ba = new batchuploadactivity();
					ba.setActivity("Macro Error. macro: " + macroName + " macroId: " + cdt.getMacroId() + " for configId:" + batch.getConfigId() + " total records with Macro error: " + macroError);
					ba.setBatchUploadId(batchId);
					transactionInDAO.submitBatchActivityLog(ba);
				    }
				}
			    }
			}
		    }
		}
		
		if (sysErrors > 0) {
		    insertProcessingError(processingSysErrorId, null, batchId, null, null, null, null, false, false, errorMessage);
		    updateBatchStatus(batchId, 39, "endDateTime");
		}

		//we check handling here for rejecting entire batch
		List<configurationTransport> batchHandling = getHandlingDetailsByBatch(batchId);
		
		// if entire batch failed and have no configIds, there will be no error handling found
		if (getRecordCounts(batchId, Arrays.asList(11), false) == getRecordCounts(batchId, new ArrayList<>(), false)) {
		    //entire batch failed, we reject entire batch
		    updateRecordCounts(batchId, errorStatusIds, false, "errorRecordCount");
		    updateRecordCounts(batchId, new ArrayList<Integer>(), false, "totalRecordCount");
		    updateBatchStatus(batchId, 7, "endDateTime");
		    //need to insert error on why we are rejecting
		    insertProcessingError(7, null, batchId, null, null, null, null, false, false, "No valid configurations were found for batch.");
		    
		    //log batch activity
		    ba = new batchuploadactivity();
		    ba.setActivity("No valid configurations were found for batch.");
		    ba.setBatchUploadId(batchId);
		    transactionInDAO.submitBatchActivityLog(ba);
		} 
		else if (batchHandling.size() != 1) {
		    //TODO email admin to fix problem
		    insertProcessingError(8, null, batchId, null, null, null, null, false, false, "Multiple or no file handling found, please check auto-release and error handling configurations");
		    updateRecordCounts(batchId, new ArrayList<Integer>(), false, "totalRecordCount");
		    // do we count pass records as errors?
		    updateRecordCounts(batchId, errorStatusIds, false, "errorRecordCount");
		    updateBatchStatus(batchId, 39, "endDateTime");
		    
		    //log batch activity
		    ba = new batchuploadactivity();
		    ba.setActivity("Multiple or no file handling found, please check auto-release and error handling configurations");
		    ba.setBatchUploadId(batchId);
		    transactionInDAO.submitBatchActivityLog(ba);
		}
		
		if (batchHandling.size() == 1) {
		    //reject submission on error
		    if (batchHandling.get(0).geterrorHandling() == 3) {
			// at this point we will only have invalid records
			if (getRecordCounts(batchId, errorStatusIds, false) > 0) {
			    updateBatchStatus(batchId, 7, "endDateTime");
			    updateRecordCounts(batchId, errorStatusIds, false, "errorRecordCount");
			}
		    }
		}

		updateRecordCounts(batchId, errorStatusIds, false, "errorRecordCount");
		updateRecordCounts(batchId, new ArrayList<>(), false, "totalRecordCount");
		batchStatusId = 43; //loaded without targets 
		
	    } catch (Exception ex) {
		insertProcessingError(processingSysErrorId, null, batchId, null, null, null, null, false, false, ("loadBatch method error " + ex.getMessage()));
		batchStatusId = 39;
		
		//log batch activity
		batchuploadactivity ba = new batchuploadactivity();
		ba.setActivity("loadBatch method error " + ex.getMessage());
		ba.setBatchUploadId(batchId);
		transactionInDAO.submitBatchActivityLog(ba);
	   }

	    try {
		updateBatchStatus(batchId, batchStatusId, "");
		updateRecordCounts(batchId, new ArrayList<>(), false, "totalRecordCount");
		// do we count pass records as errors?
		updateRecordCounts(batchId, errorStatusIds, false, "errorRecordCount");
		
		//log batch activity
		batchuploadactivity ba = new batchuploadactivity();
		ba.setActivity("Uploaded batchId:"+batchId+" status was set to " + batchStatusId);
		ba.setBatchUploadId(batchId);
		transactionInDAO.submitBatchActivityLog(ba);
		
	    } catch (Exception ex1) {
		Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ("loadBatch error at updating batch status - " + ex1));
	    }
	}
    }

    /**
     * We should only be loading up mass batches every 10 mins
     */
    @Override
    public void processMassBatches() throws Exception {

	// we only want to process one mass batches at a time but don't want to set the scheduler to be 1.5 hours each time a file runs
	//we check to see if anything is running first
	boolean run = true;
	List<batchUploads> batchInProcess = getBatchesByStatusIds(Arrays.asList(4));
	
	//we check time stamp to see how long that file has been processing get the details
	if (!batchInProcess.isEmpty()) {
	    LocalDateTime d1;
	    LocalDateTime d2;
	    long diffHours;

	    for (batchUploads runningBatches : batchInProcess) {
		batchUploads stuckBatchDetails = getBatchDetails(runningBatches.getId());

		if (stuckBatchDetails != null) {

		    d1 = LocalDateTime.ofInstant(stuckBatchDetails.getStartDateTime().toInstant(), ZoneId.systemDefault());
		    d2 = LocalDateTime.now();

		    diffHours = java.time.Duration.between(d1, d2).toHours();

		    if (diffHours >= 3) {
			
			try {
			    //log user activity
			    batchuploadactivity ba = new batchuploadactivity();
			    ba.setActivity("System Set Batch (id: " + stuckBatchDetails.getId() + ") to Status 58 - Processing");
			    ba.setBatchUploadId(stuckBatchDetails.getId());
			    transactionInDAO.submitBatchActivityLog(ba);
			} catch (Exception ex) {
			    ex.printStackTrace();
			    System.err.println("System Set Batch (id: " + stuckBatchDetails.getId() + ") To Retry insert user log error " + ex.toString());
			}
			String subject = "Batch set to Need to Review - processMassBatches ";
			String msgBody = "Batch " + stuckBatchDetails.getId() + " (" + stuckBatchDetails.getUtBatchName() + ") needs to be reviewed.";
			updateBatchStatus(stuckBatchDetails.getId(), 58, "endDateTime");
			
			//we notify admin
			//we also notify admin
			mailMessage mail = new mailMessage();
			mail.settoEmailAddress(myProps.getProperty("admin.email"));
			mail.setfromEmailAddress("support@health-e-link.net");
			mail.setmessageSubject(subject + " " + myProps.getProperty("server.identity"));
			StringBuilder emailBody = new StringBuilder();
			emailBody.append("<br/>Current Time " + d2.toString());
			emailBody.append("<br/><br/>" + msgBody + "<br/>File Name is  - " + stuckBatchDetails.getOriginalFileName() + ".");
			emailBody.append("<br/>" + batchInProcess.size() + " batch(es) with status 4 in queue.<br/>");
			mail.setmessageBody(emailBody.toString());
			emailManager.sendEmail(mail);
		    }

		}
	    }
	}

	if (run) {
	    //0. grab all mass batches with MSL (43)
	    try {

		List<batchUploads> batches = getBatchesByStatusIds(Arrays.asList(43));
		
		if (batches != null && batches.size() != 0) {
		    //Parallel processing of batches
		    for (batchUploads batch : batches) {
			executor.execute(new Runnable() {
			    @Override
			    public void run() {
				try {
				    processBatch(batch.getId(), false);
				} catch (Exception ex) {
				    Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			    }
			});
		    }
		    //OLD WAY
		    //processBatch(batches.get(0).getId(), false, 0);
		    //we process one file at a time

		}
	    } catch (Exception ex1) {
		Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ("processMassBatches error - " + ex1));
	    }
	}
    }
    
    /**
     * We will take a batch and then from its status etc we will decide if we want to process transactions or not.This method allowa admin to run just one batch This assumes batches SR - 6, Trans status REL We still run through entire process but these records should pass... (check to make sure it aligns with file upload) just be applying Macros / CW and inserting into our message tables This method will only process a batch that is RP or SSL

 We added to this method as if a batch is being call from fixErrors (ERG Form), we do not clear errors in transactionInErrors table. We default the flag to false as when it is call from old methods, we

 *
     * @param batchUploadId
     * @param doNotClearErrors
     * @return
     * @throws java.lang.Exception
     */
    @Override
    public boolean processBatch(int batchUploadId, boolean doNotClearErrors) throws Exception {

	Integer batchStatusId = 29;
	List<Integer> errorStatusIds = Arrays.asList(11, 13, 14, 16);
	
	//Get a full list of macros
	List<Macros> macroList = configurationManager.getMacros();
	
	//get batch details
	batchUploads batch = getBatchDetails(batchUploadId);

	if (batch.getOrgName() == null || "".equals(batch.getOrgName())) {
	    Organization sendingOrgDetails = organizationmanager.getOrganizationById(batch.getOrgId());
	    batch.setOrgName(sendingOrgDetails.getOrgName());
	}

	List<configurationConnection> batchTargetList = null;

	//this should be the same point of both ERG and Uploaded File *
	Integer systemErrorCount = 0;
	// Check to make sure the file is valid for processing, valid file is a batch with SSL (3) or SR (6)*

	boolean insertTargets = false;
	
	/**
	* batches get processed again when user hits release button, maybe have separate method call for those that are just going 
	* from pending release to release, have to think about scenario when upload file is huge 
	 */
	List<configurationTransport> handlingDetails = getHandlingDetailsByBatch(batchUploadId);

	// we should only insert for batches that are just loaded
	if (batch.getStatusId() == 36 || batch.getStatusId() == 43) {
	    insertTargets = true;
	}
	
	batchUploads updatedBatchDetails = getBatchDetails(batchUploadId);
	
	if ((batch.getStatusId() == 3 || batch.getStatusId() == 6 || batch.getStatusId() == 36 || batch.getStatusId() == 43)) {

	    //insert log
	    try {
		
		//log batch activity
		batchuploadactivity ba = new batchuploadactivity();
		ba.setActivity("Scheduled job processBatch was called to load the contents for this batch.");
		ba.setBatchUploadId(batchUploadId);
		transactionInDAO.submitBatchActivityLog(ba);
	    } catch (Exception ex) {
		ex.printStackTrace();
		System.err.println("transactionId - insert user log error for batch " + batchUploadId + " " + ex.toString());
	    }

	    // set batch to SBP - 4*
	    updateBatchStatus(batchUploadId, 4, "");
	    
	    //log batch activity
	    batchuploadactivity ba = new batchuploadactivity();
	    ba.setActivity("Uploaded batchId:" + batchUploadId + " status was set to 4");
	    ba.setBatchUploadId(batchUploadId);
	    transactionInDAO.submitBatchActivityLog(ba);

	    //clear transactionInError table for batch, if do not clear errors is true, we skip this.
	    if (!doNotClearErrors) {
		cleanAuditErrorTable(batchUploadId);
		
		//log batch activity
		ba = new batchuploadactivity();
		ba.setActivity("Cleared the audit tables for batchId:" + batchUploadId);
		ba.setBatchUploadId(batchUploadId);
		transactionInDAO.submitBatchActivityLog(ba);
	    }
	    
	    //we need to run all checks before insert regardless 
	    //1. required
	    //2. cw/macro 
	    //3. validate 
	    
	    //Step 1: check R/O
	    List<configurationFormFields> reqFields = getRequiredFieldsForConfig(batch.getConfigId());
	    
	    if(reqFields != null) {
		if(!reqFields.isEmpty()) {
		    Integer reqError = 0;
		    
		    for (configurationFormFields cff : reqFields) {
			reqError = insertFailedRequiredFields(cff, batchUploadId);
			
			if(reqError == 9999999) {
			   systemErrorCount++; 
			}
			else if(reqError > 0) {
			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("Required Field Error. Field No:" + cff.getFieldNo() + " Field Desc:" + cff.getFieldDesc() + " for configId:" + batch.getConfigId());
			    ba.setBatchUploadId(batchUploadId);
			    transactionInDAO.submitBatchActivityLog(ba);
			}
		    }
		}
	    }
	    
	    // update status of the failed records to ERR - 14 (Only updating REQUIRED records from transactioninerrors)
	    updateStatusForErrorTrans(batchUploadId, 14, false);
	    
	    //Step 2: grab the configurationDataTranslations and run cw/macros
	    List<configurationDataTranslations> dataTranslations = configurationManager.getDataTranslationsWithFieldNo(batch.getConfigId(), 1);
	    
	    if(dataTranslations != null) {
		if(!dataTranslations.isEmpty()) {
		    Integer crosswalkErrors = 0;
		    Integer macroError = 0;
		    String macroName = "";
		    
		    for (configurationDataTranslations cdt : dataTranslations) {
			crosswalkErrors = 0;
			macroError = 0;
			
			if (cdt.getCrosswalkId() != 0) {
			    crosswalkErrors = processCrosswalk(batch.getConfigId(), batchUploadId, cdt, false);
			    
			    if(crosswalkErrors == 9999999) {
				systemErrorCount++; 
			    }
			    else if(crosswalkErrors > 0) {
				//log batch activity
				ba = new batchuploadactivity();
				ba.setActivity("Crosswalk Error. CWId:" + cdt.getCrosswalkId() + " for configId:" + batch.getConfigId() + " total records with CW error: " + crosswalkErrors);
				ba.setBatchUploadId(batchUploadId);
				transactionInDAO.submitBatchActivityLog(ba);
			    }
			} 
			else if (cdt.getMacroId() != 0) {
			    macroName = "";
			    macroError = processMacro(batch.getConfigId(), batchUploadId, cdt, false);
			    
			    if(macroError == 9999999) {
				systemErrorCount++; 
			    }
			    else if(macroError > 0) {
				if(!macroList.isEmpty()) {
				    for(Macros macro : macroList) {
					if(macro.getId() == cdt.getMacroId()) {
					    macroName = macro.getMacroName().trim();
					}
				    }
				}
				
				//log batch activity
				ba = new batchuploadactivity();
				ba.setActivity("Macro Error. macro: " + macroName + " macroId: " + cdt.getMacroId() + " for configId:" + batch.getConfigId() + " total records with Macro error: " + macroError);
				ba.setBatchUploadId(batchUploadId);
				transactionInDAO.submitBatchActivityLog(ba);
			    }
			}
		    }
		}
	    }
	    
	    //Trim all field values again
	    trimFieldValues(batchUploadId, false, batch.getConfigId(), true);
	    ba = new batchuploadactivity();
	    ba.setActivity("All final batch entries were trimmed for batchId: " + batchUploadId);
	    ba.setBatchUploadId(batchUploadId);
	    transactionInDAO.submitBatchActivityLog(ba);
	   
	    //Step 3: Check validation errors
	    Integer validationErrors = runValidations(batchUploadId, batch.getConfigId());
	    
	    // update status of the failed records to ERR - 14 (Only updating REQUIRED records from transactioninerrors)
	    updateStatusForErrorTrans(batchUploadId, 14, false);

	    /**
	     * targets should only be inserted if it hasn't gone through this loop already *
	     */
	    if (insertTargets) {

		batchTargetList = getBatchTargets(batchUploadId, true);

		int sourceConfigId = 0;
		
		if (batchTargetList.isEmpty()) {
		    //log batch activity
		    ba = new batchuploadactivity();
		    ba.setActivity("No valid connections were found for loading batch.");
		    ba.setBatchUploadId(batchUploadId);
		    transactionInDAO.submitBatchActivityLog(ba);

		    insertProcessingError(10, null, batchUploadId, null, null, null, null, false, false, "No valid connections were found for loading batch.");
		    updateRecordCounts(batchUploadId, new ArrayList<>(), false, "errorRecordCount");
		    updateRecordCounts(batchUploadId, new ArrayList<>(), false, "totalRecordCount");
		    updateBatchStatus(batchUploadId, 7, "endDateTime");
		    return false;
		}

		//Check to make sure all returned targets match the config of the uploaded batch
		Integer checkTargets = 0;
		for (configurationConnection bt : batchTargetList) {
		    if (bt.getsourceConfigId() != sourceConfigId) {
			sourceConfigId = bt.getsourceConfigId();

			if (bt.getTargetOrgCol() != 0) {
			    checkTargets = rejectInvalidTargetOrg(batchUploadId, bt);
			    if(checkTargets == 9999999) {
				systemErrorCount++; 
			    }
			}
		    }
		}
	    }

	    //we apply post processing rules here - categoryId 3
	    //1. we loop it by config
	    List<configurationDataTranslations> postDataTranslations = configurationManager.getDataTranslationsWithFieldNo(batch.getConfigId(), 3);
	    
	    if(postDataTranslations != null) {
		if(!postDataTranslations.isEmpty()) {
		    Integer postMacroError = 0;
		    String macroName = "";
		    
		    for (configurationDataTranslations cdt : postDataTranslations) {
			macroName = "";
			postMacroError = processMacro(batch.getConfigId(), batchUploadId, cdt, false);
			    
			if(postMacroError == 9999999) {
			    systemErrorCount++; 
			}
			else if(postMacroError > 0) {
			    if(!macroList.isEmpty()) {
				for(Macros macro : macroList) {
				    if(macro.getId() == cdt.getMacroId()) {
					macroName = macro.getMacroName().trim();
				    }
				}
			    }

			    //log batch activity
			    ba = new batchuploadactivity();
			    ba.setActivity("Post Macro Error. macro: " + macroName + " macroId: " + cdt.getMacroId() + " for configId:" + batch.getConfigId() + " total records with Macro error: " + postMacroError);
			    ba.setBatchUploadId(batchUploadId);
			    transactionInDAO.submitBatchActivityLog(ba);
			}
		    }
		}
	    }
	    

	    // if there are errors, those are system errors, they will be logged we get errorId 5 and email to admin, update batch to 29 *
	    if (systemErrorCount > 0) {
		setBatchToError(batchUploadId, "System error occurred during processBatch, please review errors in audit report");
		
		//clean
		cleanAuditErrorTable(batch.getId());

		//populate
		populateAuditReport(batch.getId(), configurationManager.getMessageSpecs(batch.getConfigId()));
		
		//populate dropped values 
		populateDroppedValues(batch.getId(), batch.getConfigId(), false);
		
		updateRecordCounts(batchUploadId, new ArrayList<Integer>(), false, "totalRecordCount");

		updateRecordCounts(batchUploadId, errorStatusIds, false, "errorRecordCount");
		
		//log batch activity
		ba = new batchuploadactivity();
		ba.setActivity("Populated the batch audit report table of any errors found for batchId:" + batchUploadId);
		ba.setBatchUploadId(batchUploadId);
		transactionInDAO.submitBatchActivityLog(ba);
		
		return false;
	    }

	    //1 = Post errors to ERG 
	    //2 = Reject record on error 
	    //3 = Reject submission on error 4 = Pass through errors
	    if (getRecordCounts(batchUploadId, finalStatusIds, false, false) > 0 && batch.getStatusId() == 6) {
		//we stop here as batch is not in final status and release batch was triggered
		batch.setStatusId(5);
		batchStatusId = 5;
		updateRecordCounts(batchUploadId, new ArrayList<Integer>(), false, "totalRecordCount");
		updateRecordCounts(batchUploadId, errorStatusIds, false, "errorRecordCount");
		updateBatchStatus(batchUploadId, batchStatusId, "endDateTime");
		return true;
	    }

	    // if auto and batch contains transactions that are not final status
	    if (batch.getStatusId() == 6 || (handlingDetails.get(0).getautoRelease() && (handlingDetails.get(0).geterrorHandling() == 2 || handlingDetails.get(0).geterrorHandling() == 4 || handlingDetails.get(0).geterrorHandling() == 3))) {

		//run check to make sure we have records 
		if (getRecordCounts(batchUploadId, Arrays.asList(12), false, true) > 0) {
		    updateRecordCounts(batchUploadId, new ArrayList<Integer>(), false, "totalRecordCount");
		    //do we count pass records as errors?
		    updateRecordCounts(batchUploadId, errorStatusIds, false, "errorRecordCount");
		    updateBatchStatus(batchUploadId, 29, "endDateTime");

		}
		batchStatusId = 24;

	    } 
	    else if (!handlingDetails.get(0).getautoRelease()) { //manual release
		//transaction will be set to saved, batch will be set to RP
		batchStatusId = 5;
		//we leave status alone as we already set them
	    } 

	    updateRecordCounts(batchUploadId, new ArrayList<Integer>(), false, "totalRecordCount");

	    updateRecordCounts(batchUploadId, errorStatusIds, false, "errorRecordCount");
	    
	    // Insert all Targets if batch status = 24
	    boolean targetsInserted = false;
	    boolean noTargetsFound = false;

	    updatedBatchDetails = getBatchDetails(batchUploadId);

	    //clean
	    cleanAuditErrorTable(batch.getId());

	    //log batch activity
	    ba = new batchuploadactivity();
	    ba.setActivity("Clean Audit Error table for batchId:" + batchUploadId);
	    ba.setBatchUploadId(batchUploadId);
	    transactionInDAO.submitBatchActivityLog(ba);

	    //populate the audit report if errors were found
	    if(updatedBatchDetails.getErrorRecordCount() > 0) {

		//log batch activity
		ba = new batchuploadactivity();
		ba.setActivity("Populate Audit Error table for batchId:" + batchUploadId);
		ba.setBatchUploadId(batchUploadId);
		transactionInDAO.submitBatchActivityLog(ba);

		populateAuditReport(batch.getId(), configurationManager.getMessageSpecs(batch.getConfigId()));

		//log batch activity
		ba = new batchuploadactivity();
		ba.setActivity("Audit Error table fully populated for batchId:" + batchUploadId);
		ba.setBatchUploadId(batchUploadId);
		transactionInDAO.submitBatchActivityLog(ba);
	    }

	    //populate dropped values
	    populateDroppedValues(batch.getId(), batch.getConfigId(), false);

	    //log batch activity
	    ba = new batchuploadactivity();
	    ba.setActivity("Populate Dropped Values for batchId:" + batchUploadId);
	    ba.setBatchUploadId(batchUploadId);
	    transactionInDAO.submitBatchActivityLog(ba);

	    updateBatchStatus(batchUploadId, batchStatusId, "");

	    //log batch activity
	    ba = new batchuploadactivity();
	    ba.setActivity("Uploaded batchId:" + batchUploadId + " status was set to " + batchStatusId);
	    ba.setBatchUploadId(batchUploadId);
	    transactionInDAO.submitBatchActivityLog(ba);

	    //we finish processing, we need to alert admin if there are any records there are rejected
	    //we check batch to see if the batch has any rejected records. if it does, we send an email to notify reject.email in properties file
	    updatedBatchDetails = getBatchDetails(batchUploadId);

	    if (updatedBatchDetails.getErrorRecordCount() > 0) {
		sendRejectNotification(updatedBatchDetails);
	    }

	    if (updatedBatchDetails.getStatusId() == 24) {

		//If no errors found then create the batch download entries
		if(updatedBatchDetails.getErrorRecordCount() == 0) {
		    targetsInserted = true;
		    noTargetsFound = assignBatchDLId(batchUploadId, batch.getConfigId());
		}
		else {
		    Integer totalErrorRows = 0;

		    try {
			totalErrorRows = transactionInDAO.getTotalErroredRows(batchUploadId);
		    }
		    catch (Exception ex) {}

		    //if errors are found and the configuration is not set to "Reject entire file on a single transaction error" then create the batch download entry.
		    if(totalErrorRows > 0 && handlingDetails.get(0).geterrorHandling() == 3) {
			batchStatusId = 7;
			updateBatchStatus(batchUploadId, batchStatusId, "endDateTime");

			//log batch activity
			ba = new batchuploadactivity();
			ba.setActivity("BatchId:" + batchUploadId + " was rejected because error handling is set to 'Reject entire file on a single transaction error' and has a total of " + updatedBatchDetails.getErrorRecordCount() + " errors.");
			ba.setBatchUploadId(batchUploadId);
			transactionInDAO.submitBatchActivityLog(ba);
		    }
		    else {
			targetsInserted = true;
			noTargetsFound = assignBatchDLId(batchUploadId, batch.getConfigId());
		    }
		}
	    }

	    //If no targets were found
	    if(targetsInserted && !noTargetsFound) {
		//log batch activity
		ba = new batchuploadactivity();
		ba.setActivity("No valid connections were found for loading batch.");
		ba.setBatchUploadId(batchUploadId);
		transactionInDAO.submitBatchActivityLog(ba);

		insertProcessingError(10, null, batchUploadId, null, null, null, null, false, false, "No valid connections were found for loading batch.");
		updateRecordCounts(batchUploadId, new ArrayList<Integer>(), false, "errorRecordCount");
		updateRecordCounts(batchUploadId, new ArrayList<Integer>(), false, "totalRecordCount");
		updateBatchStatus(batchUploadId, 7, "endDateTime");

		return false;
	    }

	    if (batchStatusId == 24) {

		 Organization orgDetails = organizationmanager.getOrganizationById(batch.getOrgId());

		//Clear some files that are no longer needed
		//File in Load Files
		File fileToDelete = new File(myProps.getProperty("ut.directory.utRootDir") + "loadFiles/" + batch.getUtBatchName() + batch.getOriginalFileName().substring(batch.getOriginalFileName().lastIndexOf(".")).toLowerCase());

		if (fileToDelete.exists()) {
		    //log batch activity
		    ba = new batchuploadactivity();
		    ba.setActivity("Deleted file: " + fileToDelete.getAbsolutePath());
		    ba.setBatchUploadId(batchUploadId);
		    transactionInDAO.submitBatchActivityLog(ba);

		    fileToDelete.delete();
		}

		//Input File
		fileToDelete = new File(myProps.getProperty("ut.directory.utRootDir") + orgDetails.getCleanURL() + "/input files/" + batch.getUtBatchName() + batch.getOriginalFileName().substring(batch.getOriginalFileName().lastIndexOf(".")).toLowerCase());

		if (fileToDelete.exists()) {
		    //log batch activity
		    ba = new batchuploadactivity();
		    ba.setActivity("Deleted file: " + fileToDelete.getAbsolutePath());
		    ba.setBatchUploadId(batchUploadId);
		    transactionInDAO.submitBatchActivityLog(ba);

		    fileToDelete.delete();
		}
	    }
	    

	} // end of single batch insert 

	

	return true;
    }

    @Override
    public List<CrosswalkData> getCrosswalkDataForBatch(configurationDataTranslations cdt, Integer batchId, boolean foroutboundProcessing) throws Exception {
	return transactionInDAO.getCrosswalkDataForBatch(cdt, batchId, foroutboundProcessing);
    }

    @Override
    public List<referralActivityExports> getReferralActivityExportsByStatus(
	    List<Integer> statusIds, Integer howMany) throws Exception {
	return transactionInDAO.getReferralActivityExportsByStatus(statusIds, howMany);
    }

    @Override
    public void updateReferralActivityExport(referralActivityExports activityExport) throws Exception {
	transactionInDAO.updateReferralActivityExport(activityExport);
    }

    @Override
    public void sendExportEmail(utUser userDetails) throws Exception {
	String exportMessage = "Dear " + userDetails.getFirstName() + ", <br/>Please login to download your referral activity export.  Thank you.";
	mailMessage mail = new mailMessage();
	mail.setfromEmailAddress("support@health-e-link.net");
	mail.setmessageBody(exportMessage);
	mail.setmessageSubject("Referral activity export is ready to be downloaded.");
	mail.settoEmailAddress(userDetails.getEmail());
	emailManager.sendEmail(mail);
    }

    @Override
    public void saveReferralActivityExport(referralActivityExports activityExport) throws Exception {
	transactionInDAO.saveReferralActivityExport(activityExport);
    }

    @Override
    public List<referralActivityExports> getReferralActivityExportsWithUserNames(List<Integer> statusIds) throws Exception {
	return transactionInDAO.getReferralActivityExportsWithUserNames(statusIds);
    }

    @Override
    public referralActivityExports getReferralActivityExportById(
	    Integer exportId) throws Exception {
	return transactionInDAO.getReferralActivityExportById(exportId);
    }

    @Override
    public void populateAuditReport(Integer batchUploadId, configurationMessageSpecs cms) throws Exception {
    	transactionInDAO.populateAuditReport(batchUploadId, cms.getconfigId());
    }

    @Override
    public List<Integer> getErrorFieldNos(Integer batchUploadId)
	    throws Exception {
	return transactionInDAO.getErrorFieldNos(batchUploadId);
    }

    @Override
    public void populateFieldError(Integer batchUploadId, Integer fieldNo,
	    configurationMessageSpecs cms) throws Exception {
	transactionInDAO.populateFieldError(batchUploadId, fieldNo, cms);
    }

    @Override
    public void cleanAuditErrorTable(Integer batchUploadId) throws Exception {
	transactionInDAO.cleanAuditErrorTable(batchUploadId);
    }

    @Override
    public void deleteMoveFileLogsByStatus(Integer statusId, Integer transportMethodId) throws Exception {
	transactionInDAO.deleteMoveFileLogsByStatus(statusId, transportMethodId);
    }

    @Override
    public void deleteLoadTableRows(Integer howMany, String ascOrDesc,
	    String laodTableName) throws Exception {
	transactionInDAO.deleteLoadTableRows(howMany, ascOrDesc, laodTableName);

    }

    @Override
    public BigInteger getUserRejectedCount(Integer userId, Integer orgId, String fromDate, String toDate) throws Exception {
	return transactionInDAO.getUserRejectedCount(userId, orgId, fromDate, toDate);
    }

    @Override
    public List<batchErrorSummary> getBatchErrorSummary(int batchId, String inboundOutbound) throws Exception {
	return transactionInDAO.getBatchErrorSummary(batchId,inboundOutbound);
    }

    @Override
    public List getErrorDataBySQLStmt(String sqlStmt) throws Exception {
	return transactionInDAO.getErrorDataBySQLStmt(sqlStmt);
    }

    @Override
    public List getErrorReportField(Integer batchId) throws Exception {
	return transactionInDAO.getErrorReportField(batchId);
    }

    @Override
    public List<batchUploads> getBatchesByStatusIdsAndDate(Date fromDate, Date toDate, Integer fetchSize, List<Integer> statusIds) throws Exception {
	return transactionInDAO.getBatchesByStatusIdsAndDate(fromDate, toDate, fetchSize, statusIds);
    }

    @Override
    public batchRetry getBatchRetryByUploadId(Integer batchUploadId, Integer statusId)
	    throws Exception {
	return transactionInDAO.getBatchRetryByUploadId(batchUploadId, statusId);
    }

    @Override
    public void saveBatchRetry(batchRetry br) throws Exception {
	transactionInDAO.saveBatchRetry(br);
    }

    @Override
    public void clearBatchRetry(Integer batchUploadId) throws Exception {
	transactionInDAO.clearBatchRetry(batchUploadId);
    }

    @Override
    public Integer insertRestApiMessage(RestAPIMessagesIn newRestAPIMessage) throws Exception {

	Integer newRestAPIMesageId = 0;

	try {
	    newRestAPIMesageId = transactionInDAO.insertRestApiMessage(newRestAPIMessage);

	    return newRestAPIMesageId;
	} catch (Exception ex) {
	    return newRestAPIMesageId;
	}
    }

    /**
     * This method process rest api messages received. These are messages that has valid senders. 1 for not processed 2 processed - written as text file 3 for rejected 4 for while writing to text ifle processing
     *
     * It will gather the list and write to to proper folder as a text file then moveFile process will pick them up and process them using the same codes
     *
     **
     * @return
     */
    @Override
    public void processRestAPIMessages() {
	
	try {
	    List<RestAPIMessagesIn> RestAPIMessageList = getRestAPIMessagesByStatusId(Arrays.asList(1));
	    
	    if (RestAPIMessageList != null) {
		
		//Parallel processing of batches
		for (RestAPIMessagesIn RestAPIMessage : RestAPIMessageList) {
		    //we check to make sure again as time could have pass and it could have been processed already
		    RestAPIMessagesIn RestAPIRecheck = getRestAPIMessagesById(RestAPIMessage.getId());
		    if (RestAPIRecheck.getStatusId() == 1) {
			executor.execute(new Runnable() {
			    @Override
			    public void run() {
				try {
				    processRestAPIMessage(RestAPIRecheck);
				} catch (Exception ex) {
				    Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			    }
			});
		    }
		}
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("processRestAPIMessages " + ex.toString());
	    try {
		sendEmailToAdmin((ex.toString() + "<br/>" + Arrays.toString(ex.getStackTrace())), "Rest API Job Error - main method errored");
	    } catch (Exception ex1) {
		ex1.printStackTrace();
		System.err.println("processRestAPIMessages - can't send email for rest api service job error " + ex1.toString());
	    }
	}

    }

    @Override
    public List<RestAPIMessagesIn> getRestAPIMessagesByStatusId(List<Integer> statusIds) {
	return transactionInDAO.getRestAPIMessagesByStatusId(statusIds);
    }

    @Override
    public RestAPIMessagesIn getRestAPIMessagesById(Integer wsMessageId) {
	return transactionInDAO.getRestAPIMessagesById(wsMessageId);
    }

    /**
     * this will process each rest api message, it should be less intensive if we treat it like a file upload instead of file drop At the end of this, file should be written to input folder, it should be SSA or SRJ and logged
     *
     *
     * @param APIMessage
     * @return
     */
    public void processRestAPIMessage(RestAPIMessagesIn APIMessage) {
	
	try {
	    APIMessage.setStatusId(4);
	    updateRestAPIMessage(APIMessage);

	    configurationTransport ct = configurationtransportmanager.getTransportDetails(APIMessage.getConfigId());

	    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
	    Date date = new Date();
	    /* Create the batch name (TransportMethodId+OrgId+Date/Time/Seconds) */
	    String batchName = new StringBuilder().append("rest").append("_").append(APIMessage.getId()).append("_").append(APIMessage.getOrgId()).append(dateFormat.format(date)).toString();

	    Integer encodingId = 1;
	    Integer batchId = 0;
	    Integer errorId = 0;
	    Integer maxfileSize = 0;
	    Integer statusId = 0;
	    Integer configId = APIMessage.getConfigId();

	    batchUploads batchInfo = new batchUploads();
	    batchInfo.setOrgId(APIMessage.getOrgId());
	    batchInfo.setTransportMethodId(13);
	    batchInfo.setStatusId(4);
	    batchInfo.setStartDateTime(date);
	    batchInfo.setUtBatchName(batchName);
	    batchInfo.setConfigId(configId);
	    batchInfo.setOriginalFileName(APIMessage.getMessageTitle());

	    Organization orgDetails = organizationmanager.getOrganizationById(APIMessage.getOrgId());
	    String writeToFolder = myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/input files/";
	    String fileNamePath = writeToFolder + APIMessage.getMessageTitle();
	    
	    //set folder path
	    fileSystem dir = new fileSystem();
	    String writeToFile = fileNamePath;
	    
	    //File Drop directory
	    List<configurationFileDropFields> fileDropFields = configurationtransportmanager.getTransFileDropDetails(ct.getId());

	    String fileDropDir = orgDetails.getcleanURL() + "/input files/";

	    for(configurationFileDropFields dropField : fileDropFields){
		if(dropField.getMethod() == 1) {
		    fileDropDir = dropField.getDirectory().trim();
		}
	    }
	    
	    String restFile = myProps.getProperty("ut.directory.utRootDir") + fileDropDir;
	    batchInfo.setOriginalFolder(restFile);

	    //we reject
	    if (ct == null) {
		batchInfo.setUserId(usermanager.getUserByTypeByOrganization(APIMessage.getOrgId()).get(0).getId());
		batchInfo.setConfigId(0);
		batchInfo.setFileLocation(writeToFolder);
		batchInfo.setEncodingId(encodingId);
		//copy the file
		FileUtils.copyFile(new File(restFile+APIMessage.getMessageTitle()), new File(writeToFile));
		if (batchInfo.getConfigId() != 0 && batchInfo.getStatusId() == 2) {
		    batchInfo.setStatusId(42);
		}
		batchId = submitBatchUpload(batchInfo);
		//insert error
		errorId = 13;
		statusId = 7;

	    } else {

		//Get the utConfiguration details
		utConfiguration configDetails = configurationManager.getConfigurationById(ct.getconfigId());

		encodingId = ct.getEncodingId();
		String fileExt = "." + ct.getfileExt();
		writeToFolder = ct.getfileLocation();
		fileNamePath = myProps.getProperty("ut.directory.utRootDir") + writeToFolder + batchName + fileExt;
		String archivefileNamePath = myProps.getProperty("ut.directory.utRootDir") + writeToFolder +"encoded_" + batchName + fileExt;
		maxfileSize = ct.getmaxFileSize();

		batchInfo.setContainsHeaderRow(ct.getContainsHeaderRow());
		batchInfo.setDelimChar(ct.getDelimChar());
		batchInfo.setFileLocation(ct.getfileLocation());
		batchInfo.setEncodingId(encodingId);
		batchInfo.setUserId(0);

		//copy file
		writeToFile = fileNamePath;
		
		File encodedFile = new File(archivefileNamePath);
		
		File movedFile = new File(fileNamePath);
		
		FileUtils.moveFile(new File(restFile+APIMessage.getMessageTitle()),movedFile);

		String encodedOldFile = filemanager.encodeFileToBase64Binary(movedFile);
		filemanager.writeFile(encodedFile.getAbsolutePath(), encodedOldFile);
		
		if (statusId != 7) {
		    //decode and check delimiter
		    File file = new File(writeToFile);
		    
		    if (ct.getEncodingId() == 2 && filemanager.isFileBase64Encoded(file)) {
			//write to temp file
			String strDecode = filemanager.decodeFileToBase64Binary(file);
			file = new File(myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + batchName + "_dec" + fileExt);
			String decodeFilePath = myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + batchName + "_dec" + fileExt;
			filemanager.writeFile(decodeFilePath, strDecode);
			
			String encodeArchivePath = myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + batchName + fileExt;
			Files.copy(new File(writeToFile).toPath(), new File(encodeArchivePath).toPath(), REPLACE_EXISTING);
		    } 
		    else {
			String encodeArchivePath = myProps.getProperty("ut.directory.utRootDir") + "archivesIn/archive_" + batchName + fileExt;
			Files.copy(new File(writeToFile).toPath(), new File(encodeArchivePath).toPath(), REPLACE_EXISTING);
		    }

		    statusId = 2;
		    /**
		     * can't check delimiter for certain files, xml, hr etc *
		     */
		    if (fileExt.equalsIgnoreCase(".txt")) {
			
			String delimiter = "";
			
			if(!"".equals(ct.getDelimChar())) {
			   delimiter = (String) messageTypeDAO.getDelimiterChar(ct.getfileDelimiter());
			}
			else {
			    delimiter = ct.getDelimChar();
			}
			
			int delimCount = dir.checkFileDelimiter(new File(fileNamePath), delimiter);
			if (delimCount < 3) {
			    statusId = 7;
			    errorId = 15;
			}
		    }
		    //check file size
		    if (statusId == 2) {
			double uploadedFileSizeInBytes = file.length();
			double uploadedFileSizeInKB = (uploadedFileSizeInBytes / 1024);
			double uploadedFileSizeInMB = (uploadedFileSizeInKB / 1024);
			
			if (uploadedFileSizeInMB > maxfileSize) {
			    statusId = 7;
			    errorId = 12;
			}
		    }
		}

		batchInfo.setStatusId(statusId);
		batchInfo.setEndDateTime(new Date());
		batchInfo.setTotalRecordCount(1); // need to be at least one to show up in activites

		if (batchInfo.getConfigId() != 0 && batchInfo.getStatusId() == 2) {

		    //If utConfiguration is set for passthru
		    if (configDetails.getConfigurationType() == 2) {
			batchInfo.setStatusId(24);
			statusId = 24;
		    } 
		    else {
			statusId = 42;
			batchInfo.setStatusId(statusId);
		    }
		}

		batchId = submitBatchUpload(batchInfo);
		
		updateBatchStatus(batchId, statusId, "startDateTime");
		
		createBatchTables(batchId, configId);

		if (statusId != 2 && statusId != 42 && statusId != 24) {
		    insertProcessingError(errorId, 0, batchId, null, null, null, null, false, false, "");
		}

		//update message status to done
		APIMessage.setStatusId(2);
		APIMessage.setBatchUploadId(batchId);
		updateRestAPIMessage(APIMessage);
		
	    }

	    //insert log
	    try {
		//log batch activity
		batchuploadactivity ba = new batchuploadactivity();
		ba.setActivity("System Processed Rest API Message " + APIMessage.getId());
		ba.setBatchUploadId(batchInfo.getId());
		transactionInDAO.submitBatchActivityLog(ba);

	    } catch (Exception ex) {
		ex.printStackTrace();
		System.err.println("processRestAPIMessage - insert user log" + ex.toString());
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("processRestAPIMessage " + ex.toString());
	    try {
		sendEmailToAdmin((ex.toString() + "<br/>" + Arrays.toString(ex.getStackTrace())), "processRestAPIMessage");
	    } catch (Exception ex1) {
		ex1.printStackTrace();
		System.err.println("processRestAPIMessage " + ex1.toString());
	    }
	}
    }

    @Override
    public Integer updateRestAPIMessage(RestAPIMessagesIn APIMessage) {
	return transactionInDAO.updateRestAPIMessage(APIMessage);
    }

    @Override
    public void updateBatchClearAfterDeliveryByBatchUploadId(
	    Integer batchUploadId, Integer newStatusId) throws Exception {
	transactionInDAO.updateBatchClearAfterDeliveryByBatchUploadId(batchUploadId, newStatusId);
    }

    @Override
    public Integer clearBatchClearAfterDeliveryByBatchUploadId(
	    Integer batchUploadId) throws Exception {
	return transactionInDAO.clearBatchClearAfterDeliveryByBatchUploadId(batchUploadId);
    }

    @Override
    public boolean assignBatchDLId(Integer batchUploadId, Integer batchConfigId) throws Exception {
	
	boolean targetsInserted = false;

	// now we assign batchdownload Id to each config
	List<Integer> configIds = getTargetConfigsForUploadBatch(batchUploadId, batchConfigId);

	//we get upload details
	batchUploads batchUploadDetails = getBatchDetails(batchUploadId);

	//Need to check if any batchDownloads are set to reset and if so only update that single target
	//instead of creating new entries. In this scenario a single target has been reset and we do not
	//need to reprocess all targets.
	List<batchDownloads> pendingResetBatchDownloads = transactionoutmanager.getPendingResetBatches(batchUploadId);

	if (!pendingResetBatchDownloads.isEmpty()) {
	    targetsInserted = true;

	    for (batchDownloads pendingBatches : pendingResetBatchDownloads) {
		pendingBatches.setStatusId(61);
		pendingBatches.setTotalErrorCount(0);
		pendingBatches.setTotalRecordCount(0);
		//pendingBatches.setStartDateTime(new Date());

		transactionOutDAO.submitBatchDownloadChanges(pendingBatches);
	    }

	} 
	else {

	    //If reseting inbound batch we need to make sure all the targets are cleared and removed.
	    List<batchDownloads> batchDownloads = transactionOutDAO.getDLBatchesByBatchUploadId(batchUploadId);

	    if (!batchDownloads.isEmpty()) {
		transactionoutmanager.deleteBatchDownloadTablesByBatchUpload(batchUploadId);

		List<Integer> batchDLIDs = new ArrayList<>();
		for (batchDownloads batchDetails : batchDownloads) {
		    batchDLIDs.add(batchDetails.getId());
		}

		if (!batchDLIDs.isEmpty()) {
		    transactionInDAO.clearBatchDownloads(batchDLIDs);
		}
	    }
	    
	    Integer useTargetOrgId = 0;
	    boolean useTarget = false;

	    //we loop configs here 
	    for (Integer configId : configIds) {
		useTargetOrgId = 0;
		useTarget = false;

		// Create the batch name (OrgId+MessageTypeId+Date/Time) - need milliseconds as computer is fast and files have the same name
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
		Date date = new Date();

		//get transport & config details
		utConfiguration configDetails = configurationManager.getConfigurationById(configId);
		
		//get the target org details
		Organization targetOrg = organizationmanager.getOrganizationById(configDetails.getorgId());
		
		//We need to check to see if the configuration is looking for the target in specific column
		List<configurationMessageSpecs> configurationMessageSpecs = configurationtransportmanager.getConfigurationMessageSpecsForOrgTransport(batchUploadDetails.getOrgId(), batchUploadDetails.getTransportMethodId(), true);

		if(configurationMessageSpecs != null) {
		    if(!configurationMessageSpecs.isEmpty()) {
			configurationMessageSpecs messageSpec = configurationMessageSpecs.get(0);
			
			if(messageSpec.gettargetOrgCol() > 0 && !"".equals(messageSpec.gettargetOrgCol())) {
			    
			    //Pull the first record for the batch
			    String recordVal = transactionInDAO.getFieldValue("transactiontranslatedin_"+batchUploadId,"F"+messageSpec.gettargetOrgCol(), "batchUploadId", batchUploadId);
			    
			    if(recordVal.trim().toLowerCase().equals(String.valueOf(targetOrg.getId()))) {
				useTarget = true;
				useTargetOrgId = targetOrg.getId();
			    }
			    
			    //Check the HEL Registry OrgId
			    if(!useTarget) {
				recordVal = transactionInDAO.getFieldValue("transactioninrecords_"+batchUploadId,"F"+messageSpec.gettargetOrgCol(), "batchUploadId", batchUploadId);
				
				if(recordVal.trim().toLowerCase().equals(String.valueOf(targetOrg.getHelRegistryOrgId()))) {
				    useTarget = true;
				    useTargetOrgId = targetOrg.getId();
				}
			    }
			}
			else {
			    useTarget = true;
			    useTargetOrgId = targetOrg.getId();
			}
		    }
		}
		
		if(useTarget && useTargetOrgId > 0) {
		    targetsInserted = true;
		    
		    configurationTransport transportDetails = configurationtransportmanager.getTransportDetails(configDetails.getId());

		    String utbatchName = new StringBuilder().append(transportDetails.gettransportMethodId()).append("_m_").append(batchUploadId).append(configDetails.getorgId()).append(configDetails.getMessageTypeId()).append(dateFormat.format(date)).toString();

		    // Get the userId for the utConfiguration
		    List<configurationConnection> connections = configurationManager.getConnectionsBySrcAndTargetConfigurations(batchUploadDetails.getConfigId(), configDetails.getId());

		    int userId = 0;

		    //we create a batchDownloads
		    batchDownloads batchDownload = new batchDownloads();

		    //set batch download details
		    batchDownload.setUtBatchName(utbatchName);

		    //Need to get the schedule for the configuration to find out if the targets need to be processed automatically
		    configurationSchedules configurationScheduleDetails = configurationManager.getScheduleDetails(configId);

		    if(configurationScheduleDetails.gettype() == 5) {
		       batchDownload.setStatusId(61);
		    }
		    else if (configurationScheduleDetails.gettype() == 1) {
			batchDownload.setStatusId(64);
		    }
		    else {
			batchDownload.setStatusId(59);
		    }

		    //we determine output file name
		    batchDownload.setOutputFileName(transactionoutmanager.generateDLBatchName(utbatchName,transportDetails, configDetails, batchUploadDetails, date) + "." + transportDetails.getfileExt());
		    batchDownload.setMergeable(false);
		    //batchDownload.setStartDateTime(new Date());
		    batchDownload.setTransportMethodId(transportDetails.gettransportMethodId());
		    batchDownload.setOrgId(useTargetOrgId);
		    batchDownload.setUserId(userId);
		    batchDownload.setTotalErrorCount(0);
		    batchDownload.setTotalRecordCount(0);
		    batchDownload.setDeleted(false);
		    batchDownload.setDateCreated(new Date());
		    batchDownload.setBatchUploadId(batchUploadId);
		    batchDownload.setConfigId(configId);

		    /* Submit a new batch */
		    int batchDLId = (int) transactionOutDAO.submitBatchDownload(batchDownload);
		}
		else {
		    //No batches found
		}
	    }
	}
	
	return targetsInserted;

    }

    @Override
    public List<Integer> getTargetConfigsForUploadBatch(Integer batchUploadId, Integer configId) throws Exception {
	return transactionInDAO.getTargetConfigsForUploadBatch(batchUploadId, configId);
    }

    @Override
    public Integer checkClearAfterDeliveryBatch(int batchUploadId)
	    throws Exception {
	return transactionInDAO.checkClearAfterDeliveryBatch(batchUploadId);
    }

    @Override
    public Integer removeLoadTableBlankRows(Integer batchUploadId,
	    String loadTableName) throws Exception {
	return transactionInDAO.removeLoadTableBlankRows(batchUploadId, loadTableName);
    }

    @Override
    public Integer getLoadTransactionCount(String loadTableName) throws Exception {
	return transactionInDAO.getLoadTransactionCount(loadTableName);
    }

    @Override
    public void deleteBatch(Integer batchId) throws Exception {
	transactionInDAO.deleteBatch(batchId);
    }


    @Override
    public Integer getRecordCountForTable(String tableName, String colName, int matchId) throws Exception {
	return transactionInDAO.getRecordCountForTable(tableName, colName, matchId);
    }

    @Override
    public void updateBatchUpload(batchUploads batchUpload) throws Exception {
	transactionInDAO.updateBatchUpload(batchUpload);
    }

    @Override
    public List<Integer> getConfigIdsForBatchOnly(int batchUploadId) throws Exception {
	return transactionInDAO.getConfigIdsForBatchOnly(batchUploadId);
    }

    public void createBatchTables(int batchUploadId, int configId) throws Exception {
	transactionInDAO.createBatchTables(batchUploadId, configId);
    }

    public void deleteBatchTransactionTables(Integer batchUploadId) throws Exception {
	transactionInDAO.deleteBatchTransactionTables(batchUploadId);
    }

    @Override
    public List<batchDownloads> findBatchesToCleanUp() throws Exception {
	return transactionInDAO.findBatchesToCleanUp();
    }

    @Override
    public void batchUploadTableCleanUp() throws Exception {
	
	//Get a list of batches that can be cleaned up
	List<batchDownloads> batchesToCleanup = transactionInDAO.findBatchesToCleanUp();

	if (batchesToCleanup != null) {
	    if (!batchesToCleanup.isEmpty()) {
		transactionInDAO.batchUploadTableCleanUp(batchesToCleanup);
	    }
	}
	
	//Get a list of rejected batches to clean up
	List<batchUploads> rejectedInboundBatchesToCleanup = transactionInDAO.findRejectedBatchesToCleanUp();
	
	if (rejectedInboundBatchesToCleanup != null) {
	    if (!rejectedInboundBatchesToCleanup.isEmpty()) {
		transactionInDAO.rejectedBatchUploadTableCleanUp(rejectedInboundBatchesToCleanup);
	    }
	}
    }

    @Override
    public List<configurationConnection> getPassThruBatchTargets(Integer batchId, boolean active) {
	return transactionInDAO.getPassThruBatchTargets(batchId, active);
    }

    /**
     * The handlePassthruBatchUpload method will take care of a batch were no processing needs to take place and just needs to pass the passedin file to the outbound batch.
     *
     * @param batchId
     */
    public void handlePassthruBatchUpload(Integer batchId, String sourceFile) throws Exception {

	//Get the batch details
	batchUploads batchDetails = transactionInDAO.getBatchDetails(batchId);

	//Get the utConfiguration details
	utConfiguration configDetails = configurationManager.getConfigurationById(batchDetails.getConfigId());

	//Get the list of targets for this batch
	List<configurationConnection> batchTargetList = getPassThruBatchTargets(batchId, true);

	for (configurationConnection bt : batchTargetList) {

	    utConfiguration tgtconfigDetails = configurationManager.getConfigurationById(bt.gettargetConfigId());

	    if (batchDetails.getConfigId() != bt.getsourceConfigId()) {
		if (bt.getTargetOrgCol() != 0) {
		    rejectInvalidTargetOrg(batchId, bt);
		}
	    } 
	    else {

		// Create the batch name (OrgId+MessageTypeId+Date/Time) - need milliseconds as computer is fast and files have the same name*/
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
		Date date = new Date();

		configurationTransport transportDetails = configurationtransportmanager.getTransportDetails(tgtconfigDetails.getId());

		String utbatchName = new StringBuilder().append(transportDetails.gettransportMethodId()).append("_m_").append(batchDetails.getId()).append(tgtconfigDetails.getorgId()).append(tgtconfigDetails.getMessageTypeId()).append(dateFormat.format(date)).toString();

		//we create a batchDownloads
		batchDownloads batchDownload = new batchDownloads();

		//set batch download details
		batchDownload.setUtBatchName(utbatchName);
		
		//set the batch to a completed status (passthru requires no processing)
		batchDownload.setStatusId(28);

		//we determine output file name
		batchDownload.setOutputFileName(transactionoutmanager.generateDLBatchName(utbatchName,transportDetails, tgtconfigDetails, batchDetails, date) + "." + transportDetails.getfileExt());
		batchDownload.setMergeable(false);
		batchDownload.setStartDateTime(new Date());
		batchDownload.setEndDateTime(new Date());
		batchDownload.setTransportMethodId(transportDetails.gettransportMethodId());
		batchDownload.setOrgId(tgtconfigDetails.getorgId());
		batchDownload.setUserId(0);
		batchDownload.setTotalErrorCount(0);
		batchDownload.setTotalRecordCount(1);
		batchDownload.setDeleted(false);
		batchDownload.setDateCreated(new Date());
		batchDownload.setConfigId(tgtconfigDetails.getId());
		batchDownload.setBatchUploadId(batchDetails.getId());
		
		// Submit a new batch
		int batchDLId = (int) transactionOutDAO.submitBatchDownload(batchDownload);

		batchDownloads batchDLDetails = transactionOutDAO.getBatchDetails(batchDLId);

		//Need to copy the source file into the target location (final target location to send from.
		String fileName = new StringBuilder().append(batchDLDetails.getOutputFileName()).toString();
		fileSystem dir = new fileSystem();

		String filelocation = transportDetails.getfileLocation().trim();

		File targetFile = new File(myProps.getProperty("ut.directory.utRootDir") + filelocation + fileName);
		Files.copy(new File(sourceFile).toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		//Copy the file to the archive path as well
		File archiveFile = new File(myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + batchDownload.getUtBatchName() + "." + transportDetails.getfileExt());
		File archiveOutFile = new File(myProps.getProperty("ut.directory.utRootDir") + "archivesOut/" + batchDownload.getUtBatchName() + "." + transportDetails.getfileExt());

		//at this point, message it not encrypted
		//we always encrypt the archive file
		String strEncodedFile = filemanager.encodeFileToBase64Binary(new File(sourceFile));
		if (archiveFile.exists()) {
		    archiveFile.delete();
		}
		if (archiveOutFile.exists()) {
		    archiveOutFile.delete();
		}
		//write to archive IN folder
		filemanager.writeFile(archiveFile.getAbsolutePath(), strEncodedFile);

		//write to archive OUT folder
		filemanager.writeFile(archiveOutFile.getAbsolutePath(), strEncodedFile);

		//Need to call new method for sending of the file only
		transactionoutmanager.sendPassThruFiles(batchDetails,batchDLDetails,transportDetails,archiveFile);
	    }
	}
    }
    
    @Override
    public void resetTransactionCounts(Integer batchUploadId) throws Exception {
	 transactionInDAO.resetTransactionCounts(batchUploadId);
    }
    
    @Override
    public Integer insertDMMessage(directmessagesin newDirectMessageIn) throws Exception {
        return transactionInDAO.insertDMMessage(newDirectMessageIn);
    }
    
    /**
     * This method process direct api messages received. These are messages that has valid senders. 1 for not processed 2 processed - written as text file 3 for rejected 4 for while writing to text ifle processing
     *
     * It will gather the list and write to to proper folder as a text file then moveFile process will pick them up and process them using the same codes
     *
     **
     * @return
     */
    @Override
    public void processDirectAPIMessages() {

	try {
	    List<directmessagesin> directMessageList = getDirectAPIMessagesByStatusId(Arrays.asList(1));
	    
	    if (directMessageList != null) {
		
		//Parallel processing of batches
		for (directmessagesin directmessage : directMessageList) {
		    //we check to make sure again as time could have pass and it could have been processed already
		    directmessagesin directMessageRecheck = getDirectAPIMessagesById(directmessage.getId());
		    if (directMessageRecheck.getStatusId() == 1) {
			executor.execute(new Runnable() {
			    @Override
			    public void run() {
				try {
				    processDirectAPIMessage(directmessage);
				} catch (Exception ex) {
				    Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			    }
			});
		    }
		}
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("processRestAPIMessages " + ex.toString());
	    try {
		sendEmailToAdmin((ex.toString() + "<br/>" + Arrays.toString(ex.getStackTrace())), "Rest API Job Error - main method errored");
	    } catch (Exception ex1) {
		ex1.printStackTrace();
		System.err.println("processRestAPIMessages - can't send email for rest api service job error " + ex1.toString());
	    }
	}

    }

    @Override
    public List<directmessagesin> getDirectAPIMessagesByStatusId(List<Integer> statusIds) {
	return transactionInDAO.getDirectAPIMessagesByStatusId(statusIds);
    }

    @Override
    public directmessagesin getDirectAPIMessagesById(Integer directMessageId) {
	return transactionInDAO.getDirectAPIMessagesById(directMessageId);
    }
    
    @Override
    public directmessagesin getDirectAPIMessagesByBatchUploadId(Integer BatchUploadId) {
	return transactionInDAO.getDirectAPIMessagesByBatchUploadId(BatchUploadId);
    }

    /**
     * this will process each rest api message, it should be less intensive if we treat it like a file upload instead of file drop At the end of this, file should be written to input folder, it should be SSA or SRJ and logged
     *
     * @param directMessage
     */
    public void processDirectAPIMessage(directmessagesin directMessage) {
	
	try {
	    directMessage.setStatusId(4);
	    updateDirectAPIMessage(directMessage);

	    configurationTransport ct = configurationtransportmanager.getTransportDetails(directMessage.getConfigId());

	    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
	    Date date = new Date();
	    /* Create the batch name (TransportMethodId+OrgId+Date/Time/Seconds) */
	    String batchName = new StringBuilder().append("direct").append("_").append(directMessage.getId()).append("_").append(directMessage.getOrgId()).append(dateFormat.format(date)).toString();

	    Integer encodingId = 1;
	    Integer batchId = 0;
	    Integer errorId = 0;
	    Integer maxfileSize = 0;
	    Integer statusId = 0;
	    Integer configId = directMessage.getConfigId();

	    batchUploads batchInfo = new batchUploads();
	    batchInfo.setOrgId(directMessage.getOrgId());
	    batchInfo.setTransportMethodId(13);
	    batchInfo.setStatusId(4);
	    batchInfo.setStartDateTime(date);
	    batchInfo.setUtBatchName(batchName);
	    batchInfo.setConfigId(configId);
	    batchInfo.setOriginalFileName(directMessage.getReferralFileName());
	    batchInfo.setSenderEmail(directMessage.getFromDirectAddress());
	    batchInfo.setRecipientEmail(directMessage.getToDirectAddress());

	    Organization orgDetails = organizationmanager.getOrganizationById(directMessage.getOrgId());
	    String writeToFolder = myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/input files/";
	    String fileExt = "." + FilenameUtils.getExtension(directMessage.getReferralFileName());
	    String fileNamePath = writeToFolder + batchName + fileExt;
	    
	    //set folder path
	    fileSystem dir = new fileSystem();
	    String writeToFile = fileNamePath;
	    
	     //File Drop directory
	    List<configurationFileDropFields> fileDropFields = configurationtransportmanager.getTransFileDropDetails(ct.getId());

	    String fileDropDir = orgDetails.getcleanURL() + "/input files/";

	    for(configurationFileDropFields dropField : fileDropFields){
		if(dropField.getMethod() == 1) {
		    fileDropDir = dropField.getDirectory();
		}
	    }
	    
	    String DMFile = myProps.getProperty("ut.directory.utRootDir") + fileDropDir;
	    batchInfo.setOriginalFolder(DMFile);

	    //we reject
	    if (ct == null) {
		batchInfo.setUserId(usermanager.getUserByTypeByOrganization(directMessage.getOrgId()).get(0).getId());
		batchInfo.setConfigId(0);
		batchInfo.setFileLocation(writeToFolder);
		batchInfo.setEncodingId(encodingId);
		//copy the file
		FileUtils.copyFile(new File(DMFile+directMessage.getReferralFileName()), new File(writeToFile));
		if (batchInfo.getConfigId() != 0 && batchInfo.getStatusId() == 2) {
		    batchInfo.setStatusId(42);
		}
		batchId = submitBatchUpload(batchInfo);
		//insert error
		errorId = 13;
		statusId = 7;

	    } else {

		//Get the utConfiguration details
		utConfiguration configDetails = configurationManager.getConfigurationById(ct.getconfigId());

		encodingId = ct.getEncodingId();
		fileExt = "." + ct.getfileExt();
		writeToFolder = ct.getfileLocation();
		fileNamePath = myProps.getProperty("ut.directory.utRootDir") + writeToFolder + batchName + fileExt;
		String archivefileNamePath = myProps.getProperty("ut.directory.utRootDir") + writeToFolder +"encoded_" + batchName + fileExt;
		maxfileSize = ct.getmaxFileSize();

		batchInfo.setContainsHeaderRow(ct.getContainsHeaderRow());
		batchInfo.setDelimChar(ct.getDelimChar());
		batchInfo.setFileLocation(ct.getfileLocation());
		batchInfo.setEncodingId(encodingId);
		batchInfo.setUserId(0);

		//copy file
		writeToFile = fileNamePath;
		
		File encodedFile = new File(archivefileNamePath);
		
		File movedFile = new File(fileNamePath);
		
		FileUtils.moveFile(new File(DMFile+directMessage.getReferralFileName()),movedFile);

		String encodedOldFile = filemanager.encodeFileToBase64Binary(movedFile);
		filemanager.writeFile(encodedFile.getAbsolutePath(), encodedOldFile);

		if (statusId != 7) {
		    //decode and check delimiter
		    File file = new File(writeToFile);

		    if (ct.getEncodingId() == 2 && filemanager.isFileBase64Encoded(file)) {
			//write to temp file
			String strDecode = filemanager.decodeFileToBase64Binary(file);
			file = new File(myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + batchName + "_dec" + fileExt);
			String decodeFilePath = myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + batchName + "_dec" + fileExt;
			filemanager.writeFile(decodeFilePath, strDecode);
			
			String encodeArchivePath = myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + batchName + fileExt;
			Files.copy(new File(writeToFile).toPath(), new File(encodeArchivePath).toPath(), REPLACE_EXISTING);
		    } 
		    else {
			String encodeArchivePath = myProps.getProperty("ut.directory.utRootDir") + "archivesIn/archive_" + batchName + fileExt;
			Files.copy(new File(writeToFile).toPath(), new File(encodeArchivePath).toPath(), REPLACE_EXISTING);
			Files.copy(encodedFile.toPath(), new File(myProps.getProperty("ut.directory.utRootDir") + "archivesIn/" + batchName + fileExt).toPath(), REPLACE_EXISTING);
		    }

		    statusId = 2;
		    /**
		     * can't check delimiter for certain files, xml, hr etc *
		     */
		    if (fileExt.equalsIgnoreCase(".txt")) {
			
			String delimiter = "";
			
			if(!"".equals(ct.getDelimChar())) {
			   delimiter = (String) messageTypeDAO.getDelimiterChar(ct.getfileDelimiter());
			}
			else {
			    delimiter = ct.getDelimChar();
			}
			
			int delimCount = dir.checkFileDelimiter(new File(fileNamePath), delimiter);
			if (delimCount < 3) {
			    statusId = 7;
			    errorId = 15;
			}
		    }
		    //check file size
		    if (statusId == 2) {
			double uploadedFileSizeInBytes = file.length();
			double uploadedFileSizeInKB = (uploadedFileSizeInBytes / 1024);
			double uploadedFileSizeInMB = (uploadedFileSizeInKB / 1024);
			
			if (uploadedFileSizeInMB > maxfileSize) {
			    statusId = 7;
			    errorId = 12;
			}
		    }
		}

		batchInfo.setStatusId(statusId);
		//batchInfo.setEndDateTime(new Date());
		batchInfo.setTotalRecordCount(1); // need to be at least one to show up in activites

		if (batchInfo.getConfigId() != 0 && batchInfo.getStatusId() == 2) {

		    //If utConfiguration is set for passthru
		    if (configDetails.getConfigurationType() == 2) {
			batchInfo.setStatusId(24);
			statusId = 24;
		    } 
		    else {
			statusId = 42;
			batchInfo.setStatusId(42);
		    }
		}

		batchId = submitBatchUpload(batchInfo);
		
		updateBatchStatus(batchId, statusId, "startDateTime");
		
		createBatchTables(batchId, configId);

		if (statusId != 2 && statusId != 42 && statusId != 24) {
		    insertProcessingError(errorId, 0, batchId, null, null, null, null, false, false, "");
		}

		//update message status to done
		directMessage.setStatusId(2);
		directMessage.setBatchUploadId(batchId);
		updateDirectAPIMessage(directMessage);
		
	    }

	    //insert log
	    try {
		batchuploadactivity ba = new batchuploadactivity();
		ba.setActivity("System Processed Direct API Message " + directMessage.getId());
		ba.setBatchUploadId(batchInfo.getId());
		transactionInDAO.submitBatchActivityLog(ba);
		

	    } catch (Exception ex) {
		ex.printStackTrace();
		System.err.println("processDirectAPIMessage - insert user log" + ex.toString());
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("processDirectAPIMessage " + ex.toString());
	    try {
		sendEmailToAdmin((ex.toString() + "<br/>" + Arrays.toString(ex.getStackTrace())), "processDirectAPIMessage");
	    } catch (Exception ex1) {
		ex1.printStackTrace();
		System.err.println("processDirectAPIMessage " + ex1.toString());
	    }
	}
    }

    @Override
    public Integer updateDirectAPIMessage(directmessagesin directMessage) {
	return transactionInDAO.updateDirectAPIMessage(directMessage);
    }
    
    @Override
    public List<batchUploads> getAllUploadBatchesPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
	return transactionInDAO.getAllUploadBatchesPaged(fromDate,toDate, displayStart, displayRecords, searchTerm, sortColumnName, sortDirection);
    }
    
    @Override
    public void updateRecordCountsFromAuditErrorTable(Integer batchUploadId) throws Exception {
	transactionInDAO.updateRecordCountsFromAuditErrorTable(batchUploadId);
    }
    
    @Override
    public void checkRemoteSFTPConfigurations() throws Exception {
	
	//Need to find all source configurations with FTP enabled
	List<configurationFTPFields> ftpConfigurations = configurationtransportmanager.getFTPSourceConfigurations();
	
	if(!ftpConfigurations.isEmpty()) {
	    //Parallel processing of FTP Configurations
	    for (configurationFTPFields ftpConfiguration : ftpConfigurations) {
		executor.execute(new Runnable() {
		    @Override
		    public void run() {
			try {
			    connectToRemoteFTP(ftpConfiguration);
			} catch (Exception ex) {
			    Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
			}
		    }
		});
	    }
	}
    }
    
    /**
     * 
     * @param ftpConfiguration
     * @throws Exception 
     */
    public void connectToRemoteFTP(configurationFTPFields ftpConfiguration) throws Exception,JSchException {
	
	if(ftpConfiguration != null) {
	    
	    //Need to get the configuration file drop details
	    configurationFileDropFields fileDropDetails = configurationtransportmanager.getTransFileDropDetailsPull(ftpConfiguration.gettransportId());
	    
	    configurationTransport transportDetails= configurationtransportmanager.getTransportDetailsByTransportId(ftpConfiguration.gettransportId());
	    
	    //Get configuration details
	    utConfiguration configDetails = configurationManager.getConfigurationById(transportDetails.getconfigId());
	    
	    if(fileDropDetails != null) {
		if(!"".equals(fileDropDetails.getDirectory())) {
		    
		    JSch jSch = new JSch();
		    
		    Session session = null;
		    Channel channel = null;
		    ChannelSftp channelSftp = null;
		    
		    try {
			
			//If using Key File do this below
			/*
			    String privateKey = "LOCATION OF KEY FILE";
			    jSch.addIdentity(privateKey, "Private Key for Key file");
			*/
			
			session = jSch.getSession(ftpConfiguration.getusername(),ftpConfiguration.getip(),ftpConfiguration.getport());
			
			// Set password here if not using key file
			session.setPassword(ftpConfiguration.getpassword());
			
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			
			session.connect();

			channel = session.openChannel("sftp");
			channelSftp.cd(ftpConfiguration.getdirectory());

			Vector filelist = channelSftp.ls(ftpConfiguration.getdirectory());

			if(filelist.size() > 0) {
			    for(int i=0; i<filelist.size();i++){
				LsEntry entry = (LsEntry) filelist.get(i);

				if(!entry.getAttrs().isDir()) {
				    boolean fileMoved = false;

				   //Move the file locally
				   try {

				       channelSftp.get(entry.getFilename(),fileDropDetails.getDirectory() + entry.getFilename());
				       fileMoved = true;
				   }
				   catch (SftpException e) {
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					try {
					    String emailBody = "IP: " + ftpConfiguration.getip() + "<br/> Port:" + ftpConfiguration.getport() + "<br />Folder: " + ftpConfiguration.getdirectory() + "<br />Config Id:" + configDetails.getId() + "<br /><br />Error:<br />"+errors.toString();
					    mailMessage mail = new mailMessage();
					    mail.setfromEmailAddress("support@health-e-link.net");
					    mail.setmessageBody(emailBody);
					    mail.setmessageSubject("Error retriving FTP files" + " " + myProps.getProperty("server.identity"));
					    mail.settoEmailAddress(myProps.getProperty("admin.email"));
					    emailManager.sendEmail(mail);
					} catch (Exception ex) {
					    ex.printStackTrace();
					    throw new Exception(ex);
					}
					e.printStackTrace();
				   }

				   if(fileMoved) {
				       channelSftp.rm(entry.getFilename());
				   }
				}

			    }
			}
		    } catch (JSchException e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			try {
			    String emailBody = "IP: " + ftpConfiguration.getip() + "<br/> Port:" + ftpConfiguration.getport() + "<br />Folder: " + ftpConfiguration.getdirectory() + "<br />Config Id:" + configDetails.getId() + "<br /><br />Error:<br />"+errors.toString();
			    mailMessage mail = new mailMessage();
			    mail.setfromEmailAddress("support@health-e-link.net");
			    mail.setmessageBody(emailBody);
			    mail.setmessageSubject("Error retriving FTP files" + " " + myProps.getProperty("server.identity"));
			    mail.settoEmailAddress(myProps.getProperty("admin.email"));
			    emailManager.sendEmail(mail);
			} catch (Exception ex) {
			    ex.printStackTrace();
			    throw new Exception(ex);
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		    } catch (SftpException e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			try {
			    String emailBody = "IP: " + ftpConfiguration.getip() + "<br/> Port:" + ftpConfiguration.getport() + "<br />Folder: " + ftpConfiguration.getdirectory() + "<br />Config Id:" + configDetails.getId() + "<br /><br />Error:<br />"+errors.toString();
			    mailMessage mail = new mailMessage();
			    mail.setfromEmailAddress("support@health-e-link.net");
			    mail.setmessageBody(emailBody);
			    mail.setmessageSubject("Error retriving FTP files" + " " + myProps.getProperty("server.identity"));
			    mail.settoEmailAddress(myProps.getProperty("admin.email"));
			    emailManager.sendEmail(mail);
			} catch (Exception ex) {
			    ex.printStackTrace();
			    throw new Exception(ex);
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }finally{
			if(channelSftp!=null){
			    channelSftp.disconnect();
			    channelSftp.exit();
			}
			if(channel!=null) channel.disconnect();

			if(session!=null) session.disconnect();
			
		    }
		    
		}
	    }
	    
	}
	
    }

    @Override
    public void insertCWDroppedValues(Integer configId, Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) throws Exception {
	transactionInDAO.insertCWDroppedValues(configId, batchId, cdt, foroutboundProcessing);
    }
    
    @Override
    public void populateDroppedValues(Integer batchUploadId, Integer configId, boolean foroutboundProcessing) throws Exception {
    	transactionInDAO.populateDroppedValues(batchUploadId, configId, foroutboundProcessing);
    }
    
    @Override
    public List<batchUploadDroppedValues> getBatchDroppedValues(Integer batchUploadId) throws Exception {
	return transactionInDAO.getBatchDroppedValues(batchUploadId);
    }
    
    @Override
    public Integer getTotalErroredRows(Integer batchUploadId) throws Exception {
	return transactionInDAO.getTotalErroredRows(batchUploadId);
    }
    
    @Override
    public List<batchErrorSummary> getBatchSystemErrorSummary(int batchId, String inboundOutbound) throws Exception {
	return transactionInDAO.getBatchSystemErrorSummary(batchId,inboundOutbound);
    }
    
    @Override
    public void clearBatchActivityLogTable(Integer batchId) throws Exception {
	transactionInDAO.clearBatchActivityLogTable(batchId);
    }
    
    @Override
    public void executePassClearLogic(Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) throws Exception {
	transactionInDAO.executePassClearLogic(batchId, cdt, foroutboundProcessing);
    }
    
    @Override
    public void insertMacroDroppedValues(Integer batchId, configurationDataTranslations cdt, boolean foroutboundProcessing) throws Exception {
	transactionInDAO.insertMacroDroppedValues(batchId, cdt, foroutboundProcessing);
    }
    
    @Override
    public void executePassClearLogicForValidationError(Integer batchId, configurationFormFields cff, boolean foroutboundProcessing) throws Exception {
	transactionInDAO.executePassClearLogicForValidationError(batchId, cff, foroutboundProcessing);
    }
    
    @Override
    public void insertValidationDroppedValues(Integer batchId, configurationFormFields cff, boolean foroutboundProcessing) throws Exception {
	transactionInDAO.insertValidationDroppedValues(batchId, cff, foroutboundProcessing);
    }
}
