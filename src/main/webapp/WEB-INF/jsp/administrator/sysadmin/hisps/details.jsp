<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title"><c:choose><c:when test="${btnValue == 'Update'}">Update</c:when><c:when test="${btnValue == 'Create'}">Add</c:when></c:choose> HISP ${success}</h3>
                </div>
                <div class="modal-body">
            <form:form id="hispform" commandName="hisps" modelAttribute="hispDetails" method="post" role="form">
                <form:hidden path="id" id="id" />
		<form:hidden path="dateCreated" />
		<form:hidden path="status" />

                <div class="form-container">
                    <spring:bind path="hispName">
                        <div id="hispNameDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="hispName">HISP Name *</label>
                            <form:input path="hispName" id="hispName" class="form-control" type="text" maxLength="45" />
                            <span id="hispNameMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <spring:bind path="utAPIUsername">
                        <div id="hispUTAPIUsernameDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="utAPIUsername">UT API Username *</label>
                            <form:input path="utAPIUsername" id="utAPIUsername" class="form-control" type="text" maxLength="45" />
			    <span id="hispUTAPIUsernameMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
		    <spring:bind path="utAPIPassword">
                        <div id="hispUTAPIPasswordDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="utAPIPassword">UT API Password *</label>
                            <form:input path="utAPIPassword" id="utAPIPassword" class="form-control" type="text" maxLength="45" />
			    <span id="hispUTAPIPasswordMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <spring:bind path="hispAPIUsername">
                        <div id="hispAPIUsernameDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="hispAPIUsername">HISP API Username *</label>
                            <form:input path="hispAPIUsername" id="hispAPIUsername" class="form-control" type="text" maxLength="45" />
			    <span id="hispAPIUsernameMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
		    <spring:bind path="hispAPIPassword">
                        <div id="hispAPIPasswordDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="hispAPIPassword">HISP API Password *</label>
                            <form:input path="hispAPIPassword" id="hispAPIPassword" class="form-control" type="text" maxLength="45" />
                            <span id="hispAPIPasswordMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
		    <spring:bind path="hispAPIURL">
                        <div id="hispAPIURLDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="hispAPIURL">HISP API URL *</label>
                            <form:input path="hispAPIURL" id="hispAPIURL" class="form-control" type="text" maxLength="255" />
                            <span id="hispAPIURLMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
		    <spring:bind path="primaryContact">
                        <div class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="primaryContact">HISP Primary Contact</label>
                            <form:input path="primaryContact" id="primaryContact" class="form-control" type="text" maxLength="45" />
                        </div>
                    </spring:bind>
		    <spring:bind path="primaryContactEmail">
                        <div class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="primaryContactEmail">HISP Primary Contact Email</label>
                            <form:input path="primaryContactEmail" id="primaryContactEmail" class="form-control" type="text" maxLength="45" />
                        </div>
                    </spring:bind>
		    <spring:bind path="primaryContactPhone">
                        <div class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="primaryContactPhone">HISP Primary Contact Phone</label>
                            <form:input path="primaryContactPhone" id="primaryContactPhone" class="form-control" type="text" maxLength="45" />
                        </div>
                    </spring:bind>
                    <div class="form-group">
                        <input type="button" id="submitButton" rel="${btnValue}" class="btn btn-primary" value="${btnValue}"/>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function () {
        $("input:text,form").attr("autocomplete", "off");
    });
</script>
