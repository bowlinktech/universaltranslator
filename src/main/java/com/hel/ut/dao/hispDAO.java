package com.hel.ut.dao;

import com.hel.ut.model.hisps;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface hispDAO {
    
    List<hisps> getAllActiveHisps() throws Exception;

    hisps getHispById(Integer hispId) throws Exception;
}
