package com.hel.ut.webServices;

import java.util.Date;
import java.util.List;

import javax.xml.soap.SOAPMessage;

import com.hel.ut.model.WSMessagesIn;
import com.hel.ut.model.wsMessagesOut;
import com.hel.ut.model.custom.ToWSSOAP;

public interface WSManager {

    wsMessagesOut sendHIESoapMessage(wsMessagesOut wsMessagesOut, String fileContent);

    String getXMLValue(String xmlTag, String xml) throws Exception;

    SOAPMessage createSOAPRequest(String fileContent, String email, ToWSSOAP toWSOAP) throws Exception;

    List<WSMessagesIn> getWSMessagesInList(Date fromDate, Date toDate, Integer fetchSize) throws Exception;

    WSMessagesIn getWSMessagesIn(Integer wsId) throws Exception;

    void saveWSMessagesOut(wsMessagesOut wsMessagesOut) throws Exception;

    List<wsMessagesOut> getWSMessagesOutList(Date fromDate, Date toDate, Integer fetchSize) throws Exception;

    wsMessagesOut getWSMessagesOut(Integer wsId) throws Exception;

    List<wsMessagesOut> getWSMessagesOutByBatchId(Integer batchId) throws Exception;

    List<WSMessagesIn> getWSMessagesInByBatchId(Integer batchId) throws Exception;

    void saveWSMessagesIn(WSMessagesIn wsIn) throws Exception;

}
