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
				   <form:radiobutton id="configurationType" path="configurationType" class="configurationType" value="1" disabled="${configurationDetails.id == 0 ? 'false' : 'true' }" /> Translate Incoming Message 
			       </label>
			       <label class="radio-inline">
				   <form:radiobutton id="configurationType" path="configurationType" class="configurationType" value="2" disabled="${configurationDetails.id == 0 ? 'false' : 'true' }"/> Passthru
			       </label>
			       <c:if test="${configurationDetails.id > 0}"><form:hidden id="configurationTypeVal" path="configurationType"/></c:if>  
			   </div>
		        </div>   
                        <spring:bind path="orgId">
                            <div id="orgDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="organization">Organization *</label>
                                <form:select path="orgId" id="organization" class="form-control half">
                                    <option value="">- Select -</option>
                                    <c:forEach items="${organizations}" var="org" varStatus="oStatus">
                                        <option value="${organizations[oStatus.index].id}" <c:if test="${configurationDetails.orgId == organizations[oStatus.index].id}">selected</c:if>>${organizations[oStatus.index].orgName} </option>
                                    </c:forEach>
                                </form:select>
                                <c:if test="${configurationDetails.id > 0}">
				    <form:hidden id="organization" class="savedOrgId" path="orgId"/></c:if>  
                                    <span id="configOrgMsg" class="control-label"></span>
                                </div>
                        </spring:bind>
			<spring:bind path="associatedSourceConfigId">
                            <div id="associatedSourceConfigIdDiv" class="form-group ${status.error ? 'has-error' : '' }" style="display:none">
                                <label class="control-label" for="associatedSourceConfigId">Associated Source Configuration *</label>
                                <form:select path="associatedSourceConfigId" id="associatedSourceConfigId" class="form-control half">
                                    <option value="">- Select -</option>
                                    <c:forEach items="${sourceConfigurations}" var="sourceConfig">
                                        <option value="${sourceConfig.id}" <c:if test="${configurationDetails.associatedSourceConfigId == sourceConfig.id}">selected</c:if>>${sourceConfig.orgName} - ${sourceConfig.configName} (ID:${sourceConfig.id})</option>
                                    </c:forEach>
                                </form:select>
                                <c:if test="${configurationDetails.id > 0}">
				    <form:hidden id="organization" class="savedOrgId" path="orgId"/>
				    <form:hidden id="associatedSourceConfigId" path="associatedSourceConfigId"/>
				</c:if>  
				<span id="associatedSourceConfigMsg" class="control-label"></span>
			    </div>
                        </spring:bind>       
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