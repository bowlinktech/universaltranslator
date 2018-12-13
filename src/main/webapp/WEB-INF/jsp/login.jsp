<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<div class="login-container" style="width:500px; margin-left: -250px;">
    <c:if test="${not cookie.js.value}">
        JavaScript and cookies are required for this application in order to function correctly
    </c:if>
    <div class="login clearfix">
        <header class="login-header" role="banner">
            <div class="login-header-content">
                <p style="color:white; font-family: sans-serif; font-size: 25px">Health-e-Link Universal Translator</p>
            </div>
        </header>
        <c:if test="${not empty error}">
            <div class="alert alert-danger center-text" role="alert">
                Your login attempt was unsuccessful.<br />
                - ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} -
            </div>
        </c:if>     
        <c:if test="${not empty msg}">
            <c:choose>
                <c:when test="${msg == 'notfound'}">
                    <div class="alert alert-danger center-text" role="alert">
                        No user was found with that reset code.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-success center-text" role="alert">
                        Your password has been updated.
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>
        <form role="form" id="form-admin-login" name='f' action="<c:url value='loginAction' />" method='POST'>
            <fieldset name="login-fields" form="form-admin-login" class="basic-clearfix">
                <div class="form-group ${not empty error ? 'has-error' : '' }">
                    <label class="control-label" for="username">Username</label>
                    <input id="username" name='username' class="form-control" type="text" value="" autofocus="true" autocomplete="off" />
                </div>
                <div class="form-group ${not empty error ? 'has-error' : '' }">
                    <label class="control-label" for="password">Password</label>
                    <input id="password" name='password' class="form-control" type="password" value=""  autocomplete="off" />
                </div>
                <input type="submit" value="Login" class="btn btn-primary pull-right" role="button"/>
                <!--<label for="remember-me" class="pull-left"><input id="j_remember" name="_spring_security_remember_me" type="checkbox" value="1">&nbsp;Remember Me</label>-->
            </fieldset>
        </form>
    </div>
    <p class="login-note"><a href="/forgotPassword" id="forgotPassword" title="Forget Your Password?">Forgot Password?</a></p>
</div>
