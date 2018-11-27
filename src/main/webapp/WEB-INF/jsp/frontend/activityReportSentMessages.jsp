<%-- 
    Document   : activityReportSentMessages
    Created on : Jun 22, 2016, 11:27:21 AM
    Author     : chadmccue
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<form:form id="viewSentMessagesForm" action="activityReport/sent" method="post">
    <input type="hidden" id="selRow" name="selRow" />
    <input type="hidden" id="fromDateSel"  name="fromDate" />
    <input type="hidden" id="toDateSel" name="toDate" />
    <input type="hidden" id="selOrg" name="selOrg" value="0" />
</form:form>
<div class="rejectedMessageTotal" style="display:none;">${totalRejected}</div>
<table class="table table-hover table-default" >
    <thead>
        <tr>
            <th scope="col">Receiving Organization</th>
            <th scope="col">Message Type</th>
            <th scope="col" class="center-text">Total Sent</th>
            <c:if test="${showOpenClosed == true}">
                <th scope="col" class="center-text">Total Open</th>
                <th scope="col" class="center-text">Total Closed</th>
            </c:if>
            <th scope="col" class="center-text"></th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${not empty sentMessages}">
                <c:forEach var="message" items="${sentMessages}">
                    <tr>
                        <td scope="row">
                            ${message.orgName}
                        </td>
                        <td>
                           ${message.messageTypeName}
                        </td>
                        <td class="center-text sentMessageTotal">
			  ${message.totalSent}
                        </td>
                         <c:if test="${showOpenClosed == true}">
                            <td class="center-text">
				<fmt:formatNumber type = "number" value="${message.totalOpen}" />
                           </td>
                            <td class="center-text">
				<fmt:formatNumber type = "number" value="${message.totalClosed}" />
                           </td>
                        </c:if>
                        <td class="actions-col" style="width:50px;">
                            <a href="javascript:void(0);" rel="${message.rowId}"  class="btn btn-link viewLink">
                                <span class="glyphicon glyphicon-edit"></span>
                                View
                            </a>
                        </td>
                    </tr>
                </c:forEach>
           </c:when>
           <c:otherwise>
                <tr><td colspan="7" class="center-text">You currently have no sent messages that match your selected criteria.</td></tr>
            </c:otherwise>
      </c:choose>                  
    </tbody>
</table>
<script>
   $('#fromDateSel').val($('#fromDate').val());
   $('#toDateSel').val($('#toDate').val());
</script>