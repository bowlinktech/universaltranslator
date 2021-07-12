package com.hel.ut.service.impl;

import com.hel.ut.dao.messageTypeDAO;
import com.hel.ut.dao.organizationDAO;
import com.hel.ut.dao.utConfigurationDAO;
import com.hel.ut.dao.utConfigurationTransportDAO;
import com.hel.ut.model.*;
import com.hel.ut.service.emailMessageManager;
import com.hel.ut.service.utConfigurationManager;
import org.apache.commons.io.FileUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

@Service
public class utConfigurationManagerImpl implements utConfigurationManager {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private utConfigurationDAO utConfigurationDAO;
    
    @Autowired
    private utConfigurationTransportDAO configurationTransportDAO;

    @Autowired
    private organizationDAO organizationDAO;
    
    @Autowired
    private messageTypeDAO messageTypeDAO;
    
     @Autowired
    private emailMessageManager emailMessageManager;
    
    @Resource(name = "myProps")
    private Properties myProps;

    @Override
    public Integer createConfiguration(utConfiguration configuration) {
	configuration.setstepsCompleted(1);
	return utConfigurationDAO.createConfiguration(configuration);
    }

    @Override
    public void updateConfiguration(utConfiguration configuration) {
	utConfigurationDAO.updateConfiguration(configuration);
    }

    @Override
    public utConfiguration getConfigurationById(int configId) {
	return utConfigurationDAO.getConfigurationById(configId);
    }

    @Override
    public List<utConfiguration> getConfigurationsByOrgId(int configId, String searchTerm) {
	return utConfigurationDAO.getConfigurationsByOrgId(configId, searchTerm);
    }

    @Override
    public List<utConfiguration> getActiveConfigurationsByOrgId(int configId) {
	return utConfigurationDAO.getActiveConfigurationsByOrgId(configId);
    }

    @Override
    public utConfiguration getConfigurationByName(String configName, int orgId) {
	return utConfigurationDAO.getConfigurationByName(configName, orgId);
    }

    @Override
    public List<utConfiguration> getConfigurations() {
	return utConfigurationDAO.getConfigurations();
    }

    @Override
    public List<utConfiguration> getLatestConfigurations(int maxResults) {
	return utConfigurationDAO.getLatestConfigurations(maxResults);
    }

    @Override
    public Long findTotalConfigs() {
	return utConfigurationDAO.findTotalConfigs();
    }

    @Override
    public Long getTotalConnections(int configId) {
	return utConfigurationDAO.getTotalConnections(configId);
    }

