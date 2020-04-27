package com.hel.ut.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hel.ut.model.TransportMethod;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.configurationFileDropFields;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.configurationWebServiceFields;
import com.hel.ut.model.configurationWebServiceSenders;
import com.hel.ut.model.Organization;
import com.hel.ut.model.configurationFTPFields;
import com.hel.ut.model.configurationTransportMessageTypes;
import com.hel.ut.reference.fileSystem;
import com.hel.ut.service.organizationManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import com.hel.ut.dao.utConfigurationTransportDAO;
import com.hel.ut.model.configurationCCDElements;
import com.hel.ut.model.configurationconnectionfieldmappings;
import com.hel.ut.model.organizationDirectDetails;
import com.hel.ut.model.utConfiguration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;
import javax.annotation.Resource;
import org.hibernate.Query;

@Service
public class utConfigurationTransportManagerImpl implements utConfigurationTransportManager {

    @Autowired
    private utConfigurationTransportDAO configurationTransportDAO;

    @Autowired
    private organizationManager organizationManager;

    @Autowired
    private utConfigurationManager utConfigurationManager;
    
    @Resource(name = "myProps")
    private Properties myProps;

    @Override
    public configurationTransport getTransportDetails(int configId) throws Exception {
        return configurationTransportDAO.getTransportDetails(configId);
    }

    @Override
    public configurationTransport getTransportDetailsByTransportMethod(int configId, int transportMethod) {
        return configurationTransportDAO.getTransportDetailsByTransportMethod(configId, transportMethod);
    }

    @Override
    public Integer updateTransportDetails(utConfiguration configurationDetails, configurationTransport transportDetails) throws Exception {

        MultipartFile CCDTemplatefile = transportDetails.getCcdTemplatefile();
        //If a file is uploaded
        if (CCDTemplatefile != null && !CCDTemplatefile.isEmpty()) {

            String CCDTemplatefileName = CCDTemplatefile.getOriginalFilename();

            int orgId = utConfigurationManager.getConfigurationById(transportDetails.getconfigId()).getorgId();

            Organization orgDetails = organizationManager.getOrganizationById(orgId);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = CCDTemplatefile.getInputStream();
                File newCCDTemplateFile = null;

                newCCDTemplateFile = new File(myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/templates/"+ CCDTemplatefileName);

                if (newCCDTemplateFile.exists()) {
                    newCCDTemplateFile.delete();
                }
                newCCDTemplateFile.createNewFile();

                outputStream = new FileOutputStream(newCCDTemplateFile);
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                outputStream.close();

                //Set the filename to the file name
                transportDetails.setCcdSampleTemplate(CCDTemplatefileName);
		
		//If configuration is a target, populate the CCD data elements from the uploaded file.
		if(configurationDetails.getType() == 2 && "xml".equals(transportDetails.getfileExt())) {
		    insertCCDDataElements(configurationDetails.getId(),newCCDTemplateFile);
		}

            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception(e);
            }

        }

        int transportDetailId;

        transportDetailId = (Integer) configurationTransportDAO.updateTransportDetails(transportDetails);

        return transportDetailId;
    }
    
    @SuppressWarnings("rawtypes")
    public List getTransportMethods() {
        return configurationTransportDAO.getTransportMethods();
    }
    
    @SuppressWarnings("rawtypes")
    public List getTransportMethodsByType(utConfiguration configurationDetails) {
        return configurationTransportDAO.getTransportMethodsByType(configurationDetails);
    }

    @Override
    public List<configurationFormFields> getConfigurationFields(int configId, int transportDetailId) {
        return configurationTransportDAO.getConfigurationFields(configId, transportDetailId);
    }

    @Override
    public List<configurationFormFields> getConfigurationFieldsByBucket(int configId, int transportDetailId, int bucket) throws Exception {
        return configurationTransportDAO.getConfigurationFieldsByBucket(configId, transportDetailId, bucket);
    }

    @Override
    public configurationFormFields getConfigurationFieldsByFieldNo(int configId, int transportDetailId, int fieldNo) throws Exception {
        return configurationTransportDAO.getConfigurationFieldsByFieldNo(configId, transportDetailId, fieldNo);
    }

    @Override
    public void updateConfigurationFormFields(configurationFormFields formField) {
        configurationTransportDAO.updateConfigurationFormFields(formField);
    }

