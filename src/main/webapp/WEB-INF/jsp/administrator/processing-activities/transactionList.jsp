<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Transactions Accessed for Batch ${batchDetails.utBatchName}</h3>
        </div>
        <div class="modal-body">
            <div class="form-container">
                <div class="form-group">
                    <c:if test="${not empty userActivity}">
                        <c:forEach var="transactionId" items="${userActivity.transactionInIds}">
                            <a href="#messageDetailsModal" data-toggle="modal" rel="${transactionId}" rel2="0" class="viewLink">${transactionId}</a>
                            <br/>
                        </c:forEach>
                    </c:if>
                </div> 
            </div>
        </div>
    </div>
</div>
