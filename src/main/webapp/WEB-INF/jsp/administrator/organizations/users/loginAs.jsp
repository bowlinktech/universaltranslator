<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Login As User ${msg}</h3>
        </div>
        <div class="modal-body">

            <form role="form" id="form-admin-login" name='f' action="<c:url value='j_spring_security_check' />" method='POST' target="_parent">
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
                                <option value="${user.username}">${user.username}&#160;(${user.firstName}&#160;${user.lastName})</option>
                            </c:forEach>
                        </select>
                        <span id="loginAsUserMsg" class="control-label"></span>
                    </div>
                    <input type="button" id="submitLoginAsButton" value="Login As" class="btn btn-primary pull-left" role="button"/>
                </fieldset>
            </form>
        </div>
    </div>
</div>

<script type="text/javascript">
    require(['./main'], function () {
        require(['jquery'], function ($) {
            var loginUser = '${loginAsUser}';
            $('#loginAsUser').val(loginUser);
        });
    });
</script>


