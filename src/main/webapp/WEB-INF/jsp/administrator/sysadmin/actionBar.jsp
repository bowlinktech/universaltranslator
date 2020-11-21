<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-default actions-nav" role="navigation">
    <div class="contain">
        <div class="navbar-header">
            <h1 class="section-title navbar-brand">
                <c:choose>
                    <c:when test="${param['page'] == 'list'}">
                        <a href="javascript:void(0);" title="Look Up Tables" class="unstyled-link">Look Up Tables</a>
                    </c:when>
                    <c:when test="${param['page'] == 'data'}">
                        <a href="javascript:void(0);" title="Table Data" class="unstyled-link">Look Up Table Data</a>
                    </c:when>
                    <c:when test="${param['page'] == 'macros'}">
                        <a href="javascript:void(0);" title="Macros" class="unstyled-link">Manage System Macros</a>
                    </c:when>
                    <c:when test="${param['page'] == 'crosswalks'}">
                        <a href="javascript:void(0);" title="Standard Crosswalks" class="unstyled-link">Manage Standard Crosswalks</a>
                    </c:when>
                    <c:when test="${param['page'] == 'logos'}">
                        <a href="javascript:void(0);" title="Logos" class="unstyled-link">Manage Logos</a>
                    </c:when> 
                    <c:when test="${param['page'] == 'hl7List'}">
                        <a href="javascript:void(0);" title="HL7 Specs" class="unstyled-link">Manage HL7 Specs</a>
                    </c:when> 
                    <c:when test="${param['page'] == 'hl7Details'}">
                        <a href="javascript:void(0);" title="Create new HL7 Spec" class="unstyled-link">Create new HL7 Spec</a>
                    </c:when>  
                    <c:when test="${param['page'] == 'loginAs'}">
                        <a href="javascript:void(0);" title="Login As" class="unstyled-link">Login As</a>
                    </c:when>  
		    <c:when test="${param['page'] == 'hisps'}">
                        <a href="javascript:void(0);" title="Manage HISPS" class="unstyled-link">Manage HISPS</a>
                    </c:when>  
		    <c:when test="${param['page'] == 'systemadmins'}">
                        <a href="javascript:void(0);" title="Manage System Admins" class="unstyled-link">Manage System Administrators</a>
                    </c:when>
                </c:choose>
            </h1>
        </div>
        <ul class="nav navbar-nav navbar-right navbar-actions">
            <c:choose>
                <c:when test="${param['page'] == 'logos'}">
                    <li><a href="javascript:void(0);" id="saveDetails" title="Save logo(s)"><span class="glyphicon glyphicon-ok icon-stacked"></span> Save </a></li>
                    <li><a href="<c:url value='/administrator/sysadmin/' />" title="Cancel"><span class="glyphicon icon-stacked custom-icon icon-cancel"></span>Cancel</a></li>
                </c:when>    
                <c:when test="${param['page'] == 'hl7List'}">
                    <li><a href="<c:url value='/administrator/sysadmin/hl7/create' />" title="Create New HL7 Version" role="button"><span class="glyphicon icon-stacked glyphicon glyphicon-plus"></span>Create New</a></li>
                </c:when>
                <c:when test="${param['page'] == 'hl7Details'}">
                    <li><a href="javascript:void(0);" id="saveDetails" title="Save this Configuration initial setup" role="button"><span class="glyphicon glyphicon-ok icon-stacked"></span> Save </a></li>
                </c:when>
                <c:when test="${param['page'] == 'macros'}">
                    <li>
                        <a href="javascript:void(0);" class="runTestMacroFile" title="Run Test Macro File" role="button"><span class="glyphicon glyphicon-play icon-stacked"></span> Run Test Macro File</a>
                    </li>
                    <li>
                        <a href="javascript:void(0);" class="printMacros" title="Print Full Macro List" role="button"><span class="glyphicon glyphicon-print icon-stacked"></span> Print Macros</a>
                    </li>
                </c:when>
            </c:choose>
        </ul>
    </div>
</nav>

