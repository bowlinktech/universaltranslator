package com.hel.ut.webServices.impl;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hel.ut.dao.WebServicesDAO;
import com.hel.ut.dao.transactionOutDAO;
import com.hel.ut.model.WSMessagesIn;
import com.hel.ut.model.wsMessagesOut;
import com.hel.ut.model.custom.ToWSSOAP;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.transactionOutManager;
import com.hel.ut.webServices.WSManager;

@Service
public class WSManagerImpl implements WSManager {

    @Resource(name = "myProps")
    private Properties myProps;

    @Autowired
    private transactionInManager transactionInManager;

    @Autowired
    private transactionOutDAO transactionOutDAO;

    @Autowired
    private transactionOutManager transactionoutmanager;

    @Autowired
    private WebServicesDAO webservicesDAO;

    @Override
    public wsMessagesOut sendHIESoapMessage(wsMessagesOut wsMessagesOut, String fileContent) {
        try {
            wsMessagesOut.setMessageResult("failed");
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            ToWSSOAP toWSOAP = new ToWSSOAP();

            wsMessagesOut.setEndPoint(myProps.getProperty("ws.endpoint"));
            toWSOAP.setEndPoint(myProps.getProperty("ws.endpoint"));
            toWSOAP.setFromEmail(myProps.getProperty("ws.fromEmail"));
            toWSOAP.setMimeType(wsMessagesOut.getMimeType());

            if (!wsMessagesOut.getFromEmail().equalsIgnoreCase("")) {
                toWSOAP.setFromEmail(wsMessagesOut.getFromEmail());
            } else {
                wsMessagesOut.setFromEmail(myProps.getProperty("ws.fromEmail"));
            }

            SOAPMessage soapMessage = createSOAPRequest(fileContent, wsMessagesOut.getToEmail(), toWSOAP);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            soapMessage.writeTo(stream);
            wsMessagesOut.setSoapMessage(new String(stream.toByteArray(), "utf-8"));

            SOAPMessage soapResponse = soapConnection.call(soapMessage, toWSOAP.getEndPoint());
	    soapResponse.writeTo(System.out);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            soapResponse.writeTo(os);
            String responseXML = new String(os.toByteArray());
            wsMessagesOut.setSoapResponse(responseXML);

            try {
                wsMessagesOut.setMessageResult(getXMLValue("rhap:payload", responseXML));
            } catch (Exception ex) {
            	ex.printStackTrace();
                transactionOutDAO.updateBatchStatus(wsMessagesOut.getBatchDownloadId(), 30);
                wsMessagesOut.setSoapResponse(ex.toString());
                /**
                 * insert error*
                 */
                transactionInManager.insertProcessingError(21, null, wsMessagesOut.getBatchDownloadId(), null, null, null, null, true, true, ex.toString(), null);
               
            }
            soapConnection.close();

        } catch (Exception ex) {
            transactionOutDAO.updateBatchStatus(wsMessagesOut.getBatchDownloadId(), 30);
            /**
             * insert error*
             */
            transactionInManager.insertProcessingError(21, null, wsMessagesOut.getBatchDownloadId(), null, null, null, null, true, true, ex.toString(), null);
           
            ex.printStackTrace();
            wsMessagesOut.setSoapResponse(ex.toString());

        }
        try {
            saveWSMessagesOut(wsMessagesOut);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return wsMessagesOut;

    }

    @Override
    public String getXMLValue(String xmlTag, String xml) {
        String xmlValue = "";
        //first we find where the tag is

        /**
         * *
         */
        xml = xml.toLowerCase();
        xmlTag = xmlTag.toLowerCase();

        String startTag = "<" + xmlTag + ">";
        String endTag = "</" + xmlTag + ">";

        int startLoc = xml.indexOf(startTag) + startTag.length();
        int endLoc = xml.indexOf(endTag);
        try {
            xmlValue = xml.substring(startLoc, endLoc);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Utilities:getXMLValue - xmlTag is " + xmlTag + " error is:" + e);
        }
        return xmlValue;

    }

    @Override
    public SOAPMessage createSOAPRequest(String fileContent, String toEmail, ToWSSOAP toWSOAP) {
        try {

            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            String rhapURI = "http://www.orionhealth.com/rhapsodyconnect";
            String iheURI = "http://www.orionhealth.com/2011/08/ihe";

            // SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("ihe", iheURI);
            envelope.addNamespaceDeclaration("rhap", rhapURI);

            // SOAP Body
            SOAPBody soapBody = envelope.getBody();
            SOAPElement soapBodyElem = soapBody.addChildElement("SendDocuments", "rhap");
            SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("From", "rhap");
            SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("To", "rhap");
            soapBodyElem1.addTextNode(toWSOAP.getFromEmail());
            soapBodyElem2.addTextNode(toEmail);

            SOAPElement soapBodyElemIHE = soapBodyElem.addChildElement("ProvideAndRegisterDocumentSet", "ihe");
            SOAPElement soapBodyElemIHEDocument = soapBodyElemIHE.addChildElement("Document", "ihe");
            soapBodyElemIHEDocument.setAttribute("mimeType", toWSOAP.getMimeType());
            soapBodyElemIHEDocument.addChildElement("ClassCode", "ihe");
            soapBodyElemIHEDocument.addChildElement("ConfidentialityCode", "ihe");
            soapBodyElemIHEDocument.addChildElement("FormatCode", "ihe");
            soapBodyElemIHEDocument.addChildElement("HealthcareFacilityTypeCode", "ihe");
            soapBodyElemIHEDocument.addChildElement("PracticeSettingCode", "ihe");
            soapBodyElemIHEDocument.addChildElement("TypeCode", "ihe");
            SOAPElement soapBodyElemIHEChild8 = soapBodyElemIHEDocument.addChildElement("DocumentContents", "ihe");
            soapBodyElemIHEChild8.addTextNode(fileContent);

            soapMessage.saveChanges();

            /* Print the request message */
            soapMessage.writeTo(System.out);

            return soapMessage;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    @Override
    public List<WSMessagesIn> getWSMessagesInList(Date fromDate, Date toDate,
            Integer fetchSize) throws Exception {
        return webservicesDAO.getWSMessagesInList(fromDate, toDate, fetchSize);
    }

    @Override
    public WSMessagesIn getWSMessagesIn(Integer wsId) throws Exception {
        return webservicesDAO.getWSMessagesIn(wsId);
    }

    @Override
    public void saveWSMessagesOut(wsMessagesOut wsMessagesOut) throws Exception {
        webservicesDAO.saveWSMessagesOut(wsMessagesOut);
    }

    @Override
    public List<wsMessagesOut> getWSMessagesOutList(Date fromDate, Date toDate,
            Integer fetchSize) throws Exception {
        return webservicesDAO.getWSMessagesOutList(fromDate, toDate, fetchSize);
    }

    @Override
    public wsMessagesOut getWSMessagesOut(Integer wsId) throws Exception {
        return webservicesDAO.getWSMessagesOut(wsId);
    }

    @Override
    public List<wsMessagesOut> getWSMessagesOutByBatchId(Integer batchId)
            throws Exception {
        return webservicesDAO.getWSMessagesOutByBatchId(batchId);
    }

    @Override
    public List<WSMessagesIn> getWSMessagesInByBatchId(Integer batchId)
            throws Exception {
        return webservicesDAO.getWSMessagesInByBatchId(batchId);
    }

    @Override
    public void saveWSMessagesIn(WSMessagesIn wsIn) throws Exception {
        webservicesDAO.saveWSMessagesIn(wsIn);
    }

}
