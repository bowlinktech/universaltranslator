<%-- 
    Document   : activityReport
    Created on : Jun 22, 2016, 9:23:31 AM
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
                <li class="active">Activity Report</li>
            </ol>

            <input type="hidden" name="fromDate" id="fromDate" rel="<fmt:formatDate value="${fromDate}" type="date" pattern="MM/dd/yyyy" />" rel2="<fmt:formatDate value="${userDetails.dateOrgWasCreated}" type="date" pattern="MM/dd/yyyy" />" value="${fromDate}" />
            <input type="hidden" name="toDate" id="toDate" rel="<fmt:formatDate value="${toDate}" type="date" pattern="MM/dd/yyyy" />" value="${toDate}" />

            <div class="col-md-12">
                <div class="date-range-picker-trigger form-control daterange pull-right" style="width:265px;">
                    <i class="glyphicon glyphicon-calendar"></i>
                    <span class="date-label"  id="fromdaterangedate" rel="" rel2=""><fmt:formatDate value="${fromDate}" type="date" pattern="MMM dd, yyyy" /> - <fmt:formatDate value="${toDate}" type="date" pattern="MMM dd, yyyy" /></span> <b class="caret"></b>
                </div>
                <div class="pull-left" style="width:265px;">
                    <select id="organization"  class="form-control">
                        <option value="0">- All Affiliated Organizations -</option>
                        <c:forEach var="org" items="${associatedOrgs}">
                            <option value="${org.id}">${org.orgName}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </div>
    </div>
    <div class="row" style="margin-top:20px;">
        <div class="col-md-12">
            <div class="row">

                <div class="col-md-4 col-sm-4 col-xs-6">
                    <section class="panel panel-default panel-stats" role="widget" aria-labelleby="Sent Referrals">
                        <div class="panel-body">
                            <span class="stat-number"><a href="javascript:void(0);" class="viewAllSentLink" title="${sentSectionName}"><span id="totalSent"></span></a></span>
                            <h3>${sentSectionName}<br />(Sent, Canceled, Errors)</h3>
                            <a href="javascript:void(0);" title="${sentSectionName}" class="btn btn-primary btn-small viewAllSentLink">View all</a>
                        </div>
                    </section>
                </div>
                <div class="col-md-4 col-sm-4 col-xs-6">
                    <form:form id="viewRejectedMessagesForm" action="activityReport/rejected" method="post">
                        <input type="hidden" id="fromRejectedDate" name="fromDate" />
                        <input type="hidden" id="toRejectedDate" name="toDate" />
                        <input type="hidden" id="selOrg" name="selOrg" value="0" />
                    </form:form>
                    <c:if test="${sentSectionName != 'Sent Files'}">
                        <section class="panel panel-default panel-stats" role="widget" aria-labelleby="Rejected ${sentSectionName}">
                            <div class="panel-body">
                                <span class="stat-number"><a href="javascript:void(0);" class="viewAllRejectedLink" title="Rejected ${sentSectionName}"><span id="totalRejected"></span></a></span>
                                <h3>Rejected ${sentSectionName}<br /><p></p></h3>
                                <a href="javascript:void(0);" title="Rejected ${sentSectionName}" class="btn btn-primary btn-small viewAllRejectedLink">View all</a>
                            </div>
                        </section>
                    </c:if>    
                </div>
                <div class="col-md-4 col-sm-4 col-xs-6">
                    <section class="panel panel-default panel-stats" role="widget" aria-labelleby="Received ${receivedSectionname}">
                        <div class="panel-body">
                            <span class="stat-number"><a href="javascript:void(0);" class="viewAllReceivedLink" title="Received ${receivedSectionname}"><span id="totalReceived"></span></a></span>
                            <h3>Received ${receivedSectionname}<br /><p></p></h3>
                            <a href="javascript:void(0);" title="Received ${receivedSectionname}" class="btn btn-primary btn-small viewAllReceivedLink">View all</a>
                        </div>
                    </section>
                </div>
            </div>
        </div>
    </div>        
    <div class="row" style="margin-top:20px;">
        <div class="col-md-12">
            <section class="panel panel-default">
                <div class="panel-heading">
                    <h2 class="panel-title">${sentSectionName} (Sent, Canceled, Errors)</h2>
                </div>
                <div class="panel-body">
                    <div id="sentMessages" class="form-container scrollable"></div>
                </div>
            </section>

            <section class="panel panel-default">
                <div class="panel-heading">
                    <h2 class="panel-title">Received ${receivedSectionname}</h2>
                </div>
                <div class="panel-body">
                    <div id="receivedMessages" class="form-container scrollable"></div>
                </div>
            </section> 
        </div>
    </div>
</div>

