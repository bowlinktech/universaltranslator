<%-- 
    Document   : download
    Created on : Feb 10, 2:28:43 PM
    Author     : chadmccue
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container main-container" role="main">
    <div class="row">

        <div class="col-md-12 page-content">

            <div class="row" style="overflow:hidden;">

                <div class="col-md-3">
                    <form:form class="form form-inline" id="searchForm" action="/frontend/download" method="post">
                        <div class="form-group">
                            <input type="hidden" name="fromDate" id="fromDate" rel="<fmt:formatDate value="${fromDate}" type="date" pattern="MM/dd/yyyy" />" rel2="<fmt:formatDate value="${userDetails.dateOrgWasCreated}" type="date" pattern="MM/dd/yyyy" />" value="${fromDate}" />
                            <input type="hidden" name="toDate" id="toDate" rel="<fmt:formatDate value="${toDate}" type="date" pattern="MM/dd/yyyy" />" value="${toDate}" />
                        </div>
                    </form:form>
                </div>

            </div>

            <div class="form-container scrollable">
                <div class="date-range-picker-trigger form-control pull-right daterange" style="width:265px; margin-left: 10px;">
                    <i class="glyphicon glyphicon-calendar"></i>
                    <span class="date-label"  rel="" rel2=""><fmt:formatDate value="${fromDate}" type="date" pattern="MMM dd, yyyy" /> - <fmt:formatDate value="${toDate}" type="date" pattern="MMM dd, yyyy" /></span> <b class="caret"></b>
                </div>
                <table class="table table-striped table-hover table-default" <c:if test="${not empty downloadableBatches}">id="dataTable"</c:if>>
                    <caption style="display:none">Downloaded Files</caption>
                    <thead>
                        <tr>
                            <th scope="col">Batch Name</th>
                            <th scope="col">File Name</th>
                            <th scope="col" class="center-text">System Status</th>
                            <th scope="col" class="center-text">Date Created</th>
                            <th scope="col" class="center-text">Last Downloaded</th>
                            <th scope="col"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty downloadableBatches}">
                                <c:forEach var="batch" items="${downloadableBatches}">
                                    <tr>
                                        <td scope="row">${batch.utBatchName}</td>
                                        <td>
                                            ${batch.outputFIleName}
                                        </td>
                                        <td class="center-text" id="statusDiv${batch.id}">
                                            <a href="#statusModal" data-toggle="modal" class="btn btn-link viewStatus" rel="${batch.statusId}" title="View this Status">${batch.statusValue}&nbsp;<span class="badge badge-help" data-placement="top" title="" data-original-title="">?</span></a>
                                        </td>
                                        <td class="center-text"><fmt:formatDate value="${batch.dateCreated}" type="date" pattern="M/dd/yyyy" /><br /><fmt:formatDate value="${batch.dateCreated}" type="time" pattern="h:mm:ss a" /></td>
                                        <td class="center-text"  id="lastDownloadDiv${batch.id}"><fmt:formatDate value="${batch.lastDownloaded}" type="date" pattern="M/dd/yyyy h:mm:ss a" /></td>
                                        <td class="actions-col" style="width:100px;">
                                            <a href="javascript:void(0);" rel="${batch.id}" rel1="${batch.outputFIleName}" rel2="${batch.orgId}" class="downloadFile" title="Download">
                                                <span class="glyphicon glyphicon-edit"></span>
                                                Download	
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr><td colspan="6" class="center-text">There are currently no batches ready to be downloaded</td></tr>
                            </c:otherwise>
                        </c:choose>    
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>