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
	<c:forEach var="tField" items="${helConfigurationFields}">
	    <tr class="fieldRow" rel="${tField.id}-${tField.dspPos}-${tField.dataElementName}-${tField.validationId}">
		<td scope="row" class="center-text">${tField.dspPos}</td>
		<td>
		    ${tField.dataElementName}
		    <input type="hidden" id="validationType_${tField.id}" value="${tField.validationName}" />
		</td>
		<td class="center-text">
		    <input type="checkbox" disabled="disabled" <c:if test="${tField.requiredField == true}">checked</c:if>  />
		</td>
	    </tr>
	</c:forEach>
    </tbody>
</table>