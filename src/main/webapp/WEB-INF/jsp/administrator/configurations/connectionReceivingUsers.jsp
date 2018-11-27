<%-- 
    Document   : connectionSendingUsers
    Created on : Jun 9, 2016, 10:48:02 AM
    Author     : chadmccue
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<table border="1" style="border-color: DBDADA">
    <tr>
        <td style="width:15%"  class="center-text">Assign <br /><input type="checkbox" id="selectAllTgtUsers" value="1" /></td>
        <td style="width:30%">User</td>
        <td style="width:45%">Org Name</td>
        <td style="width:30%"class="center-text">Send Email <br /><input type="checkbox" id="sendAllTgtUsers" value="1" /></td>
    </tr>
    <c:forEach items="${connectonReceivers}" var="receiver">
        <tr>
            <td class="center-text"><input type="checkbox" id="tgtUsers" class="tgtUsers" name="tgtUsers" value="${receiver.id}" <c:if test="${receiver.connectionAssociated == true}">checked</c:if> /></td>
            <td>${receiver.firstName} ${receiver.lastName}</td>
            <td>${receiver.orgName}</td>
            <td class="center-text"><input type="checkbox" id="tgtUsersSendEmail" class="tgtUsersSendEmail" name="tgtUsersSendEmail" value="${receiver.id}" <c:if test="${receiver.sendReceivedEmail == true}">checked</c:if>  /></td>
            </tr>
    </c:forEach>
</table>

