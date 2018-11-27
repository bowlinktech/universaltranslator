<%-- 
    Document   : activityReportSent
    Created on : Jun 23, 2016, 8:44:53 AM
    Author     : chadmccue
--%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container main-container" role="main">
    <div class="row" style="overflow:hidden;">
	<div class="col-md-12">
	    <form:form class="form form-inline" id="searchForm" action="/frontend/upload" method="post">
		<div class="form-group">
		    <input type="hidden" name="fromDate" id="fromDate" rel="<fmt:formatDate value="${fromDate}" type="date" pattern="MM/dd/yyyy" />" rel2="<fmt:formatDate value="${userDetails.dateOrgWasCreated}" type="date" pattern="MM/dd/yyyy" />" value="${fromDate}" />
		    <input type="hidden" name="toDate" id="toDate" rel="<fmt:formatDate value="${toDate}" type="date" pattern="MM/dd/yyyy" />" value="${toDate}" />
		    <input type="hidden" name="batchId" id="batchId" value="" />
		</div>
	    </form:form>
	</div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <ol class="breadcrumb">
                <li><a href="<c:url value='/profile'/>">My Account</a></li>
                <li class="active goBack"><a href="javascript:void(0);">Activity Report</a></li>
                <li class="active">Sent Files</li>
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
                    <h2 class="panel-title" rel="${sentSectionName}">Sent Files  (Sent, Canceled, Errors)</h2>
                </div>
                <div class="panel-body">
                    <div id="sentMessages" class="form-container scrollable">
                        <table class="table table-hover table-default" <c:if test="${not empty transactions}">id="dtSentMessages"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Receiving Organization</th>
                                    <th scope="col">Batch ID</th>
                                    <th scope="col" class="center-text"># of Sent Transactions</th>
				    <th scope="col" class="center-text"># of Error Transactions</th>
                                    <th scope="col" class="center-text">Date Created</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty transactions}">
                                        <c:forEach var="transaction" items="${transactions}">
                                           <tr  style="cursor: pointer">
                                            <td scope="row">
                                                ${transaction.targetOrgName}
                                            </td>
                                            <td>
                                                ${transaction.batchName}
                                                <c:if test="${not empty transaction.fileName}">
                                                    <br />
                                                    <a href="/FileDownload/downloadFile.do?filename=${transaction.fileName}&foldername=archivesIn&orgId=0" title="View Uploaded Source File">Download File Sent</a>
                                                </c:if>
						<br />    
						<a href="javascript:void(0);" rel="${transaction.batchId}" class="viewLink">View Audit Report</a>    
                                            </td>
                                            <td class="center-text">
						<fmt:formatNumber type = "number" value="${transaction.totalRecordCount}" />
                                            </td>
					    <td class="center-text">
						<fmt:formatNumber type = "number" value="${transaction.totalErrorCount}" />
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
<div class="modal fade" id="messageDetailsModal" role="dialog" tabindex="-1" aria-labeledby="Message Details" aria-hidden="true" aria-describedby="Message Details"></div>
<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>
