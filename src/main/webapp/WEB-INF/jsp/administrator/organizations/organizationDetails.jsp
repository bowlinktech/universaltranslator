<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main clearfix" role="main">

    <div class="col-md-12">

        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success" role="alert">
                <strong>Success!</strong> 
                <c:choose>
                    <c:when test="${savedStatus == 'updated'}">The organization has been successfully updated!</c:when>
                    <c:otherwise>The organization has been successfully created!</c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <form:form commandName="organization"  method="post" role="form" enctype="multipart/form-data">
            <input type="hidden" id="action" name="action" value="save" />
            <form:hidden path="id" id="orgId" />
            <form:hidden path="cleanURL" id="cleanURL" />
            <form:hidden path="dateCreated" />

            <section class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Details</h3>
                </div>
                <div class="panel-body">
                    <div class="form-container">
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
                        <div class="form-group">
                            <label for="orgType">Organization Type *</label>
                            <div>
				<label class="radio-inline">
                                    <form:radiobutton id="orgType" path="orgType" value="2"/> Community Based Organization 
                                </label>
				<label class="radio-inline">
                                    <form:radiobutton id="orgType" path="orgType" value="4"/> Data Warehouse 
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="orgType" path="orgType" value="1"/> Health Care Provider
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="orgType" path="orgType" value="3"/> Health Management Information System
                                </label>
                            </div>
                        </div>   
                        <spring:bind path="orgName">
                            <div class="form-group ${status.error ? 'has-error' : '' } ${not empty existingOrg ? 'has-error' : ''}">
                                <label class="control-label" for="orgName">Name *</label>
                                <form:input path="orgName" id="orgName" class="form-control" type="text" maxLength="255" />
                                <form:errors path="orgName" cssClass="control-label" element="label" />
                                <c:if test="${not empty existingOrg}">${existingOrg}</c:if>
                                </div>
                        </spring:bind>
			<spring:bind path="rrProgramId">
			    <div id="rrProgramDiv" class="form-group ${status.error ? 'has-error' : '' }">
				<label class="control-label" for="rrProgramId">Registry</label>
				<form:select path="rrProgramId" id="rrProgramId" class="form-control half">
				    <option value="0">- Select -</option>
				    <c:forEach items="${registries}" var="registry">
					<option value="${registry.id}" <c:if test="${organization.rrProgramId == registry.id}">selected</c:if>>${registry.programName}</option>                                                
				    </c:forEach>
				</form:select>
				<span id="rrProgramMsg" class="control-label"></span>
			    </div>
			</spring:bind> 
			<spring:bind path="rrEntity2DetailId">
			    <div id="rrEntity2DetailIdDiv" val="${organization.rrEntity2DetailId}" class="form-group ${status.error ? 'has-error' : '' }">
				<label class="control-label" for="rrEntity2DetailId">Associated Registry Entity</label>
				<form:select path="rrEntity2DetailId" id="rrEntity2DetailId" class="form-control half">
				    <option value="0">- Select -</option>
				</form:select>
				<span id="rrEntity2DetailIdMsg" class="control-label"></span>
			    </div>
			</spring:bind> 
                        <spring:bind path="address">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="address">Address</label>
                                <form:input path="address" id="address" class="form-control" type="text" maxLength="45" />
                                <form:errors path="address" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="address2">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="address2">Address 2</label>
                                <form:input path="address2" id="address2" class="form-control" type="text" maxLength="45" />
                                <form:errors path="address2" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>    
                        <spring:bind path="city">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="city">City</label>
                                <form:input path="city" id="city" class="form-control" type="text" maxLength="45" />
                                <form:errors path="city" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <div id="stateDiv" style="display:none">
                            <spring:bind path="state">
                                <div class="form-group ${status.error ? 'has-error' : '' }">
                                    <label class="control-label" for="state">State</label>
                                    <form:select id="state" path="state" cssClass="form-control half">
                                        <option value="" label=" - Select - " ></option>
                                        <form:options items="${stateList}"/>
                                    </form:select>
                                    <form:errors path="state" cssClass="control-label" element="label" />
                                </div>
                            </spring:bind>
                        </div>    
                        <spring:bind path="country">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="country">Country</label>
                                <form:select id="country" path="country" cssClass="form-control half">
                                    <option value="" label=" - Select - " ></option>
                                    <form:options items="${countryList}"/>
                                </form:select>
                                <form:errors path="country" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="postalCode">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="postalCode">Postal Code</label>
                                <form:input path="postalCode" id="postalCode" class="form-control xs-input" type="text" maxLength="15" />
                                <form:errors path="postalCode" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="phone">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="phone">Phone</label>
                                <form:input path="phone" id="phone" class="form-control sm-input" type="text" maxLength="45" />
                                <form:errors path="phone" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="fax">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="fax">Fax</label>
                                <form:input path="fax" id="fax" class="form-control sm-input" type="text" maxLength="45" />
                                <form:errors path="fax" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="infoURL">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="infoURL">Information URL</label>
                                <form:input path="infoURL" id="infoURL" class="form-control" type="text" maxLength="255" />
                                <form:errors path="infoURL" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>        
                        <c:if test="${id > 0}">
                            <c:if test="${not empty organization.parsingTemplate}">
                                <div class="form-group">
                                    <label class="control-label" for="parsingTemplate">Current Inbound File (CCD/HL7) Parsing Script</label>
                                    <input type="text" disabled id="parsingTemplate" class="form-control" value="${organization.parsingTemplate}" />
                                    <form:hidden id="parsingTemplate" path="parsingTemplate" />
                                </div>
                            </c:if>
                            <spring:bind path="file">
                                <div id="parsingTemplateDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                    <label class="control-label" for="file">Inbound File (CCD/HL7) Parsing Script (JAR file)</label>
                                    <form:input path="file" id="file" class="form-control" type="file" />
                                    <form:errors path="file" cssClass="control-label" element="label" />
                                    <span id="parsingTemplateMsg" class="control-label"></span>
                                </div>
                            </spring:bind>
                        </c:if>   
                    </div>
                </div>
            </section>
        </form:form>
    </div>
</div>

<!-- The delete an organization modal -->
<div class="modal fade" id="confirmationOrgDelete" role="dialog" tabindex="-1" aria-labeledby="Delete Organization" aria-hidden="true" aria-describedby="Delete Organization">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" role="button">&times;</button>
                <h3 class="panel-title">Organization Delete</h3>
            </div>
            <div class="modal-body">
                <p>
                    Are you <strong>ABSOLUTELY</strong> sure?
                </p>
                <p>
                    This action <strong>CANNOT</strong> be undone. This will delete all associated configurations, system users, providers and uploaded brochures.
                    An alternative would be to make the organization inactive. This will set all system users and configurations to an inactive state.
                </p>
                <form id="confirmOrgDelete" method="post" role="form" action="delete">
                    <div id="confirmDiv" class="form-group" >
                        <input type="hidden" name="id" value="${id}" />
                        <input type="hidden" id="realUsername" name="realUsername" value="${pageContext.request.userPrincipal.name}" />
                        <div class="form-group">
                            <label for="username">Please type in your username to confirm this deletion:</label>
                            <input type="text" id="username" name="username" class="form-control" maxLength="15"  />
                            <span id="confirmMsg" class="control-label"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <input type="button" disabled id="submitButton" class="btn btn-primary" value="I understand the consequences, delete this organization" />
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

