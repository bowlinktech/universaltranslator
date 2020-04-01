<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main" rel="dataForTable">
    <div class="col-md-12">
        <c:if test="${not empty param.msg}">
            <div class="alert alert-success">
                <c:choose><c:when test="${param.msg == 'updated'}">The crosswalk has been successfully updated!</c:when>
                    <c:when test="${param.msg == 'created'}">The crosswalk has been successfully added!</c:when>
                    <c:when test="${param.msg == 'deleted'}">The crosswalk has been successfully deleted!</c:when>
                </c:choose>
            </div>
        </c:if>
        <c:if test="${not empty error}" >
                <div class="alert alert-danger" role="alert">
                    The selected file was not found.
                </div>
            </c:if>
        <section class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Standard Crosswalks</h3>
            </div>
            <div class="panel-body">
                <div class="table-actions">
                    <div class="form form-inline pull-left">

                    </div>
                    <a href="#crosswalkModal" id="createNewCrosswalk" data-toggle="modal" class="btn btn-primary btn-sm pull-right" title="Create new Crosswalk">  
                        <span class="glyphicon glyphicon-plus"></span>
                    </a>
                </div>

                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty crosswalkList}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Name</th>
                                    <th scope="col">File Name</th>
                                    <th scope="col">Date Created</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty crosswalkList}">
                                    <c:forEach var="crosswalk" items="${crosswalkList}">
                                        <tr id="dataRow">
                                            <td>
                                                ${crosswalk.name}
                                            </td>
                                            <td>
                                                ${crosswalk.fileName}
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${crosswalk.dateCreated}" type="date" pattern="M/dd/yyyy" />
                                            </td>
                                            <td class="actions-col">
                                                <a href="#crosswalkModal" data-toggle="modal" rel="?i=${crosswalk.id}" class="viewCrosswalk" title="Edit this Crosswalk">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Edit	
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="4" class="center-text">There are no standard crosswalks in the system.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
        </section>
    </div>		
</div>	

<!-- Providers modal -->
<div class="modal fade" id="crosswalkModal" role="dialog" tabindex="-1" aria-labeledby="Add/ Edit Crosswalks" aria-hidden="true" aria-describedby="Add/Edit Crosswalks"></div>


