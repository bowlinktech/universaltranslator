<%-- 
    Document   : messageDetails
    Created on : Mar 28, 2014, 11:36:55 AM
    Author     : chadmccue
--%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<div class="modal-dialog">
    <div class="modal-content" style="width:840px;">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Message Details</h3>
        </div>
        <div class="modal-body">
            <div class="container main-container" role="main">
                <div class="row">
                    <div class="col-md-12 page-content">
                        <c:choose>
                            <c:when test="${empty transactionDetails}">
                                This transaction is no longer available.
                            </c:when>
                            <c:otherwise>
                                <div class="form-section row">
                                    <div class="col-md-6" style="width:450px;">
                                        <h4 class="form-section-heading">Originating Organization: </h4>
                                        <dl class="vcard">
                                            <dd class="fn">${transactionDetails.sourceOrgFields[0].fieldValue}</dd>
                                            <dd class="adr">
                                                <span class="street-address">${transactionDetails.sourceOrgFields[1].fieldValue}</span><c:if test="${not empty transactionDetails.sourceOrgFields[2].fieldValue}"><span class="street-address"> ${transactionDetails.sourceOrgFields[2].fieldValue}</span></c:if><br/>
                                                <span class="region">${transactionDetails.sourceOrgFields[3].fieldValue}&nbsp;${transactionDetails.sourceOrgFields[4].fieldValue}</span>, <span class="postal-code">${transactionDetails.sourceOrgFields[5].fieldValue}</span>
                                            </dd>
                                            <c:if test="${not empty transactionDetails.sourceOrgFields[6].fieldValue}"><dd>phone: <span class="tel">${transactionDetails.sourceOrgFields[6].fieldValue}</span></dd></c:if>
                                            <c:if test="${not empty transactionDetails.sourceOrgFields[7].fieldValue}"><dd>fax: <span class="tel">${transactionDetails.sourceOrgFields[7].fieldValue}</span></dd></c:if>
                                            </dl>

                                        <c:if test="${not empty transactionDetails.sourceProviderFields[0].fieldValue}">
                                            <h4 class="form-section-heading">Originating Organization Provider: </h4>
                                            <dl class="vcard">
                                                <dd class="fn">${transactionDetails.sourceProviderFields[0].fieldValue}&nbsp;${transactionDetails.sourceProviderFields[1].fieldValue}</dd>
                                                <dd class="fn">Id: ${transactionDetails.sourceProviderFields[2].fieldValue}</dd>
                                                <dd class="adr">
                                                    <span class="street-address">${transactionDetails.sourceProviderFields[3].fieldValue}</span><br/>
                                                    <c:if test="${not empty transactionDetails.sourceProviderFields[4].fieldValue}"><span class="street-address">${transactionDetails.sourceProviderFields[4].fieldValue}</span><br/></c:if>
                                                    <span class="region">${transactionDetails.sourceProviderFields[5].fieldValue}&nbsp;${transactionDetails.sourceProviderFields[6].fieldValue}</span>, <span class="postal-code">${transactionDetails.sourceProviderFields[7].fieldValue}</span>
                                                </dd>
                                                <c:if test="${not empty transactionDetails.sourceProviderFields[8].fieldValue}"><dd>phone: <span class="tel">${transactionDetails.sourceProviderFields[8].fieldValue}</span></dd></c:if>
                                                <c:if test="${not empty transactionDetails.sourceProviderFields[9].fieldValue}"><dd>fax: <span class="tel">${transactionDetails.sourceProviderFields[9].fieldValue}</span></dd></c:if>
                                                </dl>
                                        </c:if>
                                    </div>
                                    <div class="col-md-6">
                                        <h4 class="form-section-heading">Recipient Organization:</h4>
                                        <dl class="vcard">
                                            <dd class="fn">${transactionDetails.targetOrgFields[0].fieldValue}</dd>
                                            <dd class="adr">
                                                <span class="street-address">${transactionDetails.targetOrgFields[1].fieldValue}</span><c:if test="${not empty transactionDetails.targetOrgFields[2].fieldValue}"><span class="street-address"> ${transactionDetails.targetOrgFields[2].fieldValue}</span></c:if><br/>
                                                <span class="region">${transactionDetails.targetOrgFields[3].fieldValue}&nbsp;${transactionDetails.targetOrgFields[4].fieldValue}</span>, <span class="postal-code">${transactionDetails.targetOrgFields[5].fieldValue}</span>
                                            </dd>
                                            <c:if test="${not empty transactionDetails.targetOrgFields[6].fieldValue}"><dd>phone: <span class="tel">${transactionDetails.targetOrgFields[6].fieldValue}</span></dd></c:if>
                                            <c:if test="${not empty transactionDetails.targetOrgFields[7].fieldValue}"><dd>fax: <span class="tel">${transactionDetails.targetOrgFields[7].fieldValue}</span></dd></c:if>
                                            </dl>

                                        <c:if test="${not empty transactionDetails.targetProviderFields[0].fieldValue}">
                                            <h4 class="form-section-heading">Recipient Organization Provider: </h4>
                                            <dl class="vcard">
                                                <dd class="fn">${transactionDetails.targetProviderFields[0].fieldValue}&nbsp;${transactionDetails.targetProviderFields[1].fieldValue}</dd>
                                                <dd class="fn">Id: ${transactionDetails.targetProviderFields[2].fieldValue}</dd>
                                                <dd class="adr">
                                                    <span class="street-address">${transactionDetails.targetProviderFields[3].fieldValue}</span><br/>
                                                    <c:if test="${not empty transactionDetails.targetProviderFields[4].fieldValue}"><span class="street-address">${transactionDetails.targetProviderFields[4].fieldValue}</span><br/></c:if>
                                                    <span class="region">${transactionDetails.targetProviderFields[5].fieldValue}&nbsp;${transactionDetails.targetProviderFields[6].fieldValue}</span>, <span class="postal-code">${transactionDetails.targetProviderFields[7].fieldValue}</span>
                                                </dd>
                                                <c:if test="${not empty transactionDetails.targetProviderFields[8].fieldValue}"><dd>phone: <span class="tel">${transactionDetails.targetProviderFields[8].fieldValue}</span></dd></c:if>
                                                <c:if test="${not empty transactionDetails.targetProviderFields[9].fieldValue}"><dd>fax: <span class="tel">${transactionDetails.targetProviderFields[9].fieldValue}</span></dd></c:if>
                                                </dl>
                                        </c:if>
                                    </div>
                                </div>

                                <div class="form-section row">
                                    <div class="col-md-12"><h4 class="form-section-heading">Patient Information:</h4></div>
                                    <c:forEach items="${transactionDetails.patientFields}" var="patientInfo" varStatus="pfield">
                                        <div class="col-md-6" style="width:400px;">
                                            <div id="fieldDiv_${patientInfo.fieldNo}" class="form-group">
                                                <label class="control-label" for="${patientInfo.fieldNo}">${patientInfo.fieldLabel}</label>
                                                <c:choose>
                                                    <c:when test="${patientInfo.fieldSelectOptions.size() > 0}">
                                                        <select disabled id="${patientInfo.fieldNo}" style="width:350px;" name="patientFields[${pfield.index}].fieldValue" class="form-control <c:if test="${patientInfo.required == true}"> required</c:if>">
                                                                <option value="">-Choose-</option>
                                                            <c:forEach items="${patientInfo.fieldSelectOptions}" var="options">
                                                                <option value="${options.optionValue}" <c:if test="${patientInfo.fieldValue == options.optionValue}">selected</c:if>>${options.optionDesc}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input id="${patientInfo.fieldNo}" style="width:350px;" disabled name="patientFields[${pfield.index}].fieldValue" value="${patientInfo.fieldValue}" class="form-control ${patientInfo.validation.replace(' ','-')} <c:if test="${patientInfo.required == true}"> required</c:if>" type="text">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>

                                <div class="form-section row">
                                    <div class="col-md-12"><h4 class="form-section-heading">Message Details: </h4></div>
                                    <c:forEach items="${transactionDetails.detailFields}" var="detailInfo" varStatus="dfield">
                                        <div class="col-md-6" style="width:400px;">
                                            <div id="fieldDiv_${detailInfo.fieldNo}" class="form-group">
                                                <label class="control-label" for="fieldA">${detailInfo.fieldLabel}</label>
                                                <c:choose>
                                                    <c:when test="${detailInfo.fieldSelectOptions.size() > 0}">
                                                        <select disabled id="${detailInfo.fieldNo}" style="width:350px;" name="detailFields[${dfield.index}].fieldValue" class="form-control <c:if test="${detailInfo.required == true}"> required</c:if>">
                                                                <option value="">-Choose-</option>
                                                            <c:forEach items="${detailInfo.fieldSelectOptions}" var="options">
                                                                <option value="${options.optionValue}" <c:if test="${detailInfo.fieldValue == options.optionValue}">selected</c:if>>${options.optionDesc}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input disabled id="${detailInfo.fieldNo}" style="width:350px;" name="detailFields[${dfield.index}].fieldValue" value="${detailInfo.fieldValue}" class="form-control ${detailInfo.validation.replace(' ','-')} <c:if test="${detailInfo.required == true}"> required</c:if>" type="text">
                                                    </c:otherwise>
                                                </c:choose>               
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>

                    </div>

                </div>

            </div>
        </div>
    </div>
</div>