    @Override
    public List<configurationFTPFields> getTransportFTPDetails(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransportFTPDetails(transportDetailId);
    }

    @Override
    public configurationFTPFields getTransportFTPDetailsPush(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransportFTPDetailsPush(transportDetailId);
    }

    @Override
    public configurationFTPFields getTransportFTPDetailsPull(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransportFTPDetailsPull(transportDetailId);
    }

    @Override
    public void saveTransportFTP(int orgId, configurationFTPFields FTPFields) throws Exception {

        /* Need to upload the certificate if uploaded */
        if (FTPFields.getfile() != null && FTPFields.getfile().getSize() > 0) {

            //Need to get the cleanURL of the organization for the brochure
            Organization orgDetails = organizationManager.getOrganizationById(orgId);

            MultipartFile file = FTPFields.getfile();
            String fileName = file.getOriginalFilename();

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = file.getInputStream();
                File newFile = null;

                newFile = new File(myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/certificates/" + fileName);

                if (!newFile.exists()) {
                    newFile.createNewFile();
                }

                outputStream = new FileOutputStream(newFile);
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                outputStream.close();

                //Set the filename to the original file name
                FTPFields.setcertification(fileName);

            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception(e);
            }

        }

        configurationTransportDAO.saveTransportFTP(FTPFields);
	
    }

    @Override
    public String getTransportMethodById(int Id) {
        return configurationTransportDAO.getTransportMethodById(Id);
    }

    @Override
    public List<configurationTransportMessageTypes> getTransportMessageTypes(int configTransportId) {
        return configurationTransportDAO.getTransportMessageTypes(configTransportId);
    }

    @Override
    public void deleteTransportMessageTypes(int configTransportId) {
        configurationTransportDAO.deleteTransportMessageTypes(configTransportId);
    }

    @Override
    public void saveTransportMessageTypes(configurationTransportMessageTypes messageType) {
        configurationTransportDAO.saveTransportMessageTypes(messageType);
    }

    @Override
    public List<configurationFormFields> getRequiredFieldsForConfig(Integer configId) {
        return configurationTransportDAO.getRequiredFieldsForConfig(configId);
    }

    @Override
    public List<configurationFormFields> getCffByValidationType(Integer configId, Integer validationTypeId) {
        return configurationTransportDAO.getCffByValidationType(configId, validationTypeId);
    }

    @Override
    public List<configurationTransport> getDistinctConfigTransportForOrg(Integer orgId, Integer transportMethodId) {
        return configurationTransportDAO.getDistinctConfigTransportForOrg(orgId, transportMethodId);
    }

    @Override
    public List<configurationMessageSpecs> getConfigurationMessageSpecsForUserTransport(Integer userId, Integer transportMethodId, boolean getZeroMessageTypeCol) {
        return configurationTransportDAO.getConfigurationMessageSpecsForUserTransport(userId, transportMethodId, getZeroMessageTypeCol);
    }

    @Override
    public configurationFormFields getCFFByFieldNo(int configId, int fieldNo)
            throws Exception {
        return configurationTransportDAO.getCFFByFieldNo(configId, fieldNo);
    }

    @Override
    public List<configurationMessageSpecs> getConfigurationMessageSpecsForOrgTransport(Integer orgId, Integer transportMethodId, boolean getZeroMessageTypeCol) {
        return configurationTransportDAO.getConfigurationMessageSpecsForOrgTransport(orgId, transportMethodId, getZeroMessageTypeCol);
    }

    @Override
    public List<configurationTransport> getConfigTransportForFileExtAndPath(String fileExt, Integer transportMethodId, Integer status,Integer transportDetailsId) {
        return configurationTransportDAO.getConfigTransportForFileExtAndPath(fileExt, transportMethodId, status, transportDetailsId);
    }

    @Override
    public List<configurationTransport> getTransportListForFileExtAndPath(String fileExt, Integer transportMethodId, Integer status, Integer transportDetailsId) {
        return configurationTransportDAO.getTransportListForFileExtAndPath(fileExt, transportMethodId, status, transportDetailsId);
    }

    @Override
    public configurationTransport getTransportDetailsByTransportId(Integer transportId) {
        return configurationTransportDAO.getTransportDetailsByTransportId(transportId);
    }

