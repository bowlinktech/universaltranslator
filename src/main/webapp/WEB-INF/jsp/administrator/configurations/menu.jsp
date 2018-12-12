<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<aside class="secondary">
    <nav class="secondary-nav" role="navigation">
        <ul class="nav nav-pills nav-stacked nav-steps" role="menu" >
            <li role="menuitem" ${param['page'] == 'details' ? 'class="active"' : ''}><a href="${param['page'] != 'details' ? 'details' : 'javascript:void(0);'}" title="Initial Setup">Initial Setup</a></li>
            <li role="menuitem" ${param['page'] == 'transport' ? 'class="active"' : ''} ${id > 0 and stepsCompleted >= 1 ? '' : 'class="disabled"'}><a href="${param['page'] != 'transport' and id > 0 and stepsCompleted >= 1 ? 'transport' : 'javascript:void(0);'}" title="Transport Method">Transport Method</a></li>
	    <c:if test="${showAllConfigOptions}">
		<c:if test="${(configurationDetails.type == 1) || (configurationDetails.type == 2 && configurationDetails.transportMethod != 'ERG')}">
		    <li role="menuitem" ${param['page'] == 'specs' ? 'class="active"' : ''} ${id > 0 and configurationDetails.configurationType == 1 and stepsCompleted >= 2 ? '' : 'class="disabled"'}><a href="${param['page'] != 'specs' and id > 0 and stepsCompleted >= 2 ? 'messagespecs' : 'javascript:void(0);'}" title="Detail Message Specs">Message Specs</a></li>
		</c:if>
		<c:if test="${mappings == 1 or mappings == 3}">
		    <li role="menuitem" ${param['page'] == 'mappings' ? 'class="active"' : ''} ${id > 0 and configurationDetails.configurationType == 1 and stepsCompleted >= 3 and (mappings == 1 or mappings == 3) ? '' : 'class="disabled"'}><a href="${param['page'] != 'mappings' and id > 0 and stepsCompleted >= 3 and (mappings == 1 or mappings == 3) ? 'mappings' : 'javascript:void(0);'}" title="Field Mappings">Field Mappings</a></li>
		</c:if>
		<%--<li role="menuitem" ${param['page'] == 'ERGCustomize' ? 'class="active"' : ''} ${id > 0 and stepsCompleted >= 3 and (mappings == 2 or mappings == 3) ? '' : 'class="disabled"'}><a href="${param['page'] != 'ERGCustomize' and id > 0 and stepsCompleted >= 3 and (mappings == 2 or mappings == 3) ? 'ERGCustomize' : 'javascript:void(0);'}" title="ERG Customization">ERG Customization</a></li>--%>
		<li role="menuitem" ${param['page'] == 'translations' ? 'class="active"' : ''} ${id > 0 and configurationDetails.configurationType == 1 and stepsCompleted >= 4 ? '' : 'class="disabled"'}><a href="${param['page'] != 'translations' and id > 0 and stepsCompleted >= 4 ? 'translations' : 'javascript:void(0);'}" title="Data Translations">Data Translations</a></li>
		<c:if test="${mappings != 2}">
		    <li role="menuitem" ${param['page'] == 'schedule' ? 'class="active"' : ''} ${id > 0 and ((configurationDetails.configurationType == 2 and stepsCompleted >= 2) or stepsCompleted >= 5) and (mappings == 1 or mappings == 3) ? '' : 'class="disabled"'}><a href="${param['page'] != 'schedule' and id > 0 and stepsCompleted >= 5 and (mappings == 1 or mappings == 3) ? 'scheduling' : 'javascript:void(0);'}" title="Scheduling">Scheduling</a></li>
		</c:if>
		<c:if test="${HL7 == true}">
		    <li role="menuitem" ${param['page'] == 'HL7' ? 'class="active"' : ''} ${id > 0 and configurationDetails.configurationType == 1 and stepsCompleted >= 5 ? '' : 'class="disabled"'}><a href="${param['page'] != 'HL7' and id > 0 and stepsCompleted >= 5 ? 'HL7' : 'javascript:void(0);'}" title="HL7 Customization">HL7 Customization</a></li>
		    <li role="menuitem" ${param['page'] == 'CCD' ? 'class="active"' : ''} ${id > 0 and configurationDetails.configurationType == 1 and stepsCompleted >= 5 ? '' : 'class="disabled"'}><a href="${param['page'] != 'CCD' and id > 0 and stepsCompleted >= 5 ? 'CCD' : 'javascript:void(0);'}" title="PDF Customization">PDF Customization</a></li>
		</c:if>
		<c:if test="${CCD == true}">
		    <li role="menuitem" ${param['page'] == 'CCD' ? 'class="active"' : ''} ${id > 0 and configurationDetails.configurationType == 1 and stepsCompleted >= 5 ? '' : 'class="disabled"'}><a href="${param['page'] != 'CCD' and id > 0 and stepsCompleted >= 5 ? 'CCD' : 'javascript:void(0);'}" title="CCD Customization">CCD/XML/JSON Setup</a></li>
		</c:if>    
		<li role="menuitem" ${param['page'] == 'preprocessing' ? 'class="active"' : ''} ${id > 0 and configurationDetails.configurationType == 1 and stepsCompleted >= 5 ? '' : 'class="disabled"'}><a href="${param['page'] != 'preprocessing' and id > 0 and stepsCompleted >= 5 ? 'preprocessing' : 'javascript:void(0);'}" title="Pre-Process Macros">Pre-Process Macros</a></li>
		<li role="menuitem" ${param['page'] == 'postprocessing' ? 'class="active"' : ''} ${id > 0 and configurationDetails.configurationType == 1 and stepsCompleted >= 5 ? '' : 'class="disabled"'}><a href="${param['page'] != 'postprocessing' and id > 0 and stepsCompleted >= 5 ? 'postprocessing' : 'javascript:void(0);'}" title="Post-Process Macros">Post-Process Macros</a></li>
	    </c:if>
        </ul>
    </nav>
</aside>