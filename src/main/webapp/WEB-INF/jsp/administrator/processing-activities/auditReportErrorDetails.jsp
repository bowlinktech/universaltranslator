<%-- 
    Document   : auditReportErrorDetails
    Created on : Sep 30, 2017, 11:11:57 AM
    Author     : chadmccue
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="hasOutboundError" value="0" />
<c:if test="${not empty errors}">
    <c:forEach var="errorRow" items="${errors}">
        <c:if test="${hasOutboundError == 0 && errorRow[0] == 'true'}"><c:set var="hasOutboundError" value="1" /></c:if>
    </c:forEach>     
</c:if>   

<%-- <div class="pull-right" style="margin-bottom:10px;">
   ${i.index} <button class="btn btn-minier btn-grey print" rel2="${batchId}" rel="${indexVal}"><i class="fa fa-print"> Print</i></button>
</div>--%>
<span><strong>Showing <fmt:formatNumber value = "${fn:length(errors)}" type = "number"/> out of <fmt:formatNumber value = "${totalErrors}" type = "number"/></strong></span>
<div id="errorTable-${indexVal}">
<c:if test="${hasOutboundError == 1}">
<span><span style="color:#ffe3a4;font-weight: bold;margin-top:5px;">Rows</span> = errors found from processing the outbound target file.</span>
</c:if>
<table border="1" class="table table-bordered" id="errordatatable">
    <thead>
	<tr>
	    <c:if test="${not empty customCols}">
		<c:forEach var="colValue" items="${customCols}">
                    <c:if test="${colValue != 'From Outbound'}"><th scope="col">${colValue}</th></c:if>
		</c:forEach>
	    </c:if>
	</tr>
    </thead>
    <tbody>
	<c:choose>
	    <c:when test="${not empty errors}">
		<c:forEach var="errorRow" items="${errors}">
                    <tr <c:if test="${errorRow[0] == 'true'}">bgcolor="#ffe3a4"</c:if>>
			<c:forEach varStatus="i" var="colValue" items="${customCols}">
                            <c:if test="${i.index > 0}">
                                 <td scope="row">${errorRow[i.index]}</td>
                            </c:if>
			</c:forEach>
		    </tr>
		</c:forEach>     
	    </c:when>   
	    <c:otherwise>
		<tr><td colspan="${fn:length(customCols-1)}" class="center-text">There are currently no submitted batches.</td></tr>
	    </c:otherwise>
	</c:choose>           
    </tbody>
</table>
<c:if test="${hasOutboundError == 1}">
<span><span style="color:#ffe3a4;font-weight: bold">Rows</span> = errors found from processing the outbound target file.</span>
</c:if>
</div>
