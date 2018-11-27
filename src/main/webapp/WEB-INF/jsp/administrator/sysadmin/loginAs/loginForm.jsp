<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main" rel="dataForTable">
    <div class="col-md-12">
        <section class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Login As ${msg}</h3>
            </div>
            <div class="panel-body">
                <form role="form" id="form-admin-login" name='f' action="<c:url value='j_spring_security_check' />" method='POST'>
                    <input type="hidden" id="realUsername" name="realUsername" value="${pageContext.request.userPrincipal.name}" />
                    <fieldset name="login-fields" form="form-admin-login" class="basic-clearfix">           
                        <div id="userNameDiv" class="form-group">
                            <label class="control-label" for="userName">Please confirm your user name</label>
                            <input id="userName" name='j_username' class="form-control" type="text" value=""  autocomplete="off" />
                            <span id="userNameMsg" class="control-label"></span>
                        </div>
                        <div id="passwordDiv" class="form-group">
                            <label class="control-label" for="password">Please enter your password</label>
                            <input id="password" name='j_password' class="form-control" type="password" value=""  autocomplete="off" />
                            <span id="passwordMsg" class="control-label"></span>
                        </div>
                        <div id="loginAsUserDiv" class="form-group">
                            <label class="control-label" for="loginAsUser">Login As</label>
                            <select id="loginAsUser" name="loginAsUser" class="form-control">
                                <option value="">Please select the user you wish to login as</option>
                                <c:forEach items="${usersList}" var="user" varStatus="userCount">
                                    <option value="${user.username}">${user.username}&#160;(${user.firstName}&#160;${user.lastName})&#160;-&#160;${user.orgName}</option>
                                </c:forEach>
                            </select>
                            <span id="loginAsUserMsg" class="control-label"></span>
                        </div>
                        <input type="button" id="submitButton" value="Login As" class="btn btn-primary pull-left" role="button"/>
                    </fieldset>
                </form>
            </div>
        </section>
    </div>		
</div>	


