<%-- 
    Document   : activityReport
    Created on : Dec 11, 2014, 9:33:19 AM
    Author     : chadmccue
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main">
    <div class="col-md-12">

        <div class="row-fluid contain basic-clearfix">
            <form:form class="form form-inline" id="searchForm" action="/administrator/processing-activity/activityReport" method="post">
                <div class="form-group">
                    <input type="hidden" name="fromDate" id="fromDate" rel="<fmt:formatDate value="${fromDate}" type="date" pattern="MM/dd/yyyy" />" rel2="<fmt:formatDate value="${originalDate}" type="date" pattern="MM/dd/yyyy" />" value="${fromDate}" />
                    <input type="hidden" name="toDate" id="toDate" rel="<fmt:formatDate value="${toDate}" type="date" pattern="MM/dd/yyyy" />" value="${toDate}" />
                </div>
            </form:form>
            <div class="form-container scrollable">
                <div class="date-range-picker-trigger form-control pull-right daterange" style="width:285px; margin-left: 10px;">
                    <i class="glyphicon glyphicon-calendar"></i>
                    <span class="date-label"><fmt:formatDate value="${fromDate}" type="date" pattern="MMMM dd, yyyy" /> - <fmt:formatDate value="${toDate}" type="date" pattern="MMMM dd, yyyy" /></span> <b class="caret"></b>
                </div>
            </div>
        </div>

        <div class="row-fluid contain basic-clearfix" style="margin-top:20px;">
            <div class="col-md-3 col-sm-3 col-xs-6">
                <section class="panel panel-default panel-stats" role="widget" aria-labelleby="Messages Received">
                    <div class="panel-body">
                        <span class="stat-number"><a href="/administrator/processing-activity/inbound"><c:choose><c:when test="${totalMessagesReceived >= 0}"><fmt:formatNumber value = "${totalMessagesReceived}" type = "number"/></c:when><c:otherwise>0</c:otherwise></c:choose></a></span>
			<h3>Message<c:if test="${totalMessagesReceived == 0 || totalMessagesReceived > 1}">s</c:if> Received</h3>
		    </div>
		</section>
	    </div>
	    <div class="col-md-3 col-sm-3 col-xs-6">
		<section class="panel panel-default panel-stats" role="widget" aria-labelleby="Messages Delivered">
		    <div class="panel-body">
			<span class="stat-number"><a href="/administrator/processing-activity/outbound"><c:choose><c:when test="${totalMessagesDelivered >= 0}"><fmt:formatNumber value = "${totalMessagesDelivered}" type = "number"/></c:when><c:otherwise>0</c:otherwise></c:choose></a></span>
			<h3>Message<c:if test="${totalMessagesDelivered == 0 || totalMessagesDelivered > 1}">s</c:if> Delivered</h3>
		    </div>
		</section>
	    </div>   
	    <div class="col-md-3 col-sm-3 col-xs-6">
		<section class="panel panel-default panel-stats" role="widget" aria-labelleby="Rejected Received">
		    <div class="panel-body">
			    <span class="stat-number"><a href="/administrator/processing-activity/inbound"><c:choose><c:when test="${totalRejectedReceived >= 0}"><fmt:formatNumber value = "${totalRejectedReceived}" type = "number"/></c:when><c:otherwise>0</c:otherwise></c:choose></a></span>
			<h3>Rejected Received</h3>
		    </div>
		</section>
	    </div> 
	    <div class="col-md-3 col-sm-3 col-xs-6">
		<section class="panel panel-default panel-stats" role="widget" aria-labelleby="Rejected Transactions">
		    <div class="panel-body">
			    <span class="stat-number"><a href="/administrator/processing-activity/rejected"><c:choose><c:when test="${totalRejected >= 0}"><fmt:formatNumber value = "${totalRejected}" type = "number"/></c:when><c:otherwise>0</c:otherwise></c:choose></a></span>
			<h3>Rejected Transaction<c:if test="${totalRejected == 0 || totalRejected > 1}">s</c:if></h3>
		    </div>
		</section>
	    </div>  
	    <div class="col-md-3 col-sm-3 col-xs-6">
		<section class="panel panel-default panel-stats" role="widget" aria-labelleby="Rejected Delivered">
		    <div class="panel-body">
			    <span class="stat-number"><a href="/administrator/processing-activity/outbound"><c:choose><c:when test="${totalDeliveredRejected >= 0}"><fmt:formatNumber value = "${totalDeliveredRejected}" type = "number"/></c:when><c:otherwise>0</c:otherwise></c:choose></a></span>
			<h3>Rejected Delivered</h3>
		    </div>
		</section>
	    </div> 		
	</div>

	<section class="panel panel-default">
	    <div class="panel-heading">
		<h3 class="panel-title">Files Received into the Universal Translator</h3>
	    </div>
	    <div class="panel-body">
		<div class="form-container scrollable">
		    <table class="table table-striped table-hover table-default">
			<thead>
			    <tr>
				<th scope="col" style="width:350px;">Organization Name</th>
				<th scope="col" style="width:350px;">Configuration Name</th>
				<th scope="col" class="center-text">Total Received</th>
			    </tr>
			    <c:choose>
				<c:when test="${not empty referralList}">
				    <c:forEach var="item" items="${referralList}">
					<tr>
					    <td scope="row">
						${item.orgName}
					    </td>
					    <td>
						${item.messageType}
					    </td>
					    <td class="center-text">
						${item.total}
					    </td>
					</tr>
				    </c:forEach>
				</c:when>
				<c:otherwise>
				    <tr>
					<td colspan="3" class="center-text">
					    No files were received in the selected date range.
					</td>
				    </tr>
				</c:otherwise>
			    </c:choose>
			</thead>
		    </table>
		</div>
	    </div>
	</section>
    </div>
</div>
