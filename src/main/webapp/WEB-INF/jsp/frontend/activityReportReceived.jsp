<%-- 
    Document   : activityReportReceived
    Created on : Jun 23, 2016, 8:45:08 AM
    Author     : chadmccue
--%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container main-container" role="main">
    <div class="row">
        <div class="col-md-12">
            <ol class="breadcrumb">
                <li><a href="<c:url value='/profile'/>">My Account</a></li>
                <li class="active goBack"><a href="javascript:void(0);">Activity Report</a></li>
                <li class="active">Received ${receivedSectionname}</li>
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
                    <h2 class="panel-title" rel="${receivedSectionname}">Received Files</h2>
                </div>
                <div class="panel-body">
                    <div id="sentMessages" class="form-container scrollable">
                        <table class="table table-hover table-default" <c:if test="${not empty transactions}">id="dtReceivedMessages"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Sending Organization</th>
                                    <th scope="col">Batch ID</th>
                                    <th scope="col">Originating File</th>
                                    <th scope="col" class="center-text"># of Transactions</th>
                                    <th scope="col" class="center-text">Date Created</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty transactions}">
                                        <c:forEach var="transaction" items="${transactions}">
                                           <tr  style="cursor: pointer">
                                            <td scope="row">
                                                ${transaction.srcOrgName}
                                            </td>
                                            <td>
						<strong>${transaction.messageTypeName}</strong><br />
                                                ${transaction.batchName}
						<br />
						<a href="/FileDownload/downloadFile.do?filename=${transaction.fileName}&foldername=archivesOut&orgId=0" title="View Original File">
						    Download Outbound File
						</a>
                                            </td>
                                            <td>
                                                ${transaction.frombatchName}
                                                <c:if test="${not empty transaction.originalFileName}">
                                                    <br />
                                                    <a href="/FileDownload/downloadFile.do?filename=${transaction.originalFileName}&foldername=archivesIn&orgId=0" title="View Uploaded Source File">
                                                        Download Inbound File
                                                    </a>
                                                </c:if>
                                            </td>
                                            <td class="center-text">
						<fmt:formatNumber type = "number" value="${transaction.totalRecordCount}" />
                                            </td>
                                            <td class="center-text"><fmt:formatDate value="${transaction.dateSubmitted}" type="both" pattern="M/dd/yyyy h:mm:ss a" /></td>
                                            <%--<td class="actions-col">
                                                <a href="<c:url value='/administrator/processing-activity/outbound/batch/${batch.utBatchName}' />" class="btn btn-link viewTransactions" title="View Batch Transactions" role="button">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    View Transactions
                                                </a>
                                            </td>--%>
                                        </tr>
                                        </c:forEach>
                                   </c:when>
                                   <c:otherwise>
                                        <tr><td colspan="7" class="center-text">You currently have no received messages</td></tr>
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