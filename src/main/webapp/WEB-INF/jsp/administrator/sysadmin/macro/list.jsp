<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main" rel="dataForTable">
    <div class="col-md-12">
        <c:if test="${not empty param.msg}">
            <div class="alert alert-success">
                <c:choose><c:when test="${param.msg == 'updated'}">The macro has been successfully updated!</c:when>
                    <c:when test="${param.msg == 'created'}">The macro has been successfully added!</c:when>
                    <c:when test="${param.msg == 'deleted'}">The macro has been successfully deleted!</c:when>
                    <c:when test="${param.msg == 'notDeleted'}">The macro was not deleted.  Please try again.</c:when>
                </c:choose>
            </div>
        </c:if>
        <section class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Macros</h3>
            </div>
            <div class="panel-body">
                <div class="table-actions">
                    <div class="form form-inline pull-left">

                    </div>
                    <a href="#macroModal" id="createNewMacro" data-toggle="modal" class="btn btn-primary btn-sm pull-right" title="Create new macro">  
                        <span class="glyphicon glyphicon-plus"></span>
                    </a>
                </div>

                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty macroList}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Category</th>
                                    <th scope="col">Macro Name</th>
                                    <th scope="col">Formula</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty macroList}">
                                    <c:forEach var="macro" items="${macroList}">
                                        <tr id="dataRow">
                                            <td>
                                                <c:if test="${macro.categoryId == 1}">Data Manipulation</c:if>
                                                <c:if test="${macro.categoryId == 2}">Processing</c:if>
                                            </td>
                                            <td>
                                                <a href="#macroModal" data-toggle="modal" rel="${macro.id}" class="macroEdit" title="Edit this macro">
                                                <c:choose>
                                                    <c:when test="${fn:length(macro.macroName) > 255}">
                                                        ${fn:substring(macro.macroName,0,254)}...
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${macro.macroName}
                                                    </c:otherwise>
                                                </c:choose>
                                                </a>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${fn:length(macro.formula) > 45}">
                                                        ${fn:substring(macro.formula,0,44)}...
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${macro.formula}
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="actions-col">
                                                <a href="#macroModal" data-toggle="modal" rel="${macro.id}" class="macroEdit" title="Edit this macro">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Edit	
                                                </a>
                                                <a href="javascript:void(0);" rel="${macro.id}" class="btn btn-link marcoDelete" title="Delete this row">
                                                    <span class="glyphicon glyphicon-remove"></span>
                                                    Delete
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="5" class="center-text">There where no macros in the system.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
        </section>
    </div>		
</div>	

<!-- Providers modal -->
<div class="modal fade" id="macroModal" role="dialog" tabindex="-1" aria-labeledby="Add/ Edit Macros" aria-hidden="true" aria-describedby="Add/Edit Macros"></div>


