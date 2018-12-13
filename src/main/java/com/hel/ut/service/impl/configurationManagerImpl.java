package com.hel.ut.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hel.ut.dao.configurationDAO;
import com.hel.ut.dao.organizationDAO;
import com.hel.ut.model.CrosswalkData;
import com.hel.ut.model.HL7Details;
import com.hel.ut.model.HL7ElementComponents;
import com.hel.ut.model.HL7Elements;
import com.hel.ut.model.HL7Segments;
import com.hel.ut.model.Macros;
import com.hel.ut.model.Organization;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationCCDElements;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationConnectionReceivers;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.configurationDataTranslations;
import com.hel.ut.model.configurationExcelDetails;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.configurationSchedules;
import com.hel.ut.model.watchlist;
import com.hel.ut.model.watchlistEntry;
import com.hel.ut.reference.fileSystem;
import com.hel.ut.service.configurationManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import org.hibernate.SessionFactory;
import org.springframework.web.multipart.MultipartFile;

@Service
public class configurationManagerImpl implements configurationManager {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private configurationDAO configurationDAO;

    @Autowired
    private organizationDAO organizationDAO;

    @Override
    public Integer createConfiguration(utConfiguration configuration) {
	configuration.setstepsCompleted(1);
	return configurationDAO.createConfiguration(configuration);
    }

    @Override
    public void updateConfiguration(utConfiguration configuration) {
	configurationDAO.updateConfiguration(configuration);
    }

    @Override
    public utConfiguration getConfigurationById(int configId) {
	return configurationDAO.getConfigurationById(configId);
    }

    @Override
    public List<utConfiguration> getConfigurationsByOrgId(int configId, String searchTerm) {
	return configurationDAO.getConfigurationsByOrgId(configId, searchTerm);
    }

    @Override
    public List<utConfiguration> getActiveConfigurationsByOrgId(int configId) {
	return configurationDAO.getActiveConfigurationsByOrgId(configId);
    }

    @Override
    public utConfiguration getConfigurationByName(String configName, int orgId) {
	return configurationDAO.getConfigurationByName(configName, orgId);
    }

    @Override
    public List<utConfiguration> getConfigurations() {
	return configurationDAO.getConfigurations();
    }

    @Override
    public List<utConfiguration> getLatestConfigurations(int maxResults) {
	return configurationDAO.getLatestConfigurations(maxResults);
    }

    @Override
    public Long findTotalConfigs() {
	return configurationDAO.findTotalConfigs();
    }

    @Override
    public Long getTotalConnections(int configId) {
	return configurationDAO.getTotalConnections(configId);
    }

