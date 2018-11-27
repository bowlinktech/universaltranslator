
<head>
    <meta http-equiv="Content-Language" content="en">
</head>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Status Details</h3>
         </div>
         <div class="modal-body">
            <div class="form-container">
                <div class="form-group">
                    <label class="control-label" >Status Code</label>
                    <br />
                    ${statusDetails.endUserDisplayCode}
                </div>
                <div class="form-group">
                    <label class="control-label" >Status Text</label>
                    <br />
                    ${statusDetails.endUserDisplayText}
                </div>
                <c:if test="${not empty statusDetails.endUserDescription}">
                    <div class="form-group">
                        <label class="control-label" >Description</label>
                        <br />
                        ${statusDetails.endUserDescription}
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>
