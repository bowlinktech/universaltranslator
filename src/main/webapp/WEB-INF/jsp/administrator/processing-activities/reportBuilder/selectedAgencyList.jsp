<%-- 
    Document   : agencyList
    Created on : Mar 4, 2021, 10:52:05 AM
    Author     : chadmccue
--%>


<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Selected Agencies</h3>
        </div>
        <div class="modal-body">
            <c:choose>
                <c:when test="${not empty agencies}">
                    <table class="table table-bordered table-striped table-hover table-default">
                        <thead>
                            <tr>
                                <th style="width:90%">Agency</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${agencies}" var="agency">
                                <tr>
                                    <td>${agency.orgName}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p>No agencies were found based on the selected activity report.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>