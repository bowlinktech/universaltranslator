<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title"><c:choose><c:when test="${btnValue == 'Update'}">Update</c:when><c:when test="${btnValue == 'Create'}">Add</c:when></c:choose> System User ${success}</h3>
                </div>
                <div class="modal-body">
            <form:form id="userdetailsform" commandName="userdetails" modelAttribute="userdetails"  method="post" role="form">
                <form:hidden path="id" id="id" />
                <form:hidden path="orgId" id="orgId" />
                <form:hidden path="dateCreated" />
		<form:hidden path="mainContact" value="1" />
		<form:hidden path="userType" value="1" />
		<form:hidden path="editAuthority" value="true" />
		<form:hidden path="deliverAuthority" value="true" />
		<form:hidden path="cancelAuthority" value="true" />
                <div class="form-container">
                    <%--<div class="form-group">
                        <div>
                            <label class="radio-inline" for="mainContact1">
                                <form:radiobutton id="mainContact1" path="mainContact" value="1" /> Primary Contact
                            </label>
                            <label class="radio-inline" for="mainContact2">
                                <form:radiobutton id="mainContact2" path="mainContact" value="2" /> Secondary Contact
                            </label>
                            <label class="radio-inline" for="mainContact3">
                                <form:radiobutton id="mainContact3" path="mainContact" value="0" /> Not a Main Contact
                            </label>
                        </div>
                    </div>--%>
                    <div class="row">
                        <div class="form-group col-md-6">
                            <label for="status">Status *</label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="status" path="status" value="true" /> Active
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="status" path="status" value="false" /> Inactive
                                </label>
                            </div>
                        </div>
                        <%--<div class="form-group col-md-6">
                            <label for="status">User Type *</label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="userType" path="userType" value="1" /> Manager
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="userType" path="userType" value="2" /> Staff Member
                                </label>
                            </div>
                        </div> --%>
                    </div>
                    <div class="row">
                        <div class="form-group col-md-6">
                            <label for="status">File Upload Capability? *</label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="createAuthority" path="createAuthority" value="true" /> Enabled
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="createAuthority" path="createAuthority" value="false" /> Disabled
                                </label>
                            </div>
                        </div> 
                        <%--<div class="form-group col-md-6">
                            <label for="status">Edit Capability? *</label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="editAuthority" path="editAuthority" value="true" /> Enabled
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="editAuthority" path="editAuthority" value="false" /> Disabled
                                </label>
                            </div>
                        </div>--%>
                    </div>
                    <%--<div class="row">
                        <div class="form-group  col-md-6">
                            <label for="status">Send Capability? *</label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="deliverAuthority" path="deliverAuthority" value="true" /> Enabled
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="deliverAuthority" path="deliverAuthority" value="false" /> Disabled
                                </label>
                            </div>
                        </div> 
                        <div class="form-group col-md-6">
                            <label for="status">Cancel Capability? *</label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="cancelAuthority" path="cancelAuthority" value="true" /> Enabled
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="cancelAuthority" path="cancelAuthority" value="false" /> Disabled
                                </label>
                            </div>
                        </div>          
                    </div>--%>
                    <spring:bind path="firstName">
                        <div class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="firstName">First Name *</label>
                            <form:input path="firstName" id="firstName" class="form-control" type="text" maxLength="55" />
                            <form:errors path="firstName" cssClass="control-label" element="label" />
                        </div>
                    </spring:bind>
                    <spring:bind path="lastName">
                        <div class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="lastName">Last Name *</label>
                            <form:input path="lastName" id="lastName" class="form-control" type="text" maxLength="55" />
                            <form:errors path="lastName" cssClass="control-label" element="label" />
                        </div>
                    </spring:bind>
                    <spring:bind path="email">
                        <div class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="email">Email</label>
                            <form:input path="email" id="email" class="form-control" type="text"  maxLength="255" />
                            <form:errors path="email" cssClass="control-label" element="label" />
                        </div>
                    </spring:bind>
                    <spring:bind path="username">
                        <div class="form-group ${status.error ? 'has-error' : '' } ${not empty existingUsername ? 'has-error' : ''}">
                            <label class="control-label" for="username">Username *</label>
                            <form:input path="username" id="username" class="form-control" type="text" maxLength="15" />
                            <form:errors path="username" cssClass="control-label" element="label" />
                            <c:if test="${not empty existingUsername}"><span class="control-label">${existingUsername}</span></c:if>
                            </div>
                    </spring:bind>
                    <spring:bind path="password">
                        <div id="passwordDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="password"><c:if test="${btnValue == 'Update'}"><i>Please leave blank if not changing</i><br/></c:if>Password *</label>
                                <form:input path="password" id="password" class="form-control" type="password" maxLength="15" autocomplete="off"  />
                                <form:errors path="password" cssClass="control-label" element="label" />
                        </div>
                    </spring:bind>
                    <div id="confirmPasswordDiv" class="form-group">
                        <label class="control-label" for="confirmPassword">Confirm Password *</label>
                        <input id="confirmPassword" name="confirmpassword" class="form-control" maxLength="15" autocomplete="off" type="password" value="" />
                        <span id="confimPasswordMsg" class="control-label"></span>
                    </div>
		    <c:if test="${not empty sections}">
			<div class="form-group ${sectionListError ? 'has-error' : '' }" id="sectionListDiv">
			    <label class="control-label" for="section">Available Sections</label>
			    <span id="sectionListMsg" class="control-label"><c:if test="${sectionListError}"><br/>Please assign at least one section to user.</c:if></span>
				<c:forEach items="${sections}" var="section" varStatus="loopStatus">  
				<div class="row">
				    <div class="col-md-6">
					<label class="checkbox-inline">
					    <form:checkbox id="sectionList" path="sectionList" value="${section.id}" />${section.featureName}
					</label>
				    </div>
				</div>
			    </c:forEach>     
			</div>
		    </c:if>
                    <div class="form-group">
                        <input type="button" id="submitButton" rel="${btnValue}" role="button" class="btn btn-primary" value="${btnValue}"/>
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
