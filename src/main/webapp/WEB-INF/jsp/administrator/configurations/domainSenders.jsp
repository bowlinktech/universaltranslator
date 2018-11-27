<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Sender Domain(s) ${success}</h3>
        </div>
        <div class="modal-body">
            <div id="domainDanger" class="alert alert-danger" style="display:none;">
                You must enter in at least one domain.
            </div> 
            <form:form id="cwsf" commandName="cwsf" modelAttribute="cwsf" method="post" role="form">
                <form:hidden path="transportId" id="transportId" />       
                <c:forEach items="${cwsf.senderDomainList}" var="senderDomainList" varStatus="field">
                    <input name="senderDomainList[${field.index}].id" id="id${field.index}" class="form-control" type="hidden" value="${senderDomainList.id}"  />
                    <div id="senderDomainList${field.index}Div" class="form-group">
                        <input name="senderDomainList[${field.index}].domain" id="senderDomainList${field.index}" class="form-control" type="text" maxLength="75" value="${senderDomainList.domain}"  />
                        <span id="senderDomainList${field.index}Msg" class="control-label"></span>
                    </div>
                </c:forEach>

                <c:if test="${fn:length(cwsf.senderDomainList) == 0}">
                    <c:set var="index" value="0"/>
                </c:if>
                <c:if test="${fn:length(cwsf.senderDomainList) > 0}">
                    <c:set var="index" value="${fn:length(cwsf.senderDomainList)}"/>
                </c:if>

                <div id="sender${index}Div" class="form-group">
                    <input name="senderDomainList[${index}].id" id="id${index}" class="form-control" type="hidden" value="0"  />
                    <input name="senderDomainList[${index}].domain" id="senderDomainList${index}" class="form-control" type="text" maxLength="75" value=""  />
                    <span id="senderDomainList${index}Msg" class="control-label"></span>
                </div>
                <div class="form-group">
                    <input type="button" id="submitDomainButton" rel="${index}" class="btn btn-primary submitDomainButton" value="Save"/>
                </div>       
            </form:form>
        </div>
    </div>
</div>

<c:if test="${not empty senders}">
    <script type="text/javascript">
        require(['./main'], function () {
            require(['jquery'], function ($) {
                $('#domain1').val('${senders}');
            });
        });
    </script>
</c:if>