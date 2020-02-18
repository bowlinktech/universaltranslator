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

import javax.servlet.http.HttpSession;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String database = "universaltranslatorca";
	
	if (attr != null) {
	    if(attr.getRequest().getParameter("tenantId") != null) {
		database = attr.getRequest().getParameter("tenantId");
	    }
	    else if(attr.getRequest().getSession() != null) {
		HttpSession session = attr.getRequest().getSession();
		if(session.getAttribute("tenantId") != null) {
		    if(!"".equals(session.getAttribute("tenantId"))) {
			database = session.getAttribute("tenantId").toString();
		    }
		}
	    }
	}
	
        return database;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
