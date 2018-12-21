<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-default actions-nav" role="navigation">
    <div class="contain">
        <div class="navbar-header">
            <h1 class="section-title navbar-brand">
                <c:choose>
                    <c:when test="${param['page'] == 'listConfigs'}">
                        <a href="javascript:void(0);" title="Configuration List" class="unstyled-link">Configurations</a>
                    </c:when>
                    <c:when test="${param['page'] == 'configDetails'}">
                        <a href="javascript:void(0);" title="Configuration Details" class="unstyled-link">
                            Configuration - Details
                        </a>
                    </c:when>
                    <c:when test="${param['page'] == 'transport'}">
                        <a href="javascript:void(0);" title="Configuration Transport Details" class="unstyled-link">Configuration - Transport Method</a>
                    </c:when>
                    <c:when test="${param['page'] == 'mappings'}">
                        <a href="javascript:void(0);" title="Configuration Field Mappings" class="unstyled-link">Configuration - Field Mappings</a>
                    </c:when>
                    <c:when test="${param['page'] == 'ERGCustomize'}">
                        <a href="javascript:void(0);" title="Configuration ERG Customization" class="unstyled-link">Configuration - ERG Customization</a>
                    </c:when>
                    <c:when test="${param['page'] == 'translations'}">
                        <a href="javascript:void(0);" title="Configuration Data Translations" class="unstyled-link">Configuration - Data Translations</a>
                    </c:when>
                    <c:when test="${param['page'] == 'specs'}">
                        <a href="javascript:void(0);" title="Detailed Message Specs" class="unstyled-link">Configuration - Message Specs</a>
                    </c:when>
                    <c:when test="${param['page'] == 'schedule'}">
                        <a href="javascript:void(0);" title="Configuration Schedule" class="unstyled-link">Configuration - Schedule</a>
                    </c:when>  
                    <c:when test="${param['page'] == 'connections'}">
                        <a href="javascript:void(0);" title="Configuration Connection List" class="unstyled-link">Configuration Connections</a>
                    </c:when> 
                    <c:when test="${param['page'] == 'HL7'}">
                        <a href="javascript:void(0);" title="HL7 Customization" class="unstyled-link">Configuration - HL7 Customization</a>
                    </c:when>  
                    <c:when test="${param['page'] == 'CCD'}">
                        <a href="javascript:void(0);" title="CCD/XML/JSON Customization" class="unstyled-link">Configuration - CCD/XML/JSON Customization</a>
                    </c:when>     
                    <c:when test="${param['page'] == 'preprocessing'}">
                        <a href="javascript:void(0);" title="Configuration Pre-Processing Macros" class="unstyled-link">Configuration - Pre-Process Macros</a>
                    </c:when>   
                    <c:when test="${param['page'] == 'postprocessing'}">
                        <a href="javascript:void(0);" title="Configuration Post-Processing Macros" class="unstyled-link">Configuration - Post-Process Macros</a>
                    </c:when>   
                </c:choose>
            </h1>
        </div>
        <ul class="nav navbar-nav navbar-right navbar-actions">
            <c:choose>
                <c:when test="${param['page'] == 'listConfigs'}">
                    <li><a href="create" title="Create New Configuration" role="button"><span class="glyphicon icon-stacked glyphicon glyphicon-plus"></span>Create New</a></li>
                </c:when>
                <c:when test="${param['page'] == 'connections'}">
                    <li><a href="#connectionsModal" id="createNewConnection" data-toggle="modal" role="button" title="Create Configuration Connection"><span class="glyphicon icon-stacked glyphicon glyphicon-plus"></span>Create New</a></li>
                </c:when>  
                <c:when test="${param['page'] == 'CCD'}">
                    <li><a href="#ccdElementModal" id="createNewCCDElement" data-toggle="modal" role="button" title="Create CCD Element"><span class="glyphicon icon-stacked glyphicon glyphicon-plus"></span>Create New</a></li>
                </c:when>    
                <c:otherwise>
                    <li>
			<a href="javascript:void(0);" id="saveDetails" title="Save this Configuration initial setup" role="button"><span class="glyphicon glyphicon-ok icon-stacked"></span> Save </a>
		    </li>
		    <c:if test="${configurationDetails.configurationType == 1 || (configurationDetails.configurationType == 2 && param['page'] != 'schedule')}">
			 <c:if test="${param['page'] != 'postprocessing'}">
			    <li><a href="javascript:void(0);" id="next" title="Save and Proceed to the Next Step"><span class="glyphicon glyphicon-forward icon-stacked" role="button"></span>Next Step</a></li>
			</c:if>
		    </c:if>
                    <%--<c:if test="${not empty id}"><li><a href="#confirmationOrgDelete" data-toggle="modal" rel="${id}" title="Delete this Configuration"><span class="glyphicon glyphicon-remove icon-stacked"></span>Delete</a></li></c:if>--%>
                    <li><a href="<c:url value='/administrator/configurations/list' />" title="Cancel" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span>Cancel</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</nav>
