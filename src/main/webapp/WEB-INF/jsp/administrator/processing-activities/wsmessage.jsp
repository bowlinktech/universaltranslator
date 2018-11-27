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
                        <form:form class="form form-inline" id="searchForm" action="/administrator/processing-activity/wsmessage" method="post">
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
                        <b>There are over 200 web service messages found.  The first 200 results are displayed.  Please refine your results using the date search box.</b>
                        <br/><br/>
                    </div>
                </c:if>
                <div class="form-group">
                    <select id="wsDirection" name="wsDirection" class="form-control" style="width:150px;">
                        <option value="inbound" selected>Inbound</option>
                        <option value="outbound">Outbound</option>
                    </select>
                </div>


                <div class="form-container scrollable">

                    <div class="date-range-picker-trigger form-control pull-right daterange" style="width:285px; margin-left: 10px;">
                        <i class="glyphicon glyphicon-calendar"></i>
                        <span class="date-label"><fmt:formatDate value="${fromDate}" type="date" pattern="MMMM dd, yyyy" /> - <fmt:formatDate value="${toDate}" type="date" pattern="MMMM dd, yyyy" /></span> <b class="caret"></b>
                    </div>
                    <table class="table table-striped table-hover table-default"  <c:if test="${not empty wsMessages}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Send To Organization</th>
                                    <th scope="col" style="width:50px">Batch Id<br/>Assigned</th>
                                    <th scope="col">From Address</th>
                                    <th scope="col" class="center-text">Status</th>
                                    <th scope="col" class="center-text">Error<br/>(if any)</th>
                                    <th scope="col" class="center-text">Attachment<br/>Position</th>
                                    <th scope="col" class="center-text">At Expected<br/>Position</th>
                                    <th scope="col" class="center-text">Date Received</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty wsMessages}">
                                    <c:forEach var="wsmessage" items="${wsMessages}">
                                        <tr  style="cursor: pointer">
                                            <td scope="row">
                                                ${wsmessage.orgName}                                               
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${wsmessage.batchUploadId != 0}">
                                                        <a href="<c:url value='/administrator/processing-activity/inbound/auditReport/${wsmessage.batchName}' />" class="btn btn-link viewAuditReport" title="View Audit Report" role="button">${wsmessage.batchName}</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        N/A
                                                    </c:otherwise>
                                                </c:choose>    
                                            </td>
                                            <td>
                                                ${wsmessage.fromAddress }
                                            </td>
                                            <td class="center-text">
                                                ${wsmessage.statusName}
                                            </td>
                                            <td class="center-text">
                                                ${wsmessage.errorDisplayText }
                                            </td>
                                            <td class="center-text">
                                                ${wsmessage.foundPosition}
                                            </td>
                                            <td class="center-text">
                                                ${wsmessage.positionMatched}
                                            </td>
                                            <td class="center-text"><fmt:formatDate value="${wsmessage.dateCreated}" type="both" pattern="M/dd/yyyy h:mm:ss a" /></td>
                                            <td class="actions-col">
                                                <a href="#payloadModal" data-toggle="modal" class="viewPayload" rel="${wsmessage.id}" title="View Payload">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    View Payload
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>     
                                </c:when>   
                                <c:otherwise>
                                    <tr><td colspan="9" class="center-text">There are currently no submitted web service messages.</td></tr>
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