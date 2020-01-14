<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="showStrikethrough" value="false" />

<c:forEach items="${sourceConfigurationDataElements}" var="fields" >
    <c:if test="${fields.useField == false}">
	<c:set var="showStrikethrough" value="true" />
    </c:if>
</c:forEach>

<c:if test="${showStrikethrough == true}"><p class="text-danger"><strong>Strikethrough rows are not in use.</strong></p></c:if>
<table class="table table-striped table-hover table-default" style="border-collapse:collapse">
    <thead>
	<tr>
	    <th scope="col" class="center-text">Field No</th>
	    <th scope="col">Field Name</th>
	    <th scope="col" class="center-text">Required</th> 
	</tr>
    </thead>
    <tbody>
	<c:forEach items="${sourceConfigurationDataElements}" var="fields" varStatus="field">
	    <tr class="uFieldRow ${fields.useField == false ? 'strikeout':''}" rel="${fields.fieldNo}" rel2="${field.index}">
		<td scope="row" class="center-text">
		    ${fields.fieldNo}
		</td>
		<td>${fields.fieldDesc}</td>
		<td class="center-text">
		    <c:choose>
			<c:when test="${fields.required == true}">Required</c:when>
			<c:otherwise>Not Required</c:otherwise>
		    </c:choose>
		</td>
	    </tr>
	</c:forEach>
    </tbody>
</table>