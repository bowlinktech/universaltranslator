<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>



<table class="table table-striped table-hover table-default">
    <thead>
	<tr>
	    <th scope="col" class="center-text">Field No</th>
	    <th scope="col">Field Name</th>
	    <th scope="col" class="center-text">Required</th>
	</tr>
    </thead>
    <tbody>
	<c:forEach var="tField" items="${sourceConfigurationFields}">
	    <tr class="fieldRow" rel="${tField.id}-${tField.fieldNo}-${tField.fieldDesc}-${tField.validationType}">
		<td scope="row" class="center-text">${tField.fieldNo}</td>
		<td>
		    ${tField.fieldDesc}
		    <input type="hidden" id="validationType_${tField.id}" value="${tField.validationType}" />
		</td>
		<td class="center-text">
		    <input type="checkbox" disabled="disabled" <c:if test="${tField.required == true}">checked</c:if>  />
		</td>
	    </tr>
	</c:forEach>
    </tbody>
</table>