    @Override
    public Integer getOrgIdForFTPPath(configurationFTPFields ftpInfo)
            throws Exception {
        return configurationTransportDAO.getOrgIdForFTPPath(ftpInfo);
    }

    @Override
    public Integer getMinMaxFileSize(String fileExt, Integer transportMethodId) {
        return configurationTransportDAO.getMinMaxFileSize(fileExt, transportMethodId);
    }

    @Override
    public List<configurationTransport> getCountContainsHeaderRow(String fileExt, Integer transportMethodId) {
        return configurationTransportDAO.getCountContainsHeaderRow(fileExt, transportMethodId);
    }

    @Override
    public List<Integer> getConfigCount(String fileExt, Integer transportMethodId, Integer fileDelimiter) {
        return configurationTransportDAO.getConfigCount(fileExt, transportMethodId, fileDelimiter);

    }

    @Override
    public List<configurationTransport> getDistinctDelimCharForFileExt(
            String fileExt, Integer transportMethodId) {
        return configurationTransportDAO.getDistinctDelimCharForFileExt(fileExt, transportMethodId);
    }

    @Override
    public void saveTransportFileDrop(configurationFileDropFields fileDropFields) throws Exception {
        configurationTransportDAO.saveTransportFileDrop(fileDropFields);
	//Makes sure the directory is created
        fileSystem dir = new fileSystem();
	dir.creatFTPDirectory(myProps.getProperty("ut.directory.utRootDir") + fileDropFields.getDirectory().replace("/Applications/HELProductSuite/universalTranslator/", "").replace("/home/HELProductSuite/universalTranslator/", "").replace("/HELProductSuite/universalTranslator/", ""));
	
    }

    @Override
    public List<configurationFileDropFields> getTransFileDropDetails(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransFileDropDetails(transportDetailId);
    }

    @Override
    public configurationFileDropFields getTransFileDropDetailsPush(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransFileDropDetailsPush(transportDetailId);
    }

    @Override
    public configurationFileDropFields getTransFileDropDetailsPull(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransFileDropDetailsPull(transportDetailId);
    }

    @Override
    public List<configurationTransport> getTransportEncoding(String fileExt, Integer transportMethodId) {
        return configurationTransportDAO.getTransportEncoding(fileExt, transportMethodId);
    }

    @Override
    public Integer getOrgIdForFileDropPath(
           configurationFileDropFields fileDropInfo) throws Exception {
        return configurationTransportDAO.getOrgIdForFileDropPath(fileDropInfo);
    }

    @Override
    public List<TransportMethod> getTransportMethods(List<Integer> statusIds) {
        return configurationTransportDAO.getTransportMethods(statusIds);
    }

    @Override
    public List<configurationTransport> getConfigurationTransportFileExtByFileType(
            Integer orgId, Integer transportMethodId,
            List<Integer> fileTypeIds, List<Integer> statusIds, boolean distinctOnly, boolean foroutboundProcessing) {
        return configurationTransportDAO.getConfigurationTransportFileExtByFileType(orgId, transportMethodId, fileTypeIds, statusIds, distinctOnly, foroutboundProcessing);
    }

    @Override
    public List<configurationWebServiceFields> getTransWSDetails(int transportDetailId) throws Exception {
        List<configurationWebServiceFields> wsFieldsList = configurationTransportDAO.getTransWSDetails(transportDetailId);
        for (configurationWebServiceFields wsFields : wsFieldsList) {
            if (wsFields.getMethod() == 1) {
                wsFields.setSenderDomainList(getWSSenderList(transportDetailId));
            }
        }
        return wsFieldsList;
    }

    @Override
    public void saveTransportWebService(configurationWebServiceFields wsFields) throws Exception {
        configurationTransportDAO.saveTransportWebService(wsFields);
    }

    @Override
    public List<configurationTransport> getDistinctTransportDetailsForOrgByTransportMethodId(
            Integer transportMethodId, Integer status, Integer orgId) {
        return configurationTransportDAO.getDistinctTransportDetailsForOrgByTransportMethodId(transportMethodId, status, orgId);
    }

    @Override
    public List<configurationTransport> getCTForOrgByTransportMethodId(
            Integer transportMethodId, Integer status, Integer orgId) {
        return configurationTransportDAO.getCTForOrgByTransportMethodId(transportMethodId, status, orgId);
    }

    @Override
    public configurationWebServiceFields getTransWSDetailsPush(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransWSDetailsPush(transportDetailId);
    }

