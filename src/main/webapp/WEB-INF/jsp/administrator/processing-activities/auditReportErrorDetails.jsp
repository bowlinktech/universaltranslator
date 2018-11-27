<%-- 
    Document   : auditReportErrorDetails
    Created on : Sep 30, 2017, 11:11:57 AM
    Author     : chadmccue
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="pull-right" style="margin-bottom:10px;">
   ${i.index} <button class="btn btn-minier btn-grey print" rel2="${batchId}" rel="${indexVal}"><i class="fa fa-print"> Print</i></button>
</div>
<span><strong>Showing ${fn:length(errors)} out of <fmt:formatNumber value = "${totalErrors}" type = "number"/></strong></span>
<div id="errorTable-${indexVal}">
<table border="1" class="table table-striped table-hover table-bordered">
    <thead>
	<tr>
	    <c:if test="${not empty customCols}">
		<c:forEach var="colValue" items="${customCols}">
		    <th scope="col">${colValue}</th>
		</c:forEach>
	    </c:if>
	</tr>
    </thead>
    <tbody>
	<c:choose>
	    <c:when test="${not empty errors}">
		<c:forEach var="errorRow" items="${errors}">
		    <tr>
			<c:forEach varStatus="i" var="colValue" items="${customCols}">
			     <td scope="row">${errorRow[i.index]}</td>
			</c:forEach>
		    </tr>
		</c:forEach>     
	    </c:when>   
	    <c:otherwise>
		<tr><td colspan="${fn:length(customCols)+2}" class="center-text">There are currently no submitted batches.</td></tr>
	    </c:otherwise>
	</c:choose>           
    </tbody>
</table>
</div>
