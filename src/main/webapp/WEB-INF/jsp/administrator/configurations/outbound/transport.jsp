<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script src="<%=request.getContextPath()%>/dspResources/js/admin/configuration/outbound/transport.js"></script>

<form:form id="transportDetails" commandName="transportDetails" modelAttribute="transportDetails" method="post" role="form" enctype="multipart/form-data">

    <input type="hidden" id="action" name="action" value="save" />
    <input type="hidden" id="messageTypeId" value="${messageTypeId}" />
    <input type="hidden" id="helRegistryFolderName" value="${helRegistryFolderName}" />
    <form:hidden path="id" id="id" />
    <form:hidden path="configId" id="configId" />
    <form:hidden path="autoRelease" value="1" />
    <form:hidden path="clearRecords" value="1" />
    <form:hidden path="massTranslation" value="true" />
    <form:hidden path="helRegistryId" id="helRegistryId" />
    <form:hidden path="helSchemaName" id="helSchemaName" />
    <form:hidden path="mergeBatches" />
    <form:hidden path="restAPIType" value = "0" />

    <section class="panel panel-default">
	<div class="panel-heading">
	    <h3 class="panel-title">
		Transport Method (How is the file leaving the UT?) *
	    </h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<spring:bind path="transportMethodId">
		    <div id="transportMethodDiv" class="form-group ${status.error ? 'has-error' : '' }">
			<form:select path="transportMethodId" id="transportMethod" class="form-control"  disabled="${transportDetails.id == 0 ? 'false' : 'true' }">
			    <option value="">- Select -</option>
			    <c:forEach items="${transportMethods}" var="transMethod" varStatus="tStatus">
				<option value="${transportMethods[tStatus.index][0]}" <c:if test="${transportDetails.transportMethodId == transportMethods[tStatus.index][0]}">selected</c:if>>
				${transportMethods[tStatus.index][1]}
			    </option>
			    </c:forEach>
			</form:select>
			<span id="transportMethodMsg" class="control-label"></span>
			<c:if test="${transportDetails.id > 0}">
			    <form:hidden path="transportMethodId" />
			</c:if>
		    </div>
		</spring:bind>
	    </div>
	    <spring:bind path="helRegistryConfigId">
		<div id="helRegistryConfigDiv" class="form-group ${status.error ? 'has-error' : '' }"  style="display:none">
		    <label class="control-label" for="helRegistryConfigId">Health-e-Link Registry Configuration *</label>
		    <form:select path="helRegistryConfigId" id="helRegistryConfigId" rel="${transportDetails.helRegistryConfigId}" class="form-control half" disabled="${transportDetails.id == 0 ? 'false' : 'true' }"></form:select>
		    <c:if test="${transportDetails.id > 0}">
			<form:hidden path="helRegistryConfigId"/>
		    </c:if> 
		    <span id="helRegistryConfigIdMsg" class="control-label"></span>
		</div>
	    </spring:bind>
	     <spring:bind path="ergFileDownload">
		<div class="form-group"  id="ergFileDownloadDiv" style="display:none">
		    <label class="control-label" for="ergFileDownload">Will this file be created to be downloaded from the eRG?</label>
		    <p><small>(This file will not update or create a received message in the eRG, it will only be attached to the message to be downloaded.)</small></p>
		    <div>
			<label class="radio-inline">
			    <form:radiobutton id="ergFileDownload" path="ergFileDownload" value="1" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Yes 
			</label>
			<label class="radio-inline">
			    <form:radiobutton id="ergFileDownload" path="ergFileDownload" value="0" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> No
			</label>
		    </div>
		    <c:if test="${transportDetails.copiedTransportId > 0}">
			<form:hidden path="ergFileDownload" />
		    </c:if>    
		</div>
	    </spring:bind>
	    
	</div>
    </section>

    <section class="panel panel-default">
	<div class="panel-heading">
	    <h3 class="panel-title">Transport File Details</h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<%-- File Details --%>
		<div id="fileDetailsDiv"  style="display:none">
		    <spring:bind path="fileLocation">
			<div class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="fileLocation">Where will the file be stored on the UT prior to processing? *</label>
			    <form:input origVal="${transportDetails.fileLocation}" path="fileLocation" id="fileLocation" class="form-control" type="text" maxLength="255" />
			    <form:errors path="fileLocation" cssClass="control-label" element="label" />
			</div>
		    </spring:bind>
		    <spring:bind path="maxFileSize">
			<div id="maxFileSizeDiv" class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="maxFileSize">Max Accepted File Size (mb) *</label>
			    <form:input path="maxFileSize" id="maxFileSize" class="form-control sm-input" type="text" maxLength="11" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" />
			    <form:errors path="maxFileSize" cssClass="control-label" element="label" />
			    <span id="maxFileSizeMsg" class="control-label"></span>
			    <c:if test="${transportDetails.copiedTransportId > 0}">
				<form:hidden path="maxFileSize" />
			    </c:if>
			</div>
		    </spring:bind>
		    <spring:bind path="zipped">
			<div class="form-group">
			    <label class="control-label" for="status">Is the file Zipped? *</label>
			    <div>
				<label class="radio-inline">
				    <form:radiobutton id="zipped" name="zipped" class="zipped" path="zipped" value="1" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Yes 
				</label>
				<label class="radio-inline">
				    <form:radiobutton id="zipped" name="zipped" class="zipped" path="zipped" value="0" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}"/> No
				</label>
			    </div>
			    <c:if test="${transportDetails.copiedTransportId > 0}">
				<form:hidden path="zipped" />
			    </c:if>
			</div>
		    </spring:bind>
		    <div id="zipTypeTopDiv" style="display:${transportDetails.zipped == true ? 'block' : 'none'}">
			<spring:bind path="zipType">
			    <div id="zipTypeDiv" class="form-group ${status.error ? 'has-error' : '' }">
				<label class="control-label" for="zipType">Zip Type *</label>
				<form:select path="zipType" id="zipType" class="form-control" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}">
				    <option value="">- Select -</option>
				    <c:forEach items="${zipTypes}" varStatus="zStatus">
					<option value="${zipTypes[zStatus.index][0]}" <c:if test="${transportDetails.zipType == zipTypes[zStatus.index][0]}">selected</c:if>>${zipTypes[zStatus.index][1]}</option>
				    </c:forEach>
				</form:select>
				<span id="zipTypeMsg" class="control-label"></span>
				<c:if test="${transportDetails.copiedTransportId > 0}">
				    <form:hidden path="zipType" />
				</c:if>
			    </div>
			</spring:bind>
		    </div>
		    <spring:bind path="fileType">
			<div id="fileTypeDiv" class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="fileType">File Type *</label>
			    <form:select path="fileType" id="fileType" class="form-control" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}">
				<option value="">- Select -</option>
				<c:forEach items="${fileTypes}" varStatus="fStatus">
				    <c:if test="${fileTypes[fStatus.index][0] != 1}">
					<option value="${fileTypes[fStatus.index][0]}" <c:if test="${transportDetails.fileType == fileTypes[fStatus.index][0]}">selected</c:if>>${fileTypes[fStatus.index][1]}</option>
				    </c:if>
				</c:forEach>
			    </form:select>
			    <span id="fileTypeMsg" class="control-label"></span>
			    <c:if test="${transportDetails.copiedTransportId > 0}">
				<form:hidden path="fileType" />
			    </c:if>
			</div>
		    </spring:bind>
		    <spring:bind path="jsonWrapperElement">
			<div id="jsonWrapperElementDiv" class="form-group ${status.error ? 'has-error' : '' }" style="display:none;">
			    <label class="control-label" for="jsonWrapperElement">JSON Main Data Elements Wrapper</label>
			    <form:input path="jsonWrapperElement" id="jsonWrapperElement" class="form-control" type="text" maxLength="55" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" />
			    <form:errors path="jsonWrapperElement" cssClass="control-label" element="label" />
			    <span id="jsonWrapperElementMsg" class="control-label"></span>
			    <c:if test="${transportDetails.copiedTransportId > 0}">
				<form:hidden path="jsonWrapperElement" />
			    </c:if>
			</div>
		    </spring:bind>
		    <spring:bind path="fileExt">
			<div id="fileExtDiv" class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="fileExt">File Extension *</label>
			    <form:input path="fileExt" id="fileExt" class="form-control sm-input" type="text" maxLength="4" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" />
			    <form:errors path="fileExt" cssClass="control-label" element="label" />
			    <span id="fileExtMsg" class="control-label"></span>
			    <c:if test="${transportDetails.copiedTransportId > 0}">
				<form:hidden path="fileExt" />
			    </c:if>
			</div>
		    </spring:bind>
		    <spring:bind path="fileDelimiter">
			<div id="fileDelimiterDiv" class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="fileDelimiter">File Delimiter *</label>
			    <form:select path="fileDelimiter" id="fileDelimiter" class="form-control" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}">
				<option value="">- Select -</option>
				<c:forEach items="${delimiters}" varStatus="dStatus">
				    <option value="${delimiters[dStatus.index][0]}" <c:if test="${transportDetails.fileDelimiter == delimiters[dStatus.index][0]}">selected</c:if>>${delimiters[dStatus.index][1]}</option>
				</c:forEach>
			    </form:select>
			    <span id="fileDelimiterMsg" class="control-label"></span>
			    <c:if test="${transportDetails.copiedTransportId > 0}">
				<form:hidden path="fileDelimiter" />
			    </c:if>
			</div>
		    </spring:bind>
		    <spring:bind path="lineTerminator">
			<div id="lineTerminatortDiv" class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="lineTerminator">Line Terminator</label>
			    <form:input path="lineTerminator" id="lineTerminator" class="form-control sm-input" type="text" maxLength="10" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" />
			    <form:errors path="lineTerminator" cssClass="control-label" element="label" />
			    <span id="fileExtMsg" class="control-label"></span>
			    <c:if test="${transportDetails.copiedTransportId > 0}">
				<form:hidden path="lineTerminator" />
			    </c:if>
			</div>
		    </spring:bind>
		    <spring:bind path="encodingId">
			<div id="encodingDiv" class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="encodingId">What type of Encoding does the file have? *</label>
			    <form:select path="encodingId" id="encodingId" class="form-control" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}">
				<option value="">- Select -</option>
				<c:forEach items="${encodings}" varStatus="fStatus">
				    <option value="${encodings[fStatus.index][0]}" <c:if test="${transportDetails.encodingId == encodings[fStatus.index][0]}">selected</c:if>>${encodings[fStatus.index][1]}</option>                                                
				</c:forEach>
			    </form:select>
			    <span id="encodingMsg" class="control-label"></span>
			    <c:if test="${transportDetails.copiedTransportId > 0}">
				<form:hidden path="encodingId" />
			    </c:if>
			</div>
		    </spring:bind>
		    <spring:bind path="targetFileName">
			<div class="targetFileNameDiv form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="targetFileName">File Name * <span id="useSourceFileName"><input id="useSource" type="checkbox"> Use Source File Name</label></span>
				    <form:input path="targetFileName" id="targetFileName" class="form-control" type="text" maxLength="255" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" />
				    <form:errors path="targetFileName" cssClass="control-label" element="label" />
				    <c:if test="${transportDetails.copiedTransportId > 0}">
					<form:hidden path="targetFileName" />
				    </c:if>
				<span id="targetFileNameMsg" class="control-label"></span>
			</div>
		    </spring:bind>
		    <spring:bind path="appendDateTime">
			<div class="form-group"  id="appendDateTimeDiv">
			    <label class="control-label" for="appendDateTime">Append Date and Time to file Name? *</label>
			    <div>
				<label class="radio-inline">
				    <form:radiobutton id="appendDateTime" path="appendDateTime" value="1" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Yes 
				</label>
				<label class="radio-inline">
				    <form:radiobutton id="appendDateTime" path="appendDateTime" value="0" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> No
				</label>
			    </div>
			    <c:if test="${transportDetails.copiedTransportId > 0}">
				<form:hidden path="appendDateTime" />
			    </c:if>    
			</div>
		    </spring:bind>
		</div>
	    </div>
	</div>
    </section>
			
    <section id="hl7PDFSampleDiv" class="panel panel-default" style="display:${transportDetails.fileType == 4 ? 'block' : 'none'}">
	<div class="panel-heading">
	    <h3 class="panel-title">HL7 Details</h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<c:if test="${id > 0}">
		    <c:if test="${not empty transportDetails.HL7PDFSampleTemplate}">
			<div class="form-group">
			    <label class="control-label" for="HL7PDFSampleTemplate">Current HL7 PDF Template File</label>
			    <input type="text" disabled id="HL7PDFSampleTemplate" class="form-control" value="${transportDetails.HL7PDFSampleTemplate}" />
			    <form:hidden id="HL7PDFSampleTemplate" path="HL7PDFSampleTemplate" />
			</div>
		    </c:if>
		</c:if>     
		<spring:bind path="hl7PDFTemplatefile">
		    <div id="HL7PDFTemplateDiv" class="form-group ${status.error ? 'has-error' : '' }">
			<label class="control-label" for="hl7PDFTemplatefile">Sample HL7 PDF Template (txt file)</label>
			<form:input path="hl7PDFTemplatefile" id="hl7PDFTemplatefile" class="form-control" type="file" />
			<form:errors path="hl7PDFTemplatefile" cssClass="control-label" element="label" />
			<span id="HL7PDFTemplateMsg" class="control-label"></span>
		    </div>
		</spring:bind> 
	    </div>
	</div>
    </section>

    <section id="ccdDetailsDiv" class="panel panel-default" style="display:${(transportDetails.fileType == 9 || transportDetails.fileType == 12) ? 'block' : 'none'}">
	<div class="panel-heading">
	    <h3 class="panel-title">CCD Details</h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<c:if test="${id > 0}">
		    <c:if test="${not empty transportDetails.ccdSampleTemplate}">
			<div class="form-group">
			    <label class="control-label" for="ccdSampleTemplate">Current Output Template File</label>
			    <input type="text" disabled id="ccdSampleTemplate" class="form-control" value="${transportDetails.ccdSampleTemplate}" />
			    <form:hidden id="ccdSampleTemplate" path="ccdSampleTemplate" />
			</div>
		    </c:if>
		</c:if>     
		<spring:bind path="ccdTemplatefile">
		    <div id="ccdTemplateDiv" class="form-group ${status.error ? 'has-error' : '' }">
			<label class="control-label" for="ccdTemplatefile">Output Template (XML file)</label>
			<form:input path="ccdTemplatefile" id="ccdTemplatefile" class="form-control" type="file" />
			<form:errors path="ccdTemplatefile" cssClass="control-label" element="label" />
			<span id="ccdTemplateMsg" class="control-label"></span>
		    </div>
		</spring:bind> 
	    </div>
	</div>
    </section>

    <%-- DIRECT MESSAGE Details --%>	
    <section id="directMessageDetailsDiv" class="panel panel-default" style="display:none">
	<div class="panel-heading">
	    <h3 class="panel-title">Direct Message Details</h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<div class="form-group">
		   <c:set var="directMessageFields" value="${transportDetails.directMessageFields[0]}" />
		   <input name="directMessageFields[0].id" type="hidden" value="${directMessageFields.id}"  />
		   <input name="directMessageFields[0].orgId" type="hidden" value="${directMessageFields.orgId}"  />
		   <input name="directMessageFields[0].dateCreated" type="hidden" value="${directMessageFields.dateCreated}"  />
		   <input name="directMessageFields[0].directDomain" type="hidden" value="${directMessageFields.directDomain}"  />
		   <input name="directMessageFields[0].dmFindConfig" type="hidden" value="${directMessageFields.dmFindConfig}"  />
		   <label class="control-label" for="hispId">Select the HISP *</label>
		   <select name="directMessageFields[0].hispId" id="hispId" class="form-control sm-input">
		       <option value="">- Select -</option>
		       <c:forEach var="hisp" items="${hisps}">
			   <option value="${hisp.id}" <c:if test="${directMessageFields.hispId == hisp.id}">selected</c:if>>${hisp.hispName}</option>                                                
		       </c:forEach>
		   </select>
		   <span id="encodingMsg" class="control-label"></span>
	       </div>
	    </div>
	</div>
    </section>

    <%-- FILE DROP Details --%>	
    <section id="fileDropDetailsDiv" class="panel panel-default" style="display:none">
	<div class="panel-heading">
	    <h3 class="panel-title">File Drop Details</h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<div class="row">
		    <div class="form-group col-md-6">
		    <c:forEach items="${transportDetails.fileDropFields}" var="fileDropFields" varStatus="field">
			<input name="fileDropFields[${field.index}].method" class="form-control" type="hidden" value="${fileDropFields.method}"  />
			<input name="fileDropFields[${field.index}].id" id="id${fileDropFields.method}" class="form-control" type="hidden" value="${fileDropFields.id}"  />
			<c:if test="${fileDropFields.method == 1}">
			  <input name="fileDropFields[${field.index}].directory" id="directory${fileDropFields.method}" class="form-control" type="hidden" maxLength="255" value="${fileDropFields.directory}"  />
			</c:if>
			<c:if test="${fileDropFields.method == 2}">
			    <label class="control-label" for="directory${fileDropFields.method}">Location on the server where generated target file will be dropped</label>
			    <input name="fileDropFields[${field.index}].directory" id="directory${fileDropFields.method}" class="form-control" type="text" maxLength="255" value="${fileDropFields.directory}"  />
			    <span id="rDirectory${fileDropFields.method}Msg" class="control-label"></span>
			</c:if>  
		    </c:forEach>
		    </div>
		</div>
	    </div>
	</div>
    </section>

    <%-- REST Details --%>	
    <section id="restDetailsDiv" class="panel panel-default" style="display:none">
	<div class="panel-heading">
	    <h3 class="panel-title">REST API Details</h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<spring:bind path="restAPIURL">
		    <div id="apiURLDiv" class="form-group ${status.error ? 'has-error' : '' }">
			<label class="control-label" for="restAPIURL">API URL *</label>
			<form:input path="restAPIURL" id="restAPIURL" class="form-control" type="text" maxLength="255" />
			<form:errors path="restAPIURL" cssClass="control-label" element="label" />
			<span id="apiURLMsg" class="control-label"></span>
		    </div>
		</spring:bind>
		<spring:bind path="restAPIUsername">
		    <div id="apiUsernameDiv" class="form-group ${status.error ? 'has-error' : '' }">
			<label class="control-label" for="restAPIUsername">API Username *</label>
			<form:input path="restAPIUsername" id="restAPIUsername" class="form-control half" type="text" maxLength="45" />
			<form:errors path="restAPIUsername" cssClass="control-label" element="label" />
			<span id="apiUsernameMsg" class="control-label"></span>
		    </div>
		</spring:bind>
		<spring:bind path="restAPIPassword">
		    <div id="apiPasswordDiv" class="form-group ${status.error ? 'has-error' : '' }">
			<label class="control-label" for="restAPIPassword">API Password *</label>
			<form:input path="restAPIPassword" id="restAPIPassword" class="form-control half" type="text" maxLength="45" />
			<form:errors path="restAPIPassword" cssClass="control-label" element="label" />
			<span id="apiPasswordMsg" class="control-label"></span>
		    </div>
		</spring:bind>
		<spring:bind path="restAPIFunctionId">
		    <div id="restAPIFunctionIdDiv" class="form-group ${status.error ? 'has-error' : '' }">
			<label class="control-label" for="restAPIFunctionId">IL Rest API Function to Call *</label>
			<form:select path="restAPIFunctionId" id="restAPIFunctionId" class="form-control half">
			    <option value="">- Select -</option>
			    <c:forEach items="${restAPIFunctions}"  varStatus="tStatus">
				<option value="${restAPIFunctions[tStatus.index][0]}" <c:if test="${transportDetails.restAPIFunctionId == restAPIFunctions[tStatus.index][0]}">selected</c:if>>${restAPIFunctions[tStatus.index][1]}</option>
			    </c:forEach>
			</form:select>
			<span id="restAPIFunctionIdMsg" class="control-label"></span>
		    </div>
		</spring:bind>
		<spring:bind path="waitForResponse">
		    <div class="form-group">
			<label class="control-label" for="waitForResponse">Wait for SAVED ACK from target before finalizing batch status? <span class="badge badge-help" data-placement="top" title="" data-original-title="If set to NO, the inbound and outbound batch will be set to a final success status, if 200 is received with the initial API ACK. If YES, the inbound and outbound batch will be set to SDP until the target sends a message to the IL with an updated status.">?</span> *</label>
			<div>
			    <label class="radio-inline">
				<form:radiobutton id="waitForResponse" path="waitForResponse" value="1" /> Yes, wait for target SAVED ACK
			    </label>
			    <label class="radio-inline">
				<form:radiobutton id="waitForResponse" path="waitForResponse" value="0"  /> No, finalize on initial 200 ACK
			    </label>
			</div>  
		    </div>
		</spring:bind>
	    </div>
	</div>
    </section>

    <%-- FTP Details --%>	
    <section id="ftpDetailsDiv" class="panel panel-default" style="display:none">
	<div class="panel-heading">
	    <h3 class="panel-title">FTP Details</h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<div id="FTPDanger" class="alert alert-danger" style="display:none;">
		    At least one FTP section must be filled out!
		</div> 
		<div class="row">
		    <c:forEach items="${transportDetails.FTPFields}" var="ftpDetails" varStatus="field">
			<div class="form-group col-md-6">
			    <div class="form-group">
				<label for="status">FTP <c:choose><c:when test="${ftpDetails.method == 1}">Get</c:when><c:otherwise>Push</c:otherwise></c:choose> Details</label>
				<input name="FTPFields[${field.index}].method" class="form-control" type="hidden" value="${ftpDetails.method}"  />
				<input name="FTPFields[${field.index}].id" id="id${ftpDetail.method}" class="form-control" type="hidden" value="${ftpDetails.id}"  />
			    </div>
			    <div id="protocol${ftpDetails.method}Div" class="form-group">
				<label class="control-label" for="protocol${ftpDetails.method}">Protocol *</label>
				<select name="FTPFields[${field.index}].protocol" id="protocol${ftpDetails.method}" rel="${ftpDetails.method}" class="form-control ftpProtocol">
				    <option value="">- Select -</option>
				    <option value="FTP" <c:if test="${ftpDetails.protocol == 'FTP'}">selected</c:if>>FTP</option>
				    <option value="FTPS" <c:if test="${ftpDetails.protocol == 'FTPS'}">selected</c:if>>FTPS</option>
				    <option value="SFTP" <c:if test="${ftpDetails.protocol == 'SFTP'}">selected</c:if>>SFTP</option>
				    </select>
				    <span id="protocol${ftpDetails.method}Msg" class="control-label"></span>
			    </div>
			    <div id="ip${ftpDetails.method}Div" class="form-group">
				<label class="control-label" for="ip${ftpDetails.method}">Host *</label>
				<input name="FTPFields[${field.index}].ip" id="ip${ftpDetails.method}" class="form-control" type="text" maxLength="45" value="${ftpDetails.ip}"  />
				<span id="ip${ftpDetails.method}Msg" class="control-label"></span>
			    </div>
			    <div id="username${ftpDetails.method}Div" class="form-group">
				<label class="control-label" for="username${ftpDetails.method}">Username *</label>
				<input name="FTPFields[${field.index}].username" id="username${ftpDetails.method}" class="form-control" type="text" maxLength="45" value="${ftpDetails.username}"  />
				<span id="username${ftpDetails.method}Msg" class="control-label"></span>
			    </div>
			    <div id="password${ftpDetails.method}Div" class="form-group">
				<label class="control-label" for="password${ftpDetails.method}">Password *</label>
				<input name="FTPFields[${field.index}].password" id="password${ftpDetails.method}" class="form-control" type="text" maxLength="45" value="${ftpDetails.password}"  />
				<span id="password${ftpDetails.method}Msg" class="control-label"></span>
			    </div>
			    <div id="directory${ftpDetails.method}Div" class="form-group">
				<label class="control-label" for="directory${ftpDetails.method}">Directory *</label>
				<input name="FTPFields[${field.index}].directory" id="directory${ftpDetails.method}" class="form-control" type="text" maxLength="255" value="${ftpDetails.directory}"  />
				<span id="directory${ftpDetails.method}Msg" class="control-label"></span>
			    </div>
			    <div id="port${ftpDetails.method}Div" class="form-group">
				<label class="control-label" for="port${ftpDetails.method}">Port *</label>
				<input name="FTPFields[${field.index}].port" id="port${ftpDetails.method}" class="form-control" type="text" maxLength="45" value="${ftpDetails.port}"  />
				<span id="port${ftpDetails.method}Msg" class="control-label"></span>
			    </div>
			    <div class="form-group" <c:if test="${ftpDetails.certification == null || ftpDetails.certification == ''}">style="display:none" </c:if>>
				    <label class="control-label">Existing Certifciation:</label>
				    <input type="text" disabled value="${ftpDetails.certification}" class="form-control" />
				<input type="hidden" id="certification${ftpDetails.method}" name="FTPFields[${field.index}].certification" value="${ftpDetails.certification}" />
			    </div>
			    <div id="certificationfileDiv${ftpDetails.method}" class="form-group ${status.error ? 'has-error' : '' }" style="display:none;">
				<label class="control-label" for="certification"><c:if test="${ftpDetails.certification != null}">New </c:if>Certification File </label>
				<input type="file" id="file${ftpDetails.method}" name="FTPFields[${field.index}].file" class="form-control"  />
				<span id="certificationfileMsg" class="control-label"></span>
			    </div>
			    <c:if test="${ftpDetails.ip != ''}">
				<div class="pull-right">
				    <a href="javascript:void(0);" class="btn btn-primary btn-xs testFTP<c:choose><c:when test="${ftpDetails.method == 1}">Get</c:when><c:otherwise>Push</c:otherwise></c:choose>"  title="Test FTP Connection">Test FTP Connection</a>
				</div>
			    </c:if>
			</div>
		    </c:forEach>
		</div>
	    </div>
	</div>
    </section>
    <section class="panel panel-default">
	<div class="panel-heading">
	    <h3 class="panel-title">File Error Handling</h3>
	</div>
	<div class="panel-body"> 
	    <div class="form-container">
                <spring:bind path="errorHandling">
		    <div class="form-group">
			<label class="control-label" for="errorHandling">Error Handling *</label>
			<div>
			    <label class="radio-inline">
				<form:radiobutton id="errorHandling" path="errorHandling" value="2" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Reject individual transactions on error
			    </label>
			    <label class="radio-inline">
				<form:radiobutton id="errorHandling" path="errorHandling" value="3" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Reject entire file on a single transaction error
			    </label>
			    <label class="radio-inline">
				<form:radiobutton id="errorHandling" path="errorHandling" value="4" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Send transactions with errors through to the final generated target file
			    </label>
			</div>
			<c:if test="${transportDetails.copiedTransportId > 0}">
			    <form:hidden path="errorHandling" />
			</c:if>      
		    </div>
		</spring:bind>
		<spring:bind path="populateInboundAuditReport">
		    <div class="form-group" style="padding-top:10px;">
			<label class="control-label" for="populateInboundAuditReport">Do you want to populate the inbound audit report with errors found while processing this target file? *</label>
			<div>
			    <label class="radio-inline">
				<form:radiobutton class="populateInboundAuditReport" path="populateInboundAuditReport" value="1" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Yes
			    </label>
			    <label class="radio-inline">
				<form:radiobutton class="populateInboundAuditReport" path="populateInboundAuditReport" value="0" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> No
			    </label>
			</div>
			<c:if test="${transportDetails.copiedTransportId > 0}">
			    <form:hidden path="populateInboundAuditReport" />
			</c:if>      
		    </div>
		</spring:bind>
	    </div>
	</div>
    </section>	
</form:form>