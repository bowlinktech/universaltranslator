package com.hel.ut.service;


import com.hel.ut.model.hisps;
import java.util.List;

public interface hispManager {
    
    List<hisps> getAllActiveHisps() throws Exception;

    hisps getHispById(Integer hispId) throws Exception;
}