    @Override
    public void updateCompletedSteps(int configId, int stepCompleted) {
	utConfigurationDAO.updateCompletedSteps(configId, stepCompleted);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getFileTypes() {
	return utConfigurationDAO.getFileTypes();
    }

    @Override
    public String getFileTypesById(int id) {
	return utConfigurationDAO.getFileTypesById(id);
    }

    @Override
    public List<configurationDataTranslations> getDataTranslations(int configId) {
	return utConfigurationDAO.getDataTranslations(configId);
    }

    @Override
    public String getFieldName(int fieldId) {
	return utConfigurationDAO.getFieldName(fieldId);
    }

    @Override
    public void deleteDataTranslations(int configId, int categoryId) {
	utConfigurationDAO.deleteDataTranslations(configId, categoryId);
    }

    @Override
    public void saveDataTranslations(configurationDataTranslations translations) {
	utConfigurationDAO.saveDataTranslations(translations);
    }

    @Override
    public List<Macros> getMacros() {
	return utConfigurationDAO.getMacros();
    }

    @Override
    public List<Macros> getMacrosByCategory(int categoryId) {
	return utConfigurationDAO.getMacrosByCategory(categoryId);
    }

    @Override
    public Macros getMacroById(int macroId) {
	return utConfigurationDAO.getMacroById(macroId);
    }

    @Override
    public List<configurationConnection> getAllConnections() {
	return utConfigurationDAO.getAllConnections();
    }

    @Override
    public List<configurationConnection> getLatestConnections(int maxResults) {
	return utConfigurationDAO.getLatestConnections(maxResults);
    }

    @Override
    public List<configurationConnection> getConnectionsByConfiguration(int configId, int userId) {
	return utConfigurationDAO.getConnectionsByConfiguration(configId, userId);
    }

    @Override
    public List<configurationConnection> getConnectionsByTargetConfiguration(int configId) {
	return utConfigurationDAO.getConnectionsByTargetConfiguration(configId);
    }

    @Override
    public Integer saveConnection(configurationConnection connection) {
	return utConfigurationDAO.saveConnection(connection);
    }

    @Override
    public configurationConnection getConnection(int connectionId) {
	return utConfigurationDAO.getConnection(connectionId);
    }

    @Override
    public void updateConnection(configurationConnection connection) {
	utConfigurationDAO.updateConnection(connection);
    }

    @Override
    public configurationSchedules getScheduleDetails(int configId) {
	return utConfigurationDAO.getScheduleDetails(configId);
    }

    @Override
    public void saveSchedule(configurationSchedules scheduleDetails) {
	utConfigurationDAO.saveSchedule(scheduleDetails);
    }

    @Override
    public configurationMessageSpecs getMessageSpecs(int configId) {
	return utConfigurationDAO.getMessageSpecs(configId);
    }

    @Override
    public List<utConfiguration> getActiveConfigurationsByUserId(int userId, int transportMethod) throws Exception {
	return utConfigurationDAO.getActiveConfigurationsByUserId(userId, transportMethod);
    }

    @Override
    public List<configurationConnectionSenders> getConnectionSenders(int connectionId) {
	return utConfigurationDAO.getConnectionSenders(connectionId);
    }

    @Override
    public List<configurationConnectionReceivers> getConnectionReceivers(int connectionId) {
	return utConfigurationDAO.getConnectionReceivers(connectionId);
    }

    @Override
    public void saveConnectionSenders(configurationConnectionSenders senders) {
	utConfigurationDAO.saveConnectionSenders(senders);
    }

    @Override
    public void saveConnectionReceivers(configurationConnectionReceivers receivers) {
	utConfigurationDAO.saveConnectionReceivers(receivers);
    }

    @Override
    public void removeConnectionSenders(int connectionId) {
	utConfigurationDAO.removeConnectionSenders(connectionId);
    }

    @Override
    public void removeConnectionReceivers(int connectionId) {
	utConfigurationDAO.removeConnectionReceivers(connectionId);
    }
    
    @Override
    public void removeConnection(int connectionId) {
	utConfigurationDAO.removeConnection(connectionId);
    }

    @Override
    public void updateMessageSpecs(configurationMessageSpecs messageSpecs, int transportDetailId, int fileType, boolean hasHeader, Integer fileLayout) throws Exception {

	//Need to get the selected organization clean url
	utConfiguration configDetails = utConfigurationDAO.getConfigurationById(messageSpecs.getconfigId());
	Organization orgDetails = organizationDAO.getOrganizationById(configDetails.getorgId());
	String cleanURL = orgDetails.getcleanURL();
	
	boolean processFile = false;
	String fileName = null;
	String directory = "";
	
	String currentTemplateFileName = messageSpecs.gettemplateFile();
	
	if(messageSpecs.getFile() != null) {
	    if(!messageSpecs.getFile().isEmpty()) {
		
		MultipartFile file = messageSpecs.getFile();

		//If a file is uploaded
		if (file != null && !file.isEmpty()) {
		    processFile = true;

		    fileName = file.getOriginalFilename();

		    InputStream inputStream = null;
		    OutputStream outputStream = null;

		    try {
			inputStream = file.getInputStream();
			File newFile = null;

			//Set the directory to save the uploaded message type template to
			directory = myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/templates/";
			
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
			Date date = new Date();
			
			fileName = dateFormat.format(date) + "-" + fileName.replace(" ", "-");

			newFile = new File(directory + fileName);
			newFile.createNewFile();
			
			outputStream = new FileOutputStream(newFile);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
			    outputStream.write(bytes, 0, read);
			}
			outputStream.close();
			inputStream.close();

			//Set the filename to the file name
			messageSpecs.settemplateFile(fileName);

		    } catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e);
		    }
		}
	    }
	}
	
	MultipartFile parsingScript = messageSpecs.getParsingScriptFile();
	//If a file is uploaded
	if (parsingScript != null && !parsingScript.isEmpty()) {

	    String parsingScriptFileName = parsingScript.getOriginalFilename();

	    InputStream inputStream = null;
	    OutputStream outputStream = null;

	    cleanURL = orgDetails.getcleanURL();

	    try {
		inputStream = parsingScript.getInputStream();
		File newFile = null;

		//Set the directory to save the uploaded message type template to
		directory = myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/templates/";

		newFile = new File(directory + parsingScriptFileName);

		if (newFile.exists()) {
		    newFile.delete();
		}
		newFile.createNewFile();

		outputStream = new FileOutputStream(newFile);
		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = inputStream.read(bytes)) != -1) {
		    outputStream.write(bytes, 0, read);
		}
		outputStream.close();
		inputStream.close();

		//Set the filename to the file name
		messageSpecs.setParsingTemplate(parsingScriptFileName);

	    } catch (IOException e) {
		e.printStackTrace();
		throw new Exception(e);
	    }
	}

	utConfigurationDAO.updateMessageSpecs(messageSpecs, transportDetailId);

	if (processFile == true) {
	    try {
		loadExcelContents(messageSpecs, transportDetailId, fileName, directory, hasHeader,fileLayout, currentTemplateFileName);
	    } catch (Exception e1) {
		e1.printStackTrace();
		throw new Exception(e1);
	    }

	}

    }

    /**
     * The 'loadExcelContents' will take the contents of the uploaded excel template file and populate the corresponding utConfiguration form fields table.This function will split up the contents into the appropriate buckets.Buckets (1 - 4) will be separated by spacer rows with in the excel file.
     *
     * @param id value of the latest added utConfiguration
     * @param transportDetailId
     * @param fileName	file name of the uploaded excel file.
     * @param dir	the directory of the uploaded file
     * @param hasHeader
     * @param fileLayout
     * @throws java.lang.Exception
     *
     */
    public void loadExcelContents(configurationMessageSpecs messageSpecs, int transportDetailId, String fileName, String dir, boolean hasHeader, Integer fileLayout, String currentTemplateFileName) throws Exception {
	utConfigurationDAO.loadExcelContents(messageSpecs, transportDetailId, fileName, dir, hasHeader, fileLayout, currentTemplateFileName);
    }

    @Override
    public List<configurationDataTranslations> getDataTranslationsWithFieldNo(int configId, int categoryId) {
	return utConfigurationDAO.getDataTranslationsWithFieldNo(configId, categoryId);
    }

    @Override
    public List<CrosswalkData> getCrosswalkData(int cwId) {
	return utConfigurationDAO.getCrosswalkData(cwId);
    }

    @Override
    public HL7Details getHL7Details(int configId) {
	return utConfigurationDAO.getHL7Details(configId);
    }

    @Override
    public List<HL7Segments> getHL7Segments(int hl7Id) {
	return utConfigurationDAO.getHL7Segments(hl7Id);
    }

    @Override
    public List<HL7Elements> getHL7Elements(int hl7Id, int segmentId) {
	return utConfigurationDAO.getHL7Elements(hl7Id, segmentId);
    }

    @Override
    public List<HL7ElementComponents> getHL7ElementComponents(int elementId) {
	return utConfigurationDAO.getHL7ElementComponents(elementId);
    }

    @Override
    public void updateHL7Details(HL7Details details) {
	utConfigurationDAO.updateHL7Details(details);
    }

    @Override
    public void updateHL7Segments(HL7Segments segment) {
	utConfigurationDAO.updateHL7Segments(segment);
    }

    @Override
    public void updateHL7Elements(HL7Elements element) {
	utConfigurationDAO.updateHL7Elements(element);
    }

    @Override
    public void updateHL7ElementComponent(HL7ElementComponents component) {
	utConfigurationDAO.updateHL7ElementComponent(component);
    }

    @Override
    public int saveHL7Details(HL7Details details) {
	return utConfigurationDAO.saveHL7Details(details);
    }

    @Override
    public int saveHL7Segment(HL7Segments newSegment) {
	return utConfigurationDAO.saveHL7Segment(newSegment);
    }

    @Override
    public int saveHL7Element(HL7Elements newElement) {
	return utConfigurationDAO.saveHL7Element(newElement);
    }

    @Override
    public void saveHL7Component(HL7ElementComponents newcomponent) {
	utConfigurationDAO.saveHL7Component(newcomponent);
    }

    @Override
    public String getMessageTypeNameByConfigId(Integer configId) {
	return utConfigurationDAO.getMessageTypeNameByConfigId(configId);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getEncodings() {
	return utConfigurationDAO.getEncodings();
    }

    @Override
    public void removeHL7ElementComponent(Integer componentId) {
	utConfigurationDAO.removeHL7ElementComponent(componentId);
    }

    @Override
    public void removeHL7Element(Integer elementId) {
	utConfigurationDAO.removeHL7Element(elementId);
    }

    @Override
    public void removeHL7Segment(Integer segmentId) {
	utConfigurationDAO.removeHL7Segment(segmentId);
    }

    @Override
    public List<configurationCCDElements> getCCDElements(Integer configId) throws Exception {
	return utConfigurationDAO.getCCDElements(configId);
    }

    @Override
    public void saveCCDElement(configurationCCDElements ccdElement) throws Exception {
	utConfigurationDAO.saveCCDElement(ccdElement);
    }

    @Override
    public configurationCCDElements getCCDElement(Integer elementId) throws Exception {
	return utConfigurationDAO.getCCDElement(elementId);
    }

    @Override
    public configurationExcelDetails getExcelDetails(Integer configId, Integer orgId)
	    throws Exception {
	return utConfigurationDAO.getExcelDetails(configId, orgId);
    }

    @Override
    public void updateExcelConfigDetails(Integer orgId, configurationMessageSpecs messageSpecs) throws Exception {
	utConfigurationDAO.updateExcelConfigDetails(orgId, messageSpecs);
    }

    @Override
    public Integer getFieldCrosswalkIdByFieldName(int configId, String fieldName) throws Exception {
	return utConfigurationDAO.getFieldCrosswalkIdByFieldName(configId, fieldName);
    }

    @Override
    public List<utConfiguration> getActiveConfigurationsByTransportType(int userId, List<Integer> transportMethods) throws Exception {
	return utConfigurationDAO.getActiveConfigurationsByTransportType(userId, transportMethods);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getZipTypes() {
	return utConfigurationDAO.getZipTypes();
    }

    @SuppressWarnings("rawtypes")
    public List getrestAPITypes() {
	return utConfigurationDAO.getrestAPITypes();
    }

    @Override
    public List<configurationConnection> getConnectionsBySrcAndTargetConfigurations(
	    int sourceConfigId, int targetConfigId) {
	return utConfigurationDAO.getConnectionsBySrcAndTargetConfigurations(sourceConfigId, targetConfigId);
    }
    
    @SuppressWarnings("rawtypes")
    public List getrestAPIFunctions(Integer orgId) {
	return utConfigurationDAO.getrestAPIFunctions(orgId);
    }
    
    
    @Override
    public List<watchlist> getDashboardWatchList() throws Exception {
	return utConfigurationDAO.getDashboardWatchList();
    }
    
    @Override
    public watchlist getDashboardWatchListById(int watchId) throws Exception {
	return utConfigurationDAO.getDashboardWatchListById(watchId);
    }
    
    @Override
    public Integer saveDashboardWatchListEntry(watchlist watchListEntry) {
	return utConfigurationDAO.saveDashboardWatchListEntry(watchListEntry);
    }

    @Override
    public void updateDashboardWatchListEntry(watchlist watchListEntry) {
	utConfigurationDAO.updateDashboardWatchListEntry(watchListEntry);
    }
    
    @Override
    public List<watchlistEntry> getWatchListEntries(Date fromDate, Date toDate) throws Exception  {
	return utConfigurationDAO.getWatchListEntries(fromDate,toDate);
    }
    
    @Override
    public void CheckDashboardWatchList() throws Exception {
	
	List<watchlist> watchListEntries = utConfigurationDAO.getDashboardWatchListToInsert();
	
	if(watchListEntries != null) {
	    if(!watchListEntries.isEmpty()) {
		
		for(watchlist entries : watchListEntries) {
		   
		    watchlistEntry newEntry = new watchlistEntry();
		    newEntry.setWatchlistentryId(entries.getId());
		    newEntry.setConfigId(entries.getConfigId());
		    newEntry.setOrgId(entries.getOrgId());
		    newEntry.setMessageTypeId(entries.getMessageTypeId());
		   
		    utConfigurationDAO.insertDashboardWatchListEntry(newEntry);
		    
		    Calendar c = Calendar.getInstance();
		    String dateParts[] = entries.getNextInsertDate().toString().split("\\s+");
		    String datePart[] = dateParts[0].split("-");
		    String timepart[] = entries.getExpectFirstFileTime().split("\\s+");
		    String timepart2[] = timepart[0].split(":");
		    
		    int month = Integer.parseInt(datePart[1]);
		    
		    if(month == 1) {
			month = 0;
		    }
		    else {
			month = (month*1)-1;
		    }
		    
		    int hour = 12;
		    if("PM".equals(timepart[1])) {
			hour = (Integer.parseInt(timepart2[0])*1)+12;
		    }
		    else {
			hour = Integer.parseInt(timepart2[0]);
			if(hour == 12) {
			    hour = 0;
			}
		    }
		    
		    int min = Integer.parseInt(timepart2[1]);
		    
		    c.set(Integer.parseInt(datePart[0]),month,Integer.parseInt(datePart[2]), hour, min, 0);
		    
		    //Update the next insert date
		    switch (entries.getExpected().toLowerCase()) {
			case "daily":
			    c.add(Calendar.DATE, 1);
			    break;
			    
			case "weekly":
			    c.add(Calendar.DATE, 7);
			    break;
			    
			case "monthly":
			    c.add(Calendar.MONTH, 1);
			    break;
			    
		    }
		    
		    entries.setNextInsertDate(c.getTime());
		    
		    utConfigurationDAO.updateDashboardWatchListEntry(entries);
 		}
	    }
	}
    }
    
    @Override
    public void updateMessageSpecs(configurationMessageSpecs messageSpecs) throws Exception {
	utConfigurationDAO.updateMessageSpecs(messageSpecs, 0);
    }
    
    @Override
    public void deleteWatchEntry(Integer watchId) throws Exception {
	utConfigurationDAO.deleteWatchEntry(watchId);
    }
    
    @Override
    public List<watchlistEntry> getGenericWatchListEntries(Date fromDate, Date toDate) throws Exception  {
	return utConfigurationDAO.getGenericWatchListEntries(fromDate,toDate);
    }
    
    @Override
    public void insertDashboardWatchListEntry(watchlistEntry watchListEntry) {
	utConfigurationDAO.insertDashboardWatchListEntry(watchListEntry);
    }
    
    @Override
    public watchlistEntry getWatchListEntry(Integer entryId) throws Exception {
	return utConfigurationDAO.getWatchListEntry(entryId);
    }
    
    @Override
    public List<utConfiguration>  getAllActiveSourceConfigurations() throws Exception {
	return utConfigurationDAO.getAllActiveSourceConfigurations();
    }
    
    @Override
    public List<configurationConnection> getConnectionsBySourceConfiguration(Integer configId) {
	return utConfigurationDAO.getConnectionsBySourceConfiguration(configId);
    }
    
    @Override
    public List<utConfiguration>  getAllSourceConfigurations() throws Exception {
	return utConfigurationDAO.getAllSourceConfigurations();
    }
    
    @Override
    public List<utConfiguration>  getAllTargetConfigurations() throws Exception {
	return utConfigurationDAO.getAllTargetConfigurations();
    }
    
    @Override
    public List getDataTranslationsForDownload(Integer configId) throws Exception {
	
	String sqlStatement = "select configName, processOrder, fieldDesc, macroId, macroName, crosswalkId, crosswalkname,  passClear,  fieldA, fieldB, constant1, constant2, fieldNo "
	    + "from (select cff.configId configId, "
	    + "processOrder, fieldDesc, fieldNo, crosswalkId, IFNULL(name,'') as crosswalkname, macroId, IFNULL(concat(Macro_Short_Name, ' (', formula,')'),'') macroname,"
	    + "fieldA, fieldB, replace(replace(replace(constant1, '\\\\', '^^'), '''', '|_|'), '\"', '&') constant1 , constant2, case when passclear = 1 then 'Pass' else 'Clear' end passClear "
	    + "from (select dts.*, name from (select configurationdatatranslations.*, Macro_Short_Name, formula  from configurationdatatranslations left join "
	    + "(select * from macro_names) macros on macros.id = configurationdatatranslations.macroId where configId = " + configId
	    + " order by categoryId, processOrder)  dts left join (Select * from crosswalks) cws on cws.id = crosswalkId ) dts inner join "
	    + "(select * from configurationformfields ) cff on cff.id = fieldId) cff join (select configName, id from configurations) configurations on configurations.id = cff.configId "
	    + "order by configName, processOrder";
	
	return utConfigurationDAO.getDTCWForDownload(sqlStatement);
	
    }
    
    @Override
    public List getCrosswalksForDownload (Integer configId, boolean inUseOnly) throws Exception {
	
	String sqlStatement = "select crosswalks.name, rel_crosswalkdata.crosswalkId, rel_crosswalkdata.sourcevalue, rel_crosswalkdata.targetvalue, rel_crosswalkdata.descValue " 
	+ "from crosswalks inner join ";
	
	if(inUseOnly) {
	    sqlStatement += "configurationdatatranslations b on (b.crosswalkid = crosswalks.id or (b.macroId in (129,160,177,195,199) and b.constant1 = crosswalks.id)) and b.configId = "+configId+" inner join ";
	}

	sqlStatement += "rel_crosswalkdata on rel_crosswalkdata.crosswalkId = crosswalks.id " 
	+ " where orgId = 0 or orgId in (select orgId from configurations where id = " + configId + ") " 
	+ "order by name,crosswalks.id";
	 
	return utConfigurationDAO.getDTCWForDownload(sqlStatement);
    }
    
    @Override 
    public StringBuffer printDetailsSection(utConfiguration configDetails, Organization orgDetails, String siteTimeZone) throws Exception {
	
	configurationSchedules scheduleDetails = utConfigurationDAO.getScheduleDetails(configDetails.getId());
	
	//Get the last updated date for this configuration
	configurationUpdateLogs lastConfigUpdatelog = utConfigurationDAO.getLastConfigUpdateLog(configDetails.getId());
	
	//DateFormat dft = new SimpleDateFormat("MM/dd/yyyy h:mm a");
	
	String configType = "Source Configuration";
	String messageType = "eReferral Configuration";
	String status = "Active";
		
	if(configDetails.getType() == 2) {
	    configType = "Target Configuration";
	}
	
	if(configDetails.getMessageTypeId() == 2) {
	    messageType = "Family Planning Configuration";
	}
	else {
	    messageType = "Other Configuration";
	}
	
	if(!configDetails.getStatus()) {
	    status = "Inactive";
	}
	
	TimeZone timeZone = TimeZone.getTimeZone(siteTimeZone);
	DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	requiredFormat.setTimeZone(timeZone);
	String dateinTZ = requiredFormat.format(configDetails.getDateCreated());
	
	Date createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateinTZ);
	
	StringBuffer reportBody = new StringBuffer();
	reportBody.append("<div style='text-align:center'>");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'>").append(configDetails.getconfigName()).append("</span><br />");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'>Organization: ").append(orgDetails.getOrgName()).append("</span><br />");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>This configuration was created on ").append(new SimpleDateFormat("M/dd/yyyy h:mm a").format(createDate)).append("</span><br />");
	if(lastConfigUpdatelog != null) {
	    dateinTZ = requiredFormat.format(lastConfigUpdatelog.getDateCreated());
	    Date updateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateinTZ);
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>This configuration was last updated on ").append(new SimpleDateFormat("M/dd/yyyy h:mm a").format(updateDate)).append("</span><br />");
	}
	reportBody.append("</div>");
	reportBody.append("<div>");
	reportBody.append("<br /><span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>DETAILS</strong></span><br />");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Status: </strong>").append(status).append("</span><br /><br />");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Configuration Type: </strong>").append(configType).append("</span><br /><br />");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Message Type: </strong>").append(messageType).append("</span><br /><br />");
	reportBody.append("</div>");
	
	reportBody.append(printConfigurationNotesSection(configDetails,siteTimeZone));
	
	String scheduleType = "Automatically";
	String processingType = "";
	
	if(scheduleDetails != null) {
	
	    if(scheduleDetails.gettype() == 1) {
		scheduleType = "Manually";
	    }
	    else if(scheduleDetails.gettype() == 2) {
		scheduleType = "Daily";
	    }
	    else if(scheduleDetails.gettype() == 2) {
		scheduleType = "Weekly";
	    }
	    else if(scheduleDetails.gettype() == 2) {
		scheduleType = "Monthly";
	    }
	
	    if(scheduleDetails.getprocessingType() > 0) {
		if(scheduleDetails.getprocessingType() == 1) {
		    processingType = "Scheduled";
		}
		else if (scheduleDetails.getprocessingType() == 2) {
		    processingType = "Continuous";
		}
	    }
	}
	
	reportBody.append("<div>");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>SCHEDULE</strong></span><br />");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Schedule Type: </strong>").append(scheduleType).append("</span><br /><br />");
	if(!"".equals(processingType)) {
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Type of Processing: </strong>").append(processingType).append("</span><br /><br />");
	}
	if(scheduleDetails != null) {
	    if(scheduleDetails.getnewfileCheck() > 0) {
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>How often to check for a new file: </strong>").append(scheduleDetails.getnewfileCheck()).append("</span><br /><br />");
	    }
	    if(scheduleDetails.getprocessingDay()> 0) {
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Process on what Day: </strong>");
		if(scheduleDetails.getprocessingDay() == 1) {
		    reportBody.append("Sunday").append("</span><br /><br />");
		}
		else if(scheduleDetails.getprocessingDay() == 2) {
		    reportBody.append("Monday").append("</span><br /><br />");
		}  
		else if(scheduleDetails.getprocessingDay() == 3) {
		    reportBody.append("Tuesday").append("</span><br /><br />");
		} 
		else if(scheduleDetails.getprocessingDay() == 4) {
		    reportBody.append("Wednesday").append("</span><br /><br />");
		} 
		else if(scheduleDetails.getprocessingDay() == 5) {
		    reportBody.append("Thursday").append("</span><br /><br />");
		} 
		else if(scheduleDetails.getprocessingDay() == 6) {
		    reportBody.append("Friday").append("</span><br /><br />");
		} 
		else if(scheduleDetails.getprocessingDay() == 7) {
		    reportBody.append("Saturday").append("</span><br /><br />");
		} 
	    }
	    if(scheduleDetails.getprocessingTime() > 0) {
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Time of Day to process files: </strong>").append(scheduleDetails.getprocessingTime()).append("</span><br /><br />");
	    }
	}
	reportBody.append("</div>");

	return reportBody;
	
    }
    
    @Override 
    public StringBuffer printTransportMethodSection(utConfiguration configDetails) throws Exception {
	
	configurationTransport transportDetails = configurationTransportDAO.getTransportDetails(configDetails.getId());
	
	StringBuffer reportBody = new StringBuffer();
	reportBody.append("<div style='padding-top:10px;'>");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>TRANSPORT METHOD</strong></span><br />");
	
	if(transportDetails != null) {
	    
	    List<configurationFileDropFields> fileDropFields = configurationTransportDAO.getTransFileDropDetails(transportDetails.getId());
	
	    List<configurationFTPFields> ftpFields = configurationTransportDAO.getTransportFTPDetails(transportDetails.getId());

	    String transportMethod = "File Drop";

	    if(transportDetails.gettransportMethodId() == 1) {
		transportMethod = "File Upload";
	    }
	    else if(transportDetails.gettransportMethodId() == 3) {
		transportMethod = "Secure FTP";
	    }
	    else if(transportDetails.gettransportMethodId() == 6) {
		transportMethod = "Web Service";
	    }
	    else if(transportDetails.gettransportMethodId() == 8) {
		transportMethod = "To a eReferral Registry";
	    }
	    else if(transportDetails.gettransportMethodId() == 9) {
		transportMethod = "Rest API";
	    }
	    else if(transportDetails.gettransportMethodId() == 10) {
		transportMethod = "Online Form";
	    }
	    else if(transportDetails.gettransportMethodId() == 11) {
		transportMethod = "From a eReferral Registry (File Upload)";
	    }
	    else if(transportDetails.gettransportMethodId() == 12) {
		transportMethod = "Direct Message";
	    }

	    String isZipped = "No";
	    String zipType = "";

	    if(transportDetails.isZipped()) {
		isZipped = "Yes";
	    }

	    if(transportDetails.getZipType() == 1) {
		zipType = "GZIP";
	    }

	    String fileType = "Text File";

	    if(transportDetails.getfileType() == 1) {
		fileType = "Does not apply";
	    }
	    else if(transportDetails.getfileType() == 3) {
		fileType = "CSV";
	    }
	    else if(transportDetails.getfileType() == 4) {
		fileType = "HL7";
	    }
	    else if(transportDetails.getfileType() == 5) {
		fileType = "MS Access DB File (MDB)";
	    }
	    else if(transportDetails.getfileType() == 6) {
		fileType = "PDF";
	    }
	    else if(transportDetails.getfileType() == 8) {
		fileType = "Excel (XLS)";
	    }
	    else if(transportDetails.getfileType() == 9) {
		fileType = "CCD";
	    }
	    else if(transportDetails.getfileType() == 10) {
		fileType = "MS Word Document";
	    }
	    else if(transportDetails.getfileType() == 11) {
		fileType = "Excel (XLSX)";
	    }
	    else if(transportDetails.getfileType() == 12) {
		fileType = "JSON";
	    }

	    String delim = "comma";

	    if(transportDetails.getfileDelimiter() == 2) {
		delim = "pipe (|)";
	    }
	    else if(transportDetails.getfileDelimiter() == 3) {
		delim = "colon (:)";
	    }
	    else if(transportDetails.getfileDelimiter() == 11) {
		delim = "semi-colon (;)";
	    }
	    else if(transportDetails.getfileDelimiter() == 12) {
		delim = "tab";
	    }

	    String apiType = "";

	    if(transportDetails.getRestAPIType() == 1) {
		apiType = "Receive Payload and process";
	    }
	    else if(transportDetails.getRestAPIType() == 2) {
		apiType = "Receive ACK to modify status";
	    }
	    else if(transportDetails.getRestAPIType() == 3) {
		apiType = "Receive Payload and passthru";
	    }

	    String errorHandling = "";

	    if(transportDetails.geterrorHandling() == 2) {
		errorHandling = "Reject individual transactions on error";
	    }
	    else if(transportDetails.geterrorHandling() == 3) {
		errorHandling = "Reject entire file on a single transaction error";
	    }
	    else if(transportDetails.geterrorHandling() == 4) {
		errorHandling = "Send errors through to the target file";
	    }
	    
	    reportBody.append("</div>");
	    reportBody.append("<div>");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>How is the file getting to the UT?</strong></span><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(transportMethod).append("</span><br /><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Where will the file be stored on the UT prior to processing?</strong></span><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(transportDetails.getfileLocation()).append("</span><br /><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Max Accepted File Size (mb)</strong></span><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(transportDetails.getmaxFileSize()).append("</span><br /><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Is the file Zipped?</strong></span><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(isZipped).append("</span><br /><br />");
	    if(!"".equals(zipType)) {
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Zip Type</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(zipType).append("</span><br /><br />");
	    }
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>File Type</strong></span><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(fileType).append("</span><br /><br />");
	    
	    if("CSV".equals(fileType) || "Text File".equals(fileType)) {
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>File Extension</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>.").append(transportDetails.getfileExt()).append("</span><br /><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>File Delimiter</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(delim).append("</span><br /><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Line Terminator</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(transportDetails.getLineTerminator()).append("</span><br /><br />");
	    }
	    
	     reportBody.append("</div>");
	    
	    if(fileDropFields != null) {
		if(fileDropFields.size() > 0) {
		    reportBody.append("<div>");
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>File Drop Location</strong></span><br />");
		    for(configurationFileDropFields dropField : fileDropFields) {
			if(configDetails.getType() == 1 && dropField.getMethod() == 1) {
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(dropField.getDirectory()).append("</span><br /><br />");
			}
			else if(configDetails.getType() == 2 && dropField.getMethod() == 2) {
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(dropField.getDirectory()).append("</span><br /><br />");
			}
		    }
		    reportBody.append("</div>");
		}
	    }
	    
	    if(!"".equals(transportDetails.getRestAPIURL())) {
		reportBody.append("<div>");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>REST API Details</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>API URL</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(transportDetails.getRestAPIURL()).append("</span><br /><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>API Username</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(transportDetails.getRestAPIUsername()).append("</span><br /><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>API Password</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(transportDetails.getRestAPIPassword()).append("</span><br /><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>API Type</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(apiType).append("</span><br /><br />");
		reportBody.append("</div>");
	    }
	    
	    if(ftpFields != null) {
		if(ftpFields.size() > 0) {
		    reportBody.append("<div>");
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>FTP Details</strong></span><br />");
		    for(configurationFTPFields ftpField : ftpFields) {
			if(ftpField.getmethod() == 1) {
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Protocal</strong></span><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(ftpField.getprotocol()).append("</span><br /><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Host</strong></span><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(ftpField.getip()).append("</span><br /><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Username</strong></span><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(ftpField.getusername()).append("</span><br /><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Password</strong></span><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(ftpField.getpassword()).append("</span><br /><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Directory</strong></span><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(ftpField.getdirectory()).append("</span><br /><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Port</strong></span><br />");
			    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(ftpField.getport()).append("</span><br /><br />");
			}
		    }
		    reportBody.append("</div>");
		}
	    }
	    
	    if(configDetails.getType() == 2) {
		reportBody.append("<div>");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Target File Name</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(transportDetails.gettargetFileName()).append("</span><br /><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Append Date and Time to file Name?</strong></span><br />");
		if(transportDetails.getappendDateTime()) {
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Yes</span><br /><br />");
		}
		else {
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>No</span><br /><br />");
		}
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Add header row to the target file?</strong></span><br />");
		if(transportDetails.isAddTargetFileHeaderRow()) {
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Yes</span><br /><br />");
		}
		else {
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>No</span><br /><br />");
		}
		reportBody.append("</div>");
	    }
	    
	    if(!"".equals(errorHandling)) {
		reportBody.append("<div>");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Error Handling</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(errorHandling).append("</span><br /><br />");
		if(configDetails.getType() == 1 && transportDetails.getThreshold() > 0) {
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Error Threshold %</strong></span><br />");
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(configDetails.getThreshold()).append("</span><br /><br />");
		}
		if(configDetails.getType() == 2) {
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Do you want to populate the inbound audit report with errors found while processing this target file?</strong></span><br />");
		    if(transportDetails.isPopulateInboundAuditReport()) {
			reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Yes</span><br /><br />");
		    }
		    else {
			reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>No</span><br /><br />");
		    }
		}
		reportBody.append("</div>");
	    }
	}
	else {
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'>No transport method was found for this configuration.</span><br />");
	    reportBody.append("</div>");
	}
	
	return reportBody;
	
    }
    
    @Override 
    public StringBuffer printMessageSpecsSection(utConfiguration configDetails) throws Exception {
	
	configurationMessageSpecs messageSpecs = utConfigurationDAO.getMessageSpecs(configDetails.getId());
	
	StringBuffer reportBody = new StringBuffer();
	reportBody.append("<div style='padding-top:10px;'>");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>MESSAGE SPECS</strong></span><br />");
	
	if(messageSpecs != null) {
	    configurationTransport transportDetails = configurationTransportDAO.getTransportDetails(configDetails.getId());
	
	    List<configurationFormFields> fields = configurationTransportDAO.getConfigurationFields(configDetails.getId(), transportDetails.getId());

	    String hasHeaderRow = "Yes";

	    if(!messageSpecs.isHasHeader()) {
		hasHeaderRow = "No";
	    }

	    String submissionHeaderRow = "Yes";

	    if(!messageSpecs.getcontainsHeaderRow()) {
		submissionHeaderRow = "No";
	    }

	    String fileLayout = "Horiztonal";

	    if(messageSpecs.getFileLayout() == 2) {
		fileLayout = "Vertical";
	    }

	    String errorField1 = "N/A";
	    String errorField2 = "N/A";
	    String errorField3 = "N/A";
	    String errorField4 = "N/A";

	    if(messageSpecs.getrptField1() > 0) {
		for(configurationFormFields field : fields) {
		    if(field.getFieldNo()== messageSpecs.getrptField1()) {
			errorField1 = field.getFieldDesc();
		    }
		}
	    }
	    if(messageSpecs.getrptField2() > 0) {
		for(configurationFormFields field : fields) {
		    if(field.getFieldNo() == messageSpecs.getrptField2()) {
			errorField2 = field.getFieldDesc();
		    }
		}
	    }
	    if(messageSpecs.getrptField3() > 0) {
		for(configurationFormFields field : fields) {
		    if(field.getFieldNo() == messageSpecs.getrptField3()) {
			errorField3 = field.getFieldDesc();
		    }
		}
	    }
	    if(messageSpecs.getrptField4() > 0) {
		for(configurationFormFields field : fields) {
		    if(field.getFieldNo() == messageSpecs.getrptField4()) {
			errorField4 = field.getFieldDesc();
		    }
		}
	    }
	    reportBody.append("</div>");
	    reportBody.append("<div>");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Current Template File?</strong></span><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(messageSpecs.gettemplateFile()).append("</span><br /><br />");
	    if(configDetails.getType() == 1) {
		
		if(messageSpecs.getFileNameConfigHeader() != null) {
		    if(!"".equals(messageSpecs.getFileNameConfigHeader())) {
			reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Message Type Identifier in file name</strong></span><br />");
			reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(messageSpecs.getFileNameConfigHeader()).append("</span><br /><br />");
		    }
		}
		
		if(messageSpecs.getmessageTypeCol() > 0) {
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Column containing the message type (Enter 0 if not provided)</strong></span><br />");
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(messageSpecs.getmessageTypeCol()).append("</span><br /><br />");
		}
		
		if(messageSpecs.getmessageTypeVal() != null) {
		    if(!"".equals(messageSpecs.getmessageTypeVal())) {
			reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Message Type Value</strong></span><br />");
			reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(messageSpecs.getmessageTypeVal()).append("</span><br /><br />");
		    }
		}
		
		if(messageSpecs.gettargetOrgCol() > 0) {
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Column containing the target organization (Enter 0 if not provided)</strong></span><br />");
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(messageSpecs.gettargetOrgCol()).append("</span><br /><br />");
		}
		
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Will the submitted file have any header rows?</strong></span><br />");
		reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(submissionHeaderRow).append("</span><br /><br />");
		if(submissionHeaderRow.toLowerCase().equals("yes")) {
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>How many header rows does the file have?</strong></span><br />");
		    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(messageSpecs.getTotalHeaderRows()).append("</span><br /><br />");
		}
		if(messageSpecs.getParsingTemplate() != null) {
		    if(!"".equals(messageSpecs.getParsingTemplate())) { 
			reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'><strong>Configuration Parsing Script</strong></span><br />");
			reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(messageSpecs.getParsingTemplate()).append("</span><br /><br />");
		    }
		}
	    }
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Audit Report Reportable Fields</strong></span><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field 1: ").append(errorField1).append("</span><br /><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field 2: ").append(errorField2).append("</span><br /><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field 3: ").append(errorField3).append("</span><br /><br />");
	    reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field 4: ").append(errorField4).append("</span><br /><br />");
	    reportBody.append("</div>");
	}
	else {
	   reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'>No transport method was found for this configuration.</span><br />");
	   reportBody.append("</div>");
	}
	
	return reportBody;
	
    }
    
    @Override 
    public StringBuffer printFieldSettingsSection(utConfiguration configDetails) throws Exception {
	
	configurationTransport transportDetails = configurationTransportDAO.getTransportDetails(configDetails.getId());
	
	List<configurationFormFields> fields = configurationTransportDAO.getConfigurationFields(configDetails.getId(), transportDetails.getId());
	
	StringBuffer reportBody = new StringBuffer();
	reportBody.append("<div style='padding-top:10px;'>");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>FIELD SETTINGS</strong></span><br /><br />");
	
	if(fields != null) {
	    if(!fields.isEmpty()) {
		reportBody.append("</div>");
		reportBody.append("<div><table border='1' cellpadding='1' cellspacing='1' width='100%'>");
		reportBody.append("<thead><tr><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field No</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field Name</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Sample Data</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Use Field</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Required</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Validation</th>");
		if(configDetails.getType() == 2) {
		    reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Default Value</th>");
		}	
		reportBody.append("</tr></thead><tbody>");
		for(configurationFormFields field : fields) {
		    reportBody.append("<tr><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>F").append(field.getFieldNo()).append("</td><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(field.getFieldDesc()).append("</td>");
		    
		    if(field.getSampleData() == null) {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append("").append("</td>");
		    }
		    else {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(field.getSampleData()).append("</td>");
		    }
		    
		    if(field.getUseField()) {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Yes</td>");
		    }
		    else {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>No</td>");
		    }
		    if(field.getRequired()) {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Yes</td>");
		    }
		    else {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>No</td>");
		    }
		    if(field.getValidationType() == 1) {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>None</td>");
		    }
		    else if(field.getValidationType() == 2) {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Email</td>");
		    }
		    else if(field.getValidationType() == 3) {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Phone Number</td>");
		    }
		    else if(field.getValidationType() == 4) {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Date</td>");
		    }
		    else if(field.getValidationType() == 5) {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Numeric</td>");
		    }
		    else if(field.getValidationType() == 6) {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>URL</td>");
		    }
		    if(configDetails.getType() == 2) {
			reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(field.getDefaultValue()).append("</td>");
		    }
		    reportBody.append("</tr>");
		}
		reportBody.append("</tbody></table></div>");
	    }
	}
	else {
	   reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'>No fields have been uploaded for this configuration.</span><br />");
	   reportBody.append("</div>");
	}
	
	return reportBody;
    }

    @Override 
    public StringBuffer printDataTranslationsSection(utConfiguration configDetails, String siteTimeZone) throws Exception {
	
        configurationTransport transportDetails = configurationTransportDAO.getTransportDetails(configDetails.getId());

        List<configurationDataTranslations> existingTranslations = utConfigurationDAO.getDataTranslationsWithFieldNo(configDetails.getId(), 1);

        List<configurationFormFields> fields = configurationTransportDAO.getConfigurationFields(configDetails.getId(), transportDetails.getId());

        List<Macros> macros = utConfigurationDAO.getMacros();

        List<Crosswalks> crosswalks = messageTypeDAO.getCrosswalks(1, 0, configDetails.getorgId());

        List crosswalksWithData = messageTypeDAO.getConfigCrosswalksWithData(configDetails.getorgId(),configDetails.getId());

        StringBuffer reportBody = new StringBuffer();
        reportBody.append("<div style='padding-top:10px;'>");
        reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>DATA TRANSLATIONS</strong></span><br /><br />");

        String fieldName = "";
        Integer fieldNo = 1;
        String macroName = "";
        String crosswalkName = "";
	
        if(existingTranslations != null) {
            if(!existingTranslations.isEmpty()) {
            reportBody.append("</div>");
            reportBody.append("<div><table border='1' cellpadding='1' cellspacing='1' width='100%'>");
            reportBody.append("<thead><tr><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field No</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field Name</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Macro Name</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Crosswalk Name</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Pass/Clear</th>");
            reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field A</th>");
            reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Field B</th>");
            reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Constant 1</th>");
            reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Constant 2</th>");
            reportBody.append("<th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Process Order</th>");
            reportBody.append("</tr></thead><tbody>");
            for(configurationDataTranslations dt : existingTranslations) {
                macroName = "";
                crosswalkName = "";

                for(configurationFormFields field : fields) {
                if(field.getId() == dt.getFieldId()) { fieldName = field.getFieldDesc(); fieldNo = field.getFieldNo(); }
                }

                if(dt.getMacroId() > 0) {
                for(Macros macro : macros) {
                    if(macro.getId() == dt.getMacroId()) { macroName = macro.getMacroName(); }
                }
                }

                if(dt.getCrosswalkId() > 0) {
                for(Crosswalks crosswalk : crosswalks) {
                    if(crosswalk.getId() == dt.getCrosswalkId()) { crosswalkName = crosswalk.getName(); }
                }
                }

                reportBody.append("<tr>");
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append("F").append(fieldNo).append("</td>");
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(fieldName).append("</td>");
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(escapeHtml(macroName)).append("</td>");
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(crosswalkName).append("</td>");

                if(dt.getPassClear() == 1) {
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Pass</td>");
                }
                else {
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Clear</td>");
                }
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(escapeHtml(dt.getFieldA())).append("</td>");
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(escapeHtml(dt.getFieldB())).append("</td>");
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(escapeHtml(dt.getConstant1())).append("</td>");
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(escapeHtml(dt.getConstant2())).append("</td>");
                reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(dt.getProcessOrder()).append("</td>");
                reportBody.append("</tr>");
            }
            reportBody.append("</tbody></table></div>");
            }
            else {
            reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'>No fields have been uploaded for this configuration.</span><br />");
            reportBody.append("</div>");
            }
        }
        else {
           reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'>No fields have been uploaded for this configuration.</span><br />");
           reportBody.append("</div>");
        }
	
        reportBody.append("<div style='padding-top:10px;'>");
        reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>CONFIGURATION CROSSWALKS</strong></span><br /><br />");

        if(crosswalksWithData != null) {
            if(!crosswalksWithData.isEmpty()) {
            reportBody.append("</div>");
            Iterator<Object[]> cwIterator = crosswalksWithData.iterator();
            String cwname = "";
            String delim = "";
            String dateCreated = "";
            String lastUpdated = "";
            while(cwIterator.hasNext()) {
                Object[] cwData = cwIterator.next();

                if("".equals(cwname) || !cwname.equals(cwData[0])) {
                if(!"".equals(cwname)) {
                     reportBody.append("</tbody></table></div><br />");
                }
                cwname = (String) cwData[0];
                if((Integer) cwData[5] == 1) {
                    delim = "comma";
                }
                else if((Integer) cwData[5] == 2) {
                    delim = "pipe";
                }
                else if((Integer) cwData[5] == 3) {
                    delim = "colon";
                }
                else if((Integer) cwData[5] == 11) {
                    delim = "semi-colon";
                }
                else if((Integer) cwData[5] == 12) {
                    delim = "tab";
                }

                dateCreated = cwData[6].toString();
                lastUpdated = cwData[7].toString();

                TimeZone timeZone = TimeZone.getTimeZone(siteTimeZone);
                DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                requiredFormat.setTimeZone(timeZone);

                Date createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateCreated);
                Date lastUpdateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastUpdated);

                reportBody.append("<div><span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>CW Name: "+cwname+" (ID=" + cwData[4].toString() + ")</strong></span><br />");
                reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Date Created: " + new SimpleDateFormat("M/dd/yyyy").format(createDate) + "</strong></span><br />");
                reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Last Updated: " + new SimpleDateFormat("M/dd/yyyy").format(lastUpdateDate) + "</strong></span><br />");
                reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Delimiter Used: " + delim+ "</strong></span><br />");
                reportBody.append("<table border='1' cellpadding='1' cellspacing='1' width='100%'>");
                reportBody.append("<thead><tr><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Source Value</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Target Value</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>Desc</th>");
                reportBody.append("</tr></thead><tbody>");
                }

                reportBody.append("<tr><td>")
                .append(escapeHtml(cwData[1].toString()))
                .append("</td><td>")
                .append(escapeHtml(cwData[2].toString()))
                .append("</td><td>")
                .append(escapeHtml(cwData[3].toString()))
                .append("</td></tr>");

            }

            reportBody.append("</tbody></table></div>");
            }
            else {
            reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'>No crosswalks have been uploaded for this organization.</span><br />");
            reportBody.append("</div>");
            }
        }
        else {
           reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'>No crosswalks have been uploaded for this organization.</span><br />");
           reportBody.append("</div>");
        }
	
	    return reportBody;
	
    }
    
    @Override 
    public StringBuffer printConnectionDetails(utConfiguration srcconfigDetails,utConfiguration tgtconfigDetails) throws Exception {
	
	Organization srcorgDetails = organizationDAO.getOrganizationById(srcconfigDetails.getorgId());
	Organization tgtorgDetails = organizationDAO.getOrganizationById(tgtconfigDetails.getorgId());
	
	List<configurationFormFields> sourceconfigurationDataElements = configurationTransportDAO.getConfigurationFields(srcconfigDetails.getId(), 0);
	List<configurationFormFields> targetconfigurationDataElements = configurationTransportDAO.getConfigurationFields(tgtconfigDetails.getId(), 0);
	List<configurationconnectionfieldmappings> fieldMappings = configurationTransportDAO.getConnectionFieldMappings(tgtconfigDetails.getId(), srcconfigDetails.getId());
	
	if(!fieldMappings.isEmpty()) {
	    for(configurationconnectionfieldmappings fieldMapping : fieldMappings) {
		for(configurationFormFields tgtDataElements : targetconfigurationDataElements) {
		    if(fieldMapping.getFieldNo() == tgtDataElements.getFieldNo() && !fieldMapping.isUseField()) {
			tgtDataElements.setUseField(false);
		    }
		    if(fieldMapping.getFieldNo() == tgtDataElements.getFieldNo()) {
			tgtDataElements.setMappedErrorField(fieldMapping.getPopulateErrorFieldNo());
			tgtDataElements.setMappedToField(fieldMapping.getAssociatedFieldNo());
		    }
		}
	    }
	}

	
	StringBuffer reportBody = new StringBuffer();
	reportBody.append("<div style='text-align:center'>");
	reportBody.append("<table border='0' cellpadding='1' cellspacing='1' width='100%'><tbody>");
	reportBody.append("<tr><td width='50%' style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Source Organization</strong></td>");
	reportBody.append("<td width='50%' style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Target Organization</strong></td></tr>");
	
	reportBody.append("<tr><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(srcorgDetails.getOrgName()).append("</td>");
	reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(tgtorgDetails.getOrgName()).append("</td></tr>");
	reportBody.append("<tr><td colspan='2'>").append("&nbsp;").append("</td></tr>");
	
	reportBody.append("<tr><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Configuration</strong></td>");
	reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 14px;'><strong>Configuration</strong></td></tr>");
	
	reportBody.append("<tr><td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(srcconfigDetails.getconfigName()).append("</td>");
	reportBody.append("<td style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(tgtconfigDetails.getconfigName()).append("</td></tr>");
	reportBody.append("</tbody></table>");
	reportBody.append("<br /><br /></div>");
	
	reportBody.append("<div style='text-align:center; margin-top:15px;'>");
	reportBody.append("<table border='0' cellpadding='1' cellspacing='1' width='100%'><tbody>");
	reportBody.append("<tr><td width='50%' style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Source Configuration Data Elements</strong></td>");
	reportBody.append("<td width='50%' style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>Target Configuration Data Elements</strong></td></tr>");
	
	
	reportBody.append("<tr>");
	reportBody.append("<td width='50%' valign='top'><span style='color:red;'>Rows in red are not in use</span><br />");
	
	reportBody.append("<table border='1' cellpadding='1' cellspacing='1' width='100%'>");
	reportBody.append("<thead><tr><th style='width:5%'>No</th><th>Name</th><th style='width:5%'>R/O</th></tr></thead><tbody>");
	String required = "O";
	for(configurationFormFields srcFormField : sourceconfigurationDataElements) {
	    if(srcFormField.getRequired()) {required = "R"; }
	    if(!srcFormField.getUseField()) {
		reportBody.append("<tr><td style='color:red;'>").append(srcFormField.getFieldNo()).append("</td><td style='color:red;'>").append(srcFormField.getFieldDesc()).append("</td><td style='color:red;'>").append(required).append("</td></tr>");
	    }
	    else {
		reportBody.append("<tr><td>").append(srcFormField.getFieldNo()).append("</td><td>").append(srcFormField.getFieldDesc()).append("</td><td>").append(required).append("</td></tr>");
	    }
	}
	
	reportBody.append("</tbody></table>");
	reportBody.append("</td>");
	reportBody.append("<td width='50%' valign='top'><span style='color:red;'>Rows in red are not in use</span><br />");
	reportBody.append("<table border='1' cellpadding='1' cellspacing='1' width='100%'>");
	reportBody.append("<thead><tr><th style='width:5%'>No</th><th>Name</th><th style='width:5%'>R/O</th><th>Src Field</th></tr></thead><tbody>");
	required = "O";
	String mappedField = "";
	for(configurationFormFields tgtFormField : targetconfigurationDataElements) {
	    
	    if(tgtFormField.getMappedToField() == 0 && tgtFormField.getDefaultValue() != null && !"".equals(tgtFormField.getDefaultValue())) {
		mappedField = "Default Value - " + tgtFormField.getDefaultValue();
	    }
	    else if(tgtFormField.getMappedToField() == 0) {
		mappedField = "Blank Value";
	    }
	    else {
		for(configurationFormFields srcFormField : sourceconfigurationDataElements) {
		    if(srcFormField.getFieldNo() == tgtFormField.getMappedToField()) {
			mappedField = String.valueOf(srcFormField.getFieldNo());
		    }
		}
	    }
	    
	    if(tgtFormField.getRequired()) {required = "R"; }
	    if(!tgtFormField.getUseField()) {
		reportBody.append("<tr><td style='color:red;'>").append(tgtFormField.getFieldNo()).append("</td><td style='color:red;'>").append(tgtFormField.getFieldDesc()).append("</td><td style='color:red;'>").append(required).append("</td><td style='color:red;'>").append(mappedField).append("</td></tr>");
	    }
	    else {
		reportBody.append("<tr><td>").append(tgtFormField.getFieldNo()).append("</td><td>").append(tgtFormField.getFieldDesc()).append("</td><td>").append(required).append("</td><td>").append(mappedField).append("</td></tr>");
	    }
	}
	
	
	reportBody.append("</tbody></table>");
	reportBody.append("</td>");
	reportBody.append("</tr>");
	
	
	
	reportBody.append("</tbody></table>");
	reportBody.append("</div>");
	
	return reportBody;
	
    }
    
    @Override
    public void updateConfigurationDirectories(List<Integer> configIds, String oldCleanURL, String newCleanURL) throws Exception {
	
	String joinedList = configIds.stream().map(String::valueOf).collect(Collectors.joining(","));
	
	String sqlStatement = "update configurationtransportdetails set fileLocation = REPLACE(fileLocation,'"+oldCleanURL+"','"+newCleanURL+"') "
	    + "where configId in ("+joinedList+"); "
	    + "update rel_transportfiledropdetails set directory = REPLACE(directory,'"+oldCleanURL+"','"+newCleanURL+"') "
	    + "where transportId in (select id from configurationtransportdetails where configId in ("+joinedList+"));";
	
	messageTypeDAO.executeSQLStatement(sqlStatement);
	
	//Need to copy crosswalks, input files and templates from the old folder to the new one
	File sourceCrosswalkFolder = new File(myProps.getProperty("ut.directory.utRootDir") + oldCleanURL + "/crosswalks");
	
	if(sourceCrosswalkFolder.exists()) {
	    File destinationCrossalkFolder = new File(myProps.getProperty("ut.directory.utRootDir") + newCleanURL + "/crosswalks");

	    if(!sourceCrosswalkFolder.equals(destinationCrossalkFolder)) {
		try {
		    FileUtils.copyDirectory(sourceCrosswalkFolder, destinationCrossalkFolder);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	
	File sourceTemplateFolder = new File(myProps.getProperty("ut.directory.utRootDir") + oldCleanURL + "/templates");
	
	if(sourceTemplateFolder.exists()) {
	    File destinationTemplateFolder = new File(myProps.getProperty("ut.directory.utRootDir") + newCleanURL + "/templates");

	    if(!sourceTemplateFolder.equals(destinationTemplateFolder)) {
		try {
		    FileUtils.copyDirectory(sourceTemplateFolder, destinationTemplateFolder);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	
	File sourceInputFolder = new File(myProps.getProperty("ut.directory.utRootDir") + oldCleanURL + "/input files");
	
	if(sourceInputFolder.exists()) {
	    File destinationInputFolder = new File(myProps.getProperty("ut.directory.utRootDir") + newCleanURL + "/input files");

	    if(!sourceInputFolder.equals(destinationInputFolder)) {
		try {
		    FileUtils.copyDirectory(sourceInputFolder, destinationInputFolder);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	
	File sourceOutputFolder = new File(myProps.getProperty("ut.directory.utRootDir") + oldCleanURL + "/output files");
	
	if(sourceOutputFolder.exists()) {
	    File destinationOutputFolder = new File(myProps.getProperty("ut.directory.utRootDir") + newCleanURL + "/output files");
	
	    if(!sourceOutputFolder.equals(destinationOutputFolder)) {
		try {
		    FileUtils.copyDirectory(sourceOutputFolder, destinationOutputFolder);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }
    
    @Override
    public void generateMissingCrosswalk(String cleanURL, String fileName) throws Exception {
	
	Organization orgDetails = null;
	
	Integer orgId = 0;
	
	if(!"".equals(cleanURL) && !"libraryFiles".equals(cleanURL)) {
	    List<Organization> orgs = organizationDAO.getOrganizationByName(cleanURL);
	    
	    if(orgs != null) {
		if(!orgs.isEmpty()) {
		    orgDetails = orgs.get(0);
		    orgId = orgDetails.getId();
		}
	    }
	}
	
	if(fileName != null) {
	    
	    if(!"".equals(fileName)) {
		List crosswalksWithData = messageTypeDAO.getCrosswalksWithDataByFileName(orgId, fileName);
		
		if(crosswalksWithData != null) {
		    if(!crosswalksWithData.isEmpty()) {
			
			Iterator<Object[]> cwIterator = crosswalksWithData.iterator();
			
			//Create the file
			File crosswalkFile = new File(myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/crosswalks/"+ fileName);
			
			if(crosswalkFile.createNewFile()) {
			    
			    FileWriter fileWriter = new FileWriter(crosswalkFile);
			    
			    while(cwIterator.hasNext()) {
				Object[] cwData = cwIterator.next();
				
				fileWriter.write(cwData[0].toString());
				
				if("tab".equals(cwData[3].toString())) {
				    fileWriter.write("\t");
				}
				else {
				    fileWriter.write(cwData[4].toString());
				}
				
				fileWriter.write(cwData[1].toString());
				
				if("tab".equals(cwData[3].toString())) {
				    fileWriter.write("\t");
				}
				else {
				    fileWriter.write(cwData[4].toString());
				}
				
				fileWriter.write(cwData[2].toString());
				
				if(cwIterator.hasNext()) {
				    fileWriter.write(System.getProperty( "line.separator" ));
				}

			    }
			    
			    fileWriter.close();
			}
			
			
		    }
		}
	    }
	}
	
    }
    
    @Override
    public void saveConfigurationUpdateLog(configurationUpdateLogs updateLog) throws Exception {
	utConfigurationDAO.saveConfigurationUpdateLog(updateLog);
    }
    
    @Override
    public void checkForUnusedFolders() throws Exception {
	
	//Get folder structures no longer in use
	ArrayList invalidFoldernames = new ArrayList();
	
	List<Organization> orgs = organizationDAO.getOrganizations();
	
	if(!orgs.isEmpty()) {
	    String[] validDirectories = {"medAlliesArchives","massoutputfiles","archivesOut","sFTP","archivesIn","loadFiles","webServicesIn","bowlink","libraryFiles"};
	    List<String> dirToSkip = Arrays.asList(validDirectories);  
	    
	    String UTDirectory = myProps.getProperty("ut.directory.utRootDir");
	    File[] directories = new File(UTDirectory.replace("/home/","/")).listFiles(File::isDirectory);
	    boolean folderFound = false;
	    boolean checkFolder = true;
	    
	    List<String> unUsedFolders = new ArrayList();
	    
	    for(File dir : directories) {
		checkFolder = true;
		
		if(dirToSkip.stream().anyMatch(s -> s.equals(dir.getName()))) {
		    checkFolder = false;
		}
		
		if(checkFolder) {
		    folderFound = false;
		    for(Organization org : orgs) {
			if(dir.getName().equals(org.getCleanURL())) {
			    folderFound = true;
			    break;
			}
		    }
		    if(!folderFound) {
			unUsedFolders.add(dir.getAbsolutePath());
		    }
		}
	    }
	    
	    if(!unUsedFolders.isEmpty()) {
		mailMessage messageDetails = new mailMessage();

		messageDetails.settoEmailAddress("cmccue@health-e-link.net");
		messageDetails.setmessageSubject("Unused Folders on UT" + myProps.getProperty("server.identity"));

		StringBuilder sb = new StringBuilder();
		
		for(String unUsedFolder: unUsedFolders) {
		    sb.append(unUsedFolder + "<br />");
		}
		
		messageDetails.setmessageBody(sb.toString());
		messageDetails.setfromEmailAddress("support@health-e-link.net");
		
		emailMessageManager.sendEmail(messageDetails);
	    }
	}
    }
    
    @Override
    public List<configurationUpdateLogs> getConfigurationUpdateLogs(Integer configId) throws Exception {
	return utConfigurationDAO.getConfigurationUpdateLogs(configId);
    }
    
    @Override
    public configurationUpdateLogs getConfigurationUpdateLog(Integer noteId) throws Exception {
	return utConfigurationDAO.getConfigurationUpdateLog(noteId);
    }
    
    @Override
    public void updateConfigurationUpdateLog(configurationUpdateLogs updateLog) throws Exception {
	utConfigurationDAO.updateConfigurationUpdateLog(updateLog);
    }
    
    @Override
    public void deletConfigurationNote(Integer noteId) throws Exception {
	utConfigurationDAO.deletConfigurationNote(noteId);
    }
    
    @Override 
    public StringBuffer printConfigurationNotesSection(utConfiguration configDetails, String siteTimeZone) throws Exception {
	
	List<configurationUpdateLogs> configurationNotes = utConfigurationDAO.getConfigurationUpdateLogs(configDetails.getId());
	
	StringBuffer reportBody = new StringBuffer();
	reportBody.append("<div>");
	reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'><strong>CONFIGURATION NOTES</strong></span><br />");
	
	
	if(!configurationNotes.isEmpty()) {
	    TimeZone timeZone = TimeZone.getTimeZone(siteTimeZone);
	    DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    requiredFormat.setTimeZone(timeZone);
	    String dateinTZ = "";
	    Date createDate;
	    
	    reportBody.append("</div>");
	    reportBody.append("<div><table border='1' cellpadding='1' cellspacing='1' width='100%'>");
	    reportBody.append("<thead><tr><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px; width:80%'>Note</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px; width:20%'>Created By</th><th style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px; width:20%'>Date Created</th>");
	    reportBody.append("</tr></thead><tbody>");
	    for(configurationUpdateLogs note : configurationNotes) {
		
		dateinTZ = requiredFormat.format(note.getDateCreated());
		createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateinTZ);
		
		reportBody.append("<tr><td valign='top' style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(note.getUpdateMade()).append("</td>");
		reportBody.append("<td valign='top' style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(note.getUsersName()).append("</td>");
		reportBody.append("<td valign='top' style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 12px;'>").append(new SimpleDateFormat("M/dd/yyyy h:mm a").format(createDate)).append("</td>");
		reportBody.append("</tr>");
	    }
	    reportBody.append("</tbody></table></div><br />");
	}
	else {
	   reportBody.append("<span style='font-family: Franklin Gothic Medium, Franklin Gothic; font-size: 16px;'>No notes have been made for this configuration.</span><br />");
	   reportBody.append("</div><br />");
	}
	
	return reportBody;
    }
    
    @Override
    public void deleteConfigurationFTPInformation(int transportId) throws Exception {
	utConfigurationDAO.deleteConfigurationFTPInformation(transportId);
    }
    
    @Override 
    public StringBuffer exportConfigOrgSection(Organization orgDetails) throws Exception {
	
	StringBuffer sb = new StringBuffer();
	sb.append("[orgDetails|");
	sb.append(orgDetails.getOrgName()).append("|");
	sb.append(orgDetails.getAddress()).append("|");
	sb.append(orgDetails.getAddress2()).append("|");
	sb.append(orgDetails.getCity()).append("|");
	sb.append(orgDetails.getState()).append("|");
	sb.append(orgDetails.getPostalCode()).append("|");
	sb.append(orgDetails.getPhone()).append("|");
	sb.append(orgDetails.getFax()).append("|");
	sb.append(orgDetails.getStatus()).append("|");
	sb.append(orgDetails.getCleanURL()).append("|");
	sb.append("''|");
	sb.append(orgDetails.getOrgDesc()).append("|");
	sb.append(orgDetails.getOrgType()).append("|");
	sb.append(orgDetails.getTown()).append("|");
	sb.append(orgDetails.getCounty()).append("|");
	sb.append(orgDetails.getInfoURL()).append("|");
	sb.append(orgDetails.getCountry()).append("|");
	sb.append(orgDetails.getHelRegistryId()).append("|");
	sb.append(orgDetails.getHelRegistryOrgId()).append("|");
	sb.append(orgDetails.getHelRegistrySchemaName()).append("|");
	sb.append(orgDetails.getPrimaryContactEmail()).append("|");
	sb.append("0|");
	sb.append(orgDetails.getPrimaryContactName()).append("|");
	sb.append(orgDetails.getPrimaryTechContactEmail()).append("|");
	sb.append(orgDetails.getPrimaryTechContactName());
	sb.append("]");
	
	return sb;
    }
    
    @Override 
    public StringBuffer exportConfigDetailsSection(utConfiguration configDetails) throws Exception {
	
	StringBuffer sb = new StringBuffer();
	sb.append("[configDetails|");
	sb.append("0|");
	sb.append(configDetails.getStatus()).append("|");
	sb.append(configDetails.getType()).append("|");
	sb.append(configDetails.getMessageTypeId()).append("|");
	sb.append(configDetails.getstepsCompleted()).append("|");
	sb.append(configDetails.getconfigName()).append("|");
	sb.append(configDetails.getThreshold()).append("|");
	sb.append(configDetails.getConfigurationType()).append("|");
	sb.append("0");
	sb.append("]");
	
	return sb;
    }
    
    @Override 
    public StringBuffer exportConfigTransportSection(HttpSession session,configurationTransport transportDetails) throws Exception {
	
	StringBuffer sb = new StringBuffer();
	sb.append("[transportDetails|");
	sb.append("0|");
	sb.append(transportDetails.gettransportMethodId()).append("|");
	sb.append(transportDetails.getfileType()).append("|");
	sb.append(transportDetails.getfileDelimiter()).append("|");
	sb.append(transportDetails.getstatus()).append("|");
	sb.append(transportDetails.gettargetFileName()).append("|");
	sb.append(transportDetails.getappendDateTime()).append("|");
	sb.append(transportDetails.getmaxFileSize()).append("|");
	sb.append(transportDetails.getclearRecords()).append("|");
	sb.append(transportDetails.getfileLocation()).append("|");
	sb.append(transportDetails.getautoRelease()).append("|");
	sb.append(transportDetails.geterrorHandling()).append("|");
	sb.append(transportDetails.getmergeBatches()).append("|");
	sb.append(transportDetails.getcopiedTransportId()).append("|");
	sb.append(transportDetails.getfileExt()).append("|");
	sb.append(transportDetails.getEncodingId()).append("|");
	sb.append(transportDetails.getCcdSampleTemplate()).append("|");
	sb.append(transportDetails.getHL7PDFSampleTemplate()).append("|");
	sb.append(transportDetails.isMassTranslation()).append("|");
	sb.append(transportDetails.isZipped()).append("|");
	sb.append(transportDetails.getZipType()).append("|");
	sb.append(transportDetails.getRestAPIURL()).append("|");
	sb.append(transportDetails.getRestAPIUsername()).append("|");
	sb.append(transportDetails.getRestAPIPassword()).append("|");
	sb.append(transportDetails.getRestAPIType()).append("|");
	sb.append(transportDetails.isWaitForResponse()).append("|");
	sb.append(transportDetails.getRestAPIFunctionId()).append("|");
	sb.append(transportDetails.getJsonWrapperElement()).append("|");
	sb.append(transportDetails.getLineTerminator()).append("|");
	sb.append(transportDetails.getHelRegistryConfigId()).append("|");
	sb.append(transportDetails.getHelSchemaName()).append("|");
	sb.append(transportDetails.getHelRegistryId()).append("|");
	sb.append(transportDetails.getDmConfigKeyword()).append("|");
	sb.append(transportDetails.isErgFileDownload()).append("|");
	sb.append(transportDetails.isPopulateInboundAuditReport()).append("|");
	sb.append(transportDetails.isAddTargetFileHeaderRow()).append("|");
	sb.append(transportDetails.getErrorEmailAddresses());
	sb.append("]");
	
	StringBuffer emailBodySB = (StringBuffer) session.getAttribute("emailBody");
	
	if(transportDetails.getCcdSampleTemplate() != null) {
	    if(!transportDetails.getCcdSampleTemplate().isEmpty()) {
		emailBodySB.append("<br />The following CCD Template File needs to be UPLOADED to the configuration<br />").append("File Name: ").append(transportDetails.getCcdSampleTemplate().trim());
	    }
	}
	
	if(transportDetails.getHL7PDFSampleTemplate() != null) {
	    if(!transportDetails.getHL7PDFSampleTemplate().isEmpty()) {
		emailBodySB.append("<br />The following HL7 PDF Template File needs to be UPLOADED to the configuration<br />").append("File Name: ").append(transportDetails.getHL7PDFSampleTemplate().trim());
	    }
	}
	
	//See if there is any file drop details
	List<configurationFileDropFields> fileDropSettings = configurationTransportDAO.getTransFileDropDetails(transportDetails.getId());
	
	if(!fileDropSettings.isEmpty()) {
	    for(configurationFileDropFields fileDropDetails : fileDropSettings) {
		sb.append(System.getProperty("line.separator"));
		sb.append("[fileDropDetails|");
		sb.append("0|");
		sb.append(fileDropDetails.getDirectory()).append("|");
		sb.append(fileDropDetails.getMethod());
		sb.append("]");
	    }
	}
	else {
	   sb.append(System.getProperty("line.separator"));
	   sb.append("[fileDropDetails]"); 
	}
	
	//See if there is any FTP file drop details
	List<configurationFTPFields> ftpSettings = configurationTransportDAO.getTransportFTPDetails(transportDetails.getId());
	
	if(!ftpSettings.isEmpty()) {
	    for(configurationFTPFields ftpDetails : ftpSettings) {
		sb.append(System.getProperty("line.separator"));
		sb.append("[ftpDropDetails|");
		sb.append("0|");
		sb.append(ftpDetails.getip()).append("|");
		sb.append(ftpDetails.getdirectory()).append("|");
		sb.append(ftpDetails.getusername()).append("|");
		sb.append(ftpDetails.getpassword()).append("|");
		sb.append(ftpDetails.getmethod()).append("|");
		sb.append(ftpDetails.getport()).append("|");
		sb.append(ftpDetails.getprotocol()).append("|");
		sb.append(ftpDetails.getcertification());
		sb.append("]");
		
		if(ftpDetails.getcertification() != null) {
		    if(!ftpDetails.getcertification().isEmpty()) {
			emailBodySB.append("<br />The following FTP Certificate File needs to be MOVED to the organizations certificate folder<br />").append("File Name: ").append(ftpDetails.getcertification().trim());
		    }
		}
	    }
	}
	else {
	   sb.append(System.getProperty("line.separator"));
	   sb.append("[ftpDropDetails]");  
	}
	
	session.setAttribute("emailBody", emailBodySB);
	
	return sb;
    }
    
    @Override 
    public StringBuffer exportConfigMessageSpecSection(HttpSession session,Integer configId) throws Exception {
	
	//Get configuration message specs
	configurationMessageSpecs messageSpecDetails = utConfigurationDAO.getMessageSpecs(configId);
	
	StringBuffer sb = new StringBuffer();
	sb.append("[messageSpecDetails|");
	sb.append("0|");
	sb.append(messageSpecDetails.gettemplateFile()).append("|");
	sb.append(messageSpecDetails.getmessageTypeCol()).append("|");
	sb.append(messageSpecDetails.getmessageTypeVal()).append("|");
	sb.append(messageSpecDetails.gettargetOrgCol()).append("|");
	sb.append(messageSpecDetails.getcontainsHeaderRow()).append("|");
	sb.append(messageSpecDetails.getrptField1()).append("|");
	sb.append(messageSpecDetails.getrptField2()).append("|");
	sb.append(messageSpecDetails.getrptField3()).append("|");
	sb.append(messageSpecDetails.getrptField4()).append("|");
	sb.append(messageSpecDetails.getSourceSubOrgCol()).append("|");
	sb.append(messageSpecDetails.getExcelstartrow()).append("|");
	sb.append(messageSpecDetails.getExcelskiprows()).append("|");
	sb.append(messageSpecDetails.getParsingTemplate()).append("|");
	sb.append(messageSpecDetails.getFileNameConfigHeader()).append("|");
	sb.append(messageSpecDetails.getTotalHeaderRows());
	sb.append("]");
	
	StringBuffer emailBodySB = (StringBuffer) session.getAttribute("emailBody");
	
	if(messageSpecDetails.gettemplateFile() != null) {
	    if(!messageSpecDetails.gettemplateFile().isEmpty()) {
		emailBodySB.append("<br />The following template File needs to be MOVED to the organization templates folder <br />").append("File Name: ").append(messageSpecDetails.gettemplateFile().trim());
	    }
	}

	if(messageSpecDetails.getParsingTemplate() != null) {
	    if(!messageSpecDetails.getParsingTemplate().isEmpty()) {
		emailBodySB.append("<br />The following parsing File needs to be UPLOADED to the configuration <br />").append("File Name: ").append(messageSpecDetails.getParsingTemplate().trim());
	    }
	}
	
	session.setAttribute("emailBody", emailBodySB);
	
	return sb;
    }
    
    @Override 
    public StringBuffer exportConfigFieldsSection(Integer configId, Integer transportDetailsId) throws Exception {
	
	//Get configuration fields
	List<configurationFormFields> fields = configurationTransportDAO.getConfigurationFields(configId,transportDetailsId);
	Iterator<configurationFormFields> fieldsIt = fields.iterator();
	
	List<configurationDataTranslations> dataTranslations = utConfigurationDAO.getDataTranslations(configId);
	
	StringBuffer sb = new StringBuffer();
	
	while (fieldsIt.hasNext()) {
	    configurationFormFields field = fieldsIt.next();
	    sb.append("[fields|");
	    sb.append("0|0|0|");
	    sb.append(field.getFieldNo()).append("|");
	    sb.append(field.getFieldDesc()).append("|");
	    sb.append(field.getValidationType()).append("|");
	    sb.append(field.getRequired()).append("|");
	    sb.append(field.getUseField()).append("|");
	    sb.append(field.getAssociatedFieldNo()).append("|");
	    sb.append(field.getDefaultValue()).append("|");
	    sb.append(field.getSampleData());
	    sb.append("]");
	    
	    for(configurationDataTranslations dTs : dataTranslations) {
		if(dTs.getFieldId() == field.getId()) {
		    sb.append(System.getProperty("line.separator"));
		    sb.append("[fieldDTS|");
		    sb.append("0|0|");
		    sb.append(dTs.getCrosswalkId()).append("|");
		    sb.append(dTs.getMacroId()).append("|");
		    sb.append(dTs.getPassClear()).append("|");
		    sb.append(dTs.getFieldA()).append("|");
		    sb.append(dTs.getFieldB()).append("|");
		    sb.append(dTs.getConstant1()).append("|");
		    sb.append(dTs.getConstant2()).append("|");
		    sb.append(dTs.getProcessOrder()).append("|");
		    sb.append(dTs.getCategoryId()).append("|");
		    sb.append(dTs.getDefaultValue());
		    sb.append("]");
		}
	    }
	    
	    if(fieldsIt.hasNext()) {
		sb.append(System.getProperty("line.separator"));
	    }
	}
	
	return sb;
    }
    
    @Override 
    public StringBuffer exportConfigSchedulingSection(Integer configId) throws Exception {
	
	//Get configuration schedule
	configurationSchedules scheduleDetails = utConfigurationDAO.getScheduleDetails(configId);
	
	StringBuffer sb = new StringBuffer();
	sb.append("[scheduleDetails|");
	sb.append("0|");
	sb.append(scheduleDetails.gettype()).append("|");
	sb.append(scheduleDetails.getprocessingType()).append("|");
	sb.append(scheduleDetails.getnewfileCheck()).append("|");
	sb.append(scheduleDetails.getprocessingDay()).append("|");
	sb.append(scheduleDetails.getprocessingTime());
	sb.append("]");
	
	return sb;
    }
    
    @Override 
    public StringBuffer exportOrgCrosswalks(Integer orgId) throws Exception {
	
	List crosswalks = getCrosswalksForExport(orgId);
	
	StringBuffer sb = new StringBuffer();
		
	if(!crosswalks.isEmpty()) {

	    Iterator cwDataIt = crosswalks.iterator();
	    
	    while (cwDataIt.hasNext()) {
		
		Object cwDatarow[] = (Object[]) cwDataIt.next();
		
		sb.append("[crosswalks|");
		sb.append(cwDatarow[0]).append("|");
		sb.append(cwDatarow[1]).append("|");
		sb.append(cwDatarow[2]).append("|");
		sb.append(cwDatarow[3]).append("|");
		sb.append(cwDatarow[4]).append("|");
		sb.append(cwDatarow[5]).append("|");
		sb.append(cwDatarow[6].toString().trim());
		sb.append("]");
		
		if(cwDataIt.hasNext()) {
		    sb.append(System.getProperty("line.separator"));
		}
	    }
	}
	else {
	   sb.append(System.getProperty("line.separator"));
	   sb.append("[crosswalks]");  
	}
	
	return sb;
    }
    
    public List getCrosswalksForExport(Integer orgId) throws Exception {
	
	String sqlStatement = "select a.id, a.name, a.fileDelimiter, a.fileName, b.sourceValue, b.targetValue, b.descValue " 
	+ "from crosswalks a inner join rel_crosswalkdata b on b.crosswalkId = a.id "
	+ "where a.orgId = " + orgId + " "
	+ "order by a.id";
	 
	return utConfigurationDAO.getDTCWForDownload(sqlStatement);
    }
    
    @Override 
    public StringBuffer  exportConnectionSrcDetails(utConfiguration configDetails,Organization orgDetails) throws Exception {
	
	StringBuffer sb = new StringBuffer();
	sb.append("[srcconfig|");
	sb.append(configDetails.getconfigName().trim()).append("|");
	sb.append(orgDetails.getCleanURL().trim());
	sb.append("]");
	
	return sb;
    }
    
    @Override 
    public StringBuffer  exportConnectionTgtDetails(utConfiguration configDetails,Organization orgDetails) throws Exception {
	
	StringBuffer sb = new StringBuffer();
	sb.append("[tgtconfig|");
	sb.append(configDetails.getconfigName().trim()).append("|");
	sb.append(orgDetails.getCleanURL().trim());
	sb.append("]");
	
	return sb;
    }
    
    @Override 
    public StringBuffer exportConnectionFields(Integer connectionId) throws Exception {
	
	List<configurationconnectionfieldmappings> connectionFields = configurationTransportDAO.getConnectionFieldMappingsByConnectionId(connectionId);
	
	StringBuffer sb = new StringBuffer();
		
	if(!connectionFields.isEmpty()) {
	    
	    Iterator<configurationconnectionfieldmappings> fieldsIt = connectionFields.iterator();
	    
	    while (fieldsIt.hasNext()) {
		configurationconnectionfieldmappings field = fieldsIt.next();
		
		sb.append("[fieldmapping|");
		sb.append(0).append("|");
		sb.append(0).append("|");
		sb.append(0).append("|");
		sb.append(0).append("|");
		sb.append(field.getFieldNo()).append("|");
		sb.append(field.getFieldDesc()).append("|");
		sb.append(field.isUseField()).append("|");
		sb.append(field.getAssociatedFieldNo()).append("|");
		sb.append(field.getPopulateErrorFieldNo()).append("|");
		sb.append(field.getDefaultValue());
		sb.append("]");
		
		if(fieldsIt.hasNext()) {
		    sb.append(System.getProperty("line.separator"));
		}
	    }
	}
	else {
	   sb.append(System.getProperty("line.separator"));
	   sb.append("[fieldmapping]");  
	}
	
	return sb;
    }
}

