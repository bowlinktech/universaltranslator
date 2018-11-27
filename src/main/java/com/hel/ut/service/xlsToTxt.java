/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service;

import com.hel.ut.model.Organization;
import com.hel.ut.model.batchUploads;
import com.hel.ut.reference.fileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gchan
 */
@Service
public class xlsToTxt {

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private configurationTransportManager configurationTransportManager;

    @Autowired
    private transactionInManager transactioninmanager;

    public String TranslateXLStoTxt(String fileLocation, String excelFileName, batchUploads batch) throws Exception {

    	Organization orgDetails = organizationmanager.getOrganizationById(batch.getOrgId());

    	fileSystem dir = new fileSystem();

    	dir.setDir(orgDetails.getcleanURL(), "loadFiles");
    	/* Get the uploaded xls File */
    	fileLocation = fileLocation.replace("/Applications/ILTZ/", "").replace("/home/ILTZ/", "").replace("/ILTZ/", "");
    	dir.setDirByName(fileLocation);
    	
    	String excelFile = (excelFileName + ".xls");
    	
    	/* Create the txt file that will hold the excel fields */
    	String newfileName = (excelFileName + ".txt");

    	File newFile = new File(dir.getDir() + newfileName);
    	File inputFile = new File(dir.getDir() + excelFile);

    	if (newFile.exists()) {
    	    try {

    		if (newFile.exists()) {
    		    int i = 1;
    		    while (newFile.exists()) {
    			int iDot = newfileName.lastIndexOf(".");
    			newFile = new File(dir.getDir() + newfileName.substring(0, iDot) + "(" + ++i + ")" + newfileName.substring(iDot));
    		    }
    		    newfileName = newFile.getName();
    		    newFile.createNewFile();
    		} else {
    		    newFile.createNewFile();
    		}
    	    } catch (Exception e) {
    		e.printStackTrace();
    	    }

    	} else {
    	    newFile.createNewFile();
    	    newfileName = newFile.getName();

    	}

    	try {
    		String text = "";
        	FileWriter fw = new FileWriter(newFile);
	        
        		InputStream inp = new FileInputStream(inputFile);
        	    HSSFWorkbook wb = new HSSFWorkbook(inp);
        	    ExcelExtractor extractor = new ExcelExtractor(wb);
        	    extractor.setIncludeBlankCells(true);
        	    extractor.setFormulasNotResults(true);
        	    extractor.setIncludeSheetNames(false);
        	    text = extractor.getText();
        	    fw.write(text.replaceAll("(?m)^[ \t]*\r?\n", ""));
		        extractor.close();
		        wb.close();
		        inp.close();
		        fw.close();
		        
        } catch (Exception ex) {
        	ex.printStackTrace();
        	newfileName = "ERRORERRORERROR";
        	PrintStream ps = new PrintStream(newFile);
        	ex.printStackTrace(ps);
        	ps.close();
        	transactioninmanager.insertProcessingError(5, null, batch.getId(), null, null, null, null,
                    false, false, ex.getStackTrace().toString());
        }
        return newfileName;

    }

}
