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
                    <c:if test="${fn:length(userActivities) > 100}">
                        <div class="form-group">
                            There were a total of ${fn:length(userActivities)} user activities found.  The last 100 activities are displayed.
                        </div>
                    </c:if>
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty userActivities}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">User Name</th>
                                    <th scope="col">User Activity</th>
                                    <th scope="col">Transaction(s) Accessed</th>
                                    <th scope="col">Date / Time</th>                            	
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty userActivities}">     
                                    <c:forEach var="ua" items="${userActivities}" end="99">
                                        <tr>
                                            <td scope="row">${ua.userFirstName} ${ua.userLastName}<br/>
                                                ${ua.orgName}
                                            </td>
                                            <td>
                                                ${ua.activity}<c:if test="${fn:length(ua.activityDesc) > 0}">- ${ua.activityDesc}</c:if>
                                                </td>
                                                <td>
                                                <c:set var="tInIdList" value="${fn:split(ua.transactionInIds, ',')}"/>
                                                <c:choose>
                                                    <c:when test="${fn:length(tInIdList) > 10}">
                                                        <c:forEach items="${ua.transactionInIds}" end="9" var="transactionInId">
                                                            <a href="#messageDetailsModal" data-toggle="modal" rel="${transactionInId}" rel2="0" class="viewLink">
                                                                ${transactionInId}
                                                            </a>
                                                            <br/>
                                                        </c:forEach>
                                                        <a href="#messageDetailsModal" data-toggle="modal" rel="${ua.id}" class="viewMore">more ...</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:forEach var="transactionInId" items="${ua.transactionInIds}">
                                                            <a href="#messageDetailsModal" data-toggle="modal" rel="${transactionInId}" rel2="0" class="viewLink">${transactionInId}</a>
                                                            <br/>
                                                        </c:forEach>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${ua.dateCreated}" type="date" pattern="M/dd/yyyy" />&nbsp;&nbsp;<fmt:formatDate value="${ua.dateCreated}" type="time" pattern="h:mm:ss a" />
                                            </td>                                         
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="7" class="center-text">There are currently no user activities for this batch.</td></tr>
                                </c:otherwise>
                            </c:choose>           
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>
<div class="modal fade" id="messageDetailsModal" role="dialog" tabindex="-1" aria-labeledby="Message Details" aria-hidden="true" aria-describedby="Message Details"></div>