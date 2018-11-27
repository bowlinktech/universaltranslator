<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="main clearfix" role="main">
    <div class="col-md-12">

        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success">
                <strong>Success!</strong> 
                <c:choose>
                    <c:when test="${savedStatus == 'updated'}">The HL7 Spec has been successfully updated!</c:when>
                    <c:when test="${savedStatus == 'created'}">The HL7 Spec has been successfully added!</c:when>
                </c:choose>
            </div>
        </c:if>

        <section class="panel panel-default">
            <div class="panel-body">

                <div class="form-container scrollable"><br />
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty hl7Versions}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">HL7 Spec Name</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty hl7Versions}">
                                    <c:forEach var="hl7" items="${hl7Versions}">
                                        <tr id="hl7Row" rel="${hl7.id}" style="cursor: pointer">
                                            <td scope="row">
                                                <a href="javascript:void(0);" title="Edit this HL7 Version">${hl7.name}</a>
                                            </td>
                                            <td class="actions-col">
                                                <a href="javascript:void(0);" class="btn btn-link" title="Edit this HL7 Version">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Edit	
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="7" class="center-text">There are currently no HL7 versions set up.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>		
</div>