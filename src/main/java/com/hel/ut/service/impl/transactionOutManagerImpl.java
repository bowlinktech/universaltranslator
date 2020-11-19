/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service.impl;

import com.hel.ut.dao.messageTypeDAO;
import com.hel.ut.dao.transactionInDAO;
import com.hel.ut.dao.transactionOutDAO;
import com.hel.ut.model.*;
import com.hel.ut.model.custom.ConfigOutboundForInsert;
import com.hel.ut.model.custom.batchErrorSummary;
import com.hel.ut.reference.fileSystem;
import com.hel.ut.restAPI.directManager;
import com.hel.ut.restAPI.restfulManager;
import com.hel.ut.service.*;
import com.hel.ut.webServices.WSManager;
import com.registryKit.registry.helRegistry;
import com.registryKit.registry.helRegistryManager;
import com.registryKit.registry.submittedMessages.submittedMessage;
import com.registryKit.registry.submittedMessages.submittedMessageManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger.OutputField;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chadmccue
 */
@Service
public class transactionOutManagerImpl implements transactionOutManager {

    @Resource(name = "myProps")
    private Properties myProps;

    @Autowired
    private transactionOutDAO transactionOutDAO;

    @Autowired
    private transactionInDAO transactionInDAO;

    @Autowired
    private utConfigurationManager configurationManager;

    @Autowired
    private utConfigurationTransportManager configurationTransportManager;

    @Autowired
    private transactionInManager transactionInManager;

    @Autowired
    private messageTypeDAO messageTypeDAO;

    @Autowired
    private userManager userManager;

    @Autowired
    private organizationManager organizationManager;

    @Autowired
    private emailMessageManager emailMessageManager;

    @Autowired
    private fileManager filemanager;

    @Autowired
    private userManager usermanager;

    @Autowired
    private WSManager wsManager;

    @Autowired
    private utilManager utilmanager;

    @Autowired
    private restfulManager restfulManager;

    @Autowired
    private convertTextToPDF txtToPDF;
    
    @Autowired
    private helRegistryManager helregistrymanager;
    
    @Autowired
    private submittedMessageManager submittedmessagemanager;
    
    @Autowired
    private hispManager hispManager;
    
    @Autowired
    private directManager directManager;
     
    @Autowired
    ThreadPoolTaskExecutor executor;

    private int processingSysErrorId = 5;
    

    //list of final status - these records we skip
    private List<Integer> transRELId = Arrays.asList(11, 12, 13, 16, 18, 20, 9);

    private List<Integer> rejectIds = Arrays.asList(13, 14);

    @Override
    public List<batchDownloads> getInboxBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception {
	return transactionOutDAO.getInboxBatches(userId, orgId, fromDate, toDate);
    }

    @Override
    public batchDownloads getBatchDetails(int batchId) throws Exception {
	return transactionOutDAO.getBatchDetails(batchId);
    }

    @Override
    public batchDownloads getBatchDetailsByBatchName(String batchName) throws Exception {
	return transactionOutDAO.getBatchDetailsByBatchName(batchName);
    }

    @Override
    public List<transactionOutRecords> getTransactionRecords(Integer batchId, Integer configId, Integer totalFields) throws Exception {
	return transactionOutDAO.getTransactionRecords(batchId,configId, totalFields);
    }

    @Override
    public List getInternalStatusCodes() {
	return transactionOutDAO.getInternalStatusCodes();
    }

    @Override
    public void updateTargetBatchStatus(Integer batchDLId, Integer statusId, String timeField) throws Exception {
	transactionOutDAO.updateTargetBatchStatus(batchDLId, statusId, timeField);
    }

