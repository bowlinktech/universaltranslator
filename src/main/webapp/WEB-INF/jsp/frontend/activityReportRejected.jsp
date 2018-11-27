<%-- 
    Document   : activityReportSent
    Created on : Jun 23, 2016, 8:44:53 AM
    Author     : chadmccue
--%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container main-container" role="main">
    <div class="row">
        <div class="col-md-12">
            <ol class="breadcrumb">
                <li><a href="<c:url value='/profile'/>">My Account</a></li>
                <li class="active goBack"><a href="javascript:void(0);">Activity Report</a></li>
                <li class="active">Rejected ${sentSectionName}</li>
            </ol>

            <div class="col-md-12">
                <a href="javascript:void(0);" class="btn btn-primary btn-action-sm goBack pull-right">Return to Activity Log</a>
                <a href="javascript:void(0);" class="btn btn-primary btn-action-sm print pull-right" style="margin-right:10px;">Print</a>
            </div>
        </div>
    </div>
    <div class="row" style="margin-top:20px;">
        <div class="col-md-12">
            <section class="panel panel-default">
                <div class="panel-heading">
                    <h2 class="panel-title">Rejected ${sentSectionName}</h2>
                </div>
                <div class="panel-body">
                    <div id="sentMessages" class="form-container scrollable">
                        <table class="table table-hover table-default" <c:if test="${not empty transactions}">id="dtRejectedMessages"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col" style="width:20%">Receiving Organization</th>
                                    <th scope="col">Message Type</th>
                                    <th scope="col">Patient</th>
                                    <th scope="col" class="center-text">Date Sent</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty transactions}">
                                        <c:forEach var="transaction" items="${transactions}">
                                            <tr>
                                                <td scope="row">
                                                    <c:choose>
                                                        <c:when test="${not empty transaction.targetOrgName}">
                                                            ${transaction.targetOrgName}
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${transaction.targetOrgFields[0].fieldValue}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                   ${transaction.messageTypeName}
                                                </td>
                                                <td style="max-width: 180px;">
                                                    <c:forEach var="patientField" items="${transaction.patientFields}">
                                                        <c:if test="${patientField.saveToTableCol == 'firstName'}">${patientField.fieldValue}</c:if>
                                                        <c:if test="${patientField.saveToTableCol == 'lastName'}">${patientField.fieldValue}<br /></c:if>
                                                        <c:if test="${patientField.saveToTableCol == 'line1'}">
                                                            <dd class="adr">
                                                                ${patientField.fieldValue}
                                                        </c:if>
                                                        <c:if test="${patientField.saveToTableCol == 'line2'}">
                                                            ${patientField.fieldValue}<br />
                                                        </c:if>      
                                                        <c:if test="${patientField.saveToTableCol == 'city'}">
                                                            ${patientField.fieldValue}
                                                        </c:if>  
                                                        <c:if test="${patientField.saveToTableCol == 'state'}">
                                                            ${patientField.fieldValue}
                                                        </c:if> 
                                                        <c:if test="${patientField.saveToTableCol == 'postalCode'}">
                                                            ${patientField.fieldValue}<br />
                                                        </c:if>    
                                                        <c:if test="${patientField.saveToTableCol == 'phone1'}">
                                                            <span class="postal-code">${patientField.fieldValue}</span>
                                                            </dd>
                                                        </c:if>
                                                    </c:forEach>
                                                </td>
                                                <td class="center-text"><fmt:formatDate value="${transaction.dateSubmitted}" type="date" pattern="M/dd/yyyy h:mm a" /></td>
                                                <td class="center-text">
                                                    <c:if test="${transaction.transactionId > 0}">
                                                        <a href="#messageDetailsModal" data-toggle="modal" rel="${transaction.transactionId}" class="btn btn-primary btn-xs viewSent">View Details</a>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                   </c:when>
                                   <c:otherwise>
                                        <tr><td colspan="7" class="center-text">You currently have no sent messages</td></tr>
                                    </c:otherwise>
                              </c:choose>                  
                            </tbody>
                        </table>
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>
<div class="modal fade" id="messageDetailsModal" role="dialog" tabindex="-1" aria-labeledby="Message Details" aria-hidden="true" aria-describedby="Message Details"></div>