    @Override
    public configurationWebServiceFields getTransWSDetailsPull(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransWSDetailsPull(transportDetailId);
    }

    @Override
    public List<configurationWebServiceSenders> getWSSenderList(
            int transportDetailId) throws Exception {
        return configurationTransportDAO.getWSSenderList(transportDetailId);
    }

    @Override
    public void saveWSSender(configurationWebServiceSenders wsSender)
            throws Exception {
        configurationTransportDAO.saveWSSender(wsSender);
    }

    @Override
    public void deleteWSSender(configurationWebServiceSenders wsSender)
            throws Exception {
        configurationTransportDAO.deleteWSSender(wsSender);
    }

    @Override
    public boolean hasConfigsWithMasstranslations(
            Integer orgId, Integer transportMethodId) throws Exception {
        return configurationTransportDAO.hasConfigsWithMasstranslations(orgId, transportMethodId);
    }
    
    @Override
    public configurationTransport validateAPICall(String apiCustomCall) throws Exception {
	return configurationTransportDAO.validateAPICall(apiCustomCall);
    }
    
    @Override
    public configurationTransport validateAPIAuthentication(String[] credValue, String apiCustomCall) throws Exception {
	return configurationTransportDAO.validateAPIAuthentication(credValue,apiCustomCall);
    }
    
    @Override
    public String getRestAPIMethodName(Integer methodId) throws Exception {
	return configurationTransportDAO.getRestAPIMethodName(methodId);
    }
    
    @Override
    public Integer saveConfigurationFormFields(configurationFormFields formField) throws Exception {
	return configurationTransportDAO.saveConfigurationFormFields(formField);
    }
    
    @Override
    public List<configurationFormFields> getConfigurationFieldsToCopy(int configId) {
	return configurationTransportDAO.getConfigurationFieldsToCopy(configId);
    }
    
