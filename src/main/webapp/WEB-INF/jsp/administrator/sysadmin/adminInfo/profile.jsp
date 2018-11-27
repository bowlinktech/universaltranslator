<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">System Administrator Profile</h3>
            <div id="errorMsgDiv" class="text-danger">${failed}</div>
            <div id="msgDiv">${success}</div>
        </div>
        <div class="modal-body">
            <form:form id="userdetailsform" commandName="userdetails" modelAttribute="userdetails"  method="post" role="form">
                <form:hidden path="id" id="id" />
                <form:hidden path="orgId" id="orgId" />
                <form:hidden path="dateCreated" />
                <div class="form-container">
		    <div class="form-group">
			<label for="status">Status * </label>
			<div>
			    <label class="radio-inline">
				<form:radiobutton id="status" path="status" value="true"/>Active 
			    </label>
			    <label class="radio-inline">
				<form:radiobutton id="status" path="status" value="false"/>Inactive
			    </label>
			</div>
		    </div>
		     <spring:bind path="roleId">
                        <div id="roleDiv" class="form-group">
                            <label class="control-label" for="role">Role *</label>
                            <form:select path="roleId" class="form-control">
				<option value="1" <c:if test="${userdetails.roleId == 1}">selected</c:if>>System Administrator</option>
				<option value="3" <c:if test="${userdetails.roleId == 3}">selected</c:if>>Operations Staff</option>
			    </form:select>
                            <span id="froleMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <spring:bind path="firstName">
                        <div id="firstNameDiv" class="form-group">
                            <label class="control-label" for="firstName">First Name *</label>
                            <form:input path="firstName" id="firstName" class="form-control" type="text" maxLength="55" />
                            <form:errors path="firstName" cssClass="control-label" element="label" />
                            <span id="firstNameMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <spring:bind path="lastName">
                        <div id="lastNameDiv" class="form-group">
                            <label class="control-label" for="lastName">Last Name *</label>
                            <form:input path="lastName" id="lastName" class="form-control" type="text" maxLength="55" />
                            <form:errors path="lastName" cssClass="control-label" element="label" />
                            <span id="lastNameMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
		    <spring:bind path="username">
                        <div id="usernameDiv" class="form-group">
                            <label class="control-label" for="username">Username *</label>
                            <form:input path="username" id="username" class="form-control" type="text"  maxLength="25" />
                            <form:errors path="username" cssClass="control-label" element="label" />
                            <span id="usernameMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <spring:bind path="email">
                        <div id="emailDiv" class="form-group">
                            <label class="control-label" for="email">Email *</label>
                            <form:input path="email" id="email" class="form-control" type="text"  maxLength="255" />
                            <form:errors path="email" cssClass="control-label" element="label" />
                            <span id="emailMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
		    <c:choose>
			<c:when test="${userdetails.id > 0}">
			    <div id="existingPasswordDiv" class="form-group">
				<label class="control-label" for="existingPassword">Existing Password *</label>
				<input id="existingPassword" name="existingPassword" class="form-control" maxLength="15" autocomplete="off" type="password" value="" />
				<span id="existingPasswordMsg" class="control-label"></span>
			    </div>
			    <div id="newPasswordDiv" class="form-group">
				<label class="control-label" for="newPassword">New Password *<br/><i>Leave blank if not updating</i></label>
				<input id="newPassword" name="newPassword" class="form-control" maxLength="15" 
				       autocomplete="off" type="password"/>
				<span id="newPasswordMsg" class="control-label"></span>
			    </div>

			    <div id="confirmPasswordDiv" class="form-group">
				<label class="control-label" for="confirmPassword">Confirm New Password *</label>
				<input id="confirmPassword" name="confirmpassword" class="form-control" maxLength="15" 
				       autocomplete="off" type="password"/>
				<span id="confimPasswordMsg" class="control-label"></span>
			    </div>       
			</c:when>
			<c:otherwise>
			    <div id="newPasswordDiv" class="form-group">
				<label class="control-label" for="newPassword">Password *</label>
				<input id="newPassword" name="newPassword" class="form-control" maxLength="15" 
				       autocomplete="off" type="password"/>
				<span id="newPasswordMsg" class="control-label"></span>
			    </div>

			    <div id="confirmPasswordDiv" class="form-group">
				<label class="control-label" for="confirmPassword">Confirm Password *</label>
				<input id="confirmPassword" name="confirmpassword" class="form-control" maxLength="15" 
				       autocomplete="off" type="password"/>
				<span id="confimPasswordMsg" class="control-label"></span>
			    </div>     
			</c:otherwise>
		    </c:choose>
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
