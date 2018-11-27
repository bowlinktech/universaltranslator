<%-- 
    Document   : auditReports
    Author     : Grace Chan
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container main-container" role="main">
    <div class="row">
        <div class="col-md-12 page-content">        

            <div style="display:none;" class="alert alert-danger" role="alert"></div> 
            <c:set var="alertClass" value="alert alert-danger"/>
            <c:if test="${noErrors}">
                <c:set var="alertClass" value="alert alert-success"/>
            </c:if>
            <c:if test="${not empty batchOptionStatus}"><div class="${alertClass}">${batchOptionStatus}</div></c:if> 


                <div class="row" style="overflow:hidden;">
                    <div class="col-md-12">
                    <form:form class="form form-inline" id="searchForm" action="/Health-e-Connect/auditReports" method="post">
                        <div class="form-group">
                            <input type="hidden" name="fromDate" id="fromDate" rel="<fmt:formatDate value="${fromDate}" type="date" pattern="MM/dd/yyyy" />" rel2="<fmt:formatDate value="${userDetails.dateOrgWasCreated}" type="date" pattern="MM/dd/yyyy" />" value="${fromDate}" />
                            <input type="hidden" name="toDate" id="toDate" rel="<fmt:formatDate value="${toDate}" type="date" pattern="MM/dd/yyyy" />" value="${toDate}" />
                            <input type="hidden" name="batchId" id="batchId" value=""/>
                        </div>
                    </form:form>
                </div>
                </div>
                <div class="form-container scrollable">
                    <div class="date-range-picker-trigger form-control pull-right daterange" style="width:265px; margin-left: 10px;">
                        <i class="glyphicon glyphicon-calendar"></i>
                        <span class="date-label"  rel="" rel2=""><fmt:formatDate value="${fromDate}" type="date" pattern="MMM dd, yyyy" /> - <fmt:formatDate value="${toDate}" type="date" pattern="MMM dd, yyyy" /></span> <b class="caret"></b>
                </div>
                <table class="table table-striped table-hover table-default" <c:if test="${not empty uploadedBatches}">id="dataTable"</c:if>>
                    <caption style="display:none">Audit Reports</caption>
                        <thead>
                            <tr>
                                <th scope="col"><c:if test="${showRelButton == true}">Release?</c:if>&nbsp;</th>
                                <th scope="col">Batch Name</th>
                                <th scope="col">File Name</th>
                                <th scope="col" class="center-text"># of Transactions</th>
                                <th scope="col" class="center-text"># with Errors</th>
                                <th scope="col" class="center-text">System Status</th>
                                <th scope="col" class="center-text">Date Uploaded</th>
                                <th scope="col" class="center-text">View Audit Report</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty uploadedBatches}">
                                <c:forEach var="batch" items="${uploadedBatches}">
                                    <tr>
                                        <td scope="row"  class="center-text">
                                            <c:if test="${batch.statusId == 5 && userDetails.deliverAuthority == true && batch.transTotalNotFinal == 0}">
                                                <input type="checkbox" id="relBatchId" name="relBatchId" class="relBatchId" value="${batch.id}" />
                                            </c:if>
                                        </td>

                                        <td>${batch.utBatchName}</td>
                                        <td>
                                            ${batch.originalFileName}
                                        </td>
                                        <td class="center-text">
					    <fmt:formatNumber type = "number" value="${batch.totalRecordCount}" />
					</td>
                                        <td class="center-text">
					    <fmt:formatNumber type = "number" value="${batch.errorRecordCount}" />
					</td>
                                        <td class="center-text">
                                            <a href="#statusModal" data-toggle="modal" class="btn btn-link viewStatus" rel="${batch.statusId}" title="View this Status">${batch.statusValue}&nbsp;<span class="badge badge-help" data-placement="top" title="" data-original-title="">?</span></a>
                                        </td>
                                        <td class="center-text"><fmt:formatDate value="${batch.dateSubmitted}" type="date" pattern="M/dd/yyyy h:mm:ss a" /></td>
                                        <td class="actions-col center-text" style="width:50px;">
					<c:if test="${batch.statusId != 4}">
                                            <a href="javascript:void(0);" rel="${batch.id}" class="btn btn-link viewLink">
                                                <span class="glyphicon glyphicon-edit"></span>
                                                View
                                            </a> 
					</c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr><td colspan="9" class="center-text">There are currently no audit reports.</td></tr>
                            </c:otherwise>
                        </c:choose>    
                    </tbody>
                </table>
                <div class="row pull-left" style="margin-left: 1px; margin-top: 20px">
              	 <c:if test="${showRelButton == true}">
                <a href="javascript:void(0);" title="Send Batches" class="pull-right btn btn-primary sendBatches"><span class="glyphicon glyphicon-send"></span> Release Marked Batches</a> 
               </c:if>
            </div>
            </div>
        </div>
    </div>
</div>
<form action="releaseBatches" id="releaseBatches" method="post">
    <input type="hidden" id="idList" name="idList" value="" />                        
</form>
<div class="modal fade" id="uploadFile" role="dialog" tabindex="-1" aria-labeledby="Upload New File" aria-hidden="true" aria-describedby="Upload New File"></div>
<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>
