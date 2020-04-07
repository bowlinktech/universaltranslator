<%-- 
    Document   : dashboard
    Created on : Jun 20, 2018, 9:01:56 AM
    Author     : chadmccue
--%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style>
.highcharts-figure .chart-container  {
    width: 200px;
    height: 100px;
    float: left;
}

.highcharts-figure, .highcharts-data-table table {
    width: 200px;
    margin: 0 auto;
}

</style>


<div class="main clearfix full-width" role="main">
    <div class="row-fluid contain basic-clearfix">
	<div class="col-md-12 col-sm-12 col-xs-12">
	    <div>
		<div class="pull-left">
		    <div class="form-group"></div>
		    <div class="form-container scrollable">
			 <a href="<c:url value='watchlist' />" title='Manage "Watch List"' class="btn btn-primary btn-small" role="button">Manage "Watch List"</a>
		    </div>
		</div>
		<div class="pull-right">
		     <form:form class="form form-inline" id="searchForm" action="/administrator" method="post">
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
	    </div>
	   
	</div>
    </div>
    <div class="row"><p></p></div>
    <div class="row-fluid contain basic-clearfix">
	<div class="col-md-12">
	   <section class="panel panel-default container-fluid">
		<div class="panel-heading">
		    <h3 class="panel-title">Generic Watch List Entries</h3>
		</div>
	       <div class="panel-body">
		   <div class="form-container scrollable genericMessages"></div>
	       </div>
	   </section>
	</div>
    </div>
    <div class="row"><p></p></div>
    <div class="row-fluid contain basic-clearfix">
	<div class="col-md-12">
	    <section class="panel panel-default container-fluid">
		<div class="panel-heading">
		    <h3 class="panel-title">Message Type Watch List Entries</h3>
		</div>
		<div class="panel-body">
		    <div class="row-fluid contain basic-clearfix">
                        <div class="col-md-12">
			   <section class="panel panel-info container-fluid">
				<div class="panel-heading">
				    <h3 class="panel-title">Inbound Messages</h3>
				</div>
			       <div class="panel-body">
				   <div class="form-container scrollable">
				       <table class="table table-bordered table-default" id="inbounddataTable">
					    <thead class="thead-light">
						<tr>
						    <th scope="col" class="center-text">Type</th>
						    <th scope="col" class="center-text">Date/Time</th>
						    <th scope="col" class="center-text">Trans. ID</th>
						    <th scope="col" class="center-text">Source</th>
						    <th scope="col" class="center-text">Configuration</th>
						    <th scope="col" class="center-text">Interface</th>
						    <th scope="col" class="center-text">Transactions (Rows)</th>
						    <th scope="col" class="center-text">Errors</th>
						    <th scope="col" class="center-text">Error Threshold</th>
						</tr>
					    </thead>
				       </table>
				   </div>
			       </div>
			   </section>
			</div>
		    </div>
		    <div class="row"><p></p></div>
		    <div class="row-fluid contain basic-clearfix">
			<div class="col-md-12">
			   <section class="panel panel-warning">
				<div class="panel-heading">
				    <h3 class="panel-title">Outbound Messages</h3>
				</div>
				<div class="panel-body">
				    <div class="form-container scrollable">
					<table class="table table-bordered table-default" id="outbounddataTable">
					    <thead class="thead-light">
						<tr>
						    <th scope="col" class="center-text">Type</th>
						    <th scope="col" class="center-text">Date/Time</th>
						    <th scope="col" class="center-text">Trans. ID</th>
						    <th scope="col" class="center-text">Target</th>
						    <th scope="col" class="center-text">Configuration</th>
						    <th scope="col" class="center-text">Interface</th>
						    <th scope="col" class="center-text">Transactions (Rows)</th>
						    <th scope="col" class="center-text">Errors</th>
                                                    <th scope="col" class="center-text">Error Threshold</th>
						</tr>
					    </thead>
					 </table>
				    </div>
				</div>
			   </section>
			</div>
		    </div>
		</div>
	    </section>
	</div>
    </div>
</div>


