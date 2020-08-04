<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">System Admin Logins</h3>
            <div id="errorMsgDiv" class="text-danger">${failed}</div>
            <div id="msgDiv">${success}</div>
        </div>
        <div class="modal-body">
	    <div class="form-container">
		<table class="table table-striped table-hover table-default" <c:if test="${not empty systemAdminLogins}">id="loginDataTable"</c:if>>
			<thead>
			    <tr>
				<th scope="col" class="center-text">Log In Date</th>
				<th scope="col" class="center-text">Session Length</th>
			    </tr>
			</thead>
			<tbody>
			<c:forEach var="systemAdminLogin" items="${systemAdminLogins}">
			    <tr id="dataRow" scope="row">
				<td class="center-text">
				    <fmt:formatDate value="${systemAdminLogin.dateCreated}" type="date" pattern="M/dd/yyyy h:mm:ss a" />
				</td>
				<td class="center-text">
				    <c:choose>
					<c:when test="${systemAdminLogin.totalTimeLoggedIn > 0}">
					    ${systemAdminLogin.totalTimeLoggedIn} min
					</c:when>
					<c:otherwise>
					    N/A
					</c:otherwise>
				    </c:choose>
				</td>
			    </tr>
			</c:forEach>
		    </tbody>
		</table>
	    </div>
        </div>
    </div>
</div>

<script>
    $('#loginDataTable').dataTable({
	"bStateSave": false,
        "sPaginationType": "bootstrap",
	"bFilter": false,
	"bLengthChange": false,
	"aaSorting": [[ 0, "desc" ]],
	"sDom": '<"top"flpi>rt<"clear">'
    });

</script>