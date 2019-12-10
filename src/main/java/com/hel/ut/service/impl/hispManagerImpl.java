package com.hel.ut.service.impl;

import com.hel.ut.dao.hispDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hel.ut.model.hisps;
import com.hel.ut.service.hispManager;
import java.util.List;

@Service
public class hispManagerImpl implements hispManager {

    @Autowired
    private hispDAO hispDAO;
    
    @Override
    public List<hisps> getAllActiveHisps() throws Exception {
	return hispDAO.getAllActiveHisps();
    }

    @Override
    public hisps getHispById(Integer hispId) throws Exception {
        return hispDAO.getHispById(hispId);
    }
    
    @Override
    public void saveHisp(hisps hispDetails) throws Exception {
	hispDAO.saveHisp(hispDetails);
    }
}
