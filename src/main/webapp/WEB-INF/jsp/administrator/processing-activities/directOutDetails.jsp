<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Direct HIPS Message Details</h3>
        </div>
        <div class="modal-body">
            <div class="form-container">
                <div class="form-group">
                    <label class="control-label" >From Direct Address</label>
                    <br />
                    ${directMessageDetails.fromDirectAddress}
                </div>
		<div class="form-group">
                    <label class="control-label" >To Direct Address</label>
                    <br />
                    ${directMessageDetails.toDirectAddress}
                </div>
                <c:if test="${not empty directMessageDetails.responseMessage}">
                    <div class="form-group">
                        <label class="control-label" >HIPS Response</label>
                        <br />
                        ${directMessageDetails.responseMessage}
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>
