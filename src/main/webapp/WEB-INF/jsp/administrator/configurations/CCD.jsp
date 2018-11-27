<%-- 
    Document   : CCD
    Created on : Jan 7, 2015, 12:03:47 PM
    Author     : chadmccue
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="main clearfix" role="main">

    <div class="col-md-12">

        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success" role="alert">
                <strong>Success!</strong> 
                <c:choose>
                    <c:when test="${savedStatus == 'savedElement'}">The new element has been successfully saved!</c:when>
                </c:choose>
            </div>
        </c:if>

        <section class="panel panel-default">
            <div class="panel-body">

                <div class="form-container scrollable"><br />
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty ccdElements}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Element</th>
                                    <th scope="col">Default Value</th>
                                    <th scope="col">Selected Field</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty ccdElements}">
                                    <c:forEach var="elements" items="${ccdElements}">
                                        <tr>
                                            <td scope="row">
                                                <a href="#ccdElementModal" data-toggle="modal" class="editElement" rel="${elements.id}" title="Edit this element">${elements.element}</a>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${elements.defaultValue == ''}">N/A</c:when>
                                                    <c:otherwise>${elements.defaultValue}</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${elements.fieldLabel == ''}">N/A</c:when>
                                                    <c:otherwise>${elements.fieldLabel}</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="actions-col">
                                                <a href="#ccdElementModal" data-toggle="modal" class="editElement btn btn-link" rel="${elements.id}" title="Edit this element">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Edit	
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="7" class="center-text">There are currently no data elements set up.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>  
    </div>         
</div>
<!-- CCD Element modal -->
<div class="modal fade" id="ccdElementModal" role="dialog" tabindex="-1" aria-labeledby="Add CCD Element" aria-hidden="true" aria-describedby="Add CCD Element"></div>