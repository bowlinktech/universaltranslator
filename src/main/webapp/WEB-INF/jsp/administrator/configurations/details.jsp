<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main clearfix" role="main">

    <div class="col-md-12">

        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success">
                <strong>Success!</strong> 
                <c:choose><c:when test="${savedStatus == 'updated'}">The configuration has been successfully updated!</c:when><c:otherwise>The configuration has been successfully created!</c:otherwise></c:choose>
                    </div>
        </c:if>

        <form:form id="configuration" commandName="configurationDetails" modelAttribute="configurationDetails" method="post" enctype="multipart/form-data" role="form">
            <input type="hidden" id="action" name="action" value="save" />
            <form:hidden path="id" id="id" />
            <form:hidden path="dateCreated" />
            <form:hidden path="stepsCompleted" />
	    <form:hidden path="helRegistryId" id="savedhelRegistryId" />
	    <form:hidden path="helSchemaName"  id="savedhelSchemaName"/>

            <section class="panel panel-default">

                <div class="panel-heading">
                    <h3 class="panel-title">Details</h3>
                </div>
                <div class="panel-body">
                    <div class="form-container">
                        <div class="form-group">
                            <label for="status">Status *</label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="status" path="status" value="1" />Active 
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="status" path="status" value="0"/>Inactive
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="type">Configuration Type * <span class="badge badge-help" data-placement="top" title="" data-original-title="Select &quot;For Source&quot; if this is for sending a message and &quot;For Target&quot; if this is for receiving a message">?</span></label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="type" path="type" value="1" class="type" disabled="${configurationDetails.id == 0 ? 'false' : 'true' }"/> For Source Organization 
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="type" path="type" value="2" class="type" disabled="${configurationDetails.id == 0 ? 'false' : 'true' }"/> For Target Organization
                                </label>
                                <c:if test="${configurationDetails.id > 0}"><form:hidden id="typeVal" path="type"/></c:if>  
                                </div>
			</div>
			<div id="sourceTypeDiv" class="form-group" style="display:${configurationDetails.type == 1 ? 'block' : 'none'}">
                            <label for="type">Source Type * </label>
                            <div>
				<label class="radio-inline">
				    <form:radiobutton id="sourceType" path="sourceType" class="sourceType" value="1" disabled="${configurationDetails.id == 0 ? 'false' : 'true' }"/> Originating Message 
				</label>
				<label class="radio-inline">
				    <form:radiobutton id="sourceType" path="sourceType" class="sourceType" value="2" disabled="${configurationDetails.id == 0 ? 'false' : 'true' }"/> Feedback Report
				</label>
				<c:if test="${configurationDetails.id > 0}"><form:hidden id="sourceTypeVal" path="sourceType"/></c:if>  
			    </div>
			</div>       
			<div id="configurationTypeDiv" class="form-group" style="display:${configurationDetails.type == 1 ? 'block' : 'none'}">
			   <label for="type">Processing Type * </label>
			   <div>
			       <label class="radio-inline">
				   <form:radiobutton id="configurationType" path="configurationType" class="configurationType" value="1" /> Translate Incoming Message 
			       </label>
			       <label class="radio-inline">
				   <form:radiobutton id="configurationType" path="configurationType" class="configurationType" value="2"/> Passthru
			       </label>
			   </div>
		        </div>   
                        <spring:bind path="orgId">
                            <div id="orgDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="organization">Organization *</label>
                                <form:select path="orgId" id="organization" class="form-control half" disabled="${configurationDetails.id == 0 ? 'false' : 'true' }">
                                    <option value="">- Select -</option>
                                    <c:forEach items="${organizations}" var="org" varStatus="oStatus">
                                        <option helRegistryId="${organizations[oStatus.index].helRegistryId}" helRegistrySchemaName="${organizations[oStatus.index].helRegistrySchemaName}" value="${organizations[oStatus.index].id}" <c:if test="${configurationDetails.orgId == organizations[oStatus.index].id}">selected</c:if>>${organizations[oStatus.index].orgName} </option>
                                    </c:forEach>
                                </form:select>
                                <c:if test="${configurationDetails.id > 0}">
				    <form:hidden id="organization" class="savedOrgId" helRegistryId="${configurationDetails.helRegistryId}" helRegistrySchemaName="${configurationDetails.helSchemaName}" path="orgId"/></c:if>  
                                    <span id="configOrgMsg" class="control-label"></span>
                                </div>
                        </spring:bind>
			<div id="askTargetQuestion1" class="form-group" style="display:none">
			    <label class="control-label">Will this configuration be based off a Health-e-Link Registry Configuration?</label>
			    <div>
			       <label class="radio-inline">
				   <input type="radio" id="basedOffHELConfig_1" name="basedOffHELConfig" class="basedOffHELConfig" value="1" /> Yes
			       </label>
			       <label class="radio-inline">
				   <input type="radio" id="basedOffHELConfig_2" name="basedOffHELConfig" class="basedOffHELConfig" value="0" /> No
			       </label>
			   </div>
			</div>
			<div id="askTargetQuestion2" class="form-group" style="display:none">
			    <label class="control-label" ">Select the Health-e-link Registry</label>
			    <select id="helRegistry" class="form-control half">
				<option value="0">- Select -</option>
			    </select>
			</div> 
		     </div>
			<spring:bind path="messageTypeId">
			    <div id="messageTypeDiv" class="form-group ${status.error ? 'has-error' : '' }" style="display:none">
				<label class="control-label" for="messageTypeId">Message Type *</label>
				<form:select path="messageTypeId" id="messageTypeId" class="form-control half" disabled="${configurationDetails.id == 0 ? 'false' : 'true' }">
				    <option value="">- Select -</option>
				    <c:forEach items="${messageTypes}" var="messageType" varStatus="mStatus">
					<option value="${messageType.id}" <c:if test="${messageType.id == configurationDetails.messageTypeId}">selected</c:if>>${messageType.name}</option>
				    </c:forEach>
				</form:select>
				<c:if test="${configurationDetails.id > 0}"><form:hidden path="messageTypeId"/></c:if> 
				<span id="configMessageTypeMsg" class="control-label"></span>
			    </div>
			</spring:bind>
			<spring:bind path="helRegistryConfigId">
			    <div id="helRegistryConfigDiv" class="form-group ${status.error ? 'has-error' : '' }"  style="display:none">
				<label class="control-label" for="helRegistryConfigId">Health-e-Link Registry Configuration *</label>
				<form:select path="helRegistryConfigId" id="helRegistryConfigId" rel="${configurationDetails.helRegistryConfigId}" class="form-control half" disabled="${configurationDetails.id == 0 ? 'false' : 'true' }"></form:select>
				<c:if test="${configurationDetails.id > 0}">
				    <form:hidden path="helRegistryConfigId"/>
				</c:if> 
				<span id="helRegistryConfigIdMsg" class="control-label"></span>
			    </div>
			</spring:bind>
			<%--<div id="associatedmessageTypeTopDiv" class="form-group" style="display:${configurationDetails.sourceType == 2 ? 'block' : 'none'}">        
                            <spring:bind path="associatedMessageTypeId">
                                <div id="associatedmessageTypeDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                    <label class="control-label" for="associatedMessageTypeId">Associated Message Type *</label>
                                    <form:select path="associatedMessageTypeId" id="associatedMessageTypeId" class="form-control half" disabled="${configurationDetails.id == 0 ? 'false' : 'true' }">
                                        <option value="">- Select -</option>
                                        <c:forEach items="${messageTypes}" var="messageType" varStatus="mStatus">
                                            <option value="${messageType.id}" <c:if test="${messageType.id == configurationDetails.associatedMessageTypeId}">selected</c:if>>${messageType.name}</option>
                                        </c:forEach>
                                    </form:select>
                                    <c:if test="${configurationDetails.id > 0}"><form:hidden path="associatedMessageTypeId"/></c:if> 
                                        <span id="associatedmessageTypeMsg" class="control-label"></span>
                                    </div>
                            </spring:bind>
                        </div>--%>
                        <spring:bind path="configName">
                            <div id="configNameDiv" class="form-group ${status.error ? 'has-error' : '' } ${not empty existingName ? 'has-error' : ''}">
                                <label class="control-label" for="configName">Unique Configuration Name *</label>
                                <form:input path="configName" id="configName" class="form-control" type="text" maxLength="45"  />
                                <c:if test="${not empty existingName}"><span class="control-label">${existingName}</span></c:if>
                                    <span id="configNameMsg" class="control-label"></span>
                                </div>
                        </spring:bind>        
                    </div>
                </div>
            </section>   
        </form:form> 
    </div>
</div>