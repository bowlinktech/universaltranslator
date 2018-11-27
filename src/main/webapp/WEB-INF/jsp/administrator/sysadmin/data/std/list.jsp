<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>


<div class="main clearfix" role="main" rel="dataForTable">
    <div class="col-md-12">
        <div class="alert alert-success">
            <c:choose>
                <c:when test="${param.msg == 'updated'}">The look up data has been successfully updated!</c:when>
                <c:when test="${param.msg == 'created'}">The look up data has been successfully added!</c:when>
                <c:when test="${savedStatus == 'deleted'}">The look up data has been successfully deleted!</c:when>
                <c:when test="${savedStatus == 'notDeleted'}">The look up data has NOT been deleted!  Please try again.</c:when>
            </c:choose>
        </div>

        <section class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><c:if test="${not empty tableInfo}">Data for "${tableInfo.displayName}" Table</c:if></h3>
                </div>
                <div class="panel-body">
                    <div class="table-actions">
                        <div class="form form-inline pull-left">

                        </div>
                        <a href="#addLUDataModal" id="createNewdataItem" data-toggle="modal" class="btn btn-primary btn-sm pull-right" title="Add look up data">  
                            <span class="glyphicon glyphicon-plus"></span>
                        </a>
                    </div>

                    <div class="form-container scrollable">
                        <table class="table table-striped table-hover table-default" <c:if test="${not empty dataList}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Universal Translator<br/>Crosswalk Value</th>
                                    <th scope="col">Display Text</th>
                                    <th scope="col">Description</th>
                                    <th scope="col" class="center-text">Date Created</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty dataList}">
                                    <c:forEach var="dataItem" items="${dataList}">
                                        <tr id="dataRow">
                                            <td>${dataItem.id}</td>
                                            <td scope="row"><a href="#addLUDataModal" data-toggle="modal" rel="${dataItem.id}" class="dataEdit" title="Edit this data">${dataItem.displayText}</a>
                                                <br />(<c:choose><c:when test="${dataItem.status == true}">active</c:when><c:otherwise>inactive</c:otherwise></c:choose><c:if test="${dataItem.custom == true}">, custom data</c:if>)</td>
                                                    <td>
                                                ${dataItem.description}
                                            </td>
                                            <td class="center-text"><fmt:formatDate value="${dataItem.dateCreated}" type="date" pattern="M/dd/yyyy" /></td>
                                            <td class="actions-col">
                                                <a href="#addLUDataModal" data-toggle="modal" rel="${dataItem.id}" class="dataEdit" title="Edit this data">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Edit	
                                                </a>
                                                <a href="javascript:void(0);" rel="${dataItem.id}" class="btn btn-link dataItemDelete" title="Delete this row">
                                                    <span class="glyphicon glyphicon-remove"></span>
                                                    Delete
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="5" class="center-text">There where no items found for this table.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>		
</div>	

<p rel="${goToURL}" id="goToURL" style="display:none"></p>
<p rel="${urlIdInfo}" id="urlIdInfo" style="display:none"></p>

<!-- Providers modal -->
<div class="modal fade" id="addLUDataModal" role="dialog" tabindex="-1" aria-labeledby="Add look up data" aria-hidden="true" aria-describedby="Add look up data" rel="${tableInfo.displayName}"></div>