    /**
     * The 'generateTargetFile' function will generate the actual file in the correct organizations outpufiles folder.
     *
     * @param createNewFile
     * @param batchId
     * @param transportDetails
     * @param encrypt
     * @return
     * @throws java.lang.Exception
     */
    public String generateTargetFile(boolean createNewFile, int batchId, configurationTransport transportDetails, boolean encrypt) throws Exception {

	String fileName = null;
	String strFileLoc = "";
	batchDownloads batchDetails = transactionOutDAO.getBatchDetails(batchId);
	batchUploads batchUploadDetails = transactionInDAO.getBatchDetails(batchDetails.getBatchUploadId());
	
	Organization sendingOrgDetails = organizationManager.getOrganizationById(batchUploadDetails.getOrgId());

	InputStream inputStream = null;
	OutputStream outputStream = null;

	String filelocation = transportDetails.getfileLocation();
	filelocation = filelocation.replace("/HELProductSuite/universalTranslator/", "");
	String directory = myProps.getProperty("ut.directory.utRootDir") + filelocation;

	boolean hl7 = false;
	boolean CCD = false;
	boolean json = false;
	String fileType = (String) configurationManager.getFileTypesById(transportDetails.getfileType());

	if ("hl7".equals(fileType)) {
	    hl7 = true;
	} else if ("xml (CCD)".equals(fileType)) {
	    CCD = true;
	} else if ("json".equals(fileType)) {
	    json = true;
	}

	int findExt = batchDetails.getOutputFileName().lastIndexOf(".");

	if (findExt >= 0) {
	    fileName = batchDetails.getOutputFileName();
	} else {
	    fileName = new StringBuilder().append(batchDetails.getOutputFileName()).append(".").append(transportDetails.getfileExt()).toString();
	}

	File newFile = new File(directory + fileName);

	// Create the empty file in the correct location
	if (createNewFile == true || !newFile.exists()) {
	    try {

		if (newFile.exists()) {
		    int i = 1;
		    while (newFile.exists()) {
			int iDot = fileName.lastIndexOf(".");
			newFile = new File(directory + fileName.substring(0, iDot) + "_(" + ++i + ")" + fileName.substring(iDot));
		    }
		    fileName = newFile.getName();
		    newFile.createNewFile();
		} else {
		    newFile.createNewFile();
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    /* Need to update the batch with the updated file name */
	    transactionOutDAO.updateBatchOutputFileName(batchDetails.getId(), fileName);

	}

	// Read in the file
	FileInputStream fileInput = null;
	File file = new File(directory + fileName);
	fileInput = new FileInputStream(file);
        
	// Need to get the records for the transaction
	String recordRow = "";

	List<configurationFormFields> formFields = configurationTransportManager.getConfigurationFields(transportDetails.getconfigId(), 0);

	List<transactionOutRecords> records = transactionOutDAO.getTransactionRecords(batchId, transportDetails.getconfigId(), formFields.size());
	
	// Need to get the max field number
	int maxFieldNo = transactionOutDAO.getMaxFieldNo(transportDetails.getconfigId());

	// Need to get the correct delimiter for the output file
	String delimChar = (String) messageTypeDAO.getDelimiterChar(transportDetails.getfileDelimiter());

	if (records != null) {

	    FileWriter fw = null;

	    try {
		fw = new FileWriter(file, true);
	    } catch (IOException ex) {
		Logger.getLogger(transactionOutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
	    }

	    // If a CCD file is to be generated
	    if (CCD == true) {

		Organization orgDetails = organizationManager.getOrganizationById(batchDetails.getOrgId());

		String ccdSampleTemplate = transportDetails.getCcdSampleTemplate();

		Path path = Paths.get(directory = myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/templates/" + ccdSampleTemplate);
		String ccdSampleContent = new String(Files.readAllBytes(path));

		Path newFilePath = Paths.get(directory + fileName);
		Files.write(newFilePath, ccdSampleContent.getBytes());

		String contentToUpdate = new String(Files.readAllBytes(newFilePath));

		// Get the configurationCCDElements
		List<configurationCCDElements> ccdElements = configurationManager.getCCDElements(transportDetails.getconfigId());

		if (!ccdElements.isEmpty()) {
		    String fieldValue = "";

		    for (configurationCCDElements element : ccdElements) {
			fieldValue = "";
			
			//Check to see if element is for sending organiation information
			if(element.getElement().contains("[@sendingOrg")) {
			    if(element.getElement().equals("[@sendingOrgAddress@]")) {
				if(sendingOrgDetails.getAddress() == null) {
				    fieldValue = "";
				}
				else if("null".equals(sendingOrgDetails.getAddress().trim())) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getAddress().trim().isEmpty()) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getAddress().trim().length() == 0) {
				    fieldValue = "";
				}
				else {
				    fieldValue = sendingOrgDetails.getAddress().trim();
				}
			    }
			    else if(element.getElement().equals("[@sendingOrgCity@]")) {
				if(sendingOrgDetails.getCity() == null) {
				    fieldValue = "";
				}
				else if("null".equals(sendingOrgDetails.getCity().trim())) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getCity().trim().isEmpty()) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getCity().trim().length() == 0) {
				    fieldValue = "";
				}
				else {
				    fieldValue = sendingOrgDetails.getCity().trim();
				}
			    }
			    else if(element.getElement().equals("[@sendingOrgState@]")) {
				if(sendingOrgDetails.getState() == null) {
				    fieldValue = "";
				}
				else if("null".equals(sendingOrgDetails.getState().trim())) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getState().trim().isEmpty()) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getState().trim().length() == 0) {
				    fieldValue = "";
				}
				else {
				    fieldValue = sendingOrgDetails.getState().trim();
				}
			    }
			    else if(element.getElement().equals("[@sendingOrgZipCode@]")) {
				if(sendingOrgDetails.getPostalCode() == null) {
				    fieldValue = "";
				}
				else if("null".equals(sendingOrgDetails.getPostalCode().trim())) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getPostalCode().trim().isEmpty()) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getPostalCode().trim().length() == 0) {
				    fieldValue = "";
				}
				else {
				    fieldValue = sendingOrgDetails.getPostalCode().trim();
				}
			    }
			    else if(element.getElement().equals("[@sendingOrgPhone@]")) {
				if(sendingOrgDetails.getPhone() == null) {
				    fieldValue = "";
				}
				else if("null".equals(sendingOrgDetails.getPhone().trim())) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getPhone().trim().isEmpty()) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getPhone().trim().length() == 0) {
				    fieldValue = "";
				}
				else {
				    fieldValue = sendingOrgDetails.getPhone().trim();
				    StringBuilder sb1 = new StringBuilder(element.getDefaultValue().trim());
				    sb1.append(" ").append(fieldValue);
				    fieldValue = sb1.toString();
				}
			    }
			    else if(element.getElement().equals("[@sendingOrgName@]")) {
				if(sendingOrgDetails.getOrgName() == null) {
				    fieldValue = "";
				}
				else if("null".equals(sendingOrgDetails.getOrgName().trim())) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getOrgName().trim().isEmpty()) {
				    fieldValue = "";
				}
				else if(sendingOrgDetails.getOrgName().trim().length() == 0) {
				    fieldValue = "";
				}
				else {
				    fieldValue = sendingOrgDetails.getOrgName().trim();
				}
			    }
			    
			    contentToUpdate = contentToUpdate.replace(element.getElement(), fieldValue);
			}
			else {
			    if ("~currDate~".equals(element.getDefaultValue())) {
				SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHms");
				String date = date_format.format(batchDetails.getDateCreated());
				contentToUpdate = contentToUpdate.replace(element.getElement(), date);
			    } 
			    else if(!"".equals(element.getDefaultValue().trim()) && "".equals(element.getFieldValue())) {
				contentToUpdate = contentToUpdate.replace(element.getElement(), element.getDefaultValue().trim());
			    }
			    else {
				String colName = new StringBuilder().append("f").append(element.getFieldValue()).toString();

				fieldValue = (String) PropertyUtils.getProperty(records.get(0), colName);

				if (fieldValue == null) {
				    fieldValue = "";
				} else if ("null".equals(fieldValue)) {
				    fieldValue = "";
				} else if (fieldValue.isEmpty()) {
				    fieldValue = "";
				} else if (fieldValue.length() == 0) {
				    fieldValue = "";
				}

				if(!"".equals(fieldValue) && !"".equals(element.getDefaultValue().trim())) {
				    StringBuilder sb1 = new StringBuilder(element.getDefaultValue().trim());
				    sb1.append(" ").append(fieldValue);
				    fieldValue = sb1.toString();
				}

				contentToUpdate = contentToUpdate.replace(element.getElement(), fieldValue);
			    }
			}
		    }
		}

		// need to see if we need to encrypt file here
		if (!encrypt) {
		    Files.write(newFilePath, contentToUpdate.getBytes());
		} else {
		    String strEncodedFile = utilmanager.encodeStringToBase64Binary(contentToUpdate);
		    Files.write(newFilePath, strEncodedFile.getBytes());
		}
	    } 
	    // If an hl7 file is to be generated 
	    else if (hl7 == true) {

		/* Get the hl7 details */
		HL7Details hl7Details = configurationManager.getHL7Details(transportDetails.getconfigId());

		if (hl7Details != null) {

		    /* Get the hl7 Segments */
		    List<HL7Segments> hl7Segments = configurationManager.getHL7Segments(hl7Details.getId());

		    if (!hl7Segments.isEmpty()) {

			StringBuilder hl7recordRow = new StringBuilder();

			for (HL7Segments segment : hl7Segments) {

			    // Get the segment elements
			    List<HL7Elements> hl7Elements = configurationManager.getHL7Elements(hl7Details.getId(), segment.getId());

			    if (!hl7Elements.isEmpty()) {

				hl7recordRow.append(segment.getsegmentName()).append(hl7Details.getfieldSeparator());

				int elementCounter = 1;
				for (HL7Elements element : hl7Elements) {

				    if ("pdfattachment".equals(element.getelementName().toLowerCase()) && transportDetails.getHL7PDFSampleTemplate() != null && !"".equals(transportDetails.getHL7PDFSampleTemplate())) {

					Organization orgDetails = organizationManager.getOrganizationById(batchDetails.getOrgId());
					
					String hl7PDFSampleTemplate = transportDetails.getHL7PDFSampleTemplate();

					File inputFile;

					if (hl7PDFSampleTemplate.contains(".docx")) {
					    String inputfilepath = myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/templates/" + hl7PDFSampleTemplate;

					    String outputfilepath = directory + "OUT_variableReplace.docx";

					    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File(inputfilepath));

					    //MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
					    List<configurationCCDElements> hl7PDFElements = configurationManager.getCCDElements(transportDetails.getconfigId());

					    if (!hl7PDFElements.isEmpty()) {

						List<Map<DataFieldName, String>> data = new ArrayList<Map<DataFieldName, String>>();

						Map<DataFieldName, String> map1 = new HashMap<DataFieldName, String>();

						for (configurationCCDElements CCDelement : hl7PDFElements) {

						    String elementName = CCDelement.getElement().replace("<%", "").replace("%>", "");

						    if (!"".equals(CCDelement.getDefaultValue())) {

							if ("~currDate~".equals(CCDelement.getDefaultValue())) {
							    SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd");
							    String date = date_format.format(batchDetails.getDateCreated());

							    map1.put(new DataFieldName(elementName), date);

							} else {
							    map1.put(new DataFieldName(elementName), CCDelement.getDefaultValue());
							}
						    } else {
							String colName = new StringBuilder().append("f").append(CCDelement.getFieldValue()).toString();

							String fieldValue = BeanUtils.getProperty(records.get(0), colName);

							if (fieldValue == null) {
							    fieldValue = "";
							} else if ("null".equals(fieldValue)) {
							    fieldValue = "";
							} else if (fieldValue.isEmpty()) {
							    fieldValue = "";
							} else if (fieldValue.length() == 0) {
							    fieldValue = "";
							}

							map1.put(new DataFieldName(elementName), fieldValue);
						    }
						}
						data.add(map1);

						org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.REMOVED);

						int x = 0;
						for (Map<DataFieldName, String> docMapping : data) {
						    org.docx4j.model.fields.merge.MailMerger.performMerge(wordMLPackage, docMapping, true);
						    wordMLPackage.save(new java.io.File(outputfilepath));
						}

					    }

					    inputFile = new File(directory + "OUT_variableReplace.docx");

					    FieldUpdater updater = new FieldUpdater(wordMLPackage);
					    updater.update(true);

					    FOSettings foSettings = Docx4J.createFOSettings();
					    foSettings.setWmlPackage(wordMLPackage);

					    String outputfilepath2 = directory + "hl7pdf.pdf";
					    OutputStream os = new java.io.FileOutputStream(outputfilepath2);
					    Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);

					    if (wordMLPackage.getMainDocumentPart().getFontTablePart() != null) {
						wordMLPackage.getMainDocumentPart().getFontTablePart().deleteEmbeddedFontTempFiles();
					    }

					    updater = null;
					    foSettings = null;
					    wordMLPackage = null;

					    fileSystem attachDir = new fileSystem();
					    File f = new File(directory + "hl7pdf.pdf");
					    byte[] bytes = attachDir.loadFile(f);
					    byte[] encoded = Base64.encode(bytes);
					    String encodedString = new String(encoded);

					    hl7recordRow.append(encodedString);

					    /* Delete files */
					    inputFile.delete();
					    f.delete();
					} else {

					    Path path = Paths.get(myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/templates/" + hl7PDFSampleTemplate);
					    String hl7PDFSampleContent = new String(Files.readAllBytes(path));

					    Path newFilePath = Paths.get(directory + "hl7pdf.txt");
					    Files.write(newFilePath, hl7PDFSampleContent.getBytes());

					    String contentToUpdate = new String(Files.readAllBytes(newFilePath));

					    List<configurationCCDElements> hl7PDFElements = configurationManager.getCCDElements(transportDetails.getconfigId());

					    if (!hl7PDFElements.isEmpty()) {

						for (configurationCCDElements CCDelement : hl7PDFElements) {

						    if (!"".equals(CCDelement.getDefaultValue())) {
							if ("~currDate~".equals(CCDelement.getDefaultValue())) {
							    SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd");
							    String date = date_format.format(batchDetails.getDateCreated());
							    contentToUpdate = contentToUpdate.replace(CCDelement.getElement(), date);
							} else {
							    contentToUpdate = contentToUpdate.replace(CCDelement.getElement(), CCDelement.getDefaultValue());
							}

						    } else {
							String colName = new StringBuilder().append("f").append(CCDelement.getFieldValue()).toString();

							String fieldValue = BeanUtils.getProperty(records.get(0), colName);

							if (fieldValue == null) {
							    fieldValue = "";
							} else if ("null".equals(fieldValue)) {
							    fieldValue = "";
							} else if (fieldValue.isEmpty()) {
							    fieldValue = "";
							} else if (fieldValue.length() == 0) {
							    fieldValue = "";
							}

							contentToUpdate = contentToUpdate.replace(CCDelement.getElement(), fieldValue);
						    }

						}
					    }

					    Files.write(newFilePath, contentToUpdate.getBytes());

					    inputFile = new File(directory + "hl7pdf.txt");

					    if (txtToPDF.convertTextToPDF(inputFile, directory, "hl7pdf.pdf")) {
						fileSystem attachDir = new fileSystem();
						File f = new File(directory + "hl7pdf.pdf");
						byte[] bytes = attachDir.loadFile(f);
						byte[] encoded = Base64.encode(bytes);
						String encodedString = new String(encoded);

						hl7recordRow.append(encodedString);

						/* Delete files */
						inputFile.delete();
						f.delete();
					    }
					}

				    }

				    /* If the HL7 requires attachments then we need to look for the "attachments" keyword
                                    in order to loop through and retrieve all attachments to the batch.
				     */
				    if ("attachments".equals(element.getelementName().toLowerCase())) {
					/*transactionTarget targetDetails = transactionOutDAO.getTransactionDetails(transactionTargetId);
					List<transactionAttachment> attachments = transactionInManager.getAttachmentsByTransactionId(targetDetails.gettransactionInId());

					if (!attachments.isEmpty()) {
					    Integer attachmentCounter = 1;
					    for (transactionAttachment attachment : attachments) {
						fileSystem attachDir = new fileSystem();
						attachDir.setDirByName(attachment.getfileLocation() + "/");
						File f = new File(attachDir.getDir() + attachment.getfileName());
						byte[] bytes = attachDir.loadFile(f);
						byte[] encoded = Base64.encode(bytes);
						String encodedString = new String(encoded);
						if (!"".equals(attachment.gettitle()) && attachment.gettitle() != null) {
						    hl7recordRow.append(attachmentCounter).append(hl7Details.getfieldSeparator()).append(attachment.gettitle()).append(hl7Details.getfieldSeparator());
						} else {
						    hl7recordRow.append(attachmentCounter).append(hl7Details.getfieldSeparator()).append(attachment.getfileName()).append(hl7Details.getfieldSeparator());
						}

						hl7recordRow.append(encodedString);

						if (attachmentCounter < attachments.size()) {
						    hl7recordRow.append(System.getProperty("line.separator"));
						    hl7recordRow.append(segment.getsegmentName()).append(hl7Details.getfieldSeparator());
						    attachmentCounter += 1;
						}
					    }

					}*/
				    } else {

					if (!"".equals(element.getdefaultValue()) && element.getdefaultValue() != null) {
					    if ("~currDate~".equals(element.getdefaultValue())) {
						SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd");
						String date = date_format.format(batchDetails.getDateCreated());
						hl7recordRow.append(date);
					    } else {
						hl7recordRow.append(element.getdefaultValue());
					    }

					} else {

					    /* Get the element components */
					    List<HL7ElementComponents> hl7Components = configurationManager.getHL7ElementComponents(element.getId());

					    if (!hl7Components.isEmpty()) {
						int counter = 1;
						for (HL7ElementComponents component : hl7Components) {

						    String fieldValue = "";

						    if (!"".equals(component.getDefaultValue()) && component.getDefaultValue() != null) {

							/* If the HL7 requires attachments then we need to look for the "attachments" keyword
                                                        in order to loop through and retrieve all attachments to the batch.
							 */
							if ("attachments".equals(component.getDefaultValue().toLowerCase())) {
							    /*transactionTarget targetDetails = transactionOutDAO.getTransactionDetails(transactionTargetId);
							    List<transactionAttachment> attachments = transactionInManager.getAttachmentsByTransactionId(targetDetails.gettransactionInId());

							    if (!attachments.isEmpty()) {
								Integer attachmentCounter = 1;
								for (transactionAttachment attachment : attachments) {
								    fileSystem attachDir = new fileSystem();
								    attachDir.setDirByName(attachment.getfileLocation() + "/");
								    File f = new File(attachDir.getDir() + attachment.getfileName());
								    byte[] bytes = attachDir.loadFile(f);
								    byte[] encoded = Base64.encode(bytes);
								    fieldValue = new String(encoded);
								}
							    }*/
							} else {
							    fieldValue = component.getDefaultValue();
							}

						    } else {
							String colName = new StringBuilder().append("f").append(component.getfieldValue()).toString();

							fieldValue = BeanUtils.getProperty(records.get(0), colName);

							if (fieldValue == null) {
							    fieldValue = "";
							} else if ("null".equals(fieldValue)) {
							    fieldValue = "";
							} else if (fieldValue.isEmpty()) {
							    fieldValue = "";
							} else if (fieldValue.length() == 0) {
							    fieldValue = "";
							}
						    }

						    if (!"".equals(component.getfieldDescriptor()) && component.getfieldDescriptor() != null) {
							hl7recordRow.append(component.getfieldDescriptor()).append(" ").append(fieldValue);
						    } else {
							hl7recordRow.append(fieldValue);
						    }

						    if (!"".equals(component.getFieldAppendText()) && component.getFieldAppendText() != null) {
							hl7recordRow.append(" ").append(component.getFieldAppendText());
						    }

						    if (counter < hl7Components.size()) {
							hl7recordRow.append(hl7Details.getcomponentSeparator());
							counter += 1;
						    }

						}

					    } else {
						hl7recordRow.append("");
					    }

					}
				    }

				    if (elementCounter < hl7Elements.size()) {
					hl7recordRow.append(hl7Details.getfieldSeparator());
					elementCounter += 1;
				    }

				}

				hl7recordRow.append(System.getProperty("line.separator"));
			    }

			}

			if (!"".equals(hl7recordRow.toString())) {
			    try {
				if (encrypt == true) {
				    byte[] encoded = Base64.encode(hl7recordRow.toString().getBytes());
				    fw.write(new String(encoded));
				} else {
				    fw.write(hl7recordRow.toString());
				}

			    } catch (IOException ex) {
				throw new IOException(ex);
			    }
			}

			fw.close();

		    }

		}

	    } //JSON File Type
	    else if (json) {
		StringBuilder sb = new StringBuilder("");
		sb.append("{");
		
		for(transactionOutRecords record : records) {

		    for (configurationFormFields field : formFields) {
			String colName = new StringBuilder().append("f").append(field.getFieldNo()).toString();

			try {
			    String fieldValue = BeanUtils.getProperty(record, colName);

			    if (fieldValue == null) {
				fieldValue = "";
			    } else if ("null".equals(fieldValue)) {
				fieldValue = "";
			    } else if (fieldValue.isEmpty()) {
				fieldValue = "";
			    } else if (fieldValue.length() == 0) {
				fieldValue = "";
			    }

			    //if (i == maxFieldNo) {
			    //New
			    if (field.getFieldNo() == maxFieldNo) {
				sb.append(recordRow).append("\"").append(field.getFieldDesc()).append("\":").append("\"").append(fieldValue).append("\"");
			    } else {
				sb.append(recordRow).append("\"").append(field.getFieldDesc()).append("\":").append("\"").append(fieldValue).append("\"").append(",");
			    }

			} catch (IllegalAccessException ex) {
			    Logger.getLogger(transactionOutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvocationTargetException ex) {
			    Logger.getLogger(transactionOutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
			} catch (NoSuchMethodException ex) {
			    Logger.getLogger(transactionOutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
			}
		    }
		}
		
		sb.append("}");

		if ("".equals(sb.toString()) || "{}".equals(sb.toString())) {
		    recordRow = "";
		} else {
		    recordRow = sb.toString();
		}

		if (!"".equals(recordRow)) {
		    try {
			if (encrypt == true) {
			    byte[] encoded = Base64.encode(recordRow.getBytes());
			    fw.write(new String(encoded));
			} else {
			    fw.write(recordRow);
			}

			fw.close();
		    } catch (IOException ex) {
			throw new IOException(ex);
		    }
		}
	    } 
	    else {

			StringBuilder sb = new StringBuilder("");

			boolean addHeader = true;

			if(addHeader) {
				for (configurationFormFields field : formFields) {
					if (field.getUseField() == true) {
						if (field.getFieldNo() == maxFieldNo) {
							sb.append(field.getFieldDesc().trim()).append(System.getProperty("line.separator"));
						} else {
							sb.append(field.getFieldDesc().trim()).append(delimChar);
						}
					}
				}
			}

			for(transactionOutRecords record : records) {

				for (configurationFormFields field : formFields) {

					//String colName = new StringBuilder().append("f").append(i).toString();
					//NEW
					if (field.getUseField() == true) {
						//NEW
						String colName = new StringBuilder().append("f").append(field.getFieldNo()).toString();

						try {
							String fieldValue = BeanUtils.getProperty(record, colName);

							if (fieldValue == null) {
								fieldValue = "";
							} else if ("null".equals(fieldValue)) {
								fieldValue = "";
							} else if (fieldValue.isEmpty()) {
								fieldValue = "";
							} else if (fieldValue.length() == 0) {
								fieldValue = "";
							}

							//if (i == maxFieldNo) {
							//New
							if (field.getFieldNo() == maxFieldNo) {
								sb.append(recordRow).append(fieldValue).append(System.getProperty("line.separator"));
							} else {
								sb.append(recordRow).append(fieldValue).append(delimChar);
							}

						} catch (IllegalAccessException ex) {
							Logger.getLogger(transactionOutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
						} catch (InvocationTargetException ex) {
							Logger.getLogger(transactionOutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
						} catch (NoSuchMethodException ex) {
							Logger.getLogger(transactionOutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
						}
						//NEW
					}
				}
			}

			if ("".equals(sb.toString())) {
				recordRow = "";
			} else {
				recordRow = sb.toString();
			}

			if (!"".equals(recordRow)) {
				try {
					if (encrypt == true) {
						byte[] encoded = Base64.encode(recordRow.getBytes());
						fw.write(new String(encoded));
					} else {
						fw.write(recordRow);
					}
					fw.close();
				} catch (IOException ex) {
					throw new IOException(ex);
				}
			}
	    }
	}
	strFileLoc = file.getAbsolutePath();
	return strFileLoc;
    }

    @Override
    public List<batchDownloads> getdownloadableBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception {
	return transactionOutDAO.getdownloadableBatches(userId, orgId, fromDate, toDate);
    }

    @Override
    public void updateLastDownloaded(int batchId) throws Exception {
	transactionOutDAO.updateLastDownloaded(batchId);
    }

    /**
     * The 'generateSystemOutboundSummary' function will return the summary object for outbound system batches
     *
     * @return This function will return a systemSummary object
     */
    @Override
    public systemSummary generateSystemOutboundSummary() {

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

	    Integer batchesThisHour = transactionOutDAO.getAllBatches(thishour.getTime(), nexthour.getTime(), "").size();

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

	    Integer batchesToday = transactionOutDAO.getAllBatches(starttoday.getTime(), starttomorrow.getTime(), "").size();

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

	    Integer batchesThisWeek = transactionOutDAO.getAllBatches(thisweek.getTime(), nextweek.getTime(), "").size();

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
    public List<batchDownloads> getAllBatches(Date fromDate, Date toDate, String batchName) throws Exception {
	return transactionOutDAO.getAllBatches(fromDate, toDate, batchName);
    }

    @Override
    public boolean searchTransactions(Transaction transaction, String searchTerm) throws Exception {

	boolean matchFound = false;

	String lcaseSearchTerm = searchTerm.toLowerCase();

	if (transaction.getmessageTypeName() != null && transaction.getmessageTypeName().toLowerCase().matches(".*" + lcaseSearchTerm + ".*")) {
	    matchFound = true;
	}

	if (transaction.getstatusValue() != null && transaction.getstatusValue().toLowerCase().matches(".*" + lcaseSearchTerm + ".*")) {
	    matchFound = true;
	}

	if (transaction.getsourceOrgFields().size() > 0) {

	    for (int i = 0; i < transaction.getsourceOrgFields().size(); i++) {
		if (transaction.getsourceOrgFields().get(i).getFieldValue() != null && transaction.getsourceOrgFields().get(i).getFieldValue().toLowerCase().matches(".*" + lcaseSearchTerm + ".*")) {
		    matchFound = true;
		}
	    }
	}

	if (transaction.gettargetOrgFields().size() > 0) {

	    for (int i = 0; i < transaction.gettargetOrgFields().size(); i++) {
		if (transaction.gettargetOrgFields().get(i).getFieldValue() != null && transaction.gettargetOrgFields().get(i).getFieldValue().toLowerCase().matches(".*" + lcaseSearchTerm + ".*")) {
		    matchFound = true;
		}
	    }
	}

	return matchFound;

    }

    @Override
    public boolean searchTransactionsByMessageType(pendingDeliveryTargets transaction, String searchTerm) throws Exception {
	boolean matchFound = false;

	String lcaseSearchTerm = searchTerm.toLowerCase();

	if (transaction.getMessageType() != null && transaction.getMessageType().toLowerCase().matches(".*" + lcaseSearchTerm + ".*")) {
	    matchFound = true;
	}

	if (transaction.getOrgDetails().toLowerCase().matches(".*" + lcaseSearchTerm + ".*")) {
	    matchFound = true;
	}

	return matchFound;
    }

    @Override
    public boolean searchPendingTransactions(Transaction transaction, String searchTerm) throws Exception {

	boolean matchFound = false;

	String lcaseSearchTerm = searchTerm.toLowerCase();

	if (transaction.getbatchName() != null && transaction.getbatchName().toLowerCase().matches(".*" + lcaseSearchTerm + ".*")) {
	    matchFound = true;
	}

	if (transaction.getsourceOrgFields().size() > 0) {

	    for (int i = 0; i < transaction.getsourceOrgFields().size(); i++) {
		if (transaction.getsourceOrgFields().get(i).getFieldValue() != null && transaction.getsourceOrgFields().get(i).getFieldValue().toLowerCase().matches(".*" + lcaseSearchTerm + ".*")) {
		    matchFound = true;
		}
	    }
	}

	if (transaction.getpatientFields().size() > 0) {

	    for (int i = 0; i < transaction.getpatientFields().size(); i++) {
		if (transaction.getpatientFields().get(i).getFieldValue() != null && transaction.getpatientFields().get(i).getFieldValue().toLowerCase().matches(".*" + lcaseSearchTerm + ".*")) {
		    matchFound = true;
		}
	    }
	}

	return matchFound;

    }

    @Override
    public Integer writeOutputToTextFile(configurationTransport transportDetails, Integer batchDownLoadId, String filePathAndName, String fieldNos, Integer batchUploadId) throws Exception {
	return transactionOutDAO.writeOutputToTextFile(transportDetails, batchDownLoadId, filePathAndName, fieldNos, batchUploadId);
    }

    @Override
    public String generateDLBatchName(String utbatchName, configurationTransport transportDetails, utConfiguration configDetails,
	    batchUploads batchUploadDetails, Date date) throws Exception {

	DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");

	String batchName = "";
	String sourceFileName = batchUploadDetails.getOriginalFileName();
	
	if (transportDetails.gettargetFileName() == null) {
	    // Create the batch name (OrgId+MessageTypeId)
	    if(!"".equals(utbatchName)) {
		batchName = utbatchName;
	    }
	    else {
		batchName = new StringBuilder().append(configDetails.getorgId()).append(configDetails.getMessageTypeId()).toString();
	    }
	} 
	else if ("".equals(transportDetails.gettargetFileName())) {
	    if(!"".equals(utbatchName)) {
		batchName = utbatchName;
	    }
	    else {
		batchName = new StringBuilder().append(configDetails.getorgId()).append(configDetails.getMessageTypeId()).toString();
	    }
	}
	else if ("USE SOURCE FILE".equals(transportDetails.gettargetFileName())) {
	    int lastPeriodPos = sourceFileName.lastIndexOf(".");

	    if (lastPeriodPos <= 0) {
		batchName = sourceFileName;
	    } else {
		batchName = sourceFileName.substring(0, lastPeriodPos);
	    }

	} else {
	    batchName = transportDetails.gettargetFileName();

	}

	/* Append the date time */
	if (transportDetails.getappendDateTime() == true) {
	    batchName = new StringBuilder().append(batchName).append(dateFormat.format(date)).toString();
	}

	return batchName;
    }

    @Override
    public List<ConfigOutboundForInsert> setConfigOutboundForInsert(int configId, int batchDownloadId) throws Exception {
	return transactionOutDAO.setConfigOutboundForInsert(configId, batchDownloadId);
    }

    @Override
    public String getConfigFieldsForOutput(Integer configId) throws Exception {
	transactionOutDAO.setSessionLength();
	return transactionOutDAO.getConfigFieldsForOutput(configId);
    }

    @Override
    public Integer runValidations(Integer batchDownloadId, Integer configId) throws Exception {
	
	Integer totalValidationErrors = 0;
	Integer errorCount = 0;
	
	//1. we get validation types
	//2. we skip 1 as that is not necessary
	//3. we skip date (4) as there is no isDate function in MySQL
	//4. we skip the ids that are not null as Mysql will bomb out checking character placement
	//5. back to date, we grab transaction info and we loop (errId 7)

	/**
	 * MySql RegEXP validate numeric - ^-?[0-9]+[.]?[0-9]*$|^-?[.][0-9]+$ validate email - ^[a-z0-9\._%+!$&*=^|~#%\'`?{}/\-]+@[a-z0-9\.-]+\.[a-z]{2,6}$ or ^[A-Z0-9._%-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$ validate url - ^(https?://)?([\da-z.-]+).([a-z0-9])([0-9a-z]*)*[/]?$ - need to fix not correct - might have to run in java as mysql is not catching all. validate phone - should be no longer than 11 digits ^[0-9]{7,11}$ validate date - doing this in java
	 *
	 */
	//TODO was hoping to have one SP but concat in SP not setting and not catching errors correctly. Need to recheck
	List<configurationFormFields> configurationFormFields = configurationTransportManager.getCffByValidationType(configId, 0);

	if(configurationFormFields != null) {
	    if(!configurationFormFields.isEmpty()) {
		String validation = "";
		Integer validationTypeId = 0; 
	
		for (configurationFormFields cff : configurationFormFields) {
		    errorCount = 0;
		    
		    validationTypeId = cff.getValidationType();
		    
		    switch (cff.getValidationType()) {
			case 1:
			    break; // no validation
			//email calling SQL to validation and insert - one statement
			case 2:
			    validation = "Email";
			    errorCount = genericValidation(cff, validationTypeId, batchDownloadId, "");
			    break;
			//phone  calling SP to validation and insert - one statement 
			case 3:
			    validation = "Phone Number";
			    errorCount = genericValidation(cff, validationTypeId, batchDownloadId, "");
			    break;
			// need to loop through each record / each field
			case 4:
			    validation = "Date";
			    errorCount = genericValidation(cff, validationTypeId, batchDownloadId, "");
			    break;
			//numeric   calling SQL to validation and insert - one statement      
			case 5:
			    validation = "Numeric";
			    errorCount = genericValidation(cff, validationTypeId, batchDownloadId, "");
			    break;
			//url - need to rethink as regExp is not validating correctly
			case 6:
			    validation = "URL";
			    errorCount = genericValidation(cff, validationTypeId, batchDownloadId, "");
			    break;
			//anything new we hope to only have to modify sp
			default:
			    validation = "";
			    //errorCount = genericValidation(cff, validationTypeId, batchDownloadId, "");
			    break;
		    }
		    
		    if(errorCount > 0) {
			totalValidationErrors = totalValidationErrors + errorCount;
			
			//log batch activity
			batchdownloadactivity ba = new batchdownloadactivity();
			ba.setActivity("Validation Error. Validation Type: " + validation + " for configId: " + configId + " Field No: " + cff.getFieldNo());
			ba.setBatchDownloadId(batchDownloadId);
			transactionOutDAO.submitBatchActivityLog(ba);
		    }

		}
	    }
	}
	
	return totalValidationErrors;
    }
    
    @Override
    public Integer genericValidation(configurationFormFields cff, Integer validationTypeId, Integer batchDownloadId, String regEx) {
	return transactionOutDAO.genericValidation(cff, validationTypeId, batchDownloadId, regEx);
    }


    /**
     * this will select an upload batch that has status of 24, check its config to make sure it is for mass translation and start translating utConfiguration that is for mass translation
     */
    @Override
    public void processMassOutputBatches() throws Exception {

	boolean run = true;
	List<batchDownloads> batchInProcess = getDLBatchesByStatusIds(Arrays.asList(25, 30));

	//we check time stamp to see how long that file has been processing
	if (!batchInProcess.isEmpty()) {
	    LocalDateTime d1;
	    LocalDateTime d2;
	    long diffHours;

	    for (batchDownloads runningOutputBatch : batchInProcess) {
		batchDownloads stuckBatchDetails = getBatchDetails(runningOutputBatch.getId());

		d1 = LocalDateTime.ofInstant(stuckBatchDetails.getStartDateTime().toInstant(), ZoneId.systemDefault());
		d2 = LocalDateTime.now();

		diffHours = java.time.Duration.between(d1, d2).toHours();

		boolean retry = false;
		//we check the status, if 30, we retry right away
		if (stuckBatchDetails.getStatusId() == 30) {
		    retry = true;
		} else if (diffHours >= 1) {
		    retry = true;
		}

		if (retry) {
		    // batch running for over 1 hour or at TPE, we need to see if it has been re-process *
		    //see if this batch has been retried
		    batchDLRetry br = getBatchDLRetryByDownloadId(stuckBatchDetails.getId(), 4);
		    String subject = "Retrying DL Batch - processMassOutputBatches";
		    String msgBody = "Download Batch " + stuckBatchDetails.getId() + " (" + stuckBatchDetails.getUtBatchName() + ") will be retried.";
		    if (br == null) {
			//we retry this batch
			br = new batchDLRetry();
			br.setFromStatusId(4);
			br.setBatchDownloadId(stuckBatchDetails.getId());
			saveBatchDLRetry(br);
			//reset status for batch so it will get pick up again
			//reprocess means resetting it to 61 and insert an entry into batchDLRetry so we know
			updateTargetBatchStatus(stuckBatchDetails.getId(), 61, "startDateTime");
			
			//having log in new table and checking userActivity as if it is reset manually by user and gets stuck again it wont' retry and we want it to retry at least once each time it is reset
			try {
			    //log batch activity
			    batchdownloadactivity ba = new batchdownloadactivity();
			    ba.setActivity("System Set DL Batch (Id: " + stuckBatchDetails.getId() + ") To Retry - Processing");
			    ba.setBatchDownloadId(stuckBatchDetails.getId());
			    transactionOutDAO.submitBatchActivityLog(ba);
			    
			} catch (Exception ex) {
			    ex.printStackTrace();
			    System.err.println("Set DL Batch (Id: " + stuckBatchDetails.getId() + ") - insert user log " + ex.toString());
			}
		    } else {
			try {
			    //log batch activity
			    batchdownloadactivity ba = new batchdownloadactivity();
			    ba.setActivity("System Set Batch (Id: " + stuckBatchDetails.getId() + ") to Status 58 - Processing");
			    ba.setBatchDownloadId(stuckBatchDetails.getId());
			    transactionOutDAO.submitBatchActivityLog(ba);
			} catch (Exception ex) {
			    ex.printStackTrace();
			    System.err.println("Set DL Batch (Id: " + stuckBatchDetails.getId() + ") - insert user log " + ex.toString());
			}
			subject = "Batch set to Need to Review - processMassOutput ";
			msgBody = "Batch " + stuckBatchDetails.getId() + " (" + stuckBatchDetails.getUtBatchName() + ") needs to be reviewed.";
			//58
			updateTargetBatchStatus(stuckBatchDetails.getId(), 58, "endDateTime");
		    }
		    //we notify admin
		    //we also notify admin
		    mailMessage mail = new mailMessage();
		    mail.settoEmailAddress(myProps.getProperty("admin.email"));
		    mail.setfromEmailAddress("support@health-e-link.net");
		    mail.setmessageSubject(subject + " " + myProps.getProperty("server.identity"));
		    StringBuilder emailBody = new StringBuilder();
		    emailBody.append("<br/>Current Time " + d2.toString());
		    emailBody.append("<br/><br/>" + msgBody);
		    emailBody.append("<br/><br/>" + batchInProcess.size() + " download batch(es) with status " + batchInProcess.get(0).getStatusId() + " in queue.<br/>");
		    mail.setmessageBody(emailBody.toString());
		    emailMessageManager.sendEmail(mail);
		}
	    }
	}

	if (run) {
	    //0. grab all mass batches with BPO (61)
	    try {
		List<batchDownloads> batches = getDLBatchesByStatusIds(Arrays.asList(61));
		if (batches != null && batches.size() != 0) {

		    //Parallel processing of batches
		    for (batchDownloads batch : batches) {
			executor.execute(new Runnable() {
			    @Override
			    public void run() {
				try {
				    updateTargetBatchStatus(batch.getId(), 25, "startDateTime");
				    processMassOutputBatch(batch);
				} catch (Exception ex) {
				    Logger.getLogger(transactionOutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			    }
			});
		    }
		}
	    } catch (Exception ex1) {
		ex1.printStackTrace();
		//send email to admin
		throw new Exception("Error occurred for processMassOutputBatches - ", ex1);
	    }
	}
    }

    @Override
    public Integer processMassOutputBatch(batchDownloads batchDownload) throws Exception {
	
	batchdownloadactivity ba = new batchdownloadactivity();
	String patientId = "";
	String patientDOB = "";
	String patientFirstname = "";
	String patientLastname = "";
	
	//Get a full list of macros
	List<Macros> macroList = configurationManager.getMacros();
	
	try {
	    ba = new batchdownloadactivity();
	    ba.setActivity("processMassOutputBatch for batch (Id: " + batchDownload.getId() + ")");
	    ba.setBatchDownloadId(batchDownload.getId());
	    transactionOutDAO.submitBatchActivityLog(ba);
	    
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("batchDownload - insert user log for batch (Id: " + batchDownload.getId() + ") " + ex.toString());
	}

	if ("".equals(batchDownload.getConfigId()) || batchDownload.getConfigId() == 0) {
	    //error batch
	    updateTargetBatchStatus(batchDownload.getId(), 30, "endDateTime");
	    //email admin
	    transactionInManager.sendEmailToAdmin(("No targets were found for this config for this download batch - " + batchDownload.getId()), "Mass process output config error");
	    return 1;
	}
	
	//get upload details    
	batchUploads batchUploadDetails = transactionInManager.getBatchDetails(batchDownload.getBatchUploadId());
	utConfiguration uploadConfigDetails = configurationManager.getConfigurationById(batchUploadDetails.getConfigId());
	
	Organization sendingOrgDetails = organizationManager.getOrganizationById(batchUploadDetails.getOrgId());
	
	//Get the error handling for the source config.
	configurationTransport sourceConfigTransportDetails = configurationTransportManager.getTransportDetails(batchUploadDetails.getConfigId());

	//Create target tables
	transactionOutDAO.createTargetBatchTables(batchDownload.getId(), batchDownload.getConfigId());
	
	ba = new batchdownloadactivity();
	ba.setActivity("All target tables were created for batchId: " + batchDownload.getId());
	ba.setBatchDownloadId(batchDownload.getId());
	transactionOutDAO.submitBatchActivityLog(ba);

	//Load target tables
	transactionOutDAO.loadTargetBatchTables(batchDownload.getId(), batchDownload.getBatchUploadId(), batchDownload.getConfigId(), batchUploadDetails.getConfigId());
	
	ba = new batchdownloadactivity();
	ba.setActivity("All target tables were loaded for batchId: " + batchDownload.getId());
	ba.setBatchDownloadId(batchDownload.getId());
	transactionOutDAO.submitBatchActivityLog(ba);
	
	//Trim all field values again
	transactionInManager.trimFieldValues(batchDownload.getId(), true, batchDownload.getConfigId(), true);
	
	ba = new batchdownloadactivity();
	ba.setActivity("All target entries were trimmed for batchId: " + batchDownload.getId());
	ba.setBatchDownloadId(batchDownload.getId());
	transactionOutDAO.submitBatchActivityLog(ba);
	
	//Delete all batch upload tables
	transactionOutDAO.deleteBatchUploadTables(batchDownload.getBatchUploadId());
	
	ba = new batchdownloadactivity();
	ba.setActivity("All batch upload tables were deleted for upload batchId: " + batchDownload.getBatchUploadId());
	ba.setBatchDownloadId(batchDownload.getId());
	transactionOutDAO.submitBatchActivityLog(ba);

	clearDownloadBatch(batchDownload.getId());

	utConfiguration configDetails = configurationManager.getConfigurationById(batchDownload.getConfigId());
	configurationTransport transportDetails = configurationTransportManager.getTransportDetails(batchDownload.getConfigId());

	Integer statusId = 37;
	
	Integer systemErrorCount = 0;
	Integer totalErrorCount = 0;
	
	 //Step 1: check R/O
	List<configurationFormFields> reqFields = transactionInManager.getRequiredFieldsForConfig(batchDownload.getConfigId());
	
	if(reqFields != null) {
	    if(!reqFields.isEmpty()) {
		Integer reqError = 0;
		
		for (configurationFormFields cff : reqFields) {
		    reqError = insertFailedRequiredFields(cff, batchDownload.getId());
		    
		    if(reqError == 9999999) {
			systemErrorCount++; 
		     }
		     else if(reqError > 0) {
			 totalErrorCount = totalErrorCount + reqError;
			 
			 //log batch activity
			 ba = new batchdownloadactivity();
			 ba.setActivity("Required Field Error. Field No:" + cff.getFieldNo() + " Field Desc:" + cff.getFieldDesc() + " for configId:" + cff.getconfigId());
			 ba.setBatchDownloadId(batchDownload.getId());
			 transactionOutDAO.submitBatchActivityLog(ba);
		     }
		}
	    }
	}

	//Update all outbound records with missing required fields to have a status id of 14
	if(totalErrorCount > 0) {
	    updateMissingRequiredFieldStatus(batchDownload.getId());
	}
	
	//Step 2: grab the configurationDataTranslations and run cw/macros
	List<configurationDataTranslations> dataTranslations = configurationManager.getDataTranslationsWithFieldNo(batchDownload.getConfigId(), 1);
	
	if(dataTranslations != null) {
	    if(!dataTranslations.isEmpty()) {
		Integer crosswalkErrors = 0;
		Integer macroError = 0;
		String macroName = "";
		
		for (configurationDataTranslations cdt : dataTranslations) {
		    crosswalkErrors = 0;
		    macroError = 0;
		    
		    if (cdt.getCrosswalkId() != 0) {
			crosswalkErrors = transactionInManager.processCrosswalk(batchDownload.getConfigId(), batchDownload.getId(), cdt, true);

			if(crosswalkErrors == 9999999) {
			    systemErrorCount++; 
			} 
			else if(crosswalkErrors > 0) {
			    totalErrorCount = totalErrorCount + crosswalkErrors;

			    //log batch activity
			    ba = new batchdownloadactivity();
			    ba.setActivity("Crosswalk Error. CWId:" + cdt.getCrosswalkId() + " for configId:" + batchDownload.getConfigId() + " total records with CW error: " + crosswalkErrors);
			    ba.setBatchDownloadId(batchDownload.getId());
			    transactionOutDAO.submitBatchActivityLog(ba);
			} 	
		    }	
		    else if (cdt.getMacroId() != 0) {
			macroName = "";
			macroError = transactionInManager.processMacro(batchDownload.getConfigId(), batchDownload.getId(), cdt, true);

			if(macroError == 9999999) {
			    systemErrorCount++; 
			}
			else if(macroError > 0) {
			    totalErrorCount = totalErrorCount + macroError;
			    
			    if(!macroList.isEmpty()) {
				for(Macros macro : macroList) {
				    if(macro.getId() == cdt.getMacroId()) {
					macroName = macro.getMacroName().trim();
				    }
				}
			    }

			    //log batch activity
			    ba = new batchdownloadactivity();
			    ba.setActivity("Macro Error. macro: " + macroName + " macroId: " + cdt.getMacroId() + " for configId:" + batchDownload.getConfigId() + " total records with Macro error: " + macroError);
			    ba.setBatchDownloadId(batchDownload.getId());
			    transactionOutDAO.submitBatchActivityLog(ba);
			}
		    }
		} 
	    }
	}
	
	//Step 3: Check validation errors
	Integer validationErrors = runValidations(batchDownload.getId(), batchDownload.getConfigId());
	
	//Trim all field values again
	transactionInManager.trimFieldValues(batchDownload.getId(), true, batchDownload.getConfigId(), true);
	
	ba = new batchdownloadactivity();
	ba.setActivity("All final target entries were trimmed for batchId: " + batchDownload.getId());
	ba.setBatchDownloadId(batchDownload.getId());
	transactionOutDAO.submitBatchActivityLog(ba);
	
	// update status of the failed records to ERR - 14 (Only updating REQUIRED records from transactionouterrors)
	transactionInManager.updateStatusForErrorTrans(batchDownload.getId(), 14, true);
	
	totalErrorCount = totalErrorCount + validationErrors;
	
	boolean inserteReferralMessage = true;
	
	//Get the total number of errors found that are for required fields
	Integer totalRequiredErrorsFound = transactionOutDAO.getTotalErrors(batchDownload.getId());
	
	// some macros have to error in the macro and do not return macro_error, those are R errors, we add them to totalErrorCount so that populateOutboundAuditReport will run
	totalErrorCount = totalErrorCount + totalRequiredErrorsFound;
		
	
	if (totalRequiredErrorsFound > 0 && (sourceConfigTransportDetails.geterrorHandling() == 3 || transportDetails.geterrorHandling() == 3)) {
	    
	    ba = new batchdownloadactivity();
	    
	    if(transportDetails.geterrorHandling() == 3) {
		ba.setActivity("Target batch batchId:"+batchDownload.getId()+" was rejected due to finding an error and the target config error handling set to reject entire file on single error.");
	    }
	    else {
		ba.setActivity("Target batch batchId:"+batchDownload.getId()+" was rejected due to finding an error and the source config error handling set to reject entire file on single error.");
	    }
	    
	    ba.setBatchDownloadId(batchDownload.getId());
	    transactionOutDAO.submitBatchActivityLog(ba);
	    
	    updateTargetBatchStatus(batchDownload.getId(), 41, "endDateTime");
	    
	    populateOutboundAuditReport(batchDownload.getConfigId(),batchDownload.getId(), batchDownload.getBatchUploadId(),batchUploadDetails.getConfigId());
	    
	    transactionInManager.populateDroppedValues(batchDownload.getId(), batchDownload.getConfigId(), true);
	    
	    ba = new batchdownloadactivity();
	    ba.setActivity("Populate outbound Audit Report for batchId:"+batchDownload.getId());
	    ba.setBatchDownloadId(batchDownload.getId());
	    transactionOutDAO.submitBatchActivityLog(ba);
	    
	    //we need to update our totals
	    transactionInManager.updateRecordCounts(batchDownload.getId(), rejectIds, true, "totalErrorCount");
	    transactionInManager.updateRecordCounts(batchDownload.getId(), new ArrayList<>(), true, "totalRecordCount");
	    
	    if(transportDetails.isPopulateInboundAuditReport()) {
		//Update inbound file for total errors
		transactionInManager.updateRecordCountsFromAuditErrorTable(batchDownload.getBatchUploadId());
		
		//Update inbound status
		transactionInManager.updateBatchStatus(batchDownload.getBatchUploadId(), 41, "endDateTime");
		
		ba = new batchdownloadactivity();
		ba.setActivity("Populate inbound batch Audit Report and error count for batchId:"+batchDownload.getBatchUploadId()+" and reject inbound batch.");
		ba.setBatchDownloadId(batchDownload.getId());
		transactionOutDAO.submitBatchActivityLog(ba);
	    }
	    
	    return 1;
	    
	} 
	
	else {
	    
	    if(totalErrorCount > 0) {
		populateOutboundAuditReport(batchDownload.getConfigId(),batchDownload.getId(), batchDownload.getBatchUploadId(),batchUploadDetails.getConfigId());
		
		transactionInManager.populateDroppedValues(batchDownload.getId(), batchDownload.getConfigId(), true);
	    
		ba = new batchdownloadactivity();
		ba.setActivity("Populate outbound Audit Report for batchId:"+batchDownload.getId());
		ba.setBatchDownloadId(batchDownload.getId());
		transactionOutDAO.submitBatchActivityLog(ba);
		
		//we need to update our totals
		transactionInManager.updateRecordCounts(batchDownload.getId(), rejectIds, true, "totalErrorCount");
		transactionInManager.updateRecordCounts(batchDownload.getId(), new ArrayList<>(), true, "totalRecordCount");
		
		if(transportDetails.isPopulateInboundAuditReport()) {
		    //Update inbound file for total errors
		    transactionInManager.updateRecordCountsFromAuditErrorTable(batchDownload.getBatchUploadId());
		    
		    ba = new batchdownloadactivity();
		    ba.setActivity("Populate inbound batch Audit Report and error count for batchId:"+batchDownload.getBatchUploadId());
		    ba.setBatchDownloadId(batchDownload.getId());
		    transactionOutDAO.submitBatchActivityLog(ba);
		}
	    }
	    
	    // Generate the file according to transportDetails 
	    // we generate output file according to encoding in transportDetails
	    // we always save an encrypted copy to archivesOut
	    //get list of config fields here
	    String configFields = getConfigFieldsForOutput(batchDownload.getConfigId());

	    //we move the file extension
	    String fileExt = transportDetails.getfileExt();

	    boolean encryptMessage = false;
	    // we only support base64 for now
	    if (transportDetails.getEncodingId() == 2) {
		encryptMessage = true;
	    }
	    
	    //Check to see if the transport type has an uploaded custom XML template to follow
	    boolean processCustomXMLTemplate = false;

	    if (transportDetails.getCcdSampleTemplate() != null) {
		if (!"".equals(transportDetails.getCcdSampleTemplate())) {
		    processCustomXMLTemplate = true;
		}
	    }
	    
	    String generatedFilePath = generateTargetFile(true, batchDownload.getId(), transportDetails, encryptMessage);
	    transportDetails.setDelimChar(messageTypeDAO.getDelimiterChar(transportDetails.getfileDelimiter()));
	    
	    //make sure we remove old file
	    File generatedFile = new File(generatedFilePath);
	    
	    if (generatedFile.exists()) {
		generatedFile.delete();
	    }
	    
	    //mysql is the fastest way to output a file, but the permissions are tricky we write 
	    //to massoutfiles where both tomcat and mysql has permission. 
	    //Then we can create, copy and delete
	    File massOutFile = new File(myProps.getProperty("ut.directory.massOutputPath") + batchDownload.getUtBatchName() + "." + fileExt);

	    //check to see if file is there, if so remove old file
	    if (massOutFile.exists()) {
		massOutFile.delete();
	    }
	    
	    if (processCustomXMLTemplate) {

		configFields = transactionOutDAO.getCustomXMLFieldsForOutput(batchDownload.getConfigId());

		List recordsToWrite = transactionOutDAO.getOutputForCustomTargetFile(transportDetails, batchDownload.getId(), configFields,batchDownload.getBatchUploadId());
		
		if (recordsToWrite != null) {
		    Organization orgDetails = organizationManager.getOrganizationById(configurationManager.getConfigurationById(transportDetails.getconfigId()).getorgId());
		    
		    String ccdSampleTemplate = transportDetails.getCcdSampleTemplate();

		    Path path = Paths.get(myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/templates/" + ccdSampleTemplate);
		    String ccdSampleContent = new String(Files.readAllBytes(path));

		    Path newFilePath = Paths.get(myProps.getProperty("ut.directory.massOutputPath") + batchDownload.getUtBatchName() + "." + fileExt);
		    
		    Files.write(newFilePath, ccdSampleContent.getBytes());

		    String contentToUpdate = new String(Files.readAllBytes(newFilePath));

		    // Get the configurationCCDElements
		    List<configurationCCDElements> ccdElements = configurationManager.getCCDElements(batchDownload.getConfigId());

		    if (!ccdElements.isEmpty()) {

			//Get repeating section
			StringBuilder sb = new StringBuilder();
			String repeatingSectionCopy = "";
			String repeatingSection = "";
			
			if(contentToUpdate.contains("[CDATA]")) {
			    repeatingSection = contentToUpdate.substring(contentToUpdate.indexOf("[CDATA]") + 7, contentToUpdate.indexOf("[/CDATA]"));
			}
			else {
			    repeatingSection = contentToUpdate;
			}
			
			Iterator recordIterator = recordsToWrite.iterator();
			Integer elementCounter = 0;
			Integer recordCounter = 0;
			while (recordIterator.hasNext()) {
			    recordCounter++;
			    Object recordrow[] = (Object[]) recordIterator.next();
			    repeatingSectionCopy = repeatingSection;
			    
			    elementCounter = 0;
			    for (configurationCCDElements element : ccdElements) {
				
				String fieldValue = "";
				
				if(element.getElement().contains("[@sendingOrg")) {
				    if(element.getElement().equals("[@sendingOrgAddress@]")) {
					if(sendingOrgDetails.getAddress() == null) {
					    fieldValue = "";
					}
					else if("null".equals(sendingOrgDetails.getAddress().trim())) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getAddress().trim().isEmpty()) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getAddress().trim().length() == 0) {
					    fieldValue = "";
					}
					else {
					    fieldValue = sendingOrgDetails.getAddress().trim();
					}
				    }
				    else if(element.getElement().equals("[@sendingOrgCity@]")) {
					if(sendingOrgDetails.getCity() == null) {
					    fieldValue = "";
					}
					else if("null".equals(sendingOrgDetails.getCity().trim())) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getCity().trim().isEmpty()) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getCity().trim().length() == 0) {
					    fieldValue = "";
					}
					else {
					    fieldValue = sendingOrgDetails.getCity().trim();
					}
				    }
				    else if(element.getElement().equals("[@sendingOrgState@]")) {
					if(sendingOrgDetails.getState() == null) {
					    fieldValue = "";
					}
					else if("null".equals(sendingOrgDetails.getState().trim())) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getState().trim().isEmpty()) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getState().trim().length() == 0) {
					    fieldValue = "";
					}
					else {
					    fieldValue = sendingOrgDetails.getState().trim();
					}
				    }
				    else if(element.getElement().equals("[@sendingOrgZipCode@]")) {
					if(sendingOrgDetails.getPostalCode() == null) {
					    fieldValue = "";
					}
					else if("null".equals(sendingOrgDetails.getPostalCode().trim())) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getPostalCode().trim().isEmpty()) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getPostalCode().trim().length() == 0) {
					    fieldValue = "";
					}
					else {
					    fieldValue = sendingOrgDetails.getPostalCode().trim();
					}
				    }
				    else if(element.getElement().equals("[@sendingOrgPhone@]")) {
					if(sendingOrgDetails.getPhone() == null) {
					    fieldValue = "";
					}
					else if("null".equals(sendingOrgDetails.getPhone().trim())) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getPhone().trim().isEmpty()) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getPhone().trim().length() == 0) {
					    fieldValue = "";
					}
					else {
					    fieldValue = sendingOrgDetails.getPhone().trim();
					    StringBuilder sb1 = new StringBuilder(element.getDefaultValue().trim());
					    sb1.append(" ").append(fieldValue);
					    fieldValue = sb1.toString();
					}
				    }
				    else if(element.getElement().equals("[@sendingOrgName@]")) {
					if(sendingOrgDetails.getOrgName() == null) {
					    fieldValue = "";
					}
					else if("null".equals(sendingOrgDetails.getOrgName().trim())) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getOrgName().trim().isEmpty()) {
					    fieldValue = "";
					}
					else if(sendingOrgDetails.getOrgName().trim().length() == 0) {
					    fieldValue = "";
					}
					else {
					    fieldValue = sendingOrgDetails.getOrgName().trim();
					}
				    }

				}
				else {
				    if ("~currDate~".equals(element.getDefaultValue())) {
					SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd");
					String date = date_format.format(new Date());

					fieldValue = date;
				    } 
				    else if(!"".equals(element.getDefaultValue().trim()) && "".equals(element.getFieldValue())) {
					fieldValue = element.getDefaultValue();
				    }
				    else {
					fieldValue = (String) recordrow[elementCounter];
					elementCounter += 1;

					if (fieldValue == null) {
					    fieldValue = "";
					} else if ("null".equals(fieldValue)) {
					    fieldValue = "";
					} else if (fieldValue.isEmpty()) {
					    fieldValue = "";
					} else if (fieldValue.length() == 0) {
					    fieldValue = "";
					}

					if(!"".equals(fieldValue) && !"".equals(element.getDefaultValue().trim())) {
					    StringBuilder sb1 = new StringBuilder(element.getDefaultValue().trim());
					    sb1.append(" ").append(fieldValue);
					    fieldValue = sb1.toString();
					}
				    }
				}
				
				if(element.getElement().equals("[@patientFirstName@]")) {
				    patientFirstname = fieldValue;
				}
				else if(element.getElement().equals("[@patientLastName@]")) {
				    patientLastname = fieldValue;
				}
				else if(element.getElement().equals("[@patientID@]")) {
				    patientId = fieldValue;
				}
				else if(element.getElement().equals("[@patientDOB@]")) {
				    patientDOB = fieldValue;
				}
				
				repeatingSectionCopy = repeatingSectionCopy.replace(element.getElement(), fieldValue);

			    }
			    if (transportDetails.getfileType() == 12) {
				sb.append(repeatingSectionCopy);
				if (recordCounter != recordsToWrite.size()) {
				    sb.append(",");
				}
			    } else {
				sb.append(repeatingSectionCopy).append(System.getProperty("line.separator"));
			    }
			}

			contentToUpdate = contentToUpdate.replace(repeatingSection, sb.toString()).replace("[CDATA]", "").replace("[/CDATA]", "");

		    }

		    Files.write(newFilePath, contentToUpdate.getBytes());
		    
		    ba = new batchdownloadactivity();
		    ba.setActivity("Target XML file was written file:" + newFilePath);
		    ba.setBatchDownloadId(batchDownload.getId());
		    transactionOutDAO.submitBatchActivityLog(ba);
		}

	    } else {
			ba = new batchdownloadactivity();
			ba.setActivity("Writing records to output file:" + myProps.getProperty("ut.directory.massOutputPath") + batchDownload.getUtBatchName() + "." + fileExt);
			ba.setBatchDownloadId(batchDownload.getId());
			transactionOutDAO.submitBatchActivityLog(ba);

			boolean isExcel = false;
			String finalFileExt = "";
			if("xlsx".equals(fileExt) || "xls".equals(fileExt)) {
				finalFileExt = fileExt;
				fileExt = "csv";
				isExcel = true;

				if(!",".equals(transportDetails.getDelimChar())) {
					transportDetails.setDelimChar(",");
				}
			}

			Integer writeOutCome = writeOutputToTextFile(transportDetails, batchDownload.getId(), myProps.getProperty("ut.directory.massOutputMySQLPath") + batchDownload.getUtBatchName() + "." + fileExt, configFields,batchDownload.getBatchUploadId());

			//Need to convert the csv into xlsx file
			if(isExcel) {
				XSSFWorkbook workBook = new XSSFWorkbook();
				XSSFSheet sheet = workBook.createSheet("sheet1");

				String currentLine=null;
				int RowNum=0;
				BufferedReader br = new BufferedReader(new FileReader(myProps.getProperty("ut.directory.massOutputMySQLPath") + batchDownload.getUtBatchName() + "." + fileExt));
				while ((currentLine = br.readLine()) != null) {
					String str[] = currentLine.split(",");
					XSSFRow currentRow=sheet.createRow(RowNum);
					for(int i=0;i<str.length;i++){
						currentRow.createCell(i).setCellValue(str[i]);
					}
					RowNum++;
				}

				FileOutputStream fileOutputStream =  new FileOutputStream(myProps.getProperty("ut.directory.massOutputMySQLPath") + batchDownload.getUtBatchName() + "." + finalFileExt);
				workBook.write(fileOutputStream);
				fileOutputStream.close();

				//Delete csv file
				File csvFile = new File(myProps.getProperty("ut.directory.massOutputMySQLPath") + batchDownload.getUtBatchName() + "." + fileExt);
				if(csvFile.exists()) {
					csvFile.delete();
				}

				fileExt = finalFileExt;
			}
	    }

	    if (!massOutFile.exists()) {
			//we induce time because file is not done writing
			TimeUnit.SECONDS.sleep(30);
	    }
	    //check one more time to be safe
	    if (!massOutFile.exists()) {
			//we induce time because file is not done writing
			TimeUnit.SECONDS.sleep(30);
	    }
	    
	    if(massOutFile.exists()) {
			ba = new batchdownloadactivity();
			ba.setActivity("MassOutFile was created:" + massOutFile.getAbsolutePath());
			ba.setBatchDownloadId(batchDownload.getId());
			transactionOutDAO.submitBatchActivityLog(ba);
	    }

	    //cp file to archiveOut and correct putput folder
	    File archiveFile = new File(myProps.getProperty("ut.directory.utRootDir") + "archivesOut/" + batchDownload.getUtBatchName() + "." + fileExt);

	    //at this point, message it not encrypted
	    //we always encrypt the archive file
	    String strEncodedFile = filemanager.encodeFileToBase64Binary(massOutFile);
	    if (archiveFile.exists()) {
		archiveFile.delete();
	    }
	    //write to archive folder
	    filemanager.writeFile(archiveFile.getAbsolutePath(), strEncodedFile);
	    
	    ba = new batchdownloadactivity();
	    ba.setActivity("Archive file was created:" + archiveFile.getAbsolutePath());
	    ba.setBatchDownloadId(batchDownload.getId());
	    transactionOutDAO.submitBatchActivityLog(ba);
	    
	    //clear out string so a gigantic string is not in memory
	    strEncodedFile = "";

	    // if we don't need to encrypt file for users to download
	    if (!encryptMessage) {
		Files.copy(massOutFile.toPath(), generatedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	    } else { //we copy the encrypted file over
		Files.copy(archiveFile.toPath(), generatedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	    }

	    //If the transport method File Drop (13) and associated to a eReferral Registry and Configuration
	    if(transportDetails.gettransportMethodId() == 13 && transportDetails.getHelRegistryConfigId() > 0 && !"".equals(transportDetails.getHelSchemaName()) && transportDetails.getHelRegistryId() > 0) {
		inserteReferralMessage = false;
		
		//Need to get the health-e-link registry details
		Organization utOrgDetails = organizationManager.getOrganizationById(batchDownload.getOrgId());
		
		if(utOrgDetails.getHelRegistryId() > 0 && !utOrgDetails.getHelRegistrySchemaName().equals("")) {
		    helRegistry registryDetails = helregistrymanager.getRegistryDetails("registries",utOrgDetails.getHelRegistryId());
		    
		    if(!registryDetails.getRegistryName().equals("")) {
			String registryFolderName = registryDetails.getRegistryName().toLowerCase().replaceAll(" ","-");
			
			//Check to see if a submitted message entry was made, if not we need to create one.
			submittedMessage existingRegistrySubmittedMessage = submittedmessagemanager.getSubmittedMessageBySQL(registryDetails.getDbschemaname(),batchUploadDetails.getOriginalFileName());
			
			boolean createSubmittedMessage = false;
			
			if(existingRegistrySubmittedMessage != null){
			    if(existingRegistrySubmittedMessage.getId() > 0) {
				//Check to see if the target configuraiton is just a downloadable file update
				if(transportDetails.isErgFileDownload()) {
				    submittedmessagemanager.updateSubmittedMessageDownloadableFileName(registryDetails.getDbschemaname(),batchDownload.getBatchUploadId(),batchDownload.getOutputFileName());
				    
				    ba = new batchdownloadactivity();
				    ba.setActivity("Updated eReferral uploaded file entry for messageId:" + existingRegistrySubmittedMessage.getId());
				    ba.setBatchDownloadId(batchDownload.getId());
				    transactionOutDAO.submitBatchActivityLog(ba);
				}
				else {
				    //Need to update the Registry submitted message entry to capture the created file name
				    submittedmessagemanager.updateSubmittedMessage(registryDetails.getDbschemaname(),batchDownload.getBatchUploadId(),batchDownload.getOutputFileName(),utOrgDetails.getHelRegistryOrgId());
				    
				    ba = new batchdownloadactivity();
				    ba.setActivity("Updated eReferral online form entry for messageId:" + existingRegistrySubmittedMessage.getId());
				    ba.setBatchDownloadId(batchDownload.getId());
				    transactionOutDAO.submitBatchActivityLog(ba);
				}
			    }
			    else {
				createSubmittedMessage = true;
			    }
			}
			else {
			    createSubmittedMessage = true;
			}
			
			if(createSubmittedMessage) {
			    
			    Integer sendingOrdId = 0;
			    
			    //Get the registery organization Id
			    Organization organizationDetails = organizationManager.getOrganizationById(batchUploadDetails.getOrgId());
			    
			    sendingOrdId = organizationDetails.getHelRegistryOrgId();
			    
			    configurationMessageSpecs sourceConfigMessageSpecs = configurationManager.getMessageSpecs(batchUploadDetails.getConfigId());
			    
			    if(sourceConfigMessageSpecs != null) {
				if(sourceConfigMessageSpecs.getSourceSubOrgCol() > 0) {
				    //Pull the first record for the batch
				    String recordVal = transactionInDAO.getFieldValue("transactiontranslatedin_"+batchUploadDetails.getId(),"F"+sourceConfigMessageSpecs.getSourceSubOrgCol(), "batchUploadId", batchUploadDetails.getId());

				    try {
					if(Integer.parseInt(recordVal.trim().toLowerCase()) > 0) {
					    sendingOrdId = Integer.parseInt(recordVal.trim().toLowerCase());
					}
				    }
				    catch (Exception ex) {}
				} 
			    }

			    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
			    Date date = new Date();

			    SecureRandom random = new SecureRandom();
			    int num = random.nextInt(100000);
			    String formattedRandom = String.format("%05d", num);

			    String messageName = new StringBuilder().append(transportDetails.getHelRegistryConfigId()).append(formattedRandom).append(dateFormat.format(date)).toString();

			    submittedMessage newSubmittedMessage = new submittedMessage();
			    newSubmittedMessage.setUtBatchUploadId(batchUploadDetails.getId());
			    newSubmittedMessage.setRegistryConfigId(transportDetails.getHelRegistryConfigId());
			    newSubmittedMessage.setUploadedFileName(batchUploadDetails.getOriginalFileName());
			    newSubmittedMessage.setAssignedFileName(batchUploadDetails.getUtBatchName());
			    newSubmittedMessage.setInFileExt(FilenameUtils.getExtension(batchUploadDetails.getOriginalFileName()));
			    newSubmittedMessage.setStatusId(23);
			    newSubmittedMessage.setTransportId(8);
			    newSubmittedMessage.setSystemUserId(0);
			    newSubmittedMessage.setTotalRows(batchUploadDetails.getTotalRecordCount());
			    newSubmittedMessage.setSourceOrganizationId(sendingOrdId);
			    newSubmittedMessage.setTargetOrganizationId(utOrgDetails.getHelRegistryOrgId());
			    newSubmittedMessage.setReceivedFileName(batchDownload.getOutputFileName());
			    newSubmittedMessage.setAssignedMessageNumber(messageName);

			    submittedmessagemanager.submitSubmittedMessage(registryDetails.getDbschemaname(),newSubmittedMessage);
			    
			    ba = new batchdownloadactivity();
			    ba.setActivity("Created new eReferral message for batch upload batchId:"+batchUploadDetails.getId());
			    ba.setBatchDownloadId(batchDownload.getId());
			    transactionOutDAO.submitBatchActivityLog(ba);
			}
			
			//Check to see if there is file drop details
			//File Drop directory
			List<configurationFileDropFields> fileDropFields = configurationTransportManager.getTransFileDropDetails(transportDetails.getId());

			String fileDropDir =  registryFolderName + "/loadFiles/";

			for(configurationFileDropFields dropField : fileDropFields){
			    if(dropField.getMethod() == 2) {
				fileDropDir = dropField.getDirectory();
			    }
			}
			
			File targetFile = new File(myProps.getProperty("registry.directory.path") + fileDropDir.replace("/HELProductSuite/registries/", "") + batchDownload.getOutputFileName());
			
			if(transportDetails.isErgFileDownload()) {
			    Files.copy(massOutFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			    
			    ba = new batchdownloadactivity();
			    ba.setActivity("Moved the massOutFile file: " + massOutFile.getAbsolutePath() + " to the eReferral directory: "+ targetFile.getAbsolutePath());
			    ba.setBatchDownloadId(batchDownload.getId());
			    transactionOutDAO.submitBatchActivityLog(ba);
			} 
			else {
			    Files.copy(archiveFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			    
			    ba = new batchdownloadactivity();
			    ba.setActivity("Moved the archive file: " + archiveFile.getAbsolutePath() + " to the eReferral directory: "+ targetFile.getAbsolutePath());
			    ba.setBatchDownloadId(batchDownload.getId());
			    transactionOutDAO.submitBatchActivityLog(ba);
			}
		    }
		}
	    }
	    
	    //File Drop (Not to a eReferral Registry)
	    else if (transportDetails.gettransportMethodId() == 13) {
		
		//Check to see if there is file drop details
		//File Drop directory
		List<configurationFileDropFields> fileDropFields = configurationTransportManager.getTransFileDropDetails(transportDetails.getId());

		String fileDropDir =  "";

		for(configurationFileDropFields dropField : fileDropFields){
		    if(dropField.getMethod() == 2) {
			fileDropDir = dropField.getDirectory();
		    }
		}

		if(!"".equals(fileDropDir)) {
		    
		    String targetDirectory = fileDropDir;
		    
		    File directory = new File(targetDirectory);
		    
		    if(!directory.exists()) {
			directory.mkdirs();
		    }
		    
		    
		    //File targetFile = new File(targetDirectory + batchDownload.getUtBatchName() + "." + fileExt);
		    File targetFile = new File(targetDirectory + batchDownload.getOutputFileName());

		    if(!targetFile.exists()) {
		    	targetFile.createNewFile();
			}
		    
		    try {
				Files.copy(archiveFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				ba = new batchdownloadactivity();
				ba.setActivity("Moved the archive file: " + archiveFile.getAbsolutePath() + " to the config drop directory: "+ targetFile.getAbsolutePath());
				ba.setBatchDownloadId(batchDownload.getId());
				transactionOutDAO.submitBatchActivityLog(ba);
		    }
		    catch (Exception ex) {
		    	System.out.println("ERROR OCCURRED");
				ba = new batchdownloadactivity();
				ba.setActivity("Failed to move the archive file: " + archiveFile.getAbsolutePath() + ". Drop Directory: "+ targetFile.getAbsolutePath() + " does not exist.");
				ba.setBatchDownloadId(batchDownload.getId());
				transactionOutDAO.submitBatchActivityLog(ba);
		    }
		    
		}
		else {
		    updateTargetBatchStatus(batchDownload.getId(), 58, "endDateTime");
		     
		    ba = new batchdownloadactivity();
		    ba.setActivity("Failed to move the target file because the target file drop directory for configId:"+transportDetails.getconfigId()+" was empty.");
		    ba.setBatchDownloadId(batchDownload.getId());
		    transactionOutDAO.submitBatchActivityLog(ba);
		}
	    }

	    //Secure FTP Transport Method
	    else if (transportDetails.gettransportMethodId() == 3) {
		
		configurationFTPFields FTPPushDetails = configurationTransportManager.getTransportFTPDetailsPush(transportDetails.getId());
		
		FTPClient client = new FTPClient();
		FileInputStream fis = null;
		
		try {
		    client.connect(FTPPushDetails.getip());
		    client.login(FTPPushDetails.getusername(), FTPPushDetails.getpassword());
		    client.setDefaultPort(FTPPushDetails.getport());
		    client.changeWorkingDirectory(FTPPushDetails.getdirectory());
		    
		    String filename = batchDownload.getOutputFileName();
		    
		    fis = new FileInputStream(archiveFile);
		    
		    client.storeFile(filename, fis);
		    client.logout();
		    
		    ba = new batchdownloadactivity();
		    ba.setActivity("Successfully FTPd the target file: " +archiveFile.getAbsolutePath()+ " to the following directory: "+FTPPushDetails.getdirectory() + " for IP: " + FTPPushDetails.getip() + " Port:" + FTPPushDetails.getport());
		    ba.setBatchDownloadId(batchDownload.getId());
		    transactionOutDAO.submitBatchActivityLog(ba);
		    
		}
		catch (IOException e) {
		    updateTargetBatchStatus(batchDownload.getId(), 58, "endDateTime");
		    
		    ba = new batchdownloadactivity();
		    ba.setActivity("Failed to FTP the target file: " +archiveFile.getAbsolutePath()+ " to the following directory: "+FTPPushDetails.getdirectory() + " for IP: " + FTPPushDetails.getip() + " Port:" + FTPPushDetails.getport());
		    ba.setBatchDownloadId(batchDownload.getId());
		    transactionOutDAO.submitBatchActivityLog(ba);
		    
		    ba = new batchdownloadactivity();
		    ba.setActivity("FTP Error: " + e.getMessage());
		    ba.setBatchDownloadId(batchDownload.getId());
		    transactionOutDAO.submitBatchActivityLog(ba);
		}
		finally {
		    try {
			if (fis != null) {
			    fis.close();
			}
			client.disconnect();
		    } catch (IOException e) {
			updateTargetBatchStatus(batchDownload.getId(), 58, "endDateTime");
			e.printStackTrace();
		    }
		}
		
	    } 
	    // REST API 
	    else if (transportDetails.gettransportMethodId() == 9) {

		//we need to update our totals
		transactionInManager.updateRecordCounts(batchDownload.getId(), rejectIds, true, "totalErrorCount");
		transactionInManager.updateRecordCounts(batchDownload.getId(), new ArrayList<>(), true, "totalRecordCount");

		String methodName = configurationTransportManager.getRestAPIMethodName(transportDetails.getRestAPIFunctionId());
		Class<?>[] paramTypes = {int.class, configurationTransport.class};
		Method method = restfulManager.getClass().getMethod(methodName, paramTypes);
		method.invoke(restfulManager, batchDownload.getId(), transportDetails);
		
		ba = new batchdownloadactivity();
		ba.setActivity("Called the REST API Method: " + methodName);
		ba.setBatchDownloadId(batchDownload.getId());
		transactionOutDAO.submitBatchActivityLog(ba);

	    }
	    // REST API VIA DIRECT
	    else if (transportDetails.gettransportMethodId() == 12) {

		//we need to update our totals
		transactionInManager.updateRecordCounts(batchDownload.getId(), rejectIds, true, "totalErrorCount");
		transactionInManager.updateRecordCounts(batchDownload.getId(), new ArrayList<>(), true, "totalRecordCount");
		
		organizationDirectDetails directDetails = configurationTransportManager.getDirectMessagingDetailsById(configDetails.getorgId());
		
		String methodName = "";
		
		if(directDetails != null) {
		    hisps hispDetails = hispManager.getHispById(directDetails.getHispId());
		    
		    methodName = "senddirectOut"+hispDetails.getHispName().toLowerCase().replaceAll(" ","");
		    Class<?>[] paramTypes = {Integer.class, configurationTransport.class, hisps.class, String.class, String.class, String.class, String.class};
		    Method method = directManager.getClass().getMethod(methodName, paramTypes);
		    method.invoke(directManager, batchDownload.getId(), transportDetails, hispDetails, patientId, patientDOB, patientFirstname, patientLastname);
		    
		    ba = new batchdownloadactivity();
		    ba.setActivity("Called the DIRECT Method: " + methodName + " for Hisp ("+hispDetails.getHispName()+") id:"+hispDetails.getId());
		    ba.setBatchDownloadId(batchDownload.getId());
		    transactionOutDAO.submitBatchActivityLog(ba);
		}
		else {
		    updateTargetBatchStatus(batchDownload.getId(), 58, "endDateTime");
		     
		    ba = new batchdownloadactivity();
		    ba.setActivity("The directDetails were not found for orgId: " + configDetails.getorgId());
		    ba.setBatchDownloadId(batchDownload.getId());
		    transactionOutDAO.submitBatchActivityLog(ba);
		}
	    }

	    //now we delete massoutput file
	    massOutFile.delete();
	    
	    ba = new batchdownloadactivity();
	    ba.setActivity("Deleted the massOutFile:"+ massOutFile.getAbsolutePath());
	    ba.setBatchDownloadId(batchDownload.getId());
	    transactionOutDAO.submitBatchActivityLog(ba);
	}

	if (transportDetails.gettransportMethodId() != 9 && transportDetails.gettransportMethodId() != 12) {
	    //restful manager already took care of status

	    //we need to update our totals
	    transactionInManager.updateRecordCounts(batchDownload.getId(), rejectIds, true, "totalErrorCount");
	    transactionInManager.updateRecordCounts(batchDownload.getId(), new ArrayList<Integer>(), true, "totalRecordCount");

	    updateTargetBatchStatus(batchDownload.getId(), 28, "endDateTime");
	    
	    ba = new batchdownloadactivity();
	    ba.setActivity("Updated batch download batchId:" + batchDownload.getId() + " to statusId:28");
	    ba.setBatchDownloadId(batchDownload.getId());
	    transactionOutDAO.submitBatchActivityLog(ba);

	}
	
	//Get the batch details again to make sure the status is 28 if so delete all the generated tables
	batchDownloads downloadBatchDetails = getBatchDetails(batchDownload.getId());
	
	if(downloadBatchDetails.getStatusId() == 28) {
	    
	    if(inserteReferralMessage) {
		//Need to check if the source message came from a HEL registry but the target is not sending back to the registry. In this case
		//We need to mark the message in the registry as complete and insert the target link.
		configurationTransport sourceTransportDetails = configurationTransportManager.getTransportDetails(batchUploadDetails.getConfigId());

		if(sourceTransportDetails.getHelSchemaName() != null) {
		    if(!"".equals(sourceTransportDetails.getHelSchemaName())) {
			submittedMessage existingRegistrySubmittedMessage = submittedmessagemanager.getSubmittedMessageBySQL(sourceTransportDetails.getHelSchemaName(),batchUploadDetails.getOriginalFileName());

			if (existingRegistrySubmittedMessage != null) {
			    submittedmessagemanager.updateSubmittedMessage(sourceTransportDetails.getHelSchemaName(),existingRegistrySubmittedMessage.getId(),28,batchUploadDetails.getId());
			    submittedmessagemanager.enterSubmittedMessageTarget(sourceTransportDetails.getHelSchemaName(),existingRegistrySubmittedMessage.getId());
			    
			    ba = new batchdownloadactivity();
			    ba.setActivity("Updated eReferral submitted message to status 28 for messageId:" + existingRegistrySubmittedMessage.getId() + " message #:" + existingRegistrySubmittedMessage.getAssignedMessageNumber());
			    ba.setBatchDownloadId(batchDownload.getId());
			    transactionOutDAO.submitBatchActivityLog(ba);
			}
		    }
		}
	    }
	    
	    
	    //Delete all transaction target tables
	    transactionOutDAO.deleteBatchDownloadTables(batchDownload.getId());
	    
	    ba = new batchdownloadactivity();
	    ba.setActivity("Deleted all batch download tables for batchId:" + batchDownload.getId());
	    ba.setBatchDownloadId(batchDownload.getId());
	    transactionOutDAO.submitBatchActivityLog(ba);
	    
	    //Need to see if any emails need to be sent
	    List<configurationConnection> connectionDetails = configurationManager.getConnectionsBySrcAndTargetConfigurations(uploadConfigDetails.getId(), configDetails.getId());
	    
	    if(connectionDetails != null) {
		if(!connectionDetails.isEmpty()) {
		    List<configurationConnectionReceivers> connectionReceivers = configurationManager.getConnectionReceivers(connectionDetails.get(0).getId());
		    List<configurationConnectionSenders> connectionSenders = configurationManager.getConnectionSenders(connectionDetails.get(0).getId());
		    
		    Organization targetOrgDetails = organizationManager.getOrganizationById(configDetails.getorgId());
		    Organization sourceOrgDetails = organizationManager.getOrganizationById(uploadConfigDetails.getorgId());
		    
		    if(!connectionSenders.isEmpty()) {
			String fromName = "";
			String fromEmail = "";
			mailMessage msg = new mailMessage();
			ArrayList<String> fromCCAddressArray = new ArrayList<String>();
			msg.setfromEmailAddress("support@health-e-link.net");
			
			for(configurationConnectionSenders sender : connectionSenders) {
			    if(sender.getSendEmailNotifications() && !"".equals(sender.getEmailAddress())) {
				if ("".equals(fromEmail)) {
				    fromEmail = sender.getEmailAddress();
				} else {
				    fromCCAddressArray.add(sender.getEmailAddress());
				}
			    }
			}
			
			if (!"".equals(fromEmail)) {
			    msg.settoEmailAddress(fromEmail);

			    if (fromCCAddressArray.size() > 0) {
				String[] fromCCAddressList = new String[fromCCAddressArray.size()];
				fromCCAddressList = fromCCAddressArray.toArray(fromCCAddressList);
				msg.setccEmailAddress(fromCCAddressList);
			    }

			    msg.setmessageSubject("Your " + uploadConfigDetails.getconfigName() + " message has been successfully delivered (" + myProps.getProperty("server.identity") + ")");

			    /* Build the body of the email */
			    StringBuilder sb = new StringBuilder();
			    sb.append("The ").append(uploadConfigDetails.getconfigName()).append(" sent to ").append(targetOrgDetails.getOrgName()).append(" has been successfully delivered.");
			    msg.setmessageBody(sb.toString());

			    /* Send the email */
			    try {
				emailMessageManager.sendEmail(msg);
			    } catch (Exception ex) {
				System.err.println("mail exception");
				//ex.printStackTrace();
			    }
			}
		    }
		    
		    if(!connectionReceivers.isEmpty()) {
			String fromName = "";
			String fromEmail = "";
			mailMessage msg = new mailMessage();
			ArrayList<String> fromCCAddressArray = new ArrayList<String>();
			msg.setfromEmailAddress("support@health-e-link.net");
			
			for(configurationConnectionReceivers receiver : connectionReceivers) {
			    if(receiver.getSendEmailNotifications() && !"".equals(receiver.getEmailAddress())) {
				if ("".equals(fromEmail)) {
				    fromEmail = receiver.getEmailAddress();
				} else {
				    fromCCAddressArray.add(receiver.getEmailAddress());
				}
			    }
			}
			
			if (!"".equals(fromEmail)) {
			    msg.settoEmailAddress(fromEmail);

			    if (fromCCAddressArray.size() > 0) {
				String[] fromCCAddressList = new String[fromCCAddressArray.size()];
				fromCCAddressList = fromCCAddressArray.toArray(fromCCAddressList);
				msg.setccEmailAddress(fromCCAddressList);
			    }

			    msg.setmessageSubject("You have received a new message from " + sourceOrgDetails.getOrgName() + " (" + myProps.getProperty("server.identity") + ")");

			    /* Build the body of the email */
			    StringBuilder sb = new StringBuilder();
			    sb.append("You have received a new message from ").append(sourceOrgDetails.getOrgName());
			    sb.append("<br /><br />");
			    sb.append("Configuration: ").append(configDetails.getconfigName());
			    sb.append("<br />");
			    sb.append("Total Records: ").append(batchDownload.getTotalRecordCount());
			    msg.setmessageBody(sb.toString());

			    /* Send the email */
			    try {
				emailMessageManager.sendEmail(msg);
			    } catch (Exception ex) {
				System.err.println("mail exception");
				//ex.printStackTrace();
			    }
			}
		    }
		}
	    }
	}
	
	transactionInManager.updateBatchStatus(batchDownload.getBatchUploadId(), 0, "endDateTime");
	
	return 0;
    }

    @Override
    public BigInteger getRejectedCount(Date fromDate, Date toDate) throws Exception {

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String dateFrom = df.format(fromDate);
	String dateTo = df.format(toDate);

	return transactionOutDAO.getRejectedCount(dateFrom, dateTo);
    }

    @Override
    public List<batchDownloads> getBatchesByStatusIdsAndDate(Date fromDate, Date toDate, Integer fetchSize, List<Integer> statusIds) throws Exception {
	return transactionOutDAO.getBatchesByStatusIdsAndDate(fromDate, toDate, fetchSize, statusIds);
    }

    @Override
    public List<batchDownloads> getDLBatchesByStatusIds(List<Integer> statusIds)
	    throws Exception {
	return transactionOutDAO.getDLBatchesByStatusIds(statusIds);
    }

    @Override
    public void clearDownloadBatch(int batchDownloadId) throws Exception {
	deleteRestAPIMessageByDownloadId(batchDownloadId);
    }

    @Override
    public Integer clearBatchTransactionTables(Integer batchDownloadId) throws Exception {
	return transactionOutDAO.clearBatchTransactionTables(batchDownloadId);
    }

    @Override
    public batchDLRetry getBatchDLRetryByDownloadId(Integer batchDownloadId, Integer statusId) throws Exception {
	return transactionOutDAO.getBatchDLRetryByDownloadId(batchDownloadId, statusId);
    }

    @Override
    public void saveBatchDLRetry(batchDLRetry br) throws Exception {
	transactionOutDAO.saveBatchDLRetry(br);
    }

    @Override
    public void clearBatchDLRetry(Integer batchDownloadId) throws Exception {
	transactionOutDAO.clearBatchDLRetry(batchDownloadId);
    }

    @Override
    public void deleteRestAPIMessageByDownloadId(int batchDownloadId) throws Exception {
	transactionOutDAO.deleteRestAPIMessageByDownloadId(batchDownloadId);
    }

    @Override
    public Integer insertFailedRequiredFields(configurationFormFields cff, Integer batchUploadId) {
	return transactionOutDAO.insertFailedRequiredFields(cff, batchUploadId);
    }
    
    @Override
    public void deleteBatchDownloadTables(Integer batchId) throws Exception {
	transactionOutDAO.deleteBatchDownloadTables(batchId);
    }
    
    @Override
    public void deleteBatchDownloadTablesByBatchUpload(Integer batchId) throws Exception {
	
	List<batchDownloads> batchDownloads = transactionOutDAO.getDLBatchesByBatchUploadId(batchId);
	
	if(batchDownloads != null) {
	    if(!batchDownloads.isEmpty()) {
		transactionOutDAO.deleteBatchDownloadTablesByBatchUpload(batchDownloads);
	    }
	}
    }
    
    @Override
    public List<batchDownloads> getPendingResetBatches(Integer batchUploadId) throws Exception {
	return transactionOutDAO.getPendingResetBatches(batchUploadId);
    }
    
    @Override
    public void submitBatchDownloadChanges(batchDownloads batchDownload) throws Exception {
	transactionOutDAO.submitBatchDownloadChanges(batchDownload);
    }
	
    @Override
    public void sendPassThruFiles(batchUploads batchULDetails, batchDownloads batchDLDetails,configurationTransport transportDetails,File archiveFile) throws Exception {
	
	//we have file already, we just need to move it
	if (transportDetails.gettransportMethodId() == 10) {
	   
	   // Get the File Drop Details
	   configurationFileDropFields fileDropDetails = configurationTransportManager.getTransFileDropDetailsPush(transportDetails.getId());

	    File targetFile = new File(myProps.getProperty("ut.directory.utRootDir") + fileDropDetails.getDirectory() + batchDLDetails.getOutputFileName());
	    Files.copy(archiveFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	} 
	
	//SFTP
	else if (transportDetails.gettransportMethodId() == 3) {
	    
	    //Get the ftp fields
	    configurationFTPFields ftpDetails = configurationTransportManager.getTransportFTPDetailsPush(transportDetails.getId());
	    
	    if(ftpDetails != null) {
		if(!ftpDetails.getdirectory().equals("")) {
		   
		    File targetFile = new File(myProps.getProperty("ut.directory.utRootDir") + ftpDetails.getdirectory() + batchDLDetails.getOutputFileName());
		    Files.copy(archiveFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	    }
	}
	
	// REST API 
	else if (transportDetails.gettransportMethodId() == 9) {
	    
	    //we need to update our totals
	    String methodName = configurationTransportManager.getRestAPIMethodName(transportDetails.getRestAPIFunctionId());
	    
	    Class<?>[] paramTypes = {int.class, configurationTransport.class};
	    Method method = restfulManager.getClass().getMethod(methodName, paramTypes);
	    method.invoke(restfulManager, batchDLDetails.getId(), transportDetails);

	}
    }
    
    @Override
    public List<batchDownloads> getDownloadBatchesByBatchUploadId(Integer batchUploadId) throws Exception {
	return transactionOutDAO.getDLBatchesByBatchUploadId(batchUploadId);
    }
    
    /**
     * THe 'checkOutboundScheduledBatches' method will check to see if there are any waiting outbound batches to be sent.
     * THe method will find any downloadBatch with status Id (59) and check the configurations schedule settings against
     * the current date/time.
     * 
     * If there is a match it will update the status Id of the batch to (61 ready to process).
     * 
     * @throws Exception 
     */
    @Override
    public void checkOutboundScheduledBatches() throws Exception {
	
	List<Integer> statusIds = new ArrayList<>();
	statusIds.add(59);
	
	List<batchDownloads> batchDownloadsToProcess = transactionOutDAO.getDLBatchesByStatusIds(statusIds);
	
	if(batchDownloadsToProcess != null) {
	    if(!batchDownloadsToProcess.isEmpty()) {
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.SECOND,0);
		today.set(Calendar.MINUTE,0);
		today.set(Calendar.MILLISECOND, 0);
		
		batchDownloadsToProcess.forEach(batchDownload -> {
		    
		    if(batchDownload.getConfigId() > 0) {
			
			//Need to get the schedule
			configurationSchedules scheduleDetails = configurationManager.getScheduleDetails(batchDownload.getConfigId());
			
			//Automatically
			if(scheduleDetails.gettype() == 5) {
			    transactionOutDAO.updateBatchStatus(batchDownload.getId(),61);
			}
			
			//Daily
			else if(scheduleDetails.gettype() == 2) {
			    
			    //Scheduled
			    if(scheduleDetails.getprocessingType() == 1) {
				if(scheduleDetails.getprocessingTime() > 0) {
				    
				    Calendar processDate = Calendar.getInstance();

				    processDate.set(Calendar.YEAR, today.get(Calendar.YEAR));
				    processDate.set(Calendar.MONTH, today.get(Calendar.MONTH));
				    processDate.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
				    processDate.set(Calendar.HOUR_OF_DAY,scheduleDetails.getprocessingTime());
				    processDate.set(Calendar.MINUTE,0);
				    processDate.set(Calendar.SECOND,0);
				    processDate.set(Calendar.MILLISECOND, 0);
				    
				    if(processDate.equals(today)) {
					transactionOutDAO.updateBatchStatus(batchDownload.getId(),61);
				    }
				    
				    
				}
				//If no time set, set the batch to manual
				else {
				    transactionOutDAO.updateBatchStatus(batchDownload.getId(),64);
				}
			    }
			    //Continuous
			    else {
				transactionOutDAO.updateBatchStatus(batchDownload.getId(),61);
			    }
			}
			
			//Weekly
			else if(scheduleDetails.gettype() == 3) {
			    
			    if(scheduleDetails.getprocessingTime() > 0 && scheduleDetails.getprocessingDay() > 0) {
				Calendar processDate = Calendar.getInstance();
				
				processDate.set(Calendar.YEAR, today.get(Calendar.YEAR));
				processDate.set(Calendar.MONTH, today.get(Calendar.MONTH));
				processDate.set(Calendar.DAY_OF_WEEK, scheduleDetails.getprocessingDay());
				processDate.set(Calendar.HOUR_OF_DAY,scheduleDetails.getprocessingTime());
				processDate.set(Calendar.MINUTE,0);
				processDate.set(Calendar.SECOND,0);
				processDate.set(Calendar.MILLISECOND, 0);
				
				if(processDate.equals(today)) {
				    transactionOutDAO.updateBatchStatus(batchDownload.getId(),61);
				}
			    }
			    else {
				transactionOutDAO.updateBatchStatus(batchDownload.getId(),64);
			    }
			}
			
			//Monthly (Always check on the first)
			else if(scheduleDetails.gettype() == 4) {
			    
			    if(scheduleDetails.getprocessingTime() > 0) {
				Calendar processDate = Calendar.getInstance();
				
				processDate.set(Calendar.YEAR, today.get(Calendar.YEAR));
				processDate.set(Calendar.MONTH, today.get(Calendar.MONTH));
				processDate.set(Calendar.DAY_OF_MONTH, 1);
				processDate.set(Calendar.HOUR_OF_DAY,scheduleDetails.getprocessingTime());
				processDate.set(Calendar.MINUTE,0);
				processDate.set(Calendar.SECOND,0);
				processDate.set(Calendar.MILLISECOND, 0);
				
				if(processDate.equals(today)) {
				    transactionOutDAO.updateBatchStatus(batchDownload.getId(),61);
				}
			    }
			}
			
			//Set to manually
			else {
			    transactionOutDAO.updateBatchStatus(batchDownload.getId(),61);
			}
		    }
		});
		
	    }
	}
    }
    
    /**
     * 
     * @param batchUploadId
     * @return
     * @throws Exception 
     */
    @Override
    public boolean chechForTransactionInTable(Integer batchUploadId) throws Exception {
	return transactionOutDAO.chechForTransactionInTable(batchUploadId);
    }
    
    @Override
    public List<batchDownloads> getAllSentBatchesPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
	return transactionOutDAO.getAllSentBatchesPaged(fromDate,toDate, displayStart, displayRecords, searchTerm, sortColumnName, sortDirection);
    }
    
    @Override
    public void insertDMMessage(directmessagesout newDirectMessageOut) throws Exception {
        transactionOutDAO.insertDMMessage(newDirectMessageOut);
    }
    
    public void populateOutboundAuditReport(Integer configId, Integer batchDownloadId, Integer batchUploadId, Integer batchUploadConfigId) throws Exception {
	transactionOutDAO.populateOutboundAuditReport(configId, batchDownloadId, batchUploadId,batchUploadConfigId);
    }
    
    @Override
    public List<batchErrorSummary> getBatchErrorSummary(int batchId) throws Exception {
	return transactionInDAO.getBatchErrorSummary(batchId,"outbound");
    }
    
    @Override
    public List<batchdownloadactivity> getBatchActivities(batchDownloads batchInfo) {
	return transactionOutDAO.getBatchActivities(batchInfo);
    }
    
    @Override
    public directmessagesout getDirectAPIMessagesById(Integer directMessageId) {
	return transactionOutDAO.getDirectAPIMessagesById(directMessageId);
    }
    
    @Override
    public List getErrorReportField(Integer batchDownloadId) throws Exception {
	return transactionOutDAO.getErrorReportField(batchDownloadId);
    }
    
    @Override
    public List<batchDownloadDroppedValues> getBatchDroppedValues(Integer batchDownloadId) throws Exception {
	return transactionOutDAO.getBatchDroppedValues(batchDownloadId);
    }
    
    @Override
    public void updateMissingRequiredFieldStatus(Integer batchDownloadId) throws Exception {
	transactionOutDAO.updateMissingRequiredFieldStatus(batchDownloadId);
    }
    
    @Override
    public void clearBatchActivityLogTable(Integer batchId) throws Exception {
	transactionInDAO.clearBatchActivityLogTable(batchId);
    }
}
