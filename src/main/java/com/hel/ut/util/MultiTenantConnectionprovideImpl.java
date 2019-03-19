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

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.PooledDataSource;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;

import javax.sql.DataSource;

public class MultiTenantConnectionprovideImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
    
    @Override
    protected DataSource selectAnyDataSource() {
	
	try {
	    return MasterService.getDefaultDataSource();
	} catch (IOException ex) {
	    Logger.getLogger(MultiTenantConnectionprovideImpl.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
	/*PooledDataSource pds = C3P0Registry.pooledDataSourceByName(tenantIdentifier);
	
	if(pds == null) {
	    try {
		return MasterService.getComboPooledDataSource(tenantIdentifier);
	    }
	    catch (IOException ex) {
		Logger.getLogger(MultiTenantConnectionprovideImpl.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	return pds;*/
	try {
	    return MasterService.getDataSource(tenantIdentifier);
	} catch (IOException ex) {
	    System.out.println("ERROR:" + ex.getMessage());
	    //Logger.getLogger(MultiTenantConnectionprovideImpl.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
	
    }

}
