<%-- 
    Document   : createConnection
    Created on : Dec 24, 2013, 11:22:00 AM
    Author     : chadmccue
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">
		<c:choose>
		    <c:when test="${watchlistEntry.id > 0}">Update</c:when>
		    <c:otherwise>Create New</c:otherwise>
		</c:choose> Dashboard Watch List Entry
	    </h3>
        </div>
	<div class="modal-body">
	    <ul class="nav nav-tabs" id="tabContent">
		<c:if test="${watchlistEntry.id == 0 || (watchlistEntry.id > 0 && watchlistEntry.orgId == 0)}"><li class="active"><a href="#generic" data-toggle="tab">Generic Entry</a></li></c:if>
		<c:if test="${watchlistEntry.id == 0 || (watchlistEntry.id > 0 && watchlistEntry.orgId > 0)}"><li ${(watchlistEntry.id > 0 && watchlistEntry.orgId > 0) ? 'class="active"' : '' }><a href="#messageType" data-toggle="tab">Message Type Entry</a></li></c:if>
	    </ul>
	    <div class="tab-content">
		<div class="tab-pane ${watchlistEntry.id == 0 || (watchlistEntry.id > 0 && watchlistEntry.orgId == 0) ? 'active' : '' }" id="generic">
		    <div class="form-container scrollable"  style="padding-top:20px;">
			<form:form id="watchListGenericEntryForm" modelAttribute="watchlistEntry" action="addDashboardWatchList.do" method="post" role="form">
			    <form:hidden path="id" id="id" />
			    <form:hidden path="orgId" value="0" />
			    <form:hidden path="configId" value="0" />
			    <form:hidden path="expectFirstFileTime" id="expectFirstFileTime" />
			    <spring:bind path="entryMessage">      
				<div id="entryMessageDiv" class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="entryMessage">Message *</label>
				    <form:input path="entryMessage" class="form-control" type="text" id="entryMessage" maxlength="55" value="${watchlistEntry.entryMessage}" />
				     <span id="entryMessageMsg" class="control-label"></span>
				</div>  
			    </spring:bind> 
			    <spring:bind path="expected">      
				<div id="expectedDiv" class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="expected">Alert Repeated *</label>
				    <form:select path="expected" id="expected" class="form-control">
					<option value="Daily" <c:if test="${'Daily' == watchlistEntry.expected}">selected</c:if>>Daily</option>
					<option value="Weekly" <c:if test="${'Weekly' == watchlistEntry.expected}">selected</c:if>>Weekly</option>
					<option value="Monthly" <c:if test="${'Monthly' == watchlistEntry.expected}">selected</c:if>>Monthly</option>
				    </form:select> 
				    <span id="expectedMsg" class="control-label"></span>
				</div>  
			    </spring:bind>
			    <spring:bind path="expectFirstFile">      
				<div id="expectFirstFileDiv" class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="expectFirstFile">Date to start the alert *</label>
				    <form:input path="expectFirstFile" class="form-control expectFirstFile" type="text" id="expectFirstFile"  value="${watchlistEntry.expectFirstFile}" />
				     <span id="expectFirstFileMsg" class="control-label"></span>
				</div>  
			    </spring:bind> 
			    <div id="expectFirstFileTimeDiv" class="form-group ${status.error ? 'has-error' : '' }">
				<label class="control-label" for="expectFirstFile">Time to start the alert</label>
				<div class="row">
				    <div class="col-md-4">
					<select id="expectedTimeHour" class="form-control">
					    <c:forEach begin="1" end="12" var="hour">
						<option value="${hour}" <c:if test="${hour == watchlistEntry.expectedTimeHour}">selected</c:if>>${hour}</option>
					    </c:forEach>
					</select>
				    </div>
				    <div class="col-md-4">
					<select id="expectedTimeMinute" class="form-control">
					    <c:forEach begin="0" end="55" var="minute" step="5">
						<option value="${minute}" <c:if test="${minute == watchlistEntry.expectedTimeMinute}">selected</c:if>><c:if test="${minute < 10}">0</c:if>${minute}</option>
					    </c:forEach>
					</select>
				    </div>
				    <div class="col-md-4">
					<select id="expectedTimeAMPM" class="form-control">
					    <option value="AM" <c:if test="${'AM' == watchlistEntry.expectedTimeAMPM}">selected</c:if>>AM</option>
					    <option value="PM" <c:if test="${'PM' == watchlistEntry.expectedTimeAMPM}">selected</c:if>>PM</option>
					</select>
				    </div>
				</div>
			    </div> 
			    <div class="form-group">
				<input type="button" id="submitButton" rel="Create" role="button" class="btn btn-primary" value="<c:choose><c:when test="${watchlistEntry.id > 0}">Update</c:when><c:otherwise>Create</c:otherwise></c:choose> Watch List Entry"/>
			    </div> 
			</form:form>
		    </div>
		</div>
		<div class="tab-pane ${(watchlistEntry.id > 0 && watchlistEntry.orgId > 0) ? 'active' : '' }" id="messageType">
		    <div class="form-container scrollable" style="padding-top:20px;">
			<form:form id="watchListEntryForm" modelAttribute="watchlistEntry" action="addDashboardWatchList.do" method="post" role="form">
			    <form:hidden path="id" id="id" />
			    <form:hidden path="expectFirstFileTime" id="expectFirstFileTimeMT" />
			    <spring:bind path="orgId">      
				<div id="orgDiv" class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="organization">Organization *</label>
				     <form:select path="orgId" id="organization" class="form-control selOrganization" disabled="${watchlistEntry.id > 0 ? 'true':'false'}">
					    <option value="">- Select -</option>
					    <c:forEach items="${organizations}" var="org" varStatus="oStatus">
						<option value="${organizations[oStatus.index].id}" <c:if test="${organizations[oStatus.index].id == watchlistEntry.orgId}">selected</c:if>>${organizations[oStatus.index].orgName} </option>
					    </c:forEach>
				    </form:select>
				    <c:if test="${watchlistEntry.id > 0}"><form:hidden id="orgId" path="orgId"/></c:if>  	
				    <span id="orgMsg" class="control-label"></span>
				</div>
			    </spring:bind>
			    <spring:bind path="configId">      
				<div id="configDiv" class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="messageTypeId">Configuration *</label>
				    <form:select path="configId" id="config" rel="${watchlistEntry.configId}" class="form-control" disabled="${watchlistEntry.id > 0 ? 'true':'false'}">
					<option value="">- Select -</option>
				    </form:select>
				    <c:if test="${watchlistEntry.id > 0}"><form:hidden id="configId" path="configId"/></c:if>       
				    <span id="configMsg" class="control-label"></span>
				</div>  
			    </spring:bind>
			    <spring:bind path="expected">      
				<div id="expectedDiv" class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="expected">Alert Repeated *</label>
				    <form:select path="expected" id="expected" class="form-control">
					<option value="Daily" <c:if test="${'Daily' == watchlistEntry.expected}">selected</c:if>>Daily</option>
					<option value="Weekly" <c:if test="${'Weekly' == watchlistEntry.expected}">selected</c:if>>Weekly</option>
					<option value="Monthly" <c:if test="${'Monthly' == watchlistEntry.expected}">selected</c:if>>Monthly</option>
				    </form:select> 
				    <span id="expectedMsg" class="control-label"></span>
				</div>  
			    </spring:bind>
			    <spring:bind path="expectFirstFile">      
				<div id="expectFirstFileMTDiv" class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="expectFirstFile">Date to start the alert *</label>
				    <form:input path="expectFirstFile" class="form-control expectFirstFile" type="text" id="expectFirstFileMT"  value="${watchlistEntry.expectFirstFile}" />
				     <span id="expectFirstFileMsg" class="control-label"></span>
				</div>  
			    </spring:bind> 
			    <div id="expectFirstFileTimeDiv" class="form-group ${status.error ? 'has-error' : '' }">
				<label class="control-label" for="expectFirstFile">Time to start the alert</label>
				<div class="row">
				    <div class="col-md-4">
					<select id="expectedTimeHourMT" class="form-control">
					    <c:forEach begin="1" end="12" var="hour">
						<option value="${hour}" <c:if test="${hour == watchlistEntry.expectedTimeHour}">selected</c:if>>${hour}</option>
					    </c:forEach>
					</select>
				    </div>
				    <div class="col-md-4">
					<select id="expectedTimeMinuteMT" class="form-control">
					    <c:forEach begin="0" end="55" var="minute" step="5">
						<option value="${minute}" <c:if test="${minute == watchlistEntry.expectedTimeMinute}">selected</c:if>><c:if test="${minute < 10}">0</c:if>${minute}</option>
					    </c:forEach>
					</select>
				    </div>
				    <div class="col-md-4">
					<select id="expectedTimeAMPMMT" class="form-control">
					    <option value="AM" <c:if test="${'AM' == watchlistEntry.expectedTimeAMPM}">selected</c:if>>AM</option>
					    <option value="PM" <c:if test="${'PM' == watchlistEntry.expectedTimeAMPM}">selected</c:if>>PM</option>
					</select>
				    </div>
				</div>
			    </div> 
			    <div class="form-group">
				<input type="button" id="submitButton" rel="Create" role="button" class="btn btn-primary" value="<c:choose><c:when test="${watchlistEntry.id > 0}">Update</c:when><c:otherwise>Create</c:otherwise></c:choose> Watch List Entry"/>
			    </div> 
			</form:form>
		    </div>
		</div> 
	    </div> 
	</div>
    </div>
</div>         

<script>
    $(function () {
        var selOrg = $('.selOrganization').val();
	
	$("input:text,form").attr("autocomplete", "off");
	
	var date = new Date();
	var currentMonth = date.getMonth();
	var currentDate = date.getDate();
	var currentYear = date.getFullYear();
	
	$('.expectFirstFile').daterangepicker({
	    singleDatePicker: true,
	    timePicker: false,
	    showDropdowns: true,
	    minDate: new Date(currentYear, currentMonth, currentDate),
	    startDate: moment(date),
	    minYear: parseInt(moment().format('YYYY')),
	    maxYear: parseInt(moment().format('YYYY'))
	  });
	

        if (selOrg > 0) {
            populateConfigurations(selOrg);
        }

    });
</script>
