<%-- 
    Document   : connectionSendingUsers
    Created on : Jun 9, 2016, 10:48:02 AM
    Author     : chadmccue
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<table border="1" style="border-color: DBDADA">
    <tr>
        <td style="width:15%"  class="center-text">Assign <br /><input type="checkbox" id="selectAllSrcUsers" value="1" /></td>
        <td style="width:30%">User</td>
        <td style="width:45%">Org Name</td>
        <td style="width:30%"class="center-text">Send Email <br /><input type="checkbox" id="sendAllSrcUsers" value="1" /></td>
    </tr>
    <c:forEach items="${connectionSenders}" var="sender">
        <tr>
            <td class="center-text"><input type="checkbox" id="srcUsers" class="srcUsers" name="srcUsers" value="${sender.id}" <c:if test="${sender.connectionAssociated == true}">checked</c:if> /></td>
            <td>${sender.firstName} ${sender.lastName}</td>
            <td>${sender.orgName}</td>
            <td class="center-text"><input type="checkbox" id="srcUsersSendEmail" class="srcUsersSendEmail" name="srcUsersSendEmail" value="${sender.id}" <c:if test="${sender.sendSentEmail == true}">checked</c:if>  /></td>
            </tr>
    </c:forEach>
</table>

