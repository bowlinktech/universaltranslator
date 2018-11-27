<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="main clearfix" role="main">
    <div class="col-md-12">

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
                <h3 class="panel-title">Configurations</h3>
            </div>
            <div class="panel-body">

                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty configs}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Configuration Name</th>
                                    <th scope="col">Message Type</th>
                                    <th scope="col">Configuration Type</th>
                                    <th scope="col" class="center-text">Transport Method</th>
                                    <th scope="col" class="center-text">Date Created</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty configs}">
                                    <c:forEach var="config" items="${configs}">
                                        <tr style="cursor: pointer">
                                            <td scope="row">
                                                <a href="<c:url value='/administrator/configurations/details?i=${config.id}' />" title="Edit this configuration">${config.configName}</a>
                                                <br />
                                                (<c:choose><c:when test="${config.status == true}">active</c:when><c:otherwise>inactive</c:otherwise></c:choose>)
                                                    </td>
                                                    <td>
                                                ${config.messageTypeName}
                                            </td>
                                            <td>
                                                <c:choose><c:when test="${config.type == 1}">Source Configuration</c:when><c:otherwise>Target Configuration</c:otherwise></c:choose>
                                                    </td>
                                                    <td class="center-text">
                                                ${config.transportMethod}
                                            </td>
                                            <td class="center-text"><fmt:formatDate value="${config.dateCreated}" type="date" pattern="M/dd/yyyy" /></td>
                                            <td class="actions-col">
                                                <a href="<c:url value='/administrator/configurations/details?i=${config.id}' />" class="btn btn-link" title="Edit this configuration">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    View	
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="6" class="center-text">There are currently no configurations set up for this organization.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>		
</div>	
