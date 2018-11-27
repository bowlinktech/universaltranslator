<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>


<div class="main clearfix" role="main" rel="lookUpTablesList">
    <div class="col-md-12">
        <section class="panel panel-default">
            <div class="panel-body">
                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty tableList}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Table Name</th>
                                    <%--<th scope="col" class="center-text">Number of Columns</th> 
                                    <th scope="col">Description</th>--%>
                                <th scope="col" class="center-text">Number of Rows</th>
                                <th scope="col"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty tableList}">
                                    <c:forEach var="tableInfo" items="${tableList}">
                                        <c:if test="${tableInfo.columnNum > 6}">
                                            <c:set var="link" value="data/nstd/${tableInfo.urlId}"/>
                                        </c:if>
                                        <c:if test="${tableInfo.columnNum <= 6}">
                                            <c:set var="link" value="data/std/${tableInfo.urlId}"/>
                                        </c:if> 
                                        <tr id="tableInfoRow" style="cursor: pointer">
                                            <td scope="row">
                                                <a href="${link}" title="View Data">${tableInfo.displayName}</a>
                                            </td>
                                            <%--<td class="center-text">${tableInfo.columnNum}</td> 
                                             <td class="center-text">${tableInfo.description}</td>--%>
                                            <td class="center-text">${tableInfo.rowNum}</td>

                                            <%-- not sure if we should let users edit our look up tables--%> 
                                            <td class="actions-col">
                                                <a href="${link}" class="btn btn-link" title="Edit data for this table">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    View Data	
                                                </a>
                                            </td>

                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="4" class="center-text">There are currently no look up tables set up.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>	
                </div>
            </div>
        </section>
    </div>		
</div>
