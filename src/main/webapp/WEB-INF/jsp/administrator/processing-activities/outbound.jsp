<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                    <dt>System Summary:</dt>
                    <dd><strong>Batches Processed in the Past Hour:</strong> <fmt:formatNumber value="${summaryDetails.batchesPastHour}" /></dd>
                    <dd><strong>Batches Processed in today:</strong> <fmt:formatNumber value="${summaryDetails.batchesToday}" /></dd>
                    <dd><strong>Batches Processed in This Week:</strong> <fmt:formatNumber value="${summaryDetails.batchesThisWeek}" /></dd>
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
                        <form:form class="form form-inline" id="searchForm" action="/administrator/processing-activity/outbound" method="post">
                            <div class="form-group">
                                <input type="hidden" name="fromDate" id="fromDate" rel="<fmt:formatDate value="${fromDate}" type="date" pattern="MM/dd/yyyy" />" rel2="<fmt:formatDate value="${originalDate}" type="date" pattern="MM/dd/yyyy" />" value="${fromDate}" />
                                <input type="hidden" name="toDate" id="toDate" rel="<fmt:formatDate value="${toDate}" type="date" pattern="MM/dd/yyyy" />" value="${toDate}" />
                                <input type="hidden" name="page" id="page" value="${currentPage}" />
				<input type="hidden" name="batchName" id="batchName" value="${batchName}" />
				<input type="hidden" name="userRole" id="userRole" value="${userRole}" />
                            </div>
                        </form:form>
                    </div>
                </div>
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
                    <table class="table table-striped table-hover table-default" id="batchdownloads-table" term="${searchFilter}">
			<thead>
			    <tr>
				<th scope="col">Organization</th>
				<th scope="col">Batch Details</th>
				<th scope="col">Associated Inbound Batch</th>
				<th scope="col" class="center-text">Transport Method</th>
				<th scope="col" class="center-text">Status</th>
				<th scope="col"># of Transactions</th>
				<th scope="col" class="center-text">Date Delivered</th>
				<th scope="col"></th>
			    </tr>
			</thead>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>