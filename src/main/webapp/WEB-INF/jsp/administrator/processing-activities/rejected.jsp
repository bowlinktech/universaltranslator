<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main">
    <div class="col-md-12">
        <section class="panel panel-default">
            <div class="panel-body">
                <div class="table-actions">
                    <div class="col-md-12" role="search">
                        <form:form class="form form-inline" id="searchForm" action="/administrator/processing-activity/rejected" method="post">
                            <div class="form-group">
                                <input type="hidden" name="fromDate" id="fromDate" rel="<fmt:formatDate value="${fromDate}" type="date" pattern="MM/dd/yyyy" />" rel2="<fmt:formatDate value="${originalDate}" type="date" pattern="MM/dd/yyyy" />" value="${fromDate}" />
                                <input type="hidden" name="toDate" id="toDate" rel="<fmt:formatDate value="${toDate}" type="date" pattern="MM/dd/yyyy" />" value="${toDate}" />
                                <input type="hidden" name="page" id="page" value="${currentPage}" />
                            </div>
                        </form:form>
                    </div>
                </div>
                <c:if test="${not empty toomany}">
                    <div>
                        <b>There are over 200 batches found.  The first 200 results are displayed.  Please refine your results using the date search box.</b>
                        <br/><br/>
                    </div>
                </c:if>

                <div class="form-container scrollable">
                    <div class="date-range-picker-trigger form-control pull-right daterange" style="width:285px; margin-left: 10px;">
                        <i class="glyphicon glyphicon-calendar"></i>
                        <span class="date-label"><fmt:formatDate value="${fromDate}" type="date" pattern="MMMM dd, yyyy" /> - <fmt:formatDate value="${toDate}" type="date" pattern="MMMM dd, yyyy" /></span> <b class="caret"></b>
                    </div>
                    <table class="table table-striped table-hover table-default"  <c:if test="${not empty batches}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Sending Organization</th>
                                    <th scope="col" style="width:50px">Batch ID</th>
                                    <th scope="col" class="center-text">Transport</th>
                                    <th scope="col" class="center-text">Status</th>
                                    <th scope="col">Transactions</th>
                                    <th scope="col" class="center-text">Date Created</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty batches}">
                                    <c:forEach var="batch" items="${batches}">
                                        <tr  style="cursor: pointer">
                                            <td scope="row">
                                                ${batch.orgName}
                                                <br />User: ${batch.usersName}
                                            </td>
                                            <td>
						<strong>${batch.configName}</strong><br />
						<a href="<c:url value='/administrator/processing-activity/inbound/${batch.utBatchName}' />" title="View Inbound Batch" role="button">${batch.utBatchName}</a>
                                                <c:if test="${batch.transportMethodId != 2}">
                                                    <c:set var="text" value="${fn:split(batch.originalFileName,'.')}" />
                                                    <c:set var="ext" value="${text[fn:length(text)-1]}" />
                                                    <br />
                                                    <c:set var="hrefLink" value="/FileDownload/downloadFile.do?filename=${batch.utBatchName}.${ext}&foldername=archivesIn"/>

                                                    <c:if test="${batch.transportMethodId  == 6}">
                                                        <c:set var="hrefLink" value="/FileDownload/downloadFile.do?filename=${batch.utBatchName}_dec.${ext}&foldername=archivesIn"/>
                                                    </c:if>

                                                    <a href="${hrefLink}" title="View Original File">
                                                        ${batch.originalFileName}
                                                    </a>
                                                </c:if>
                                            </td>
                                            <td class="center-text">
                                                ${batch.transportMethod}
                                            </td>
                                            <td class="center-text">
                                                <a href="#statusModal" data-toggle="modal" class="viewStatus" rel="${batch.statusId}" title="View this Status">${batch.statusValue}</a>
                                            </td>
                                            <td>
                                                Total Transactions: <strong><fmt:formatNumber value = "${batch.totalRecordCount}" type = "number"/></strong>
						<br />
						Total Error Transactions: <strong><fmt:formatNumber value = "${batch.errorRecordCount}" type = "number"/></strong>
                                            </td>
                                            <td class="center-text"><fmt:formatDate value="${batch.dateSubmitted}" type="both" pattern="M/dd/yyyy h:mm:ss a" /></td>
                                            <td>
						<div class="dropdown pull-left">
						    <button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
							<i class="fa fa-cog"></i>
						    </button>
						    <ul class="dropdown-menu pull-right">
							<c:if test="${batch.transportMethodId != 2}">
							    <li>
								<a href="<c:url value='/administrator/processing-activity/inbound/batchActivities/${batch.utBatchName}'/>" class="viewBatchActivities" title="View Batch Activities">
								    <span class="glyphicon glyphicon-edit"></span>
								    View Batch Activities
								</a>
							    </li>
							    <li class="divider"></li>
							    <li>
								<a href="<c:url value='/administrator/processing-activity/inbound/auditReport/${batch.utBatchName}' />" title="View Audit Report">
								    <span class="glyphicon glyphicon-edit"></span>
								    View Audit Report
								</a>
							    </li>
							</c:if>
						    </ul>
						</div>
                                            </td>
                                        </tr>
                                    </c:forEach>     
                                </c:when>   
                                <c:otherwise>
                                    <tr><td colspan="7" class="center-text">There are currently no submitted batches with rejected transactions.</td></tr>
                                </c:otherwise>
                            </c:choose>           
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>