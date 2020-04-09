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
	    <form:hidden path="helRegistrySchemaName" id="helRegistrySchemaName" />
	    <form:hidden path="helRegistryId" id="helRegistryId" />
	    <form:hidden path="helRegistryOrgId" value="0" />

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
                        <div id="parentOrgId">
                            <spring:bind path="parentOrgId">
                                <div id="parentOrgIdDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                    <label class="control-label" for="helRegistryOrgId">Parent Organization</label>
                                    <form:select path="parentOrgId" class="form-control half">
                                        <option value="0">- Select Parent Organization -</option>
                                        <c:forEach var="parentOrg" items="${organizationList}">
                                            <c:if test="${organization.id != parentOrg.id}">
                                                 <option value="${parentOrg.id}" <c:if test="${organization.parentOrgId == parentOrg.id}">selected</c:if>>${parentOrg.orgName}</option>
                                            </c:if>
                                        </c:forEach>
                                    </form:select>
                                    <span id="parentOrgIdMsg" class="control-label"></span>
                                </div>
                            </spring:bind> 
			</div>
                        <div class="form-group">
			    <div class="form-group ${status.error ? 'has-error' : '' }">
				<label class="control-label" for="orgType">Organization Type *</label>
				<form:select id="orgType" path="orgType" cssClass="form-control half">
				    <option value="" label=" - Select - ">- Select Organization Type - </option>
				    <option value="2" <c:if test="${organization.orgType == 2}">selected</c:if>>Community Based Organization</option>
				    <option value="4" <c:if test="${organization.orgType == 4}">selected</c:if>>Data Warehouse</option>
				    <option value="1" <c:if test="${organization.orgType == 1}">selected</c:if>>Health Care Provider</option>
				    <option value="3" <c:if test="${organization.orgType == 3}">selected</c:if>>Health Management Information System</option>
				    <!--<option value="5" <c:if test="${organization.orgType == 5}">selected</c:if>>Internal Health-e-Link Registry</option>-->
				</form:select>
				<form:errors path="orgType" cssClass="control-label" element="label" />
			    </div>
                        </div>  
			<!--<div class="form-group">
			    <label for="orgType">Is this organization from a Health-e-Link Registry?</label>
			    <select id="isHELRegistry" class="form-control half">
				<option value="" <c:if test="${organization.id == 0}">selected</c:if>>- Select -</option>
				<option value="1" <c:if test="${organization.helRegistryId > 0}">selected</c:if>>Yes</option>
				<option value="0" <c:if test="${organization.helRegistryId == 0}">selected</c:if>>No</option>
			    </select>
			</div>	
			<div id="HELRegistryDetails" style="${organization.helRegistryId > 0 ? 'display:block':'display:none'}">
			    <div id="helRegistryDiv" class="form-group ${status.error ? 'has-error' : '' }">
				<label class="control-label" for="helRegistry">Select the Health-e-Link Registry</label>
				<select id="helRegistry" rel="${organization.helRegistryId}-${organization.helRegistrySchemaName}" class="form-control half">
				    <option value="0">- Select -</option>
				</select>
				<span id="helRegistryMsg" class="control-label"></span>
			    </div>
			    <div id="HELRegistryOrgsDiv" style="${organization.helRegistryId > 0 ? 'display:block':'display:none'}">
				<spring:bind path="helRegistryOrgId">
				    <div id="helRegistryOrgIdDiv" class="form-group ${status.error ? 'has-error' : '' }">
					<label class="control-label" for="helRegistryOrgId">Select the Registry Organization</label>
					<form:select path="helRegistryOrgId" id="helRegistryOrgId" schema="" rel="${organization.helRegistryOrgId}" class="form-control half">
					    <option value="0">N/A </option>
					</form:select>
					<span id="helRegistryOrgIdMsg" class="control-label"></span>
				    </div>
				</spring:bind> 
			    </div>
			</div>	-->
			<div id="orgDetails" ><!--style="${organization.id > 0 ? 'display:block':'display:none'}"-->
			    <spring:bind path="orgName">
				<div class="form-group ${status.error ? 'has-error' : '' } ${not empty existingOrg ? 'has-error' : ''}">
				    <label class="control-label" for="orgName">Organization Name *</label>
				    <form:input path="orgName" id="orgName" class="form-control" type="text" maxLength="255" />
				    <form:errors path="orgName" cssClass="control-label" element="label" />
				    <c:if test="${not empty existingOrg}"><span class="control-label">${existingOrg}</span></c:if>
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
			    <div id="stateDiv">
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
			    <spring:bind path="primaryContactEmail">
				<div class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="fax">Primary Contact Email Address</label>
				    <form:input path="primaryContactEmail" id="primaryContactEmail" class="form-control sm-input" type="text" maxLength="255" />
				    <form:errors path="primaryContactEmail" cssClass="control-label" element="label" />
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

