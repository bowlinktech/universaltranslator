<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                    <dt>System Summary:</dt>
                    <dd><strong>Batches Received in the Past Hour:</strong> <fmt:formatNumber value="${summaryDetails.batchesPastHour}" /></dd>
                    <dd><strong>Batches Received in today:</strong> <fmt:formatNumber value="${summaryDetails.batchesToday}" /></dd>
                    <dd><strong>Batches Received in This Week:</strong> <fmt:formatNumber value="${summaryDetails.batchesThisWeek}" /></dd>
                    <dd><strong>Total Batches in Error:</strong> <fmt:formatNumber value="${summaryDetails.batchesInError}" /></dd>
                    </dt>
                </div>
            </section>
        </div>
    </div>
    <div class="col-md-12">
        <section class="panel panel-default">
            <div class="panel-body">
                <div class="table-actions">
                    <div class="col-md-12" role="search">
                        <form:form class="form form-inline" id="searchForm" action="/administrator/processing-activity/invalidIn" method="post">
                            <div class="form-group">
                                <input type="hidden" name="fromDate" id="fromDate" rel="<fmt:formatDate value="${fromDate}" type="date" pattern="MM/dd/yyyy" />" rel2="<fmt:formatDate value="${originalDate}" type="date" pattern="MM/dd/yyyy" />" value="${fromDate}" />
                                <input type="hidden" name="toDate" id="toDate" rel="<fmt:formatDate value="${toDate}" type="date" pattern="MM/dd/yyyy" />" value="${toDate}" />
                                <input type="hidden" name="page" id="page" value="${currentPage}" />
                                <input type="hidden" name="DTS" id="DTS" value="${DTS}" />
                            </div>
                        </form:form>
                    </div>
                </div>
                <c:if test="${not empty toomany}">
                    <div>
                        <b>There are over 200 web service messages found.  The first 200 results are displayed.  Please refine your results using the date search box.</b>
                        <br/><br/>
                    </div>
                </c:if>
                <c:if test="${not empty error}" >
                    <div class="alert alert-danger" role="alert">
                        The selected file was not found.
                    </div>
                </c:if>
                <div class="form-container scrollable">
                    <div class="date-range-picker-trigger form-control pull-right daterange" style="width:285px; margin-left: 10px;">
                        <i class="glyphicon glyphicon-calendar"></i>
                        <span class="date-label"><fmt:formatDate value="${fromDate}" type="date" pattern="MMMM dd, yyyy" /> - <fmt:formatDate value="${toDate}" type="date" pattern="MMMM dd, yyyy" /></span> <b class="caret"></b>
                    </div>
                        <table class="table table-striped table-hover table-default" <c:if test="${not empty batches}">id="invalidInbound-table"</c:if> term="${searchFilter}">
                            <thead>
                                <tr>
                                    <th scope="col">Organization</th>
                                    <th scope="col">Batch Details</th>
                                    <th scope="col" class="center-text">Transport Method</th>
                                    <th scope="col" class="center-text">Status</th>
                                    <th scope="col"># of Transactions</th>
                                    <th scope="col" class="center-text">Date Received</th>
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
                                            </td>
                                            <td>
						<strong><c:choose><c:when test="${not empty batch.configName}">${batch.configName}</c:when><c:otherwise>Invalid File</c:otherwise></c:choose></strong><br />
                                                ${batch.utBatchName}
                                                <c:if test="${batch.transportMethodId != 2}">
                                                    <c:set var="text" value="${fn:split(batch.originalFileName,'.')}" />
                                                    <c:set var="ext" value="${text[fn:length(text)-1]}" />
                                                    <br />
                                                    <c:set var="hrefLink" value="/FileDownload/downloadFile.do?fromPage=invalidin&filename=encoded_${batch.utBatchName}.${ext}&foldername=input files&orgId=${batch.orgId}"/>

                                                    <c:if test="${batch.transportMethodId == 6}">
                                                        <c:set var="hrefLink" value="/FileDownload/downloadFile.do?fromPage=invalidin&filename=${batch.utBatchName}_dec.${ext}&foldername=archivesIn"/>
                                                    </c:if>
						    <c:if test="${batch.transportMethodId == 12 || batch.transportMethodId == 9}">
							<c:set var="hrefPipeLink" value="/FileDownload/downloadFile.do?fromPage=invalidin&filename=${batch.utBatchName}.${ext}&foldername=archivesIn"/>
						    </c:if>

                                                    <a href="${hrefLink}" title="View Original File">
                                                        ${batch.originalFileName}
                                                    </a>
						    <br/>
						    <c:if test="${batch.inboundBatchConfigurationType == 1 && batch.statusValue != 'SRJ' && (batch.transportMethodId == 10 || batch.transportMethodId == 13)}">
                                                        <c:choose>
                                                            <c:when test="${batch.transportMethod == 'Direct Message' || batch.transportMethod == 'File Drop'}">
                                                                <a href="/FileDownload/downloadFile.do?fromPage=invalidin&filename=${batch.utBatchName}.txt&foldername=loadFiles" title="View Internal Processing File">Internal File - ${batch.utBatchName}.txt</a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="/FileDownload/downloadFile.do?fromPage=invalidin&filename=archive_${batch.utBatchName}.${ext}&foldername=archivesIn" title="View Internal Processing File"> Internal File - ${batch.utBatchName}</a>
                                                            </c:otherwise>
                                                        </c:choose>
						    </c:if>
                                                </c:if>
                                            </td>
                                            <td class="center-text">
						<c:choose>
						    <c:when test="${batch.transportMethod == 'Rest API'}">
							<a href="/administrator/processing-activity/apimessages/${batch.utBatchName}" title="View Rest API Message">${batch.transportMethod}</a>
						    </c:when>
                                                    <c:when test="${batch.transportMethod == 'Direct Message'}">
							<a href="#directModal" data-toggle="modal" class="viewDirectDetails" rel="${batch.id}" title="View Direct Message Details">File Drop (Direct)</a>
						    </c:when>
						    <c:otherwise>
							${batch.transportMethod}
						    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="center-text">
                                               <a href="#statusModal" data-toggle="modal" class="viewStatus" rel="${batch.statusId}" title="View this Status">${batch.statusValue}</a>
                                            </td>
                                            <td>
                                                Total Transactions: <strong><fmt:formatNumber value = "${batch.totalRecordCount}" type = "number"/></strong>
						<br />
						Total Errors: <strong><fmt:formatNumber value = "${batch.errorRecordCount}" type = "number"/></strong>
                                            </td>
                                            <td class="center-text">
                                                <fmt:formatDate value="${batch.dateSubmitted}" type="both" pattern="M/dd/yyyy h:mm a" />
                                                <c:if test="${not empty batch.startDateTime}">
                                                    <br />
                                                    Start: <fmt:formatDate value="${batch.startDateTime}" type="both" pattern="M/dd/yyyy h:mm a" />
                                                </c:if>
                                                <c:if test="${not empty batch.endDateTime}">
                                                    <br />
                                                    End <fmt:formatDate value="${batch.endDateTime}" type="both" pattern="M/dd/yyyy h:mm a" />
                                                </c:if>    
                                            </td>
                                            <td>
						<div class="dropdown pull-left">
						    <button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
							<i class="fa fa-cog"></i>
						    </button>
						    <ul class="dropdown-menu pull-right">
							<c:if test="${batch.transportMethodId != 2}">
							    <li>
								<a href="<c:url value='/administrator/processing-activity/invalidIn/batchActivities/${batch.utBatchName}'/>" class="viewBatchActivities" title="View Batch Activities">
								    <span class="glyphicon glyphicon-edit"></span>
								    View Batch Activities
								</a>
							    </li>
							    <li class="divider"></li>
							    <li>
								<a href="<c:url value='/administrator/processing-activity/invalidIn/auditReport/${batch.utBatchName}' />" title="View Audit Report">
								    <span class="glyphicon glyphicon-edit"></span>
								    View Audit Report
								</a>
							    </li>
							</c:if>
							<c:if test="${sessionScope.userDetails.roleId == 1}">
							    <li class="divider"></li>
							    <li>
								<a href="javascript:void(0);" rel="${batch.utBatchName}" class="deleteTransactions" title="Delete Batch Transactions">
								    <span class="glyphicon glyphicon-remove"></span>
								    Delete Batch
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
                                    <tr><td colspan="7" class="center-text">There were no files submitted in the date range selected.</td></tr>
                                </c:otherwise>
                            </c:choose>           
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<div class="modal fade" id="payloadModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>
<div class="modal fade" id="directModal" role="dialog" tabindex="-1" aria-labeledby="Direct Message Details" aria-hidden="true" aria-describedby="Direct Message Details"></div>