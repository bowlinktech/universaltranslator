<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                    <dt>Batch Summary:</dt>
                    <dd><strong>Batch ID:</strong> ${batchDetails.utBatchName}</dd>
                    <dd><strong>Source Organization:</strong> ${batchDetails.orgName}</dd>
                    <dd><strong>Date Submitted:</strong> <fmt:formatDate value="${batchDetails.dateSubmitted}" type="date" pattern="M/dd/yyyy" />&nbsp;&nbsp;<fmt:formatDate value="${batchDetails.dateSubmitted}" type="time" pattern="h:mm:ss a" /></dd>
                    </dt>
                </div>
            </section>
        </div>
    </div>
    <div class="col-md-12">
        <section class="panel panel-default">
            <div class="panel-body">

                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" id="batchActivitiesDataTable">
                            <thead>
                                <tr>
				    <th scope="col">Id</th>
                                    <th scope="col">Activity</th>
                                    <th scope="col">Date / Time</th>                            	
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty batchActivities}">     
                                    <c:forEach var="ba" items="${batchActivities}">
                                        <tr>
					    <td>${ba.id}</td>
                                            <td>
                                                ${ba.activity}
					    </td>
                                            <td>
                                                <fmt:formatDate value="${ba.dateCreated}" type="date" pattern="M/dd/yyyy" />&nbsp;&nbsp;<fmt:formatDate value="${ba.dateCreated}" type="time" pattern="h:mm:ss a" />
                                            </td>                                         
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="3" class="center-text">There are currently no batch activities for this batch.</td></tr>
                                </c:otherwise>
                            </c:choose>           
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>