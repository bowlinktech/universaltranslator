<%-- 
    Document   : connectionSendingUsers
    Created on : Jun 9, 2016, 10:48:02 AM
    Author     : chadmccue
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<table border="1" style="border-color: DBDADA">
    <tr>
        <td class="center-text" style="width:15%">Type</td>
        <td style="width:45%">Contact Email Address</td>
        <td style="width:30%"class="center-text">Send Email Notification <br /><input type="checkbox" id="sendAllTargetContacts" value="1" /></td>
    </tr>
     <c:choose>
	<c:when test="${not empty receivingContacts}">
	    <c:forEach items="${receivingContacts}" var="receiver">
		<tr>
		    <td class="center-text">${receiver.contactType}</td>
		    <td>${receiver.emailAddress}</td>
		    <td class="center-text">
			<input type="checkbox" id="tgtEmailNotifications" class="tgtEmailNotifications" name="tgtEmailNotifications" value="${receiver.emailAddress}" <c:if test="${receiver.sendEmailNotifications == true}">checked</c:if> />
		    </td>
		</tr>
	    </c:forEach>
	</c:when>
	<c:otherwise>
	    <tr><td colspan="3" class="center-text">There are no contacts saved for this organization</td></tr>
	</c:otherwise>
    </c:choose>
</table>

