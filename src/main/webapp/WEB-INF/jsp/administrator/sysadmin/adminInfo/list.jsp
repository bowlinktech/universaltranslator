<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main" rel="dataForTable">
    <div class="col-md-12">
        <c:if test="${not empty param.msg}">
            <div class="alert alert-success">
                <c:choose>
                    <c:when test="${param.msg == 'created'}">The system administrator has been successfully added!</c:when>
                </c:choose>
            </div>
        </c:if>
        <section class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">System Administrators</h3>
            </div>
            <div class="panel-body">
                <div class="table-actions">
                    <div class="form form-inline pull-left">

                    </div>
                    <a href="#profileModal" id="createNewSystemAdmin" data-toggle="modal" class="btn btn-primary btn-sm pull-right" title="Create new system administrator">  
                        <span class="glyphicon glyphicon-plus"></span>
                    </a>
                </div>

                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty systemAdmins}">id="sysadmindataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Name</th>
				    <th scope="col">User Role</th>
				    <th scope="col" class="center-text"># of Times Logged In</th>
                                    <th scope="col" class="center-text">Date Last Logged In</th>
                                    <th scope="col" class="center-text"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty systemAdmins}">
                                    <c:forEach var="systemAdmin" items="${systemAdmins}">
                                        <tr id="dataRow" scope="row">
                                            <td>
                                                ${systemAdmin.firstName}&nbsp;${systemAdmin.lastName}
						<br />(<c:choose><c:when test="${systemAdmin.status == true}">active</c:when><c:otherwise>inactive</c:otherwise></c:choose>)
                                            </td>
					    <td>
						<c:choose>
						    <c:when test="${systemAdmin.roleType == 'ROLE_ADMIN'}">
							System Admin
						    </c:when>
						    <c:when test="${systemAdmin.roleType == 'ROLE_OPERATIONSSTAFF'}">
							Operations
						    </c:when>
						</c:choose>
                                            </td>
					    <td class="center-text">
                                               ${systemAdmin.totalLogins}
                                            </td>
					    <td class="center-text">
                                                 <fmt:formatDate value="${systemAdmin.dateLastLoggedIn}" type="date" pattern="M/dd/yyyy h:mm a" />
                                            </td>
                                            <td>
                                                <div class="dropdown pull-left">
                                                    <button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
                                                        <i class="fa fa-cog"></i>
                                                    </button>
                                                    <ul class="dropdown-menu pull-right">
                                                        <li>
                                                            <a href="#profileModal" id="profileButton" rel="${systemAdmin.id}" class="updateprofile1"  data-toggle="modal" title="Edit this admin">
                                                                <span class="glyphicon glyphicon-edit"></span>
                                                                Edit	
                                                            </a>
                                                         </li>  
                                                         <c:if test="${systemAdmin.totalLogins > 0}">
                                                            <li>
                                                                <a href="#lastloginsModal" id="lastloginsButton" rel="${systemAdmin.id}"  data-toggle="modal" title="View All Logins">
                                                                    <span class="glyphicon glyphicon-eye-open"></span>
                                                                    View All Logins	
                                                                </a>
                                                            </li>
                                                         </c:if>
                                                    </ul>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="5" class="center-text">There are currently no system administrators set up.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
        </section>
    </div>		
</div>	

<div class="modal fade" id="profileModal" role="dialog" tabindex="-1" aria-labeledby="Modify Profile" aria-hidden="true" aria-describedby="Modify My Profile"></div>
<div class="modal fade" id="lastloginsModal" role="dialog" tabindex="-1" aria-labeledby="All Logins" aria-hidden="true" aria-describedby="All Logins"></div>
