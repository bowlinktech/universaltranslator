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
                        <form:form class="form form-inline" id="searchForm" action="/administrator/processing-activity/invalidOut" method="post">
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
                        <b>There are over 200 invalid batches found.  The first 200 results are displayed.  Please refine your results using the date search box.</b>
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
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty batches}">id="invalidOutbound-table"</c:if> term="${searchFilter}">
			<thead>
			     <tr>
				 <th scope="col">Organization</th>
				 <th scope="col">Batch Details</th>
				 <th scope="col">Associated Inbound Batch</th>
				 <th scope="col" class="center-text">Transport Method</th>
				 <th scope="col" class="center-text">Status</th>
				 <th scope="col" class="center-text"># of Transactions</th>
				 <th scope="col" class="center-text">Date Delivered</th>
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
						<strong>${batch.configName}</strong><br />
                                                ${batch.utBatchName}
                                                <c:if test="${not empty batch.outputFileName 
						    && (
						    batch.statusId == 28 || 
						    batch.statusId == 58 || 
						    batch.statusId == 59 || 
						    batch.statusId == 30)}">
                                                    <br />
                                                    <a href="/FileDownload/downloadFile.do?fromPage=invalidOut&filename=${batch.outputFileName}&utBatchName=${batch.outputFileName}&foldername=archivesOut&orgId=0" title="View Original File">
                                                        Download Outbound File
                                                    </a>
                                                </c:if>
                                            </td>
                                            <td>
						<a href="<c:url value='/administrator/processing-activity/inbound/${batch.fromBatchName}' />" title="View Inbound Batch" role="button">${batch.fromBatchName}</a>
                                                <c:if test="${not empty batch.fromBatchFile}">
                                                    <br />
                                                    <a href="/FileDownload/downloadFile.do?fromPage=invalidOut&filename=${batch.fromBatchFile}&foldername=archivesIn&orgId=0" title="View Uploaded Source File">
                                                        Download Uploaded File
                                                    </a>
                                                </c:if>
                                            </td>
                                            <td class="center-text">
                                                <c:choose>
                                                    <c:when test="${batch.transportMethod == 'File Upload'}">
                                                        File Download
                                                    </c:when>
                                                    <c:when test="${batch.transportMethodId == '6'}">
                                                        <a href="/administrator/processing-activity/wsmessageOut/${batch.utBatchName}" title="View Web Services Status">${batch.transportMethod}</a>
                                                    </c:when>
						    <c:when test="${batch.transportMethodId == '9'}">
							<a href="/administrator/processing-activity/apimessagesOut/${batch.utBatchName}" title="View Rest API Message">${batch.transportMethod}</a>
						    </c:when>	
                                                    <c:otherwise>
                                                        ${batch.transportMethod}
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="center-text">
                                                <a href="#statusModal" data-toggle="modal" class="viewStatus" rel="${batch.statusId}" title="View this Status">${batch.statusValue}</a>
                                            </td>
                                            <td class="center-text">
                                               <fmt:formatNumber value = "${batch.totalRecordCount}" type = "number"/>
					       <c:if test="${fn:contains(batch.configName, 'Service Received')}">
						   <a href="#" data-container="body" data-toggle="tooltip" data-placement="top" data-html="true" data-original-title="<p style='width:350px;'><p style='text-align:left'>This number is based on unique patients, multiple services received have been grouped togther under a single visit.</p><p style='text-align:left'>This only applies for service received message types.</p></p>">
							<span class="glyphicon glyphicon-exclamation-sign" style="cursor:pointer"></span>
						    </a>
					       </c:if>
                                            </td>
                                            <td class="center-text"><fmt:formatDate value="${batch.dateCreated}" type="both" pattern="M/dd/yyyy h:mm:ss a" /></td>
                                            <td>
						<div class="dropdown pull-left">
						    <button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
							<i class="fa fa-cog"></i>
						    </button>
						    <ul class="dropdown-menu pull-right">
							<c:if test="${batch.transportMethodId != 2}">
							    <li>
								<a href="<c:url value='/administrator/processing-activity/outbound/auditReport/${batch.utBatchName}' />" title="View Audit Report">
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
                                    <tr><td colspan="8" class="center-text">There were no files sent out in the date range selected.</td></tr>
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