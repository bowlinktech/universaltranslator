<%-- 
    Document   : messageDetailsForm
    Created on : Dec 12, 2013, 2:47:34 PM
    Author     : chadmccue
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:useBean id="date" class="java.util.Date" />
<fmt:formatDate value="${date}" pattern="yyyy" var="currentYear" />

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <div id="translationMsgDiv"  class="alert alert-danger" style="display:none;">
                <strong>Error!</strong> 
                An error has occurred in one of the below fields!
            </div>
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                    <dt>Batch Summary:</dt>
                    <dd><strong>Batch ID:</strong> <a href="inbound/auditReport/${batchDetails.utBatchName}" id="batchLink" rel="${batchDetails.utBatchName}">${batchDetails.utBatchName}</a></dd>
                    <dd><strong>Source Organization:</strong> ${batchDetails.orgName}</dd>
                    <dd><strong>Date Submitted:</strong> <fmt:formatDate value="${batchDetails.dateSubmitted}" type="date" pattern="M/dd/yyyy" />&nbsp;&nbsp;<fmt:formatDate value="${batchDetails.dateSubmitted}" type="time" pattern="h:mm:ss a" /></dd>
                    <dd><strong>Total Transactions:</strong> ${batchDetails.totalRecordCount}</dd>
                    <dd><strong>Transactions with errors:</strong> ${batchDetails.errorRecordCount}</dd>
                    <dd><strong>Batch Status:</strong><a href="#statusModal" data-toggle="modal" class="btn btn-link viewStatus" rel="${batchDetails.statusId}" title="View this Status">${batchDetails.statusValue}</a></dd>
                    </dt>
                </div>
            </section>
        </div>
    </div>
    <div class="col-md-12">
        <div class="form-container scrollable">
            <form:form id="messageForm" action="editMessage" modelAttribute="transaction" role="form" class="form" method="post">
                <form:hidden path="transactionId"  />
                <form:hidden path="batchId"  />
                <form:hidden path="configId" />
                <c:forEach items="${transaction.sourceOrgFields}" varStatus="i">
                    <form:hidden path="sourceOrgFields[${i.index}].fieldValue" />
                    <form:hidden path="sourceOrgFields[${i.index}].fieldNo" />
                </c:forEach>
                <c:forEach items="${transaction.targetOrgFields}" varStatus="t">
                    <form:hidden path="targetOrgFields[${t.index}].fieldValue" />
                    <form:hidden path="targetOrgFields[${t.index}].fieldNo" />
                </c:forEach>
                <div class="panel-group form-accordion" id="accordion">

                    <div class="panel panel-default panel-form">
                        <div id="collapseThree" class="panel-collapse collapse in">
                            <div class="panel-body">
                                <div class="form-section row">
                                    <div class="col-md-6">
                                        <h4 class="form-section-heading">Originating Organization: </h4>
                                        <dl class="vcard">
                                            <dd class="fn">${transaction.sourceOrgFields[0].fieldValue}</dd>
                                            <dd class="adr">
                                                <span class="street-address">${transaction.sourceOrgFields[1].fieldValue}</span><c:if test="${not empty transaction.sourceOrgFields[2].fieldValue}"><span class="street-address">&nbsp;${transaction.sourceOrgFields[2].fieldValue}</span></c:if><br/>
                                                <span class="region">${transaction.sourceOrgFields[3].fieldValue}&nbsp;${transaction.sourceOrgFields[4].fieldValue}</span>, <span class="postal-code">${transaction.sourceOrgFields[5].fieldValue}</span>
                                            </dd>
                                            <c:if test="${not empty transaction.sourceOrgFields[6].fieldValue}"><dd>phone: <span class="tel">${transaction.sourceOrgFields[6].fieldValue}</span></dd></c:if>
                                            <c:if test="${not empty transaction.sourceOrgFields[7].fieldValue}"><dd>fax: <span class="tel">${transaction.sourceOrgFields[7].fieldValue}</span></dd></c:if>
                                            </dl>
                                        <c:choose>
                                            <c:when test="${not empty transaction.sourceProviderFields[0].fieldValue}">
                                                <h4 class="form-section-heading">Originating Organization Provider: </h4>
                                                <dl class="vcard">
                                                    <dd class="fn">${transaction.sourceProviderFields[0].fieldValue}&nbsp;${transaction.sourceProviderFields[1].fieldValue}</dd>
                                                    <dd class="fn">Id: ${transaction.sourceProviderFields[2].fieldValue}</dd>
                                                    <dd class="adr">
                                                        <span class="street-address">${transaction.sourceProviderFields[3].fieldValue}</span><br/>
                                                        <c:if test="${not empty transaction.sourceProviderFields[4].fieldValue}"><span class="street-address">${transaction.sourceProviderFields[4].fieldValue}</span><br/></c:if>
                                                        <span class="region">${transaction.sourceProviderFields[5].fieldValue}&nbsp;${transaction.sourceProviderFields[6].fieldValue}</span>, <span class="postal-code">${transaction.sourceProviderFields[7].fieldValue}</span>
                                                    </dd>
                                                    <c:if test="${not empty transaction.sourceProviderFields[8].fieldValue}"><dd>phone: <span class="tel">${transaction.sourceProviderFields[8].fieldValue}</span></dd></c:if>
                                                    <c:if test="${not empty transaction.sourceProviderFields[9].fieldValue}"><dd>fax: <span class="tel">${transaction.sourceProviderFields[9].fieldValue}</span></dd></c:if>
                                                    </dl>
                                            </c:when>
                                            <c:when test="${not empty providers}">
                                                <dl id="fromOrgProviderChoose">
                                                    <dd>
                                                        Choose Provider: 
                                                        <select class="form-control" id="orgProvider">
                                                            <option value="">-Choose-</option>
                                                            <c:forEach items="${providers}" var="provider">
                                                                <option value="${provider.id}">${provider.firstName}&nbsp;${provider.lastName}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </dd>
                                                </dl>
                                            </c:when>
                                        </c:choose>
                                        <c:forEach items="${transaction.sourceProviderFields}" var="providerInfo" varStatus="sp">
                                            <form:hidden id="provider_${providerInfo.fieldNo}" path="sourceProviderFields[${sp.index}].fieldValue" value="" />
                                            <form:hidden path="sourceProviderFields[${sp.index}].fieldNo" />
                                        </c:forEach>
                                        <div id="fromorgProvider" style="display:none">
                                            <h4 class="form-section-heading">Originating Organization Provider:  - <a href="javascript:void(0)" title="Change selected provider." id="fromOrgProviderChange">Change</a></h4>
                                            <dl class="vcard">
                                                <dd class="fn" id="fromOrgProviderName"></dd>
                                                <dd class="adr">
                                                    <span class="street-address" id="fromOrgProviderLine1"></span>
                                                    <span class="street-address" id="fromOrgProviderLine2"></span>
                                                    <span class="region" id="fromOrgProviderRegion"></span><span class="postal-code" id="fromOrgProviderZip"></span>
                                                </dd>
                                                <dd id="fromOrgProviderPhone"></dd>
                                                <dd id="fromOrgProviderFax"></dd>
                                            </dl>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <h4 class="form-section-heading">Recipient Organization:</h4>
                                        <dl class="vcard">
                                            <dd class="fn">${transaction.targetOrgFields[0].fieldValue}</dd>
                                            <dd class="adr">
                                                <span class="street-address">${transaction.targetOrgFields[1].fieldValue}<c:if test="${not empty transaction.targetOrgFields[2].fieldValue}"><span class="street-address"> ${transaction.targetOrgFields[2].fieldValue}</span></c:if></span><br/>
                                                <span class="region">${transaction.targetOrgFields[3].fieldValue}&nbsp;${transaction.targetOrgFields[4].fieldValue}</span>, <span class="postal-code">${transaction.targetOrgFields[5].fieldValue}</span>
                                            </dd>
                                            <c:if test="${not empty transaction.targetOrgFields[6].fieldValue}"><dd>phone: <span class="tel">${transaction.targetOrgFields[6].fieldValue}</span></dd></c:if>
                                            <c:if test="${not empty transaction.targetOrgFields[7].fieldValue}"><dd>fax: <span class="tel">${transaction.targetOrgFields[7].fieldValue}</span></dd></c:if>
                                            </dl>
                                        <c:if test="${pageHeader == 'feedback' || transaction.sourceType == 2}">
                                            <c:choose>
                                                <c:when test="${not empty transaction.targetProviderFields[0].fieldValue}">
                                                    <h4 class="form-section-heading">Recipient Organization Provider: </h4>
                                                    <dl class="vcard">
                                                        <dd class="fn">${transaction.targetProviderFields[0].fieldValue}&nbsp;${transaction.targetProviderFields[1].fieldValue}</dd>
                                                        <dd class="fn">Id: ${transaction.targetProviderFields[2].fieldValue}</dd>
                                                        <dd class="adr">
                                                            <span class="street-address">${transaction.targetProviderFields[3].fieldValue}</span><br/>
                                                            <c:if test="${not empty transaction.targetProviderFields[4].fieldValue}"><span class="street-address">${transaction.targetProviderFields[4].fieldValue}</span><br/></c:if>
                                                            <span class="region">${transaction.targetProviderFields[5].fieldValue}&nbsp;${transaction.targetProviderFields[6].fieldValue}</span>, <span class="postal-code">${transaction.targetProviderFields[7].fieldValue}</span>
                                                        </dd>
                                                        <c:if test="${not empty transaction.targetProviderFields[8].fieldValue}"><dd>phone: <span class="tel">${transaction.targetProviderFields[8].fieldValue}</span></dd></c:if>
                                                        <c:if test="${not empty transaction.targetProviderFields[9].fieldValue}"><dd>fax: <span class="tel">${transaction.targetProviderFields[9].fieldValue}</span></dd></c:if>
                                                        </dl>
                                                </c:when>
                                                <c:when test="${not empty providers}">
                                                    <dl id="fromOrgProviderChoose">
                                                        <dd>
                                                            Choose Provider: 
                                                            <select class="form-control" id="targetorgProvider">
                                                                <option value="">-Choose-</option>
                                                                <c:forEach items="${providers}" var="provider">
                                                                    <option value="${provider.id}">${provider.firstName}&nbsp;${provider.lastName}</option>
                                                                </c:forEach>
                                                            </select>
                                                        </dd>
                                                    </dl>
                                                </c:when>
                                            </c:choose>
                                        </c:if>
                                        <c:forEach items="${transaction.targetProviderFields}" var="tarproviderInfo" varStatus="tp">
                                            <form:hidden id="tgtprovider_${tarproviderInfo.fieldNo}" path="targetProviderFields[${tp.index}].fieldValue" value="" />
                                            <form:hidden path="targetProviderFields[${tp.index}].fieldNo" />
                                        </c:forEach>
                                        <div id="toorgProvider" style="display:none">
                                            <h4 class="form-section-heading">Recipient Organization Provider:  - <a href="javascript:void(0)" title="Change selected provider." id="toOrgProviderChange">Change</a></h4>
                                            <dl class="vcard">
                                                <dd class="fn" id="toOrgProviderName"></dd>
                                                <dd class="adr">
                                                    <span class="street-address" id="toOrgProviderLine1"></span>
                                                    <span class="street-address" id="toOrgProviderLine2"></span>
                                                    <span class="region" id="toOrgProviderRegion"></span><span class="postal-code" id="toOrgProviderZip"></span>
                                                </dd>
                                                <dd id="toOrgProviderPhone"></dd>
                                                <dd id="toOrgProviderFax"></dd>
                                            </dl>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-section row">
                                    <div class="col-md-12"><h4 class="form-section-heading">Patient Information:</h4></div>
                                    <c:forEach items="${transaction.patientFields}" var="patientInfo" varStatus="pfield">
                                        <input type="hidden" name="patientFields[${pfield.index}].fieldNo" value="${patientInfo.fieldNo}" />
                                        <c:if test="${patientInfo.readOnly == true}">
                                            <input type="hidden" name="patientFields[${pfield.index}].fieldValue" value="${patientInfo.fieldValue}" />
                                        </c:if>
                                        <div class="col-md-6 ${(pfield.index mod 2) == 0 ? 'cb' : ''}">
                                            <div id="fieldDiv_${patientInfo.fieldNo}" class="form-group ${patientInfo.errorDesc != '' && patientInfo.errorDesc != null ? 'has-error' : '' }">
                                                <label class="control-label" for="${patientInfo.fieldNo}">
                                                    ${patientInfo.fieldLabel}&nbsp;<c:if test="${patientInfo.required == true}">*&nbsp;</c:if></label>
                                                    <c:choose>
                                                        <c:when test="${patientInfo.fieldSelectOptions.size() > 0}">
                                                        <select id="${patientInfo.fieldNo}" name="patientFields[${pfield.index}].fieldValue" class="form-control <c:if test="${patientInfo.required == true}"> required</c:if>">
                                                                <option value="">-Choose-</option>
                                                            <c:forEach items="${patientInfo.fieldSelectOptions}" var="options">
                                                                <option value="${options.optionValue}" <c:if test="${patientInfo.fieldValue == options.optionValue}">selected</c:if>>${options.optionDesc}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input id="${patientInfo.fieldNo}" name="patientFields[${pfield.index}].fieldValue" value="${patientInfo.fieldValue}" class="form-control ${patientInfo.validation.replace(' ','-')} <c:if test="${patientInfo.required == true}"> required</c:if>" type="text">
                                                    </c:otherwise>
                                                </c:choose>
                                                <span id="errorMsg_${patientInfo.fieldNo}" class="control-label">
                                                    <c:if test="${patientInfo.errorDesc != ''}">
                                                        ${patientInfo.errorDesc}
                                                    </c:if>
                                                </span>  
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>

                                <div class="form-section row">
                                    <div class="col-md-12"><h4 class="form-section-heading">Message Details: </h4></div>
                                    <c:forEach items="${transaction.detailFields}" var="detailInfo" varStatus="dfield">
                                        <input type="hidden" name="detailFields[${dfield.index}].fieldNo" value="${detailInfo.fieldNo}" />
                                        <div class="col-md-6 ${(dfield.index mod 2) == 0 ? 'cb' : ''}">
                                            <div id="fieldDiv_${detailInfo.fieldNo}" class="form-group">
                                                <label class="control-label" for="fieldA">${detailInfo.fieldLabel} <c:if test="${detailInfo.required == true}">&nbsp;*</c:if></label>
                                                <c:choose>
                                                    <c:when test="${detailInfo.fieldSelectOptions.size() > 0}">
                                                        <select id="${detailInfo.fieldNo}" name="detailFields[${dfield.index}].fieldValue" class="form-control <c:if test="${detailInfo.required == true}"> required</c:if>">
                                                                <option value="">-Choose-</option>
                                                            <c:forEach items="${detailInfo.fieldSelectOptions}" var="options">
                                                                <option value="${options.optionValue}" <c:if test="${detailInfo.fieldValue == options.optionValue}">selected</c:if>>${options.optionDesc}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${detailInfo.readOnly == true}">
                                                                <input value="${detailInfo.fieldValue}" class="form-control ${detailInfo.validation.replace(' ','-')} type="text">
                                                                       <input type="hidden" id="${detailInfo.fieldNo}" name="detailFields[${dfield.index}].fieldValue" value="${detailInfo.fieldValue}" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <input id="${detailInfo.fieldNo}" name="detailFields[${dfield.index}].fieldValue" value="${detailInfo.fieldValue}" class="form-control ${detailInfo.validation.replace(' ','-')} <c:if test="${detailInfo.required == true}"> required</c:if>" type="text">
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:otherwise>
                                                </c:choose>
                                                <span id="errorMsg_${detailInfo.fieldNo}" class="control-label"></span>                
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>
<%-- Status Definition modal --%>
<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>