    @Override
    public void updateCompletedSteps(int configId, int stepCompleted) {
	configurationDAO.updateCompletedSteps(configId, stepCompleted);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getFileTypes() {
	return configurationDAO.getFileTypes();
    }

    @Override
    public String getFileTypesById(int id) {
	return configurationDAO.getFileTypesById(id);
    }

    @Override
    public List<configurationDataTranslations> getDataTranslations(int configId) {
	return configurationDAO.getDataTranslations(configId);
    }

    @Override
    public String getFieldName(int fieldId) {
	return configurationDAO.getFieldName(fieldId);
    }

    @Override
    public void deleteDataTranslations(int configId, int categoryId) {
	configurationDAO.deleteDataTranslations(configId, categoryId);
    }

    @Override
    public void saveDataTranslations(configurationDataTranslations translations) {
	configurationDAO.saveDataTranslations(translations);
    }

    @Override
    public List<Macros> getMacros() {
	return configurationDAO.getMacros();
    }

    @Override
    public List<Macros> getMacrosByCategory(int categoryId) {
	return configurationDAO.getMacrosByCategory(categoryId);
    }

    @Override
    public Macros getMacroById(int macroId) {
	return configurationDAO.getMacroById(macroId);
    }

    @Override
    public List<configurationConnection> getAllConnections() {
	return configurationDAO.getAllConnections();
    }

    @Override
    public List<configurationConnection> getLatestConnections(int maxResults) {
	return configurationDAO.getLatestConnections(maxResults);
    }

    @Override
    public List<configurationConnection> getConnectionsByConfiguration(int configId, int userId) {
	return configurationDAO.getConnectionsByConfiguration(configId, userId);
    }

    @Override
    public List<configurationConnection> getConnectionsByTargetConfiguration(int configId) {
	return configurationDAO.getConnectionsByTargetConfiguration(configId);
    }

    @Override
    public Integer saveConnection(configurationConnection connection) {
	return configurationDAO.saveConnection(connection);
    }

    @Override
    public configurationConnection getConnection(int connectionId) {
	return configurationDAO.getConnection(connectionId);
    }

    @Override
    public void updateConnection(configurationConnection connection) {
	configurationDAO.updateConnection(connection);
    }

    @Override
    public configurationSchedules getScheduleDetails(int configId) {
	return configurationDAO.getScheduleDetails(configId);
    }

    @Override
    public void saveSchedule(configurationSchedules scheduleDetails) {
	configurationDAO.saveSchedule(scheduleDetails);
    }

    @Override
    public configurationMessageSpecs getMessageSpecs(int configId) {
	return configurationDAO.getMessageSpecs(configId);
    }

    @Override
    public List<utConfiguration> getActiveConfigurationsByUserId(int userId, int transportMethod) throws Exception {
	return configurationDAO.getActiveConfigurationsByUserId(userId, transportMethod);
    }

    @Override
    public List<configurationConnectionSenders> getConnectionSenders(int connectionId) {
	return configurationDAO.getConnectionSenders(connectionId);
    }

    @Override
    public List<configurationConnectionReceivers> getConnectionReceivers(int connectionId) {
	return configurationDAO.getConnectionReceivers(connectionId);
    }

    @Override
    public void saveConnectionSenders(configurationConnectionSenders senders) {
	configurationDAO.saveConnectionSenders(senders);
    }

    @Override
    public void saveConnectionReceivers(configurationConnectionReceivers receivers) {
	configurationDAO.saveConnectionReceivers(receivers);
    }

    @Override
    public void removeConnectionSenders(int connectionId) {
	configurationDAO.removeConnectionSenders(connectionId);
    }

    @Override
    public void removeConnectionReceivers(int connectionId) {
	configurationDAO.removeConnectionReceivers(connectionId);
    }

    @Override
    public void updateMessageSpecs(configurationMessageSpecs messageSpecs, int transportDetailId, int fileType) throws Exception {

	boolean processFile = false;
	String fileName = null;
	String cleanURL = null;
	int clearFields = 0;
	fileSystem dir = null;

	MultipartFile file = messageSpecs.getFile();

	//Need to get the selected organization clean url
	utConfiguration configDetails = configurationDAO.getConfigurationById(messageSpecs.getconfigId());
	Organization orgDetails = organizationDAO.getOrganizationById(configDetails.getorgId());
	cleanURL = orgDetails.getcleanURL();

	//If a file is uploaded
	if (file != null && !file.isEmpty()) {
	    processFile = true;

	    clearFields = 1;

	    fileName = file.getOriginalFilename();

	    InputStream inputStream = null;
	    OutputStream outputStream = null;

	    try {
		inputStream = file.getInputStream();
		File newFile = null;

		//Set the directory to save the uploaded message type template to
		dir = new fileSystem();
		dir.setDir(cleanURL, "templates");

		newFile = new File(dir.getDir() + fileName);

		if (newFile.exists()) {
		    int i = 1;
		    while (newFile.exists()) {
			int iDot = fileName.lastIndexOf(".");
			newFile = new File(dir.getDir() + fileName.substring(0, iDot) + "_(" + ++i + ")" + fileName.substring(iDot));
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
		inputStream.close();

		//Set the filename to the file name
		messageSpecs.settemplateFile(fileName);

	    } catch (IOException e) {
		e.printStackTrace();
		throw new Exception(e);
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
		dir = new fileSystem();
		dir.setDir(cleanURL, "templates");

		newFile = new File(dir.getDir() + parsingScriptFileName);

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

	configurationDAO.updateMessageSpecs(messageSpecs, transportDetailId, clearFields);

	if (processFile == true) {
	    try {
		loadExcelContents(messageSpecs.getconfigId(), transportDetailId, fileName, dir);
	    } catch (Exception e1) {
		e1.printStackTrace();
		throw new Exception(e1);
	    }

	}

    }

    /**
     * The 'loadExcelContents' will take the contents of the uploaded excel template file and populate the corresponding utConfiguration form fields table. This function will split up the contents into the appropriate buckets. Buckets (1 - 4) will be separated by spacer rows with in the excel file.
     *
     * @param id value of the latest added utConfiguration
     * @param fileName	file name of the uploaded excel file.
     * @param dir	the directory of the uploaded file
     *
     */
    public void loadExcelContents(int id, int transportDetailId, String fileName, fileSystem dir) throws Exception {
	configurationDAO.loadExcelContents(id, transportDetailId, fileName, dir);
    }

    @Override
    public List<configurationDataTranslations> getDataTranslationsWithFieldNo(
	    int configId, int categoryId) {
	return configurationDAO.getDataTranslationsWithFieldNo(configId, categoryId);
    }

    @Override
    public List<CrosswalkData> getCrosswalkData(int cwId) {
	return configurationDAO.getCrosswalkData(cwId);
    }

    @Override
    public HL7Details getHL7Details(int configId) {
	return configurationDAO.getHL7Details(configId);
    }

    @Override
    public List<HL7Segments> getHL7Segments(int hl7Id) {
	return configurationDAO.getHL7Segments(hl7Id);
    }

    @Override
    public List<HL7Elements> getHL7Elements(int hl7Id, int segmentId) {
	return configurationDAO.getHL7Elements(hl7Id, segmentId);
    }

    @Override
    public List<HL7ElementComponents> getHL7ElementComponents(int elementId) {
	return configurationDAO.getHL7ElementComponents(elementId);
    }

    @Override
    public void updateHL7Details(HL7Details details) {
	configurationDAO.updateHL7Details(details);
    }

    @Override
    public void updateHL7Segments(HL7Segments segment) {
	configurationDAO.updateHL7Segments(segment);
    }

    @Override
    public void updateHL7Elements(HL7Elements element) {
	configurationDAO.updateHL7Elements(element);
    }

    @Override
    public void updateHL7ElementComponent(HL7ElementComponents component) {
	configurationDAO.updateHL7ElementComponent(component);
    }

    @Override
    public int saveHL7Details(HL7Details details) {
	return configurationDAO.saveHL7Details(details);
    }

    @Override
    public int saveHL7Segment(HL7Segments newSegment) {
	return configurationDAO.saveHL7Segment(newSegment);
    }

    @Override
    public int saveHL7Element(HL7Elements newElement) {
	return configurationDAO.saveHL7Element(newElement);
    }

    @Override
    public void saveHL7Component(HL7ElementComponents newcomponent) {
	configurationDAO.saveHL7Component(newcomponent);
    }

    @Override
    public String getMessageTypeNameByConfigId(Integer configId) {
	return configurationDAO.getMessageTypeNameByConfigId(configId);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getEncodings() {
	return configurationDAO.getEncodings();
    }

    @Override
    public void removeHL7ElementComponent(Integer componentId) {
	configurationDAO.removeHL7ElementComponent(componentId);
    }

    @Override
    public void removeHL7Element(Integer elementId) {
	configurationDAO.removeHL7Element(elementId);
    }

    @Override
    public void removeHL7Segment(Integer segmentId) {
	configurationDAO.removeHL7Segment(segmentId);
    }

    @Override
    public List<configurationCCDElements> getCCDElements(Integer configId) throws Exception {
	return configurationDAO.getCCDElements(configId);
    }

    @Override
    public void saveCCDElement(configurationCCDElements ccdElement) throws Exception {
	configurationDAO.saveCCDElement(ccdElement);
    }

    @Override
    public configurationCCDElements getCCDElement(Integer elementId) throws Exception {
	return configurationDAO.getCCDElement(elementId);
    }

    @Override
    public configurationExcelDetails getExcelDetails(Integer configId, Integer orgId)
	    throws Exception {
	return configurationDAO.getExcelDetails(configId, orgId);
    }

    @Override
    public void updateExcelConfigDetails(Integer orgId, configurationMessageSpecs messageSpecs) throws Exception {
	configurationDAO.updateExcelConfigDetails(orgId, messageSpecs);
    }

    @Override
    public Integer getFieldCrosswalkIdByFieldName(int configId, String fieldName) throws Exception {
	return configurationDAO.getFieldCrosswalkIdByFieldName(configId, fieldName);
    }

    @Override
    public List<utConfiguration> getActiveConfigurationsByTransportType(int userId, List<Integer> transportMethods) throws Exception {
	return configurationDAO.getActiveConfigurationsByTransportType(userId, transportMethods);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getZipTypes() {
	return configurationDAO.getZipTypes();
    }

    @SuppressWarnings("rawtypes")
    public List getrestAPITypes() {
	return configurationDAO.getrestAPITypes();
    }

    @Override
    public List<configurationConnection> getConnectionsBySrcAndTargetConfigurations(
	    int sourceConfigId, int targetConfigId) {
	return configurationDAO.getConnectionsBySrcAndTargetConfigurations(sourceConfigId, targetConfigId);
    }
    
    @SuppressWarnings("rawtypes")
    public List getrestAPIFunctions(Integer orgId) {
	return configurationDAO.getrestAPIFunctions(orgId);
    }
    
    
    @Override
    public List<watchlist> getDashboardWatchList() throws Exception {
	return configurationDAO.getDashboardWatchList();
    }
    
    @Override
    public watchlist getDashboardWatchListById(int watchId) throws Exception {
	return configurationDAO.getDashboardWatchListById(watchId);
    }
    
    @Override
    public Integer saveDashboardWatchListEntry(watchlist watchListEntry) {
	return configurationDAO.saveDashboardWatchListEntry(watchListEntry);
    }

    @Override
    public void updateDashboardWatchListEntry(watchlist watchListEntry) {
	configurationDAO.updateDashboardWatchListEntry(watchListEntry);
    }
    
    @Override
    public List<watchlistEntry> getWatchListEntries(Date fromDate, Date toDate) throws Exception  {
	return configurationDAO.getWatchListEntries(fromDate,toDate);
    }
    
    @Override
    public void CheckDashboardWatchList() throws Exception {
	
	List<watchlist> watchListEntries = configurationDAO.getDashboardWatchListToInsert();
	
	if(watchListEntries != null) {
	    if(!watchListEntries.isEmpty()) {
		
		for(watchlist entries : watchListEntries) {
		   
		    watchlistEntry newEntry = new watchlistEntry();
		    newEntry.setWatchlistentryId(entries.getId());
		    newEntry.setConfigId(entries.getConfigId());
		    newEntry.setOrgId(entries.getOrgId());
		    newEntry.setMessageTypeId(entries.getMessageTypeId());
		   
		    configurationDAO.insertDashboardWatchListEntry(newEntry);
		    
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
		    
		    configurationDAO.updateDashboardWatchListEntry(entries);
 		}
	    }
	}
    }
    
    @Override
    public void updateMessageSpecs(configurationMessageSpecs messageSpecs) throws Exception {
	configurationDAO.updateMessageSpecs(messageSpecs, 0, 0);
    }
    
    @Override
    public void deleteWatchEntry(Integer watchId) throws Exception {
	configurationDAO.deleteWatchEntry(watchId);
    }
    
    @Override
    public List<watchlistEntry> getGenericWatchListEntries(Date fromDate, Date toDate) throws Exception  {
	return configurationDAO.getGenericWatchListEntries(fromDate,toDate);
    }
    
    @Override
    public void insertDashboardWatchListEntry(watchlistEntry watchListEntry) {
	configurationDAO.insertDashboardWatchListEntry(watchListEntry);
    }
    
    @Override
    public watchlistEntry getWatchListEntry(Integer entryId) throws Exception {
	return configurationDAO.getWatchListEntry(entryId);
    }
}
