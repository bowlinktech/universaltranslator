<%-- 
    Document   : activityReportSentMessages
    Created on : Jun 22, 2016, 11:27:21 AM
    Author     : chadmccue
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<form:form id="viewReceivedMessagesForm" action="activityReport/received" method="post">
    <input type="hidden" id="selReceivedRow" name="selRow" />
    <input type="hidden" id="fromDateRec" name="fromDate" />
    <input type="hidden" id="toDateRec" name="toDate" />
    <input type="hidden" id="selOrg" name="selOrg" value="0" />
</form:form>
<table class="table table-hover table-default" >
    <thead>
        <tr>
            <th scope="col">Originating Organization</th>
            <th scope="col">Message Type</th>
            <th scope="col" class="center-text">Total Received</th>
	    <th scope="col" class="center-text">Total Transactions</th>
            <th scope="col" class="center-text"></th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${not empty receivedMessages}">
                <c:forEach var="message" items="${receivedMessages}">
                    <tr>
                        <td scope="row">
                            ${message.orgName}
                        </td>
                        <td>
                           ${message.messageTypeName}
                        </td>
                        <td class="center-text receivedMessageTotal">
                           ${message.totalReceived}
                        </td>
                       <td class="center-text">
                           <fmt:formatNumber type = "number" value="${message.totalTransactions}" />
                        </td>
                        <td class="actions-col" style="width:50px;">
                            <a href="javascript:void(0);" rel="${message.rowId}" class="btn btn-link viewReceivedLink">
                                <span class="glyphicon glyphicon-edit"></span>
                                View
                            </a>
                        </td>
                    </tr>
                </c:forEach>
           </c:when>
           <c:otherwise>
                <tr><td colspan="7" class="center-text">You currently have no received messages that match your selected criteria.</td></tr>
            </c:otherwise>
      </c:choose>                  
    </tbody>
</table>
<script>
   $('#fromDateRec').val($('#fromDate').val());
   $('#toDateRec').val($('#toDate').val());
</script>
