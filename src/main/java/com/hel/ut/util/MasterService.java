/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.util;

/**
 *
 * @author chadmccue
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.util.Properties;

public class MasterService {
    
    private Properties configProp = new Properties();
    private InputStream input = null;
    
    public void loadProperties() throws IOException {
	File propDir = new File(System.getProperty("catalina.home"), "properties");
	File propFile = new File(propDir, "HELRegistryManagement.properties");
	input = new FileInputStream(propFile);
	configProp.load(input);
    }
   
    public static DataSource getDefaultDataSource() throws IOException {
	
	MasterService masterservice = new MasterService();
	masterservice.loadProperties();
	String defaultDBName = masterservice.configProp.getProperty("jdbc.defaultUTCADBName");
	
	DriverManagerDataSource defaultdataSource = new DriverManagerDataSource();
        defaultdataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        defaultdataSource.setUrl("jdbc:mysql://"+masterservice.configProp.getProperty("jdbc.url")+"/"+defaultDBName+"?allowMultiQueries=true&serverTimezone=EST");
        defaultdataSource.setUsername(masterservice.configProp.getProperty("jdbc.user"));
        defaultdataSource.setPassword(masterservice.configProp.getProperty("jdbc.password"));
	
	return defaultdataSource;
    }
    
    public static DataSource getDataSource(String tenantIdentifier) throws IOException {
	
	MasterService masterservice = new MasterService();
	masterservice.loadProperties();
	
	DriverManagerDataSource defaultdataSource = new DriverManagerDataSource();
        defaultdataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        defaultdataSource.setUrl("jdbc:mysql://"+masterservice.configProp.getProperty("jdbc.url")+"/"+tenantIdentifier+"?allowMultiQueries=true&serverTimezone=EST");
        defaultdataSource.setUsername(masterservice.configProp.getProperty("jdbc.user"));
        defaultdataSource.setPassword(masterservice.configProp.getProperty("jdbc.password"));
	
	return defaultdataSource;
    }
    
}
