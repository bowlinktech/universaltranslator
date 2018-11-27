<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Add New HL7 Element</h3>
        </div>
        <div class="modal-body">
            <form:form id="hl7ElementForm" commandName="HL7ElementDetails" modelAttribute="HL7ElementDetails"  method="post" role="form">
                <form:hidden path="hl7Id"  />
                <form:hidden path="segmentId"  />
                <form:hidden path="displayPos" />
                <div class="form-container">
                    <spring:bind path="elementName">
                        <div id="elementNameDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="elementName">Element Name *</label>
                            <form:input path="elementName" id="newelementName" class="form-control" type="text" maxLength="255" />
                            <span id="elementNameMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <spring:bind path="defaultValue">
                        <div id="defaultValueDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="defaultValue">Default Value</label>
                            <form:input path="defaultValue" id="defaultValue" class="form-control" type="text" maxLength="45" />
                        </div>
                    </spring:bind>
                    <div class="form-group">
                        <input type="button" id="submitElementButton" role="button" class="btn btn-primary" value="Save"/>
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
