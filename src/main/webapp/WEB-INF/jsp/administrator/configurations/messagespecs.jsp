<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <c:if test="${not empty templateError}" >
                <div class="alert alert-danger">
                    <strong>Template Error!</strong> 
                    ${templateError}
                </div>
            </c:if>
            <c:if test="${not empty savedStatus}" >
                <div class="alert alert-success">
                    <strong>Success!</strong> 
                    <c:choose><c:when test="${savedStatus == 'updated'}">The configuration message specs have been successfully updated!</c:when></c:choose>
                </div>
            </c:if>
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                    <dt>Configuration Summary:</dt>
                    <dd><strong>Organization:</strong> ${configurationDetails.orgName}</dd>
                    <dd><strong>Configuration Type:</strong> <span id="configType" rel="${configurationDetails.type}"><c:choose><c:when test="${configurationDetails.type == 1}">Source</c:when><c:otherwise>Target</c:otherwise></c:choose></span></dd>
                    <dd><strong>Configuration Name:</strong> ${configurationDetails.configName}</dd>
                    <dd><strong>Transport Method:</strong> <c:choose><c:when test="${configurationDetails.transportMethod == 'File Upload'}"><c:choose><c:when test="${configurationDetails.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${configurationDetails.transportMethod}</c:otherwise></c:choose></dd>
                    </dt>
                </div>
            </section>
            <c:if test="${not empty error}" >
                <div class="alert alert-danger" role="alert">
                    The selected file was not found.
                </div>
            </c:if>
	</div>
	<div class="col-md-12">
            <form:form id="messageSpecs" commandName="messageSpecs" modelAttribute="messageSpecs" enctype="multipart/form-data" method="post" role="form">
                <input type="hidden" id="action" name="action" value="save" />
                <form:hidden path="id" id="id" />
                <form:hidden path="configId" />
                <form:hidden path="hasHeader" value="0"/>
                <form:hidden path="fileLayout" value="1" /> 
                <form:hidden path="excelstartrow" />
                <form:hidden path="excelskiprows" />

                 <section class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Template Details</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-container">
                            <c:choose>
				<c:when test="${transportType == 10 || (transportType == 13 && configurationDetails.type == 2 && transportDetails.helRegistryConfigId > 0 && !transportDetails.ergFileDownload)}">
				    <div class="form-group">
					<label class="control-label" for="templateFile">Template File</label>
					<p>No need for a template file when the configuration is a eReferral message type and the transport method is Online Form (eRG), File Upload or File Drop (Going back to the eRG).</p>
					<form:hidden path="templateFile" />
				    </div>
				</c:when>
				<c:otherwise>
				    <c:if test="${not empty messageSpecs.templateFile}">
                                        <form:hidden path="templateFile" />
					<%-- <div class="form-group">
                                            <label class="control-label" for="templateFile">Current File <a href="/FileDownload/downloadFile.do?fromPage=messagespec&filename=${messageSpecs.templateFile}&foldername=${cleanOrgURL}/templates">Download Template</a></label>
					    <input type="text" disabled class="form-control" value="${messageSpecs.templateFile}" />
					    <form:hidden path="templateFile" />
					</div>--%>
					<div class="form-group">
                                            <div class="col-md-12" style="padding-left:0px;">
                                                <div class="col-lg-2" style="padding-left:0px;">
                                                    <a href="#!" class="btn btn-primary btn-action createNewTemplate" rel="${configurationDetails.id}" data-toggle="tooltip" data-original-title="Download Latest Template">Download Current Configuration Template</a>
                                                </div>
                                            </div>
					 </div>
                                        <div class="form-group">
                                            <p>
                                                <br /><br />
                                                <span style="margin-top:10px;"><strong>Last Uploaded Template File: ${lastUploadedDate}</strong></span>
                                                <hr>
                                            </p>
                                        </div>
				    </c:if>
				    <spring:bind path="file">
					<div id="templateFileDiv" class="form-group ${status.error ? 'has-error' : '' }">
					    <label class="control-label" for="file">Upload <c:if test="${not empty messageSpecs.templateFile}">New </c:if>Template (XLSX file) *</label>
					    <form:input path="file" id="file" type="file" class="form-control"  />
					    <span id="templateFileMsg" class="control-label"></span>
					</div>
				    </spring:bind>
				</c:otherwise>
			    </c:choose>
                        </div>
                    </div>
                 </section>
                
                <c:if test="${configurationDetails.type == 1}">
                <section class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Message Specs</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-container">
                            <%-- Source File Download options only --%>
                            <c:choose>
                                <c:when test="${transportType == 3}">
                                    <spring:bind path="messageTypeCol">
                                        <div class="form-group ${status.error ? 'has-error' : '' }">
                                            <label class="control-label" for="fileNameConfigHeader">Message Type Identifier in file name</label>
                                            <form:input path="fileNameConfigHeader" id="fileNameConfigHeader" class="form-control sm-input" type="text" maxLength="45" />
                                            <form:errors path="fileNameConfigHeader" cssClass="control-label" element="label" />
                                        </div>
                                    </spring:bind>
                                </c:when>
                                <c:otherwise>
                                    <form:hidden path="fileNameConfigHeader" />
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${transportType != 8}">
                                    <c:if test="${transportType != 10}">
                                        <spring:bind path="messageTypeCol">
                                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                                <label class="control-label" for="messageTypeCol">Column containing the message type (Enter 0 if not provided or not in use) *</label>
                                                <form:input path="messageTypeCol" id="messageTypeCol" class="form-control sm-input" type="text" maxLength="3" />
                                                <form:errors path="messageTypeCol" cssClass="control-label" element="label" />
                                            </div>
                                        </spring:bind>
                                        <spring:bind path="messageTypeVal">
                                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                                <label class="control-label" for="messageTypeVal">Message Type Value</label>
                                                <form:input path="messageTypeVal" id="messageTypeVal" class="form-control" type="text" maxLength="45" />
                                                <form:errors path="messageTypeVal" cssClass="control-label" element="label" />
                                            </div>
                                        </spring:bind>
                                    </c:if>
                                    <spring:bind path="sourceSubOrgCol">
                                        <div class="form-group ${status.error ? 'has-error' : '' }">
                                            <label class="control-label" for="sourceSubOrgCol">Column containing the sending site (Enter 0 if not provided or not in use)</label>
                                            <form:input path="sourceSubOrgCol" id="sourceSubOrgCol" class="form-control sm-input" type="text" maxLength="3" />
                                            <form:errors path="sourceSubOrgCol" cssClass="control-label" element="label" />
                                        </div>
                                    </spring:bind>
                                    <spring:bind path="targetOrgCol">
                                        <div class="form-group ${status.error ? 'has-error' : '' }">
                                            <label class="control-label" for="targetOrgCol">Column containing the target organization (Enter 0 if not provided or not in use) *</label>
                                            <form:input path="targetOrgCol" id="targetOrgCol" class="form-control sm-input" type="text" maxLength="3" />
                                            <form:errors path="targetOrgCol" cssClass="control-label" element="label" />
                                        </div>
                                    </spring:bind>
                                    <c:choose>
                                        <c:when test="${transportType != 10}">
                                            <spring:bind path="containsHeaderRow">
                                                <div class="form-group">
                                                    <label class="control-label" for="containsHeaderRow">Will the submitted file have any header rows? *</label>
                                                    <div>
                                                        <label class="radio-inline">
                                                            <form:radiobutton class="containsHeaderRow" id="containsHeaderRow" path="containsHeaderRow" value="1" /> Yes 
                                                        </label>
                                                        <label class="radio-inline">
                                                            <form:radiobutton class="containsHeaderRow" id="containsHeaderRow" path="containsHeaderRow" value="0"/> No
                                                        </label>
                                                    </div>
                                                </div>
                                            </spring:bind>
                                            <spring:bind path="totalHeaderRows">
                                                <div id="totalHeaderRowsDiv" class="form-group ${status.error ? 'has-error' : '' }" style="display: ${messageSpecs.containsHeaderRow == true ? 'block' : 'none'}">
                                                    <label class="control-label" for="totalHeaderRows">How many header rows does the file have? *</label>
                                                    <form:input path="totalHeaderRows" id="totalHeaderRows" class="form-control sm-input" type="text" maxLength="2" />
                                                    <form:errors path="totalHeaderRows" cssClass="control-label" element="label" />
                                                    <span id="totalHeaderRowsMsg" class="control-label"></span>
                                                </div>
                                            </spring:bind>
                                             <c:choose>
                                                <%-- Only CCD (XML), HL7 and JSON file types need parsing scripts --%>
                                                <c:when test="${fileType == 4 || fileType == 9 || fileType == 12}">
                                                   <c:if test="${not empty messageSpecs.parsingTemplate}">
                                                       <div class="form-group">
                                                           <label class="control-label" for="parsingTemplate">Current Parsing Script</label>
                                                           <input type="text" disabled id="parsingTemplate" class="form-control" value="${messageSpecs.parsingTemplate}" />
                                                           <form:hidden id="parsingTemplate" path="parsingTemplate" />
                                                       </div>
                                                   </c:if>
                                                   <spring:bind path="file">
                                                       <div id="parsingTemplateDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                                           <label class="control-label" for="file">Upload <c:if test="${not empty messageSpecs.parsingTemplate}">New </c:if> Parsing Script (JAR file)</label>
                                                           <form:input path="parsingScriptFile" id="parsingScriptFile" class="form-control" type="file" />
                                                           <form:errors path="parsingScriptFile" cssClass="control-label" element="label" />
                                                           <span id="parsingTemplateMsg" class="control-label"></span>
                                                       </div>
                                                    </spring:bind>
                                                </c:when>
                                                 <c:otherwise>
                                                    <form:hidden path="parsingTemplate" />
                                                    <form:hidden path="parsingScriptFile" />
                                                 </c:otherwise>
                                             </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <form:hidden path="containsHeaderRow" />
                                            <form:hidden path="parsingTemplate" />
                                            <form:hidden path="parsingScriptFile" />
                                            <form:hidden path="messageTypeCol" />
                                            <form:hidden path="messageTypeVal" />
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <form:hidden path="messageTypeCol" />
                                    <form:hidden path="messageTypeVal" />
                                    <form:hidden path="sourceSubOrgCol" />
                                    <form:hidden path="targetOrgCol" value="1" />
                                    <form:hidden path="containsHeaderRow" />
                                    <form:hidden path="parsingTemplate" />
                                    <form:hidden path="parsingScriptFile" />
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </section>  
                </c:if>
                
                <c:if test="${not empty availableFields}">            
                    <section class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title">Audit Report Reportable Fields</h3>
                        </div>
                        <div class="panel-body">
                            <div class="form-container">  
                                <div class="row col-lg-12">
                                    <div class="col-lg-6">
                                        <spring:bind path="rptField1">
                                            <div class="form-group rtpField ${status.error ? 'has-error' : '' }">
                                                <span id="rptFieldMsg" class="control-label"></span>
                                                <label class="control-label" for="rptField1">Field 1 *</label>
                                                <form:select path="rptField1" id="rptField1" class="form-control">
                                                    <option value="0">- Select -</option>
                                                    <c:forEach items="${availableFields}" var="field" varStatus="fStatus">
                                                          <option value="${availableFields[fStatus.index].fieldNo}" <c:if test="${messageSpecs.rptField1 == availableFields[fStatus.index].fieldNo}">selected</c:if>>${availableFields[fStatus.index].fieldDesc} </option>
                                                     </c:forEach>
                                                </form:select>
                                                <form:errors path="rptField1" cssClass="control-label" element="label" />
                                            </div>
                                        </spring:bind>
                                        <spring:bind path="rptField2">
                                            <div class="form-group rtpField ${status.error ? 'has-error' : '' }">
                                                <label class="control-label" for="rptField2">Field 2 *</label>
                                                <form:select path="rptField2" id="rptField2" class="form-control">
                                                    <option value="0">- Select -</option>
                                                    <c:forEach items="${availableFields}" var="field" varStatus="fStatus">
                                                        <option value="${availableFields[fStatus.index].fieldNo}" <c:if test="${messageSpecs.rptField2 == availableFields[fStatus.index].fieldNo}">selected</c:if>>${availableFields[fStatus.index].fieldDesc} </option>
                                                    </c:forEach>
                                                </form:select>
                                                <form:errors path="rptField2" cssClass="control-label" element="label" />
                                            </div>
                                        </spring:bind>
                                    </div>
                                    <div class="col-lg-6">
                                        <spring:bind path="rptField3">
                                            <div class="form-group rtpField ${status.error ? 'has-error' : '' }">
                                                <label class="control-label" for="rptField3">Field 3 *</label>
                                                <form:select path="rptField3" id="rptField3" class="form-control">
                                                    <option value="0">- Select -</option>
                                                    <c:forEach items="${availableFields}" var="field" varStatus="fStatus">
                                                        <option value="${availableFields[fStatus.index].fieldNo}" <c:if test="${messageSpecs.rptField3 == availableFields[fStatus.index].fieldNo}">selected</c:if>>${availableFields[fStatus.index].fieldDesc} </option>
                                                    </c:forEach>
                                                </form:select>
                                                <form:errors path="rptField3" cssClass="control-label" element="label" />
                                            </div>
                                        </spring:bind>
                                        <spring:bind path="rptField4">
                                            <div class="form-group rtpField ${status.error ? 'has-error' : '' }">
                                                <label class="control-label" for="rptField4">Field 4 *</label>
                                                <form:select path="rptField4" id="rptField4" class="form-control">
                                                    <option value="0">- Select -</option>
                                                    <c:forEach items="${availableFields}" var="field" varStatus="fStatus">
                                                        <option value="${availableFields[fStatus.index].fieldNo}" <c:if test="${messageSpecs.rptField4 == availableFields[fStatus.index].fieldNo}">selected</c:if>>${availableFields[fStatus.index].fieldDesc} </option>
                                                    </c:forEach>
                                                </form:select>
                                                <form:errors path="rptField4" cssClass="control-label" element="label" />
                                            </div>
                                        </spring:bind>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                 </c:if>
            </form:form>
        </div>     
    </div>
</div>
<div class="modal fade" id="dtDownloadModal" role="dialog" tabindex="-1" aria-labeledby="Data Translations Download" aria-hidden="true" aria-describedby="Data Translations Download"></div>


