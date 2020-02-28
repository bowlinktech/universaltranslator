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
	    <c:if test="${showErrorField}"><th scope="col" class="center-text">Error Field <span class="badge badge-help" data-placement="top" data-toggle="tooltip" data-original-title="Select the column that holds the data from the source file to show on the audit report.">?</span></th></c:if>
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
			<select fieldNo="${fields.fieldNo}" fieldDesc="${fields.fieldDesc}" copyErrorField="${showErrorField ? 'yes' : 'no'}" class="form-control matchField">
                            <c:choose>
                                <c:when test="${not empty fields.defaultValue}"><option value="0~${fields.defaultValue}">- Default Value (${fields.defaultValue}) -</option></c:when>
                                <c:otherwise><option value="0">- Blank Value -</option></c:otherwise>
                            </c:choose>
			    <c:forEach var="sourceFields" items="${sourceconfigurationDataElements}">
				<c:if test="${sourceFields.useField == true}">
				    <option value="${sourceFields.fieldNo}" <c:if test="${sourceFields.fieldNo == fields.mappedToField}">selected</c:if>>${sourceFields.fieldDesc} - ${sourceFields.fieldNo}</option>
				</c:if>
			    </c:forEach>
			</select>
		    </div>
		</td>
		<c:if test="${showErrorField}">
		    <td class="center-text">
			<div id="errorField${fields.fieldNo}" class="form-group">
			    <select fieldNo="${fields.fieldNo}" id="errorFieldSel${fields.fieldNo}" class="form-control errorField">
				<option value="0">- Outbound Value -</option>
				<c:forEach var="sourceFields" items="${sourceconfigurationDataElements}">
				    <c:if test="${sourceFields.useField == true}">
					<option value="${sourceFields.fieldNo}" <c:if test="${sourceFields.fieldNo == fields.mappedErrorField}">selected</c:if>>${sourceFields.fieldDesc} - ${sourceFields.fieldNo}</option>
				    </c:if>
				</c:forEach>
			    </select>
			</div>
		    </td>
		</c:if>
	    </tr>
	</c:forEach>
    </tbody>
</table>