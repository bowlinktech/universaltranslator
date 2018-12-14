package com.hel.ut.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hel.ut.model.TransportMethod;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.configurationRhapsodyFields;
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

@Service
public class utConfigurationTransportManagerImpl implements utConfigurationTransportManager {

    @Autowired
    private utConfigurationTransportDAO configurationTransportDAO;

    @Autowired
    private organizationManager organizationManager;

    @Autowired
    private utConfigurationManager utConfigurationManager;

    @Override
    public configurationTransport getTransportDetails(int configId) throws Exception {
        return configurationTransportDAO.getTransportDetails(configId);
    }

    @Override
    public configurationTransport getTransportDetailsByTransportMethod(int configId, int transportMethod) {
        return configurationTransportDAO.getTransportDetailsByTransportMethod(configId, transportMethod);
    }

    @Override
    public Integer updateTransportDetails(configurationTransport transportDetails) throws Exception {

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

                //Set the directory to save the uploaded message type template to
                fileSystem orgdir = new fileSystem();

                orgdir.setDir(orgDetails.getcleanURL(), "templates");

                newCCDTemplateFile = new File(orgdir.getDir() + CCDTemplatefileName);

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
    public List getTransportMethodsByType(Integer configurationType) {
        return configurationTransportDAO.getTransportMethodsByType(configurationType);
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

                //Set the directory to save the brochures to
                fileSystem dir = new fileSystem();
                dir.setDir(orgDetails.getcleanURL(), "certificates");

                newFile = new File(dir.getDir() + fileName);

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
    public List<configurationTransport> getConfigTransportForFileExtAndPath(String fileExt, Integer transportMethodId, Integer status, String inputPath) {
        return configurationTransportDAO.getConfigTransportForFileExtAndPath(fileExt, transportMethodId, status, inputPath);
    }

    @Override
    public List<configurationTransport> getTransportListForFileExtAndPath(String fileExt, Integer transportMethodId, Integer status, String inputPath) {
        return configurationTransportDAO.getTransportListForFileExtAndPath(fileExt, transportMethodId, status, inputPath);
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
    public void saveTransportRhapsody(configurationRhapsodyFields rhapsodyFields) throws Exception {
        configurationTransportDAO.saveTransportRhapsody(rhapsodyFields);
	//Makes sure the directory is created
        fileSystem dir = new fileSystem();
	dir.creatFTPDirectory(rhapsodyFields.getDirectory());
	
    }

    @Override
    public List<configurationRhapsodyFields> getTransRhapsodyDetails(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransRhapsodyDetails(transportDetailId);
    }

    @Override
    public configurationRhapsodyFields getTransRhapsodyDetailsPush(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransRhapsodyDetailsPush(transportDetailId);
    }

    @Override
    public configurationRhapsodyFields getTransRhapsodyDetailsPull(int transportDetailId) throws Exception {
        return configurationTransportDAO.getTransRhapsodyDetailsPull(transportDetailId);
    }

    @Override
    public List<configurationTransport> getTransportEncoding(String fileExt, Integer transportMethodId) {
        return configurationTransportDAO.getTransportEncoding(fileExt, transportMethodId);
    }

    @Override
    public Integer getOrgIdForRhapsodyPath(
            configurationRhapsodyFields rhapsodyInfo) throws Exception {
        return configurationTransportDAO.getOrgIdForRhapsodyPath(rhapsodyInfo);
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
}
