<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog modal-dialog-big">
    <div class="modal-content">
        <form:form id="newformFields" modelAttribute="transportDetails" action="saveNewConfigurationFields" method="post" role="form">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Append New Fields ${success}</h3>
        </div>
        <div class="modal-body">
           <p>The system will only save fields that have a value entered in the field name field and 'Use Field' is selected. All others will be ignored.</p>
            <table class="table table-striped table-hover table-default">
                <thead>
                    <tr>
                        <th scope="col" class="center-text">Field No</th>
                        <th scope="col">Field Name</th>
                        <th scope="col">Sample Data</th>
                        <th scope="col" class="center-text">Use Field</th>
                        <th scope="col" class="center-text">Required</th> 
                        <th scope="col" class="center-text">Validation</th> 
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${transportDetails.newfields}" var="mappings" varStatus="field">
                        <tr class="uFieldRow" rel="${mappings.fieldNo}" rel2="${field.index}">
                            <td scope="row" class="center-text">
                                <input type="hidden" name="newfields[${field.index}].configId" value="${mappings.configId}" />
                                <input type="hidden" name="newfields[${field.index}].transportDetailId" value="${mappings.transportDetailId}" />
                                <input type="hidden" name="newfields[${field.index}].fieldNo" value="${mappings.fieldNo}" />
                                ${mappings.fieldNo}
                            </td>
                            <td><input name="newfields[${field.index}].fieldDesc" class="form-control" type="text" maxLength="255" /></td>
                            <td><input name="newfields[${field.index}].sampleData" class="form-control" type="text" maxLength="255" /></td>
                            <td class="center-text">
                                <input type="checkbox" class="useField" fieldNo="${mappings.fieldNo}" name="newfields[${field.index}].useField" <c:if test="${mappings.useField == true}">checked</c:if> />
                            </td>
                            <td class="center-text">
                                <input type="checkbox" name="newfields[${field.index}].required"  <c:if test="${mappings.required == true}">checked</c:if>  />
                            </td>
                            <td class="center-text">
                                <select name="newfields[${field.index}].validationType" id="validation_${mappings.fieldNo}" class="formField">
                                    <c:forEach items="${validationTypes}"  var="fieldvalidationtypes" varStatus="vtype">
                                        <option value="${validationTypes[vtype.index][0]}" <c:if test="${mappings.validationType == validationTypes[vtype.index][0]}">selected</c:if>>${validationTypes[vtype.index][1]}</option>
                                    </c:forEach>
                                </select>    
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="modal-footer">
            <input type="submit" class="btn btn-primary" value="Append new Fields"/>
        </div>
       </form:form>         
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function () {
        $("input:text,form").attr("autocomplete", "off");


    });

</script>