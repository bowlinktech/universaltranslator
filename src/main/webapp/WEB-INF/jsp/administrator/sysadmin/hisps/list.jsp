<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main" rel="dataForTable">
    <div class="col-md-12">
        <c:if test="${not empty param.msg}">
            <div class="alert alert-success">
                <c:choose><c:when test="${param.msg == 'updated'}">The hisp has been successfully updated!</c:when>
                    <c:when test="${param.msg == 'created'}">The hisp has been successfully added!</c:when>
                    <c:when test="${param.msg == 'deleted'}">The hisp has been successfully deleted!</c:when>
                    <c:when test="${param.msg == 'notDeleted'}">The hisp was not deleted.  Please try again.</c:when>
                </c:choose>
            </div>
        </c:if>
        <section class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">HISPS</h3>
            </div>
            <div class="panel-body">
                <div class="table-actions">
                    <div class="form form-inline pull-left">

                    </div>
                    <a href="#hispModal" id="createNewHisp" data-toggle="modal" class="btn btn-primary btn-sm pull-right" title="Create new hisp">  
                        <span class="glyphicon glyphicon-plus"></span>
                    </a>
                </div>

                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty hisps}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Name</th>
                                    <th scope="col">Primary Contact</th>
                                    <th scope="col">Date Created</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty hisps}">
                                    <c:forEach var="hisp" items="${hisps}">
                                        <tr id="dataRow">
                                            <td>
                                                <a href="#hispModal" data-toggle="modal" rel="${hisp.id}" class="hispEdit" title="Edit this HISP">${hisp.hispName}
					    </td>
                                            <td>
                                                ${hisp.primaryContact}</br>${hisp.primaryContactEmail}</br>${hisp.primaryContactPhone}
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${hisp.dateCreated}" type="date" pattern="M/dd/yyyy hh:mm:ss a" />
                                            </td>
                                            <td class="actions-col">
                                                <a href="#hispModal" data-toggle="modal" rel="${hisp.id}" class="hispEdit" title="Edit this HISP">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Edit	
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="4" class="center-text">There where no hisps in the system.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
        </section>
    </div>		
</div>	

<!-- Providers modal -->
<div class="modal fade" id="hispModal" role="dialog" tabindex="-1" aria-labeledby="Add/ Edit HISPS" aria-hidden="true" aria-describedby="Add/Edit HISPS"></div>