    @Override
    public void populateFieldsFromHELConfiguration(Integer configId, Integer transportId, Integer HELRegistryConfigId, String HELSchemaName, boolean reload) throws Exception {
	
	//Need to query to get a list of field for the passed in HELRegistryConfigId
	String sqlStatement = "select concat(case when a.requiredField = 1 then 'true' else 'false' end,'|',a.validationId,'|',a.dspPos,'|',b.elementName) as fields "
	    + "from "+HELSchemaName+".registry_configuration_dataelements a inner join "
	    + "registries.dataElements b on b.id = a.dataElementId "
	    + "where a.configurationId = " + HELRegistryConfigId + " "
	    + "order by a.dspPos";
	
	List<String> HELConfigurationFields = configurationTransportDAO.getHELConfigurationDetailsBySQL(sqlStatement);
	
	if(HELConfigurationFields != null) {
	    if(!HELConfigurationFields.isEmpty()) {
		
		if(reload) {
		    List<configurationFormFields> currentFormFields = configurationTransportDAO.getConfigurationFieldsToCopy(configId);
		    
		    if(currentFormFields != null) {
			if(!currentFormFields.isEmpty()) {
			    
			    for(String configurationFields : HELConfigurationFields) {
				boolean found = false;
				String[] configurationFieldsAsArray = configurationFields.split("\\|");

				String required = configurationFieldsAsArray[0];
				Integer validationId = Integer.parseInt(configurationFieldsAsArray[1]);
				Integer dspPos = Integer.parseInt(configurationFieldsAsArray[2]);
				String elementName = configurationFieldsAsArray[3];
				
				for(configurationFormFields configurationFormField : currentFormFields) {
				    
				    if(configurationFormField.getFieldDesc().trim().toLowerCase().equals(elementName.trim().toLowerCase())) {
					configurationFormField.setFieldNo(dspPos);
					configurationFormField.setValidationType(validationId);
					if("true".equals(required)) {
					    configurationFormField.setRequired(true);
					}
					else {
					    configurationFormField.setRequired(false);
					}
					configurationTransportDAO.updateConfigurationFormFields(configurationFormField);
					found = true;
				    }
				}
				
				if(!found) {
				    configurationFormFields newFormField = new configurationFormFields();
				    newFormField.setAssociatedFieldId(0);
				    newFormField.setconfigId(configId);
				    newFormField.settransportDetailId(transportId);
				    newFormField.setFieldNo(dspPos);
				    newFormField.setValidationType(validationId);
				    newFormField.setFieldDesc(elementName);
				    newFormField.setUseField(true);
				    if("true".equals(required)) {
					newFormField.setRequired(true);
				    }
				    else {
					newFormField.setRequired(false);
				    }

				    configurationTransportDAO.saveConfigurationFormFields(newFormField);
				}
			    }
			    
			    //Check to see if any fields were removed from the configuration but was previously saved
			    for(configurationFormFields configurationFormField : currentFormFields) {
				boolean fieldFound = false;
				
				for(String configurationFields : HELConfigurationFields) {
				    String[] configurationFieldsAsArray = configurationFields.split("\\|");
				    String elementName = configurationFieldsAsArray[3];
				    
				    if(configurationFormField.getFieldDesc().trim().toLowerCase().equals(elementName.trim().toLowerCase())) {
					fieldFound = true;
				    }
				}
				
				if(!fieldFound) {
				    configurationTransportDAO.deleteConfigurationFormField(configurationFormField.getId());
				    configurationTransportDAO.configurationDataTranslations(configurationFormField.getId());
				}
			    }
			}
			else {
			    for(String configurationFields : HELConfigurationFields) {
				String[] configurationFieldsAsArray = configurationFields.split("\\|");

				String required = configurationFieldsAsArray[0];
				Integer validationId = Integer.parseInt(configurationFieldsAsArray[1]);
				Integer dspPos = Integer.parseInt(configurationFieldsAsArray[2]);
				String elementName = configurationFieldsAsArray[3];

				configurationFormFields newFormField = new configurationFormFields();
				newFormField.setAssociatedFieldId(0);
				newFormField.setconfigId(configId);
				newFormField.settransportDetailId(transportId);
				newFormField.setFieldNo(dspPos);
				newFormField.setValidationType(validationId);
				newFormField.setFieldDesc(elementName);
				newFormField.setUseField(true);
				if("true".equals(required)) {
				    newFormField.setRequired(true);
				}
				else {
				    newFormField.setRequired(false);
				}

				configurationTransportDAO.saveConfigurationFormFields(newFormField);
			    }
			}
		    }
		}
		else {
		    for(String configurationFields : HELConfigurationFields) {
			String[] configurationFieldsAsArray = configurationFields.split("\\|");

			String required = configurationFieldsAsArray[0];
			Integer validationId = Integer.parseInt(configurationFieldsAsArray[1]);
			Integer dspPos = Integer.parseInt(configurationFieldsAsArray[2]);
			String elementName = configurationFieldsAsArray[3];

			configurationFormFields newFormField = new configurationFormFields();
			newFormField.setAssociatedFieldId(0);
			newFormField.setconfigId(configId);
			newFormField.settransportDetailId(transportId);
			newFormField.setFieldNo(dspPos);
			newFormField.setValidationType(validationId);
			newFormField.setFieldDesc(elementName);
			newFormField.setUseField(true);
			if("true".equals(required)) {
			    newFormField.setRequired(true);
			}
			else {
			    newFormField.setRequired(false);
			}

			configurationTransportDAO.saveConfigurationFormFields(newFormField);
		    }
		}
	    }
	}
    }
    
    @Override
    public organizationDirectDetails getDirectMessagingDetails(String DMDomain) throws Exception {
	return configurationTransportDAO.getDirectMessagingDetails(DMDomain);
    }
    
    @Override
    public organizationDirectDetails getDirectMessagingDetailsById(Integer organizationId) throws Exception {
	return configurationTransportDAO.getDirectMessagingDetailsById(organizationId);
    }
    
    @Override
    public void saveTransportDirectMessageDetails(organizationDirectDetails directDetails) throws Exception {
	configurationTransportDAO.saveTransportDirectMessageDetails(directDetails);
    }
    
    @Override
    public configurationTransport findConfigurationByDirectMessagKeyword(Integer orgId, String directMessageToAddress) throws Exception {
        return configurationTransportDAO.findConfigurationByDirectMessagKeyword(orgId, directMessageToAddress);
    }
    
