<%-- 
    Document   : createConnection
    Created on : Dec 24, 2013, 11:22:00 AM
    Author     : chadmccue
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title"><c:choose><c:when test="${connectionDetails.id > 0}">Update</c:when><c:otherwise>Create New</c:otherwise></c:choose> Configuration Connection</h3>
	</div>
        <form:form id="connectionForm" modelAttribute="connectionDetails" action="addConnection.do" method="post" role="form">
            <form:hidden path="id" id="connectionId" />
            <div class="modal-body">
                <div class="form-group">
                    <label for="status">Status * </label>
                    <div>
                        <label class="radio-inline">
                            <form:radiobutton id="status" path="status" value="true"/>Active 
                        </label>
                        <label class="radio-inline">
                            <form:radiobutton id="status" path="status" value="false"/>Inactive
                        </label>
                    </div>
                </div> 
                <section class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Source Configuration</h3>
                    </div> 
                    <div class="panel-body">
                        <div class="form-container scrollable">
                            <div id="srcorgDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="organization">Organization *</label>
                                <select id="organization" class="form-control selsrcOrganization" <c:if test="${connectionDetails.id > 0}">disabled="true"</c:if>>
                                        <option value="">- Select -</option>
                                    <c:forEach items="${organizations}" var="org" varStatus="oStatus">
                                        <option value="${organizations[oStatus.index].id}" <c:if test="${organizations[oStatus.index].id == connectionDetails.srcConfigDetails.getorgId()}">selected</c:if>>${organizations[oStatus.index].orgName} </option>
                                    </c:forEach>
                                </select>
                                <span id="srcOrgMsg" class="control-label"></span>
                            </div>
                            <spring:bind path="sourceConfigId">      
                                <div id="srcConfigDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                    <label class="control-label" for="messageTypeId">Configuration *</label>
                                    <form:select path="sourceConfigId" id="srcConfig" rel="${connectionDetails.sourceConfigId}" class="form-control" disabled="${connectionDetails.id > 0 ? 'true':'false'}">
                                        <option value="">- Select -</option>
                                    </form:select>
                                    <c:if test="${connectionDetails.id > 0}"><form:hidden id="sourceConfigId" path="sourceConfigId"/></c:if>       
                                        <span id="srcConfigMsg" class="control-label"></span>
                                    </div>  
                            </spring:bind>
                            <div id="srcUsersDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="srcUsers">Authorized User(s) <span id="srcUsersFound"></span> *</label>
                                <div id="srcUsersTable" style="max-height:200px; overflow: auto"></div>
                                <span id="srcUsersMsg" class="control-label"></span>
                            </div>         
                        </div>
                    </div>
                </section>
                <section class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Target Configuration</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-container scrollable">
                            <div id="tgtorgDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="organization">Organization *</label>
                                <select id="organization" class="form-control seltgtOrganization" <c:if test="${connectionDetails.id > 0}">disabled="true"</c:if>>
                                        <option value="">- Select -</option>
                                    <c:forEach items="${organizations}" var="org" varStatus="oStatus">
                                        <option value="${organizations[oStatus.index].id}" <c:if test="${organizations[oStatus.index].id == connectionDetails.tgtConfigDetails.getorgId()}">selected</c:if>>${organizations[oStatus.index].orgName} </option>
                                    </c:forEach>
                                </select>
                                <span id="tgtOrgMsg" class="control-label"></span>
                            </div>
                            <spring:bind path="targetConfigId">        
                                <div id="tgtConfigDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                    <label class="control-label" for="messageTypeId">Configuration *</label>
                                    <form:select path="targetConfigId" id="tgtConfig" rel="${connectionDetails.targetConfigId}" class="form-control" disabled="${connectionDetails.id > 0 ? 'true':'false'}">
                                        <option value="">- Select -</option>
                                    </form:select>
                                    <c:if test="${connectionDetails.id > 0}"><form:hidden id="targetConfigId" path="targetConfigId"/></c:if>    
                                        <span id="tgtConfigMsg" class="control-label"></span>
                                    </div> 
                            </spring:bind>
                            <div id="tgtUsersDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="tgtUsers">Authorized User(s) <span id="tgtUsersFound"></span> *</label>
                                <div id="tgtUsersTable" style="max-height:200px; overflow: auto"></div>
                                <span id="tgtUsersMsg" class="control-label"></span>
                            </div>      
                        </div>
                    </div>
                </section> 
                <div class="form-group">
                    <input type="button" id="submitButton" rel="Create" role="button" class="btn btn-primary" value="<c:choose><c:when test="${connectionDetails.id > 0}">Update</c:when><c:otherwise>Create</c:otherwise></c:choose> Connection"/>
                        </div>                 
                    </div>
        </form:form>
    </div>
</div>         

<script>
    $(function () {
        var srcOrg = $('.selsrcOrganization').val();
        var tgtOrg = $('.seltgtOrganization').val();
        var connectionId = $('#connectionId').val();

        if (srcOrg > 0) {
            populateConfigurations(srcOrg, 'srcConfig');
            populateUsers(srcOrg, 'srcUsersTable', connectionId);
        }
        if (tgtOrg > 0) {
            populateConfigurations(tgtOrg, 'tgtConfig');
            populateUsers(tgtOrg, 'tgtUsersTable', connectionId);
        }

    });
</script>
