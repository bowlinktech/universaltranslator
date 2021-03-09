<%-- 
    Document   : agencyList
    Created on : Mar 4, 2021, 10:52:05 AM
    Author     : chadmccue
--%>


<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="row">
    <div class="col-md-12">
        <c:choose>
            <c:when test="${not empty agencies}">
                <table class="table table-bordered table-striped table-hover table-default" id="dataTable">
                    <thead>
                        <tr>
                            <th style="width:10%; text-align: center;">
                                <input type="checkbox" id="checkAll" > Check All
                            </th>
                            <th style="width:90%">Agency</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${agencies}" var="agency">
                            <tr>
                                <td style="text-align: center;">
                                    <input type="checkBox" id="selAgency" name="selAgency" value="${agency.id}" />
                                </td>
                                <td>${agency.orgName}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p id="noResults">No agencies were found based on the selected registry type.
            </c:otherwise>
        </c:choose>
    </div>
</div>
