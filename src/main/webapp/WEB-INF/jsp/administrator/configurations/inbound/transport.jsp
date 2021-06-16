<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script src="<%=request.getContextPath()%>/dspResources/js/admin/configuration/inbound/transport.js"></script>

<form:form id="transportDetails" commandName="transportDetails" modelAttribute="transportDetails" method="post" role="form" enctype="multipart/form-data">

    <input type="hidden" id="action" name="action" value="save" />
    <input type="hidden" id="messageTypeId" value="${messageTypeId}" />
    <form:hidden path="id" id="id" />
    <form:hidden path="configId" id="configId" />
    <form:hidden path="autoRelease" value="1" />
    <form:hidden path="clearRecords" value="1" />
    <form:hidden path="massTranslation" value="true" />
    <form:hidden path="helRegistryId" id="helRegistryId" />
    <form:hidden path="helSchemaName" id="helSchemaName" />
    <form:hidden path="mergeBatches" />
    <form:hidden path="encodingId" value="1" />

    <section class="panel panel-default">
	<div class="panel-heading">
	    <h3 class="panel-title">
		Transport Method (How is the file getting to the EAH UT?)
	    </h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<spring:bind path="transportMethodId">
		    <div id="transportMethodDiv" class="form-group ${status.error ? 'has-error' : '' }">
			<form:select path="transportMethodId" id="transportMethod" class="form-control" disabled="${transportDetails.id == 0 ? 'false' : 'true' }">
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
		    <label class="control-label" for="helRegistryConfigId">Associated Health-e-Link eReferral Configuration *</label>
		    <form:select path="helRegistryConfigId" id="helRegistryConfigId" rel="${transportDetails.helRegistryConfigId}" class="form-control half" disabled="${transportDetails.id == 0 ? 'false' : 'true' }"></form:select>
		    <c:if test="${transportDetails.id > 0}">
			<form:hidden path="helRegistryConfigId"/>
		    </c:if> 
		    <span id="helRegistryConfigIdMsg" class="control-label"></span>
		</div>
	    </spring:bind>
	</div>
    </section>

    <section class="panel panel-default">
	<div class="panel-heading">
	    <h3 class="panel-title">File Details</h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<%-- File Details --%>
		<div id="fileDetailsDiv"  style="display:none">
		    <spring:bind path="fileLocation">
			<div class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="fileLocation">Where will the file be stored on the EAH UT prior to processing? *</label>
			    <form:input disabled="${not empty transportDetails.fileLocation ? 'true' : 'false' }" origVal="${transportDetails.fileLocation}" path="fileLocation" id="fileLocation" class="form-control" type="text" maxLength="255" />
			    <form:errors path="fileLocation" cssClass="control-label" element="label" />
                            <c:if test="${not empty transportDetails.fileLocation}">
                                <form:hidden path="fileLocation"/>
                            </c:if> 
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
		    <c:if test="${configurationDetails.type == 2}">
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
		    </c:if>    
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
                    <%--  
		    <spring:bind path="encodingId">
			<div id="encodingDiv" class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="encodingId">Does the incoming file have any Encoding? *</label>
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
                    --%>
		</div>
	    </div>
	</div>
    </section>
			
    <%-- FILE DROP Details --%>	
    <section id="fileDropDetailsDiv" class="panel panel-default" style="display:none">
	<div class="panel-heading">
	    <h3 class="panel-title">File Drop Location</h3>
	</div>
	<div class="panel-body">
	    <div class="form-container">
		<div class="row">
		    <c:forEach items="${transportDetails.fileDropFields}" var="fileDropDetails" varStatus="field">
			<div class="form-group col-md-6">
			    <input name="fileDropFields[${field.index}].method" class="form-control" type="hidden" value="${fileDropDetails.method}"  />
			    <input name="fileDropFields[${field.index}].id" id="id${fileDropDetails.method}" class="form-control" type="hidden" value="${fileDropDetails.id}"  />
			    <c:if test="${fileDropDetails.method == 2}">
			      <input name="fileDropFields[${field.index}].directory" id="directory${fileDropDetails.method}" class="form-control" type="hidden" maxLength="255" value="${fileDropDetails.directory}"  />
			    </c:if>
			    <c:if test="${fileDropDetails.method == 1}">
                                <c:if test="${empty transportDetails.fileLocation}">
                                    <p>Make sure to add a sub folder after "/input files"</p>
                                </c:if>
				<input disabled="${not empty fileDropDetails.directory ? 'true' : 'false' }" name="fileDropFields[${field.index}].directory" id="directory${fileDropDetails.method}" class="form-control" type="text" maxLength="255" value="${fileDropDetails.directory}"  />
				<span id="rDirectory${fileDropDetails.method}Msg" class="control-label"></span>
                                <c:if test="${not empty transportDetails.fileLocation}">
                                    <input type="hidden" name="fileDropFields[${field.index}].directory" id="directory${fileDropDetails.method}" class="form-control"  maxLength="255" value="${fileDropDetails.directory}"  />
                                </c:if>
                             </c:if>       
			</div>
		    </c:forEach>
		</div>
	    </div>
	</div>
    </section>


    <%-- DIRECT MESSAGE Details 
    <section id="directMessageDetailsDiv" class="panel panel-default" style="display:none">
	<div class="panel-heading">
	    <h3 class="panel-title"><input type="checkbox" id="enableDirect" value="1" <c:if test="${transportDetails.directMessageFields[0].id > 0 && transportDetails.transportMethodId == 13}">checked</c:if>> Enable HISP</h3>
	</div>
	<div class="panel-body ${transportDetails.directMessageFields[0].id > 0 ? '' : 'collapse'} directDetails">
	    <div class="form-container">
		<div class="form-group">
		   <c:set var="directMessageFields" value="${transportDetails.directMessageFields[0]}" />
		   <input name="directMessageFields[0].id" type="hidden" value="${directMessageFields.id}"  />
		   <input name="directMessageFields[0].orgId" type="hidden" value="${directMessageFields.orgId}"  />
		   <input name="directMessageFields[0].dateCreated" type="hidden" value="${directMessageFields.dateCreated}"  />
		   <label class="control-label" for="hispId">Select the HISP *</label>
		   <select name="directMessageFields[0].hispId" id="hispId" class="form-control sm-input">
		       <option value="">- Select -</option>
		       <c:forEach var="hisp" items="${hisps}">
			   <option value="${hisp.id}" <c:if test="${directMessageFields.hispId == hisp.id}">selected</c:if>>${hisp.hispName}</option>                                                
		       </c:forEach>
		   </select>
		   <span id="encodingMsg" class="control-label"></span>
	       </div>
		<c:choose>
		    <c:when test="${configurationDetails.type == 1}">
			 <div class="form-group directDomainDiv">
			     <label class="control-label" for="directDomain">Direct Address Domain *</label>
			     <input name="directMessageFields[0].directDomain" id="directDomain" class="form-control" type="text" maxLength="75" value="${directMessageFields.directDomain}"  />
			     <span id="directDomainMsg" class="control-label"></span>
			 </div>  
			 <div class="form-group dmFindConfigDiv">
			     <label class="control-label" for="dmFindConfig">How will you determine the configuration? *</label>
			     <select name="directMessageFields[0].dmFindConfig" id="dmFindConfig" class="form-control sm-input">
				 <option value="">- Select </option>
				 <option value="1" <c:if test="${directMessageFields.dmFindConfig == 1}">selected="selected"</c:if>>From the direct message to address</option>
				 <option value="2" <c:if test="${directMessageFields.dmFindConfig == 2}">selected="selected"</c:if>>From within the referral file</option>
			     </select>
			     <span id="dmFindConfigMsg" class="control-label"></span>
			 </div> 
			 <div class="form-group dmConfigKeywordDiv" style="display:${directMessageFields.dmFindConfig == 1 ? 'block' : 'none'}">
			     <label class="control-label" for="dmConfigKeyword">Direct Address Configuration Keyword *</label>
			     <input name="dmConfigKeyword" id="dmConfigKeyword" class="form-control" type="text" maxLength="45" value="${transportDetails.dmConfigKeyword}"  />
			     <span id="dmConfigKeywordMsg" class="control-label"></span>
			  </div>  
		    </c:when>
		    <c:otherwise>

		    </c:otherwise>
		</c:choose>
	    </div>
	</div>
    </section>
    --%>	
    
    <%-- REST Details 	
    <section id="restDetailsDiv" class="panel panel-default" style="display:none">
	<div class="panel-heading">
	    <h3 class="panel-title"><input type="checkbox" id="enableRest" value="1" <c:if test="${not empty transportDetails.restAPIURL}">checked</c:if>> Enable API</h3>
	</div>
	<div class="panel-body ${not empty transportDetails.restAPIURL ? '' : 'collapse'} restDetails">
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
		<c:choose>
		    <c:when test="${configurationDetails.type == 1}">
			<spring:bind path="restAPIType">
			    <div id="restAPITypeDiv" class="form-group ${status.error ? 'has-error' : '' }">
				<label class="control-label" for="restAPIType">API Type *</label>
				<form:select path="restAPIType" id="restAPIType" class="form-control half">
				    <option value="">- Select -</option>
				    <c:forEach items="${restAPITypes}"  varStatus="tStatus">
					<option value="${restAPITypes[tStatus.index][0]}" <c:if test="${transportDetails.restAPIType == restAPITypes[tStatus.index][0]}">selected</c:if>>${restAPITypes[tStatus.index][1]}</option>
				    </c:forEach>
				</form:select>
				<span id="restAPITypeMsg" class="control-label"></span>
			    </div>
			</spring:bind>
		    </c:when>
		    <c:otherwise>
			<form:hidden path="restAPIType" value = "0" />
		    </c:otherwise>
		</c:choose>
		<c:choose>
		    <c:when test="${configurationDetails.type == 2}">
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
		    </c:when>
		    <c:otherwise>
			<form:hidden path="restAPIFunctionId" value = "0" />
		    </c:otherwise>
		</c:choose>
	    </div>
	</div>
    </section>--%>
    <form:hidden path="restAPIFunctionId" value = "0" />

    <%-- FTP Details --%>	
    <section id="ftpDetailsDiv" class="panel panel-default" style="display:none">
	<div class="panel-heading">
	    <h3 class="panel-title"><input type="checkbox" id="enableFTP" value="1" <c:if test="${transportDetails.FTPFields[0].id > 0}">checked</c:if>> Enable FTP</h3>
	</div>
	<div class="panel-body ${transportDetails.FTPFields[0].id > 0 ? '' : 'collapse'} ftpDetails">
	    <div class="form-container">
		<div class="row">
		    <c:forEach items="${transportDetails.FTPFields}" var="ftpDetails" varStatus="field">
			<c:if test="${ftpDetails.method == 1}">
			    <input name="FTPFields[${field.index}].method" class="form-control" type="hidden" value="${ftpDetails.method}"  />
			    <input name="FTPFields[${field.index}].id" id="id${ftpDetail.method}" class="form-control" type="hidden" value="${ftpDetails.id}"  />
			    <div class="form-group col-md-6">
				<div id="protocol${ftpDetails.method}Div" class="form-group">
				    <label class="control-label" for="protocol${ftpDetails.method}">Protocol *</label>
				    <select name="FTPFields[${field.index}].protocol" id="protocol${ftpDetails.method}" rel="${ftpDetails.method}" class="form-control ftpProtocol">
					<option value="">- Select -</option>
					<option value="FTP" <c:if test="${ftpDetails.protocol == 'FTP'}">selected</c:if>>FTP</option>
					<%--<option value="FTPS" <c:if test="${ftpDetails.protocol == 'FTPS'}">selected</c:if>>FTPS</option>--%>
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
				
				<%--<div id="logontype${ftpDetails.method}Div" class="form-group">
				    <label class="control-label" for="logontype${ftpDetails.method}">Logon Type *</label>
				    <select name="FTPFields[${field.index}].logontype" id="logontype${ftpDetails.method}" rel="${ftpDetails.method}" class="form-control logontype">
					<option value="">- Select -</option>
					<option value="keyfile" <c:if test="${ftpDetails.logontype == 'keyfile'}">selected</c:if>>Key file</option>
					<option value="normal" <c:if test="${ftpDetails.logontype == 'normal'}">selected</c:if>>Normal</option>
				    </select>
				    <span id="logontype${ftpDetails.method}Msg" class="control-label"></span>
				</div>--%>
				
				
				
				<div id="password${ftpDetails.method}Div" class="form-group">
				    <label class="control-label" for="password${ftpDetails.method}">Password *</label>
				    <input name="FTPFields[${field.index}].password" id="password${ftpDetails.method}" class="form-control" type="text" maxLength="45" value="${ftpDetails.password}"  />
				    <span id="password${ftpDetails.method}Msg" class="control-label"></span>
				</div>
				<div id="directory${ftpDetails.method}Div" class="form-group">
				    <label class="control-label" for="directory${ftpDetails.method}">Remote FTP Directory * <small>(Remote file will be moved to the File Drop location above.)</small></label>
				    <input name="FTPFields[${field.index}].directory" id="ftpdirectory${ftpDetails.method}" class="form-control" type="text" maxLength="255" value="${ftpDetails.directory}"  />
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
			    </div>
			</c:if>
		    </c:forEach>
		</div>
	    </div>
	</div>
    </section>

    <%-- Error Handling for Incoming files only --%>
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
				<form:radiobutton id="errorHandling" path="errorHandling" value="4" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Send error records through to the target file
			    </label>
			</div>
			<c:if test="${transportDetails.copiedTransportId > 0}">
			    <form:hidden path="errorHandling" />
			</c:if>      
		    </div>
		</spring:bind>
                <spring:bind path="errorEmailAddresses">
		    <div id="errorEmailAddressesDiv" class="form-group" style="${transportDetails.errorHandling != 3 ? 'display:none' : 'display:block'}">
			<label class="control-label" for="errorEmailAddresses">Enter email address(es) to receive rejection email</label>
                        <p>Separate multiple emails with a comma.</p>
			<form:input path="errorEmailAddresses" id="errorEmailAddresses" class="form-control" type="text" maxLength="750" />
			<span id="errorEmailAddressesMsg" class="control-label"></span>
		    </div>
		</spring:bind>
		<spring:bind path="threshold">
		    <div id="thresholdDiv" class="form-group">
			<label class="control-label" for="errorHandling">Error Threshold *</label>
                        <p>What % of errors should I accept before flagging the batch with an alert</p>
			<form:input path="threshold" id="threshold" class="form-control sm-input" type="text" maxLength="3" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" />
			<span id="thresholdMsg" class="control-label"></span>
		    </div>
		</spring:bind>
	    </div>
	</div>
    </section>	
</form:form>
