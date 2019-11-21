package com.hel.ut.controller;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hel.ut.model.Organization;
import com.hel.ut.model.utUser;
import com.hel.ut.model.utUserActivity;
import com.hel.ut.service.fileManager;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.userManager;

import java.io.File;
import java.util.Properties;
import javax.annotation.Resource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;

@Controller
@RequestMapping("/FileDownload")
public class fileDownloadController {

    @Autowired
    private organizationManager organizationManager;

    @Autowired
    private userManager usermanager;

    @Autowired
    private fileManager filemanager;

    @Resource(name = "myProps")
    private Properties myProps;

    /**
     * Size of a byte buffer to read/write file This decrypts the file and for users to download
     */
    private static final int BUFFER_SIZE = 4096;

    @RequestMapping(value = "/downloadFile.do", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest request, Authentication authentication,
	    @RequestParam String filename,
	    @RequestParam String foldername,
	    @RequestParam(value = "orgId", required = false) Integer orgId,
	    @RequestParam(value = "utBatchName", required = false) String utBatchName,
	    HttpServletResponse response) throws Exception {
	
	String desc = "";
	try {

	    utUser userDetails = usermanager.getUserByUserName(authentication.getName());

	    /**
	     * tracking *
	     */
	    utUserActivity ua = new utUserActivity();
	    ua.setUserId(userDetails.getId());
	    ua.setAccessMethod(request.getMethod());
	    ua.setPageAccess("/downloadFile.do"); // include mapping in case we want to send them back to page in the future
	    ua.setActivity("Downloaded File");
	    desc = foldername + filename;
	    if (orgId != null) {
		desc = orgId.toString() + "_" + foldername + filename;
	    }
	    ua.setActivityDesc(desc);
	    usermanager.insertUserLog(ua);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("Error tracking file downloaded " + desc);

	}
	OutputStream outputStream = null;
	InputStream in = null;
	ServletContext context = request.getServletContext();
	String errorMessage = "";

	try {
	    String directory;

	    if (orgId != null && orgId > 0) {
		Organization organization = organizationManager.getOrganizationById(orgId);
		String cleanURL = organization.getcleanURL();
		
		if("archivesOut".equals(foldername)) {
		    directory = myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/output files/";
		}
		else {
		    directory = myProps.getProperty("ut.directory.utRootDir") + foldername + "/";
		}
	    } else {
		directory = myProps.getProperty("ut.directory.utRootDir") + foldername + "/";
	    }

	    String mimeType = "";
	    String actualFileName = "";

	    File f = new File(directory + filename);
	    
	    if (utBatchName != null) {

		if (!f.exists() && !"".equals(utBatchName)) {
		    f = new File(directory + utBatchName);

		    if (f.exists()) {
			mimeType = context.getMimeType(directory + utBatchName);
			//we don't know when a file is encoding or decoding without having to do queries, it will be easy to try to decode first
			in = new FileInputStream(directory + utBatchName);
			try {
			    byte[] fileAsBytes = filemanager.loadFileAsBytesArray(directory + utBatchName);
			    if (fileAsBytes != null) {
				byte[] decodedBytes = Base64.decodeBase64(fileAsBytes);
				in = new ByteArrayInputStream(decodedBytes);
			    }
			} catch (Exception ex) {
			    // no need to do anything
			}
			actualFileName = utBatchName;
		    } else if (!f.exists() && "txt".equals(FilenameUtils.getExtension(utBatchName))) {
			utBatchName = utBatchName.replace("txt", FilenameUtils.getExtension(filename));

			f = new File(directory + utBatchName);
			if (f.exists()) {
			    mimeType = context.getMimeType(directory + utBatchName);
			    in = new FileInputStream(directory + utBatchName);
			    try {
				byte[] fileAsBytes = filemanager.loadFileAsBytesArray(directory + utBatchName);
				if (fileAsBytes != null) {
				    byte[] decodedBytes = Base64.decodeBase64(fileAsBytes);
				    in = new ByteArrayInputStream(decodedBytes);
				}
			    } catch (Exception ex) {
				// no need to do anything
			    }
			    actualFileName = utBatchName;
			}
		    }
		} else {
		    mimeType = context.getMimeType(directory + filename);
		    in = new FileInputStream(directory + filename);
		    try {
			byte[] fileAsBytes = filemanager.loadFileAsBytesArray(directory + utBatchName);
			if (fileAsBytes != null) {
			    byte[] decodedBytes = Base64.decodeBase64(fileAsBytes);
			    in = new ByteArrayInputStream(decodedBytes);
			}
		    } catch (Exception ex) {
			// no need to do anything
		    }
		    actualFileName = filename;
		}
	    } else {
		mimeType = context.getMimeType(directory + filename);
		in = new FileInputStream(directory + filename);
		try {
		    byte[] fileAsBytes = filemanager.loadFileAsBytesArray(directory + utBatchName);
		    if (fileAsBytes != null) {
			byte[] decodedBytes = Base64.decodeBase64(fileAsBytes);
			in = new ByteArrayInputStream(decodedBytes);
		    }
		} catch (Exception ex) {
		    // no need to do anything
		}
		actualFileName = filename;
	    }

	    if (mimeType == null) {
		// set to binary type if MIME mapping not found
		mimeType = "application/octet-stream";
	    }
	    response.setContentType(mimeType);

	    byte[] buffer = new byte[BUFFER_SIZE];
	    int bytesRead = 0;

	    response.setContentLength((int) f.length());
	    response.setHeader("Content-Transfer-Encoding", "binary");
	    response.setHeader("Content-Disposition", "attachment;filename=\"" + actualFileName + "\"");

	    outputStream = response.getOutputStream();
	    while (0 < (bytesRead = in.read(buffer))) {
		outputStream.write(buffer, 0, bytesRead);
	    }

	    in.close();
	    outputStream.close();

	} catch (FileNotFoundException e) {
	    errorMessage = errorMessage + "<br/>" + e.getMessage();
	    e.printStackTrace();

	} catch (IOException e) {
	    errorMessage = e.getMessage();
	    e.printStackTrace();
	} finally {
	    if (null != in) {
		try {
		    in.close();
		} catch (IOException e) {
		    errorMessage = errorMessage + "<br/>" + e.getMessage();
		    e.printStackTrace();
		}
	    }
	}

	/**
	 * throw error message here because want to make sure file stream is closed *
	 */
	if (!errorMessage.equalsIgnoreCase("")) {
	    throw new Exception(errorMessage);
	}
    }

}
