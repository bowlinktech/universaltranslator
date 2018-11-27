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
import java.io.FileWriter;
import java.io.PrintStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monitorjbl.xlsx.StreamingReader;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * @author gchan
 */
@Service
public class excelToTxt {

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private configurationTransportManager configurationTransportManager;

    @Autowired
    private transactionInManager transactioninmanager;

    @SuppressWarnings("deprecation")
	public String TranslateXLSXtoTxt(String fileLocation, String excelFileName, batchUploads batch) throws Exception {

	Organization orgDetails = organizationmanager.getOrganizationById(batch.getOrgId());

	fileSystem dir = new fileSystem();

	dir.setDir(orgDetails.getcleanURL(), "loadFiles");
	/* Get the uploaded xlsx File */
	fileLocation = fileLocation.replace("/Applications/ILTZ/", "").replace("/home/ILTZ/", "").replace("/ILTZ/", "");
	dir.setDirByName(fileLocation);
	
	String excelFile = (excelFileName + ".xlsx");
	
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

		FileWriter fw = new FileWriter(newFile);
    	InputStream is = new FileInputStream(inputFile);
    	Workbook workbook = StreamingReader.builder()
    	        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
    	        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
    	        .open(is);            // InputStream or File for XLSX file (required)
    
    	Sheet datatypeSheet = workbook.getSheetAt(0);
        
        DataFormatter formatter = new DataFormatter();
        int  writeRow = 0;
        
        for(Row row : datatypeSheet) {
        	String string = "";
        	for(int cn=0; cn<row.getLastCellNum(); cn++) {
     		   // If the cell is missing from the file, generate a blank one
     	       // (Works by specifying a MissingCellPolicy)
     	       Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
     	       String text = formatter.formatCellValue(cell);
     	       string = string + text.trim() + batch.getDelimChar();
     	   }
        	// check to see if row is blank
        	String stringRemoveEmptyRows = string.replaceAll("(?m)^[ \t]*\r?\n", "");
        	if (stringRemoveEmptyRows.trim().length() > 0) {
        		writeRow ++;
        		if (writeRow == 1) {
        			 fw.write(stringRemoveEmptyRows);
        		} else {
        			fw.write(System.getProperty("line.separator") + stringRemoveEmptyRows);
        		}
       		}
        }
        
    	
 	    workbook.close();
 	    is.close();
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
