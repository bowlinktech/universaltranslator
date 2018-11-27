<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>


<div class="main clearfix" role="main" rel="listoforganizationusers">
    <div class="col-md-12">

        <c:if test="${not empty param.msg}" >
            <div class="alert alert-success">
                <strong>Success!</strong> 
                <c:choose><c:when test="${param.msg == 'updated'}">The system user has been successfully updated!</c:when><c:when test="${param.msg == 'created'}">The system user has been successfully added!</c:when></c:choose>
                    </div>
        </c:if>

        <section class="panel panel-default">
            <div class="panel-body">
                <dt>
                <dt>Organization Summary:</dt>
                <dd><strong>Organization:</strong> ${orgName}</dd>
                </dt>
            </div>
        </section>
        <section class="panel panel-default">
            <div class="panel-heading">
                <div class="pull-right">
                    <a href="#systemUsersModal" data-toggle="modal" class="btn btn-primary btn-xs btn-action" id="createNewUser" title="Add New User">Add New User</a>
                </div>
                <h3 class="panel-title">System Users</h3>
            </div>
            <div class="panel-body">


                <div class="form-container scrollable">
                    <br /><table class="table table-striped table-hover table-default" <c:if test="${not empty userList}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Name</th>
                                    <th scope="col" class="center-text">User Type</th>
                                    <th scope="col" class="center-text">Date Created</th>
                                    <th scope="col" class="center-text">Times Logged In</th>
                                    <th scope="col" class="center-text">Login As</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty userList}">
                                    <c:forEach var="user" items="${userList}">
                                        <tr id="userRow">
                                            <td scope="row"><a href="#systemUsersModal" data-toggle="modal" rel="${user.firstName}${user.lastName}?i=${user.id}" class="userEdit" title="Edit this user">${user.firstName}&nbsp;${user.lastName}</a><br />(<c:choose><c:when test="${user.status == true}">active</c:when><c:otherwise>inactive</c:otherwise></c:choose>)</td>
                                            <td class="center-text"><c:choose><c:when test="${user.userType == 1}">Manager</c:when><c:otherwise>Staff Member</c:otherwise></c:choose></td>
                                            <td class="center-text"><fmt:formatDate value="${user.dateCreated}" type="date" pattern="M/dd/yyyy" /></td>
                                            <td class="center-text">${userFunctions.findTotalLogins(user.id)}</td>
                                            <td class="center-text">
                                                <c:if test="${user.status}">
                                                    <a href="#loginAsModal" data-toggle="modal" rel="${user.username}" class="btn btn-link loginAs" title="Login as this user">
                                                        Login as
                                                    </a>
                                                </c:if>
                                                <c:if test="${user.status == false}">
                                                    Inactive
                                                </c:if>
                                            </td>
                                            <td class="actions-col">
                                                <a href="#systemUsersModal" data-toggle="modal" rel="${user.firstName}${user.lastName}?i=${user.id}" class="btn btn-link userEdit" title="Edit this user">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Edit
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="7" class="center-text">There are currently no organization users set up.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<p rel="${currentPage}" id="currentPageHolder" style="display:none"></p>

<!-- Providers modal -->
<div class="modal fade" id="systemUsersModal" role="dialog" tabindex="-1" aria-labeledby="Add System Users" aria-hidden="true" aria-describedby="Add new system users"></div>
<div class="modal fade" id="loginAsModal" role="dialog" tabindex="-1" aria-labeledby="Login As User" aria-hidden="true" aria-describedby="Login As User"></div>

