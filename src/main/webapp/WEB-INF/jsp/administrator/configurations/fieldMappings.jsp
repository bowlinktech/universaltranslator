<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <div class="alert alert-success fieldsUpdated" style="display:none;">
                <strong>Success!</strong> 
                The field mappings have been successfully updated!
            </div>
            <div id="saveMsgDiv" class="alert alert-danger" style="display:none;">
                <strong>You must click SAVE above to submit the mapping changes!</strong>
            </div>
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                    <dt>Configuration Summary:</dt>
			<dd><strong>Organization:</strong> ${configurationDetails.orgName}</dd>
			<dd><strong>Configuration Type:</strong> <span id="configType" rel="${configurationDetails.type}"><c:choose><c:when test="${configurationDetails.type == 1}">Source</c:when><c:otherwise>Target</c:otherwise></c:choose></span></dd>
                    	<dd><strong>Configuration Name:</strong> ${configurationDetails.configName}</dd>
			<dd><strong>Transport Method:</strong> <c:choose><c:when test="${configurationDetails.transportMethod == 'File Upload'}"><c:choose><c:when test="${configurationDetails.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${configurationDetails.transportMethod}</c:otherwise></c:choose></dd>
		    </dt>
		</div>
	    </section>
	</div>
    </div>
    <div class="row-fluid">
	<div class="${configurationDetails.type == 2 ? 'col-md-11' : 'col-md-9'}">
	    <section class="panel panel-default">
		<div class="panel-heading">
                    <div class="pull-right">
                        <c:if test="${not empty transportDetails.fields}">
                            <a href="#appendNewFieldsModal" class="btn btn-primary btn-xs  btn-action" rel2="${transportDetails.id}" rel="${configurationDetails.id}" id="appendConfigurationFields" data-toggle="modal">Append New Fields</a>
			 </c:if>
                        <c:if test="${transportDetails.transportMethodId == 10 || (transportDetails.transportMethodId == 13 && configurationDetails.type == 2 && transportDetails.helRegistryConfigId > 0)}">
			    <a class="btn btn-primary btn-xs btn-action" rel2="${transportDetails.id}" rel="${configurationDetails.id}" id="reloadConfigurationFields" data-toggle="tooltip" data-original-title="Click here to reload Configuration Fields.">Reload Configuration Fields</a>
                        </c:if>
                        <a href="#!" class="btn btn-success btn-xs btn-action createNewTemplate" rel="${configurationDetails.id}" data-toggle="tooltip" data-original-title="Click here to create a new template with the below fields.">Create Template</a>
                    </div>     
		    <h3 class="panel-title"><c:choose><c:when test="${configurationDetails.type == 2}">Target</c:when><c:otherwise>Source</c:otherwise></c:choose> Configuration Fields</h3>
		</div>
		<div class="panel-body">
		    <div class="form-container scrollable">
                        <form:form id="formFields" modelAttribute="transportDetails" method="post" role="form">
                            <input type="hidden" id="action" name="action" value="save" />
                            <input type="hidden" id="seltransportMethod" name="transportMethod" value="${selTransportMethod}" />
                            <input type="hidden" name="errorHandling" value="${transportDetails.errorHandling}" />
                            <table class="table table-striped table-hover table-default">
                                <thead>
                                    <tr>
                                        <th scope="col" class="center-text">Field No</th>
                                        <th scope="col">Field Name</th>
                                        <th scope="col">Sample Data</th>
                                        <c:if test="${configurationDetails.type == 2}"><th scope="col" class="center-text">Default Value</th></c:if>
                                        <th scope="col" class="center-text">Use Field</th>
                                        <th scope="col" class="center-text">Required</th> 
                                        <th scope="col" class="center-text">Validation</th> 
                                </thead>
                                <tbody>
				    <c:forEach items="${transportDetails.fields}" var="mappings" varStatus="field">
					<tr class="uFieldRow" rel="${mappings.fieldNo}" rel2="${field.index}">
					    <td scope="row" class="center-text">
						<input type="hidden" name="fields[${field.index}].id" value="${mappings.id}" />
						<input type="hidden" name="fields[${field.index}].configId" value="${mappings.configId}" />
						<input type="hidden" name="fields[${field.index}].transportDetailId" value="${mappings.transportDetailId}" />
						<input type="hidden" name="fields[${field.index}].fieldNo" value="${mappings.fieldNo}" />
						<input type="hidden" name="fields[${field.index}].associatedFieldNo" value="${mappings.associatedFieldNo}" />
                                                <c:if test="${configurationDetails.type == 1}">
                                                    <input type="hidden" name="fields[${field.index}].defaultValue" value="${mappings.defaultValue}" />
                                                </c:if>
						${mappings.fieldNo}
					    </td>
					    <td>
                                                <input type="text" class="form-control" name="fields[${field.index}].fieldDesc" value="${mappings.fieldDesc}" maxlength="50" />
                                            </td>
                                            <td>
                                                <input type="text" class="form-control" name="fields[${field.index}].sampleData" value="${mappings.sampleData}" maxlength="50" />
                                            </td>
                                            <c:if test="${configurationDetails.type == 2}">
                                                <td class="center-text">
                                                    <input type="text" name="fields[${field.index}].defaultValue" value="${mappings.defaultValue}" maxlength="50" />
                                                </td>
                                            </c:if>
					    <td class="center-text">
						<input type="checkbox" class="useField" fieldNo="${mappings.fieldNo}" name="fields[${field.index}].useField" <c:if test="${mappings.useField == true}">checked</c:if> />
					    </td>
					    <td class="center-text">
						<input type="checkbox" name="fields[${field.index}].required"  <c:if test="${mappings.required == true}">checked</c:if>  />
					    </td>
					    <td class="center-text">
						<select name="fields[${field.index}].validationType" id="validation_${mappings.fieldNo}" class="formField">
						    <c:forEach items="${validationTypes}"  var="fieldvalidationtypes" varStatus="vtype">
							<option value="${validationTypes[vtype.index][0]}" <c:if test="${mappings.validationType == validationTypes[vtype.index][0]}">selected</c:if>>${validationTypes[vtype.index][1]}</option>
						    </c:forEach>
						</select>    
					    </td>
					</tr>
				    </c:forEach>
                                </tbody>
                            </table>
                        </form:form>
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>
<div class="modal fade" id="appendNewFieldsModal" role="dialog" tabindex="-1" aria-labeledby="Append New Fields" aria-hidden="true" aria-describedby="Append New Fields"></div>
