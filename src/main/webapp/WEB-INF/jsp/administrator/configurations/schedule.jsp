<%-- 
    Document   : schedule
    Created on : Nov 22, 2013, 12:42:24 PM
    Author     : chadmccue
--%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <c:choose>
                <c:when test="${not empty savedStatus}" >
                    <div class="alert alert-success">
                        <strong>Success!</strong> 
                        <c:choose>
                            <c:when test="${savedStatus == 'updated'}">The schedule have been successfully updated!</c:when>
                        </c:choose>
                    </div>
                </c:when>
            </c:choose>
            <div id="saveMsgDiv" class="alert alert-danger" style="display:none;">
                <strong>You must click SAVE above to submit the schedule changes!</strong>
            </div>
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
			<dt>Configuration Summary:</dt>
			<dd><strong>Organization:</strong> ${configurationDetails.orgName}&nbsp;<span id="configtype" rel="${configurationDetails.type}"><c:choose><c:when test="${configurationDetails.type == 1}">(Source)</c:when><c:otherwise>(Target)</c:otherwise></c:choose></span></dd>
			<dd><strong>Configuration Name:</strong> ${configurationDetails.configName}</dd>
			<dd><strong>Message Type:</strong> ${configurationDetails.messageTypeName}</dd>
			<dd><strong>Transport Method:</strong> <c:choose><c:when test="${configurationDetails.transportMethod == 'File Upload'}"><c:choose><c:when test="${configurationDetails.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${configurationDetails.transportMethod}</c:otherwise></c:choose></dd>
		    </dt>
		</div>
	    </section>
        </div>
    </div>
    <div class="row-fluid">
	<div class="col-md-6">
	    <section class="panel panel-default">
		<div class="panel-heading">
		    <h3 class="panel-title">Schedule Options</h3>
		</div>
		<div class="panel-body">
		    <div class="form-container scrollable">
			<div class="form-group">
			    <label for="status">Process Submitted File</label>
			    <div>
				<label class="radio-inline">
				    <input type="radio" class="processMethod" name="processMethod" value="1" <c:if test="${scheduleDetails.type == 1}">checked="checked"</c:if> /> Manually
				 </label>
				<c:if test="${configurationDetails.type == 2}">
				    <label class="radio-inline">
					<input type="radio" class="processMethod" name="processMethod" value="2" <c:if test="${scheduleDetails.type == 2}">checked="checked"</c:if> /> Daily 
				    </label>
				    <label class="radio-inline">
					<input type="radio" class="processMethod" name="processMethod" value="3" <c:if test="${scheduleDetails.type == 3}">checked="checked"</c:if>  /> Weekly 
				    </label>
				    <label class="radio-inline">
					<input type="radio" class="processMethod" name="processMethod" value="4" <c:if test="${scheduleDetails.type == 4}">checked="checked"</c:if>  /> Monthly 
				    </label>
				</c:if>
				<label class="radio-inline">
				    <input type="radio" class="processMethod" name="processMethod" value="5" <c:if test="${scheduleDetails.type == 5 || scheduleDetails.type == 0}">checked="checked"</c:if> /> Automatically 
				</label>
			    </div>
			</div>
		    </div>
		</div>
	    </section>
	</div>
	<div class="col-md-6" id="specs" style="display:none">
	    <section class="panel panel-default">
		<div class="panel-heading">
		    <h3 class="panel-title">Scheduling Specs</h3>
		</div>
		<div class="panel-body">
		    <div class="form-container scrollable">
		    <form:form id="schedulingSpecs" modelAttribute="scheduleDetails" action="scheduling" method="post" role="form">
			<form:hidden path="configId" value="${scheduleDetails.configId}" />
			<form:hidden path="id" value="${scheduleDetails.id}" />
			<form:hidden path="type" id="type" />
			<input type="hidden" id="action" name="action" value="save" />
			<div class="form-group specFormFields" id="processingTypeDiv" style="display:none">
			    <label for="status">Type of Processing</label>
			    <div>
				<label class="radio-inline">
				    <form:radiobutton class="processingType" id="processingType" path="processingType" value="1" /> Scheduled 
				</label>
				<label class="radio-inline">
				    <form:radiobutton class="processingType" id="processingType" path="processingType" value="2" /> Continuous
				</label>
			    </div>
			</div>
			<div class="form-group specFormFields" id="newfilecheckDiv" style="display:none">
			    <label for="status">How often to check for a new file</label>
			    <form:select path="newfileCheck" id="newfilecheck" class="form-control">
				<option value="0">- Select -</option>
				<c:forEach begin="5" end="60" var="m" step="5">
				    <option value="${m}" <c:if test="${scheduleDetails.newfileCheck == m}">selected</c:if>>Every ${m} Minutes</option>
				</c:forEach>
			    </form:select>
			</div>          
			<div class="form-group specFormFields" id="processingDayDiv" style="display:none">
			    <label for="status">Process on what Day</label>
			    <div>
				<label class="radio-inline">
				    <form:radiobutton id="processingDay" path="processingDay" value="1" /> Sunday 
				</label>
				<label class="radio-inline">
				    <form:radiobutton id="processingDay" path="processingDay" value="2" /> Monday
				</label>
				<label class="radio-inline">
				    <form:radiobutton id="processingDay" path="processingDay" value="3" /> Tuesday 
				</label>
				<label class="radio-inline">
				    <form:radiobutton id="processingDay" path="processingDay" value="4" /> Wednesday
				</label>
				<label class="radio-inline">
				    <form:radiobutton id="processingDay" path="processingDay" value="5" /> Thursday 
				</label>
				<label class="radio-inline">
				    <form:radiobutton id="processingDay" path="processingDay" value="6" /> Friday
				</label>
				<label class="radio-inline">
				    <form:radiobutton id="processingDay" path="processingDay" value="7" /> Saturday
				</label>
			    </div>
			</div> 
			<div class="form-group specFormFields" id="processingTimeDiv" style="display:none">
			    <label for="status">Time of Day to process files</label>
			    <form:select id="processingTime" path="processingTime" class="form-control">
				<option value="0">- Select -</option>
				<c:forEach begin="1" end="23" var="i">
				    <option value="${i}" <c:if test="${scheduleDetails.processingTime == i}">selected</c:if>><c:choose><c:when test="${i < 12}">${i} AM</c:when><c:when test="${i == 12}">12 PM</c:when><c:otherwise>${i-12} PM</c:otherwise></c:choose></option>
				</c:forEach>
			    </form:select>
			</div>       
		    </form:form>
		    </div>
		</div>
	    </section>
	</div>
    </div>
</div>