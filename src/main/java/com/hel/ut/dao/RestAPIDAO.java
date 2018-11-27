package com.hel.ut.dao;

import com.hel.ut.model.RestAPIMessagesIn;
import com.hel.ut.model.RestAPIMessagesOut;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;


@Repository
public interface RestAPIDAO {

    List<RestAPIMessagesIn> getRestAPIMessagesInList(Date fromDate, Date toDate, Integer fetchSize, String batchName) throws Exception;

    RestAPIMessagesIn getRestAPIMessagesIn(Integer messageId) throws Exception;
    
    RestAPIMessagesIn getRestAPIMessagesInByBatchId(Integer batchId) throws Exception;
    
    List<RestAPIMessagesOut> getRestAPIMessagesOutList(Date fromDate, Date toDate, Integer fetchSize, String batchName) throws Exception;

    RestAPIMessagesOut getRestAPIMessagesOut(Integer messageId) throws Exception;

}
