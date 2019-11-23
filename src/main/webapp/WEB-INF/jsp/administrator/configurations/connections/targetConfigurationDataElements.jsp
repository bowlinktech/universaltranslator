<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<table class="table table-striped table-hover table-default">
    <thead>
	<tr>
	    <th scope="col" class="center-text">Field No</th>
	    <th scope="col">Field Name</th>
	    <th scope="col" class="center-text">Use Field</th>
	    <th scope="col" class="center-text">Required</th> 
	    <th scope="col" class="center-text">Matching Field</th>
	</tr>
    </thead>
    <tbody>
	<c:forEach items="${targetConfigurationDataElements}" var="fields" varStatus="field">
	    <tr class="uFieldRow" rel="${fields.fieldNo}" rel2="${field.index}">
		<td scope="row" class="center-text">
		    ${fields.fieldNo}
		</td>
		<td>${fields.fieldDesc}</td>
		<td class="center-text">
		    <input type="checkbox" class="useField" id="useField${fields.fieldNo}" name="targetConfigurationDataElements[${field.index}].useField" <c:if test="${fields.useField == true}">checked</c:if> />
		</td>
		<td class="center-text">
		    <c:choose>
			<c:when test="${fields.required == true}">Required</c:when>
			<c:otherwise>Not Required</c:otherwise>
		    </c:choose>
		</td>
		<td class="center-text">
		    <div id="matchField${fields.fieldNo}" class="form-group">
			<select fieldNo="${fields.fieldNo}" fieldDesc="${fields.fieldDesc}" class="form-control matchField">
			    <option value="0">-</option>
			    <c:forEach var="sourceFields" items="${sourceconfigurationDataElements}">
				<option value="${sourceFields.fieldNo}" <c:if test="${sourceFields.mappedToField == fields.fieldNo}">selected</c:if>>${sourceFields.fieldDesc} - ${sourceFields.fieldNo}</option>
			    </c:forEach>
			</select>
		    </div>
		</td>
	    </tr>
	</c:forEach>
    </tbody>
</table>