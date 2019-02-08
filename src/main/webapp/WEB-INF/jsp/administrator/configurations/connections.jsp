<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<div class="main clearfix full-width" role="main">
    <div class="col-md-12">
        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success">
                <strong>Success!</strong> 
                <c:choose>
                    <c:when test="${savedStatus == 'created'}">The configuration connection has been successfully created!</c:when>
                    <c:when test="${savedStatus == 'updated'}">The configuration connection has been successfully updated!</c:when>
                </c:choose>
            </div>
        </c:if>
        <c:if test="${not empty param['msg']}" >
            <div class="alert alert-success">
                <strong>Success!</strong> 
                The configuration connection status has been successfully updated!
            </div>
        </c:if>

        <section class="panel panel-default">
            <div class="panel-body">

                <div class="form-container scrollable"><br />
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty connections}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Source Details</th>
                                    <th scope="col">Target Details</th>
                                    <th scope="col" class="center-text">Date Created</th>
                                    <th scope="col" class="center-text">Status</th>
                                    <th scope="col" class="center-text"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty connections}">
                                    <c:forEach var="connection" items="${connections}">
                                        <tr>
                                            <td scope="row">
                                                Organization: <strong>${connection.srcConfigDetails.getOrgName()}</strong>
						<br />
						Configuration: <strong>${connection.srcConfigDetails.getconfigName()} (Id: ${connection.srcConfigDetails.getId()})</strong>
						<br />
						Transport Method: <strong>${connection.srcConfigDetails.gettransportMethod()}</strong>
                                            </td>
					    <td scope="row">
                                                Organization: <strong>${connection.tgtConfigDetails.getOrgName()}</strong>
						<br />
						Configuration: <strong>${connection.tgtConfigDetails.getconfigName()} (Id: ${connection.tgtConfigDetails.getId()})</strong>
						<br />
						Transport Method: <strong>${connection.tgtConfigDetails.gettransportMethod()}</strong>
                                            </td>
                                            <td class="center-text"><fmt:formatDate value="${connection.dateCreated}" type="date" pattern="M/dd/yyyy" /></td>
                                            <td class="center-text actions-col">
                                                <c:choose>
                                                    <c:when test="${connection.status == true}">
                                                        Active
                                                    </c:when>
                                                    <c:otherwise>
                                                        Inactive
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="center-text actions-col">
                                                <a href="#connectionsModal" data-toggle="modal" rel="${connection.id}" class="btn btn-link connectionEdit" title="Edit this connection">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Edit
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="8" class="center-text">There are currently no configuration connections set up.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>

<!-- Connections modal -->
<div class="modal fade" id="connectionsModal" role="dialog" tabindex="-1" aria-labeledby="Add Configuration Connection" aria-hidden="true" aria-describedby="Add Configuration Connection"></div>