package com.hel.ut.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hel.ut.dao.organizationDAO;
import com.hel.ut.model.Organization;
import com.hel.ut.service.organizationManager;
import com.hel.ut.model.utUser;
import com.hel.ut.reference.fileSystem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ListIterator;
import org.springframework.web.multipart.MultipartFile;

@Service
public class organizationManagerImpl implements organizationManager {

    @Autowired
    private organizationDAO organizationDAO;

    @Override
    public Integer createOrganization(Organization organization) {
        Integer lastId = null;
        lastId = (Integer) organizationDAO.createOrganization(organization);

        //Need to create the directory structure for the new organization
        //Use the cleanURL (name without spaces) for the directory name
        //First get the operating system
        fileSystem dir = new fileSystem();

        dir.creatOrgDirectories(organization.getcleanURL());

        return lastId;
    }

    @Override
    public void updateOrganization(Organization organization) throws Exception {

        //Need to make sure all folders are created for
        //the organization
        fileSystem dir = new fileSystem();

        dir.creatOrgDirectories(organization.getcleanURL());
        
        MultipartFile file = organization.getFile();
        //If a file is uploaded
        if (file != null && !file.isEmpty()) {

            String fileName = file.getOriginalFilename();

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = file.getInputStream();
                File newFile = null;

                //Set the directory to save the uploaded message type template to
                fileSystem orgdir = new fileSystem();

                orgdir.setDir(organization.getcleanURL(), "templates");

                newFile = new File(orgdir.getDir() + fileName);

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

                //Set the filename to the file name
                organization.setparsingTemplate(fileName);

            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception(e);
            }

        }

        organizationDAO.updateOrganization(organization);
    }

    @Override
    public void deleteOrganization(int orgId) {
        organizationDAO.deleteOrganization(orgId);
    }

    @Override
    public Organization getOrganizationById(int orgId) {
        return organizationDAO.getOrganizationById(orgId);
    }

    @Override
    public List<Organization> getOrganizationByName(String cleanURL) {
        return organizationDAO.getOrganizationByName(cleanURL);
    }

    @Override
    public List<Organization> getOrganizations() {
        return organizationDAO.getOrganizations();
    }

    @Override
    public List<Organization> getLatestOrganizations(int maxResults) {
        return organizationDAO.getLatestOrganizations(maxResults);
    }

    @Override
    public List<Organization> getAllActiveOrganizations() {
        return organizationDAO.getAllActiveOrganizations();
    }

    @Override
    public Long findTotalOrgs() {
        return organizationDAO.findTotalOrgs();
    }

    @Override
    public Long findTotalUsers(int orgId) {
        return organizationDAO.findTotalUsers(orgId);
    }

    @Override
    public Long findTotalConfigurations(int orgId) {
        return organizationDAO.findTotalConfigurations(orgId);
    }

    @Override
    public List<utUser> getOrganizationUsers(int orgId) {
        return organizationDAO.getOrganizationUsers(orgId);
    }

    @Override
    public List<Organization> getAssociatedOrgs(int orgId) {
        return organizationDAO.getAssociatedOrgs(orgId);
    }

}