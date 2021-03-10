<%-- 
    Document   : generateReport
    Created on : Mar 4, 2021, 10:38:43 AM
    Author     : chadmccue
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main">
    <div class="col-md-12">
        <div class="col-md-6">
            <section class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Run New Activity Report</h3>
                </div>
                <div class="panel-body">
                    <div class="form-container scrollable">
                        <div class="col-md-12">
                            <div class="col-md-6" style="padding-left:0px;">
                                <div class="form-group">
                                    <label for="reportType">Report Type *</label>
                                    <div>
                                        <label class="radio-inline">
                                            <input type="radio" name="reportType" id="reportType" value="1" class="type" checked /> PDF
                                        </label>
                                        <label class="radio-inline">
                                            <input type="radio" name="reportType" id="reportType" value="2" class="type" /> Excel
                                        </label>
                                    </div>
                                </div> 
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="reportType">Activity Date Range *</label>
                                    <input type="hidden" name="fromDate" id="fromDate" rel="<fmt:formatDate value="${fromDate}" type="date" pattern="MM/dd/yyyy" />" rel2="<fmt:formatDate value="${originalDate}" type="date" pattern="MM/dd/yyyy" />" value="${fromDate}" />
                                    <input type="hidden" name="toDate" id="toDate" rel="<fmt:formatDate value="${toDate}" type="date" pattern="MM/dd/yyyy" />" value="${toDate}" />
                                    <div class="date-range-picker-trigger form-control daterange" style="width:285px;">
                                        <i class="glyphicon glyphicon-calendar"></i>
                                        <span class="date-label"><fmt:formatDate value="${fromDate}" type="date" pattern="MMMM dd, yyyy" /> - <fmt:formatDate value="${toDate}" type="date" pattern="MMMM dd, yyyy" /></span> <b class="caret"></b>
                                    </div>
                                </div> 
                            </div>
                            <div class="form-group">
                                <label for="registryType">Registry Type *</label>
                                <div>
                                    <label class="radio-inline">
                                        <input type="radio" name="registryType" id="registryType" value="2" class="type" checked /> Family Planning
                                    </label>
                                </div>
                            </div> 
                            <div id="agencyDiv" class="form-group">
                                <label class="control-label" for="messageTypeId">Select one or more agencies *</label>
                                <div id="agencyList"></div>
                                <span id="agencyMsg" class="control-label"></span>
                            </div> 
                            <div class="form-group">
                                <input type="button" id="generateReport" class="btn btn-primary" value="Generate Report"/>
                            </div>          
                        </div>  
                    </div>
                </div>
            </section>
        </div>
        <div class="col-md-6">
            <section class="panel panel-default">
                <div class="panel-heading">
                    <div class="pull-right">
			<c:if test="${not empty reportLinkClicked}">
                            <a href="/advanced/DLReport?${reportLinkClicked}" class="reportLinkClicked" role="button" style="display:none">
                                <button class="btn btn-xs btn-success">
                                       <i class="ace-icon fa fa-download bigger-120"></i>
                                </button>
                            </a>
                        </c:if>
                        <div class="dropdown pull-left refreshRate" rel="${pageRefreshRate}">
                            <button class="btn btn-xs btn-info dropdown-toggle" type="button" data-toggle="dropdown">
                                <i class="fa fa-refresh"></i> Refresh rate: 
                                <c:choose>
                                    <c:when test="${pageRefreshRate == 15000}">Every 15 seconds</c:when>
                                    <c:when test="${pageRefreshRate == 35000}">Every 35 seconds</c:when>
                                    <c:when test="${pageRefreshRate == 60000}">Every minute</c:when>
                                    <c:when test="${pageRefreshRate == 300000}">Every 5 minutes</c:when>
                                    <c:when test="${pageRefreshRate == 0}">Off</c:when>
                                </c:choose>
                            </button>
                            <ul class="dropdown-menu pull-right">
                                <li>
                                    <a href="#" rel="15000" class="setRefreshRate" title="Refresh Page Every 15 Seconds" data-toggle="tooltip">
                                        Every 15 Seconds
                                    </a>
                                </li>
                                <li>
                                    <a href="#" rel="35000" class="setRefreshRate" title="Refresh Page Every 15 Seconds" data-toggle="tooltip">
                                        Every 35 Seconds
                                    </a>
                                </li>
                                <li>
                                    <a href="#" rel="60000" class="setRefreshRate" title="Refresh Page Every 15 Seconds" data-toggle="tooltip">
                                        Every Minute
                                    </a>
                                </li>
                                <li>
                                    <a href="#" rel="300000" class="setRefreshRate" title="Refresh Page Every 15 Seconds" data-toggle="tooltip">
                                        Every 5 Minutes
                                    </a>
                                </li>
                                <li>
                                    <a href="#" rel="0" class="setRefreshRate" title="Refresh Page Every 15 Seconds" data-toggle="tooltip">
                                        Turn off Auto Refresh
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <h3 class="panel-title">Past Activity Reports</h3>
                </div>
                <div class="panel-body">
                    <div class="form-container scrollable">
                        <c:choose>
                            <c:when test="${not empty activityReports}">
                                <table class="table table-striped table-hover table-default" id="reportDataTable">
                                    <thead>
                                        <tr>
                                            <th>Status</th>
                                            <th>Report Type</th>
                                            <th>Agencies</th>
                                            <th>Date Range</th>
                                            <th>Date Created</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${activityReports}" var="activityReport">
                                            <tr>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${activityReport.status == 1}">Processing...</c:when>
                                                        <c:when test="${activityReport.status == 2}">Ready</c:when>
                                                        <c:when test="${activityReport.status == 3}">Error</c:when>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${activityReport.reportType == 1}">PDF</c:when>
                                                        <c:when test="${activityReport.reportType == 2}">Excel</c:when>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <a href="#reportAgencyModal" rel="${activityReport.id}" data-toggle="modal" class="btn btn-primary btn-xs btn-action showAgencies" title="Selected Agencies">Selected Agencies</a>
                                                </td>
                                                <td>${activityReport.dateRange}</td>
                                                <td><fmt:formatDate value="${activityReport.dateCreated}" type="both" pattern="M/dd/yyyy h:mm a" /></td>
                                                <td>
                                                    <div class="dropdown pull-left">
							 <button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
							     <i class="fa fa-cog"></i>
							 </button>
							 <ul class="dropdown-menu pull-right">
							     <c:if test="${activityReport.status == 2}">
								 <li>
                                                                     <c:choose>
                                                                         <c:when test="${activityReport.reportType == 1}">
                                                                             <a href="/administrator/processing-activity/printActivityReportToPDF/${activityReport.fileName}" rel="${activityReport.id}" title="Download this activity report">
                                                                                <span class="glyphicon glyphicon-download"></span>
                                                                                Download
                                                                            </a>
                                                                         </c:when>
                                                                         <c:when test="${activityReport.reportType == 2}">
                                                                             <a href="/administrator/processing-activity/printActivityReportToExcel/${activityReport.fileName}" rel="${activityReport.id}" title="Download this activity report">
                                                                                <span class="glyphicon glyphicon-download"></span>
                                                                                Download
                                                                            </a>
                                                                         </c:when>
                                                                     </c:choose>
								 </li>
                                                                 <li class="divider"></li>
							     </c:if>
                                                             <li>
                                                                <a href="javascript:void(0);" class="rerunReport" rel="${activityReport.id}" title="Rerun this activity report">
                                                                    <span class="glyphicon glyphicon-refresh"></span>
                                                                    Rerun Report
                                                                </a>
                                                            </li>
                                                            <li class="divider"></li>    
							     <li>
								 <a href="javascript:void(0);" class="deleteReport" rel="${activityReport.id}" title="Delete this activity report">
								     <span class="glyphicon glyphicon-remove-circle"></span>
								     Delete
								 </a>
							     </li>
							 </ul>
						     </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <p>There were no saved activity reports found.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>
<div class="modal fade" id="reportAgencyModal" role="dialog" tabindex="-1" aria-labeledby="Activity Report Selected Agencies" aria-hidden="true" aria-describedby="Activity Report Selected Agencies"></div>