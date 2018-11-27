<%-- 
    Document   : HL7MessageDetails
    Created on : Jul 1, 2014, 2:22:51 PM
    Author     : chadmccue
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="modal-dialog">
    <div class="modal-content" style="width:840px;">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Message Details</h3>
        </div>
        <div class="modal-body">
            <div class="container main-container" role="main">
                <div class="row">
                    <div class="col-md-12 page-content">
                        <c:choose>
                            <c:when test="${empty transactionDetails}">
                                This HL7 transaction is no longer available.
                            </c:when>
                            <c:otherwise>

                                <c:forEach items="${transactionDetails.sourceOrgFields}" var="sourceFields" varStatus="sfield">
                                    <div class="col-md-6" style="width:400px;">
                                        <div id="fieldDiv_${sourceFields.fieldNo}" class="form-group">
                                            <label class="control-label" for="${sourceFields.fieldNo}">${sourceFields.fieldLabel}</label>
                                            <input id="${sourceFields.fieldNo}" style="width:350px;" disabled name="sourceFields[${sfield.index}].fieldValue" value="${sourceFields.fieldValue}" class="form-control ${sourceFields.validation.replace(' ','-')} <c:if test="${sourceFields.required == true}"> required</c:if>" type="text">
                                            </div>
                                        </div>
                                </c:forEach>

                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>