<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Add New HL7 Segment</h3>
        </div>
        <div class="modal-body">
            <form:form id="hl7SegmentForm" commandName="HL7SegmentDetails" modelAttribute="HL7SegmentDetails"  method="post" role="form">
                <form:hidden path="hl7Id"  />
                <form:hidden path="displayPos" />
                <div class="form-container">
                    <spring:bind path="segmentName">
                        <div id="segmentNameDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="segmentName">Segment Name *</label>
                            <form:input path="segmentName" id="newsegmentName" class="form-control" type="text" maxLength="3" />
                            <span id="segmentNameMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <div class="form-group">
                        <input type="button" id="submitSegmentButton" role="button" class="btn btn-primary" value="Save"/>
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
