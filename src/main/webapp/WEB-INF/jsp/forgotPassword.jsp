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
        <div id="noResults" class="alert alert-danger center-text" role="alert" style="display:none">
            <strong>No Search Results</strong><br />
            Your search did not return any results. Please try again with other information.
        </div>            
        <div id="emailSent" class="alert alert-success center-text" role="alert" style="display:none">
            <strong>Email Sent</strong><br />
            An email has been sent that will contain a link to reset your password.
        </div>
        <form:form id="passwordForm" method="post" role="form">
            <div class="form-container">
                <div id="brochureTitleDiv" class="form-group ${status.error ? 'has-error' : '' }">
                    <label class="control-label" for="identifier">Email, Username or Full Name</label>
                    <input id="identifier" name="identifier" class="form-control" type="text" maxLength="255" />
                </div>
                <div class="form-group">
                    <input type="button" id="submitButton" class="btn btn-primary" value="Search"/>
                </div>
            </div>
        </form:form>
    </div>
</div>


