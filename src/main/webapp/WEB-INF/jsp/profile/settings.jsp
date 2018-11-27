<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Account Settings</h3>
            <c:if test="${not empty success}" >
                <div class="alert alert-success" role="alert">
                    <strong>${success}</strong> 
                </div>
            </c:if>
        </div>
        <div class="modal-body">
            <form:form id="userdetailsform" commandName="userdetails" modelAttribute="userdetails"  method="post" role="form">
                <form:hidden path="id" id="id" />
                <form:hidden path="orgId" id="orgId" />
                <form:hidden path="dateCreated" />
               		<spring:bind path="firstName">
                        <div class="form-group" id="firstNameDiv">
                            <label class="control-label" for="firstName">First Name *</label>
                            <form:input path="firstName" id="firstName" class="form-control" type="text" maxLength="55" />
                            <span id="firstNameMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <spring:bind path="lastName">
                        <div class="form-group"  id="lastNameDiv">
                            <label class="control-label" for="lastName">Last Name *</label>
                            <form:input path="lastName" id="lastName" class="form-control" type="text" maxLength="55" />
                            <span id="lastNameMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <spring:bind path="email">
                        <div class="form-group" id="emailDiv">
                            <label class="control-label" for="email">Email</label>
                            <form:input path="email" id="email" class="form-control" type="text"  maxLength="255" />
                            <span id="emailMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <spring:bind path="username">
                        <div class="form-group ${not empty existingUsername ? 'has-error' : ''}" id="userNameDiv">
                            <label class="control-label" for="username">Username *</label>
                            <form:input path="username" id="username" class="form-control" type="text" maxLength="15" />
                            <span id="userNameMsg" class="control-label"></span>
                            <c:if test="${not empty existingUsername}"><span class="control-label">${existingUsername}</span></c:if>
                            </div>
                    </spring:bind>
                    <spring:bind path="password">
                        <div id="passwordDiv" class="form-group">
                            <label class="control-label" for="password"><i>Please leave blank if not changing</i><br/>Password *</label>
                            <form:input path="password" id="password" class="form-control" type="password" maxLength="15" autocomplete="off"  />
                            <span id="passwordMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <div id="confirmPasswordDiv" class="form-group">
                        <label class="control-label" for="confirmPassword">Confirm Password *</label>
                        <input id="confirmPassword" name="confirmpassword" class="form-control" maxLength="15" autocomplete="off" type="password"/>
                        <span id="confimPasswordMsg" class="control-label"></span>
                    </div>
                    <div class="form-group">
                        <input type="button" id="submitButton" rel="${btnValue}" role="button" class="btn btn-primary" value="${btnValue}"/>
                    </div>
            </form:form>
        </div>
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function() {
        $("input:text,form").attr("autocomplete", "off");
    });
</script>
