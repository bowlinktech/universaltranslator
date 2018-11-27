<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<div class="main clearfix" role="main">
    <div class="col-md-12">
        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success">
                <strong>Success!</strong> 
                <c:choose><c:when test="${savedStatus == 'created'}">The message type has been successfully created!</c:when><c:when test="${savedStatus == 'fieldcreated'}">The new form field has been successfully created!</c:when></c:choose>
             </div>
        </c:if>
        <div class="alert alert-success mappingsUpdated" style="display:none;">
            <strong>Success!</strong> 
            The field mappings have been successfully updated!
        </div>
        <div id="mappingErrorMsgDiv" class="alert alert-danger" style="display:none;">
            <strong>There is an error with your Field Mappings, scroll to find the highlighted field!</strong>
        </div>

        <form:form id="fieldMappings" modelAttribute="messageTypeDetails" method="post" role="form">
	    <section class="panel panel-default">
		<div class="panel-heading">
		    <div class="pull-right">
			<a href="#newFieldModal" data-toggle="modal" class="btn btn-primary btn-xs btn-action" id="addNewField" title="Add New Field">Add New Field</a>
		    </div>
		    <h3 class="panel-title"><strong>Message Type Fields</strong></h3>
		</div>
		<div class="panel-body">
		    <div class="form-container scrollable">
			<table class="table table-striped table-hover bucketTable_${i}">
			    <thead>
				<tr>
				    <th scope="col">Field Name</th>
				    <th scope="col" class="text-center">Required Field</th>
				    <th scope="col">Field Validation </th>
				</tr>
			    </thead>
			    <tbody>  
				<c:forEach items="${messageTypeDetails.fields}" var="mappings" varStatus="field">
				    <c:set var="maxFieldNo" value="${mappings.fieldNo}" />
				    <tr>
					<input type="hidden" name="fields[${field.index}].id" value="${mappings.id}" />
					<input type="hidden" class="messageTypeId" name="fields[${field.index}].messageTypeId" value="${mappings.messageTypeId}" />
					<input type="hidden" class="fieldNo" name="fields[${field.index}].fieldNo" value="${mappings.fieldNo}" />
					<td>
					    <input type="hidden" name="fields[${field.index}].fieldDesc" value="${mappings.fieldDesc}" />
					    ${mappings.fieldDesc}
					</td>
					<td class="text-center">
					    <input type="checkbox" name="fields[${field.index}].required" value="true" <c:if test="${mappings.required == true}">checked</c:if>  /><br />
					</td>
					<td>
					    <select name="fields[${field.index}].validationType" class="form-control half">
						<c:forEach items="${validationTypes}"  var="fieldvalidationtypes" varStatus="vtype">
						    <option value="${validationTypes[vtype.index][0]}" <c:if test="${mappings.validationType == validationTypes[vtype.index][0]}">selected</c:if>>${validationTypes[vtype.index][1]}</option>
						</c:forEach>
					    </select>
					</td>
				    </tr>
				</c:forEach>
				<input type="hidden" id="maxFieldNo" value="${maxFieldNo}" />    
			    </tbody>
			</table>
		    </div>
		</div>
	    </section>
        </form:form>
    </div>
</div>

<!-- Brochure Form modal -->
<div class="modal fade" id="newFieldModal" role="dialog" tabindex="-1" aria-labeledby="New Field" aria-hidden="true" aria-describedby="New Field"></div>