<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">

            <c:if test="${not empty savedStatus}" >
                <div class="alert alert-success">
                    <strong>Success!</strong> 
                    <c:choose><c:when test="${savedStatus == 'updated'}">The configuration transport details have been successfully updated!</c:when><c:otherwise>The configuration initial setup has been saved!</c:otherwise></c:choose>
                        </div>
            </c:if>
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                    <dt>Configuration Summary:</dt>
                    <dd><strong>Organization:</strong> ${configurationDetails.orgName}&nbsp;<span id="configtype" rel="${configurationDetails.type}"><c:choose><c:when test="${configurationDetails.type == 1}">(Source)</c:when><c:otherwise>(Target)</c:otherwise></c:choose></span></dd>
                    <dd><strong>Configuration Type:</strong> <span id="configType" rel="${configurationDetails.type}"><c:choose><c:when test="${configurationDetails.type == 1}">Source</c:when><c:otherwise>Target</c:otherwise></c:choose></span></dd>
                    <dd><strong>Configuration Name:</strong> ${configurationDetails.configName}</dd>
                    <dd><strong>Message Type:</strong> ${configurationDetails.messageTypeName}</dd>
                    <dd><strong>Transport Method:</strong> <c:choose><c:when test="${configurationDetails.transportMethod == 'File Upload'}"><c:choose><c:when test="${configurationDetails.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${configurationDetails.transportMethod}</c:otherwise></c:choose></dd>
                    </dt>
                </div>
            </section>
        </div>
       <div class="col-md-12">
            <form:form id="transportDetails" commandName="transportDetails" modelAttribute="transportDetails" method="post" role="form" enctype="multipart/form-data">

                <c:if test="${(transportDetails.id == 0 && fn:length(availConfigurations) > 1) || transportDetails.copiedTransportId > 0}">
                    <section class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title">Existing Transport Methods</h3>
                        </div>
                        <div class="panel-body">
                            <div class="form-container">
                                <div id="existingtransportMethodDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                    <form:select path="copiedTransportId" id="existingtransportMethod" class="form-control" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}">
                                        <option value="">- Select -</option>
                                        <c:forEach items="${availConfigurations}" var="config">
                                            <c:if test="${config.getId() != transportDetails.configId}">
                                                <option value="${config.gettransportDetailId()}" <c:if test="${config.gettransportDetailId() == transportDetails.copiedTransportId}">selected</c:if>>${config.getconfigName()}&nbsp;&#149;&nbsp;${config.getMessageTypeName()}&nbsp;&#149;&nbsp;${config.gettransportMethod()}</option>
                                            </c:if>
                                        </c:forEach>
                                    </form:select>
                                </div>
                            </div>  
                        </div>
                    </section>  
                </c:if>
                <form:hidden path="copiedTransportId" />     


                <input type="hidden" id="action" name="action" value="save" />
                <form:hidden path="id" id="id" />
                <form:hidden path="configId" id="configId" />
		<form:hidden path="autoRelease" value="1" />
		<form:hidden path="clearRecords" value="1" />
		<form:hidden path="massTranslation" value="true" />

                <section class="panel panel-default">

                    <div class="panel-heading">
                        <h3 class="panel-title">Details</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-container">
                            <spring:bind path="transportMethodId">
                                <div id="transportMethodDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                    <label class="control-label" for="transportMethod">Transport Method *</label>
                                    <form:select path="transportMethodId" id="transportMethod" class="form-control" disabled="${transportDetails.id > 0 && transportDetails.transportMethodId > 0 ? 'true' : 'false'}">
                                        <option value="">- Select -</option>
                                        <c:forEach items="${transportMethods}" var="transMethod" varStatus="tStatus">
                                            <option value="${transportMethods[tStatus.index][0]}" <c:if test="${transportDetails.transportMethodId == transportMethods[tStatus.index][0]}">selected</c:if>><c:choose><c:when test="${transportMethods[tStatus.index][0] == 1}"><c:choose><c:when test="${configurationDetails.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${transportMethods[tStatus.index][1]}</c:otherwise></c:choose></option>
                                        </c:forEach>
                                    </form:select>
                                    <span id="transportMethodMsg" class="control-label"></span>
                                    <c:if test="${transportDetails.id > 0}">
                                        <form:hidden path="transportMethodId" />
                                    </c:if>
                                </div>
                            </spring:bind>
                            <div id="upload-downloadDiv" class="methodDiv" style="display:none">
                                <spring:bind path="fileLocation">
                                    <div class="form-group ${status.error ? 'has-error' : '' }">
                                        <label class="control-label" for="fileLocation">File Location *</label>
                                        <form:input origVal="${transportDetails.fileLocation}" path="fileLocation" id="fileLocation" class="form-control" type="text" maxLength="255" />
                                        <form:errors path="fileLocation" cssClass="control-label" element="label" />
                                    </div>
                                </spring:bind>
                                <spring:bind path="maxFileSize">
                                    <div id="maxFileSizeDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                        <label class="control-label" for="maxFileSize">Max File Size (mb) *</label>
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
                                <spring:bind path="encodingId">
                                    <div id="encodingDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                        <label class="control-label" for="encodingId">Encoding *</label>
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
                                <%-- Target File Download options only --%>
                                <c:if test="${configurationDetails.type == 2}">
                                    <div id="hl7PDFSampleDiv" style="display:${transportDetails.fileType == 4 ? 'block' : 'none'}">
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
                                    <div id="ccdSampleDiv" style="display:${(transportDetails.fileType == 9 || transportDetails.fileType == 12) ? 'block' : 'none'}">
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
				    <spring:bind path="rrProgramId">
					<div id="rrProgramDiv" class="form-group ${status.error ? 'has-error' : '' }">
					    <label class="control-label" for="rrProgramId">Registry *</label>
					    <form:select path="rrProgramId" id="rrProgramId" class="form-control">
						<option value="">- Select -</option>
						<c:forEach items="${registries}" var="registry">
						    <option value="${registry.id}" <c:if test="${transportDetails.rrProgramId == registry.id}">selected</c:if>>${registry.programName}</option>                                                
						</c:forEach>
					    </form:select>
					    <span id="rrProgramMsg" class="control-label"></span>
					</div>
				    </spring:bind> 
				    <spring:bind path="rrprogramuploadtypeid">
					<div id="rrprogramuploadtypeDiv" val="${transportDetails.rrprogramuploadtypeid}" class="form-group ${status.error ? 'has-error' : '' }">
					    <label class="control-label" for="rrprogramuploadtypeid">Registry Upload Type *</label>
					    <form:select path="rrprogramuploadtypeid" id="rrprogramuploadtypeid" class="form-control">
						<option value="">- Select -</option>
					    </form:select>
					    <span id="rrprogramuploadtypeMsg" class="control-label"></span>
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
                                    <spring:bind path="mergeBatches">
                                        <div class="form-group" id="mergeBatchesDiv">
                                            <label class="control-label" for="mergeBatches">Merge Batches? <span class="badge badge-help" data-placement="top" title="" data-original-title="If multiple batches found should they be created all in one file?">?</span> *</label>
                                            <div>
                                                <label class="radio-inline">
                                                    <form:radiobutton id="mergeBatches" path="mergeBatches" value="1" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Yes 
                                                </label>
                                                <label class="radio-inline">
                                                    <form:radiobutton id="mergeBatches" path="mergeBatches" value="0" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> No
                                                </label>
                                            </div>
                                            <c:if test="${transportDetails.copiedTransportId > 0}">
                                                <form:hidden path="mergeBatches" />
                                            </c:if>       
                                        </div>
                                    </spring:bind>
                                </c:if>
                                <%-- Source File Upload options only --%>
                                <c:if test="${configurationDetails.type == 1}">
				    <spring:bind path="rrProgramId">
					<div id="rrProgramDiv" class="form-group ${status.error ? 'has-error' : '' }">
					    <label class="control-label" for="rrProgramId">Registry *</label>
					    <form:select path="rrProgramId" id="rrProgramId" class="form-control">
						<option value="">- Select -</option>
						<c:forEach items="${registries}" var="registry">
						    <option value="${registry.id}" <c:if test="${transportDetails.rrProgramId == registry.id}">selected</c:if>>${registry.programName}</option>                                                
						</c:forEach>
					    </form:select>
					    <span id="rrProgramMsg" class="control-label"></span>
					</div>
				    </spring:bind> 
				    <spring:bind path="rrprogramuploadtypeid">
					<div id="rrprogramuploadtypeDiv" val="${transportDetails.rrprogramuploadtypeid}" class="form-group ${status.error ? 'has-error' : '' }">
					    <label class="control-label" for="rrprogramuploadtypeid">Registry Upload Type *</label>
					    <form:select path="rrprogramuploadtypeid" id="rrprogramuploadtypeid" class="form-control">
						<option value="">- Select -</option>
					    </form:select>
					    <span id="rrprogramuploadtypeMsg" class="control-label"></span>
					</div>
				    </spring:bind> 
                                    <spring:bind path="errorHandling">
                                        <div class="form-group">
                                            <label class="control-label" for="errorHandling">Error Handling *</label>
                                            <div>
                                                <label class="radio-inline">
                                                    <form:radiobutton id="errorHandling" path="errorHandling" value="1" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Post errors to ERG 
                                                </label>
                                                <label class="radio-inline">
                                                    <form:radiobutton id="errorHandling" path="errorHandling" value="2" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Reject record on error
                                                </label>
                                                <label class="radio-inline">
                                                    <form:radiobutton id="errorHandling" path="errorHandling" value="3" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Reject submission on error
                                                </label>
                                                <label class="radio-inline">
                                                    <form:radiobutton id="errorHandling" path="errorHandling" value="4" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" /> Pass through errors
                                                </label>
                                            </div>
                                            <c:if test="${transportDetails.copiedTransportId > 0}">
                                                <form:hidden path="errorHandling" />
                                            </c:if>      
                                        </div>
                                    </spring:bind>
				     <spring:bind path="threshold">
                                        <div id="thresholdDiv" class="form-group">
                                            <label class="control-label" for="errorHandling">Error Threshold %*</label>
                                            <form:input path="threshold" id="threshold" class="form-control sm-input" type="text" maxLength="3" disabled="${transportDetails.copiedTransportId > 0 ? 'true' : 'false'}" />
					    <span id="thresholdMsg" class="control-label"></span>
					</div>
                                    </spring:bind>
                                </c:if>
                            </div>
                            <div id="additionalFTPDiv" class="methodDiv" style="display:none">
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

                            <div id="rhapsodyDiv" class="methodDiv" style="display:none">
                                <div id="rhapsodyDanger" class="alert alert-danger" style="display:none;">
                                    Please fill out both paths.
                                </div> 
                                <div class="row">
                                    <c:forEach items="${transportDetails.rhapsodyFields}" var="rhapsodyFields" varStatus="field">
                                        <div class="form-group col-md-6">
                                            <div class="form-group">
                                                <input name="rhapsodyFields[${field.index}].method" class="form-control" type="hidden" value="${rhapsodyFields.method}"  />
                                                <input name="rhapsodyFields[${field.index}].id" id="id${rhapsodyFields.method}" class="form-control" type="hidden" value="${rhapsodyFields.id}"  />
						<c:if test="${rhapsodyFields.method == 2}">
						  <input name="rhapsodyFields[${field.index}].directory" id="directory${rhapsodyFields.method}" class="form-control" type="hidden" maxLength="255" value="${rhapsodyFields.directory}"  />
                                                </c:if>
					    </div>
					    <c:if test="${rhapsodyFields.method == 1}">
						<div id="rDirectory${rhapsodyFields.method}Div" class="form-group">
						    <label class="control-label" for="directory${rhapsodyFields.method}">Location on HIM where HDR will move uploaded files to</label>
						    <input name="rhapsodyFields[${field.index}].directory" id="directory${rhapsodyFields.method}" class="form-control" type="text" maxLength="255" value="${rhapsodyFields.directory}"  />
						    <span id="rDirectory${rhapsodyFields.method}Msg" class="control-label"></span>
						</div>           
					    </c:if>           
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
			    <div id="apiDiv" class="methodDiv" style="display:none">
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
                            <div id="wsDiv" class="methodDiv" style="display:none">
                                <div id="wsDanger" class="alert alert-danger" style="display:none;">
                                    <c:if test="${configurationDetails.type == 1}">
                                        Inbound domain is a required field.
                                    </c:if>
                                    <c:if test="${configurationDetails.type == 2}">
                                        Outbound email and outbound mimeType are required fields.
                                    </c:if>
                                </div> 
                                <div id="configurationDetailsTypeDiv" style="display:none;">
                                    <input name="configurationDetailsType" id="configurationDetailsType" class="form-control" type="hidden" value="${configurationDetails.type}"  />
                                </div>
                                <div class="row">
                                    <c:if test="${configurationDetails.type == 1}">
                                        <c:set var="webServiceFields" value="${transportDetails.webServiceFields[0]}"/>
                                        <div class="form-group col-md-6">
                                            <div class="form-group">
                                                <label for="status">Web Service Inbound Details</label>
                                                <input name="webServiceFields[0].method" class="form-control" type="hidden" value="1"  />
                                                <input name="webServiceFields[0].id" id="id1" class="form-control" type="hidden" value="${webServiceFields.id}"  />
                                            </div>
                                            <c:if test="${webServiceFields.method == 1}">
                                                <div id="wsDomain1Div" class="form-group">
                                                    <label class="control-label" for="domain1">Sender Domain<c:if test="${fn:length(transportDetails.webServiceFields[0].senderDomainList) != 0}">(s)</c:if>*</label>
                                                    <c:if test="${fn:length(transportDetails.webServiceFields[0].senderDomainList) != 0}">
                                                        <a href="#domainModal" data-toggle="modal" rel="${webServiceFields.transportId}" id="addEditDomain" 
                                                           class="addEditDomain" title="Edit domains">
                                                            Add/Edit Domains
                                                        </a>
                                                    </c:if>
                                                    <c:set var="domains" value=""/>
                                                    <c:forEach 
                                                        items="${transportDetails.webServiceFields[0].senderDomainList}" 
                                                        var="domain" begin="0" end="0">
                                                        <c:set var="domainList" value="${domain.domain}"/>
                                                    </c:forEach>
                                                    <c:forEach 
                                                        items="${transportDetails.webServiceFields[0].senderDomainList}" 
                                                        var="domain" begin="1" end="3">
                                                        <c:set var="domainList" value="${domainList}, ${domain.domain}"/>
                                                    </c:forEach>
                                                    <input name="domain1" id="domain1" <c:if test="${fn:length(transportDetails.webServiceFields[0].senderDomainList) != 0}">readOnly</c:if> 
                                                           class="form-control" type="text" maxLength="255" value="${domainList}"/>
                                                    <span id="wsDomain1Msg" class="control-label"></span>
                                                </div> 
                                                <div id="ws1TagNameDiv" class="form-group">
                                                    <label class="control-label" for="tagName1">Tag Name</label>
                                                    <input name="webServiceFields[0].tagName" id="tagName1" class="form-control" type="text" maxLength="255" value="${webServiceFields.tagName}"  />
                                                    <span id="wsTagName1Msg" class="control-label"></span>
                                                </div>
                                                <div id="ws1TagPositonDiv" class="form-group">
                                                    <label class="control-label" for="tagPosition${webServiceFields.method}">Expected Tag Position</label>
                                                    <input name="webServiceFields[0].tagPosition" id="tagPosition1" class="form-control" type="text" maxLength="4" value="${webServiceFields.tagPosition}"  />
                                                    <span id="wsTagPosition1Msg" class="control-label"></span>
                                                </div>
                                                <div id="ws1textInAttachment}Div" class="form-group">
                                                    <label class="control-label" for="textInAttachement${webServiceFields.method}">Expected Text in Attachement</label>
                                                    <input name="webServiceFields[0].textInAttachment" id="textInAttachment1" class="form-control" type="text" maxLength="100" value='<c:out value="${webServiceFields.textInAttachment}" escapeXml="false"/>'/>
                                                    <span id="wsTextInAttachment1Msg" class="control-label"></span>
                                                </div>
                                            </c:if>                                              
                                        </div>
                                    </c:if>
                                    <c:if test="${configurationDetails.type == 2}">
                                        <c:set var="webServiceFields" value="${transportDetails.webServiceFields[0]}"/>
                                        <div class="form-group col-md-6">
                                            <div class="form-group">
                                                <label for="status">Web Service Outbound Details</label>
                                                <input name="webServiceFields[1].method" class="form-control" type="hidden" value="2"  />
                                                <input name="webServiceFields[1].id" id="id2" class="form-control" type="hidden" value="${webServiceFields.id}"  />
                                            </div>
                                            <div id="wsEmail2Div" class="form-group">
                                                <label class="control-label" for="email2">Email *</label>
                                                <input name="webServiceFields[1].email" id="email2" class="form-control" type="text" maxLength="255" value="${webServiceFields.email}"  />
                                                <span id="wsEmail2Msg" class="control-label"></span>
                                            </div> 
                                            <div id="wsMimeType2Div" class="form-group">
                                                <label class="control-label" for="mimeType2">Mime Type *</label>
                                                <input name="webServiceFields[1].mimeType" id="mimeType2" class="form-control" type="text" maxLength="255" value="${webServiceFields.mimeType}"  />
                                                <span id="wsMimeType2Msg" class="control-label"></span>
                                            </div>                           
                                        </div>
                                    </c:if>
				    
                                </div>
                            </div>                            

                        </div>
                    </div>
                </section>

                <div id="messageTypeDanger" class="alert alert-danger" style="display:none;">
                    At least one message type must be selected!
                </div>                 
                <section class="panel panel-default assocMessageTypes" style="display:none;">
                    <div class="panel-heading">
                        <h3 class="panel-title">Associated Message Types</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-container">
                            <table class="table table-striped table-hover table-default">
                                <thead>
                                    <tr>
                                        <th scope="col">Use?</th>
                                        <th scope="col">Configuration Name</th>
                                        <th scope="col">Message Type</th>
                                        <th scope="col" class="center-text">Date Created</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${availConfigurations}" var="configs">
                                        <tr id="configRow" rel="${config.id}" style="cursor: pointer">
                                            <td scope="row">
                                                <form:checkbox class="availMessageTypes" path="messageTypes" value="${configs.getId()}" />
                                            </td>
                                            <td>
                                                ${configs.getconfigName()}
                                            <td>
                                                ${configs.getMessageTypeName()}
                                            </td>
                                            <td class="center-text"><fmt:formatDate value="${configs.dateCreated}" type="date" pattern="M/dd/yyyy" /></td>
                                        </tr>
                                    </c:forEach> 
                                </tbody>
                            </table>

                        </div>  
                    </div>
                </section>    
            </form:form>
        </div>     
    </div>
</div>

<div class="modal fade" id="domainModal" role="dialog" tabindex="-1" aria-labeledby="Add / Edit Domains" aria-hidden="true" aria-describedby="Add / Edit Domains"></div>