    /**
     * The 'insertCCDDataELements' method will take in the configId and uploaded CCD Template file and scan the file
     * for all CCD data elements and create entries in the ccdelements table.
     * @param configId
     * @param ccdTemplateFile 
     */
    private void insertCCDDataElements(Integer configId, File ccdTemplateFile) throws Exception {
	
	//Check to see if there are any CCD Data elements already loaded
	List<configurationCCDElements> configCCDDataElements = utConfigurationManager.getCCDElements(configId);
	
	Reader fileReader = new FileReader(ccdTemplateFile);
        BufferedReader bufReader = new BufferedReader(fileReader);

	StringBuilder sb = new StringBuilder();
        String line = bufReader.readLine();
	String lineSubString = "";
	ArrayList<String> substrings = new ArrayList<String>();
	
	Integer endTag = 0;
	
        while( line != null){
	    endTag = 0;
            
	    if(line.contains("[@") && line.contains("@]")) {
		
		endTag = line.indexOf("@]")+2;
		
		lineSubString = line.substring(line.indexOf("[@"),line.indexOf("@]")+2);
		
		substrings.add(lineSubString);
		
		while(line.indexOf("[@", endTag) != -1) {
		    lineSubString = line.substring(line.indexOf("[@", endTag),line.indexOf("@]", endTag)+2);
		    substrings.add(lineSubString);
		    endTag = line.indexOf("@]", endTag)+2;
		}
		
	    }
	    
            line = bufReader.readLine();
        }
	
	bufReader.close();
	
	if(!substrings.isEmpty()) {
	    Query query = null;
	    String sqlQuery = "";
	    
	    if(configCCDDataElements != null) {
		if(!configCCDDataElements.isEmpty()) {
		    boolean fieldFound = false;
		    for(String subString : substrings) {
			fieldFound = false;
			for(configurationCCDElements ccdDataElement : configCCDDataElements) {
			    if(subString.toLowerCase().equals(ccdDataElement.getElement().trim().toLowerCase())) {
				fieldFound = true;
			    }
			}
			
			if(!fieldFound) {
			    sqlQuery = "insert into configurationccdelements (configId, element, fieldValue, defaultValue) values "
				+ "("+configId+", '"+subString+"', '','')";
			    
			    configurationTransportDAO.executeConfigTransportSQL(sqlQuery);
			    
			}
		    }
		    
		    //Delete any existing element that was not found in the new uploaded file
		    Integer fieldId = 0;
		    for(configurationCCDElements ccdDataElement : configCCDDataElements) {
			fieldFound = false;
			fieldId = ccdDataElement.getId();
			for(String subString : substrings) {
			    if(subString.toLowerCase().equals(ccdDataElement.getElement().trim().toLowerCase())) {
				fieldFound = true;
			    }
			}
			
			if(!fieldFound && fieldId > 0) {
			    configurationTransportDAO.executeConfigTransportSQL("delete from configurationccdelements where id = " + fieldId);
			}
		    }
		}
		else {
		    for(String subString : substrings) {
			sqlQuery = "insert into configurationccdelements (configId, element, fieldValue, defaultValue) values "
			    + "("+configId+", '"+subString+"', '','')";
			configurationTransportDAO.executeConfigTransportSQL(sqlQuery);
		    }
		}
	    }
	    else {
		for(String subString : substrings) {
		    sqlQuery = "insert into configurationccdelements (configId, element, fieldValue, defaultValue) values "
			+ "("+configId+", '"+subString+"', '','')";
		    configurationTransportDAO.executeConfigTransportSQL(sqlQuery);
		}
	    }
	}
    }
    
    @Override
    public List<configurationconnectionfieldmappings> getConnectionFieldMappings(Integer targetConfigId, Integer sourceConfigId) throws Exception {
	return configurationTransportDAO.getConnectionFieldMappings(targetConfigId,sourceConfigId);
    }
    
    @Override
    public void deleteConnectionMappedFields(Integer connectionId) throws Exception {
	configurationTransportDAO.deleteConnectionMappedFields(connectionId);
    }
    
    @Override
    public void saveConnectionFieldMapping(configurationconnectionfieldmappings fieldMapping) throws Exception {
	configurationTransportDAO.saveConnectionFieldMapping(fieldMapping);
    }
    
    @Override
    public List<configurationFTPFields> getFTPSourceConfigurations() throws Exception {
	return configurationTransportDAO.getFTPSourceConfigurations();
    }

	@Override
	public configurationFormFields getConfigurationFieldById(int fieldId) throws Exception {
		 return configurationTransportDAO.getConfigurationFieldById(fieldId);
	}
      
}
