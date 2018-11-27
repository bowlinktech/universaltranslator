
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
            <h3 class="panel-title">Soap Response</h3>
        </div>
        <div class="modal-body">
            <div class="form-container">
                <div class="form-group">
                    <label class="control-label" >End Point</label>
                    <br />
                    ${wsMessage.endPoint}
                </div>
                <div class="form-group">
                    <label class="control-label" >Soap Response</label>
                    <br />
                    <c:out value="${wsMessage.soapResponse}" escapeXml="true"/>     
                </div>
            </div>
        </div>
    </div>
</div>
