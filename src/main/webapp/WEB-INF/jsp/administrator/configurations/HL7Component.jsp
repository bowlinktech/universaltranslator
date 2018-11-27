<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentBucket" value="0" />
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Add New HL7 Element Component</h3>
        </div>
        <div class="modal-body">
            <form:form id="hl7ComponentForm" commandName="HL7ComponentDetails" modelAttribute="HL7ComponentDetails"  method="post" role="form">
                <form:hidden path="elementId"  />
                <form:hidden path="displayPos" />
                <div class="form-container">
                    <spring:bind path="fieldDescriptor">
                        <div id="fieldDescriptorDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="fieldDescriptor">Field Descriptor</label>
                            <form:input path="fieldDescriptor" id="newelementName" class="form-control" type="text" maxLength="255" />
                        </div>
                    </spring:bind>
                    <spring:bind path="fieldDescriptor">
                        <div id="fieldAppendDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="fieldAppendText">Append Text</label>
                            <form:input path="fieldAppendText" id="fieldAppendText" class="form-control" type="text" maxLength="255" />
                        </div>
                    </spring:bind>
                    <spring:bind path="defaultValue">
                        <div id="defaultValueDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="defaultValue">Default Value</label>
                            <form:input path="defaultValue" id="newdefaultValue" class="form-control" type="text" maxLength="45" />
                        </div>
                    </spring:bind>
                    <spring:bind path="fieldValue">
                        <div id="fieldValueDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="fieldValue">Field Value</label>
                            <form:select path="fieldValue" id="newFieldValue" class="form-control half">
                                <option value="">- Select -</option>
                                <c:forEach items="${fields}" var="field" varStatus="fStatus">
                                    <c:if test="${currentBucket != fields[fStatus.index].bucketNo}">
                                        <c:if test="${currentBucket > 0}"></optgroup></c:if>
                                        <c:set var="currentBucket" value="${fields[fStatus.index].bucketNo}" />
                                        <c:choose>
                                            <c:when test="${currentBucket == 1}">
                                                <optgroup label="(Sender Organiation Information)">
                                                </c:when>
                                                <c:when test="${currentBucket == 2}">
                                                <optgroup label="(Sender Provider Information)">
                                                </c:when>     
                                                <c:when test="${currentBucket == 3}">
                                                <optgroup label="(Recipient Organization Information)">
                                                </c:when>
                                                <c:when test="${currentBucket == 4}">
                                                <optgroup label="(Recipient Provider Information)">
                                                </c:when>
                                                <c:when test="${currentBucket == 5}">
                                                <optgroup label="(Patient Information)">
                                                </c:when>
                                                <c:when test="${currentBucket == 6}">
                                                <optgroup label="(Details)">
                                                </c:when>
                                            </c:choose>
                                        </c:if>
                                        <option value="${fields[fStatus.index].fieldNo}">${fields[fStatus.index].fieldLabel} </option>
                                    </c:forEach>
                                </form:select>
                            <span id="fieldValueMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <div class="form-group">
                        <input type="button" id="submitComponentButton" role="button" class="btn btn-primary" value="Save"/>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function () {
        $("input:text,form").attr("autocomplete", "off");
    });
</script>
