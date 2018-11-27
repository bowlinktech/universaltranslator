<%-- 
    Document   : forgotPassword
    Created on : Mar 13, 2014, 1:16:39 PM
    Author     : chadmccue
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="login-container" style="width:500px; margin-left: -250px;">
    <div class="login clearfix">
        <header class="login-header" role="banner">
            <div class="login-header-content">
                <a href="<c:url value='/' />" title="Return to home page.">
                    <span class="logo" alt="{Company Name Logo}"></span>
                </a>
            </div>
        </header>
        <form:form id="resetPassword" method="post" role="form">
            <input type="hidden" name="resetCode" value="${resetCode}" />
            <div class="form-container">
                <div id="newPasswordDiv" class="form-group ${status.error ? 'has-error' : '' }">
                    <label class="control-label" for="identifier">New Password</label>
                    <input id="newPassword" name="newPassword" class="form-control" type="password" maxLength="15" />
                    <span id="newPasswordMsg" class="control-label"></span>
                </div>
                <div id="confirmPasswordDiv" class="form-group ${status.error ? 'has-error' : '' }">
                    <label class="control-label" for="identifier">Confirm Password</label>
                    <input id="confirmPassword" name="confirmPassword" class="form-control" type="password" maxLength="15" />
                    <span id="confirmPasswordMsg" class="control-label"></span>
                </div>    
                <div class="form-group">
                    <input type="button" id="submitButton" class="btn btn-primary" value="Continue"/>
                </div>
            </div>
        </form:form>
    </div>
</div>


